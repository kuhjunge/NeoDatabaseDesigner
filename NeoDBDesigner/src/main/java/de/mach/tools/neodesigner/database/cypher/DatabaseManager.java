/*******************************************************************************
 * Copyright (C) 2017 Chris Deter
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/

package de.mach.tools.neodesigner.database.cypher;

import de.mach.tools.neodesigner.core.datamodel.Domain;
import de.mach.tools.neodesigner.core.datamodel.Domain.DomainId;
import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Node;
import de.mach.tools.neodesigner.core.datamodel.Relations;
import de.mach.tools.neodesigner.core.datamodel.Relations.Type;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.core.datamodel.impl.FieldImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.ForeignKeyImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.IndexImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.TableImpl;
import de.mach.tools.neodesigner.database.DatabaseConnection;
import de.mach.tools.neodesigner.database.Strings;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Verwaltet die Datenbankverbindung und stellt mithilfe des QueryBuilders die
 * Cypher Statements für de Datenbank zusammen.
 *
 * @author Chris Deter
 *
 */
public class DatabaseManager implements DatabaseConnection {
  private static final Logger LOG = Logger.getLogger(DatabaseManager.class.getName());
  private final QueryBuilder qb = new QueryBuilder();
  private final DatabaseConnector dbcon;

  /**
   * Konstruktor.
   *
   * @param dbc
   *          der DatenbankConnector
   */
  public DatabaseManager(final DatabaseConnector dbc) {
    dbcon = dbc;
  }

  /**
   * Konstruktor.
   *
   * @param dbc
   *          der Datenbank Connector
   * @param addr
   *          die Adresse der Datenbank
   * @param nutzername
   *          der Nutzername
   * @param pw
   *          das Passwort
   */
  public DatabaseManager(final DatabaseConnector dbc, final String addr, final String nutzername, final String pw) {
    dbcon = dbc;
    connectDb(addr, nutzername, pw);
  }

  @Override
  public boolean isReady() {
    return dbcon.isOnline();
  }

  @Override
  public boolean connectDb(final String addr, final String nutzername, final String pw) {
    boolean b = dbcon.connectDb(addr, nutzername, pw);
    try { // Teste ob die Datenbankverbindung wirklich besteht
      qb.clear();
      qb.getAllTables();
      qb.getOne();
      dbcon.runCypher(qb.getQuery());
    } catch (final Exception e) {
      b = false;
    }
    return b;
  }

  @Override
  public void createIndexOnDb() {
    dbcon.runCypher(QueryBuilder.insertIndexOnDb(Strings.NODE_TABLE));
  }

  @Override
  public void importTable(final Table t) {
    qb.importTable(t);
    dbcon.runCypher(qb.getQuery(), qb.getKeys());
  }

  @Override
  public void importForeignKey(final ForeignKey i) {
    qb.importForeignKey(i);
    dbcon.runCypher(qb.getQuery(), qb.getKeys());
  }

  @Override
  public List<String> getListWithTableNames() {
    final List<String> l = new ArrayList<>();
    qb.clear();
    qb.getAllTables();
    for (final ResultUnit record : dbcon.runCypher(qb.getQuery())) {
      l.add(record.get(Strings.IDENT_NAME));
    }
    return l;
  }

  @Override
  public List<String> getListWithCategories() {
    final List<String> l = new ArrayList<>();
    qb.clear();
    qb.getAllTables();
    for (final ResultUnit record : dbcon.runCypher(qb.getQuery())) {
      l.add(record.get(Strings.IDENT_CATEGORY));
    }
    return l;
  }

  @Override
  public Optional<Table> getTable(final String name) {
    final Optional<Table> ret = getTableRaw(name);
    if (ret.isPresent()) {
      ret.get().getForeignKeys().addAll(getForeignKeysFromTable(ret.get()));
      ret.get().getRefForeignKeys().addAll(getRefForeignKeysFromTable(ret.get()));
    }
    qb.clear();
    return ret;
  }

