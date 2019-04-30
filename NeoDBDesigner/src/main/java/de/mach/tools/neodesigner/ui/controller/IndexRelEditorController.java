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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import de.mach.tools.neodesigner.core.Validator;
import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Index.Type;
import de.mach.tools.neodesigner.core.datamodel.viewimpl.ViewIndex;
import de.mach.tools.neodesigner.core.datamodel.viewimpl.ViewTable;
import de.mach.tools.neodesigner.ui.GuiUtil;
import de.mach.tools.neodesigner.ui.Strings;
import de.mach.tools.neodesigner.ui.controller.customcells.RowCell;


/** Diese Klasse zeigt einen Editor für Indizes.
 *
 * @author Chris Deter */
public class IndexRelEditorController implements Initializable {
  private ViewTable table;
  private ViewIndex index;
  private boolean isNew = false;
  private TableView<ViewIndex> tvi;
  private boolean isLocked = false;
  private static final Logger LOG = Logger.getLogger(IndexRelEditorController.class.getName());
  private Validator val;

  @FXML
  private TextField textFieldName;

  @FXML
  private Button buttonResyncfk;

  @FXML
  private ChoiceBox<Type> choiceBoxType;

  @FXML
  private ListView<Field> listViewFields;

  @FXML
  private ListView<Field> listViewIndexData;

  /** Der Starter für das Index Bearbeitungsfenster.
   *
   * @param t Die Tabelle
   * @param i der Index
   * @param primaryStage das Fenster
   * @param tvi Alle Indizes
   * @param isNew boolean ob der Index neu erstellt ist */
  public static void startIndexRelEditor(final ViewTable t, final ViewIndex i, final Window primaryStage,
                                         final TableView<ViewIndex> tvi, final boolean isNew, Validator val) {
    if (i != null) {
      if (i.getType() == Index.Type.XAK || i.getType() == Index.Type.XIE || i.getType() == Index.Type.XPK
          || i.getType() == Index.Type.XIF) {
        IndexRelEditorController.startEditor(t, i, primaryStage, tvi, isNew, val);
      }
      else {
        IndexRelEditorController.openInformation();
      }
    }
  }

  /** öffnet einen Alert Dialog. */
  private static void openInformation() {
    final Alert alert = new Alert(AlertType.INFORMATION);
    alert.setTitle(Strings.ALTITLE_RELEDITOR);
    alert.setHeaderText(Strings.ALTEXT_RELEDITOR);
    alert.showAndWait();
  }

  /** Startet den Index Editor (nachdem die Prüfung ob der Index kompatibel ist, abgeschlossen ist).
   *
   * @param t die Tabelle
   * @param i der Index
   * @param primaryStage die Stage
   * @param tvi die Tabelle im View mit dem Index
   * @param isNew True wenn neu erstellter Index */
  private static void startEditor(final ViewTable t, final ViewIndex i, final Window primaryStage,
                                  final TableView<ViewIndex> tvi, final boolean isNew, Validator val) {
    IndexRelEditorController relContrl;
    Parent root;
    try {
      final FXMLLoader fxmlLoader = new FXMLLoader(t.getClass().getResource(Strings.FXML_INDEXRELEDITOR));
      relContrl = new IndexRelEditorController();
      relContrl.setData(t, i, tvi, isNew, val);
      fxmlLoader.setController(relContrl);
      root = fxmlLoader.load();
      final Stage stage = new Stage();
      stage.setTitle(Strings.TITLE_RELEDITOR);
      stage.setScene(new Scene(root));
      stage.initModality(Modality.WINDOW_MODAL);
      stage.initOwner(primaryStage.getScene().getWindow());
      stage.getIcons().add(new Image(IndexRelEditorController.class.getResourceAsStream(Strings.FXML_ICON)));
      stage.show();
    }
    catch (final IOException e) {
      IndexRelEditorController.LOG.log(Level.SEVERE, e.toString(), e);
    }
  }

  @FXML
  private void moveElement(final ActionEvent event) {
    final Field f = listViewFields.getSelectionModel().getSelectedItem();
    if (!listViewIndexData.getItems().contains(f) && !isLocked) {
      listViewIndexData.getItems().add(f);
    }
  }

