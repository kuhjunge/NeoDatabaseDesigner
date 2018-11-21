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

package de.mach.tools.neodesigner.database.cypher;


import java.util.List;


/** Interface welches das Neo4J Framework kapselt.
 *
 * @author Chris Deter */
public interface DatabaseConnector {

  /** Prüft ob die Datenbank verbunden ist.
   *
   * @return "True" wenn die Datenbank verbunden ist, sonst "False" */
  boolean isOnline();

  /** Verbindet die Datenbank.
   *
   * @param addr Adresse der Datenbank
   * @param nutzername Nutzername für die Datenbank
   * @param pw Passwort für die Datenbank
   * @return "True" wenn die Datenbank verbunden wurde */
  boolean connectDb(String addr, String nutzername, String pw);

  /** Trennt die Datenbankverbindung. */
  void disconnectDb();

  /** Führt eine Query auf der Datenbank aus.
   *
   * @param q Query
   * @param v Parameter der Query
   * @return eine Ergebnis Einheit */
  List<ResultUnit> runCypher(String q, Object[] v);

  /** Führt eine Query auf der Datenbank aus.
   *
   * @param q Query
   * @return eine Ergebnis Einheit */
  List<ResultUnit> runCypher(String q);

  /** Verbindet die Datenbank neu.
   *
   * @return true wenn erfolgreich */
  boolean reconnect();
}
