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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.mach.tools.neodesigner.core.category.CategoryTranslator;
import de.mach.tools.neodesigner.core.datamodel.Domain.DomainId;
import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.core.datamodel.impl.FieldImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.ForeignKeyImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.IndexImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.TableImpl;
import de.mach.tools.neodesigner.inex.ImportTask;
import de.mach.tools.neodesigner.inex.nimport.ImportJsonTask;


public class TestUtil {
  static Map<String, String> getCatList() {
    final Map<String, String> categoryToName = new HashMap<>();
    categoryToName.put("0,0", "Cat0");
    categoryToName.put("1", "Cat1");
    categoryToName.put("1,1", "Cat1a");
    categoryToName.put("1,2", "Cat1b");
    categoryToName.put("1,3", "Cat1c");
    categoryToName.put("1,4", "Cat1d");
    categoryToName.put("1,5", "Cate");
    return categoryToName;
  }

  static Table getExampleTable() {
    final Table t = new TableImpl("ExampleTable");
    final Table reft = new TableImpl("ExampleRefTable");
    t.setCategory("1,1");
    final Field f = new FieldImpl("Feld", DomainId.STRING, 10, true, "", t);
    final Field f2 = new FieldImpl("FeldZwei", DomainId.AMOUNT, 0, true, "", t);
    final Field f3 = new FieldImpl("FeldDrei", DomainId.DATE, 0, false, "", t);
    final Index i = new IndexImpl("XIF10" + "BlaBlub", t);
    final Index i2 = new IndexImpl("XIE10" + "TestIndex", t);
    final Index xpk = new IndexImpl("XPKTable", t);
    xpk.addField(f2);
    final ForeignKey fk = new ForeignKeyImpl("R_10", t);
    t.addField(f);
    t.addField(f2);
    t.addField(f3);
    i.addField(f);
    i.addField(f2);
    i2.addField(f2);
    i2.addField(f3);
    fk.setIndex(i);
    fk.setRefTable(reft);
    reft.addField(new FieldImpl("FeldZwei", DomainId.AMOUNT, 0, true, "", t));
    reft.getRefForeignKeys().add(fk);
    // fk.getIndex().setAltName("Feld", "FeldZwei");
    t.getIndizies().add(i2);
    t.getIndizies().add(i);
    t.getForeignKeys().add(fk);
    t.setXpk(xpk);
    return t;
  }

  public static List<Table> getTableList() {
    final List<Table> lt = new ArrayList<>();
    for (int j = 0; j < 5; j++) {
      final Table t = new TableImpl("Table" + j);
      t.setCategory("1," + j);
      final Field f = new FieldImpl("Feld" + j, DomainId.STRING, 10, true, "", t);
      final Field f2 = new FieldImpl("FeldZwei" + j, DomainId.AMOUNT, 0, true, "", t);
      final Field f3 = new FieldImpl("FeldDrei" + j, DomainId.DATE, 0, false, "", t);
      final Index i = new IndexImpl("XIF10" + j + "BlaBlub", t);
      final Index i2 = new IndexImpl("XIE10" + j + "TestIndex", t);
      final Index xpk = new IndexImpl("XPKTable" + j, t);
      xpk.addField(f2);
      final ForeignKey fk = new ForeignKeyImpl("R_10" + j, t);
      t.addField(f);
      // Feld 3 ist vor Feld 2
      t.addField(f3);
      t.addField(f2);
      i.addField(f);
      i2.addField(f);
      fk.setIndex(i);
      fk.setRefTable(t);
      fk.setFkOrder(f, 1);
      // fk.getIndex().setAltName("Feld" + j, "FeldZwei" + j);
      t.getIndizies().add(i2);
      t.getIndizies().add(i);
      t.getForeignKeys().add(fk);
      t.setXpk(xpk);
      lt.add(t);
    }
    return lt;
  }

