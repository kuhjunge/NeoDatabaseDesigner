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

package de.mach.tools.neodesigner.ui.controller;

import de.mach.tools.neodesigner.core.LoadFromDbTask;
import de.mach.tools.neodesigner.core.Model;
import de.mach.tools.neodesigner.core.ModelImpl;
import de.mach.tools.neodesigner.core.Util;
import de.mach.tools.neodesigner.core.category.CategoryTranslator;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.core.nimport.ImportTask;
import de.mach.tools.neodesigner.database.cypher.DatabaseConnectorImpl;
import de.mach.tools.neodesigner.ui.DbTableTabStarter;
import de.mach.tools.neodesigner.ui.GuiUtil;
import de.mach.tools.neodesigner.ui.SaveHandler;
import de.mach.tools.neodesigner.ui.Strings;
import de.mach.tools.neodesigner.ui.TreeViewObserver;
import de.mach.tools.neodesigner.ui.graph.DisplayGraph;

import java.io.File;
import java.net.URL;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.controlsfx.control.Notifications;

/**
 * GUI der Anwendung mit Treeview aller Tabellen, Suchfunktion, Login in die DB
 * und Tab Register für detailierte Anzeige von Tabellen.
 *
 * @author Chris Deter
 *
 */
public class NeoDbDesignerController implements Initializable {

  private Model ndbm;
  private TreeViewObserver tvo;
  private SaveHandler saveHandler;
  private boolean refreshAccess = true;
  private static final Logger LOG = Logger.getLogger(NeoDbDesignerController.class.getName());
  private HostServices hostServices = null;
  private boolean eventHandlerSet = false;

  @FXML
  private VBox vboxView;

  @FXML
  private Menu menuDatabase;

  @FXML
  private Label labelStatus;

  @FXML
  private Label dbStatus;

  @FXML
  private TextArea sqlInput;

  @FXML
  private TextField searchText;

  @FXML
  private TextField dbDatabase;

  @FXML
  private TextField dbUser;

  @FXML
  private TextField dbPassword;

  @FXML
  private Button buttonDbConnect;

  @FXML
  private Button buttonNewTable;

  @FXML
  private Button buttonImport;

  @FXML
  private Button buttonExport;

  @FXML
  private TreeView<String> treeView;

  @FXML
  private ProgressBar progressStatus;

  @FXML
  private TabPane tableView;

  @FXML
  private void connectToDb(final ActionEvent event) {
    if (!ndbm.isOnline()) {
      buttonDbConnect.setDisable(true);
      connectDb();
      connectEventHandler();
    } else {
      menuDatabase.setDisable(true);
      disconnectDb();
    }
  }

  private void connectEventHandler() {
    if (!eventHandlerSet) {
      // F5
      searchText.getScene().setOnKeyPressed(keyEvent -> {
        if (keyEvent.getCode() == KeyCode.F5) {
          refreshData();
          keyEvent.consume();
        }
      });
      searchText.getScene().addEventHandler(KeyEvent.KEY_RELEASED, event -> processKeyComb(event));
      eventHandlerSet = true;
    }
  }

