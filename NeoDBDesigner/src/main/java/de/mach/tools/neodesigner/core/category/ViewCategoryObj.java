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

package de.mach.tools.neodesigner.core.category;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


/** Klasse zum Verwalten von Kategorien in der GUI.
 *
 * @author cd */
public class ViewCategoryObj {
  private final CategoryObj data;
  private final StringProperty id = new SimpleStringProperty();
  private final StringProperty name = new SimpleStringProperty();

  /** Konstruktor.
   *
   * @param categoryObj Original Obj */
  public ViewCategoryObj(final CategoryObj categoryObj) {
    data = categoryObj;
    id.set(data.getCategory());
    name.set(data.getCategoryText());
    id.addListener((obs, ov, nv) -> data.setCategory(nv));
    name.addListener((obs, ov, nv) -> data.setCategoryText(nv));
  }

  public StringProperty getId() {
    return id;
  }

  public StringProperty getName() {
    return name;
  }

  public String getCategory() {
    return data.getCategory();
  }

  public String getCategoryText() {
    return data.getCategoryText();
  }

  @Override
  public String toString() {
    return data.toString();
  }
}
