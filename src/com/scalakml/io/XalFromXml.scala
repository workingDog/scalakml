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
import com.scalakml.kml.Kml
import scala.Some
import com.scalakml.kml.Kml
import com.scalakml.xAL.AdministrativeAreaTypeSet._
import scala.Some
import com.scalakml.kml.Kml
import scala.Some
import com.scalakml.kml.Kml
import scala.Some
import com.scalakml.kml.Kml
import scala.Some
import com.scalakml.kml.Kml
import com.scalakml.xAL.SortingCode
import com.scalakml.xAL.AdministrativeAreaTypeSet.Locality
import com.scalakml.xAL.Locality
import com.scalakml.xAL.AddressIdentifier
import com.scalakml.xAL.AdministrativeAreaTypeSet
import com.scalakml.xAL.AdministrativeArea
import scala.Some
import com.scalakml.xAL.XAL
import com.scalakml.xAL.AddressDetails
import com.scalakml.xAL.AddressLines
import com.scalakml.xAL.PostalServiceElements
import com.scalakml.xAL.ContentType
import com.scalakml.xAL.SubAdministrativeArea
import com.scalakml.xAL.AddressLine
import com.scalakml.xAL.Address
import com.scalakml.kml.Kml
import javax.xml.namespace.QName
import com.scalakml.xAL.CountryTypeSet._
import com.scalakml.xAL.MailStopNumber
import com.scalakml.xAL.AdministrativeArea
import com.scalakml.xAL.AdministrativeAreaTypeSet.PostOffice
import com.scalakml.xAL.PostOffice
import scala.Some
import com.scalakml.xAL.CountryNameCode
import com.scalakml.xAL.MailStop
import com.scalakml.xAL.Firm
import com.scalakml.xAL.AddressDetails
import com.scalakml.xAL.PostBoxNumberExtension
import com.scalakml.xAL.SubAdministrativeArea
import com.scalakml.xAL.PostBox
import com.scalakml.xAL.PostBoxNumberPrefix
import com.scalakml.xAL.PostBoxNumberSuffix
import com.scalakml.xAL.PostBoxNumber
import com.scalakml.xAL.SortingCode
import com.scalakml.xAL.Country
import com.scalakml.xAL.AdministrativeAreaTypeSet.Locality
import com.scalakml.xAL.Locality
import com.scalakml.xAL.AddressIdentifier
import com.scalakml.xAL.AdministrativeAreaTypeSet.PostalCode
import com.scalakml.xAL.PostalCode
import com.scalakml.xAL.CountryTypeSet
import com.scalakml.xAL.XAL
import com.scalakml.xAL.AddressLines
import com.scalakml.xAL.PostalServiceElements
import com.scalakml.xAL.ContentType
import com.scalakml.xAL.AddressLine
import com.scalakml.xAL.Address
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
object XalFromXml {

  import AddressDetailsTypeSet._
  import CountryTypeSet._
  import AdministrativeAreaTypeSet._
  import LocalityTypeSet._
  import ThoroughfareTypeSet._
  import DependentLocalityTypeSet._
  import PremiseTypeSet2._
  import SubPremiseTypeSet._

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
      addressDetailsOption = makeAddressDetailsType(nodeSeq),
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

  def makeAddressDetailsType(nodeSeq: NodeSeq, addressType: AddressDetailsTypeSet): Option[AddressDetailsType] = {
   if (nodeSeq.isEmpty) None else {
     addressType match {
          case AddressDetailsTypeSet.Address => makeAddress(nodeSeq \ "Address")
          case AddressDetailsTypeSet.AddressLines => makeAddressLines(nodeSeq \ "AddressLines")
          case AddressDetailsTypeSet.AdministrativeArea => makeAdministrativeArea(nodeSeq)
          case AddressDetailsTypeSet.Country => makeCountry(nodeSeq \"Country")
          case AddressDetailsTypeSet.Locality => makeLocality(nodeSeq \"Locality")
          case AddressDetailsTypeSet.Thoroughfare => makeThoroughfare(nodeSeq \"Thoroughfare")
          case _ => None
        }
      }
    }

  def makeCountryNameCode(nodeSeq: NodeSeq): Option[CountryNameCode] = {
    if (nodeSeq.isEmpty) None else Some(new CountryNameCode(
      content = getFromNode[String](nodeSeq),
      scheme = getFromNode[String](nodeSeq \ "@Scheme"),
      code = getFromNode[String](nodeSeq \ "@Code"),
      attributes = Map()))
  }

  def makeCountryNameCodeSet(nodeSeq: NodeSeq): Seq[CountryNameCode] = {
    if (nodeSeq.isEmpty) Seq.empty else (nodeSeq collect { case x => makeCountryNameCode(x \ "CountryNameCode") } flatten)
  }

  def makeCountry(nodeSeq: NodeSeq): Option[Country] = {
    if (nodeSeq.isEmpty) None else Some(new Country(
      addressLine = makeAddressLineSet(nodeSeq),
      countryNameCode = makeCountryNameCodeSet(nodeSeq),
      countryName = makeContentTypeSet(nodeSeq \ "CountryName"),
      countryType = makeCountryType(nodeSeq),
      any = Seq.empty,
      attributes = Map()))
  }

//  case class Thoroughfare(addressLine: Seq[AddressLine] = Nil,
//                          thoroughfareTypeSeq: Seq[ThoroughfareType] = Nil,
//                          thoroughfareNumberPrefix: Seq[ThoroughfareNumberPrefix] = Nil,
//                          thoroughfareNumberSuffix: Seq[ThoroughfareNumberSuffix] = Nil,
//                          thoroughfarePreDirection: Option[ContentType] = None,
//                          thoroughfareLeadingType: Option[ContentType] = None,
//                          thoroughfareName: Seq[ContentType] = Nil,
//                          thoroughfareTrailingType: Option[ContentType] = None,
//                          thoroughfarePostDirection: Option[ContentType] = None,
//                          dependentThoroughfare: Option[DependentThoroughfare] = None,
//                          thoroughfareType: Option[ThoroughfareType] = None,
//                          any: Seq[Any] = Nil,
//                          objectType: Option[String] = None,
//                          dependentThoroughfares: Option[DependentThoroughfares] = None,
//                          dependentThoroughfaresIndicator: Option[String] = None,
//                          dependentThoroughfaresConnector: Option[String] = None,
//                          dependentThoroughfaresType: Option[String] = None,
//                          attributes: Map[String, QName])

