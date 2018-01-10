package de.mach.tools.neodesigner.core;

import de.mach.tools.neodesigner.database.SaveDatabase;

/**
 * Save Interface für das Model, wird benötigt um mit der GUI zu kommunizieren
 * 
 * @author Chris Deter
 *
 */
public interface Save extends SaveDatabase {

  /**
   * Gibt die Information zurück ob eine Tabelle im Datenmodel vorhanden ist.
   *
   * @param name
   *          der Tabelle
   * @return True wenn die Tabelle vorhanden ist
   */
  boolean hasTable(String name);
}
