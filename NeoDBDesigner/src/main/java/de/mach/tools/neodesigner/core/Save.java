package de.mach.tools.neodesigner.core;

import de.mach.tools.neodesigner.database.SaveDatabase;

/**
 * Save Interface f�r das Model, wird ben�tigt um mit der GUI zu kommunizieren
 * 
 * @author Chris Deter
 *
 */
public interface Save extends SaveDatabase {

  /**
   * Gibt die Information zur�ck ob eine Tabelle im Datenmodel vorhanden ist.
   *
   * @param name
   *          der Tabelle
   * @return True wenn die Tabelle vorhanden ist
   */
  boolean hasTable(String name);
}
