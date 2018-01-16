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
import org.junit.Assert;
import org.junit.Test;

public class TestDatamodel {
  public static final String kategory = "1.1";
  public static final String tableName = "Testtabelle";
  public static final String tableName2 = "Testtabelle2";
  public static final String fieldName = "TestFeld";
  public static final String fieldName2 = "TestFeld2";
  public static final int order = 1;
  public static final int order2 = 2;
  public static final String indexName = "XAKTestIndex";
  public static final String indexName2 = "XIETestIndex";
  public static final String indexName3 = "XIFTestIndex";
  public static final String indexName4 = "XIFIndexNeuTest";
  public static final String indexNameShort = "XIF";
  public static final String indexPrimName = "TestPrimIndex";
  public static final String foreignKeyName = "R_TestForeignKey";
  public static final DomainId fieldDatatype = DomainId.STRING;
  public static final DomainId fieldDatatype2 = DomainId.AMOUNT;
  public static final int fieldDatatypeSize = 20;
  public static final int fieldDatatypeSize2 = 0;
  public static final Boolean fieldIsNull = true;

  @Test
  public void testFieldWrapper() {
    final Field f = new FieldImpl(TestDatamodel.fieldName);
    final FieldWrapper fw = new FieldWrapper(f, TestDatamodel.fieldName2, 1);
    final FieldWrapper fwComp = new FieldWrapper(f, TestDatamodel.fieldName2, 1);
    final FieldWrapper fwComp2 = new FieldWrapper(f, TestDatamodel.fieldName2, 2);
    fw.setAltName(TestDatamodel.tableName2);
    Assert.assertTrue(fw.getAltName().equals(TestDatamodel.tableName2));
    Assert.assertTrue(fw.getOrder() == 1);
    Assert.assertTrue(fw.toString().equals(TestDatamodel.fieldName + " (" + TestDatamodel.tableName2 + ") 1"));
    Assert.assertTrue(fw.equals(fwComp));
    Assert.assertTrue(!fw.equals(fwComp2));
    Assert.assertTrue(fw.hashCode() == -1997653415);
  }

  @Test
  public void testRelations() {
    Assert.assertTrue(Relations.Type.valueOf("CONNECTED").compareTo(Relations.Type.CONNECTED) == 0);
    Assert.assertTrue(Relations.Type.values().length == 7);
    Assert.assertTrue(Relations.getType(Relations.Type.DEFAULT).contains(""));
    Assert.assertTrue(Relations.getType(Relations.Type.CONNECTED).contains("CONNECTED"));
    Assert.assertTrue(Relations.getType(Relations.Type.DATA).contains("DATA"));
    Assert.assertTrue(Relations.getType(Relations.Type.FOREIGNKEY).contains("FOREIGNKEY"));
    Assert.assertTrue(Relations.getType(Relations.Type.INDEX).contains("INDEX"));
    Assert.assertTrue(Relations.getType(Relations.Type.REFERENCE).contains("REFERENCE"));
    Assert.assertTrue(Relations.getType(Relations.Type.XPK).contains("XPK"));

  }

  @Test
  public void testTableImpl() {
    final Table t = new TableImpl(TestDatamodel.tableName);
    final Field f = new FieldImpl(TestDatamodel.fieldName);
    final Index i = new IndexImpl(TestDatamodel.indexName, t);
    final Index i2 = new IndexImpl(TestDatamodel.indexName2, null);
    final Index xpk = new IndexImpl(TestDatamodel.indexPrimName, t);
    final ForeignKey fk = new ForeignKeyImpl(TestDatamodel.foreignKeyName, t);
    tableInterfaceTest(t, f, i, i2, xpk, fk);
  }

