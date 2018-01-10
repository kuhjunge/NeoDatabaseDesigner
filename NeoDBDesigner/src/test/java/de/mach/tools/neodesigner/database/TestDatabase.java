package de.mach.tools.neodesigner.database;

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
import de.mach.tools.neodesigner.database.DatabaseManager;
import de.mach.tools.neodesigner.database.QueryBuilder;
import de.mach.tools.neodesigner.database.ResultUnit;

import java.util.ArrayList;
import java.util.HashMap;
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
		Assert.assertTrue(dbm.isOnline());
		dbm.getTable(name);
		Assert.assertTrue(
				"MATCH(t:Table{name:'name'}) RETURN t.name AS name, t.category AS category".equals(dc.lastQuery));
		final ArrayList<ResultUnit> lruIndexFields = new ArrayList<>();
		final Map<String, Object> map = new HashMap<>();
		map.put(name, name);
		map.put("category", category);
		lruIndexFields.add(new ResultUnitImpl(map));
		dc.list2DResultUnit.add(lruIndexFields);
		final Table t = dbm.getTable(name);
		Assert.assertTrue(t.getName().equals(name));
		Assert.assertTrue(t.getCategory().equals(category));
		Assert.assertTrue(
				"MATCH(t:Table{name:'name'})-[:INDEX]->(i:Index) RETURN DISTINCT i.name AS name , i.unique AS unique ORDER BY name"
						.equals(dc.lastQuery));
	}

	@Test
	public void getFKNumber() {
		final DatabaseMockConnector dc = new DatabaseMockConnector();
		final DatabaseConnection dbm = new DatabaseManager(dc);
		dbm.connectDb("", "", "");
		Assert.assertTrue(dbm.isOnline());
		dbm.getForeignKeyNumber(3);
		Assert.assertTrue(
				"MATCH(b:Table)-[:FOREIGNKEY]->(n:Index) where n.name =~ 'R_([0-9]){3}' return n.name As name order by name DESC Limit 1"
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
	}

	@Test
	public void insertNewTable() {
		final DatabaseMockConnector dc = new DatabaseMockConnector();
		final DatabaseConnection dbm = new DatabaseManager(dc);
		dbm.connectDb("", "", "");
		Assert.assertTrue(dbm.isOnline());
		final Table table = new TableImpl(name);
		table.setCategory(test1);
		dbm.insertNewTable(table);
		Assert.assertTrue("CREATE(t:Table{name:'name',category:'Test1'})".equals(dc.lastQuery));

		final Table t = new TableImpl("tableName");
		final Table rt = new TableImpl("tableName");
		final Index i = new IndexImpl("indexName", Index.Type.XAK, t);
		final Index i2 = new IndexImpl("XPK_tableName", t);
		final Field f2 = new FieldImpl("fieldName", "fieldDatatype", true, t);
		final ForeignKey fk = new ForeignKeyImpl(TestDatamodel.foreignKeyName, t);
		fk.setRefTable(rt);
		fk.setIndex(i);
		fk.addField(f2);
		final SaveDatabase db = dbm;
		db.insertNewField(f2);
		Assert.assertTrue(
				"MATCH(t:Table{name:'tableName'}) CREATE(t_fieldName:Field{name:'fieldName',type:'fieldDatatype',isRequired:'true'}) CREATE(t)-[:DATA]->(t_fieldName)"
						.equals(dc.lastQuery));
		db.insertNewIndex(i);
		Assert.assertTrue(
				"MATCH(t:Table{name:'tableName'}) MERGE(t_indexName:Index{name:'indexName',type:'XAK',unique:'true'}) MERGE(t)-[:INDEX]->(t_indexName)"
						.equals(dc.lastQuery));
		db.insertNewIndex(i2);
		Assert.assertTrue(
				"MATCH(t:Table{name:'tableName'}) MERGE(t_XPK_tableName:Index{name:'XPK_tableName',type:'XPK'}) MERGE(t)-[:XPK]->(t_XPK_tableName)"
						.equals(dc.lastQuery));
		db.insertNewForeignKey(fk);
		Assert.assertTrue(
				"MATCH(t:Table{name:'tableName'}) MERGE(t_R_TestForeignKey:Index{name:'R_TestForeignKey',type:'R'}) MERGE(t)-[:FOREIGNKEY]->(t_R_TestForeignKey)"
						.equals(dc.lastQuery));
		db.deleteNode(fk);
		Assert.assertTrue(
				"MATCH(tableName:Table{name:'tableName'})--(R_TestForeignKey{name:'R_TestForeignKey'}) DETACH DELETE R_TestForeignKey"
						.equals(dc.lastQuery));
		db.deleteNode(i2);
		Assert.assertTrue(
				"MATCH(tableName:Table{name:'tableName'})--(XPK_tableName{name:'XPK_tableName'}) DETACH DELETE XPK_tableName"
						.equals(dc.lastQuery));
	}

	@Test
	public void testDatabaseManagerAdvanced() {
		final DatabaseMockConnector dc = new DatabaseMockConnector();
		final DatabaseConnection dbm = new DatabaseManager(dc);
		Assert.assertTrue(dbm.deleteDatabase() == false);
		dbm.connectDb("", "", "");
		Assert.assertTrue(dbm.isOnline());
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
		Assert.assertTrue(dbm.isOnline());
		final SaveDatabase db = dbm;
		final Field f = new FieldImpl(TestDatamodel.fieldName);
		final Table t = new TableImpl("TestTable");
		final Index i = new IndexImpl("TestIndex", Index.Type.XIF, t);
		i.addField(f);
		db.changeDataFields(i);
		Assert.assertTrue(
				"MATCH(t:Table{name:'TestTable'})--(TestIndex{name:'TestIndex'}) MATCH(t)--(TestFeld{name:'TestFeld'}) MERGE(TestIndex)-[:INDEX{order:'1'}]->(TestFeld)"
						.equals(dc.lastQuery));
		i.clearFieldList();
		i.addField(f, "test");
		db.changeDataFields(i);
		Assert.assertTrue(
				"MATCH(t:Table{name:'TestTable'})--(TestIndex{name:'TestIndex'}) MATCH(t)--(TestFeld{name:'TestFeld'}) MERGE(TestIndex)-[:INDEX{refname:'test',order:'1'}]->(TestFeld)"
						.equals(dc.lastQuery));
		db.changeFieldRequired("nodeName", "tableName", true);
		Assert.assertTrue(
				"MATCH(tableName:Table{name:'tableName'})--(nodeName{name:'nodeName'}) SET nodeName.isRequired='true'"
						.equals(dc.lastQuery));
		db.changeFieldTypeOfData("nodeName", "tableName", "type");
		Assert.assertTrue(
				"MATCH(tableName:Table{name:'tableName'})--(nodeName{name:'nodeName'}) SET nodeName.type='type'"
						.equals(dc.lastQuery));
		db.changeFkRelations("foreignKeyName", "tableName", "refTableName", "indexName");
		Assert.assertTrue(
				"MATCH(t:Table{name:'tableName'}) MATCH(d:Table{name:'refTableName'}) MATCH(t)-[:FOREIGNKEY]->(c {name:'foreignKeyName'}) MATCH(d)-[:XPK]->(xpk) CREATE(t)-[:CONNECTED{key:'foreignKeyName'}]->(d)  MERGE(c)-[:REFERENCE]->(xpk)"
						.equals(dc.lastQuery));
		db.changeIndexUnique("nodeName", "tableName", true);
		Assert.assertTrue(
				"MATCH(tableName:Table{name:'tableName'})--(nodeName{name:'nodeName'}) SET nodeName.unique='true'"
						.equals(dc.lastQuery));
		db.changeNodeNameFromTable("nodeName", "tableName", "newNodeName");
		Assert.assertTrue(
				"MATCH(tableName:Table{name:'tableName'})--(nodeName{name:'nodeName'}) SET nodeName.name='newNodeName'"
						.equals(dc.lastQuery));
		db.changeTableCategory("tableName", "newKategory");
		Assert.assertTrue("MATCH(t:Table{name:'tableName'}) SET t.category='newKategory'".equals(dc.lastQuery));
	}

	@Test
	public void testDatabaseManager() {
		final DatabaseMockConnector dc = new DatabaseMockConnector();
		dc.addr = test1;
		dc.nutzername = test2;
		dc.pw = test3;
		final DatabaseConnection dbm = new DatabaseManager(dc);
		Assert.assertTrue(!dbm.connectDb("", test3, test3));
		Assert.assertTrue(!dbm.isOnline());
		Assert.assertTrue(dbm.connectDb(test1, test2, test3));
		Assert.assertTrue(dbm.isOnline());
		dbm.createIndexOnDb();
		Assert.assertTrue(dc.lastQuery.equals(QueryBuilder.insertIndexOnDb()));
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
		final Table t = dbm.getAllTables().get(0);
		Assert.assertTrue(t.getName().equals(test1));
		Assert.assertTrue(t.getCategory().equals(test2));
		Assert.assertTrue(t.getXpk().getName().equals("XPK" + test1));
		Assert.assertTrue(t.getData().get(0).getName().equals(test2));
		// assertTrue(t.getData().get(0).getAltName().equals(test3));
		Assert.assertTrue(t.getData().get(0).getNodeOf().getName().equals(test1));
		Assert.assertTrue(t.getData().get(0).getTypeOfData().equals("type3"));
		Assert.assertTrue(t.getData().get(0).isRequired());
		Assert.assertTrue(!t.getData().get(0).isPartOfPrimaryKey());
		Assert.assertTrue(t.getIndizies().get(0).getName().equals("XAK" + test3));
		Assert.assertTrue(t.getIndizies().get(0).getType().equals(Index.Type.XAK));
		Assert.assertTrue(t.getIndizies().get(0).getFieldList().get(0).getName().equals(test2));
		dbm.disconnectDb();
		Assert.assertTrue(!dbm.isOnline());
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
		Assert.assertTrue(ru.get(Strings.IDENT_ALTNAME).equals(Strings.NULL));
	}
}
