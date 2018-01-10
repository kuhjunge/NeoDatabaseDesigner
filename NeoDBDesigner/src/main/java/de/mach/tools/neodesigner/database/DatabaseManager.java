package de.mach.tools.neodesigner.database;

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
import de.mach.tools.neodesigner.database.Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Verwaltet die Datenbankverbindung und stellt mithilfe des QueryBuilders die
 * Cypher Statements für de Datenbank zusammen.
 *
 * @author Chris Deter
 *
 */
public class DatabaseManager implements DatabaseConnection {
  private final QueryBuilder qb = new QueryBuilder();
  private final DatabaseConnector dbcon;

  /**
   * Konstruktor
   *
   * @param dbc
   */
  public DatabaseManager(final DatabaseConnector dbc) {
    dbcon = dbc;
  }

  /**
   * Konstruktor
   *
   * @param dbc
   * @param addr
   * @param nutzername
   * @param pw
   */
  public DatabaseManager(final DatabaseConnector dbc, final String addr, final String nutzername, final String pw) {
    dbcon = dbc;
    connectDb(addr, nutzername, pw);
  }

  @Override
  public boolean isOnline() {
    return dbcon.isOnline();
  }

  @Override
  public boolean connectDb(final String addr, final String nutzername, final String pw) {
    return dbcon.connectDb(addr, nutzername, pw);
  }

  @Override
  public void createIndexOnDb() {
    dbcon.runCypher(QueryBuilder.insertIndexOnDb());
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
    if (dbcon.isOnline()) {
      final QueryBuilder qb = new QueryBuilder();
      qb.getAllTables();
      for (final ResultUnit record : dbcon.runCypher(qb.getQuery())) {
        l.add(record.get("name"));
      }
    }
    return l;
  }

  @Override
  public Table getTable(final String name) {
    final QueryBuilder qb = new QueryBuilder();
    qb.getTable(name);
    for (final ResultUnit record : dbcon.runCypher(qb.getQuery(), qb.getKeys())) {
      return getTableFromQuery(record);
    }
    return null;
  }

  @Override
  public List<Table> getAllTables() {
    final List<Table> tables = new ArrayList<>();
    qb.getAllTables();
    for (final ResultUnit record : dbcon.runCypher(qb.getQuery())) {
      tables.add(getTableFromQuery(record));
    }
    for (final Table t : tables) {
      t.getForeignKeys().addAll(getForeignKeysFromTable(t, tables));
    }
    return tables;
  }

