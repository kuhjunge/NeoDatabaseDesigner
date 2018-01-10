package de.mach.tools.neodesigner.ui.controller;

import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Index.Type;
import de.mach.tools.neodesigner.core.datamodel.viewimpl.ViewIndex;
import de.mach.tools.neodesigner.core.datamodel.viewimpl.ViewTable;
import de.mach.tools.neodesigner.ui.Strings;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;

/**
 * Diese Klasse zeigt einen Editor für Indizes.
 *
 * @author Chris Deter
 *
 */
public class IndexRelEditorController implements Initializable {
  private ViewTable table;
  private ViewIndex index;
  private boolean isNew = false;
  private TableView<ViewIndex> tvi;

  @FXML
  private TextField textFieldName;

  @FXML
  private ChoiceBox<Type> choiceBoxType;

  @FXML
  private ListView<Field> listViewFields;

  @FXML
  private ListView<Field> listViewIndexData;

  @FXML
  private void moveElement(final ActionEvent event) {
    final Field f = listViewFields.getSelectionModel().getSelectedItem();
    if (!listViewIndexData.getItems().contains(f)) {
      listViewIndexData.getItems().add(f);
    }
  }

  @FXML
  private void deleteElement(final ActionEvent event) {
    listViewIndexData.getItems().remove(listViewIndexData.getSelectionModel().getSelectedItem());
  }

  @FXML
  private void cancel(final ActionEvent event) {
    final Stage stage = (Stage) textFieldName.getScene().getWindow();
    stage.close();
  }

  @FXML
  private void save(final ActionEvent event) {
    if (isNew) {
      table.getIndizies().add(index);
      tvi.getItems().add(index);
    }
    index.setType(choiceBoxType.getValue());
    index.setName(textFieldName.getText());
    index.clearFieldList();
    for (final Field f : listViewIndexData.getItems()) {
      index.addField(f);
    }
    index.setType(choiceBoxType.getSelectionModel().getSelectedItem());
    final Stage stage = (Stage) textFieldName.getScene().getWindow();
    stage.close();
  }

  /**
   * Setzt die Daten in den Controller.
   *
   * @param ta
   *          die ViewTabelle
   * @param in
   *          der zu bearbeitende ViewIndex
   * @param tvin
   *          die GUI Liste in welcher ein neuer Index aufgenommen wird beim
   *          speichern
   * @param isNew
   *          true wenn es sich um einen neu erstellten index handelt
   */
  public void setData(final ViewTable ta, final ViewIndex in, final TableView<ViewIndex> tvin, final boolean isNew) {
    table = ta;
    index = in;
    this.isNew = isNew;
    tvi = tvin;
  }

  @Override
  public void initialize(final URL location, final ResourceBundle resources) {
    textFieldName.setText(index.getName());
    loadItems();
    choiceBoxType.getItems().addAll(Index.Type.XIE, Index.Type.XAK);
    choiceBoxType.getSelectionModel().select(index.getType());
    final Tooltip tooltip = new Tooltip();
    tooltip.setText(Strings.INDEXTOOLTIP);
    choiceBoxType.setTooltip(tooltip);
    choiceBoxType.getSelectionModel().selectedItemProperty()
        .addListener((final ObservableValue<? extends Type> observable, final Type oldValue,
            final Type newValue) -> textFieldName.setText(newValue.name() + textFieldName.getText().substring(3)));
  }

  /**
   * läd die Felder aus den Index in die GUI
   */
  private void loadItems() {
    listViewFields.setItems(FXCollections.observableArrayList(table.getData()));
    listViewFields.getSelectionModel().selectFirst();
    listViewIndexData.setItems(FXCollections.observableArrayList(index.getFieldList()));
  }
}
