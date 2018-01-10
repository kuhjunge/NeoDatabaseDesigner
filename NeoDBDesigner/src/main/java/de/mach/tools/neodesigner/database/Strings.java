package de.mach.tools.neodesigner.database;

/**
 * Strings werden hier hin ausgelagert.
 * 
 * @author Chris Deter
 *
 */
class Strings {
  private Strings() {
  }

  static final String COMMA = ", ";
  static final String COLON = ":";
  static final String QUOUTE = "'";
  static final String UNDERSCORE = "_";
  static final String BRACKETSOPEN = "\\{";
  static final String BRACKETSCLOSED = "\\}";
  static final String NULL = "";

  static final String NODE_TABLE = "Table";
  static final String NODE_INDEX = "Index";
  static final String NODE_FIELD = "Field";

  static final String INDEXTYPE_XPK = "XPK";

  static final String TBL_ID = "t";
  static final String TBL2_ID = "b";
  static final String INDEX_ID = "i";
  static final String FIELD_ID = "f";
  static final String PRIM_REF_ID = "p";
  static final String FOREIGNKEY_ID = "c";
  static final String REFTABLE_ID = "d";
  static final String VALUE_ID = "value";
  static final String REL_XPK_ID = "r_xpk";
  static final String REL_FK_ID = "r_fk";
  static final String DEL_ID = "del";
  static final String IDENT_NAME = "name";
  static final String IDENT_ALTNAME = "altName";
  static final String IDENT_CATEGORY = "category";
  static final String IDENT_ISREQ = "isRequired";
  static final String IDENT_TYPE = "type";
  static final String IDENT_UNIQUE = "unique";
  static final String PUTVAL_TABLE = "t_";
  static final String PUTVAL_FINDNODE = "_v";
  static final String PUTVAL_INDEXNAME = "idn";
  static final String PUTVAL_FKN = "fkn_";
  static final String PUTVAL_TABLENAME = "_tn";
  static final String PUTVAL_TABLECATEGORY = "_tk";
  static final String PUTVAL_NAME = "_n";
  static final String PUTVAL_TYPE = "_t";
  static final String PUTVAL_REQ = "_r";
  static final String PUTVAL_UNIQUE = "_u";
  static final String PUTVAL_R = "r";
  static final String PUTVAL_ORDER = "_o";
  public static final String PUTVAL_ALTNAME = "altn";
  // Match
  static final String CYPHER_GET_FK_ID = "MATCH(b:Table)-[:FOREIGNKEY]->(n:Index) "
      + "where n.name =~ 'R_([0-9]){%s}' return n.name As name order by name DESC Limit 1";
  static final String CYPHER_GET_PK = " MATCH(%s)-[:%s]->(pk:%s)";
  static final String CYPHER_FIND_INDEX2 = " MATCH(%s_%s:%s{name:{%s_n}})--(%s)";
  static final String CYPHER_GET = " MATCH(%s)";
  static final String CYPHER_FIND_TABLE = " MATCH(%s:%s{name:{t_%s}})";
  static final String CYPHER_GET_FKN = " MATCH(%s)-[:%s]->(%s {name:{fkn_%s}})";
  static final String CYPHER_GET_XPK = " MATCH(%s)-[:%s]->(xpk)";

  // Create
  static final String CYPHER_CREATEINDEX = "CREATE INDEX ON :%s(name);";
  static final String CYPHER_CREATE_TABLETOREFERENCE = " CREATE(%s)-[:%s{key:{fkn_%s}}]->(%s) ";
  static final String CYPHER_INSERT_NEWTABLE = " CREATE(%s:%s{name:{%s_tn},category:{%s_tk}})";
  static final String CYPHER_INSERT_NEWFIELD = " CREATE(%s_%s:%s{name:{%s_n},type:{%s_t}"
      + ",isRequired:{%s_r}}) CREATE(%s)-[:%s]->(%s_%s)";
  static final String CYPHER_FKTOXIF = " CREATE(%s_%s)-[:%s]->(%s_%s)";
  // Merge
  static final String CYPHER_MERGE = " MERGE(%s)-[:%s]->(%s)";
  public static final String CYPHER_MERGELONG = " MERGE(%s)-[:%s{%sorder:{%s}}]->(%s)";
  public static final String CYPHER_REFNAME = "refname:{%s},";
  static final String CYPHER_MERGE_PRIMKEYREL = " MERGE(%s)<-[:%s{order:{%s}}]-(pk) ";
  static final String CYPHER_CREATE_FK_TO_REF_XPK = " MERGE(%s)-[:%s]->(xpk)";
  static final String CYPHER_INSERT_INDEXTOFIELD = " MERGE(%s_%s)-[:%s{order:{r%s_o}}]->(%s_%s)";
  static final String CYPHER_INSERT_INDEXTOFIELDFORFK = " MERGE(%s_%s)-[:%s{refname:{r%s_%s},order"
      + ":{r%s_o}}]->(%s_%s)";
  static final String CYPHER_INSERT_NEWINDEX = " MERGE(%s_%s:%s{name:{%s_n},type:{%s_t}";
  static final String CYPHER_INSERT_INDEXREL = "}) MERGE(%s)-[:%s]->(%s_%s)";
  // Return
  static final String CYPHER_RETURN_FK = " RETURN DISTINCT %s.name AS name,%s.name as refTable," + "%s.name as xif";
  static final String CYPHER_RETURN_TABLE = " RETURN %s.name AS name, %s.category AS category";
  static final String CYPHER_RETURN_FIELD = " RETURN %s.name AS name, %s.isRequired AS isRequired, "
      + "%s.type AS type";
  static final String CYPHER_RETURN_INDEXFIELD = ",r.refname AS altName, r.order AS order";
  static final String CYPHER_RETURN_INDEX = " RETURN DISTINCT %s.name AS name %s";
  // Other
  static final String CYPHER_DELETE = " DETACH DELETE %s";
  static final String CYPHER_DELETE_PRIMKEYREL = "<-[r:%s]-(primKey:%s) DELETE r ";
  static final String CYPHER_DELETE_FKTABLEREL = "-[r:%s {key:{%s}}]-(%s:%s) DELETE r";
  static final String CYPHER_ORDER_BYNAME = " ORDER BY name";
  static final String CYPHER_ORDER_BYORDER = " ORDER BY toInt(order)";
  static final String CYPHER_SET = " SET %s.%s={%s}";
  static final String CYPHER_DELETE_TABLEREL = "-[ :%s | :%s | :%s | :%s | :%s ]-(%s)";
  static final String CYPHER_FIND_NODEAFTERTABLE = "--(%s{name:{%s_v}})";
  static final String CYPHER_ANYREL = "--";
  static final String CYPHER_FINDFIELDOFINDEX = "<-[r:%s]-(%s{name:{idn}})";
  static final String CYPHER_FIND_INDEX = "-[:%s]->(%s)";
  static final String CYPHER_FIND_REL = "-[:%s]->";
  static final String CYPHER_FIND_REL2 = "<-[:%s]-";
  static final String CYPHER_FIND_REL3 = "-[:%s]->(%s:%s)";
  static final String CYPHER_FIND_REL4 = "<-[:%s]-(%s:%s)";

  static final String CYPHER_RETURN_UNIQUEINDEX = ", %s.unique AS unique";

  static final String CYPHER_INSERT_UNIQUE = ",unique:{%s_u}";

  static final String CYPHER_DELETE_REL = "-[r:%s]->(:%s) delete r";
  static final String CYPHER_NODE = "(%s:%s)";
}
