package de.mach.tools.neodesigner.database;

import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Relations;
import de.mach.tools.neodesigner.core.datamodel.Relations.Type;
import de.mach.tools.neodesigner.core.datamodel.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * erstellt Cypher Statements.
 *
 * @author Chris Deter
 *
 */
class QueryBuilder {
  private final StringBuilder query = new StringBuilder();
  private final List<Object> val = new ArrayList<>();

  /**
   * Leert den QueryBuilder.
   */
  void clear() {
    val.clear();
    query.setLength(0);
  }

  /**
   * Importiert eine Tabelle.
   *
   * @param t
   *          die Tabelle
   */
  void importTable(final Table t) {
    clear();
    insertNewTable(t, Strings.TBL_ID);
    for (final Field f : t.getData()) {
      insertNewField(f, Strings.TBL_ID);
    }
    insertNewIndex(t.getXpk(), Type.XPK, Strings.TBL_ID);
    insertIndexToFieldRelation(t.getXpk(), Type.XPK, Strings.TBL_ID);
    for (final Index i : t.getIndizies()) {
      insertNewIndex(i, Type.INDEX, Strings.TBL_ID);
      final List<ForeignKey> listOfFk = t.getForeignKeys().stream().filter(p -> p.getIndex().equals(i))
          .collect(Collectors.toList());
      if (!listOfFk.isEmpty()) {
        insertIndexToFieldRelationForFk(i, Type.INDEX, Strings.TBL_ID, listOfFk.get(0));
      } else {
        insertIndexToFieldRelation(i, Type.INDEX, Strings.TBL_ID);
      }
    }
    for (final ForeignKey fk : t.getForeignKeys()) {
      insertNewIndex(fk, Type.FOREIGNKEY, Strings.TBL_ID);
      insertForeignKeyToXif(fk, Strings.TBL_ID);
    }
  }

  /**
   * Importiert die Beziehung eines Fremdschl�ssels.
   *
   * @param i
   *          der Fremdschl�ssel
   */
  void importForeignKey(final ForeignKey i) {
    clear();
    findTable(i.getNodeOf().getName(), Strings.TBL_ID);
    findIndex(i.getName(), Strings.TBL_ID);
    findTable(i.getRefTable().getName(), Strings.TBL2_ID);
    findIndex(i.getRefTable().getXpk().getName(), Strings.TBL2_ID);
    insertForeignKeyConnection(i.getName(), Strings.TBL_ID, Strings.TBL2_ID, i.getName());
  }

  /**
   * Setzt einen Key Wert.
   *
   * @param a
   *          Key
   * @param b
   *          Value
   */
  private void putVals(final String a, final String b) {
    val.add(a);
    val.add(b);
  }

  /**
   * Setzt einen Key Wert.
   *
   * @param a
   *          Key
   * @param b
   *          Value als Boolean
   */
  private void putVals(final String a, final Boolean b) {
    val.add(a);
    val.add(b);
  }

  /**
   * Gibt alle Keys zur�ck.
   *
   * @return gibt alle Keys zur�ck
   */
  public Object[] getKeys() {
    return val.toArray();
  }

  /**
   * Gibt die Query als String zur�ck.
   *
   * @return String der Query ohne Keys
   */
  public String getQuery() {
    return query.toString().trim();
  }

  /**
   * Gibt die h�chste Foreign Key Nummer zur�ck.
   *
   * @param size
   *          Anzal der Stellen der Zahl
   */
  void getForeignKeyNumber(final int size) {
    clear();
    query.append(String.format(Strings.CYPHER_GET_FK_ID, size));
  }

  /**
   * generiert die Cypher Abfrage um alle Tabellen abzufragen.
   */
  void getAllTables() {
    clear();
    findAllTables(Strings.TBL_ID);
    returnTable(Strings.TBL_ID);
    query.append(Strings.CYPHER_ORDER_BYNAME);
  }

  /**
   * generiert die Cypher Abfrage um eine Tabelle abzufragen.
   *
   * @param tableName
   *          Name der Tabelle
   */
  void getTable(final String tableName) {
    clear();
    findTable(tableName, Strings.TBL_ID);
    returnTable(Strings.TBL_ID);
  }

