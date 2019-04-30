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


import java.util.ArrayList;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.FieldList;
import de.mach.tools.neodesigner.core.datamodel.impl.FieldImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.OrderedFieldList;


class TestFieldList {
  @Test
  void testRefOrder() {
    FieldList fl = new OrderedFieldList(new ArrayList<>());
    Field checkField = new FieldImpl("Feld 2");
    fl.addField(new FieldImpl("Feld 1"));
    fl.addField(checkField);
    fl.addField(new FieldImpl("Feld 3"));
    Assert.assertEquals(3, fl.get().size());
    Assert.assertEquals("[Feld 1, Feld 2, Feld 3] noRef", fl.toString());
    Assert.assertEquals(2, fl.getReferenceOrder("Feld 2"));
    Assert.assertEquals(2, fl.getOrder("Feld 2"));
    // Get By Order
    Assert.assertEquals(checkField, fl.getFieldByOrder(2));
    Assert.assertEquals(checkField, fl.getRefFieldByOrder(2));
    // Reference Order setzen
    fl.setReferenceOrder("Feld 3", 1);
    fl.setReferenceOrder("Feld 1", 3);
    Assert.assertEquals("Feld 1 (3), Feld 2 (2), Feld 3 (1), ", fl.toString());
    Assert.assertEquals(2, fl.getOrder("Feld 2"));
    Assert.assertEquals(1, fl.getOrder("Feld 1"));
    Assert.assertEquals(3, fl.getOrder("Feld 3"));
    Assert.assertEquals(1, fl.getReferenceOrder("Feld 3"));
    Assert.assertEquals(2, fl.getReferenceOrder("Feld 2"));
    Assert.assertEquals(checkField, fl.getRefFieldByOrder(2));
  }

  @Test
  void testOrder() {
    FieldList fl = new OrderedFieldList(new ArrayList<>());
    Field checkField = new FieldImpl("Feld 2");
    fl.addField(new FieldImpl("Feld 1"));
    fl.addField(checkField);
    fl.addField(new FieldImpl("Feld 3"));
    Assert.assertEquals(3, fl.get().size());
    Assert.assertEquals(2, fl.getReferenceOrder("Feld 2"));
    Assert.assertEquals(2, fl.getOrder("Feld 2"));
    // Löschen
    fl.deleteField("Feld 2");
    Assert.assertEquals(2, fl.get().size());
    Assert.assertEquals(2, fl.getOrder("Feld 3"));
    // Ersetzen
    fl.replaceField(checkField, "Feld 3");
    Assert.assertEquals(checkField, fl.getFieldByOrder(2));
    // Ersetzen von nicht vorhandenen Feld
    fl.replaceField(checkField, "Feld 3");
    Assert.assertEquals(checkField, fl.getFieldByOrder(2));
    // alles löschen
    fl.clear();
    Assert.assertEquals(0, fl.get().size());
  }
}
