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

import org.junit.jupiter.api.Test;

import de.mach.tools.neodesigner.core.datamodel.Domain.DomainId;
import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Relations.Type;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.core.datamodel.impl.FieldImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.IndexImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.TableImpl;


public class TestQueryBuilder {
  private Object runCypher(final String query, final Object[] keys) {
    return QueryBuilder.queryToString(keys, query);
  }

  @Test
  public void testCountMethodes() {
    final QueryBuilder qb = new QueryBuilder();
    qb.countNodes("Table");
    assertTrue("MATCH(t:Table) return count (t) as number".equals(runCypher(qb.getQuery(), qb.getKeys())));
    qb.clear();
    qb.countRelation("DATA");
    assertTrue("MATCH(t)-[r:DATA]->(b) return count (r) as number".equals(runCypher(qb.getQuery(), qb.getKeys())));
  }

  @Test
  public void testCypher() {
    final QueryBuilder qb = new QueryBuilder();
    qb.getAllTables();
    assertTrue("MATCH(t:Table) RETURN t.name AS name, t.category AS category, t.comment AS comment ORDER BY name"
        .equals(runCypher(qb.getQuery(), qb.getKeys())));
    qb.clear();
    assertTrue("".equals(runCypher(qb.getQuery(), qb.getKeys())));
    qb.getForeignKeyNumber(3);
    assertTrue("MATCH(:Table)-[r:references]-(n) where r.name =~ 'R_([0-9]){3}' return r.name As name order by name DESC Limit 1"
        .equals(runCypher(qb.getQuery(), qb.getKeys())));
    qb.clear();
    qb.getTable("Test");
    assertTrue("MATCH(t:Table{name:'Test'}) RETURN t.name AS name, t.category AS category, t.comment AS comment LIMIT 1"
        .equals(runCypher(qb.getQuery(), qb.getKeys())));
    qb.clear();
    qb.getFieldsOfTable("Test");
    assertTrue("MATCH(t:Table{name:'Test'})-[r:has_column]-(c:Column) RETURN c.name as name,c.type as type,c.nullable as nullable,c.length as length,c.accuracy as accuracy"
        .equals(runCypher(qb.getQuery(), qb.getKeys())));
    qb.clear();
    qb.getIndizesOfTable("Test");
    assertTrue("MATCH(t:Table{name:'Test'})-[r:identified_by|indexed_by]-(c) RETURN type(r) as type, r.order as order, r.name as indexname, c.name as name ORDER BY type, order"
        .equals(runCypher(qb.getQuery(), qb.getKeys())));
    qb.clear();
    qb.getIndizesOfTable();
    assertTrue("MATCH(t:Table)-[r:identified_by|indexed_by]-(c:Column) RETURN t.name as tablename,type(r) as type, r.order as order,r.name as indexname,c.name as name ORDER BY tablename,type,indexname,order"
        .equals(runCypher(qb.getQuery(), qb.getKeys())));
    qb.clear();
    qb.getForeignKeysOfTable("Test");
    assertTrue("MATCH(t:Table{name:'Test'})-[:has_column]->(c:Column)-[r:references]->(o:Table) RETURN DISTINCT t.name as tablename, r.order as order, r.name as indexname, c.name as name, o.name as origin order by tablename, order"
        .equals(runCypher(qb.getQuery(), qb.getKeys())));
    qb.clear();
  }

  @Test
  public void testCypherAdd() {
    final QueryBuilder qb = new QueryBuilder();
    qb.insertFkRelation("tableName", "refTableName", "foreignKeyName", "ColumnName", 1);
    assertTrue("MATCH(t:Table{name:'tableName'})-[:has_column]->(f{name:'ColumnName'}),(b:Table{name:'refTableName'}) CREATE (f)-[:references{name:'foreignKeyName', order:'1'}]->(b)"
        .equals(runCypher(qb.getQuery(), qb.getKeys())));
    qb.clear();
    qb.insertPkRelation("tableName", "indexName", "columnName", 5, Type.XPK);
    assertTrue("MATCH(t:Table{name:'tableName'})-[:has_column]-(f:Column{name:'columnName'}) MERGE (t)-[x:identified_by{name:'indexName',order:'5'}]->(f)"
        .equals(runCypher(qb.getQuery(), qb.getKeys())));
    qb.clear();
    final Table t = new TableImpl("tableName");
    t.addIndex(new IndexImpl("indexName", Index.Type.XAK, t));
    final Field f = new FieldImpl("fieldName", DomainId.STRING, 15, true, "", t);
    qb.insertNewField(f);
    assertTrue("MATCH(t:Table{name:'tableName'}) CREATE(t_fieldName:Column{name:'fieldName',type:'VARCHAR2',accuracy:'0',length:'15',nullable:'false',table:'tableName'})<-[:has_column]-(t)"
        .equals(runCypher(qb.getQuery(), qb.getKeys())));
    qb.clear();
    qb.insertIndexRelation("tableName", "indexName", "fieldName", Type.INDEX, 0, true);
    assertTrue("MATCH(t:Table{name:'tableName'})-[:has_column]-(f:Column{name:'fieldName'}) CREATE(t)-[:indexed_by{name:'indexName',order:'0',unique:'true'}]->(f)"
        .equals(runCypher(qb.getQuery(), qb.getKeys())));
  }

