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

package de.mach.tools.neodesigner.core.nexport;


import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Table;


public class ExportUtil {

  /** nur statischer Zugriff */
  private ExportUtil() {}

  /** Prüft ob der Primärschlüssel einer Tabelle identisch ist mit dem angegebenen Index
   *
   * @param i der Index
   * @param t Die Tabelle, die den Primärschlüssel enthält
   * @return true wenn identisch, sonst false */
  public static boolean isIdentical(final Index i, final Table t) {
    boolean ret = false;
    if (t.getXpk().getFieldList().size() == i.getFieldList().size()
        && t.getXpk().getFieldList().containsAll(i.getFieldList())
        && i.getFieldList().containsAll(t.getXpk().getFieldList())) {
      ret = true;
    }

    return ret;
  }
}
