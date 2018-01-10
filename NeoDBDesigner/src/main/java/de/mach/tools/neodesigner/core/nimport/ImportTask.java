package de.mach.tools.neodesigner.core.nimport;

import de.mach.tools.neodesigner.database.DatabaseConnection;

import java.util.Date;
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
  private final String input;
  private final long start = new Date().getTime();
  private int max = 100;
  private int progress = 0;
  private boolean isTask = false;
  private static final Logger LOG = Logger.getLogger(ImportTask.class.getName());

  /**
   * der Konstruktor
   * 
   * @param db
   * @param in
   */
  ImportTask(final DatabaseConnection db, final String in) {
    dbcon = db;
    input = in;
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
    updateProgressMessage(0, "start parsing");
    if (dbcon.isOnline()) {
      doImport(input);
      updateProgressMessage(max, "finished Import (" + ((new Date().getTime() - start) / 1000 + 1) + " s)");
      return true;
    } else {
      updateProgressMessage(0, "no Import Database! (" + (new Date().getTime() - start) / 1000 + " s)");
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
  protected int getMax() {
    return max;
  }

  /**
   * Führt den Import durch.
   *
   * @param input
   *          Input Information
   */
  protected abstract void doImport(String input);

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
