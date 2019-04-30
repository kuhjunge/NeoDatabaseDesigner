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

package de.mach.tools.neodesigner.core;


import java.io.File;

import de.mach.tools.neodesigner.inex.nexport.pdf.PdfConf;
import de.mach.tools.neodesigner.ui.GuiConf;


/** Klasse die die Einstellung der Software von der Festplatte läd und dort wieder speichert.
 *
 * @author Chris Deter */
public class Configuration implements PdfConf, GuiConf {
  private static final String[] SELECTDATATYPE = { "Counter", "Lookup", "Date", "Amount", "Boolean", "String:1",
                                                   "String:2", "String:3", "String:4", "String:5", "String:10",
                                                   "String:20", "String:27", "String:30", "String:40", "String:50",
                                                   "String:120", "String:254", "Blob", "Clob" };
  private static final int WORDLENGTH = 18;
  private static final int UNIQTABLELENGTH = 15;
  private ConfigSaver conf = null;

  String getAddrOfDb() {
    return conf.getValue(Strings.CONFID_ADR);
  }

  @Override
  public String getPdfAuthor() {
    return conf.getValue(Strings.CONFID_PDFAUTHOR);
  }

  @Override
  public String getConfigPath() {
    return ConfigSaverImpl.getConfigPathRaw(Strings.SOFTWARENAME).getAbsolutePath() + File.separatorChar;
  }

  @Override
  public String getMikTexPath() {
    return conf.getValue(Strings.CONFID_MIKTEX);
  }

  public String getNeoDbStarterLocation() {
    return conf.getValue(Strings.CONFID_LOC);
  }

  @Override
  public String getPathExportGeneric() {
    return conf.getValue(Strings.CONFID_PATHOUTCQL);
  }

  @Override
  public String getPathExportCsv() {
    return conf.getValue(Strings.CONFID_PATHOUTCSV);
  }

  @Override
  public String getPathExportSql() {
    return conf.getValue(Strings.CONFID_PATHOUTSQL);
  }

  @Override
  public String getPathImportCat() {
    return conf.getValue(Strings.CONFID_PATHINCAT);
  }

  @Override
  public String getPathImportCsv() {
    return conf.getValue(Strings.CONFID_PATHINCSV);
  }

  @Override
  public String getPathImportSql() {
    return conf.getValue(Strings.CONFID_PATHINSQL);
  }

  @Override
  public String getPdfFile() {
    return conf.getValue(Strings.CONFID_PDFPATH);
  }

  @Override
  public String getPdfTitle() {
    return conf.getValue(Strings.CONFID_PDFTITLE);
  }

  @Override
  public String getPw() {
    return conf.getValue(Strings.CONFID_PW);
  }

  String[] getSelectDataType() {
    return ConfigSaverImpl.stringToArray(conf.getValue(Strings.CONFID_SDT));
  }

  int getUniqueTableLength() {
    return Integer.parseInt(conf.getValue(Strings.CONFID_UTL));
  }

  @Override
  public String getUser() {
    return conf.getValue(Strings.CONFID_USR);
  }

  int getWordLength() {
    return Integer.parseInt(conf.getValue(Strings.CONFID_WL));
  }