  @Test
  public void testCypherChange() {
    final QueryBuilder qb = new QueryBuilder();
    qb.changeTableName("Test", "Test1", "XPKTest1");
    assertTrue("MATCH(t:Table{name:'Test'})-[i:identified_by]->(f) SET t.name='Test1' SET i.name='XPKTest1'"
        .equals(runCypher(qb.getQuery(), qb.getKeys())));
    qb.clear();
    qb.changeTableCategory("Table", "Kategorie");
    assertTrue("MATCH(t:Table{name:'Table'}) SET t.category='Kategorie'"
        .equals(runCypher(qb.getQuery(), qb.getKeys())));
    qb.clear();
    qb.changeColumnName("NodeName", "TableName", "NewName");
    assertTrue("MATCH(t:Table{name:'NodeName'})-[:has_column]->(f{name:'TableName'}) SET f.name='NewName'"
        .equals(runCypher(qb.getQuery(), qb.getKeys())));
    qb.clear();
    qb.changeFieldNullable("nodeName", "tableName", true);
    assertTrue("MATCH(tableName:Table{name:'tableName'})-[:has_column]->(nodeName{name:'nodeName'}) SET nodeName.nullable='true'"
        .equals(runCypher(qb.getQuery(), qb.getKeys())));
    qb.clear();
    qb.changeFieldTypeLengthAccuracy("tableName", "nodeName", "VARCHAR", 20, 0);
    assertTrue("MATCH(tableName:Table{name:'tableName'})-[:has_column]->(nodeName{name:'nodeName'}) SET t.type='VARCHAR' SET nodeName.length='20' SET nodeName.accuracy='0'"
        .equals(runCypher(qb.getQuery(), qb.getKeys())));
    qb.clear();
    qb.changeIndexUnique("nodeName", "tableName", true);
    assertTrue("MATCH(t:Table{name:'nodeName'})-[i{name:'tableName'}]->() SET i.unique='true'"
        .equals(runCypher(qb.getQuery(), qb.getKeys())));
  }

  @Test
  public void testCypherComment() {
    final QueryBuilder qb = new QueryBuilder();
    qb.changeComment("Field", "Table", "Commenttext");
    assertTrue("MATCH(t:Table{name:'Table'})-[:has_column]->(Field{name:'Field'}) SET Field.comment='Commenttext'"
        .equals(runCypher(qb.getQuery(), qb.getKeys())));
  }

  @Test
  public void testCypherCommentTable() {
    final QueryBuilder qb = new QueryBuilder();
    qb.changeComment("Table", "Commenttext");
    assertTrue("MATCH(t:Table{name:'Table'}) SET t.comment='Commenttext'"
        .equals(runCypher(qb.getQuery(), qb.getKeys())));
  }

  @Test
  public void testCypherDelete() {
    final QueryBuilder qb = new QueryBuilder();
    qb.deleteIndexToFieldRelation("tableName", "indexName");
    assertTrue("MATCH(t:Table{name:'tableName'})-[i{name:'indexName'}]->() DELETE i"
        .equals(runCypher(qb.getQuery(), qb.getKeys())));
    qb.clear();
    qb.deleteSpecificIndexToFieldRelation("tableName", "indexName", Type.XPK);
    assertTrue("MATCH(t:Table{name:'tableName'})-[i:identified_by]-(f{name:'indexName'}) DELETE i"
        .equals(runCypher(qb.getQuery(), qb.getKeys())));
    qb.clear();
    qb.deleteField("tableName", "columnName");
    assertTrue("MATCH(t:Table{name:'tableName'})-[:has_column]->(f{name:'columnName'}) DETACH DELETE f"
        .equals(runCypher(qb.getQuery(), qb.getKeys())));
    qb.clear();
    qb.deleteTable("name");
    assertTrue("MATCH(t:Table{name:'name'})OPTIONAL MATCH (t)-[:has_column]->(f) DETACH DELETE f,t"
        .equals(runCypher(qb.getQuery(), qb.getKeys())));
    qb.clear();
    qb.deleteDatabase();
    assertTrue("MATCH(t) DETACH DELETE t".equals(runCypher(qb.getQuery(), qb.getKeys())));
  }

  @Test
  public void testCypherGetIndistinctName() {
    final QueryBuilder qb = new QueryBuilder();
    qb.getFieldNameCase("Test");
    assertTrue("MATCH(c:Column) WHERE c.name =~ '(?i)Test' return distinct c.name as name"
        .equals(runCypher(qb.getQuery(), qb.getKeys())));
  }
}
