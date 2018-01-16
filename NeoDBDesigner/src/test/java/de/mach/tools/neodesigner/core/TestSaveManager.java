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

import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.Table;
import org.junit.Assert;
import org.junit.Test;

public class TestSaveManager {
  @Test
  public void testInsertFk() {
    final Table test = TestUtil.getExampleTable();
    final MockSave s = new MockSave();
    final SaveManager saveManager = new SaveManager(s);
    s.retTable = test.getForeignKeys().get(0).getRefTable();
    saveManager.saveNewForeignKey(test.getForeignKeys().get(0), test);
    Assert.assertTrue(s.insertedForeignKey.compareTo(test.getForeignKeys().get(0)) == 0);
    Assert.assertTrue(s.insertedForeignKey.equals(test.getForeignKeys().get(0)));
    Assert.assertTrue(s.insertedForeignKey.getRefTable().compareTo(test.getForeignKeys().get(0).getRefTable()) == 0);
    Assert.assertTrue(s.insertedForeignKey.getIndex().compareTo(test.getForeignKeys().get(0).getIndex()) == 0);
    Assert.assertTrue(!(s.insertedForeignKey == test.getForeignKeys().get(0)));
  }

  @Test
  public void testRenameField() {
    final Table test = TestUtil.getExampleTable();
    final MockSave s = new MockSave();
    final SaveManager saveManager = new SaveManager(s);
    final Field f = test.getField("Feld").get();
    f.setName("FeldNeu");
    saveManager.changeFieldName(f, "Feld");
    Assert.assertTrue(s.changeNodeNameFromTable.compareTo("Feld" + test.getName() + "FeldNeu") == 0);
  }

  @Test
  public void testRenameFieldPrim() {
    final Table test = TestUtil.getExampleTable();
    final MockSave s = new MockSave();
    final SaveManager saveManager = new SaveManager(s);
    final Field f = test.getField("FeldZwei").get();
    f.setName("FeldZweiNeu");
    saveManager.changeFieldName(f, "FeldZwei");
    Assert.assertTrue(s.changeNodeNameFromTable.compareTo("FeldZwei" + test.getName() + "FeldZweiNeu") == 0);
    Assert.assertTrue(s.changePrimFieldNameRelation.compareTo("FeldZwei" + test.getName() + "FeldZweiNeu") == 0);
  }
}
