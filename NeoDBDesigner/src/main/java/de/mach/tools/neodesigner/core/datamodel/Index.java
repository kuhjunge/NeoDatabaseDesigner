package de.mach.tools.neodesigner.core.datamodel;

import java.util.List;

/**
 * Representiert einen Index im Relationalen Datenbankmodel.
 *
 * @author Chris Deter
 *
 */
public interface Index extends Node {
  public enum Type {
    XPK, R, XIF, XAK, XIE
  }

  /**
   * getter für Type.
   *
   * @return den Type
   */
  Type getType();

  /**
   * setter für Type.
   *
   * @param s
   *          Der neue Type
   */
  void setType(Type s);

  /**
   * getter isUnique.
   *
   * @return boolean
   */
  boolean isUnique();

  /**
   * setter isUnique.
   *
   * @param unique
   *          boolean ob unique
   */
  void setUnique(boolean unique);

  /**
   * Gibt eine Lise mit allen Feldern dieses Indexes zurück.
   *
   * @return Liste mit allen Feldern
   */
  List<Field> getFieldList();

  /**
   * fügt ein Feld zum Index hinzu.
   *
   * @param f
   *          das Feld
   */
  void addField(Field f);

  /**
   * Fügt ein Feld mit alternativen Namen zum Index hinzu.
   *
   * @param f
   *          das Feld
   * @param altName
   *          der alternative Name
   */
  void addField(Field f, String altName);

  /**
   * entfernt ein Feld vom Index.
   *
   * @param i
   *          Stelle an der das Feld im Index zugeordnet ist
   */
  void removeField(int i);

  /**
   * löscht alle Felder des Indexes.
   */
  void clearFieldList();

  /**
   * setzt einen neuen alternativen Namen.
   *
   * @param name
   *          Name des Feldes
   * @param altName
   *          der Referenz Tabellen Name des Feldes
   */
  public void setAltName(String name, String altName);

  /**
   * gibt den alternativen Namen eines Feldes zurück.
   *
   * @param name
   *          Name des Feldes
   * @return alternativer Name des Feldes
   */
  public String getAltName(String name);

  /**
   * gibt einen String zurück welcher die Ordnung dieses Elementes ausdrückt.
   *
   * @param name
   *          Name des elementes
   * @return die Orndungszahl
   */
  String getOrder(String name);
}
