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

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import de.mach.tools.neodesigner.core.datamodel.Domain;
import de.mach.tools.neodesigner.core.datamodel.Domain.DomainId;
import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Index.Type;
import de.mach.tools.neodesigner.core.datamodel.Relations;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.core.datamodel.impl.FieldImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.FieldWrapper;
import de.mach.tools.neodesigner.core.datamodel.impl.ForeignKeyImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.IndexImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.TableImpl;
import de.mach.tools.neodesigner.core.datamodel.viewimpl.ViewField;
import de.mach.tools.neodesigner.core.datamodel.viewimpl.ViewForeignKey;
import de.mach.tools.neodesigner.core.datamodel.viewimpl.ViewIndex;
import de.mach.tools.neodesigner.core.datamodel.viewimpl.ViewTable;


class TestDatamodel {
  static final String kategory = "1.1";
  static final String tableName = "Testtabelle";
  static final String tableName2 = "Testtabelle2";
  static final String fieldName = "TestFeld";
  static final String fieldName2 = "TestFeld2";
  private static final int order = 1;
  private static final int order2 = 2;
  static final String indexName = "XAKTESTINDEX";
  static final String indexName2 = "XIETESTINDEX";
  private static final String indexName3 = "XIFTESTINDEX";
  public static final String indexName4 = "XIFINDEXNEUTEST";
  private static final String indexNameShort = "XIF";
  private static final String indexPrimName = "XPKTESTINDEX";
  static final String foreignKeyName = "R_1704";
  static final DomainId fieldDatatype = DomainId.STRING;
  static final DomainId fieldDatatype2 = DomainId.AMOUNT;
  static final int fieldDatatypeSize = 20;
  static final int fieldDatatypeSize2 = 0;
  static final Boolean fieldIsNull = true;
  static final String xpkName2 = "XPKTesttabelle2";

  private void fieldInterfaceTest(final Table t, final Field f) {
    assertEquals(TestDatamodel.fieldName, f.getName());
    assertEquals(f.getTable(), t);
    assertEquals(TestDatamodel.fieldDatatype, f.getDomain());
    assertEquals(TestDatamodel.fieldIsNull, f.isRequired());
    assertTrue(!f.isPartOfPrimaryKey());
    f.setName(TestDatamodel.fieldName2);
    t.getXpk().addField(f);
    f.setRequired(!TestDatamodel.fieldIsNull);
    f.setDomain(TestDatamodel.fieldDatatype2);

    assertEquals(TestDatamodel.fieldName2, f.getName());
    assertEquals(f.getDomain(), TestDatamodel.fieldDatatype2);
    assertTrue(f.isRequired());
    // assertTrue(f.isPartOfPrimaryKey());
    f.setDomainLength(20);
    assertEquals(0, f.getDomainLength());
    f.setDomain(TestDatamodel.fieldDatatype);
    f.setDomainLength(20);
    assertEquals(20, f.getDomainLength());
  }

  private void fkInterfaceTest(final Table rt, final Index i, final Field f, final ForeignKey fk) {
    fk.setRefTable(rt);
    fk.setIndex(i);
    // Füge Feld hinzu und Prüfe ob das Feld hinzugefügt wurde
    fk.getIndex().addField(f);
    fk.getTable().getXpk().addField(f);
    assertEquals(fk.getRefTable(), rt);
    assertEquals(fk.getIndex(), i);
    assertEquals((int) fk.getIndex().getOrder(f.getName(), false), TestDatamodel.order);
    // Feld entfernen
    assertTrue(fk.getIndex().getFieldList().contains(f));
    fk.getIndex().removeField(0);
    assertTrue(!fk.getIndex().getFieldList().contains(f));
    // Prüfe die Clear Field Funktion
    fk.getIndex().addField(f);
    assertTrue(fk.getIndex().getFieldList().contains(f));
    fk.getIndex().clearFieldList();
    assertTrue(!fk.getIndex().getFieldList().contains(f));
  }

