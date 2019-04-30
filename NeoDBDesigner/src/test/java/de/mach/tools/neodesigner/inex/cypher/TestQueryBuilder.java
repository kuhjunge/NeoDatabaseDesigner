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


class TestQueryBuilder {
  private Object runCypher(final String query, final Object[] keys) {
    return QueryBuilder.queryToString(keys, query);
  }

  @Test
  void testCountMethodes() {
    final QueryBuilder qb = new QueryBuilder();
    qb.countNodes("Table");
    assertEquals("MATCH(t:Table) return count (t) as number", runCypher(qb.getQuery(), qb.getKeys()));
    qb.clear();
    qb.countRelation("DATA");
    assertEquals("MATCH(t)-[r:DATA]->(b) return count (r) as number", runCypher(qb.getQuery(), qb.getKeys()));
  }

  @Test
  void testCypher() {
    final QueryBuilder qb = new QueryBuilder();
    qb.getAllTables();
    assertEquals("MATCH(t:Table) RETURN t.name AS name, t.category AS category, t.comment AS comment ORDER BY name",
                 runCypher(qb.getQuery(), qb.getKeys()));
    qb.clear();
    assertEquals("", runCypher(qb.getQuery(), qb.getKeys()));
    qb.getForeignKeyNumber(3);
    assertEquals("MATCH(:Table)-[r:references]-(n) where r.name =~ 'R_([0-9]){3}' return r.name As name order by name DESC Limit 1",
                 runCypher(qb.getQuery(), qb.getKeys()));
    qb.clear();
    qb.getTable("Test");
    assertEquals("MATCH(t:Table{name:'Test'}) RETURN t.name AS name, t.category AS category, t.comment AS comment LIMIT 1",
                 runCypher(qb.getQuery(), qb.getKeys()));
    qb.clear();
    qb.getFieldsOfTable("Test");
    assertEquals("MATCH(t:Table{name:'Test'})-[r:has_column]-(c:Column) RETURN c.name as name,c.type as type,c.nullable as nullable,c.length as length,c.accuracy as accuracy",
                 runCypher(qb.getQuery(), qb.getKeys()));
    qb.clear();
    qb.getIndizesOfTable("Test");
    assertEquals("MATCH(t:Table{name:'Test'})-[r:identified_by|indexed_by]-(c) RETURN type(r) as type, r.order as order, r.name as indexname, c.name as name ORDER BY type, order",
                 runCypher(qb.getQuery(), qb.getKeys()));
    qb.clear();
    qb.getIndizesOfTable();
    assertEquals("MATCH(t:Table)-[r:identified_by|indexed_by]-(c:Column) RETURN t.name as tablename,type(r) as type, r.order as order,r.name as indexname,c.name as name ORDER BY tablename,type,indexname,order",
                 runCypher(qb.getQuery(), qb.getKeys()));
    qb.clear();
    qb.getForeignKeysOfTable("Test");
    assertEquals("MATCH(t:Table{name:'Test'})-[:has_column]->(c:Column)-[r:references]->(o:Table) RETURN DISTINCT t.name as tablename, r.order as order, r.name as indexname, c.name as name, o.name as origin order by tablename, order",
                 runCypher(qb.getQuery(), qb.getKeys()));
    qb.clear();
  }

  @Test
  void testCypherAdd() {
    final QueryBuilder qb = new QueryBuilder();
    qb.insertFkRelation("tableName", "refTableName", "foreignKeyName", "ColumnName", 1);
    assertEquals("MATCH(t:Table{name:'tableName'})-[:has_column]->(f{name:'ColumnName'}),(b:Table{name:'refTableName'}) CREATE (f)-[:references{name:'foreignKeyName', order:'1'}]->(b)",
                 runCypher(qb.getQuery(), qb.getKeys()));
    qb.clear();
    qb.insertPkRelation("tableName", "indexName", "columnName", 5, Type.XPK);
    assertEquals("MATCH(t:Table{name:'tableName'})-[:has_column]-(f:Column{name:'columnName'}) MERGE (t)-[x:identified_by{name:'indexName',order:'5'}]->(f)",
                 runCypher(qb.getQuery(), qb.getKeys()));
    qb.clear();
    final Table t = new TableImpl("tableName");
    t.addIndex(new IndexImpl("indexName", Index.Type.XAK, t));
    final Field f = new FieldImpl("fieldName", DomainId.STRING, 15, true, "", t);
    t.addField(f);
    qb.insertNewField(f);
    assertEquals("MATCH(t:Table{name:'tableName'}) CREATE(t_fieldName:Column{name:'fieldName',type:'VARCHAR2',accuracy:'0',length:'15',nullable:'false',order:'1',table:'tableName'})<-[:has_column]-(t)",
                 runCypher(qb.getQuery(), qb.getKeys()));
    qb.clear();
    qb.insertIndexRelation("tableName", "indexName", "fieldName", Type.INDEX, 0, true);
    assertEquals("MATCH(t:Table{name:'tableName'})-[:has_column]-(f:Column{name:'fieldName'}) CREATE(t)-[:indexed_by{name:'indexName',order:'0',unique:'true'}]->(f)",
                 runCypher(qb.getQuery(), qb.getKeys()));
  }

  @Test
  void testCypherDelete() {
    final QueryBuilder qb = new QueryBuilder();
    qb.deleteDatabase();
    assertEquals("MATCH(t) DETACH DELETE t", runCypher(qb.getQuery(), qb.getKeys()));
  }
}
