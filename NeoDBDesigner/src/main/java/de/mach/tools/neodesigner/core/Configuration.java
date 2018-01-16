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

package de.mach.tools.neodesigner.core;

import de.mach.tools.neodesigner.core.nexport.pdf.LoadPdfConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Klasse die die Einstellung der Software von der Festplatte läd und dort
 * wieder speichert.
 *
 * @author Chris Deter
 *
 */
class Configuration implements LoadPdfConfiguration {

  private final File configPath = new File(System.getenv(Strings.CONF_LOC) + File.separatorChar + Strings.SOFTWARENAME);
  private File configFile = null;
  private static final Logger LOG = Logger.getLogger(Configuration.class.getName());
  // Config Values
  private String neoDbStarterLocation = Strings.EMPTYSTRING;
  private String addrOfDb = Strings.CONF_DEFAULT_ADR;
  private String user = Strings.CONF_DEFAULT_USRPW;
  private String pw = Strings.CONF_DEFAULT_USRPW;
  private String pdfTitle = Strings.CONF_DEFAULT_TITLE;
  private int wordLength = 18;
  private int uniqueTableLength = 15;
  private String[] selectDataType = { "Counter", "Lookup", "Date", "Amount", "Boolean", "String:1", "String:2",
      "String:3", "String:4", "String:5", "String:10", "String:20", "String:27", "String:30", "String:40", "String:50",
      "String:120", "String:254", "Blob", "Clob" };
  private String mikTexLoc = Strings.EMPTYSTRING;
  private String pdfLoc = Strings.EMPTYSTRING;
  private String pathImportSql = Strings.DEFAULTPATH;
  private String pathImportCat = Strings.DEFAULTPATH;
  private String pathImportCsv = Strings.DEFAULTPATH;
  private String pathExportSql = Strings.DEFAULTPATH;
  private String pathExportCsv = Strings.DEFAULTPATH;
  private String pathExportCql = Strings.DEFAULTPATH;

  public String getPathImportSql() {
    return pathImportSql;
  }

  public void setPathImportSql(final String pathImportSql) {
    this.pathImportSql = pathImportSql;
  }

  public String getPathImportCat() {
    return pathImportCat;
  }

  public void setPathImportCat(final String pathImportCat) {
    this.pathImportCat = pathImportCat;
  }

  public String getPathImportCsv() {
    return pathImportCsv;
  }

  public void setPathImportCsv(final String pathImportCsv) {
    this.pathImportCsv = pathImportCsv;
  }

  public String getPathExportSql() {
    return pathExportSql;
  }

  public void setPathExportSql(final String pathExportSql) {
    this.pathExportSql = pathExportSql;
  }

  public String getPathExportCsv() {
    return pathExportCsv;
  }

  public void setPathExportCsv(final String pathExportCsv) {
    this.pathExportCsv = pathExportCsv;
  }

  public String getPathExportCql() {
    return pathExportCql;
  }

  public void setPathExportCql(final String pathExportCql) {
    this.pathExportCql = pathExportCql;
  }

  /**
   * setter.
   *
   * @param i
   *          eindeutige Länge von Tabellen
   */
  public void setUniqueTableLength(final int i) {
    uniqueTableLength = i;
  }

  /**
   * getter.
   *
   * @return eindeutige Länge von Tabellen
   */
  public int getUniqueTableLength() {
    return uniqueTableLength;
  }

  /**
   * Getter.
   *
   * @return Speicherort von der Neo4J Installation
   */
  public String getNeoDbStarterLocation() {
    return neoDbStarterLocation;
  }

  /**
   * Setter.
   *
   * @param str
   *          Ort der Neo4J Installation
   */
  public void setNeoDbStarterLocation(final String str) {
    neoDbStarterLocation = str;
  }

  /**
   * Getter.
   *
   * @return Adresse der Datenbank
   */
  public String getAddrOfDb() {
    return addrOfDb;
  }

  /**
   * Setter.
   *
   * @param addrOfDb
   *          die neue Datenbankadresse
   */
  public void setAddrOfDb(final String addrOfDb) {
    this.addrOfDb = addrOfDb;
  }

