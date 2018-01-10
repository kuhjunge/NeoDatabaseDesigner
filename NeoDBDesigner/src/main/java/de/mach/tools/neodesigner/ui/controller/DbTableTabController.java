package de.mach.tools.neodesigner.ui.controller;

import de.mach.tools.neodesigner.core.Model;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.core.datamodel.impl.TableImpl;
import de.mach.tools.neodesigner.core.datamodel.viewimpl.ViewForeignKey;
import de.mach.tools.neodesigner.core.datamodel.viewimpl.ViewIndex;
import de.mach.tools.neodesigner.core.datamodel.viewimpl.ViewTable;
import de.mach.tools.neodesigner.ui.DbTableTabView;
import de.mach.tools.neodesigner.ui.NeoDbDesignerStarter;

import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Window;

/**
 * Diese Klasse generiert ein Tab mit einer Tabelle aus dem Datenbankmodel.
 *
 * @author Chris Deter
 *
 */
public class DbTableTabController {
  private final Model ndbm;
  private final DbTableTabView view;
  private final ViewTable tbl;

  /**
   * initialisiert die Datenbank.
   *
   * @param tv
   *          TabPane für die Tabs
   * @param st
   *          Die Stage des Programmes
   * @param t
   *          Die darzustellene Tabelle
   * @param m
   *          das Model
   * @param newCreated
   *          TRUE wenn die Tabelle neu erstellt wurde (und noch nicht in der
   *          Datenbank vorhanden ist )
   */
  DbTableTabController(final TabPane tv, final Window st, final Table t, final Model m, final boolean newCreated) {
    tbl = new ViewTable(t);
    view = new DbTableTabView(tv, m.getSelectDatatype());
    ndbm = m;
    if (newCreated) {
      tbl.setAsNewCreated();
    }
    view.createNewTab(tbl);
    setListener(tbl, st);
  }

  /**
   * Getter Table
   * 
   * @return
   */
  public ViewTable getTable() {
    return tbl;
  }

  /**
   * Getter SaveButton
   * 
   * @return
   */
  public Button getSaveButton() {
    return view.getSaveButton();
  }

  /**
   * Getter deleteButton
   * 
   * @return
   */
  public Button getDeleteButton() {
    return view.getDeleteButton();
  }

  /**
   * Entfernt ein Tab
   */
  public void removeTab() {
    view.removeTab();
  }

  /**
   * Getter Name
   * 
   * @return
   */
  public String getNewName() {
    return view.getNewName();
  }

  /**
   * Getter Category
   * 
   * @return
   */
  public String getNewKategory() {
    return view.getNewKategory();
  }

  /**
   * Getter
   * 
   * @return Tab
   */
  public Tab getTab() {
    return view.getTab();
  }

  /**
   * Setzt die Listener in der GUI um Aktionen auf Schaltflächen zu setzen
   *
   * @param t
   * @param window
   */
  private void setListener(final ViewTable t, final Window window) {
    // Index listener
    view.getAddIndex().setOnAction(
        event -> NeoDbDesignerStarter.startIndexRelEditor(t, newTableIndex(), window, view.getTvi(), true));
    view.getModIndexRel()
        .setOnAction(ev -> NeoDbDesignerStarter.startIndexRelEditor(t,
            t.indizes.get(t.indizes.indexOf(view.getTvi().getSelectionModel().getSelectedItem())), window,
            view.getTvi(), false));
    // FK listener
    view.getAddFk().setOnAction(event -> NeoDbDesignerStarter.startFkRelEditor(t, newTableForeignKey(t), ndbm, window));
    view.getModFkRel().setOnAction(ev -> NeoDbDesignerStarter.startFkRelEditor(t,
        view.getTvfk().getSelectionModel().getSelectedItem(), ndbm, window));
  }

  /**
   * erstellt einen neuen Index
   *
   * @return
   */
  private ViewIndex newTableIndex() {
    final ViewIndex i = new ViewIndex("XIE" + ndbm.getNextNumberForIndex(tbl.getIndizies()) + tbl.getName(), tbl);
    i.setAsNewCreated();
    return i;
  }

  /**
   * erstellt einen neuen Fremdschlüssel
   *
   * @param t
   * @return
   */
  private ViewForeignKey newTableForeignKey(final ViewTable t) {
    final String number = Integer.toString(ndbm.getNextFkNumber());
    final ViewForeignKey fk = new ViewForeignKey(Index.Type.R.name() + "_" + number, t);
    final ViewIndex i = new ViewIndex(Index.Type.XIF.name() + number, t);
    i.setAsNewCreated();
    fk.setIndex(i);
    fk.setAsNewCreated();
    fk.setRefTable(new TableImpl(t.getName()));
    return fk;
  }

}
