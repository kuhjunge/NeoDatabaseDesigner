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


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;

import com.beust.jcommander.JCommander;

import de.mach.tools.neodesigner.core.ConfigSaverImpl;
import de.mach.tools.neodesigner.core.Model;
import de.mach.tools.neodesigner.core.ModelImpl;
import de.mach.tools.neodesigner.core.Strings;
import de.mach.tools.neodesigner.core.Util;
import de.mach.tools.neodesigner.inex.Generator;
import de.mach.tools.neodesigner.inex.ImportTask;
import de.mach.tools.neodesigner.inex.nexport.HtmlConstraintListGenerator;
import de.mach.tools.neodesigner.inex.nexport.TexGenerator;
import de.mach.tools.neodesigner.inex.nexport.pdf.ModelPathManager;
import de.mach.tools.neodesigner.inex.nexport.pdf.ModelPrinter;
import de.mach.tools.neodesigner.inex.nexport.pdf.ModelPrinterImpl;


class Headless {
  private static final Logger LOG = Logger.getLogger(Headless.class.getName());
  private final ModelPathManager mpm = new ModelPathManager();
  private final Model ndbm = new ModelImpl(new ConfigSaverImpl(de.mach.tools.neodesigner.core.Strings.SOFTWARENAME),
                                           new ConfigSaverImpl(de.mach.tools.neodesigner.core.Strings.SOFTWARENAME,
                                                               de.mach.tools.neodesigner.core.Strings.CATEGORYFILE));

  private Headless(String versName, String versAuthor) {
    ndbm.connect();
    mpm.init(ndbm.getPdfConfig());
    ndbm.getPdfConfig().setPdfTitle(versName);
    ndbm.getPdfConfig().setPdfAuthor(versAuthor);
  }

  public static void start(String[] args) {
    // Argumente parsen
    Args argv = new Args();
    JCommander.newBuilder().addObject(argv).build().parse(args);
    // Argumente verarbeiten
    Headless headless = new Headless(argv.getTitle(), argv.getAuthor());
    if (headless.loadData(argv)) {
      switch (argv.getExporttype()) {
        case "pdf":
          headless.generateTexPdf(argv.getTo(), argv.getFilename(), true);
          break;
        case "tex":
          headless.generateTexPdf(argv.getTo(), argv.getFilename(), false);
          break;
        case "htmlWeb10":
          headless.generateHtml(argv.getTo(), argv.getFilename(), true);
          break;
        case "htmlCs":
          headless.generateHtml(argv.getTo(), argv.getFilename(), false);
          break;
        default:
          LOG.log(Level.SEVERE, "Wrong Parameter Usage [pdf, tex, htmlWeb10, htmlCs]");
          break;
      }
    }
    headless.exit();
  }

  private boolean loadData(Args argv) {
    boolean ret;
    if (argv.getCsvfilepath().length() > 0) {
      ret = loadDataFromCsvFiles(argv.getCsvfilepath());
    }
    else {
      ret = loadDataFromUrl(argv.getUrl());
    }
    return ret;
  }

  private boolean loadDataFromUrl(List<String> urls) {
    boolean ret = false;
    try {
      List<String> content = generateCsv(urls);
      final ImportTask importTask = ndbm.importFolderTask(new File("").getAbsolutePath(), content.remove(0),
                                                          content.remove(0), content.remove(0), content.remove(0),
                                                          content.remove(0), content.remove(0), content.remove(0));
      executeHeadlessProcessing(importTask);
      ret = true;
    }
    catch (IOException e) {
      Headless.LOG.log(Level.SEVERE, ".csv nicht gefunden -> Unvollständige Importurl!", e);
    }
    return ret;
  }

  private boolean loadDataFromCsvFiles(String inpPath) {
    boolean ret = false;
    final File in = new File(inpPath);
    if (in.exists()) {
      if (new File(inpPath + File.separator + "MetaTbls.csv").exists()) {
        executeHeadlessProcessing(Util.loadCsvFiles(in, ndbm));
        ret = true;
      }
      else {
        Headless.LOG.log(Level.SEVERE, () -> "MetaTbls.csv nicht gefunden -> Unvollständiger Importordner! " + inpPath);
      }
    }
    else {
      Headless.LOG.log(Level.SEVERE, () -> "Pfad nicht korrekt! " + inpPath);
    }
    return ret;
  }

  private void executeHeadlessProcessing(ImportTask importTask) {
    // Wenn Dateien für Import existieren
    importTask.setOnFailed(t -> Headless.LOG.log(Level.SEVERE, importTask.getException().getMessage(),
                                                 importTask.getException()));
    importTask.setOnSucceeded(t -> ndbm.importTables(importTask.getList()));
    // Starte Prozess für CSV Import
    final Thread t = new Thread(importTask);
    t.start();
    try {
      t.join();
    }
    catch (final InterruptedException e) {
      Headless.LOG.log(Level.SEVERE, "Fehler Prozessmanagement", e);
      t.interrupt();
    }
  }

