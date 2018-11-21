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

package de.mach.tools.neodesigner.ui;


import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import de.mach.tools.neodesigner.ui.controller.NeoDbDesignerController;


/** Startet die Haupt GUI.
 *
 * @author Chris Deter */
public class NeoDbDesignerStarter extends Application {
  /** Startet die Anwendung.
   *
   * @param args Die Argumente */
  public static void main(final String[] args) {
    String target = "";
    if (args.length > 2 && (args[0].equals("pdf") || args[0].equals("tex"))) {
      final String path = args[1]; // CSV Pfad
      final String title = args[2]; // Titel des Dokuments
      String author = "";
      if (args.length > 3) {
        author = args[3]; // Optional Export Zielpfad
      }
      if (args.length > 4) {
        target = args[4]; // Optional Export Zielpfad
      }
      new Headless(path, target, title, author, args[0].equals("pdf"));
    }
    else {
      Application.launch(args);
    }
  }

  private NeoDbDesignerController neoContrl;

  @Override
  public void start(final Stage primaryStage) throws Exception {
    startMainStage(primaryStage);
  }

  Scene startMainStage(final Stage primaryStage) throws IOException {
    neoContrl = new NeoDbDesignerController();
    final FXMLLoader loader = new FXMLLoader();
    loader.setLocation(getClass().getResource(Strings.FXML_NEDDBDESIGNER));
    loader.setController(neoContrl);
    final Parent root = loader.load();
    final Scene scene = new Scene(root);
    primaryStage.setTitle(Strings.SOFTWARENAME);
    primaryStage.setScene(scene);
    primaryStage.getIcons().add(new Image(getClass().getResourceAsStream(Strings.FXML_ICON)));
    primaryStage.show();
    neoContrl.setHostServices(getHostServices());
    return scene;
  }

  @Override
  public void stop() {
    neoContrl.close();
  }
}
