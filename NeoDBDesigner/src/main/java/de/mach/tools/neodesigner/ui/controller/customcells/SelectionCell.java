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

package de.mach.tools.neodesigner.ui.controller.customcells;

import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

// TODO: fertig machen
public class SelectionCell extends TableCell<ComboBox<String>, String> {
  private final ObservableList<String> items;

  private ComboBox<String> comboBox;

  public SelectionCell(final ObservableList<String> items) {
    this.items = items;
  }

  public static Callback<TableColumn<ComboBox<String>, String>, TableCell<ComboBox<String>, String>> forTableColumn(
      final ObservableList<String> items) {
    return list -> new SelectionCell(items);
  }

  /**
   * Returns the items to be displayed in the ChoiceBox when it is showing.
   */
  public ObservableList<String> getItems() {
    return items;
  }

  /** {@inheritDoc} */
  @Override
  public void startEdit() {
    if (!isEditable() || !getTableView().isEditable() || !getTableColumn().isEditable()) {
      return;
    }

    if (comboBox == null) {
      comboBox = createComboBox(this, items, null);
      comboBox.editableProperty().bind(comboBoxEditableProperty());
    }

    comboBox.getSelectionModel().select(getItem());

    super.startEdit();
    setText(null);
    setGraphic(comboBox);
  }

  private ComboBox<String> createComboBox(final SelectionCell selectionCell, final ObservableList<String> items2,
      final Object object) {
    // TODO Auto-generated method stub
    return null;
  }

  private ObservableValue<? extends Boolean> comboBoxEditableProperty() {
    // TODO Auto-generated method stub
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public void cancelEdit() {
    super.cancelEdit();

    setText(getItem());
    setGraphic(null);
  }

  /** {@inheritDoc} */
  @Override
  public void updateItem(final String item, final boolean empty) {
    super.updateItem(item, empty);
    CellUtilsupdateItem(this, null, null, null, comboBox);
  }

  private void CellUtilsupdateItem(final SelectionCell selectionCell, final Object object, final Object object2,
      final Object object3, final ComboBox<String> comboBox2) {
    // TODO Auto-generated method stub

  }
}