  /** Generiert eine TEX Datei oder eine PDF Datei
   *
   * @param expPath der Pfad, nach dem die PDF Dateien exportiert werden sollen
   * @param pdfexport True bei PDF, false bei TEX */
  private void generateTexPdf(final String expPath, final String filename, final boolean pdfexport) {
    if (pdfexport) {
      if (expPath.length() > 1) {
        mpm.setPdfFile(new File(expPath));
      }
      processPdf();
    }
    else {
      processTex(new File(expPath + File.separatorChar + (filename.length() > 0 ? filename : "Datenmodell.tex")));
    }
  }

  private void generateHtml(final String expPath, final String filename, final boolean isWeb10) {
    final Generator g = new HtmlConstraintListGenerator(isWeb10);
    Util.writeFile(new File(expPath + File.separatorChar + (filename.length() > 0 ? filename : "Constraints.htm")), g,
                   ndbm.getAllTables());
  }

  private List<String> generateCsv(List<String> baseurls) throws IOException {
    Headless.LOG.log(Level.INFO, "Beginn Download");
    List<String> files = new ArrayList<>();
    boolean finished = false;
    for (String url : baseurls) {
      if (!finished) {
        files.clear();
        boolean success = true;
        for (int i = 0; i < Util.CSV_FILES.length; i++) {
          if (success) {
            Headless.LOG.log(Level.INFO, "Load:" + url.replace("<>", Util.CSV_FILES[i]));
            String csv = Headless.downloadFileFromUrl(url.replace("<>", Util.CSV_FILES[i]));
            if (csv.startsWith("<!DOCTYPE html>") || csv.length() < 5) {
              success = false;
            }
            else {
              files.add(csv);
            }
          }
        }
        if (success) {
          InputStream in = new URL(url.replace("<>", Strings.CATEGORYFILE)).openStream();
          Files.copy(in, Paths.get(ndbm.getGuiConf().getConfigPath() + File.separator + Strings.CATEGORYFILE),
                     StandardCopyOption.REPLACE_EXISTING);
          finished = true;
        }
      }
    }
    return files;
  }

  private void exit() {
    ndbm.deleteDatabase();
    System.exit(0);
  }

  /** Task zur Erstellung des PDFs.
   *
   * @return Task */
  private Task<Boolean> pdfTask() {
    return new Task<Boolean>() {
      @Override
      protected Boolean call() {
        return true;
      }

      @Override
      public void run() {
        try {
          final ModelPrinter mp = new ModelPrinterImpl(mpm);
          mp.verarbeiteTemplate(ndbm.getAllTables(), mpm.getPdfTitle(), mpm.getAuthor(), ndbm.getCategoryTranslation());
          Headless.LOG.log(Level.INFO, () -> "PDF wird erstellt 1/3");
          mp.erstellePdf();
          mp.erstelleIndex();
          Headless.LOG.log(Level.INFO, () -> "PDF wird erstellt 2/3");
          mp.erstellePdf();
          Headless.LOG.log(Level.INFO, () -> "PDF wird erstellt 3/3");
          mp.erstellePdf();
          mp.kopierePdf(mpm.getPdfFile());
          mpm.clean();
          Headless.LOG.log(Level.INFO, () -> "PDF erstellt!");
        }
        catch (IOException | InterruptedException e) {
          mpm.clean();
          Headless.LOG.log(Level.SEVERE, e.toString(), e);
        }
      }
    };
  }

  private void processPdf() {
    if (mpm.isEveryFileSet()) {
      mpm.setPdfTitle(mpm.getPdfTitle());
      mpm.setDeleteTemp(true);
      // Import abgeschlossen, Starte PDF erstellung
      final Task<Boolean> pdftask = pdfTask();
      final Thread g = new Thread(pdftask);
      g.start();
      try {
        g.join();
      }
      catch (final InterruptedException e) {
        Headless.LOG.log(Level.SEVERE, "Fehler Prozessmanagement", e);
        g.interrupt();
      }
    }
  }

  private void processTex(final File out) {
    final File categories = new File(ndbm.getPdfConfig().getConfigPath() + Strings.CATEGORYFILE);
    if (categories.exists()) {
      final Generator g = new TexGenerator(ndbm.getPdfConfig().getPdfTitle(), ndbm.getPdfConfig().getPdfAuthor(),
                                           ndbm.getCategoryTranslation());
      Util.writeFile(out, g, ndbm.getAllTables());
    }
    else {
      Headless.LOG.log(Level.SEVERE, "Could not find " + Strings.CATEGORYFILE + " in Configurationsfolder - Abort! "
                                     + ndbm.getPdfConfig().getConfigPath());
    }
  }

  private static String downloadFileFromUrl(String urlstr) {
    StringBuilder sb = new StringBuilder();
    try (BufferedReader in = new BufferedReader(new InputStreamReader(new URL(urlstr).openStream()))) {
      String content;
      while ((content = in.readLine()) != null) {
        sb.append(content);
        sb.append(Strings.EOL);
      }
    }
    catch (IOException e) {
      Headless.LOG.log(Level.WARNING, "Couldn't access URL: " + urlstr);
    }
    return sb.toString();
  }
}
