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

package de.mach.tools.neodesigner.inex.nexport;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;

import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.inex.DatabaseConnection;


/** Task schreiben von Daten in die NEO4J Datenbank.
 *
 * @author Chris Deter */
public class WriteToDbTask extends Task<Boolean> {
  private static final Logger LOG = Logger.getLogger(WriteToDbTask.class.getName());
  private final DatabaseConnection dbm;
  private final List<Table> tl;
  private final long start = new Date().getTime();
  private int max = 1;
  private int progress = 0;

  public WriteToDbTask(final DatabaseConnection databaseManager, final List<Table> lt) {
    dbm = databaseManager;
    this.tl = lt;
  }

  @Override
  protected Boolean call() {
    if (dbm.isReady()) {
      dbm.deleteDatabase();
      // Entscheidener Code aus Import Klasse
      max = tl.size() * 2;
      updateProgressMessage(0, "Tables");
      dbm.createIndexOnDb();
      for (final Table t : tl) {
        dbm.importTable(t);
        updateBar();
      }
      updateProgressMessage(tl.size(), "ForeignKeys");
      for (final Table t : tl) {
        final List<ForeignKey> lfk = new ArrayList<>(t.getForeignKeys());
        for (final ForeignKey i : lfk) {
          dbm.importForeignKey(i);
        }
        updateBar();
      }
    }
    return true;
  }

  /** Setzt den Ladebalken einen weiter. */
  private void updateBar() {
    updateProgress(progress++, max);
  }

  /** Setzt die Nachricht und den Ladebalken.
   *
   * @param progress progress als Int
   * @param msg Fortschrittsmeldung */
  private void updateProgressMessage(final int progress, final String msg) {
    updateProgress(progress, max);
    updateMessage(msg);
    LOG.log(Level.FINE, () -> msg + (new Date().getTime() - start));
  }
}
