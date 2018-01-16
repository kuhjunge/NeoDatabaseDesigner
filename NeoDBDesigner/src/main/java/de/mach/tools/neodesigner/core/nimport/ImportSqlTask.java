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

package de.mach.tools.neodesigner.core.nimport;

import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.core.nimport.antlrsql.SQLLexer;
import de.mach.tools.neodesigner.core.nimport.antlrsql.SQLParser;
import de.mach.tools.neodesigner.database.DatabaseConnection;

import java.util.List;

import org.antlr.v4.runtime.CharStreams;
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
  private final String input;

  /**
   * Konstruktor.
   *
   * @param db
   *          die Datenbank
   * @param in
   *          der Input der Importiert werden soll
   */
  public ImportSqlTask(final DatabaseConnection db, final String in) {
    super(db);
    input = in;
  }

  @Override
  protected List<Table> parse() {
    final Lexer lexer = new SQLLexer(CharStreams.fromString(input));
    final CommonTokenStream tokens = new CommonTokenStream(lexer);
    final SQLParser parser = new SQLParser(tokens);
    parser.setBuildParseTree(true); // tell ANTLR to build a parse tree
    final ParseTree tree = parser.parseAll(); // parse
    final ParseTreeWalker walker = new ParseTreeWalker();
    walker.walk(sqlListener, tree);
    return sqlListener.tables;
  }
}
