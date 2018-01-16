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

package de.mach.tools.neodesigner.core;

/**
 * Klasse in der alle Strings ausgelagert werden.
 *
 * @author Chris Deter
 *
 */
public final class Strings {
  private Strings() {
  }

  public static final String SOFTWARENAME = "NeoDatabaseDesigner";
  static final String CONFIGFILEENDING = ".conf";
  static final String NAME_REGEX = "([A-Za-z0-9_])+";
  static final String[] RESERVED_SQL_WORDS = { " ACCESS ", " ACTION ", " ACTIVATION ", " ACTIVE ", " ADD ", " ADMIN ",
      " AFTER ", " ALL ", " ALLOCATE ", " ALPHANUMERIC ", " ALTER ", " ANALYZE ", " AND ", " ANY ", " ARCHIVE ",
      " ARCHIVELOG ", " ARITH_OVERFLOW ", " AS ", " ASC ", " ASCENDING ", " AT ", " AUDIT ", " AUTHORIZATION ",
      " AUTO ", " AUTODDL ", " AUTOINC ", " AUTOINCREMENT ", " AVG ", " BACKUP ", " BASE_NAME ", " BASED ",
      " BASENAME ", " BECOME ", " BEFORE ", " BEGIN ", " BETWEEN ", " BIGINT ", " BINARY ", " BIT ", " BLOB ",
      " BLOBEDI ", " BLOCK ", " BODY ", " BOOLEAN ", " BOTH ", " BOTTOM ", " BREAK ", " BROWSE ", " BUFFER ", " BULK ",
      " BY ", " BYBYTE ", " BYTES ", " CACHE ", " CALL ", " CANCEL ", " CASCADE ", " CASE ", " CAST ", " CHANGE ",
      " CHAR ", " CHAR_CONVERT ", " CHAR_LENGTH ", " CHARACTER ", " CHARACTER_LENGTH ", " CHECK ", " CHECK_POINT_LEN ",
      " CHECK_POINT_LENGTH ", " CHECKPOINT ", " CLOSE ", " CLUSTER ", " CLUSTERED ", " COALESCE ", " COBOL ",
      " COLLATE ", " COLLATION ", " COLUMN ", " COMMENT ", " COMMIT ", " COMMITTED ", " COMPILE ", " COMPILETIME ",
      " COMPRESS ", " COMPUTE ", " COMPUTED ", " CONDITIONAL ", " CONFIRM ", " CONNECT ", " CONSTRAINT ",
      " CONSTRAINTS ", " CONSUMERS ", " CONTAINING ", " CONTAINS ", " CONTAINSTABLE ", " CONTENTS ", " CONTINUE ",
      " CONTROLFILE ", " CONTROLROW ", " CONVERT ", " COUNT ", " COUNTER ", " CREATE ", " CROSS ", " CSTRING ",
      " CURRENCY ", " CURRENT ", " CURRENT_DATE ", " CURRENT_TIME ", " CURRENT_TIMESTAMP ", " CURRENT_USER ",
      " CURSOR ", " CYCLE ", " DATABASE ", " DATAFILE ", " DATE ", " DATETIME ", " DAY ", " DB_KEY ", " DBA ", " DBCC ",
      " DBSPACE ", " DEALLOCATE ", " DEBUG ", " DEC ", " DECIMAL ", " DECLARE ", " DEFAULT ", " DELETE ", " DENY ",
      " DESC ", " DESCENDING ", " DESCRIBE ", " DESCRIPTOR ", " DISABLE ", " DISALLOW ", " DISCONNECT ", " DISK ",
      " DISMOUNT ", " DISPLAY ", " DISTINCT ", " DISTINCTROW ", " DISTRIBUTED ", " DO ", " DOMAIN ", " DOUBLE ",
      " DROP ", " DUMMY ", " DUMP ", " DYNAMIC ", " EACH ", " ECHO ", " EDIT ", " ELSE ", " ELSEIF ", " ENABLE ",
      " ENCRYPTED ", " END ", " ENDIF ", " ENDTRAN ", " ENTRY_POINT ", " EQV ", " ERRLVL ", " ERRORDATA ",
      " ERROREXIT ", " ESCAPE ", " EVENT ", " EVENTS ", " EXCEPT ", " EXCEPTION ", " EXCEPTIONS ", " EXCLUSIVE ",
      " EXEC ", " EXECUTE ", " EXISTING ", " EXISTS ", " EXIT ", " EXPLAIN ", " EXTENT ", " EXTERN ", " EXTERNAL ",
      " EXTERNALLY ", " EXTERNLOGIN ", " EXTRACT ", " FETCH ", " FILE ", " FILLFACTOR ", " FILTER ", " FIRST ",
      " FLOAT ", " FLOAT4 ", " FLOAT8 ", " FLOPPY ", " FLUSH ", " FOR ", " FORCE ", " FOREIGN ", " FORTRAN ",
      " FORWARD ", " FOUND ", " FREE_IT ", " FREELIST ", " FREELISTS ", " FREETEXT ", " FREETEXTTABLE ", " FROM ",
      " FULL ", " FUNCTION ", " GDSCODE ", " GEN_ID ", " GENERAL ", " GENERATOR ", " GLOBAL ", " GO ", " GOTO ",
      " GRANT ", " GROUP ", " GROUP_COMMIT_ ", " GROUP_COMMIT_WAIT ", " GROUP_COMMIT_WAIT_TIME ", " GROUPS ", " GUID ",
      " HAVING ", " HELP ", " HOLDLOCK ", " HOUR ", " IDENTIFIED ", " IDENTITY ", " IDENTITY_INSERT ",
      " IDENTITY_START ", " IDENTITYCOL ", " IEEEDOUBLE ", " IEEESINGLE ", " IF ", " IGNORE ", " IMMEDIATE ", " IMP ",
      " IN ", " INACTIVE ", " INCLUDING ", " INCREMENT ", " INDEX ", " INDICATOR ", " INIT ", " INITIAL ", " INITRANS ",
      " INNER ", " INOUT ", " INPUT ", " INPUT_TYPE ", " INSENSITIVE ", " INSERT ", " INSTALL ", " INSTANCE ",
      " INSTEAD ", " INT ", " INTEGER ", " INTEGER1 ", " INTEGER2 ", " INTEGER4 ", " INTEGRATED ", " INTERSECT ",
      " INTO ", " IQ ", " IS ", " ISOLATION ", " ISQL ", " JOIN ", " KEY ", " KILL ", " LANGUAGE ", " LAYER ",
      " LC_MESSAGES ", " LC_TYPE ", " LEADING ", " LEFT ", " LENGTH ", " LEV ", " LEVEL ", " LIKE ", " LINENO ",
      " LINK ", " LISTS ", " LOAD ", " LOCK ", " LOG_BUF_SIZE ", " LOG_BUFFER_SIZE ", " LOGFILE ", " LOGICAL ",
      " LOGICAL1 ", " LOGIN ", " LONG ", " LONGBINARY ", " LONGTEXT ", " LOWER ", " MANAGE ", " MANUAL ", " MATCH ",
      " MAX ", " MAX_ROWS_PER_PAGE ", " MAX_SEGMENT ", " MAXDATAFILES ", " MAXEXTENTS ", " MAXIMUM ",
      " MAXIMUM_SEGMENT ", " MAXINISTANCES ", " MAXLOGFILES ", " MAXLOGHISTORY ", " MAXLOGMEMBERS ", " MAXTRANS ",
      " MAXVALUE ", " MEMBERSHIP ", " MEMO ", " MENT ", " MERGE ", " MESSAGE ", " MIN ", " MINEXTENTS ", " MINIMUM ",
      " MINUS ", " MINUTE ", " MINVALUE ", " MIRROR ", " MIRROREXIT ", " MOD ", " MODE ", " MODIFY ", " MODULE ",
      " MODULE_NAME ", " MONEY ", " MONTH ", " MOUNT ", " NAMES ", " NATIONAL ", " NATURAL ", " NCHAR ", " NEW ",
      " NEXT ", " NO ", " NOARCHIVELOG ", " NOAUDIT ", " NOAUTO ", " NOCACHE ", " NOCHECK ", " NOCOMPRESS ",
      " NOCYCLE ", " NOHOLDLOCK ", " NOMAXVALUE ", " NOMINVALUE ", " NONCLUSTERED ", " NONE ", " NOORDER ",
      " NORESETLOGS ", " NORMAL ", " NOSORT ", " NOT ", " NOTIFY ", " NOWAIT ", " NULL ", " NULLIF ",
      " NUM_LOG_BUFFERS ", " NUM_LOG_BUFS ", " NUMBER ", " NUMERIC ", " NUMERIC_TRANSACTION ", " OCTET_LENGTH ", " OF ",
      " OFF ", " OFFLINE ", " OFFSETS ", " OLD ", " OLEOBJECT ", " ON ", " ONCE ", " ONLINE ", " ONLY ", " OPEN ",
      " OPENDATASOURCE ", " OPENQUERY ", " OPENROWSET ", " OPTIMAL ", " OPTION ", " OPTIONS ", " OR ", " ORDER ",
      " OTHERS ", " OUT ", " OUTER ", " OUTPUT ", " OUTPUT_TYPE ", " OVER ", " OVERFLOW ", " OWN ", " OWNERACCESS ",
      " PACKAGE ", " PAGE ", " PAGE_SIZE ", " PAGELENGTH ", " PAGES ", " PARALLEL ", " PARAMETER ", " PARAMETERS ",
      " PARTITION ", " PASCAL ", " PASSTHROUGH ", " PASSWD ", " PASSWORD ", " PCTFREE ", " PCTINCREASE ", " PCTUSED ",
      " PERCENT ", " PERM ", " PERMANENT ", " PIPE ", " PIVOT ", " PLAN ", " PLI ", " POSITION ", " POST_EVENT ",
      " PRECISION ", " PREPARE ", " PRIMARY ", " PRINT ", " PRIOR ", " PRIVATE ", " PRIVILEGES ", " PROC ",
      " PROCEDURE ", " PROCESSEXIT ", " PROFILE ", " PROTECTED ", " PROXY ", " PUBLIC ", " PUBLICATION ", " QUIT ",
      " QUOTA ", " RAISERROR ", " RAW ", " RAW_PARTITIONS ", " RDB$DB_KEY ", " READ ", " READTEXT ", " REAL ",
      " RECONFIGURE ", " RECORD_VERSION ", " RECOVER ", " REFERENCE ", " REFERENCES ", " REFERENCING ", " RELEASE ",
      " REMOTE ", " REMOVE ", " RENAME ", " REPEATABLE ", " REPLACE ", " REPLICATION ", " RESERV ", " RESERVING ",
      " RESETLOGS ", " RESOURCE ", " RESTORE ", " RESTRICT ", " RESTRICTED ", " RETAIN ", " RETURN ",
      " RETURNING_VALUES ", " RETURNS ", " REUSE ", " REVOKE ", " RIGHT ", " ROLE ", " ROLES ", " ROLLBACK ", " ROW ",
      " ROWCOUNT ", " ROWGUIDCOL ", " ROWID ", " ROWLABEL ", " ROWNUM ", " ROWS ", " RULE ", " RUNTIME ", " SAVE ",
      " SAVEPOINT ", " SCHEDULE ", " SCHEMA ", " SCN ", " SCROLL ", " SECOND ", " SECTION ", " SEGMENT ", " SELECT ",
      " SEQUENCE ", " SERIALIZABLE ", " SESSION ", " SESSION_USER ", " SET ", " SETUSER ", " SHADOW ", " SHARE ",
      " SHARED ", " SHELL ", " SHORT ", " SHOW ", " SHUTDOWN ", " SINGLE ", " SINGULAR ", " SIZE ", " SMALLINT ",
      " SNAPSHOT ", " SOME ", " SORT ", " SQL ", " SQLCODE ", " SQLERROR ", " SQLSTATE ", " SQLWARNING ", " STABILITY ",
      " START ", " STARTING ", " STARTS ", " STATEMENT_ID ", " STATIC ", " STATISTICS ", " STDEV ", " STDEVP ",
      " STOP ", " STORAGE ", " STRING ", " STRIPE ", " SUB_TYPE ", " SUBSTRING ", " SUBTRANS ", " SUBTRANSACTION ",
      " SUCCESSFUL ", " SUM ", " SUSPEND ", " SWITCH ", " SYB_IDENTITY ", " SYB_RESTREE ", " SYNCHRONIZE ", " SYNONYM ",
      " SYNTAX_ERROR ", " SYSDATE ", " SYSTEM ", " SYSTEM_USER ", " TABLE ", " TABLEID ", " TABLES ", " TABLESPACE ",
      " TAPE ", " TEMP ", " TEMPORARY ", " TERMINATOR ", " TEXT ", " TEXTSIZE ", " THEN ", " THREAD ", " TIME ",
      " TIMESTAMP ", " TIMEZONE_HOUR ", " TIMEZONE_MINUTE ", " TINYINT ", " TO ", " TOP ", " TRACING ", " TRAILING ",
      " TRAN ", " TRANSACTION ", " TRANSFORM ", " TRANSLATE ", " TRANSLATION ", " TRIGGER ", " TRIGGERS ", " TRIM ",
      " TRUNCATE ", " TSEQUAL ", " UID ", " UNCOMMITTED ", " UNDER ", " UNION ", " UNIQUE ", " UNKNOWN ", " UNLIMITED ",
      " UNPARTITION ", " UNSIGNED ", " UNTIL ", " UPDATE ", " UPDATETEXT ", " UPPER ", " USE ", " USER ",
      " USER_OPTION ", " USING ", " VALIDATE ", " VALUE ", " VALUES ", " VAR ", " VARBINARY ", " VARCHAR ",
      " VARCHAR2 ", " VARIABLE ", " VARP ", " VARYING ", " VERSION ", " VIEW ", " WAIT ", " WAIT_TIME ", " WAITFOR ",
      " WHEN ", " WHENEVER ", " WHERE ", " WHILE ", " WITH ", " WORK ", " WRITE ", " WRITETEXT ", " XOR ", " YEAR ",
      " YESNO " };

