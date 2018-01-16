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

package de.mach.tools.neodesigner.core;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Enthält diverse Utility Funktionen.
 *
 * @author cd
 *
 */
public class Util {
  private static final Logger LOG = Logger.getLogger(Util.class.getName());

  private Util() {

  }

  /**
   * Schreibt den ersten Buchstaben des Strings groß.
   *
   * @param name
   *          der String
   * @return der Großgeschriebene String
   */
  public static String getCapName(final String name) {
    return name.substring(0, 1).toUpperCase() + name.substring(1);
  }

  /**
   * Wandelt einen String in einen Integer um.
   *
   * @param value
   *          String
   * @return integer oder 0 bei einem Fehler
   */
  public static int tryParseInt(final String value) {
    int ret = 0;
    try {
      ret = Integer.parseInt(value);
    } catch (final Exception e) {
      Util.LOG.log(Level.FINE, e.getMessage(), e);
    }
    return ret;
  }

  /**
   * prüft ob eine Zahl eine valide Integer Zahl ist.
   *
   * @param str
   *          der String
   * @return true, wenn Integer
   */
  public static boolean isInteger(final String str) {
    if (str == null) {
      return false;
    }
    final int length = str.length();
    if (length == 0) {
      return false;
    }
    int i = 0;
    if (str.charAt(0) == '-') {
      if (length == 1) {
        return false;
      }
      i = 1;
    }
    for (; i < length; i++) {
      final char c = str.charAt(i);
      if (c < '0' || c > '9') {
        return false;
      }
    }
    return true;
  }

  /**
   * Checks the path.
   *
   * @param path
   *          The Path
   * @return null or an existing path
   */
  public static File getFile(final String path) {
    final File f = new File(path);
    if (f.exists() && f.isDirectory()) {
      return f;
    }
    return null;
  }
}
