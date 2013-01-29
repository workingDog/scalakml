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

package com.scalakml.xAL

import javax.xml.namespace.QName

/**
 * Author: Ringo Wathelet Jan 2013
 *
 * Modified classes from the generated by <a href="http://scalaxb.org/">scalaxb</a>.
 *
 * reference: https://www.oasis-open.org/committees/ciq/ciq.html#6
 *
 */

/** xAL: eXtensible Address Language 
This is an XML document type definition (DTD) for
defining addresses.
Original Date of Creation: 1 March 2001
Copyright(c) 2000, OASIS. All Rights Reserved [http://www.oasis-open.org]
Contact: Customer Information Quality Technical Committee, OASIS
http://www.oasis-open.org/committees/ciq
VERSION: 2.0 [MAJOR RELEASE] Date of Creation: 01 May 2002
Last Update: 24 July 2002
Previous Version: 1.3
  */

object AddressDetailsTypeSet extends Enumeration {
  type AddressDetailsTypeSet = Value
  val Address, AddressLines, Country, AdministrativeArea, Locality, Thoroughfare = Value
}

object CountryTypeSet extends Enumeration {
  type CountryTypeSet = Value
  val Country, AdministrativeArea, Locality, Thoroughfare = Value
}

object DependentLocalityTypeSet extends Enumeration {
  type DependentLocalityTypeSet = Value
  val LargeMailUserType, PostalRouteType, PostOffice, PostBox = Value
}


case class XAL(addressDetails: Seq[AddressDetails] = Nil,
               any: Seq[Any] = Nil,
               version: Option[String] = None,
               attributes: Map[String, QName]) {

  def this() = this(Nil, Nil, None, Map())
}

case class ContentType(content: Option[String] = None,
                       objectType: Option[String] = None,  // attribute
                       code: Option[String] = None,        // attribute
                       attributes: Map[String, QName])     // attribute

case class AddressIdentifier(content: Option[String] = None,
                             identifierType: Option[String] = None,
                             objectType: Option[String] = None,
                             code: Option[String] = None,
                             attributes: Map[String, QName])

case class SortingCode(objectType: Option[String] = None,
                       code: Option[String] = None)


case class PostalServiceElements(addressIdentifier: Seq[AddressIdentifier] = Nil,
                                 endorsementLineCode: Option[ContentType] = None,
                                 keyLineCode: Option[ContentType] = None,
                                 barcode: Option[ContentType] = None,
                                 sortingCode: Option[SortingCode] = None,
                                 addressLatitude: Option[ContentType] = None,
                                 addressLatitudeDirection: Option[ContentType] = None,
                                 addressLongitude: Option[ContentType] = None,
                                 addressLongitudeDirection: Option[ContentType] = None,
                                 supplementaryPostalServiceData: Seq[ContentType] = Nil,
                                 any: Seq[Any] = Nil,
                                 objectType: Option[String] = None,
                                 attributes: Map[String, QName])


case class Address(content: Option[String] = None,
                   objectType: Option[String] = None,
                   code: Option[String] = None,
                   attributes: Map[String, QName]) extends AddressDetailsType {

  def this() = this(None, None, None, Map())
}

case class CountryNameCode(content: Option[String] = None,
                           scheme: Option[String] = None,
                           code: Option[String] = None,
                           attributes: Map[String, QName])


case class Country(addressLine: Seq[AddressLine] = Nil,
                   countryNameCode: Seq[CountryNameCode] = Nil,
                   countryName: Seq[ContentType] = Nil,
                   countryOption: Option[CountryType] = None,
                   any: Seq[Any] = Nil,
                   attributes: Map[String, QName]) extends AddressDetailsType {

  def this() = this(Nil, Nil, Nil, None, Nil, Map())
}

trait CountryType

case class AddressDetails(postalServiceElements: Option[PostalServiceElements] = None,
                          addressDetailsOption: Option[AddressDetailsType] = None,
                          addressType: Option[String] = None,
                          currentStatus: Option[String] = None,
                          validFromDate: Option[String] = None,
                          validToDate: Option[String] = None,
                          usage: Option[String] = None,
                          code: Option[String] = None,
                          addressDetailsKey: Option[String] = None,
                          attributes: Map[String, QName] = Map(),
                          any: Seq[Any] = Nil) {

  def this() = this(None, None, None, None, None, None, None, None, None, Map(), Nil)
}

