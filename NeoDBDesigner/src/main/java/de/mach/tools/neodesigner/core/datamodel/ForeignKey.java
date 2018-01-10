package de.mach.tools.neodesigner.core.datamodel;

/**
 * Represnetiert einen Fremdschlüssel im Relationalen Datenbankmodel.
 *
 * @author Chris Deter
 *
 */
public interface ForeignKey extends Index {

  /**
   * Gibt die Referenztablle zurück.
   *
   * @return Referenz Tabelle (Tabelle die mit dieser Tabelle verknüpft ist)
   */
  public Table getRefTable();

  /**
   * setzt die Referenz Tabelle.
   *
   * @param refTable
   *          die Referenz Tabelle
   */
  public void setRefTable(Table refTable);

  /**
   * gibt den Index zu diesem FK zurück.
   *
   * @return Index des Fremdschlüssels
   */
  public Index getIndex();

  /**
   * setzt den Index des Fremdschlüssels
   *
   * @param i
   *          der Index des Fremdschlüssels.
   */
  public void setIndex(Index i);
}
