/* Copyright (C) 2018 Chris Deter Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the
 * Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions: The
 * above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE. */

package de.mach.tools.neodesigner.core.category;


import de.mach.tools.neodesigner.core.Util;


/** Verwaltungsobjekt für Kategorie.
 *
 * @author cd */
public class CategoryObj implements Comparable<CategoryObj> {
  private String category;
  private String categoryText;
  protected int id;
  protected int sortId;

  /** Konstruktor.
   *
   * @param category der Farbe
   * @param id der Farbe */
  public CategoryObj(final String category, final int id) {
    this.category = category;
    this.id = id;
    sortId = id;
    categoryText = category;
  }

  /** Konstruktor.
   *
   * @param category der Farbe
   * @param id der Farbe */
  public CategoryObj(final String category, final int id, final String categoryText) {
    this.category = category;
    this.id = id;
    sortId = id;
    this.categoryText = categoryText;
  }

  public String getCategory() {
    return category;
  }

  public String getCategoryText() {
    return categoryText;
  }

  public void setCategory(final String c) {
    category = c;
  }

  public void setCategoryText(final String ct) {
    categoryText = ct;
  }

  @Override
  public String toString() {
    return getDisplayText() + "(id: " + id + " sid: " + sortId + ")";
  }

  @Override
  public boolean equals(final Object o) {
    if (!(o instanceof CategoryObj)) {
      return false;
    }
    final CategoryObj other = (CategoryObj) o;
    return getCategory().equals(other.getCategory());
  }

  @Override
  public int hashCode() {
    return getCategory().hashCode();
  }

  @Override
  public int compareTo(final CategoryObj o) {
    Integer cat = 0;
    Integer refCat = 0;
    final String[] refCatParts = o.getCategory().split(",");
    if (Util.isInteger(refCatParts[0])) {
      refCat = Integer.parseInt(refCatParts[0]) * 100 + (refCatParts.length > 1 ? Integer.parseInt(refCatParts[1]) : 0);
    }
    final String[] catParts = getCategory().split(",");
    if (Util.isInteger(catParts[0])) {
      cat = Integer.parseInt(catParts[0]) * 100 + (catParts.length > 1 ? Integer.parseInt(catParts[1]) : 0);
    }
    return cat.compareTo(refCat);
  }

  /** Setze SortierID für die Farbsortierung.
   *
   * @param i die ID */
  public void setSortId(final int i) {
    sortId = i;
  }

  public String getDisplayText() {
    return "(" + getCategory() + ") " + getCategoryText();
  }
}