trait AddressDetailsType

case class AddressLines(addressLines: Seq[AddressLine] = Nil,
                        any: Seq[Any] = Nil,
                        attributes: Map[String, QName]) extends AddressDetailsType {

  def this() = this(Nil, Nil, Map())
}

trait TypeOccurrence

object TypeOccurrence {
  def fromString(value: String): TypeOccurrence = value match {
    case "Before" => Before
    case "After" => After

  }
}

case object Before extends TypeOccurrence {
  override def toString = "Before"
}

case object After extends TypeOccurrence {
  override def toString = "After"
}


case class BuildingName(content: Option[String] = None,
                        objectType: Option[String] = None,
                        typeOccurrence: Option[TypeOccurrence] = None,
                        code: Option[String] = None,
                        attributes: Map[String, QName])


case class DependentLocalityNumber(content: Option[String] = None,
                                   nameNumberOccurrence: Option[TypeOccurrence] = None,
                                   code: Option[String] = None,
                                   attributes: Map[String, QName])


case class DependentLocality(addressLine: Seq[AddressLine] = Nil,
                             dependentLocalityName: Seq[ContentType] = Nil,
                             dependentLocalityNumber: Option[DependentLocalityNumber] = None,
                             dependentLocalityType: Option[DependentLocalityType] = None,
                             thoroughfare: Option[Thoroughfare] = None,
                             premise: Option[Premise] = None,
                             dependentLocality: Option[DependentLocality] = None,
                             postalCode: Option[PostalCode] = None,
                             any: Seq[Any] = Nil,
                             objectType: Option[String] = None,
                             usageType: Option[String] = None,
                             connector: Option[String] = None,
                             indicator: Option[String] = None,
                             attributes: Map[String, QName]) extends ThoroughfareType

trait DependentLocalityType

case class Firm(addressLine: Seq[AddressLine] = Nil,
                    firmName: Seq[ContentType] = Nil,
                    department: Seq[Department] = Nil,
                    mailStop: Option[MailStop] = None,
                    postalCode: Option[PostalCode] = None,
                    any: Seq[Any] = Nil,
                    objectType: Option[String] = None,
                    attributes: Map[String, QName]) extends ThoroughfareType with PremiseType

case class LargeMailUserIdentifier(content: Option[String] = None,
                                   objectType: Option[String] = None,
                                   indicator: Option[String] = None,
                                   code: Option[String] = None,
                                   attributes: Map[String, QName])


case class LargeMailUser(addressLine: Seq[AddressLine] = Nil,
                             largeMailUserName: Seq[ContentType] = Nil,
                             largeMailUserIdentifier: Option[LargeMailUserIdentifier] = None,
                             buildingName: Seq[BuildingName] = Nil,
                             department: Option[Department] = None,
                             postBox: Option[PostBox] = None,
                             thoroughfare: Option[Thoroughfare] = None,
                             postalCode: Option[PostalCode] = None,
                             any: Seq[Any] = Nil,
                             objectType: Option[String] = None,
                             attributes: Map[String, QName]) extends DependentLocalityType with LocalityType


case class MailStopNumber(content: Option[String] = None,
                          nameNumberSeparator: Option[String] = None,
                          code: Option[String] = None,
                          attributes: Map[String, QName])


case class MailStop(addressLine: Seq[AddressLine] = Nil,
                        mailStopName: Option[ContentType] = None,
                        mailStopNumber: Option[MailStopNumber] = None,
                        any: Seq[Any] = Nil,
                        objectType: Option[String] = None,
                        attributes: Map[String, QName])


case class PostalRoute(addressLine: Seq[AddressLine] = Nil,
                           postalRouteTypeOption: Seq[PostalRouteType] = Nil,
                           postBox: Option[PostBox] = None,
                           any: Seq[Any] = Nil,
                           objectType: Option[String] = None,
                           attributes: Map[String, QName]) extends DependentLocalityType with LocalityType