  /**
   * generiert die Cypher Abfrage um die Felder einer Tabelle abzufragen.
   *
   * @param name
   *          der Tabelle
   */
  void getFieldsOfTable(final String name) {
    clear();
    findTable(name, Strings.TBL_ID);
    findFieldAfterTable(Strings.FIELD_ID);
    returnField(Strings.FIELD_ID);
    query.append(Strings.CYPHER_ORDER_BYNAME);
  }

  /**
   * generiert die Cypher Abfrage um Indizes abzufragen.
   *
   * @param name
   *          Name der Tabelle
   * @param n
   *          Typ der Beziehung
   */
  void getIndizesOfTable(final String name, final Type n, final boolean isSimpleIndex) {
    clear();
    findTable(name, Strings.TBL_ID);
    findIndexAfterTable(n, Strings.INDEX_ID);
    returnIndex(isSimpleIndex, Strings.INDEX_ID);
    query.append(Strings.CYPHER_ORDER_BYNAME);
  }

  /**
   * L�d die Fremdschl�ssel einer Tabelle
   *
   * @param name
   */
  void getForeignKeysOfTable(final String name) {
    clear();
    findTable(name, Strings.TBL_ID);
    findFkAfterTable(Strings.INDEX_ID, Strings.TBL2_ID, Strings.FIELD_ID, Strings.PRIM_REF_ID);
    returnForeignKey(Strings.INDEX_ID, Strings.TBL2_ID, Strings.FIELD_ID);
    query.append(Strings.CYPHER_ORDER_BYNAME);
  }

  /**
   * L�d die Felder eines Indexes
   *
   * @param tableName
   * @param indexName
   * @param n
   */
  void getFieldsOfIndex(final String tableName, final String indexName, final Type n) {
    clear();
    findTable(tableName, Strings.TBL_ID);
    findFieldOfIndex(indexName, n, Strings.FIELD_ID, Strings.INDEX_ID);
    returnFieldForIndex(Strings.FIELD_ID);
    query.append(Strings.CYPHER_ORDER_BYORDER);
  }

  /**
   * �ndert einen Wert eines Knotens
   *
   * @param change
   * @param value
   * @param changeEntity
   */
  private void changeSomething(final String change, final String value, final String changeEntity) {
    query.append(String.format(Strings.CYPHER_SET, change, changeEntity, Strings.VALUE_ID));
    putVals(Strings.VALUE_ID, value);
  }

  /**
   * �ndert einen Boolean Wert eines Knotens
   *
   * @param nodeName
   * @param tableName
   * @param val
   * @param changeEntity
   */
  private void changeSomethingUnique(final String nodeName, final String tableName, final boolean val,
      final String changeEntity) {
    findTable(tableName, tableName);
    findNodeAfterTable(nodeName);
    query.append(String.format(Strings.CYPHER_SET, nodeName, changeEntity, Strings.VALUE_ID));
    putVals(Strings.VALUE_ID, val);
  }

  /**
   * �ndert einen Tabellennamen.
   *
   * @param tableName
   * @param newName
   */
  void changeTableName(final String tableName, final String newName) {
    findTable(tableName, Strings.TBL_ID);
    changeSomething(Strings.TBL_ID, newName, Strings.IDENT_NAME);

  }

  /**
   * �ndert einen Knotennamen.
   *
   * @param nodeName
   * @param tableName
   * @param newName
   */
  void changeNodeName(final String nodeName, final String tableName, final String newName) {
    findTable(tableName, tableName);
    findNodeAfterTable(nodeName);
    changeSomething(nodeName, newName, Strings.IDENT_NAME);
  }

  /**
   * �ndert eine Kategorie
   *
   * @param tableName
   * @param newName
   */
  void changeTableCategory(final String tableName, final String newName) {
    findTable(tableName, Strings.TBL_ID);
    changeSomething(Strings.TBL_ID, newName, Strings.IDENT_CATEGORY);
  }

  /**
   * �ndert ein Required Feld
   *
   * @param nodeName
   * @param tableName
   * @param val
   */
  void changeFieldRequired(final String nodeName, final String tableName, final boolean val) {
    changeSomethingUnique(nodeName, tableName, val, Strings.IDENT_ISREQ);
  }

  /**
   * �ndert ein Typeofdata Feld.
   *
   * @param nodeName
   * @param tableName
   * @param type
   */
  void changeFieldTypeOfData(final String nodeName, final String tableName, final String type) {
    findTable(tableName, tableName);
    findNodeAfterTable(nodeName);
    changeSomething(nodeName, type, Strings.IDENT_TYPE);
  }

