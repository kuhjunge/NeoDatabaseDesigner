package de.mach.tools.neodesigner.database;

import org.junit.Assert;

import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.core.datamodel.Relations.Type;
import de.mach.tools.neodesigner.core.datamodel.impl.FieldImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.ForeignKeyImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.IndexImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.TableImpl;
import de.mach.tools.neodesigner.database.QueryBuilder;

import org.junit.Test;

public class TestQueryBuilder {
	@Test
	public void testCypherImport() {
		final QueryBuilder qb = new QueryBuilder();
		qb.importTable(getTable());
		Assert.assertTrue(
				"CREATE(t:Table{name:'Table1',category:'1,1'}) CREATE(t_Feld1:Field{name:'Feld1',type:'VARCHAR2(50)',isRequired:'true'}) CREATE(t)-[:DATA]->(t_Feld1) CREATE(t_FeldZwei1:Field{name:'FeldZwei1',type:'INTEGER',isRequired:'true'}) CREATE(t)-[:DATA]->(t_FeldZwei1) CREATE(t_FeldDrei1:Field{name:'FeldDrei1',type:'DATE',isRequired:'false'}) CREATE(t)-[:DATA]->(t_FeldDrei1) MERGE(t_XPKTable1:Index{name:'XPKTable1',type:'XPK'}) MERGE(t)-[:XPK]->(t_XPKTable1) MERGE(t_XPKTable1)-[:XPK{order:'1'}]->(t_FeldZwei1) MERGE(t_XIE101TestIndex:Index{name:'XIE101TestIndex',type:'XIE',unique:'false'}) MERGE(t)-[:INDEX]->(t_XIE101TestIndex) MERGE(t_XIE101TestIndex)-[:INDEX{order:'1'}]->(t_Feld1) MERGE(t_XIF101BlaBlub:Index{name:'XIF101BlaBlub',type:'XIF',unique:'false'}) MERGE(t)-[:INDEX]->(t_XIF101BlaBlub) MERGE(t_XIF101BlaBlub)-[:INDEX{refname:'',order:'1'}]->(t_Feld1) MERGE(t_R_101:Index{name:'R_101',type:'R'}) MERGE(t)-[:FOREIGNKEY]->(t_R_101) CREATE(t_R_101)-[:FOREIGNKEY]->(t_XIF101BlaBlub)"
						.equals(qb.toString()));

		qb.importForeignKey(getTable().getForeignKeys().get(0));
		Assert.assertTrue(
				"MATCH(t:Table{name:'Table1'}) MATCH(t_R_101:Index{name:'R_101'})--(t) MATCH(b:Table{name:'Table1'}) MATCH(b_XPKTable1:Index{name:'XPKTable1'})--(b) MATCH(t)-[:FOREIGNKEY]->(R_101 {name:'R_101'}) MATCH(b)-[:XPK]->(xpk) CREATE(t)-[:CONNECTED{key:'R_101'}]->(b)  MERGE(R_101)-[:REFERENCE]->(xpk)"
						.equals(runCypher(qb.getQuery(), qb.getKeys())));
		// System.out.println(runCypher(qb.getQuery(), qb.getKeys()));
	}

	@Test
	public void testCypherChange() {
		final QueryBuilder qb = new QueryBuilder();
		qb.changeTableName("Test", "Test1");
		Assert.assertTrue(
				"MATCH(t:Table{name:'Test'}) SET t.name='Test1'".equals(runCypher(qb.getQuery(), qb.getKeys())));
		qb.clear();
		qb.getFieldsOfIndex("Table", "Index", Type.CONNECTED);
		Assert.assertTrue(
				"MATCH(t:Table{name:'Table'})--(f:Field)<-[r:CONNECTED]-(i{name:'Index'}) RETURN f.name AS name, f.isRequired AS isRequired, f.type AS type,r.refname AS altName, r.order AS order ORDER BY toInt(order)"
						.equals(runCypher(qb.getQuery(), qb.getKeys())));
		qb.clear();
		qb.changeTableCategory("Table", "Kategorie");
		Assert.assertTrue("MATCH(t:Table{name:'Table'}) SET t.category='Kategorie'"
				.equals(runCypher(qb.getQuery(), qb.getKeys())));
		qb.clear();
		qb.changeNodeName("NodeName", "TableName", "NewName");
		Assert.assertTrue(
				"MATCH(TableName:Table{name:'TableName'})--(NodeName{name:'NodeName'}) SET NodeName.name='NewName'"
						.equals(runCypher(qb.getQuery(), qb.getKeys())));
		qb.clear();
		qb.changeFieldRequired("nodeName", "tableName", true);
		Assert.assertTrue(
				"MATCH(tableName:Table{name:'tableName'})--(nodeName{name:'nodeName'}) SET nodeName.isRequired='true'"
						.equals(runCypher(qb.getQuery(), qb.getKeys())));
		qb.clear();
		qb.changeFieldTypeOfData("nodeName", "tableName", "type");
		Assert.assertTrue(
				"MATCH(tableName:Table{name:'tableName'})--(nodeName{name:'nodeName'}) SET nodeName.type='type'"
						.equals(runCypher(qb.getQuery(), qb.getKeys())));
		qb.clear();
		qb.changeIndexUnique("nodeName", "tableName", true);
		Assert.assertTrue(
				"MATCH(tableName:Table{name:'tableName'})--(nodeName{name:'nodeName'}) SET nodeName.unique='true'"
						.equals(runCypher(qb.getQuery(), qb.getKeys())));
	}