  private void processKeyComb(final KeyEvent event) {
    final KeyCodeCombination keyComb1 = new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN);
    final KeyCodeCombination keyComb2 = new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN);
    final KeyCodeCombination keyComb3 = new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN);
    final KeyCodeCombination keyComb4 = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
    if (keyComb1.match(event)) {
      GuiUtil.repeatFocus(searchText);
    }
    if (keyComb2.match(event)) {
      exportPdfToFile(null);
    }
    if (keyComb3.match(event)) {
      final Object i = tableView.getSelectionModel().getSelectedItem().getUserData();
      if (i != null && i instanceof TableViewController) {
        ((TableViewController) i).createNew();
      }
    }
    if (keyComb4.match(event)) {
      final Object i = tableView.getSelectionModel().getSelectedItem().getUserData();
      if (i != null && i instanceof TableViewController) {
        ((TableViewController) i).save();
      }
    }
    event.consume();
  }

  @FXML
  private void openTable(final ActionEvent event) {
    final String tableName = searchText.getText().trim();
    openTabWithTable(tableName);
  }

  @FXML
  private void newTable(final ActionEvent event) {
    if (ndbm.isOnline()) {
      createNewTableController(ndbm.getnewTable(Strings.NAME_NEWTABLE), true);
    }
  }

  @FXML
  private void editCategory(final ActionEvent event) {
    openCategoryEditor();
  }

  @FXML
  private void startNeo(final ActionEvent event) {
    if (!ndbm.isNeoServerStarterFileKnown()) {
      final DirectoryChooser folderChooser = new DirectoryChooser();
      folderChooser.setTitle(Strings.TITLE_FINDNEO4JFOLDER);
      ndbm.setNewNeoServerFolder(folderChooser.showDialog(tableView.getScene().getWindow()));
    }
    if (!ndbm.startNeoServer()) {
      NeoDbDesignerController.popupError(Strings.ALTITLE_FILEERR, Strings.ALTEXT_FILEERR, Strings.EMPTYSTRING);
    }
  }

  @FXML
  private void openAbout(final ActionEvent event) {
    NeoDbDesignerController.popupInfo(Strings.SOFTWARENAME, Strings.SOFTWAREINFO, Strings.VERSION,
        AlertType.INFORMATION);
  }

  @FXML
  private void refresh(final ActionEvent event) {
    refreshData();
  }

  @FXML
  private void stats(final ActionEvent event) {
    if (ndbm.isOnline()) {
      final String title = Strings.ALTITLEDBINFO;
      final StringBuilder str = new StringBuilder(Strings.ALLNODES);
      for (final Entry<String, Integer> info : ndbm.getDatabaseStats().entrySet()) {
        str.append(info.getKey() + Strings.SPACERFORINFO + info.getValue().toString() + Strings.EOL);
      }
      NeoDbDesignerController.popupInfo(title, title, str.toString(), AlertType.INFORMATION);
    }
  }

  @FXML
  private void importKategoryFromFile(final ActionEvent event) {
    importFileChooserDialog(Strings.TITLE_IMPORTKATEGORIE, false, ndbm.getPathImportCat());
  }

  @FXML
  private void importSqlFromFile(final ActionEvent event) {
    importFileChooserDialog(Strings.TITLE_IMPORTSQL, true, ndbm.getPathImportSql());
  }

  /**
   * Dialog um den CSV oder SQL Import zu starten.
   *
   * @param title
   *          der Titel
   * @param isSql
   *          True für SQL import, sonst False
   */
  private void importFileChooserDialog(final String title, final boolean isSql, final String path) {
    final FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle(title);
    fileChooser.setInitialDirectory(Util.getFile(path));
    final File file = fileChooser.showOpenDialog(tableView.getScene().getWindow());
    if (file != null) {
      if (isSql) {
        deleteDbBeforeImport();
        importTask(ndbm.importTask(file, Strings.TYPE_SQL), progressStatus, labelStatus);
      } else {
        importTask(ndbm.importTask(file, Strings.TYPE_CSV), progressStatus, labelStatus);
      }
      clearTableview();
    }
  }

  /**
   * Löscht die Datenbank vor dem Import, wenn der Nutzer dies wünscht.
   */
  private void deleteDbBeforeImport() {
    if (!ndbm.getAllTables().isEmpty()) {
      final Alert alert = new Alert(AlertType.CONFIRMATION);
      alert.setTitle(Strings.ALTITLE_DATABASENOTEMPTY);
      alert.setHeaderText(Strings.ALTEXT_DATABASEERR_NOTEMPTY);
      alert.setContentText(Strings.ALTEXT_CLEANDATABASE);

      final Optional<ButtonType> result = alert.showAndWait();
      if (result.isPresent() && result.get() == ButtonType.OK) {
        ndbm.deleteDatabase();
      }
    }
  }

  @FXML
  private void importCsvFromFiles(final ActionEvent event) {
    final DirectoryChooser folderChooser = new DirectoryChooser();
    folderChooser.setTitle(Strings.TITLE_EXPORTCSV);
    folderChooser.setInitialDirectory(Util.getFile(ndbm.getPathImportCsv()));
    final File folder = folderChooser.showDialog(tableView.getScene().getWindow());
    if (folder != null) {
      deleteDbBeforeImport();
      importTask(ndbm.importFolderTask(folder), progressStatus, labelStatus);
    }
  }

  @FXML
  private void exportCqlToFile(final ActionEvent event) {
    final File file = getFileChooser(Strings.TITLE_EXPORTCQL, Strings.EXPORTCQLDEFAULT, ndbm.getPathExportCql());
    exportResult(ndbm.writeExportFile(file, Strings.TYPE_CQL));
  }

  @FXML
  private void exportSqlToFile(final ActionEvent event) {
    final File file = getFileChooser(Strings.TITLE_EXPORTSQL, Strings.EXPORTSQLDEFAULT, ndbm.getPathExportSql());
    exportResult(ndbm.writeExportFile(file, Strings.TYPE_SQL));
  }

  @FXML
  private void exportPdfToFile(final ActionEvent event) {
    ModelPrinterController.startPdfCreator(ndbm.getAllTables(), tableView.getScene().getWindow(), ndbm.getPdfConfig(),
        hostServices);
  }

  @FXML
  private void exportCsvToolchainToFile(final ActionEvent event) {
    final DirectoryChooser folderChooser = new DirectoryChooser();
    folderChooser.setTitle(Strings.TITLE_EXPORTCSV);
    folderChooser.setInitialDirectory(Util.getFile(ndbm.getPathExportCsv()));
    final File file = folderChooser.showDialog(tableView.getScene().getWindow());
    exportResult(ndbm.writeToolchainReport(file));
  }

  /**
   * managt die Benachrichtigung an den User über den Ausgang des Exportes.
   *
   * @param b
   *          true wenn erfolgreich, sonst false
   */
  private void exportResult(final boolean b) {
    if (!b) {
      NeoDbDesignerController.popupError(Strings.ALTITLE_EXPORTERR, Strings.ALTEXT_EXPORTERR, Strings.EMPTYSTRING);
    } else {
      notification(Strings.SOFTWARENAME, Strings.NOTIFICATION_EXPORT);
    }
  }

  /**
   * ruft einen File Chooser Dialog auf.
   *
   * @param title
   *          Titel des Fensters
   * @param initialName
   *          Intialer Dateiname
   * @return die Datei die ausgewählt wurde
   */
  private File getFileChooser(final String title, final String initialName, final String path) {
    final FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle(title);
    fileChooser.setInitialFileName(initialName);
    fileChooser.setInitialDirectory(Util.getFile(path));
    return fileChooser.showSaveDialog(tableView.getScene().getWindow());
  }

  @FXML
  private void deleteDatabase(final ActionEvent event) {
    if (ndbm.deleteDatabase()) {
      refreshData();
    } else {
      NeoDbDesignerController.popupError(Strings.ALTITLE_DATABASEERR, Strings.ALTEXT_DATABASEERR_CONNECT, "");
    }
  }

  @FXML
  private void showDatamodel(final ActionEvent event) {
    final DisplayGraph dg = new DisplayGraph();
    dg.showCompleteDatamodel(tableView.getScene().getWindow(), ndbm);
  }

  @FXML
  private void showCategory(final ActionEvent event) {
    final TextInputDialog dialog = new TextInputDialog(Strings.DEFAULT_CATEGORY);
    dialog.setTitle(Strings.TITLE_DISPLAYCATGRAPH);
    dialog.setHeaderText(Strings.ALTITLE_DISPLAYCATGRAPH);
    dialog.setContentText(Strings.ALTEXT_DISPLAYCATGRAPH);
    final DisplayGraph dg = new DisplayGraph();
    final Optional<String> result = dialog.showAndWait();
    result.ifPresent(category -> dg.showCategory(tableView.getScene().getWindow(), ndbm, category));
  }

  /**
   * Bereitet GUI beim ersten Start auf die Nutzung vor. Initialisiert das Model
   * und setzt den Observer für den TreeView
   */
  @Override
  public void initialize(final URL location, final ResourceBundle resources) {
    ndbm = new ModelImpl(new DatabaseConnectorImpl(), new CategoryTranslator());
    saveHandler = new SaveHandler(ndbm.getSaveObj());
    dbDatabase.setText(ndbm.getAddrOfDb());
    dbUser.setText(ndbm.getUser());
    dbPassword.setText(ndbm.getPw());
    treeView.setRoot(new TreeItem<>(Strings.NAME_TABLES));
    tableView.setTabClosingPolicy(TabClosingPolicy.ALL_TABS);
    tvo = new TreeViewObserver(this, treeView, searchText, ndbm);
    treeView.setFocusTraversable(false);
  }

  /**
   * Läd die Daten erneut aus der Datenbank.
   */
  private void refreshData() {
    if (ndbm.isOnline()) {
      loadDataFromDb(progressStatus);
      clearTableview();
    }
  }

  /**
   * Verbindet die Datenbank.
   */
  private void connectDb() {
    if (ndbm.isOnline()) {
      disconnectDb();
    }
    if (ndbm.connectDb(dbDatabase.getText(), dbUser.getText(), dbPassword.getText())) {
      loadDataFromDb(progressStatus);
      dbStatus.setText(Strings.DBSTATUS_ONLINE);
      buttonDbConnect.setText(Strings.DBBUTTON_DISCONNECT);
    } else {
      NeoDbDesignerController.popupError(Strings.ALTITLE_DATABASEERR, Strings.ALTEXT_DATABASEERR_CONNECT,
          Strings.DETAILDATABASEERR);
      buttonDbConnect.setDisable(false);
    }
  }

  /**
   * Trennt die Datenbank.
   */
  private void disconnectDb() {
    ndbm.disconnectDb();
    dbStatus.setText(Strings.DBSTATUS_OFFLINE);
    buttonDbConnect.setText(Strings.DBBUTTON_CONNECT);
    treeView.getRoot().getChildren().clear();
    buttonDbConnect.setDisable(false);
  }

  /**
   * Zeigt ein Popup an.
   *
   * @param title
   *          Titel des Popups
   * @param message
   *          Nachricht des Popups
   * @param moreInformation
   *          Wird nur angezeigt wenn es mindestens ein Zeichen hat
   * @param t
   *          Typ des Popups
   */
  private static void popupInfo(final String title, final String message, final String moreInformation,
      final AlertType t) {
    final Alert alert = new Alert(t);
    alert.setTitle(title);
    alert.setHeaderText(message);
    if (moreInformation != null && moreInformation.length() > 0) {
      alert.setContentText(moreInformation);
    }
    alert.showAndWait();
  }

  /**
   * Zeigt ein Error Popup an.
   *
   * @param title
   *          Titel der Errormeldung
   * @param message
   *          Nachricht der Errormeldung
   * @param moreInformation
   *          Erweiterte Information bei der Errormeldung
   */
  public static void popupError(final String title, final String message, final String moreInformation) {
    NeoDbDesignerController.popupInfo(title, message, moreInformation, AlertType.ERROR);
  }

  /**
   * Startet den Task, der die Daten aus der Datenbank läd.
   *
   * @param progressStatus
   *          die Statusbar die Verändert werden soll
   * @param labelStatus
   *          das Label auf dem die Änderungen textuell dargestellt werden
   */
  private void loadDataFromDb(final ProgressBar progressStatus) {
    if (refreshAccess) {
      refreshAccess = false;
      tvo.loadAutoComplete();
      tvo.loadTreeView();

      final LoadFromDbTask task = ndbm.loadFromDbTask(new DatabaseConnectorImpl());
      progressStatus.progressProperty().bind(task.progressProperty());
      task.setOnSucceeded(t -> {
        progressStatus.progressProperty().unbind();
        progressStatus.progressProperty().set(0);
        try {
          ndbm.addTableList(task.get());
        } catch (final Exception e) {
          NeoDbDesignerController.LOG.log(Level.SEVERE, e.getMessage(), e);
        }
        menuDatabase.setDisable(false);
        buttonDbConnect.setDisable(false);
        refreshAccess = true;
      });
      task.setOnFailed(t -> {
        NeoDbDesignerController.popupError(Strings.ALTITLE_DATABASEERR, Strings.ALTEXT_DATABASEERR_UNEX_INPUT,
            task.getException().getMessage());
        NeoDbDesignerController.LOG.log(Level.SEVERE, task.getException().getMessage(), task.getException());
        refreshAccess = true;
        buttonDbConnect.setDisable(false);
      });
      new Thread(task).start();
    } else {
      NeoDbDesignerController.LOG.log(Level.WARNING, Strings.LOG_WARNINGLOADDBMORETHANONCE);
    }
  }

  /**
   * Startet den Task zum Import (je nach übergebenen Import Task).
   *
   * @param task
   *          Der Task für den Import
   * @param progressStatus
   *          die Statusbar die Verändert werden soll
   * @param labelStatus
   *          das Label auf dem die Änderungen textuell dargestellt werden
   */
  private void importTask(final ImportTask task, final ProgressBar progressStatus, final Label labelStatus) {
    if (task != null) {
      progressStatus.progressProperty().bind(task.progressProperty());
      labelStatus.textProperty().bind(task.messageProperty());
      task.setOnSucceeded(t -> {
        connectDb();
        notification(Strings.SOFTWARENAME, Strings.NOTIFICATION_IMPORT);
      });
      task.setOnFailed(t -> {
        NeoDbDesignerController.popupError(Strings.ALTITLE_DATABASEERR, Strings.ALTEXT_DATABASEERR_UNEX_ERR,
            task.getException().getMessage());
        NeoDbDesignerController.LOG.log(Level.SEVERE, task.getException().getMessage(), task.getException());
        sqlInput.setText(task.getException().getMessage());

      });
      new Thread(task).start();
    } else {
      NeoDbDesignerController.popupError(Strings.ALTITLE_IMPORTERR, Strings.ALTEXT_IMPORTERR, "");
    }
  }

  private void notification(final String title, final String text) {
    Notifications.create().title(title).text(text).showInformation();
  }

  /**
   * Öffnet ein neues Tab mit einer Tabelle aus der Datenbank.
   *
   * @param tableName
   *          Name der Tabelle
   */
  public void openTabWithTable(final String tableName) {
    final boolean foundTab = tryToOpenExistingTab(tableName);
    if (!foundTab && ndbm.isOnline() && tableName.trim().length() > 0) {
      final Optional<Table> t = ndbm.getTable(tableName);
      if (t.isPresent()) {
        createNewTableController(t.get(), false);
      }
    }
  }

  /**
   * Öffnet ein bestehendes Tab mit der Tabelle oder gibt False zurück.
   *
   * @param tableName
   *          Name der Tabelle
   * @return ob das Tab vorhanden war und ausgewählt wurde
   */
  private boolean tryToOpenExistingTab(final String tableName) {
    boolean foundTab = false;
    for (final Tab t : tableView.getTabs()) {
      if (t.getText().equals(tableName)) {
        tableView.getSelectionModel().select(t);
        foundTab = true;
      }
    }
    return foundTab;
  }

  /**
   * Erstellt den Controller für die anzuzeigene Tabelle und setzt die
   * Speicherevents.
   *
   * @param t
   *          Tabelle
   * @param newCreated
   *          True bedeutet, dass die Tabelle neu erstellt wurde und noch nicht in
   *          der Datenbank existiert
   */
  private void createNewTableController(final Table t, final boolean newCreated) {
    final DbTableTabStarter dbtv = new DbTableTabStarter(tableView, t, ndbm, newCreated);
    dbtv.getSaveButton().setOnAction(e -> saveHandler.saveHandlerForTab(dbtv, ndbm.getValidator()));
    dbtv.getDeleteButton().setOnAction(e -> saveHandler.deleteHandlerForTab(dbtv.getTable(), dbtv));
    tableView.getSelectionModel().selectLast();
  }

  private void openCategoryEditor() {
    final String catEditorName = Strings.TABCATEGORY;
    if (!tryToOpenExistingTab(catEditorName)) {
      final Tab tab = new Tab();
      tab.setClosable(true);
      tab.setText(catEditorName);
      tableView.getTabs().add(tab);
      tableView.getSelectionModel().select(tab);
      new CategoryEditorController(ndbm).generateContent(tab);
    }
  }

  public void setHostServices(final HostServices hs) {
    hostServices = hs;
  }

  private void clearTableview() {
    final Tab t = tableView.getTabs().get(0);
    tableView.getTabs().clear();
    tableView.getTabs().add(t);
  }
}
