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

package de.mach.tools.neodesigner.database;

/**
 * Strings werden hier hin ausgelagert.
 *
 * @author Chris Deter
 *
 */
public class Strings {
  private Strings() {
  }

  public static final String COMMA = ", ";
  public static final String COLON = ":";
  public static final String QUOUTE = "'";
  public static final String SPACE = " ";
  public static final String UNDERSCORE = "_";
  public static final String BRACKETSOPEN = "\\{";
  public static final String BRACKETSCLOSED = "\\}";
  public static final String EMPTYSTRING = "";
  public static final String NODE_TABLE = "Table";
  public static final String NODE_INDEX = "Index";
  public static final String NODE_FIELD = "Field";
  public static final String NODE_FK = "ForeignKey";
  public static final String INDEXTYPE_XPK = "XPK";
  public static final String R = "R";
  public static final String TBL_ID = "t";
  public static final String TBL2_ID = "b";
  public static final String INDEX_ID = "i";
  public static final String FIELD_ID = "f";
  public static final String PRIM_REF_ID = "p";
  public static final String FOREIGNKEY_ID = "c";
  public static final String VALUE_ID = "v";
  public static final String REL_XPK_ID = "r_xpk";
  public static final String REL_FK_ID = "r_fk";
  public static final String DEL_ID = "del";
  public static final String REL_ID = "r";
  public static final String IDENT_NAME = "name";
  public static final String IDENT_ALTNAME = "altName";
  public static final String IDENT_CATEGORY = "category";
  public static final String IDENT_ISREQ = "isRequired";
  public static final String IDENT_TYPE = "type";
  public static final String IDENT_UNIQUE = "unique";
  public static final String IDENT_NUMBER = "number";
  public static final String IDENT_REFTABLENAME = "refTable";
  public static final String IDENT_REFNAME = "refname";
  public static final String IDENT_XIF = "xif";
  public static final String IDENT_COMMENT = "comment";
  public static final String PUTVAL_TABLE = "t_";
  public static final String PUTVAL_FINDNODE = "_v";
  public static final String PUTVAL_INDEXNAME = "idn";
  public static final String PUTVAL_FKN = "fkn_";
  public static final String PUTVAL_TABLENAME = "_tn";
  public static final String PUTVAL_TABLECATEGORY = "_tk";
  public static final String PUTVAL_TABLECOMMENT = "_tc";
  public static final String PUTVAL_NAME = "_n";
  public static final String PUTVAL_TYPE = "_t";
  public static final String PUTVAL_REQ = "_r";
  public static final String PUTVAL_UNIQUE = "_u";
  public static final String PUTVAL_R = "r";
  public static final String PUTVAL_ORDER = "_o";
  public static final String PUTVAL_ALTNAME = "altn";

  // Match
  public static final String CYPHER_MATCH = " MATCH";
  public static final String CYPHER_CREATE = "CREATE";
  public static final Object CYPHER_MERGE = "MERGE";
  public static final String CYPHER_GET_FK_ID = "MATCH(b:" + Strings.NODE_TABLE + ")-[:FOREIGNKEY]->(n:"
      + Strings.NODE_FK + ") where n.name =~ 'R_([0-9]){%s}' return n.name As name order by name DESC Limit 1";
  public static final String CYPHER_GET_PK = " MATCH(%s)-[:%s]->(pk:%s)";
  public static final String CYPHER_GET = " MATCH(%s)";
  public static final String CYPHER_GET_REFERENCES = "-[:XPK]->(:Index)<-[:REFERENCE]-(fk:ForeignKey)-[:FOREIGNKEY]->"
      + "(i:Index)";
  public static final String CYPHER_GET_REFERENCES_TABLE_RETURN = "<-[inRel:INDEX]-(origin:Table) "
      + "return fk.name AS name, i.name AS xif, origin.name AS refTable";
  public static final String CYPHER_FIND_TABLE = "%s(%s:%s{name:{t_%s}})";
  public static final String CYPHER_GET_INCASESENSITIVEFIELDNAME = "MATCH(t:Field) WHERE t.name =~ '(?i)%s'"
      + " return distinct t.name as name";
  // Create
  public static final String CYPHER_CREATEINDEX = "CREATE CONSTRAINT ON (t:%s) ASSERT t.name IS UNIQUE";
  public static final String CYPHER_CREATE_TABLETOREFERENCE = " CREATE(%s)-[:%s{key:{fkn_%s}}]->(%s) ";
  public static final String CYPHER_INSERT_NEWTABLE = " CREATE(%s:%s{name:{%s_tn},category:{%s_tk}%s})";
  public static final String CYPHER_INSERT_NEWFIELD = "%s(%s:%s{name:{%s_n},type:{%s_t},isRequired:{%s_r}%s})<-[:%s]-(%s)";
  // Merge
  public static final String CYPHER_CREATE_NODE = " CREATE(%s)-[:%s]->(%s)";
  public static final String CYPHER_MERGELONG = " CREATE(%s)-[:%s{%sorder:{%s}}]->(%s)";
  public static final String CYPHER_MERGE_PRIMKEYREL = " CREATE(%s)<-[:%s{order:{%s}}]-(pk) ";
  public static final String CYPHER_INSERT_INDEXTOFIELD = " CREATE(%s)-[:%s{order:{r%s_o}}]->(%s)";
  public static final String CYPHER_INSERT_INDEXTOFIELDFORFK = " CREATE(%s)-[:%s{refname:{%s},order"
      + ":{r%s_o}}]->(%s)";
  public static final String CYPHER_INSERT_INDEXTOFIELDFORFK_SHORT = " %s(%s)-[:%s{refname:{%s},order:{%s}}]->";
  public static final String CYPHER_INSERT_NEWINDEX = "%s(%s:%s{name:{%s_n},type:{%s_t}";
  public static final String CYPHER_INSERT_INDEXREL = "})<-[:%s]-(%s)";
  public static final String CYPHER_INSERT_PREINDEXREL = " CREATE(%s)-[:%s]->";
  public static final String CYPHER_INSERT_CLOSEBRACKED = "})";
  public static final String CYPHER_REFNAME = "refname:{%s},";
  // Return
  public static final String CYPHER_COUNT = " return count (%s) as number";
  public static final String CYPHER_RETURN_FK = " RETURN DISTINCT %s.name AS name,%s.name as refTable,%s.name as xif";
  public static final String CYPHER_RETURN_TABLE = " RETURN %s.name AS name, %s.category AS category, %s.comment AS comment";
  public static final String CYPHER_RETURN_FIELD = " RETURN %s.name AS name, %s.isRequired AS isRequired, %s.comment AS comment, "
      + "%s.type AS type";
  public static final String CYPHER_RETURN_INDEXFIELD = ",%s.refname AS altName, %s.order AS order";
  public static final String CYPHER_RETURN_INDEX = " RETURN DISTINCT %s.name AS name %s";
  // Other

