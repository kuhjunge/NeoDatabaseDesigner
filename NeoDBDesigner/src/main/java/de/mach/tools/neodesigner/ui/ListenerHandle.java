package de.mach.tools.neodesigner.ui;

/**
 * Interface f�r den ListenerHandle (nach dem Observer Pattern)
 *
 * @author Chris Deter
 *
 */
interface ListenerHandle {
  /**
   * f�gt einen Listnener hinzu
   */
  void attach();

  /**
   * l�scht einen listener
   */
  void detach();
}
