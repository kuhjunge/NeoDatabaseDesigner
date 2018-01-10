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
   * gibt die Spalten der Tabelle zur�ck.
   *
   * @return Spalten der Tabelle
   */
  List<Field> getData();

  /**
   * getter.
   *
   * @return Prim�rschl�ssel
   */
  Index getXpk();

  /**
   * setter.
   *
   * @param xpk
   *          neuer Prim�rschl�ssel
   */
  void setXpk(Index xpk);

  /**
   * gibt eine Liste mit Foreignkeys zur�ck.
   *
   * @return liste mit Foreignkeys
   */
  List<ForeignKey> getForeignKeys();

  /**
   * gibt eine Liste mit Indizes zur�ck.
   *
   * @return Liste mit Indizes
   */
  List<Index> getIndizies();

  /**
   * f�gt ein Feld zur Tabelle hinzu.
   *
   * @param f
   *          das Feld was zur Tabelle hinzugef�gt wird.
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