  public static final String RELNAME_XPK = "XPK";
  public static final String RELNAME_DATA = "DATA";
  public static final String RELNAME_REFERENCE = "REFERENCE";
  public static final String RELNAME_FOREIGNKEY = "FOREIGNKEY";
  public static final String RELNAME_INDEX = "INDEX";
  public static final String RELNAME_CONNECTED = "CONNECTED";

  public static final String INDEXTYPE_XPK = "XPK";
  public static final String INDEXTYPE_XIF = "XIF";
  public static final String INDEXTYPE_XAK = "XAK";
  public static final String INDEXTYPE_XIE = "XIE";

  static final String VALIDATOR_WORDWRAP = " %s ";
  static final String VALIDATOR_NAMETOLONG = "Name is too long: %s (Max: %d )";
  static final String VALIDATOR_NAMENOTVALID = "Name is not valid: %s (REGEX: %s )";
  static final String VALIDATOR_TABLEALREADYEXISTS = "Tablename already exists!";
  static final String VALIDATOR_NAMECONTAINSSQL = "Name contains SQL Keywords: %s -> %s";
  static final String VALIDATOR_TABLEHASNOFIELDS = "Table has no Fields!";
  static final String VALIDATOR_REFTABLEISNULL = "RefTable of %s is null";
  static final String VALIDATOR_DUPLICATEFIELDS = "There are Fields with the same Name!";
  static final String VALIDATOR_INDEXHASNOFIELDS = "Index has no Fields: %s";
  static final String LOADFROMDB = "von DB geladen: ";
  static final String CONF_DEFAULT_ADR = "bolt://localhost:7687";
  static final String CONF_DEFAULT_USRPW = "neo4j";
  static final String CONF_DEFAULT_TITLE = "Gesamtmodell v1.00.1 \\copyright Firma";
  public static final String CONF_LOC = "APPDATA";

