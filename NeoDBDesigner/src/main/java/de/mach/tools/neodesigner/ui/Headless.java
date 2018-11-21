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


import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;

import de.mach.tools.neodesigner.core.Model;
import de.mach.tools.neodesigner.core.ModelImpl;
import de.mach.tools.neodesigner.core.Strings;
import de.mach.tools.neodesigner.core.Util;
import de.mach.tools.neodesigner.core.category.CategoryTranslator;
import de.mach.tools.neodesigner.core.nexport.Generator;
import de.mach.tools.neodesigner.core.nexport.TexGenerator;
import de.mach.tools.neodesigner.core.nexport.pdf.ModelPathManager;
import de.mach.tools.neodesigner.core.nexport.pdf.ModelPrinter;
import de.mach.tools.neodesigner.core.nexport.pdf.ModelPrinterImpl;
import de.mach.tools.neodesigner.core.nimport.ImportTask;
import de.mach.tools.neodesigner.database.cypher.DatabaseConnectorImpl;


public class Headless {
  private static final Logger LOG = Logger.getLogger(Headless.class.getName());
  private final ModelPathManager mpm = new ModelPathManager();
  private final Model ndbm = new ModelImpl(new DatabaseConnectorImpl(), new CategoryTranslator());
  private boolean pdfexport;

  /** Generiert eine TEX Datei oder eine PDF Datei
   * 
   * @param inpPath der Pfad, der die Zieldateien enthält
   * @param expPath der Pfad, nach dem die PDF Dateien exportiert werden sollen
   * @param versName Die Version
   * @param pdfExp Boolean soll es als PDF exportiert werden? [benötigt MikTex] */
  public Headless(final String inpPath, final String expPath, final String versName, final String versAuthor,
                  final boolean pdfExp) {
    pdfexport = pdfExp;
    ndbm.connectLocalDb();
    mpm.init(ndbm.getPdfConfig());
    ndbm.getPdfConfig().setPdfTitle(versName);
    ndbm.getPdfConfig().setPdfAuthor(versAuthor);

    final File in = new File(inpPath);
    if (in.exists()) {
      if (new File(inpPath + File.separator + "MetaTbls.csv").exists()) {

        executeHeadlessProcessing(in, expPath);
      }
      else {
        Headless.LOG.log(Level.SEVERE, () -> "MetaTbls.csv nicht gefunden -> Unvollständiger Importordner! " + inpPath);
      }
    }
    else {
      Headless.LOG.log(Level.SEVERE, () -> "Pfad nicht korrekt! " + inpPath);
    }
    Headless.LOG.log(Level.INFO, "Finished Export");
    ndbm.disconnectDb();
    System.exit(0);
  }

  private void executeHeadlessProcessing(final File f, final String expPath) {
    // Wenn Dateien für Import existieren
    final ImportTask importTask = ndbm.importFolderTask(f);
    importTask.setOnFailed(t -> Headless.LOG.log(Level.SEVERE, importTask.getException().getMessage(),
                                                 importTask.getException()));
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
    // Import PDF fertig
    if (pdfexport) {
      if (expPath.length() > 1) {
        mpm.setPdfFile(new File(expPath));
      }
      processPdf();
    }
    else {
      processTex(new File(expPath + File.separatorChar + "Datenmodell.tex"));
    }
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
          mp.verarbeiteTemplate(ndbm.getAllTables(), mpm.getPdfTitle(), mpm.getAuthor());
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
      final Generator g = new TexGenerator(ndbm.getPdfConfig().getPdfTitle(), ndbm.getPdfConfig().getAuthor(),
                                           categories);
      Util.writeFile(out, g, ndbm.getAllTables());
    }
    else {
      Headless.LOG.log(Level.SEVERE, "Could not find " + Strings.CATEGORYFILE + " in Configurationsfolder - Abort! "
                                     + ndbm.getPdfConfig().getConfigPath());
    }
  }
}