  /**
   * Läd eine Tabelle ohne FK und RefFK Verbindungen aus der Datenbank.
   *
   * @param name
   *          der Tabelle
   * @return Tabelle, wenn sie gefunden wurde in der DB
   */
  private Optional<Table> getTableRaw(final String name) {
    Optional<Table> ret = Optional.empty();
    qb.clear();
    qb.getTable(name);
    final List<ResultUnit> ru = dbcon.runCypher(qb.getQuery(), qb.getKeys());
    if (!ru.isEmpty()) {
      ret = Optional.of(getTableFromQuery(ru.get(0)));
    }
    return ret;
  }

  @Override
  public List<Table> getTables() {
    final List<Table> tables = new ArrayList<>();
    qb.getAllTables();
    for (final ResultUnit record : dbcon.runCypher(qb.getQuery())) {
      final Table t = getTableFromQuery(record);
      tables.add(t);
    }
    for (final Table t : tables) {
      t.getForeignKeys().addAll(getForeignKeysFromTable(t));
      for (final ForeignKey fk : t.getForeignKeys()) {
        setExternRefFk(fk.getRefTable().getName(), tables, fk);
        fk.getRefTable().getRefForeignKeys().add(fk);
      }
    }
    return tables;
  }

  @Override
  public Map<String, Integer> getDatabaseStats() {
    final Map<String, Integer> res = new LinkedHashMap<>();
    countNode(res, Strings.EMPTYSTRING);
    countNode(res, Strings.NODE_TABLE);
    countNode(res, Strings.NODE_FIELD);
    countNode(res, Strings.NODE_INDEX);
    countNode(res, Strings.NODE_FK);
    //
    countEdge(res, Type.DEFAULT, Strings.RELTEXT);
    countEdge(res, Type.XPK, Strings.RELTEXT + Type.XPK);
    countEdge(res, Type.DATA, Strings.RELTEXT + Type.DATA);
    countEdge(res, Type.INDEX, Strings.RELTEXT + Type.INDEX);
    countEdge(res, Type.CONNECTED, Strings.RELTEXT + Type.CONNECTED);
    countEdge(res, Type.REFERENCE, Strings.RELTEXT + Type.REFERENCE);
    countEdge(res, Type.FOREIGNKEY, Strings.RELTEXT + Type.FOREIGNKEY);
    return res;
  }

  private void countEdge(final Map<String, Integer> res, final Type type, final String name) {
    qb.countRelation(Relations.getType(type));
    getNumber(res, name);
  }

  private void countNode(final Map<String, Integer> res, final String type) {
    qb.countNodes(type);
    getNumber(res, type);
  }

  private void getNumber(final Map<String, Integer> res, final String type) {
    final List<ResultUnit> ru = dbcon.runCypher(qb.getQuery(), qb.getKeys());
    if (!ru.isEmpty()) {
      res.put(type, Integer.parseInt(ru.get(0).get(Strings.IDENT_NUMBER)));
    }
  }

  /**
   * Läd eine Tabelle aus einer Query.
   *
   * @param record
   *          die ResultUnit
   * @return eine Tabelle
   */
  private Table getTableFromQuery(final ResultUnit record) {
    final Table t = new TableImpl(record.get(Strings.IDENT_NAME));
    t.setCategory(record.get(Strings.IDENT_CATEGORY));
    final String comment = record.get(Strings.IDENT_COMMENT);
    if (comment != null) {
      t.setComment(comment);
    }
    t.getFields().addAll(getFields(getFieldFromTable(t), t));
    // TODO: Direkter Zugriff auf Fieldliste entfernen
    final List<Index> li = getIndiziesFromTable(t, Type.XPK);
    if (!li.isEmpty()) {
      t.setXpk(li.get(0));
      for (final Field f : li.get(0).getFieldList()) {
        final Optional<Field> opF = t.getField(f.getName());
        if (opF.isPresent()) {
          opF.get().setPartOfPrimaryKey(true);
        } else {
          DatabaseManager.LOG.log(Level.SEVERE, () -> String.format(Strings.SOMETHINGISWRONG, Strings.ERR_PRIMARYKEY));
        }
      }
    } else {
      t.setXpk(new IndexImpl(Strings.INDEXTYPE_XPK + t.getName(), t));
    }
    t.getIndizies().addAll(getIndiziesFromTable(t, Type.INDEX));
    return t;
  }

