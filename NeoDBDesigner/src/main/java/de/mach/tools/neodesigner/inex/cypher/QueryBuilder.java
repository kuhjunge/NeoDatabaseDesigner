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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.mach.tools.neodesigner.core.datamodel.Domain;
import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Relations;
import de.mach.tools.neodesigner.core.datamodel.Relations.Type;
import de.mach.tools.neodesigner.core.datamodel.Table;


public class QueryBuilder {
  /** generiert eine String Representation der Query für Debug Zwecke.
   *
   * @param v die Query ohne Paramaeter
   * @param qw die Parameter der Query
   * @return fertige Query wie sie an die Datenbank gehen würde */
  public static String queryToString(final Object[] v, final String qw) {
    final int len = v.length;
    String q = qw;
    for (int i = 0; i < len; i = i + 2) {
      q = q.replaceAll(Strings.BRACKETSOPEN + ((String) v[i]).trim() + Strings.BRACKETSCLOSED,
                       Strings.QUOUTE + v[i + 1] + Strings.QUOUTE);
    }
    return q;
  }

  private final StringBuilder query = new StringBuilder();
  private final List<Object> val = new ArrayList<>();


  /** Leert den QueryBuilder. */
  void clear() {
    val.clear();
    query.setLength(0);
  }

  /** generiert die Cypher Abfrage um alle Tabellen abzufragen.
   *
   * <pre>
   * MATCH(t:nodeType) return count (t) as number
   * MATCH(t) return count (t) as number
   * </pre>
   */
  void countNodes(final String nodeType) {
    findAll(Strings.TBL_ID + (nodeType.length() > 0 ? Strings.COLON + nodeType : Strings.EMPTYSTRING));
    query.append(String.format(Strings.CYPHER_COUNT, Strings.TBL_ID));
  }

  /** generiert die Cypher Abfrage um alle Tabellen abzufragen.
   *
   * <pre>
   * MATCH(t)-[r:relation]->(b) return count (r) as number
   * </pre>
   */
  void countRelation(final String relation) {
    findAll(Strings.TBL_ID);
    query
        .append(String.format(Strings.CYPHER_COUNTREL, Strings.REL_ID,
                              relation.length() > 0 ? Strings.COLON + relation : Strings.EMPTYSTRING, Strings.TBL2_ID));
    query.append(String.format(Strings.CYPHER_COUNT, Strings.REL_ID));
  }

  void createIndexOnColumn() {
    query.append("create index on :Column(table, name)");
  }

  /** löscht die komplette Datenbank.
   *
   * <pre>
   * MATCH(t) DETACH DELETE t
   * </pre>
   */
  void deleteDatabase() {
    findAll(Strings.TBL_ID);
    deleteIdentifers(Strings.TBL_ID);
  }

  /** löscht einen Datenbank Constraint auf Name (Table.name).
   *
   * <pre>
   * DROP CONSTRAINT ON (t:type) ASSERT t.name IS UNIQUE
   * </pre>
   *
   * @param type der Typ (bsp: Table) */
  void deleteDatabaseConstraint(final String type) {
    query.append(String.format(Strings.CYPHER_DROPCONSTRAINT, type));
  }

  /** löscht Element incl Beziehungen.
   *
   * <pre>
   *  DETACH DELETE tblId
   * </pre>
   *
   * @param tblId des Elements */
  private void deleteIdentifers(final String... tblId) {
    final String identifers = String.join(Strings.COMMA, tblId);
    query.append(String.format(Strings.CYPHER_DELETE, identifers));
  }


  /** Findet alle Tabellen.
   *
   * <pre>
   * MATCH(bez)
   * </pre>
   *
   * @param bez Bezeichner für die Tabellen */
  private void findAll(final String bez) {
    query.append(String.format(Strings.CYPHER_GET, bez));
  }

  /** Findet einen Knoten nnach einer Tabelle findet Anfrage.
   *
   * <pre>
   * -[:has_column]-(name{name:{name}})
   * </pre>
   *
   * @param identifer des Knoten
   * @param name des Knoten */
  private void findNodeAfterTable(final String identifer, final String name) {
    query.append(String.format(Strings.CYPHER_FIND_NODEAFTERTABLE, Strings.COLON + Relations.getType(Type.DATA),
                               identifer, identifer));
    putVals(identifer + Strings.PUTVAL_FINDNODE, name);
  }

