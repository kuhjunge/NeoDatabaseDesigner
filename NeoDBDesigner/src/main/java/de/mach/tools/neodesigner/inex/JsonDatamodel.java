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

package de.mach.tools.neodesigner.inex;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import de.mach.tools.neodesigner.core.category.CategoryObj;
import de.mach.tools.neodesigner.core.datamodel.Domain;
import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.core.datamodel.impl.FieldImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.ForeignKeyImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.IndexImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.TableImpl;
import de.mach.tools.neodesigner.inex.nexport.UtilExport;
import de.mach.tools.neodesigner.inex.nimport.UtilImport;


public class JsonDatamodel implements Generator {
  private static Gson g = new Gson();
  private List<TableJ> tables = new ArrayList<>();
  private List<CategoryJ> categories = new ArrayList<>();

  private JsonDatamodel() {
    // Default Konstruktor für GSON
  }

  public JsonDatamodel(List<CategoryObj> categorySelection) {
    setCategories(categorySelection);
  }

  public static JsonDatamodel getDatamodelFromJson(String json) {
    return g.fromJson(json, JsonDatamodel.class);
  }

  @Override
  public String toString() {
    return g.toJson(this);
  }

  public String generate(List<Table> tableList) {
    for (Table t : tableList) {
      TableJ tj = new TableJ(t.getName(), t.getCategory(), UtilExport.cleanComment(t.getComment()),
                             new IndexJ(t.getXpk().getName(), t.getXpk().isUnique(), t.getXpk().getFieldList()));
      for (Field f : t.getFields()) {
        tj.addField(new FieldJ(f.getName(), Domain.getName(f.getDomain()), f.getDomainLength(), !f.isRequired(),
                               UtilExport.cleanComment(f.getComment())));
      }
      for (Index i : t.getIndizies()) {
        if (!UtilExport.isRedundant(i, t, true)) {
          tj.addIndex(new IndexJ(i.getName(), i.isUnique(), i.getFieldList()));
        }
      }
      for (ForeignKey i : t.getForeignKeys()) {
        tj.addForeignKey(new ForeignKeyJ(i.getName(), i.getRefTable().getName(), UtilExport.sortPkElem(i)));
      }
      tables.add(tj);
    }
    return toString();
  }

  private void setCategories(List<CategoryObj> categorySelection) {
    for (CategoryObj c : categorySelection) {
      categories.add(new CategoryJ(c.getCategory(), c.getCategoryText()));
    }
  }

  public List<Table> getDatamodel() {
    List<Table> lt = new ArrayList<>();
    // Tabelle
    for (TableJ tj : tables) {
      Table t = new TableImpl(tj.n);
      t.setCategory(tj.cat);
      t.setComment(tj.getNote());
      // Felder
      for (FieldJ fj : tj.fl) {
        t.addField(new FieldImpl(fj.n, Domain.getFromName(fj.dom), fj.getLength(), !fj.isnull, fj.getNote(), t));
      }
      // PK
      t.setXpk(genIndex(tj.xpk, t));
      // Indizes
      for (IndexJ ij : tj.il) {
        t.addIndex(genIndex(ij, t));
      }
      lt.add(t);
    }
    // FKs können erst hinzugefügt werden, wenn alle Tabellen da sind - sonst schlägt referenzierung fehl
    for (TableJ tj : tables) {
      Table t = getTable(lt, tj.n);
      // FKs
      for (ForeignKeyJ fkj : tj.fkl) {
        ForeignKey fk = new ForeignKeyImpl(fkj.n, t);
        t.addForeignKey(fk, false);
        fk.setRefTable(getTable(lt, fkj.rt));
        fk.getRefTable().addForeignKey(fk, true);
        List<Field> lf = new ArrayList<>();
        for (String field : fkj.fl) {
          lf.add(t.getField(field));
        }
        UtilImport.setFieldForeignkeyRelation(fk, lf);
      }
    }
    return lt;
  }

  private Table getTable(List<Table> lt, final String name) {
    return lt.get(lt.indexOf(new TableImpl(name.trim())));
  }

  private Index genIndex(IndexJ ij, Table t) {
    Index i = new IndexImpl(ij.n, t);
    for (String field : ij.fl) {
      i.addField(t.getField(field));
    }
    return i;
  }

  public Map<String, String> getCategoryMap() {
    Map<String, String> catMap = new HashMap<>();
    for (CategoryJ cj : categories) {
      catMap.put(cj.id, cj.n);
    }
    return catMap;
  }

  class TableJ {
    String n;
    String cat;
    String note;
    List<FieldJ> fl = new ArrayList<>();
    IndexJ xpk;
    List<IndexJ> il = new ArrayList<>();
    List<ForeignKeyJ> fkl = new ArrayList<>();

    TableJ(String name, String category, String cleanComment, IndexJ xpk) {
      this.n = name;
      this.cat = category;
      if (cleanComment.length() > 0) {
        this.note = cleanComment;
      }
      this.xpk = xpk;
    }

    void addField(FieldJ f) {
      fl.add(f);
    }

    void addIndex(IndexJ i) {
      il.add(i);
    }

    void addForeignKey(ForeignKeyJ i) {
      fkl.add(i);
    }

    String getNote() {
      return note != null ? note : "";
    }
  }


  class FieldJ {
    String n;
    String dom;
    Integer l;
    Boolean isnull;
    String note;

    FieldJ(String name, String dom, int domainLength, boolean isnull, String cleanComment) {
      this.n = name;
      this.dom = dom;
      if (domainLength > 0) {
        this.l = domainLength;
      }
      this.isnull = isnull;
      if (cleanComment.length() > 0) {
        this.note = cleanComment;
      }
    }

    int getLength() {
      return l != null ? l : 0;
    }

    String getNote() {
      return note != null ? note : "";
    }
  }


  class IndexJ {
    String n;
    Boolean unique;
    List<String> fl = new ArrayList<>();

    IndexJ(String name, boolean unique, List<Field> fieldList) {
      this.n = name;
      this.unique = unique;
      for (Field f : fieldList) {
        fl.add(f.getName());
      }
    }
  }


  class ForeignKeyJ {
    String n;
    String rt;
    List<String> fl = new ArrayList<>();

    ForeignKeyJ(String name, String refTable, List<Field> fieldList) {
      this.n = name;
      this.rt = refTable;
      for (Field f : fieldList) {
        fl.add(f.getName());
      }
    }
  }

  class CategoryJ {
    String id;
    String n;

    CategoryJ(String category, String categoryText) {
      id = category;
      n = categoryText;
    }
  }
}
