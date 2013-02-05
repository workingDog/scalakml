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
package com.scalakml.gx

/**
 * package of classes and constructs for the Google KML Version 2.2 extension model
 *
 * @author Ringo Wathelet
 * Date: 12/12/12
 * Version: 1
 *
 * Reference: OGC 07-147r2 Version: 2.2.0, Category: OGC� Standard, Editor: Tim Wilson, at
 * http://www.opengeospatial.org/standards/kml
 * also
 * Google developers KML Reference, at
 * https://developers.google.com/kml/documentation/kmlreference
 * also
 * http://developers.google.com/kml/schema/kml22gx.xsd.
 *
 * The documentations are taken from these 3 references.
 *
 */


import com.scalakml.kml._
import com.scalaxal.xAL.AddressDetails

/**
 * Enumeration of all TourPrimitive types.
 */
object TourPrimitiveTypes extends Enumeration {
  type TourPrimitiveTypes = Value
  val AnimatedUpdate, FlyTo, SoundCue, Wait, TourControl= Value
}

trait FlyToMode

object FlyToMode {
  def fromString(value: String): FlyToMode =
    if (value == null) null
    else
      value.trim match {
        case "bounce" => Bounce
        case "smooth" => Smooth
        case _ => null
      }
}

case object Bounce extends FlyToMode {
  override def toString = "bounce"
}

case object Smooth extends FlyToMode {
  override def toString = "smooth"
}

trait PlayMode

object PlayMode {
  def fromString(value: String): PlayMode =
    if (value == null) null
    else
      value.trim match {
        case "pause" => Pause
        case _ => null
      }
}

case object Pause extends PlayMode {
  override def toString = "pause"
}

trait TourPrimitive extends KmlObject

case class AnimatedUpdate(duration: Option[Double] = None,
                          update: Option[Update] = None,
                          id: Option[String] = None,
                          targetId: Option[String] = None,
                          objectSimpleExtensionGroup: Seq[Any] = Nil) extends TourPrimitive {

  def this() = this(None,None,None,None, Nil)

  def withId(newValue: String) = { this.copy(id = Some(newValue)) }
  def withTargetId(newValue: String) = { this.copy(targetId = Some(newValue)) }
  def withDuration(newValue: Double) = { this.copy(duration = Some(newValue)) }
  def withUpdate(newValue: Update) = { this.copy(update = Some(newValue)) }
}


case class FlyTo(duration: Option[Double] = None,
                 flyToMode: Option[FlyToMode] = None,
                 abstractView: Option[AbstractView] = None,
                 id: Option[String] = None,
                 targetId: Option[String] = None,
                 objectSimpleExtensionGroup: Seq[Any] = Nil) extends TourPrimitive {

  def this() = this(None,None,None,None, None, Nil)

  def withId(newValue: String) = { this.copy(id = Some(newValue)) }
  def withTargetId(newValue: String) = { this.copy(targetId = Some(newValue)) }
  def withDuration(newValue: Double) = { this.copy(duration = Some(newValue)) }
  def withFlyToMode(newValue: FlyToMode) = { this.copy(flyToMode = Some(newValue)) }
  def withAbstractView(newValue: AbstractView) = { this.copy(abstractView = Some(newValue)) }

}


case class Playlist(tourPrimitiveGroup: Option[Seq[TourPrimitive]] = None,
                    id: Option[String] = None,
                    targetId: Option[String] = None,
                    objectSimpleExtensionGroup: Seq[Any] = Nil) extends KmlObject {

  def this() = this(None,None,None, Nil)
  def withId(newValue: String) = { this.copy(id = Some(newValue)) }
  def withTargetId(newValue: String) = { this.copy(targetId = Some(newValue)) }
  def withTourPrimitiveGroup(newValue: Seq[TourPrimitive]) = { this.copy(tourPrimitiveGroup = Some(newValue)) }
  def withAddedTourPrimitive(newTourPrimitive: TourPrimitive) = {
    val newSet = if (this.tourPrimitiveGroup == None) (Seq.empty :+ newTourPrimitive) else (this.tourPrimitiveGroup.get :+ newTourPrimitive)
    this.copy(tourPrimitiveGroup = Some(newSet))
  }
}


case class SoundCue(href: Option[String] = None,
                    id: Option[String] = None,
                    targetId: Option[String] = None,
                    objectSimpleExtensionGroup: Seq[Any] = Nil) extends TourPrimitive {

  def this() = this(None,None,None, Nil)
  def withId(newValue: String) = { this.copy(id = Some(newValue)) }
  def withTargetId(newValue: String) = { this.copy(targetId = Some(newValue)) }
  def withHref(newValue: String) = { this.copy(href = Some(newValue)) }
}


