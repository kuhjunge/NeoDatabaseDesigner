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

package de.mach.tools.neodesigner.ui;


import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import org.controlsfx.control.textfield.AutoCompletionBinding;

import de.mach.tools.neodesigner.core.DatamodelListener;
import de.mach.tools.neodesigner.core.Model;
import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.ui.controller.MainController;


/** Observer für den Treeview.
 *
 * @author Chris Deter */
public class TreeViewRefresher implements DatamodelListener {

  private final TreeView<String> treeView;
  private final TextField searchText;
  private final Model ndbm;
  private final ListenerHandle listenerTreeView;
  private AutoCompletionBinding<String> autocomplete;

  /** Konstruktor.
   *
   * @param neoDbD der Controller in dem der Treeview sitzt
   * @param tv Der Treeview
   * @param st Das Textfeld, dass Autocomplete verwendet
   * @param m das Model */
  public TreeViewRefresher(final MainController neoDbD, final TreeView<String> tv, final TextField st, final Model m) {
    treeView = tv;
    searchText = st;
    ndbm = m;
    listenerTreeView = new ListenerHandleTreeImpl(neoDbD, treeView);
    listenerTreeView.attach();
    autocomplete = GuiUtil.getAutocomplete(searchText, new ArrayList<>());
    m.addDataModelListener(this);
  }

  /** leert den Treeview. */
  private void clearData() {
    listenerTreeView.detach();
    treeView.getRoot().getChildren().clear();
  }

  /** Läd die Treeview Ansicht.
   *
   * @param listOfTables Liste aller Tabellen im Datenmodell */
  private void loadTreeViewElements(final List<String> listOfTables) {
    final TreeItem<String> rootItem = treeView.getRoot();
    rootItem.getChildren().clear();
    for (final String tableName : listOfTables) {
      final TreeItem<String> ti = createItemMain(tableName);
      ti.expandedProperty().addListener(b -> createItemsDetail(ti, ndbm.getTable(ti.getValue()).orElse(null)));
      rootItem.getChildren().add(ti);
    }
    treeView.refresh();
    listenerTreeView.attach();
  }

  /** erstellt ein Item im Treeview.
   *
   * @param tableName die Tabelle
   * @return TreeItem für diese Tabelle */
  private TreeItem<String> createItemMain(final String tableName) {
    final TreeItem<String> item = new TreeItem<>(tableName);
    final TreeItem<String> field = new TreeItem<>(Strings.NAME_FIELDS);
    item.getChildren().add(field);
    final TreeItem<String> prim = new TreeItem<>(Strings.NAME_PRIMKEY);
    item.getChildren().add(prim);
    final TreeItem<String> index = new TreeItem<>(Strings.NAME_INDEXES);
    item.getChildren().add(index);
    final TreeItem<String> foreign = new TreeItem<>(Strings.NAME_FKS);
    item.getChildren().add(foreign);
    return item;
  }

  /** erstellt ein Item im Treeview.
   *
   * @param t die Tabelle */
  private void createItemsDetail(final TreeItem<String> ti, final Table t) {
    final TreeItem<String> field = ti.getChildren().get(0);
    field.getChildren().clear();
    for (final Field f : t.getFields()) {
      field.getChildren().add(new TreeItem<>(f.getName()));
    }
    final TreeItem<String> prim = ti.getChildren().get(1);
    prim.getChildren().clear();
    prim.getChildren().add(new TreeItem<>(t.getXpk().getName()));
    final TreeItem<String> index = ti.getChildren().get(2);
    index.getChildren().clear();
    for (final Index i : t.getIndizies()) {
      index.getChildren().add(new TreeItem<>(i.getName()));
    }
    final TreeItem<String> foreign = ti.getChildren().get(3);
    foreign.getChildren().clear();
    for (final ForeignKey i : t.getForeignKeys()) {
      foreign.getChildren().add(new TreeItem<>(i.getName() + " (" + i.getRefTable().getName() + ")"));
    }
  }

  /** fügt Autocomplete Funktion zum Suchfeld hinzu. */
  public void loadAutoComplete() {
    if (autocomplete != null) {
      autocomplete.dispose();
      autocomplete = GuiUtil.getAutocomplete(searchText, ndbm.getListWithTableNames());
    }
  }

  /** Läd die Treeview Ansicht. (initialisierung vom Treeview) */
  public void loadTreeView() {
    clearData();
    loadTreeViewElements(ndbm.getListWithTableNames());
  }

  @Override
  public void trigger() {
    Platform.runLater(() -> {
      loadAutoComplete();
      clearData();
      loadTreeViewElements(ndbm.getListWithTableNames());
    });
  }
}
