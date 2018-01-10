package de.mach.tools.neodesigner.database;

import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Table;

import java.util.List;

/**
 * Interface mit dem andere Schichten auf die Datenbank zugreifen k�nnen
 * 
 * @author Chris Deter
 *
 */
public interface DatabaseConnection extends SaveDatabase {
  /**
   * gibt zur�ck ob die Datenbank Online ist.
   *
   * @return True wenn Online
   */
  boolean isOnline();

  /**
   * verbindet die Datebank.
   *
   * @param addr
   *          Adresse der Datenbank
   * @param nutzername
   *          der Nutzername
   * @param pw
   *          das Passwort
   * @return True wenn die Verbindung erfolgreich war
   */
  boolean connectDb(String addr, String nutzername, String pw);

  /**
   * Trennt die Datenbankverbindung.
   */
  void disconnectDb();

  /**
   * erstellt einen Index in der Graphendatenbank f�r beschleunigten Zugriff.
   */
  void createIndexOnDb();

  /**
   * F�gt eine Tabelle mit allen Feldern in der Datenbank ein.
   *
   * @param t
   *          Die Tabelle
   */
  void importTable(Table t);

  /**
   * f�gt eine Fremdschl�sselbeziehung zwischen Table->RefTable und.
   * Fremdschl�ssel->RefTable-XPK in die Datenbank ein.
   *
   * @param i
   *          der Fremdschl�ssel
   */
  void importForeignKey(ForeignKey i);

  /**
   * gibt eine String Liste f�r das Autocomplete Feld zur�ck.
   *
   * @return String Liste mit allen Tabellennamen
   */
  List<String> getListWithTableNames();

  /**
   * Gibt eine Tabelle aus der Datenbank zur�ck.
   *
   * @param name
   *          der Tabellename
   * @return die Tabelle
   */
  @Override
  Table getTable(String name);

  /**
   * Gibt eine Liste mit allen Tabellen zur�ck (WARNUNG: bei gro�en Datenmengen.
   * recht langsam!)
   *
   * @return gibt eine Liste mit allen Tabellen zur�ck
   */
  List<Table> getAllTables();

  /**
   * gibt eine Nummer f�r den letzten ForeignKey Schl�ssel zur�ck.
   *
   * @param lenght
   *          Die L�nge der Nummer
   * @return neue FK Nummer
   */
  int getForeignKeyNumber(int lenght);

  /**
   * L�scht die Datenbank.
   *
   * @return true wenn erfolgreich
   */
  boolean deleteDatabase();
}