  @Test
  public void testViewTable2() {
    final Table t = new TableImpl(TestDatamodel.tableName);
    final Field f = new FieldImpl(TestDatamodel.fieldName);
    final Index i = new IndexImpl(TestDatamodel.indexName, t);
    final Index i2 = new IndexImpl(TestDatamodel.indexName2, null);
    final Index xpk = new IndexImpl(TestDatamodel.indexPrimName, t);
    final ForeignKey fk = new ForeignKeyImpl(TestDatamodel.foreignKeyName, t);
    fk.setIndex(i2);
    fk.setRefTable(t);
    tableInterfaceTest(t, f, i, i2, xpk, fk);
    t.setName(TestDatamodel.tableName);
    final ViewTable tt = new ViewTable(t);
    tableInterfaceTest(tt, f, i, i2, xpk, fk);
  }

  @Test
  public void testViewTable() {
    final ViewTable t = new ViewTable(TestDatamodel.tableName);
    final ViewField f = new ViewField(TestDatamodel.fieldName, TestDatamodel.fieldDatatype,
        TestDatamodel.fieldDatatypeSize, TestDatamodel.fieldIsNull, t, "");
    final ViewIndex xpk = new ViewIndex(TestDatamodel.indexPrimName, t);

    t.setCategory(TestDatamodel.kategory);
    t.addField(f);
    t.setXpk(xpk);

    Assert.assertTrue(!t.equals(null));
    Assert.assertTrue(t.hashCode() != 0);
    Assert.assertTrue(t.toString(), TestDatamodel.tableName.equals(t.getName()));
    Assert.assertTrue("T Kategory", TestDatamodel.kategory.equals(t.getCategory()));
    Assert.assertTrue(t.getTable() == null);
    Assert.assertTrue(t.getFields().contains(f));
    Assert.assertTrue(t.getVData().contains(f));
    Assert.assertTrue(t.getXpk().getName().equals(xpk.getName()));

    t.setName(TestDatamodel.tableName2);
    Assert.assertTrue("T Name", TestDatamodel.tableName2.equals(t.getName()));
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

    Assert.assertTrue(t != null);
    Assert.assertTrue(t.getFields().size() == 1);
    Assert.assertTrue(t.hashCode() != 0);
    Assert.assertTrue(t.toString(), TestDatamodel.tableName.equals(t.getName()));
    Assert.assertTrue("T Kategory", TestDatamodel.kategory.equals(t.getCategory()));
    Assert.assertTrue(t.getTable() == null);
    Assert.assertTrue(t.getFields().contains(f));
    Assert.assertTrue(t.getXpk().getName().equals(xpk.getName()));
    Assert.assertTrue(t.getIndizies().contains(i));
    Assert.assertTrue(!t.getIndizies().contains(i2));
    Assert.assertTrue(t.getForeignKeys().contains(fk));

    t.setName(TestDatamodel.tableName2);
    Assert.assertTrue("T Name", TestDatamodel.tableName2.equals(t.getName()));
    t.addField(new FieldImpl(fieldnameneu));
    Assert.assertTrue(t.getField(fieldnameneu).isPresent());
    t.deleteField(fieldnameneu);
    Assert.assertTrue(!t.getField(fieldnameneu).isPresent());
  }

  @Test
  public void testFieldImpl() {
    final Table t = new TableImpl(TestDatamodel.tableName);
    final Field f = new FieldImpl(TestDatamodel.fieldName, TestDatamodel.fieldDatatype, TestDatamodel.fieldDatatypeSize,
        TestDatamodel.fieldIsNull, "", t);

    fieldInterfaceTest(t, f);
  }

