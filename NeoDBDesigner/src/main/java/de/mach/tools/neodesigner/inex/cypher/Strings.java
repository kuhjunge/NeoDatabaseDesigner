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


/** Strings werden hier hin ausgelagert.
 *
 * @author Chris Deter */
public class Strings {
  public static final String COMMA = ",";

  public static final String COLON = ":";
  public static final String QUOUTE = "'";
  public static final String SPACE = " ";
  public static final String UNDERSCORE = "_";
  public static final String BRACKETSOPEN = "\\{";
  public static final String BRACKETSCLOSED = "\\}";
  public static final String EMPTYSTRING = "";
  public static final String NODE_TABLE = "Table";
  public static final String NODE_COLUMN = "Column";
  public static final String TBL_ID = "t";
  public static final String TBL2_ID = "b";
  public static final String INDEX_ID = "i";
  public static final String COLUMN_ID = "f";
  public static final String VALUE_ID = "v";
  public static final String REL_ID = "r";
  public static final String IDENT_ORIGIN = "origin";
  public static final String IDENT_NAME = "name";
  public static final String IDENT_CATEGORY = "category";
  public static final String IDENT_ISREQ = "isRequired";
  public static final String IDENT_TYPE = "type";
  public static final String IDENT_UNIQUE = "unique";
  public static final String IDENT_NUMBER = "number";
  public static final String IDENT_COMMENT = "comment";
  public static final String IDENT_NULLABLE = "nullable";
  public static final String IDENT_LEN = "length";
  public static final String IDENT_ACCURACY = "accuracy";
  public static final String IDENT_TABLENAME = "tablename";
  public static final String IDENT_INDEXNAME = "indexname";
  public static final String IDENT_ORDER = "order";
  public static final String PUTVAL_TABLE = "t_";
  public static final String PUTVAL_FINDNODE = "_v";
  public static final String PUTVAL_INDEXNAME = "idn";
  public static final String PUTVAL_TABLENAME = "_tn";
  public static final String PUTVAL_TABLECATEGORY = "_tk";
  public static final String PUTVAL_TABLECOMMENT = "_tc";
  public static final String PUTVAL_NAME = "_n";
  public static final String PUTVAL_REQ = "_r";
  public static final String PUTVAL_ORDER = "_o";
  // Match
  public static final String CYPHER_MATCH = " MATCH";

  public static final String CYPHER_CREATE = "CREATE";
  public static final String CYPHER_GET = " MATCH(%s)";
  public static final String CYPHER_FIND_TABLE = "%s(%s:%s{name:{t_%s}})";
  // Create
  public static final String CYPHER_CREATEINDEX = "CREATE CONSTRAINT ON (t:%s) ASSERT t.name IS UNIQUE";
  public static final String CYPHER_INSERT_NEWTABLE = " CREATE(%s:%s{name:{%s_tn},category:{%s_tk}%s})";
  // Return
  public static final String CYPHER_COUNT = " return count (%s) as number";
  public static final String CYPHER_RETURN_TABLE = " RETURN %s.name AS name, %s.category AS category, %s.comment AS comment";
  // Other
  public static final String CYPHER_DELETE = " DETACH DELETE %s";
  public static final String CYPHER_ORDER_BYNAME = " ORDER BY name";
  public static final String CYPHER_FIND_NODEAFTERTABLE = "-[%s]->(%s{name:{%s_v}})";
  public static final String CYPHER_COUNTREL = "-[%s%s]->(%s)";
  public static final String CYPHER_DROPCONSTRAINT = "DROP CONSTRAINT ON (t:%s) ASSERT t.name IS UNIQUE";
  public static final String LIMIT_TO_ONE = " LIMIT 1";
  public static final String COMMENT = ",comment:{%s%s}";
  // Messages
  public static final String NOCONSTRAINTTODELETE = "No Constraint to delete";
  public static final String RELTEXT = "Relation ";
  public static final String VALUE_ID2 = "v2";
  public static final String VALUE_ID3 = "v3";
  public static final String CYPHER_GETFIELDSOFTABLE = "-[r:has_column]-(c:Column) RETURN t.name as tablename,c.name as name,c.type as type,c.nullable as nullable,c.length as length,c.accuracy as accuracy ORDER BY tablename";
  public static final String CYPHER_GETSPECIFICFIELDSOFTABLE = "-[r:has_column]-(c:Column) RETURN c.name as name,c.type as type,c.nullable as nullable,c.length as length,c.accuracy as accuracy";
  public static final String CYPHER_GETFOREIGNKEYNUMBER = "MATCH(:Table)-[r:references]-(n) where r.name =~ 'R_([0-9]){%s}' return r.name As name order by name DESC Limit 1";
  public static final String CYPHER_GETFOREIGNKEYSOFTABLE = "MATCH(t:Table)-[:indexed_by]->(c:Column)-[r:references]->(o:Table)-[p:identified_by{order:r.order}]->(pk:Column) RETURN DISTINCT t.name as tablename,r.order as order,r.name as indexname,c.name as name,o.name as origin,pk.name as altname ORDER BY tablename,indexname,order";
  public static final String CYPHER_MERGEREFERNCES = " CREATE (%s)-[:references{name:{%s}, order:{%s}}]->(%s)";
  public static final String CYPHER_FINDINDEXREL = "-[:has_column]-(f:Column{name:{%s}}) ";
  public static final String CYPHER_INSERTINDEXREL = "CREATE(t)-[:%s{name:{%s},order:{%s},unique:{%s}}]->(%s)";
  public static final String CYPHER_INSERTNEWFIELD = "%s(%s:%s{name:{%s},type:{%s},accuracy:{%s},length:{%s},nullable:{%s},order:{%s},table:{%s}%s})<-[:%s]-(%s)";
  public static final String CYPHER_INSERTPKREL = "MATCH(t:Table{name:{%s}})-[:has_column]-(f:Column{name:{%s}}) MERGE (t)-[x:%s{name:{%s},order:{%s}}]->(f)";
  public static final String CYPHER_RETURN_NAME = " RETURN %s.name AS name";
  public static final String CYPHER_GETFK = "MATCH(t:Table{name:{tn}})-[:has_column]->(c:Column)-[r:references]->(o:Table) RETURN DISTINCT t.name as tablename, r.order as order, r.name as indexname, c.name as name, o.name as origin order by tablename, order";
  public static final String CYPHER_GETINDEX = "MATCH(t:Table)-[r:identified_by|indexed_by]-(c:Column) RETURN t.name as tablename,type(r) as type, r.order as order,r.name as indexname,c.name as name ORDER BY tablename,type,indexname,order";
  public static final String CYPHER_GETSPECIFICINDEX = "-[r:identified_by|indexed_by]-(c) RETURN type(r) as type, r.order as order, r.name as indexname, c.name as name ORDER BY type, order";

  private Strings() {}
}
