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


import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.mach.tools.neodesigner.core.category.CategoryTranslator;
import de.mach.tools.neodesigner.core.datamodel.Table;


public class MockCategoryTranslator extends CategoryTranslator {

  @Override
  public void load(final Iterable<String> c) {
    for (final String category : getCategories(c)) {
      categoryToName.put(category, category.replace(',', '-'));
    }
  }

  // TODO: test hierfür schreiben
  public List<String> tableToCat(final List<Table> lt) {
    return super.getCategoriesFromTable(lt);
  }

  @Override
  public void save(final Map<String, String> catIdAndCatName) {
    for (final Entry<String, String> c : catIdAndCatName.entrySet()) {
      categoryToName.put(c.getKey(), c.getValue());
    }
  }
}