  /**
   * �ndert das Unique eines Indexes.
   *
   * @param nodeName
   * @param tableName
   * @param val
   */
  void changeIndexUnique(final String nodeName, final String tableName, final boolean val) {
    changeSomethingUnique(nodeName, tableName, val, Strings.IDENT_UNIQUE);
  }

  /**
   * �ndert den Index Type.
   *
   * @param nodeName
   * @param tableName
   * @param type
   */
  void changeIndexType(final String nodeName, final String tableName, final Index.Type type) {
    findTable(tableName, tableName);
    findNodeAfterTable(nodeName);
    query.append(String.format(Strings.CYPHER_SET, nodeName, Strings.IDENT_TYPE, Strings.PUTVAL_TYPE));
    putVals(Strings.PUTVAL_TYPE, type.toString());
  }

  /**
   * indiziert den Tabellennamen f�r schnellere Suchanfragen.
   *
   * @return
   */
  static String insertIndexOnDb() {
    return String.format(Strings.CYPHER_CREATEINDEX, Strings.NODE_TABLE);
  }

  /**
   * f�gt eine Beziehung in der Datenbank hinzu
   *
   * @param tableName
   * @param indexName
   * @param fieldName
   * @param t
   */
  void addRelation(final String tableName, final String indexName, final String fieldName, final Type t) {
    clear();
    findTable(tableName, Strings.TBL_ID);
    findNodeAfterTable(indexName);
    query.append(String.format(Strings.CYPHER_GET, Strings.TBL_ID));
    findNodeAfterTable(fieldName);
    query.append(String.format(Strings.CYPHER_MERGE, indexName, Relations.getType(t), fieldName));
  }

  /**
   * f�gt eine Beziehung in der Datenbank hinzu.
   *
   * @param tableName
   * @param indexName
   * @param fieldName
   * @param t
   * @param order
   * @param altName
   */
  void addRelation(final String tableName, final String indexName, final String fieldName, final Type t,
      final String order, final String altName) {
    clear();
    String altN = "";
    findTable(tableName, Strings.TBL_ID);
    findNodeAfterTable(indexName);
    query.append(String.format(Strings.CYPHER_GET, Strings.TBL_ID));
    findNodeAfterTable(fieldName);
    if (!"".equals(altName)) {
      putVals(indexName + Strings.PUTVAL_ALTNAME, altName);
      altN = String.format(Strings.CYPHER_REFNAME, indexName + Strings.PUTVAL_ALTNAME);
    }
    query.append(String.format(Strings.CYPHER_MERGELONG, indexName, Relations.getType(t), altN,
        indexName + Strings.PUTVAL_ORDER, fieldName));
    putVals(indexName + Strings.PUTVAL_ORDER, order);

  }

  /**
   * f�gt eine Fremdschl�sselbeziehung in der Datenbank hinzu.
   *
   * @param tableName
   * @param refTableName
   * @param foreignKeyName
   */
  void addFkRelation(final String tableName, final String refTableName, final String foreignKeyName) {
    clear();
    findTable(tableName, Strings.TBL_ID);
    findTable(refTableName, Strings.REFTABLE_ID);
    insertForeignKeyConnection(foreignKeyName, Strings.TBL_ID, Strings.REFTABLE_ID, Strings.FOREIGNKEY_ID);
  }

  /**
   * f�gt ein neues Feld ein.
   *
   * @param f
   */
  void addNewField(final Field f) {
    findTable(f.getNodeOf().getName(), Strings.TBL_ID);
    insertNewField(f, Strings.TBL_ID);
  }

  /**
   * f�gt einen neuen Index ein.
   *
   * @param i
   * @param t
   */
  void addNewIndex(final Index i, final Type t) {
    findTable(i.getNodeOf().getName(), Strings.TBL_ID);
    insertNewIndex(i, t, Strings.TBL_ID);
  }

