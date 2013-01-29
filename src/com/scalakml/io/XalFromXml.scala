/*
 * Copyright (c) 2013, Ringo Wathelet
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice, this
 *   list of conditions and the following disclaimer in the documentation and/or
 *   other materials provided with the distribution.
 *
 * - Neither the name of "scalakml" nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.scalakml.io

import com.scalakml.io.KmlFromXml._
import scala.xml._
import com.scalakml.xAL._
import scala.xml
import com.scalakml.kml.Kml


/**
 * @author Ringo Wathelet
 * Date: 29/01/13
 * Version: 1
 *
 * Reference: OGC 07-147r2 Version: 2.2.0, Category: OGC Standard, Editor: Tim Wilson, at
 * http://www.opengeospatial.org/standards/kml
 * also
 * Google developers KML Reference, at
 * https://developers.google.com/kml/documentation/kmlreference
 * also
 * xAL: eXtensible Address Language at: http://www.oasis-open.org/committees/ciq
 *
 */

/** Factory for creating xAL objects instances from scala xml NodeSeq */
object XalFromXml extends KmlExtractor {

  import AddressDetailsTypeSet._
  import CountryTypeSet._

  // TO BE REMOVED
  def makeKml(nodeSeq: xml.NodeSeq): Option[Kml] = None

  def makeXAL(nodeSeq: xml.NodeSeq): Option[XAL] = {
    if (nodeSeq.isEmpty) None else
      (nodeSeq \\ "xAL") match {
        case x if (x.isEmpty) => None
        case x => Some(new XAL(addressDetails = makeAddressDetailsSet(x),
          any = Seq.empty,
          version = getFromNode[String](nodeSeq \ "@Version"),
          attributes = Map()))
      }
  }

  def makeAddressDetailsSet(nodeSeq: NodeSeq): Seq[AddressDetails] = {
    if (nodeSeq.isEmpty) Seq.empty else (nodeSeq collect { case x => makeAddressDetails(x \ "AddressDetails") } flatten)
  }

  def makeAddressDetails(nodeSeq: NodeSeq): Option[AddressDetails] = {
    if (nodeSeq.isEmpty) None
    else Some(new AddressDetails(
      postalServiceElements = makePostalServiceElements(nodeSeq \ "PostalServiceElements"),
      addressDetailsOption = makeAddressDetailsOption(nodeSeq),
      addressType = getFromNode[String](nodeSeq \ "@AddressType"),
      currentStatus = getFromNode[String](nodeSeq \ "@CurrentStatus"),
      validFromDate = getFromNode[String](nodeSeq \ "@ValidFromDate"),
      validToDate = getFromNode[String](nodeSeq \ "@ValidToDate"),
      usage = getFromNode[String](nodeSeq \ "@Usage"),
      code = getFromNode[String](nodeSeq \ "@Code"),
      addressDetailsKey = getFromNode[String](nodeSeq \ "@AddressDetailsKey"),
      attributes = Map(),
      any = Seq.empty))
  }

  def makeAddressDetailsOption(nodeSeq: NodeSeq, addressType: AddressDetailsTypeSet): Option[AddressDetailsType] = {
   if (nodeSeq.isEmpty) None else {
     addressType match {
          case AddressDetailsTypeSet.Address => makeAddress(nodeSeq \ "Address")
          case AddressDetailsTypeSet.AddressLines => makeAddressLines(nodeSeq \ "AddressLines")
//          case AddressDetailsTypeSet.AdministrativeArea => makeAdministrativeArea(nodeSeq)
//          case AddressDetailsTypeSet.Country => makeCountry(nodeSeq)
//          case AddressDetailsTypeSet.Locality => makeLocality(nodeSeq)
//          case AddressDetailsTypeSet.Thoroughfare => makeThoroughfare(nodeSeq)
          case _ => None
        }
      }
    }

  def makeAddress(nodeSeq: NodeSeq): Option[Address] = {
    if (nodeSeq.isEmpty) None else Some(new Address(
      content = getFromNode[String](nodeSeq),
      objectType = getFromNode[String](nodeSeq \ "@Type"),
      code = getFromNode[String](nodeSeq \ "@Code"),
      attributes = Map()))
  }

  def makeAddressLine(nodeSeq: NodeSeq): Option[AddressLine] = {
    if (nodeSeq.isEmpty) None else Some(new AddressLine(
      content = getFromNode[String](nodeSeq),
      objectType = getFromNode[String](nodeSeq \ "@Type"),
      code = getFromNode[String](nodeSeq \ "@Code"),
      attributes = Map()))
  }

