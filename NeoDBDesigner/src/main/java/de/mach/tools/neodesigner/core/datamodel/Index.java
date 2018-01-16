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

package de.mach.tools.neodesigner.core.datamodel;

import java.util.List;

/**
 * Representiert einen Index im Relationalen Datenbankmodel.
 *
 * @author Chris Deter
 *
 */
public interface Index extends Node {
  public enum Type {
    XPK, XIF, XAK, XIE
  }

  /**
   * getter für Type.
   *
   * @return den Type
   */
  Type getType();

  /**
   * setter für Type.
   *
   * @param s
   *          Der neue Type
   */
  void setType(Type s);

  /**
   * getter isUnique.
   *
   * @return boolean
   */
  boolean isUnique();

  /**
   * setter isUnique.
   *
   * @param unique
   *          boolean ob unique
   */
  void setUnique(boolean unique);

  /**
   * Gibt eine Liste mit allen Feldern dieses Indexes sortiert zurück.
   *
   * @return Liste mit allen Feldern
   */
  List<Field> getFieldList();

  /**
   * fügt ein Feld zum Index hinzu.
   *
   * @param f
   *          das Feld
   */
  void addField(Field f);

  /**
   * Fügt ein Feld mit alternativen Namen zum Index hinzu.
   *
   * @param f
   *          das Feld
   * @param altName
   *          der alternative Name
   */
  void addField(Field f, String altName);

  /**
   * entfernt ein Feld vom Index.
   *
   * @param i
   *          Stelle an der das Feld im Index zugeordnet ist
   */
  void removeField(int i);

  /**
   * löscht alle Felder des Indexes.
   */
  void clearFieldList();

  /**
   * setzt einen neuen alternativen Namen.
   *
   * @param name
   *          Name des Feldes
   * @param altName
   *          der Referenz Tabellen Name des Feldes
   */
  public void setAltName(String name, String altName);

  /**
   * gibt den alternativen Namen eines Feldes zurück.
   *
   * @param name
   *          Name des Feldes
   * @return alternativer Name des Feldes
   */
  public String getAltName(String name);

  /**
   * gibt anhand des alternativen Namen den richtigen Namen zurück.
   *
   * @param name
   *          alternativer Name des Feldes
   * @return Name des Feldes
   */
  public String getNameFromAltName(String name);

  /**
   * gibt einen Int zurück welcher die Ordnung dieses Elementes ausdrückt.
   *
   * @param name
   *          Name des elementes
   * @return die Orndungszahl
   */
  Integer getOrder(String name);

  /**
   * Ersetzt ein Feld.
   *
   * @param field
   *          das neue feld
   * @param oldFieldName
   *          der Name des alten feldes
   */
  void replaceField(final Field field, final String oldFieldName);

  /**
   * Setzt die Order eines Feldes manuell.
   *
   * @param name
   *          Name des Feldes
   * @param order
   *          neue Ordnung des Feldes
   */
  void setOrder(String name, int order);
}