  def makeThoroughfare(nodeSeq: NodeSeq): Option[Thoroughfare] = {
    if (nodeSeq.isEmpty) None else Some(new Thoroughfare(
      addressLine = makeAddressLineSet(nodeSeq),
      thoroughfareTypeSeq = makeThoroughfareTypeSeq(nodeSeq),
      thoroughfareNumberPrefix = makeThoroughfareNumberPrefixSet(nodeSeq \ "ThoroughfareNumberPrefix"),
      thoroughfareNumberSuffix = makeThoroughfareNumberSuffixSet(nodeSeq \ "ThoroughfareNumberSuffix"),
      thoroughfarePreDirection = makeContentType(nodeSeq \ "ThoroughfarePreDirection"),
      thoroughfareLeadingType = makeContentType(nodeSeq \ "ThoroughfareLeadingType"),
      thoroughfareName = makeContentTypeSet(nodeSeq \ "ThoroughfareName"),
      thoroughfareTrailingType = makeContentType(nodeSeq \ "ThoroughfareTrailingType"),
      thoroughfarePostDirection = makeContentType(nodeSeq \ "ThoroughfarePostDirection"),
      dependentThoroughfare = makeDependentThoroughfare(nodeSeq \ "DependentThoroughfare"),
      thoroughfareType = makeThoroughfareType(nodeSeq),
      dependentThoroughfares = makeMode[DependentThoroughfares](nodeSeq \ "@DependentThoroughfare", DependentThoroughfares),
      dependentThoroughfaresIndicator = getFromNode[String](nodeSeq \ "@DependentThoroughfaresIndicator"),
      dependentThoroughfaresConnector = getFromNode[String](nodeSeq \ "@DependentThoroughfaresConnector"),
      dependentThoroughfaresType = getFromNode[String](nodeSeq \ "@DependentThoroughfaresType"),
      objectType = getFromNode[String](nodeSeq \ "@Type"),
      any = Seq.empty,
      attributes = Map()))
  }

  def makeDependentThoroughfare(nodeSeq: NodeSeq): Option[DependentThoroughfare] = {
    if (nodeSeq.isEmpty) None else Some(new DependentThoroughfare(
      addressLine = makeAddressLineSet(nodeSeq),
      thoroughfarePreDirection = makeContentType(nodeSeq \ "ThoroughfarePreDirection"),
      thoroughfareLeadingType = makeContentType(nodeSeq \ "ThoroughfareLeadingType"),
      thoroughfareName = makeContentTypeSet(nodeSeq \ "ThoroughfareName"),
      thoroughfareTrailingType = makeContentType(nodeSeq \ "ThoroughfareTrailingType"),
      thoroughfarePostDirection = makeContentType(nodeSeq \ "ThoroughfarePostDirection"),
      any = Seq.empty,
      objectType = getFromNode[String](nodeSeq \ "@Type"),
      attributes = Map()))
  }

  def makeThoroughfareNumberPrefix(nodeSeq: NodeSeq): Option[ThoroughfareNumberPrefix] = {
    if (nodeSeq.isEmpty) None else Some(new ThoroughfareNumberPrefix(
      content = getFromNode[String](nodeSeq),
      numberPrefixSeparator =getFromNode[String](nodeSeq \ "@NumberPrefixSeparator"),
      code = getFromNode[String](nodeSeq \ "@Code"),
      objectType = getFromNode[String](nodeSeq \ "@Type"),
      attributes = Map()))
  }

  def makeThoroughfareNumberPrefixSet(nodeSeq: NodeSeq): Seq[ThoroughfareNumberPrefix] = {
    if (nodeSeq.isEmpty) Seq.empty else (nodeSeq collect { case x => makeThoroughfareNumberPrefix(x \ "ThoroughfareNumberPrefix") } flatten)
  }

  def makeThoroughfareNumberSuffix(nodeSeq: NodeSeq): Option[ThoroughfareNumberSuffix] = {
    if (nodeSeq.isEmpty) None else Some(new ThoroughfareNumberSuffix(
      content = getFromNode[String](nodeSeq),
      numberSuffixSeparator =getFromNode[String](nodeSeq \ "@NumberPrefixSeparator"),
      code = getFromNode[String](nodeSeq \ "@Code"),
      objectType = getFromNode[String](nodeSeq \ "@Type"),
      attributes = Map()))
  }

  def makeThoroughfareNumberSuffixSet(nodeSeq: NodeSeq): Seq[ThoroughfareNumberSuffix] = {
    if (nodeSeq.isEmpty) Seq.empty else (nodeSeq collect { case x => makeThoroughfareNumberSuffix(x \ "ThoroughfareNumberSuffix") } flatten)
  }

  def makeThoroughfareNumber(nodeSeq: NodeSeq): Option[ThoroughfareNumber] = {
    if (nodeSeq.isEmpty) None else Some(new ThoroughfareNumber(
      content = getFromNode[String](nodeSeq),
      numberType = makeMode[NumberType](nodeSeq \ "NumberType", NumberType),
      indicatorOccurrence = makeMode[TypeOccurrence](nodeSeq \ "IndicatorOccurrence", TypeOccurrence),
      numberOccurrence = makeMode[NumberOccurrence](nodeSeq \ "NumberOccurrence", NumberOccurrence),
      code = getFromNode[String](nodeSeq \ "@Code"),
      objectType = getFromNode[String](nodeSeq \ "@Type"),
      indicator = getFromNode[String](nodeSeq \ "@Indicator"),
      attributes = Map()))
  }

