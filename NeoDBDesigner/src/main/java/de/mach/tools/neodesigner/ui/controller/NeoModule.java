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
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import de.mach.tools.neodesigner.core.Model;
import de.mach.tools.neodesigner.inex.LoadFromDbTask;
import de.mach.tools.neodesigner.inex.cypher.DatabaseConnectorImpl;
import de.mach.tools.neodesigner.inex.cypher.DatabaseManagerLean;
import de.mach.tools.neodesigner.inex.nexport.WriteToDbTask;
import de.mach.tools.neodesigner.ui.Strings;


class NeoModule implements Initializable {
  private static final Logger LOG = Logger.getLogger(NeoModule.class.getName());
  private Model ndbm;
  private DatabaseManagerLean dbcon;

  private NeoModule(Model ndbm) {
    this.ndbm = ndbm;
    dbcon = new DatabaseManagerLean(new DatabaseConnectorImpl());
  }

  public static void startNeoEditor(final Model ndbm, final Window primaryStage) {
    Parent root;
    NeoModule neoMod;
    try {
      final FXMLLoader fxmlLoader = new FXMLLoader(ndbm.getClass().getResource(Strings.FXML_NEOMODULE));
      neoMod = new NeoModule(ndbm);
      fxmlLoader.setController(neoMod);
      root = fxmlLoader.load();
      final Stage stage = new Stage();
      stage.setTitle(Strings.TITLE_RELEDITOR);
      stage.setScene(new Scene(root));
      stage.initModality(Modality.WINDOW_MODAL);
      stage.initOwner(primaryStage.getScene().getWindow());
      stage.getIcons().add(new Image(FkRelEditorController.class.getResourceAsStream(Strings.FXML_ICON)));
      stage.show();
      // Wichtig, damit Prozess korrekt beendet wird
      stage.setOnCloseRequest(event -> neoMod.disconnect());
    }
    catch (final IOException e) {
      LOG.log(Level.SEVERE, e.toString(), e);
    }
  }

  private void set(Model dbm) {
    this.ndbm = dbm;
  }

  @FXML
  private TextField dbDatabase;

  @FXML
  private TextField dbUser;

  @FXML
  private TextField dbPassword;

  @FXML
  private Button buttonDbConnect;

  @FXML
  private Button buttonExport;

  @FXML
  private Button buttonImport;

  @FXML
  private ProgressBar progressStatus;

  @FXML
  private Label dbStatus;

  @FXML
  private void connectToDb(final ActionEvent event) {
    if (!dbcon.isReady()) {
      buttonDbConnect.setDisable(true);
      if (dbcon.connectDb(dbDatabase.getText(), dbUser.getText(), dbPassword.getText())) {
        dbStatus.setText(Strings.DBSTATUS_ONLINE);
        buttonDbConnect.setText(Strings.DBBUTTON_DISCONNECT);
        disableDbInfo(true);
      }
      else {
        MainController.popupError(Strings.ALTITLE_DATABASEERR, Strings.ALTEXT_DATABASEERR_CONNECT,
                                  Strings.DETAILDATABASEERR);
        buttonDbConnect.setDisable(false);
        buttonDbConnect.setText(Strings.DBBUTTON_CONNECT);
        disableDbInfo(false);
      }
      buttonDbConnect.setDisable(false);
    }
    else {
      disconnect();
      disableDbInfo(false);
      dbStatus.setText(Strings.DBSTATUS_OFFLINE);
      buttonDbConnect.setDisable(false);
    }
  }


  private void disconnect() {
    if (dbcon.isReady()) {
      dbcon.disconnectDb();
    }
  }

