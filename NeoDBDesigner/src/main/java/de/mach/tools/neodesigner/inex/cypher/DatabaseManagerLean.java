/* Copyright (C) 2018 Chris Deter Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the
 * Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions: The
 * above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE. */

package de.mach.tools.neodesigner.inex.cypher;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.mach.tools.neodesigner.core.datamodel.Domain;
import de.mach.tools.neodesigner.core.datamodel.Domain.DomainId;
import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Relations;
import de.mach.tools.neodesigner.core.datamodel.Relations.Type;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.core.datamodel.impl.FieldImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.ForeignKeyImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.IndexImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.TableImpl;
import de.mach.tools.neodesigner.inex.DatabaseConnection;
import de.mach.tools.neodesigner.inex.nimport.UtilImport;


/** Verwaltet die Datenbankverbindung und stellt mithilfe des QueryBuilders die Cypher Statements für de Datenbank
 * zusammen.
 *
 * @author Chris Deter */
public class DatabaseManagerLean implements DatabaseConnection {
  private static final Logger LOG = Logger.getLogger(DatabaseManagerLean.class.getName());
  private final DatabaseConnector dbconnection;
  private final QueryBuilder staticqb = new QueryBuilder();

  /** Konstruktor.
   *
   * @param dc der DatenbankConnector */
  public DatabaseManagerLean(final DatabaseConnector dc) {
    dbconnection = dc;
  }

  private ForeignKey checkForExistingFk(final String string, final Table t, final Table reft) {
    final ForeignKey workfk = new ForeignKeyImpl(string, t);
    if (t.getForeignKeys().contains(workfk)) {
      return t.getForeignKeys().get(t.getForeignKeys().indexOf(workfk));
    }
    workfk.setIndex(new IndexImpl("XIF" + "_" + string, t));
    workfk.setRefTable(reft);
    t.addForeignKey(workfk, false);
    reft.addForeignKey(workfk, true);
    return workfk;
  }

  @Override
  public boolean connectDb(final String addr, final String nutzername, final String pw) {
    final QueryBuilder qb = new QueryBuilder();
    boolean b = dbconnection.connectDb(addr, nutzername, pw);
    try { // Teste ob die Datenbankverbindung wirklich besteht
      qb.getAllTableNames();
      qb.getOne();
      runCypher(qb);
    }
    catch (final Exception e) {
      b = false;
      DatabaseManagerLean.LOG.log(Level.WARNING, "Error: Could not connect Database", e);
    }
    return b;
  }

  private void countEdge(final Map<String, Integer> res, final Type type, final String name, final QueryBuilder qb) {
    qb.countRelation(Relations.getType(type));
    getNumber(res, name, qb);
  }

  private void countNode(final Map<String, Integer> res, final String type, final QueryBuilder qb) {
    qb.countNodes(type);
    getNumber(res, type, qb);
  }

  @Override
  public void createIndexOnDb() {
    final QueryBuilder qb = new QueryBuilder();
    qb.insertIndexOnDb(Strings.NODE_TABLE);
    runCypher(qb);
  }

  @Override
  public boolean deleteDatabase() {
    if (dbconnection.isOnline()) {
      final QueryBuilder qb = new QueryBuilder();
      try {
        qb.deleteDatabaseConstraint(Strings.NODE_TABLE);
        runCypher(qb);
      }
      catch (final Exception e) {
        DatabaseManagerLean.LOG.log(Level.INFO, Strings.NOCONSTRAINTTODELETE, e);
        dbconnection.reconnect();
      }
      qb.deleteDatabase();
      runCypher(qb);
      return true;
    }
    return false;
  }

  @Override
  public void disconnectDb() {
    dbconnection.disconnectDb();
  }

  private Field extractFieldData(final ResultUnit record, final Table t) {
    final Domain domain = Domain.csvTypeToDomain(record.get(Strings.IDENT_TYPE), record.get(Strings.IDENT_LEN),
                                                 record.get(Strings.IDENT_ACCURACY));
    String comment = record.get(Strings.IDENT_COMMENT);
    comment = comment == null ? Strings.EMPTYSTRING : comment;

    return new FieldImpl(record.get(Strings.IDENT_NAME), domain.getDomain(), domain.getDomainlength(),
                         !record.getBoolean(Strings.IDENT_NULLABLE), comment, t);
  }

  private void fillTableWithForeignkeys(final ResultUnit record, final Map<String, Table> tbldic) {
    final Table origin = tbldic.get(record.get(Strings.IDENT_ORIGIN));
    final Table t = tbldic.get(record.get(Strings.IDENT_TABLENAME));
    insertRefKey(record, t.getField(record.get(Strings.IDENT_NAME)), t, origin);
  }

