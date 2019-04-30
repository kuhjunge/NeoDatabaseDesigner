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
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.mach.tools.neodesigner.core.Strings;


/** Path Manager für den PDF Export.
 *
 * @author cd */
public class ModelPathManager {
  private static final Logger LOG = Logger.getLogger(ModelPathManager.class.getName());

  /** Prüfe MikTex Installation.
   *
   * @param checkPath der Installationsordner
   * @return true, wenn nutzbar */
  private static Boolean checkMikTex(final File checkPath) {
    if (checkPath == null || !checkPath.exists()) {
      return false;
    }
    final File checkprocessBuilder = new File(checkPath + Strings.PATH_PROCESS_BUILDER);
    final File checkPdfbuilder = new File(checkPath + Strings.PATH_PDF_BUILDER);
    return checkprocessBuilder.exists() && checkPdfbuilder.exists();
  }

  static void copyFile(final File source, final File dest) throws IOException {
    Files.copy(source.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
  }

  static boolean deleteAFile(final Path path) {
    boolean ret = false;
    try {
      Files.delete(path);
      ret = true;
    }
    catch (final NoSuchFileException e) {
      ModelPathManager.LOG.log(Level.SEVERE, "No such File", e);
    }
    catch (final DirectoryNotEmptyException e) {
      ModelPathManager.LOG.log(Level.SEVERE, "Dir not empty", e);
    }
    catch (final IOException e) {
      ModelPathManager.LOG.log(Level.SEVERE, "IO-Error", e);
    }
    return ret;
  }

  private static boolean deleteAllFiles(final boolean successBefore, final File[] files) {
    boolean success = successBefore;
    for (final File file : files) {
      if (file != null) {
        if (file.isDirectory()) {
          success = success && ModelPathManager.deleteDirContent(file);
        }
        success = ModelPathManager.deleteAFile(file.toPath()) && success;
      }
    }
    return success;
  }

  /** Löscht den Inhalt eines Verzeichnis.
   *
   * @param path der Pfad
   * @return True, wenn erfolgreich alles gelöscht wurde */
  private static boolean deleteDirContent(final File path) {
    boolean b = true;
    if (path != null) {
      final File[] files = path.listFiles();
      if (files != null) {
        b = ModelPathManager.deleteAllFiles(true, files);
      }
      return ModelPathManager.deleteAFile(path.toPath()) && b;
    }
    return false;
  }

  /** Versucht ein MikTex Verzeichnis zu finden.
   *
   * @return gültiges MikTex Verzeichnis oder null */
  private static File findMikTex() {
    File ret = null;
    final List<String> paths = new ArrayList<>();
    paths.add(System.getProperty("user.dir") + File.pathSeparator + Strings.MIKTEX);

    paths.add(Strings.MIKTEX);
    for (final String path : paths) {
      final File test = new File(path);
      if (ModelPathManager.checkMikTex(test)) {
        ret = test;
      }
    }
    return ret;
  }


  private File mikTexPath = null;

  private File outputPath = null;

  private File pdfFile = null;

  private PdfConf conf = null;

  private boolean deleteTemp = true;

  /** Löscht das Temporäre Verzeichnis. */
  public void clean() {
    if (deleteTemp) {
      ModelPathManager.deleteDirContent(outputPath);
      if (outputPath != null && !outputPath.mkdir()) {
        ModelPathManager.LOG.log(Level.SEVERE, Strings.ERROR_FOLDER);
      }
    }
  }

  private void createTempWorkDir() {
    setOutputPath(findOutputPath());
    outputPath.deleteOnExit();
  }

  private File findOutputPath() {
    File wd;
    try {
      wd = Files.createTempDirectory(Strings.SOFTWARENAME).toFile();
    }
    catch (final IOException e) {
      wd = new File(System.getProperty(Strings.USER_HOME) + File.separatorChar + Strings.SOFTWARENAME);
    }
    if (!wd.exists() && !wd.mkdir()) {
      ModelPathManager.LOG.log(Level.SEVERE, Strings.ERROR_FOLDER);
    }
    return wd;
  }

  private File findPdfPath() {
    return new File(System.getProperty(Strings.USER_HOME) + File.separatorChar + Strings.PDF_FILE);
  }

  public String getAuthor() {
    return conf.getPdfAuthor();
  }

  public String getConfigPath() {
    return conf.getConfigPath();
  }

  public File getDataModelSection() {
    return new File(conf.getConfigPath() + Strings.CATEGORYFILE);
  }

  public String getMikTexPath() {
    return mikTexPath != null ? mikTexPath.getAbsolutePath() : Strings.EMPTYSTRING;
  }

  /** getter.
   *
   * @return Datei */
  File getOutputFile() {
    return outputPath;
  }

  public String getOutputPath() {
    return outputPath != null ? outputPath.getAbsolutePath() : Strings.EMPTYSTRING;
  }

  /** getter.
   *
   * @return Datei */
  public File getPdfFile() {
    return pdfFile;
  }

  public String getPdfPath() {
    return pdfFile != null ? pdfFile.getAbsolutePath() : Strings.EMPTYSTRING;
  }

  public String getPdfTitle() {
    return conf.getPdfTitle();
  }

  /** getter.
   *
   * @return Datei */
  File getTexFile() {
    return mikTexPath;
  }

  /** Initalisiert alle Pfade und Dateien.
   *
   * @param config Config Interface, aus dem die Pfade geladen werden können */
  public void init(final PdfConf config) {
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


  /** Öffnet einen Ordner Funktioniert nur unter Windows
   * 
   * @param path der Pfad der geöffnet werden soll
   * @throws IOException IO-Zugriffsfehler */
  public void openPath(final String path) throws IOException {
    if (System.getProperty("os.name").toLowerCase().contains("windows")) {
      Runtime.getRuntime().exec(Strings.EXPLOREREXEC + path);
    }
  }

  /** speichert die Pfade der Software. */
  public void save() {
    conf.setMikTexPath(getMikTexPath());
    conf.setPdfFile(getPdfPath());
    conf.save();
  }

  public void setDeleteTemp(final boolean b) {
    deleteTemp = b;
  }

  /** Setzt den Pfad zum MikTex Software.
   *
   * @param f der Ordner von Mitex
   * @return ob die Installation genutzt werden kann, oder nicht */
  public boolean setMikTexPath(final File f) {
    if (ModelPathManager.checkMikTex(f)) {
      mikTexPath = f;
      return true;
    }
    return false;
  }

  /** setzt den Output Pfad für den PDF Exporter.
   *
   * @param f das Output Verzeichnis
   * @return true, wenn das Verzeichnis nutzbar ist */
  private boolean setOutputPath(final File f) {
    if (f != null && f.exists()) {
      outputPath = f;
      return true;
    }
    return false;
  }

  /** Setzt die PDF Datei für die Ausgabe.
   *
   * @param f die PDF Datei
   * @return True, wenn die Datei nutzbar ist */
  public boolean setPdfFile(final File f) {
    if (f != null && f.isDirectory() && f.exists()) {
      pdfFile = new File(f.getAbsolutePath() + File.separatorChar + Strings.PDF_FILE);
      return true;
    }
    else if (f != null) {
      pdfFile = f;
      return true;
    }
    return false;
  }

  public void setPdfTitle(final String text) {
    conf.setPdfTitle(text);
  }
}