  /** Select Anfrage für eine bestimmte Tabelle
   *
   * <pre>
   * MATCH(t:Table{name:{name}})
   * </pre>
   *
   * @param name Name der Tabelle
   * @param bez Bezeichner */
  private void findTable(final String name, final String bez) {
    query.append(String.format(Strings.CYPHER_FIND_TABLE, Strings.CYPHER_MATCH, bez, Strings.NODE_TABLE, bez));
    putVals(Strings.PUTVAL_TABLE + bez, name);
  }

  /** generiert die Cypher Abfrage um alle Tabellen abzufragen.
   *
   * <pre>
   *   MATCH(t) RETURN t.name AS name ORDER BY name
   * </pre>
   */
  void getAllTableNames() {
    findAll(Strings.TBL_ID + Strings.COLON + Strings.NODE_TABLE);
    returnTableNames(Strings.TBL_ID);
    query.append(Strings.CYPHER_ORDER_BYNAME);
  }

  /** generiert die Cypher Abfrage um alle Tabellen abzufragen.
   *
   * <pre>
   *   MATCH(t) RETURN t.name AS name, t.category AS category, t.comment AS comment ORDER BY name
   * </pre>
   */
  void getAllTables() {
    findAll(Strings.TBL_ID + Strings.COLON + Strings.NODE_TABLE);
    returnTable(Strings.TBL_ID);
    query.append(Strings.CYPHER_ORDER_BYNAME);
  }

  /** Fügt eine Kommentarzeile bei bedarf ein
   *
   * <pre>
   * ",comment:{comment}"
   * </pre>
   *
   * @param comment Kommentar
   * @param bez bezeichner
   * @return Querypart für das Kommentar */
  private String getCommentLine(final String comment, final String bez) {
    String infoLine = Strings.EMPTYSTRING;
    if (comment.length() > 1) {
      putVals(bez + Strings.PUTVAL_TABLECOMMENT, comment);
      infoLine = String.format(Strings.COMMENT, bez, Strings.PUTVAL_TABLECOMMENT);
    }
    return infoLine;
  }

  /** Fragt die Felder aller Tabelle ab
   *
   * <pre>
   *  MATCH(t:Table)-[r:has_column]-(c:Column) RETURN t.name as tablename,c.name as name,c.type as type, c.nullable as nullable, c.length as length, c.accuracy as accuracy ORDER BY tablename
   * </pre>
   */
  void getFieldsOfTable() {
    // TODO: umbauen auf neue Ordnung (low prio da CQL Import momentan nicht genutzt wird)
    findAll(Strings.TBL_ID);
    query.append(Strings.CYPHER_GETFIELDSOFTABLE);
  }

  /** Fragt die Felder einer Tabelle ab
   *
   * <pre>
   *  MATCH(t:Table{name:{name}})-[r:has_column]-(c) RETURN c.name as name,c.type as ntype, c.nullable as nullable, c.length as length
   * </pre>
   *
   * @param name Name der Tabelle dessen Felder ausgelesen werden */
  void getFieldsOfTable(final String name) {
    // TODO: umbauen auf neue Ordnung (low prio da CQL Import momentan nicht genutzt wird)
    findTable(name, Strings.TBL_ID);
    query.append(Strings.CYPHER_GETSPECIFICFIELDSOFTABLE);
  }

  /** Gibt die höchste Foreign Key Nummer zurück.
   *
   * <pre>
   * MATCH(b:Table)-[r:references]-(n) where r.name =~ 'R_([0-9]){%s}' return r.name As name order by name DESC Limit 1
   * </pre>
   *
   * @param lenght Anzal der Stellen der Zahl */
  void getForeignKeyNumber(final int lenght) {
    query.append(String.format(Strings.CYPHER_GETFOREIGNKEYNUMBER, lenght));
  }

  /** Gibt die Fremdschlüssel aller Tabellen zurück *
   *
   * <pre>
   *     MATCH(t:Table)-->(c:Column)-[r:references]->(o:Table)-[p:identified_by{order:r.order}]->(pk:Column) return DISTINCT t.name as tablename, r.order as order, r.name as indexname, c.name as name, o.name as origin,pk.name as altname order by tablename, indexname, order
   * </pre>
   */
  void getForeignKeysOfTable() {
    query.append(Strings.CYPHER_GETFOREIGNKEYSOFTABLE);
  }