  def makeThoroughfareNumberRange(nodeSeq: NodeSeq): Option[ThoroughfareNumberRange] = {
    if (nodeSeq.isEmpty) None else Some(new ThoroughfareNumberRange(
      addressLine = makeAddressLineSet(nodeSeq),
      thoroughfareNumberFrom = makeContentType(nodeSeq \ "ThoroughfareNumberFrom").get,   // <-------need to fix this
      thoroughfareNumberTo = makeContentType(nodeSeq \ "ThoroughfareNumberTo").get,  // <-------need to fix this
      rangeType = makeMode[RangeType](nodeSeq \ "RangeType", RangeType),
      separator = getFromNode[String](nodeSeq \ "@SeparatorType"),
      indicatorOccurrence = makeMode[TypeOccurrence](nodeSeq \ "IndicatorOccurrence", TypeOccurrence),
      numberRangeOccurrence = makeMode[NumberOccurrence](nodeSeq \ "NumberRangeOccurrence", NumberOccurrence),
      objectType = getFromNode[String](nodeSeq \ "@Type"),
      code = getFromNode[String](nodeSeq \ "@Code"),
      indicator = getFromNode[String](nodeSeq \ "@Indicator"),
      attributes = Map()))
  }

  def makeThoroughfareType(nodeSeq: NodeSeq, thoroughfareType: ThoroughfareTypeSet): Option[ThoroughfareType] = {
    if (nodeSeq.isEmpty) None else {
      thoroughfareType match {
        case ThoroughfareTypeSet.PostalCode => makePostalCode(nodeSeq)
        case ThoroughfareTypeSet.Premise => makePremise(nodeSeq)
        case ThoroughfareTypeSet.ThoroughfareNumber => makeThoroughfareNumber(nodeSeq)
        case ThoroughfareTypeSet.ThoroughfareNumberRange => makeThoroughfareNumberRange(nodeSeq)
        case _ => None
      }
    }
  }

  def makeThoroughfareTypeSeq(nodeSeq: NodeSeq): Seq[ThoroughfareType] = {
    if (nodeSeq.isEmpty) Seq.empty else
      (ThoroughfareTypeSet.values.flatMap(x => makeThoroughfareTypes(nodeSeq \ x.toString, x)).toSeq.flatten)
  }

  def makeThoroughfareType(nodeSeq: NodeSeq): Option[ThoroughfareType] = {
    if (nodeSeq.isEmpty) None else {
      // just pick the first match
      for (x <- ThoroughfareTypeSet.values) {
        val thoroughfareType = makeThoroughfareType(nodeSeq \ x.toString, x)
        if(thoroughfareType.isDefined) return thoroughfareType
      }
    }
    None
  }

  def makeThoroughfareTypes(nodeSeq: NodeSeq, thoroughfareType: ThoroughfareTypeSet): Seq[Option[ThoroughfareType]] = {
    if (nodeSeq.isEmpty) Seq.empty else
      (nodeSeq collect { case x => makeThoroughfareType(x, thoroughfareType) }) filter (_ != None)
  }

  def makeCountryType(nodeSeq: NodeSeq, countryType: CountryTypeSet): Option[CountryType] = {
    if (nodeSeq.isEmpty) None else {
      countryType match {
        case CountryTypeSet.AdministrativeArea => makeAdministrativeArea(nodeSeq)
        case CountryTypeSet.Locality => makeLocality(nodeSeq)
        case CountryTypeSet.Thoroughfare => makeThoroughfare(nodeSeq)
        case _ => None
      }
    }
  }

  def makeCountryType(nodeSeq: NodeSeq): Option[CountryType] = {
    if (nodeSeq.isEmpty) None else {
      // just pick the first match
      for (x <- CountryTypeSet.values) {
        val countryType = makeCountryType(nodeSeq \ x.toString, x)
        if(countryType.isDefined) return countryType
      }
    }
    None
  }

  def makePremise(nodeSeq: NodeSeq): Option[Premise] = {
    if (nodeSeq.isEmpty) None else Some(new Premise(
      addressLine = makeAddressLineSet(nodeSeq),
      premiseName = makePremiseNameSet(nodeSeq),
      premiseLocation = makePremiseLocationSet(nodeSeq),
      premiseNumberPrefix = makePremiseNumberPrefixSet(nodeSeq),
      premiseNumberSuffix = makePremiseNumberSuffixSet(nodeSeq),
      buildingName = makeBuildingNameSet(nodeSeq \ "BuildingName"),
      premiseFirmOrSubPremiseType = makePremiseType2Set(nodeSeq),
      mailStop = makeMailStop(nodeSeq \ "MailStop"),
      postalCode = makePostalCode(nodeSeq \ "PostalCode"),
      premise = makePremise(nodeSeq \ "Premise"),
      premiseDependency = getFromNode[String](nodeSeq \ "@PremiseDependency"),
      premiseDependencyType = getFromNode[String](nodeSeq \ "@PremiseDependencyType"),
      premiseThoroughfareConnector = getFromNode[String](nodeSeq \ "@PremiseThoroughfareConnector"),
      any = Seq.empty,
      objectType = getFromNode[String](nodeSeq \ "@Type"),
      attributes = Map()))
  }

  def makePremiseType2(nodeSeq: NodeSeq, premiseType: PremiseTypeSet2): Option[PremiseType2] = {
    if (nodeSeq.isEmpty) None else {
      premiseType match {
        case PremiseTypeSet2.Firm => makeFirm(nodeSeq)
        case PremiseTypeSet2.SubPremise => makeSubPremise(nodeSeq)
        case _ => None
      }
    }
  }