  @Override
  public int getForeignKeyNumber(final int lenght) {
    int number = 0;
    qb.getForeignKeyNumber(lenght);
    final List<ResultUnit> fknumber = dbcon.runCypher(qb.getQuery());
    try {
      if (!fknumber.isEmpty()) {
        final ResultUnit rec = fknumber.get(0);
        number = Integer.parseInt(rec.get(Strings.IDENT_NAME).substring(2, 2 + lenght));
      } else {
        number = 100;
      }
    } catch (final NumberFormatException e) {
      if (lenght == 3) {
        number = 1000;
      } else if (lenght == 4) {
        number = 10000;
      }
    }
    return number;
  }

  /**
   * Läd die Felder einer Tabelle aus der Datenbank.
   *
   * @param t
   *          Tabelle
   * @return Result der Datenbank
   */
  private List<ResultUnit> getFieldFromTable(final Table t) {
    qb.getFieldsOfTable(t.getName());
    return dbcon.runCypher(qb.getQuery(), qb.getKeys());
  }

  /**
   * Verarbeitet die Felder die aus der Datenbank kommen.
   *
   * @param fieldRes
   *          Ergebnisse der Datenbank
   * @param origin
   *          Origin Table
   * @return Liste mit Feldern
   */
  private List<Field> getFields(final List<ResultUnit> fieldRes, final Table origin) {
    final List<Field> fields = new ArrayList<>();
    for (final ResultUnit record : fieldRes) {
      fields.add(createFieldFromQuery(record, origin));
    }
    return fields;
  }

  /**
   * Läd die Felder aus einer existierenden Tabelle.
   *
   * @param fieldRes
   *          Ergebnis einer Datenbank
   * @param origin
   *          die Origin Tabelle
   * @param i
   *          der Index, dem die Felder hinzugefügt werden sollen
   */
  private void getExistingFields(final List<ResultUnit> fieldRes, final Table origin, final Index i) {
    for (final ResultUnit record : fieldRes) {
      final Optional<Field> f = origin.getField(record.get(Strings.IDENT_NAME));
      if (f.isPresent()) {
        i.addField(f.get(), record.get(Strings.IDENT_ALTNAME));
      } else {
        DatabaseManager.LOG.log(Level.SEVERE, () -> String.format(Strings.SOMETHINGISWRONG, Strings.ERR_FIELD));
      }
    }
  }

  /**
   * Erstellt ein Field aus den Informationen der Datenbank.
   *
   * @param recordField
   *          Ergebnisse der Datenbank
   * @param origin
   *          Origin Tabelle
   * @return ein Feld aus dem Datenbankergebniss
   */
  private Field createFieldFromQuery(final ResultUnit recordField, final Table origin) {
    // TODO: Test schreiben, der diese Funktion korrekt prüft
    final Domain domain = new Domain(recordField.get(Strings.IDENT_TYPE));
    String comment = recordField.get(Strings.IDENT_COMMENT);
    comment = comment == null ? Strings.EMPTYSTRING : comment;
    return new FieldImpl(recordField.get(Strings.IDENT_NAME), domain.getDomain(), domain.getDomainlength(),
        recordField.getBoolean(Strings.IDENT_ISREQ), comment, origin);
  }

  /**
   * Läd die Indizes aus der Datenbank.
   *
   * @param name
   *          Name der Tabelle
   * @param n
   *          Typ der Beziehung
   * @return Liste mit Indizes
   */
  private List<Index> getIndiziesFromTable(final Table name, final Type n) {
    final List<Index> indizies = new ArrayList<>();
    final boolean isSimpleIndex = n == Type.INDEX;
    qb.getIndizesOfTable(name.getName(), n, isSimpleIndex);
    for (final ResultUnit record : dbcon.runCypher(qb.getQuery(), qb.getKeys())) {
      indizies.add(createIndexFromQuery(name, n, record));
    }
    return indizies;
  }

