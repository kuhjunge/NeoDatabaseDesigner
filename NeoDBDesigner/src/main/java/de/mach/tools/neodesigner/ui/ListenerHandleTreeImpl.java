package de.mach.tools.neodesigner.ui;

import de.mach.tools.neodesigner.ui.controller.NeoDbDesignerController;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

/**
 * Listener für die TreeView Impl
 *
 * @author Chris Deter
 *
 */
class ListenerHandleTreeImpl implements ListenerHandle {

  private final NeoDbDesignerController neoDbD;
  private final ChangeListener<TreeItem<String>> changeListener;
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
    changeListener = getTreeChangeListener();
    treeView = tv;
  }

  @Override
  public void attach() {
    treeView.getSelectionModel().selectedItemProperty().addListener(changeListener);
  }

  @Override
  public void detach() {
    treeView.getSelectionModel().selectedItemProperty().removeListener(changeListener);
  }

  /**
   * Öffnet ein neus Tab in der View mit der Tabelle oder der RefTabelle (bei
   * Klick auf FK)
   *
   * @return
   */
  private ChangeListener<TreeItem<String>> getTreeChangeListener() {
    return (observable, oldVal, newVal) -> {
      final TreeItem<String> selectedItem = newVal;
      if (selectedItem.getParent().getValue().equals(Strings.NAME_TABLES)) {
        neoDbD.openTabWithTable(selectedItem.getValue());
      } else if (selectedItem.getParent().getValue().equals(Strings.NAME_FKS)) {
        final String refTable = selectedItem.getValue().substring(selectedItem.getValue().indexOf('(') + 1,
            selectedItem.getValue().length() - 1);
        neoDbD.openTabWithTable(refTable);
      }
    };
  }
}
