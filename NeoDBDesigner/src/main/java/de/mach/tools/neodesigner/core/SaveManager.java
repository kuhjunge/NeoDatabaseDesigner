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
import java.util.List;
import java.util.Optional;

import de.mach.tools.neodesigner.core.datamodel.Domain.DomainId;
import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.core.datamodel.impl.FieldImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.ForeignKeyImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.IndexImpl;


/** Save Manager, welche komplexere Speichervorgänge im Datenmodel regelt.
 *
 * @author cd */
public class SaveManager {
  private final Save save;

  public SaveManager(final Save m) {
    save = m;
  }

  /** Ändert den Namen eines Feldes. Passt ggf. die Referenz vom Primärschlüssel in Fremdschlüsseln an.
   *
   * @param tf das Feld
   * @param oldName der "alte Name" des Feldes */
  public void changeFieldName(final Field tf, final String oldName) {
    save.changeNodeNameFromTable(oldName, tf.getTable().getName(), tf.getName(), tf.getNodeType());
  }

  /** Fügt ein Feld hinzu.
   *
   * @param f Das Feld */
  private void addFieldToPrim(final Field f) {
    for (final ForeignKey fk : f.getTable().getRefForeignKeys()) {
      final Field field = new FieldImpl(f.getName(), f.getDomain(), f.getDomainLength(), true, Strings.EMPTYSTRING,
                                        fk.getTable());
      // Feld hinzufügen
      save.insertNewField(field);
      // Index anpassen
      final Index index = new IndexImpl(fk.getIndex(), fk.getTable());
      index.addField(f);
      changeDatafields(index, fk.getTable());
    }
  }

  /** Löscht ein Feld, welches im Primärschlüssel verwendet wurde, aus Tabellen die darauf referenzieren.
   *
   * @param fieldname Name des Feldes
   * @param tablename Name der Tabelle
   * @param removeThis Entfernt das Feld auch aus der aktuellen (ursprungs) Tabelle */
  private void removeFieldFromPrim(final String fieldname, final String tablename, final boolean removeThis,
                                   final List<String> checkedTables) {
    checkedTables.add(tablename + fieldname);
    final Field field = getField(fieldname, tablename);
    if (field != null) {
      // Feld Teil vom Primärschlüssel?
      if (field.isPartOfPrimaryKey()) {
        executeForAllRefTables(fieldname, checkedTables, field);
      }
      // Ist Feld in anderen FK genutzt (und nicht die originaltabelle)?
      if (!removeThis) {
        executeForAllForeignKeys(fieldname, checkedTables, field);
      }
      // Feld löschen
      if (removeThis) {
        save.deleteNode(field);
      }
    }
  }

  private void executeForAllForeignKeys(final String fieldname, final List<String> checkedTables, final Field field) {
    for (final ForeignKey fk : field.getConnectedFks()) {
      // Funktion aufrufen für anderen PK
      if (checkedTables.contains(fk.getRefTable().getName() + fk.getAltName(fieldname))) {
        removeFieldFromPrim(fk.getAltName(fieldname), fk.getRefTable().getName(), true, checkedTables);
      }
    }
  }

  private void executeForAllRefTables(final String fieldname, final List<String> checkedTables, final Field field) {
    for (final ForeignKey fk : field.getTable().getRefForeignKeys()) {
      // Funktion aufrufen für andern PK
      removeFieldFromPrim(fk.getNameFromAltName(fieldname), fk.getTable().getName(), true, checkedTables);
    }
  }

  /** Gibt ein Feld aus der Datenbank zurück.
   *
   * @param fieldname Name des Feldes
   * @param tablename Name der dazugehörigen Tabelle
   * @return Feld Objekt */
  private Field getField(final String fieldname, final String tablename) {
    Field f = null;
    final Optional<Table> originTable = save.getTable(tablename);
    if (originTable.isPresent()) {
      final Optional<Field> field = originTable.get().getField(fieldname);
      if (field.isPresent()) {
        f = field.get();
      }
    }
    return f;
  }

  /** Ändert die Domain eines Feldes.
   *
   * @param fieldname der Name des Feldes
   * @param newDataType der neue domaintyp
   * @param length die neue domainlänge
   * @param tablename der Tabellenname */
  public void changeFieldDataType(final String fieldname, final DomainId newDataType, final int length,
                                  final String tablename) {
    final Field field = getField(fieldname, tablename);
    if (field != null && !(field.getDomain().equals(newDataType) && field.getDomainLength() == length)) {
      // umbenennen
      save.changeFieldDomain(field.getName(), field.getTable().getName(), newDataType, length);
      // Ist Feld Teil vom Primärschlüssel
      if (field.isPartOfPrimaryKey()) {
        for (final ForeignKey fk : field.getTable().getRefForeignKeys()) {
          // Funktion aufrufen für andern PK
          changeFieldDataType(fk.getNameFromAltName(fieldname), newDataType, length, fk.getTable().getName());
        }
      }
      // Ist Feld in anderen FK genutzt ?
      for (final ForeignKey fk : field.getConnectedFks()) {
        // Funktion aufrufen für anderen PK
        changeFieldDataType(fk.getAltName(fieldname), newDataType, length, fk.getRefTable().getName());
      }
    }
  }

  /** Löscht oder fügt ein Feld zum Primärschlüssel hinzu.
   *
   * @param nodename das Feld
   * @param tablename die Tabelle
   * @param partOfPrimaryKey der Primärschlüssel */
  public void changeFieldIsPartOfPrim(final String nodename, final String tablename, final boolean partOfPrimaryKey) {
    final Field field = getField(nodename, tablename);
    if (field != null) {
      if (!field.isPartOfPrimaryKey() && partOfPrimaryKey) {
        addFieldToPrim(field);
      }
      else if (field.isPartOfPrimaryKey() && !partOfPrimaryKey) {
        removeFieldFromPrim(field.getName(), field.getTable().getName(), false, new ArrayList<>());
      }
      save.changeFieldIsPartOfPrim(tablename, field.getTable().getXpk().getName(), nodename, partOfPrimaryKey,
                                   field.getTable().getXpk().getFieldList().size());
    }
  }

  /** Ändert die Felder im Index.
   *
   * @param tf der Index
   * @param table die Datenmodell Tabelle */
  public void changeDatafields(final Index tf, final Table table) {
    final Index newIndex = new IndexImpl(tf.getName(), table);
    for (final Field f : tf.getFieldList()) {
      newIndex.addField(f);
    }
    save.changeDataFields(newIndex);
  }

  /** fügt einen neuen Fremdschlüssel ein.
   *
   * @param dmt die Tabelle
   * @param vfk der Fremdschlüssel */
  public void saveNewForeignKey(final ForeignKey vfk, final Table dmt) {
    final ForeignKey fk = new ForeignKeyImpl(vfk.getName(), dmt);
    final Optional<Table> opRefTab = save.getTable(vfk.getRefTable().getName());
    fk.setIndex(dmt.getIndizies().get(dmt.getIndizies().indexOf(vfk.getIndex())));
    opRefTab.ifPresent(table -> {
      fk.setRefTable(table);
      save.insertNewForeignKey(fk);
    });
  }
}
