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

import java.util.Map;

import org.junit.jupiter.api.Test;

import de.mach.tools.neodesigner.core.category.CategoryObj;
import de.mach.tools.neodesigner.core.category.ViewCategoryObj;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.core.datamodel.impl.TableImpl;


class TestCategory {
  @Test
  void testDataModel() {
    final CategoryObj cobj = new CategoryObj("1,1", 1, "PrimeCategory");
    assertEquals("1,1", cobj.getCategory());
    assertEquals("PrimeCategory", cobj.getCategoryText());
    assertEquals("(1,1) PrimeCategory", cobj.getDisplayText());
    assertEquals("(1,1) PrimeCategory(id: 1 sid: 1)", cobj.toString());
    cobj.setCategory("1,2");
    assertEquals("1,2", cobj.getCategory());
    cobj.setCategoryText("BestCategory");
    assertEquals("BestCategory", cobj.getCategoryText());
  }

  @Test
  void testViewDataModel() {
    final ViewCategoryObj cobj = new ViewCategoryObj(new CategoryObj("1,1", 1, "PrimeCategory"));
    assertEquals("1,1", cobj.getCategory());
    assertEquals("PrimeCategory", cobj.getCategoryText());
    assertEquals("(1,1) PrimeCategory(id: 1 sid: 1)", cobj.toString());
    cobj.getId().set("1,2");
    assertEquals("1,2", cobj.getCategory());
    cobj.getName().set("BestCategory");
    assertEquals("BestCategory", cobj.getCategoryText());
  }

  @Test
  void testCategoryTranslator() {
    final MockConfigSaver catConf = new MockConfigSaver();
    catConf.setValue(Strings.SECTION + "1,1", "firstCategory");
    final Model model = new ModelImpl(new MockConfigSaver(), catConf);
    for (int i = 0; i < 5; i++) {
      final Table t = new TableImpl("FirstTable");
      t.setCategory("1," + i);
      model.getSaveObj().insertNewTable(t);
    }
    Map<String, String> categories = model.getCategoryTranslation();
    assertEquals("firstCategory", categories.get("1,1"));
    assertEquals("1,2", categories.get("1,2"));
    assertEquals("1,3", categories.get("1,3"));
    assertEquals("1,4", categories.get("1,4"));
    assertNull(categories.get("1,5"));
  }
}