  /**
   * erstellt ein Index aus den Informationen der Datenbank.
   *
   * @param name
   *          Name der Tabelle
   * @param reltype
   *          Typ der Beziehung
   * @param rec
   *          Result der Datebank
   * @return ein Index aus den Informationen der Datenbank
   */
  private Index createIndexFromQuery(final Table name, final Type reltype, final ResultUnit rec) {
    final Index index = new IndexImpl(rec.get(Strings.IDENT_NAME), name);
    getExistingFields(getFieldsOfIndexFromDb(name.getName(), reltype, index.getName()), name, index);
    return index;
  }

  /**
   * Läd die Felder eines Indexes aus der Datenbank.
   *
   * @param name
   *          Origin Tabelle
   * @param n
   *          Typ der Beziehung
   * @param i
   *          Der Index, dem die Felder hinzugefügt werden sollen
   * @return Result der Datenbank
   */
  private List<ResultUnit> getFieldsOfIndexFromDb(final String tableName, final Type n, final String indexname) {
    qb.getFieldsOfIndex(tableName, indexname, n);
    return dbcon.runCypher(qb.getQuery(), qb.getKeys());
  }

  /**
   * Läd die Fremdschlüssel aus der Datenbank.
   *
   * @param name
   *          der Tabelle
   * @param tableList
   *          Liste aller Tabellen
   * @return Liste mit ForeignKeys der Tabelle
   */
  private List<ForeignKey> getForeignKeysFromTable(final Table table) {
    final List<ForeignKey> fk = new ArrayList<>();
    qb.getForeignKeysOfTable(table.getName());
    for (final ResultUnit record : dbcon.runCypher(qb.getQuery(), qb.getKeys())) {
      fk.add(createForeignKeysFromQuery(table, record));
    }
    return fk;
  }

  /**
   * Läd die Referenz Fremdschlüssel aus der Datenbank.
   *
   * @param table
   *          die Tabelle
   * @return eine Liste mit Fremdschlüssel die auf diese Tabelle zeigen
   */
  private List<ForeignKey> getRefForeignKeysFromTable(final Table table) {
    qb.getRefForeignKeysOfTable(table.getName());
    final List<ForeignKey> fkl = new ArrayList<>();
    for (final ResultUnit record : dbcon.runCypher(qb.getQuery(), qb.getKeys())) {
      final Optional<Table> optOrigin = getTableRaw(record.get(Strings.IDENT_REFTABLENAME));
      final Table origin = optOrigin.isPresent() ? optOrigin.get()
          : new TableImpl(record.get(Strings.IDENT_REFTABLENAME));
      final ForeignKey fk = new ForeignKeyImpl(record.get(Strings.IDENT_NAME), origin);
      fk.setRefTable(table);
      fk.setIndex(new IndexImpl(record.get(Strings.IDENT_XIF), origin));
      getExistingFields(getFieldsOfIndexFromDb(origin.getName(), Type.INDEX, fk.getIndex().getName()), origin,
          fk.getIndex());
      fkl.add(fk);
    }
    return fkl;
  }

  /**
   * erstellt einen Fremdschlüssel aus den Informationen aus der Datenbank.
   * Strings auslagern
   *
   * @param name
   *          der Tabelle
   * @param rec
   *          Result Unit
   * @param tableList
   *          Liste mit allen Tabellen
   * @return Fremdschlüssel
   */
  private ForeignKey createForeignKeysFromQuery(final Table name, final ResultUnit rec) {
    final ForeignKey fk = new ForeignKeyImpl(rec.get(Strings.IDENT_NAME), name);
    fk.setRefTable(new TableImpl(rec.get(Strings.IDENT_REFTABLENAME)));
    fk.setIndex(name.getIndizies().stream().filter(p -> p.equals(new IndexImpl(rec.get(Strings.IDENT_XIF), name)))
        .collect(Collectors.toList()).get(0));
    return fk;
  }

