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
  public static final String foreignKeyName = "R_1704";
  public static final DomainId fieldDatatype = DomainId.STRING;
  public static final DomainId fieldDatatype2 = DomainId.AMOUNT;
  public static final int fieldDatatypeSize = 20;
  public static final int fieldDatatypeSize2 = 0;
  public static final Boolean fieldIsNull = true;
  public static final String xpkName2 = "XPKTesttabelle2";

  private void fieldInterfaceTest(final Table t, final Field f) {
    assertTrue(TestDatamodel.fieldName.equals(f.getName()));
    assertTrue(f.getTable().equals(t));
    assertTrue(f.getDomain().equals(TestDatamodel.fieldDatatype));
    assertTrue(f.isRequired() == TestDatamodel.fieldIsNull);

    f.setName(TestDatamodel.fieldName2);
    f.setPartOfPrimaryKey(true);
    f.setRequired(!TestDatamodel.fieldIsNull);
    f.setDomain(TestDatamodel.fieldDatatype2);

    assertTrue(TestDatamodel.fieldName2.equals(f.getName()));
    assertTrue(f.getDomain().equals(TestDatamodel.fieldDatatype2));
    assertTrue(f.isRequired() == true);
    assertTrue(f.isPartOfPrimaryKey() == true);
    f.setDomainLength(20);
    assertTrue(f.getDomainLength() == 0);
    f.setDomain(TestDatamodel.fieldDatatype);
    f.setDomainLength(20);
    assertTrue(f.getDomainLength() == 20);
  }

  private void fkInterfaceTest(final Table rt, final Index i, final Field f, final ForeignKey fk) {
    fk.setRefTable(rt);
    fk.setIndex(i);
    // Füge Feld hinzu und Prüfe ob das Feld hinzugefügt wurde
    fk.getIndex().addField(f);
    fk.getTable().getXpk().addField(f);
    assertTrue(fk.getRefTable().equals(rt));
    assertTrue(fk.getIndex().equals(i));
    assertTrue(fk.getIndex().getOrder(f.getName(), false).equals(TestDatamodel.order));
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

  private void indexInterfaceTest(final Table t, final Index i, final Field f, final Field f2) {
    i.addField(f2);
    // i.setAltName(f2.getName(), TestDatamodel.fieldName2);
    // assertTrue(i.getAltName(f2.getName()).equals(TestDatamodel.fieldName2));
    // assertTrue(i.getNameFromAltName(TestDatamodel.fieldName2).endsWith(f2.getName()));
    assertTrue(i.getOrder(f2.getName(), false).equals(TestDatamodel.order));
    i.addField(f);
    assertTrue(i.getOrder(f.getName(), false).equals(TestDatamodel.order2));
    i.removeField(0);
    assertTrue(i.getOrder(f.getName(), false).equals(TestDatamodel.order));

    assertTrue(TestDatamodel.indexName.equals(i.getName()));
    assertTrue(i.getTable().equals(t));
    assertTrue(i.getType() == Type.XAK);
    assertTrue(i.isUnique() == true);
    assertTrue(i.getFieldList().contains(f));

    i.setUnique(false);
    assertTrue(i.isUnique() == false);
    assertTrue(i.getType() == Type.XIE);
    assertTrue(i.getName().equals(TestDatamodel.indexName2));

    i.setUnique(true);
    assertTrue(i.isUnique() == true);
    assertTrue(i.getType() == Type.XAK);
    assertTrue(i.getName().equals(TestDatamodel.indexName));

    i.setType(Type.XIF);
    assertTrue(i.getType() == Type.XIF);
    assertTrue(i.isUnique() == false);

    i.setType(Type.XAK);
    assertTrue(i.getType() == Type.XAK);
    assertTrue(i.isUnique() == true);

    i.setType(Type.XIE);
    assertTrue(i.getType() == Type.XIE);
    assertTrue(i.isUnique() == false);

    final Type p = i.getType();
    i.setName(TestDatamodel.indexNameShort);
    i.setUnique(true);
    assertTrue(TestDatamodel.indexNameShort.equals(i.getName()));
    assertTrue(i.getType() == p);
    assertTrue(i.isUnique() == false);

    i.setName(TestDatamodel.indexName2);
    assertTrue(TestDatamodel.indexName2.equals(i.getName()));
    assertTrue(i.getType() == Type.XIE);
    assertTrue(i.isUnique() == false);

    i.setName(TestDatamodel.indexName3);
    assertTrue(TestDatamodel.indexName3.equals(i.getName()));
    assertTrue(i.getType() == Type.XIF);

    assertTrue(i.getOrder(TestDatamodel.fieldName, false) == TestDatamodel.order);
    i.setOrder(TestDatamodel.fieldName, TestDatamodel.order + 1, false);
    assertTrue(i.getOrder(TestDatamodel.fieldName, false) == TestDatamodel.order + 1);
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

    assertTrue(t != null);
    assertTrue(t.getFields().size() == 1);
    assertTrue(t.hashCode() != 0);
    assertTrue(TestDatamodel.tableName.equals(t.getName()));
    assertTrue(TestDatamodel.kategory.equals(t.getCategory()));
    assertTrue(t.getTable() == null);
    assertTrue(t.getFields().contains(f));
    assertTrue(t.getXpk().getName().equals(xpk.getName()));
    assertTrue(t.getIndizies().contains(i));
    assertTrue(!t.getIndizies().contains(i2));
    assertTrue(t.getForeignKeys().contains(fk));

    t.setName(TestDatamodel.tableName2);
    assertTrue(TestDatamodel.tableName2.equals(t.getName()));
    t.addField(new FieldImpl(fieldnameneu));
    assertTrue(t.getField(fieldnameneu).isPresent());
    t.deleteField(fieldnameneu);
    assertTrue(!t.getField(fieldnameneu).isPresent());
  }

  @Test
  public void testComment() {
    final String comment = "Kommentar zur Tabelle";
    final Table t = new TableImpl(TestDatamodel.tableName);
    final ViewTable vt = new ViewTable(t, true);
    assertTrue(vt.getComment().equals(""));
    assertTrue(!vt.isModifiedComment());
    vt.setComment(comment);
    assertTrue(vt.commentProperty() != null);
    assertTrue(vt.getComment().equals(comment));
    assertTrue(vt.isModifiedComment());
  }

  @Test
  public void testComparing() {
    final Field f = new FieldImpl(TestDatamodel.fieldName);
    final Table t = new TableImpl(TestDatamodel.tableName);
    final Index i = new IndexImpl(TestDatamodel.indexName, Type.XIE, t);
    final ForeignKey fk = new ForeignKeyImpl(TestDatamodel.foreignKeyName, t);

    assertTrue(f.hashCode() != i.hashCode());
    assertTrue(t.hashCode() != fk.hashCode());
  }

  @Test
  public void testComparingView() {
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
  public void testDomain() {
    final Domain d = new Domain("String:25");
    assertTrue(d.getDomainlength() == 25);
    assertTrue(d.getDomain().equals(DomainId.STRING));
    d.setDomainlength(20);
    assertTrue(d.getDomainlength() == 20);
    d.setDomain(DomainId.BLOB);
    assertTrue(d.getDomain().equals(DomainId.BLOB));
    assertTrue(Domain.getFromName(Domain.getName(DomainId.LOOKUP)).equals(DomainId.LOOKUP));
    assertTrue(Domain.getFromName(Domain.getName(DomainId.STRING)).equals(DomainId.STRING));
    assertTrue(Domain.getFromName(Domain.getName(DomainId.AMOUNT)).equals(DomainId.AMOUNT));
    assertTrue(Domain.getFromName(Domain.getName(DomainId.COUNTER)).equals(DomainId.COUNTER));
    assertTrue(Domain.getFromName(Domain.getName(DomainId.BOOLEAN)).equals(DomainId.BOOLEAN));
    assertTrue(Domain.getFromName(Domain.getName(DomainId.DATE)).equals(DomainId.DATE));
    assertTrue(Domain.getFromName(Domain.getName(DomainId.BLOB)).equals(DomainId.BLOB));
    assertTrue(Domain.getFromName(Domain.getName(DomainId.CLOB)).equals(DomainId.CLOB));
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
    assertTrue(t.getField(TestDatamodel.fieldName).get().getConnectedFks().size() == 1);
    assertTrue(t.getField(TestDatamodel.fieldName).get().getConnectedFks().get(0).equals(fk));
    assertTrue(!t.getField(TestDatamodel.fieldName).get().getConnectedFks().get(0).equals(fk2));
  }

  @Test
  public void testFieldImpl() {
    final Table t = new TableImpl(TestDatamodel.tableName);
    final Field f = new FieldImpl(TestDatamodel.fieldName, TestDatamodel.fieldDatatype, TestDatamodel.fieldDatatypeSize,
                                  TestDatamodel.fieldIsNull, "", t);

    fieldInterfaceTest(t, f);
  }

  @Test
  public void testFieldWrapper() {
    final Field f = new FieldImpl(TestDatamodel.fieldName);
    final FieldWrapper fw = new FieldWrapper(f, 1);
    final FieldWrapper fwComp = new FieldWrapper(f, 1);
    final FieldWrapper fwComp2 = new FieldWrapper(f, 2);
    assertTrue(fw.getOrder(false) == 1);
    assertTrue(fw.toString().equals(TestDatamodel.fieldName + " 1"));
    assertTrue(fw.equals(fwComp));
    assertTrue(!fw.equals(fwComp2));
    assertTrue(fw.hashCode() == -959971302);
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
    assertTrue(fk.getIndex().getFieldList().isEmpty());

    fkInterfaceTest(rt, i, f, fk);
  }

  @Test
  public void testHasFeature() {
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
    final ViewForeignKey fk = new ViewForeignKey("R_101", i, t);
    fk.setRefTable(rt);
    fk.setIndex(i);
    t.getForeignKeysRaw().add(fk);
    assertTrue(i.hasFk());
  }

  @Test
  public void testIndexEnum() {
    assertTrue(Index.Type.values().length == 4);
    assertTrue(Index.Type.valueOf("XPK").compareTo(Index.Type.XPK) == 0);
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

    final Index ix = new IndexImpl(i, t);
    indexInterfaceTest(t, ix, f, f2);
  }

  @Test
  public void testRelations() {
    assertTrue(Relations.Type.values().length == 5);
    assertTrue(Relations.getType(Relations.Type.DEFAULT).contains(""));
    assertTrue(Relations.getType(Relations.Type.DATA).contains("has_column"));
    assertTrue(Relations.getType(Relations.Type.INDEX).contains("indexed_by"));
    assertTrue(Relations.getType(Relations.Type.REFERENCE).contains("references"));
    assertTrue(Relations.getType(Relations.Type.XPK).contains("identified_by"));
  }

  @Test
  public void testReplaceField() {
    final Table t = new TableImpl(TestDatamodel.tableName);
    final Field f = new FieldImpl(TestDatamodel.fieldName);
    final Field f2 = new FieldImpl(TestDatamodel.fieldName2);
    final Index i = new IndexImpl(TestDatamodel.indexName, t);
    i.addField(f);
    assertTrue(i.getFieldList().get(0).equals(f));
    i.replaceField(f2, f.getName());
    assertTrue(!i.getFieldList().get(0).equals(f));
    assertTrue(i.getFieldList().get(0).equals(f2));
  }

  @Test
  public void testReplaceViewField() {
    final Table t = new TableImpl(TestDatamodel.tableName);

    final ViewTable vt = new ViewTable(t, true);
    final ViewIndex vi = new ViewIndex(new IndexImpl(TestDatamodel.indexName, t), vt);
    final ViewField vf = new ViewField(new FieldImpl(TestDatamodel.fieldName), vt);
    final ViewField vf2 = new ViewField(new FieldImpl(TestDatamodel.fieldName2), vt);
    vi.addField(vf);
    assertTrue(vi.getFieldList().get(0).equals(vf));
    vi.replaceField(vf2, vf.getName());
    assertTrue(!vi.getFieldList().get(0).equals(vf));
    assertTrue(vi.getFieldList().get(0).equals(vf2));
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
  public void testViewField() {
    final ViewTable t = new ViewTable(TestDatamodel.tableName);
    ViewField f = new ViewField(new FieldImpl(TestDatamodel.fieldName, TestDatamodel.fieldDatatype,
                                              TestDatamodel.fieldDatatypeSize, TestDatamodel.fieldIsNull, "", t),
                                t);
    assertTrue(f.isModifiedName() == false);
    assertTrue(f.isModifiedPrim() == false);
    assertTrue(f.isModifiedReq() == false);
    assertTrue(f.isModifiedDomain() == false);
    fieldInterfaceTest(t, f);
    assertTrue(f.nameProperty() != null);
    assertTrue(f.primProperty() != null);
    assertTrue(f.requiredProperty() != null);
    assertTrue(f.typeOfDataProperty() != null);
    assertTrue(f.isNewCreated() == false);
    assertTrue(f.isModifiedName() == true);
    assertTrue(f.isModifiedPrim() == true);
    assertTrue(f.isModifiedReq() == false);
    assertTrue(f.isModifiedDomain() == true);
    assertTrue(f.getOldName().equals(TestDatamodel.fieldName));
    f.saved();
    assertTrue(f.isModifiedName() == false);
    assertTrue(f.isModifiedPrim() == false);
    assertTrue(f.isModifiedReq() == false);
    assertTrue(f.isModifiedDomain() == false);
    assertTrue(f.getOldName().equals(f.getName()));

    f = new ViewField(TestDatamodel.fieldName, TestDatamodel.fieldDatatype, TestDatamodel.fieldDatatypeSize,
                      TestDatamodel.fieldIsNull, t, "");
    f.setAsNewCreated();
    fieldInterfaceTest(t, f);
    f.setTypeOfData("DaTe");
    assertTrue(f.getTypeOfData().equals(Domain.getName(DomainId.DATE)));
    // Entferne Prim, setzte nullable und dann teste ob boolean wieder auf required
    // zurücksetzt
    f.setPartOfPrimaryKey(false);
    f.setRequired(false);
    assertTrue(f.isRequired() == false);
    f.setDomain(DomainId.BOOLEAN);
    assertTrue(f.isRequired() == true);
  }

  @Test
  public void testViewForeignKey() {
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
    assertTrue(fk.fieldListStringProperty() != null);
    assertTrue(fk.getRefTable().equals(rt));
    assertTrue(fk.getIndex().equals(i));

    fk = new ViewForeignKey(TestDatamodel.foreignKeyName, i, t);
    assertTrue(!fk.isModifiedRel());
    fk.setRefTable(rt);
    fk.setIndex(i);
    assertTrue(fk.getVRefTable().equals(rt));
    assertTrue(fk.getVIndex().equals(i));
    assertTrue(fk.isModifiedRel());
    fk.saved();
    assertTrue(!fk.isModifiedRel());
    fk.setModifiedRel();
    assertTrue(fk.isModifiedRel());
    fk.getIndex().clearFieldList();
    assertTrue(fk.getRefTableName() != null);
    assertTrue(fk.modified().get());

    fkInterfaceTest(rt, i, f, fk);
  }

  @Test
  public void testViewIndex() {
    final ViewTable t = new ViewTable(TestDatamodel.tableName);
    final ViewIndex i = new ViewIndex(new IndexImpl(TestDatamodel.indexName, Type.XAK, t), t);
    final ViewField f = new ViewField(new FieldImpl(TestDatamodel.fieldName, TestDatamodel.fieldDatatype,
                                                    TestDatamodel.fieldDatatypeSize, TestDatamodel.fieldIsNull, "", t),
                                      t);

    assertTrue(!i.isModifiedDatafields());
    assertTrue(!i.isModifiedName());
    assertTrue(!i.isModifiedUnique());
    assertTrue(!i.isNewCreated());

    final Field f2 = new FieldImpl(TestDatamodel.fieldName2, TestDatamodel.fieldDatatype,
                                   TestDatamodel.fieldDatatypeSize, TestDatamodel.fieldIsNull, "", t);
    indexInterfaceTest(t, i, f, f2);
    assertTrue(i.isModifiedDatafields());
    assertTrue(i.isModifiedName());
    assertTrue(i.isModifiedUnique());
    i.saved();
    assertTrue(!i.isModifiedDatafields());
    assertTrue(!i.isModifiedName());
    assertTrue(!i.isModifiedUnique());
    assertTrue(i.fieldListStringProperty() != null);
    assertTrue(i.uniqueProperty() != null);
    i.setName(TestDatamodel.indexName2);
    i.setName(TestDatamodel.indexName);
    assertTrue(i.isModifiedName());
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
    assertTrue(i.getFieldList().size() == 2);
    assertTrue(!i.hasFk());
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

    assertTrue(!t.equals(null));
    assertTrue(t.hashCode() != 0);
    assertTrue(TestDatamodel.tableName.equals(t.getName()));
    assertTrue(TestDatamodel.kategory.equals(t.getCategory()));
    assertTrue(t.getTable() == null);
    assertTrue(t.getFields().contains(f));
    assertTrue(t.getDataFieldsRaw().contains(f));
    assertTrue(t.getXpk().getName().equals(xpk.getName()));

    t.setName(TestDatamodel.tableName2);
    assertTrue(TestDatamodel.tableName2.equals(t.getName()));
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
    final ViewTable tt = new ViewTable(t, true);
    tableInterfaceTest(tt, f, i, i2, xpk, fk);
  }

  @Test
  public void testViewTable3() {
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
    final ViewTable tt = new ViewTable(t, true);
    assertTrue(tt.getForeignKeyNumber(100) == 1704);
    assertTrue(tt.getForeignKeyNumber(1805) == 1805);
    assertTrue(tt.getForeignKeyNumber(1703) == 1704);
    assertTrue(tt.getForeignKeyNumber(1704) == 1704);
    assertTrue(tt.getForeignKeyNumber(1705) == 1705);
    tt.getForeignKeysRaw().get(0).setName("R_neuesNamesNsystem");
    assertTrue(tt.getForeignKeyNumber(101) == 101);
  }
}