  @Test
  public void testViewField() {
    final ViewTable t = new ViewTable(TestDatamodel.tableName);
    ViewField f = new ViewField(new FieldImpl(TestDatamodel.fieldName, TestDatamodel.fieldDatatype,
        TestDatamodel.fieldDatatypeSize, TestDatamodel.fieldIsNull, "", t), t);
    Assert.assertTrue(f.isModifiedName() == false);
    Assert.assertTrue(f.isModifiedPrim() == false);
    Assert.assertTrue(f.isModifiedReq() == false);
    Assert.assertTrue(f.isModifiedDomain() == false);
    fieldInterfaceTest(t, f);
    Assert.assertTrue(f.nameProperty() != null);
    Assert.assertTrue(f.primProperty() != null);
    Assert.assertTrue(f.requiredProperty() != null);
    Assert.assertTrue(f.typeOfDataProperty() != null);
    Assert.assertTrue(f.isNewCreated() == false);
    Assert.assertTrue(f.isModifiedName() == true);
    Assert.assertTrue(f.isModifiedPrim() == true);
    Assert.assertTrue(f.isModifiedReq() == false);
    Assert.assertTrue(f.isModifiedDomain() == true);
    Assert.assertTrue(f.getOldName().equals(TestDatamodel.fieldName));
    f.saved();
    Assert.assertTrue(f.isModifiedName() == false);
    Assert.assertTrue(f.isModifiedPrim() == false);
    Assert.assertTrue(f.isModifiedReq() == false);
    Assert.assertTrue(f.isModifiedDomain() == false);
    Assert.assertTrue(f.getOldName().equals(f.getName()));

    f = new ViewField(TestDatamodel.fieldName, TestDatamodel.fieldDatatype, TestDatamodel.fieldDatatypeSize,
        TestDatamodel.fieldIsNull, t, "");
    f.setAsNewCreated();
    fieldInterfaceTest(t, f);
    f.setTypeOfData("DaTe");
    Assert.assertTrue(f.getTypeOfData().equals(Domain.getName(DomainId.DATE)));
  }

  private void fieldInterfaceTest(final Table t, final Field f) {
    Assert.assertTrue(TestDatamodel.fieldName.equals(f.getName()));
    Assert.assertTrue(f.getTable().equals(t));
    Assert.assertTrue(f.getDomain().equals(TestDatamodel.fieldDatatype));
    Assert.assertTrue(f.isRequired() == TestDatamodel.fieldIsNull);

    f.setName(TestDatamodel.fieldName2);
    f.setPartOfPrimaryKey(true);
    f.setRequired(!TestDatamodel.fieldIsNull);
    f.setDomain(TestDatamodel.fieldDatatype2);

    Assert.assertTrue(TestDatamodel.fieldName2.equals(f.getName()));
    Assert.assertTrue(f.getDomain().equals(TestDatamodel.fieldDatatype2));
    Assert.assertTrue(f.isRequired() == true);
    Assert.assertTrue(f.isPartOfPrimaryKey() == true);
    f.setDomainLength(20);
    Assert.assertTrue(f.getDomainLength() == 0);
    f.setDomain(TestDatamodel.fieldDatatype);
    f.setDomainLength(20);
    Assert.assertTrue(f.getDomainLength() == 20);
  }

  @Test
  public void testIndexImpl() {
    final Table t = new TableImpl(TestDatamodel.tableName);
    final Index i = new IndexImpl(TestDatamodel.indexName, Type.XAK, t);
    final Field f = new FieldImpl(TestDatamodel.fieldName, TestDatamodel.fieldDatatype, TestDatamodel.fieldDatatypeSize,
        TestDatamodel.fieldIsNull, "", t);
    final Field f2 = new FieldImpl(TestDatamodel.fieldName2, TestDatamodel.fieldDatatype,
        TestDatamodel.fieldDatatypeSize, TestDatamodel.fieldIsNull, "", t);
    indexInterfaceTest(t, i, f, f2);
  }

  @Test
  public void testIndexImplCopy() {
    final Table t = new TableImpl(TestDatamodel.tableName);
    final Index i = new IndexImpl(TestDatamodel.indexName, Type.XAK, t);
    final Field f = new FieldImpl(TestDatamodel.fieldName, TestDatamodel.fieldDatatype, TestDatamodel.fieldDatatypeSize,
        TestDatamodel.fieldIsNull, "", t);
    final Field f2 = new FieldImpl(TestDatamodel.fieldName2, TestDatamodel.fieldDatatype,
        TestDatamodel.fieldDatatypeSize, TestDatamodel.fieldIsNull, "", t);

    final Index ix = new IndexImpl(i);
    indexInterfaceTest(t, ix, f, f2);
  }

