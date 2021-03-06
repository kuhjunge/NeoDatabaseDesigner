/* Copyright (C) 2018 Chris Deter Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the
 * Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions: The
 * above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE. */

package de.mach.tools.neodesigner.inex.legacyImport;


import java.util.List;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.inex.ImportTask;
import de.mach.tools.neodesigner.inex.nimport.SqlImportListener;
import de.mach.tools.neodesigner.inex.nimport.antlrsql.SQLLexer;
import de.mach.tools.neodesigner.inex.nimport.antlrsql.SQLParser;


/** Konkrete Implementation des SQL Import Tasks.
 *
 * @author Chris Deter */
public class ImportSqlPartialTask extends ImportTask {
  private final SqlImportListener sqlListener = new SqlImportListener();
  private final String input;

  /** Konstruktor.
   *
   * @param in der Input der Importiert werden soll */
  public ImportSqlPartialTask(final String in, List<Table> update) {
    super();
    input = in;
    tl = update;
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

  @Override
  protected void doImport() {
    List<Table> n = parse();
    for (Table t : tl) {
      int tblid = n.indexOf(t);
      if (tblid >= 0) {
        for (Field f : t.getFields()) {
          Field fn = n.get(tblid).getField(f.getName());
          if (fn != null) {
            f.setDomain(fn.getDomain());
            t.setOrder(f.getName(), n.get(tblid).getOrder(f.getName()));
          }
        }
      }
    }
  }
}