  def makeAddressLines(nodeSeq: NodeSeq): Option[AddressLines] = {
    if (nodeSeq.isEmpty) None else Some(new AddressLines(
      addressLines = makeAddressLineSet(nodeSeq),
      any = Seq.empty,
      attributes = Map()))
  }

  def makeAddressLineSet(nodeSeq: NodeSeq): Seq[AddressLine] = {
    if (nodeSeq.isEmpty) Seq.empty else (nodeSeq collect { case x => makeAddressLine(x) } flatten)
  }

  def makeAddressDetailsOption(nodeSeq: NodeSeq): Option[AddressDetailsType] = {
    if (nodeSeq.isEmpty) None else {
      // just pick the first match
      for (x <- AddressDetailsTypeSet.values) {
        val address = makeAddressDetailsOption(nodeSeq \ x.toString, x)
        if(address.isDefined) return address
      }
    }
    None
  }

//  def makeAddressDetailsOptionSet(nodeSeq: NodeSeq): Seq[AddressDetailsType] = {
//    if (nodeSeq.isEmpty) Seq.empty else
//      (AddressDetailsTypeSet.values.flatMap(x => makeAddressDetailsOption(nodeSeq \ x.toString, x)).toSeq.flatten)
//  }
//
//  def makeAddressDetailsOptions(nodeSeq: NodeSeq, addressType: AddressDetailsTypeSet): Seq[Option[AddressDetailsType]] = {
//    if (nodeSeq.isEmpty) Seq.empty else
//      (nodeSeq collect { case x => makeAddressDetailsOption(x, addressType) }) filter (_ != None)
//  }

  def makeContentType(nodeSeq: NodeSeq): Option[ContentType] = {
    if (nodeSeq.isEmpty) None else Some(new ContentType(
      content = getFromNode[String](nodeSeq),
      objectType = getFromNode[String](nodeSeq \ "@Type"),
      code = getFromNode[String](nodeSeq \ "@Code"),
      attributes = Map()))
  }

  def makeContentTypeSet(nodeSeq: NodeSeq): Seq[ContentType] = {
    if (nodeSeq.isEmpty) Seq.empty else (nodeSeq collect { case x => makeContentType(x) } flatten)
  }

  def makeAddressIdentifier(nodeSeq: NodeSeq): Option[AddressIdentifier] = {
    if (nodeSeq.isEmpty) None else Some(new AddressIdentifier(
      identifierType = getFromNode[String](nodeSeq \ "@IdentifierType"),
      content = getFromNode[String](nodeSeq),
      objectType = getFromNode[String](nodeSeq \ "@Type"),
      code = getFromNode[String](nodeSeq \ "@Code"),
      attributes = Map()))
  }

  def makeAddressIdentifierSet(nodeSeq: NodeSeq): Seq[AddressIdentifier] = {
    if (nodeSeq.isEmpty) Seq.empty else (nodeSeq collect { case x => makeAddressIdentifier(x) } flatten)
  }

  def makeSortingCode(nodeSeq: NodeSeq): Option[SortingCode] = {
    if (nodeSeq.isEmpty) None else Some(new SortingCode(
      objectType = getFromNode[String](nodeSeq \ "@Type"),
      code = getFromNode[String](nodeSeq \ "@Code")))
  }

  def makePostalServiceElements(nodeSeq: NodeSeq): Option[PostalServiceElements] = {
    if (nodeSeq.isEmpty) None
    else Some(new PostalServiceElements(
      addressIdentifier = makeAddressIdentifierSet(nodeSeq),
      endorsementLineCode = makeContentType(nodeSeq),
      keyLineCode = makeContentType(nodeSeq),
      barcode = makeContentType(nodeSeq),
      sortingCode = makeSortingCode(nodeSeq),
      addressLatitude = makeContentType(nodeSeq),
      addressLatitudeDirection = makeContentType(nodeSeq),
      addressLongitude = makeContentType(nodeSeq),
      addressLongitudeDirection = makeContentType(nodeSeq),
      supplementaryPostalServiceData = makeContentTypeSet(nodeSeq),
      any = Seq.empty,
      objectType = getFromNode[String](nodeSeq \ "@Type"),
      attributes = Map()))
  }

}
