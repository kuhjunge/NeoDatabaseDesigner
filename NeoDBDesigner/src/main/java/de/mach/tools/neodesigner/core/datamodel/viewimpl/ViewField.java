/*******************************************************************************
 * Copyright (C) 2017 Chris Deter
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/

package de.mach.tools.neodesigner.core.datamodel.viewimpl;

import de.mach.tools.neodesigner.core.Strings;
import de.mach.tools.neodesigner.core.Util;
import de.mach.tools.neodesigner.core.datamodel.Domain.DomainId;
import de.mach.tools.neodesigner.core.datamodel.Domain;
import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.core.datamodel.impl.FieldImpl;

import java.util.List;
import java.util.stream.Collectors;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Implementation von Field für den View.
 *
 * @author Chris Deter
 *
 */
public class ViewField extends ViewNodeImpl<Field> implements Field {
  private final StringProperty domain = new SimpleStringProperty();
  private final BooleanProperty isRequired = new SimpleBooleanProperty(true);
  private final BooleanProperty isPartOfPrimary = new SimpleBooleanProperty(false);
  private boolean modifiedType = false;
  private boolean modifiedReq = false;
  private boolean modifiedPrim = false;

  /**
   * Konstruktor.
   *
   * @param name
   *          Name des Feldes
   * @param domain
   *          Datentyp des Feldes
   * @param domainLength
   *          Die Länge des Datentypes
   * @param req
   *          ist das Feld ein Pflichtfeld?
   * @param nodeOf
   *          Tabelle dem das Feld zugeordnet ist
   */
  public ViewField(final String name, final DomainId domain, final int domainLength, final boolean req,
      final Table nodeOf, final String comment) {
    super(new FieldImpl(name, domain, domainLength, req, comment, nodeOf));
    setListener();
  }

  /**
   * Konstruktor für den Imprort eines vorhanden Feldes.
   *
   * @param f
   *          das Feld
   * @param t
   *          Die tabelle dem das neue Feld zugeordnet werden soll
   */
  public ViewField(final Field f, final ViewTable t) {
    super(new FieldImpl(f.getName(), f.getDomain(), f.getDomainLength(), f.isRequired(), f.getComment(), t));
    getNode().setPartOfPrimaryKey(f.isPartOfPrimaryKey());
    setListener();
  }

  /**
   * Setzt die Listener um änderungen im Objekt auch im internen Model zu ändern.
   */
  private void setListener() {
    domain.set(Domain.getName(getNode().getDomain()) + getDomainExtra());
    setRequired(getNode().isRequired());
    setPartOfPrimaryKey(getNode().isPartOfPrimaryKey());
    domain.addListener((obs, oldValue, newValue) -> setDomainIntern(newValue));
    isRequired.addListener((obs, oldValue, newValue) -> {
      modifiedReq = true;
      setModified();
      getNode().setRequired(newValue);
      isRequired.set(getNode().isRequired());
    });
    isPartOfPrimary.addListener((obs, oldValue, newValue) -> {
      modifiedPrim = true;
      getNode().setPartOfPrimaryKey(newValue);
      isPartOfPrimary.set(getNode().isPartOfPrimaryKey());
      isRequired.set(getNode().isRequired());
      setModified();
    });
  }

  private String getDomainExtra() {
    return getNode().getDomainLength() > 0 ? Strings.COLON + getNode().getDomainLength() : Strings.EMPTYSTRING;
  }

  /**
   * Speichert den neuen Wert nur wenn es erlaubt ist. Zudem wird der neue Wert
   * mit dem Internen Model abgeglichen.
   *
   * @param val
   *          der neue Wert
   */
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
  }

  public String getTypeOfData() {
    return domain.get();
  }

  public void setTypeOfData(final String typeOfData) {
    domain.set(typeOfData);
  }

  @Override
  public DomainId getDomain() {
    return getNode().getDomain();
  }

  @Override
  public void setDomain(final DomainId did) {
    domain.set(did.name() + Strings.COLON + getNode().getDomainLength());
  }

  @Override
  public int getDomainLength() {
    return getNode().getDomainLength();
  }

  @Override
  public void setDomainLength(final int length) {
    domain.set(getNode().getDomain() + Strings.COLON + length);
  }

  @Override
  public boolean isRequired() {
    return isRequired.get();
  }

  @Override
  public void setRequired(final boolean isRequired) {
    getNode().setRequired(isRequired);
    this.isRequired.set(getNode().isRequired());
  }

  @Override
  public boolean isPartOfPrimaryKey() {
    return isPartOfPrimary.get();
  }

  @Override
  public void setPartOfPrimaryKey(final boolean prim) {
    getNode().setPartOfPrimaryKey(prim);
    isPartOfPrimary.set(getNode().isPartOfPrimaryKey());
    isRequired.set(getNode().isRequired());
  }

  @Override
  public void saved() {
    super.saved();
    modifiedType = false;
    modifiedReq = false;
    modifiedPrim = false;
  }

  /**
   * View Methode um zu erkennnen ob ein Typ verändert wurde.
   *
   * @return True wenn ein Typ verändert wurde
   */
  public boolean isModifiedDomain() {
    return modifiedType;
  }

  /**
   * View Methode um zu erkennen ob die Zuorndung zum Primärschlüssel geändert
   * wurde.
   *
   * @return True wenn die Zuordnung zum Primärschlüssel verändert wurde
   */
  public boolean isModifiedPrim() {
    return modifiedPrim;
  }

  /**
   * View Methode um zu erkennen ob die Eigenschaft Required verändert wurde.
   *
   * @return True wenn die Eigenschaft Required verändert wurde
   */
  public boolean isModifiedReq() {
    return modifiedReq;
  }

  /**
   * View Methode um eine Prim Property für die Tabelle in der GUI zu bekommen.
   *
   * @return Property Eigenschaft für View
   */
  public BooleanProperty primProperty() {
    return isPartOfPrimary;
  }

  /**
   * View Methode um eine typeOfData Property für die Tabelle in der GUI zu
   * bekommen.
   *
   * @return Property Eigenschaft für View
   */
  public StringProperty typeOfDataProperty() {
    return domain;
  }

  /**
   * View Methode um eine Required Property für die Tabelle in der GUI zu
   * bekommen.
   *
   * @return Property Eigenschaft für View
   */
  public BooleanProperty requiredProperty() {
    return isRequired;
  }

  /**
   * Funktion zum Herausfinden ob dieses Feldn einem Index zugeordnet ist.
   *
   * @return true wenn ein Index existiert
   */
  public boolean hasIndex() {
    final List<Index> li = getTable().getIndizies().stream().filter(l -> l.getFieldList().contains(this))
        .collect(Collectors.toList());
    return !li.isEmpty() ? true : false;
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
  public int hashCode() {
    return getName().hashCode();
  }

  @Override
  public List<ForeignKey> getConnectedFks() {
    return getNode().getConnectedFks();
  }
}
