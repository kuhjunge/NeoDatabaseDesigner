package de.mach.tools.neodesigner.database;

/**
 * ResultUnit Interface für die Datenbank Ergebnisse (Kapselung von Record)
 *
 * @author Chris Deter
 *
 */
interface ResultUnit {

  /**
   * gibt den Stringwert der gewählten Information zurück.
   *
   * @param bez
   *          Bezeichnung
   * @return String Value
   */
  String get(String bez);

  /**
   * gibt den booleischen Wert der gewählten Information zurück.
   *
   * @param bez
   *          Bezeichnung
   * @return String Value
   */
  Boolean getBoolean(String bez);

}