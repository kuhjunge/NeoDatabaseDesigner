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

package de.mach.tools.neodesigner.database.cypher;


import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import de.mach.tools.neodesigner.core.TestDatamodel;
import de.mach.tools.neodesigner.core.datamodel.Domain.DomainId;
import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.core.datamodel.impl.FieldImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.ForeignKeyImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.IndexImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.TableImpl;
import de.mach.tools.neodesigner.database.DatabaseConnection;
import de.mach.tools.neodesigner.database.Strings;


public class TestDatabase {
  DatabaseMockConnector dc = new DatabaseMockConnector();
  String test1 = "Test1";
  String test2 = "Test2";
  String test3 = "Test3";
  String name = "name";
  String category = "test";

  @Test
  public void getTable() {
    final DatabaseMockConnector dc = new DatabaseMockConnector();
    final DatabaseConnection dbm = new DatabaseManagerLean(dc);
    dbm.connectDb("", "", "");
    assertTrue(dbm.isReady());
    dbm.getTable(name);
    assertTrue("MATCH(t:Table{name:'name'}) RETURN t.name AS name, t.category AS category, t.comment AS comment LIMIT 1"
        .equals(dc.lastQuery));
    final ArrayList<ResultUnit> lruIndexFields = new ArrayList<>();
    final Map<String, Object> map = new HashMap<>();
    map.put(name, name);
    map.put("category", category);
    lruIndexFields.add(new ResultUnitImpl(map));
    dc.list2DResultUnit.add(lruIndexFields);
    final Table t = dbm.getTable(name).get();
    assertTrue(t.getName().equals(name));
    assertTrue(t.getCategory().equals(category));
    assertTrue("MATCH(t:Table{name:'name'})-[r:identified_by|indexed_by]-(c) RETURN type(r) as type, r.order as order, r.name as indexname, c.name as name ORDER BY type, order"
        .equals(dc.lastQuery));
  }

  @Test
  public void getFKNumber() {
    final DatabaseMockConnector dc = new DatabaseMockConnector();
    final DatabaseConnection dbm = new DatabaseManagerLean(dc);
    dbm.connectDb("", "", "");
    assertTrue(dbm.isReady());
    dbm.getForeignKeyNumber(3);
    assertTrue("MATCH(:Table)-[r:references]-(n) where r.name =~ 'R_([0-9]){3}' return r.name As name order by name DESC Limit 1"
        .equals(dc.lastQuery));
    int i = dbm.getForeignKeyNumber(3);
    assertTrue(i == 100);
    final ArrayList<ResultUnit> lruIndexFields = new ArrayList<>();
    final Map<String, Object> map = new HashMap<>();
    map.put("name", "R_933");
    lruIndexFields.add(new ResultUnitImpl(map));
    dc.list2DResultUnit.add(lruIndexFields);
    i = dbm.getForeignKeyNumber(3);
    assertTrue(i == 933);
    map.clear();
    map.put("name", "R_9999");
    lruIndexFields.clear();
    lruIndexFields.add(new ResultUnitImpl(map));
    dc.list2DResultUnit.clear();
    dc.list2DResultUnit.add(lruIndexFields);
    i = dbm.getForeignKeyNumber(4);
    assertTrue(i == 9999);
  }

  @Test
  public void getIndistinctName() {
    final DatabaseMockConnector dc = new DatabaseMockConnector();
    final DatabaseConnection dbm = new DatabaseManagerLean(dc);
    dbm.connectDb("", "", "");
    assertTrue(dbm.isReady());
    final ArrayList<ResultUnit> lruIndexFields = new ArrayList<>();
    final Map<String, Object> map = new HashMap<>();
    map.put("name", "tEST");
    lruIndexFields.add(new ResultUnitImpl(map));
    dc.list2DResultUnit.add(lruIndexFields);
    final List<String> ls = dbm.getFieldNameCase("Test");
    assertTrue(ls.get(0).equals("tEST"));
  }

