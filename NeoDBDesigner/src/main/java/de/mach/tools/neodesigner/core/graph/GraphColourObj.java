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

package de.mach.tools.neodesigner.core.graph;


import javafx.scene.paint.Color;

import de.mach.tools.neodesigner.core.category.CategoryObj;


/** Stellt eine Farbe im Graphen dar (für eine bestimmte Kategorie).
 *
 * @author Chris Deter */
public class GraphColourObj extends CategoryObj {

  /** Konstruktor.
   *
   * @param category der Farbe
   * @param id der Farbe */
  public GraphColourObj(final String category, final int id) {
    super(category, id);
  }

  public String getCssId() {
    return "c" + id;
  }

  public String getHexColour(final int colourCount) {
    return String.format("#%02x%02x%02x", (int) (getColour(colourCount).getRed() * 255),
                         (int) (getColour(colourCount).getGreen() * 255),
                         (int) (getColour(colourCount).getBlue() * 255));
  }

  public Color getColour(final int colourCount) {
    return Color.hsb(360.0 / colourCount * sortId, 1, 1);
  }

  @Override
  public String toString() {
    return getCategory() + "(id: " + getCssId() + " sid: " + sortId + ")";
  }
}
