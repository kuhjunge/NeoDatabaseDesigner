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

import java.util.Optional;

import org.junit.jupiter.api.Test;

import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.core.datamodel.viewimpl.ViewTable;


class TestSaveManager {
  @Test
  void testInsertFk() {
    final Table test = TestUtil.getExampleTable();
    final MockSave s = new MockSave();
    final SaveManager saveManager = new SaveManager(s);
    s.retTable = test.getForeignKeys().get(0).getRefTable();
    saveManager.saveNewForeignKey(test.getForeignKeys().get(0), test);
    assertEquals(0, s.insertedForeignKey.compareTo(test.getForeignKeys().get(0)));
    assertEquals(s.insertedForeignKey, test.getForeignKeys().get(0));
    assertEquals(0, s.insertedForeignKey.getRefTable().compareTo(test.getForeignKeys().get(0).getRefTable()));
    assertEquals(0, s.insertedForeignKey.getIndex().compareTo(test.getForeignKeys().get(0).getIndex()));
    assertTrue(!(s.insertedForeignKey == test.getForeignKeys().get(0)));
  }

  @Test
  void testRenameField() {
    final Table test = TestUtil.getExampleTable();
    final MockSave s = new MockSave();
    final SaveManager saveManager = new SaveManager(s);
    final Field f = test.getField("Feld");
    f.setName("FeldNeu");
    saveManager.changeFieldName(f, "Feld");
    assertEquals(0, s.changeNodeNameFromTable.compareTo("Feld" + test.getName() + "FeldNeu"));
  }

  @Test
  void testRenameFieldPrim() {
    final Table test = TestUtil.getExampleTable();
    final MockSave s = new MockSave();
    final SaveManager saveManager = new SaveManager(s);
    final Field f = test.getField("FeldZwei");
    f.setName("FeldZweiNeu");
    saveManager.changeFieldName(f, "FeldZwei");
    assertEquals(0, s.changeNodeNameFromTable.compareTo("FeldZwei" + test.getName() + "FeldZweiNeu"));
  }

  @Test
  void testTableProcessing() {
    Model m = TestUtil.getDatamodel();
    Optional<Table> origTbl = m.getTable("TABLE1");
    assertTrue(origTbl.isPresent());
    final SaveManager saveManager = new SaveManager(m.getSaveObj());
    saveManager.deleteNode(origTbl.get());
    origTbl = m.getTable("TABLE1");
    assertTrue(!origTbl.isPresent());
    origTbl = m.getTable("TABLE0");
    ViewTable vt = new ViewTable(origTbl.get(), true);
    saveManager.processTable("TABLE0-1", "10,0", "Test", vt);
    origTbl = m.getTable("TABLE0");
    assertTrue(!origTbl.isPresent());
    origTbl = m.getTable("TABLE0-1");
    assertTrue(origTbl.isPresent());
    assertEquals("TABLE0-1", origTbl.get().getName());
    assertEquals("10,0", origTbl.get().getCategory());
    assertEquals("Test", origTbl.get().getComment());
  }
}
