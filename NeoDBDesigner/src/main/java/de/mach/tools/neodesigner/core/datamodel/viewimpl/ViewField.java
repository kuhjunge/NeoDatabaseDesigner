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

package de.mach.tools.neodesigner.core.datamodel.viewimpl;


import java.util.List;
import java.util.stream.Collectors;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import de.mach.tools.neodesigner.core.Strings;
import de.mach.tools.neodesigner.core.Util;
import de.mach.tools.neodesigner.core.datamodel.Domain;
import de.mach.tools.neodesigner.core.datamodel.Domain.DomainId;
import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Node;
import de.mach.tools.neodesigner.core.datamodel.impl.FieldImpl;


/** Implementation von Field für den View.
 *
 * @author Chris Deter */
public class ViewField extends ViewNodeImpl<Field> implements Field {
  private final StringProperty domain = new SimpleStringProperty();
  private final BooleanProperty isRequired = new SimpleBooleanProperty(true);
  private final BooleanProperty isPartOfPrimary = new SimpleBooleanProperty(false);
  private final StringProperty orderProp = new SimpleStringProperty();
  private boolean modifiedType = false;
  private boolean modifiedReq = false;
  private boolean modifiedPrim = false;
  private boolean modifiedOrder = false;

  /** Konstruktor für den Imprort eines vorhanden Feldes.
   *
   * @param f das Feld
   * @param t Die tabelle dem das neue Feld zugeordnet werden soll */
  public ViewField(final Field f, final ViewTable t) {
    super(new FieldImpl(f.getName(), f.getDomain(), f.getDomainLength(), f.isRequired(), f.getComment(), t), t);
    setListener();
  }

  /** Konstruktor.
   *
   * @param name Name des Feldes
   * @param domain Datentyp des Feldes
   * @param domainLength Die Länge des Datentypes
   * @param req ist das Feld ein Pflichtfeld?
   * @param nodeOf Tabelle dem das Feld zugeordnet ist */
  public ViewField(final String name, final DomainId domain, final int domainLength, final boolean req,
                   final ViewTable nodeOf, final String comment) {
    super(new FieldImpl(name, domain, domainLength, req, comment, nodeOf), nodeOf);
    setListener();
  }

  @Override
  public boolean equals(final Object o) {
    if (!(o instanceof Field)) {
      return false;
    }
    final Field other = (Field) o;
    return getName().equals(other.getName());
  }

  @Override
  public List<ForeignKey> getConnectedFks() {
    return getNode().getConnectedFks();
  }

  @Override
  public DomainId getDomain() {
    return getNode().getDomain();
  }

  private String getDomainExtra() {
    return getNode().getDomainLength() > 0 ? Strings.COLON + getNode().getDomainLength() : Strings.EMPTYSTRING;
  }

  @Override
  public int getDomainLength() {
    return getNode().getDomainLength();
  }

  public String getTypeOfData() {
    return domain.get();
  }

  @Override
  public int hashCode() {
    return getName().hashCode();
  }

  /** Funktion zum Herausfinden ob dieses Feldn einem Index zugeordnet ist.
   *
   * @return true wenn ein Index existiert */
  public boolean hasIndex() {
    final List<Index> li = getTable().getIndizies().stream().filter(l -> l.getFieldList().contains(this))
        .collect(Collectors.toList());
    return !li.isEmpty();
  }

  /** View Methode um zu erkennnen ob ein Typ verändert wurde.
   *
   * @return True wenn ein Typ verändert wurde */
  public boolean isModifiedDomain() {
    return modifiedType;
  }

  /** View Methode um zu erkennen ob die Zuorndung zum Primärschlüssel geändert wurde.
   *
   * @return True wenn die Zuordnung zum Primärschlüssel verändert wurde */
  public boolean isModifiedPrim() {
    return modifiedPrim;
  }

  public boolean isModifiedOrder() {
    return modifiedOrder;
  }

  /** View Methode um zu erkennen ob die Eigenschaft Required verändert wurde.
   *
   * @return True wenn die Eigenschaft Required verändert wurde */
  public boolean isModifiedReq() {
    return modifiedReq;
  }

  @Override
  public boolean isPartOfPrimaryKey() {
    return getNode().isPartOfPrimaryKey();
  }

  @Override
  public boolean isRequired() {
    return getNode().isRequired();
  }

  /** View Methode um eine Prim Property für die Tabelle in der GUI zu bekommen.
   *
   * @return Property Eigenschaft für View */
  public BooleanProperty primProperty() {
    return isPartOfPrimary;
  }