  private void indexInterfaceTest(final Table t, final Index i, final Index i2, final Field f, final Field f2) {
    t.addIndex(i);
    t.addIndex(i2);
    i.addField(f2);
    // i.setAltName(f2.getName(), TestDatamodel.fieldName2);
    // assertTrue(i.getAltName(f2.getName()).equals(TestDatamodel.fieldName2));
    // assertTrue(i.getNameFromAltName(TestDatamodel.fieldName2).endsWith(f2.getName()));
    assertEquals((int) i.getOrder(f2.getName(), false), TestDatamodel.order);
    i.addField(f);
    assertEquals((int) i.getOrder(f.getName(), false), TestDatamodel.order2);
    i.removeField(0);
    assertEquals((int) i.getOrder(f.getName(), false), TestDatamodel.order);

    assertEquals(TestDatamodel.indexName, i.getName());
    assertEquals(i.getTable(), t);
    assertSame(i.getType(), Type.XAK);
    assertTrue(i.isUnique());
    assertTrue(i.getFieldList().contains(f));

    i.setUnique(false);
    assertTrue(!i.isUnique());
    assertSame(i.getType(), Type.XIE);
    assertEquals(i.getName(), TestDatamodel.indexName2);

    i.setUnique(true);
    assertTrue(i.isUnique());
    assertSame(i.getType(), Type.XAK);
    assertEquals(i.getName(), TestDatamodel.indexName);

    i.setType(Type.XIF);
    assertSame(i.getType(), Type.XIF);
    assertTrue(!i.isUnique());

    i.setType(Type.XAK);
    assertSame(i.getType(), Type.XAK);
    assertTrue(i.isUnique());

    i.setType(Type.XIE);
    assertSame(i.getType(), Type.XIE);
    assertTrue(!i.isUnique());

    final Type p = i.getType();
    i.setName(TestDatamodel.indexNameShort);
    i.setUnique(true);
    assertEquals(TestDatamodel.indexNameShort, i.getName());
    assertSame(i.getType(), p);
    assertTrue(!i.isUnique());

    i.setName(TestDatamodel.indexName2);
    assertEquals(TestDatamodel.indexName2, i.getName());
    assertSame(i.getType(), Type.XIE);
    assertTrue(!i.isUnique());

    i.setName(TestDatamodel.indexName3);
    assertEquals(TestDatamodel.indexName3, i.getName());
    assertSame(i.getType(), Type.XIF);

    assertEquals((int) i.getOrder(TestDatamodel.fieldName, false), TestDatamodel.order);
    i.addField(f2);
    i.setOrder(TestDatamodel.fieldName, TestDatamodel.order + 1, false);
    assertEquals((int) i.getOrder(TestDatamodel.fieldName, false), TestDatamodel.order + 1);
  }

  private void tableInterfaceTest(final Table t, final Field f, final Index i, final Index i2, final Index xpk,
                                  final ForeignKey fk) {
    final String fieldnameneu = "asdf";
    t.setCategory(TestDatamodel.kategory);
    t.addField(f);
    t.addField(f);
    t.getIndizies().add(i);
    t.setXpk(xpk);
    t.getForeignKeys().add(fk);

    assertEquals(1, t.getFields().size());
    assertTrue(t.hashCode() != 0);
    assertEquals(TestDatamodel.tableName, t.getName());
    assertEquals(TestDatamodel.kategory, t.getCategory());
    assertNull(t.getTable());
    assertTrue(t.getFields().contains(f));
    assertEquals(t.getXpk().getName(), xpk.getName());
    assertTrue(t.getIndizies().contains(i));
    assertTrue(!t.getIndizies().contains(i2));
    assertTrue(t.getForeignKeys().contains(fk));

    t.setName(TestDatamodel.tableName2);
    assertEquals(TestDatamodel.tableName2, t.getName());
    t.addField(new FieldImpl(fieldnameneu));
    assertNotNull(t.getField(fieldnameneu));
    t.deleteField(fieldnameneu);
    assertNull(t.getField(fieldnameneu));
  }

  @Test
  void testComment() {
    final String comment = "Kommentar zur Tabelle";
    final Table t = new TableImpl(TestDatamodel.tableName);
    final ViewTable vt = new ViewTable(t, true);
    assertEquals("", vt.getComment());
    assertTrue(!vt.isModifiedComment());
    vt.setComment(comment);
    assertNotNull(vt.commentProperty());
    assertEquals(vt.getComment(), comment);
    assertTrue(vt.isModifiedComment());
  }

