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

package de.mach.tools.neodesigner.ui;


import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.Parameter;


/**
 * Parameter für die Headless Verarbeitung
 * */
public class Args {
  String getExporttype() {
    return exporttype;
  }

  List<String> getUrl() {
    return url;
  }

  String getCsvfilepath() {
    return csvfilepath;
  }

  public String getTitle() {
    return title;
  }

  public String getAuthor() {
    return author;
  }

  public String getTo() {
    return to;
  }

  public String getFilename() {
    return filename;
  }

  @Parameter
  private List<String> parameters = new ArrayList<>();

  @Parameter(names = "-export", description = "Das Format in das exportiert werden soll [pdf, tex, htmlWeb10, htmlCs]", order = 0)
  private String exporttype = "";

  @Parameter(names = "-url", description = "URL, an der die CSV Dateien liegen. Z.B. http://path.to/csv/<> Mit <> kann ein Platzhalter für die Stelle der CSV Dateien angegeben werden", order = 1)
  private List<String> url = new ArrayList<>();

  @Parameter(names = { "-csvfilepath", "-from" }, description = "Pfad zur CSV Datei", order = 2)
  private String csvfilepath = "";

  @Parameter(names = "-title", description = "Titel des Dokuments", order = 3)
  private String title = "Data Model";

  @Parameter(names = "-filename", description = "Name des Dokuments", order = 4)
  private String filename = "";

  @Parameter(names = "-author", description = "Author des Dokuments", order = 5)
  private String author = "Company";

  @Parameter(names = { "-to", "-d", "--outputDirectory" }, description = "Ausgabeverzeichnis", order = 6)
  private String to = "";
}