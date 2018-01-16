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

package de.mach.tools.neodesigner.core;

import org.junit.Assert;
import org.junit.Test;

import de.mach.tools.neodesigner.core.category.CategoryObj;
import de.mach.tools.neodesigner.core.category.CategoryTranslator;
import de.mach.tools.neodesigner.core.category.ViewCategoryObj;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.core.datamodel.impl.TableImpl;
import de.mach.tools.neodesigner.database.local.LocalDatabaseManager;

public class TestCategory {
  @Test
  public void testDataModel() {
    final CategoryObj cobj = new CategoryObj("1,1", 1, "PrimeCategory");
    Assert.assertTrue(cobj.getCategory().equals("1,1"));
    Assert.assertTrue(cobj.getCategoryText().equals("PrimeCategory"));
    Assert.assertTrue(cobj.getDisplayText().equals("(1,1) PrimeCategory"));
    Assert.assertTrue(cobj.toString().equals("(1,1) PrimeCategory(id: 1 sid: 1)"));
    cobj.setCategory("1,2");
    Assert.assertTrue(cobj.getCategory().equals("1,2"));
    cobj.setCategoryText("BestCategory");
    Assert.assertTrue(cobj.getCategoryText().equals("BestCategory"));
  }

  @Test
  public void testViewDataModel() {
    final ViewCategoryObj cobj = new ViewCategoryObj(new CategoryObj("1,1", 1, "PrimeCategory"));
    Assert.assertTrue(cobj.getCategory().equals("1,1"));
    Assert.assertTrue(cobj.getCategoryText().equals("PrimeCategory"));
    Assert.assertTrue(cobj.toString().equals("(1,1) PrimeCategory(id: 1 sid: 1)"));
    cobj.getId().set("1,2");
    Assert.assertTrue(cobj.getCategory().equals("1,2"));
    cobj.getName().set("BestCategory");
    Assert.assertTrue(cobj.getCategoryText().equals("BestCategory"));
  }

  @Test
  public void testCategoryTranslator() {
    final CategoryTranslator catTrans = new MockCategoryTranslator();
    final LocalDatabaseManager ldm = new LocalDatabaseManager(true);
    for (int i = 0; i < 5; i++) {
      final Table t = new TableImpl("FirstTable");
      t.setCategory("1," + i);
      ldm.importTable(t);
    }
    catTrans.load(ldm.getListWithCategories());
    Assert.assertTrue(catTrans.translateNumberIntoName("1,1").equals("1-1"));
    Assert.assertTrue(catTrans.translateNumberIntoName("1,2").equals("1-2"));
    Assert.assertTrue(catTrans.translateNumberIntoName("1,3").equals("1-3"));
    Assert.assertTrue(catTrans.translateNumberIntoName("1,4").equals("1-4"));
    Assert.assertTrue(!catTrans.translateNumberIntoName("1,5").equals("1-5"));
  }

  @Test
  public void testCategoryTranslator2() {
    final MockCategoryTranslator catTrans = new MockCategoryTranslator();
    catTrans.tableToCat(TestUtil.getTableList());
    Assert.assertTrue(catTrans.translateNumberIntoName("1,1").equals("1,1"));
    Assert.assertTrue(catTrans.translateNumberIntoName("1,2").equals("1,2"));
    Assert.assertTrue(catTrans.translateNumberIntoName("1,3").equals("1,3"));
    Assert.assertTrue(catTrans.translateNumberIntoName("1,4").equals("1,4"));
    Assert.assertTrue(catTrans.translateNumberIntoName("1,5").equals("1,5"));
  }
}
