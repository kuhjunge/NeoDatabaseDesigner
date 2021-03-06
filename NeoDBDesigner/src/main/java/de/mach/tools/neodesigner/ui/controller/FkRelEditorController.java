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

package de.mach.tools.neodesigner.ui.controller;


import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import de.mach.tools.neodesigner.core.Model;
import de.mach.tools.neodesigner.core.Validator;
import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.core.datamodel.viewimpl.ViewField;
import de.mach.tools.neodesigner.core.datamodel.viewimpl.ViewForeignKey;
import de.mach.tools.neodesigner.core.datamodel.viewimpl.ViewTable;
import de.mach.tools.neodesigner.ui.GuiUtil;
import de.mach.tools.neodesigner.ui.Strings;
import de.mach.tools.neodesigner.ui.controller.customcells.NameCell;


/** Diese Klasse stellt einen Editor für einen Fremdschlüssel bereit.
 *
 * @author Chris Deter */
public class FkRelEditorController implements Initializable {
  private static final Logger LOG = Logger.getLogger(FkRelEditorController.class.getName());
  private Validator val;

  /** Startet den Dialog zum Bearbeiten eines FKs.
   *
   * @param t die Tabelle
   * @param m der Foreignkey
   * @param ndbm das Model
   * @param primaryStage das Fenster */
  public static void startFkRelEditor(final ViewTable t, final ViewForeignKey m, final Model ndbm,
                                      final Window primaryStage, final TableView<ViewForeignKey> tv, Validator val) {
    Parent root;
    FkRelEditorController relContrl;
    if (m != null) {
      try {
        final FXMLLoader fxmlLoader = new FXMLLoader(t.getClass().getResource(Strings.FXML_FKRELEDITOR));
        relContrl = new FkRelEditorController();
        relContrl.setData(t, m, ndbm, tv, val);
        fxmlLoader.setController(relContrl);
        root = fxmlLoader.load();
        final Stage stage = new Stage();
        stage.setTitle(Strings.TITLE_RELEDITOR);
        stage.setScene(new Scene(root));
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(primaryStage.getScene().getWindow());
        stage.getIcons().add(new Image(FkRelEditorController.class.getResourceAsStream(Strings.FXML_ICON)));
        stage.show();
      }
      catch (final IOException e) {
        FkRelEditorController.LOG.log(Level.SEVERE, e.toString(), e);
      }
    }
  }

  private TableView<ViewForeignKey> tvfk;
  private ViewTable table;
  private ViewForeignKey fk;
  private ViewForeignKey oldFk;
  private Model ndbm;
  private final List<ViewField> lvf = new ArrayList<>();
  private List<ViewField> oldLvf;

  private boolean newTableData = false;

  @FXML
  private Label labelName;

  @FXML
  private TextField textFieldFkName;

  @FXML
  private TextField textFieldFkTable;

  @FXML
  private TableView<ViewField> tableMatching;

  @FXML
  private CheckBox chkBoxAutoCreate;

  @FXML
  private Label labelIndex;

  /** öffnet einen Alert Dialog.
   *
   * @param isChanged True wenn eine Änderung am Fremdschlüssel vorgenommen wurde */
  private boolean askIfFieldCanBeReused(final List<Field> lf, final boolean isChanged) {
    boolean ret = true;
    if (!lf.isEmpty() && isChanged) {
      final Alert alert = new Alert(AlertType.CONFIRMATION);
      alert.setTitle(Strings.ALTITLE_OVERWRITEFIELD);
      alert.setHeaderText(Strings.ALTEXT_OVERWRITEFIELD);
      alert.setContentText(Strings.ALTEXT_OVERWRITEFIELDDETAIL + lf.toString());

      final Optional<ButtonType> result = alert.showAndWait();
      if (result.isPresent() && result.get() == ButtonType.CANCEL) {
        ret = false;
      }
    }
    return ret;
  }

  private boolean canFieldsBeReused() {
    boolean res = true;
    for (final ViewField newField : table.getDataFieldsRaw()) {
      if (lvf.contains(newField)) {
        final Field originalField = lvf.get(lvf.indexOf(newField));
        res = originalField.getDomain().equals(newField.getDomain())
              && originalField.getDomainLength() == newField.getDomainLength() && res;
      }
    }
    return res;
  }

