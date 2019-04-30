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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckMenuItem;
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

import de.mach.tools.neodesigner.core.ConfigSaverImpl;
import de.mach.tools.neodesigner.core.Model;
import de.mach.tools.neodesigner.core.ModelImpl;
import de.mach.tools.neodesigner.core.SaveManager;
import de.mach.tools.neodesigner.core.Util;
import de.mach.tools.neodesigner.core.Validator;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.core.datamodel.viewimpl.ViewTable;
import de.mach.tools.neodesigner.inex.ImportTask;
import de.mach.tools.neodesigner.inex.JsonDatamodel;
import de.mach.tools.neodesigner.inex.nexport.CypherGenerator;
import de.mach.tools.neodesigner.inex.nexport.HtmlConstraintListGenerator;
import de.mach.tools.neodesigner.inex.nexport.SqlGenerator;
import de.mach.tools.neodesigner.inex.nexport.TexGenerator;
import de.mach.tools.neodesigner.ui.DbTableTabStarter;
import de.mach.tools.neodesigner.ui.GuiConf;
import de.mach.tools.neodesigner.ui.GuiUtil;
import de.mach.tools.neodesigner.ui.Strings;
import de.mach.tools.neodesigner.ui.TreeViewRefresher;
import de.mach.tools.neodesigner.ui.graph.DisplayGraph;


/** GUI der Anwendung mit Treeview aller Tabellen, Suchfunktion, Login in die DB und Tab Register für detailierte
 * Anzeige von Tabellen.
 *
 * @author Chris Deter */
public class MainController implements Initializable {

  private static final Logger LOG = Logger.getLogger(MainController.class.getName());

