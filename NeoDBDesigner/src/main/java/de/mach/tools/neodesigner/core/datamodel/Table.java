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


/** Representiert eine Tabelle aus dem Relationalen Datenbankmodel aus der Graphendatenbank.
 *
 * @author Chris Deter */
public interface Table extends Node {
  /** gibt die Spalten der Tabelle zurück.
   *
   * @return Spalten der Tabelle */
  List<Field> getFields();

  /** getter.
   *
   * @return Primärschlüssel */
  Index getXpk();

  /** setter.
   *
   * @param xpk neuer Primärschlüssel */
  void setXpk(Index xpk);

  /** gibt eine Liste mit Foreignkeys zurück.
   *
   * @return liste mit Foreignkeys */
  List<ForeignKey> getForeignKeys();

  /** gibt eine Liste mit referenzierenden Foreignkeys zurück, die auf diese Tabelle zeigen.
   *
   * @return liste mit Foreignkeys */
  List<ForeignKey> getRefForeignKeys();

  /** gibt eine Liste mit Indizes zurück.
   *
   * @return Liste mit Indizes */
  List<Index> getIndizies();

  /** fügt ein Feld zur Tabelle hinzu.
   *
   * @param f das Feld was zur Tabelle hinzugefügt wird. */
  void addField(Field... f);

  /** fügt einen Index zur Tabelle hinzu
   *
   * @param i Indexobjekt */
  void addIndex(Index i);

  /** Gibt ein Feld mit diesem Namen zurück.
   *
   * @param fieldName der Name von dem Feld welches zruückgegeben werden soll
   * @return Das Feld */
  Field getField(String fieldName);

  /** getter cateogory.
   *
   * @return category */
  String getCategory();

  /** setter category.
   *
   * @param category neue category */
  void setCategory(String category);

  /** löscht ein Feld aus der Tabelle.
   *
   * @param fieldName das zu löschende Feld */
  void deleteField(String fieldName);

  /** fügt einen Fremdschlüssel hinzu
   *
   * @param fk der Fremdschlüssel
   * @param refkey Ist dieser Fremdschlüssel von dieser Tabelle (True) oder von einer fremden Tabelle (false) */
  void addForeignKey(ForeignKey fk, boolean refkey);

  int getOrder(String fieldName);

  void setOrder(String fieldName, int order);

  void setDataFieldSrc(FieldList data);
}
