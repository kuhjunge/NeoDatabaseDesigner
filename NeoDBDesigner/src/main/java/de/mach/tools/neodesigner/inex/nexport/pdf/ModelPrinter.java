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


import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javafx.application.HostServices;

import de.mach.tools.neodesigner.core.datamodel.Table;


public interface ModelPrinter {

  /** erstellt das Indexverzeichnis.
   *
   * @throws IOException Fehler bei der Verarbeitung
   * @throws InterruptedException Fehler bei der Verarbeitung */
  void erstelleIndex() throws IOException, InterruptedException;

  /** erstellt das PDF. */
  void erstellePdf();

  /** Kopiert die PDF vom Temp Order in das Zielverzeichnis, wenn nicht möglich gibt es die Temp Datei der PDF zurück
   *
   * @param output -> mpm.getPdfFile()
   * @return die Zieldatei als File */
  File kopierePdf(File output);

  /** versucht ein PDF zu öffnen.
   *
   * @param hostServices notwendiger Parameter für das Öffnen der PDF */
  void oeffnePdf(File output, HostServices hostServices);

  /** Liest das Datenmodell ein und verarbeitet es im Template.
   *
   * @param lt das Datenmodell
   * @param title Der Titel im PDF
   * @param author Der Author des PDFs */
  void verarbeiteTemplate(final List<Table> lt, final String title, final String author, Map<String, String> catTransl);
}
