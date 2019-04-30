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

package de.mach.tools.neodesigner.inex.nexport.tex.model;


import java.util.regex.Matcher;
import java.util.regex.Pattern;


/** @author lw */
public class Column {
  private final String name;
  private String domain;
  private boolean nullable;
  private boolean primaryKey;
  private boolean foreignKey;
  private String foreignKeyTableName;
  private String indexName;

  Column(final String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  // Wird aufgerufen über Apache Velocity
  public String getDomain() {
    return domain;
  }

  public void setDomain(final String domain) {
    this.domain = domain;
  }

  // Wird aufgerufen über Apache Velocity
  public boolean isNullable() {
    return nullable;
  }

  public void setNullable(final boolean nullable) {
    this.nullable = nullable;
  }

  public boolean isPrimaryKey() {
    return primaryKey;
  }

  public void setPrimaryKey(final boolean primaryKey) {
    this.primaryKey = primaryKey;
  }

  // Wird aufgerufen über Apache Velocity
  public boolean isForeignKey() {
    return foreignKey;
  }

  public void setForeignKey(final boolean foreignKey) {
    this.foreignKey = foreignKey;
  }

  // Wird aufgerufen über Apache Velocity
  public String getForeignKeyTableName() {
    return foreignKeyTableName;
  }

  public void setForeignKeyTableName(final String foreignKeyTableName) {
    this.foreignKeyTableName = foreignKeyTableName;
  }

  // Wird aufgerufen über Apache Velocity
  public String getIndexName() {
    return indexName;
  }

  // Wird aufgerufen über Apache Velocity
  public boolean hasIndex() {
    return indexName != null && !"".equals(indexName);
  }

  public void addIndexColumn(final String indexName, final int position) {
    this.indexName = getIndexShortNameForColumn(position, indexName);
  }

  private String getIndexShortNameForColumn(final int position, final String indexName) {
    if (position >= 0) {
      final Pattern pattern = Pattern.compile("X(IE[0-9]+|AK[0-9]+).*");
      final Matcher matcher = pattern.matcher(indexName);
      if (matcher.matches()) {
        final String shortName = matcher.group(1);
        return shortName + "." + position;
      }
    }
    return "";
  }
}