  /**
   * f�gt einen neuen Prim�rschl�ssel ein.
   *
   * @param nodeName
   * @param tableName
   * @param order
   */
  void addPrimKeyRel(final String nodeName, final String tableName, final int order) {
    findTable(tableName, Strings.TBL_ID);
    findNodeAfterTable(nodeName);
    query.append(String.format(Strings.CYPHER_GET_PK, Strings.TBL_ID, Relations.getType(Relations.Type.XPK),
        Strings.NODE_INDEX));
    query.append(String.format(Strings.CYPHER_MERGE_PRIMKEYREL, nodeName, Relations.getType(Relations.Type.XPK),
        Strings.REL_XPK_ID + nodeName + order));
    putVals(Strings.REL_XPK_ID + nodeName + order, Integer.toString(order));
  }

  /**
   * l�scht einen Prim�rschl�ssel
   *
   * @param nodeName
   * @param tableName
   */
  void deletePrimKeyRel(final String nodeName, final String tableName) {
    findTable(tableName, Strings.TBL_ID);
    findNodeAfterTable(nodeName);
    query.append(
        String.format(Strings.CYPHER_DELETE_PRIMKEYREL, Relations.getType(Relations.Type.XPK), Strings.NODE_INDEX));
  }

  /**
   * L�scht die Beziehung zwischen Index und Feld
   *
   * @param tableName
   * @param indexName
   */
  void deleteIndexToFieldRelation(final String tableName, final String indexName) {
    findTable(tableName, Strings.TBL_ID);
    findNodeAfterTable(indexName);
    removeRelationship(Relations.Type.INDEX, Strings.NODE_FIELD);
  }

  /**
   * l�scht die verkn�pfung zwischen zwei Tabellen.
   *
   * @param tableName
   * @param foreignKeyName
   */
  void deleteConnectionBetweenTables(final String tableName, final String foreignKeyName) {
    clear();
    findTable(tableName, Strings.TBL_ID);
    query.append(String.format(Strings.CYPHER_DELETE_FKTABLEREL, Relations.getType(Relations.Type.CONNECTED),
        Strings.REL_FK_ID, Strings.TBL2_ID, Strings.NODE_TABLE));
    putVals(Strings.REL_FK_ID, foreignKeyName);
  }

  /**
   * l�scht ein Knoten mit Beziehungen.
   *
   * @param nodeName
   * @param tableName
   */
  void deleteNodeWithRelations(final String nodeName, final String tableName) {
    findTable(tableName, tableName);
    findNodeAfterTable(nodeName);
    deleteNodeWithRelations(nodeName);
  }

  /**
   * l�scht Element incl Beziehungen.
   *
   * @param name
   *          des Elements
   */
  private void deleteNodeWithRelations(final String name) {
    query.append(String.format(Strings.CYPHER_DELETE, name));
  }

  /**
   * l�scht eine Tabelle.
   *
   * @param name
   *          der Tabelle
   */
  void deleteTable(final String name) {
    findTable(name, name);
    query.append(String.format(Strings.CYPHER_DELETE_TABLEREL, Relations.getType(Relations.Type.DATA),
        Relations.getType(Relations.Type.INDEX), Relations.getType(Relations.Type.FOREIGNKEY),
        Relations.getType(Relations.Type.XPK), Relations.getType(Relations.Type.REFERENCE), Strings.DEL_ID));
    deleteNodeWithRelations(Strings.DEL_ID + Strings.COMMA + name);
  }

  /**
   * L�scht eine Beziehung zwischen zwei tabellen und dem Fremdschl�ssel.
   *
   * @param foreignKeyName
   *          Name des Fremdschl�ssels
   * @param tableName
   *          Name der Tabelle
   * @param relation
   *          Die Beziehung
   */
  void deleteRelationship(final String foreignKeyName, final String tableName, final Type relation) {
    clear();
    findTable(foreignKeyName, Strings.TBL_ID);
    findNodeAfterTable(tableName);
    removeRelationship(relation, Strings.NODE_INDEX);
  }

  /**
   * l�scht die komplette Datenbank.
   */
  void deleteDatabase() {
    query.append(String.format(Strings.CYPHER_GET, Strings.TBL_ID));
    deleteNodeWithRelations(Strings.TBL_ID);
  }

  /**
   * Findet alle Tabellen.
   *
   * @param bez
   *          Bezeichner f�r die Tabellen
   */
  private void findAllTables(final String bez) {
    query.append(String.format(Strings.CYPHER_GET, bez + Strings.COLON + Strings.NODE_TABLE));
  }

