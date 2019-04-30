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


/** Klasse welche Felder in einer Liste hält. Die Liste hat dabei eine feste Sortierung. */
public interface FieldList {
  /** Löscht ein Feld aus der Liste
   * 
   * @param fieldName Name des Feldes */
  void deleteField(final String fieldName);

  /** gibt ein spezifisches Feld der Liste zurück
   * 
   * @param fieldName Name des Feldes
   * @return Das Feld oder Null */
  Field getField(final String fieldName);

  /** Gibt eine komplette Liste der Felder zurück
   * 
   * @return Liste mit Feldern */
  List<Field> get();

  /** Fügt ein Feld in die Liste hinzu
   * 
   * @param f Feld */
  void addField(final Field f);

  /** Gibt die Ordnung eines Feldes zurück
   * 
   * @param fieldName Name des Feldes
   * @return Ordnungsziffer */
  int getOrder(String fieldName);

  /** Setzt die Ordnung eines Feldes
   * 
   * @param fieldName Name des Feldes
   * @param order Ordnungsziffer, die das Feld haben soll */
  void setOrder(String fieldName, int order);

  /** Gibt die alternative Sortierung Zurück. Gibt es keine alternative Ordnung, wird die normale Ordung ausgegeben.
   * 
   * @param fieldName Name des Feldes
   * @return eine Ordnungsziffer */
  int getReferenceOrder(String fieldName);

  /** Setzt die Ordnung in der alternativen Sortierung der Liste
   * 
   * @param fieldName Name des Feldes
   * @param order Ordnungsziffer */
  void setReferenceOrder(String fieldName, int order);

  /** Gibt das Feld an der Stelle X in der Liste zurück
   * 
   * @param order die Stelle des Elementes, welches gewählt wurde
   * @return Feld */
  Field getFieldByOrder(final int order);

  /** Gibt das Feld an der Stelle X in der Liste mit der alternativen Sortierung zurück
   * 
   * @param order die Stelle des Elementes, welches gewählt wurde
   * @return Feld */
  Field getRefFieldByOrder(final int order);

  /** Löscht alle Felder aus der Liste */
  void clear();

  /** Ersetzt ein Feld in der Liste
   * 
   * @param f neues Feld
   * @param oldname Name des Feldes, welches ersetzt werden soll */
  void replaceField(Field f, String oldname);
}
