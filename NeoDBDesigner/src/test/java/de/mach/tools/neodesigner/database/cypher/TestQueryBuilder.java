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

import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Relations.Type;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.core.datamodel.Domain.DomainId;
import de.mach.tools.neodesigner.core.datamodel.impl.FieldImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.ForeignKeyImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.IndexImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.TableImpl;
import de.mach.tools.neodesigner.database.Strings;
import de.mach.tools.neodesigner.database.cypher.QueryBuilder;

import org.junit.Assert;
import org.junit.Test;

public class TestQueryBuilder {
  @Test
  public void testCypherImport() {
    final QueryBuilder qb = new QueryBuilder();
    qb.importTable(getTable());

    Assert.assertTrue(
        "CREATE(t:Table{name:'Table1',category:'1,1',comment:'AComment'}) CREATE(f1:Field{name:'Feld1',type:'String:10',isRequired:'true'})<-[:DATA]-(t) CREATE(f2:Field{name:'FeldZwei1',type:'Amount',isRequired:'true'})<-[:DATA]-(t) CREATE(f3:Field{name:'FeldDrei1',type:'Date',isRequired:'false'})<-[:DATA]-(t) CREATE(p:Index{name:'XPKTable1',type:'XPK'})<-[:XPK]-(t) CREATE(p)-[:XPK{order:'1'}]->(f2) CREATE(i1:Index{name:'XIE101TestIndex',type:'XIE',unique:'false'})<-[:INDEX]-(t) CREATE(i1)-[:INDEX{order:'1'}]->(f1) CREATE(i2:Index{name:'XIF101BlaBlub',type:'XIF',unique:'false'})<-[:INDEX]-(t) CREATE(i2)-[:INDEX{refname:'',order:'1'}]->(f1) CREATE(t)-[:FOREIGNKEY]->(c1:ForeignKey{name:'R_101',type:'R'})-[:FOREIGNKEY]->(i2)"
            .contains(qb.toString()));
    qb.importForeignKey(getTable().getForeignKeys().get(0));
    Assert.assertTrue(
        "MATCH(t:Table{name:'Table1'})-[:FOREIGNKEY]->(c:ForeignKey{name:'R_101'}), (b:Table{name:'Table1'})-[:XPK]->(p:Index) CREATE(t)-[:CONNECTED{key:'R_101'}]->(b)  CREATE(c)-[:REFERENCE]->(p)"
            .equals(runCypher(qb.getQuery(), qb.getKeys())));
  }

  @Test
  public void testCypherChange() {
    final QueryBuilder qb = new QueryBuilder();
    qb.changeTableName("Test", "Test1", "XPKTest1");
    Assert.assertTrue("MATCH(t:Table{name:'Test'})-[:XPK]->(XPK) SET t.name='Test1' SET XPK.name='XPKTest1'"
        .equals(runCypher(qb.getQuery(), qb.getKeys())));
    qb.clear();
    qb.getFieldsOfIndex("Table", "Index", Type.CONNECTED);
    Assert.assertTrue(
        "MATCH(t:Table{name:'Table'})--(f:Field)<-[r:CONNECTED]-(i{name:'Index'}) RETURN f.name AS name, f.isRequired AS isRequired, f.comment AS comment, f.type AS type,r.refname AS altName, r.order AS order ORDER BY toInt(order)"
            .equals(runCypher(qb.getQuery(), qb.getKeys())));
    qb.clear();
    qb.changeTableCategory("Table", "Kategorie");
    Assert.assertTrue(
        "MATCH(t:Table{name:'Table'}) SET t.category='Kategorie'".equals(runCypher(qb.getQuery(), qb.getKeys())));
    qb.clear();
    qb.changeNodeName("NodeName", "TableName", "NewName");
    Assert.assertTrue(
        "MATCH(TableName:Table{name:'TableName'})-[]-(NodeName{name:'NodeName'}) SET NodeName.name='NewName'"
            .equals(runCypher(qb.getQuery(), qb.getKeys())));
    qb.clear();
    qb.changeFieldRequired("nodeName", "tableName", true);
    Assert.assertTrue(
        "MATCH(tableName:Table{name:'tableName'})-[]-(nodeName{name:'nodeName'}) SET nodeName.isRequired='true'"
            .equals(runCypher(qb.getQuery(), qb.getKeys())));
    qb.clear();
    qb.changeFieldTypeOfData("nodeName", "tableName", DomainId.STRING, 30);
    Assert.assertTrue(
        "MATCH(tableName:Table{name:'tableName'})-[]-(nodeName{name:'nodeName'}) SET nodeName.type='String:30'"
            .equals(runCypher(qb.getQuery(), qb.getKeys())));
    qb.clear();
    qb.changeIndexUnique("nodeName", "tableName", true);
    Assert
        .assertTrue("MATCH(tableName:Table{name:'tableName'})-[]-(nodeName{name:'nodeName'}) SET nodeName.unique='true'"
            .equals(runCypher(qb.getQuery(), qb.getKeys())));
  }