  @Test
  public void insertNewTable() {
    final DatabaseMockConnector dc = new DatabaseMockConnector();
    final DatabaseConnection dbm = new DatabaseManagerLean(dc);
    dbm.connectDb("", "", "");
    assertTrue(dbm.isReady());
    final Table table = new TableImpl(name);
    table.setCategory(test1);
    dbm.insertNewTable(table);
    assertTrue("CREATE(t:Table{name:'name',category:'Test1'})".equals(dc.lastQuery));

    final Table t = new TableImpl("tableName");
    final Table rt = new TableImpl("tableName");
    final Index i = new IndexImpl("indexName", Index.Type.XAK, t);
    final Index i2 = new IndexImpl("XPK_tableName", t);
    final Field f2 = new FieldImpl("fieldName", DomainId.STRING, 20, true, "", t);
    final ForeignKey fk = new ForeignKeyImpl(TestDatamodel.foreignKeyName, t);
    fk.setRefTable(rt);
    fk.setIndex(i);
    i2.addField(f2);
    fk.getIndex().addField(f2);
    dbm.insertNewField(f2);
    assertTrue("MATCH(t:Table{name:'tableName'}) CREATE(t_fieldName:Column{name:'fieldName',type:'VARCHAR2',accuracy:'0',length:'20',nullable:'false',table:'tableName'})<-[:has_column]-(t)"
        .equals(dc.lastQuery));
    dbm.insertNewIndex(i);
    assertTrue("MATCH(t:Table{name:'tableName'})-[:has_column]-(f:Column{name:'fieldName'}) CREATE(t)-[:indexed_by{name:'indexName',order:'1',unique:'true'}]->(f)"
        .equals(dc.lastQuery));
    dbm.insertNewIndex(i2);
    assertTrue("MATCH(t:Table{name:'tableName'})-[:has_column]-(f:Column{name:'fieldName'}) CREATE(t)-[:indexed_by{name:'XPK_tableName',order:'1',unique:'true'}]->(f)"
        .equals(dc.lastQuery));
    dbm.insertNewForeignKey(fk);
    assertTrue("MATCH(t:Table{name:'tableName'})-[:has_column]->(f{name:'fieldName'}),(b:Table{name:'tableName'}) CREATE (f)-[:references{name:'R_1704', order:'0'}]->(b)"
        .equals(dc.lastQuery));
    dbm.deleteNode(fk);
    assertTrue("MATCH(t:Table{name:'tableName'})-[:has_column]->(f{name:'fieldName'})-[r{name:'R_1704'}]->() DELETE r"
        .equals(dc.lastQuery));
    dbm.deleteNode(i2);
    assertTrue("MATCH(t:Table{name:'tableName'})-[i{name:'XPK_tableName'}]->() DELETE i".equals(dc.lastQuery));
  }

  @Test
  public void testDatabaseManagerAdvanced() {
    final DatabaseMockConnector dc = new DatabaseMockConnector();
    final DatabaseConnection dbm = new DatabaseManagerLean(dc);
    assertTrue(!dbm.deleteDatabase());
    dbm.connectDb("", "", "");
    assertTrue(dbm.isReady());
    dbm.deleteDatabase();
    assertTrue("MATCH(t) DETACH DELETE t".equals(dc.lastQuery));
    final Table t = new TableImpl(name);
    dbm.deleteNode(t);
    assertTrue("MATCH(t:Table{name:'name'})OPTIONAL MATCH (t)-[:has_column]->(f) DETACH DELETE f,t"
        .equals(dc.lastQuery));
  }

