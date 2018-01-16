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

package de.mach.tools.neodesigner.core.datamodel;

import de.mach.tools.neodesigner.core.Strings;
import de.mach.tools.neodesigner.core.Util;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Klasse zum Verwalten der Domain.
 *
 * @author cd
 *
 */
public class Domain {
  public enum DomainId {
    LOOKUP, STRING, AMOUNT, COUNTER, BOOLEAN, DATE, BLOB, CLOB
  }

  private static final Logger LOG = Logger.getLogger(Domain.class.getName());
  private int domainlength = 0;
  private DomainId domain = DomainId.STRING;

  /**
   * Konstruktor für das Domain Objekt. Kann einen Inputstring verarbeiten in dem
   * Domain entweder kombiniert mit der Länge (String:10) oder als Name (Boolean)
   * enthalten ist, verarbeiten.
   *
   * @param val
   *          der Inputstring
   */
  public Domain(final String val) {
    if (val.contains(Strings.COLON)) {
      final String[] split = val.split(Strings.COLON);
      domain = Domain.getFromName(split[0]);
      domainlength = Util.tryParseInt(split[1]);
    } else {
      domain = Domain.getFromName(val);
    }
  }

  public int getDomainlength() {
    return domainlength;
  }

  public void setDomainlength(final int domainlength) {
    this.domainlength = domainlength;
  }

  public DomainId getDomain() {
    return domain;
  }

  public void setDomain(final DomainId domain) {
    this.domain = domain;
  }

  /**
   * Wandelt einen String in eine Domain um.
   *
   * @param domain
   *          die Domain als String
   * @return der Datentyp Domain
   */
  public static DomainId getFromName(final String domain) {
    DomainId ret;
    switch (domain.toLowerCase()) {
      case Strings.DOMAIN_LOOKUP:
        ret = DomainId.LOOKUP;
        break;
      case Strings.DOMAIN_AMOUNT:
        ret = DomainId.AMOUNT;
        break;
      case Strings.DOMAIN_BLOB:
        ret = DomainId.BLOB;
        break;
      case Strings.DOMAIN_BOOLEAN:
        ret = DomainId.BOOLEAN;
        break;
      case Strings.DOMAIN_CLOB:
        ret = DomainId.CLOB;
        break;
      case Strings.DOMAIN_COUNTER:
        ret = DomainId.COUNTER;
        break;
      case Strings.DOMAIN_DATE:
        ret = DomainId.DATE;
        break;
      case Strings.DOMAIN_STRING:
        ret = DomainId.STRING;
        break;
      default:
        Domain.LOG.log(Level.WARNING, () -> Strings.ERROR_DOMAIN + domain);
        ret = DomainId.STRING;
        break;
    }
    return ret;
  }

  /**
   * Übersetzt die Domain in einen Namen.
   *
   * @param domain
   *          die Domain
   * @return Name der Domain
   */
  public static String getName(final DomainId domain) {
    String ret = Strings.WARN;
    if (domain != null) {
      switch (domain) {
        case LOOKUP:
          ret = Util.getCapName(Strings.DOMAIN_LOOKUP);
          break;
        case AMOUNT:
          ret = Util.getCapName(Strings.DOMAIN_AMOUNT);
          break;
        case BLOB:
          ret = Util.getCapName(Strings.DOMAIN_BLOB);
          break;
        case BOOLEAN:
          ret = Util.getCapName(Strings.DOMAIN_BOOLEAN);
          break;
        case CLOB:
          ret = Util.getCapName(Strings.DOMAIN_CLOB);
          break;
        case COUNTER:
          ret = Util.getCapName(Strings.DOMAIN_COUNTER);
          break;
        case DATE:
          ret = Util.getCapName(Strings.DOMAIN_DATE);
          break;
        case STRING:
          ret = Util.getCapName(Strings.DOMAIN_STRING);
          break;
        default:
          Domain.LOG.log(Level.WARNING, () -> Strings.DOMAIN_ERROR_IN + domain);
          break;
      }
    }
    return ret;
  }

}
