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

package de.mach.tools.neodesigner.inex.nexport.pdf;


/** Interface über welches der Path Manager seine Informationen beziehen kann.
 *
 * @author cd */
public interface PdfConf {

  /** Pfad zur MikTex Installation.
   *
   * @return der Pfad */
  String getMikTexPath();

  /** Pfad zum PDF.
   *
   * @return der Pfad */
  String getPdfFile();

  /** gibt den PDF Title zurück.
   *
   * @return Title als String */
  String getPdfTitle();

  /** Pfad des Config Ordners.
   *
   * @return der Pfad */
  String getConfigPath();

  /** Setze den Pfad der MikTex Installation.
   *
   * @param path der Pfad */
  void setMikTexPath(String path);

  /** Setze die PDF Datei.
   *
   * @param path der Pfad */
  void setPdfFile(String path);

  /** Setzt den PDF Title.
   *
   * @param title der neue Title */
  void setPdfTitle(String title);

  /** Setzt den PDF Autohr.
   *
   * @param title der neue Title */
  void setPdfAuthor(String title);

  /** Speichert die Informationen. */
  void save();

  String getPdfAuthor();
}