  @Test
  public void testViewIndex() {
    final ViewTable t = new ViewTable(TestDatamodel.tableName);
    final ViewIndex i = new ViewIndex(new IndexImpl(TestDatamodel.indexName, Type.XAK, t), t);
    final ViewField f = new ViewField(new FieldImpl(TestDatamodel.fieldName, TestDatamodel.fieldDatatype,
        TestDatamodel.fieldDatatypeSize, TestDatamodel.fieldIsNull, "", t), t);

    Assert.assertTrue(!i.isModifiedDatafields());
    Assert.assertTrue(!i.isModifiedName());
    Assert.assertTrue(!i.isModifiedUnique());
    Assert.assertTrue(!i.isNewCreated());

    final Field f2 = new FieldImpl(TestDatamodel.fieldName2, TestDatamodel.fieldDatatype,
        TestDatamodel.fieldDatatypeSize, TestDatamodel.fieldIsNull, "", t);
    indexInterfaceTest(t, i, f, f2);
    Assert.assertTrue(i.isModifiedDatafields());
    Assert.assertTrue(i.isModifiedName());
    Assert.assertTrue(i.isModifiedUnique());
    i.saved();
    Assert.assertTrue(!i.isModifiedDatafields());
    Assert.assertTrue(!i.isModifiedName());
    Assert.assertTrue(!i.isModifiedUnique());
    Assert.assertTrue(i.fieldListStringProperty() != null);
    Assert.assertTrue(i.uniqueProperty() != null);
    i.setName(TestDatamodel.indexName2);
    i.setName(TestDatamodel.indexName);
    Assert.assertTrue(i.isModifiedName());
  }

  @Test
  public void testViewIndex2() {
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
    Assert.assertTrue(i.getFieldList().size() == 2);
    Assert.assertTrue(!i.hasFk());

  }

  private void indexInterfaceTest(final Table t, final Index i, final Field f, final Field f2) {
    i.addField(f2);
    i.setAltName(f2.getName(), TestDatamodel.fieldName2);
    Assert.assertTrue(i.getAltName(f2.getName()).equals(TestDatamodel.fieldName2));
    Assert.assertTrue(i.getNameFromAltName(TestDatamodel.fieldName2).endsWith(f2.getName()));
    Assert.assertTrue(i.getOrder(f2.getName()).equals(TestDatamodel.order));
    i.addField(f);
    Assert.assertTrue(i.getOrder(f.getName()).equals(TestDatamodel.order2));
    i.removeField(0);
    Assert.assertTrue(i.getOrder(f.getName()).equals(TestDatamodel.order));

    Assert.assertTrue(TestDatamodel.indexName.equals(i.getName()));
    Assert.assertTrue(i.getTable().equals(t));
    Assert.assertTrue(i.getType() == Type.XAK);
    Assert.assertTrue(i.isUnique() == true);
    Assert.assertTrue(i.getFieldList().contains(f));

    i.setUnique(false);
    Assert.assertTrue(i.isUnique() == false);
    Assert.assertTrue(i.getType() == Type.XIE);
    Assert.assertTrue(i.getName().equals(TestDatamodel.indexName2));

    i.setUnique(true);
    Assert.assertTrue(i.isUnique() == true);
    Assert.assertTrue(i.getType() == Type.XAK);
    Assert.assertTrue(i.getName().equals(TestDatamodel.indexName));

    i.setType(Type.XIF);
    Assert.assertTrue(i.getType() == Type.XIF);
    Assert.assertTrue(i.isUnique() == false);

    i.setType(Type.XAK);
    Assert.assertTrue(i.getType() == Type.XAK);
    Assert.assertTrue(i.isUnique() == true);

    i.setType(Type.XIE);
    Assert.assertTrue(i.getType() == Type.XIE);
    Assert.assertTrue(i.isUnique() == false);

    final Type p = i.getType();
    i.setName(TestDatamodel.indexNameShort);
    i.setUnique(true);
    Assert.assertTrue(TestDatamodel.indexNameShort.equals(i.getName()));
    Assert.assertTrue(i.getType() == p);
    Assert.assertTrue(i.isUnique() == false);

    i.setName(TestDatamodel.indexName2);
    Assert.assertTrue(TestDatamodel.indexName2.equals(i.getName()));
    Assert.assertTrue(i.getType() == Type.XIE);
    Assert.assertTrue(i.isUnique() == false);

    i.setName(TestDatamodel.indexName3);
    Assert.assertTrue(TestDatamodel.indexName3.equals(i.getName()));
    Assert.assertTrue(i.getType() == Type.XIF);

    Assert.assertTrue(i.getOrder(TestDatamodel.fieldName) == TestDatamodel.order);
    i.setOrder(TestDatamodel.fieldName, TestDatamodel.order + 1);
    Assert.assertTrue(i.getOrder(TestDatamodel.fieldName) == TestDatamodel.order + 1);
  }

