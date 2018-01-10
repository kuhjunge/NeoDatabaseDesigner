package de.mach.tools.neodesigner.core.nimport;

import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.core.nimport.antlrsql.SQLLexer;
import de.mach.tools.neodesigner.core.nimport.antlrsql.SQLParser;
import de.mach.tools.neodesigner.database.DatabaseConnection;

import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

/**
 * Konkrete Implementation des SQL Import Tasks.
 *
 * @author Chris Deter
 *
 */
public class ImportSqlTask extends ImportTask {
  private final SqlImportListener sqlListener = new SqlImportListener();

  /**
   * Konstruktor.
   * 
   * @param db
   *          die Datenbank
   * @param in
   *          der Input der Importiert werden soll
   */
  public ImportSqlTask(final DatabaseConnection db, final String in) {
    super(db, in);
  }

  @Override
  protected void doImport(final String input) {
    final List<Table> tl = parse(input);
    setMax(tl.size() * 2);
    updateProgressMessage(0, "writing tables");
    getDatabaseCon().createIndexOnDb();
    for (final Table t : tl) {
      getDatabaseCon().importTable(t);
      updateBar();
    }
    updateProgressMessage(tl.size(), "writing ForeignKeys");
    getDatabaseCon().createIndexOnDb();
    for (final Table t : tl) {
      for (final ForeignKey i : t.getForeignKeys()) {
        getDatabaseCon().importForeignKey(i);
      }
      updateBar();
    }
    updateProgressMessage(getMax(), "cleanup");
    getDatabaseCon().disconnectDb();
  }

  /**
   * Diese Methode baut mithilfe von ANTLR einen Parse Tree und wandelt diesen
   * dann in eine Liste von Tabellen um.
   *
   * @param s
   *          der Eingabestring
   * @return eine Liste mit Tabellen
   */
  private List<Table> parse(final String s) {
    final ANTLRInputStream input = new ANTLRInputStream(s.toCharArray(), s.length());
    final Lexer lexer = new SQLLexer(input);
    final CommonTokenStream tokens = new CommonTokenStream(lexer);
    final SQLParser parser = new SQLParser(tokens);
    parser.setBuildParseTree(true); // tell ANTLR to build a parse tree
    final ParseTree tree = parser.parseAll(); // parse
    final ParseTreeWalker walker = new ParseTreeWalker();
    walker.walk(sqlListener, tree);
    return sqlListener.tables;
  }
}
