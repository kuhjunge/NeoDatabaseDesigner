package de.mach.tools.neodesigner.core.datamodel.impl;

import de.mach.tools.neodesigner.core.datamodel.Field;

/**
 * Wrapping Klasse um Fields in Indizes eine Ordnung zu geben und zusäztlich
 * alternative Namen (die Namen der Felder auf die der Fremdschlüssel zeigt) zu
 * geben
 *
 * @author Chris Deter
 *
 */
public class FieldWrapper {
  private final Field field;
  private String altName;
  private int order;

  /**
   * Fieldwrapper Konstruktor.
   *
   * @param field
   *          das Feld
   * @param altname
   *          der alternative Name
   * @param order
   *          die Ordnung der Elemente
   */
  public FieldWrapper(final Field field, final String altname, final int order) {
    this.field = field;
    altName = altname;
    this.order = order;
  }

  /**
   * Getter.
   *
   * @retun Field
   */
  public Field getField() {
    return field;
  }

  /**
   * Getter.
   *
   * @return alternativer Name
   */
  public String getAltName() {
    return altName;
  }

  /**
   * Setter.
   *
   * @param altName
   *          setzt alternativen Namen
   */
  public void setAltName(final String altName) {
    this.altName = altName;
  }

  /**
   * Getter
   *
   * @return die Position dieses Felds (Reihenfolge für Index)
   */
  public int getOrder() {
    return order;
  }

  /**
   * Setter.
   *
   * @param order
   */
  public void setOrder(final int order) {
    this.order = order;
  }

  @Override
  public String toString() {
    return field + " (" + altName + ") " + order;
  }
}
