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

import de.mach.tools.neodesigner.ui.controller.MainController;


/** Startet die Haupt GUI.
 *
 * @author Chris Deter */
public class Starter extends Application {
  /** Startet die Anwendung. <br/>
   * 
   * @param args Die Argumente */
  public static void main(final String[] args) {
    if (args.length > 0) {
      Headless.start(args);
    }
    else {
      Application.launch(args);
    }
  }

  @Override
  public void start(final Stage primaryStage) throws Exception {
    startMainStage(primaryStage);
  }

  Scene startMainStage(final Stage primaryStage) throws IOException {
    MainController neoContrl = new MainController();
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
    // Hier Code einfügen, damit Neo4J auch wirklich terminiert
    // Datenbankverbindung wird in NeoModule geschlossen
  }
}