  private void fillTableWithIndizes(final ResultUnit record, final Table t) {
    final Field f = t.getField(record.get(Strings.IDENT_NAME));
    final int order = Integer.parseInt(record.get(Strings.IDENT_ORDER));
    if (record.get(Strings.IDENT_TYPE).equals("identified_by") && f != null) {
      t.getXpk().setName(record.get(Strings.IDENT_INDEXNAME));
      t.getXpk().addField(f);
      t.getXpk().setOrder(f.getName(), order, false);
    }
    else if (record.get(Strings.IDENT_TYPE).equals("indexed_by") && f != null) {
      Index i = new IndexImpl(record.get(Strings.IDENT_INDEXNAME), t);
      if (t.getIndizies().contains(i)) {
        i = t.getIndizies().get(t.getIndizies().indexOf(i));
      }
      else {
        t.addIndex(i);
      }
      i.addField(f);
      i.setOrder(f.getName(), order, false);
    }
  }

  private void getAllFields(final ResultUnit record, final Map<String, Table> tbldic) {
    final Table t = tbldic.get(record.get(Strings.IDENT_TABLENAME));
    t.addField(extractFieldData(record, t));
  }

  @Override
  public Map<String, Integer> getDatabaseStats() {
    final QueryBuilder qb = new QueryBuilder();
    final Map<String, Integer> res = new LinkedHashMap<>();
    countNode(res, Strings.EMPTYSTRING, qb);
    countNode(res, Strings.NODE_TABLE, qb);
    countNode(res, Strings.NODE_COLUMN, qb);
    //
    countEdge(res, Type.DEFAULT, Strings.RELTEXT, qb);
    countEdge(res, Type.XPK, Strings.RELTEXT + Type.XPK, qb);
    countEdge(res, Type.DATA, Strings.RELTEXT + Type.DATA, qb);
    countEdge(res, Type.INDEX, Strings.RELTEXT + Type.INDEX, qb);
    countEdge(res, Type.REFERENCE, Strings.RELTEXT + Type.REFERENCE, qb);
    return res;
  }

  private Collection<? extends Field> getFields(final List<ResultUnit> fields, final Table t) {
    final List<Field> fieldsres = new ArrayList<>();
    for (final ResultUnit record : fields) {
      fieldsres.add(extractFieldData(record, t));
    }
    return fieldsres;
  }

  @Override
  public int getForeignKeyNumber(final int lenght) {
    final QueryBuilder qb = new QueryBuilder();
    int number = 0;
    qb.getForeignKeyNumber(lenght);
    final List<ResultUnit> fknumber = runCypher(qb);
    try {
      if (!fknumber.isEmpty()) {
        final ResultUnit rec = fknumber.get(0);
        number = Integer.parseInt(rec.get(Strings.IDENT_NAME).substring(2, 2 + lenght));
      }
      else {
        number = 100;
      }
    }
    catch (final NumberFormatException e) {
      if (lenght == 3) {
        number = 1000;
      }
      else if (lenght == 4) {
        number = 10000;
      }
    }
    return number;
  }

  @Override
  public List<String> getListWithCategories() {
    final QueryBuilder qb = new QueryBuilder();
    final List<String> l = new ArrayList<>();
    qb.getAllTables();
    for (final ResultUnit record : runCypher(qb)) {
      l.add(record.get(Strings.IDENT_CATEGORY));
    }
    return l;
  }

  @Override
  public List<String> getListWithTableNames() {
    final QueryBuilder qb = new QueryBuilder();
    final List<String> l = new ArrayList<>();
    qb.getAllTableNames();
    for (final ResultUnit record : runCypher(qb)) {
      l.add(record.get(Strings.IDENT_NAME));
    }
    return l;
  }

  private void getNumber(final Map<String, Integer> res, final String type, final QueryBuilder qb) {
    final List<ResultUnit> ru = runCypher(qb);
    if (!ru.isEmpty()) {
      res.put(type, Integer.parseInt(ru.get(0).get(Strings.IDENT_NUMBER)));
    }
  }

  @Override
  public Optional<Table> getTable(final String name) {
    final QueryBuilder qb = new QueryBuilder();
    Optional<Table> ret = Optional.empty();
    qb.getTable(name);
    final List<ResultUnit> ru = runCypher(qb);
    if (!ru.isEmpty()) {
      ret = Optional.of(getTableFromQuery(ru.get(0)));
    }
    ret.ifPresent(table -> {
      // Felder
      qb.getFieldsOfTable(table.getName());
      final List<ResultUnit> fields = runCypher(qb);
      getFields(fields, table).forEach(table::addField);
      qb.getIndizesOfTable(table.getName());
      for (final ResultUnit record : runCypher(qb)) {
        fillTableWithIndizes(record, table);
      }
      // FKs einlesen an dieser Stelle nicht so wichtig, da diese Methode nur ein Fallback ist, falls die Datenbank
      // nicht komplett geladen wird
    });
    return ret;
  }

