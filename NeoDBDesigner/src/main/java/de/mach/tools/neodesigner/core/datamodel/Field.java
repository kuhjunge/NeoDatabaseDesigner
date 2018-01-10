package de.mach.tools.neodesigner.core.datamodel;

/**
 * Representiert eine Spalte oder Feld im relationalen Datenbankmodel.
 *
 * @author Chris Deter
 *
 */
public interface Field extends Node {
  /**
   * Gibt zur�ck was f�r ein Datentyp das Feld hat.
   *
   * @return Datentyp als String
   */
  public String getTypeOfData();

  /**
   * setter f�r den Datentyp.
   *
   * @param typeOfData
   *          die Daten
   */
  public void setTypeOfData(String typeOfData);

  /**
   * getter required.
   *
   * @return ob das Feld required ist
   */
  public boolean isRequired();

  /**
   * setter required.
   *
   * @param isRequired
   *          Boolean ob Feld notwendig ist
   */
  public void setRequired(boolean isRequired);

  /**
   * getter is Part of Prim Key.
   *
   * @return boolean ob das Feld Teil eines Prim�rschl�ssels ist
   */
  public boolean isPartOfPrimaryKey();

  /**
   * setter ist part of Prim Key.
   *
   * @param prim
   *          setzt ob das Feld Teil eines prim Schl�ssels ist
   */
  public void setPartOfPrimaryKey(boolean prim);
}