  public static final String CYPHER_DELETE = " DETACH DELETE %s";
  public static final String CYPHER_DELETE_PRIMKEYREL = "<-[r:%s]-(:%s) DELETE r ";
  public static final String CYPHER_DELETE_FKTABLEREL = "-[r:%s {key:{%s}}]-(%s:%s) DELETE r";
  public static final String CYPHER_DELETE_TABLEREL = "-[ :%s | :%s | :%s | :%s | :%s ]-(%s)";
  public static final String CYPHER_ORDER_BYNAME = " ORDER BY name";
  public static final String CYPHER_ORDER_BYORDER = " ORDER BY toInt(order)";
  public static final String CYPHER_SET = " SET %s.%s={%s}";
  public static final String CYPHER_FIND_NODEAFTERTABLE = "-[%s]-(%s{name:{%s_v}})";
  public static final String CYPHER_ANYREL = "--";
  public static final String CYPHER_FINDFIELDOFINDEX = "<-[r:%s]-(%s{name:{idn}})";
  public static final String CYPHER_FIND_INDEX = "-[:%s]->(%s)";
  // - [rel:type]->(%n)
  public static final String CYPHER_COUNTREL = "-[%s%s]->(%s)";
  public static final String CYPHER_FIND_REL = "-[:%s]->";
  public static final String CYPHER_FIND_REL2 = "<-[:%s]-";
  public static final String CYPHER_FIND_REL3 = "-[:%s]->(%s:%s)";
  public static final String CYPHER_FIND_REL4 = "<-[:%s]-(%s:%s)";
  // -[rel:type{prop:"abc"}]-(n:type)
  public static final String CYPHER_GET_REFERENCES_FIELD = "-[%s:%s{%s:{%s}}]-(%s:%s)";
  public static final String CYPHER_RETURN_UNIQUEINDEX = ", %s.unique AS unique";
  public static final String CYPHER_INSERT_UNIQUE = ",unique:{%s_u}";
  public static final String CYPHER_DELETE_REL = "-[r%s]->(:%s) delete r";
  public static final String CYPHER_NODE = "(%s:%s)";
  public static final String CYPHER_DROPCONSTRAINT = "DROP CONSTRAINT ON (t:%s) ASSERT t.name IS UNIQUE";
  public static final String LIMIT_TO_ONE = " LIMIT 1";
  public static final String NOCONSTRAINTTODELETE = "No Constraint to delete";
  public static final String RELTEXT = "Relation ";
  public static final String COMMENT = ",comment:{%s%s}";
  public static final String SOMETHINGISWRONG = "Failed to process Data correctly - something is wrong! (%s)";
  public static final String ERR_PRIMARYKEY = "could not set PK correctly";
  public static final String ERR_FIELD = "could not process Field";
}