  @Test
  void testComparing() {
    final Field f = new FieldImpl(TestDatamodel.fieldName);
    final Table t = new TableImpl(TestDatamodel.tableName);
    final Index i = new IndexImpl(TestDatamodel.indexName, Type.XIE, t);
    final ForeignKey fk = new ForeignKeyImpl(TestDatamodel.foreignKeyName, t);

    assertTrue(f.hashCode() != i.hashCode());
    assertTrue(t.hashCode() != fk.hashCode());
  }

  @Test
  void testComparingView() {
    final ViewTable t = new ViewTable(TestDatamodel.tableName);
    final ViewTable rt = new ViewTable(TestDatamodel.tableName2);
    final ViewIndex i = new ViewIndex(new IndexImpl(TestDatamodel.indexName, Type.XAK, t), t);
    final ViewField f = new ViewField(new FieldImpl(TestDatamodel.fieldName, TestDatamodel.fieldDatatype,
                                                    TestDatamodel.fieldDatatypeSize, TestDatamodel.fieldIsNull, "", t),
                                      t);
    final ForeignKey fkOriginal = new ForeignKeyImpl(TestDatamodel.foreignKeyName, t);
    fkOriginal.setRefTable(rt);
    fkOriginal.setIndex(i);
    final ViewForeignKey fk = new ViewForeignKey(fkOriginal, t);
    assertTrue(f.hashCode() != i.hashCode());
    assertTrue(t.hashCode() != fk.hashCode());
  }

  @Test
  void testDomain() {
    final Domain d = new Domain("String:25");
    assertEquals(25, d.getDomainlength());
    assertEquals(d.getDomain(), DomainId.STRING);
    d.setDomainlength(20);
    assertEquals(20, d.getDomainlength());
    d.setDomain(DomainId.BLOB);
    assertEquals(d.getDomain(), DomainId.BLOB);
    assertEquals(Domain.getFromName(Domain.getName(DomainId.LOOKUP)), DomainId.LOOKUP);
    assertEquals(Domain.getFromName(Domain.getName(DomainId.STRING)), DomainId.STRING);
    assertEquals(Domain.getFromName(Domain.getName(DomainId.AMOUNT)), DomainId.AMOUNT);
    assertEquals(Domain.getFromName(Domain.getName(DomainId.COUNTER)), DomainId.COUNTER);
    assertEquals(Domain.getFromName(Domain.getName(DomainId.BOOLEAN)), DomainId.BOOLEAN);
    assertEquals(Domain.getFromName(Domain.getName(DomainId.DATE)), DomainId.DATE);
    assertEquals(Domain.getFromName(Domain.getName(DomainId.BLOB)), DomainId.BLOB);
    assertEquals(Domain.getFromName(Domain.getName(DomainId.CLOB)), DomainId.CLOB);
  }

  @Test
  void testFieldConnectedFKs() {
    final ViewTable t = new ViewTable(TestDatamodel.tableName);
    final ViewTable rt = new ViewTable(TestDatamodel.tableName2);
    final ViewIndex i = new ViewIndex(new IndexImpl(TestDatamodel.indexName, Type.XAK, t), t);
    t.getIndizesRaw().add(i);
    final ViewIndex i2 = new ViewIndex(new IndexImpl(TestDatamodel.indexName + "2", Type.XAK, t), t);
    t.getIndizesRaw().add(i2);
    final ViewField f = new ViewField(new FieldImpl(TestDatamodel.fieldName, TestDatamodel.fieldDatatype,
                                                    TestDatamodel.fieldDatatypeSize, TestDatamodel.fieldIsNull, "", t),
                                      t);
    t.addField(f);
    final ForeignKey fkOriginal = new ForeignKeyImpl(TestDatamodel.foreignKeyName, t);
    final ForeignKey fkOriginal2 = new ForeignKeyImpl(TestDatamodel.foreignKeyName + "2", t);
    fkOriginal.setRefTable(rt);
    fkOriginal.setIndex(i);
    fkOriginal2.setRefTable(rt);
    fkOriginal2.setIndex(i2);
    final ViewForeignKey fk = new ViewForeignKey(fkOriginal, t);
    t.getForeignKeysRaw().add(fk);
    fk.getIndex().addField(f);
    final ViewForeignKey fk2 = new ViewForeignKey(fkOriginal2, t);
    t.getForeignKeysRaw().add(fk2);
    assertEquals(1, t.getField(TestDatamodel.fieldName).getConnectedFks().size());
    assertEquals(t.getField(TestDatamodel.fieldName).getConnectedFks().get(0), fk);
    assertTrue(!t.getField(TestDatamodel.fieldName).getConnectedFks().get(0).equals(fk2));
  }

