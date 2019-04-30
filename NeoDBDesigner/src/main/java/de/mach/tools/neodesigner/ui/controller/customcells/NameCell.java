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

package de.mach.tools.neodesigner.ui.controller.customcells;


import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;

import de.mach.tools.neodesigner.core.Validator;
import de.mach.tools.neodesigner.core.datamodel.viewimpl.ViewField;
import de.mach.tools.neodesigner.ui.GuiUtil;


/** Custom Cell für die Tabelle.
 *
 * @author cd */
public class NameCell extends TableCell<ViewField, String> {

  private TextField textField;
  private final de.mach.tools.neodesigner.core.Validator validatorForField;

  public static Callback<TableColumn<ViewField, String>, TableCell<ViewField, String>> forTableColumn(final Validator validator) {
    return list -> new NameCell(validator);
  }

  private NameCell(final Validator validatorForField) {
    getStyleClass().add("text-field-table-cell");
    this.validatorForField = validatorForField;
  }


  /** {@inheritDoc} */
  @Override
  public void startEdit() {
    if (!isEditable() || !getTableView().isEditable() || !getTableColumn().isEditable()) {
      return;
    }
    super.startEdit();
    if (isEditing()) {
      if (textField == null) {
        textField = createTextField();
      }
      startEditOfField(textField);
    }
  }

  private void startEditOfField(final TextField txtFld) {
    setText(null);
    if (txtFld != null) {
      txtFld.setText(getItem());
    }
    setText(null);
    setGraphic(txtFld);
    if (txtFld != null) {
      txtFld.selectAll();
      txtFld.requestFocus();
    }
  }

  private TextField createTextField() {
    final TextField txtFld = new TextField(getItem());
    GuiUtil.setValidator(txtFld, validatorForField);
    txtFld.focusedProperty().addListener((observable, oldValue, newValue) -> {
      if (oldValue && !newValue) {
        commitEdit(txtFld.getText());
      }
    });
    txtFld.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
      if (e.getCode() == KeyCode.ESCAPE) {
        cancelEdit();
        requestFocus();
      }
      if (e.getCode() == KeyCode.ENTER) {
        if (isEditing()) {
          commitEdit(txtFld.getText());
          requestFocus();

        }
        else {
          startEdit();
        }
      }
    });
    return txtFld;
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
    if (isEditing()) {
      if (textField != null) {
        textField.setText(getItem());
      }
      setText(null);
      setGraphic(textField);
    }
    else {
      setText(getItem());
      setGraphic(null);
    }
  }
}
