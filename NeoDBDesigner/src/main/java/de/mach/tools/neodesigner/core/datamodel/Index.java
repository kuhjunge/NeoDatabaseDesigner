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

package de.mach.tools.neodesigner.core.datamodel;


import java.util.List;


/** Representiert einen Index im Relationalen Datenbankmodel.
 *
 * @author Chris Deter */
public interface Index extends Node {
  enum Type
  {
    XPK,
    XIF,
    XAK,
    XIE
  }

  /** fügt ein Feld zum Index hinzu.
   *
   * @param f das Feld */
  void addField(Field f);

  /** löscht alle Felder des Indexes. */
  void clearFieldList();

  Field getFieldByOrder(int order, final boolean ref);

  /** Gibt eine nach Reihenfolge sortierte Liste mit allen Feldern dieses Indexes sortiert zurück.
   *
   * @return Liste mit allen Feldern */
  List<Field> getFieldList();

  /** gibt einen Int zurück welcher die Ordnung dieses Elementes ausdrückt.
   *
   * @param name Name des elementes
   * @return die Orndungszahl */
  Integer getOrder(String name, final boolean ref);

  /** getter für Type.
   *
   * @return den Type */
  Type getType();

  /** getter isUnique.
   *
   * @return boolean */
  boolean isUnique();

  /** entfernt ein Feld vom Index.
   *
   * @param i Stelle an der das Feld im Index zugeordnet ist */
  void removeField(int i);

  /** Ersetzt ein Feld.
   *
   * @param field das neue feld
   * @param oldFieldName der Name des alten feldes */
  void replaceField(final Field field, final String oldFieldName);

  /** Setzt die Order eines Feldes manuell.
   *
   * @param name Name des Feldes
   * @param order neue Ordnung des Feldes */
  void setOrder(String name, int order, final boolean isRef);

  /** setter für Type.
   *
   * @param s Der neue Type */
  void setType(Type s);

  /** setter isUnique.
   *
   * @param unique boolean ob unique */
  void setUnique(boolean unique);

  boolean hasField(String fieldname);
}