  @Test
  void testFieldImpl() {
    final Table t = new TableImpl(TestDatamodel.tableName);
    final Field f = new FieldImpl(TestDatamodel.fieldName, TestDatamodel.fieldDatatype, TestDatamodel.fieldDatatypeSize,
                                  TestDatamodel.fieldIsNull, "", t);
    fieldInterfaceTest(t, f);
  }

  @Test
  void testFieldWrapper() {
    final Field f = new FieldImpl(TestDatamodel.fieldName);
    final FieldWrapper fw = new FieldWrapper(f, 1);
    final FieldWrapper fwComp = new FieldWrapper(f, 1);
    final FieldWrapper fwComp2 = new FieldWrapper(f, 2);
    assertEquals(1, fw.getOrder(false));
    assertEquals(fw.toString(), TestDatamodel.fieldName + " 1");
    assertEquals(fw, fwComp);
    assertTrue(!fw.equals(fwComp2));
    assertEquals(fw.hashCode(), -959971302);
  }

  @Test
  void testForeignKeyImpl() {
    final Table t = new TableImpl(TestDatamodel.tableName);
    final Table rt = new TableImpl(TestDatamodel.tableName2);
    final Index i = new IndexImpl(TestDatamodel.indexName, Type.XIF, t);
    final Field f = new FieldImpl(TestDatamodel.fieldName, TestDatamodel.fieldDatatype, TestDatamodel.fieldDatatypeSize,
                                  TestDatamodel.fieldIsNull, "", t);
    final ForeignKey fk = new ForeignKeyImpl(TestDatamodel.foreignKeyName, t);
    fk.setIndex(i);
    assertTrue(fk.getIndex().getFieldList().isEmpty());
    fkInterfaceTest(rt, i, f, fk);
  }

  @Test
  void testForeignKeyRefFieldImpl() {
    final Table t = new TableImpl(TestDatamodel.tableName);
    final Table rt = new TableImpl(TestDatamodel.tableName2);
    final Index i = new IndexImpl(TestDatamodel.indexName, Type.XIF, t);
    final Field f = new FieldImpl(TestDatamodel.fieldName, TestDatamodel.fieldDatatype, TestDatamodel.fieldDatatypeSize,
                                  TestDatamodel.fieldIsNull, "", t);
    final Field fxpk = new FieldImpl(TestDatamodel.fieldName + "bla", TestDatamodel.fieldDatatype,
                                     TestDatamodel.fieldDatatypeSize, TestDatamodel.fieldIsNull, "", t);
    final Field fxpk2 = new FieldImpl(TestDatamodel.fieldName + "blub", TestDatamodel.fieldDatatype,
                                      TestDatamodel.fieldDatatypeSize, TestDatamodel.fieldIsNull, "", t);
    final ForeignKey fk = new ForeignKeyImpl(TestDatamodel.foreignKeyName, t);
    t.addForeignKey(fk, false);
    rt.addForeignKey(fk, true);
    rt.addField(fxpk2);
    rt.addField(fxpk);
    rt.getXpk().addField(fxpk2);
    rt.getXpk().addField(fxpk);
    fk.setRefTable(rt);
    fk.setIndex(i);
    fk.getIndex().addField(f);
    String refFieldName = fxpk.getName();
    Integer refFieldOrder = fk.getRefTable().getXpk().getOrder(refFieldName, false);
    fk.getIndex().setOrder(f.getName(), refFieldOrder, true);
    assertEquals(2, (int) fk.getIndex().getOrder(f.getName(), true));
  }

