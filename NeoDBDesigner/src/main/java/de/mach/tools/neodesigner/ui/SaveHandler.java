package de.mach.tools.neodesigner.ui;

import de.mach.tools.neodesigner.core.Save;

import de.mach.tools.neodesigner.core.Validator;
import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Index;
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
import de.mach.tools.neodesigner.ui.Strings;
import de.mach.tools.neodesigner.ui.controller.DbTableTabController;
import de.mach.tools.neodesigner.ui.controller.NeoDbDesignerController;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

/**
 * Bekommt ein ViewObjekt von der GUI und startet die Verarbeitung der
 * Änderungen im Model.
 *
 * @author Chris Deter
 *
 */
public class SaveHandler {

  private final Save save;

  /**
   * Konstruktor
   *
   * @param dm
   */
  public SaveHandler(final Save dm) {
    save = dm;
  }

  /**
   * Wird aufgerufen wenn eine Tabelle gelöscht werden soll.
   *
   * @param t
   *          Die zu löschende Tabelle
   * @param dbtv
   *          Der TableTab Controller
   */
  public void deleteHandlerForTab(final ViewTable t, final DbTableTabController dbtv) {
    final Alert alert = new Alert(AlertType.CONFIRMATION);
    alert.setTitle(Strings.ALTITLE_DELTABLE);
    alert.setHeaderText(Strings.ALTEXT_DELTABLE + t.getName());

    final Optional<ButtonType> result = alert.showAndWait();
    if (result.isPresent() && result.get() == ButtonType.OK && !t.isNewCreated()) {
      save.deleteNode(t);
      dbtv.removeTab();
    }
  }

  /**
   * Wird aufgerufen um eine Tabelle zu speichern.
   *
   * @param dbtv
   *          Der TableTab Controller
   * @param wordLength
   *          die maximale Wortlänge die tabellen, Spalten, Indizes und FKs
   *          haben dürfen
   */
  public void saveHandlerForTab(final DbTableTabController dbtv, final int wordLength) {
    final ViewTable t = dbtv.getTable();
    final Validator validator = new Validator(wordLength, wordLength, save);

    final int maxXpkLenght = wordLength - Strings.RELNAME_XPK.length() - 1;
    final String xpkName = Strings.RELNAME_XPK + dbtv.getNewName().substring(0,
        dbtv.getNewName().length() >= maxXpkLenght ? maxXpkLenght : dbtv.getNewName().length());
    t.getXpk().setName(xpkName);

    if (validator.validateTable(t, t.isNewCreated()) && validator.validateName(dbtv.getNewName())) {
      if (t.isNewCreated()) { // füge neue Tabelle ein
        createTable(dbtv, t);
      } else {
        saveTable(dbtv, t);
        for (final ViewNodeImpl tf : t.delete) {
          save.deleteNode(tf.getNode());
        }
      }
      saveData(t);
      saveIndizies(t);
      saveForeignKeys(t);

      t.saved();
    } else {
      NeoDbDesignerController.popupError(Strings.ALTITLE_SAVEERR, Strings.ALTEXT_SAVEERR, validator.getLastError());
    }
  }

  /**
   * erstellt eine neue Tabelle
   *
   * @param dbtv
   * @param t
   */
  private void createTable(final DbTableTabController dbtv, final ViewTable t) {
    final TableImpl newTable = new TableImpl(dbtv.getNewName());
    t.setName(dbtv.getNewName());
    newTable.setCategory(dbtv.getNewKategory());
    newTable.setXpk(t.getXpk());
    save.insertNewTable(newTable);
    save.insertNewIndex(newTable.getXpk());
    dbtv.getTab().setText(t.getName());
  }

  /**
   * Speichert eine Tabelle
   *
   * @param dbtv
   * @param t
   */
  private void saveTable(final DbTableTabController dbtv, final ViewTable t) {
    // Speichere neuenTabellennamen
    if (t.getName().compareTo(dbtv.getNewName()) != 0) {
      save.changeTableName(t.getName(), dbtv.getNewName(), t.getXpk().getName());
      t.setName(dbtv.getNewName());
      dbtv.getTab().setId(t.getName());
      dbtv.getTab().setText(t.getName());
    }
    // Speichere neue Kategorie
    if (t.getCategory().compareTo(dbtv.getNewKategory()) != 0) {
      save.changeTableCategory(t.getName(), dbtv.getNewKategory());
    }
  }

