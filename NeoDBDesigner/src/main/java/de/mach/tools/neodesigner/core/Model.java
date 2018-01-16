/*******************************************************************************
 * Copyright (C) 2017 Chris Deter
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/

package de.mach.tools.neodesigner.core;

import de.mach.tools.neodesigner.core.category.CategoryObj;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.core.nexport.pdf.LoadPdfConfiguration;
import de.mach.tools.neodesigner.core.nimport.ImportTask;
import de.mach.tools.neodesigner.database.cypher.DatabaseConnector;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Optional;

/**
 * Schnittstelle für den View (der drüberliegenden Schicht).
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
  LoadFromDbTask loadFromDbTask(DatabaseConnector dbcon);

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
  Optional<Table> getTable(String tablename);

  /**
   * gibt eine Tabelle aus der Datenbank zurück.
   *
   * @param tablename
   *          Name der Tabelle
   *
   * @param useLocalDb
   *          true: nutze Datenmodell als quelle: false: lade Tabelle aus
   *          Datenbank
   * @return die angeforderte Tabelle aus der Datenbank oder Null
   */
  Optional<Table> getTable(String tablename, boolean useLocalDb);

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
   * Gibt die Information zurück ob eine Datei bekannt ist um den Neo4J Server zu
   * starten.
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
   * @param f
   *          Die zu importierende Datei
   * @param type
   *          Typ des imports. c für CSV, s für SQL
   * @return Task
   */
  ImportTask importTask(File f, char type);

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

  /**
   * Gibt die Länge zurück, in der ein Tabellenname eindeutig sein muss.
   *
   * @return int mit eindeutiger Länge
   */
  Validator getValidator();

  /**
   * Läd eine Liste mit Tabellen ins Model.
   *
   * @param lt
   *          Liste mit Tabellen
   */
  void addTableList(List<Table> lt);

  /**
   * Schreibt im Zielverzeichnis alle CSV Dateien die für die Datenbank Toolchain
   * benötigt werden.
   *
   * @param file
   *          der Zielordner
   * @return die Dateien
   */
  boolean writeToolchainReport(File file);

  /**
   * Importiert ein ganzes Verzeichnis (mit CSV Dateien).
   *
   * @param file
   *          Die Dateien
   * @return den Task zum Importieren
   */
  ImportTask importFolderTask(File file);

  /**
   * Gibt eine Liste mit allen Kategorien in der Datenbank zurück.
   *
   * @return Liste mit allen Kategorien.
   */
  List<String> getAllCategories();

  /**
   * füllt das Kategorie Auswahlfeld.
   *
   * @return sortierte Category List
   */
  List<CategoryObj> getCategorySelection();

  /**
   * Speichert eine Liste mit Kategorien ab.
   *
   * @param map
   *          die Liste mit Kategorien
   */
  void saveCategoryList(Map<String, String> map);

  /**
   * gibt die Config für den PDF Export zurück.
   *
   * @return pdf Config
   */
  LoadPdfConfiguration getPdfConfig();

  /**
   * Gibt informationen über die Größe der Datenbank zurück.
   *
   * @return Map mit Eigenschaften der Datenbank
   */
  Map<String, Integer> getDatabaseStats();

  String getPathImportSql();

  String getPathImportCat();

  String getPathImportCsv();

  String getPathExportSql();

  String getPathExportCsv();

  String getPathExportCql();

}