  @Test
  void testHasFeature() {
    final ViewTable t = new ViewTable(TestDatamodel.tableName);
    final ViewTable rt = new ViewTable(TestDatamodel.tableName2);
    final ViewIndex i = new ViewIndex(new IndexImpl(TestDatamodel.indexName, Type.XIF, t), t);
    final ViewField f = new ViewField(new FieldImpl(TestDatamodel.fieldName, TestDatamodel.fieldDatatype,
                                                    TestDatamodel.fieldDatatypeSize, TestDatamodel.fieldIsNull, "", t),
                                      t);
    assertTrue(!f.hasIndex());
    t.addField(f);
    t.getIndizesRaw().add(i);
    i.addField(f);
    assertTrue(f.hasIndex());
    assertTrue(!i.hasFk());
    i.setOrder(fieldName, 1, true);
    final ViewForeignKey fk = new ViewForeignKey("R_101", i, t);
    fk.setRefTable(rt);
    fk.setIndex(i);
    t.getForeignKeysRaw().add(fk);
    assertTrue(i.hasFk());
  }

  @Test
  void testIndexEnum() {
    assertEquals(4, Type.values().length);
    assertEquals(0, Type.valueOf("XPK").compareTo(Type.XPK));
  }

  @Test
  void testIndexImpl() {
    final Table t = new TableImpl(TestDatamodel.tableName);
    final Index i = new IndexImpl(TestDatamodel.indexName, Type.XAK, t);
    final Index i2 = new IndexImpl(TestDatamodel.indexName + "2", Type.XAK, t);
    final Field f = new FieldImpl(TestDatamodel.fieldName, TestDatamodel.fieldDatatype, TestDatamodel.fieldDatatypeSize,
                                  TestDatamodel.fieldIsNull, "", t);
    final Field f2 = new FieldImpl(TestDatamodel.fieldName2, TestDatamodel.fieldDatatype,
                                   TestDatamodel.fieldDatatypeSize, TestDatamodel.fieldIsNull, "", t);
    indexInterfaceTest(t, i, i2, f, f2);
  }

  @Test
  void testIndexImplCopy() {
    final Table t = new TableImpl(TestDatamodel.tableName);
    final Index i = new IndexImpl(TestDatamodel.indexName, Type.XAK, t);
    final Index i2 = new IndexImpl(TestDatamodel.indexName + "2", Type.XAK, t);
    final Field f = new FieldImpl(TestDatamodel.fieldName, TestDatamodel.fieldDatatype, TestDatamodel.fieldDatatypeSize,
                                  TestDatamodel.fieldIsNull, "", t);
    final Field f2 = new FieldImpl(TestDatamodel.fieldName2, TestDatamodel.fieldDatatype,
                                   TestDatamodel.fieldDatatypeSize, TestDatamodel.fieldIsNull, "", t);
    final Index ix = new IndexImpl(i, t);
    indexInterfaceTest(t, ix, i2, f, f2);
  }

  @Test
  void testRelations() {
    assertEquals(5, Relations.Type.values().length);
    assertNotNull(Relations.getType(Relations.Type.DEFAULT));
    assertTrue(Relations.getType(Relations.Type.DATA).contains("has_column"));
    assertTrue(Relations.getType(Relations.Type.INDEX).contains("indexed_by"));
    assertTrue(Relations.getType(Relations.Type.REFERENCE).contains("references"));
    assertTrue(Relations.getType(Relations.Type.XPK).contains("identified_by"));
  }

  @Test
  void testReplaceField() {
    final Table t = new TableImpl(TestDatamodel.tableName);
    final Field f = new FieldImpl(TestDatamodel.fieldName);
    final Field f2 = new FieldImpl(TestDatamodel.fieldName2);
    final Index i = new IndexImpl(TestDatamodel.indexName, t);
    i.addField(f);
    assertEquals(i.getFieldList().get(0), f);
    i.replaceField(f2, f.getName());
    assertTrue(!i.getFieldList().get(0).equals(f));
    assertEquals(i.getFieldList().get(0), f2);
  }

  @Test
  void testReplaceViewField() {
    final Table t = new TableImpl(TestDatamodel.tableName);
    final ViewTable vt = new ViewTable(t, true);
    final ViewIndex vi = new ViewIndex(new IndexImpl(TestDatamodel.indexName, t), vt);
    final ViewField vf = new ViewField(new FieldImpl(TestDatamodel.fieldName), vt);
    final ViewField vf2 = new ViewField(new FieldImpl(TestDatamodel.fieldName2), vt);
    vt.addField(vf);
    vt.addIndex(vi);
    vi.addField(vf);
    assertEquals(vi.getFieldList().get(0), vf);
    vi.replaceField(vf2, vf.getName());
    assertTrue(!vi.getFieldList().get(0).equals(vf));
    assertEquals(vi.getFieldList().get(0), vf2);
  }

