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
import de.mach.tools.neodesigner.core.datamodel.Relations;
import de.mach.tools.neodesigner.core.datamodel.Relations.Type;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.database.Strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * erstellt Cypher Statements.
 *
 * @author Chris Deter
 *
 */
public class QueryBuilder {
  private final StringBuilder query = new StringBuilder();
  private final List<Object> val = new ArrayList<>();
  private final Map<String, String> nameTransl = new HashMap<>();

  /**
   * Leert den QueryBuilder.
   */
  void clear() {
    val.clear();
    query.setLength(0);
    nameTransl.clear();
  }

  /**
   * Importiert eine Tabelle.
   *
   * @param t
   *          die Tabelle
   */
  public void importTable(final Table t) {
    clear();
    int fieldCounter = 1;
    int xifCounter = 1;
    int fkCounter = 1;
    insertNewTable(t, Strings.TBL_ID);
    for (final Field f : t.getFields()) {
      final String fieldIdent = Strings.FIELD_ID + fieldCounter++;
      nameTransl.put(f.getName(), fieldIdent);
      insertNewField(f, fieldIdent, Strings.TBL_ID, Strings.SPACE + Strings.CYPHER_CREATE);
    }
    insertNewIndex(t.getXpk(), Type.XPK, Strings.TBL_ID, Strings.PRIM_REF_ID);
    insertIndexToFieldRelation(t.getXpk(), Type.XPK, Strings.TBL_ID, Strings.PRIM_REF_ID, nameTransl);
    for (final Index i : t.getIndizies()) {
      final String indexName = Strings.INDEX_ID + xifCounter++;
      nameTransl.put(i.getName(), indexName);
      insertNewIndex(i, Type.INDEX, Strings.TBL_ID, indexName);
      final List<ForeignKey> listOfFk = t.getForeignKeys().stream().filter(p -> p.getIndex().equals(i))
          .collect(Collectors.toList());
      if (!listOfFk.isEmpty()) {
        insertIndexToFieldRelationForFk(i, Type.INDEX, indexName, Strings.TBL_ID, listOfFk.get(0), nameTransl);
      } else {
        insertIndexToFieldRelation(i, Type.INDEX, Strings.TBL_ID, indexName, nameTransl);
      }
    }
    for (final ForeignKey fk : t.getForeignKeys()) {
      insertNewFk(fk, Type.FOREIGNKEY, Strings.TBL_ID, Strings.FOREIGNKEY_ID + fkCounter++,
          nameTransl.get(fk.getIndex().getName()));
    }
  }

