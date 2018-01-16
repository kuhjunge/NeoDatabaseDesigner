/*******************************************************************************
 * Copyright (C) 2017 Chris Deter
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/

package de.mach.tools.neodesigner.core.datamodel.impl;

import de.mach.tools.neodesigner.core.datamodel.Field;

/**
 * Wrapping Klasse um Fields in Indizes eine Ordnung zu geben und zusäztlich
 * alternative Namen (die Namen der Felder auf die der Fremdschlüssel zeigt) zu
 * geben.
 *
 * @author Chris Deter
 *
 */
public class FieldWrapper implements Comparable<FieldWrapper> {
  private Field field;
  private String altName;
  private int order;

  /**
   * Fieldwrapper Konstruktor.
   *
   * @param f
   *          das Feld
   * @param altname
   *          der alternative Name
   * @param order
   *          die Ordnung der Elemente
   */
  public FieldWrapper(final Field f, final String altname, final int order) {
    field = f;
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
   * setter.
   *
   * @param f
   *          Field
   */
  public void setField(final Field f) {
    field = f;
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
   * Getter.
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
   *          die Ordnung
   */
  public void setOrder(final int order) {
    this.order = order;
  }

  @Override
  public String toString() {
    return field + " (" + altName + ") " + order;
  }

  @Override
  public int hashCode() {
    return (field + altName + order).hashCode();
  }

  @Override
  public boolean equals(final Object o) {
    if (!(o instanceof FieldWrapper)) {
      return false;
    }
    final FieldWrapper other = (FieldWrapper) o;
    return getField().equals(other.getField()) && compareTo(other) == 0;
  }

  @Override
  public int compareTo(final FieldWrapper o) {
    return Integer.compare(getOrder(), o.getOrder());
  }
}
