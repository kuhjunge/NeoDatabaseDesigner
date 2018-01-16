package de.mach.tools.neodesigner.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.core.datamodel.Domain.DomainId;
import de.mach.tools.neodesigner.core.datamodel.impl.FieldImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.ForeignKeyImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.IndexImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.TableImpl;

public class TestUtil {
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
      t.addField(f2);
      t.addField(f3);
      i.addField(f);
      i2.addField(f);
      fk.setIndex(i);
      fk.setRefTable(t);
      fk.getIndex().setAltName("Feld" + j, "FeldZwei" + j);
      t.getIndizies().add(i2);
      t.getIndizies().add(i);
      t.getForeignKeys().add(fk);
      t.setXpk(xpk);
      lt.add(t);
    }
    return lt;
  }

  public static Map<String, String> getCatList() {
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

  public static Table getExampleTable() {
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
    f2.setPartOfPrimaryKey(true);
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
    reft.getField("FeldZwei").get().setPartOfPrimaryKey(true);
    reft.getRefForeignKeys().add(fk);
    fk.getIndex().setAltName("Feld", "FeldZwei");
    t.getIndizies().add(i2);
    t.getIndizies().add(i);
    t.getForeignKeys().add(fk);
    t.setXpk(xpk);
    return t;
  }

}