  static final String CONFID_PDFTITLE = "pdfTitle";
  static final String CONFID_ADR = "addrOfDB";
  static final String CONFID_USR = "user";
  static final String CONFID_PW = "pw";
  static final String CONFID_WL = "wordLength";
  static final String CONFID_LOC = "neoDBStarterLocation";
  static final String CONFID_SDT = "SelectDomain";
  static final String CONFID_UTL = "uniqueTableLength";
  static final String CONFID_MIKTEX = "miktexpath";
  static final String CONFID_PDFPATH = "pdfexportpath";

  static final String PATH_NEO4J = "\\bin\\neo4j.bat";
  static final String EXECNEO4J_PRE = " C:\\Windows\\system32\\cmd.exe /c cd /d \"";
  static final String EXECNEO4J_POST = "\\bin\" & start C:\\Windows\\system32\\cmd.exe /k \"neo4j.bat console\"";

  public static final String CATEGORYNONE = "0,0";
  public static final String IMPORTGENERATEDINDEXMARKER = "_g";
  static final String CONF_ERR = "Deleted faulty Config!";

  public static final String HTML_BR = "<br />";
  public static final String CARRIAGE_RETURN = "\r";
  public static final String NEWLINE = "\n";
  public static final String EOL = "\r\n";
  public static final String OPENBRACKET = "(";
  public static final String CLOSEDBRACKET = ")";
  public static final String EMPTYSTRING = "";
  public static final String SPACE = " ";
  public static final String SEMICOLON = ";";
  public static final String COMMA = ",";
  public static final String COMMAVALUE = "%s, ";
  public static final String NULL = "NULL";
  public static final String NOTNULL = "NOT NULL";
  public static final String UNIQUE = "UNIQUE";
  public static final String SQL_PRIMKEY = "ALTER TABLE %s       ADD" + "  ( CONSTRAINT %s PRIMARY KEY (";
  public static final String SQL_INDEX = "CREATE%s INDEX %s ON %s(%s";
  public static final String SQL_INDEX_ELEM = "       %s ASC ,%s";
  public static final String SQL_TABLE_COMMENT = " -- Table: %s Category: %s Comment:%s";
  public static final String SQL_TABLE = "CREATE TABLE %s (%s";
  public static final String SQL_TABLE_ELEM = "       %s %s,%s";
  public static final String SQL_FOREIGNKEY_START = "ALTER TABLE %s       ADD"
      + "  ( CONSTRAINT %s              FOREIGN KEY (";
  public static final String SQL_FOREIGNKEY_END = ")%s                             REFERENCES %s ";
  public static final String CORRECTEDIMPORT = "Korrektur Tabelle: %s Index (%s) erstellt";
  public static final String EXPORTFROMDB = "Exported in ms: ";
  public static final String CATEGORYFILE = "DatenmodellSektionen.properties";
  public static final String SECTION = "section.";
  static final char IMPORTTYPESQL = 's';
  static final String VALIDATOR_NAMEWIHTANOTHERCASE = "There are fields that have a similar name for"
      + " '%s' with another case: %s";
  static final String VALIDATOR_TABLEISSIMILAR = "There is a Table with a similar Name "
      + "(first 15 Chars identical).";
  public static final String COLON = ":";
  public static final String IMPORT_CATEGORIES = "writing categories";
  public static final String IMPORT_PARSING = "start parsing";
  public static final String IMPORT_FINISHED = "finished Import (%s s)";
  public static final String IMPORT_FAILED = "no Import Database! (%s s)";
  public static final String IMPORT_TABLES = "writing tables";
  public static final String IMPORT_FOREIGNKEYS = "writing ForeignKeys";
  public static final String IMPORT_CLEANUP = "cleanup";

