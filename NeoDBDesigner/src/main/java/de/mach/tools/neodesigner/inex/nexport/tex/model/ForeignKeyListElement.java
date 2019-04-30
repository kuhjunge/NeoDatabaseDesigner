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


/** Klasse für die Listeneinträge aller Fremdschlüssel in PDF
 *
 * @author cd
 *         <p>
 *         !!! Wichtig für Apace Velocity muss diese Klasse umbedingt public bleiben !!! */
public class ForeignKeyListElement implements Comparable<ForeignKeyListElement> {

  private final String foreignkeyName;
  private final String tableName;
  private final String refTableName;
  private final String columnName;
  private final Integer order;

  ForeignKeyListElement(final String foreignkeyName, final String tableName, final String refTableName,
                        final String columnName, final Integer order) {
    this.foreignkeyName = foreignkeyName.replace("_", "\\_");
    this.tableName = tableName;
    this.refTableName = refTableName;
    this.columnName = columnName;
    this.order = order;
  }

  @Override
  public int compareTo(final ForeignKeyListElement o) {
    int ret;
    if (getSortValue() == 0 || o.getSortValue() == 0) {
      ret = (getCompleteForeignkeyName() + getOrder())
          .compareToIgnoreCase(o.getCompleteForeignkeyName() + o.getOrder());
    }
    else {
      ret = Integer.compare(getSortValue(), o.getSortValue());
    }
    return ret;
  }

  @Override
  public boolean equals(final Object obj) {
    return obj instanceof ForeignKeyListElement
           && getCompleteForeignkeyName().equals(((ForeignKeyListElement) obj).getCompleteForeignkeyName())
           && getColumnName().equals(((ForeignKeyListElement) obj).getColumnName());
  }

  /** @return the columnName */
  public String getColumnName() {
    return columnName;
  }

  /** @return the foreignkeyName */
  private String getCompleteForeignkeyName() {
    return foreignkeyName;
  }

  /** @return the foreignkeyName */
  public String getForeignkeyName() {
    return getOrder() == 1 ? foreignkeyName : "";
  }

  /** @return the order */
  public Integer getOrder() {
    return order;
  }

  /** @return the refTableName */
  public String getRefTableName() {
    return getOrder() == 1 ? refTableName : "";
  }

  private int getSortValue() {
    try {
      return Integer.parseInt(getCompleteForeignkeyName().trim().substring(3)) * 100 + getOrder();
    }
    catch (final NumberFormatException e) {
      return 0;
    }
  }

  /** @return the tableName */
  public String getTableName() {
    return getOrder() == 1 ? tableName : "";
  }

  @Override
  public int hashCode() {
    return (getCompleteForeignkeyName() + getColumnName()).hashCode();
  }
}
