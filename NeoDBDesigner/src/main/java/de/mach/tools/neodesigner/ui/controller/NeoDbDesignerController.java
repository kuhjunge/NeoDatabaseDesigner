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

package de.mach.tools.neodesigner.ui.controller;


import java.io.File;
import java.net.URL;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.HostServices;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
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

import de.mach.tools.neodesigner.core.LoadFromDbTask;
import de.mach.tools.neodesigner.core.Model;
import de.mach.tools.neodesigner.core.ModelImpl;
import de.mach.tools.neodesigner.core.Util;
import de.mach.tools.neodesigner.core.category.CategoryTranslator;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.core.nexport.CypherGenerator;
import de.mach.tools.neodesigner.core.nexport.SqlGenerator;
import de.mach.tools.neodesigner.core.nexport.TexGenerator;
import de.mach.tools.neodesigner.core.nimport.ImportTask;
import de.mach.tools.neodesigner.database.cypher.DatabaseConnectorImpl;
import de.mach.tools.neodesigner.ui.DbTableTabStarter;
import de.mach.tools.neodesigner.ui.GuiConf;
import de.mach.tools.neodesigner.ui.GuiUtil;
import de.mach.tools.neodesigner.ui.SaveHandler;
import de.mach.tools.neodesigner.ui.Strings;
import de.mach.tools.neodesigner.ui.TreeViewObserver;
import de.mach.tools.neodesigner.ui.graph.DisplayGraph;


/** GUI der Anwendung mit Treeview aller Tabellen, Suchfunktion, Login in die DB und Tab Register für detailierte
 * Anzeige von Tabellen.
 *
 * @author Chris Deter */
public class NeoDbDesignerController implements Initializable {

  private static final Logger LOG = Logger.getLogger(NeoDbDesignerController.class.getName());

  /** Zeigt ein Error Popup an.
   *
   * @param title Titel der Errormeldung
   * @param message Nachricht der Errormeldung
   * @param moreInformation Erweiterte Information bei der Errormeldung */
  public static void popupError(final String title, final String message, final String moreInformation) {
    NeoDbDesignerController.popupInfo(title, message, moreInformation, AlertType.ERROR);
  }

  /** Zeigt ein Popup an.
   *
   * @param title Titel des Popups
   * @param message Nachricht des Popups
   * @param moreInformation Wird nur angezeigt wenn es mindestens ein Zeichen hat
   * @param t Typ des Popups */
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

  private Model ndbm;
  private GuiConf conf;
  private TreeViewObserver tvo;
  private SaveHandler saveHandler;
  private boolean refreshAccess = true;

  private HostServices hostServices = null;

  private boolean eventHandlerSet = false;

  @FXML
  private ChoiceBox<String> choiceBoxDatabaseTyp;

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
  private Menu neoMenu;

  @FXML
  private TabPane tableView;

  private void clearTableview() {
    final Tab t = tableView.getTabs().get(0);
    tableView.getTabs().clear();
    tableView.getTabs().add(t);
  }

  /** Schließt die Datenbankverbindung */
  public void close() {
    if (ndbm.isOnline()) {
      disconnectDb();
    }
  }