	@Test
	public void testCypherDelete() {
		final QueryBuilder qb = new QueryBuilder();
		qb.deletePrimKeyRel("nodeName", "tableName");
		Assert.assertTrue(
				"MATCH(t:Table{name:'tableName'})--(nodeName{name:'nodeName'})<-[r:XPK]-(primKey:Index) DELETE r"
						.equals(runCypher(qb.getQuery(), qb.getKeys())));
		qb.clear();
		qb.deleteIndexToFieldRelation("tableName", "indexName");
		Assert.assertTrue("MATCH(t:Table{name:'tableName'})--(indexName{name:'indexName'})-[r:INDEX]->(:Field) delete r"
				.equals(runCypher(qb.getQuery(), qb.getKeys())));
		qb.clear();
		qb.deleteConnectionBetweenTables("tableName", "foreignKeyName");
		Assert.assertTrue("MATCH(t:Table{name:'tableName'})-[r:CONNECTED {key:'foreignKeyName'}]-(b:Table) DELETE r"
				.equals(runCypher(qb.getQuery(), qb.getKeys())));
		qb.clear();
		qb.deleteNodeWithRelations("nodeName", "tableName");
		Assert.assertTrue("MATCH(tableName:Table{name:'tableName'})--(nodeName{name:'nodeName'}) DETACH DELETE nodeName"
				.equals(runCypher(qb.getQuery(), qb.getKeys())));
		qb.clear();
		qb.deleteTable("name");
		Assert.assertTrue(
				"MATCH(name:Table{name:'name'})-[ :DATA | :INDEX | :FOREIGNKEY | :XPK | :REFERENCE ]-(del) DETACH DELETE del, name"
						.equals(runCypher(qb.getQuery(), qb.getKeys())));
		qb.clear();
		qb.deleteRelationship("foreignKeyName", "tableName", Type.DEFAULT);
		Assert.assertTrue("MATCH(t:Table{name:'foreignKeyName'})--(tableName{name:'tableName'})-[r:]->(:Index) delete r"
				.equals(runCypher(qb.getQuery(), qb.getKeys())));
		qb.clear();
		qb.deleteDatabase();
		Assert.assertTrue("MATCH(t) DETACH DELETE t".equals(runCypher(qb.getQuery(), qb.getKeys())));
	}

	@Test
	public void testCypherAdd() {
		final QueryBuilder qb = new QueryBuilder();
		qb.addRelation("tableName", "indexName", "fieldName", Type.CONNECTED);
		Assert.assertTrue(
				"MATCH(t:Table{name:'tableName'})--(indexName{name:'indexName'}) MATCH(t)--(fieldName{name:'fieldName'}) MERGE(indexName)-[:CONNECTED]->(fieldName)"
						.equals(runCypher(qb.getQuery(), qb.getKeys())));
		qb.clear();
		qb.addFkRelation("tableName", "refTableName", "foreignKeyName");
		Assert.assertTrue(
				"MATCH(t:Table{name:'tableName'}) MATCH(d:Table{name:'refTableName'}) MATCH(t)-[:FOREIGNKEY]->(c {name:'foreignKeyName'}) MATCH(d)-[:XPK]->(xpk) CREATE(t)-[:CONNECTED{key:'foreignKeyName'}]->(d)  MERGE(c)-[:REFERENCE]->(xpk)"
						.equals(runCypher(qb.getQuery(), qb.getKeys())));
		qb.clear();
		qb.addPrimKeyRel("nodeName", "tableName", 5);
		Assert.assertTrue(
				"MATCH(t:Table{name:'tableName'})--(nodeName{name:'nodeName'}) MATCH(t)-[:XPK]->(pk:Index) MERGE(nodeName)<-[:XPK{order:'5'}]-(pk)"
						.equals(runCypher(qb.getQuery(), qb.getKeys())));
		qb.clear();
		final Table t = new TableImpl("tableName");
		final Index i = new IndexImpl("indexName", Index.Type.XAK, t);
		final Field f = new FieldImpl("fieldName", "fieldDatatype", true, t);
		qb.addNewField(f);
		Assert.assertTrue(
				"MATCH(t:Table{name:'tableName'}) CREATE(t_fieldName:Field{name:'fieldName',type:'fieldDatatype',isRequired:'true'}) CREATE(t)-[:DATA]->(t_fieldName)"
						.equals(runCypher(qb.getQuery(), qb.getKeys())));
		qb.clear();
		qb.addNewIndex(i, Type.FOREIGNKEY);
		Assert.assertTrue(
				"MATCH(t:Table{name:'tableName'}) MERGE(t_indexName:Index{name:'indexName',type:'XAK'}) MERGE(t)-[:FOREIGNKEY]->(t_indexName)"
						.equals(runCypher(qb.getQuery(), qb.getKeys())));

	}

