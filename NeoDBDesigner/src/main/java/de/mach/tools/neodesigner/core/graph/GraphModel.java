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

package de.mach.tools.neodesigner.core.graph;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.scene.paint.Color;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;

import de.mach.tools.neodesigner.core.ConfigSaverImpl;
import de.mach.tools.neodesigner.core.category.CategoryTranslator;
import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.ui.Strings;


/** Generiert einen Graphen.
 *
 * @author Chris Deter */
public class GraphModel {
  private static String cssStyleSheet = "node {size: 10px; z-index: 1; text-alignment: under;"
                                        + " text-offset: 0px, 4px; text-color: #444; stroke-mode: plain; stroke-color: #333; shape: circle;}"
                                        + " node.table {size: 30px; fill-color: white; size-mode: fit; shape: rounded-box; "
                                        + " stroke-mode: plain; padding: 3px, 2px; text-alignment: center; }"
                                        + "node.field { size: 20px; fill-color: green; text-background-mode: plain;} "
                                        + "node.index { size: 20px; fill-color: red; text-background-mode: plain;} "
                                        + "node.primary { size: 20px; fill-color: yellow; text-background-mode: plain;}"
                                        + "edge { shape: line; size: 1px; fill-color: navy; arrow-size: 5px, 5px; z-index: 0; }"
                                        + "edge.doubleRel {fill-color: #FF0000;}";
  private static String cssProperty = "ui.stylesheet";

  private final Graph graph;
  private final GraphColours colour = new GraphColours();
  private final List<Table> lt = new ArrayList<>();

  public GraphModel(final Graph g) {
    graph = g;
  }

  /** Gibt einen Graph für alle Tabellen in der übergebenen liste zurück.
   *
   * @param tables die Tabellen für den graphen
   * @param precompute wie oft der Graph vor dem Anzeigen vorformatiert werden soll
   * @return den Graphen */
  public Graph getAllGraphs(final List<Table> tables, final int precompute) {
    final Layout layout = new SpringBox(false);
    lt.addAll(tables);
    graph.addSink(layout);
    layout.addAttributeSink(graph);
    getGraphForTableList(graph, tables, colour);
    for (int j = 0; j < precompute; j++) {
      layout.shake();
      for (int i = 0; i < precompute; i++) {
        layout.compute();
      }
    }
    return graph;
  }

  private Graph getGraphForTableList(final Graph g, final List<Table> tables, final GraphColours colour) {
    lt.addAll(tables);
    for (final Table t : tables) {
      final Node tblNode = g.addNode(t.getName());
      tblNode.setAttribute(Strings.GRAPHSTREAMUICSSLABEL, t.getName());
      tblNode.addAttribute(Strings.GRAPHSTREAMUICSSCLASS, colour.categoryToColourTransl(t.getCategory()));
    }
    for (final Table t : tables) {
      for (final ForeignKey fk : t.getForeignKeys()) {
        final Table ref = fk.getRefTable();
        if (g.getNode(ref.getName()) != null && g.getEdge(t.getName() + ref.getName()) == null) {
          final Edge e = g.addEdge(t.getName() + ref.getName(), t.getName(), ref.getName(), true);
          e.addAttribute(Strings.GRAPHSTREAMUICSSCLASS, Strings.CLASS_SMALL);
        }
      }
    }
    applyCss();
    return g;
  }

  /** Erstellt einen Graphen und sortiert die Tabellenfelder nach den übergebenen Kategorien.
   *
   * @param tables Tabellen des Graphen
   * @param c die Kategorien mit Semikolon getrennt
   * @return den Graphen */
  public Graph getCategoryGraphs(final List<Table> tables, final String c) {
    lt.addAll(tables);
    final List<Table> all = new ArrayList<>();
    for (final String category : c.split(Strings.SEMICOLON)) {
      colour.giveCategoryColour(category);
      all.addAll(tables.stream().filter(x -> x.getCategory().startsWith(category)).filter(x -> !all.contains(x))
          .collect(Collectors.toList()));
    }
    getGraphForTableList(graph, all, colour);
    return graph;
  }

  /** Gibt den Graphen für eine Tabelle zurück.
   *
   * @param t die Tabelle
   * @return der Graph */
  public Graph getGraphForTable(final Table t) {
    lt.add(t);
    final Node tblNode = graph.addNode(t.getName());
    tblNode.setAttribute(Strings.GRAPHSTREAMUICSSLABEL, t.getName());
    tblNode.addAttribute(Strings.GRAPHSTREAMUICSSCLASS, Strings.CLASS_TABLE);
    for (final ForeignKey fk : t.getForeignKeys()) {
      final Table rt = fk.getRefTable();
      Node n;
      if (graph.getNode(rt.getName()) == null) {
        n = graph.addNode(rt.getName());
        graph.addEdge(t.getName() + rt.getName(), t.getName(), rt.getName(), true);
        n.setAttribute(Strings.GRAPHSTREAMUICSSLABEL, rt.getName());
        n.addAttribute(Strings.GRAPHSTREAMUICSSCLASS, Strings.CLASS_FIELD);
      }
    }
    for (final ForeignKey fk : t.getRefForeignKeys()) {
      final Table rt = fk.getTable();
      Node n;
      if (graph.getNode(rt.getName()) == null) {
        n = graph.addNode(rt.getName());
        n.setAttribute(Strings.GRAPHSTREAMUICSSLABEL, rt.getName());
        n.addAttribute(Strings.GRAPHSTREAMUICSSCLASS, Strings.CLASS_INDEX);
        graph.addEdge(rt.getName() + t.getName(), rt.getName(), t.getName(), true);
      }
      else {
        n = graph.getNode(rt.getName());
        if (graph.getEdge(t.getName() + rt.getName()) != null) {
          n.addAttribute(Strings.GRAPHSTREAMUICSSCLASS, Strings.CLASS_PRIMARY);
        }
        final Edge e = graph.getEdge(rt.getName() + t.getName());
        if (e != null) {
          e.addAttribute(Strings.GRAPHSTREAMUICSSCLASS, Strings.CLASS_DOBULEREL);
        }
      }
    }
    applyCss();
    return graph;
  }

  /** gibt die Legende zurück.
   *
   * @return Map mit Bezeichnung der Kategorie und der Farbe */
  public Map<String, Color> getLegend() {
    final CategoryTranslator ct = new CategoryTranslator(new ConfigSaverImpl(de.mach.tools.neodesigner.core.Strings.SOFTWARENAME,
                                                                             de.mach.tools.neodesigner.core.Strings.CATEGORYFILE));
    ct.getCategoriesFromTable(lt);
    final Map<String, Color> ret = new LinkedHashMap<>();
    final List<GraphColourObj> gcol = colour.getColour();
    for (final GraphColourObj cat : gcol) {
      ret.put(ct.translateNumberIntoName(cat.getCategory()), cat.getColour(gcol.size()));
    }
    return ret;
  }

  private void applyCss() {
    GraphModel.addNodeCss(graph, colour);
  }

  /** fügt das CSS zu einem Graphen hinzu.
   *
   * @param g der Graph
   * @return den Graphen mit CSS */
  private static Graph addNodeCss(final Graph g, final GraphColours gc) {
    g.addAttribute(GraphModel.cssProperty, GraphModel.cssStyleSheet + gc.getColoursAsCss());
    return g;
  }
}