  @Test
  public void testForeignKeyImpl() {
    final Table t = new TableImpl(TestDatamodel.tableName);
    final Table rt = new TableImpl(TestDatamodel.tableName2);
    final Index i = new IndexImpl(TestDatamodel.indexName, Type.XIF, t);
    final Field f = new FieldImpl(TestDatamodel.fieldName, TestDatamodel.fieldDatatype, TestDatamodel.fieldDatatypeSize,
        TestDatamodel.fieldIsNull, "", t);
    final ForeignKey fk = new ForeignKeyImpl(TestDatamodel.foreignKeyName, t);
    fk.setIndex(i);
    Assert.assertTrue(fk.getIndex().getFieldList().isEmpty());

    fkInterfaceTest(rt, i, f, fk);
  }

  private void fkInterfaceTest(final Table rt, final Index i, final Field f, final ForeignKey fk) {
    fk.setRefTable(rt);
    fk.setIndex(i);
    fk.getIndex().addField(f);
    Assert.assertTrue(fk.getRefTable().equals(rt));
    Assert.assertTrue(fk.getIndex().equals(i));
    Assert.assertTrue(fk.getIndex().getFieldList().contains(f));
    fk.getIndex().removeField(0);
    Assert.assertTrue(!fk.getIndex().getFieldList().contains(f));
    fk.getIndex().addField(f, TestDatamodel.fieldName2);
    Assert.assertTrue(fk.getIndex().getAltName(f.getName()).equals(TestDatamodel.fieldName2));
    fk.getIndex().setAltName(f.getName(), TestDatamodel.tableName2);
    Assert.assertTrue(fk.getIndex().getAltName(f.getName()).equals(TestDatamodel.tableName2));
    Assert.assertTrue(fk.getIndex().getOrder(f.getName()).equals(TestDatamodel.order));
    Assert.assertTrue(fk.getIndex().getFieldList().contains(f));
    fk.getIndex().clearFieldList();
    Assert.assertTrue(!fk.getIndex().getFieldList().contains(f));
  }

  @Test
  public void testViewForeignKey() {
    final ViewTable t = new ViewTable(TestDatamodel.tableName);
    final ViewTable rt = new ViewTable(TestDatamodel.tableName2);
    final ViewIndex i = new ViewIndex(new IndexImpl(TestDatamodel.indexName, Type.XAK, t), t);
    final ViewField f = new ViewField(new FieldImpl(TestDatamodel.fieldName, TestDatamodel.fieldDatatype,
        TestDatamodel.fieldDatatypeSize, TestDatamodel.fieldIsNull, "", t), t);
    final ForeignKey fkOriginal = new ForeignKeyImpl(TestDatamodel.foreignKeyName, t);
    fkOriginal.setRefTable(rt);
    fkOriginal.setIndex(i);

    ViewForeignKey fk = new ViewForeignKey(fkOriginal, t);
    Assert.assertTrue(!fk.isModifiedRel());
    fk.getIndex().addField(f);
    Assert.assertTrue(fk.fieldListStringProperty() != null);
    Assert.assertTrue(fk.getRefTable().equals(rt));
    Assert.assertTrue(fk.getIndex().equals(i));

    fk = new ViewForeignKey(TestDatamodel.foreignKeyName, i, t);
    Assert.assertTrue(!fk.isModifiedRel());
    fk.setRefTable(rt);
    fk.setIndex(i);
    Assert.assertTrue(fk.getVRefTable().equals(rt));
    Assert.assertTrue(fk.getVIndex().equals(i));
    Assert.assertTrue(fk.isModifiedRel());
    fk.saved();
    Assert.assertTrue(!fk.isModifiedRel());
    fk.setModifiedRel();
    Assert.assertTrue(fk.isModifiedRel());
    fk.getIndex().clearFieldList();
    Assert.assertTrue(fk.getRefTableName() != null);
    Assert.assertTrue(fk.modified().get());

    fkInterfaceTest(rt, i, f, fk);
  }

