package de.mach.tools.neodesigner.core.datamodel.viewimpl;

import de.mach.tools.neodesigner.core.Strings;
import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.core.datamodel.impl.TableImpl;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Implementation von Table für den View.
 *
 * @author Chris Deter
 *
 */
public class ViewTable extends ViewNodeImpl implements Table {
  public ObservableList<ViewField> dataFields = FXCollections.observableArrayList();
  private ViewIndex xpk;
  public ObservableList<ViewForeignKey> foreignKeys = FXCollections.observableArrayList();
  public ObservableList<ViewIndex> indizes = FXCollections.observableArrayList();
  public ObservableList<ViewNodeImpl> delete = FXCollections.observableArrayList();
  private final Table data;

  /**
   * Konstruktor.
   *
   * @param n
   *          Name der neuen Tabelle
   */
  public ViewTable(final String n) {
    super(new TableImpl(n));
    data = (Table) super.getNode();
    final ViewIndex t = new ViewIndex(Strings.INDEXTYPE_XPK + n, this);
    t.setAsNewCreated();
    xpk = t;
  }

  /**
   * Konstruktor für Tabellenimport.
   *
   * @param t
   *          Das zu importierende Tabellenobjekt
   */
  public ViewTable(final Table t) {
    super(new TableImpl(t.getName()));
    data = (Table) super.getNode();
    data.setCategory(t.getCategory());
    for (final Field f : t.getData()) {
      dataFields.add(new ViewField(f, this));
    }
    for (final Index i : t.getIndizies()) {
      indizes.add(new ViewIndex(i, this));
    }
    xpk = new ViewIndex(t.getXpk(), this);
    for (final ForeignKey i : t.getForeignKeys()) {
      foreignKeys.add(new ViewForeignKey(i, this));
    }
  }

  @Override
  public String getCategory() {
    return data.getCategory();
  }

  @Override
  public List<Field> getData() {
    final List<Field> l = new ArrayList<>();
    l.addAll(dataFields);
    return l;
  }

  /**
   * Gibt die Field Objekte als ViewField zurück
   *
   * @return Data la ViewField
   */
  public List<ViewField> getVData() {
    return dataFields;
  }

  @Override
  public Index getXpk() {
    return xpk;
  }

  @Override
  public void setXpk(final Index lxpk) {
    xpk = new ViewIndex(lxpk, this);
  }

  @Override
  public List<ForeignKey> getForeignKeys() {
    final List<ForeignKey> l = new ArrayList<>();
    l.addAll(foreignKeys);
    return l;
  }

  @Override
  public List<Index> getIndizies() {
    final List<Index> l = new ArrayList<>();
    l.addAll(indizes);
    return l;
  }

  @Override
  public void addField(final Field f) {
    dataFields.add(new ViewField(f, this));

  }

  @Override
  public void setCategory(final String kategory) {
    data.setCategory(kategory);
  }

  @Override
  public boolean equals(final Object o) {
    if (!(o instanceof Table)) {
      return false;
    }
    final Table other = (Table) o;
    return getName().equals(other.getName());
  }

  @Override
  public int hashCode() {
    return getName().hashCode();
  }
}