  /** View Methode um eine Required Property für die Tabelle in der GUI zu bekommen.
   *
   * @return Property Eigenschaft für View */
  public BooleanProperty requiredProperty() {
    return isRequired;
  }

  public StringProperty orderProperty() {
    return orderProp;
  }

  @Override
  public void saved() {
    super.saved();
    modifiedType = false;
    modifiedReq = false;
    modifiedPrim = false;
    modifiedOrder = false;
  }

  @Override
  public void setDomain(final DomainId did) {
    domain.set(did.name() + Strings.COLON + getNode().getDomainLength());
  }

  /** Speichert den neuen Wert nur wenn es erlaubt ist. Zudem wird der neue Wert mit dem Internen Model abgeglichen.
   *
   * @param val der neue Wert */
  private void setDomainIntern(final String val) {
    final String[] completeDomain = val.split(Strings.COLON);
    final String domainName = completeDomain[0];
    int domainSize = 0;
    if (completeDomain.length > 1) {
      domainSize = Util.tryParseInt(completeDomain[1]);
    }
    if (!domainName.equals(Domain.getName(getNode().getDomain())) || domainSize != getNode().getDomainLength()) {
      if (!hasIndex()) {
        modifiedType = true;
        getNode().setDomain(Domain.getFromName(domainName));
        getNode().setDomainLength(domainSize);
        setModified();
      }
      domain.set(Domain.getName(getNode().getDomain()) + getDomainExtra());
    }
    // Boolean ist per Default Required
    if (Domain.getFromName(domain.get()).equals(Domain.DomainId.BOOLEAN)) {
      setRequired(true);
    }
  }

  @Override
  public void setDomainLength(final int length) {
    domain.set(getNode().getDomain() + Strings.COLON + length);
  }

  /** Setzt die Listener um änderungen im Objekt auch im internen Model zu ändern. */
  private void setListener() {
    // Initalwerte
    domain.set(Domain.getName(getNode().getDomain()) + getDomainExtra());
    setRequired(getNode().isRequired());
    orderProp.set((getTable().getFields().size() + 1) + "");
    // Listener
    domain.addListener((obs, oldValue, newValue) -> setDomainIntern(newValue));
    isRequired.addListener((obs, oldValue, newValue) -> {
      modifiedReq = true;
      setModified();
      getNode().setRequired(newValue);
      isRequired.set(getNode().isRequired());
    });
    isPartOfPrimary.addListener((obs, oldValue, newValue) -> {
      modifiedPrim = true;
      isPartOfPrimary.set(getNode().isPartOfPrimaryKey());
      isRequired.set(getNode().isRequired());
      setPartOfPrimaryKey(newValue);
      setModified();
    });
    orderProp.addListener((obs, oldVal, newVal) -> {
      int i = Util.tryParseInt(newVal);
      if (i != 0) {
        getNode().setDisplayOrder(i);
        modifiedOrder = true;
      }
      else {
        orderProp.set(getNode().getDisplayOrder() + "");
      }
    });
  }


  public void setPartOfPrimaryKey(final boolean prim) {
    if (getTable().getXpk() != null) {
      if (prim && !getTable().getXpk().hasField(this.getName())) {
        getTable().getXpk().addField(this);
      }
      else if (!prim && getTable().getXpk().hasField(this.getName())) {
        getTable().getXpk().removeField(getTable().getXpk().getOrder(this.getName(), false) - 1);
      }
    }
    isPartOfPrimary.set(getNode().isPartOfPrimaryKey());
  }

  @Override
  public void setRequired(final boolean isReq) {
    getNode().setRequired(isReq);
    isRequired.set(isReq);
  }

  public void setTypeOfData(final String typeOfData) {
    domain.set(typeOfData);
  }

  @Override
  public String toString() {
    return getName();
  }

  /** View Methode um eine typeOfData Property für die Tabelle in der GUI zu bekommen.
   *
   * @return Property Eigenschaft für View */
  public StringProperty typeOfDataProperty() {
    return domain;
  }

  public void setDisplayOrder(int order) {
    orderProp.setValue(order + "");
  }

  public int getDisplayOrder() {
    return getNode().getDisplayOrder();
  }

  @Override
  public int compareTo(final Node o) {
    if (o instanceof Field) {
      return Integer.compare(getNode().getDisplayOrder(), ((Field) o).getDisplayOrder());
    }
    else {
      return getName().compareToIgnoreCase(o.getName());
    }
  }
}