  @Test
  void testTableImpl() {
    final Table t = new TableImpl(TestDatamodel.tableName);
    final Field f = new FieldImpl(TestDatamodel.fieldName);
    final Index i = new IndexImpl(TestDatamodel.indexName, t);
    final Index i2 = new IndexImpl(TestDatamodel.indexName2, null);
    final Index xpk = new IndexImpl(TestDatamodel.indexPrimName, t);
    final ForeignKey fk = new ForeignKeyImpl(TestDatamodel.foreignKeyName, t);
    tableInterfaceTest(t, f, i, i2, xpk, fk);
  }

  @Test
  void testViewField() {
    final ViewTable t = new ViewTable(TestDatamodel.tableName);
    ViewField f = new ViewField(new FieldImpl(TestDatamodel.fieldName, TestDatamodel.fieldDatatype,
                                              TestDatamodel.fieldDatatypeSize, TestDatamodel.fieldIsNull, "", t),
                                t);
    assertTrue(!f.isModifiedName());
    assertTrue(!f.isModifiedPrim());
    assertTrue(!f.isModifiedReq());
    assertTrue(!f.isModifiedDomain());
    fieldInterfaceTest(t, f);
    f.setPartOfPrimaryKey(true);
    assertNotNull(f.nameProperty());
    assertNotNull(f.primProperty());
    assertNotNull(f.requiredProperty());
    assertNotNull(f.typeOfDataProperty());
    assertTrue(!f.isNewCreated());
    assertTrue(f.isModifiedName());
    assertTrue(f.isModifiedPrim());
    assertTrue(f.isModifiedReq());
    assertTrue(f.isModifiedDomain());
    assertEquals(f.getOldName(), TestDatamodel.fieldName);
    f.saved();
    assertTrue(!f.isModifiedName());
    assertTrue(!f.isModifiedPrim());
    assertTrue(!f.isModifiedReq());
    assertTrue(!f.isModifiedDomain());
    assertEquals(f.getOldName(), f.getName());

    f = new ViewField(TestDatamodel.fieldName, TestDatamodel.fieldDatatype, TestDatamodel.fieldDatatypeSize,
                      TestDatamodel.fieldIsNull, t, "");
    f.setAsNewCreated();
    fieldInterfaceTest(t, f);
    f.setTypeOfData("DaTe");
    assertEquals(f.getTypeOfData(), Domain.getName(DomainId.DATE));
    // Entferne Prim, setzte nullable und dann teste ob boolean wieder auf required
    // zurücksetzt
    f.getTable().getXpk().removeField(0);
    f.setPartOfPrimaryKey(false);
    f.setRequired(false);
    assertTrue(!f.isRequired());
    f.setDomain(DomainId.BOOLEAN);
    assertTrue(f.isRequired());
  }

  @Test
  void testdeepcopy() {
    Model m = TestUtil.getDatamodel();
    Optional<Table> t = m.getTable("TABLE0");
    Assert.assertTrue(t.isPresent());
    ViewTable vt = new ViewTable(t.get(), true);
    assertEquals(t.get().getName(), vt.getName());
    assertEquals("TABLE0: 0,0" + "[FELD0, FELDZWEI0, FELDDREI0]" + "XPKTABLE0(TABLE0)"
                 + "[XIE100TESTINDEX(TABLE0), XIF100BLABLUB(TABLE0)]" + "[R_100(TABLE0)]",
                 vt.toString().replaceAll(Strings.EOL, ""));
    for (Field f : vt.getFields()) {
      assertSame(t.get().getField(f.getName()).isPartOfPrimaryKey(), vt.getField(f.getName()).isPartOfPrimaryKey());
      assertEquals(vt.getField(f.getName()).isPartOfPrimaryKey(),
                   vt.getDataFieldsRaw().get(vt.getDataFieldsRaw().indexOf(f)).primProperty().get());
    }
    // ViewTable Test schreiben, der Deep Copy Constructor testet (Ordnung des Original
    // Indexes muss erhalten bleiben)
    Index original = t.get().getIndizies().get(0);
    Index comp = vt.getIndizesRaw().get(1);
    for (Field f : comp.getFieldList()) {
      assertSame(original.getOrder(f.getName(), false), comp.getOrder(f.getName(), false));
    }
    assertEquals(original.getFieldList(), comp.getFieldList());
    // FK Test schreiben, der Deep Copy Constructor testet (u.a. Order von PK der
    // RefTable über FK)
    ForeignKey originalfk = t.get().getForeignKeys().get(0);
    ForeignKey compfk = vt.getForeignKeysRaw().get(0);
    for (Field f : compfk.getIndex().getFieldList()) {
      assertSame(compfk.getIndex().getOrder(f.getName(), true), compfk.getIndex().getOrder(f.getName(), true));
    }
  }

