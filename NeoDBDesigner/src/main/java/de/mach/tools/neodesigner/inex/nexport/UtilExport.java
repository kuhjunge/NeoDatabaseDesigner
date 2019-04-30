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

package de.mach.tools.neodesigner.inex.nexport;


import java.util.ArrayList;
import java.util.List;

import de.mach.tools.neodesigner.core.Strings;
import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Table;


public class UtilExport {

  /** nur statischer Zugriff */
  private UtilExport() {}

  /** Prüft ob der angegebene Index redundant ist
   *
   * @param index der Index
   * @param t Die Tabelle, die den Primärschlüssel enthält
   * @return true wenn identisch, sonst false */
  public static boolean isRedundant(final Index index, final Table t, boolean checkIndizes) {
    boolean ret;
    if (checkIndizes) {
      // Prüft ob der Primärschlüssel einer Tabelle identisch ist mit dem angegebenen Index
      ret = identicalWithPk(index, t.getXpk());
      // Wenn Index bereits über andere Indizes abgedeckt ist

      for (Index comp : t.getIndizies()) {
        if (!index.getName().equals(comp.getName())) {
          ret = ret | identicalWithIndex(index, comp);
        }
      }
    }
    else {
      // Workaround um altes SQL und CSV Verhalten zu imitieren
      ret = isIdentical(index, t);
    }
    return ret;
  }

  // TODO: Nach Umstellung diese alte Funktion entfernen und nur noch die neue Funktion nutzen.
  /** Prüft ob der Primärschlüssel einer Tabelle identisch ist mit dem angegebenen Index
   *
   * @param i der Index
   * @param t Die Tabelle, die den Primärschlüssel enthält
   * @return true wenn identisch, sonst false */
  private static boolean isIdentical(final Index i, final Table t) {
    boolean ret = false;
    if (t.getXpk().getFieldList().size() == i.getFieldList().size()
        && t.getXpk().getFieldList().containsAll(i.getFieldList())
        && i.getFieldList().containsAll(t.getXpk().getFieldList())) {
      ret = true;
    }
    return ret;
  }

  private static boolean identicalWithPk(Index original, Index comp) {
    return original.getFieldList().size() <= comp.getFieldList().size()
           && isIdentical(original, comp, original.getFieldList().size());
  }

  private static boolean identicalWithIndex(Index original, Index comp) {
    return original.getFieldList().size() < comp.getFieldList().size()
           && isIdentical(original, comp, original.getFieldList().size());
  }

  private static boolean isIdentical(Index original, Index comp, int count) {
    boolean isIdentical = true; // Annahme -> Indizes sind gleich, versuche das Gegenteil zu beweisen
    for (int i = 1; i <= count; i++) {
      if (!comp.getFieldByOrder(i, false).equals(original.getFieldByOrder(i, false))) {
        isIdentical = false; // Sind nicht identisch
        break;
      }
    }
    return isIdentical;
  }

  public static String cleanComment(final String input) {
    return input.replaceAll("\r", "").replaceAll("\n", Strings.HTML_BR).replaceAll(";", ",").trim();
  }

  /** Sortiert die Field Elemente in die Reihenfolge des Pks auf die der FK verweist.
   *
   * @param fk der Felder und RefTable mit PK enthält
   * @return eine sortierte Liste mit Feldern die die gleiche Reihenfolge wie der PK hat */
  public static List<Field> sortPkElem(final ForeignKey fk) {
    // Sortiere Felder nach PK Reihenfolge
    final List<Field> sortedList = new ArrayList<>();
    for (final Field rf : fk.getRefTable().getXpk().getFieldList()) {
      for (final Field ff : fk.getIndex().getFieldList()) {
        if (fk.getAltName(ff.getName()).equals(rf.getName())) {
          sortedList.add(ff);
        }
      }
    }
    return sortedList;
  }

  /** erzeugt Leerzeichen.
   *
   * @param count Anzahl der Leerzeichen
   * @return die angegebene Anzahl an Leerzeichen */
  static String getSpace(final int count) {
    final StringBuilder sb = new StringBuilder();
    for (int i = 1; i < count; i++) {
      sb.append(Strings.SPACE);
    }
    return sb.toString();
  }

  public static void removeLastComma(StringBuilder sb, boolean somethingAdded) {
    // Alles vor dem Komma löschen (Windows \r\n , Linux \n )
    while (somethingAdded && sb.charAt(sb.length() - 1) != ',') {
      sb.deleteCharAt(sb.length() - 1);
    }
    sb.deleteCharAt(sb.length() - 1); // Komma löschen
  }
}