  /**
   * Getter.
   *
   * @return DB User
   */
  public String getUser() {
    return user;
  }

  /**
   * Setter
   *
   * @param user
   *          DB User.
   */
  public void setUser(final String user) {
    this.user = user;
  }

  /**
   * Getter.
   *
   * @return passowort
   */
  public String getPw() {
    return pw;
  }

  /**
   * Setter.
   *
   * @param pw
   *          password
   */
  public void setPw(final String pw) {
    this.pw = pw;
  }

  /**
   * Getter.
   *
   * @return Datatypes für Feldtyp Dropdown in View
   */
  public String[] getSelectDataType() {
    return selectDataType;
  }

  /**
   * Setter.
   *
   * @param selectDataType
   *          Datatypes für Feldtyp Dropdown in View
   */
  public void setSelectDataType(final String[] selectDataType) {
    this.selectDataType = selectDataType;
  }

  /**
   * Getter.
   *
   * @return Max Wordlänge für Prüfung
   */
  public int getWordLength() {
    return wordLength;
  }

  /**
   * Setter.
   *
   * @param l
   *          Max Wortlänge für Prüfung
   */
  public void setWordLength(final int l) {
    wordLength = l;
  }

  /**
   * Hilfsfunktion, welche Arrays zu dem String "Element 1;Element 2;Element 3"
   * formatiert. Wird benötigt um das Array mit den Typen von Feldern zu
   * speichern.
   *
   * @param input
   *          ein Array mit Werten
   * @return kommagetrennte Array Elemente
   */
  private String arrayToString(final String[] input) {
    final StringBuilder builder = new StringBuilder();
    for (final String value : input) {
      builder.append(Strings.SEMICOLON + value.trim());
    }
    builder.deleteCharAt(0);
    return builder.toString();
  }

  /**
   * initialisiert das Objekt und läd die Config von der Festplatte.
   *
   * @throws Exception
   *           Speichern fehlgeschlagen
   */
  void init() {
    // ist der Pfad vorhanden
    if (!configPath.exists() && !configPath.mkdirs()) {
      Configuration.LOG.log(Level.SEVERE, Strings.ERROR_FOLDER);
    }
    // die Config Datei
    configFile = new File(
        configPath.getAbsolutePath() + File.separatorChar + Strings.SOFTWARENAME + Strings.CONFIGFILEENDING);
    // ist die Config Datei vorhanden - dann lade die daten
    if (configFile.exists()) {
      try (FileInputStream fis = new FileInputStream(configFile)) {
        loadProperties(fis);
      } catch (final IOException e) {
        if (configFile.exists()) {
          Configuration.LOG.log(Level.SEVERE, e.toString(), e);
          // Lösche Config, wenn ein Fehler aufgetreten ist in der Config
          if (configFile.delete()) {
            Configuration.LOG.log(Level.WARNING, Strings.CONF_ERR, e);
          }
        }
      }
    }
  }

  /**
   * Speichert die Datenbankverbindungsdaten (& alles andere auch).
   *
   * @param addr
   *          die Adresse der Datenbank
   * @param user
   *          der Nutzer der Datenbank
   * @param pw
   *          das Passwort der Datenbank
   */
  void save(final String addr, final String user, final String pw) {
    setAddrOfDb(addr);
    setUser(user);
    setPw(pw);
    save();
  }

  /**
   * Speichert die Config File auf die Festplatte.
   */
  @Override
  public void save() {
    try (FileOutputStream fos = new FileOutputStream(configFile)) {
      saveProperties(fos);
    } catch (final IOException e) {
      Configuration.LOG.log(Level.SEVERE, e.toString(), e);
    }
  }

