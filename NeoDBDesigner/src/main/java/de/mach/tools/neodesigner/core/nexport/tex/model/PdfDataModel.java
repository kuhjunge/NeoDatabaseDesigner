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

package de.mach.tools.neodesigner.core.nexport.tex.model;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

import de.mach.tools.neodesigner.core.nexport.tex.util.PageComparator;
import de.mach.tools.neodesigner.database.Strings;


public class PdfDataModel {
  private final Map<String, Map<String, Map<String, List<Column>>>> sections;
  private final Properties headlines;
  private final PageComparator pageComparator = new PageComparator();
  private final List<ForeignKeyListElement> fks = new ArrayList<>();

  public PdfDataModel(final Properties headlines) {
    sections = new HashMap<>();
    this.headlines = headlines;
  }

  public List<String> getSortedSections() {
    final List<String> result = new ArrayList<>(sections.keySet());
    result.sort(Comparator.comparingInt(Integer::valueOf));
    return result;
  }

  public String getSectionOrPageTitle(final String section) {
    return headlines.getProperty("section." + section);
  }

  public List<String> getSortedPages(final String section) {
    final List<String> result = new ArrayList<>(sections.get(section).keySet());
    result.sort(pageComparator);
    return result;
  }

  public List<String> getSortedTables(final String page) {
    final String section = page.split(Strings.COMMA)[0];
    final List<String> result = new ArrayList<>(sections.get(section).get(page).keySet());
    result.sort(String::compareTo);
    return result;
  }

  public List<Column> getPrimaryKeyColumns(final String page, final String table) {
    final String section = page.split(Strings.COMMA)[0];
    return sections.get(section).get(page).get(table).stream().filter(Column::isPrimaryKey)
        .collect(Collectors.toList());
  }

  public List<Column> getNonPrimaryKeyColumns(final String page, final String table) {
    final String section = page.split(Strings.COMMA)[0];
    return sections.get(section).get(page).get(table).stream().filter(column -> !column.isPrimaryKey())
        .collect(Collectors.toList());
  }

  public Column getColumn(final String page, final String tableName, final String columnName) {
    final String section = page.split(Strings.COMMA)[0];
    if (!sections.containsKey(section)) {
      sections.put(section, new HashMap<>());
    }
    if (!sections.get(section).containsKey(page)) {
      sections.get(section).put(page, new HashMap<>());
    }
    if (!sections.get(section).get(page).containsKey(tableName)) {
      sections.get(section).get(page).put(tableName, new ArrayList<>());
    }
    final Optional<Column> optColumn = sections.get(section).get(page).get(tableName).stream()
        .filter(column -> columnName.equals(column.getName())).findFirst();
    if (!optColumn.isPresent()) {
      final Column column = new Column(columnName);
      sections.get(section).get(page).get(tableName).add(column);
      return column;
    }
    else {
      return optColumn.get();
    }
  }

  public void addFkToList(final String foreignkeyName, final String tableName, final String refTableName,
                          final String columnName, final Integer order) {
    fks.add(new ForeignKeyListElement(foreignkeyName, tableName, refTableName, columnName, order));
  }

  public List<ForeignKeyListElement> getFkList() {
    Collections.sort(fks);
    return fks;
  }
}
