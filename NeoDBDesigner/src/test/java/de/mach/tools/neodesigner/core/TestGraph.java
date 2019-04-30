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

package de.mach.tools.neodesigner.core;


import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import de.mach.tools.neodesigner.core.graph.GraphColourObj;
import de.mach.tools.neodesigner.core.graph.GraphColours;


class TestGraph {

  @Test
  void testGraphColours() {
    final GraphColours gc = new GraphColours();
    gc.giveCategoryColour("1,1");
    gc.giveCategoryColour("1");
    assertNotEquals(gc.categoryToColourTransl("1,1"), gc.categoryToColourTransl("1"));
    assertEquals(gc.categoryToColourTransl("1,2"), gc.categoryToColourTransl("1"));
    assertTrue(gc.categoryToColourTransl("2").length() > 1);
    assertEquals("node.c2 {  fill-color: #00ff00;} node.c1 {  fill-color: #0000ff;} node.c3 {  fill-color: #ff0000;} ",
                 gc.getColoursAsCss());
    assertEquals(3, gc.getColour().size());
  }

  @Test
  void testGraphColours2() {
    final GraphColourObj gco0 = new GraphColourObj("1,1", 2);
    final GraphColourObj gco = new GraphColourObj("1,2", 3);
    final GraphColourObj gco2 = new GraphColourObj("2,3", 1);
    assertEquals("1,2", gco.getCategory());
    assertTrue(gco.compareTo(gco0) > 0);
    assertTrue(gco.compareTo(gco2) < 0);
    assertEquals("0xff0000ff", gco.getColour(3).toString());
    assertEquals("#ff0000", gco.getHexColour(3));
    assertTrue(gco.hashCode() > 0);
    assertEquals("1,2(id: c3 sid: 3)", gco.toString());
  }
}