  /** schließt den Editor. */
  private void closeEditor() {
    final Stage stage = (Stage) labelName.getScene().getWindow();
    stage.close();
  }

  private void deleteOldFields() {
    for (final ViewField vf : oldLvf) {
      // Feld hat kein Index und ist nicht in den neuen Feldern (lvf)
      // enhalten
      if (!vf.hasIndex() && !lvf.contains(vf)) {
        table.addNoteToDeleteList(vf);
        table.getDataFieldsRaw().remove(vf);
        // Feld ist in den neuen Feldern enthalten
      }
    }
  }

  @Override
  public void initialize(final URL location, final ResourceBundle resources) {
    labelName.setText(Strings.LABELNAME_TABLE + table.getName());
    textFieldFkName.setText(fk.getName());
    textFieldFkTable.setText(fk.getRefTable().getName());
    GuiUtil.getAutocomplete(textFieldFkTable, ndbm.getListWithTableNames());
    // Holt alle ViewField mithilve von t.getVData die fk getField
    // zugeorndet sind (ansonsten hätten wir nur Field)
    lvf.addAll(table.getDataFieldsRaw().stream().filter(p -> fk.getIndex().getFieldList().contains(p))
        .collect(Collectors.toList()));
    oldLvf = new ArrayList<>(lvf);
    loadItems(fk, lvf);
    final Tooltip tooltip = new Tooltip();
    tooltip.setText("If activated, it will be ensured that \nalready existing Fields will not be replaced");
    chkBoxAutoCreate.setTooltip(tooltip);
  }

  private void insertNewFields(final ArrayList<Field> fieldsOfRefTblPk, final ViewForeignKey fk) {
    for (final Field refTblPkF : fieldsOfRefTblPk) {
      final ViewField vfCopy = new ViewField(refTblPkF, table);
      vfCopy.setAsNewCreated();
      vfCopy.setPartOfPrimaryKey(false);
      // Wenn Autocreate -> sicherstellen, dass kein Feld doppelt genutzt wird
      while (table.getFields().contains(vfCopy) && chkBoxAutoCreate.selectedProperty().get()) {
        vfCopy.setName(vfCopy.getName() + Strings.NAME_SECONDELEMENT);
      }
      lvf.add(vfCopy);
      fk.getIndex().addField(vfCopy);
      fk.getIndex().setOrder(vfCopy.getName(), fk.getRefTable().getXpk().getOrder(refTblPkF.getName(), false), true);
    }
  }

  private void loadItems(final ViewForeignKey fk, final List<ViewField> lvf) {
    tableMatching.getItems().clear();
    tableMatching.getColumns().clear();
    labelIndex.setText(fk.getIndex().getName());
    for (final ViewField f : lvf) {
      tableMatching.getItems().add(f);
    }
    final TableColumn<ViewField, String> fieldCol = new TableColumn<>(Strings.TABLEROW_NAME);
    fieldCol.setMinWidth(180);
    fieldCol.setCellValueFactory(f -> f.getValue().nameProperty());
    fieldCol.setCellFactory(NameCell.forTableColumn(val));

    final TableColumn<ViewField, String> refFieldCol = new TableColumn<>(Strings.TABLEROW_REFNAME);
    refFieldCol.setMinWidth(180);
    refFieldCol.setCellValueFactory(f -> tableRefName(fk, f));
    refFieldCol.setCellFactory(TextFieldTableCell.forTableColumn());
    refFieldCol.setEditable(false);
    tableMatching.getColumns().addAll(fieldCol, refFieldCol);
    tableMatching.setEditable(true);
  }

  @FXML
  private void onCancel(final ActionEvent event) {
    closeEditor();
  }

  @FXML
  private void onDelete(final ActionEvent event) {
    table.addNoteToDeleteList(oldFk);
    table.getForeignKeysRaw().remove(oldFk);
    table.addNoteToDeleteList(oldFk.getVIndex());
    table.getIndizesRaw().remove(oldFk.getVIndex());
    for (final ViewField vf : oldLvf) {
      if (!vf.hasIndex()) {
        table.addNoteToDeleteList(vf);
        table.getDataFieldsRaw().remove(vf);
      }
    }
    closeEditor();
  }