  /**
   * Importiert die Beziehung eines Fremdschlüssels.
   *
   * @param i
   *          der Fremdschlüssel
   */
  public void importForeignKey(final ForeignKey i) {
    addFkRelation(i.getTable().getName(), i.getRefTable().getName(), i.getName());
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
   * Gibt alle Keys zurück.
   *
   * @return gibt alle Keys zurück
   */
  public Object[] getKeys() {
    return val.toArray();
  }

  /**
   * Gibt die Query als String zurück.
   *
   * @return String der Query ohne Keys
   */
  public String getQuery() {
    return query.toString().trim();
  }

  /**
   * Gibt die höchste Foreign Key Nummer zurück.
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
   * generiert die Cypher Abfrage um alle Tabellen abzufragen.
   */
  void countNodes(final String nodeType) {
    clear();
    query.append(String.format(Strings.CYPHER_GET,
        Strings.TBL_ID + (nodeType.length() > 0 ? Strings.COLON + nodeType : Strings.EMPTYSTRING)));
    query.append(String.format(Strings.CYPHER_COUNT, Strings.TBL_ID));
  }

  /**
   * generiert die Cypher Abfrage um alle Tabellen abzufragen.
   */
  void countRelation(final String relation) {
    clear();
    query.append(String.format(Strings.CYPHER_GET, Strings.TBL_ID));
    query.append(String.format(Strings.CYPHER_COUNTREL, Strings.REL_ID,
        relation.length() > 0 ? Strings.COLON + relation : Strings.EMPTYSTRING, Strings.TBL2_ID));
    query.append(String.format(Strings.CYPHER_COUNT, Strings.REL_ID));
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
    getOne();
  }

  /**
   * Reduziert das Ergebnis auf einen Rückgabewert.
   */
  void getOne() {
    query.append(Strings.LIMIT_TO_ONE);
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
   * Läd die Fremdschlüssel einer Tabelle.
   *
   * @param name
   *          Name der Tabelle
   */
  void getForeignKeysOfTable(final String name) {
    clear();
    findTable(name, Strings.TBL_ID);
    findFkAfterTable(Strings.INDEX_ID, Strings.TBL2_ID, Strings.FIELD_ID, Strings.PRIM_REF_ID);
    returnForeignKey(Strings.INDEX_ID, Strings.TBL2_ID, Strings.FIELD_ID);
    query.append(Strings.CYPHER_ORDER_BYNAME);
  }

  /**
   * Fragt alle Fremdschlüssel ab, die auf diese Tabelle referenzieren.
   *
   * @param name
   *          der Tabelle
   */
  void getRefForeignKeysOfTable(final String name) {
    clear();
    findTable(name, Strings.TBL_ID); // match (Table{name:"Benutzer"})
    query.append(Strings.CYPHER_GET_REFERENCES);
    query.append(Strings.CYPHER_GET_REFERENCES_TABLE_RETURN);
    query.append(Strings.CYPHER_ORDER_BYNAME);
  }

  /**
   * Läd die Felder eines Indexes.
   *
   * @param tableName
   *          Name der Tabelle
   * @param indexName
   *          Name des Indexes
   * @param n
   *          Typ der Beziehung
   */
  void getFieldsOfIndex(final String tableName, final String indexName, final Type n) {
    clear();
    findTable(tableName, Strings.TBL_ID);
    findFieldOfIndex(indexName, n, Strings.FIELD_ID, Strings.INDEX_ID);
    returnFieldForIndex(Strings.FIELD_ID);
    query.append(Strings.CYPHER_ORDER_BYORDER);
  }

  /**
   * Fragt eine Liste von Case Insensitive Namen aus der Datenbank ab.
   *
   * @param name
   *          der zu prüfende Name
   */
  void getFieldNameCase(final String name) {
    query.append(String.format(Strings.CYPHER_GET_INCASESENSITIVEFIELDNAME, name));
  }

  /**
   * Ändert einen Wert eines Knotens.
   *
   * @param change
   *          das Element
   * @param value
   *          der neue Wert
   * @param changeEntity
   *          die Element Eigenschaft
   */
  private void changeSomething(final String change, final String value, final String changeEntity) {
    query.append(String.format(Strings.CYPHER_SET, change, changeEntity, change + Strings.VALUE_ID));
    putVals(change + Strings.VALUE_ID, value);
  }

  /**
   * Ändert einen Boolean Wert eines Knotens.
   *
   * @param nodeName
   *          Knotenname
   * @param tableName
   *          Tabellenname
   * @param val
   *          der Wert
   * @param changeEntity
   *          die zu änderende Eigenschaft
   */
  private void changeSomethingUnique(final String nodeName, final String tableName, final boolean val,
      final String changeEntity) {
    findTable(tableName, tableName);
    findNodeAfterTable(nodeName);
    query.append(String.format(Strings.CYPHER_SET, nodeName, changeEntity, Strings.VALUE_ID));
    putVals(Strings.VALUE_ID, val);
  }

  /**
   * Ändert einen Tabellennamen.
   *
   * @param tableName
   *          der Tabellenname
   * @param newName
   *          der neue Tabellenname
   */
  void changeTableName(final String tableName, final String newName, final String xpkName) {
    findTable(tableName, Strings.TBL_ID);
    query.append(String.format(Strings.CYPHER_FIND_INDEX, Relations.getType(Type.XPK), Strings.INDEXTYPE_XPK));
    changeSomething(Strings.TBL_ID, newName, Strings.IDENT_NAME);
    changeSomething(Strings.INDEXTYPE_XPK, xpkName, Strings.IDENT_NAME);
  }

  /**
   * Ändert einen Knotennamen.
   *
   * @param nodeName
   *          Name des Knoten
   * @param tableName
   *          Name der Tabelle
   * @param newName
   *          neuer Name
   */
  void changeNodeName(final String nodeName, final String tableName, final String newName) {
    findTable(tableName, tableName);
    findNodeAfterTable(nodeName);
    changeSomething(nodeName, newName, Strings.IDENT_NAME);
  }

  /**
   * Ändert eine Kategorie.
   *
   * @param tableName
   *          Name der Tabelle
   * @param newName
   *          Name der Kategorie
   */
  void changeTableCategory(final String tableName, final String newName) {
    findTable(tableName, Strings.TBL_ID);
    changeSomething(Strings.TBL_ID, newName, Strings.IDENT_CATEGORY);
  }

  /**
   * ändert ein Required Feld.
   *
   * @param nodeName
   *          Name des Feldes
   * @param tableName
   *          Name der Tabelle
   * @param val
   *          Name des Wertes
   */
  void changeFieldRequired(final String nodeName, final String tableName, final boolean val) {
    changeSomethingUnique(nodeName, tableName, val, Strings.IDENT_ISREQ);
  }

  /**
   * ändert ein Typeofdata Feld.
   *
   * @param nodeName
   *          Name des Feldes
   * @param tableName
   *          Name der Tabelle
   * @param type
   *          Name des Types
   */
  void changeFieldTypeOfData(final String nodeName, final String tableName, final DomainId type, final int length) {
    final String domainExtra = length > 0 ? Strings.COLON + length : Strings.EMPTYSTRING;
    findTable(tableName, tableName);
    findNodeAfterTable(nodeName);
    changeSomething(nodeName, Domain.getName(type) + domainExtra, Strings.IDENT_TYPE);
  }

  /**
   * Ändert das Unique eines Indexes.
   *
   * @param nodeName
   *          Name des Indexes
   * @param tableName
   *          Name der Tabelle
   * @param val
   *          neuer Wert
   */
  void changeIndexUnique(final String nodeName, final String tableName, final boolean val) {
    changeSomethingUnique(nodeName, tableName, val, Strings.IDENT_UNIQUE);
  }

  /**
   * Ändert den Index Type.
   *
   * @param nodeName
   *          Name des Indexes
   * @param tableName
   *          Name der Tabelle
   * @param type
   *          des Indexes
   */
  void changeIndexType(final String nodeName, final String tableName, final Index.Type type) {
    findTable(tableName, tableName);
    findNodeAfterTable(nodeName);
    query.append(String.format(Strings.CYPHER_SET, nodeName, Strings.IDENT_TYPE, Strings.PUTVAL_TYPE));
    putVals(Strings.PUTVAL_TYPE, type.toString());
  }

  /**
   * Ändert den Namen der Verknüpfung von allen Feldern die mit dem Feld des
   * Primärschlüssels verbunden sind.
   *
   * @param tableName
   *          Der Tabllenname
   * @param actualNodeName
   *          der neue Feldname
   * @param oldRelNodeName
   *          der alte Feldname
   */
  void changePrimFieldNameRelation(final String tableName, final String actualNodeName, final String oldRelNodeName) {
    findTable(tableName, Strings.TBL_ID);
    query.append(Strings.CYPHER_GET_REFERENCES);
    query.append(String.format(Strings.CYPHER_GET_REFERENCES_FIELD, Strings.REL_ID, Relations.getType(Type.INDEX),
        Strings.IDENT_REFNAME, Strings.PUTVAL_ALTNAME, Strings.FIELD_ID, Strings.NODE_FIELD));
    // SET rel.refname="Mandant"
    putVals(Strings.PUTVAL_ALTNAME, oldRelNodeName);
    changeSomething(Strings.REL_ID, actualNodeName, Strings.IDENT_REFNAME);
  }

  /**
   * Speichert ein Kommentar in einen Knoten.
   *
   * @param nodeName
   *          der Knoten
   * @param tableName
   *          die Tabelle des Knoten
   * @param comment
   *          das Kommentar
   */
  void changeComment(final String nodeName, final String tableName, final String comment) {
    findTable(tableName, tableName);
    findNodeAfterTable(nodeName);
    changeSomething(nodeName, comment, Strings.IDENT_COMMENT);
  }

  /**
   * Speichert ein Kommentar in einer Tabelle.
   *
   * @param tableName
   *          der Tabellenname
   * @param comment
   *          das Kommentar
   */
  void changeComment(final String tableName, final String comment) {
    findTable(tableName, Strings.TBL_ID);
    changeSomething(Strings.TBL_ID, comment, Strings.IDENT_COMMENT);
  }

  /**
   * indiziert den Tabellennamen für schnellere Suchanfragen.
   *
   * @return die Cypher Query für das erstellen eines Suchindexes
   */
  static String insertIndexOnDb(final String type) {
    return String.format(Strings.CYPHER_CREATEINDEX, type);
  }

  /**
   * fügt eine Beziehung zwischen einen Index und einem Feld in der Datenbank
   * hinzu.
   *
   * @param tableName
   *          der Tabellenname
   * @param indexName
   *          der Indexname
   * @param fieldName
   *          der Name des Feldes
   * @param t
   *          der Typ der Beziehung
   */
  void addRelation(final String tableName, final String indexName, final String fieldName, final Type t) {
    clear();
    findTable(tableName, Strings.TBL_ID);
    findNodeAfterTable(indexName);
    query.append(String.format(Strings.CYPHER_GET, Strings.TBL_ID));
    findNodeAfterTable(fieldName);
    query.append(String.format(Strings.CYPHER_CREATE_NODE, indexName, Relations.getType(t), fieldName));
  }

  /**
   * fügt eine Beziehung zwischen einen Index und einen Feld in der Datenbank
   * hinzu.
   *
   * @param tableName
   *          der Tabellenname
   * @param indexName
   *          der Indexname
   * @param fieldName
   *          der Feldname
   * @param t
   *          die Beziehung
   * @param order
   *          die Ordnungseigenschaft der Beziehung
   * @param altName
   *          der altName (Für XIF)
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
   * fügt eine Fremdschlüsselbeziehung in der Datenbank hinzu.
   *
   * @param tableName
   *          Name der Tabelle
   * @param refTableName
   *          Name der Ref Tabelle
   * @param foreignKeyName
   *          Name des Fremdschlüssels
   */
  void addFkRelation(final String tableName, final String refTableName, final String foreignKeyName) {
    clear();
    findTableFk(Strings.TBL_ID, tableName, Type.FOREIGNKEY, Strings.FOREIGNKEY_ID, foreignKeyName);
    findTableIndex(Strings.TBL2_ID, refTableName, Type.XPK, Strings.PRIM_REF_ID);
    insertForeignKeyConnection(foreignKeyName, Strings.TBL_ID, Strings.TBL2_ID, Strings.FOREIGNKEY_ID,
        Strings.PRIM_REF_ID);
  }

  /**
   * fügt ein neues Feld ein.
   *
   * @param f
   *          Das neue Feld
   */
  void addNewField(final Field f) {
    findTable(f.getTable().getName(), Strings.TBL_ID);
    insertNewField(f, Strings.TBL_ID + Strings.UNDERSCORE + f.getName(), Strings.TBL_ID,
        Strings.SPACE + Strings.CYPHER_CREATE);
  }

  /**
   * fügt einen neuen Index ein.
   *
   * @param i
   *          Index
   * @param t
   *          Beziehung des Indexes
   */
  void addNewIndex(final Index i, final Type t) {
    findTable(i.getTable().getName(), Strings.TBL_ID);
    insertNewIndex(i, t, Strings.TBL_ID, Strings.PRIM_REF_ID);
  }

  /**
   * fügt einen neuen ForeignKey ein.
   *
   * @param f
   *          ForeignKey
   */
  void addNewFk(final ForeignKey f) {
    findTable(f.getTable().getName(), Strings.TBL_ID);
    insertNewFk(f, Type.FOREIGNKEY, Strings.TBL_ID, Strings.TBL_ID + Strings.UNDERSCORE + f.getName(), "");
  }

  /**
   * fügt einen neuen Primärschlüssel ein.
   *
   * @param nodeName
   *          der Name des Feldes
   * @param tableName
   *          der Name der Tabelle
   * @param order
   *          die Ordnung
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
   * löscht einen Primärschlüssel.
   *
   * @param nodeName
   *          Name des Feldes
   * @param tableName
   *          Name der Tabelle
   */
  void deletePrimKeyRel(final String nodeName, final String tableName) {
    findTable(tableName, Strings.TBL_ID);
    findNodeAfterTable(nodeName);
    query.append(
        String.format(Strings.CYPHER_DELETE_PRIMKEYREL, Relations.getType(Relations.Type.XPK), Strings.NODE_INDEX));
  }

  /**
   * Löscht die Beziehung zwischen Index und Feld.
   *
   * @param tableName
   *          Name der Tabelle
   * @param indexName
   *          Name des Indexes
   */
  void deleteIndexToFieldRelation(final String tableName, final String indexName) {
    findTable(tableName, Strings.TBL_ID);
    findNodeAfterTable(indexName);
    removeRelationship(Relations.Type.INDEX, Strings.NODE_FIELD);
  }

  /**
   * löscht die verknüpfung zwischen zwei Tabellen.
   *
   * @param tableName
   *          Name der Tabelle
   * @param foreignKeyName
   *          Name des Fremdschlüssels
   */
  void deleteConnectionBetweenTables(final String tableName, final String foreignKeyName) {
    clear();
    findTable(tableName, Strings.TBL_ID);
    query.append(String.format(Strings.CYPHER_DELETE_FKTABLEREL, Relations.getType(Relations.Type.CONNECTED),
        Strings.REL_FK_ID, Strings.TBL2_ID, Strings.NODE_TABLE));
    putVals(Strings.REL_FK_ID, foreignKeyName);
  }

  /**
   * löscht ein Knoten mit Beziehungen.
   *
   * @param nodeName
   *          Name des Knotens
   * @param tableName
   *          Name der Tabelle
   */
  void deleteNodeWithRelations(final String nodeName, final String tableName) {
    findTable(tableName, tableName);
    findNodeAfterTable(nodeName);
    deleteNodeWithRelations(nodeName);
  }

  /**
   * löscht Element incl Beziehungen.
   *
   * @param name
   *          des Elements
   */
  private void deleteNodeWithRelations(final String name) {
    query.append(String.format(Strings.CYPHER_DELETE, name));
  }

  /**
   * löscht eine Tabelle.
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
   * Löscht eine Beziehung zwischen zwei tabellen und dem Fremdschlüssel.
   *
   * @param tableName
   *          Name der Tabelle
   * @param foreignKeyName
   *          Name des Fremdschlüssels
   * @param relation
   *          Die Beziehung
   */
  void deleteRelationship(final String tableName, final String foreignKeyName, final Type relation) {
    clear();
    findTable(tableName, Strings.TBL_ID);
    findNodeAfterTable(Strings.FOREIGNKEY_ID, foreignKeyName, Type.FOREIGNKEY);
    removeRelationship(relation, Strings.NODE_INDEX);
  }

  /**
   * löscht die komplette Datenbank.
   */
  void deleteDatabase() {
    query.append(String.format(Strings.CYPHER_GET, Strings.TBL_ID));
    deleteNodeWithRelations(Strings.TBL_ID);
  }

  /**
   * Findet alle Tabellen.
   *
   * @param bez
   *          Bezeichner für die Tabellen
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
    query.append(String.format(Strings.CYPHER_FIND_TABLE, Strings.CYPHER_MATCH, bez, Strings.NODE_TABLE, bez));
    putVals(Strings.PUTVAL_TABLE + bez, name);
  }

  /**
   * Findet einen Knoten nnach einer Tabelle findet Anfrage.
   *
   * @param name
   *          des Knoten
   */
  private void findNodeAfterTable(final String name) {
    query.append(String.format(Strings.CYPHER_FIND_NODEAFTERTABLE, "", name, name));
    putVals(name + Strings.PUTVAL_FINDNODE, name);
  }

  private void findNodeAfterTable(final String identifier, final String name, final Type n) {
    query.append(String.format(Strings.CYPHER_FIND_NODEAFTERTABLE, Strings.COLON + Relations.getType(n), identifier,
        identifier));
    putVals(identifier + Strings.PUTVAL_FINDNODE, name);
  }

  /**
   * Findet ein Feld nach einer Tabelle findet Anfrage.
   *
   * @param bezField
   *          Feld Bezeichner
   */
  private void findFieldAfterTable(final String bezField) {
    query.append(Strings.CYPHER_ANYREL);
    findNodeAfterRelation(bezField, Strings.NODE_FIELD);
  }

  /**
   * Findet einen Index nach einer Tabelle findet Anfrage.
   *
   * @param n
   *          Typ der Beziehung
   * @param bezIn
   *          Bezeichner Index
   */
  private void findIndexAfterTable(final Type n, final String bezIn) {
    query.append(String.format(Strings.CYPHER_FIND_REL, Relations.getType(n)));
    findNodeAfterRelation(bezIn, Strings.NODE_INDEX);
  }

  /**
   * Findet die Felder eines Indexes.
   *
   * @param indexname
   *          Name des Indexes
   * @param n
   *          Typ der Beziehung
   * @param bezField
   *          Bezeichner Feld
   * @param bezIndex
   *          Bezeichner Index
   */
  private void findFieldOfIndex(final String indexname, final Type n, final String bezField, final String bezIndex) {
    query.append(Strings.CYPHER_ANYREL);
    findNodeAfterRelation(bezField, Strings.NODE_FIELD);
    query.append(String.format(Strings.CYPHER_FINDFIELDOFINDEX, Relations.getType(n), bezIndex));
    putVals(Strings.PUTVAL_INDEXNAME, indexname);
  }

  /**
   * Findet einen Fremdschlüssel nach einer Tabelle findet Anfrage.
   *
   * @param indexBez
   *          Index Bezeichner
   * @param refTableBez
   *          Ref Table Bezeichner
   * @param xifBez
   *          XIF Bezeichner
   * @param primRef
   *          Primärschlüssel Bezeichner
   */
  private void findFkAfterTable(final String indexBez, final String refTableBez, final String xifBez,
      final String primRef) {
    query.append(String.format(Strings.CYPHER_FIND_INDEX, Relations.getType(Type.INDEX), xifBez));
    query.append(String.format(Strings.CYPHER_FIND_REL2, Relations.getType(Type.FOREIGNKEY)));
    findNodeAfterRelation(indexBez, Strings.NODE_FK);
    query.append(
        String.format(Strings.CYPHER_FIND_REL3, Relations.getType(Type.REFERENCE), primRef, Strings.NODE_INDEX));
    query.append(String.format(Strings.CYPHER_FIND_REL4, Relations.getType(Type.XPK), refTableBez, Strings.NODE_TABLE));
  }

  /**
   * findet eine Tabelle mit Index.
   *
   * @param tblId
   *          Tabellen Bezeichner
   * @param name
   *          Name der Tabelle
   * @param t
   *          Typ der Beziehung
   * @param indexId
   *          Index Bezeichner
   * @param indexname
   *          Indexname
   */
  private void findTableFk(final String tblId, final String name, final Type t, final String indexId,
      final String indexname) {
    findTable(name, tblId);
    query.append(
        String.format("-[:%s]->(%s:%s{name:{idn%s}})", Relations.getType(t), indexId, Strings.NODE_FK, indexId));
    putVals(Strings.PUTVAL_TABLE + tblId, name);
    putVals(Strings.PUTVAL_INDEXNAME + indexId, indexname);
  }

  /**
   * findet ein Index nach einer Tabelle.
   *
   * @param tblId
   *          ID der Tabelle
   * @param name
   *          Name der Tabelle
   * @param t
   *          Txp der Beziehung
   * @param indexID
   *          ID des Indexes
   */
  private void findTableIndex(final String tblId, final String name, final Type t, final String indexId) {
    query.append(String.format(Strings.CYPHER_FIND_TABLE, Strings.COMMA, tblId, Strings.NODE_TABLE, tblId));
    query.append(String.format(Strings.CYPHER_FIND_REL3, Relations.getType(t), indexId, Strings.NODE_INDEX));
    putVals(Strings.PUTVAL_TABLE + tblId, name);
  }

  /**
   * Fügt die Beziehungen für einen Fremdschlüssel ein.
   *
   * @param foreignKeyName
   *          Name des Foreign Keys
   * @param tableBez
   *          Bezeichnung der Tabelle
   * @param refTableBez
   *          Bezeichnung der Referenz Tabelle
   * @param foreignKeyBez
   *          Foreign Key Bezeichnung
   * @param indextypeXpk
   *          IndexTyp
   */
  private void insertForeignKeyConnection(final String foreignKeyName, final String tableBez, final String refTableBez,
      final String foreignKeyBez, final String indextypeXpk) {
    // Tabelle -Reference> REF Tabelle
    query.append(String.format(Strings.CYPHER_CREATE_TABLETOREFERENCE, tableBez,
        Relations.getType(Relations.Type.CONNECTED), foreignKeyBez, refTableBez));
    // FK -REFERENCE> PRIMARY KEY
    query.append(
        String.format(Strings.CYPHER_CREATE_NODE, foreignKeyBez, Relations.getType(Type.REFERENCE), indextypeXpk));
    putVals(Strings.PUTVAL_FKN + foreignKeyBez, foreignKeyName);
  }

  /**
   * Fügt ein Return Statement an, welches Name und Kategory zurück gibt.
   *
   * @param bez
   *          gibt den Tabellenbezeichner an
   */
  private void returnTable(final String bez) {
    query.append(String.format(Strings.CYPHER_RETURN_TABLE, bez, bez, bez));
  }

  /**
   * Gibt Name, isRequired und type zurück.
   *
   * @param bez
   *          Bezeichner des Feldes
   */
  private void returnField(final String bez) {
    query.append(String.format(Strings.CYPHER_RETURN_FIELD, bez, bez, bez, bez));
  }

  /**
   * gibt ein Feld für einen Index zurück.
   *
   * @param bez
   *          Bezeichner des Feldes
   */
  private void returnFieldForIndex(final String bez) {
    returnField(bez);
    query.append(String.format(Strings.CYPHER_RETURN_INDEXFIELD, Strings.PUTVAL_R, Strings.PUTVAL_R));
  }

  /**
   * Gibt Informationen vom Index zurück.
   *
   * @param isSimpleIndex
   *          Index der Unique hat?
   * @param bezIn
   *          Bezeichner für den Index
   */
  private void returnIndex(final boolean isSimpleIndex, final String bezIn) {
    query.append(String.format(Strings.CYPHER_RETURN_INDEX, bezIn,
        isSimpleIndex ? String.format(Strings.CYPHER_RETURN_UNIQUEINDEX, bezIn) : ""));
  }

  /**
   * Gibt Name vom ForeignKey (i), RefTable (b) und XIF (f) zurück.
   *
   * @param indexBez
   *          Bezeichnung ForeignKey (i)
   * @param refTableBez
   *          Bezeichnung RefTable (b)
   * @param xifBez
   *          Bezeichnung XIF (f)
   */
  private void returnForeignKey(final String fkBez, final String refTableBez, final String xifBez) {
    query.append(String.format(Strings.CYPHER_RETURN_FK, fkBez, refTableBez, xifBez));
  }

  /**
   * fügt eine neue Tabelle ein.
   *
   * @param t
   *          Tabelle
   * @param bez
   *          Bezeichner der Tabelle
   */
  void insertNewTable(final Table t, final String bez) {
    final String infoLine = getCommentLine(t.getComment(), bez);
    query.append(String.format(Strings.CYPHER_INSERT_NEWTABLE, bez, Strings.NODE_TABLE, bez, bez, infoLine));
    putVals(bez + Strings.PUTVAL_TABLENAME, t.getName());
    putVals(bez + Strings.PUTVAL_TABLECATEGORY, t.getCategory());
  }

  /**
   * fügt ein neues Feld ein.
   *
   * @param f
   *          das Feld
   * @param bezTab
   *          Bezeichner der Tabelle
   *
   * @param bezField
   *          bezeichner des Feldes
   * @param dbOperationType
   *          Operationstyp der DB (Merge,Create oder nichts?)
   */
  private void insertNewField(final Field f, final String bezField, final String bezTab, final String dbOperationType) {
    final String domainExtra = f.getDomainLength() > 0 ? Strings.COLON + f.getDomainLength() : Strings.EMPTYSTRING;
    final String infoLine = getCommentLine(f.getComment(), bezField);
    query.append(String.format(Strings.CYPHER_INSERT_NEWFIELD, dbOperationType, bezField, Strings.NODE_FIELD, bezField,
        bezField, bezField, infoLine, Relations.getType(Relations.Type.DATA), bezTab));
    putVals(bezField + Strings.PUTVAL_NAME, f.getName());
    putVals(bezField + Strings.PUTVAL_TYPE, Domain.getName(f.getDomain()) + domainExtra);
    putVals(bezField + Strings.PUTVAL_REQ, f.isRequired());
  }

  private String getCommentLine(final String comment, final String bez) {
    String infoLine = Strings.EMPTYSTRING;
    if (comment.length() > 1) {
      putVals(bez + Strings.PUTVAL_TABLECOMMENT, comment);
      infoLine = String.format(Strings.COMMENT, bez, Strings.PUTVAL_TABLECOMMENT);
    }
    return infoLine;
  }

  /**
   * fügt einen neuen Index ein.
   *
   * @param i
   *          Das Indexobjekt
   * @param t
   *          der Typ der Beziehung zur Tabelle
   * @param bezTbl
   *          Bezeichner der Tabelle
   */
  private void insertNewIndex(final Index i, final Type t, final String bezTbl, final String bezIndex) {
    query.append(String.format(Strings.CYPHER_INSERT_NEWINDEX, Strings.SPACE + Strings.CYPHER_CREATE, bezIndex,
        Strings.NODE_INDEX, bezIndex, bezIndex));
    putVals(bezIndex + Strings.PUTVAL_NAME, i.getName());
    putVals(bezIndex + Strings.PUTVAL_TYPE, i.getType().name());
    if (t == Type.INDEX) {
      query.append(String.format(Strings.CYPHER_INSERT_UNIQUE, bezIndex));
      putVals(bezIndex + Strings.PUTVAL_UNIQUE, i.isUnique());
    }
    // Beziehung Tabelle -index> Index
    query.append(String.format(Strings.CYPHER_INSERT_INDEXREL, Relations.getType(t), bezTbl));
  }

  /**
   * Fügt einen neuen Fremdschlüssel ein.
   *
   * @param i
   *          der Fremdschlüssel
   * @param t
   *          der Typ der Beziehung
   * @param bezTbl
   *          die Bezeichnung der Tabelle
   */
  private void insertNewFk(final ForeignKey i, final Type t, final String bezTbl, final String bezFk,
      final String bezIndex) {
    // Beziehung Tabelle -index> Index
    query.append(String.format(Strings.CYPHER_INSERT_PREINDEXREL, bezTbl, Relations.getType(t)));
    // Fremdschlüssel
    query.append(
        String.format(Strings.CYPHER_INSERT_NEWINDEX, Strings.EMPTYSTRING, bezFk, Strings.NODE_FK, bezFk, bezFk));
    query.append(Strings.CYPHER_INSERT_CLOSEBRACKED);
    // Index zu Fremdschlüssel Verbindung
    // (FK)-[ForeignKey]->(XIF)
    if (!bezIndex.equals(Strings.EMPTYSTRING)) {
      query.append(String.format(Strings.CYPHER_COUNTREL, Strings.COLON, Relations.getType(Relations.Type.FOREIGNKEY),
          bezIndex));
    }
    putVals(bezFk + Strings.PUTVAL_NAME, i.getName());
    putVals(bezFk + Strings.PUTVAL_TYPE, Strings.R);
  }

  /**
   * Löscht eine Beziehung.
   *
   * @param rel
   *          die Beziehungsart
   * @param typeOfSecondNode
   *          Typ des Knotens (Table, Index, Field)
   */
  private void removeRelationship(final Type rel, final String typeOfSecondNode) {
    query.append(String.format(Strings.CYPHER_DELETE_REL, Strings.COLON + Relations.getType(rel), typeOfSecondNode));
  }

  /**
   * Verwendet für die Abfrage nach einen Knoten als Teilstück für ein Element
   * nach einer Findet Suche.
   *
   * @param name
   *          bezeichner des NOdes
   * @param t
   *          Typ des Nodes (Table, Field, Inde)
   */
  private void findNodeAfterRelation(final String name, final String t) {
    query.append(String.format(Strings.CYPHER_NODE, name, t));
  }

  /**
   * Fügt die Beziehung zwischen Tabellen und Indizies ein.
   *
   * @param i
   *          Index
   * @param t
   *          Typ der Beziehung
   * @param bezTbl
   *          Bezeichner für den Index
   * @param nameTransl
   *          Map welche die Identifier für die Feldnamen enthält
   */
  private void insertIndexToFieldRelation(final Index i, final Type t, final String bezTbl, final String bezIndex,
      final Map<String, String> nameTransl) {
    for (final Field field : i.getFieldList()) {
      final String order = i.getOrder(field.getName()).toString();
      query.append(String.format(Strings.CYPHER_INSERT_INDEXTOFIELD, bezIndex, Relations.getType(t),
          bezTbl + i.getName() + order, nameTransl.get(field.getName())));
      putVals(Strings.PUTVAL_R + bezTbl + i.getName() + order + Strings.PUTVAL_ORDER, order);
    }
  }

  /**
   * Fügt die Beziehung zwischen Tabellen und Indizies ein.
   *
   * @param i
   *          der Index
   * @param t
   *          der Typ
   * @param bezTable
   *          Bezeichner der Tabelle
   */
  private void insertIndexToFieldRelationForFk(final Index i, final Type t, final String bezIndex,
      final String bezTable, final ForeignKey fk, final Map<String, String> nameTransl) {
    for (final Field field : i.getFieldList()) {
      query.append(String.format(Strings.CYPHER_INSERT_INDEXTOFIELDFORFK, bezIndex, Relations.getType(t),
          Strings.PUTVAL_R + bezIndex + nameTransl.get(field.getName()),
          bezTable + fk.getName() + fk.getIndex().getOrder(field.getName()), nameTransl.get(field.getName())));
      putVals(Strings.PUTVAL_R + bezIndex + nameTransl.get(field.getName()), fk.getIndex().getAltName(field.getName()));
      putVals(Strings.PUTVAL_R + bezTable + fk.getName() + fk.getIndex().getOrder(field.getName()).toString()
          + Strings.PUTVAL_ORDER, fk.getIndex().getOrder(field.getName()).toString());
    }
  }

  @Override
  public String toString() {
    return QueryBuilder.queryToString(val.toArray(), query.toString().trim());
  }

  /**
   * generiert eine String Representation der Query für Debug Zwecke.
   *
   * @param v
   *          die Query ohne Paramaeter
   * @param q
   *          die Parameter der Query
   * @return fertige Query wie sie an die Datenbank gehen würde
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

  /**
   * löscht einen Datenbank Constraint auf Name (Table.name).
   *
   * @param type
   *          der Typ (bsp: Table)
   */
  void deleteDatabaseConstraint(final String type) {
    query.append(String.format(Strings.CYPHER_DROPCONSTRAINT, type));
  }
}
