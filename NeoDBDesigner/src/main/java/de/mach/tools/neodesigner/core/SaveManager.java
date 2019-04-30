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
import java.util.logging.Level;
import java.util.logging.Logger;

import de.mach.tools.neodesigner.core.datamodel.Domain.DomainId;
import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Node;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.core.datamodel.impl.FieldImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.ForeignKeyImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.IndexImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.TableImpl;
import de.mach.tools.neodesigner.core.datamodel.viewimpl.ViewField;
import de.mach.tools.neodesigner.core.datamodel.viewimpl.ViewForeignKey;
import de.mach.tools.neodesigner.core.datamodel.viewimpl.ViewIndex;
import de.mach.tools.neodesigner.core.datamodel.viewimpl.ViewNodeImpl;
import de.mach.tools.neodesigner.core.datamodel.viewimpl.ViewTable;


/** Save Manager, welche komplexere Speichervorgänge im Datenmodel regelt.
 *
 * @author cd */
public class SaveManager {
  private final Save save;
  private static final Logger LOG = Logger.getLogger(SaveManager.class.getName());

  public SaveManager(final Save m) {
    save = m;
  }

  /** Ändert den Namen eines Feldes. Passt ggf. die Referenz vom Primärschlüssel in Fremdschlüsseln an.
   *
   * @param tf das Feld
   * @param oldName der "alte Name" des Feldes */
  void changeFieldName(final Field tf, final String oldName) {
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
      final Field field = originTable.get().getField(fieldname);
      if (field != null) {
        f = field;
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
  private void changeFieldDataType(final String fieldname, final DomainId newDataType, final int length,
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
  private void changeFieldIsPartOfPrim(final String nodename, final String tablename, final boolean partOfPrimaryKey) {
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
  private void changeDatafields(final Index tf, final Table table) {
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
  void saveNewForeignKey(final ForeignKey vfk, final Table dmt) {
    final ForeignKey fk = new ForeignKeyImpl(vfk.getName(), dmt);
    final Optional<Table> opRefTab = save.getTable(vfk.getRefTable().getName());
    fk.setIndex(dmt.getIndizies().get(dmt.getIndizies().indexOf(vfk.getIndex())));
    opRefTab.ifPresent(table -> {
      fk.setRefTable(table);
      save.insertNewForeignKey(fk);
    });
  }

  /** Speichert die Felder einer Tabelle.
   *
   * @param t die tabelle
   * @param table Datenmodell Tabelle */
  private void saveData(final ViewTable t, final Table table) {
    for (final ViewField tf : t.getDataFieldsRaw()) {
      if (tf.isNewCreated()) {
        // Speichere neue Felder
        save.insertNewField(new FieldImpl(tf.getName(), tf.getDomain(), tf.getDomainLength(), tf.isRequired(),
                                          tf.getComment(), table));
        if (tf.isModifiedPrim()) {
          changeFieldIsPartOfPrim(tf.getName(), t.getName(), tf.isPartOfPrimaryKey());
        }
        if (tf.isModifiedOrder()) {
          save.changeOrder(tf.getName(), table.getName(), tf.getDisplayOrder());
        }
        tf.saved();
      }
      else {
        saveFieldChanges(t, tf);
        tf.saved();
      }
    }
    // Übertragen der neuen Fremdschlüsselordnung
    if (t.getNewXpkOrder() != null) {
      save.changeXpkOrder(t.getName(), t.getNewXpkOrder());
    }
  }

  /** Speichert Änderungen für ein existierendes Feld.
   *
   * @param t die Tabelle zu dem das Feld gehört
   * @param tf das Feld */
  private void saveFieldChanges(final ViewTable t, final ViewField tf) {
    if (tf.isModifiedName()) {
      changeFieldName(tf, tf.getOldName());
    }
    if (tf.isModifiedReq()) {
      save.changeFieldRequired(tf.getName(), t.getName(), tf.isRequired());
    }
    if (tf.isModifiedPrim()) {
      changeFieldIsPartOfPrim(tf.getName(), t.getName(), tf.isPartOfPrimaryKey());
    }
    if (tf.isModifiedDomain()) {
      changeFieldDataType(tf.getName(), tf.getDomain(), tf.getDomainLength(), t.getName());
    }
    if (tf.isModifiedComment()) {
      save.changeComment(tf.getName(), t.getName(), tf.getComment());
    }
    if (tf.isModifiedOrder()) {
      save.changeOrder(tf.getName(), t.getName(), tf.getDisplayOrder());
    }
  }

  /** Speichert den Fremdschlüssel.
   *
   * @param t die Tabelle
   * @param table Datenmodell Tabelle */
  private void saveForeignKeys(final ViewTable t, final Table table) {
    for (final ViewForeignKey vfk : t.getForeignKeysRaw()) {
      if (vfk.isNewCreated()) {
        saveNewForeignKey(vfk, table);
      }
      else if (vfk.isModifiedName()) {
        save.changeNodeNameFromTable(vfk.getOldName(), t.getName(), vfk.getName(), vfk.getNodeType());
      }

      if (vfk.isModifiedRel()) {
        for (final Field f : vfk.getIndex().getFieldList()) {
          save.changeFkRelations(vfk.getName(), t.getName(), vfk.getRefTable().getName(), vfk.getIndex().getName(),
                                 f.getName(), vfk.getIndex().getOrder(f.getName(), true));
        }
      }
      vfk.saved();
    }
  }


  /** Speichert Indizes.
   *
   * @param t die Tabelle
   * @param table die Datenmodell Tabelle */
  private void saveIndizies(final ViewTable t, final Table table) {
    for (final ViewIndex tf : t.getIndizesRaw()) {
      if (tf.getType() != Index.Type.XPK) {
        if (tf.isNewCreated()) {
          save.insertNewIndex(new IndexImpl(tf.getName(), table));
        }
        else if (tf.isModifiedName()) {
          save.changeIndexUnique(tf.getOldName(), t.getName(), tf.isUnique());
          save.changeNodeNameFromTable(tf.getOldName(), t.getName(), tf.getName(), tf.getNodeType());
        }
        if (tf.isModifiedDatafields()) {
          changeDatafields(tf, table);
        }
        tf.saved();
      }
    }
  }

  /** erstellt eine neue Tabelle.
   *
   * @param newName neuer Name
   * @param newCat neue Kategorie
   * @param newComment neues kommentar
   * @param t die Tabelle
   * @return neuer Name der Tabelle */
  private String createTable(final String newName, final String newCat, final String newComment, final ViewTable t) {
    final TableImpl newTable = new TableImpl(t);
    newTable.setName(newName);
    newTable.setCategory(newCat);
    newTable.setXpk(new IndexImpl(t.getXpk(), newTable));
    newTable.setComment(newComment);
    save.insertNewTable(newTable);
    save.insertNewIndex(newTable.getXpk());
    return newTable.getName();
  }

  /** Speichert eine Tabelle.
   * 
   * @param newName neuer Name
   * @param newCategory Neue Kategorie
   * @param newComment Neues Kommentar
   * @param t die Tabelle
   * @return Boolean Namensänderung ja/nein */
  private boolean saveTable(final String newName, final String newCategory, final String newComment,
                            final ViewTable t) {
    boolean nameChanged = false;
    // Speichere neuenTabellennamen
    if (!t.getOldName().equals(newName)) {
      save.changeTableName(t.getOldName(), newName, t.getXpk().getName());
      t.setName(newName);
      nameChanged = true;
    }
    // Speichere neue Kategorie
    if (!t.getCategory().equals(newCategory)) {
      save.changeTableCategory(t.getName(), newCategory);
    }
    // Speichere neues Kommentar
    if (!t.getComment().equals(newComment)) {
      save.changeComment(t.getName(), newComment);
    }
    return nameChanged;
  }

  public void deleteNode(Node t) {
    save.deleteNode(t);
  }

  private Table getTable(String name) {
    return save.getTable(name).orElse(null);
  }

  public String processTable(final String newName, final String newCategory, final String newComment, ViewTable t) {
    String newGeneratedName = newName;
    if (t.isNewCreated()) { // füge neue Tabelle ein
      newGeneratedName = createTable(newName, newCategory, newComment, t);
    }
    else {
      if (saveTable(newName, newCategory, newComment, t)) {
        newGeneratedName = t.getName();
      }
      for (final ViewNodeImpl<?> tf : t.getNodesToDelete()) {
        save.deleteNode(tf.getNode());
      }
    }
    final Table dbtable = getTable(t.getName());
    if (dbtable != null) {
      saveData(t, dbtable);
      saveIndizies(t, dbtable);
      saveForeignKeys(t, dbtable);
    }
    else {
      LOG.log(Level.WARNING, () -> Strings.LOG_COULDNOTSAVE + t.getName());
    }
    t.saved();
    return newGeneratedName;
  }

}