  /**
   * Findet eine Tabelle.
   *
   * @param name
   *          Name der Tabelle
   * @param bez
   *          Bezeichner der Tabelle
   */
  private void findTable(final String name, final String bez) {
    query.append(String.format(Strings.CYPHER_FIND_TABLE, bez, Strings.NODE_TABLE, bez));
    putVals(Strings.PUTVAL_TABLE + bez, name);
  }

  /**
   * Findet einen Knoten nnach einer Tabelle findet Anfrage.
   *
   * @param name
   */
  private void findNodeAfterTable(final String name) {
    query.append(String.format(Strings.CYPHER_FIND_NODEAFTERTABLE, name, name));
    putVals(name + Strings.PUTVAL_FINDNODE, name);
  }

  /**
   * Findet ein Feld nach einer Tabelle findet Anfrage.
   *
   * @param bezField
   */
  private void findFieldAfterTable(final String bezField) {
    query.append(Strings.CYPHER_ANYREL);
    findNodeAfterRelation(bezField, Strings.NODE_FIELD);
  }

  /**
   * Findet einen Index nach einer Tabelle findet Anfrage.
   *
   * @param n
   * @param bezIn
   */
  private void findIndexAfterTable(final Type n, final String bezIn) {
    query.append(String.format(Strings.CYPHER_FIND_REL, Relations.getType(n)));
    findNodeAfterRelation(bezIn, Strings.NODE_INDEX);
  }

  /**
   * Findet die Felder eines Indexes
   *
   * @param indexname
   * @param n
   * @param bezField
   * @param bezIndex
   */
  private void findFieldOfIndex(final String indexname, final Type n, final String bezField, final String bezIndex) {
    query.append(Strings.CYPHER_ANYREL);
    findNodeAfterRelation(bezField, Strings.NODE_FIELD);
    query.append(String.format(Strings.CYPHER_FINDFIELDOFINDEX, Relations.getType(n), bezIndex));
    putVals(Strings.PUTVAL_INDEXNAME, indexname);
  }

  /**
   * Findet einen Fremdschl�ssel nach einer Tabelle findet Anfrage.
   *
   * @param indexBez
   * @param refTableBez
   * @param xifBez
   * @param primRef
   */
  private void findFkAfterTable(final String indexBez, final String refTableBez, final String xifBez,
      final String primRef) {
    query.append(String.format(Strings.CYPHER_FIND_INDEX, Relations.getType(Type.INDEX), xifBez));
    query.append(String.format(Strings.CYPHER_FIND_REL2, Relations.getType(Type.FOREIGNKEY)));
    findNodeAfterRelation(indexBez, Strings.NODE_INDEX);
    query.append(
        String.format(Strings.CYPHER_FIND_REL3, Relations.getType(Type.REFERENCE), primRef, Strings.NODE_INDEX));
    query.append(String.format(Strings.CYPHER_FIND_REL4, Relations.getType(Type.XPK), refTableBez, Strings.NODE_TABLE));
  }

  /**
   * F�gt die Beziehungen f�r einen Fremdschl�ssel ein.
   *
   * @param foreignKeyName
   * @param tableBez
   * @param refTableBez
   * @param foreignKeyBez
   */
  private void insertForeignKeyConnection(final String foreignKeyName, final String tableBez, final String refTableBez,
      final String foreignKeyBez) {
    query.append(String.format(Strings.CYPHER_GET_FKN, tableBez, Relations.getType(Type.FOREIGNKEY), foreignKeyBez,
        foreignKeyBez));
    query.append(String.format(Strings.CYPHER_GET_XPK, refTableBez, Relations.getType(Type.XPK)));
    // Tabelle -Reference> REF Tabelle
    query.append(String.format(Strings.CYPHER_CREATE_TABLETOREFERENCE, tableBez,
        Relations.getType(Relations.Type.CONNECTED), foreignKeyBez, refTableBez));
    // FK -REFERENCE> PRIMARY KEY
    query.append(String.format(Strings.CYPHER_CREATE_FK_TO_REF_XPK, foreignKeyBez, Relations.getType(Type.REFERENCE)));
    putVals(Strings.PUTVAL_FKN + foreignKeyBez, foreignKeyName);
  }

