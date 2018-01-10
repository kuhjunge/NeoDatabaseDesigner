package de.mach.tools.neodesigner.database;

import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Table;

import java.util.List;

/**
 * Interface mit dem andere Schichten auf die Datenbank zugreifen können
 * 
 * @author Chris Deter
 *
 */
public interface DatabaseConnection extends SaveDatabase {
  /**
   * gibt zurück ob die Datenbank Online ist.
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
   * erstellt einen Index in der Graphendatenbank für beschleunigten Zugriff.
   */
  void createIndexOnDb();

  /**
   * Fügt eine Tabelle mit allen Feldern in der Datenbank ein.
   *
   * @param t
   *          Die Tabelle
   */
  void importTable(Table t);

  /**
   * fügt eine Fremdschlüsselbeziehung zwischen Table->RefTable und.
   * Fremdschlüssel->RefTable-XPK in die Datenbank ein.
   *
   * @param i
   *          der Fremdschlüssel
   */
  void importForeignKey(ForeignKey i);

  /**
   * gibt eine String Liste für das Autocomplete Feld zurück.
   *
   * @return String Liste mit allen Tabellennamen
   */
  List<String> getListWithTableNames();

  /**
   * Gibt eine Tabelle aus der Datenbank zurück.
   *
   * @param name
   *          der Tabellename
   * @return die Tabelle
   */
  @Override
  Table getTable(String name);

  /**
   * Gibt eine Liste mit allen Tabellen zurück (WARNUNG: bei großen Datenmengen.
   * recht langsam!)
   *
   * @return gibt eine Liste mit allen Tabellen zurück
   */
  List<Table> getAllTables();

  /**
   * gibt eine Nummer für den letzten ForeignKey Schlüssel zurück.
   *
   * @param lenght
   *          Die Länge der Nummer
   * @return neue FK Nummer
   */
  int getForeignKeyNumber(int lenght);

  /**
   * Löscht die Datenbank.
   *
   * @return true wenn erfolgreich
   */
  boolean deleteDatabase();
}