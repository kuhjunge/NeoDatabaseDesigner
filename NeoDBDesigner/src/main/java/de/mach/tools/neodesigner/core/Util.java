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

package de.mach.tools.neodesigner.core;


import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.core.nexport.Generator;


/** Enthält diverse Utility Funktionen.
 *
 * @author cd */
public class Util {
  private static final Logger LOG = Logger.getLogger(Util.class.getName());

  /** Schreibt den ersten Buchstaben des Strings groß.
   *
   * @param name der String
   * @return der Großgeschriebene String */
  public static String getCapName(final String name) {
    return name.substring(0, 1).toUpperCase() + name.substring(1);
  }

  /** Checks the path.
   *
   * @param path The Path
   * @return null or an existing path */
  public static File getFolder(final String path) {
    final File f = new File(path);
    if (f.exists() && f.isDirectory()) {
      return f;
    }
    return null;
  }

  /** Gibt einen Teil des Namens zurück, wenn dieser eine gewisse Länge überschreitet
   *
   * @param name
   * @param length
   * @return */
  public static String getShortName(final String name, final int length) {
    return name.substring(0, name.length() > length - 1 ? length : name.length() - 1);
  }

  /** prüft ob eine Zahl eine valide Integer Zahl ist.
   *
   * @param str der String
   * @return true, wenn Integer */
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

  /** Liest eine Datei ein.
   *
   * @param file Name der Datei
   * @return der Inhalt der Datei */
  static String readFile(final File file) {
    List<String> lines = new ArrayList<>();
    if (file != null && file.exists()) {
      try {
        lines = Files.readAllLines(file.toPath());
      }
      catch (final IOException e) {
        Util.LOG.log(Level.SEVERE, e.toString(), e);
      }
    }
    return lines.stream().map(Object::toString).collect(Collectors.joining(Strings.EOL));
  }

  /** Wandelt einen String in einen Integer um.
   *
   * @param value String
   * @return integer oder 0 bei einem Fehler */
  public static int tryParseInt(final String value) {
    int ret = 0;
    try {
      ret = Integer.parseInt(value);
    }
    catch (final Exception e) {
      Util.LOG.log(Level.FINE, e.getMessage(), e);
    }
    return ret;
  }

  /** Schreibt eine File die über einen Generator erstellt wurde
   *
   * @param f
   * @param gen
   * @param lt */
  public static void writeFile(final File f, final Generator gen, final List<Table> lt) {
    if (!f.getParentFile().exists()) {
      if (f.getParentFile().mkdirs()) {
        Util.LOG.log(Level.WARNING, Strings.ERROR_FOLDER);
      }
    }
    try (BufferedWriter writer = Files.newBufferedWriter(f.toPath())) {
      writer.write(gen.generate(lt));
    }
    catch (final IOException e) {
      Util.LOG.log(Level.SEVERE, e.toString(), e);
    }
  }

  // leerer privater Konstruktor da Klasse komplett statisch ist
  private Util() {}
}
