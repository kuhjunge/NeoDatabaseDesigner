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

package de.mach.tools.neodesigner.core.nimport;

import de.mach.tools.neodesigner.core.Strings;
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
import de.mach.tools.neodesigner.database.DatabaseConnection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Konkrete Umsetzung des Import Task für den Import von Kategorien.
 *
 * @author Chris Deter
 *
 */
public class ImportCsvTask extends ImportTask {
  private static final String DELIMITER = ";";
  private final String tbls;
  private final String cols;
  private final String idxs;
  private final String pkeys;
  private final String fkeys;
  private final String meta;

  /**
   * Startet den Import der CSV Daten.
   *
   * @param db
   *          die Datenbank
   * @param tbls
   *          die Tabellen
   * @param cols
   *          die Spalten
   * @param idxs
   *          die Indizes
   * @param pkeys
   *          die Primärschlüssel
   * @param fkeys
   *          die Fremdschlüssel
   * @param meta
   *          die Meta Informationen
   */
  public ImportCsvTask(final DatabaseConnection db, final String tbls, final String cols, final String idxs,
      final String pkeys, final String fkeys, final String meta) {
    super(db);
    this.tbls = tbls;
    this.cols = cols;
    this.idxs = idxs;
    this.pkeys = pkeys;
    this.fkeys = fkeys;
    this.meta = meta;
  }

  /**
   * Läd eine Tabelle aus einer Liste mit Tabellen.
   *
   * @param source
   *          die Liste mit Tabellen
   * @param named
   *          der Tabellenname
   * @return die gesuchte Tabelle (oder Null)
   */
  private static Table getTable(final List<Table> source, final String name, final boolean create) {
    int index = source.indexOf(new TableImpl(name));
    if (index < 0) {
      index = source.indexOf(new TableImpl(name.toUpperCase()));
    }
    if (index >= 0) {
      return source.get(index);
    } else if (create) {
      final Table t = new TableImpl(name);
      source.add(t);
      return t;
    } else {
      return null;
    }
  }

  /**
   * Wählt ein Feld aus einer Liste mit Tabellen aus.
   *
   * @param source
   *          die Liste mit allen Tabellen
   * @param tablename
   *          der Tabellenname
   * @param fieldname
   *          der Feldname
   * @return das gesuchte Feld (oder null)
   */
  private static Field getField(final Table tablename, final String fieldname) {
    final Optional<Field> field = tablename.getField(fieldname);
    return field.isPresent() ? field.get() : null;
  }

  @Override
  protected List<Table> parse() {
    final List<Table> lt = new ArrayList<>();
    // Import Tabellen
    for (final String te : Arrays.asList(tbls.split(Strings.EOL))) {
      lt.add(new TableImpl(te));
    }
    importCols(lt);
    importPk(lt);
    importIndex(lt);
    importFk(lt);
    importMeta(lt);
    return lt;
  }

  private void importMeta(final List<Table> lt) {
    // Import Meta
    for (final String m : Arrays.asList(meta.split(Strings.EOL))) {
      final String[] row = m.split(ImportCsvTask.DELIMITER);
      if ("T".equals(row[0])) {
        final Table t = ImportCsvTask.getTable(lt, row[1].toUpperCase(), false);
        if (t != null) {
          t.setCategory(row[2]);
          t.setComment(row[3].replaceAll(Strings.HTML_BR, Strings.EOL));
          t.setName(row[1]);
        }
      } else if ("R".equals(row[0])) {
        final Table t = ImportCsvTask.getTable(lt, row[1], false);
        final Optional<Field> opField = t.getField(row[2].toUpperCase());
        if (opField.isPresent()) {
          final Field f = opField.get();
          f.setName(row[2]);
          f.setComment(row[3].replaceAll(Strings.HTML_BR, Strings.EOL));
          for (final ForeignKey fk : t.getRefForeignKeys()) {
            fk.getIndex().setAltName(fk.getIndex().getNameFromAltName(row[2].toUpperCase()), row[2]);
          }
        }
      }
    }
  }

