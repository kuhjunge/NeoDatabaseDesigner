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

package de.mach.tools.neodesigner.ui.graph;


import java.util.Map;
import java.util.Map.Entry;
import javafx.embed.swing.SwingNode;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.Window;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;

import de.mach.tools.neodesigner.core.Model;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.core.graph.GraphModel;


/** Bindet den Graphen in die GUI ein.
 *
 * @author Chris Deter */
public class DisplayGraph {
  private static Viewer viewer = null;

  static {
    System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
  }

  public void showTable(final Window w, final Table t) {
    final GraphModel gm = new GraphModel(getGraph(t.getName()));
    showGraph(w, gm.getGraphForTable(t), "Details: " + t.getName(), true, gm.getLegend());
  }

  public void showCategory(final Window w, final Model m, final String category) {
    final GraphModel gm = new GraphModel(getGraph("CategoryView"));
    showGraph(w, gm.getCategoryGraphs(m.getAllTables(), category), "Category View: " + category, true, gm.getLegend());
  }

  public void showCompleteDatamodel(final Window w, final Model m) {
    final GraphModel gm = new GraphModel(getGraph("All"));
    showGraph(w, gm.getAllGraphs(m.getAllTables(), 10), "All!", false, gm.getLegend());
  }

  private void showGraph(final Window w, final Graph g, final String windowTitle, final boolean isAutoLayout,
                         final Map<String, Color> legend) {
    final SwingNode swingNode = new SwingNode();
    DisplayGraph.createSwingContent(swingNode, g, isAutoLayout);
    setUpWindow(w, windowTitle, swingNode, isAutoLayout, legend);
  }

  private void setUpWindow(final Window w, final String name, final SwingNode swingNode, final boolean isAutoLayout,
                           final Map<String, Color> legend) {
    final HBox hbox = new HBox();
    final ScrollPane crPan = new ScrollPane();
    final VBox vbox = new VBox();
    vbox.setPadding(new Insets(10, 10, 10, 10));
    hbox.getChildren().add(getGraphContainer(swingNode));
    crPan.setContent(vbox);
    hbox.getChildren().add(crPan);
    vbox.getChildren().add(createAutoLayoutCheckbox(isAutoLayout));
    crPan.setMinWidth(200);
    setLegend(legend, vbox);
    final Stage stage = new Stage();
    stage.initOwner(w.getScene().getWindow());
    stage.setTitle(name);
    stage.setScene(new Scene(hbox, 1280, 900));
    stage.setOnCloseRequest(event -> DisplayGraph.viewer.close());
    stage.show();
  }

  private void setLegend(final Map<String, Color> legend, final VBox vbox) {
    for (final Entry<String, Color> str : legend.entrySet()) {
      final HBox container = new HBox();
      container.setAlignment(Pos.CENTER_LEFT);
      final Label l = new Label();
      l.setText(str.getKey());
      l.setPadding(new Insets(0, 5, 1, 5));
      final Circle col = new Circle();
      col.setRadius(7);
      col.setFill(str.getValue());
      container.getChildren().add(col);
      container.getChildren().add(l);
      vbox.getChildren().add(container);
    }
  }

  private CheckBox createAutoLayoutCheckbox(final boolean isAutoLayout) {
    final CheckBox chk = new CheckBox("Auto Layout");
    chk.selectedProperty().set(isAutoLayout);
    chk.selectedProperty().addListener(event -> {
      if (chk.isSelected()) {
        DisplayGraph.viewer.enableAutoLayout();
      }
      else {
        DisplayGraph.viewer.disableAutoLayout();
      }
    });
    return chk;
  }

  private StackPane getGraphContainer(final SwingNode swingNode) {
    final StackPane pane = new StackPane();
    pane.getChildren().add(swingNode);
    pane.setPrefWidth(1920);
    pane.setPrefHeight(1080);
    return pane;
  }

  private static void createSwingContent(final SwingNode swingNode, final Graph g, final boolean enableAutoLayout) {
    SwingUtilities.invokeLater(() -> {
      DisplayGraph.viewer = new Viewer(g, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
      final View view = DisplayGraph.viewer.addDefaultView(false);
      if (enableAutoLayout) {
        DisplayGraph.viewer.enableAutoLayout();
      }
      else {
        DisplayGraph.viewer.disableAutoLayout();
      }
      DisplayGraph.viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.EXIT);
      swingNode.setContent((JComponent) view);
    });
  }

  private Graph getGraph(final String name) {
    final Graph graph = new MultiGraph(name);
    graph.addAttribute("ui.quality");
    graph.addAttribute("ui.antialias");
    return graph;
  }
}
