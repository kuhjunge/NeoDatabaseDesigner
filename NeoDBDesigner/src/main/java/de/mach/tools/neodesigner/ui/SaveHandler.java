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

package de.mach.tools.neodesigner.ui;

import de.mach.tools.neodesigner.core.Save;
import de.mach.tools.neodesigner.core.SaveManager;
import de.mach.tools.neodesigner.core.Validator;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.core.datamodel.impl.FieldImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.IndexImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.TableImpl;
import de.mach.tools.neodesigner.core.datamodel.viewimpl.ViewField;
import de.mach.tools.neodesigner.core.datamodel.viewimpl.ViewForeignKey;
import de.mach.tools.neodesigner.core.datamodel.viewimpl.ViewIndex;
import de.mach.tools.neodesigner.core.datamodel.viewimpl.ViewNodeImpl;
import de.mach.tools.neodesigner.core.datamodel.viewimpl.ViewTable;
import de.mach.tools.neodesigner.ui.Strings;
import de.mach.tools.neodesigner.ui.controller.NeoDbDesignerController;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

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
  private static final Logger LOG = Logger.getLogger(SaveHandler.class.getName());
  private final Save save;
  private final SaveManager pkcm;

  /**
   * Konstruktor.
   *
   * @param dm
   *          Save Interface
   */
  public SaveHandler(final Save dm) {
    save = dm;
    pkcm = new SaveManager(save);
  }

  /**
   * Wird aufgerufen wenn eine Tabelle gelöscht werden soll.
   *
   * @param t
   *          Die zu löschende Tabelle
   * @param dbtv
   *          Der TableTab Controller
   */
  public void deleteHandlerForTab(final ViewTable t, final DbTableTabStarter dbtv) {
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
   * @param dbtts
   *          Der TableTab Controller
   * @param validator
   *          der Validator, welcher verwendet wird um die Tabelle zu validieren
   */
  public void saveHandlerForTab(final DbTableTabStarter dbtts, final Validator validator) {
    final ViewTable t = dbtts.getTable();

    final int maxXpkLenght = validator.getNodeNameLength() - Strings.RELNAME_XPK.length() - 1;
    final String xpkName = Strings.RELNAME_XPK + dbtts.getNewName().substring(0,
        dbtts.getNewName().length() >= maxXpkLenght ? maxXpkLenght : dbtts.getNewName().length());
    t.getXpk().setName(xpkName);

    if (validator.validateTable(t, t.isNewCreated())
        && validator.validateTableName(dbtts.getNewName(), t.getOldName())) {
      if (t.isNewCreated()) { // füge neue Tabelle ein
        createTable(dbtts, t);
      } else {
        saveTable(dbtts, t);
        for (final ViewNodeImpl<?> tf : t.getDeleteRaw()) {
          save.deleteNode(tf.getNode());
        }
      }
      final Optional<Table> dbtable = save.getTable(t.getName());
      if (dbtable.isPresent()) {
        saveData(t, dbtable.get());
        saveIndizies(t, dbtable.get());
        saveForeignKeys(t, dbtable.get());
      } else {
        SaveHandler.LOG.log(Level.WARNING, () -> Strings.LOG_COULDNOTSAVE + t.getName());
      }
      t.saved();
    } else {
      NeoDbDesignerController.popupError(Strings.ALTITLE_SAVEERR, Strings.ALTEXT_SAVEERR, validator.getLastError());
    }
  }

  /**
   * erstellt eine neue Tabelle.
   *
   * @param dbtv
   *          TableTabController
   * @param t
   *          Table
   */
  private void createTable(final DbTableTabStarter dbtv, final ViewTable t) {
    final TableImpl newTable = new TableImpl(dbtv.getNewName());
    t.setName(dbtv.getNewName());
    newTable.setCategory(dbtv.getNewCategory());
    newTable.setXpk(t.getXpk());
    newTable.setComment(dbtv.getNewComment());
    save.insertNewTable(newTable);
    save.insertNewIndex(newTable.getXpk());
    dbtv.getTab().setText(t.getName());
  }

  /**
   * Speichert eine Tabelle.
   *
   * @param dbtv
   *          TableTabController um neuen Namen und Kategorie der tabelle aus der
   *          GUI zu laden
   * @param t
   *          die Tabelle
   */
  private void saveTable(final DbTableTabStarter dbtv, final ViewTable t) {
    // Speichere neuenTabellennamen
    if (!t.getName().equals(dbtv.getNewName())) {
      save.changeTableName(t.getName(), dbtv.getNewName(), t.getXpk().getName());
      t.setName(dbtv.getNewName());
      dbtv.getTab().setId(t.getName());
      dbtv.getTab().setText(t.getName());
    }
    // Speichere neue Kategorie
    if (!t.getCategory().equals(dbtv.getNewCategory())) {
      save.changeTableCategory(t.getName(), dbtv.getNewCategory());
    }
    // Speichere neues Kommentar
    if (!t.getComment().equals(dbtv.getNewComment())) {
      save.saveComment(t.getName(), dbtv.getNewComment());
    }
  }

  /**
   * Speichert die Felder einer Tabelle.
   *
   * @param t
   *          die tabelle
   * @param table
   *          Datenmodell Tabelle
   */
  private void saveData(final ViewTable t, final Table table) {
    for (final ViewField tf : t.getDataFieldsRaw()) {
      if (tf.isNewCreated()) {
        // TODO: auslagern
        // Speichere neue Felder
        save.insertNewField(
            new FieldImpl(tf.getName(), tf.getDomain(), tf.getDomainLength(), tf.isRequired(), tf.getComment(), table));
        if (tf.isModifiedPrim()) {
          pkcm.changeFieldIsPartOfPrim(tf.getName(), t.getName(), tf.isPartOfPrimaryKey());
        }
        tf.saved();
      } else {
        saveFieldChanges(t, tf);
        tf.saved();
      }
    }
  }

  /**
   * Speichert Änderungen für ein existierendes Feld.
   *
   * @param t
   *          die Tabelle zu dem das Feld gehört
   * @param tf
   *          das Feld
   */
  private void saveFieldChanges(final ViewTable t, final ViewField tf) {
    if (tf.isModifiedName()) {
      pkcm.changeFieldName(tf, tf.getOldName());
    }
    if (tf.isModifiedReq()) {
      save.changeFieldRequired(tf.getName(), t.getName(), tf.isRequired());
    }
    if (tf.isModifiedPrim()) {
      pkcm.changeFieldIsPartOfPrim(tf.getName(), t.getName(), tf.isPartOfPrimaryKey());
    }
    if (tf.isModifiedDomain()) {
      pkcm.changeFieldDataType(tf.getName(), tf.getDomain(), tf.getDomainLength(), t.getName());
    }
    if (tf.isModifiedComment()) {
      save.saveComment(tf.getName(), t.getName(), tf.getComment());
    }
  }

  /**
   * Speichert Indizes.
   *
   * @param t
   *          die Tabelle
   * @param table
   *          die Datenmodell Tabelle
   */
  private void saveIndizies(final ViewTable t, final Table table) {
    for (final ViewIndex tf : t.getIndizesRaw()) {
      if (tf.isNewCreated()) {
        save.insertNewIndex(new IndexImpl(tf.getName(), table));
      } else {
        if (tf.isModifiedName()) {
          save.changeIndexUnique(tf.getOldName(), t.getName(), tf.isUnique());
          save.changeNodeNameFromTable(tf.getOldName(), t.getName(), tf.getName());
        }
      }
      if (tf.isModifiedDatafields()) {
        pkcm.changeDatafields(tf, table);
      }
      tf.saved();
    }
  }

  /**
   * Speichert den Fremdschlüssel.
   *
   * @param t
   *          die Tabelle
   * @param table
   *          Datenmodell Tabelle
   */
  private void saveForeignKeys(final ViewTable t, final Table table) {
    for (final ViewForeignKey vfk : t.getForeignKeysRaw()) {
      if (vfk.isNewCreated()) {
        pkcm.saveNewForeignKey(vfk, table);
      } else if (vfk.isModifiedName()) {
        save.changeNodeNameFromTable(vfk.getOldName(), t.getName(), vfk.getName());
      }

      if (vfk.isModifiedRel()) {
        save.changeFkRelations(vfk.getName(), t.getName(), vfk.getRefTable().getName(), vfk.getIndex().getName());
      }
      vfk.saved();
    }
  }
}
