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

package de.mach.tools.neodesigner.ui;


import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.jupiter.api.Test;


class TestFXPaths {

  @Test
  void testMainFxml() {
    checkPath(Strings.FXML_NEDDBDESIGNER);
  }

  @Test
  void testFkFxml() {
    checkPath(Strings.FXML_FKRELEDITOR);
  }

  @Test
  void testTableTab() {
    checkPath(Strings.FXML_TABLEVIEW);
  }

  @Test
  void testIndexFxml() {
    checkPath(Strings.FXML_INDEXRELEDITOR);
  }

  @Test
  void testModelPrinterFxml() {
    checkPath(Strings.FXML_PDFEDITOR);
  }

  @Test
  void testModelNeoModuleFxml() {
    checkPath(Strings.FXML_NEOMODULE);
  }

  @Test
  void testIcon() {
    checkPath(Strings.FXML_ICON);
  }

  private void checkPath(final String p) {
    final URL u = getClass().getResource(p);
    try {
      final File f = new File(u.toURI());
      assertTrue(f.exists());
    }
    catch (final URISyntaxException e) {
      e.printStackTrace();
      fail("IO Exception");
    }
  }
}
