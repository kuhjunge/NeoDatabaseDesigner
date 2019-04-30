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

package de.mach.tools.neodesigner.inex;


import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;

import de.mach.tools.neodesigner.core.Strings;
import de.mach.tools.neodesigner.core.datamodel.Table;


/** Von dieser abstrakten Klasse werden konkrete Import Task abgeleitet Bietet das Framework für das Importieren von
 * Informationen.
 *
 * @author Chris Deter */
public abstract class ImportTask extends Task<Boolean> {

  protected static final Logger LOG = Logger.getLogger(ImportTask.class.getName());
  protected List<Table> tl = null;
  private final long start = new Date().getTime();
  private int max = 100;
  private boolean isTask = false;

  @Override
  protected Boolean call() {
    isTask = true;
    return startImport();
  }

  /** Führt den Import durch. */
  protected void doImport() {
    tl = parse();
  }

  public List<Table> getList() {
    return tl;
  }

  /** Diese Methode baut mithilfe von ANTLR einen Parse Tree und wandelt diesen dann in eine Liste von Tabellen um.
   *
   * @return eine Liste mit Tabellen */
  protected abstract List<Table> parse();

  /** startet den Importvorgang wenn die Datenbank online ist.
   *
   * @return True wenn Erfolgreich sonst false */
  public Boolean startImport() {
    updateProgressMessage(0, Strings.IMPORT_PARSING);
    doImport();
    updateProgressMessage(max, String.format(Strings.IMPORT_FINISHED, (new Date().getTime() - start) / 1000 + 1));
    return true;
  }

  /** Setzt die Nachricht und den Ladebalken.
   *
   * @param progress progress als Int
   * @param msg Fortschrittsmeldung */
  protected void updateProgressMessage(final int progress, final String msg) {
    if (isTask) {
      updateProgress(progress, max);
      updateMessage(msg);
      ImportTask.LOG.log(Level.FINE, () -> msg + (new Date().getTime() - start));
    }
  }
}
