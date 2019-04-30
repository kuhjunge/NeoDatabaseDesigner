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

package de.mach.tools.neodesigner.inex;


import org.junit.Assert;
import org.junit.jupiter.api.Test;

import de.mach.tools.neodesigner.core.Model;
import de.mach.tools.neodesigner.core.TestUtil;
import de.mach.tools.neodesigner.inex.nexport.tex.LineProcessor;
import de.mach.tools.neodesigner.inex.nexport.tex.model.Column;
import de.mach.tools.neodesigner.inex.nexport.tex.model.ForeignKeyListElement;
import de.mach.tools.neodesigner.inex.nexport.tex.model.PdfDataModel;


class TexModelTest {

  @Test
  void testTexDataModel() {
    Model model = TestUtil.getDatamodel();
    final LineProcessor lineProcessor = new LineProcessor(model.getCategoryTranslation());
    lineProcessor.generate(model.getAllTables());
    PdfDataModel pdfmodel = lineProcessor.getDatamodel();
    Assert.assertEquals(2, pdfmodel.getSortedSections().size());
    String section = pdfmodel.getSortedSections().get(0);
    Assert.assertEquals("BeispielUeberschrift", pdfmodel.getSectionOrPageTitle(section));
    String page = pdfmodel.getSortedPages(section).get(0);
    Assert.assertEquals("0,0", page);
    String table = pdfmodel.getSortedTables(page).get(0);
    // Anzahl der Tabellen innerhalb einer Kategorie
    Assert.assertEquals(4, pdfmodel.getSortedTables(page).size());
    Assert.assertEquals("TABLE0", table);
    Assert.assertEquals(2, pdfmodel.getNonPrimaryKeyColumns(page, table).size());
    Column c = pdfmodel.getNonPrimaryKeyColumns(page, table).get(0);
    Assert.assertEquals("FELD0", c.getName());
    Assert.assertEquals("String50", c.getDomain());
    Assert.assertEquals("TABLE0", c.getForeignKeyTableName());
    Assert.assertEquals("IE100.1", c.getIndexName());
    Assert.assertFalse(c.isPrimaryKey());
    Assert.assertTrue(c.hasIndex());
    Assert.assertTrue(c.isForeignKey());
    Assert.assertFalse(c.isNullable());
    Assert.assertEquals(1, pdfmodel.getPrimaryKeyColumns(page, table).size());
    Assert.assertEquals(5, pdfmodel.getFkList().size());
    ForeignKeyListElement fk = pdfmodel.getFkList().get(0);
    Assert.assertEquals("R\\_100", fk.getForeignkeyName());
    Assert.assertEquals("TABLE0", fk.getTableName());
    Assert.assertEquals("TABLE0", fk.getRefTableName());
    Assert.assertEquals("FELD0", fk.getColumnName());
    Assert.assertEquals(1, (int) fk.getOrder());
    section = pdfmodel.getSortedSections().get(1);
    Assert.assertEquals("BeispielUeberschrift2", pdfmodel.getSectionOrPageTitle(section));
  }
}
