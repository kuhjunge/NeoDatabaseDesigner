// Generated from SQL.g4 by ANTLR 4.7
package de.mach.tools.neodesigner.core.nimport.antlrsql;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class SQLParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, T__20=21, T__21=22, T__22=23, T__23=24, 
		T__24=25, T__25=26, T__26=27, T__27=28, SINGLE_LINE_COMMENT=29, ID=30, 
		INT=31, SPECIAL=32, WS=33;
	public static final int
		RULE_parseAll = 0, RULE_parse = 1, RULE_createTable = 2, RULE_createIndex = 3, 
		RULE_createUniqueIndex = 4, RULE_primKey = 5, RULE_foreignKey = 6, RULE_fieldNameList = 7, 
		RULE_createFieldName = 8, RULE_isNull = 9, RULE_isNotNull = 10, RULE_catComment = 11, 
		RULE_tablename = 12, RULE_fieldname = 13, RULE_indexname = 14, RULE_name = 15, 
		RULE_type = 16;
	public static final String[] ruleNames = {
		"parseAll", "parse", "createTable", "createIndex", "createUniqueIndex", 
		"primKey", "foreignKey", "fieldNameList", "createFieldName", "isNull", 
		"isNotNull", "catComment", "tablename", "fieldname", "indexname", "name", 
		"type"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'CREATE TABLE'", "'('", "','", "');'", "'CREATE INDEX'", "'ON'", 
		"'CREATE UNIQUE INDEX'", "'ALTER TABLE'", "'ADD'", "'CONSTRAINT'", "'PRIMARY KEY'", 
		"') ;'", "'FOREIGN KEY'", "'REFERENCES'", "'ON DELETE SET NULL'", "'ASC'", 
		"')'", "'NULL'", "'NOT NULL'", "'INTEGER'", "'NUMBER('", "'VARCHAR2('", 
		"'DATE'", "'SMALLINT'", "'LONG RAW'", "'CHAR('", "'BLOB'", "'CLOB'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, "SINGLE_LINE_COMMENT", "ID", "INT", "SPECIAL", 
		"WS"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "SQL.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public SQLParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class ParseAllContext extends ParserRuleContext {
		public List<ParseContext> parse() {
			return getRuleContexts(ParseContext.class);
		}
		public ParseContext parse(int i) {
			return getRuleContext(ParseContext.class,i);
		}
		public ParseAllContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parseAll; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLListener ) ((SQLListener)listener).enterParseAll(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLListener ) ((SQLListener)listener).exitParseAll(this);
		}
	}

	public final ParseAllContext parseAll() throws RecognitionException {
		ParseAllContext _localctx = new ParseAllContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_parseAll);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(35); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(34);
				parse();
				}
				}
				setState(37); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__4) | (1L << T__6) | (1L << T__7) | (1L << SINGLE_LINE_COMMENT))) != 0) );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ParseContext extends ParserRuleContext {
		public CreateTableContext createTable() {
			return getRuleContext(CreateTableContext.class,0);
		}
		public CreateIndexContext createIndex() {
			return getRuleContext(CreateIndexContext.class,0);
		}
		public CreateUniqueIndexContext createUniqueIndex() {
			return getRuleContext(CreateUniqueIndexContext.class,0);
		}
		public PrimKeyContext primKey() {
			return getRuleContext(PrimKeyContext.class,0);
		}
		public ForeignKeyContext foreignKey() {
			return getRuleContext(ForeignKeyContext.class,0);
		}
		public CatCommentContext catComment() {
			return getRuleContext(CatCommentContext.class,0);
		}
		public ParseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parse; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLListener ) ((SQLListener)listener).enterParse(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLListener ) ((SQLListener)listener).exitParse(this);
		}
	}

	public final ParseContext parse() throws RecognitionException {
		ParseContext _localctx = new ParseContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_parse);
		try {
			setState(45);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(39);
				createTable();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(40);
				createIndex();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(41);
				createUniqueIndex();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(42);
				primKey();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(43);
				foreignKey();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(44);
				catComment();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CreateTableContext extends ParserRuleContext {
		public CreateTableContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_createTable; }
	 
		public CreateTableContext() { }
		public void copyFrom(CreateTableContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class CreTableContext extends CreateTableContext {
		public TablenameContext tablename() {
			return getRuleContext(TablenameContext.class,0);
		}
		public List<CreateFieldNameContext> createFieldName() {
			return getRuleContexts(CreateFieldNameContext.class);
		}
		public CreateFieldNameContext createFieldName(int i) {
			return getRuleContext(CreateFieldNameContext.class,i);
		}
		public CreTableContext(CreateTableContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLListener ) ((SQLListener)listener).enterCreTable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLListener ) ((SQLListener)listener).exitCreTable(this);
		}
	}

	public final CreateTableContext createTable() throws RecognitionException {
		CreateTableContext _localctx = new CreateTableContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_createTable);
		int _la;
		try {
			_localctx = new CreTableContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(47);
			match(T__0);
			setState(48);
			tablename();
			setState(49);
			match(T__1);
			setState(50);
			createFieldName();
			setState(57);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__2) {
				{
				setState(53); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(51);
					match(T__2);
					setState(52);
					createFieldName();
					}
					}
					setState(55); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__2 );
				}
			}

			setState(59);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CreateIndexContext extends ParserRuleContext {
		public CreateIndexContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_createIndex; }
	 
		public CreateIndexContext() { }
		public void copyFrom(CreateIndexContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class CreIndexContext extends CreateIndexContext {
		public IndexnameContext indexname() {
			return getRuleContext(IndexnameContext.class,0);
		}
		public TablenameContext tablename() {
			return getRuleContext(TablenameContext.class,0);
		}
		public FieldNameListContext fieldNameList() {
			return getRuleContext(FieldNameListContext.class,0);
		}
		public CreIndexContext(CreateIndexContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLListener ) ((SQLListener)listener).enterCreIndex(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLListener ) ((SQLListener)listener).exitCreIndex(this);
		}
	}

	public final CreateIndexContext createIndex() throws RecognitionException {
		CreateIndexContext _localctx = new CreateIndexContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_createIndex);
		try {
			_localctx = new CreIndexContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(61);
			match(T__4);
			setState(62);
			indexname();
			setState(63);
			match(T__5);
			setState(64);
			tablename();
			setState(65);
			fieldNameList();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CreateUniqueIndexContext extends ParserRuleContext {
		public CreateUniqueIndexContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_createUniqueIndex; }
	 
		public CreateUniqueIndexContext() { }
		public void copyFrom(CreateUniqueIndexContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class CreUniqueIndexContext extends CreateUniqueIndexContext {
		public IndexnameContext indexname() {
			return getRuleContext(IndexnameContext.class,0);
		}
		public TablenameContext tablename() {
			return getRuleContext(TablenameContext.class,0);
		}
		public FieldNameListContext fieldNameList() {
			return getRuleContext(FieldNameListContext.class,0);
		}
		public CreUniqueIndexContext(CreateUniqueIndexContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLListener ) ((SQLListener)listener).enterCreUniqueIndex(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLListener ) ((SQLListener)listener).exitCreUniqueIndex(this);
		}
	}

	public final CreateUniqueIndexContext createUniqueIndex() throws RecognitionException {
		CreateUniqueIndexContext _localctx = new CreateUniqueIndexContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_createUniqueIndex);
		try {
			_localctx = new CreUniqueIndexContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(67);
			match(T__6);
			setState(68);
			indexname();
			setState(69);
			match(T__5);
			setState(70);
			tablename();
			setState(71);
			fieldNameList();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PrimKeyContext extends ParserRuleContext {
		public PrimKeyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_primKey; }
	 
		public PrimKeyContext() { }
		public void copyFrom(PrimKeyContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class CrePrimKeyContext extends PrimKeyContext {
		public TablenameContext tablename() {
			return getRuleContext(TablenameContext.class,0);
		}
		public IndexnameContext indexname() {
			return getRuleContext(IndexnameContext.class,0);
		}
		public FieldNameListContext fieldNameList() {
			return getRuleContext(FieldNameListContext.class,0);
		}
		public CrePrimKeyContext(PrimKeyContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLListener ) ((SQLListener)listener).enterCrePrimKey(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLListener ) ((SQLListener)listener).exitCrePrimKey(this);
		}
	}

	public final PrimKeyContext primKey() throws RecognitionException {
		PrimKeyContext _localctx = new PrimKeyContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_primKey);
		try {
			_localctx = new CrePrimKeyContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(73);
			match(T__7);
			setState(74);
			tablename();
			setState(75);
			match(T__8);
			setState(76);
			match(T__1);
			setState(77);
			match(T__9);
			setState(78);
			indexname();
			setState(79);
			match(T__10);
			setState(80);
			fieldNameList();
			setState(81);
			match(T__11);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ForeignKeyContext extends ParserRuleContext {
		public ForeignKeyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_foreignKey; }
	 
		public ForeignKeyContext() { }
		public void copyFrom(ForeignKeyContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class CreForeignKeyContext extends ForeignKeyContext {
		public List<TablenameContext> tablename() {
			return getRuleContexts(TablenameContext.class);
		}
		public TablenameContext tablename(int i) {
			return getRuleContext(TablenameContext.class,i);
		}
		public IndexnameContext indexname() {
			return getRuleContext(IndexnameContext.class,0);
		}
		public FieldNameListContext fieldNameList() {
			return getRuleContext(FieldNameListContext.class,0);
		}
		public CreForeignKeyContext(ForeignKeyContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLListener ) ((SQLListener)listener).enterCreForeignKey(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLListener ) ((SQLListener)listener).exitCreForeignKey(this);
		}
	}

	public final ForeignKeyContext foreignKey() throws RecognitionException {
		ForeignKeyContext _localctx = new ForeignKeyContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_foreignKey);
		int _la;
		try {
			_localctx = new CreForeignKeyContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(83);
			match(T__7);
			setState(84);
			tablename();
			setState(85);
			match(T__8);
			setState(86);
			match(T__1);
			setState(87);
			match(T__9);
			setState(88);
			indexname();
			setState(89);
			match(T__12);
			setState(90);
			fieldNameList();
			setState(91);
			match(T__13);
			setState(92);
			tablename();
			setState(94);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__14) {
				{
				setState(93);
				match(T__14);
				}
			}

			setState(96);
			match(T__11);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FieldNameListContext extends ParserRuleContext {
		public FieldNameListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fieldNameList; }
	 
		public FieldNameListContext() { }
		public void copyFrom(FieldNameListContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class CrefieldNameListContext extends FieldNameListContext {
		public List<FieldnameContext> fieldname() {
			return getRuleContexts(FieldnameContext.class);
		}
		public FieldnameContext fieldname(int i) {
			return getRuleContext(FieldnameContext.class,i);
		}
		public CrefieldNameListContext(FieldNameListContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLListener ) ((SQLListener)listener).enterCrefieldNameList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLListener ) ((SQLListener)listener).exitCrefieldNameList(this);
		}
	}

	public final FieldNameListContext fieldNameList() throws RecognitionException {
		FieldNameListContext _localctx = new FieldNameListContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_fieldNameList);
		int _la;
		try {
			_localctx = new CrefieldNameListContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(98);
			match(T__1);
			setState(99);
			fieldname();
			setState(101);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__15) {
				{
				setState(100);
				match(T__15);
				}
			}

			setState(112);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__2) {
				{
				setState(108); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(103);
					match(T__2);
					setState(104);
					fieldname();
					setState(106);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==T__15) {
						{
						setState(105);
						match(T__15);
						}
					}

					}
					}
					setState(110); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__2 );
				}
			}

			setState(115);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__3 || _la==T__16) {
				{
				setState(114);
				_la = _input.LA(1);
				if ( !(_la==T__3 || _la==T__16) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CreateFieldNameContext extends ParserRuleContext {
		public CreateFieldNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_createFieldName; }
	 
		public CreateFieldNameContext() { }
		public void copyFrom(CreateFieldNameContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class CreFieldContext extends CreateFieldNameContext {
		public FieldnameContext fieldname() {
			return getRuleContext(FieldnameContext.class,0);
		}
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public IsNullContext isNull() {
			return getRuleContext(IsNullContext.class,0);
		}
		public IsNotNullContext isNotNull() {
			return getRuleContext(IsNotNullContext.class,0);
		}
		public CreFieldContext(CreateFieldNameContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLListener ) ((SQLListener)listener).enterCreField(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLListener ) ((SQLListener)listener).exitCreField(this);
		}
	}

	public final CreateFieldNameContext createFieldName() throws RecognitionException {
		CreateFieldNameContext _localctx = new CreateFieldNameContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_createFieldName);
		try {
			_localctx = new CreFieldContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(117);
			fieldname();
			setState(118);
			type();
			setState(121);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__17:
				{
				setState(119);
				isNull();
				}
				break;
			case T__18:
				{
				setState(120);
				isNotNull();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IsNullContext extends ParserRuleContext {
		public IsNullContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_isNull; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLListener ) ((SQLListener)listener).enterIsNull(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLListener ) ((SQLListener)listener).exitIsNull(this);
		}
	}

	public final IsNullContext isNull() throws RecognitionException {
		IsNullContext _localctx = new IsNullContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_isNull);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(123);
			match(T__17);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IsNotNullContext extends ParserRuleContext {
		public IsNotNullContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_isNotNull; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLListener ) ((SQLListener)listener).enterIsNotNull(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLListener ) ((SQLListener)listener).exitIsNotNull(this);
		}
	}

	public final IsNotNullContext isNotNull() throws RecognitionException {
		IsNotNullContext _localctx = new IsNotNullContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_isNotNull);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(125);
			match(T__18);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CatCommentContext extends ParserRuleContext {
		public CatCommentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_catComment; }
	 
		public CatCommentContext() { }
		public void copyFrom(CatCommentContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class CreCategoryContext extends CatCommentContext {
		public TerminalNode SINGLE_LINE_COMMENT() { return getToken(SQLParser.SINGLE_LINE_COMMENT, 0); }
		public CreCategoryContext(CatCommentContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLListener ) ((SQLListener)listener).enterCreCategory(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLListener ) ((SQLListener)listener).exitCreCategory(this);
		}
	}

	public final CatCommentContext catComment() throws RecognitionException {
		CatCommentContext _localctx = new CatCommentContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_catComment);
		try {
			_localctx = new CreCategoryContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(127);
			match(SINGLE_LINE_COMMENT);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TablenameContext extends ParserRuleContext {
		public List<TerminalNode> ID() { return getTokens(SQLParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(SQLParser.ID, i);
		}
		public List<TerminalNode> INT() { return getTokens(SQLParser.INT); }
		public TerminalNode INT(int i) {
			return getToken(SQLParser.INT, i);
		}
		public TablenameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tablename; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLListener ) ((SQLListener)listener).enterTablename(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLListener ) ((SQLListener)listener).exitTablename(this);
		}
	}

	public final TablenameContext tablename() throws RecognitionException {
		TablenameContext _localctx = new TablenameContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_tablename);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(129);
			match(ID);
			setState(131); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(130);
				_la = _input.LA(1);
				if ( !(_la==ID || _la==INT) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
				}
				setState(133); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==ID || _la==INT );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FieldnameContext extends ParserRuleContext {
		public List<TerminalNode> ID() { return getTokens(SQLParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(SQLParser.ID, i);
		}
		public List<TerminalNode> INT() { return getTokens(SQLParser.INT); }
		public TerminalNode INT(int i) {
			return getToken(SQLParser.INT, i);
		}
		public FieldnameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fieldname; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLListener ) ((SQLListener)listener).enterFieldname(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLListener ) ((SQLListener)listener).exitFieldname(this);
		}
	}

	public final FieldnameContext fieldname() throws RecognitionException {
		FieldnameContext _localctx = new FieldnameContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_fieldname);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(135);
			match(ID);
			setState(137); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(136);
				_la = _input.LA(1);
				if ( !(_la==ID || _la==INT) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
				}
				setState(139); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==ID || _la==INT );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IndexnameContext extends ParserRuleContext {
		public List<TerminalNode> ID() { return getTokens(SQLParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(SQLParser.ID, i);
		}
		public List<TerminalNode> INT() { return getTokens(SQLParser.INT); }
		public TerminalNode INT(int i) {
			return getToken(SQLParser.INT, i);
		}
		public IndexnameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_indexname; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLListener ) ((SQLListener)listener).enterIndexname(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLListener ) ((SQLListener)listener).exitIndexname(this);
		}
	}

	public final IndexnameContext indexname() throws RecognitionException {
		IndexnameContext _localctx = new IndexnameContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_indexname);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(141);
			match(ID);
			setState(143); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(142);
				_la = _input.LA(1);
				if ( !(_la==ID || _la==INT) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
				}
				setState(145); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==ID || _la==INT );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NameContext extends ParserRuleContext {
		public List<TerminalNode> ID() { return getTokens(SQLParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(SQLParser.ID, i);
		}
		public List<TerminalNode> INT() { return getTokens(SQLParser.INT); }
		public TerminalNode INT(int i) {
			return getToken(SQLParser.INT, i);
		}
		public List<TerminalNode> SPECIAL() { return getTokens(SQLParser.SPECIAL); }
		public TerminalNode SPECIAL(int i) {
			return getToken(SQLParser.SPECIAL, i);
		}
		public NameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLListener ) ((SQLListener)listener).enterName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLListener ) ((SQLListener)listener).exitName(this);
		}
	}

	public final NameContext name() throws RecognitionException {
		NameContext _localctx = new NameContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_name);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(148); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(147);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ID) | (1L << INT) | (1L << SPECIAL))) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
				}
				setState(150); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ID) | (1L << INT) | (1L << SPECIAL))) != 0) );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TypeContext extends ParserRuleContext {
		public List<TerminalNode> INT() { return getTokens(SQLParser.INT); }
		public TerminalNode INT(int i) {
			return getToken(SQLParser.INT, i);
		}
		public TypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLListener ) ((SQLListener)listener).enterType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLListener ) ((SQLListener)listener).exitType(this);
		}
	}

	public final TypeContext type() throws RecognitionException {
		TypeContext _localctx = new TypeContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_type);
		try {
			setState(169);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__19:
				enterOuterAlt(_localctx, 1);
				{
				setState(152);
				match(T__19);
				}
				break;
			case T__20:
				enterOuterAlt(_localctx, 2);
				{
				setState(153);
				match(T__20);
				setState(154);
				match(INT);
				setState(155);
				match(T__2);
				setState(156);
				match(INT);
				setState(157);
				match(T__16);
				}
				break;
			case T__21:
				enterOuterAlt(_localctx, 3);
				{
				setState(158);
				match(T__21);
				setState(159);
				match(INT);
				setState(160);
				match(T__16);
				}
				break;
			case T__22:
				enterOuterAlt(_localctx, 4);
				{
				setState(161);
				match(T__22);
				}
				break;
			case T__23:
				enterOuterAlt(_localctx, 5);
				{
				setState(162);
				match(T__23);
				}
				break;
			case T__24:
				enterOuterAlt(_localctx, 6);
				{
				setState(163);
				match(T__24);
				}
				break;
			case T__25:
				enterOuterAlt(_localctx, 7);
				{
				setState(164);
				match(T__25);
				setState(165);
				match(INT);
				setState(166);
				match(T__16);
				}
				break;
			case T__26:
				enterOuterAlt(_localctx, 8);
				{
				setState(167);
				match(T__26);
				}
				break;
			case T__27:
				enterOuterAlt(_localctx, 9);
				{
				setState(168);
				match(T__27);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3#\u00ae\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\3\2\6\2&\n\2\r\2\16\2\'\3\3\3\3\3\3\3\3\3\3\3\3\5\3\60\n\3\3\4\3\4\3"+
		"\4\3\4\3\4\3\4\6\48\n\4\r\4\16\49\5\4<\n\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5"+
		"\3\5\3\6\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3"+
		"\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\5\ba\n\b\3\b\3\b\3\t\3\t\3"+
		"\t\5\th\n\t\3\t\3\t\3\t\5\tm\n\t\6\to\n\t\r\t\16\tp\5\ts\n\t\3\t\5\tv"+
		"\n\t\3\n\3\n\3\n\3\n\5\n|\n\n\3\13\3\13\3\f\3\f\3\r\3\r\3\16\3\16\6\16"+
		"\u0086\n\16\r\16\16\16\u0087\3\17\3\17\6\17\u008c\n\17\r\17\16\17\u008d"+
		"\3\20\3\20\6\20\u0092\n\20\r\20\16\20\u0093\3\21\6\21\u0097\n\21\r\21"+
		"\16\21\u0098\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3"+
		"\22\3\22\3\22\3\22\3\22\3\22\5\22\u00ac\n\22\3\22\2\2\23\2\4\6\b\n\f\16"+
		"\20\22\24\26\30\32\34\36 \"\2\5\4\2\6\6\23\23\3\2 !\3\2 \"\2\u00b7\2%"+
		"\3\2\2\2\4/\3\2\2\2\6\61\3\2\2\2\b?\3\2\2\2\nE\3\2\2\2\fK\3\2\2\2\16U"+
		"\3\2\2\2\20d\3\2\2\2\22w\3\2\2\2\24}\3\2\2\2\26\177\3\2\2\2\30\u0081\3"+
		"\2\2\2\32\u0083\3\2\2\2\34\u0089\3\2\2\2\36\u008f\3\2\2\2 \u0096\3\2\2"+
		"\2\"\u00ab\3\2\2\2$&\5\4\3\2%$\3\2\2\2&\'\3\2\2\2\'%\3\2\2\2\'(\3\2\2"+
		"\2(\3\3\2\2\2)\60\5\6\4\2*\60\5\b\5\2+\60\5\n\6\2,\60\5\f\7\2-\60\5\16"+
		"\b\2.\60\5\30\r\2/)\3\2\2\2/*\3\2\2\2/+\3\2\2\2/,\3\2\2\2/-\3\2\2\2/."+
		"\3\2\2\2\60\5\3\2\2\2\61\62\7\3\2\2\62\63\5\32\16\2\63\64\7\4\2\2\64;"+
		"\5\22\n\2\65\66\7\5\2\2\668\5\22\n\2\67\65\3\2\2\289\3\2\2\29\67\3\2\2"+
		"\29:\3\2\2\2:<\3\2\2\2;\67\3\2\2\2;<\3\2\2\2<=\3\2\2\2=>\7\6\2\2>\7\3"+
		"\2\2\2?@\7\7\2\2@A\5\36\20\2AB\7\b\2\2BC\5\32\16\2CD\5\20\t\2D\t\3\2\2"+
		"\2EF\7\t\2\2FG\5\36\20\2GH\7\b\2\2HI\5\32\16\2IJ\5\20\t\2J\13\3\2\2\2"+
		"KL\7\n\2\2LM\5\32\16\2MN\7\13\2\2NO\7\4\2\2OP\7\f\2\2PQ\5\36\20\2QR\7"+
		"\r\2\2RS\5\20\t\2ST\7\16\2\2T\r\3\2\2\2UV\7\n\2\2VW\5\32\16\2WX\7\13\2"+
		"\2XY\7\4\2\2YZ\7\f\2\2Z[\5\36\20\2[\\\7\17\2\2\\]\5\20\t\2]^\7\20\2\2"+
		"^`\5\32\16\2_a\7\21\2\2`_\3\2\2\2`a\3\2\2\2ab\3\2\2\2bc\7\16\2\2c\17\3"+
		"\2\2\2de\7\4\2\2eg\5\34\17\2fh\7\22\2\2gf\3\2\2\2gh\3\2\2\2hr\3\2\2\2"+
		"ij\7\5\2\2jl\5\34\17\2km\7\22\2\2lk\3\2\2\2lm\3\2\2\2mo\3\2\2\2ni\3\2"+
		"\2\2op\3\2\2\2pn\3\2\2\2pq\3\2\2\2qs\3\2\2\2rn\3\2\2\2rs\3\2\2\2su\3\2"+
		"\2\2tv\t\2\2\2ut\3\2\2\2uv\3\2\2\2v\21\3\2\2\2wx\5\34\17\2x{\5\"\22\2"+
		"y|\5\24\13\2z|\5\26\f\2{y\3\2\2\2{z\3\2\2\2|\23\3\2\2\2}~\7\24\2\2~\25"+
		"\3\2\2\2\177\u0080\7\25\2\2\u0080\27\3\2\2\2\u0081\u0082\7\37\2\2\u0082"+
		"\31\3\2\2\2\u0083\u0085\7 \2\2\u0084\u0086\t\3\2\2\u0085\u0084\3\2\2\2"+
		"\u0086\u0087\3\2\2\2\u0087\u0085\3\2\2\2\u0087\u0088\3\2\2\2\u0088\33"+
		"\3\2\2\2\u0089\u008b\7 \2\2\u008a\u008c\t\3\2\2\u008b\u008a\3\2\2\2\u008c"+
		"\u008d\3\2\2\2\u008d\u008b\3\2\2\2\u008d\u008e\3\2\2\2\u008e\35\3\2\2"+
		"\2\u008f\u0091\7 \2\2\u0090\u0092\t\3\2\2\u0091\u0090\3\2\2\2\u0092\u0093"+
		"\3\2\2\2\u0093\u0091\3\2\2\2\u0093\u0094\3\2\2\2\u0094\37\3\2\2\2\u0095"+
		"\u0097\t\4\2\2\u0096\u0095\3\2\2\2\u0097\u0098\3\2\2\2\u0098\u0096\3\2"+
		"\2\2\u0098\u0099\3\2\2\2\u0099!\3\2\2\2\u009a\u00ac\7\26\2\2\u009b\u009c"+
		"\7\27\2\2\u009c\u009d\7!\2\2\u009d\u009e\7\5\2\2\u009e\u009f\7!\2\2\u009f"+
		"\u00ac\7\23\2\2\u00a0\u00a1\7\30\2\2\u00a1\u00a2\7!\2\2\u00a2\u00ac\7"+
		"\23\2\2\u00a3\u00ac\7\31\2\2\u00a4\u00ac\7\32\2\2\u00a5\u00ac\7\33\2\2"+
		"\u00a6\u00a7\7\34\2\2\u00a7\u00a8\7!\2\2\u00a8\u00ac\7\23\2\2\u00a9\u00ac"+
		"\7\35\2\2\u00aa\u00ac\7\36\2\2\u00ab\u009a\3\2\2\2\u00ab\u009b\3\2\2\2"+
		"\u00ab\u00a0\3\2\2\2\u00ab\u00a3\3\2\2\2\u00ab\u00a4\3\2\2\2\u00ab\u00a5"+
		"\3\2\2\2\u00ab\u00a6\3\2\2\2\u00ab\u00a9\3\2\2\2\u00ab\u00aa\3\2\2\2\u00ac"+
		"#\3\2\2\2\22\'/9;`glpru{\u0087\u008d\u0093\u0098\u00ab";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}