  /**
   * Läd eine Tabelle aus einer Query
   *
   * @param record
   * @return
   */
  private Table getTableFromQuery(final ResultUnit record) {
    final Table t = new TableImpl(record.get(Strings.IDENT_NAME));
    t.setCategory(record.get(Strings.IDENT_CATEGORY));
    t.getData().addAll(getFields(getFieldFromTable(t), t));
    final List<Index> li = getIndiziesFromTable(t, Type.XPK);
    if (!li.isEmpty()) {
      t.setXpk(li.get(0));
      for (final Field f : li.get(0).getFieldList()) {
        t.getData().get(t.getData().indexOf(new FieldImpl(f.getName()))).setPartOfPrimaryKey(true);
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
   * Läd die Felder einer Tabelle aus der Datenbank
   *
   * @param t
   * @return
   */
  private List<ResultUnit> getFieldFromTable(final Table t) {
    qb.getFieldsOfTable(t.getName());
    return dbcon.runCypher(qb.getQuery(), qb.getKeys());
  }

  /**
   * Verarbeitet die Felder die aus der Datenbank kommen.
   *
   * @param fieldRes
   * @param origin
   * @return
   */
  private List<Field> getFields(final List<ResultUnit> fieldRes, final Node origin) {
    final List<Field> fields = new ArrayList<>();
    for (final ResultUnit record : fieldRes) {
      fields.add(createFieldFromQuery(record, origin));
    }
    return fields;
  }

  /**
   * Läd die Felder aus einer existierenden Tabelle
   *
   * @param fieldRes
   * @param origin
   * @param i
   */
  private void getExistingFields(final List<ResultUnit> fieldRes, final Table origin, final Index i) {
    for (final ResultUnit record : fieldRes) {
      final Field f = origin.getData().get(origin.getData().indexOf(new FieldImpl(record.get(Strings.IDENT_NAME))));
      i.addField(f, record.get(Strings.IDENT_ALTNAME));
    }
  }

  /**
   * Erstellt ein Field aus den Informationen der Datenbank
   *
   * @param recordField
   * @param origin
   * @return
   */
  private Field createFieldFromQuery(final ResultUnit recordField, final Node origin) {
    return new FieldImpl(recordField.get(Strings.IDENT_NAME), recordField.get(Strings.IDENT_TYPE),
        recordField.getBoolean(Strings.IDENT_ISREQ), origin);
  }

  /**
   * Läd die Indizes aus der Datenbank
   *
   * @param name
   * @param n
   * @return
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
   * @param reltype
   * @param rec
   * @return
   */
  private Index createIndexFromQuery(final Table name, final Type reltype, final ResultUnit rec) {
    final Index index = new IndexImpl(rec.get(Strings.IDENT_NAME), name);
    getExistingFields(getFieldsOfIndexFromDb(name, reltype, index), name, index);
    return index;
  }

  /**
   * Läd die Felder eines Indexes aus der Datenbank.
   *
   * @param name
   * @param n
   * @param i
   * @return
   */
  private List<ResultUnit> getFieldsOfIndexFromDb(final Node name, final Type n, final Index i) {
    qb.getFieldsOfIndex(name.getName(), i.getName(), n);
    return dbcon.runCypher(qb.getQuery(), qb.getKeys());
  }

  /**
   * Läd die Fremdschlüssel aus der DAtenbank
   *
   * @param name
   * @param tableList
   * @return
   */
  private List<ForeignKey> getForeignKeysFromTable(final Table name, final List<Table> tableList) {
    final List<ForeignKey> fk = new ArrayList<>();
    qb.getForeignKeysOfTable(name.getName());
    for (final ResultUnit record : dbcon.runCypher(qb.getQuery(), qb.getKeys())) {
      fk.add(createForeignKeysFromQuery(name, record, tableList));
    }
    return fk;
  }

  /**
   * erstellt einen Fremdschlüssel aus den Informationen aus der Datenbank
   *
   * @param name
   * @param rec
   * @param tableList
   * @return
   */
  private ForeignKey createForeignKeysFromQuery(final Table name, final ResultUnit rec, final List<Table> tableList) {
    final ForeignKey fk = new ForeignKeyImpl(rec.get(Strings.IDENT_NAME), name);
    fk.setUnique(true);
    final int refTabIndex = tableList.indexOf(new TableImpl(rec.get("refTable")));
    final Table refTable = tableList.get(refTabIndex);
    fk.setRefTable(refTable);
    fk.setIndex(name.getIndizies().stream().filter(p -> p.equals(new IndexImpl(rec.get("xif"), name)))
        .collect(Collectors.toList()).get(0));
    return fk;
  }

  @Override
  public void changeTableName(final String tableName, final String newName, final String xpkName) {
    final QueryBuilder qb = new QueryBuilder();
    qb.changeTableName(tableName, newName);
    dbcon.runCypher(qb.getQuery(), qb.getKeys());
    final Table t = getTable(newName);
    changeNodeNameFromTable(t.getXpk().getName(), newName, xpkName);
  }

  @Override
  public void changeTableCategory(final String tableName, final String newName) {
    final QueryBuilder qb = new QueryBuilder();
    qb.changeTableCategory(tableName, newName);
    dbcon.runCypher(qb.getQuery(), qb.getKeys());
  }

  @Override
  public void changeNodeNameFromTable(final String nodeName, final String tableName, final String newName) {
    final QueryBuilder qb = new QueryBuilder();
    qb.changeNodeName(nodeName, tableName, newName);
    dbcon.runCypher(qb.getQuery(), qb.getKeys());
  }

  @Override
  public void changeFieldRequired(final String nodeName, final String tableName, final Boolean val) {
    final QueryBuilder qb = new QueryBuilder();
    qb.changeFieldRequired(nodeName, tableName, val);
    dbcon.runCypher(qb.getQuery(), qb.getKeys());
  }

  @Override
  public void changeFieldTypeOfData(final String nodeName, final String tableName, final String type) {
    final QueryBuilder qb = new QueryBuilder();
    qb.changeFieldTypeOfData(nodeName, tableName, type);
    dbcon.runCypher(qb.getQuery(), qb.getKeys());
  }

  @Override
  public void changeIndexUnique(final String nodeName, final String tableName, final Boolean val) {
    final QueryBuilder qb = new QueryBuilder();
    qb.changeIndexType(nodeName, tableName, val ? Index.Type.XAK : Index.Type.XIE);
    dbcon.runCypher(qb.getQuery(), qb.getKeys());
    qb.clear();
    qb.changeIndexUnique(nodeName, tableName, val);
    dbcon.runCypher(qb.getQuery(), qb.getKeys());
  }

  @Override
  public void changeFieldIsPartOfPrim(final String nodeName, final String tableName, final Boolean partOfPrimaryKey) {
    final QueryBuilder qb = new QueryBuilder();
    if (partOfPrimaryKey) {
      final Table t = getTable(tableName);
      qb.addPrimKeyRel(nodeName, tableName, t.getXpk().getFieldList().size());
    } else {
      qb.deletePrimKeyRel(nodeName, tableName);
    }
    dbcon.runCypher(qb.getQuery(), qb.getKeys());
  }

  @Override
  public void changeFkRelations(final String foreignKeyName, final String tableName, final String refTableName,
      final String indexName) {
    removeFkRelation(foreignKeyName, tableName);
    final QueryBuilder qb = new QueryBuilder();
    qb.addRelation(tableName, foreignKeyName, indexName, Relations.Type.FOREIGNKEY);
    dbcon.runCypher(qb.getQuery(), qb.getKeys());
    qb.addFkRelation(tableName, refTableName, foreignKeyName);
    dbcon.runCypher(qb.getQuery(), qb.getKeys());
  }

  @Override
  public void insertNewTable(final Table t) {
    final QueryBuilder qb = new QueryBuilder();
    qb.insertNewTable(t, Strings.TBL_ID);
    dbcon.runCypher(qb.getQuery(), qb.getKeys());
  }

  @Override
  public void insertNewField(final Field f) {
    final QueryBuilder qb = new QueryBuilder();
    qb.addNewField(f);
    dbcon.runCypher(qb.getQuery(), qb.getKeys());
  }

  @Override
  public void insertNewIndex(final Index i) {
    final QueryBuilder qb = new QueryBuilder();
    qb.addNewIndex(i, i.getType().equals(Index.Type.XPK) ? Type.XPK : Type.INDEX);
    dbcon.runCypher(qb.getQuery(), qb.getKeys());
  }

  @Override
  public void insertNewForeignKey(final ForeignKey f) {
    final QueryBuilder qb = new QueryBuilder();
    qb.addNewIndex(f, Type.FOREIGNKEY);
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
   * @param tableName
   */
  private void removeFkRelation(final String foreignKeyName, final String tableName) {
    final QueryBuilder qb = new QueryBuilder();
    qb.deleteRelationship(foreignKeyName, tableName, Type.FOREIGNKEY);
    dbcon.runCypher(qb.getQuery(), qb.getKeys());
    qb.deleteRelationship(foreignKeyName, tableName, Type.REFERENCE);
    dbcon.runCypher(qb.getQuery(), qb.getKeys());
    qb.deleteConnectionBetweenTables(tableName, foreignKeyName);
    dbcon.runCypher(qb.getQuery(), qb.getKeys());
  }

  /**
   * Löscht eine Node (außer Tabelle)
   * 
   * @param node
   */
  private void deleteNodeElement(final Node node) {
    final QueryBuilder qb = new QueryBuilder();
    qb.deleteNodeWithRelations(node.getName(), node.getNodeOf().getName());
    dbcon.runCypher(qb.getQuery(), qb.getKeys());
  }

  /**
   * Löscht eine Tabelle
   * 
   * @param tableName
   */
  private void deleteTable(final String tableName) {
    final QueryBuilder qb = new QueryBuilder();
    qb.deleteTable(tableName);
    dbcon.runCypher(qb.getQuery(), qb.getKeys());
  }

  @Override
  public boolean deleteDatabase() {
    if (dbcon.isOnline()) {
      final QueryBuilder qb = new QueryBuilder();
      qb.deleteDatabase();
      dbcon.runCypher(qb.getQuery());
      return true;
    }
    return false;
  }

  @Override
  public void changeDataFields(final Index index) {
    final QueryBuilder qb = new QueryBuilder();
    qb.deleteIndexToFieldRelation(index.getNodeOf().getName(), index.getName());
    dbcon.runCypher(qb.getQuery(), qb.getKeys());
    for (final Field f : index.getFieldList()) {
      qb.addRelation(index.getNodeOf().getName(), index.getName(), f.getName(), Relations.Type.INDEX,
          index.getOrder(f.getName()), index.getAltName(f.getName()));
      dbcon.runCypher(qb.getQuery(), qb.getKeys());
    }
  }

  @Override
  public void deleteNode(final Node node) {
    if (node instanceof ForeignKey) {
      removeFkRelation(node.getName(), node.getNodeOf().getName());
      deleteNodeElement(node);
    } else if (node instanceof Table) {
      deleteTable(node.getName());
    } else {
      deleteNodeElement(node);
    }
  }
}