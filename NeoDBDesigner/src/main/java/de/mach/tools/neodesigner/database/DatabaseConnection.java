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

package de.mach.tools.neodesigner.database;

import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Table;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Interface mit dem andere Schichten auf die Datenbank zugreifen können.
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
  boolean isReady();

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
   * Löscht die Datenbank.
   *
   * @return true wenn erfolgreich
   */
  boolean deleteDatabase();

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
  Optional<Table> getTable(String name);

  /**
   * Gibt eine Liste mit allen Tabellen zurück (WARNUNG: bei großen Datenmengen.
   * recht langsam!)
   *
   * @return gibt eine Liste mit allen Tabellen zurück
   */
  List<Table> getTables();

  /**
   * gibt eine Nummer für den letzten ForeignKey Schlüssel zurück.
   *
   * @param lenght
   *          Die Länge der Nummer
   * @return neue FK Nummer
   */
  int getForeignKeyNumber(int lenght);

  /**
   * Gibt eine Liste mit allen Kategorien aus der Datenbank zurück.
   *
   * @return Liste mit allen Kategorien
   */
  List<String> getListWithCategories();

  /**
   * Gibt informationen über die Größe der Datenbank zurück.
   *
   * @return Map mit Eigenschaften der Datenbank
   */
  Map<String, Integer> getDatabaseStats();
}