  def makeSubPremise(nodeSeq: NodeSeq): Option[SubPremise] = {
    if (nodeSeq.isEmpty) None else Some(new SubPremise(
      addressLine = makeAddressLineSet(nodeSeq),
      subPremiseName = makeSubPremiseNameSet(nodeSeq),
      subPremiseType = makeSubPremiseTypeSet(nodeSeq),
      subPremiseNumberPrefix = makeSubPremiseNumberPrefixSet(nodeSeq),
      subPremiseNumberSuffix = makeSubPremiseNumberSuffixSet(nodeSeq),
      buildingName = makeBuildingNameSet(nodeSeq \ "BuildingName"),
      firm = makeFirm(nodeSeq \ "Firm"),
      mailStop = makeMailStop(nodeSeq \ "MailStop"),
      postalCode = makePostalCode(nodeSeq \ "PostalCode"),
      subPremise = makeSubPremise(nodeSeq \ "SubPremise"),
      any = Seq.empty,
      objectType = getFromNode[String](nodeSeq \ "@Type"),
      attributes = Map()))
  }

  def makeSubPremiseNumber(nodeSeq: NodeSeq): Option[SubPremiseNumber] = {
    if (nodeSeq.isEmpty) None else Some(new SubPremiseNumber(
      content = getFromNode[String](nodeSeq),
      indicatorOccurrence = makeMode[TypeOccurrence](nodeSeq \ "IndicatorOccurrence", TypeOccurrence),
      numberOccurrence = makeMode[TypeOccurrence](nodeSeq \ "NumberTypeOccurrence", TypeOccurrence),
      premiseNumberSeparator =getFromNode[String](nodeSeq \ "@PremiseNumberSeparator:"),
      code = getFromNode[String](nodeSeq \ "@Code"),
      objectType = getFromNode[String](nodeSeq \ "@Type"),
      indicator = getFromNode[String](nodeSeq \ "@Indicator"),
      attributes = Map()))
  }

  def makeSubPremiseLocation(nodeSeq: NodeSeq): Option[SubPremiseLocation] = {
    if (nodeSeq.isEmpty) None else Some(new SubPremiseLocation(
      content = getFromNode[String](nodeSeq),
      code = getFromNode[String](nodeSeq \ "@Code")))
  }

  def makeSubPremiseType(nodeSeq: NodeSeq): Option[SubPremiseType] = {
    if (nodeSeq.isEmpty) None else {
      // just pick the first match
      for (x <- SubPremiseTypeSet.values) {
        val premiseType = makeSubPremiseType(nodeSeq \ x.toString, x)
        if(premiseType.isDefined) return premiseType
      }
    }
    None
  }

  def makeSubPremiseTypeSet(nodeSeq: NodeSeq): Seq[SubPremiseType] = {
    if (nodeSeq.isEmpty) Seq.empty else
      (SubPremiseTypeSet.values.flatMap(x => makeSubPremiseType(nodeSeq \ x.toString, x)).toSeq)
  }

  def makeSubPremiseType(nodeSeq: NodeSeq, premiseType: SubPremiseTypeSet): Option[SubPremiseType] = {
    if (nodeSeq.isEmpty) None else {
      premiseType match {
        case SubPremiseTypeSet.SubPremiseLocation => makeSubPremiseLocation(nodeSeq)
        case SubPremiseTypeSet.SubPremiseNumber => makeSubPremiseNumber(nodeSeq)
        case _ => None
      }
    }
  }

  def makeSubPremiseNumberPrefix(nodeSeq: NodeSeq): Option[SubPremiseNumberPrefix] = {
    if (nodeSeq.isEmpty) None else Some(new SubPremiseNumberPrefix(
      content = getFromNode[String](nodeSeq),
      numberPrefixSeparator =getFromNode[String](nodeSeq \ "@NumberPrefixSeparator"),
      code = getFromNode[String](nodeSeq \ "@Code"),
      objectType = getFromNode[String](nodeSeq \ "@Type"),
      attributes = Map()))
  }

  def makeSubPremiseNumberPrefixSet(nodeSeq: NodeSeq): Seq[SubPremiseNumberPrefix] = {
    if (nodeSeq.isEmpty) Seq.empty else (nodeSeq collect { case x => makeSubPremiseNumberPrefix(x \ "SubPremiseNumberPrefix") } flatten)
  }

  def makeSubPremiseNumberSuffix(nodeSeq: NodeSeq): Option[SubPremiseNumberSuffix] = {
    if (nodeSeq.isEmpty) None else Some(new SubPremiseNumberSuffix(
      content = getFromNode[String](nodeSeq),
      numberSuffixSeparator =getFromNode[String](nodeSeq \ "@NumberPrefixSeparator"),
      code = getFromNode[String](nodeSeq \ "@Code"),
      objectType = getFromNode[String](nodeSeq \ "@Type"),
      attributes = Map()))
  }

  def makeSubPremiseNumberSuffixSet(nodeSeq: NodeSeq): Seq[SubPremiseNumberSuffix] = {
    if (nodeSeq.isEmpty) Seq.empty else (nodeSeq collect { case x => makeSubPremiseNumberSuffix(x \ "SubPremiseNumberSuffix") } flatten)
  }

  def makeSubPremiseName(nodeSeq: NodeSeq): Option[SubPremiseName] = {
    if (nodeSeq.isEmpty) None else Some(new SubPremiseName(
      content = getFromNode[String](nodeSeq),
      objectType = getFromNode[String](nodeSeq \ "@Type"),
      typeOccurrence = makeMode[TypeOccurrence](nodeSeq \  "TypeOccurrence", TypeOccurrence),
      code = getFromNode[String](nodeSeq \ "@Code"),
      attributes = Map()))
  }

