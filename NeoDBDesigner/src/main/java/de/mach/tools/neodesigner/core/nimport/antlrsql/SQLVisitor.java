// Generated from SQL.g4 by ANTLR 4.6
package de.mach.tools.neodesigner.core.nimport.antlrsql;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link SQLParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface SQLVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link SQLParser#parseAll}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParseAll(SQLParser.ParseAllContext ctx);
	/**
	 * Visit a parse tree produced by {@link SQLParser#parse}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParse(SQLParser.ParseContext ctx);
	/**
	 * Visit a parse tree produced by the {@code creTable}
	 * labeled alternative in {@link SQLParser#createTable}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCreTable(SQLParser.CreTableContext ctx);
	/**
	 * Visit a parse tree produced by the {@code creIndex}
	 * labeled alternative in {@link SQLParser#createIndex}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCreIndex(SQLParser.CreIndexContext ctx);
	/**
	 * Visit a parse tree produced by the {@code creUniqueIndex}
	 * labeled alternative in {@link SQLParser#createUniqueIndex}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCreUniqueIndex(SQLParser.CreUniqueIndexContext ctx);
	/**
	 * Visit a parse tree produced by the {@code crePrimKey}
	 * labeled alternative in {@link SQLParser#primKey}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCrePrimKey(SQLParser.CrePrimKeyContext ctx);
	/**
	 * Visit a parse tree produced by the {@code creForeignKey}
	 * labeled alternative in {@link SQLParser#foreignKey}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCreForeignKey(SQLParser.CreForeignKeyContext ctx);
	/**
	 * Visit a parse tree produced by the {@code crefieldNameList}
	 * labeled alternative in {@link SQLParser#fieldNameList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCrefieldNameList(SQLParser.CrefieldNameListContext ctx);
	/**
	 * Visit a parse tree produced by the {@code creField}
	 * labeled alternative in {@link SQLParser#createFieldName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCreField(SQLParser.CreFieldContext ctx);
	/**
	 * Visit a parse tree produced by {@link SQLParser#isNull}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIsNull(SQLParser.IsNullContext ctx);
	/**
	 * Visit a parse tree produced by {@link SQLParser#isNotNull}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIsNotNull(SQLParser.IsNotNullContext ctx);
	/**
	 * Visit a parse tree produced by {@link SQLParser#tablename}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTablename(SQLParser.TablenameContext ctx);
	/**
	 * Visit a parse tree produced by {@link SQLParser#fieldname}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFieldname(SQLParser.FieldnameContext ctx);
	/**
	 * Visit a parse tree produced by {@link SQLParser#indexname}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIndexname(SQLParser.IndexnameContext ctx);
	/**
	 * Visit a parse tree produced by {@link SQLParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType(SQLParser.TypeContext ctx);
}