  public static final String RES_PATH = "/de/mach/tools/neodesigner/core/";
  public static final String ALTEXT_DELETEERROR = "Could not delete previous PDF File!";
  static final String LOG_CHANGETABLENAME = "change table name";
  static final String LOG_CHANGENODENAME = "change node name";
  static final String LOG_CHANGETABLECAT = "change table category";
  static final String LOG_CHANGEFIELDREQ = "change field required";
  static final String LOG_CHANGEFIELDTOD = "change field type of data";
  static final String LOG_CHANGEFIELDPRIM = "change field is part of prim";
  static final String LOG_CHANGEINDEXUNIQUE = "change index unique";
  static final String LOG_TABLENEW = "insert new table";
  static final String LOG_NEWFIELD = "insert new field";
  static final String LOG_NEWINDEX = "insert new index";
  static final String LOG_NEWFK = "insert new FK";
  static final String LOG_DELETENODE = "delete node";
  static final String LOG_CHANGEDATAFIELDS = "change data fields";
  static final String LOG_CHANGEFKREL = "change fk rel";
  static final String LOG_GETTABLE = "get table";
  static final String LOG_HASTABLE = "has table";
  static final String LOG_GETTABLES = "get tables";
  static final String LOG_GETFIELDNAMECASE = "get field name case";
  static final String LOG_SAVECOMMENT = "change comment";
  public static final String IMPORT_TYPE_NUMBER = "NUMBER";
  public static final String IMPORT_TYPE_VARCHAR = "VARCHAR2";
  public static final String IMPORT_TYPE_CHAR = "CHAR";
  public static final String IMPORT_TYPE_SPLIT = "\\(";
  public static final String IMPORT_TYPE_INT = "INTEGER";
  public static final String IMPORT_TYPE_BLOB = "LONG RAW";
  public static final String IMPORT_NULL = "0";
  public static final String IMPORT_TYPE_SMALLINT = "SMALLINT";
  public static final String IMPORT_18 = "18";
  public static final char REPLACECHAR_BRACKETOPEN = ')';
  public static final char REPLACECHAR_SPACE = ' ';
  public static final String LOG_TABLEERROR = "Failed to find correct Entity in Datamodel! Trying to recover! Please refresh Datamodel!!";
  static final String VALIDATOR_PRIMAERFIELDNOTVALID = "A field from the primary key is already in use in a foreign key!";
  public static final String DOMAIN_LOOKUP = "lookup";
  public static final String DOMAIN_AMOUNT = "amount";
  public static final String DOMAIN_BLOB = "blob";
  public static final String DOMAIN_BOOLEAN = "boolean";
  public static final String DOMAIN_CLOB = "clob";
  public static final String DOMAIN_COUNTER = "counter";
  public static final String DOMAIN_DATE = "date";
  public static final String DOMAIN_STRING = "string";
  public static final String ERROR_DOMAIN = "Unknown Domain while converting -> Fallback to String!";
  public static final String DOMAIN_ERROR_IN = "Unknown Domain while converting";
  public static final String ENCODINGUTF8 = "UTF-8";
  public static final String PROP_PATH = "file.resource.loader.path";
  public static final String PROP_IN_ENCODING = "input.encoding";
  public static final String PROP_OUT_ENCODING = "output.encoding";
  public static final String REGEX_REPLACE_EXT = "[.][^.]+$";
  public static final String EXT_PDF = ".pdf";
  public static final String INTERACTION_NONSTOPMODE = "-interaction=nonstopmode";
  public static final String NAME_PROCESSLOG = "ProcessLog.txt";
  public static final String NAME_ERRORLOG = "ErrorLog.txt";
  public static final String N = "N";
  public static final String Y = "Y";
  public static final String N20 = "20";
  public static final String WARN = "-!-";
  public static final String DOMAIN_DATETIME = "Datetime";
  public static final String DOMAIN_RAWBLOB = "RawBlob";
  public static final String DOMAIN_STRINGBLOB = "StringBlob";
  public static final String ERROR_FOLDER = "Failed to create Folder!";
  public static final String TEXT_VISIT = "Visit:";
  public static final String PATH_PDF_BUILDER = "\\miktex\\bin\\pdflatex.exe";
  public static final String PATH_PROCESS_BUILDER = "\\miktex\\bin\\makeindex.exe";
  public static final String PATH_DATA_MODEL = "\\Datenmodell.tex";
  public static final String PDF_FILE = "Datenmodell.pdf";
  public static final String DATAMODEL_TEMPLATE_NAME = "DatenmodellVorlage.tex";
  public static final String MIKTEX = "MikTex";
  public static final String MIKTEX_PATH_P = "P:\\cd\\Programme\\ModelPrinter\\MikTex";
  public static final String MIKTEX_PATH_E = "E:\\Programme\\MikTex";
  public static final String MIKTEX_PATH_D = "D:\\Programme\\MikTex";
  public static final String MIKTEX_PATH_C = "C:\\Programme\\MikTex";
  public static final String USER_HOME = "user.home";
  public static final String EXPLOREREXEC = "explorer ";
  public static final String CONFID_PATHINSQL = "pathinsql";
  public static final String CONFID_PATHINCAT = "pathincat";
  public static final String CONFID_PATHINCSV = "pathincsv";
  public static final String CONFID_PATHOUTSQL = "pathoutsql";
  public static final String CONFID_PATHOUTCSV = "pathoutcsv";
  public static final String CONFID_PATHOUTCQL = "pathoutcql";
  public static final String DEFAULTPATH = System.getProperty(Strings.USER_HOME);
}