  @Test
  public void testIndexEnum() {
    Assert.assertTrue(Index.Type.values().length == 4);
    Assert.assertTrue(Index.Type.valueOf("XPK").compareTo(Index.Type.XPK) == 0);
  }

  @Test
  public void testComparing() {
    final Field f = new FieldImpl(TestDatamodel.fieldName);
    final Table t = new TableImpl(TestDatamodel.tableName);
    final Index i = new IndexImpl(TestDatamodel.indexName, Type.XIE, t);
    final ForeignKey fk = new ForeignKeyImpl(TestDatamodel.foreignKeyName, t);

    Assert.assertTrue(f.hashCode() != i.hashCode());
    Assert.assertTrue(t.hashCode() != fk.hashCode());
  }

  @Test
  public void testComparingView() {
    final ViewTable t = new ViewTable(TestDatamodel.tableName);
    final ViewTable rt = new ViewTable(TestDatamodel.tableName2);
    final ViewIndex i = new ViewIndex(new IndexImpl(TestDatamodel.indexName, Type.XAK, t), t);
    final ViewField f = new ViewField(new FieldImpl(TestDatamodel.fieldName, TestDatamodel.fieldDatatype,
        TestDatamodel.fieldDatatypeSize, TestDatamodel.fieldIsNull, "", t), t);
    final ForeignKey fkOriginal = new ForeignKeyImpl(TestDatamodel.foreignKeyName, t);
    fkOriginal.setRefTable(rt);
    fkOriginal.setIndex(i);
    final ViewForeignKey fk = new ViewForeignKey(fkOriginal, t);
    Assert.assertTrue(f.hashCode() != i.hashCode());
    Assert.assertTrue(t.hashCode() != fk.hashCode());
  }

  @Test
  public void testHasFeature() {
    final ViewTable t = new ViewTable(TestDatamodel.tableName);
    final ViewTable rt = new ViewTable(TestDatamodel.tableName2);
    final ViewIndex i = new ViewIndex(new IndexImpl(TestDatamodel.indexName, Type.XIF, t), t);
    final ViewField f = new ViewField(new FieldImpl(TestDatamodel.fieldName, TestDatamodel.fieldDatatype,
        TestDatamodel.fieldDatatypeSize, TestDatamodel.fieldIsNull, "", t), t);
    Assert.assertTrue(!f.hasIndex());
    t.addField(f);
    t.getIndizesRaw().add(i);
    i.addField(f);
    Assert.assertTrue(f.hasIndex());
    Assert.assertTrue(!i.hasFk());
    final ViewForeignKey fk = new ViewForeignKey("R_101", i, t);
    fk.setRefTable(rt);
    fk.setIndex(i);
    t.getForeignKeysRaw().add(fk);
    Assert.assertTrue(i.hasFk());
  }

  @Test
  public void testReplaceField() {
    final Table t = new TableImpl(TestDatamodel.tableName);
    final Field f = new FieldImpl(TestDatamodel.fieldName);
    final Field f2 = new FieldImpl(TestDatamodel.fieldName2);
    final Index i = new IndexImpl(TestDatamodel.indexName, t);
    i.addField(f);
    Assert.assertTrue(i.getFieldList().get(0).equals(f));
    i.replaceField(f2, f.getName());
    Assert.assertTrue(!i.getFieldList().get(0).equals(f));
    Assert.assertTrue(i.getFieldList().get(0).equals(f2));
  }