  /**
   * F�gt ein Return Statement an, welches Name und Kategory zur�ck gibt.
   *
   * @param bez
   *          gibt den Tabellenbezeichner an
   */
  private void returnTable(final String bez) {
    query.append(String.format(Strings.CYPHER_RETURN_TABLE, bez, bez));
  }

  /**
   * Gibt Name, isRequired und type zur�ck.
   *
   * @param bez
   *          Bezeichner des Feldes
   */
  private void returnField(final String bez) {
    query.append(String.format(Strings.CYPHER_RETURN_FIELD, bez, bez, bez));
  }

  /**
   * gibt ein Feld f�r einen Index zur�ck
   *
   * @param bez
   */
  private void returnFieldForIndex(final String bez) {
    returnField(bez);
    query.append(Strings.CYPHER_RETURN_INDEXFIELD);
  }

  /**
   * Gibt Informationen vom Index zur�ck.
   *
   * @param isSimpleIndex
   *          Index der Unique hat?
   * @param bezIn
   *          Bezeichner f�r den Index
   */
  private void returnIndex(final boolean isSimpleIndex, final String bezIn) {
    query.append(String.format(Strings.CYPHER_RETURN_INDEX, bezIn,
        isSimpleIndex ? String.format(Strings.CYPHER_RETURN_UNIQUEINDEX, bezIn) : ""));
  }

  /**
   * Gibt Name vom ForeignKey (i), RefTable (b) und XIF (f) zur�ck.
   *
   * @param indexBez
   *          Bezeichnung ForeignKey (i)
   * @param refTableBez
   *          Bezeichnung RefTable (b)
   * @param xifBez
   *          Bezeichnung XIF (f)
   */
  private void returnForeignKey(final String indexBez, final String refTableBez, final String xifBez) {
    query.append(String.format(Strings.CYPHER_RETURN_FK, indexBez, refTableBez, xifBez));
  }

  /**
   * f�gt eine neue Tabelle ein.
   *
   * @param t
   *          Tabelle
   * @param bez
   *          Bezeichner der Tabelle
   */
  void insertNewTable(final Table t, final String bez) {
    query.append(String.format(Strings.CYPHER_INSERT_NEWTABLE, bez, Strings.NODE_TABLE, bez, bez));
    putVals(bez + Strings.PUTVAL_TABLENAME, t.getName());
    putVals(bez + Strings.PUTVAL_TABLECATEGORY, t.getCategory());
  }

  /**
   * f�gt ein neues Feld ein.
   *
   * @param f
   *          das Feld
   * @param bez
   *          Bezeichner der Tabelle
   */
  private void insertNewField(final Field f, final String bez) {
    query.append(String.format(Strings.CYPHER_INSERT_NEWFIELD, bez, f.getName(), Strings.NODE_FIELD, f.getName(),
        f.getName(), f.getName(), bez, Relations.getType(Relations.Type.DATA), bez, f.getName()));
    putVals(f.getName() + Strings.PUTVAL_NAME, f.getName());
    putVals(f.getName() + Strings.PUTVAL_TYPE, f.getTypeOfData());
    putVals(f.getName() + Strings.PUTVAL_REQ, f.isRequired());
  }

  /**
   * f�gt einen neuen Index ein.
   *
   * @param i
   *          Das Indexobjekt
   * @param t
   *          der Typ der Beziehung zur Tabelle
   * @param bez
   *          Bezeichner der Tabelle
   */
  private void insertNewIndex(final Index i, final Type t, final String bez) {
    query.append(
        String.format(Strings.CYPHER_INSERT_NEWINDEX, bez, i.getName(), Strings.NODE_INDEX, i.getName(), i.getName()));
    putVals(i.getName() + Strings.PUTVAL_NAME, i.getName());
    putVals(i.getName() + Strings.PUTVAL_TYPE, i.getType().name());
    if (t == Type.INDEX) {
      query.append(String.format(Strings.CYPHER_INSERT_UNIQUE, i.getName()));
      putVals(i.getName() + Strings.PUTVAL_UNIQUE, i.isUnique());
    }
    // Beziehung Tabelle -index> Index
    query.append(String.format(Strings.CYPHER_INSERT_INDEXREL, bez, Relations.getType(t), bez, i.getName()));
  }

