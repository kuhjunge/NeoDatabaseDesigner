package de.mach.tools.neodesigner.core;

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
class Configuration {

  private final File configPath = new File(System.getenv(Strings.CONF_LOC) + File.separatorChar + Strings.SOFTWARENAME);
  private File configFile = null;
  private static final Logger LOG = Logger.getLogger(Configuration.class.getName());
  // Config Values
  private String neoDbStarterLocation = "";
  private String addrOfDb = Strings.CONF_DEFAULT_ADR;
  private String user = Strings.CONF_DEFAULT_USRPW;
  private String pw = Strings.CONF_DEFAULT_USRPW;
  private int wordLength = 18;
  private String[] selectDataType = { "INTEGER", "NUMBER(18,5)", "VARCHAR2(10)", "VARCHAR2(20)", "VARCHAR2(30)",
      "VARCHAR2(40)", "VARCHAR2(254)", "VARCHAR2(120)", "DATE", "SMALLINT", "LONG RAW", "CHAR(1)", "CHAR(10)", "BLOB",
      "CLOB" };

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
   * Getter
   *
   * @return passowort
   */
  public String getPw() {
    return pw;
  }

  /**
   * Setter
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
   * Setter
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
   * Setter
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
    if (!configPath.exists()) {
      configPath.mkdirs();
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
  void save() {
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
   * @throws IOException
   */
  private void saveProperties(final FileOutputStream fos) throws IOException {
    final java.util.Properties prop = new java.util.Properties();
    prop.put(Strings.CONFID_ADR, getAddrOfDb());
    prop.put(Strings.CONFID_USR, getUser());
    prop.put(Strings.CONFID_PW, getPw());
    prop.put(Strings.CONFID_WL, Integer.toString(getWordLength()));
    prop.put(Strings.CONFID_LOC, getNeoDbStarterLocation());
    prop.put(Strings.CONFID_SDT, arrayToString(getSelectDataType()));
    prop.store(fos, Strings.SOFTWARENAME);
    fos.flush();
  }

  /**
   * Läd die Werte von der Festplatte.
   *
   * @param fis
   * @throws IOException
   */
  private void loadProperties(final FileInputStream fis) throws IOException {
    final java.util.Properties prop = new java.util.Properties();
    prop.load(fis);
    setAddrOfDb(prop.getProperty(Strings.CONFID_ADR, addrOfDb));
    setUser(prop.getProperty(Strings.CONFID_USR, user));
    setPw(prop.getProperty(Strings.CONFID_PW, pw));
    setNeoDbStarterLocation(prop.getProperty(Strings.CONFID_LOC, neoDbStarterLocation));
    setWordLength(Integer.parseInt(prop.getProperty(Strings.CONFID_WL, Integer.toString(wordLength))));
    final String stringarray = prop.getProperty(Strings.CONFID_SDT, arrayToString(selectDataType));
    setSelectDataType(stringarray.split(Strings.SEMICOLON));
  }
}