  /** Zeigt ein Error Popup an.
   *
   * @param title Titel der Errormeldung
   * @param message Nachricht der Errormeldung
   * @param moreInformation Erweiterte Information bei der Errormeldung */
  static void popupError(final String title, final String message, final String moreInformation) {
    MainController.popupInfo(title, message, moreInformation, AlertType.ERROR);
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
  private TreeViewRefresher tvo;
  private SaveManager saveHandler;
  private boolean refreshAccess = true;

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

  @FXML
  private CheckMenuItem duplicateIndizes;

  private void clearTableview() {
    final Tab t = tableView.getTabs().get(0);
    tableView.getTabs().clear();
    tableView.getTabs().add(t);
  }


  private void connectEventHandler() {
    if (!eventHandlerSet) {
      // F5
      /* searchText.getScene().setOnKeyPressed(keyEvent -> { if (keyEvent.getCode() == KeyCode.F5) { refreshData();
       * keyEvent.consume(); } }); */
      searchText.getScene().addEventHandler(KeyEvent.KEY_RELEASED, this::processKeyComb);
      eventHandlerSet = true;
    }
  }

  @FXML
  private void loadCsv(final ActionEvent event) {
    importCsvToolchainFile();
    connectEventHandler();
  }

  /** Wird aufgerufen wenn eine Tabelle gelöscht werden soll.
   *
   * @param t Die zu löschende Tabelle
   * @param dbtv Der TableTab Controller */
  private void deleteHandlerForTab(final ViewTable t, final DbTableTabStarter dbtv) {
    final Alert alert = new Alert(AlertType.CONFIRMATION);
    alert.setTitle(Strings.ALTITLE_DELTABLE);
    alert.setHeaderText(Strings.ALTEXT_DELTABLE + t.getName());

    final Optional<ButtonType> result = alert.showAndWait();
    if (result.isPresent() && result.get() == ButtonType.OK && !t.isNewCreated()) {
      saveHandler.deleteNode(t);
      dbtv.removeTab();
    }
  }

  /** Wird aufgerufen um eine Tabelle zu speichern.
   *
   * @param dbtts Der TableTab Controller
   * @param validator der Validator, welcher verwendet wird um die Tabelle zu validieren */
  private void saveHandlerForTab(final DbTableTabStarter dbtts, final Validator validator) {
    final ViewTable t = dbtts.getTable();

    final int maxXpkLenght = validator.getNodeNameLength() - Strings.RELNAME_XPK.length() - 1;
    final String xpkName = Strings.RELNAME_XPK + dbtts.getNewName()
        .substring(0, dbtts.getNewName().length() >= maxXpkLenght ? maxXpkLenght : dbtts.getNewName().length());
    t.getXpk().setName(xpkName);

    if (validator.validateTable(t, t.isNewCreated())
        && validator.validateTableName(dbtts.getNewName(), t.getOldName())) {
      String newName = saveHandler.processTable(dbtts.getNewName(), dbtts.getNewCategory(), dbtts.getNewComment(), t);
      if (!dbtts.getTab().getText().equals(newName)) {
        dbtts.getTab().setId(newName);
        dbtts.getTab().setText(newName);
      }
    }
    else {
      MainController.popupError(Strings.ALTITLE_SAVEERR, Strings.ALTEXT_SAVEERR, validator.getLastError());
    }
  }

  /** Erstellt den Controller für die anzuzeigene Tabelle und setzt die Speicherevents.
   *
   * @param t Tabelle
   * @param newCreated True bedeutet, dass die Tabelle neu erstellt wurde und noch nicht in der Datenbank existiert */
  private void createNewTableController(final Table t, final boolean newCreated) {
    final DbTableTabStarter dbtv = new DbTableTabStarter(tableView, t, ndbm, newCreated);
    dbtv.getSaveButton().setOnAction(e -> saveHandlerForTab(dbtv, ndbm.getValidator()));
    dbtv.getDeleteButton().setOnAction(e -> deleteHandlerForTab(dbtv.getTable(), dbtv));
    tableView.getSelectionModel().selectLast();
  }

  @FXML
  private void deleteDatabase(final ActionEvent event) {
    if (ndbm.deleteDatabase()) {
      refreshData();
    }
    else {
      MainController.popupError(Strings.ALTITLE_DATABASEERR, Strings.ALTEXT_DATABASEERR_CONNECT, "");
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

  @FXML
  private void editCategory(final ActionEvent event) {
    openCategoryEditor();
  }

  @FXML
  private void exportCqlToFile(final ActionEvent event) {
    final File file = getFileChooser(Strings.TITLE_EXPORTCQL, Strings.EXPORTCQLDEFAULT, conf.getPathExportGeneric());
    conf.setPathExportGeneric(file.getParent());
    exportResult(Util.writeExportFile(file, new CypherGenerator(), ndbm));
  }

  @FXML
  private void exportCsvToolchainToFile(final ActionEvent event) {
    final DirectoryChooser folderChooser = new DirectoryChooser();
    folderChooser.setTitle(Strings.TITLE_EXPORTCSV);
    folderChooser.setInitialDirectory(Util.getFolder(conf.getPathExportCsv()));
    final File file = folderChooser.showDialog(tableView.getScene().getWindow());
    exportResult(Util.writeToolchainReport(file, ndbm));
  }

  @FXML
  private void exportPdfToFile(final ActionEvent event) {
    ModelPrinterController.startPdfCreator(ndbm.getAllTables(), ndbm.getCategoryTranslation(),
                                           tableView.getScene().getWindow(), ndbm.getPdfConfig(), hostServices);
  }

  /** managt die Benachrichtigung an den User über den Ausgang des Exportes.
   *
   * @param b true wenn erfolgreich, sonst false */
  private void exportResult(final boolean b) {
    if (!b) {
      MainController.popupError(Strings.ALTITLE_EXPORTERR, Strings.ALTEXT_EXPORTERR, Strings.EMPTYSTRING);
    }
    else {
      GuiUtil.notification(Strings.SOFTWARENAME, Strings.NOTIFICATION_EXPORT);
    }
  }

  @FXML
  private void exportSqlToFile(final ActionEvent event) {
    final File file = getFileChooser(Strings.TITLE_EXPORTSQL, Strings.EXPORTSQLDEFAULT, conf.getPathExportSql());
    conf.setPathExportSql(file.getParent());
    exportResult(Util.writeExportFile(file, new SqlGenerator(conf.getCheckDuplicateIndizes()), ndbm));
  }

  @FXML
  private void exportTexToFile(final ActionEvent event) {
    final File file = getFileChooser("Choose Export TeX File", "export.tex", conf.getPathExportGeneric());
    if (file != null) {
      conf.setPathExportGeneric(file.getParent());
      final TextInputDialog dialog = new TextInputDialog(ndbm.getPdfConfig().getPdfTitle());
      dialog.setTitle("Export");
      dialog.setHeaderText("Choose Export TeX File");
      dialog.setContentText("Please enter Title:");
      final Optional<String> result = dialog.showAndWait();
      result.ifPresent(name -> exportResult(Util
          .writeExportFile(file,
                           new TexGenerator(name, ndbm.getPdfConfig().getPdfAuthor(), ndbm.getCategoryTranslation()),
                           ndbm)));
    }
  }

  @FXML
  private void exportJsonToFile(final ActionEvent event) {
    final File file = getFileChooser("Choose Export Json File", Strings.EXPORTJSONDEFAULT, conf.getPathExportGeneric());
    conf.setPathExportGeneric(file.getParent());
    exportResult(Util.writeExportFile(file, new JsonDatamodel(ndbm.getCategorySelection()), ndbm));
  }

  @FXML
  private void exportHlpToFile(final ActionEvent event) {
    final File file = getFileChooser(Strings.TITLE_EXPORTSQL, "Export.htm", conf.getPathExportGeneric());
    conf.setPathExportGeneric(file.getParent());
    exportResult(Util.writeExportFile(file, new HtmlConstraintListGenerator(false), ndbm));
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
  private void importCsvFromFiles(final ActionEvent event) {
    importCsvToolchainFile();
  }

  private void importCsvToolchainFile() {
    final DirectoryChooser folderChooser = new DirectoryChooser();
    folderChooser.setTitle(Strings.TITLE_EXPORTCSV);
    folderChooser.setInitialDirectory(Util.getFolder(conf.getPathImportCsv()));
    final File folder = folderChooser.showDialog(tableView.getScene().getWindow());
    if (folder != null) {
      deleteDbBeforeImport();
      importTask(Util.loadCsvFiles(folder, ndbm), progressStatus, labelStatus);
    }
  }

  /** Dialog um den CSV oder SQL Import zu starten.
   *
   * @param title der Titel
   * @param type Typ des Imports */
  private void importFileChooserDialog(final String title, final Character type, final String path) {
    final FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle(title);
    fileChooser.setInitialDirectory(Util.getFolder(path));
    final File file = fileChooser.showOpenDialog(tableView.getScene().getWindow());
    if (file != null) {
      if (type.equals(Strings.TYPE_CSV)) {
        deleteDbBeforeImport();
        importTask(Util.importTask(ndbm, file, type), progressStatus, labelStatus);
      }
      else {
        importTask(Util.importTask(ndbm, file, type), progressStatus, labelStatus);
      }
      clearTableview();
    }
  }

  @FXML
  private void importKategoryFromFile(final ActionEvent event) {
    importFileChooserDialog(Strings.TITLE_IMPORTKATEGORIE, Strings.TYPE_CSV, conf.getPathImportCat());
  }

  @FXML
  private void importJsonFromFile(final ActionEvent event) {
    importFileChooserDialog("Open JSON File", Strings.TYPE_JSON, conf.getPathExportGeneric());
  }


  @FXML
  private void importSqlFromFile(final ActionEvent event) {
    importFileChooserDialog(Strings.TITLE_IMPORTSQL, Strings.TYPE_SQL, conf.getPathImportSql());
  }

  @FXML
  private void importSqlPartialFromFile(final ActionEvent event) {
    importFileChooserDialog(Strings.TITLE_IMPORTSQL, Strings.TYPE_SQL_PART, conf.getPathImportSql());
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
        labelStatus.textProperty().unbind();
        ndbm.importTables(task.getList());
        GuiUtil.notification(Strings.SOFTWARENAME, Strings.NOTIFICATION_IMPORT);
        progressStatus.progressProperty().unbind();
        progressStatus.progressProperty().set(0);
        refreshData();
      });
      task.setOnFailed(t -> {
        MainController.popupError(Strings.ALTITLE_DATABASEERR, Strings.ALTEXT_DATABASEERR_UNEX_ERR,
                                  task.getException().getMessage());
        MainController.LOG.log(Level.SEVERE, task.getException().getMessage(), task.getException());
        sqlInput.setText(task.getException().getMessage());
      });
      new Thread(task).start();
    }
    else {
      MainController.popupError(Strings.ALTITLE_IMPORTERR, Strings.ALTEXT_IMPORTERR, Strings.EMPTYSTRING);
    }
  }

  /** Bereitet GUI beim ersten Start auf die Nutzung vor. Initialisiert das Model und setzt den Observer für den
   * TreeView */
  @Override
  public void initialize(final URL location, final ResourceBundle resources) {
    ndbm = new ModelImpl(new ConfigSaverImpl(de.mach.tools.neodesigner.core.Strings.SOFTWARENAME),
                         new ConfigSaverImpl(de.mach.tools.neodesigner.core.Strings.SOFTWARENAME,
                                             de.mach.tools.neodesigner.core.Strings.CATEGORYFILE));
    conf = ndbm.getGuiConf();

    treeView.setRoot(new TreeItem<>(Strings.NAME_TABLES));
    tableView.setTabClosingPolicy(TabClosingPolicy.ALL_TABS);
    tvo = new TreeViewRefresher(this, treeView, searchText, ndbm);
    treeView.setFocusTraversable(false);
    ndbm.connect();
    saveHandler = new SaveManager(ndbm.getSaveObj());
    duplicateIndizes.setSelected(conf.getCheckDuplicateIndizes());
    duplicateIndizes.setOnAction(e -> conf.setCheckDuplicateIndizes(duplicateIndizes.isSelected()));
  }

  @FXML
  private void newTable(final ActionEvent event) {
    createNewTableController(ndbm.getNewTable(Strings.NAME_NEWTABLE), true);
  }

  @FXML
  private void openAbout(final ActionEvent event) {
    MainController.popupInfo(Strings.SOFTWARENAME, Strings.SOFTWAREINFO, Strings.VERSION, AlertType.INFORMATION);
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
    if (!foundTab && tableName.trim().length() > 0) {
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
      if (i instanceof TableViewController) {
        ((TableViewController) i).createNew();
      }
    }
    if (keyComb4.match(event)) {
      final Object i = tableView.getSelectionModel().getSelectedItem().getUserData();
      if (i instanceof TableViewController) {
        ((TableViewController) i).save();
      }
    }
    event.consume();
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
  private void startNeoMod(final ActionEvent event) {
    NeoModule.startNeoEditor(ndbm, tableView.getScene().getWindow());
  }

  /** Läd die Daten erneut aus der Datenbank. */
  private void refreshData() {
    if (refreshAccess) {
      refreshAccess = false;
      tvo.loadAutoComplete();
      tvo.loadTreeView();
      refreshAccess = true;
    }
    else {
      MainController.LOG.log(Level.WARNING, Strings.LOG_WARNINGLOADDBMORETHANONCE);
    }
    clearTableview();
  }


  @FXML
  private void stats(final ActionEvent event) {
    final String title = Strings.ALTITLEDBINFO;
    final StringBuilder str = new StringBuilder(Strings.ALLNODES);
    for (final Entry<String, Integer> info : ndbm.getDatabaseStats().entrySet()) {
      str.append(info.getKey());
      str.append(Strings.SPACERFORINFO);
      str.append(info.getValue().toString());
      str.append(Strings.EOL);
    }
    MainController.popupInfo(title, title, str.toString(), AlertType.INFORMATION);
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
