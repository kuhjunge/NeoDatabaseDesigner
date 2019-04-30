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
import java.util.List;

import de.mach.tools.neodesigner.core.datamodel.Domain.DomainId;
import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Table;


/** Implementation vom Field.
 *
 * @author Chris Deter */
public class FieldImpl extends NodeImpl implements Field {
  private DomainId domain;
  private int size;
  private boolean isRequired = true;

  /** Konstruktor.
   *
   * @param name des Feldes */
  public FieldImpl(final String name) {
    super(name);
  }

  /** Konstruktor Field.
   *
   * @param name Der Name
   * @param domain Der Datentyp
   * @param domainLength Die Lenge des Datentyp String
   * @param isReq ist das Feld required
   * @param nodeOf die Tabelle des Feldes */
  public FieldImpl(final String name, final DomainId domain, final int domainLength, final boolean isReq,
                   final String comment, final Table nodeOf) {
    super(name, nodeOf);
    setRequired(isReq);
    setDomain(domain);
    setDomainLength(domainLength);
    setComment(comment);
  }

  @Override
  public DomainId getDomain() {
    return domain;
  }

  @Override
  public void setDomain(final DomainId domain) {
    this.domain = domain;
  }

  @Override
  public int getDomainLength() {
    return domain == DomainId.STRING ? size : 0;
  }

  @Override
  public void setDomainLength(final int length) {
    if (domain == DomainId.STRING) {
      size = length;
    }
  }

  @Override
  public boolean isRequired() {
    return isRequired || isPartOfPrimaryKey();
  }

  @Override
  public void setRequired(final boolean isReq) {
    isRequired = isReq;
  }

  @Override
  public boolean isPartOfPrimaryKey() {
    boolean ret = false;
    if (getTable().getXpk() != null) {
      ret = getTable().getXpk().getFieldList().contains(this);
    }
    return ret;
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
    final List<ForeignKey> fkl = new ArrayList<>();
    for (final ForeignKey fk : getTable().getForeignKeys()) {
      for (final Field tempField : fk.getIndex().getFieldList()) {
        if (tempField.getName().equals(getName())) {
          fkl.add(fk);
        }
      }
    }
    return fkl;
  }

  @Override
  public String getNodeType() {
    return "Column";
  }

  public void setDisplayOrder(int order) {
    getTable().setOrder(getName(), order);
  }

  public int getDisplayOrder() {
    return getTable().getOrder(getName());
  }
}