  /** Gibt die Fremdschlüssel einer Tabelle zurück *
   *
   * <pre>
   *     MATCH(t:Table{name:{tn}})-->(c:Column)-[r:references]->(o:Table) RETURN DISTINCT t.name as tablename, r.order as order, r.name as indexname, c.name as name, o.name as origin order by tablename, order
   * </pre>
   *
   * @param name Name der Tabelle */
  void getForeignKeysOfTable(final String name) {
    query.append(Strings.CYPHER_GETFK);
    putVals("tn", name);
  }

  /** Gibt alle Indizes und Primärschlüssel aller Tabellen zurück
   *
   * <pre>
   *  MATCH(t:Table)-[r:identified_by|indexed_by]-(c:Column) RETURN t.name as tablename,type(r) as type, r.order as order,r.name as indexname,c.name as name ORDER BY tablename,type,indexname,order
   * </pre>
   */
  void getIndizesOfTable() {
    query.append(Strings.CYPHER_GETINDEX);
  }

  /** Gibt alle Indizes und Primärschlüssel einer Tabelle zurück
   *
   * <pre>
   *  MATCH(t:Table{name:{name}})-[r:identified_by | indexed_by]-(c) RETURN type(r) as type, r.order as order, r.name as indexname, c.name as name, ORDER BY type, order
   * </pre>
   *
   * @param name Tabellename */
  void getIndizesOfTable(final String name) {
    findTable(name, Strings.TBL_ID);
    query.append(Strings.CYPHER_GETSPECIFICINDEX);
  }

  /** Gibt alle Keys zurück.
   *
   * @return gibt alle Keys zurück */
  Object[] getKeys() {
    return val.toArray();
  }

  /** Reduziert das Ergebnis auf einen Rückgabewert.
   *
   * <pre>
   *  LIMIT 1
   * </pre>
   */
  void getOne() {
    query.append(Strings.LIMIT_TO_ONE);
  }

  /** Gibt die Query als String zurück.
   *
   * @return String der Query ohne Keys */
  String getQuery() {
    return query.toString().trim();
  }

  /** generiert die Cypher Abfrage um eine Tabelle abzufragen.
   *
   * <pre>
   * MATCH(t:Table{name:{tableName}}) RETURN bez.name AS name, bez.category AS category, bez.comment AS comment
   * </pre>
   *
   * @param name Name der Tabelle */
  void getTable(final String name) {
    findTable(name, Strings.TBL_ID);
    returnTable(Strings.TBL_ID);
    getOne();
  }

  /** Importiert eine Tabelle (Ohne Fremdschlüssel)
   *
   * @param t die Tabelle */
  void importTable(final Table t) {
    final Map<String, String> nt = new HashMap<>();
    int ix = 0;
    // Tabelle Importieren
    insertNewTable(t.getName(), t.getCategory(), t.getComment(), Strings.TBL_ID);
    // Feld Importieren
    String tempName;
    for (final Field f : t.getFields()) {
      tempName = Strings.COLUMN_ID + ix++;
      insertNewField(f, Strings.TBL_ID, tempName);
      nt.put(f.getName(), tempName);
    }
    // Primärschlüsselfeldbeziehung
    for (final Field f : t.getXpk().getFieldList()) {
      insertIndexRelation(t.getXpk().getName(), Type.XPK, t.getXpk().getOrder(f.getName(), false),
                          t.getXpk().isUnique(), Strings.INDEX_ID, nt.get(f.getName()));
    }
    // Indexbeziehungen
    ix = 0;
    for (final Index i : t.getIndizies()) {
      for (final Field f : i.getFieldList()) {
        insertIndexRelation(i.getName(), Type.INDEX, i.getOrder(f.getName(), false), i.isUnique(),
                            Strings.INDEX_ID + ix++, nt.get(f.getName()));
      }
    }
  }