  /** Verbindet die Datenbank. */
  private void connectDb() {
    if (ndbm.isOnline()) {
      disconnectDb();
    }
    if (choiceBoxDatabaseTyp.getSelectionModel().getSelectedIndex() == 0) {
      if (ndbm.connectDb(dbDatabase.getText(), dbUser.getText(), dbPassword.getText())) {
        loadDataFromDb(progressStatus);
        dbStatus.setText(Strings.DBSTATUS_ONLINE);
        buttonDbConnect.setText(Strings.DBBUTTON_DISCONNECT);
      }
      else {
        NeoDbDesignerController.popupError(Strings.ALTITLE_DATABASEERR, Strings.ALTEXT_DATABASEERR_CONNECT,
                                           Strings.DETAILDATABASEERR);
        buttonDbConnect.setDisable(false);
      }
    }
    else {
      ndbm.connectLocalDb();
      neoMenu.setDisable(true);
      dbStatus.setText(Strings.DBSTATUS_ONLINE);
      buttonDbConnect.setText(Strings.DBBUTTON_DISCONNECT);
      menuDatabase.setDisable(false);
      buttonDbConnect.setDisable(false);
    }
    saveHandler = new SaveHandler(ndbm.getSaveObj());
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
      searchText.getScene().addEventHandler(KeyEvent.KEY_RELEASED, this::processKeyComb);
      eventHandlerSet = true;
    }
  }

  @FXML
  private void connectToDb(final ActionEvent event) {
    if (!ndbm.isOnline()) {
      buttonDbConnect.setDisable(true);
      connectDb();
      connectEventHandler();
    }
    else {
      menuDatabase.setDisable(true);
      neoMenu.setDisable(false);
      disconnectDb();
    }
  }

  /** Erstellt den Controller für die anzuzeigene Tabelle und setzt die Speicherevents.
   *
   * @param t Tabelle
   * @param newCreated True bedeutet, dass die Tabelle neu erstellt wurde und noch nicht in der Datenbank existiert */
  private void createNewTableController(final Table t, final boolean newCreated) {
    final DbTableTabStarter dbtv = new DbTableTabStarter(tableView, t, ndbm, newCreated);
    dbtv.getSaveButton().setOnAction(e -> saveHandler.saveHandlerForTab(dbtv, ndbm.getValidator()));
    dbtv.getDeleteButton().setOnAction(e -> saveHandler.deleteHandlerForTab(dbtv.getTable(), dbtv));
    tableView.getSelectionModel().selectLast();
  }

  @FXML
  private void deleteDatabase(final ActionEvent event) {
    if (ndbm.deleteDatabase()) {
      refreshData();
    }
    else {
      NeoDbDesignerController.popupError(Strings.ALTITLE_DATABASEERR, Strings.ALTEXT_DATABASEERR_CONNECT, "");
    }
  }

  /** Löscht die Datenbank vor dem Import, wenn der Nutzer dies wünscht. */
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

  private void disableDbInfo(final boolean disable) {
    dbDatabase.setDisable(disable);
    dbUser.setDisable(disable);
    dbPassword.setDisable(disable);
  }

  /** Trennt die Datenbank. */
  private void disconnectDb() {
    ndbm.disconnectDb();
    dbStatus.setText(Strings.DBSTATUS_OFFLINE);
    buttonDbConnect.setText(Strings.DBBUTTON_CONNECT);
    treeView.getRoot().getChildren().clear();
    buttonDbConnect.setDisable(false);
  }

  @FXML
  private void editCategory(final ActionEvent event) {
    openCategoryEditor();
  }

  @FXML
  private void exportCqlToFile(final ActionEvent event) {
    final File file = getFileChooser(Strings.TITLE_EXPORTCQL, Strings.EXPORTCQLDEFAULT, conf.getPathExportCql());
    conf.setPathExportCql(file.getParent());
    exportResult(ndbm.writeExportFile(file, new CypherGenerator()));
  }

  @FXML
  private void exportCsvToolchainToFile(final ActionEvent event) {
    final DirectoryChooser folderChooser = new DirectoryChooser();
    folderChooser.setTitle(Strings.TITLE_EXPORTCSV);
    folderChooser.setInitialDirectory(Util.getFolder(conf.getPathExportCsv()));
    final File file = folderChooser.showDialog(tableView.getScene().getWindow());
    exportResult(ndbm.writeToolchainReport(file));
  }

  @FXML
  private void exportPdfToFile(final ActionEvent event) {
    ModelPrinterController.startPdfCreator(ndbm.getAllTables(), tableView.getScene().getWindow(), ndbm.getPdfConfig(),
                                           hostServices);
  }

  /** managt die Benachrichtigung an den User über den Ausgang des Exportes.
   *
   * @param b true wenn erfolgreich, sonst false */
  private void exportResult(final boolean b) {
    if (!b) {
      NeoDbDesignerController.popupError(Strings.ALTITLE_EXPORTERR, Strings.ALTEXT_EXPORTERR, Strings.EMPTYSTRING);
    }
    else {
      notification(Strings.SOFTWARENAME, Strings.NOTIFICATION_EXPORT);
    }
  }

  @FXML
  private void exportSqlToFile(final ActionEvent event) {
    final File file = getFileChooser(Strings.TITLE_EXPORTSQL, Strings.EXPORTSQLDEFAULT, conf.getPathExportSql());
    conf.setPathExportSql(file.getParent());
    exportResult(ndbm.writeExportFile(file, new SqlGenerator()));
  }

  @FXML
  private void exportTexToFile(final ActionEvent event) {
    final File file = getFileChooser("Choose Export TeX File", "export.tex", conf.getPathExportCql());
    if (file != null) {
      conf.setPathExportSql(file.getParent());
      final TextInputDialog dialog = new TextInputDialog(ndbm.getPdfConfig().getPdfTitle());
      dialog.setTitle("Export");
      dialog.setHeaderText("Choose Export TeX File");
      dialog.setContentText("Please enter Title:");
      final Optional<String> result = dialog.showAndWait();
      result.ifPresent(name -> exportResult(ndbm
          .writeExportFile(file,
                           new TexGenerator(name, ndbm.getPdfConfig().getAuthor(),
                                            new File(ndbm.getPdfConfig().getConfigPath()
                                                     + de.mach.tools.neodesigner.core.Strings.CATEGORYFILE)))));
    }
  }

  /** ruft einen File Chooser Dialog auf.
   *
   * @param title Titel des Fensters
   * @param initialName Intialer Dateiname
   * @return die Datei die ausgewählt wurde */
  private File getFileChooser(final String title, final String initialName, final String path) {
    final FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle(title);
    fileChooser.setInitialFileName(initialName);
    fileChooser.setInitialDirectory(Util.getFolder(path));
    return fileChooser.showSaveDialog(tableView.getScene().getWindow());
  }

  @FXML
  private void importBulkCsvFromFiles(final ActionEvent event) {
    final DirectoryChooser folderChooser = new DirectoryChooser();
    folderChooser.setTitle(Strings.TITLE_EXPORTCSV);
    folderChooser.setInitialDirectory(Util.getFolder(conf.getPathImportCsv()));
    final File folder = folderChooser.showDialog(tableView.getScene().getWindow());
    if (folder != null) {
      deleteDbBeforeImport();
      importTask(ndbm.bulkimportTask(folder), progressStatus, labelStatus);
    }
  }

  @FXML
  private void importCsvFromFiles(final ActionEvent event) {
    final DirectoryChooser folderChooser = new DirectoryChooser();
    folderChooser.setTitle(Strings.TITLE_EXPORTCSV);
    folderChooser.setInitialDirectory(Util.getFolder(conf.getPathImportCsv()));
    final File folder = folderChooser.showDialog(tableView.getScene().getWindow());
    if (folder != null) {
      deleteDbBeforeImport();
      importTask(ndbm.importFolderTask(folder), progressStatus, labelStatus);
    }
  }

  /** Dialog um den CSV oder SQL Import zu starten.
   *
   * @param title der Titel
   * @param isSql True für SQL import, sonst False */
  private void importFileChooserDialog(final String title, final boolean isSql, final String path) {
    final FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle(title);
    fileChooser.setInitialDirectory(Util.getFolder(path));
    final File file = fileChooser.showOpenDialog(tableView.getScene().getWindow());
    if (file != null) {
      if (isSql) {
        deleteDbBeforeImport();
        importTask(ndbm.importTask(file, Strings.TYPE_SQL), progressStatus, labelStatus);
      }
      else {
        importTask(ndbm.importTask(file, Strings.TYPE_CSV), progressStatus, labelStatus);
      }
      clearTableview();
    }
  }

  @FXML
  private void importKategoryFromFile(final ActionEvent event) {
    importFileChooserDialog(Strings.TITLE_IMPORTKATEGORIE, false, conf.getPathImportCat());
  }

  @FXML
  private void importSqlFromFile(final ActionEvent event) {
    importFileChooserDialog(Strings.TITLE_IMPORTSQL, true, conf.getPathImportSql());
  }

  /** Startet den Task zum Import (je nach übergebenen Import Task).
   *
   * @param task Der Task für den Import
   * @param progressStatus die Statusbar die Verändert werden soll
   * @param labelStatus das Label auf dem die Änderungen textuell dargestellt werden */
  private void importTask(final ImportTask task, final ProgressBar progressStatus, final Label labelStatus) {
    if (task != null) {
      progressStatus.progressProperty().bind(task.progressProperty());
      labelStatus.textProperty().bind(task.messageProperty());
      task.setOnSucceeded(t -> {
        refreshData();
        notification(Strings.SOFTWARENAME, Strings.NOTIFICATION_IMPORT);
      });
      task.setOnFailed(t -> {
        NeoDbDesignerController.popupError(Strings.ALTITLE_DATABASEERR, Strings.ALTEXT_DATABASEERR_UNEX_ERR,
                                           task.getException().getMessage());
        NeoDbDesignerController.LOG.log(Level.SEVERE, task.getException().getMessage(), task.getException());
        sqlInput.setText(task.getException().getMessage());
      });
      new Thread(task).start();
    }
    else {
      NeoDbDesignerController.popupError(Strings.ALTITLE_IMPORTERR, Strings.ALTEXT_IMPORTERR, Strings.EMPTYSTRING);
    }
  }

  /** Bereitet GUI beim ersten Start auf die Nutzung vor. Initialisiert das Model und setzt den Observer für den
   * TreeView */
  @Override
  public void initialize(final URL location, final ResourceBundle resources) {
    ndbm = new ModelImpl(new DatabaseConnectorImpl(), new CategoryTranslator());
    conf = ndbm.getGuiConf();
    dbDatabase.setText(ndbm.getAddrOfDb());
    dbUser.setText(conf.getUser());
    dbPassword.setText(conf.getPw());
    treeView.setRoot(new TreeItem<>(Strings.NAME_TABLES));
    tableView.setTabClosingPolicy(TabClosingPolicy.ALL_TABS);
    tvo = new TreeViewObserver(this, treeView, searchText, ndbm);
    treeView.setFocusTraversable(false);
    choiceBoxDatabaseTyp.setItems(FXCollections.observableArrayList("Neo4J DB", "in Memory"));
    choiceBoxDatabaseTyp.getSelectionModel().select(conf.getLastDatabaseTyp());
    setDbInfo(conf.getLastDatabaseTyp());
    choiceBoxDatabaseTyp.getSelectionModel().selectedIndexProperty().addListener((ov, v, n) -> {
      conf.setLastDatabaseTyp(choiceBoxDatabaseTyp.getSelectionModel().getSelectedIndex());
      final int i = n.intValue();
      setDbInfo(i);
    });
  }

  /** Startet den Task, der die Daten aus der Datenbank läd.
   *
   * @param progressStatus die Statusbar die Verändert werden soll */
  private void loadDataFromDb(final ProgressBar progressStatus) {
    if (refreshAccess) {
      refreshAccess = false;
      tvo.loadAutoComplete();
      tvo.loadTreeView();

      final LoadFromDbTask task = ndbm.loadFromDbTask();
      progressStatus.progressProperty().bind(task.progressProperty());
      task.setOnSucceeded(t -> {
        progressStatus.progressProperty().unbind();
        progressStatus.progressProperty().set(0);
        try {
          ndbm.addTableList(task.get());
        }
        catch (final Exception e) {
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
    }
    else {
      NeoDbDesignerController.LOG.log(Level.WARNING, Strings.LOG_WARNINGLOADDBMORETHANONCE);
    }
  }

  @FXML
  private void newTable(final ActionEvent event) {
    if (ndbm.isOnline()) {
      createNewTableController(ndbm.getnewTable(Strings.NAME_NEWTABLE), true);
    }
  }

  private void notification(final String title, final String text) {
    Notifications.create().title(title).text(text).showInformation();
  }

  @FXML
  private void openAbout(final ActionEvent event) {
    NeoDbDesignerController.popupInfo(Strings.SOFTWARENAME, Strings.SOFTWAREINFO, Strings.VERSION,
                                      AlertType.INFORMATION);
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

  @FXML
  private void openTable(final ActionEvent event) {
    final String tableName = searchText.getText().trim();
    openTabWithTable(tableName);
  }

  /** Öffnet ein neues Tab mit einer Tabelle aus der Datenbank.
   *
   * @param tableName Name der Tabelle */
  public void openTabWithTable(final String tableName) {
    final boolean foundTab = tryToOpenExistingTab(tableName);
    if (!foundTab && ndbm.isOnline() && tableName.trim().length() > 0) {
      final Optional<Table> t = ndbm.getTable(tableName);
      t.ifPresent(table -> createNewTableController(table, false));
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
  private void refresh(final ActionEvent event) {
    refreshData();
  }

  /** Läd die Daten erneut aus der Datenbank. */
  private void refreshData() {
    if (ndbm.isOnline()) {
      loadDataFromDb(progressStatus);
      clearTableview();
    }
  }

  private void setDbInfo(final int i) {
    if (i > 0) {
      disableDbInfo(true);
    }
    else {
      disableDbInfo(false);
    }
  }

  public void setHostServices(final HostServices hs) {
    hostServices = hs;
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

  @FXML
  private void showDatamodel(final ActionEvent event) {
    final DisplayGraph dg = new DisplayGraph();
    dg.showCompleteDatamodel(tableView.getScene().getWindow(), ndbm);
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
  private void stats(final ActionEvent event) {
    if (ndbm.isOnline()) {
      final String title = Strings.ALTITLEDBINFO;
      final StringBuilder str = new StringBuilder(Strings.ALLNODES);
      for (final Entry<String, Integer> info : ndbm.getDatabaseStats().entrySet()) {
        str.append(info.getKey());
        str.append(Strings.SPACERFORINFO);
        str.append(info.getValue().toString());
        str.append(Strings.EOL);
      }
      NeoDbDesignerController.popupInfo(title, title, str.toString(), AlertType.INFORMATION);
    }
  }

  /** Öffnet ein bestehendes Tab mit der Tabelle oder gibt False zurück.
   *
   * @param tableName Name der Tabelle
   * @return ob das Tab vorhanden war und ausgewählt wurde */
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
}
