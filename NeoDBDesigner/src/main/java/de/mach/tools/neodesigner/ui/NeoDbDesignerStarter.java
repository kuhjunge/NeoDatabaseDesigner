package de.mach.tools.neodesigner.ui;

import de.mach.tools.neodesigner.core.Model;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.viewimpl.ViewForeignKey;
import de.mach.tools.neodesigner.core.datamodel.viewimpl.ViewIndex;
import de.mach.tools.neodesigner.core.datamodel.viewimpl.ViewTable;
import de.mach.tools.neodesigner.ui.controller.FkRelEditorController;
import de.mach.tools.neodesigner.ui.controller.IndexRelEditorController;
import de.mach.tools.neodesigner.ui.controller.NeoDbDesignerController;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * Startet die Haupt GUI.
 *
 * @author Chris Deter
 *
 */
public class NeoDbDesignerStarter extends Application {
  private static final Logger LOG = Logger.getLogger(NeoDbDesignerStarter.class.getName());

  /**
   * Startet die Anwendung.
   *
   * @param args
   */
  public static void main(final String[] args) {
    Application.launch(args);
  }

  @Override
  public void start(final Stage primaryStage) throws Exception {
    final NeoDbDesignerController neoContrl = new NeoDbDesignerController();
    final FXMLLoader loader = new FXMLLoader();
    loader.setLocation(getClass().getResource(Strings.FXML_NEDDBDESIGNER));
    loader.setController(neoContrl);
    final Parent root = loader.load();
    final Scene scene = new Scene(root);
    primaryStage.setTitle(Strings.SOFTWARENAME);
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  /**
   * Startet den Dialog zum Bearbeiten eines FKs.
   *
   * @param t
   *          die Tabelle
   * @param m
   *          der Foreignkey
   * @param ndbm
   *          das Model
   * @param primaryStage
   *          das Fenster
   */
  public static void startFkRelEditor(final ViewTable t, final ViewForeignKey m, final Model ndbm,
      final Window primaryStage) {
    Parent root;
    FkRelEditorController relContrl;
    if (m != null) {
      try {
        final FXMLLoader fxmlLoader = new FXMLLoader(t.getClass().getResource(Strings.FXML_FKRELEDITOR));
        relContrl = new FkRelEditorController();
        relContrl.setData(t, m, ndbm);
        fxmlLoader.setController(relContrl);
        root = fxmlLoader.load();
        final Stage stage = new Stage();
        stage.setTitle(Strings.TITLE_RELEDITOR);
        stage.setScene(new Scene(root));
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(primaryStage.getScene().getWindow());
        stage.show();
      } catch (final IOException e) {
        NeoDbDesignerStarter.LOG.log(Level.SEVERE, e.toString(), e);
      }
    }
  }

  /**
   * Der Starter für das Index Bearbeitungsfenster.
   *
   * @param t
   *          Die Tabelle
   * @param i
   *          der Index
   * @param primaryStage
   *          das Fenster
   * @param tvi
   *          Alle Indizes
   * @param isNew
   *          boolean ob der Index neu erstellt ist
   */
  public static void startIndexRelEditor(final ViewTable t, final ViewIndex i, final Window primaryStage,
      final TableView<ViewIndex> tvi, final boolean isNew) {
    if (i != null) {
      if (i.getType() == Index.Type.XAK || i.getType() == Index.Type.XIE || i.getType() == Index.Type.XPK) {
        NeoDbDesignerStarter.startEditor(t, i, primaryStage, tvi, isNew);
      } else {
        NeoDbDesignerStarter.openInformation();
      }
    }
  }

  /**
   * öffnet einen Alert Dialog
   */
  private static void openInformation() {
    final Alert alert = new Alert(AlertType.INFORMATION);
    alert.setTitle(Strings.ALTITLE_RELEDITOR);
    alert.setHeaderText(Strings.ALTEXT_RELEDITOR);
    alert.showAndWait();
  }

  /**
   * Startet den Index Editor (nachdem die Prüfung ob der Index kompatibel ist,
   * abgeschlossen ist)
   *
   * @param t
   * @param i
   * @param primaryStage
   * @param tvi
   * @param isNew
   */
  private static void startEditor(final ViewTable t, final ViewIndex i, final Window primaryStage,
      final TableView<ViewIndex> tvi, final boolean isNew) {
    IndexRelEditorController relContrl;
    Parent root;
    try {
      final FXMLLoader fxmlLoader = new FXMLLoader(t.getClass().getResource(Strings.FXML_INDEXRELEDITOR));
      relContrl = new IndexRelEditorController();
      relContrl.setData(t, i, tvi, isNew);
      fxmlLoader.setController(relContrl);
      root = fxmlLoader.load();
      final Stage stage = new Stage();
      stage.setTitle(Strings.TITLE_RELEDITOR);
      stage.setScene(new Scene(root));
      stage.initModality(Modality.WINDOW_MODAL);
      stage.initOwner(primaryStage.getScene().getWindow());
      stage.show();
    } catch (final IOException e) {
      NeoDbDesignerStarter.LOG.log(Level.SEVERE, e.toString(), e);
    }
  }
}
