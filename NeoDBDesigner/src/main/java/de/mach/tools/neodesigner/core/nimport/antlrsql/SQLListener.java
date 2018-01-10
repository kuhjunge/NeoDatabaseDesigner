// Generated from SQL.g4 by ANTLR 4.6
package de.mach.tools.neodesigner.core.nimport.antlrsql;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link SQLParser}.
 */
public interface SQLListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link SQLParser#parseAll}.
	 * @param ctx the parse tree
	 */
	void enterParseAll(SQLParser.ParseAllContext ctx);
	/**
	 * Exit a parse tree produced by {@link SQLParser#parseAll}.
	 * @param ctx the parse tree
	 */
	void exitParseAll(SQLParser.ParseAllContext ctx);
	/**
	 * Enter a parse tree produced by {@link SQLParser#parse}.
	 * @param ctx the parse tree
	 */
	void enterParse(SQLParser.ParseContext ctx);
	/**
	 * Exit a parse tree produced by {@link SQLParser#parse}.
	 * @param ctx the parse tree
	 */
	void exitParse(SQLParser.ParseContext ctx);
	/**
	 * Enter a parse tree produced by the {@code creTable}
	 * labeled alternative in {@link SQLParser#createTable}.
	 * @param ctx the parse tree
	 */
	void enterCreTable(SQLParser.CreTableContext ctx);
	/**
	 * Exit a parse tree produced by the {@code creTable}
	 * labeled alternative in {@link SQLParser#createTable}.
	 * @param ctx the parse tree
	 */
	void exitCreTable(SQLParser.CreTableContext ctx);
	/**
	 * Enter a parse tree produced by the {@code creIndex}
	 * labeled alternative in {@link SQLParser#createIndex}.
	 * @param ctx the parse tree
	 */
	void enterCreIndex(SQLParser.CreIndexContext ctx);
	/**
	 * Exit a parse tree produced by the {@code creIndex}
	 * labeled alternative in {@link SQLParser#createIndex}.
	 * @param ctx the parse tree
	 */
	void exitCreIndex(SQLParser.CreIndexContext ctx);
	/**
	 * Enter a parse tree produced by the {@code creUniqueIndex}
	 * labeled alternative in {@link SQLParser#createUniqueIndex}.
	 * @param ctx the parse tree
	 */
	void enterCreUniqueIndex(SQLParser.CreUniqueIndexContext ctx);
	/**
	 * Exit a parse tree produced by the {@code creUniqueIndex}
	 * labeled alternative in {@link SQLParser#createUniqueIndex}.
	 * @param ctx the parse tree
	 */
	void exitCreUniqueIndex(SQLParser.CreUniqueIndexContext ctx);
	/**
	 * Enter a parse tree produced by the {@code crePrimKey}
	 * labeled alternative in {@link SQLParser#primKey}.
	 * @param ctx the parse tree
	 */
	void enterCrePrimKey(SQLParser.CrePrimKeyContext ctx);
	/**
	 * Exit a parse tree produced by the {@code crePrimKey}
	 * labeled alternative in {@link SQLParser#primKey}.
	 * @param ctx the parse tree
	 */
	void exitCrePrimKey(SQLParser.CrePrimKeyContext ctx);
	/**
	 * Enter a parse tree produced by the {@code creForeignKey}
	 * labeled alternative in {@link SQLParser#foreignKey}.
	 * @param ctx the parse tree
	 */
	void enterCreForeignKey(SQLParser.CreForeignKeyContext ctx);
	/**
	 * Exit a parse tree produced by the {@code creForeignKey}
	 * labeled alternative in {@link SQLParser#foreignKey}.
	 * @param ctx the parse tree
	 */
	void exitCreForeignKey(SQLParser.CreForeignKeyContext ctx);
	/**
	 * Enter a parse tree produced by the {@code crefieldNameList}
	 * labeled alternative in {@link SQLParser#fieldNameList}.
	 * @param ctx the parse tree
	 */
	void enterCrefieldNameList(SQLParser.CrefieldNameListContext ctx);
	/**
	 * Exit a parse tree produced by the {@code crefieldNameList}
	 * labeled alternative in {@link SQLParser#fieldNameList}.
	 * @param ctx the parse tree
	 */
	void exitCrefieldNameList(SQLParser.CrefieldNameListContext ctx);
	/**
	 * Enter a parse tree produced by the {@code creField}
	 * labeled alternative in {@link SQLParser#createFieldName}.
	 * @param ctx the parse tree
	 */
	void enterCreField(SQLParser.CreFieldContext ctx);
	/**
	 * Exit a parse tree produced by the {@code creField}
	 * labeled alternative in {@link SQLParser#createFieldName}.
	 * @param ctx the parse tree
	 */
	void exitCreField(SQLParser.CreFieldContext ctx);
	/**
	 * Enter a parse tree produced by {@link SQLParser#isNull}.
	 * @param ctx the parse tree
	 */
	void enterIsNull(SQLParser.IsNullContext ctx);
	/**
	 * Exit a parse tree produced by {@link SQLParser#isNull}.
	 * @param ctx the parse tree
	 */
	void exitIsNull(SQLParser.IsNullContext ctx);
	/**
	 * Enter a parse tree produced by {@link SQLParser#isNotNull}.
	 * @param ctx the parse tree
	 */
	void enterIsNotNull(SQLParser.IsNotNullContext ctx);
	/**
	 * Exit a parse tree produced by {@link SQLParser#isNotNull}.
	 * @param ctx the parse tree
	 */
	void exitIsNotNull(SQLParser.IsNotNullContext ctx);
	/**
	 * Enter a parse tree produced by {@link SQLParser#tablename}.
	 * @param ctx the parse tree
	 */
	void enterTablename(SQLParser.TablenameContext ctx);
	/**
	 * Exit a parse tree produced by {@link SQLParser#tablename}.
	 * @param ctx the parse tree
	 */
	void exitTablename(SQLParser.TablenameContext ctx);
	/**
	 * Enter a parse tree produced by {@link SQLParser#fieldname}.
	 * @param ctx the parse tree
	 */
	void enterFieldname(SQLParser.FieldnameContext ctx);
	/**
	 * Exit a parse tree produced by {@link SQLParser#fieldname}.
	 * @param ctx the parse tree
	 */
	void exitFieldname(SQLParser.FieldnameContext ctx);
	/**
	 * Enter a parse tree produced by {@link SQLParser#indexname}.
	 * @param ctx the parse tree
	 */
	void enterIndexname(SQLParser.IndexnameContext ctx);
	/**
	 * Exit a parse tree produced by {@link SQLParser#indexname}.
	 * @param ctx the parse tree
	 */
	void exitIndexname(SQLParser.IndexnameContext ctx);
	/**
	 * Enter a parse tree produced by {@link SQLParser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(SQLParser.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link SQLParser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(SQLParser.TypeContext ctx);
}