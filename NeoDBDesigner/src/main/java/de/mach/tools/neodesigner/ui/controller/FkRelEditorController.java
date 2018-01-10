package de.mach.tools.neodesigner.ui.controller;

import de.mach.tools.neodesigner.core.Model;
import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.viewimpl.ViewField;
import de.mach.tools.neodesigner.core.datamodel.viewimpl.ViewForeignKey;
import de.mach.tools.neodesigner.core.datamodel.viewimpl.ViewTable;
import de.mach.tools.neodesigner.ui.Strings;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;

import org.controlsfx.control.textfield.TextFields;

/**
 * Diese Klasse stellt einen Editor für einen Fremdschlüssel bereit.
 *
 * @author Chris Deter
 *
 */
public class FkRelEditorController implements Initializable {
  private ViewTable table;
  private ViewForeignKey fk;
  private ViewForeignKey oldFk;
  private Model ndbm;
  private final List<ViewField> lvf = new ArrayList<>();
  private List<ViewField> oldLvf;
  private boolean newTableData = false;

  /**
   * Setzt die Daten für den Fremdschlüsseleditor.
   *
   * @param ta
   *          ViewTable
   * @param in
   *          ViewForeignKey
   * @param m
   *          Model (für Fremd Tabellenabfrage)
   */
  public void setData(final ViewTable ta, final ViewForeignKey in, final Model m) {
    table = ta;
    oldFk = in;
    fk = new ViewForeignKey(in, table);
    ndbm = m;
  }

  @FXML
  private Label labelName;

  @FXML
  private TextField textFieldFkName;

  @FXML
  private TextField textFieldFkTable;

  @FXML
  private TableView<ViewField> tableMatching;

  @FXML
  private CheckBox chkBoxAutoIndex;

  @FXML
  private CheckBox chkBoxAutoField;

  @FXML
  private Label labelIndex;

  @FXML
  private void onDelete(final ActionEvent event) {
    table.delete.add(oldFk);
    table.foreignKeys.remove(oldFk);
    table.delete.add(oldFk.getVIndex());
    table.indizes.remove(oldFk.getVIndex());
    table.delete.addAll(oldLvf);
    table.getVData().removeAll(oldLvf);
    closeEditor();
  }

  @FXML
  private void onCancel(final ActionEvent event) {
    closeEditor();
  }

  @FXML
  private void onSave(final ActionEvent event) {
    if (newTableData) {
      if (table.foreignKeys.contains(fk)) {
        table.foreignKeys.remove(oldFk);
        table.indizes.remove(oldFk.getVIndex());
        table.delete.addAll(oldFk, oldFk.getVIndex());
        table.getVData().removeAll(oldLvf);
        table.delete.addAll(oldLvf);
      }
      fk.setModified();
      fk.setAsNewCreated();
      fk.getVIndex().setAsNewCreated();
      table.foreignKeys.add(fk);
      table.indizes.add(fk.getVIndex());
      table.getVData().addAll(lvf);
    }
    fk.setModified();
    fk.setName(textFieldFkName.getText());
    closeEditor();
  }

  @FXML
  private void onNewFkt(final ActionEvent event) {
    final ArrayList<Field> alf = new ArrayList<>(ndbm.getTable(textFieldFkTable.getText()).getXpk().getFieldList());
    final ViewTable localTable = new ViewTable(textFieldFkTable.getText());
    // Löschen der alten Felder (FK Referenz und Table)
    lvf.clear();
    fk.getIndex().clearFieldList();
    // Setzen der neuen FK Referenz
    fk.setRefTable(localTable);
    // FK Indexname von FK an Zieltabellenname anpassen
    fk.getIndex().setName(fk.getIndex().getName().substring(0, fk.getName().length() + 1) + fk.getRefTable().getName());
    labelIndex.setText(fk.getIndex().getName());
    // Einfügen der neuen Felder in die Tabelle
    for (final Field f : alf) {
      final ViewField vf = new ViewField(f, table);
      vf.setAsNewCreated();
      vf.setPartOfPrimaryKey(false);
      while (table.getData().contains(vf)) {
        vf.setName(vf.getName() + Strings.NAME_SECONDELEMENT);
      }
      lvf.add(vf);
      fk.addField(vf);
      fk.setAltName(vf.getName(), f.getName());
    }
    // Ansicht neu laden
    loadItems(fk, lvf);
    // Flag fürs speichern
    newTableData = true;
  }

  @Override
  public void initialize(final URL location, final ResourceBundle resources) {
    labelName.setText(Strings.LABELNAME_TABLE + table.getName());
    textFieldFkName.setText(fk.getName());
    textFieldFkTable.setText(fk.getRefTable().getName());
    TextFields.bindAutoCompletion(textFieldFkTable, ndbm.getListWithTableNames().toArray());

    // Holt alle ViewField mithilve von t.getVData die fk getField
    // zugeorndet sind (ansonsten hätten wir nur Field)
    lvf.addAll(table.getVData().stream().filter(p -> fk.getFieldList().contains(p)).collect(Collectors.toList()));
    oldLvf = new ArrayList<>(lvf);
    loadItems(fk, lvf);
  }

  @SuppressWarnings("unchecked")
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
    fieldCol.setCellFactory(TextFieldTableCell.forTableColumn());

    final TableColumn<ViewField, String> refFieldCol = new TableColumn<>(Strings.TABLEROW_REFNAME);
    refFieldCol.setMinWidth(180);
    refFieldCol.setCellValueFactory(f -> new SimpleObjectProperty<>(fk.getAltName(f.getValue().getName())));
    refFieldCol.setCellFactory(TextFieldTableCell.forTableColumn());
    refFieldCol.setEditable(false);
    tableMatching.getColumns().clear();
    tableMatching.getColumns().addAll(fieldCol, refFieldCol);
    tableMatching.setEditable(true);
  }

  /**
   * schließt den Editor
   */
  private void closeEditor() {
    final Stage stage = (Stage) labelName.getScene().getWindow();
    stage.close();
  }
}
