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


import java.util.Optional;


/** Represnetiert einen Fremdschlüssel im Relationalen Datenbankmodel.
 *
 * @author Chris Deter */
public interface ForeignKey extends Node {

  /** gibt den alternativen Namen eines Feldes zurück.
   *
   * @param name Name des Feldes
   * @return alternativer Name des Feldes */
  String getAltName(String name);

  Optional<Field> getFieldOfTableByOrder(Field xpkf);

  /** gibt den Index zu diesem FK zurück.
   *
   * @return Index des Fremdschlüssels */
  Index getIndex();

  String getNameFromAltName(String fieldname);

  /** Gibt die Referenztablle zurück.
   *
   * @return Referenz Tabelle (Tabelle die mit dieser Tabelle verknüpft ist) */
  Table getRefTable();

  void setFkOrder(Field f, int order);

  /** setzt den Index des Fremdschlüssels
   *
   * @param i der Index des Fremdschlüssels. */
  void setIndex(Index i);

  /** setzt die Referenz Tabelle.
   *
   * @param refTable die Referenz Tabelle */
  void setRefTable(Table refTable);
}