  private Table getTableFromQuery(final ResultUnit record) {
    // Table
    final Table t = new TableImpl(record.get(Strings.IDENT_NAME));
    t.setCategory(record.get(Strings.IDENT_CATEGORY));
    final String comment = record.get(Strings.IDENT_COMMENT);
    if (comment != null) {
      t.setComment(comment);
    }
    return t;
  }

  @Override
  public List<Table> getTables() {
    final QueryBuilder qb = new QueryBuilder();
    final List<Table> tables = new ArrayList<>();
    final Map<String, Table> tbldic = new HashMap<>();
    qb.getAllTables();
    for (final ResultUnit record : runCypher(qb)) {
      final Table t = getTableFromQuery(record);
      tables.add(t);
      tbldic.put(t.getName(), t);
    }
    qb.getFieldsOfTable();
    for (final ResultUnit record : runCypher(qb)) {
      getAllFields(record, tbldic);
    }
    qb.getIndizesOfTable();
    for (final ResultUnit record : runCypher(qb)) {
      fillTableWithIndizes(record, tbldic.get(record.get(Strings.IDENT_TABLENAME)));
    }
    qb.getForeignKeysOfTable();
    for (final ResultUnit record : runCypher(qb)) {
      fillTableWithForeignkeys(record, tbldic);
    }
    for (final Table t : tables) {
      for (final ForeignKey fk : t.getForeignKeys()) {
        final List<Field> tempFields = fk.getIndex().getFieldList();
        UtilImport.setFieldForeignkeyRelation(fk, tempFields);
        for (final Field f : fk.getIndex().getFieldList()) {
          if (f.getDomain().equals(DomainId.STRING) && f.getDomainLength() == 20) {
            f.setDomain(DomainId.LOOKUP);
          }
        }
      }
    }
    return tables;
  }

  @Override
  public void importForeignKey(final ForeignKey i) {
    insertNewForeignKey(i);
  }

  @Override
  public void importTable(final Table t) {
    staticqb.importTable(t);
    runCypher(staticqb);
  }

  private void insertNewForeignKey(final ForeignKey fk) {
    final QueryBuilder qb = new QueryBuilder();
    for (final Field f : fk.getIndex().getFieldList()) {
      qb.insertFkRelation(fk.getTable().getName(), fk.getRefTable().getName(), fk.getName(), f.getName(),
                          fk.getRefTable().getXpk().getOrder(fk.getAltName(f.getName()), false));
      runCypher(qb);
    }
  }

  private void insertRefKey(final ResultUnit record, final Field f, final Table mainTable, final Table refTable) {
    if (mainTable != null && refTable != null && f != null) {
      final ForeignKey fk = checkForExistingFk(record.get(Strings.IDENT_INDEXNAME), mainTable, refTable);
      fk.getIndex().addField(f);
      fk.getIndex().setOrder(record.get(Strings.IDENT_NAME), Integer.parseInt(record.get(Strings.IDENT_ORDER)), true);
    }
  }

  @Override
  public boolean isReady() {
    return dbconnection.isOnline();
  }

  /** Führt eine Query auf der Datenbank aus.
   *
   * @param q Query
   * @return eine Ergebnis Einheit */
  private List<ResultUnit> runCypher(final QueryBuilder q) {
    final String query = q.getQuery();
    final Object[] params = q.getKeys();
    q.clear();
    return dbconnection.runCypher(query, params);
  }

  public void startCsvImport() {
    final QueryBuilder qb = new QueryBuilder();
    qb.startCsvBulkImportTables();
    runCypher(qb);
    createIndexOnDb();
    qb.createIndexOnColumn();
    runCypher(qb);
    qb.startCsvBulkImportColumns();
    runCypher(qb);
    qb.startCsvBulkImportTableColumnRel();
    runCypher(qb);
    qb.startCsvBulkImportPrimaryKeys();
    runCypher(qb);
    qb.startCsvBulkImportForeignKeys();
    runCypher(qb);
    qb.startCsvBulkImportIndizes();
    runCypher(qb);
    qb.startCsvBulkImportMetaTbls();
    runCypher(qb);
    qb.startCsvBulkImportMetaCols();
    runCypher(qb);
  }
}