  /** Fügt eine FK Beziehung hinzu
   *
   * <pre>
   * MATCH(t:Table{name:{tableName}})-[:has_column]-(f:Column{name:"TEST"}) (tt:Table{name:"RASTER"}) CREATE (f)-[:references{name:"R999", order:99}]->(tt)
   * </pre>
   *
   * @param tableName Tabellenname
   * @param refTableName Referenzierte Tabellenname
   * @param foreignKeyName Fremdschlüssel
   * @param columnName Spaltenname
   * @param i Ordnung */
  void insertFkRelation(final String tableName, final String refTableName, final String foreignKeyName,
                        final String columnName, final int i) {
    // MATCH (t:Table{name:"BLA"})
    findTable(tableName, Strings.TBL_ID);
    // --(f:Column{name:"BLUB"})
    findNodeAfterTable(Strings.COLUMN_ID, columnName);
    // ,
    query.append(Strings.COMMA);
    // (tt:Table{name:"RASTER"})
    query.append(String.format(Strings.CYPHER_FIND_TABLE, Strings.EMPTYSTRING, Strings.TBL2_ID, Strings.NODE_TABLE,
                               Strings.TBL2_ID));
    putVals(Strings.PUTVAL_TABLE + Strings.TBL2_ID, refTableName);
    // MERGE (f)-[:references{name:"R999", order:99}]->(tt)
    query
        .append(String.format(Strings.CYPHER_MERGEREFERNCES, Strings.COLUMN_ID, Strings.COLUMN_ID + Strings.PUTVAL_NAME,
                              Strings.COLUMN_ID + Strings.PUTVAL_ORDER, Strings.TBL2_ID));
    putVals(Strings.COLUMN_ID + Strings.PUTVAL_NAME, foreignKeyName);
    putVals(Strings.COLUMN_ID + Strings.PUTVAL_ORDER, i);
  }

  /** Fügt einen Suchindex in die Datenbank ein
   *
   * <pre>
   * CREATE CONSTRAINT ON (t:type) ASSERT t.name IS UNIQUE
   * </pre>
   *
   * @param type der Type auf dem der Index erstellt wird */
  void insertIndexOnDb(final String type) {
    query.append(String.format(Strings.CYPHER_CREATEINDEX, type));
  }

  /** Fügt eine Beziehung zwischen Tabelle und Column
   *
   * <pre>
   * MATCH (t:Table{name:"AFAPLAN"})-[:has_column]-(f:Column{name:"RASTER"}) MERGE (t)-[x:indexed_by2{name:"test",order:0,unique:false}]-(f)
   * </pre>
   *
   * @param tableName Tabellenname
   * @param indexName Indexname
   * @param fieldName Spaltenname
   * @param rel Typ der Beziehung (identified_by)
   * @param order Ordnungszahl
   * @param isUnique Einzigartigkeit */
  void insertIndexRelation(final String tableName, final String indexName, final String fieldName, final Type rel,
                           final int order, final boolean isUnique) {
    final String indexBez = Strings.INDEX_ID;
    findTable(tableName, Strings.TBL_ID);
    query.append(String.format(Strings.CYPHER_FINDINDEXREL, indexBez + Strings.PUTVAL_NAME));
    putVals(indexBez + Strings.PUTVAL_NAME, fieldName);
    insertIndexRelation(indexName, rel, order, isUnique, indexBez, Strings.COLUMN_ID);
  }

  private void insertIndexRelation(final String indexName, final Type rel, final int order, final boolean isUnique,
                                   final String indexBez, final String fieldBez) {
    query.append(String
        .format(Strings.CYPHER_INSERTINDEXREL, Relations.getType(rel), indexBez + fieldBez + Strings.PUTVAL_INDEXNAME,
                indexBez + fieldBez + Strings.PUTVAL_ORDER, indexBez + fieldBez + Strings.PUTVAL_REQ, fieldBez));
    putVals(indexBez + fieldBez + Strings.PUTVAL_INDEXNAME, indexName);
    putVals(indexBez + fieldBez + Strings.PUTVAL_ORDER, order);
    putVals(indexBez + fieldBez + Strings.PUTVAL_REQ, isUnique);
  }

  /** fügt ein neues Feld ein.
   *
   * <pre>
   * MATCH(t:Table{name:'tableName'}) CREATE(t_fieldName:Column{name:'fieldName',type:'VARCHAR2',accuracy:'0',length:'15',nullable:'false',table:'tableName'})<-[:has_column]-(t)"
   * </pre>
   *
   * @param f Das neue Feld */
  void insertNewField(final Field f) {
    findTable(f.getTable().getName(), Strings.TBL_ID);
    insertNewField(f, Strings.TBL_ID, Strings.TBL_ID + Strings.UNDERSCORE + f.getName());
  }

