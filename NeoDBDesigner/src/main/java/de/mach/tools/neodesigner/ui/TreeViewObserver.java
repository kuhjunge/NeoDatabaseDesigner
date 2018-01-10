package de.mach.tools.neodesigner.ui;

import de.mach.tools.neodesigner.core.Model;
import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.ui.controller.NeoDbDesignerController;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javafx.application.Platform;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

/**
 * Observer für den Treeview
 *
 * @author Chris Deter
 *
 */
public class TreeViewObserver implements Observer {

  private final TreeView<String> treeView;
  private final TextField searchText;
  private final Model ndbm;
  private final ListenerHandle listenerTreeView;
  private AutoCompletionBinding<String> autocomplete;

  /**
   * Konstruktor.
   *
   * @param neoDbD
   *          der Controller in dem der Treeview sitzt
   * @param tv
   *          Der Treeview
   * @param st
   *          Das Textfeld, dass Autocomplete verwendet
   * @param m
   *          das Model
   */
  public TreeViewObserver(final NeoDbDesignerController neoDbD, final TreeView<String> tv, final TextField st,
      final Model m) {
    treeView = tv;
    searchText = st;
    ndbm = m;
    listenerTreeView = new ListenerHandleTreeImpl(neoDbD, treeView);
    listenerTreeView.attach();
    autocomplete = TextFields.bindAutoCompletion(searchText, new ArrayList<String>());
    observe(ndbm.dataModelObservable());
  }

  /**
   * Observe Methode
   *
   * @param o
   */
  private void observe(final Observable o) {
    o.addObserver(this);
  }

  @Override
  public void update(final Observable o, final Object arg) {
    Platform.runLater(() -> {
      clearData();
      loadTreeView(ndbm.getAllLocalTables());
      loadAutoComplete();
    });
  }

  /**
   * leert den Treeview.
   */
  private void clearData() {
    listenerTreeView.detach();
    treeView.getRoot().getChildren().clear();
  }

  /**
   * Läd die Treeview Ansicht
   *
   * @param listOfTables
   */
  private void loadTreeView(final List<Table> listOfTables) {
    final TreeItem<String> rootItem = treeView.getRoot();
    rootItem.getChildren().clear();
    for (final Table t : listOfTables) {
      rootItem.getChildren().add(createItems(t));
    }
    treeView.refresh();
    listenerTreeView.attach();
  }

  /**
   * erstellt ein Item im Treeview
   *
   * @param t
   * @return
   */
  private TreeItem<String> createItems(final Table t) {
    final TreeItem<String> item = new TreeItem<>(t.getName());
    final TreeItem<String> field = new TreeItem<>(Strings.NAME_FIELDS);
    item.getChildren().add(field);
    for (final Field f : t.getData()) {
      field.getChildren().add(new TreeItem<>(f.getName()));
    }

    final TreeItem<String> prim = new TreeItem<>(Strings.NAME_PRIMKEY);
    item.getChildren().add(prim);
    prim.getChildren().add(new TreeItem<>(t.getXpk().getName()));

    final TreeItem<String> index = new TreeItem<>(Strings.NAME_INDEXES);
    item.getChildren().add(index);
    for (final Index i : t.getIndizies()) {
      index.getChildren().add(new TreeItem<>(i.getName()));
    }

    final TreeItem<String> foreign = new TreeItem<>(Strings.NAME_FKS);
    item.getChildren().add(foreign);
    for (final ForeignKey i : t.getForeignKeys()) {
      foreign.getChildren().add(new TreeItem<>(i.getName() + " (" + i.getRefTable().getName() + ")"));
    }
    return item;
  }

  /**
   * fügt Autocomplete Funktion zum Suchfeld hinzu
   */
  public void loadAutoComplete() {
    autocomplete.dispose();
    autocomplete = TextFields.bindAutoCompletion(searchText, ndbm.getListWithTableNames());
  }
}
