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


import de.mach.tools.neodesigner.core.Strings;


/** Die Beziehungen aus der Graphendatenbank.
 *
 * @author Chris Deter */
public class Relations {

  private Relations() {}

  public enum Type
  {
    XPK,
    DATA,
    REFERENCE,
    INDEX,
    DEFAULT
  }

  /** Gibt den Typ der Beziehung zurück.
   *
   * @param t Typ
   * @return Textrepresentation des Types */
  public static String getType(final Type t) {
    // @formatter:off
    switch (t) {
      case XPK:
        return Strings.RELNAME_XPK;
      case DATA:
        return Strings.RELNAME_DATA;
      case REFERENCE:
        return Strings.RELNAME_REFERENCE;
      case INDEX:
        return Strings.RELNAME_INDEX;
      default:
        return "";
    }
    // @formatter:on
  }
}
