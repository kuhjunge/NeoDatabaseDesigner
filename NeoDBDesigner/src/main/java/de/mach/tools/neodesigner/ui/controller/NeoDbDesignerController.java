package de.mach.tools.neodesigner.ui.controller;

import de.mach.tools.neodesigner.core.LoadFromDbTask;
import de.mach.tools.neodesigner.core.Model;
import de.mach.tools.neodesigner.core.ModelImpl;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.core.nimport.ImportTask;
import de.mach.tools.neodesigner.database.DatabaseConnectorImpl;
import de.mach.tools.neodesigner.ui.SaveHandler;
import de.mach.tools.neodesigner.ui.Strings;
import de.mach.tools.neodesigner.ui.TreeViewObserver;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

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
  private static final Logger LOG = Logger.getLogger(NeoDbDesignerController.class.getName());

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
      connectDb();
      menuDatabase.setDisable(false);
    } else {
      menuDatabase.setDisable(true);
      disconnectDb();
    }
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
  private void startNeo(final ActionEvent event) {
    if (!ndbm.isNeoServerStarterFileKnown()) {
      final DirectoryChooser folderChooser = new DirectoryChooser();
      folderChooser.setTitle(Strings.TITLE_FINDNEO4JFOLDER);
      ndbm.setNewNeoServerFolder(folderChooser.showDialog(treeView.getScene().getWindow()));
    }
    if (!ndbm.startNeoServer()) {
      NeoDbDesignerController.popupError(Strings.ALTITLE_FILEERR, Strings.ALTEXT_FILEERR, "");
    }
  }

  @FXML
  private void openAbout(final ActionEvent event) {
    NeoDbDesignerController.popupInfo(Strings.SOFTWARENAME, Strings.SOFTWAREAUTHOR, Strings.VERSION,
        AlertType.INFORMATION);
  }

  @FXML
  private void refresh(final ActionEvent event) {
    refreshData();
  }

  @FXML
  private void importKategoryFromFile(final ActionEvent event) {
    importFileChooserDialog(Strings.TITLE_IMPORTKATEGORIE, false);
  }

  @FXML
  private void importSqlFromFile(final ActionEvent event) {
    importFileChooserDialog(Strings.TITLE_IMPORTSQL, true);
  }

  /**
   * Dialog um den CSV oder SQL Import zu starten
   * 
   * @param title
   * @param isSql
   *          True für SQL import, sonst False
   */
  private void importFileChooserDialog(final String title, final boolean isSql) {
    final FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle(title);
    final File file = fileChooser.showOpenDialog(treeView.getScene().getWindow());
    if (isSql) {
      importTask(ndbm.importTask(file, 's'), progressStatus, labelStatus);
    } else {
      importTask(ndbm.importTask(file, 'c'), progressStatus, labelStatus);
    }
  }

  @FXML
  private void exportSqlToFile(final ActionEvent event) {
    final File file = getFileChooser(Strings.TITLE_IMPORTSQL, Strings.EXPORTSQLDEFAULT);
    if (!ndbm.writeExportFile(file, 's')) {
      NeoDbDesignerController.popupError(Strings.ALTITLE_EXPORTERR, Strings.ALTEXT_EXPORTERR, "");
    }
  }

  @FXML
  private void exportCsvToFile(final ActionEvent event) {
    final File file = getFileChooser(Strings.TITLE_EXPORTCSV, Strings.EXPORTCSVDEFAULT);
    if (!ndbm.writeExportFile(file, 's')) {
      NeoDbDesignerController.popupError(Strings.ALTITLE_EXPORTERR, Strings.ALTEXT_EXPORTERR, "");
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
  private File getFileChooser(final String title, final String initialName) {
    final FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle(title);
    fileChooser.setInitialFileName(initialName);
    return fileChooser.showSaveDialog(treeView.getScene().getWindow());
  }

  @FXML
  private void deleteDatabase(final ActionEvent event) {
    if (ndbm.deleteDatabase()) {
      refreshData();
    } else {
      NeoDbDesignerController.popupError(Strings.ALTITLE_DATABASEERR, Strings.ALTEXT_DATABASEERR_CONNECT, "");
    }
  }

  /**
   * Bereitet GUI beim ersten Start auf die Nutzung vor. Initialisiert das Model
   * und setzt den Observer für den TreeView
   */
  @Override
  public void initialize(final URL location, final ResourceBundle resources) {
    ndbm = new ModelImpl(new DatabaseConnectorImpl());
    saveHandler = new SaveHandler(ndbm.getSaveObj());
    dbDatabase.setText(ndbm.getAddrOfDb());
    dbUser.setText(ndbm.getUser());
    dbPassword.setText(ndbm.getPw());
    treeView.setRoot(new TreeItem<>(Strings.NAME_TABLES));
    tableView.setTabClosingPolicy(TabClosingPolicy.ALL_TABS);
    tvo = new TreeViewObserver(this, treeView, searchText, ndbm);

  }

  /**
   * Läd die Daten erneut aus der Datenbank.
   */
  private void refreshData() {
    if (ndbm.isOnline()) {
      loadDataFromDb(progressStatus);
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
      NeoDbDesignerController.popupError(Strings.ALTITLE_DATABASEERR, Strings.ALTEXT_DATABASEERR_CONNECT, "");
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
    tvo.loadAutoComplete();
    final LoadFromDbTask task = ndbm.loadFrmDbTask();
    progressStatus.progressProperty().bind(task.progressProperty());
    task.setOnSucceeded(t -> {
      progressStatus.progressProperty().unbind();
      progressStatus.progressProperty().set(0);
    });
    task.setOnFailed(t -> {
      NeoDbDesignerController.popupError(Strings.ALTITLE_DATABASEERR, Strings.ALTEXT_DATABASEERR_UNEX_INPUT,
          task.getException().getMessage());
      NeoDbDesignerController.LOG.log(Level.SEVERE, task.getException().getMessage(), task.getException());
    });
    new Thread(task).start();
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
      task.setOnSucceeded(t -> connectDb());
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

  /**
   * Öffnet ein neues Tab mit einer Tabelle aus der Datenbank.
   *
   * @param tableName
   *          Name der Tabelle
   */
  public void openTabWithTable(final String tableName) {
    final boolean foundTab = tryToOpenExistingTab(tableName);
    if (!foundTab && ndbm.isOnline() && tableName.trim().length() > 0) {
      final Table t = ndbm.getTable(tableName);
      if (t != null) {
        createNewTableController(t, false);
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
   *          True bedeutet, dass die Tabelle neu erstellt wurde und noch nicht
   *          in der Datenbank existiert
   */
  private void createNewTableController(final Table t, final boolean newCreated) {
    final DbTableTabController dbtv = new DbTableTabController(tableView, treeView.getScene().getWindow(), t, ndbm,
        newCreated);
    dbtv.getSaveButton().setOnAction(e -> saveHandler.saveHandlerForTab(dbtv, ndbm.getWordLength()));
    dbtv.getDeleteButton().setOnAction(e -> saveHandler.deleteHandlerForTab(dbtv.getTable(), dbtv));
    tableView.getSelectionModel().selectLast();
  }
}
