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

package de.mach.tools.neodesigner.inex.nimport;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import de.mach.tools.neodesigner.core.Strings;
import de.mach.tools.neodesigner.core.Util;
import de.mach.tools.neodesigner.core.datamodel.Domain;
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


/** Konkrete Umsetzung des Import Task für den Import von Kategorien.
 *
 * @author Chris Deter */
public class ImportCsvTask extends ImportTask {
  private static final String DELIMITER = ";";

  /** Wählt ein Feld aus einer Liste mit Tabellen aus.
   *
   * @param table der Tabellenname
   * @param fieldname der Feldname
   * @return das gesuchte Feld (oder null) */
  private static Field getField(final Table table, final String fieldname) {
    return table.getField(fieldname);
  }

  private final String tbls;
  private final String cols;
  private final String idxs;
  private final String pkeys;
  private final String fkeys;
  private final String metaTbls;
  private final String metaCols;

  private final Map<String, Table> dbr = new HashMap<>();

  /** Startet den Import der CSV Daten.
   *
   * @param tbls die Tabellen
   * @param cols die Spalten
   * @param idxs die Indizes
   * @param pkeys die Primärschlüssel
   * @param fkeys die Fremdschlüssel */
  public ImportCsvTask(final String tbls, final String cols, final String idxs, final String pkeys, final String fkeys,
                       final String metaTbls, final String metaCols) {
    super();
    this.tbls = tbls;
    this.cols = cols;
    this.idxs = idxs;
    this.pkeys = pkeys;
    this.fkeys = fkeys;
    this.metaTbls = metaTbls;
    this.metaCols = metaCols;
  }

  private Table getTable(final String name) {
    return dbr.get(name.toUpperCase().trim());
  }

  private void importCols() {
    // Import Cols
    final String[] items = cols.split(Strings.EOL);
    for (final String item : items) {
      final String[] row = item.split(ImportCsvTask.DELIMITER);
      final Table t = getTable(row[0]);
      final Domain d = Domain.csvTypeToDomain(row[2], row[3], row[4]);
      t.addField(new FieldImpl(row[1], d.getDomain(), d.getDomainlength(), row[5].trim().equals(Strings.N),
                               Strings.EMPTYSTRING, t));
    }
  }

  private void importFk() {
    // Import Fremdschlüssel (Part I)
    ForeignKey actual = null;
    final List<Field> tempFields = new ArrayList<>();
    for (final String fk : fkeys.split(Strings.EOL)) {
      final String[] row = fk.split(ImportCsvTask.DELIMITER);
      final Table t = getTable(row[0]);
      if (actual != null && !actual.getName().equals(row[2])) {
        UtilImport.setFieldForeignkeyRelation(actual, tempFields);
        tempFields.clear();
      }
      if (!t.getForeignKeys().contains(new ForeignKeyImpl(row[2], t))) {
        actual = new ForeignKeyImpl(row[2], t);
        t.addForeignKey(actual, false);
        actual.setRefTable(getTable(row[1]));
        actual.getRefTable().addForeignKey(actual, true);
      }
      final Field nf = t.getField(row[3]);
      if (nf != null) {
        tempFields.add(nf);
      }
      else {
        ImportTask.LOG.log(Level.SEVERE, "Could not get Optional Value - should not happen! Data inconsistend");
      }
    }
    // letztes Element auch noch eintragen
    if (actual != null) {
      UtilImport.setFieldForeignkeyRelation(actual, tempFields);
    }
    else {
      ImportTask.LOG.log(Level.SEVERE, "Could not link Foreignkey - should not happen! Data inconsistend");
    }


    // Import Fremdschlüssel (Part II)
    for (final String fk : fkeys.split(Strings.EOL)) {
      final String[] row = fk.split(ImportCsvTask.DELIMITER);
      final Table t = getTable(row[0]);
      final int i = t.getForeignKeys().indexOf(new ForeignKeyImpl(row[2], t));
      actual = t.getForeignKeys().get(i);
      // Reihenfolge könnte hier auf Fremdschlüssel Reihenfolge gesetzt werden, das
      // würde aber auch die Indexreihenfolge beeinflussen
      actual.setFkOrder(actual.getTable().getField(row[3]), Util.tryParseInt(row[4]));
      for (final Field f : actual.getIndex().getFieldList()) {
        if (f.getDomain().equals(DomainId.STRING) && f.getDomainLength() == 20) {
          f.setDomain(DomainId.LOOKUP);
        }
      }
    }
  }

