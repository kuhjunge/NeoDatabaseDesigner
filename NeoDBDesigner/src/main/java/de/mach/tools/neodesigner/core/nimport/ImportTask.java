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

import de.mach.tools.neodesigner.core.Strings;
import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.database.DatabaseConnection;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javafx.concurrent.Task;

/**
 * Von dieser abstrakten Klasse werden konkrete Import Task abgeleitet Bietet
 * das Framework für das Importieren von Informationen.
 *
 * @author Chris Deter
 *
 */
public abstract class ImportTask extends Task<Boolean> {

  private final DatabaseConnection dbcon;
  private final long start = new Date().getTime();
  private int max = 100;
  private int progress = 0;
  private boolean isTask = false;
  private static final Logger LOG = Logger.getLogger(ImportTask.class.getName());

  /**
   * der Konstruktor.
   *
   * @param db
   *          die Datenbankverbindung
   * @param in
   *          der Input
   */
  ImportTask(final DatabaseConnection db) {
    dbcon = db;
  }

  @Override
  protected Boolean call() throws Exception {
    isTask = true;
    return startImport();
  }

  /**
   * startet den Importvorgang wenn die Datenbank online ist.
   *
   * @return True wenn Erfolgreich sonst false
   */
  public Boolean startImport() {
    updateProgressMessage(0, Strings.IMPORT_PARSING);
    if (dbcon.isReady()) {
      doImport();
      updateProgressMessage(max, String.format(Strings.IMPORT_FINISHED, (new Date().getTime() - start) / 1000 + 1));
      return true;
    } else {
      updateProgressMessage(0, String.format(Strings.IMPORT_FAILED, (new Date().getTime() - start) / 1000));
      return false;
    }
  }

  /**
   * gibt die Datenbankverbindung zurück.
   *
   * @return die Databasse Connection
   */
  protected DatabaseConnection getDatabaseCon() {
    return dbcon;
  }

  /**
   * Setzt das Maxium des Ladebalkens.
   *
   * @param max
   *          Integer Maximalwert
   */
  protected void setMax(final int max) {
    this.max = max;
  }

  /**
   * gibt das Maxium des Ladebalkens zurück.
   *
   * @return Maximum Ladebalken als INT
   */
  private int getMax() {
    return max;
  }

  /**
   * Führt den Import durch.
   *
   * @param input
   *          Input Information
   */
  protected void doImport() {
    final List<Table> tl = parse();
    setMax(tl.size() * 2);
    updateProgressMessage(0, Strings.IMPORT_TABLES);
    getDatabaseCon().createIndexOnDb();
    for (final Table t : tl) {
      getDatabaseCon().importTable(t);
      updateBar();
    }
    updateProgressMessage(tl.size(), Strings.IMPORT_FOREIGNKEYS);
    for (final Table t : tl) {
      for (final ForeignKey i : t.getForeignKeys()) {
        getDatabaseCon().importForeignKey(i);
      }
      updateBar();
    }
    updateProgressMessage(getMax(), Strings.IMPORT_CLEANUP);
    getDatabaseCon().disconnectDb();
  }

  /**
   * Diese Methode baut mithilfe von ANTLR einen Parse Tree und wandelt diesen
   * dann in eine Liste von Tabellen um.
   *
   * @return eine Liste mit Tabellen
   */
  protected abstract List<Table> parse();

  /**
   * Setzt den Ladebalken einen weiter.
   */
  protected void updateBar() {
    if (isTask) {
      updateProgress(progress++, max);
    }
  }

  /**
   * Setzt die Nachricht und den Ladebalken.
   *
   * @param progress
   *          progress als Int
   * @param msg
   *          Fortschrittsmeldung
   */
  protected void updateProgressMessage(final int progress, final String msg) {
    if (isTask) {
      updateProgress(progress, max);
      updateMessage(msg);
      ImportTask.LOG.info(() -> msg + (new Date().getTime() - start));
    }
  }
}