  @FXML
  private void deleteElement(final ActionEvent event) {
    if (!isLocked) {
      listViewIndexData.getItems().remove(listViewIndexData.getSelectionModel().getSelectedItem());
    }
  }

  @FXML
  private void cancel(final ActionEvent event) {
    final Stage stage = (Stage) textFieldName.getScene().getWindow();
    stage.close();
  }

  @FXML
  private void resyncfk(final ActionEvent event) {
    List<Field> ovl = new ArrayList<>();
    for (int i = 1; i <= index.getFieldList().size(); i++) {
      ovl.add(index.getFieldByOrder(i, true));
    }
    listViewIndexData.getItems().clear();
    listViewIndexData.setItems(FXCollections.observableArrayList(ovl));
  }

  @FXML
  private void save(final ActionEvent event) {
    if (isNew && !isLocked) {
      table.getIndizies().add(index);
      tvi.getItems().add(index);
    }
    index.setName(textFieldName.getText());
    if (index.getType() == Index.Type.XPK) {
      Map<Integer, String> newXpkOrder = new HashMap<>();
      int i = 1;
      for (final Field f : listViewIndexData.getItems()) {
        newXpkOrder.put(i++, f.getName());
      }
      i = 1;
      for (final Field f : listViewIndexData.getItems()) {
        index.setOrder(f.getName(), i++, false);
      }
      table.changeXPKOrder(newXpkOrder);
    }
    else if (index.getType() == Type.XIF) {
      int i = 1;
      for (final Field f : listViewIndexData.getItems()) {
        index.setOrder(f.getName(), i++, false);
      }
    }
    else {
      index.setType(choiceBoxType.getValue());
      index.clearFieldList();
      // Die neuen Felder geordnet den Index hinzufügen
      for (final Field f : listViewIndexData.getItems()) {
        index.addField(f);
      }
    }
    index.setType(choiceBoxType.getSelectionModel().getSelectedItem());
    tvi.refresh();
    final Stage stage = (Stage) textFieldName.getScene().getWindow();
    stage.close();
  }

  /** Setzt die Daten in den Controller.
   *
   * @param ta die ViewTabelle
   * @param in der zu bearbeitende ViewIndex
   * @param tvin die GUI Liste in welcher ein neuer Index aufgenommen wird beim speichern
   * @param isNew true wenn es sich um einen neu erstellten index handelt */
  private void setData(final ViewTable ta, final ViewIndex in, final TableView<ViewIndex> tvin, final boolean isNew,
                       Validator val) {
    table = ta;
    index = in;
    this.isNew = isNew;
    tvi = tvin;
    this.val = val;
  }

  @Override
  public void initialize(final URL location, final ResourceBundle resources) {
    textFieldName.setText(index.getName());
    loadItems();
    choiceBoxType.getItems().addAll(Index.Type.XIE, Index.Type.XAK, Index.Type.XIF);
    choiceBoxType.getSelectionModel().select(index.getType());
    final Tooltip tooltip = new Tooltip();
    tooltip.setText(Strings.INDEXTOOLTIP);
    choiceBoxType.setTooltip(tooltip);
    choiceBoxType.getSelectionModel().selectedItemProperty()
        .addListener((final ObservableValue<? extends Type> observable, final Type oldValue,
                      final Type newValue) -> textFieldName
                          .setText(newValue.name() + textFieldName.getText().substring(3)));
    // Drag and Drop für ListView
    listViewIndexData.setCellFactory(param -> new RowCell());
    isLocked = index.getType() != Index.Type.XAK && index.getType() != Index.Type.XIE;
    if (!index.hasIdenticalIndexFKOrder()) {
      buttonResyncfk.setVisible(true);
    }
    GuiUtil.validator(textFieldName, val, index.getName());
  }

  /** läd die Felder aus den Index in die GUI. */
  private void loadItems() {
    listViewFields.setItems(FXCollections.observableArrayList(table.getFields()));
    listViewFields.getSelectionModel().selectFirst();
    listViewIndexData.setItems(FXCollections.observableArrayList(index.getFieldList()));
  }
}
