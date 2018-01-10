package de.mach.tools.neodesigner.core;

/**
 * Klasse in der alle Strings ausgelagert werden
 * 
 * @author Chris Deter
 *
 */
public final class Strings {
  private Strings() {
  }

  static final String SOFTWARENAME = "NeoDatabaseDesigner";
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
  public static final char INDEXTYPE_R = 'R';

  static final String VALIDATOR_WORDWRAP = " %s ";
  static final String VALIDATOR_NAMETOLONG = "Name is too long: %s (Max: %d )";
  static final String VALIDATOR_NAMENOTVALID = "Name is not valid: %s (REGEX: %s )";
  static final String VALIDATOR_TABLEALREADYEXISTS = "Tablename already exists!";
  static final String VALIDATOR_NAMECONTAINSSQL = "Name contains SQL Keywords: %s -> %s";
  static final String VALIDATOR_TABLEHASNOFIELDS = "Table has no Fields!";
  static final String VALIDATOR_REFTABLEISNULL = "RefTable of %s is null";
  static final String VALIDATOR_INDEXHASNOFIELDS = "Index has no Fields: %s";
  static final String LOADFROMDB = "von DB geladen: ";
  static final String CONF_DEFAULT_ADR = "bolt://localhost:7687";
  static final String CONF_DEFAULT_USRPW = "neo4j";
  static final String CONF_LOC = "APPDATA";

  static final String CONFID_ADR = "addrOfDB";
  static final String CONFID_USR = "user";
  static final String CONFID_PW = "pw";
  static final String CONFID_WL = "wordLength";
  static final String CONFID_LOC = "neoDBStarterLocation";
  static final String CONFID_SDT = "SelectDataTypes";

  static final String PATH_NEO4J = "\\bin\\neo4j.bat";
  static final String EXECNEO4J_PRE = "cmd.exe /c cd /d \"";
  static final String EXECNEO4J_POST = "\\bin\" & start cmd.exe /k \"neo4j.bat console\"";

  public static final String CATEGORYNONE = "none";
  public static final String IMPORTGENERATEDINDEXMARKER = "_g";
  static final String CONF_ERR = "Deleted faulty Config!";

  public static final String EOL = "\r\n";
  public static final String OPENBRACKET = "(";
  public static final String CLOSEDBRACKET = ")";
  public static final String EMPTYSTRING = "";
  public static final String SPACE = " ";
  public static final String SEMICOLON = ";";
  public static final String COMMAVALUE = "%s, ";
  public static final String NULL = "NULL";
  public static final String NOTNULL = "NOT NULL";
  public static final String UNIQUE = "UNIQUE";
  public static final String SQL_PRIMKEY = "ALTER TABLE %s       ADD" + "  ( CONSTRAINT %s PRIMARY KEY (";
  public static final String SQL_INDEX = "CREATE%s INDEX %s ON %s(%s";
  public static final String SQL_INDEX_ELEM = "       %s ASC ,%s";
  public static final String SQL_TABLE_COMMENT = " -- Table: %s Category: %s";
  public static final String SQL_TABLE = "CREATE TABLE %s (%s";
  public static final String SQL_TABLE_ELEM = "       %s %s,%s";
  public static final String SQL_FOREIGNKEY_START = "ALTER TABLE %s       ADD"
      + "  ( CONSTRAINT %s              FOREIGN KEY (";
  public static final String SQL_FOREIGNKEY_END = ")%s                             REFERENCES %s ";
  public static final String CORRECTEDIMPORT = "Korrektur Tabelle: %s Index (%s) erstellt";
  public static final String EXPORTFROMDB = "Exported in ms: ";
}