  /**
   * Speichert die Felder einer Tabelle
   *
   * @param t
   */
  private void saveData(final ViewTable t) {
    for (final ViewField tf : t.dataFields) {
      if (tf.isNewCreated()) {
        // Speichere neue Felder
        save.insertNewField(
            new FieldImpl(tf.getName(), tf.getTypeOfData(), tf.isRequired(), save.getTable(t.getName())));
        if (tf.isModifiedPrim()) {
          save.changeFieldIsPartOfPrim(tf.getName(), t.getName(), tf.isPartOfPrimaryKey());
        }
        tf.saved();
      }
      if (tf.isModifiedName()) {
        save.changeNodeNameFromTable(tf.getOldName(), t.getName(), tf.getName());
      }
      if (tf.isModifiedType()) {
        save.changeFieldTypeOfData(tf.getName(), t.getName(), tf.getTypeOfData());
      }
      if (tf.isModifiedReq()) {
        save.changeFieldRequired(tf.getName(), t.getName(), tf.isRequired());
      }
      if (tf.isModifiedPrim()) {
        save.changeFieldIsPartOfPrim(tf.getName(), t.getName(), tf.isPartOfPrimaryKey());
      }
      tf.saved();
    }
  }

  /**
   * Speichert Indizes.
   *
   * @param t
   */
  private void saveIndizies(final ViewTable t) {
    for (final ViewIndex tf : t.indizes) {
      if (tf.isNewCreated()) {
        save.insertNewIndex(new IndexImpl(tf.getName(), save.getTable(t.getName())));
      } else {
        if (tf.isModifiedName()) {
          save.changeIndexUnique(tf.getOldName(), t.getName(), tf.isUnique());
          save.changeNodeNameFromTable(tf.getOldName(), t.getName(), tf.getName());
        }
      }
      if (tf.isModifiedDatafields()) {
        changeDatafields(t, tf);
      }
      tf.saved();
    }
  }

  /**
   * Ändert die Felder im Index
   *
   * @param t
   * @param tf
   */
  private void changeDatafields(final ViewTable t, final ViewIndex tf) {
    final Index newIndex = new IndexImpl(tf.getName(), save.getTable(t.getName()));
    for (final Field f : tf.getFieldList()) {
      if (!"".equals(tf.getAltName(f.getName()))) {
        newIndex.addField(f, tf.getAltName(f.getName()));
      } else {
        newIndex.addField(f);
      }
    }
    save.changeDataFields(newIndex);
  }

  /**
   * Speichert den Fremdschlüssel.
   *
   * @param t
   */
  private void saveForeignKeys(final ViewTable t) {
    for (final ViewForeignKey vfk : t.foreignKeys) {
      if (vfk.isNewCreated()) {
        saveNewForeignKey(t, vfk);
      } else if (vfk.isModifiedName()) {
        save.changeNodeNameFromTable(vfk.getOldName(), t.getName(), vfk.getName());
      }

      if (vfk.isModified()) {
        save.changeFkRelations(vfk.getName(), t.getName(), vfk.getRefTable().getName(), vfk.getIndex().getName());
      }
      vfk.saved();
    }
  }

  /**
   * fügt einen neuen Fremdschlüssel ein
   * 
   * @param t
   * @param vfk
   */
  private void saveNewForeignKey(final ViewTable t, final ViewForeignKey vfk) {
    final Table dmt = save.getTable(t.getName());
    final ForeignKey fk = new ForeignKeyImpl(vfk.getName(), dmt);
    fk.setIndex(dmt.getIndizies().get(dmt.getIndizies().indexOf(vfk.getIndex())));
    fk.setRefTable(save.getTable(vfk.getRefTable().getName()));
    save.insertNewForeignKey(fk);
  }
}
