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

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Files;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

/**
 * Ersetzer Engine um das Latex Dokument zu erstellen.
 *
 * @author cd
 *
 */
class LaTexReportGenerator {
  private static final Logger LOG = Logger.getLogger(LaTexReportGenerator.class.getName());
  private final VelocityEngine ve = new VelocityEngine();
  private final VelocityContext context = new VelocityContext();
  private File result = null;

  /**
   * Initialisiert den Ersetzer.
   *
   */
  LaTexReportGenerator(final File path) {
    /* first, get and initialize an engine */
    final Properties props = new Properties();
    props.put(Strings.PROP_PATH, path.getAbsolutePath());
    props.put(Strings.PROP_IN_ENCODING, Strings.ENCODINGUTF8);
    props.put(Strings.PROP_OUT_ENCODING, Strings.ENCODINGUTF8);
    ve.init(props);
  }

  void replace(final String key, final Object value) {
    /* create a context and add data */
    context.put(key, value);
  }

  /**
   * parst das Dokument.
   *
   * @param template
   *          das zu parsende Template
   * @param target
   *          die Zieldatei
   * @return True, wenn erfolgreich
   */
  boolean parse(final File template, final File target) {
    /* next, get the Template */
    if (template.exists()) {
      final Template t = ve.getTemplate(template.getName(), Strings.ENCODINGUTF8);
      /* now render the template into a StringWriter */
      final StringWriter writer = new StringWriter();
      t.merge(context, writer);
      /* show the World */
      return writeFile(target, writer.toString());
    }
    return false;
  }

  /**
   * Schreibt eine Datei.
   *
   * @param f
   *          Zieldatei
   * @param content
   *          Inhalt
   * @return True, wenn erfolgreich
   */
  private boolean writeFile(final File f, final String content) {
    try (BufferedWriter writer = Files.newBufferedWriter(f.toPath())) {
      writer.write(content);
    } catch (final IOException e) {
      LaTexReportGenerator.LOG.log(Level.SEVERE, e.toString(), e);
      return false;
    }
    return true;
  }

  /**
   * Generiert ein PDF.
   *
   * @param pdflatexExecutable
   *          MikTex Executable vom PDF Creator
   * @param texFile
   *          Tex Datei
   * @param outputPath
   *          der Ergebnispfad
   * @param debug
   *          Debug Ausgabe auf der Konsole (und nicht in eine seperate Datei)
   * @return true, wenn der Prozess läuft
   */
  boolean generate(final File pdflatexExecutable, final File texFile, final File outputPath, final boolean debug) {
    try {
      processBuilder(outputPath, pdflatexExecutable, texFile, debug);
    } catch (IOException | InterruptedException e) {
      LaTexReportGenerator.LOG.log(Level.SEVERE, e.toString(), e);
      return false;
    }
    result = new File(
        texFile.getAbsolutePath().replaceFirst(Strings.REGEX_REPLACE_EXT, Strings.EMPTYSTRING) + Strings.EXT_PDF);
    return true;
  }

  private void processBuilder(final File outputPath, final File pdflatexExecutable, final File texFile,
      final boolean debug) throws IOException, InterruptedException {
    final ProcessBuilder pb = new ProcessBuilder(pdflatexExecutable.getAbsolutePath(), Strings.INTERACTION_NONSTOPMODE,
        texFile.getName());
    if (debug) {
      pb.redirectOutput(Redirect.INHERIT);
      pb.redirectError(Redirect.INHERIT);
    } else {
      final File output = new File(outputPath.getAbsolutePath() + File.separatorChar + Strings.NAME_PROCESSLOG);
      pb.redirectOutput(output);
      final File error = new File(outputPath.getAbsolutePath() + File.separatorChar + Strings.NAME_ERRORLOG);
      pb.redirectError(error);
    }
    pb.directory(outputPath);
    final Process process = pb.start();
    process.waitFor();
  }

  /**
   * Gibt den Speicherort der PDF zurück.
   *
   * @return die PDF als Datei
   */
  public File getPdf() {
    return result;
  }
}