trait PostalRouteType


case class SubPremiseName(content: Option[String] = None,
                          objectType: Option[String] = None,
                          typeOccurrence: Option[TypeOccurrence] = None,
                          code: Option[String] = None,
                          attributes: Map[String, QName])


case class SubPremiseLocation(content: Option[String] = None,
                              code: Option[String] = None) extends SubPremiseType

case class SubPremiseNumber(content: Option[String] = None,
                            indicator: Option[String] = None,
                            indicatorOccurrence: Option[TypeOccurrence] = None,
                            numberTypeOccurrence: Option[TypeOccurrence] = None,
                            premiseNumberSeparator: Option[String] = None,
                            objectType: Option[String] = None,
                            code: Option[String] = None,
                            attributes: Map[String, QName]) extends SubPremiseType


case class SubPremiseNumberPrefix(content: Option[String] = None,
                                  numberPrefixSeparator: Option[String] = None,
                                  objectType: Option[String] = None,
                                  code: Option[String] = None,
                                  attributes: Map[String, QName])


case class SubPremiseNumberSuffix(content: Option[String] = None,
                                  numberSuffixSeparator: Option[String] = None,
                                  objectType: Option[String] = None,
                                  code: Option[String] = None,
                                  attributes: Map[String, QName])


case class SubPremise(addressLine: Seq[AddressLine] = Nil,
                          subPremiseName: Seq[SubPremiseName] = Nil,
                          subPremiseTypeOption: Seq[SubPremiseType] = Nil,
                          subPremiseNumberPrefix: Seq[SubPremiseNumberPrefix] = Nil,
                          subPremiseNumberSuffix: Seq[SubPremiseNumberSuffix] = Nil,
                          buildingName: Seq[BuildingName] = Nil,
                          firm: Option[Firm] = None,
                          mailStop: Option[MailStop] = None,
                          postalCode: Option[PostalCode] = None,
                          subPremise: Option[SubPremise] = None,
                          any: Seq[Any] = Nil,
                          objectType: Option[String] = None,
                          attributes: Map[String, QName]) extends PremiseType

trait SubPremiseType


case class AddressLine(content: Option[String] = None,
                       objectType: Option[String] = None,
                       code: Option[String] = None,
                       attributes: Map[String, QName])

case class Locality(addressLine: Seq[AddressLine] = Nil,
                    localityName: Seq[ContentType] = Nil,
                    localityOption: Option[LocalityType] = None,
                    thoroughfare: Option[Thoroughfare] = None,
                    premise: Option[Premise] = None,
                    dependentLocality: Option[DependentLocality] = None,
                    postalCode: Option[PostalCode] = None,
                    any: Seq[Any] = Nil,
                    objectType: Option[String] = None,
                    usageType: Option[String] = None,
                    indicator: Option[String] = None,
                    attributes: Map[String, QName]) extends CountryType with AddressDetailsType with SubAdministrativeAreaType with AdministrativeAreaType {

  def this() = this(Nil, Nil, None, None, None, None, None, Nil, None, None, None, Map())
}

trait LocalityType

trait DependentThoroughfares

object DependentThoroughfares {
  def fromString(value: String): DependentThoroughfares = value match {
    case "Yes" => Yes
    case "No" => No

  }
}

case object Yes extends DependentThoroughfares {
  override def toString = "Yes"
}

case object No extends DependentThoroughfares {
  override def toString = "No"
}

trait RangeType

object RangeType {
  def fromString(value: String): RangeType = value match {
    case "Odd" => Odd
    case "Even" => Even

  }
}

case object Odd extends RangeType {
  override def toString = "Odd"
}

case object Even extends RangeType {
  override def toString = "Even"
}


case class ThoroughfareNumberRange(addressLine: Seq[AddressLine] = Nil,
                                   thoroughfareNumberFrom: ContentType,
                                   thoroughfareNumberTo: ContentType,
                                   rangeType: Option[RangeType] = None,
                                   indicator: Option[String] = None,
                                   separator: Option[String] = None,
                                   indicatorOccurrence: Option[TypeOccurrence] = None,
                                   numberRangeOccurrence: Option[NumberOccurrence] = None,
                                   objectType: Option[String] = None,
                                   code: Option[String] = None,
                                   attributes: Map[String, QName]) extends ThoroughfareType