  private void importIndex() {
    // Import Indizes
    for (final String index : idxs.split(Strings.EOL)) {
      final String[] row = index.split(ImportCsvTask.DELIMITER);
      final Table t = getTable(row[0]);
      Index actual;
      if (!t.getIndizies().contains(new IndexImpl(row[1], t))) {
        actual = new IndexImpl(row[1], t);
        t.getIndizies().add(actual);
      }
      else {
        final int i = t.getIndizies().indexOf(new IndexImpl(row[1], t));
        actual = t.getIndizies().get(i);
      }
      actual.addField(ImportCsvTask.getField(t, row[2]));
      actual.setOrder(row[2], Integer.parseInt(row[3]), false);
    }
  }

  private void importMetaCol(final boolean allInOne, final String[] row) {
    if ("R".equals(row[0]) || !allInOne) {
      final int index = allInOne ? 1 : 0;
      final Table t = getTable(row[index]);
      final Field nf = t.getField(row[index + 1].toUpperCase());
      if (nf != null) {
        nf.setName(row[index + 1]);
        // Optionales Feld
        if (row.length > index + 2) {
          nf.setComment(row[index + 2].replaceAll(Strings.HTML_BR, Strings.EOL));
        }
        // Optionales Feld
        if (row.length > index + 3) {
          nf.setDomain(Domain.getFromName(row[index + 3]));
        }
      }
    }
  }

  private void importMetaCols(final String meta, final boolean allInOne) {
    // Import Meta
    for (final String m : meta.split(Strings.EOL)) {
      final String[] row = m.split(ImportCsvTask.DELIMITER);
      importMetaCol(allInOne, row);
    }
  }

  private void importMetaTbl(final String[] row, final boolean allInOne) {
    if ("T".equals(row[0]) || !allInOne) {
      final int index = allInOne ? 1 : 0;
      final Table t = getTable(row[index]);
      if (t != null) {
        t.setName(row[index]);
        t.setCategory(row[index + 1]);
        if (row.length > index + 2) {
          t.setComment(row[index + 2].replaceAll(Strings.HTML_BR, Strings.EOL));
        }
      }
    }
  }

  private void importMetaTbls(final String meta, final boolean allInOne) {
    // Import Meta
    for (final String m : meta.split(Strings.EOL)) {
      final String[] row = m.split(ImportCsvTask.DELIMITER);
      importMetaTbl(row, allInOne);
    }
  }

  private void importPk() {
    // Import Primärschlüssel
    for (final String pk : pkeys.split(Strings.EOL)) {
      final String[] row = pk.split(ImportCsvTask.DELIMITER);
      final Table t = getTable(row[0]);
      if (t.getXpk() == null) {
        t.setXpk(new IndexImpl(row[1], Index.Type.XPK, t));
      }
      t.getXpk().setName(row[1]);
      t.getXpk().addField(ImportCsvTask.getField(t, row[2]));
      t.getXpk().setOrder(row[2], Integer.parseInt(row[3]), false);
    }
  }

  @Override
  protected List<Table> parse() {
    // Import Tabellen
    for (final String te : tbls.split(Strings.EOL)) {
      dbr.put(te.toUpperCase().trim(), new TableImpl(te));
    }
    importCols();
    importPk();
    importIndex();
    importFk();
    final boolean allInOne = metaTbls.equals(metaCols);
    importMetaTbls(metaTbls, allInOne);
    importMetaCols(metaCols, allInOne);
    final List<Table> l = new ArrayList<>(dbr.values());
    Collections.sort(l);
    return l;
  }
}
