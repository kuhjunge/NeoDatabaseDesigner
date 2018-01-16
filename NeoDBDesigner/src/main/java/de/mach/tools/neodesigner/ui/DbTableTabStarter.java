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

import de.mach.tools.neodesigner.core.Model;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.core.datamodel.impl.TableImpl;
import de.mach.tools.neodesigner.core.datamodel.viewimpl.ViewForeignKey;
import de.mach.tools.neodesigner.core.datamodel.viewimpl.ViewIndex;
import de.mach.tools.neodesigner.core.datamodel.viewimpl.ViewTable;
import de.mach.tools.neodesigner.ui.controller.FkRelEditorController;
import de.mach.tools.neodesigner.ui.controller.IndexRelEditorController;
import de.mach.tools.neodesigner.ui.controller.TableViewController;
import de.mach.tools.neodesigner.ui.graph.DisplayGraph;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Window;

/**
 * Diese Klasse generiert ein Tab mit einer Tabelle aus dem Datenbankmodel.
 *
 * @author Chris Deter
 *
 */
public class DbTableTabStarter {
  private static final Logger LOG = Logger.getLogger(DbTableTabStarter.class.getName());
  private final Model ndbm;
  private final ViewTable tbl;
  private final TabPane tv;
  private final Tab tab = new Tab();
  private TableViewController tabControl;

  /**
   * initialisiert die Datenbank.
   *
   * @param tv
   *          TabPane für die Tabs
   * @param t
   *          Die darzustellene Tabelle
   * @param m
   *          das Model
   * @param newCreated
   *          TRUE wenn die Tabelle neu erstellt wurde (und noch nicht in der
   *          Datenbank vorhanden ist )
   */
  public DbTableTabStarter(final TabPane tv, final Table t, final Model m, final boolean newCreated) {
    this.tv = tv;
    tbl = new ViewTable(t);
    ndbm = m;

    tab.setClosable(true);
    tab.setText(tbl.getName());

    tabControl = new TableViewController(m.getSelectDatatype());
    final FXMLLoader loader = new FXMLLoader();
    loader.setLocation(getClass().getResource(Strings.FXML_TABLEVIEW));
    loader.setController(tabControl);
    tabControl = loader.getController();
    try {
      tab.setContent(loader.load());
    } catch (final IOException e) {
      removeTab();
      DbTableTabStarter.LOG.log(Level.SEVERE, e.toString(), e);
    }
    tv.getTabs().add(tab);
    tabControl.setCategoryLookup(FXCollections.observableArrayList(ndbm.getCategorySelection()));
    if (newCreated) {
      tbl.setAsNewCreated();
      tabControl.focusTableName();
    }
    tabControl.fillTabWithData(tbl, ndbm.getValidator());
    getSaveButton().setDisable(!tbl.modified().get());
    setListener(tbl, tv.getScene().getWindow());
    tab.setUserData(tabControl);
    // Marker für das Tab, welcher anzeigt, ob die Daten bereits gespeichert wurden
    // oder nicht.
    tbl.modified()
        .addListener(l -> tab.setText(tbl.getName() + (tbl.modified().get() ? Strings.STAR : Strings.EMPTYSTRING)));
  }

  /**
   * Getter Table.
   *
   * @return die Tabelle
   */
  public ViewTable getTable() {
    return tbl;
  }

  /**
   * Getter SaveButton.
   *
   * @return den Save Button
   */
  public Button getSaveButton() {
    return tabControl.getSaveButton();
  }

  /**
   * Getter deleteButton.
   *
   * @return den Lösch Button
   */
  public Button getDeleteButton() {
    return tabControl.getDeleteButton();
  }

  /**
   * Entfernt ein Tab.
   */
  void removeTab() {
    tv.getTabs().remove(tab);
  }

  /**
   * Getter Name.
   *
   * @return Name der Tabelle
   */
  public String getNewName() {
    return tabControl.getNewName();
  }

  /**
   * Getter Category.
   *
   * @return Name der Kategorie
   */
  public String getNewCategory() {
    return tabControl.getNewCategory();
  }

  /**
   * Getter Comment.
   *
   * @return Name der Kategorie
   */
  public String getNewComment() {
    return tabControl.getNewComment();
  }

  /**
   * Getter.
   *
   * @return Tab
   */
  public Tab getTab() {
    return tab;
  }

  /**
   * Setzt die Listener in der GUI um Aktionen auf Schaltflächen zu setzen.
   *
   * @param t
   *          die Tabelle
   * @param window
   *          das Fenster
   */
  private void setListener(final ViewTable t, final Window window) {
    // Index listener
    tabControl.getAddIndex().setOnAction(
        event -> IndexRelEditorController.startIndexRelEditor(t, newTableIndex(), window, tabControl.getTvi(), true));
    tabControl.getModIndexRel()
        .setOnAction(ev -> IndexRelEditorController.startIndexRelEditor(t,
            t.getIndizesRaw().get(t.getIndizesRaw().indexOf(tabControl.getTvi().getSelectionModel().getSelectedItem())),
            window, tabControl.getTvi(), false));
    // FK listener
    tabControl.getAddFk().setOnAction(
        event -> FkRelEditorController.startFkRelEditor(t, newTableForeignKey(t), ndbm, window, tabControl.getTvfk()));
    tabControl.getModFkRel().setOnAction(ev -> FkRelEditorController.startFkRelEditor(t,
        tabControl.getTvfk().getSelectionModel().getSelectedItem(), ndbm, window, tabControl.getTvfk()));
    // Graph
    final DisplayGraph dg = new DisplayGraph();
    tabControl.getGraphButton().setOnAction(event -> dg.showTable(window, t));
    t.modified().addListener((observable, oldValue, newValue) -> getSaveButton().setDisable(!newValue));
    //
    getTab().setOnCloseRequest(e -> {
      if (!getSaveButton().isDisabled()) {
        final Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(Strings.ALTITLE_CLOSETABLE);
        alert.setHeaderText(String.format(Strings.ALTEXT_CLOSETABLE, t.getName()));

        final Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() != ButtonType.OK) {
          e.consume();
        }
      }
    });
  }

  /**
   * erstellt einen neuen Index.
   *
   * @return einen neuen Index
   */
  private ViewIndex newTableIndex() {
    final ViewIndex i = new ViewIndex(Strings.NAME_XIE + ndbm.getNextNumberForIndex(tbl.getIndizies()) + tbl.getName(),
        tbl);
    i.setAsNewCreated();
    return i;
  }

  /**
   * erstellt einen neuen Fremdschlüssel.
   *
   * @param t
   *          die Tabelle
   * @return der neue Fremdschlüssel
   */
  private ViewForeignKey newTableForeignKey(final ViewTable t) {
    final String number = Integer.toString(ndbm.getNextFkNumber());
    final ViewIndex i = new ViewIndex(Index.Type.XIF.name() + number, t);
    i.setAsNewCreated();
    final ViewForeignKey fk = new ViewForeignKey(Strings.NAME_R + Strings.UNDERSCORE + number, i, t);
    fk.setAsNewCreated();
    fk.setRefTable(new TableImpl(t.getName()));
    return fk;
  }

}
