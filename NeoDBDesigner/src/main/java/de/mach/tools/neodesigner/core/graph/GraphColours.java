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

package de.mach.tools.neodesigner.core.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Generiert die Farben des Graphen.
 *
 * @author Chris Deter
 *
 */
public class GraphColours {
  private final List<GraphColourObj> colour = new ArrayList<>();

  /**
   * fügt eine neue Kategorie (Farbe) in den Graphen ein.
   *
   * @param category
   *          die Kategorie
   */
  public void giveCategoryColour(final String category) {
    final GraphColourObj gco = new GraphColourObj(category, colour.size() + 1);
    if (!colour.contains(gco)) {
      colour.add(gco);
    }
  }

  /**
   * Übersetzt die Kategorie in eine CSS Kategorie.
   *
   * @param category
   *          String mit der Kategorie
   * @return String mit der CSS ID
   */
  public String categoryToColourTransl(final String category) {
    return getColourObj(category).getCssId();
  }

  private GraphColourObj getColourObj(final String category) {
    final GraphColourObj longCategory = new GraphColourObj(category, 0);
    final GraphColourObj shortCategory = new GraphColourObj(category.substring(0, 1), 0);
    if (colour.contains(longCategory)) {
      return colour.get(colour.indexOf(longCategory));
    } else if (category.length() > 1 && colour.contains(shortCategory)) {
      return colour.get(colour.indexOf(shortCategory));
    } else {
      giveCategoryColour(category);
      return getColourObj(category);
    }
  }

  @Override
  public String toString() {
    return colour.toString();
  }

  /**
   * gibt den CSS Code zurück für die verschiedenen Farben.
   *
   * @return CSS für Farben
   */
  public String getColoursAsCss() {
    sort();
    final StringBuilder strB = new StringBuilder();
    for (final GraphColourObj css : colour) {
      strB.append("node." + css.getCssId() + " {  fill-color: " + css.getHexColour(colour.size()) + ";} ");
    }
    return strB.toString();
  }

  /**
   * gibt eine Liste mit den Colour Objekten zurück.
   *
   * @return Liste mit Colour Objekten
   */
  public List<GraphColourObj> getColour() {
    sort();
    return colour;
  }

  /**
   * sortiert die Liste.
   */
  private void sort() {
    Collections.sort(colour);
    for (int i = 0; i < colour.size(); i++) {
      colour.get(i).setSortId(i + 1);
    }
  }
}