  @Test
  public void testDatabaseManagerSave() {
    final DatabaseMockConnector dc = new DatabaseMockConnector();
    final DatabaseConnection dbm = new DatabaseManagerLean(dc);
    dbm.connectDb("", "", "");
    assertTrue(dbm.isReady());
    final Field f = new FieldImpl(TestDatamodel.fieldName);
    final Table t = new TableImpl("TestTable");
    final Index i = new IndexImpl("TestIndex", Index.Type.XIF, t);
    i.addField(f);
    dbm.changeDataFields(i);
    assertTrue("MATCH(t:Table{name:'TestTable'})-[:has_column]-(f:Column{name:'TestFeld'}) CREATE(t)-[:indexed_by{name:'TestIndex',order:'1',unique:'false'}]->(f)"
        .equals(dc.lastQuery));
    i.clearFieldList();
    i.addField(f);
    dbm.changeDataFields(i);
    assertTrue("MATCH(t:Table{name:'TestTable'})-[:has_column]-(f:Column{name:'TestFeld'}) CREATE(t)-[:indexed_by{name:'TestIndex',order:'1',unique:'false'}]->(f)"
        .equals(dc.lastQuery));
    dbm.changeFieldRequired("nodeName", "tableName", true);
    assertTrue("MATCH(tableName:Table{name:'tableName'})-[:has_column]->(nodeName{name:'nodeName'}) SET nodeName.nullable='false'"
        .equals(dc.lastQuery));
    dbm.changeFieldDomain("nodeName", "tableName", DomainId.STRING, 10);

    assertTrue("MATCH(tableName:Table{name:'tableName'})-[:has_column]->(nodeName{name:'nodeName'}) SET t.type='VARCHAR2' SET nodeName.length='10' SET nodeName.accuracy='0'"
        .equals(dc.lastQuery));
    dbm.changeFkRelations("foreignKeyName", "tableName", "refTableName", "indexName", "columnName", 0);

    assertTrue("MATCH(t:Table{name:'tableName'})-[:has_column]->(f{name:'columnName'}),(b:Table{name:'refTableName'}) CREATE (f)-[:references{name:'foreignKeyName', order:'0'}]->(b)"
        .equals(dc.lastQuery));
    dbm.changeIndexUnique("nodeName", "tableName", true);

    assertTrue("MATCH(t:Table{name:'tableName'})-[i{name:'nodeName'}]->() SET i.unique='true'".equals(dc.lastQuery));
    dbm.changeNodeNameFromTable("nodeName", "tableName", "newNodeName", "Column");
    assertTrue("MATCH(t:Table{name:'tableName'})-[:has_column]->(f{name:'nodeName'}) SET f.name='newNodeName'"
        .equals(dc.lastQuery));
    dbm.changeTableCategory("tableName", "newKategory");
    assertTrue("MATCH(t:Table{name:'tableName'}) SET t.category='newKategory'".equals(dc.lastQuery));
    dbm.changeTableName("tableName", "tableName2", "XPKTableName");
    assertTrue("MATCH(t:Table{name:'tableName'})-[i:identified_by]->(f) SET t.name='tableName2' SET i.name='XPKTableName'"
        .equals(dc.lastQuery));
  }

  @Test
  public void testComment() {
    final DatabaseMockConnector dc = new DatabaseMockConnector();
    final DatabaseConnection dbm = new DatabaseManagerLean(dc);
    dbm.connectDb("", "", "");
    assertTrue(dbm.isReady());
    dbm.saveComment("Table", "Comment");
    assertTrue("MATCH(t:Table{name:'Table'}) SET t.comment='Comment'".equals(dc.lastQuery));
    dbm.saveComment("Field", "Table", "Comment");
    assertTrue("MATCH(t:Table{name:'Table'})-[:has_column]->(Field{name:'Field'}) SET Field.comment='Comment'"
        .equals(dc.lastQuery));
  }

