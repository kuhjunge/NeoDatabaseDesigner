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
import java.lang.ProcessBuilder.Redirect;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.HostServices;

import de.mach.tools.neodesigner.core.Strings;
import de.mach.tools.neodesigner.core.Util;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.inex.Generator;
import de.mach.tools.neodesigner.inex.nexport.TexGenerator;


/** Klasse zum Erstellen eines PDFs.
 *
 * @author cd */
public class ModelPrinterImpl implements ModelPrinter {
  private static final Logger LOG = Logger.getLogger(ModelPrinterImpl.class.getName());
  private final ModelPathManager mpm;
  private final File outputPath;
  private final File texFile;

  private File result = null;

  /** Konstruktor.
   *
   * @param mpm der Path Manager */
  public ModelPrinterImpl(final ModelPathManager mpm) {
    this.mpm = mpm;
    outputPath = mpm.getOutputFile();
    texFile = new File(mpm.getOutputPath() + Strings.PATH_DATA_MODEL);
  }

  @Override
  public void erstelleIndex() throws IOException, InterruptedException {
    processBuilder(outputPath, false, mpm.getTexFile().getCanonicalPath() + Strings.PATH_PROCESS_BUILDER,
                   "Datenmodell");
  }

  @Override
  public void erstellePdf() {
    final boolean debug = false;
    try {
      processBuilder(outputPath, debug, mpm.getMikTexPath() + Strings.PATH_PDF_BUILDER, Strings.INTERACTION_NONSTOPMODE,
                     texFile.getName());
    }
    catch (IOException | InterruptedException e) {
      ModelPrinterImpl.LOG.log(Level.SEVERE, e.toString(), e);
    }
    result = new File(texFile.getAbsolutePath().replaceFirst(Strings.REGEX_REPLACE_EXT, Strings.EMPTYSTRING)
                      + Strings.EXT_PDF);
  }

  @Override
  public File kopierePdf(final File output) {
    File ret = output;
    if (ret.exists() && !ModelPathManager.deleteAFile(ret.toPath())) {
      ModelPrinterImpl.LOG.log(Level.SEVERE, Strings.ALTEXT_DELETEERROR);
    }
    final File pdf = result;
    if (!pdf.equals(mpm.getPdfFile())) {
      try {
        ModelPathManager.copyFile(pdf, ret);
      }
      catch (final Exception e) {
        ret = pdf;
      }
    }
    else {
      ret = pdf;
    }
    return ret;
  }

  @Override
  public void oeffnePdf(final File output, final HostServices hostServices) {
    hostServices.showDocument(output.getAbsolutePath());
  }

  private void processBuilder(final File outputPath, final boolean debug, final String... arguments)
      throws IOException, InterruptedException {
    final ProcessBuilder pb = new ProcessBuilder(arguments);
    if (debug) {
      ModelPrinterImpl.LOG.log(Level.WARNING, () -> outputPath.getAbsolutePath() + " | " + Arrays.toString(arguments));
      pb.redirectOutput(Redirect.INHERIT);
      pb.redirectError(Redirect.INHERIT);
    }
    else {
      final File output = new File(outputPath.getAbsolutePath() + File.separatorChar + Strings.NAME_PROCESSLOG);
      pb.redirectOutput(output);
      final File error = new File(outputPath.getAbsolutePath() + File.separatorChar + Strings.NAME_ERRORLOG);
      pb.redirectError(error);
    }
    pb.directory(outputPath);
    final Process process = pb.start();
    process.waitFor();
  }

  @Override
  public void verarbeiteTemplate(final List<Table> lt, final String title, final String author,
                                 final Map<String, String> catTransl) {
    final Generator g = new TexGenerator(title, author, catTransl);
    Util.writeFile(texFile, g, lt);
  }
}