  @FXML
  private void doImport(final ActionEvent event) {
    buttonImport.setDisable(true);
    final LoadFromDbTask task = new LoadFromDbTask(dbcon);
    progressStatus.progressProperty().bind(task.progressProperty());
    task.setOnSucceeded(t -> {
      progressStatus.progressProperty().unbind();
      progressStatus.progressProperty().set(0);
      try {
        ndbm.importTables(task.get());
      }
      catch (final Exception e) {
        LOG.log(Level.SEVERE, e.getMessage(), e);
      }
      buttonDbConnect.setDisable(false);
      buttonImport.setDisable(false);
    });
    task.setOnFailed(t -> {
      MainController.popupError(Strings.ALTITLE_DATABASEERR, Strings.ALTEXT_DATABASEERR_UNEX_INPUT,
                                task.getException().getMessage());
      LOG.log(Level.SEVERE, task.getException().getMessage(), task.getException());
      buttonDbConnect.setDisable(false);
      buttonImport.setDisable(false);
    });
    new Thread(task).start();
  }

  @FXML
  private void doExport(final ActionEvent event) {
    buttonExport.setDisable(true);
    final WriteToDbTask task = new WriteToDbTask(dbcon, ndbm.getAllTables());
    progressStatus.progressProperty().bind(task.progressProperty());
    task.setOnSucceeded(t -> {
      progressStatus.progressProperty().unbind();
      progressStatus.progressProperty().set(0);
      LOG.log(Level.INFO, "Export finished");
      buttonExport.setDisable(false);
    });
    task.setOnFailed(t -> {
      MainController.popupError(Strings.ALTITLE_DATABASEERR, Strings.ALTEXT_DATABASEERR_UNEX_INPUT,
                                task.getException().getMessage());
      LOG.log(Level.SEVERE, task.getException().getMessage(), task.getException());
      buttonDbConnect.setDisable(false);
      buttonExport.setDisable(false);
    });
    new Thread(task).start();
  }

  @FXML
  private void startNeoServer(final ActionEvent event) {
    if (!isNeoServerStarterFileKnown()) {
      final DirectoryChooser folderChooser = new DirectoryChooser();
      folderChooser.setTitle(Strings.TITLE_FINDNEO4JFOLDER);
      setNewNeoServerFolder(folderChooser.showDialog(buttonDbConnect.getScene().getWindow()));
    }
    if (!startNeoServer()) {
      MainController.popupError(Strings.ALTITLE_FILEERR, Strings.ALTEXT_FILEERR, Strings.EMPTYSTRING);
    }
  }

  private Boolean setNewNeoServerFolder(final File folder) {
    if (folder.isDirectory()) {
      ndbm.getGuiConf().setNeoDbStarterLocation(folder.getAbsolutePath());
      ndbm.getGuiConf().save();
    }
    return isNeoServerStarterFileKnown();
  }

  private Boolean startNeoServer() {
    if (isNeoServerStarterFileKnown()) {
      final File file = getNeoServerStarterFile();
      final File batchFile = new File(file.getAbsolutePath() + Strings.PATH_NEO4J);
      if (batchFile.exists()) {
        final Runtime r = Runtime.getRuntime();
        try {
          final String str = de.mach.tools.neodesigner.core.Strings.EXECNEO4J_PRE + file.getAbsolutePath()
                             + de.mach.tools.neodesigner.core.Strings.EXECNEO4J_POST;
          r.exec(str);
          ndbm.getGuiConf().setNeoDbStarterLocation(file.getAbsolutePath());
          return true;
        }
        catch (final IOException e) {
          LOG.log(Level.SEVERE, e.toString(), e);
        }
      }
    }
    return false;
  }

  private Boolean isNeoServerStarterFileKnown() {
    return new File(getNeoServerStarterFile().getAbsolutePath() + Strings.PATH_NEO4J).exists();
  }

  /** gibt ein File Objekt der Neo4J Start Batch zurück.
   *
   * @return die Start Datei */
  private File getNeoServerStarterFile() {
    return new File(ndbm.getGuiConf().getNeoDbStarterLocation());
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    dbDatabase.setText(ndbm.getAddrOfDb());
    dbUser.setText(ndbm.getGuiConf().getUser());
    dbPassword.setText(ndbm.getGuiConf().getPw());
  }

  private void disableDbInfo(final boolean disable) {
    dbDatabase.setDisable(disable);
    dbUser.setDisable(disable);
    dbPassword.setDisable(disable);
  }
}