case class DependentThoroughfare(addressLine: Seq[AddressLine] = Nil,
                                 thoroughfarePreDirection: Option[ContentType] = None,
                                 thoroughfareLeadingType: Option[ContentType] = None,
                                 thoroughfareName: Seq[ContentType] = Nil,
                                 thoroughfareTrailingType: Option[ContentType] = None,
                                 thoroughfarePostDirection: Option[ContentType] = None,
                                 any: Seq[Any] = Nil,
                                 objectType: Option[String] = None,
                                 attributes: Map[String, QName])


case class Thoroughfare(addressLine: Seq[AddressLine] = Nil,
                        thoroughfareOptionSeq: Seq[ThoroughfareType] = Nil,
                        thoroughfareNumberPrefix: Seq[ThoroughfareNumberPrefix] = Nil,
                        thoroughfareNumberSuffix: Seq[ThoroughfareNumberSuffix] = Nil,
                        thoroughfarePreDirection: Option[ContentType] = None,
                        thoroughfareLeadingType: Option[ContentType] = None,
                        thoroughfareName: Seq[ContentType] = Nil,
                        thoroughfareTrailingType: Option[ContentType] = None,
                        thoroughfarePostDirection: Option[ContentType] = None,
                        dependentThoroughfare: Option[DependentThoroughfare] = None,
                        thoroughfareOption: Option[ThoroughfareType] = None,
                        any: Seq[Any] = Nil,
                        objectType: Option[String] = None,
                        dependentThoroughfares: Option[DependentThoroughfares] = None,
                        dependentThoroughfaresIndicator: Option[String] = None,
                        dependentThoroughfaresConnector: Option[String] = None,
                        dependentThoroughfaresType: Option[String] = None,
                        attributes: Map[String, QName]) extends CountryType with AddressDetailsType {

  def this() = this(Nil, Nil, Nil, Nil, None, None, Nil, None, None, None, None, Nil, None, None, None, None, None, Map())
}

trait ThoroughfareType


case class SubAdministrativeArea(addressLine: Seq[AddressLine] = Nil,
                                 subAdministrativeAreaName: Seq[ContentType] = Nil,
                                 subAdministrativeAreaOption: Option[SubAdministrativeAreaType] = None,
                                 any: Seq[Any] = Nil,
                                 objectType: Option[String] = None,
                                 usageType: Option[String] = None,
                                 indicator: Option[String] = None,
                                 attributes: Map[String, QName])

trait SubAdministrativeAreaType

case class AdministrativeArea(addressLine: Seq[AddressLine] = Nil,
                              administrativeAreaName: Seq[ContentType] = Nil,
                              subAdministrativeArea: Option[SubAdministrativeArea] = None,
                              administrativeAreaOption: Option[AdministrativeAreaType] = None,
                              any: Seq[Any] = Nil,
                              objectType: Option[String] = None,
                              usageType: Option[String] = None,
                              indicator: Option[String] = None,
                              attributes: Map[String, QName]) extends CountryType with AddressDetailsType {

  def this() = this(Nil, Nil, None, None, Nil, None, None, None, Map())
}

trait AdministrativeAreaType

case class PostOfficeNumber(content: Option[String] = None,
                            indicator: Option[String] = None,
                            indicatorOccurrence: Option[TypeOccurrence] = None,
                            code: Option[String] = None,
                            attributes: Map[String, QName]) extends PostOfficeOption


case class PostOffice(addressLine: Seq[AddressLine] = Nil,
                      postOfficeOption: Seq[PostOfficeOption] = Nil,
                      postalRoute: Option[PostalRoute] = None,
                      postBox: Option[PostBox] = None,
                      postalCode: Option[PostalCode] = None,
                      any: Seq[Any] = Nil,
                      objectType: Option[String] = None,
                      indicator: Option[String] = None,
                      attributes: Map[String, QName]) extends DependentLocalityType with LocalityType with SubAdministrativeAreaType with AdministrativeAreaType