  /**
   * L�scht eine Beziehung
   * 
   * @param rel
   * @param typeOfSecondNode
   */
  private void removeRelationship(final Type rel, final String typeOfSecondNode) {
    query.append(String.format(Strings.CYPHER_DELETE_REL, Relations.getType(rel), typeOfSecondNode));
  }

  /**
   * Verwendet f�r die Abfrage nach einen Knoten als Teilst�ck f�r ein Element
   * nach einer Findet Suche
   * 
   * @param name
   * @param t
   */
  private void findNodeAfterRelation(final String name, final String t) {
    query.append(String.format(Strings.CYPHER_NODE, name, t));
  }

  /**
   * Findet einen Index in einer Tabelle.
   *
   * @param indexName
   *          der Name des Indexes
   * @param bez
   *          Bezeichner der Tabelle
   */
  private void findIndex(final String indexName, final String bez) {
    query.append(String.format(Strings.CYPHER_FIND_INDEX2, bez, indexName, Strings.NODE_INDEX, indexName, bez));
    putVals(indexName + Strings.PUTVAL_NAME, indexName);
  }

  /**
   * F�gt die Beziehung zwischen Tabellen und Indizies ein.
   *
   * @param i
   *          Index
   * @param t
   *          Typ der Beziehung
   * @param bez
   *          Bezeichner f�r den Index
   */
  private void insertIndexToFieldRelation(final Index i, final Type t, final String bez) {
    for (final Field field : i.getFieldList()) {
      final String order = i.getOrder(field.getName());
      query.append(String.format(Strings.CYPHER_INSERT_INDEXTOFIELD, bez, i.getName(), Relations.getType(t),
          bez + i.getName() + order, bez, field.getName()));
      putVals(Strings.PUTVAL_R + bez + i.getName() + order + Strings.PUTVAL_ORDER, order);
    }
  }

  /**
   * F�gt die Beziehung zwischen Tabellen und Indizies ein.
   *
   * @param i
   *          der Index
   * @param t
   *          der Typ
   * @param bez
   *          Bezeichner der Tabelle
   */
  private void insertIndexToFieldRelationForFk(final Index i, final Type t, final String bez, final ForeignKey fk) {
    for (final Field field : i.getFieldList()) {
      query.append(String.format(Strings.CYPHER_INSERT_INDEXTOFIELDFORFK, bez, i.getName(), Relations.getType(t),
          bez + fk.getName(), field.getName(), bez + fk.getName() + fk.getOrder(field.getName()), bez,
          field.getName()));
      putVals(Strings.PUTVAL_R + bez + fk.getName() + Strings.UNDERSCORE + field.getName(),
          fk.getAltName(field.getName()));
      putVals(Strings.PUTVAL_R + bez + fk.getName() + fk.getOrder(field.getName()) + Strings.PUTVAL_ORDER,
          fk.getOrder(field.getName()));
    }
  }

  /**
   * F�gt die Beziehung von ForeignKey und XIF Schl�ssel ein.
   *
   * @param i
   *          der ForeignKey
   * @param bez
   *          Bezeichner der Tabelle
   */
  private void insertForeignKeyToXif(final ForeignKey i, final String bez) {
    // FK -ForeignKey> XIF
    query.append(String.format(Strings.CYPHER_FKTOXIF, bez, i.getName(), Relations.getType(Relations.Type.FOREIGNKEY),
        bez, i.getIndex().getName()));
  }

  @Override
  public String toString() {
    final Object[] v = getKeys();
    final String q = getQuery();
    return QueryBuilder.queryToString(v, q);
  }

  /**
   * generiert eine String Representation der Query f�r Debug Zwecke.
   *
   * @param v
   *          die Query ohne Paramaeter
   * @param q
   *          die Parameter der Query
   * @return fertige Query wie sie an die Datenbank gehen w�rde
   */
  static String queryToString(final Object[] v, final String qw) {
    final int len = v.length;
    String q = qw;
    for (int i = 0; i < len; i = i + 2) {
      final String str = Strings.BRACKETSOPEN + ((String) v[i]).trim() + Strings.BRACKETSCLOSED;
      if (v[i + 1] instanceof String) {
        q = q.replaceAll(str, Strings.QUOUTE + (String) v[i + 1] + Strings.QUOUTE);
      } else {
        q = q.replaceAll(str, Strings.QUOUTE + (boolean) v[i + 1] + Strings.QUOUTE);
      }
    }
    return q;
  }
}
