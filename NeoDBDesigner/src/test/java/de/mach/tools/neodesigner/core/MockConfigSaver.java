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

package de.mach.tools.neodesigner.core;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.mach.tools.neodesigner.core.datamodel.Table;


public class MockConfigSaver implements ConfigSaver {

  public MockConfigSaver() {

  }

  public MockConfigSaver(Map<String, String> init) {
    data.putAll(init);
  }

  private Map<String, String> data = new HashMap<>();

  @Override
  public String getValue(String key) {
    return data.get(key);
  }

  @Override
  public void load() {
    // Nichts zu tun, da nicht von der Festplatte geladen werden soll
  }

  @Override
  public void save() {
    // Nichts zu tun, da nicht auf der Festplatte gespeichert werden soll
  }

  @Override
  public void setValue(String key, String value) {
    data.put(key, value);
  }

  @Override
  public Map<String, String> getValueMap() {
    return new HashMap<>(data);
  }

  @Override
  public void clearValues() {
    data.clear();
  }

  public void generateTableCategories(List<Table> lt) {
    Map<String, String> categories = new HashMap<>();
    for (Table t : lt) {

      data.put(Strings.SECTION + t.getCategory(), t.getCategory());
    }
  }
}
