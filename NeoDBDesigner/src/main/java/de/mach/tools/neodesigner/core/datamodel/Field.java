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

import de.mach.tools.neodesigner.core.datamodel.Domain.DomainId;
import java.util.List;

/**
 * Representiert eine Spalte oder Feld im relationalen Datenbankmodel.
 *
 * @author Chris Deter
 *
 */
public interface Field extends Node {
  /**
   * Gibt zurück was für ein Datentyp das Feld hat.
   *
   * @return Datentyp
   */
  DomainId getDomain();

  /**
   * setter für den Datentyp.
   *
   * @param domain
   *          die Daten
   */
  void setDomain(DomainId domain);

  /**
   * Gibt die Domaingröße zurück. Diese Methode ist nur relevant für die Domain
   * "String".
   *
   * @return Größe der Domain als int
   */
  int getDomainLength();

  /**
   * Setzt die Domainlänge. Diese Methode ist nur relevant für die Domain
   * "String".
   *
   * @param length
   *          Die neue Länge
   */
  void setDomainLength(int length);

  /**
   * getter required.
   *
   * @return ob das Feld required ist
   */
  boolean isRequired();

  /**
   * setter required.
   *
   * @param isRequired
   *          Boolean ob Feld notwendig ist
   */
  void setRequired(boolean isRequired);

  /**
   * getter is Part of Prim Key.
   *
   * @return boolean ob das Feld Teil eines Primärschlüssels ist
   */
  boolean isPartOfPrimaryKey();

  /**
   * setter ist part of Prim Key.
   *
   * @param prim
   *          setzt ob das Feld Teil eines prim Schlüssels ist
   */
  void setPartOfPrimaryKey(boolean prim);

  /**
   * Gibt alle verknüpften Fremdschlüssel zurück.
   *
   * @return gibt alle verknüpften Fremdschlüssel zurück
   */
  List<ForeignKey> getConnectedFks();
}
