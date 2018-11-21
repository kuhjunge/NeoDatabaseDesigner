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


/** Representiert einen Knoten aus der Graphendatenbank von welchen die anderen Relationalen Datenbankmodel Abbildungen
 * von abgeleitet sind.
 *
 * @author Chris Deter */
public interface Node extends Comparable<Node> {
  /** gibt den Namen zurück.
   *
   * @return String mit Namen */
  String getName();

  /** setzt den Namen.
   *
   * @param name Der Name des Nodes */
  void setName(String name);

  /** gibt das übergeordnete Node zurück (Meistens eine Tabelle).
   *
   * @return Übergeordnetes Element oder Null */
  Table getTable();

  /** gibt ein Kommentar zum Element zurück.
   *
   * @return Kommentar als String */
  String getComment();

  /** setzt ein Kommentar zum Element.
   *
   * @param name Der Kommentar */
  void setComment(String name);

  /** gibt den Node Typ zurück
   *
   * @return */
  String getNodeType();
}
