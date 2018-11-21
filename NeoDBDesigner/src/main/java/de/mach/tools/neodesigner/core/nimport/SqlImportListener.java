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

package de.mach.tools.neodesigner.core.nimport;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.mach.tools.neodesigner.core.Strings;
import de.mach.tools.neodesigner.core.datamodel.Domain;
import de.mach.tools.neodesigner.core.datamodel.Domain.DomainId;
import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.core.datamodel.impl.FieldImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.ForeignKeyImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.IndexImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.TableImpl;
import de.mach.tools.neodesigner.core.nimport.antlrsql.SQLBaseListener;
import de.mach.tools.neodesigner.core.nimport.antlrsql.SQLParser.CreCategoryContext;
import de.mach.tools.neodesigner.core.nimport.antlrsql.SQLParser.CreFieldContext;
import de.mach.tools.neodesigner.core.nimport.antlrsql.SQLParser.CreForeignKeyContext;
import de.mach.tools.neodesigner.core.nimport.antlrsql.SQLParser.CreIndexContext;
import de.mach.tools.neodesigner.core.nimport.antlrsql.SQLParser.CrePrimKeyContext;
import de.mach.tools.neodesigner.core.nimport.antlrsql.SQLParser.CreTableContext;
import de.mach.tools.neodesigner.core.nimport.antlrsql.SQLParser.CreUniqueIndexContext;
import de.mach.tools.neodesigner.core.nimport.antlrsql.SQLParser.CrefieldNameListContext;
import de.mach.tools.neodesigner.core.nimport.antlrsql.SQLParser.FieldnameContext;


/** ANTLR Import Listener für die Verarbeitung des input SQLs.
 *
 * @author Chris Deter */
class SqlImportListener extends SQLBaseListener {
  List<Table> tables = new ArrayList<>();
  private Table actualTable;
  private Index actualIndex;
  private ForeignKey actualFk;
  private String category = "";
  private String comment = "";
  private final List<Field> tempFields = new ArrayList<>();

  /** Erstellt einen Index.
   *
   * @param name Name des Indexes
   * @param tablename Name der übergeordneten Tabelle */
  private void createIndex(final String name, final String tablename) {
    actualTable = getActualTable(tablename);
    actualIndex = new IndexImpl(name, actualTable);
  }

  @Override
  public void enterCreCategory(final CreCategoryContext ctx) {
    final String infoLine = ctx.getText();
    if (infoLine.startsWith("-- Table: ")) {
      final String[] temp = infoLine.split(Strings.COLON);
      if (temp.length > 2) {
        category = temp[2].trim().split(Strings.SPACE)[0].trim();
      }
      if (temp.length > 3) {
        comment = temp[3].trim();
      }
    }
  }

  @Override
  public void enterCreField(final CreFieldContext ctx) {
    final Domain d = UtilImport.oracleTypeToDomain(ctx.type().getText());
    final FieldImpl f = new FieldImpl(ctx.fieldname().getText(), d.getDomain(), d.getDomainlength(),
                                      ctx.isNull() == null, Strings.EMPTYSTRING, actualTable);
    actualTable.addField(f);
  }

  @Override
  public void enterCrefieldNameList(final CrefieldNameListContext ctx) {
    for (final FieldnameContext fnc : ctx.fieldname()) {
      final Optional<Field> field = actualTable.getField(fnc.getText());
      field.ifPresent(field1 -> {
        if (actualIndex != null) {
          actualIndex.addField(field1);
        }
        else {
          tempFields.add(field1);
        }
      });
    }
  }

  @Override
  public void enterCreForeignKey(final CreForeignKeyContext ctx) {
    final String refTableName = ctx.tablename(1).getText();
    final String tableName = ctx.tablename(0).getText();
    final String indexName = ctx.indexname().getText();

    final Table refTable = new TableImpl(refTableName);
    actualTable = getActualTable(tableName);
    final ForeignKey index = new ForeignKeyImpl(indexName, actualTable);
    actualIndex = null;
    index.setRefTable(tables.get(tables.indexOf(refTable)));
    actualTable.getForeignKeys().add(index);
    actualFk = index;
  }

  @Override
  public void enterCreIndex(final CreIndexContext ctx) {
    createIndex(ctx.indexname().getText(), ctx.tablename().getText());
    actualTable.getIndizies().add(actualIndex);
  }

  @Override
  public void enterCrePrimKey(final CrePrimKeyContext ctx) {
    createIndex(ctx.indexname().getText(), ctx.tablename().getText());
    actualTable.setXpk(actualIndex);
  }

  @Override
  public void enterCreTable(final CreTableContext ctx) {
    actualTable = new TableImpl(ctx.tablename().getText());
    if (category.length() > 0) {
      actualTable.setCategory(category);
      actualTable.setComment(comment);
    }
    category = "";
  }

  @Override
  public void enterCreUniqueIndex(final CreUniqueIndexContext ctx) {
    createIndex(ctx.indexname().getText(), ctx.tablename().getText());
    actualTable.getIndizies().add(actualIndex);
    actualIndex.setUnique(true);
  }

  @Override
  public void exitCreForeignKey(final CreForeignKeyContext ctx) {
    UtilImport.setFieldForeignkeyRelation(actualFk, tempFields);
    for (final Field f : actualFk.getIndex().getFieldList()) {
      if (f.getDomain().equals(DomainId.STRING) && f.getDomainLength() == 20) {
        f.setDomain(DomainId.LOOKUP);
      }
    }
    tempFields.clear();
  }

  @Override
  public void exitCreTable(final CreTableContext ctx) {
    tables.add(actualTable);
    actualTable = null;
  }

  /** Läd mithilfe des Tabellennamens die Tabelle aus der Liste der importierten Tabellen.
   *
   * @param tablename Der Tabellenname */
  private Table getActualTable(final String tablename) {
    final Table table = new TableImpl(tablename);
    return tables.get(tables.indexOf(table));
  }
}
