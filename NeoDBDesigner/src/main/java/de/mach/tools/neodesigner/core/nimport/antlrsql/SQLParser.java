// Generated from SQL.g4 by ANTLR 4.6
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
	static { RuntimeMetaData.checkVersion("4.6", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, T__20=21, T__21=22, T__22=23, T__23=24, 
		T__24=25, T__25=26, T__26=27, T__27=28, SINGLE_LINE_COMMENT=29, ID=30, 
		INT=31, WS=32;
	public static final int
		RULE_parseAll = 0, RULE_parse = 1, RULE_createTable = 2, RULE_createIndex = 3, 
		RULE_createUniqueIndex = 4, RULE_primKey = 5, RULE_foreignKey = 6, RULE_fieldNameList = 7, 
		RULE_createFieldName = 8, RULE_isNull = 9, RULE_isNotNull = 10, RULE_tablename = 11, 
		RULE_fieldname = 12, RULE_indexname = 13, RULE_type = 14;
	public static final String[] ruleNames = {
		"parseAll", "parse", "createTable", "createIndex", "createUniqueIndex", 
		"primKey", "foreignKey", "fieldNameList", "createFieldName", "isNull", 
		"isNotNull", "tablename", "fieldname", "indexname", "type"
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
		null, null, null, null, null, "SINGLE_LINE_COMMENT", "ID", "INT", "WS"
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
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitParseAll(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParseAllContext parseAll() throws RecognitionException {
		ParseAllContext _localctx = new ParseAllContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_parseAll);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(31); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(30);
				parse();
				}
				}
				setState(33); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__4) | (1L << T__6) | (1L << T__7))) != 0) );
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
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitParse(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParseContext parse() throws RecognitionException {
		ParseContext _localctx = new ParseContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_parse);
		try {
			setState(40);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(35);
				createTable();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(36);
				createIndex();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(37);
				createUniqueIndex();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(38);
				primKey();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(39);
				foreignKey();
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
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitCreTable(this);
			else return visitor.visitChildren(this);
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
			setState(42);
			match(T__0);
			setState(43);
			tablename();
			setState(44);
			match(T__1);
			setState(45);
			createFieldName();
			setState(52);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__2) {
				{
				setState(48); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(46);
					match(T__2);
					setState(47);
					createFieldName();
					}
					}
					setState(50); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__2 );
				}
			}

			setState(54);
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
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitCreIndex(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CreateIndexContext createIndex() throws RecognitionException {
		CreateIndexContext _localctx = new CreateIndexContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_createIndex);
		try {
			_localctx = new CreIndexContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(56);
			match(T__4);
			setState(57);
			indexname();
			setState(58);
			match(T__5);
			setState(59);
			tablename();
			setState(60);
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
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitCreUniqueIndex(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CreateUniqueIndexContext createUniqueIndex() throws RecognitionException {
		CreateUniqueIndexContext _localctx = new CreateUniqueIndexContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_createUniqueIndex);
		try {
			_localctx = new CreUniqueIndexContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(62);
			match(T__6);
			setState(63);
			indexname();
			setState(64);
			match(T__5);
			setState(65);
			tablename();
			setState(66);
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
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitCrePrimKey(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PrimKeyContext primKey() throws RecognitionException {
		PrimKeyContext _localctx = new PrimKeyContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_primKey);
		try {
			_localctx = new CrePrimKeyContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(68);
			match(T__7);
			setState(69);
			tablename();
			setState(70);
			match(T__8);
			setState(71);
			match(T__1);
			setState(72);
			match(T__9);
			setState(73);
			indexname();
			setState(74);
			match(T__10);
			setState(75);
			fieldNameList();
			setState(76);
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
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitCreForeignKey(this);
			else return visitor.visitChildren(this);
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
			setState(78);
			match(T__7);
			setState(79);
			tablename();
			setState(80);
			match(T__8);
			setState(81);
			match(T__1);
			setState(82);
			match(T__9);
			setState(83);
			indexname();
			setState(84);
			match(T__12);
			setState(85);
			fieldNameList();
			setState(86);
			match(T__13);
			setState(87);
			tablename();
			setState(89);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__14) {
				{
				setState(88);
				match(T__14);
				}
			}

			setState(91);
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
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitCrefieldNameList(this);
			else return visitor.visitChildren(this);
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
			setState(93);
			match(T__1);
			setState(94);
			fieldname();
			setState(96);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__15) {
				{
				setState(95);
				match(T__15);
				}
			}

			setState(107);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__2) {
				{
				setState(103); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(98);
					match(T__2);
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

					}
					}
					setState(105); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__2 );
				}
			}

			setState(110);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__3 || _la==T__16) {
				{
				setState(109);
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
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitCreField(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CreateFieldNameContext createFieldName() throws RecognitionException {
		CreateFieldNameContext _localctx = new CreateFieldNameContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_createFieldName);
		try {
			_localctx = new CreFieldContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(112);
			fieldname();
			setState(113);
			type();
			setState(116);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__17:
				{
				setState(114);
				isNull();
				}
				break;
			case T__18:
				{
				setState(115);
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
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitIsNull(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IsNullContext isNull() throws RecognitionException {
		IsNullContext _localctx = new IsNullContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_isNull);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(118);
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
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitIsNotNull(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IsNotNullContext isNotNull() throws RecognitionException {
		IsNotNullContext _localctx = new IsNotNullContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_isNotNull);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(120);
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
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitTablename(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TablenameContext tablename() throws RecognitionException {
		TablenameContext _localctx = new TablenameContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_tablename);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(122);
			match(ID);
			setState(124); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(123);
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
				setState(126); 
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
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitFieldname(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FieldnameContext fieldname() throws RecognitionException {
		FieldnameContext _localctx = new FieldnameContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_fieldname);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(128);
			match(ID);
			setState(130); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(129);
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
				setState(132); 
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
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitIndexname(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IndexnameContext indexname() throws RecognitionException {
		IndexnameContext _localctx = new IndexnameContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_indexname);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(134);
			match(ID);
			setState(136); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(135);
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
				setState(138); 
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
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeContext type() throws RecognitionException {
		TypeContext _localctx = new TypeContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_type);
		try {
			setState(157);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__19:
				enterOuterAlt(_localctx, 1);
				{
				setState(140);
				match(T__19);
				}
				break;
			case T__20:
				enterOuterAlt(_localctx, 2);
				{
				setState(141);
				match(T__20);
				setState(142);
				match(INT);
				setState(143);
				match(T__2);
				setState(144);
				match(INT);
				setState(145);
				match(T__16);
				}
				break;
			case T__21:
				enterOuterAlt(_localctx, 3);
				{
				setState(146);
				match(T__21);
				setState(147);
				match(INT);
				setState(148);
				match(T__16);
				}
				break;
			case T__22:
				enterOuterAlt(_localctx, 4);
				{
				setState(149);
				match(T__22);
				}
				break;
			case T__23:
				enterOuterAlt(_localctx, 5);
				{
				setState(150);
				match(T__23);
				}
				break;
			case T__24:
				enterOuterAlt(_localctx, 6);
				{
				setState(151);
				match(T__24);
				}
				break;
			case T__25:
				enterOuterAlt(_localctx, 7);
				{
				setState(152);
				match(T__25);
				setState(153);
				match(INT);
				setState(154);
				match(T__16);
				}
				break;
			case T__26:
				enterOuterAlt(_localctx, 8);
				{
				setState(155);
				match(T__26);
				}
				break;
			case T__27:
				enterOuterAlt(_localctx, 9);
				{
				setState(156);
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
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\"\u00a2\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\3\2\6\2\"\n\2\r\2\16"+
		"\2#\3\3\3\3\3\3\3\3\3\3\5\3+\n\3\3\4\3\4\3\4\3\4\3\4\3\4\6\4\63\n\4\r"+
		"\4\16\4\64\5\4\67\n\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6"+
		"\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3"+
		"\b\3\b\3\b\3\b\3\b\3\b\5\b\\\n\b\3\b\3\b\3\t\3\t\3\t\5\tc\n\t\3\t\3\t"+
		"\3\t\5\th\n\t\6\tj\n\t\r\t\16\tk\5\tn\n\t\3\t\5\tq\n\t\3\n\3\n\3\n\3\n"+
		"\5\nw\n\n\3\13\3\13\3\f\3\f\3\r\3\r\6\r\177\n\r\r\r\16\r\u0080\3\16\3"+
		"\16\6\16\u0085\n\16\r\16\16\16\u0086\3\17\3\17\6\17\u008b\n\17\r\17\16"+
		"\17\u008c\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20"+
		"\3\20\3\20\3\20\3\20\3\20\5\20\u00a0\n\20\3\20\2\2\21\2\4\6\b\n\f\16\20"+
		"\22\24\26\30\32\34\36\2\4\4\2\6\6\23\23\3\2 !\u00ab\2!\3\2\2\2\4*\3\2"+
		"\2\2\6,\3\2\2\2\b:\3\2\2\2\n@\3\2\2\2\fF\3\2\2\2\16P\3\2\2\2\20_\3\2\2"+
		"\2\22r\3\2\2\2\24x\3\2\2\2\26z\3\2\2\2\30|\3\2\2\2\32\u0082\3\2\2\2\34"+
		"\u0088\3\2\2\2\36\u009f\3\2\2\2 \"\5\4\3\2! \3\2\2\2\"#\3\2\2\2#!\3\2"+
		"\2\2#$\3\2\2\2$\3\3\2\2\2%+\5\6\4\2&+\5\b\5\2\'+\5\n\6\2(+\5\f\7\2)+\5"+
		"\16\b\2*%\3\2\2\2*&\3\2\2\2*\'\3\2\2\2*(\3\2\2\2*)\3\2\2\2+\5\3\2\2\2"+
		",-\7\3\2\2-.\5\30\r\2./\7\4\2\2/\66\5\22\n\2\60\61\7\5\2\2\61\63\5\22"+
		"\n\2\62\60\3\2\2\2\63\64\3\2\2\2\64\62\3\2\2\2\64\65\3\2\2\2\65\67\3\2"+
		"\2\2\66\62\3\2\2\2\66\67\3\2\2\2\678\3\2\2\289\7\6\2\29\7\3\2\2\2:;\7"+
		"\7\2\2;<\5\34\17\2<=\7\b\2\2=>\5\30\r\2>?\5\20\t\2?\t\3\2\2\2@A\7\t\2"+
		"\2AB\5\34\17\2BC\7\b\2\2CD\5\30\r\2DE\5\20\t\2E\13\3\2\2\2FG\7\n\2\2G"+
		"H\5\30\r\2HI\7\13\2\2IJ\7\4\2\2JK\7\f\2\2KL\5\34\17\2LM\7\r\2\2MN\5\20"+
		"\t\2NO\7\16\2\2O\r\3\2\2\2PQ\7\n\2\2QR\5\30\r\2RS\7\13\2\2ST\7\4\2\2T"+
		"U\7\f\2\2UV\5\34\17\2VW\7\17\2\2WX\5\20\t\2XY\7\20\2\2Y[\5\30\r\2Z\\\7"+
		"\21\2\2[Z\3\2\2\2[\\\3\2\2\2\\]\3\2\2\2]^\7\16\2\2^\17\3\2\2\2_`\7\4\2"+
		"\2`b\5\32\16\2ac\7\22\2\2ba\3\2\2\2bc\3\2\2\2cm\3\2\2\2de\7\5\2\2eg\5"+
		"\32\16\2fh\7\22\2\2gf\3\2\2\2gh\3\2\2\2hj\3\2\2\2id\3\2\2\2jk\3\2\2\2"+
		"ki\3\2\2\2kl\3\2\2\2ln\3\2\2\2mi\3\2\2\2mn\3\2\2\2np\3\2\2\2oq\t\2\2\2"+
		"po\3\2\2\2pq\3\2\2\2q\21\3\2\2\2rs\5\32\16\2sv\5\36\20\2tw\5\24\13\2u"+
		"w\5\26\f\2vt\3\2\2\2vu\3\2\2\2w\23\3\2\2\2xy\7\24\2\2y\25\3\2\2\2z{\7"+
		"\25\2\2{\27\3\2\2\2|~\7 \2\2}\177\t\3\2\2~}\3\2\2\2\177\u0080\3\2\2\2"+
		"\u0080~\3\2\2\2\u0080\u0081\3\2\2\2\u0081\31\3\2\2\2\u0082\u0084\7 \2"+
		"\2\u0083\u0085\t\3\2\2\u0084\u0083\3\2\2\2\u0085\u0086\3\2\2\2\u0086\u0084"+
		"\3\2\2\2\u0086\u0087\3\2\2\2\u0087\33\3\2\2\2\u0088\u008a\7 \2\2\u0089"+
		"\u008b\t\3\2\2\u008a\u0089\3\2\2\2\u008b\u008c\3\2\2\2\u008c\u008a\3\2"+
		"\2\2\u008c\u008d\3\2\2\2\u008d\35\3\2\2\2\u008e\u00a0\7\26\2\2\u008f\u0090"+
		"\7\27\2\2\u0090\u0091\7!\2\2\u0091\u0092\7\5\2\2\u0092\u0093\7!\2\2\u0093"+
		"\u00a0\7\23\2\2\u0094\u0095\7\30\2\2\u0095\u0096\7!\2\2\u0096\u00a0\7"+
		"\23\2\2\u0097\u00a0\7\31\2\2\u0098\u00a0\7\32\2\2\u0099\u00a0\7\33\2\2"+
		"\u009a\u009b\7\34\2\2\u009b\u009c\7!\2\2\u009c\u00a0\7\23\2\2\u009d\u00a0"+
		"\7\35\2\2\u009e\u00a0\7\36\2\2\u009f\u008e\3\2\2\2\u009f\u008f\3\2\2\2"+
		"\u009f\u0094\3\2\2\2\u009f\u0097\3\2\2\2\u009f\u0098\3\2\2\2\u009f\u0099"+
		"\3\2\2\2\u009f\u009a\3\2\2\2\u009f\u009d\3\2\2\2\u009f\u009e\3\2\2\2\u00a0"+
		"\37\3\2\2\2\21#*\64\66[bgkmpv\u0080\u0086\u008c\u009f";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}