  /** initialisiert das Objekt und läd die Config von der Festplatte. */
  void init(ConfigSaver cs) {
    conf = cs;
    conf.setValue(Strings.CONFID_ADR, Strings.CONF_DEFAULT_ADR);
    conf.setValue(Strings.CONFID_USR, Strings.CONF_DEFAULT_USRPW);
    conf.setValue(Strings.CONFID_PW, Strings.CONF_DEFAULT_USRPW);
    conf.setValue(Strings.CONFID_WL, Integer.toString(Configuration.WORDLENGTH));
    conf.setValue(Strings.CONFID_LOC, Strings.EMPTYSTRING);
    conf.setValue(Strings.CONFID_SDT, ConfigSaverImpl.arrayToString(Configuration.SELECTDATATYPE));
    conf.setValue(Strings.CONFID_UTL, Integer.toString(Configuration.UNIQTABLELENGTH));
    conf.setValue(Strings.CONFID_MIKTEX, Strings.EMPTYSTRING);
    conf.setValue(Strings.CONFID_PDFPATH, Strings.EMPTYSTRING);
    conf.setValue(Strings.CONFID_PDFTITLE, Strings.CONF_DEFAULT_TITLE);
    conf.setValue(Strings.CONFID_PATHINSQL, Strings.DEFAULTPATH);
    conf.setValue(Strings.CONFID_PATHINCAT, Strings.DEFAULTPATH);
    conf.setValue(Strings.CONFID_PATHINCSV, Strings.DEFAULTPATH);
    conf.setValue(Strings.CONFID_PATHOUTSQL, Strings.DEFAULTPATH);
    conf.setValue(Strings.CONFID_PATHOUTCSV, Strings.DEFAULTPATH);
    conf.setValue(Strings.CONFID_PATHOUTCQL, Strings.DEFAULTPATH);
    conf.setValue(Strings.CONFID_PDFAUTHOR, System.getProperty("user.name"));
    // TODO: Wenn Feature getestet und eingeführt, dann diesen Parameter per default auf true setzen
    conf.setValue(Strings.CONFID_REMOVE_REDUNDANT_INDIZES, Boolean.toString(false));
    conf.load();
  }

  /** Speichert die Config File auf die Festplatte. */
  @Override
  public void save() {
    conf.save();
  }

  @Override
  public boolean getCheckDuplicateIndizes() {
    return Boolean.valueOf(conf.getValue(Strings.CONFID_REMOVE_REDUNDANT_INDIZES).trim());
  }

  @Override
  public void setCheckDuplicateIndizes(boolean b) {
    conf.setValue(Strings.CONFID_REMOVE_REDUNDANT_INDIZES, Boolean.toString(b));
  }

  /** Speichert die Datenbankverbindungsdaten (& alles andere auch).
   *
   * @param addr die Adresse der Datenbank
   * @param user der Nutzer der Datenbank
   * @param pw das Passwort der Datenbank */
  void save(final String addr, final String user, final String pw) {
    setAddrOfDb(addr);
    setUser(user);
    setPw(pw);
    conf.save();
  }

  private void setAddrOfDb(final String addrOfDb) {
    conf.setValue(Strings.CONFID_ADR, addrOfDb);
  }

  @Override
  public void setMikTexPath(final String path) {
    conf.setValue(Strings.CONFID_MIKTEX, path);
  }

  public void setNeoDbStarterLocation(final String str) {
    conf.setValue(Strings.CONFID_LOC, str);
  }

  @Override
  public void setPathExportGeneric(final String pathExportCql) {
    conf.setValue(Strings.CONFID_PATHOUTCQL, pathExportCql);
  }

  public void setPathExportCsv(final String pathExportCsv) {
    conf.setValue(Strings.CONFID_PATHOUTCSV, pathExportCsv);
  }

  @Override
  public void setPathExportSql(final String pathExportSql) {
    conf.setValue(Strings.CONFID_PATHOUTSQL, pathExportSql);
  }

  void setPathImportCat(final String pathImportCat) {
    conf.setValue(Strings.CONFID_PATHINCAT, pathImportCat);
  }

  void setPathImportCsv(final String pathImportCsv) {
    conf.setValue(Strings.CONFID_PATHINCSV, pathImportCsv);
  }

  void setPathImportSql(final String pathImportSql) {
    conf.setValue(Strings.CONFID_PATHINSQL, pathImportSql);
  }

  @Override
  public void setPdfFile(final String path) {
    conf.setValue(Strings.CONFID_PDFPATH, path);
  }

  @Override
  public void setPdfTitle(final String title) {
    conf.setValue(Strings.CONFID_PDFTITLE, title);
  }

  @Override
  public void setPdfAuthor(final String title) {
    conf.setValue(Strings.CONFID_PDFAUTHOR, title);
  }

  public void setPw(final String pw) {
    conf.setValue(Strings.CONFID_PW, pw);
  }

  private void setUser(final String user) {
    conf.setValue(Strings.CONFID_USR, user);
  }

  void setWordLength(final Integer i) {
    conf.setValue(Strings.CONFID_WL, i.toString());
  }
}
