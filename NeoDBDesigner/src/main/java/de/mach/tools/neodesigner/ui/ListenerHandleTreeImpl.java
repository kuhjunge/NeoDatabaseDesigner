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

import de.mach.tools.neodesigner.ui.controller.NeoDbDesignerController;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;

/**
 * Implementation des Listener.
 *
 * @author Chris Deter
 *
 */
class ListenerHandleTreeImpl implements ListenerHandle {

  private final NeoDbDesignerController neoDbD;
  private final TreeView<String> treeView;

  /**
   * Konstruktor.
   *
   * @param ndbd
   *          Controller
   * @param tv
   *          Treeview
   */
  ListenerHandleTreeImpl(final NeoDbDesignerController ndbd, final TreeView<String> tv) {
    neoDbD = ndbd;
    treeView = tv;
  }

  @Override
  public void attach() {
    treeView.setOnMouseClicked(ev -> {
      if (ev.getButton().equals(MouseButton.PRIMARY) && ev.getClickCount() == 2) {
        openTable(treeView.getSelectionModel().getSelectedItem());
      }
    });
  }

  @Override
  public void detach() {
    treeView.setOnMouseClicked(null);
  }

  /**
   * Öffnet eine Tabelle.
   *
   * @param tablename
   *          das TreeItem welches den Namen der angeklickten Tabelle enthält
   */
  private void openTable(final TreeItem<String> tablename) {
    final TreeItem<String> selectedItem = tablename;
    if (selectedItem != null) {
      if (selectedItem.getParent().getValue().equals(Strings.NAME_TABLES)) {
        neoDbD.openTabWithTable(selectedItem.getValue());
      } else if (selectedItem.getParent().getValue().equals(Strings.NAME_FKS)) {
        final String refTable = selectedItem.getValue().substring(selectedItem.getValue().indexOf('(') + 1,
            selectedItem.getValue().length() - 1);
        neoDbD.openTabWithTable(refTable);
      }
    }
  }
}