trait PostOfficeOption

case class PostalCodeNumberExtension(content: Option[String] = None,
                                     objectType: Option[String] = None,
                                     numberExtensionSeparator: Option[String] = None,
                                     code: Option[String] = None,
                                     attributes: Map[String, QName])

case class PostTownSuffix(content: Option[String] = None,
                          code: Option[String] = None,
                          attributes: Map[String, QName])


case class PostTown(addressLine: Seq[AddressLine] = Nil,
                    postTownName: Seq[ContentType] = Nil,
                    postTownSuffix: Option[PostTownSuffix] = None,
                    objectType: Option[String] = None,
                    attributes: Map[String, QName])


case class PostalCode(addressLine: Seq[AddressLine] = Nil,
                      postalCodeNumber: Seq[ContentType] = Nil,
                      postalCodeNumberExtension: Seq[PostalCodeNumberExtension] = Nil,
                      postTown: Option[PostTown] = None,
                      any: Seq[Any] = Nil,
                      objectType: Option[String] = None,
                      attributes: Map[String, QName]) extends ThoroughfareType with SubAdministrativeAreaType with AdministrativeAreaType


case class PostBoxNumber(content: Option[String] = None,
                         code: Option[String] = None,
                         attributes: Map[String, QName])


case class PostBoxNumberPrefix(content: Option[String] = None,
                               numberPrefixSeparator: Option[String] = None,
                               code: Option[String] = None,
                               attributes: Map[String, QName])


case class PostBoxNumberSuffix(content: Option[String] = None,
                               numberSuffixSeparator: Option[String] = None,
                               code: Option[String] = None,
                               attributes: Map[String, QName])


case class PostBoxNumberExtension(content: Option[String] = None,
                                  numberExtensionSeparator: Option[String] = None,
                                  attributes: Map[String, QName])


case class PostBox(addressLine: Seq[AddressLine] = Nil,
                   postBoxNumber: PostBoxNumber,
                   postBoxNumberPrefix: Option[PostBoxNumberPrefix] = None,
                   postBoxNumberSuffix: Option[PostBoxNumberSuffix] = None,
                   postBoxNumberExtension: Option[PostBoxNumberExtension] = None,
                   firm: Option[Firm] = None,
                   postalCode: Option[PostalCode] = None,
                   any: Seq[Any] = Nil,
                   objectType: Option[String] = None,
                   indicator: Option[String] = None,
                   attributes: Map[String, QName]) extends DependentLocalityType with LocalityType


case class Department(addressLine: Seq[AddressLine] = Nil,
                      departmentName: Seq[ContentType] = Nil,
                      mailStop: Option[MailStop] = None,
                      postalCode: Option[PostalCode] = None,
                      any: Seq[Any] = Nil,
                      objectType: Option[String] = None,
                      attributes: Map[String, QName])

case class PremiseName(content: Option[String] = None,
                       objectType: Option[String] = None,
                       typeOccurrence: Option[TypeOccurrence] = None,
                       code: Option[String] = None,
                       attributes: Map[String, QName])


case class PremiseLocation(content: Option[String] = None,
                           code: Option[String] = None,
                           attributes: Map[String, QName]) extends PremiseType

case class PremiseNumberRangeFrom(addressLine: Seq[AddressLine] = Nil,
                                  premiseNumberPrefix: Seq[PremiseNumberPrefix] = Nil,
                                  premiseNumber: Seq[PremiseNumber] = Nil,
                                  premiseNumberSuffix: Seq[PremiseNumberSuffix] = Nil)


case class PremiseNumberRangeTo(addressLine: Seq[AddressLine] = Nil,
                                premiseNumberPrefix: Seq[PremiseNumberPrefix] = Nil,
                                premiseNumber: Seq[PremiseNumber] = Nil,
                                premiseNumberSuffix: Seq[PremiseNumberSuffix] = Nil)