  /** Fügt ein neues Feld ein
   *
   * <pre>
   *  CREATE(t_fieldName:Column{name:'fieldName',type:'VARCHAR2',accuracy:'0',length:'15',nullable:'false',table:'tableName'})<-[:has_column]-(t)"
   * </pre>
   *
   * @param f das Feld
   * @param bezTab der Bezeichner der Tabelle
   * @param bezField der Bezeichner des Feldes */
  private void insertNewField(final Field f, final String bezTab, final String bezField) {
    final String[] domain = Domain.convertDomainToCsv(f.getDomain(), f.getDomainLength(), Strings.COLON)
        .split(Strings.COLON);
    final String infoLine = getCommentLine(f.getComment(), bezField);
    query.append(String
        .format(Strings.CYPHER_INSERTNEWFIELD, Strings.SPACE + Strings.CYPHER_CREATE, bezField, Strings.NODE_COLUMN,
                bezField + Strings.PUTVAL_NAME, bezField + Strings.VALUE_ID, bezField + Strings.VALUE_ID3,
                bezField + Strings.VALUE_ID2, bezField + Strings.PUTVAL_REQ, bezField + Strings.PUTVAL_ORDER,
                bezField + Strings.PUTVAL_TABLENAME, infoLine, Relations.getType(Relations.Type.DATA), bezTab));
    putVals(bezField + Strings.PUTVAL_NAME, f.getName());
    putVals(bezField + Strings.VALUE_ID, domain[0]);
    putVals(bezField + Strings.VALUE_ID2, Integer.parseInt(domain[1]));
    putVals(bezField + Strings.VALUE_ID3, Integer.parseInt(domain[2]));
    putVals(bezField + Strings.PUTVAL_REQ, !f.isRequired());
    putVals(bezField + Strings.PUTVAL_TABLENAME, f.getTable().getName());
    putVals(bezField + Strings.PUTVAL_ORDER, f.getDisplayOrder());
  }

  /** fügt eine neue Tabelle ein.
   *
   * <pre>
   * CREATE(t:Table{name:{%tablename},category:{%categoryname}, comment:{comment}})"
   * </pre>
   *
   * @param tableName Tabelle
   * @param tableCategory Tabellenkategorie
   * @param tableComment Tabellenkommentar
   * @param bez Bezeichner der Tabelle */
  private void insertNewTable(final String tableName, final String tableCategory, final String tableComment,
                              final String bez) {
    final String infoLine = getCommentLine(tableComment, bez);
    query.append(String.format(Strings.CYPHER_INSERT_NEWTABLE, bez, Strings.NODE_TABLE, bez, bez, infoLine));
    putVals(bez + Strings.PUTVAL_TABLENAME, tableName);
    putVals(bez + Strings.PUTVAL_TABLECATEGORY, tableCategory);
  }

  /** Fügt eine PK Relation hinzu
   *
   * <pre>
   * MATCH (t:Table{name:{%s}})-[:has_column]-(f:Column{name:{%s}}) MERGE (t)-[x:%s{name:{%s},order:{%s}}]->(f)
   * </pre>
   *
   * @param tableName Tabellenname
   * @param indexName Indexname
   * @param columnName Spaltenname
   * @param order Die Ordnung
   * @param rel Beziehungstyp */
  void insertPkRelation(final String tableName, final String indexName, final String columnName, final int order,
                        final Type rel) {
    query.append(String.format(Strings.CYPHER_INSERTPKREL, Strings.INDEX_ID + Strings.PUTVAL_TABLENAME,
                               Strings.INDEX_ID + Strings.PUTVAL_NAME, Relations.getType(rel),
                               Strings.INDEX_ID + Strings.PUTVAL_INDEXNAME, Strings.INDEX_ID + Strings.PUTVAL_ORDER));
    putVals(Strings.INDEX_ID + Strings.PUTVAL_TABLENAME, tableName);
    putVals(Strings.INDEX_ID + Strings.PUTVAL_INDEXNAME, indexName);
    putVals(Strings.INDEX_ID + Strings.PUTVAL_NAME, columnName);
    putVals(Strings.INDEX_ID + Strings.PUTVAL_ORDER, order);
  }

  /** Setzt einen Key Wert.
   *
   * @param a Key
   * @param b Value als Boolean */
  private void putVals(final String a, final Boolean b) {
    val.add(a);
    val.add(b);
  }

