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
import de.mach.tools.neodesigner.ui.controller.ModelPrinterController;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Path Manager für den PDF Export.
 *
 * @author cd
 *
 */
public class ModelPathManager {
  private File mikTexPath = null;
  private File outputPath = null;
  private File pdfFile = null;
  private File dataModelTemplateFile = null;
  private LoadPdfConfiguration conf = null;
  private static final Logger LOG = Logger.getLogger(ModelPathManager.class.getName());

  private boolean deleteTemp = true;

  /**
   * Initalisiert alle Pfade und Dateien.
   *
   * @param config
   *          Config Interface, aus dem die Pfade geladen werden können
   */
  public void init(final LoadPdfConfiguration config) {
    conf = config;
    if (!setMikTexPath(new File(config.getMikTexPath()))) {
      setMikTexPath(ModelPathManager.findMikTex());
    }
    if (!setPdfFile(new File(config.getPdfFile()))) {
      setPdfFile(findPdfPath());
    }
    createTempWorkDir();
  }

  public boolean isEveryFileSet() {
    return mikTexPath != null && outputPath != null && pdfFile != null;
  }

  /**
   * Setzt den Pfad zum MikTex Software.
   *
   * @param f
   *          der Ordner von Mitex
   * @return ob die Installation genutzt werden kann, oder nicht
   */
  public boolean setMikTexPath(final File f) {
    if (ModelPathManager.checkMikTex(f)) {
      mikTexPath = f;
      return true;
    }
    return false;
  }

  /**
   * setzt den Output Pfad für den PDF Exporter.
   *
   * @param f
   *          das Output Verzeichnis
   * @return true, wenn das Verzeichnis nutzbar ist
   */
  private boolean setOutputPath(final File f) {
    if (f != null && f.exists()) {
      outputPath = f;
      return true;
    }
    return false;
  }

  /**
   * Setzt die PDF Datei für die Ausgabe.
   *
   * @param f
   *          die PDF Datei
   * @return True, wenn die Datei nutzbar ist
   */
  public boolean setPdfFile(final File f) {
    if (f != null && f.isDirectory() && f.exists()) {
      pdfFile = new File(f.getAbsolutePath() + File.separatorChar + Strings.PDF_FILE);
      return true;
    } else if (f != null) {
      pdfFile = f;
      return true;
    }
    return false;
  }

  /**
   * getter.
   *
   * @return Datei
   */
  public File getMikTexFile() {
    return mikTexPath;
  }

  /**
   * getter.
   *
   * @return Datei
   */
  public File getOutputFile() {
    return outputPath;
  }

  /**
   * getter.
   *
   * @return Datei
   */
  public File getPdfFile() {
    return pdfFile;
  }

  public String getMikTexPath() {
    return mikTexPath != null ? mikTexPath.getAbsolutePath() : Strings.EMPTYSTRING;
  }

  public String getPdfPath() {
    return pdfFile != null ? pdfFile.getAbsolutePath() : Strings.EMPTYSTRING;
  }

  public String getOutputPath() {
    return outputPath != null ? outputPath.getAbsolutePath() : Strings.EMPTYSTRING;
  }

  public void setDeleteTemp(final boolean b) {
    deleteTemp = b;
  }

  /**
   * Versucht ein MikTex Verzeichnis zu finden.
   *
   * @return gültiges MikTex Verzeichnis oder null
   */
  private static File findMikTex() {
    File ret = null;
    final List<String> paths = new ArrayList<>();

    paths.add(ModelPathManager.getThisPath() + Strings.MIKTEX);
    paths.add(Strings.MIKTEX);
    paths.add(Strings.MIKTEX_PATH_P);
    paths.add(Strings.MIKTEX_PATH_E);
    paths.add(Strings.MIKTEX_PATH_D);
    paths.add(Strings.MIKTEX_PATH_C);
    for (final String path : paths) {
      final File test = new File(path);
      if (ModelPathManager.checkMikTex(test)) {
        ret = test;
      }
    }
    return ret;
  }

  private static String getThisPath() {
    String ret = Strings.EMPTYSTRING;
    final CodeSource source = ModelPrinterController.class.getClass().getProtectionDomain().getCodeSource();
    if (source != null) {
      ret = source.getLocation().getPath() + File.separatorChar;
    }
    return ret;
  }