  private void importCols(final List<Table> lt) {
    // Import Cols
    final List<String> items = Arrays.asList(cols.split(Strings.EOL));
    for (final String item : items) {
      final String[] row = item.split(ImportCsvTask.DELIMITER);
      final Table t = ImportCsvTask.getTable(lt, row[0], true);
      final Domain d = UtilImport.csvTypeToDomain(row[2], row[3], row[4]);
      t.addField(new FieldImpl(row[1], d.getDomain(), d.getDomainlength(), row[5].trim().equals(Strings.N),
          Strings.EMPTYSTRING, t));
    }
  }

  private void importPk(final List<Table> lt) {
    // Import Primärschlüssel
    for (final String pk : Arrays.asList(pkeys.split(Strings.EOL))) {
      final String[] row = pk.split(ImportCsvTask.DELIMITER);
      final Table t = ImportCsvTask.getTable(lt, row[0], true);
      if (t.getXpk() == null) {
        t.setXpk(new IndexImpl(row[1], Index.Type.XPK, t));
      }
      t.getXpk().addField(ImportCsvTask.getField(t, row[2]));
      t.getXpk().setOrder(row[2], Integer.parseInt(row[3]));
    }
  }

  private void importIndex(final List<Table> lt) {
    // Import Indizes
    for (final String index : Arrays.asList(idxs.split(Strings.EOL))) {
      final String[] row = index.split(ImportCsvTask.DELIMITER);
      final Table t = ImportCsvTask.getTable(lt, row[0], true);
      Index actual;
      if (!t.getIndizies().contains(new IndexImpl(row[1], t))) {
        actual = new IndexImpl(row[1], t);
        t.getIndizies().add(actual);
      } else {
        final int i = t.getIndizies().indexOf(new IndexImpl(row[1], t));
        actual = t.getIndizies().get(i);
      }
      actual.addField(ImportCsvTask.getField(t, row[2]));
      actual.setOrder(row[2], Integer.parseInt(row[3]));
    }
  }

  private void importFk(final List<Table> lt) {
    // Import Fremdschlüssel (Part I)
    ForeignKey actual = null;
    final List<Field> tempFields = new ArrayList<>();
    for (final String fk : Arrays.asList(fkeys.split(Strings.EOL))) {
      final String[] row = fk.split(ImportCsvTask.DELIMITER);
      final Table t = ImportCsvTask.getTable(lt, row[0], true);
      if (actual != null && !actual.getName().equals(row[2])) {
        UtilImport.setFieldForeignkeyRelation(actual, tempFields);
        tempFields.clear();
      }
      if (!t.getForeignKeys().contains(new ForeignKeyImpl(row[2], t))) {
        actual = new ForeignKeyImpl(row[2], t);
        t.getForeignKeys().add(actual);
        actual.setRefTable(ImportCsvTask.getTable(lt, row[1], true));
        actual.getRefTable().getRefForeignKeys().add(actual);
      }
      tempFields.add(t.getField(row[3]).get());
    }
    // letztes Element auch noch eintragen
    UtilImport.setFieldForeignkeyRelation(actual, tempFields);

    // Import Fremdschlüssel (Part II)
    for (final String fk : Arrays.asList(fkeys.split(Strings.EOL))) {
      final String[] row = fk.split(ImportCsvTask.DELIMITER);
      final Table t = ImportCsvTask.getTable(lt, row[0], true);
      final int i = t.getForeignKeys().indexOf(new ForeignKeyImpl(row[2], t));
      actual = t.getForeignKeys().get(i);
      for (final Field f : actual.getIndex().getFieldList()) {
        if (f.getDomain().equals(DomainId.STRING) && f.getDomainLength() == 20) {
          f.setDomain(DomainId.LOOKUP);
        }
      }
      actual.getIndex().setAltName(row[3], UtilImport.getFieldFromRefField(actual.getRefTable().getXpk().getFieldList(),
          ImportCsvTask.getField(actual.getTable(), row[3]), Integer.parseInt(row[4]) - 1).getName());
    }
  }
}
