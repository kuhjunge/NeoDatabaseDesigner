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


import javafx.collections.ObservableList;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.impl.FieldImpl;


public class RowCell extends ListCell<Field> {

  public RowCell() {
    super();
    setContentDisplay(ContentDisplay.CENTER);
    // Drag detected
    setOnDragDetected(event -> {
      if (getItem() == null) {
        return;
      }
      Dragboard dragboard = startDragAndDrop(TransferMode.MOVE);
      ClipboardContent content = new ClipboardContent();
      content.putString(getItem().getName());
      dragboard.setContent(content);
      event.consume();
    });

    // Drag Over
    setOnDragOver(event -> {
      if (event.getGestureSource() != this && event.getDragboard().hasString()) {
        event.acceptTransferModes(TransferMode.MOVE);
      }
      event.consume();
    });

    // Drag entered
    setOnDragEntered(event -> {
      if (event.getGestureSource() != this && event.getDragboard().hasString()) {
        setOpacity(0.3);
      }
    });

    // Drag Exited
    setOnDragExited(event -> {
      if (event.getGestureSource() != this && event.getDragboard().hasString()) {
        setOpacity(1);
      }
    });

    // Drop
    setOnDragDropped(event -> {
      if (getItem() == null) {
        return;
      }

      Dragboard db = event.getDragboard();
      boolean success = false;
      if (db.hasString()) {
        ObservableList<Field> items = getListView().getItems();
        int draggedIdx = items.indexOf(new FieldImpl(db.getString()));
        int newOrder = items.indexOf(getItem());
        Field f = items.remove(draggedIdx);
        items.add(newOrder, f);
        success = true;
      }
      event.setDropCompleted(success);

      event.consume();
    });

    // Drag Done
    setOnDragDone(DragEvent::consume);
  }

  @Override
  protected void updateItem(Field item, boolean empty) {
    super.updateItem(item, empty);
    if (empty) {
      setText(null);
      setGraphic(null);
    }
    else {
      setText(item.getName());
      setGraphic(null);
    }
  }
}