  def makeSubPremiseNameSet(nodeSeq: NodeSeq): Seq[SubPremiseName] = {
    if (nodeSeq.isEmpty) Seq.empty else (nodeSeq collect { case x => makeSubPremiseName(x \ "SubPremiseName") } flatten)
  }

  def makePremiseType2(nodeSeq: NodeSeq): Option[PremiseType2] = {
    if (nodeSeq.isEmpty) None else {
      // just pick the first match
      for (x <- PremiseTypeSet2.values) {
        val premiseType = makePremiseType2(nodeSeq \ x.toString, x)
        if(premiseType.isDefined) return premiseType
      }
    }
    None
  }

  def makePremiseType2Set(nodeSeq: NodeSeq): Seq[PremiseType2] = {
    if (nodeSeq.isEmpty) Seq.empty else
      (PremiseTypeSet2.values.flatMap(x => makePremiseType2(nodeSeq \ x.toString, x)).toSeq)
  }

  def makePremiseLocation(nodeSeq: NodeSeq): Option[PremiseLocation] = {
    if (nodeSeq.isEmpty) None else Some(new PremiseLocation(
      content = getFromNode[String](nodeSeq),
      code = getFromNode[String](nodeSeq \ "@Code"),
      attributes = Map()))
  }

  def makePremiseLocationSet(nodeSeq: NodeSeq): Seq[PremiseLocation] = {
    if (nodeSeq.isEmpty) Seq.empty else (nodeSeq collect { case x => makePremiseLocation(x \ "PremiseLocation") } flatten)
  }

def makePremiseNumberPrefix(nodeSeq: NodeSeq): Option[PremiseNumberPrefix] = {
  if (nodeSeq.isEmpty) None else Some(new PremiseNumberPrefix(
    content = getFromNode[String](nodeSeq),
    numberPrefixSeparator =getFromNode[String](nodeSeq \ "@NumberPrefixSeparator"),
    code = getFromNode[String](nodeSeq \ "@Code"),
    objectType = getFromNode[String](nodeSeq \ "@Type"),
    attributes = Map()))
}

  def makePremiseNumberPrefixSet(nodeSeq: NodeSeq): Seq[PremiseNumberPrefix] = {
    if (nodeSeq.isEmpty) Seq.empty else (nodeSeq collect { case x => makePremiseNumberPrefix(x \ "PremiseNumberPrefix") } flatten)
  }

  def makePremiseNumberSuffix(nodeSeq: NodeSeq): Option[PremiseNumberSuffix] = {
    if (nodeSeq.isEmpty) None else Some(new PremiseNumberSuffix(
      content = getFromNode[String](nodeSeq),
      numberSuffixSeparator =getFromNode[String](nodeSeq \ "@NumberPrefixSeparator"),
      code = getFromNode[String](nodeSeq \ "@Code"),
      objectType = getFromNode[String](nodeSeq \ "@Type"),
      attributes = Map()))
  }

  def makePremiseNumberSuffixSet(nodeSeq: NodeSeq): Seq[PremiseNumberSuffix] = {
    if (nodeSeq.isEmpty) Seq.empty else (nodeSeq collect { case x => makePremiseNumberSuffix(x \ "PremiseNumberSuffix") } flatten)
  }

def makePremiseName(nodeSeq: NodeSeq): Option[PremiseName] = {
  if (nodeSeq.isEmpty) None else Some(new PremiseName(
    content = getFromNode[String](nodeSeq),
    objectType = getFromNode[String](nodeSeq \ "@Type"),
    typeOccurrence = makeMode[TypeOccurrence](nodeSeq \  "TypeOccurrence", TypeOccurrence),
    code = getFromNode[String](nodeSeq \ "@Code"),
    attributes = Map()))
}

  def makePremiseNameSet(nodeSeq: NodeSeq): Seq[PremiseName] = {
    if (nodeSeq.isEmpty) Seq.empty else (nodeSeq collect { case x => makePremiseName(x \ "PremiseName") } flatten)
  }

  def makeAdministrativeArea(nodeSeq: NodeSeq): Option[AdministrativeArea] = {
    if (nodeSeq.isEmpty) None else Some(new AdministrativeArea(
      addressLine = makeAddressLineSet(nodeSeq),
      administrativeAreaName = makeContentTypeSet(nodeSeq),
      subAdministrativeArea = makeSubAdministrativeArea(nodeSeq \ "SubAdministrativeArea"),
      administrativeAreaType = makeAdministrativeAreaType(nodeSeq \ "AdministrativeArea"),
      any = Seq.empty,
      objectType = getFromNode[String](nodeSeq \ "@Type"),
      usageType = getFromNode[String](nodeSeq \ "@UsageType"),
      indicator = getFromNode[String](nodeSeq \ "@Indicator"),
      attributes = Map()))
  }

  def makeSubAdministrativeArea(nodeSeq: NodeSeq): Option[SubAdministrativeArea] = {
    if (nodeSeq.isEmpty) None else Some(new SubAdministrativeArea(
      addressLine = makeAddressLineSet(nodeSeq),
      subAdministrativeAreaName = makeContentTypeSet(nodeSeq),
      subAdministrativeAreaType = makeAdministrativeAreaType(nodeSeq \ "AdministrativeArea"),
      objectType = getFromNode[String](nodeSeq \ "@Type"),
      usageType = getFromNode[String](nodeSeq \ "@UsageType"),
      indicator = getFromNode[String](nodeSeq \ "@Indicator"),
      any = Seq.empty,
      attributes = Map()))
  }