  @Test
  void testViewForeignKey() {
    final ViewTable t = new ViewTable(TestDatamodel.tableName);
    final ViewTable rt = new ViewTable(TestDatamodel.tableName2);
    final ViewIndex i = new ViewIndex(new IndexImpl(TestDatamodel.indexName, Type.XAK, t), t);
    final ViewField f = new ViewField(new FieldImpl(TestDatamodel.fieldName, TestDatamodel.fieldDatatype,
                                                    TestDatamodel.fieldDatatypeSize, TestDatamodel.fieldIsNull, "", t),
                                      t);
    final ForeignKey fkOriginal = new ForeignKeyImpl(TestDatamodel.foreignKeyName, t);
    fkOriginal.setRefTable(rt);
    fkOriginal.setIndex(i);

    ViewForeignKey fk = new ViewForeignKey(fkOriginal, t);
    assertTrue(!fk.isModifiedRel());
    fk.getIndex().addField(f);
    assertNotNull(fk.fieldListStringProperty());
    assertEquals(fk.getRefTable(), rt);
    assertEquals(fk.getIndex(), i);

    fk = new ViewForeignKey(TestDatamodel.foreignKeyName, i, t);
    assertTrue(!fk.isModifiedRel());
    fk.setRefTable(rt);
    fk.setIndex(i);
    assertEquals(fk.getVRefTable(), rt);
    assertEquals(fk.getVIndex(), i);
    assertTrue(fk.isModifiedRel());
    fk.saved();
    assertTrue(!fk.isModifiedRel());
    fk.setModifiedRel();
    assertTrue(fk.isModifiedRel());
    fk.getIndex().clearFieldList();
    assertNotNull(fk.getRefTableName());
    assertTrue(fk.modified().get());

    fkInterfaceTest(rt, i, f, fk);
  }

  @Test
  void testViewIndex() {
    final ViewTable t = new ViewTable(TestDatamodel.tableName);
    final ViewIndex i = new ViewIndex(new IndexImpl(TestDatamodel.indexName, Type.XAK, t), t);
    final ViewIndex i2 = new ViewIndex(new IndexImpl(TestDatamodel.indexName + "2", Type.XAK, t), t);
    final ViewField f = new ViewField(new FieldImpl(TestDatamodel.fieldName, TestDatamodel.fieldDatatype,
                                                    TestDatamodel.fieldDatatypeSize, TestDatamodel.fieldIsNull, "", t),
                                      t);

    assertTrue(!i.isModifiedDatafields());
    assertTrue(!i.isModifiedName());
    assertTrue(!i.isModifiedUnique());
    assertTrue(!i.isNewCreated());

    final Field f2 = new FieldImpl(TestDatamodel.fieldName2, TestDatamodel.fieldDatatype,
                                   TestDatamodel.fieldDatatypeSize, TestDatamodel.fieldIsNull, "", t);
    indexInterfaceTest(t, i, i2, f, f2);
    assertTrue(i.isModifiedDatafields());
    assertTrue(i.isModifiedName());
    assertTrue(i.isModifiedUnique());
    i.saved();
    assertTrue(!i.isModifiedDatafields());
    assertTrue(!i.isModifiedName());
    assertTrue(!i.isModifiedUnique());
    assertNotNull(i.fieldListStringProperty());
    assertNotNull(i.uniqueProperty());
    i.setName(TestDatamodel.indexName2);
    i.setName(TestDatamodel.indexName);
    assertTrue(i.isModifiedName());
  }