  /**
   * Prüfe MikTex Installation.
   *
   * @param checkPath
   *          der Installationsordner
   * @return true, wenn nutzbar
   */
  private static Boolean checkMikTex(final File checkPath) {
    if (checkPath == null || !checkPath.exists()) {
      return false;
    }
    final File checkprocessBuilder = new File(checkPath + Strings.PATH_PROCESS_BUILDER);
    final File checkPdfbuilder = new File(checkPath + Strings.PATH_PDF_BUILDER);
    return checkprocessBuilder.exists() && checkPdfbuilder.exists();
  }

  private File findPdfPath() {
    return new File(System.getProperty(Strings.USER_HOME) + File.separatorChar + Strings.PDF_FILE);
  }

  private File findOutputPath() {
    File wd;
    try {
      wd = Files.createTempDirectory(Strings.SOFTWARENAME).toFile();
    } catch (final IOException e) {
      wd = new File(System.getProperty(Strings.USER_HOME) + File.separatorChar + Strings.SOFTWARENAME);
    }
    if (!wd.exists() && !wd.mkdir()) {
      ModelPathManager.LOG.log(Level.SEVERE, Strings.ERROR_FOLDER);
    }
    return wd;
  }

  private void createTempWorkDir() {
    setOutputPath(findOutputPath());
    outputPath.deleteOnExit();
  }

  public void openPath(final String path) throws IOException {
    Runtime.getRuntime().exec(Strings.EXPLOREREXEC + path);
  }

  /**
   * Löscht das Temporäre Verzeichnis.
   */
  public void clean() {
    if (deleteTemp) {
      ModelPathManager.deleteDirContent(outputPath);
      if (outputPath != null && !outputPath.mkdir()) {
        ModelPathManager.LOG.log(Level.SEVERE, Strings.ERROR_FOLDER);
      }
    }
  }

  /**
   * Löscht den Inhalt eines Verzeichnis.
   *
   * @param path
   *          der Pfad
   * @return True, wenn erfolgreich alles gelöscht wurde
   */
  private static boolean deleteDirContent(final File path) {
    boolean b = true;
    if (path != null) {
      final File[] files = path.listFiles();
      if (files != null) {
        b = ModelPathManager.deleteAllFiles(b, files);
      }
      return path.delete() && b;
    }
    return false;
  }

  private static boolean deleteAllFiles(final boolean a, final File[] files) {
    boolean b = a;
    for (final File file : files) {
      if (file != null) {
        if (file.isDirectory()) {
          b = b && ModelPathManager.deleteDirContent(file);
        }
        b = file.delete() && b;
      }
    }
    return b;
  }

  /**
   * Läd das Datamodeltemplate.
   *
   * @param wd
   *          das Arbeitsverzeichnis
   * @return das Datamodeltemplate
   * @throws IOException
   *           IO Fehler
   */
  File getDataModelTemplate(final File wd) throws IOException {
    if (dataModelTemplateFile == null || !dataModelTemplateFile.exists()) {
      dataModelTemplateFile = createFileFromThis(new File(conf.getConfigPath()), Strings.DATAMODEL_TEMPLATE_NAME);
    }
    final File template = new File(wd.getAbsolutePath() + File.separatorChar + Strings.DATAMODEL_TEMPLATE_NAME);
    ModelPathManager.copyFile(dataModelTemplateFile, template);
    return template;
  }

  public File getDataModelSection() {
    return new File(conf.getConfigPath() + Strings.CATEGORYFILE);
  }

  private File createFileFromThis(final File wd, final String resName) throws IOException {
    final File template = new File(wd.getAbsolutePath() + File.separatorChar + resName);
    if (!template.exists()) {
      final InputStream stream = ModelPathManager.class.getResourceAsStream(Strings.RES_PATH + resName);
      Files.copy(stream, template.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }
    return template;
  }

  static void copyFile(final File source, final File dest) throws IOException {
    Files.copy(source.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
  }

  /**
   * speichert die Pfade der Software.
   */
  public void save() {
    conf.setMikTexPath(getMikTexPath());
    conf.setPdfFile(getPdfPath());
    conf.save();
  }

  public String getConfigPath() {
    return conf.getConfigPath();
  }

  public String getPdfTitle() {
    return conf.getPdfTitle();
  }

  public void setPdfTitle(final String text) {
    conf.setPdfTitle(text);
  }
}
