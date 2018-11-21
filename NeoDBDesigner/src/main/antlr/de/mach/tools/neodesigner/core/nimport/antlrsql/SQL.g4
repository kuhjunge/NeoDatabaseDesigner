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
	| catComment
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

// Listelemente in der Feldaufz�hlung
fieldNameList: '(' fieldname  ('ASC')?  ((',' fieldname  ('ASC')?  )+)? (')' | ');') ? # crefieldNameList
;

createFieldName: fieldname type (isNull | isNotNull) #creField
; 

// Teilelemente der Querys
isNull: 'NULL'
;

isNotNull: 'NOT NULL'
;

catComment: SINGLE_LINE_COMMENT #creCategory
;

tablename : ID (ID | INT)+;
fieldname : ID (ID | INT)+;
indexname : ID (ID | INT)+;
name : (ID | INT | SPECIAL)+;

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
SINGLE_LINE_COMMENT : '--' ~[\r\n]*; // toss out comments     
ID  :   [a-zA-Z_-] ;      // match identifiers
INT :   [0-9]+ ;         // match integers
SPECIAL: [,];
WS  :   [ \t\r\n]+ -> skip ; // toss out whitespace
