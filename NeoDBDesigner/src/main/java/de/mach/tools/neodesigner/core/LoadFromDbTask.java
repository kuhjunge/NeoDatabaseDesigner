package de.mach.tools.neodesigner.core;

import java.util.Date;
import java.util.logging.Logger;

import javafx.concurrent.Task;

/**
 * Task zum Laden von den Daten aus der Datenbank
 * 
 * @author Chris Deter
 *
 */
public class LoadFromDbTask extends Task<Boolean> {

  private final Model dm;
  private final long start = new Date().getTime();
  private static final Logger LOG = Logger.getLogger(LoadFromDbTask.class.getName());

  /**
   * Konstruktor
   * 
   * @param db
   *          die Datenbankinstanz
   */
  LoadFromDbTask(final Model db) {
    dm = db;
  }

  @Override
  protected Boolean call() throws Exception {
    if (dm.isOnline()) {
      dm.getAllTables();
      LoadFromDbTask.LOG.info(() -> Strings.LOADFROMDB + (new Date().getTime() - start));
      return true;
    } else {
      return false;
    }
  }
}