  @Test
  void testViewIndex2() {
    final ViewTable t = new ViewTable(TestDatamodel.tableName);

    final Field f = new FieldImpl(TestDatamodel.fieldName, TestDatamodel.fieldDatatype, TestDatamodel.fieldDatatypeSize,
                                  TestDatamodel.fieldIsNull, "", t);
    final Field f2 = new FieldImpl(TestDatamodel.fieldName2, TestDatamodel.fieldDatatype,
                                   TestDatamodel.fieldDatatypeSize, TestDatamodel.fieldIsNull, "", t);
    t.addField(f);
    t.addField(f2);
    final Index indeximpl = new IndexImpl(TestDatamodel.indexName, Type.XAK, t);
    indeximpl.addField(f);
    indeximpl.addField(f2);
    final ViewIndex i = new ViewIndex(indeximpl, t);
    assertEquals(2, i.getFieldList().size());
    assertTrue(!i.hasFk());
  }

  @Test
  void testViewTable() {
    final ViewTable t = new ViewTable(TestDatamodel.tableName);
    final ViewField f = new ViewField(TestDatamodel.fieldName, TestDatamodel.fieldDatatype,
                                      TestDatamodel.fieldDatatypeSize, TestDatamodel.fieldIsNull, t, "");
    final ViewIndex xpk = new ViewIndex(TestDatamodel.indexPrimName, t);

    t.setCategory(TestDatamodel.kategory);
    t.addField(f);
    t.setXpk(xpk);

    assertTrue(t.hashCode() != 0);
    assertEquals(TestDatamodel.tableName, t.getName());
    assertEquals(TestDatamodel.kategory, t.getCategory());
    assertNull(t.getTable());
    assertTrue(t.getFields().contains(f));
    assertTrue(t.getDataFieldsRaw().contains(f));
    assertEquals(t.getXpk().getName(), xpk.getName());

    t.setName(TestDatamodel.tableName2);
    assertEquals(TestDatamodel.tableName2, t.getName());
  }

  @Test
  void testViewTable2() {
    final Table t = new TableImpl(TestDatamodel.tableName);
    final Field f = new FieldImpl(TestDatamodel.fieldName, TestDatamodel.fieldDatatype, TestDatamodel.fieldDatatypeSize,
                                  TestDatamodel.fieldIsNull, "", t);
    final Index i = new IndexImpl(TestDatamodel.indexName, t);
    final Index i2 = new IndexImpl(TestDatamodel.indexName2, null);
    final Index xpk = new IndexImpl(TestDatamodel.indexPrimName, t);
    final ForeignKey fk = new ForeignKeyImpl(TestDatamodel.foreignKeyName, t);
    t.addField(f);
    t.setXpk(xpk);
    t.getXpk().addField(f);
    fk.setIndex(i2);
    fk.setRefTable(t);
    tableInterfaceTest(t, f, i, i2, xpk, fk);
    t.setName(TestDatamodel.tableName);
    final ViewTable tt = new ViewTable(t, true);
    tableInterfaceTest(tt, f, i, i2, xpk, fk);
  }

  @Test
  void testViewTable3() {
    final Table t = new TableImpl(TestDatamodel.tableName);
    final Field f = new FieldImpl(TestDatamodel.fieldName, TestDatamodel.fieldDatatype, TestDatamodel.fieldDatatypeSize,
                                  TestDatamodel.fieldIsNull, "", t);
    final Index i = new IndexImpl(TestDatamodel.indexName, t);
    final Index i2 = new IndexImpl(TestDatamodel.indexName2, null);
    final Index xpk = new IndexImpl(TestDatamodel.indexPrimName, t);
    t.addField(f);
    t.setXpk(xpk);
    t.getXpk().addField(f);
    final ForeignKey fk = new ForeignKeyImpl(TestDatamodel.foreignKeyName, t);
    fk.setIndex(i2);
    fk.setRefTable(t);
    tableInterfaceTest(t, f, i, i2, xpk, fk);
    t.setName(TestDatamodel.tableName);
    final ViewTable tt = new ViewTable(t, true);
    assertEquals(1704, tt.getForeignKeyNumber(100));
    assertEquals(1805, tt.getForeignKeyNumber(1805));
    assertEquals(1704, tt.getForeignKeyNumber(1703));
    assertEquals(1704, tt.getForeignKeyNumber(1704));
    assertEquals(1705, tt.getForeignKeyNumber(1705));
    tt.getForeignKeysRaw().get(0).setName("R_neuesNamesNsystem");
    assertEquals(101, tt.getForeignKeyNumber(101));
  }
}