case class PremiseNumberRange(premiseNumberRangeFrom: PremiseNumberRangeFrom,
                              premiseNumberRangeTo: PremiseNumberRangeTo,
                              rangeType: Option[String] = None,
                              indicator: Option[String] = None,
                              separator: Option[String] = None,
                              objectType: Option[String] = None,
                              indicatorOccurence: Option[TypeOccurrence] = None,
                              numberRangeOccurence: Option[NumberOccurrence] = None) extends PremiseType


case class Premise(addressLine: Seq[AddressLine] = Nil,
                   premiseName: Seq[PremiseName] = Nil,
                   premiseOption: Seq[PremiseType] = Nil,
                   premiseNumberPrefix: Seq[PremiseNumberPrefix] = Nil,
                   premiseNumberSuffix: Seq[PremiseNumberSuffix] = Nil,
                   buildingName: Seq[BuildingName] = Nil,
                   premiseOption3: Seq[PremiseType] = Nil,
                   mailStop: Option[MailStop] = None,
                   postalCode: Option[PostalCode] = None,
                   premise: Option[Premise] = None,
                   any: Seq[Any] = Nil,
                   objectType: Option[String] = None,
                   premiseDependency: Option[String] = None,
                   premiseDependencyType: Option[String] = None,
                   premiseThoroughfareConnector: Option[String] = None,
                   attributes: Map[String, QName]) extends ThoroughfareType

trait PremiseType

/** A-12 where 12 is number and A is prefix and "-" is the separator
  */
case class ThoroughfareNumberPrefix(content: Option[String] = None,
                                    numberPrefixSeparator: Option[String] = None,
                                    objectType: Option[String] = None,
                                    code: Option[String] = None,
                                    attributes: Map[String, QName])


case class ThoroughfareNumberSuffix(content: Option[String] = None,
                                    numberSuffixSeparator: Option[String] = None,
                                    objectType: Option[String] = None,
                                    code: Option[String] = None,
                                    attributes: Map[String, QName])

trait NumberType

object NumberType {
  def fromString(value: String): NumberType = value match {
    case "Single" => Single
    case "Range" => Range

  }
}

case object Single extends NumberType {
  override def toString = "Single"
}

case object Range extends NumberType {
  override def toString = "Range"
}

trait NumberOccurrence

object NumberOccurrence {
  def fromString(value: String): NumberOccurrence = value match {
    case "BeforeName" => BeforeName
    case "AfterName" => AfterName
    case "BeforeType" => BeforeType
    case "AfterType" => AfterType

  }
}

case object BeforeName extends NumberOccurrence {
  override def toString = "BeforeName"
}

case object AfterName extends NumberOccurrence {
  override def toString = "AfterName"
}

case object BeforeType extends NumberOccurrence {
  override def toString = "BeforeType"
}

case object AfterType extends NumberOccurrence {
  override def toString = "AfterType"
}


case class ThoroughfareNumber(content: Option[String] = None,
                              numberType: Option[NumberType] = None,
                              objectType: Option[String] = None,
                              indicator: Option[String] = None,
                              indicatorOccurrence: Option[TypeOccurrence] = None,
                              numberOccurrence: Option[NumberOccurrence] = None,
                              code: Option[String] = None,
                              attributes: Map[String, QName]) extends ThoroughfareType

case class PremiseNumber(content: Option[String] = None,
                         numberType: Option[NumberType] = None,
                         objectType: Option[String] = None,
                         indicator: Option[String] = None,
                         indicatorOccurrence: Option[TypeOccurrence] = None,
                         numberTypeOccurrence: Option[TypeOccurrence] = None,
                         code: Option[String] = None,
                         attributes: Map[String, QName]) extends PremiseType


case class PremiseNumberPrefix(value: String,
                               numberPrefixSeparator: Option[String] = None,
                               objectType: Option[String] = None,
                               code: Option[String] = None,
                               attributes: Map[String, QName])


case class PremiseNumberSuffix(content: Option[String] = None,
                               numberSuffixSeparator: Option[String] = None,
                               objectType: Option[String] = None,
                               code: Option[String] = None,
                               attributes: Map[String, QName])


case class GrPostal(code: Option[String] = None)

