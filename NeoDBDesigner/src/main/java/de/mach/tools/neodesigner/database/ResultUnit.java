package de.mach.tools.neodesigner.database;

/**
 * ResultUnit Interface f�r die Datenbank Ergebnisse (Kapselung von Record)
 *
 * @author Chris Deter
 *
 */
interface ResultUnit {

  /**
   * gibt den Stringwert der gew�hlten Information zur�ck.
   *
   * @param bez
   *          Bezeichnung
   * @return String Value
   */
  String get(String bez);

  /**
   * gibt den booleischen Wert der gew�hlten Information zur�ck.
   *
   * @param bez
   *          Bezeichnung
   * @return String Value
   */
  Boolean getBoolean(String bez);

}