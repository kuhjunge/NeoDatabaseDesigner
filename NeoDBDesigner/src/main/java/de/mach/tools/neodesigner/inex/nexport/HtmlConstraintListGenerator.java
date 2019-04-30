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

package de.mach.tools.neodesigner.inex.nexport;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import de.mach.tools.neodesigner.core.Strings;
import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.inex.Generator;


public class HtmlConstraintListGenerator implements Generator {
  private static String TDTAG = "  <td>%s</td>";
  private static String CS_HEADER = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n"
                                    + "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n"
                                    + "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + "<head>\n"
                                    + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n"
                                    + "<meta name=\"generator\" content=\"Adobe RoboHelp 2017\" />\n"
                                    + "<title>MACH Software - Constraints</title>\n"
                                    + "<link rel=\"StyleSheet\" href=\"Benutzerhandbuch.css\" type=\"text/css\" />\n"
                                    + "<script language=\"JavaScript\" title=\"BSSC Special Effects\" src=\"ehlpdhtm.js\" \n"
                                    + "\t\t type=\"text/javascript\"></script>\n" + "</head>\n" + "\n" + "<body>\n"
                                    + "<p class=\"ￜberschrift1_Neu\">MACH Software - Constraints</p>\n"
                                    + "<p style=\"margin-top: 0; margin-bottom: 0;\">&#160;</p>\n"
                                    + "<p><font style=\"font-size: 10pt;\" color=\"#000000\" size=\"2\">Im Rahmen einer \n"
                                    + " Fehleranalyse können die folgenden im Datenmodell abgebildeten Constraints \n"
                                    + " helfen.</font></p>";
  private static String WEB_HEADER = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n"
                                     + "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n"
                                     + "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n<head>\n"
                                     + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n"
                                     + "<meta name=\"date\" content=\"03 22, 2004 11:36:08 AM\" />\n"
                                     + "<meta name=\"topic-status\" content=\"In Progress\" />\n"
                                     + "<meta name=\"topic-priority\" content=\"0\" />\n"
                                     + "<meta name=\"topic-time-estimate\" content=\"0\" />\n"
                                     + "<meta name=\"topic-comment\" content=\"\" />\n"
                                     + "<meta name=\"topic-id\" content=\"Constraints\" />\n"
                                     + "<meta name=\"generator\" content=\"Adobe RoboHelp 2017\" />\n"
                                     + "<title>Systemtechnische Grundlagen - Constraints</title>\n"
                                     + "<link rel=\"StyleSheet\" href=\"../../../m1web.css\" type=\"text/css\" />\n</head>\n\n"
                                     + "<body>\n<h1>Systemtechnische Grundlagen <img src=\"../../../01_pfeil_rechts.gif\" \n"
                                     + "\t\t\t\t\t\t\t\t\t alt=\"\" style=\"border: none;\" width=\"24\" \n"
                                     + "\t\t\t\t\t\t\t\t\t height=\"13\" border=\"0\" /> Constraints</h1>\n"
                                     + "<p>Im Rahmen einer Fehleranalyse können die folgenden im Datenmodell abgebildeten \n"
                                     + " Constraints helfen.</p>";
  private boolean isWeb10;

  public HtmlConstraintListGenerator(boolean isWeb10) {
    this.isWeb10 = isWeb10;
  }

  @Override
  public String generate(List<Table> tableList) {
    StringBuilder sb = new StringBuilder();
    // HTML Header
    if (isWeb10) {
      sb.append(WEB_HEADER);
    }
    else {
      sb.append(CS_HEADER);
    }
    sb.append("<div class=\"Section1\">\n"
              + "<table class=\"MsoTableGrid\" style=\"border-collapse: collapse;\" cellspacing=\"0\">");
    sb.append(Strings.EOL);
    sb.append("<tr>	<th>Constraint</th><th>Tabelle</th><th>Referenz Tabelle</th><th>Spalte</th><th>Ordnungsziffer</th></tr>");
    sb.append(Strings.EOL);
    // Content Table
    Map<String, ForeignKey> data = new HashMap<>();
    for (Table t : tableList) {
      for (ForeignKey fk : t.getForeignKeys()) {
        data.put(fk.getName(), fk);
      }
    }
    SortedSet<String> keys = new TreeSet<>(data.keySet());
    for (String key : keys) {
      ForeignKey fk = data.get(key);
      sb.append("<tr>");
      sb.append(String.format(TDTAG, fk.getName()));
      sb.append(String.format(TDTAG, fk.getTable().getName()));
      sb.append(String.format(TDTAG, fk.getRefTable().getName()));
      sb.append(String.format(TDTAG, fk.getName()));
      sb.append(String.format(TDTAG, fk.getName()));
      sb.append("</tr>");
      sb.append(Strings.EOL);
    }
    // HTML Footer
    sb.append("</table>\n</div>\n</body>\n</html>");
    sb.append(Strings.EOL);
    return sb.toString();
  }
}
