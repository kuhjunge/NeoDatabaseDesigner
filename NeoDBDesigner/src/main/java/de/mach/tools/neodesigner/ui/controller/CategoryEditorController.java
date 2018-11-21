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


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import de.mach.tools.neodesigner.core.Model;
import de.mach.tools.neodesigner.core.category.CategoryObj;
import de.mach.tools.neodesigner.core.category.ViewCategoryObj;
import de.mach.tools.neodesigner.ui.Strings;


class CategoryEditorController {
  private final Model ndbm;

  CategoryEditorController(final Model m) {
    ndbm = m;
  }

  /** Generiert ein Tab zum Bearbeiten von Kategorien.
   *
   * @param tab das verwendete Tab */
  void generateContent(final Tab tab) {
    final VBox container = new VBox();
    container.setSpacing(10);
    final ObservableList<ViewCategoryObj> categories = FXCollections.observableArrayList();
    for (final CategoryObj co : ndbm.getCategorySelection()) {
      categories.add(new ViewCategoryObj(co));
    }
    final TableView<ViewCategoryObj> table = new TableView<>(categories);
    table.setEditable(true);
    generateTableColForCatEditor(table);
    container.getChildren().add(table);
    container.getChildren().add(getButtonsForCatEditor(categories, table));
    tab.setContent(container);
  }

  private void generateTableColForCatEditor(final TableView<ViewCategoryObj> table) {
    final TableColumn<ViewCategoryObj, String> idCol = new TableColumn<>(Strings.TABLEROW_NAME);
    idCol.setCellValueFactory(f -> f.getValue().getId());
    idCol.setCellFactory(TextFieldTableCell.forTableColumn());
    table.getColumns().add(idCol);

    final TableColumn<ViewCategoryObj, String> nameCol = new TableColumn<>(Strings.TABLEROW_NAME);
    nameCol.setCellValueFactory(f -> f.getValue().getName());
    nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
    table.getColumns().add(nameCol);
  }

  private HBox getButtonsForCatEditor(final ObservableList<ViewCategoryObj> categories,
                                      final TableView<ViewCategoryObj> table) {
    final HBox buttonbar = new HBox();
    buttonbar.setSpacing(10);
    final Button newCat = new Button(Strings.BUTTONNAME_ADD + Strings.NAME_CATEGORY);
    newCat.setOnAction(v -> createNewCat(categories, table));
    buttonbar.getChildren().add(newCat);
    final Button delCat = new Button(Strings.BUTTONNAME_DELETE + Strings.NAME_CATEGORY);
    delCat.setOnAction(v -> table.getItems().remove(table.getSelectionModel().getSelectedItem()));
    buttonbar.getChildren().add(delCat);
    final Button saveCat = new Button(Strings.BUTTONNAME_SAVE + Strings.NAME_CATEGORIES);
    saveCat.setOnAction(v -> ndbm.saveCategoryList(convertViewCatToNormal(table.getItems())));
    buttonbar.getChildren().add(saveCat);
    return buttonbar;
  }

  private void createNewCat(final ObservableList<ViewCategoryObj> categories, final TableView<ViewCategoryObj> table) {
    table.getItems().add(new ViewCategoryObj(new CategoryObj(Strings.DEFAULT_CATEGORY, categories.size())));
    table.selectionModelProperty().get().clearAndSelect(categories.size() - 1);
    table.scrollTo(table.getSelectionModel().getFocusedIndex());
  }

  private Map<String, String> convertViewCatToNormal(final List<ViewCategoryObj> lvco) {
    final Map<String, String> catIdAndCatName = new HashMap<>();
    for (final ViewCategoryObj c : lvco) {
      catIdAndCatName.put(c.getCategory(), c.getCategoryText());
    }
    return catIdAndCatName;
  }
}