  @Test
  public void testReplaceViewField() {
    final Table t = new TableImpl(TestDatamodel.tableName);

    final ViewTable vt = new ViewTable(t);
    final ViewIndex vi = new ViewIndex(new IndexImpl(TestDatamodel.indexName, t), vt);
    final ViewField vf = new ViewField(new FieldImpl(TestDatamodel.fieldName), vt);
    final ViewField vf2 = new ViewField(new FieldImpl(TestDatamodel.fieldName2), vt);
    vi.addField(vf);
    Assert.assertTrue(vi.getFieldList().get(0).equals(vf));
    vi.replaceField(vf2, vf.getName());
    Assert.assertTrue(!vi.getFieldList().get(0).equals(vf));
    Assert.assertTrue(vi.getFieldList().get(0).equals(vf2));
  }

  @Test
  public void testComment() {
    final String comment = "Kommentar zur Tabelle";
    final Table t = new TableImpl(TestDatamodel.tableName);
    final ViewTable vt = new ViewTable(t);
    Assert.assertTrue(vt.getComment().equals(""));
    Assert.assertTrue(!vt.isModifiedComment());
    vt.setComment(comment);
    Assert.assertTrue(vt.commentProperty() != null);
    Assert.assertTrue(vt.getComment().equals(comment));
    Assert.assertTrue(vt.isModifiedComment());
  }

  @Test
  public void testDomain() {
    final Domain d = new Domain("String:25");
    Assert.assertTrue(d.getDomainlength() == 25);
    Assert.assertTrue(d.getDomain().equals(DomainId.STRING));
    d.setDomainlength(20);
    Assert.assertTrue(d.getDomainlength() == 20);
    d.setDomain(DomainId.BLOB);
    Assert.assertTrue(d.getDomain().equals(DomainId.BLOB));
    Assert.assertTrue(Domain.getFromName(Domain.getName(DomainId.LOOKUP)).equals(DomainId.LOOKUP));
    Assert.assertTrue(Domain.getFromName(Domain.getName(DomainId.STRING)).equals(DomainId.STRING));
    Assert.assertTrue(Domain.getFromName(Domain.getName(DomainId.AMOUNT)).equals(DomainId.AMOUNT));
    Assert.assertTrue(Domain.getFromName(Domain.getName(DomainId.COUNTER)).equals(DomainId.COUNTER));
    Assert.assertTrue(Domain.getFromName(Domain.getName(DomainId.BOOLEAN)).equals(DomainId.BOOLEAN));
    Assert.assertTrue(Domain.getFromName(Domain.getName(DomainId.DATE)).equals(DomainId.DATE));
    Assert.assertTrue(Domain.getFromName(Domain.getName(DomainId.BLOB)).equals(DomainId.BLOB));
    Assert.assertTrue(Domain.getFromName(Domain.getName(DomainId.CLOB)).equals(DomainId.CLOB));
  }

  @Test
  public void testFieldConnectedFKs() {
    final ViewTable t = new ViewTable(TestDatamodel.tableName);
    final ViewTable rt = new ViewTable(TestDatamodel.tableName2);
    final ViewIndex i = new ViewIndex(new IndexImpl(TestDatamodel.indexName, Type.XAK, t), t);
    t.getIndizesRaw().add(i);
    final ViewIndex i2 = new ViewIndex(new IndexImpl(TestDatamodel.indexName + "2", Type.XAK, t), t);
    t.getIndizesRaw().add(i2);
    final ViewField f = new ViewField(new FieldImpl(TestDatamodel.fieldName, TestDatamodel.fieldDatatype,
        TestDatamodel.fieldDatatypeSize, TestDatamodel.fieldIsNull, "", t), t);
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
    Assert.assertTrue(t.getField(TestDatamodel.fieldName).get().getConnectedFks().size() == 1);
    Assert.assertTrue(t.getField(TestDatamodel.fieldName).get().getConnectedFks().get(0).equals(fk));
    Assert.assertTrue(!t.getField(TestDatamodel.fieldName).get().getConnectedFks().get(0).equals(fk2));
  }
}