  /**
   * Setzt den Referenz Fremdschlüssel.
   *
   * @param refTableName
   *          der Tabellenname der Referenztabelle
   * @param tableList
   *          die Tabellenliste
   * @param fk
   *          der Fremdschlüssel
   */
  private void setExternRefFk(final String refTableName, final List<Table> tableList, final ForeignKey fk) {
    final int refTabIndex = tableList.indexOf(new TableImpl(refTableName));
    final Table refTable = tableList.get(refTabIndex);
    fk.setRefTable(refTable);
  }

  @Override
  public void changeTableName(final String tableName, final String newName, final String xpkName) {
    qb.clear();
    qb.changeTableName(tableName, newName, xpkName);
    dbcon.runCypher(qb.getQuery(), qb.getKeys());
  }

  @Override
  public void changeTableCategory(final String tableName, final String newName) {
    qb.clear();
    qb.changeTableCategory(tableName, newName);
    dbcon.runCypher(qb.getQuery(), qb.getKeys());
  }

  @Override
  public void changeNodeNameFromTable(final String nodeName, final String tableName, final String newName) {
    qb.clear();
    qb.changeNodeName(nodeName, tableName, newName);
    dbcon.runCypher(qb.getQuery(), qb.getKeys());
  }

  @Override
  public void changePrimFieldNameRelation(final String tableName, final String newNodeName, final String oldNodeName) {
    qb.clear();
    qb.changePrimFieldNameRelation(tableName, newNodeName, oldNodeName);
    dbcon.runCypher(qb.getQuery(), qb.getKeys());
  }

  @Override
  public void changeFieldRequired(final String nodeName, final String tableName, final Boolean val) {
    qb.clear();
    qb.changeFieldRequired(nodeName, tableName, val);
    dbcon.runCypher(qb.getQuery(), qb.getKeys());
  }

  @Override
  public void changeFieldDomain(final String nodeName, final String tableName, final DomainId type, final int length) {
    qb.clear();
    qb.changeFieldTypeOfData(nodeName, tableName, type, length);
    dbcon.runCypher(qb.getQuery(), qb.getKeys());
  }

  @Override
  public void changeIndexUnique(final String nodeName, final String tableName, final Boolean val) {
    qb.clear();
    qb.changeIndexType(nodeName, tableName, val ? Index.Type.XAK : Index.Type.XIE);
    dbcon.runCypher(qb.getQuery(), qb.getKeys());
    qb.clear();
    qb.changeIndexUnique(nodeName, tableName, val);
    dbcon.runCypher(qb.getQuery(), qb.getKeys());
  }

  @Override
  public void changeFieldIsPartOfPrim(final String nodeName, final String tableName, final Boolean partOfPrimaryKey) {
    qb.clear();
    int order = 0;
    if (partOfPrimaryKey) {
      final Optional<Table> t = getTable(tableName);
      if (t.isPresent()) {
        order = t.get().getXpk().getFieldList().size();
        qb.addPrimKeyRel(nodeName, tableName, order);
      }
    } else {
      qb.deletePrimKeyRel(nodeName, tableName);
    }
    dbcon.runCypher(qb.getQuery(), qb.getKeys());
    qb.clear();
  }

  @Override
  public void changeFkRelations(final String foreignKeyName, final String tableName, final String refTableName,
      final String indexName) {
    removeFkRelation(foreignKeyName, tableName);
    qb.clear();
    qb.addRelation(tableName, foreignKeyName, indexName, Relations.Type.FOREIGNKEY);
    dbcon.runCypher(qb.getQuery(), qb.getKeys());
    qb.addFkRelation(tableName, refTableName, foreignKeyName);
    dbcon.runCypher(qb.getQuery(), qb.getKeys());
  }

  @Override
  public void insertNewTable(final Table t) {
    qb.clear();
    qb.insertNewTable(t, Strings.TBL_ID);
    dbcon.runCypher(qb.getQuery(), qb.getKeys());
  }

  @Override
  public void insertNewField(final Field f) {
    qb.clear();
    qb.addNewField(f);
    dbcon.runCypher(qb.getQuery(), qb.getKeys());
  }

  @Override
  public void insertNewIndex(final Index i) {
    qb.clear();
    qb.addNewIndex(i, i.getType().equals(Index.Type.XPK) ? Type.XPK : Type.INDEX);
    dbcon.runCypher(qb.getQuery(), qb.getKeys());
  }

