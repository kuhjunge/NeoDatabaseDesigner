package de.mach.tools.neodesigner.core.datamodel;

import java.util.List;

/**
 * Representiert eine Tabelle aus dem Relationalen Datenbankmodel aus der
 * Graphendatenbank.
 *
 * @author Chris Deter
 *
 */
public interface Table extends Node {
  /**
   * gibt die Spalten der Tabelle zurück.
   *
   * @return Spalten der Tabelle
   */
  List<Field> getData();

  /**
   * getter.
   *
   * @return Primärschlüssel
   */
  Index getXpk();

  /**
   * setter.
   *
   * @param xpk
   *          neuer Primärschlüssel
   */
  void setXpk(Index xpk);

  /**
   * gibt eine Liste mit Foreignkeys zurück.
   *
   * @return liste mit Foreignkeys
   */
  List<ForeignKey> getForeignKeys();

  /**
   * gibt eine Liste mit Indizes zurück.
   *
   * @return Liste mit Indizes
   */
  List<Index> getIndizies();

  /**
   * fügt ein Feld zur Tabelle hinzu.
   *
   * @param f
   *          das Feld was zur Tabelle hinzugefügt wird.
   */
  void addField(Field f);

  /**
   * getter cateogory.
   *
   * @return category
   */
  String getCategory();

  /**
   * setter category.
   *
   * @param category
   *          neue category
   */
  void setCategory(String category);
}