  @Test
  public void testCypherDelete() {
    final QueryBuilder qb = new QueryBuilder();
    qb.deletePrimKeyRel("nodeName", "tableName");
    Assert.assertTrue("MATCH(t:Table{name:'tableName'})-[]-(nodeName{name:'nodeName'})<-[r:XPK]-(:Index) DELETE r"
        .equals(runCypher(qb.getQuery(), qb.getKeys())));
    qb.clear();
    qb.deleteIndexToFieldRelation("tableName", "indexName");
    Assert.assertTrue("MATCH(t:Table{name:'tableName'})-[]-(indexName{name:'indexName'})-[r:INDEX]->(:Field) delete r"
        .equals(runCypher(qb.getQuery(), qb.getKeys())));
    qb.clear();
    qb.deleteConnectionBetweenTables("tableName", "foreignKeyName");
    Assert.assertTrue("MATCH(t:Table{name:'tableName'})-[r:CONNECTED {key:'foreignKeyName'}]-(b:Table) DELETE r"
        .equals(runCypher(qb.getQuery(), qb.getKeys())));
    qb.clear();
    qb.deleteNodeWithRelations("nodeName", "tableName");
    Assert.assertTrue("MATCH(tableName:Table{name:'tableName'})-[]-(nodeName{name:'nodeName'}) DETACH DELETE nodeName"
        .equals(runCypher(qb.getQuery(), qb.getKeys())));
    qb.clear();
    qb.deleteTable("name");
    Assert.assertTrue(
        "MATCH(name:Table{name:'name'})-[ :DATA | :INDEX | :FOREIGNKEY | :XPK | :REFERENCE ]-(del) DETACH DELETE del, name"
            .equals(runCypher(qb.getQuery(), qb.getKeys())));
    qb.clear();
    qb.deleteRelationship("tableName", "foreignKeyName", Type.CONNECTED);
    Assert.assertTrue(
        "MATCH(t:Table{name:'tableName'})-[:FOREIGNKEY]-(c{name:'foreignKeyName'})-[r:CONNECTED]->(:Index) delete r"
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
        "MATCH(t:Table{name:'tableName'})-[]-(indexName{name:'indexName'}) MATCH(t)-[]-(fieldName{name:'fieldName'}) CREATE(indexName)-[:CONNECTED]->(fieldName)"
            .equals(runCypher(qb.getQuery(), qb.getKeys())));
    qb.clear();
    qb.addFkRelation("tableName", "refTableName", "foreignKeyName");
    Assert.assertTrue(
        "MATCH(t:Table{name:'tableName'})-[:FOREIGNKEY]->(c:ForeignKey{name:'foreignKeyName'}), (b:Table{name:'refTableName'})-[:XPK]->(p:Index) CREATE(t)-[:CONNECTED{key:'foreignKeyName'}]->(b)  CREATE(c)-[:REFERENCE]->(p)"
            .equals(runCypher(qb.getQuery(), qb.getKeys())));
    qb.clear();
    qb.addPrimKeyRel("nodeName", "tableName", 5);
    Assert.assertTrue(
        "MATCH(t:Table{name:'tableName'})-[]-(nodeName{name:'nodeName'}) MATCH(t)-[:XPK]->(pk:Index) CREATE(nodeName)<-[:XPK{order:'5'}]-(pk)"
            .equals(runCypher(qb.getQuery(), qb.getKeys())));
    qb.clear();
    final Table t = new TableImpl("tableName");
    final Index i = new IndexImpl("indexName", Index.Type.XAK, t);
    final Field f = new FieldImpl("fieldName", DomainId.STRING, 15, true, "", t);
    qb.addNewField(f);
    Assert.assertTrue(
        "MATCH(t:Table{name:'tableName'}) CREATE(t_fieldName:Field{name:'fieldName',type:'String:15',isRequired:'true'})<-[:DATA]-(t)"
            .equals(runCypher(qb.getQuery(), qb.getKeys())));
    qb.clear();
    qb.addNewIndex(i, Type.FOREIGNKEY);
    Assert.assertTrue("MATCH(t:Table{name:'tableName'}) CREATE(p:Index{name:'indexName',type:'XAK'})<-[:FOREIGNKEY]-(t)"
        .equals(runCypher(qb.getQuery(), qb.getKeys())));

  }