  @Test
  public void testDatabaseManager() {
    final DatabaseMockConnector dc = new DatabaseMockConnector();
    dc.addr = test1;
    dc.nutzername = test2;
    dc.pw = test3;
    final DatabaseConnection dbm = new DatabaseManagerLean(dc);
    assertTrue(!dbm.connectDb("", test3, test3));
    assertTrue(!dbm.isReady());
    assertTrue(dbm.connectDb(test1, test2, test3));
    assertTrue(dbm.isReady());
    dbm.createIndexOnDb();
    final QueryBuilder qb = new QueryBuilder();
    qb.insertIndexOnDb(Strings.NODE_TABLE);
    assertTrue(dc.lastQuery.equals(qb.toString()));
    final Map<String, Object> map = new HashMap<>();
    map.put(name, test1);
    map.put("category", test2);
    final ArrayList<ResultUnit> lruTable = new ArrayList<>();
    lruTable.add(new ResultUnitImpl(map));
    dc.list2DResultUnit.add(lruTable);
    assertTrue(dbm.getListWithTableNames().contains(test1));
    // Tabellenquery
    dc.list2DResultUnit.add(lruTable);
    // FeldQuery
    final ArrayList<ResultUnit> lruField = new ArrayList<>();
    map.clear();
    map.put("tablename", test1);
    map.put("name", test2);
    map.put("altname", test3);
    map.put("type", "VARCHAR");
    map.put("length", 20);
    map.put("accuracy", 0);
    map.put("comment", "CommToMe");
    map.put("nullable", false);
    lruField.add(new ResultUnitImpl(map));
    dc.list2DResultUnit.add(lruField);
    // Index
    final ArrayList<ResultUnit> lruIndex = new ArrayList<>();
    map.clear();
    map.put(name, test2);
    map.put("indexname", "XAK" + test3);
    map.put("tablename", test1);
    map.put("order", 1);
    map.put("type", "indexed_by");
    map.put("unique", true);
    lruIndex.add(new ResultUnitImpl(map));
    map.clear();
    map.put(name, test2);
    map.put("indexname", "XPK" + test1);
    map.put("tablename", test1);
    map.put("order", 1);
    map.put("type", "identified_by");
    map.put("unique", true);
    lruIndex.add(new ResultUnitImpl(map));
    dc.list2DResultUnit.add(lruIndex);
    final ArrayList<ResultUnit> lruIndexFields = new ArrayList<>();
    map.clear();
    map.put("origin", test1);
    map.put("tablename", test1);
    map.put(name, test2);
    map.put("indexname", "R_Test4");
    map.put("order", 1);
    map.put("altname", test3);
    lruIndexFields.add(new ResultUnitImpl(map));
    dc.list2DResultUnit.add(lruIndexFields);
    // Auswertung
    final Table t = dbm.getTables().get(0);
    assertTrue(t.getName().equals(test1));
    assertTrue(t.getCategory().equals(test2));
    assertTrue(t.getXpk().getName().equals("XPK" + test1));
    assertTrue(t.getFields().get(0).getName().equals(test2));
    // assertTrue(t.getData().get(0).getAltName().equals(test3));
    assertTrue(t.getFields().get(0).getTable().getName().equals(test1));
    assertTrue(t.getFields().get(0).getDomain().equals(DomainId.STRING));
    assertTrue(t.getFields().get(0).isRequired());
    assertTrue(t.getFields().get(0).isPartOfPrimaryKey());
    assertTrue(t.getIndizies().get(0).getName().equals("XAK" + test3));
    assertTrue(t.getIndizies().get(0).getType().equals(Index.Type.XAK));
    assertTrue(t.getIndizies().get(0).getFieldList().get(0).getName().equals(test2));
    dbm.disconnectDb();
    assertTrue(!dbm.isReady());
  }

  @Test
  public void testListWithCategories() {
    final DatabaseMockConnector dc = new DatabaseMockConnector();
    final DatabaseConnection dbm = new DatabaseManagerLean(dc);
    dbm.connectDb("", "", "");
    assertTrue(dbm.isReady());
    final Map<String, Object> map = new HashMap<>();
    map.put(name, test1);
    map.put("category", test2);
    final ArrayList<ResultUnit> lruTable = new ArrayList<>();
    lruTable.add(new ResultUnitImpl(map));
    dc.list2DResultUnit.add(lruTable);
    assertTrue(dbm.getListWithCategories().contains(test2));
  }

  @Test
  public void testResultUnit() {
    final Map<String, Object> map = new HashMap<>();
    map.put(Strings.IDENT_NAME, test1);
    map.put(Strings.IDENT_UNIQUE, true);
    final ResultUnit ru = new ResultUnitImpl(map);
    assertTrue(ru.get(Strings.IDENT_NAME).equals(test1));
    assertTrue(ru.getBoolean(Strings.IDENT_UNIQUE));
  }
}
