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

package de.mach.tools.neodesigner.core.nexport.pdf;

import de.mach.tools.neodesigner.core.Strings;
import de.mach.tools.neodesigner.core.datamodel.Table;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.HostServices;

/**
 * Klasse zum Erstellen eines PDFs.
 *
 * @author cd
 *
 */
public class ModelPrinter {
  private static final Logger LOG = Logger.getLogger(ModelPrinter.class.getName());
  private final ModelPathManager mpm;
  private final File outputPath;
  private final File texFile;
  private final File pdflatexExecutable;
  private final LaTexReportGenerator generator;

  /**
   * Konstruktor.
   *
   * @param mpm
   *          der Path Manager
   */
  public ModelPrinter(final ModelPathManager mpm) {
    this.mpm = mpm;
    outputPath = mpm.getOutputFile();
    texFile = new File(mpm.getOutputPath() + Strings.PATH_DATA_MODEL);
    pdflatexExecutable = new File(mpm.getMikTexPath() + Strings.PATH_PDF_BUILDER);
    generator = new LaTexReportGenerator(outputPath);
  }

  /**
   * versucht ein PDF zu öffnen.
   *
   * @param hostServices
   *
   * @throws IOException
   *           Fehler beim öffnen
   */
  public void oeffnePdf(final HostServices hostServices) throws IOException {
    File output = mpm.getPdfFile();
    if (output.exists() && !output.delete()) {
      ModelPrinter.LOG.log(Level.SEVERE, Strings.ALTEXT_DELETEERROR);
    }
    final File pdf = generator.getPdf();
    if (!pdf.equals(mpm.getPdfFile())) {
      try {
        ModelPathManager.copyFile(pdf, output);
      } catch (final Exception e) {
        output = pdf;
      }
    } else {
      output = pdf;
    }
    hostServices.showDocument(output.getAbsolutePath());
  }

  /**
   * erstellt das Indexverzeichnis.
   *
   * @throws IOException
   *           Fehler bei der Verarbeitung
   * @throws InterruptedException
   *           Fehler bei der Verarbeitung
   */
  public void erstelleIndex() throws IOException, InterruptedException {
    processBuilder(outputPath, mpm.getMikTexFile());
  }

  /**
   * erstellt das PDF.
   *
   * @throws IOException
   *           Fehler bei der Verarbeitung
   */
  public void erstellePdf() {
    generator.generate(pdflatexExecutable, texFile, outputPath, false);
  }

  /**
   * Liest das Datenmodell ein und verarbeitet es im Template.
   *
   * @param lt
   *          das Datenmodell
   * @throws IOException
   *           Fehler bei der Verabeitung des Templates
   */
  public void verarbeiteTemplate(final List<Table> lt, final String title) throws IOException {
    // final LineProcessor lp = createDataModel(lt);
    // createTemplate(lp, outputPath, texFile, title);
  }

  private void processBuilder(final File wd, final File miketex) throws IOException, InterruptedException {
    final ProcessBuilder pb = new ProcessBuilder(miketex.getCanonicalPath() + Strings.PATH_PROCESS_BUILDER,
        "Datenmodell");
    pb.directory(wd);
    final Process process = pb.start();
    process.waitFor();
  }

  /*
   * private void createTemplate(final LineProcessor lineProcessor, final File
   * workingDir, final File out, final String title) throws IOException { //
   * generator.replace("datamodel", lineProcessor.getDatamodel());
   * generator.replace("title", title);
   * generator.parse(mpm.getDataModelTemplate(workingDir), out); }
   * 
   * private LineProcessor createDataModel(final List<Table> lt) throws
   * IOException { final Properties properties = new Properties(); try (final
   * InputStream stream = new FileInputStream(mpm.getDataModelSection())) {
   * properties.load(stream); final LineProcessor lineProcessor = new
   * LineProcessor(properties); lineProcessor.generate(lt); return lineProcessor;
   * } }
   */
}