  @Test
  public void testCypher() {
    final QueryBuilder qb = new QueryBuilder();
    qb.getAllTables();
    Assert.assertTrue("MATCH(t:Table) RETURN t.name AS name, t.category AS category, t.comment AS comment ORDER BY name"
        .equals(runCypher(qb.getQuery(), qb.getKeys())));
    qb.clear();
    Assert.assertTrue("".equals(runCypher(qb.getQuery(), qb.getKeys())));
    qb.getForeignKeyNumber(3);
    Assert.assertTrue(
        "MATCH(b:Table)-[:FOREIGNKEY]->(n:ForeignKey) where n.name =~ 'R_([0-9]){3}' return n.name As name order by name DESC Limit 1"
            .equals(runCypher(qb.getQuery(), qb.getKeys())));
    qb.clear();
    qb.getTable("Test");
    Assert.assertTrue(
        "MATCH(t:Table{name:'Test'}) RETURN t.name AS name, t.category AS category, t.comment AS comment LIMIT 1"
            .equals(runCypher(qb.getQuery(), qb.getKeys())));
    qb.clear();
    qb.getFieldsOfTable("Test");
    Assert.assertTrue(
        "MATCH(t:Table{name:'Test'})--(f:Field) RETURN f.name AS name, f.isRequired AS isRequired, f.comment AS comment, f.type AS type ORDER BY name"
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
        "MATCH(t:Table{name:'Test'})-[:INDEX]->(f)<-[:FOREIGNKEY]-(i:ForeignKey)-[:REFERENCE]->(p:Index)<-[:XPK]-(b:Table) RETURN DISTINCT i.name AS name,b.name as refTable,f.name as xif ORDER BY name"
            .equals(runCypher(qb.getQuery(), qb.getKeys())));
    qb.clear();
    Assert.assertTrue("CREATE CONSTRAINT ON (t:Table) ASSERT t.name IS UNIQUE"
        .equals(QueryBuilder.insertIndexOnDb(Strings.NODE_TABLE)));
  }

  @Test
  public void testCypherGetIndistinctName() {
    final QueryBuilder qb = new QueryBuilder();
    qb.getFieldNameCase("Test");
    Assert.assertTrue("MATCH(t:Field) WHERE t.name =~ '(?i)Test' return distinct t.name as name"
        .equals(runCypher(qb.getQuery(), qb.getKeys())));
  }

  @Test
  public void testCypherCommentTable() {
    final QueryBuilder qb = new QueryBuilder();
    qb.changeComment("Table", "Commenttext");
    Assert.assertTrue(
        "MATCH(t:Table{name:'Table'}) SET t.comment='Commenttext'".equals(runCypher(qb.getQuery(), qb.getKeys())));
  }

  @Test
  public void testCypherComment() {
    final QueryBuilder qb = new QueryBuilder();
    qb.changeComment("Field", "Table", "Commenttext");
    Assert.assertTrue("MATCH(Table:Table{name:'Table'})-[]-(Field{name:'Field'}) SET Field.comment='Commenttext'"
        .equals(runCypher(qb.getQuery(), qb.getKeys())));
  }

  @Test
  public void testCypherChangePrimFieldRel() {
    final QueryBuilder qb = new QueryBuilder();
    qb.changePrimFieldNameRelation("Nutzertabelle", "Typ", "alterTyp");
    Assert.assertTrue(
        ("MATCH(t:Table{name:'Nutzertabelle'})-[:XPK]->(:Index)<-[:REFERENCE]-(fk:ForeignKey)-[:FOREIGNKEY]->"
            + "(i:Index)-[r:INDEX{refname:'alterTyp'}]-(f:Field) SET r.refname='Typ'")
                .equals(runCypher(qb.getQuery(), qb.getKeys())));
  }

  private Object runCypher(final String query, final Object[] keys) {
    return QueryBuilder.queryToString(keys, query);
  }

  @Test
  public void testCountMethodes() {
    final QueryBuilder qb = new QueryBuilder();
    qb.countNodes("Table");
    Assert.assertTrue("MATCH(t:Table) return count (t) as number".equals(runCypher(qb.getQuery(), qb.getKeys())));
    qb.clear();
    qb.countRelation("DATA");
    Assert
        .assertTrue("MATCH(t)-[r:DATA]->(b) return count (r) as number".equals(runCypher(qb.getQuery(), qb.getKeys())));
  }

  private Table getTable() {
    final int j = 1;
    final Table t = new TableImpl("Table" + j);
    t.setCategory("1," + j);
    t.setComment("AComment");
    final Field f = new FieldImpl("Feld" + j, DomainId.STRING, 10, true, "", t);
    final Field f2 = new FieldImpl("FeldZwei" + j, DomainId.AMOUNT, 0, true, "", t);
    final Field f3 = new FieldImpl("FeldDrei" + j, DomainId.DATE, 0, false, "", t);
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