  def makeAdministrativeAreaType(nodeSeq: NodeSeq, addminType: AdministrativeAreaTypeSet): Option[AdministrativeAreaType] = {
    if (nodeSeq.isEmpty) None else {
      addminType match {
        case AdministrativeAreaTypeSet.Locality => makeLocality(nodeSeq)
        case AdministrativeAreaTypeSet.PostalCode => makePostalCode(nodeSeq)
        case AdministrativeAreaTypeSet.PostOffice => makePostOffice(nodeSeq)
        case _ => None
      }
    }
  }

  def makeAdministrativeAreaType(nodeSeq: NodeSeq): Option[AdministrativeAreaType] = {
    if (nodeSeq.isEmpty) None else {
      // just pick the first match
      for (x <- AdministrativeAreaTypeSet.values) {
        val addminType = makeAdministrativeAreaType(nodeSeq \ x.toString, x)
        if(addminType.isDefined) return addminType
      }
    }
    None
  }

  def makeLocality(nodeSeq: NodeSeq): Option[Locality] = {
    if (nodeSeq.isEmpty) None else Some(new Locality(
      addressLine = makeAddressLineSet(nodeSeq),
      localityName = makeContentTypeSet(nodeSeq),
      localityType = makeLocalityType(nodeSeq),
      thoroughfare = makeThoroughfare(nodeSeq),
      premise = makePremise(nodeSeq \ "Premise"),
      dependentLocality = makeDependentLocality(nodeSeq \ "DependentLocality"),
      postalCode = makePostalCode(nodeSeq),
      objectType = getFromNode[String](nodeSeq \ "@Type"),
      usageType = getFromNode[String](nodeSeq \ "@UsageType"),
      indicator = getFromNode[String](nodeSeq \ "@Indicator"),
      any = Seq.empty,
      attributes = Map()))
  }

  def makeDependentLocality(nodeSeq: NodeSeq): Option[DependentLocality] = {
    if (nodeSeq.isEmpty) None else Some(new DependentLocality(
      addressLine = makeAddressLineSet(nodeSeq),
      dependentLocalityName = makeContentTypeSet(nodeSeq),
      dependentLocalityNumber = makeDependentLocalityNumber(nodeSeq),
      dependentLocalityType = makeDependentLocalityType(nodeSeq),
      thoroughfare = makeThoroughfare(nodeSeq),
      premise = makePremise(nodeSeq \ "Premise"),
      dependentLocality = makeDependentLocality(nodeSeq \ "DependentLocality"),
      postalCode = makePostalCode(nodeSeq),
      objectType = getFromNode[String](nodeSeq \ "@Type"),
      usageType = getFromNode[String](nodeSeq \ "@UsageType"),
      indicator = getFromNode[String](nodeSeq \ "@Indicator"),
      any = Seq.empty,
      attributes = Map()))
  }

  def makeDependentLocalityType(nodeSeq: NodeSeq): Option[DependentLocalityType] = {
    if (nodeSeq.isEmpty) None else {
      // just pick the first match
      for (x <- DependentLocalityTypeSet.values) {
        val localType = makeDependentLocalityType(nodeSeq \ x.toString, x)
        if(localType.isDefined) return localType
      }
    }
    None
  }

  def makeDependentLocalityType(nodeSeq: NodeSeq, localType: DependentLocalityTypeSet): Option[DependentLocalityType] = {
    if (nodeSeq.isEmpty) None else {
      localType match {
        case DependentLocalityTypeSet.LargeMailUser => makeLargeMailUser(nodeSeq)
        case DependentLocalityTypeSet.PostalRoute => makePostalRoute(nodeSeq)
        case DependentLocalityTypeSet.PostBox => makePostBox(nodeSeq)
        case DependentLocalityTypeSet.PostOffice => makePostOffice(nodeSeq)
        case _ => None
      }
    }
  }

  def makeDependentLocalityNumber(nodeSeq: NodeSeq): Option[DependentLocalityNumber] = {
    if (nodeSeq.isEmpty) None else Some(new DependentLocalityNumber(
      content = getFromNode[String](nodeSeq),
      nameNumberOccurrence = makeMode[TypeOccurrence](nodeSeq \ "NumberOccurrence", TypeOccurrence),
      code = getFromNode[String](nodeSeq \ "@Code"),
      attributes = Map()))
  }

  def makePostalCode(nodeSeq: NodeSeq): Option[PostalCode] = {
    if (nodeSeq.isEmpty) None else Some(new PostalCode(
      addressLine = makeAddressLineSet(nodeSeq),
      postalCodeNumber = makeContentTypeSet(nodeSeq),
      postalCodeNumberExtension = makePostalCodeNumberExtensionSet(nodeSeq),
      objectType = getFromNode[String](nodeSeq \ "@Type"),
      any = Seq.empty,
      attributes = Map()))
  }

def makePostalCodeNumberExtension(nodeSeq: NodeSeq): Option[PostalCodeNumberExtension] = {
  if (nodeSeq.isEmpty) None else Some(new PostalCodeNumberExtension(
    content = getFromNode[String](nodeSeq),
    numberExtensionSeparator = getFromNode[String](nodeSeq \ "@NumberExtensionSeparator"),
    code = getFromNode[String](nodeSeq \ "@Code"),
    objectType = getFromNode[String](nodeSeq \ "@Type"),
    attributes = Map()))
}

  def makePostalCodeNumberExtensionSet(nodeSeq: NodeSeq): Seq[PostalCodeNumberExtension] = {
    if (nodeSeq.isEmpty) Seq.empty else (nodeSeq collect { case x => makePostalCodeNumberExtension(x \ "makePostalCodeNumberExtension") } flatten)
  }

  def makePostOffice(nodeSeq: NodeSeq): Option[PostOffice] = {
    if (nodeSeq.isEmpty) None else Some(new PostOffice(
      addressLine = makeAddressLineSet(nodeSeq),
      postOfficeNumber = makePostOfficeNumberSet(nodeSeq),
      postalRoute = makePostalRoute(nodeSeq\ "PostalRoute"),
      postBox = makePostBox(nodeSeq \ "PostalBox"),
      postalCode = makePostalCode(nodeSeq \ "PostalCode"),
      any = Seq.empty,
      objectType = getFromNode[String](nodeSeq \ "@Type"),
      indicator = getFromNode[String](nodeSeq \ "@Indicator"),
      attributes = Map()))
  }

