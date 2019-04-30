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

package de.mach.tools.neodesigner.core.datamodel.impl;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.FieldList;


/** Implementation des FieldList Interfaces. Im Prinzip eine Liste mit fester Sortierung. */
public class OrderedFieldList implements FieldList {
  private List<Field> fields;
  private Map<Integer, Integer> fieldRefOrder;
  private static final Logger LOG = Logger.getLogger(OrderedFieldList.class.getName());

  /** Konstruktor
   * 
   * @param startList eine Liste, die als Basis genommen wird */
  public OrderedFieldList(List startList) {
    fields = startList;
  }

  @Override
  public void deleteField(final String fieldName) {
    fields.remove(new FieldImpl(fieldName));
  }

  @Override
  public Field getField(final String fieldName) {
    Field f = null;
    final int index = fields.indexOf(new FieldImpl(fieldName));
    if (index > -1) {
      f = fields.get(index);
    }
    return f;
  }

  @Override
  public int getOrder(String fieldName) {
    return fields.indexOf(new FieldImpl(fieldName)) + 1;
  }

  private int getOrder(Field fieldName) {
    return fields.indexOf(fieldName) + 1;
  }

  @Override
  public Field getFieldByOrder(final int order) {
    Field f = null;
    if (order <= fields.size() && order >= 0) {
      f = fields.get(order - 1);
    }
    return f;
  }

  @Override
  public void setOrder(String fieldName, int order) {
    // Prüfen ob Feld überhaupt in der Liste ist
    if (getOrder(fieldName) != 0 && order < fields.size()) {
      Field temp = getField(fieldName);
      deleteField(fieldName);
      fields.add(order - 1, temp);
    }
    else {
      Field temp = getField(fieldName);
      deleteField(fieldName);
      addField(temp);
    }
  }

  @Override
  public List<Field> get() {
    return new ArrayList<>(fields);
  }

  @Override
  public void addField(final Field f) {
    fields.removeIf(fw -> fw.getName().equals(f.getName()));
    fields.add(f);
  }

  @Override
  public void setReferenceOrder(String fieldName, int order) {
    // Achtung: Diese Klasse ändert nur die Ordnung, sie prüft nicht, ob die neue Ordnung valid ist. Es können also zwei
    // Felder an der ersten Stelle stehen.
    Field f = getField(fieldName);
    if (fieldRefOrder != null) {
      fieldRefOrder.put(getFieldIdent(f), order);
    }
    else if (order > fields.size() || !getFieldByOrder(order).equals(f)) {
      setUpRefOrder(fieldName);
      fieldRefOrder.put(getFieldIdent(f), order);
    }
  }

  private void setUpRefOrder(String fieldName) {
    if (fieldRefOrder == null) {
      LOG.log(Level.INFO, "Created Custom RefOrder for " + fieldName);
      fieldRefOrder = new HashMap<>();
      int i = 1;
      for (Field fx : fields) {
        fieldRefOrder.put(getFieldIdent(fx), i++);
      }
    }
  }

  @Override
  public int getReferenceOrder(String fieldName) {
    Field f = getField(fieldName);
    int res;
    if (fieldRefOrder == null || fieldRefOrder.isEmpty()) {
      res = getOrder(f);
    }
    else {
      Integer key = getFieldIdent(f);
      if (fieldRefOrder.containsKey(key)) {
        res = fieldRefOrder.get(key);
      }
      else {
        LOG.log(Level.SEVERE, "Failed to find RefOrder for " + fieldName);
        res = fieldRefOrder.size();
      }
    }
    return res;
  }

  @Override
  public Field getRefFieldByOrder(final int order) {
    Field f = null;
    if (fieldRefOrder == null || fieldRefOrder.isEmpty()) {
      f = getFieldByOrder(order);
    }
    else {
      for (Map.Entry<Integer, Integer> es : fieldRefOrder.entrySet()) {
        if (es.getValue() == order) {
          f = fields.stream() // convert list to stream
              .filter(line -> es.getKey().equals(getFieldIdent(line))).collect(Collectors.toList()).get(0);
        }
      }
    }
    return f;
  }

  private int getFieldIdent(Field f) {
    return System.identityHashCode(f);
  }

  @Override
  public void replaceField(Field f, String oldname) {
    int pos = getOrder(oldname);
    boolean hasRefOrder = false;
    int refPos = 0;
    if (pos > 0) {
      if (fieldRefOrder != null) {
        hasRefOrder = fieldRefOrder.containsKey(getFieldIdent(getField(oldname)));
      }
      if (hasRefOrder) {
        refPos = getReferenceOrder(oldname);
        fieldRefOrder.remove(getFieldIdent(getFieldByOrder(pos)));
      }
      deleteField(oldname);
      addField(f);
      setOrder(f.getName(), pos);
      if (hasRefOrder) {
        setReferenceOrder(f.getName(), refPos);
      }
    }
  }

  @Override
  public void clear() {
    fields.clear();
    if (fieldRefOrder != null) {
      fieldRefOrder.clear();
    }
  }

  @Override
  public String toString() {
    return (fieldRefOrder != null && fieldRefOrder.size() == fields.size()) ? fields.stream()
        .map(a -> a.getName() + " (" + getReferenceOrder(a.getName()) + "), ").reduce("", String::concat)
        : fields.toString() + " noRef";
  }
}