case class Tour(featurePart: FeaturePart = new FeaturePart(),
                playlist: Option[Playlist] = None,
                id: Option[String] = None,
                targetId: Option[String] = None,
                objectSimpleExtensionGroup: Seq[Any] = Nil) extends Feature {

  def this() = this(new FeaturePart(), None,None,None, Nil)
  def withId(newValue: String) = { this.copy(id = Some(newValue)) }
  def withTargetId(newValue: String) = { this.copy(targetId = Some(newValue)) }

  def withPlaylist(newValue: Playlist) = { this.copy(playlist = Some(newValue)) }

  def withFeaturePart(newValue: FeaturePart) = { this.copy(featurePart = newValue) }
  def withName(newValue: String) = { this.copy(featurePart = featurePart.withName(newValue)) }
  def withVisibility(newValue: Boolean) = { this.copy(featurePart = featurePart.withVisibility(newValue)) }
  def withOpen(newValue: Boolean) = { this.copy(featurePart = featurePart.withOpen(newValue)) }
  def withAtomAuthor(newValue: com.scalakml.atom.Author) = { this.copy(featurePart = featurePart.withAtomAuthor(newValue)) }
  def withAtomLink(newValue: com.scalakml.atom.Link) = { this.copy(featurePart = featurePart.withAtomLink(newValue)) }
  def withAddress(newValue: String) = { this.copy(featurePart = featurePart.withAddress(newValue)) }
  def withAddressDetails(newValue: AddressDetails) = { this.copy(featurePart = featurePart.withAddressDetails(newValue)) }
  def withPhoneNumber(newValue: String) = { this.copy(featurePart = featurePart.withPhoneNumber(newValue)) }
  def withExtendedData(newValue: ExtendedData) = { this.copy(featurePart = featurePart.withExtendedData(newValue)) }
  def withDescription(newValue: String) = { this.copy(featurePart = featurePart.withDescription(newValue)) }
  def withSnippet(newValue: Snippet) = { this.copy(featurePart = featurePart.withSnippet(newValue)) }
  def withAbstractView(newValue: AbstractView) = { this.copy(featurePart = featurePart.withAbstractView(newValue)) }
  def withTimePrimitive(newValue: TimePrimitive) = { this.copy(featurePart = featurePart.withTimePrimitive(newValue)) }
  def withStyleUrl(newValue: String) = { this.copy(featurePart = featurePart.withStyleUrl(newValue)) }
  def withStyleSelector(newValue: Seq[StyleSelector]) = { this.copy(featurePart = featurePart.withStyleSelector(newValue)) }
  def withAddedStyleSelector(newValue: StyleSelector) = { this.copy(featurePart = featurePart.withAddedStyleSelector(newValue)) }
  def withRegion(newValue: Region) = { this.copy(featurePart = featurePart.withRegion(newValue)) }

}

case class TourControl(playMode: Option[PlayMode] = None,
                    id: Option[String] = None,
                    targetId: Option[String] = None,
                    objectSimpleExtensionGroup: Seq[Any] = Nil) extends TourPrimitive {

  def this() = this(None,None,None, Nil)
  def withId(newValue: String) = { this.copy(id = Some(newValue)) }
  def withTargetId(newValue: String) = { this.copy(targetId = Some(newValue)) }
  def withPlayMode(newValue: PlayMode) = { this.copy(playMode = Some(newValue)) }
}


case class Wait(duration: Option[Double] = None,
                id: Option[String] = None,
                targetId: Option[String] = None,
                objectSimpleExtensionGroup: Seq[Any] = Nil) extends TourPrimitive {

  def this() = this(None,None,None, Nil)
  def withId(newValue: String) = { this.copy(id = Some(newValue)) }
  def withTargetId(newValue: String) = { this.copy(targetId = Some(newValue)) }
  def withDuration(newValue: Double) = { this.copy(duration = Some(newValue)) }
}


case class LatLonQuad(coordinates: Option[Seq[Location]] = None,
                      id: Option[String] = None,
                      targetId: Option[String] = None,
                      objectSimpleExtensionGroup: Seq[Any] = Nil) extends KmlObject {

  def this() = this(None,None,None, Nil)
  def withId(newValue: String) = { this.copy(id = Some(newValue)) }
  def withTargetId(newValue: String) = { this.copy(targetId = Some(newValue)) }
  def withCoordinates(newValue: Seq[Location]) = { this.copy(coordinates = Some(newValue)) }
  def withAddedCoordinate(newLocation: Location) = {
    val newCoordinateSet = if (this.coordinates == None) (Seq.empty :+ newLocation) else (this.coordinates.get :+ newLocation)
    this.copy(coordinates = Some(newCoordinateSet))
  }
}

/**
 * A track describes how an object moves through the world over a given time period.
 * This feature allows you to create one visible object in Google Earth
 * (either a Point icon or a Model) that encodes multiple positions for the same object for multiple times.
 * In Google Earth, the time slider allows the user to move the view through time,
 * which animates the position of the object.
 */
//case class Track(id: Option[String] = None,
//                  targetId: Option[String] = None,
//                  geometrySimpleExtensionGroup: Seq[Any] = Nil,
//                  geometryObjectExtensionGroup: Seq[Any] = Nil,
//                  objectSimpleExtensionGroup: Seq[Any] = Nil) extends Geometry {
//
//  def this() = this(None,None,Nil, Nil, Nil)
//  def withId(newValue: String) = { this.copy(id = Some(newValue)) }
//  def withTargetId(newValue: String) = { this.copy(targetId = Some(newValue)) }
//}
//
//case class MultiTrack(id: Option[String] = None,
//                      targetId: Option[String] = None,
//                      geometrySimpleExtensionGroup: Seq[Any] = Nil,
//                      geometryObjectExtensionGroup: Seq[Any] = Nil,
//                      objectSimpleExtensionGroup: Seq[Any] = Nil) extends Geometry {
//
//  def this() = this(None,None,Nil, Nil, Nil)
//  def withId(newValue: String) = { this.copy(id = Some(newValue)) }
//  def withTargetId(newValue: String) = { this.copy(targetId = Some(newValue)) }
//}