  public static Model getDatamodel() {
    ConfigSaver catConfig = new MockConfigSaver();
    String jsonCsv = "{\"tables\":[{\"n\":\"TABLE0\",\"cat\":\"0,0\",\"fl\":[{\"n\":\"FELD0\",\"dom\":\"String\",\"l\":50,\"isnull\":false},{\"n\":\"FELDZWEI0\",\"dom\":\"Counter\",\"isnull\":false},{\"n\":\"FELDDREI0\",\"dom\":\"Date\",\"isnull\":true}],\"xpk\":{\"n\":\"XPKTABLE0\",\"unique\":true,\"fl\":[\"FELDZWEI0\"]},\"il\":[{\"n\":\"XIE100TESTINDEX\",\"unique\":false,\"fl\":[\"FELD0\"]},{\"n\":\"XIF100BLABLUB\",\"unique\":false,\"fl\":[\"FELD0\"]}],\"fkl\":[{\"n\":\"R_100\",\"rt\":\"TABLE0\",\"fl\":[\"FELD0\"]}]},{\"n\":\"TABLE1\",\"cat\":\"0,0\",\"fl\":[{\"n\":\"FELD1\",\"dom\":\"String\",\"l\":50,\"isnull\":false},{\"n\":\"FELDZWEI1\",\"dom\":\"Counter\",\"isnull\":false},{\"n\":\"FELDDREI1\",\"dom\":\"Date\",\"isnull\":true}],\"xpk\":{\"n\":\"XPKTABLE1\",\"unique\":true,\"fl\":[\"FELDZWEI1\"]},\"il\":[{\"n\":\"XIE101TESTINDEX\",\"unique\":false,\"fl\":[\"FELD1\"]},{\"n\":\"XIF101BLABLUB\",\"unique\":false,\"fl\":[\"FELD1\"]}],\"fkl\":[{\"n\":\"R_101\",\"rt\":\"TABLE1\",\"fl\":[\"FELD1\"]}]},{\"n\":\"TABLE2\",\"cat\":\"10,1\",\"fl\":[{\"n\":\"FELD2\",\"dom\":\"String\",\"l\":50,\"isnull\":false},{\"n\":\"FELDZWEI2\",\"dom\":\"Counter\",\"isnull\":false},{\"n\":\"FELDDREI2\",\"dom\":\"Date\",\"isnull\":true}],\"xpk\":{\"n\":\"XPKTABLE2\",\"unique\":true,\"fl\":[\"FELDZWEI2\"]},\"il\":[{\"n\":\"XIE102TESTINDEX\",\"unique\":false,\"fl\":[\"FELD2\"]},{\"n\":\"XIF102BLABLUB\",\"unique\":false,\"fl\":[\"FELD2\"]}],\"fkl\":[{\"n\":\"R_102\",\"rt\":\"TABLE2\",\"fl\":[\"FELD2\"]}]},{\"n\":\"TABLE3\",\"cat\":\"0,0\",\"fl\":[{\"n\":\"FELD3\",\"dom\":\"String\",\"l\":50,\"isnull\":false},{\"n\":\"FELDZWEI3\",\"dom\":\"Counter\",\"isnull\":false},{\"n\":\"FELDDREI3\",\"dom\":\"Date\",\"isnull\":true}],\"xpk\":{\"n\":\"XPKTABLE3\",\"unique\":true,\"fl\":[\"FELDZWEI3\"]},\"il\":[{\"n\":\"XIE103TESTINDEX\",\"unique\":false,\"fl\":[\"FELD3\"]},{\"n\":\"XIF103BLABLUB\",\"unique\":false,\"fl\":[\"FELD3\"]}],\"fkl\":[{\"n\":\"R_103\",\"rt\":\"TABLE3\",\"fl\":[\"FELD3\"]}]},{\"n\":\"TABLE4\",\"cat\":\"0,0\",\"fl\":[{\"n\":\"FELD4\",\"dom\":\"String\",\"l\":50,\"isnull\":false},{\"n\":\"FELDZWEI4\",\"dom\":\"Counter\",\"isnull\":false},{\"n\":\"FELDDREI4\",\"dom\":\"Date\",\"isnull\":true}],\"xpk\":{\"n\":\"XPKTABLE4\",\"unique\":true,\"fl\":[\"FELDZWEI4\"]},\"il\":[{\"n\":\"XIE104TESTINDEX\",\"unique\":false,\"fl\":[\"FELD4\"]},{\"n\":\"XIF104BLABLUB\",\"unique\":false,\"fl\":[\"FELD4\"]}],\"fkl\":[{\"n\":\"R_104\",\"rt\":\"TABLE4\",\"fl\":[\"FELD4\"]}]}],\"categories\":[{\"id\":\"0\",\"n\":\"BeispielUeberschrift\"},{\"id\":\"0,0\",\"n\":\"BeispielKategorie\"},{\"id\":\"10\",\"n\":\"BeispielUeberschrift2\"},{\"id\":\"10,1\",\"n\":\"BeispielKategorie2\"}]}";
    ImportTask it = new ImportJsonTask(new CategoryTranslator(catConfig), jsonCsv);
    it.startImport();
    final Model model = new ModelImpl(new MockConfigSaver(), catConfig);
    model.importTables(it.getList());
    return model;
  }
}