  def makePostOfficeNumber(nodeSeq: NodeSeq): Option[PostOfficeNumber] = {
    if (nodeSeq.isEmpty) None else Some(new PostOfficeNumber(
      content = getFromNode[String](nodeSeq),
      indicator = getFromNode[String](nodeSeq \ "@Indicator"),
      indicatorOccurrence = makeMode[TypeOccurrence](nodeSeq \ "IndicatorOccurrence", TypeOccurrence),
      code = getFromNode[String](nodeSeq \ "@Code"),
      attributes = Map()))
  }

  def makePostOfficeNumberSet(nodeSeq: NodeSeq): Seq[PostOfficeNumber] = {
    if (nodeSeq.isEmpty) Seq.empty else (nodeSeq collect { case x => makePostOfficeNumber(x \ "PostOfficeNumber") } flatten)
  }

  def makePostBox(nodeSeq: NodeSeq): Option[PostBox] = {
    if (nodeSeq.isEmpty) None else Some(new PostBox(
      addressLine = makeAddressLineSet(nodeSeq),
      postBoxNumber = makePostBoxNumber(nodeSeq \ "PostBoxNumber"),
      postBoxNumberPrefix = makePostBoxNumberPrefix(nodeSeq \ "PostBoxNumberPrefix"),
      postBoxNumberSuffix = makePostBoxNumberSuffix(nodeSeq \ "PostBoxNumberSuffix"),
      postBoxNumberExtension = makePostBoxNumberExtension(nodeSeq),
      firm = makeFirm(nodeSeq \ "Firm"),
      postalCode = makePostalCode(nodeSeq \ "PostalCode"),
      any = Seq.empty,
      objectType = getFromNode[String](nodeSeq \ "@Type"),
      indicator = getFromNode[String](nodeSeq \ "@Indicator"),
      attributes = Map()))
  }

  def makeFirm(nodeSeq: NodeSeq): Option[Firm] = {
    if (nodeSeq.isEmpty) None else Some(new Firm(
      addressLine = makeAddressLineSet(nodeSeq),
      firmName = makeContentTypeSet(nodeSeq),
      department = makeDepartmentSet(nodeSeq),
      mailStop = makeMailStop(nodeSeq \ "MailStop"),
      postalCode = makePostalCode(nodeSeq \ "PostalCode"),
      objectType = getFromNode[String](nodeSeq \ "@Type"),
      any = Seq.empty,
      attributes = Map()))
  }

  def makeMailStopNumber(nodeSeq: NodeSeq): Option[MailStopNumber] = {
    if (nodeSeq.isEmpty) None else Some(new MailStopNumber(
      content = getFromNode[String](nodeSeq),
      nameNumberSeparator = getFromNode[String](nodeSeq \ "@NameNumberSeparator"),
      code = getFromNode[String](nodeSeq \ "@Code"),
      attributes = Map()))
  }

  def makeMailStop(nodeSeq: NodeSeq): Option[MailStop] = {
    if (nodeSeq.isEmpty) None else Some(new MailStop(
      addressLine = makeAddressLineSet(nodeSeq),
      mailStopName = makeContentType(nodeSeq \ "MailStopName"),
      mailStopNumber = makeMailStopNumber(nodeSeq \ "MailStopNumber"),
      objectType = getFromNode[String](nodeSeq \ "@Type"),
      any = Seq.empty,
      attributes = Map()))
  }

  def makePostBoxNumberExtension(nodeSeq: NodeSeq): Option[PostBoxNumberExtension] = {
    if (nodeSeq.isEmpty) None else Some(new PostBoxNumberExtension(
      content = getFromNode[String](nodeSeq),
      numberExtensionSeparator = getFromNode[String](nodeSeq \ "@NumberExtensionSeparator"),
      code = getFromNode[String](nodeSeq \ "@Code"),
      attributes = Map()))
  }

  def makePostBoxNumberSuffix(nodeSeq: NodeSeq): Option[PostBoxNumberSuffix] = {
    if (nodeSeq.isEmpty) None else Some(new PostBoxNumberSuffix(
      content = getFromNode[String](nodeSeq),
      numberSuffixSeparator = getFromNode[String](nodeSeq \ "@NumberSuffixSeparator"),
      code = getFromNode[String](nodeSeq \ "@Code"),
      attributes = Map()))
  }

  def makePostBoxNumberPrefix(nodeSeq: NodeSeq): Option[PostBoxNumberPrefix] = {
    if (nodeSeq.isEmpty) None else Some(new PostBoxNumberPrefix(
      content = getFromNode[String](nodeSeq),
      numberPrefixSeparator = getFromNode[String](nodeSeq \ "@NumberPrefixSeparator"),
      code = getFromNode[String](nodeSeq \ "@Code"),
      attributes = Map()))
  }

  def makePostBoxNumber(nodeSeq: NodeSeq): PostBoxNumber = {
    if (nodeSeq.isEmpty) null else new PostBoxNumber(
      content = getFromNode[String](nodeSeq),
      code = getFromNode[String](nodeSeq \ "@Code"),
      objectType = getFromNode[String](nodeSeq \ "@Type"),
      attributes = Map())
  }

  def makePostalRoute(nodeSeq: NodeSeq): Option[PostalRoute] = {
    if (nodeSeq.isEmpty) None else Some(new PostalRoute(
      addressLine = makeAddressLineSet(nodeSeq),
      postalRouteName = makeContentType(nodeSeq \ "PostalRouteName"),
      postalRouteNumber = makeContentType(nodeSeq \ "PostalRouteNumber"),
      postBox = makePostBox(nodeSeq \ "PostBox"),
      objectType = getFromNode[String](nodeSeq \ "@Type"),
      any = Seq.empty,
      attributes = Map()))
  }

