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

import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.core.nexport.pdf.LoadPdfConfiguration;
import de.mach.tools.neodesigner.core.nexport.pdf.ModelPathManager;
import de.mach.tools.neodesigner.core.nexport.pdf.ModelPrinter;
import de.mach.tools.neodesigner.ui.GuiUtil;
import de.mach.tools.neodesigner.ui.Strings;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.HostServices;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class ModelPrinterController {
  private static final Logger LOG = Logger.getLogger(ModelPrinterController.class.getName());
  private final ModelPathManager mpm = new ModelPathManager();
  private final DirectoryChooser directoryChooser = new DirectoryChooser();
  private Stage stage;
  private final List<Table> tables;
  private HostServices hostServices;

  /**
   * Startet das Fenster in dem der PDF Creator konfiguriert werden kann.
   *
   * @param t
   *          Datenquelle des PDFs
   * @param primaryStage
   *          Stage
   * @param config
   *          Config Interface aus dem die Pfade geladen werden
   * @param hs
   *          Hostservices zum PDF öffnen
   */
  static void startPdfCreator(final List<Table> t, final Window primaryStage, final LoadPdfConfiguration config,
      final HostServices hs) {
    Parent root;
    ModelPrinterController mpc;
    try {
      final FXMLLoader fxmlLoader = new FXMLLoader(t.getClass().getResource(Strings.FXML_PDFEDITOR));
      mpc = new ModelPrinterController(t);
      fxmlLoader.setController(mpc);
      root = fxmlLoader.load();
      final Stage stage = new Stage();
      stage.setTitle(Strings.TITLE_PDF_EXPORT);
      stage.setScene(new Scene(root));
      stage.initModality(Modality.WINDOW_MODAL);
      stage.initOwner(primaryStage.getScene().getWindow());
      stage.show();
      stage.getIcons().add(new Image(ModelPrinterController.class.getResourceAsStream(Strings.FXML_ICON)));
      mpc.setup(config, hs);
    } catch (final IOException e) {
      ModelPrinterController.LOG.log(Level.SEVERE, e.toString(), e);
    }
  }

  @FXML
  private Label details;

  @FXML
  private Label labelMikTexPfad;

  @FXML
  private Label labelPdfOutput;

  @FXML
  private Button btnCreatePdf;

  @FXML
  private Button btnChooseMikTex;

  @FXML
  private TextField pdfTitle;

  @FXML
  private ProgressBar progressBarPdf;

  @FXML
  private RadioMenuItem loescheTemp;

  private ModelPrinterController(final List<Table> t) {
    tables = t;
  }

  /**
   * Setzt die default Werte im Interface.
   *
   * @param config
   *          bekommt das Config interface übergeben
   * @param hs
   *          Hostservices zum PDF öffnen
   *
   */
  private void setup(final LoadPdfConfiguration config, final HostServices hs) {
    mpm.init(config);
    labelMikTexPfad.setText(mpm.getMikTexPath());
    labelPdfOutput.setText(mpm.getPdfPath());
    pdfTitle.setText(mpm.getPdfTitle());
    hostServices = hs;
    GuiUtil.repeatFocus(btnCreatePdf);
    btnCreatePdf.getScene().setOnKeyPressed(keyEvent -> {
      if (keyEvent.getCode() == KeyCode.ENTER && btnCreatePdf.isFocused()) {
        btnCreatePdf.fire();
        keyEvent.consume();
      }
    });
  }

  /**
   * Startet die Verarbeitung des PDFs durch starten des PDF Tasks Setzt die
   * Verbindung zur Gui für Output Label und Ladebalken.
   *
   * @param event
   *          event
   */
  @FXML
  protected void handleSubmitButtonAction(final ActionEvent event) {
    if (mpm.isEveryFileSet()) {
      mpm.setPdfTitle(pdfTitle.getText());
      mpm.save();
      mpm.setDeleteTemp(loescheTemp.isSelected());
      final Task<Void> task = createPdfTask();
      progressBarPdf.progressProperty().bind(task.progressProperty());
      details.textProperty().bind(task.messageProperty());
      new Thread(task).start();
    } else {
      warn(Strings.PATH_ERROR, Strings.PATH_ERROR_DETAIL);
    }
  }

  @FXML
  protected void openPathMikTex(final ActionEvent event) {
    final File checkPath = openDirChooser(mpm.getMikTexPath());
    if (mpm.setMikTexPath(checkPath)) {
      labelMikTexPfad.setText(mpm.getMikTexPath());
    } else {
      warn(Strings.PATH_ERROR, Strings.PATH_ERROR_NOT_VALID);
    }
  }

  @FXML
  protected void openPdfOutput(final ActionEvent event) {
    String f = mpm.getPdfFile().getParent();
    if (f == null) {
      f = mpm.getPdfFile().getName();
    }
    final File checkPath = openDirChooser(f);
    if (mpm.setPdfFile(checkPath)) {
      labelPdfOutput.setText(mpm.getPdfPath());
    } else {
      warn(Strings.PATH_ERROR, Strings.PATH_ERROR_NOT_VALID);
    }
  }

  /**
   * Verzeichnis Auswahldialog.
   *
   * @param s
   *          String des ausgewählten Pfades
   * @return File des Pfades
   */
  private File openDirChooser(final String s) {
    final File f = new File(s);
    if (f.exists()) {
      directoryChooser.setInitialDirectory(f);
    }
    return directoryChooser.showDialog(stage);
  }

  private void warn(final String title, final String message) {
    final Alert alert = new Alert(AlertType.INFORMATION);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }

  @FXML
  protected void explorerConfigPath(final ActionEvent event) {
    try {
      mpm.openPath(mpm.getConfigPath());
    } catch (final IOException e) {
      warn(Strings.PATH_ERROR, Strings.PATH_ERROR_NOT_VALID);
    }
  }

  @FXML
  protected void explorerTempPath(final ActionEvent event) {
    try {
      mpm.openPath(mpm.getOutputPath());
    } catch (final IOException e) {
      warn(Strings.PATH_ERROR, Strings.PATH_ERROR_NOT_VALID);
    }
  }

  @FXML
  protected void explorerMikTexPath(final ActionEvent event) {
    try {
      mpm.openPath(mpm.getMikTexPath());
    } catch (final IOException e) {
      warn(Strings.PATH_ERROR, Strings.PATH_ERROR_NOT_VALID);
    }
  }

  @FXML
  protected void explorerPdfPath(final ActionEvent event) {
    try {
      mpm.openPath(mpm.getPdfPath());
    } catch (final IOException e) {
      warn(Strings.PATH_ERROR, Strings.PATH_ERROR_NOT_VALID);
    }
  }

  /**
   * Task zur Erstellung des PDFs.
   *
   * @return Task
   */
  private Task<Void> createPdfTask() {
    return new Task<Void>() {
      static final int MAX = 10;

      @Override
      public void run() {
        updateMessage(Strings.UPDATETEXT_CREATEPDF);

        btnCreatePdf.setDisable(true);
        updateProgress(MAX * 0.0, MAX);
        try {
          final ModelPrinter mp = new ModelPrinter(mpm);
          updateProgressMessage(0.1, Strings.UPDATETEXT_TEMPLATE);
          mp.verarbeiteTemplate(tables, pdfTitle.getText());
          updateProgressMessage(0.2, Strings.UPDATETEXT_RUN1);
          mp.erstellePdf();
          updateProgressMessage(0.3, Strings.UPDATETEXT_CREATEINDEX);
          mp.erstelleIndex();
          updateProgressMessage(0.6, Strings.UPDATETEXT_RUN2);
          mp.erstellePdf();
          updateProgressMessage(0.8, Strings.UPDATETEXT_COPYPDF);
          mp.oeffnePdf(hostServices);
          updateProgressMessage(0.9, Strings.UPDATETEXT_CLEAN);
          mpm.clean();
          updateProgressMessage(MAX, Strings.UPDATETEXT_FINISHED);
        } catch (IOException | InterruptedException e) {
          updateProgressMessage(0, String.format(Strings.UPDATETEXT_ERR, getMessage()));
          mpm.clean();
          ModelPrinterController.LOG.log(Level.SEVERE, e.toString(), e);
        }
        btnCreatePdf.setDisable(false);
      }

      public void updateProgressMessage(final double progress, final String msg) {
        updateProgress(MAX * progress, MAX);
        updateMessage(msg);
      }

      @Override
      protected Void call() throws Exception {
        return null;
      }
    };
  }
}