  @FXML
  private void onNewFkt(final ActionEvent event) {
    // Dieser Button verknüpft den Fremdschlüssel mit einer neuen RefTable
    final Optional<Table> tbl = ndbm.getTable(textFieldFkTable.getText());
    tbl.ifPresent(table1 -> {
      final ViewTable refTable = new ViewTable(table1, true);
      final ArrayList<Field> columnsOfPrimaryKeyOfRefTable = new ArrayList<>(refTable.getXpk().getFieldList());
      // Löschen der alten Felder (FK Referenz und Table)
      lvf.clear();
      fk.getIndex().clearFieldList();
      // Setzen der neuen FK Referenz
      fk.setRefTable(refTable);
      // FK Indexname von FK an Zieltabellenname anpassen
      final String indName = fk.getIndex().getName().substring(0, fk.getName().length() + 1) + fk.getTable().getName();
      fk.getIndex()
          .setName(indName.length() > ndbm.getWordLength() ? indName.substring(0, ndbm.getWordLength() - 1) : indName);
      labelIndex.setText(fk.getIndex().getName());
      // Einfügen der neuen Felder in die Tabelle
      insertNewFields(columnsOfPrimaryKeyOfRefTable, fk);
      // Ansicht neu laden
      loadItems(fk, lvf);
      // Flag fürs speichern
      newTableData = true;
    });
  }

  @FXML
  private void onSave(final ActionEvent event) {
    if (canFieldsBeReused()) {
      save();
      closeEditor();
    }
    else {
      final Alert alert = new Alert(AlertType.ERROR);
      alert.setTitle(Strings.ERROR);
      alert.setHeaderText(Strings.ERROR_CANT_REUSE_FIELD);
      alert.setContentText(Strings.ERROR_CANT_REUSE_FIELD_DETAIL);
      alert.show();
    }
  }

  private void removeOldFk() {
    if (table.getForeignKeysRaw().contains(fk)) {
      table.getForeignKeysRaw().remove(oldFk);
      table.getIndizesRaw().remove(oldFk.getVIndex());
      table.addNoteToDeleteList(oldFk);
      table.addNoteToDeleteList(oldFk.getVIndex());
      // Felder löschen
      deleteOldFields();
    }
  }

  private void save() {
    // Felder des FK wurden verändert
    fk.setName(textFieldFkName.getText());
    final List<Field> reusableFields = table.getDataFieldsRaw().stream().filter(lvf::contains)
        .collect(Collectors.toList());
    if (askIfFieldCanBeReused(reusableFields, newTableData) && newTableData) {
      // FK nur geändert
      removeOldFk();
      // Felder aus der Tabelle sind bereits vorhanden
      useFieldsThatAlreadyExists();
      fk.setAsNewCreated();
      fk.getVIndex().setAsNewCreated();
      table.getForeignKeysRaw().add(fk);
      table.getIndizesRaw().add(fk.getVIndex());
      // Felder hinzufügen
      table.getDataFieldsRaw().addAll(lvf);
      fk.setModifiedRel();
    }
    else if (!oldFk.getName().equals(textFieldFkName.getText())) {
      oldFk.setName(textFieldFkName.getText());
    }
    tvfk.refresh();
  }

  /** Setzt die Daten für den Fremdschlüsseleditor.
   *
   * @param ta ViewTable
   * @param in ViewForeignKey
   * @param m Model (für Fremd Tabellenabfrage)
   * @param tv der TableView, wichtig um geänderte Informationen zu übertragen */
  private void setData(final ViewTable ta, final ViewForeignKey in, final Model m, final TableView<ViewForeignKey> tv,
                       Validator val) {
    table = ta;
    oldFk = in;
    fk = new ViewForeignKey(in, table);
    ndbm = m;
    tvfk = tv;
    this.val = val;
  }

  private SimpleObjectProperty<String> tableRefName(final ViewForeignKey fk,
                                                    final CellDataFeatures<ViewField, String> f) {
    return new SimpleObjectProperty<>(fk.getAltName(f.getValue().getName()));
  }

  private void useFieldsThatAlreadyExists() {
    for (final ViewField vf : table.getDataFieldsRaw()) {
      if (lvf.contains(vf)) {
        // entfernen aus lvf liste
        lvf.remove(vf);
        // altes Feld in den FK einfügen
        final int i = fk.getIndex().getFieldList().indexOf(vf);
        fk.getAltName(fk.getIndex().getFieldList().get(i).getName());
        fk.getIndex().replaceField(vf, vf.getName());
      }
    }
  }
}
