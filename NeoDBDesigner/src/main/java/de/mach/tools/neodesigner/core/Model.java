package de.mach.tools.neodesigner.core;

import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.core.nimport.ImportTask;

import java.io.File;
import java.util.List;
import java.util.Observable;

/**
 * Schnittstelle für den View (der drüberliegenden Schicht)
 *
 * @author Chris Deter
 *
 */
public interface Model {

  /**
   * Gibt ein Observable des Datenmodells zurück (für TreeView).
   *
   * @return Observable des Datenmodells
   */
  Observable dataModelObservable();

  /**
   * gibt eine List mit Strings zurück, die alle Namen der Datenbanken enthält.
   * (für Autocomplete)
   *
   * @return Liste mit Strings von allen Tabellen in der Datenbank
   */
  List<String> getListWithTableNames();

  /**
   * trennt die Verbindung zur Datenbank.
   */
  void disconnectDb();

  /**
   * stellt eine Verbindung mit der Datenbank her.
   *
   * @param dbadr
   *          Adresse der Datenbank
   * @param usr
   *          Nutzername
   * @param pw
   *          Passwort
   * @return true bei einer erfolgreichen Verbindung
   */
  boolean connectDb(String dbadr, String usr, String pw);

  /**
   * Task welche Daten aus der DB läd.
   *
   * @return Task
   */
  LoadFromDbTask loadFrmDbTask();

  /**
   * gibt eine neue Tabelle zurück.
   *
   * @param tableName
   *          Name der Tabelle
   * @return New Table
   */
  Table getnewTable(String tableName);

  /**
   * gibt eine Tabelle aus der Datenbank zurück.
   *
   * @param tablename
   *          Name der Tabelle
   * @return die angeforderte Tabelle aus der Datenbank oder Null
   */
  Table getTable(String tablename);

  /**
   * gibt alle Tabellen die in der Software gespeichert sind, greift nicht auf
   * die Datenbank zurück.
   *
   * @return Tabellen aus dem Datenmodell
   */
  List<Table> getAllLocalTables();

  /**
   * läd alle Tabellen aus der Datenbank (genutzt in LoadFromDBTask).
   *
   * @return Liste mit allen Datenbankfeldern
   */
  List<Table> getAllTables();

  /**
   * gibt an ob die Datenbank online ist.
   *
   * @return True wenn die Datenbank online ist
   */
  boolean isOnline();

  /**
   * gibt eine laufende Nummer für die Fremdschlüssel zurück.
   *
   * @return nächste Fremdschlüsselnummer
   */
  int getNextFkNumber();

  /**
   * Löscht die gesammte Datenbank.
   */
  boolean deleteDatabase();

  /**
   * gibt ein Objekt zurück auf dem gespeichert werden kann.
   *
   * @return das Save Objekt
   */
  Save getSaveObj();

  /**
   * Gibt die Information zurück ob eine Datei bekannt ist um den Neo4J Server
   * zu starten.
   *
   * @return True wenn die Neo4j Startdatei gefunden wurde
   */
  Boolean isNeoServerStarterFileKnown();

  /**
   * Setzt einen neuen Ordner in dem der Neo4J Server liegt.
   *
   * @param folder
   *          der Ordner
   * @return ob das setzen des neuen Neo4J Ordners geklappt hat
   */
  Boolean setNewNeoServerFolder(File folder);

  /**
   * Startet den Neo4J Server.
   *
   * @return true, wenn der Server gestartet worden ist
   */
  Boolean startNeoServer();

  /**
   * gibt die Maximale Wortlänge für Namen zurück.
   *
   * @return Max Wortlänge
   */
  int getWordLength();

  /**
   * gibt die Adresse der Datenbank zurück.
   *
   * @return Datenbankadresse
   */
  String getAddrOfDb();

  /**
   * gibt den Datenbankbenutzer zurück.
   *
   * @return Datenbankbenutzer
   */
  String getUser();

  /**
   * gibt das Datenbankpasswort zurück.
   *
   * @return Datenbankpasswort
   */
  String getPw();

  /**
   * Schreibt eine Exportdatei.
   *
   * @param f
   *          Die DAtei
   * @param type
   *          Typ der zu schreibenen Datei ? c für CSV, s für SQL
   * @return true wenn die Datei auf die Festplatte geschrieben wurde
   */
  Boolean writeExportFile(File f, char type);

  /**
   * gibt einen ImportTask zurück.
   *
   * @param file
   *          Die Datei für den Import
   * @param type
   *          Typ des imports. c für CSV, s für SQL
   * @return Task
   */
  ImportTask importTask(File file, char type);

  /**
   * gibt die nächste Nummer für den Index in einer Tabelle zurück.
   *
   * @param li
   *          Liste mit allen Indizes der Tabelle
   * @return die neue nummer
   */
  Integer getNextNumberForIndex(List<Index> li);

  /**
   * gibt einen Array mit allen Datentypen für das Select Feld zurück.
   *
   * @return Array mit allen Datentypen für das Select Feld
   */
  String[] getSelectDatatype();

}