  /** Setzt einen Key Wert.
   *
   * @param a Key
   * @param i Value als Integer */
  private void putVals(final String a, final Integer i) {
    val.add(a);
    val.add(i);
  }

  /** Setzt einen Key Wert.
   *
   * @param a Key
   * @param b Value */
  private void putVals(final String a, final String b) {
    val.add(a);
    val.add(b);
  }

  /** Fügt ein Return Statement an, welches Name, Kategorie und Kommentar zurück gibt.
   *
   * <pre>
   *  RETURN bez.name AS name, bez.category AS category, bez.comment AS comment
   * </pre>
   *
   * @param bez gibt den Tabellenbezeichner an */
  private void returnTable(final String bez) {
    query.append(String.format(Strings.CYPHER_RETURN_TABLE, bez, bez, bez));
  }

  /** Fügt ein Return Statement an, welches den Namen einer Tabelle zurückgibt
   *
   * <pre>
   *  RETURN bez.name AS name
   * </pre>
   *
   * @param tblId gibt den Tabellenbezeichner an */
  private void returnTableNames(final String tblId) {
    query.append(String.format(Strings.CYPHER_RETURN_NAME, tblId));
  }

  void startCsvBulkImportColumns() {
    query
        .append("using periodic commit load csv from 'file:///D:\\\\Cols.csv' as line FIELDTERMINATOR ';'"
                + " create (:Column{table: line[0], name: line[1], type: line[2], length: toInteger(line[3]), accuracy: toInteger(line[4]), nullable: (case line[5] when 'Y' then true else false end)})");
  }

  void startCsvBulkImportForeignKeys() {
    query
        .append("using periodic commit load csv from 'file:///D:\\\\FKeys.csv' as line FIELDTERMINATOR ';'"
                + " match(table:Table{name: line[1]})" + " match(column:Column{table: line[0], name: line[3]})"
                + " merge (column)-[:references{name: line[2], order: toInteger(line[4]), cascade: (case line[5] when 'Y' then true else false end)}]->(table)");
  }

  void startCsvBulkImportIndizes() {
    query
        .append("using periodic commit load csv from 'file:///D:\\\\Idxs.csv' as line FIELDTERMINATOR ';'"
                + " match(table:Table{name: line[0]}) match(column:Column{table: line[0], name: line[2]}) "
                + "merge (table)-[:indexed_by{name: line[1], order: toInteger(line[3]), unique: (case line[4] when 'Y' then true else false end)}]->(column)");
  }

  void startCsvBulkImportMetaCols() {
    query
        .append("using periodic commit load csv from 'file:///D:\\\\MetaCols.csv' as line FIELDTERMINATOR ';'"
                + "match(t:Table{name: line[0]})-[:has_column]->(c:Column{name: toUpper(line[1])}) SET c.name=line[1], c.comment=line[2]");
  }

  void startCsvBulkImportMetaTbls() {
    query.append("using periodic commit load csv from 'file:///D:\\\\MetaTbls.csv' as line FIELDTERMINATOR ';'"
                 + "match(t:Table{name: toUpper(line[0])}) SET t.name=line[0], t.category=line[1], t.comment=line[2]");
  }

  void startCsvBulkImportPrimaryKeys() {
    query.append("using periodic commit load csv from 'file:///D:\\\\PKeys.csv' as line FIELDTERMINATOR ';'"
                 + " match(table:Table{name: line[0]})" + " match(column:Column{table: line[0], name: line[2]})"
                 + " merge (table)-[:identified_by{name: line[1], order: toInteger(line[3])}]->(column)");
  }

  void startCsvBulkImportTableColumnRel() {
    query.append("using periodic commit load csv from 'file:///D:\\\\Cols.csv' as line FIELDTERMINATOR ';'"
                 + " match(table:Table{name: line[0]})" + " match(column:Column{table: line[0], name: line[1]})"
                 + " merge (table)-[:has_column]->(column)");
  }

  void startCsvBulkImportTables() {
    query.append("using periodic commit load csv from 'file:///D:\\\\Tbls.csv' as line create (:Table{name: line[0]})");
  }

  @Override
  public String toString() {
    return QueryBuilder.queryToString(val.toArray(), query.toString().trim());
  }
}
