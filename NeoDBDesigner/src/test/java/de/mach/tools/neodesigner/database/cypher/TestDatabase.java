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

import de.mach.tools.neodesigner.core.datamodel.Domain.DomainId;
import de.mach.tools.neodesigner.core.TestDatamodel;
import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.core.datamodel.impl.FieldImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.ForeignKeyImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.IndexImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.TableImpl;
import de.mach.tools.neodesigner.database.DatabaseConnection;
import de.mach.tools.neodesigner.database.SaveDatabase;
import de.mach.tools.neodesigner.database.Strings;
import de.mach.tools.neodesigner.database.cypher.DatabaseManager;
import de.mach.tools.neodesigner.database.cypher.QueryBuilder;
import de.mach.tools.neodesigner.database.cypher.ResultUnit;
import de.mach.tools.neodesigner.database.cypher.ResultUnitImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

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
    final DatabaseConnection dbm = new DatabaseManager(dc);
    dbm.connectDb("", "", "");
    Assert.assertTrue(dbm.isReady());
    dbm.getTable(name);
    Assert.assertTrue(
        "MATCH(t:Table{name:'name'}) RETURN t.name AS name, t.category AS category, t.comment AS comment LIMIT 1"
            .equals(dc.lastQuery));
    final ArrayList<ResultUnit> lruIndexFields = new ArrayList<>();
    final Map<String, Object> map = new HashMap<>();
    map.put(name, name);
    map.put("category", category);
    lruIndexFields.add(new ResultUnitImpl(map));
    dc.list2DResultUnit.add(lruIndexFields);
    final Table t = dbm.getTable(name).get();
    Assert.assertTrue(t.getName().equals(name));
    Assert.assertTrue(t.getCategory().equals(category));
    Assert.assertTrue(
        "MATCH(t:Table{name:'name'})-[:XPK]->(:Index)<-[:REFERENCE]-(fk:ForeignKey)-[:FOREIGNKEY]->(i:Index)<-[inRel:INDEX]-(origin:Table) return fk.name AS name, i.name AS xif, origin.name AS refTable ORDER BY name"
            .equals(dc.lastQuery));
  }

  @Test
  public void getFKNumber() {
    final DatabaseMockConnector dc = new DatabaseMockConnector();
    final DatabaseConnection dbm = new DatabaseManager(dc);
    dbm.connectDb("", "", "");
    Assert.assertTrue(dbm.isReady());
    dbm.getForeignKeyNumber(3);
    Assert.assertTrue(
        "MATCH(b:Table)-[:FOREIGNKEY]->(n:ForeignKey) where n.name =~ 'R_([0-9]){3}' return n.name As name order by name DESC Limit 1"
            .equals(dc.lastQuery));
    int i = dbm.getForeignKeyNumber(3);
    Assert.assertTrue(i == 100);
    final ArrayList<ResultUnit> lruIndexFields = new ArrayList<>();
    final Map<String, Object> map = new HashMap<>();
    map.put("name", "R_933");
    lruIndexFields.add(new ResultUnitImpl(map));
    dc.list2DResultUnit.add(lruIndexFields);
    i = dbm.getForeignKeyNumber(3);
    Assert.assertTrue(i == 933);
    map.clear();
    map.put("name", "R_9999");
    lruIndexFields.clear();
    lruIndexFields.add(new ResultUnitImpl(map));
    dc.list2DResultUnit.clear();
    dc.list2DResultUnit.add(lruIndexFields);
    i = dbm.getForeignKeyNumber(4);
    Assert.assertTrue(i == 9999);
  }

  @Test
  public void getIndistinctName() {
    final DatabaseMockConnector dc = new DatabaseMockConnector();
    final DatabaseConnection dbm = new DatabaseManager(dc);
    dbm.connectDb("", "", "");
    Assert.assertTrue(dbm.isReady());
    final ArrayList<ResultUnit> lruIndexFields = new ArrayList<>();
    final Map<String, Object> map = new HashMap<>();
    map.put("name", "tEST");
    lruIndexFields.add(new ResultUnitImpl(map));
    dc.list2DResultUnit.add(lruIndexFields);
    final List<String> ls = dbm.getFieldNameCase("Test");
    Assert.assertTrue(ls.get(0).equals("tEST"));
  }

  @Test
  public void insertNewTable() {
    final DatabaseMockConnector dc = new DatabaseMockConnector();
    final DatabaseConnection dbm = new DatabaseManager(dc);
    dbm.connectDb("", "", "");
    Assert.assertTrue(dbm.isReady());
    final Table table = new TableImpl(name);
    table.setCategory(test1);
    dbm.insertNewTable(table);
    Assert.assertTrue("CREATE(t:Table{name:'name',category:'Test1'})".equals(dc.lastQuery));

    final Table t = new TableImpl("tableName");
    final Table rt = new TableImpl("tableName");
    final Index i = new IndexImpl("indexName", Index.Type.XAK, t);
    final Index i2 = new IndexImpl("XPK_tableName", t);
    final Field f2 = new FieldImpl("fieldName", DomainId.STRING, 20, true, "", t);
    final ForeignKey fk = new ForeignKeyImpl(TestDatamodel.foreignKeyName, t);
    fk.setRefTable(rt);
    fk.setIndex(i);
    fk.getIndex().addField(f2);
    final SaveDatabase db = dbm;
    db.insertNewField(f2);
    Assert.assertTrue(
        "MATCH(t:Table{name:'tableName'}) CREATE(t_fieldName:Field{name:'fieldName',type:'String:20',isRequired:'true'})<-[:DATA]-(t)"
            .equals(dc.lastQuery));
    db.insertNewIndex(i);
    Assert.assertTrue(
        "MATCH(t:Table{name:'tableName'}) CREATE(p:Index{name:'indexName',type:'XAK',unique:'true'})<-[:INDEX]-(t)"
            .equals(dc.lastQuery));
    db.insertNewIndex(i2);

    Assert.assertTrue("MATCH(t:Table{name:'tableName'}) CREATE(p:Index{name:'XPK_tableName',type:'XPK'})<-[:XPK]-(t)"
        .equals(dc.lastQuery));
    db.insertNewForeignKey(fk);
    Assert.assertTrue(
        "MATCH(t:Table{name:'tableName'}) CREATE(t)-[:FOREIGNKEY]->(t_R_TestForeignKey:ForeignKey{name:'R_TestForeignKey',type:'R'})"
            .equals(dc.lastQuery));
    db.deleteNode(fk);
    Assert.assertTrue(
        "MATCH(tableName:Table{name:'tableName'})-[]-(R_TestForeignKey{name:'R_TestForeignKey'}) DETACH DELETE R_TestForeignKey"
            .equals(dc.lastQuery));
    db.deleteNode(i2);
    Assert.assertTrue(
        "MATCH(tableName:Table{name:'tableName'})-[]-(XPK_tableName{name:'XPK_tableName'}) DETACH DELETE XPK_tableName"
            .equals(dc.lastQuery));
  }

  @Test
  public void testDatabaseManagerAdvanced() {
    final DatabaseMockConnector dc = new DatabaseMockConnector();
    final DatabaseConnection dbm = new DatabaseManager(dc);
    Assert.assertTrue(dbm.deleteDatabase() == false);
    dbm.connectDb("", "", "");
    Assert.assertTrue(dbm.isReady());
    dbm.deleteDatabase();
    Assert.assertTrue("MATCH(t) DETACH DELETE t".equals(dc.lastQuery));
    final Table t = new TableImpl(name);
    dbm.deleteNode(t);
    Assert.assertTrue(
        "MATCH(name:Table{name:'name'})-[ :DATA | :INDEX | :FOREIGNKEY | :XPK | :REFERENCE ]-(del) DETACH DELETE del, name"
            .equals(dc.lastQuery));
  }

  @Test
  public void testDatabaseManagerSave() {
    final DatabaseMockConnector dc = new DatabaseMockConnector();
    final DatabaseConnection dbm = new DatabaseManager(dc);
    dbm.connectDb("", "", "");
    Assert.assertTrue(dbm.isReady());
    final SaveDatabase db = dbm;
    final Field f = new FieldImpl(TestDatamodel.fieldName);
    final Table t = new TableImpl("TestTable");
    final Index i = new IndexImpl("TestIndex", Index.Type.XIF, t);
    i.addField(f);
    db.changeDataFields(i);

    Assert.assertTrue(
        "MATCH(t:Table{name:'TestTable'})-[]-(TestIndex{name:'TestIndex'}) MATCH(t)-[]-(TestFeld{name:'TestFeld'}) CREATE(TestIndex)-[:INDEX{order:'1'}]->(TestFeld)"
            .equals(dc.lastQuery));
    i.clearFieldList();
    i.addField(f, "test");
    db.changeDataFields(i);
    Assert.assertTrue(
        "MATCH(t:Table{name:'TestTable'})-[]-(TestIndex{name:'TestIndex'}) MATCH(t)-[]-(TestFeld{name:'TestFeld'}) CREATE(TestIndex)-[:INDEX{refname:'test',order:'1'}]->(TestFeld)"
            .equals(dc.lastQuery));
    db.changeFieldRequired("nodeName", "tableName", true);
    Assert.assertTrue(
        "MATCH(tableName:Table{name:'tableName'})-[]-(nodeName{name:'nodeName'}) SET nodeName.isRequired='true'"
            .equals(dc.lastQuery));
    db.changeFieldDomain("nodeName", "tableName", DomainId.STRING, 10);

    Assert.assertTrue(
        "MATCH(tableName:Table{name:'tableName'})-[]-(nodeName{name:'nodeName'}) SET nodeName.type='String:10'"
            .equals(dc.lastQuery));
    db.changeFkRelations("foreignKeyName", "tableName", "refTableName", "indexName");
    Assert.assertTrue(
        "MATCH(t:Table{name:'tableName'})-[:FOREIGNKEY]->(c:ForeignKey{name:'foreignKeyName'}), (b:Table{name:'refTableName'})-[:XPK]->(p:Index) CREATE(t)-[:CONNECTED{key:'foreignKeyName'}]->(b)  CREATE(c)-[:REFERENCE]->(p)"
            .equals(dc.lastQuery));
    db.changeIndexUnique("nodeName", "tableName", true);
    Assert
        .assertTrue("MATCH(tableName:Table{name:'tableName'})-[]-(nodeName{name:'nodeName'}) SET nodeName.unique='true'"
            .equals(dc.lastQuery));
    db.changeNodeNameFromTable("nodeName", "tableName", "newNodeName");
    Assert.assertTrue(
        "MATCH(tableName:Table{name:'tableName'})-[]-(nodeName{name:'nodeName'}) SET nodeName.name='newNodeName'"
            .equals(dc.lastQuery));
    db.changeTableCategory("tableName", "newKategory");
    Assert.assertTrue("MATCH(t:Table{name:'tableName'}) SET t.category='newKategory'".equals(dc.lastQuery));
    db.changeTableName("tableName", "tableName2", "XPKTableName");
    Assert
        .assertTrue("MATCH(t:Table{name:'tableName'})-[:XPK]->(XPK) SET t.name='tableName2' SET XPK.name='XPKTableName'"
            .equals(dc.lastQuery));
  }

  @Test
  public void testComment() {
    final DatabaseMockConnector dc = new DatabaseMockConnector();
    final DatabaseConnection dbm = new DatabaseManager(dc);
    dbm.connectDb("", "", "");
    Assert.assertTrue(dbm.isReady());
    final SaveDatabase db = dbm;
    db.saveComment("Table", "Comment");
    Assert.assertTrue("MATCH(t:Table{name:'Table'}) SET t.comment='Comment'".equals(dc.lastQuery));
    db.saveComment("Field", "Table", "Comment");
    Assert.assertTrue(
        "MATCH(Table:Table{name:'Table'})-[]-(Field{name:'Field'}) SET Field.comment='Comment'".equals(dc.lastQuery));
  }

  @Test
  public void testDatabaseManager() {
    final DatabaseMockConnector dc = new DatabaseMockConnector();
    dc.addr = test1;
    dc.nutzername = test2;
    dc.pw = test3;
    final DatabaseConnection dbm = new DatabaseManager(dc);
    Assert.assertTrue(!dbm.connectDb("", test3, test3));
    Assert.assertTrue(!dbm.isReady());
    Assert.assertTrue(dbm.connectDb(test1, test2, test3));
    Assert.assertTrue(dbm.isReady());
    dbm.createIndexOnDb();
    Assert.assertTrue(dc.lastQuery.equals(QueryBuilder.insertIndexOnDb(Strings.NODE_TABLE)));
    final Map<String, Object> map = new HashMap<>();
    map.put(name, test1);
    map.put("category", test2);
    final ArrayList<ResultUnit> lruTable = new ArrayList<>();
    lruTable.add(new ResultUnitImpl(map));
    dc.list2DResultUnit.add(lruTable);
    Assert.assertTrue(dbm.getListWithTableNames().contains(test1));
    // Tabellenquery
    dc.list2DResultUnit.add(lruTable);
    // FeldQuery
    final ArrayList<ResultUnit> lruField = new ArrayList<>();
    map.clear();
    map.put("name", test2);
    map.put("altName", test3);
    map.put("type", "type3");
    map.put("isRequired", true);
    lruField.add(new ResultUnitImpl(map));
    dc.list2DResultUnit.add(lruField);
    // XPK
    dc.list2DResultUnit.add(new ArrayList<>());
    // Index
    final ArrayList<ResultUnit> lruIndex = new ArrayList<>();
    map.clear();
    map.put(name, "XAK" + test3);
    map.put("unique", true);
    lruIndex.add(new ResultUnitImpl(map));
    dc.list2DResultUnit.add(lruIndex);
    final ArrayList<ResultUnit> lruIndexFields = new ArrayList<>();
    map.clear();
    map.put(name, test2);
    map.put("altName", test3);
    lruIndexFields.add(new ResultUnitImpl(map));
    dc.list2DResultUnit.add(lruIndexFields);
    // ForeignKey
    // Auswertung
    final Table t = dbm.getTables().get(0);
    Assert.assertTrue(t.getName().equals(test1));
    Assert.assertTrue(t.getCategory().equals(test2));
    Assert.assertTrue(t.getXpk().getName().equals("XPK" + test1));
    Assert.assertTrue(t.getFields().get(0).getName().equals(test2));
    // assertTrue(t.getData().get(0).getAltName().equals(test3));
    Assert.assertTrue(t.getFields().get(0).getTable().getName().equals(test1));
    Assert.assertTrue(t.getFields().get(0).getDomain().equals(DomainId.STRING));
    Assert.assertTrue(t.getFields().get(0).isRequired());
    Assert.assertTrue(!t.getFields().get(0).isPartOfPrimaryKey());
    Assert.assertTrue(t.getIndizies().get(0).getName().equals("XAK" + test3));
    Assert.assertTrue(t.getIndizies().get(0).getType().equals(Index.Type.XAK));
    Assert.assertTrue(t.getIndizies().get(0).getFieldList().get(0).getName().equals(test2));
    dbm.disconnectDb();
    Assert.assertTrue(!dbm.isReady());
  }

  @Test
  public void testListWithCategories() {
    final DatabaseMockConnector dc = new DatabaseMockConnector();
    final DatabaseConnection dbm = new DatabaseManager(dc);
    dbm.connectDb("", "", "");
    Assert.assertTrue(dbm.isReady());
    final Map<String, Object> map = new HashMap<>();
    map.put(name, test1);
    map.put("category", test2);
    final ArrayList<ResultUnit> lruTable = new ArrayList<>();
    lruTable.add(new ResultUnitImpl(map));
    dc.list2DResultUnit.add(lruTable);
    Assert.assertTrue(dbm.getListWithCategories().contains(test2));
  }

  @Test
  public void testResultUnit() {
    final Map<String, Object> map = new HashMap<>();
    map.put(Strings.IDENT_NAME, test1);
    map.put(Strings.IDENT_UNIQUE, true);
    map.put(Strings.IDENT_ALTNAME, null);
    final ResultUnit ru = new ResultUnitImpl(map);
    Assert.assertTrue(ru.get(Strings.IDENT_NAME).equals(test1));
    Assert.assertTrue(ru.getBoolean(Strings.IDENT_UNIQUE) == true);
    Assert.assertTrue(ru.get(Strings.IDENT_ALTNAME).equals(Strings.EMPTYSTRING));
  }

  @Test
  public void testPrimKeyNameChange() {
    final DatabaseMockConnector dc = new DatabaseMockConnector();
    final DatabaseConnection dbm = new DatabaseManager(dc);
    dbm.connectDb("", "", "");
    Assert.assertTrue(dbm.isReady());
    dbm.changePrimFieldNameRelation("Tablename", "NewNodeName", "OldNodeName");
    Assert.assertTrue(
        ("MATCH(t:Table{name:'Tablename'})-[:XPK]->(:Index)<-[:REFERENCE]-(fk:ForeignKey)-[:FOREIGNKEY]->(i:Index)-[r:INDEX{refname:'OldNodeName'}]"
            + "-(f:Field) SET r.refname='NewNodeName'").equals(dc.lastQuery));

  }
}