  /**
   * Speichert die Werte auf der Festplatte.
   *
   * @param fos
   *          der File Output Stream
   * @throws IOException
   *           Wenn ein Fehler beim Speichern auftritt
   */
  private void saveProperties(final FileOutputStream fos) throws IOException {
    final java.util.Properties prop = new java.util.Properties();
    prop.put(Strings.CONFID_ADR, getAddrOfDb());
    prop.put(Strings.CONFID_USR, getUser());
    prop.put(Strings.CONFID_PW, getPw());
    prop.put(Strings.CONFID_WL, Integer.toString(getWordLength()));
    prop.put(Strings.CONFID_LOC, getNeoDbStarterLocation());
    prop.put(Strings.CONFID_SDT, arrayToString(getSelectDataType()));
    prop.put(Strings.CONFID_UTL, Integer.toString(getUniqueTableLength()));
    prop.put(Strings.CONFID_MIKTEX, getMikTexPath());
    prop.put(Strings.CONFID_PDFPATH, getPdfFile());
    prop.put(Strings.CONFID_PDFTITLE, getPdfTitle());
    prop.put(Strings.CONFID_PATHINSQL, getPathImportSql());
    prop.put(Strings.CONFID_PATHINCAT, getPathImportCat());
    prop.put(Strings.CONFID_PATHINCSV, getPathImportCsv());
    prop.put(Strings.CONFID_PATHOUTSQL, getPathExportSql());
    prop.put(Strings.CONFID_PATHOUTCSV, getPathExportCsv());
    prop.put(Strings.CONFID_PATHOUTCQL, getPathExportCql());
    prop.store(fos, Strings.SOFTWARENAME);
    fos.flush();
  }

  /**
   * Läd die Werte von der Festplatte.
   *
   * @param fis
   *          der File Input Stream
   * @throws IOException
   *           Wenn ein Fehler beim laden auftritt
   */
  private void loadProperties(final FileInputStream fis) throws IOException {
    final java.util.Properties prop = new java.util.Properties();
    prop.load(fis);
    setAddrOfDb(prop.getProperty(Strings.CONFID_ADR, addrOfDb));
    setUser(prop.getProperty(Strings.CONFID_USR, user));
    setPw(prop.getProperty(Strings.CONFID_PW, pw));
    setNeoDbStarterLocation(prop.getProperty(Strings.CONFID_LOC, neoDbStarterLocation));
    setWordLength(Integer.parseInt(prop.getProperty(Strings.CONFID_WL, Integer.toString(wordLength))));
    setMikTexPath(prop.getProperty(Strings.CONFID_MIKTEX, Strings.EMPTYSTRING));
    setPdfFile(prop.getProperty(Strings.CONFID_PDFPATH, Strings.EMPTYSTRING));
    setPdfTitle(prop.getProperty(Strings.CONFID_PDFTITLE, pdfTitle));
    final String stringarray = prop.getProperty(Strings.CONFID_SDT, arrayToString(selectDataType));
    setSelectDataType(stringarray.split(Strings.SEMICOLON));
    setUniqueTableLength(Integer.parseInt(prop.getProperty(Strings.CONFID_UTL, Integer.toString(uniqueTableLength))));
    setPathExportCql(prop.getProperty(Strings.CONFID_PATHOUTCQL, Strings.DEFAULTPATH));
    setPathExportCsv(prop.getProperty(Strings.CONFID_PATHOUTCSV, Strings.DEFAULTPATH));
    setPathExportSql(prop.getProperty(Strings.CONFID_PATHOUTSQL, Strings.DEFAULTPATH));
    setPathImportCat(prop.getProperty(Strings.CONFID_PATHINCAT, Strings.DEFAULTPATH));
    setPathImportCsv(prop.getProperty(Strings.CONFID_PATHINCSV, Strings.DEFAULTPATH));
    setPathImportSql(prop.getProperty(Strings.CONFID_PATHINSQL, Strings.DEFAULTPATH));
  }

  @Override
  public String getMikTexPath() {
    return mikTexLoc;
  }

  @Override
  public String getPdfFile() {
    return pdfLoc;
  }

  @Override
  public String getConfigPath() {
    return configPath.getAbsolutePath() + File.separatorChar;
  }

  @Override
  public void setMikTexPath(final String path) {
    mikTexLoc = path;
  }

  @Override
  public void setPdfFile(final String path) {
    pdfLoc = path;
  }

  @Override
  public String getPdfTitle() {
    return pdfTitle;
  }

  @Override
  public void setPdfTitle(final String title) {
    pdfTitle = title;
  }
}
