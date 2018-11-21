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

package de.mach.tools.neodesigner.core.nimport;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import de.mach.tools.neodesigner.core.Strings;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.database.DatabaseConnection;
import de.mach.tools.neodesigner.database.cypher.DatabaseManagerLean;


/** Konkrete Umsetzung des Import Task für den Import von Kategorien.
 *
 * @author Chris Deter */
public class ImportBulkCsvTask extends ImportTask {
  private final String folder;
  private final String neo4jPath;

  public ImportBulkCsvTask(final DatabaseConnection db, final String importFolder, final String neo4jpath) {
    super(db);
    folder = importFolder;
    neo4jPath = neo4jpath;
  }

  @Override
  protected void doImport() {
    setMax(5);
    final String[] files = { "Tbls.csv", "cols.csv", "Fkeys.csv", "Idxs.csv", "PKeys.csv", "MetaTbls.csv",
                             "MetaCols.csv" };
    for (final String file : files) {
      final Path src = Paths.get(folder + File.separator + file);
      final Path dest = Paths.get(neo4jPath + File.separator + "import" + File.separator + file);
      copyFilesToNeo4jImport(src, dest);
    }
    updateProgressMessage(2, Strings.IMPORT_TABLES);
    final DatabaseConnection dbcon = getDatabaseCon();
    if (dbcon instanceof DatabaseManagerLean) {
      ((DatabaseManagerLean) dbcon).startCsvImport();
    }
    updateBar();
  }

  private void copyFilesToNeo4jImport(final Path src, final Path dest) {
    try {
      if (dest.toFile().exists()) {
        Files.delete(dest);
      }
      if (src.toFile().exists()) {
        Files.copy(src, dest);
      }
    }
    catch (final IOException e) {
      ImportTask.LOG.log(Level.SEVERE, "Could not Copy Files", e);
    }
  }

  @Override
  protected List<Table> parse() {
    return new ArrayList<>();
  }
}
