grammar SQL;

// Parser Rules 

parseAll:
	parse+;

parse:
	createTable
	| createIndex
	| createUniqueIndex
	| primKey
	| foreignKey
;

// SQL Statements
createTable : 
'CREATE TABLE' tablename '(' createFieldName  ((',' createFieldName)+)? ');' # creTable
; 

createIndex : 
'CREATE INDEX' indexname 'ON' tablename fieldNameList # creIndex
;

createUniqueIndex : 
'CREATE UNIQUE INDEX' indexname 'ON' tablename fieldNameList # creUniqueIndex
;

primKey : 
'ALTER TABLE' tablename 'ADD' '(' 'CONSTRAINT' indexname 'PRIMARY KEY' fieldNameList ') ;' # crePrimKey
;
       
foreignKey: 
'ALTER TABLE' tablename 'ADD' '(' 'CONSTRAINT' indexname 'FOREIGN KEY' fieldNameList 'REFERENCES' tablename ('ON DELETE SET NULL')? ') ;' # creForeignKey
;

// Listelemente in der Feldaufzählung
fieldNameList: '(' fieldname  ('ASC')?  ((',' fieldname  ('ASC')?  )+)? (')' | ');') ? # crefieldNameList
;

createFieldName: fieldname type (isNull | isNotNull) #creField
; 

// Teilelemente der Querys
isNull: 'NULL'
;

isNotNull: 'NOT NULL'
;

tablename : ID (ID | INT)+;
fieldname : ID (ID | INT)+;
indexname : ID (ID | INT)+;

type	  :   'INTEGER'
			| 'NUMBER(' INT ',' INT ')'
			| 'VARCHAR2(' INT ')'
			| 'DATE'
			| 'SMALLINT'
			| 'LONG RAW'
			| 'CHAR('INT')'
			| 'BLOB'
			| 'CLOB'
			;
// Lexer Rules
SINGLE_LINE_COMMENT : '--' ~[\r\n]* -> skip; // toss out comments           
ID  :   [a-zA-Z_-] ;      // match identifiers
INT :   [0-9]+ ;         // match integers
WS  :   [ \t\r\n]+ -> skip ; // toss out whitespace