  def makeBuildingName(nodeSeq: NodeSeq): Option[BuildingName] = {
    if (nodeSeq.isEmpty) None else Some(new BuildingName(
      content = getFromNode[String](nodeSeq),
      objectType = getFromNode[String](nodeSeq \ "@Type"),
      typeOccurrence = makeMode[TypeOccurrence](nodeSeq \  "TypeOccurrence", TypeOccurrence),
      code = getFromNode[String](nodeSeq \ "@Code"),
      attributes = Map()))
  }

  def makeBuildingNameSet(nodeSeq: NodeSeq): Seq[BuildingName] = {
    if (nodeSeq.isEmpty) Seq.empty else (nodeSeq collect { case x => makeBuildingName(x) } flatten)
  }

  def makeDepartment(nodeSeq: NodeSeq): Option[Department] = {
    if (nodeSeq.isEmpty) None else Some(new Department(
      addressLine = makeAddressLineSet(nodeSeq),
      departmentName = makeContentTypeSet(nodeSeq \ "DepartmentName"),
      mailStop = makeMailStop(nodeSeq \ "MailStop"),
      postalCode = makePostalCode(nodeSeq \ "PostalCode"),
      objectType = getFromNode[String](nodeSeq \ "@Type"),
      any = Seq.empty,
      attributes = Map()))
  }

  def makeDepartmentSet(nodeSeq: NodeSeq): Seq[Department] = {
    if (nodeSeq.isEmpty) Seq.empty else (nodeSeq collect { case x => makeDepartment(x \ "Department") } flatten)
  }

  def makeLargeMailUser(nodeSeq: NodeSeq): Option[LargeMailUser] = {
    if (nodeSeq.isEmpty) None else Some(new LargeMailUser(
      addressLine = makeAddressLineSet(nodeSeq),
      largeMailUserName = makeContentTypeSet(nodeSeq \ "LargeMailUserName"),
      largeMailUserIdentifier = makeLargeMailUserIdentifier(nodeSeq \ "LargeMailUserIdentifier"),
      buildingName = makeBuildingNameSet(nodeSeq \ "BuildingName"),
      department = makeDepartment(nodeSeq \ "Department"),
      postBox = makePostBox(nodeSeq \ "PostBox"),
      thoroughfare = makeThoroughfare(nodeSeq \"Thoroughfare"),
      postalCode = makePostalCode(nodeSeq),
      objectType = getFromNode[String](nodeSeq \ "@Type"),
      any = Seq.empty,
      attributes = Map()))
  }

  def makeLargeMailUserIdentifier(nodeSeq: NodeSeq): Option[LargeMailUserIdentifier] = {
    if (nodeSeq.isEmpty) None else Some(new LargeMailUserIdentifier(
      content = getFromNode[String](nodeSeq),
      objectType = getFromNode[String](nodeSeq \ "@Type"),
      indicator = getFromNode[String](nodeSeq \ "@Indicator"),
      code = getFromNode[String](nodeSeq \ "@Code"),
      attributes = Map()))
  }

  def makeLocalityType(nodeSeq: NodeSeq, localType: LocalityTypeSet): Option[LocalityType] = {
    if (nodeSeq.isEmpty) None else {
      localType match {
        case LocalityTypeSet.PostOffice => makePostOffice(nodeSeq)
        case LocalityTypeSet.PostBox => makePostBox(nodeSeq)
        case LocalityTypeSet.PostalRoute => makePostalRoute(nodeSeq)
        case LocalityTypeSet.LargeMailUser => makeLargeMailUser(nodeSeq)
        case _ => None
      }
    }
  }

  def makeLocalityType(nodeSeq: NodeSeq): Option[LocalityType] = {
    if (nodeSeq.isEmpty) None else {
      // just pick the first match
      for (x <- LocalityTypeSet.values) {
        val localType = makeLocalityType(nodeSeq \ x.toString, x)
        if(localType.isDefined) return localType
      }
    }
    None
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
    if (nodeSeq.isEmpty) Seq.empty else (nodeSeq collect { case x => makeAddressLine(x \ "AddressLine") } flatten)
  }

  def makeAddressDetailsType(nodeSeq: NodeSeq): Option[AddressDetailsType] = {
    if (nodeSeq.isEmpty) None else {
      // just pick the first match
      for (x <- AddressDetailsTypeSet.values) {
        val address = makeAddressDetailsType(nodeSeq \ x.toString, x)
        if(address.isDefined) return address
      }
    }
    None
  }

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
      addressIdentifier = makeAddressIdentifierSet(nodeSeq \ "AddressIdentifier"),
      endorsementLineCode = makeContentType(nodeSeq \ "EndoresementLineCode"),
      keyLineCode = makeContentType(nodeSeq \ "KeyLineCode"),
      barcode = makeContentType(nodeSeq \ "Barcode"),
      sortingCode = makeSortingCode(nodeSeq \ "SortingCode"),
      addressLatitude = makeContentType(nodeSeq \ "AddressLatitude"),
      addressLatitudeDirection = makeContentType(nodeSeq \ "AddressLatitudeDirection"),
      addressLongitude = makeContentType(nodeSeq \ "AddressLongtitude"),
      addressLongitudeDirection = makeContentType(nodeSeq \ "AddressLongtitudeDirection"),
      supplementaryPostalServiceData = makeContentTypeSet(nodeSeq \"SupplementaryPostalServiceData"),
      any = Seq.empty,
      objectType = getFromNode[String](nodeSeq \ "@Type"),
      attributes = Map()))
  }

}
