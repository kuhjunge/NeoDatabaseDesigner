package de.mach.tools.neodesigner.ui;

/**
 * Interface für den ListenerHandle (nach dem Observer Pattern)
 *
 * @author Chris Deter
 *
 */
interface ListenerHandle {
  /**
   * fügt einen Listnener hinzu
   */
  void attach();

  /**
   * löscht einen listener
   */
  void detach();
}
