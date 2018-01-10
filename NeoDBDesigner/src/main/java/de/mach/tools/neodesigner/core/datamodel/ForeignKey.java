package de.mach.tools.neodesigner.core.datamodel;

/**
 * Represnetiert einen Fremdschl�ssel im Relationalen Datenbankmodel.
 *
 * @author Chris Deter
 *
 */
public interface ForeignKey extends Index {

  /**
   * Gibt die Referenztablle zur�ck.
   *
   * @return Referenz Tabelle (Tabelle die mit dieser Tabelle verkn�pft ist)
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
   * gibt den Index zu diesem FK zur�ck.
   *
   * @return Index des Fremdschl�ssels
   */
  public Index getIndex();

  /**
   * setzt den Index des Fremdschl�ssels
   *
   * @param i
   *          der Index des Fremdschl�ssels.
   */
  public void setIndex(Index i);
}