	@Test
	public void testCypher() {
		final QueryBuilder qb = new QueryBuilder();
		qb.getAllTables();
		Assert.assertTrue("MATCH(t:Table) RETURN t.name AS name, t.category AS category ORDER BY name"
				.equals(runCypher(qb.getQuery(), qb.getKeys())));
		qb.clear();
		Assert.assertTrue("".equals(runCypher(qb.getQuery(), qb.getKeys())));
		qb.getForeignKeyNumber(3);
		Assert.assertTrue(
				"MATCH(b:Table)-[:FOREIGNKEY]->(n:Index) where n.name =~ 'R_([0-9]){3}' return n.name As name order by name DESC Limit 1"
						.equals(runCypher(qb.getQuery(), qb.getKeys())));
		qb.clear();
		qb.getTable("Test");
		Assert.assertTrue("MATCH(t:Table{name:'Test'}) RETURN t.name AS name, t.category AS category"
				.equals(runCypher(qb.getQuery(), qb.getKeys())));
		qb.clear();
		qb.getFieldsOfTable("Test");

		Assert.assertTrue(
				"MATCH(t:Table{name:'Test'})--(f:Field) RETURN f.name AS name, f.isRequired AS isRequired, f.type AS type ORDER BY name"
						.equals(runCypher(qb.getQuery(), qb.getKeys())));
		qb.clear();
		qb.getIndizesOfTable("Test", Type.DATA, true);
		Assert.assertTrue(
				"MATCH(t:Table{name:'Test'})-[:DATA]->(i:Index) RETURN DISTINCT i.name AS name , i.unique AS unique ORDER BY name"
						.equals(runCypher(qb.getQuery(), qb.getKeys())));
		qb.clear();
		qb.getIndizesOfTable("Test", Type.DATA, false);
		Assert.assertTrue("MATCH(t:Table{name:'Test'})-[:DATA]->(i:Index) RETURN DISTINCT i.name AS name  ORDER BY name"
				.equals(runCypher(qb.getQuery(), qb.getKeys())));
		qb.clear();
		qb.getForeignKeysOfTable("Test");
		Assert.assertTrue(
				"MATCH(t:Table{name:'Test'})-[:INDEX]->(f)<-[:FOREIGNKEY]-(i:Index)-[:REFERENCE]->(p:Index)<-[:XPK]-(b:Table) RETURN DISTINCT i.name AS name,b.name as refTable,f.name as xif ORDER BY name"
						.equals(runCypher(qb.getQuery(), qb.getKeys())));
		qb.clear();
		Assert.assertTrue("CREATE INDEX ON :Table(name);".equals(QueryBuilder.insertIndexOnDb()));
	}

	private Object runCypher(final String query, final Object[] keys) {
		return QueryBuilder.queryToString(keys, query);
	}

	private Table getTable() {
		final int j = 1;
		final Table t = new TableImpl("Table" + j);
		t.setCategory("1," + j);
		final Field f = new FieldImpl("Feld" + j, "VARCHAR2(50)", true, t);
		final Field f2 = new FieldImpl("FeldZwei" + j, "INTEGER", true, t);
		final Field f3 = new FieldImpl("FeldDrei" + j, "DATE", false, t);
		final Index i = new IndexImpl("XIF10" + j + "BlaBlub", t);
		final Index i2 = new IndexImpl("XIE10" + j + "TestIndex", t);
		final Index xpk = new IndexImpl("XPKTable" + j, t);
		xpk.addField(f2);
		final ForeignKey fk = new ForeignKeyImpl("R_10" + j, t);
		t.addField(f);
		t.addField(f2);
		t.addField(f3);
		i.addField(f);
		i2.addField(f);
		fk.setIndex(i);
		fk.setRefTable(t);
		t.getIndizies().add(i2);
		t.getIndizies().add(i);
		t.getForeignKeys().add(fk);
		t.setXpk(xpk);
		return t;
	}
}
