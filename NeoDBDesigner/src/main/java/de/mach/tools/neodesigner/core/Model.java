/* Copyright (C) 2018 Chris Deter Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the
 * Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions: The
 * above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE. */

package de.mach.tools.neodesigner.core;


import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Optional;

import de.mach.tools.neodesigner.core.category.CategoryObj;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.core.nexport.Generator;
import de.mach.tools.neodesigner.core.nexport.pdf.PdfConf;
import de.mach.tools.neodesigner.core.nimport.ImportTask;
import de.mach.tools.neodesigner.ui.GuiConf;


/** Schnittstelle f�r den View (der dr�berliegenden Schicht).
 *
 * @author Chris Deter */
public interface Model {

  /** L�d eine Liste mit Tabellen ins Model.
   *
   * @param lt Liste mit Tabellen */
  void addTableList(List<Table> lt);

  /** Spezieller Import Task, der den Import direkt �ber die Datenbank Binarys durchf�hrt
   *
   * @param folder
   * @return */
  ImportTask bulkimportTask(File folder);

  /** stellt eine Verbindung mit der Datenbank her.
   *
   * @param dbadr Adresse der Datenbank
   * @param usr Nutzername
   * @param pw Passwort
   * @return true bei einer erfolgreichen Verbindung */
  boolean connectDb(String dbadr, String usr, String pw);

  /** stellt eine Verbindung mit der lokalen Datenbank her. */
  void connectLocalDb();

  /** Gibt ein Observable des Datenmodells zur�ck (f�r TreeView).
   *
   * @return Observable des Datenmodells */
  Observable dataModelObservable();

  /** L�scht die gesammte Datenbank. */
  boolean deleteDatabase();

  /** trennt die Verbindung zur Datenbank. */
  void disconnectDb();

  /** gibt die Adresse der Datenbank zur�ck.
   *
   * @return Datenbankadresse */
  String getAddrOfDb();

  /** Gibt eine Liste mit allen Kategorien in der Datenbank zur�ck.
   *
   * @return Liste mit allen Kategorien. */
  List<String> getAllCategories();

  /** l�d alle Tabellen aus der Datenbank (genutzt in LoadFromDBTask).
   *
   * @return Liste mit allen Datenbankfeldern */
  List<Table> getAllTables();

  /** f�llt das Kategorie Auswahlfeld.
   *
   * @return sortierte Category List */
  List<CategoryObj> getCategorySelection();

  /** Gibt informationen �ber die Gr��e der Datenbank zur�ck.
   *
   * @return Map mit Eigenschaften der Datenbank */
  Map<String, Integer> getDatabaseStats();

  /** Konfigurationsm�glichkeiten der Gui
   *
   * @return */
  GuiConf getGuiConf();

  /** gibt eine List mit Strings zur�ck, die alle Namen der Datenbanken enth�lt. (f�r Autocomplete)
   *
   * @return Liste mit Strings von allen Tabellen in der Datenbank */
  List<String> getListWithTableNames();

  /** gibt eine neue Tabelle zur�ck.
   *
   * @param tableName Name der Tabelle
   * @return New Table */
  Table getnewTable(String tableName);

  /** gibt eine laufende Nummer f�r die Fremdschl�ssel zur�ck.
   *
   * @param i
   * @return n�chste Fremdschl�sselnummer */
  int getNextFkNumber(int i);

  /** gibt die n�chste Nummer f�r den Index in einer Tabelle zur�ck.
   *
   * @param li Liste mit allen Indizes der Tabelle
   * @return die neue nummer */
  Integer getNextNumberForIndex(List<Index> li);

  /** gibt die Config f�r den PDF Export zur�ck.
   *
   * @return pdf Config */
  PdfConf getPdfConfig();

  /** gibt ein Objekt zur�ck auf dem gespeichert werden kann.
   *
   * @return das Save Objekt */
  Save getSaveObj();

  /** gibt einen Array mit allen Datentypen f�r das Select Feld zur�ck.
   *
   * @return Array mit allen Datentypen f�r das Select Feld */
  String[] getSelectDatatype();

  /** gibt eine Tabelle aus der Datenbank zur�ck.
   *
   * @param tablename Name der Tabelle
   * @return die angeforderte Tabelle aus der Datenbank oder Null */
  Optional<Table> getTable(String tablename);

  /** gibt eine Tabelle aus der Datenbank zur�ck.
   *
   * @param tablename Name der Tabelle
   * @param useLocalDb true: nutze Datenmodell als quelle: false: lade Tabelle aus Datenbank
   * @return die angeforderte Tabelle aus der Datenbank oder Null */
  Optional<Table> getTable(String tablename, boolean useLocalDb);

  /** Gibt die L�nge zur�ck, in der ein Tabellenname eindeutig sein muss.
   *
   * @return int mit eindeutiger L�nge */
  Validator getValidator();

  /** gibt die Maximale Wortl�nge f�r Namen zur�ck.
   *
   * @return Max Wortl�nge */
  int getWordLength();

  /** Importiert ein ganzes Verzeichnis (mit CSV Dateien).
   *
   * @param file Die Dateien
   * @return den Task zum Importieren */
  ImportTask importFolderTask(File file);

  /** gibt einen ImportTask zur�ck.
   *
   * @param f Die zu importierende Datei
   * @param type Typ des imports. c f�r CSV, s f�r SQL
   * @return Task */
  ImportTask importTask(File f, char type);

  /** Gibt die Information zur�ck ob eine Datei bekannt ist um den Neo4J Server zu starten.
   *
   * @return True wenn die Neo4j Startdatei gefunden wurde */
  Boolean isNeoServerStarterFileKnown();

  /** gibt an ob die Datenbank online ist.
   *
   * @return True wenn die Datenbank online ist */
  boolean isOnline();

  /** Task welche Daten aus der DB l�d.
   *
   * @return Task */
  LoadFromDbTask loadFromDbTask();

  /** Speichert eine Liste mit Kategorien ab.
   *
   * @param map die Liste mit Kategorien */
  void saveCategoryList(Map<String, String> map);

  /** Setzt einen neuen Ordner in dem der Neo4J Server liegt.
   *
   * @param folder der Ordner
   * @return ob das setzen des neuen Neo4J Ordners geklappt hat */
  Boolean setNewNeoServerFolder(File folder);

  /** Startet den Neo4J Server.
   *
   * @return true, wenn der Server gestartet worden ist */
  Boolean startNeoServer();

  /** Schreibt eine Exportdatei.
   *
   * @param f Die Datei
   * @param gen Der zu verwendene Generator
   * @return true wenn die Datei auf die Festplatte geschrieben wurde */
  Boolean writeExportFile(File f, Generator gen);

  /** Schreibt im Zielverzeichnis alle CSV Dateien die f�r die Datenbank Toolchain ben�tigt werden.
   *
   * @param file der Zielordner
   * @return die Dateien */
  boolean writeToolchainReport(File file);
}