  @Override
  public void insertNewForeignKey(final ForeignKey f) {
    qb.clear();
    qb.addNewFk(f);
    dbcon.runCypher(qb.getQuery(), qb.getKeys());
  }

  @Override
  public void disconnectDb() {
    dbcon.disconnectDb();
  }

  /**
   * Löscht die Beziehung zu einer Fremdtabelle eines Fremdschlüssels.
   *
   * @param foreignKeyName
   *          Foreign Key Name
   * @param tableName
   *          Tabellen Name
   */
  private void removeFkRelation(final String foreignKeyName, final String tableName) {
    qb.clear();
    qb.deleteRelationship(tableName, foreignKeyName, Type.FOREIGNKEY);
    dbcon.runCypher(qb.getQuery(), qb.getKeys());
    qb.deleteRelationship(tableName, foreignKeyName, Type.REFERENCE);
    dbcon.runCypher(qb.getQuery(), qb.getKeys());
    qb.deleteConnectionBetweenTables(tableName, foreignKeyName);
    dbcon.runCypher(qb.getQuery(), qb.getKeys());
  }

  /**
   * Löscht eine Node (außer Tabelle).
   *
   * @param node
   *          das Nodeelement
   */
  private void deleteNodeElement(final Node node) {
    qb.clear();
    qb.deleteNodeWithRelations(node.getName(), node.getTable().getName());
    dbcon.runCypher(qb.getQuery(), qb.getKeys());
  }

  /**
   * Löscht eine Tabelle.
   *
   * @param tableName
   *          der Name der Tabelle
   */
  private void deleteTable(final String tableName) {
    qb.clear();
    qb.deleteTable(tableName);
    dbcon.runCypher(qb.getQuery(), qb.getKeys());
  }

  @Override
  public boolean deleteDatabase() {
    if (dbcon.isOnline()) {
      qb.clear();
      try {
        qb.deleteDatabaseConstraint(Strings.NODE_TABLE);
      } catch (final Exception e) {
        DatabaseManager.LOG.log(Level.INFO, Strings.NOCONSTRAINTTODELETE, e);
        dbcon.reconnect();
      }
      qb.clear();
      qb.deleteDatabase();
      dbcon.runCypher(qb.getQuery());
      return true;
    }
    return false;
  }

  @Override
  public void changeDataFields(final Index index) {
    qb.clear();
    qb.deleteIndexToFieldRelation(index.getTable().getName(), index.getName());
    dbcon.runCypher(qb.getQuery(), qb.getKeys());
    for (final Field f : index.getFieldList()) {
      qb.addRelation(index.getTable().getName(), index.getName(), f.getName(), Relations.Type.INDEX,
          index.getOrder(f.getName()).toString(), index.getAltName(f.getName()));
      dbcon.runCypher(qb.getQuery(), qb.getKeys());
    }
  }

  @Override
  public void deleteNode(final Node node) {
    if (node instanceof ForeignKey) {
      removeFkRelation(node.getName(), node.getTable().getName());
      deleteNodeElement(node);
    } else if (node instanceof Table) {
      deleteTable(node.getName());
    } else {
      deleteNodeElement(node);
    }
  }

  @Override
  public List<String> getFieldNameCase(final String name) {
    final List<String> ret = new ArrayList<>();
    qb.clear();
    qb.getFieldNameCase(name);
    final List<ResultUnit> names = dbcon.runCypher(qb.getQuery(), qb.getKeys());
    for (final ResultUnit n : names) {
      ret.add(n.get(Strings.IDENT_NAME));
    }
    return ret;
  }

  @Override
  public void saveComment(final String name, final String tableName, final String comment) {
    qb.clear();
    qb.changeComment(name, tableName, comment);
    dbcon.runCypher(qb.getQuery(), qb.getKeys());
  }

  @Override
  public void saveComment(final String tableName, final String comment) {
    qb.clear();
    qb.changeComment(tableName, comment);
    dbcon.runCypher(qb.getQuery(), qb.getKeys());
  }
}
