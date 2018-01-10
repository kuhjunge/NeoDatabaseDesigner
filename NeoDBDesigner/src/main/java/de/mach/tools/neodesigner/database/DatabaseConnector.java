package de.mach.tools.neodesigner.database;

import java.util.List;

/**
 * Interface welches das Neo4J Framework kapselt.
 *
 * @author Chris Deter
 *
 */
public interface DatabaseConnector {

  /**
   * Pr�ft ob die Datenbank verbunden ist.
   *
   * @return "True" wenn die Datenbank verbunden ist, sonst "False"
   */
  boolean isOnline();

  /**
   * Verbindet die Datenbank.
   *
   * @param addr
   *          Adresse der Datenbank
   * @param nutzername
   *          Nutzername f�r die Datenbank
   * @param pw
   *          Passwort f�r die Datenbank
   * @return "True" wenn die Datenbank verbunden wurde
   */
  boolean connectDb(String addr, String nutzername, String pw);

  /**
   * Trennt die Datenbankverbindung.
   */
  void disconnectDb();

  /**
   * F�hrt eine Query auf der Datenbank aus.
   *
   * @param q
   *          Query
   * @param v
   *          Parameter der Query
   * @return eine Ergebnis Einheit
   */
  List<ResultUnit> runCypher(String q, Object[] v);

  /**
   * F�hrt eine Query auf der Datenbank aus.
   *
   * @param q
   *          Query
   * @return eine Ergebnis Einheit
   */
  List<ResultUnit> runCypher(String q);

}