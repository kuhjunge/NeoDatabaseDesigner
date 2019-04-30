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


import java.util.List;
import java.util.Map;
import java.util.Optional;

import de.mach.tools.neodesigner.core.category.CategoryObj;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.inex.ImportTask;
import de.mach.tools.neodesigner.inex.nexport.pdf.PdfConf;
import de.mach.tools.neodesigner.ui.GuiConf;


/** Schnittstelle für den View (der drüberliegenden Schicht).
 *
 * @author Chris Deter */
public interface Model {

  /** stellt eine Verbindung mit der lokalen Datenbank her. */
  void connect();

  /** Löscht die gesammte Datenbank. */
  boolean deleteDatabase();

  /** gibt die Adresse der Datenbank zurück.
   *
   * @return Datenbankadresse */
  String getAddrOfDb();

  /** Gibt eine Liste mit allen Kategorien in der Datenbank zurück.
   *
   * @return Liste mit allen Kategorien. */
  List<String> getAllCategories();

  /** läd alle Tabellen aus der Datenbank (genutzt in LoadFromDBTask).
   *
   * @return Liste mit allen Datenbankfeldern */
  List<Table> getAllTables();

  /** füllt das Kategorie Auswahlfeld.
   *
   * @return sortierte Category List */
  List<CategoryObj> getCategorySelection();

  /** Gibt informationen über die Größe der Datenbank zurück.
   *
   * @return Map mit Eigenschaften der Datenbank */
  Map<String, Integer> getDatabaseStats();

  /** Konfigurationsmöglichkeiten der Gui
   *
   * @return Konfigurationsoptionen der GUI */
  GuiConf getGuiConf();

  /** gibt eine List mit Strings zurück, die alle Namen der Datenbanken enthält. (für Autocomplete)
   *
   * @return Liste mit Strings von allen Tabellen in der Datenbank */
  List<String> getListWithTableNames();

  /** gibt eine neue Tabelle zurück.
   *
   * @param tableName Name der Tabelle
   * @return New Table */
  Table getNewTable(String tableName);

  /** gibt eine laufende Nummer für die Fremdschlüssel zurück.
   *
   * @param i aktuelle Indexnummer
   * @return nächste Fremdschlüsselnummer */
  int getNextFkNumber(int i);

  /** gibt die nächste Nummer für den Index in einer Tabelle zurück.
   *
   * @param li Liste mit allen Indizes der Tabelle
   * @return die neue nummer */
  Integer getNextNumberForIndex(List<Index> li);

  /** gibt die Config für den PDF Export zurück.
   *
   * @return pdf Config */
  PdfConf getPdfConfig();

  /** gibt ein Objekt zurück auf dem gespeichert werden kann.
   *
   * @return das Save Objekt */
  Save getSaveObj();

  /** gibt einen Array mit allen Datentypen für das Select Feld zurück.
   *
   * @return Array mit allen Datentypen für das Select Feld */
  String[] getSelectDatatype();

  /** gibt eine Tabelle aus der Datenbank zurück.
   *
   * @param tablename Name der Tabelle
   * @return die angeforderte Tabelle aus der Datenbank oder Null */
  Optional<Table> getTable(String tablename);

  /** Gibt die Länge zurück, in der ein Tabellenname eindeutig sein muss.
   *
   * @return int mit eindeutiger Länge */
  Validator getValidator();

  /** gibt die Maximale Wortlänge für Namen zurück.
   *
   * @return Max Wortlänge */
  int getWordLength();

  /** Gibt einen Import Task zurück
   * 
   * @param input String mit Input Dateien
   * @param type Typ des Importes (s für SQL)
   * @param inputPath Pfad aus dem Importiert werden soll
   * @return einen Import Task, der gestartet werden kann */
  ImportTask importTask(String input, char type, String inputPath);

  /** Importiert ein ganzes Verzeichnis (mit CSV Dateien).
   *
   * @return den Task zum Importieren */
  ImportTask importFolderTask(final String path, final String tbls, final String cols, final String pKeys,
                              final String idxs, final String fKeys, final String metaTbls, final String metaCols);

  /** Speichert eine Liste mit Kategorien ab.
   *
   * @param map die Liste mit Kategorien */
  void saveCategoryList(Map<String, String> map);

  /** Läd die Kategorien neu. */
  void reloadCategories();

  void importTables(List<Table> list);

  void addDataModelListener(DatamodelListener listener);

  Map<String, String> getCategoryTranslation();
}
