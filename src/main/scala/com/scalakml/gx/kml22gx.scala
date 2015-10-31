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

  def this(duration: Double) = this(Option(duration))
  def this(duration: Double, update: Update) = this(Option(duration), Option(update))
}


case class FlyTo(duration: Option[Double] = None,
                 flyToMode: Option[FlyToMode] = None,
                 abstractView: Option[AbstractView] = None,
                 id: Option[String] = None,
                 targetId: Option[String] = None,
                 objectSimpleExtensionGroup: Seq[Any] = Nil) extends TourPrimitive {

  def this(duration: Double) = this(Option(duration))
  def this(duration: Double, flyToMode: FlyToMode) = this(Option(duration), Option(flyToMode))
  def this(duration: Double, flyToMode: FlyToMode, abstractView: AbstractView) =
    this(Option(duration), Option(flyToMode), Option(abstractView))
}


case class Playlist(tourPrimitiveGroup: Option[Seq[TourPrimitive]] = None,
                    id: Option[String] = None,
                    targetId: Option[String] = None,
                    objectSimpleExtensionGroup: Seq[Any] = Nil) extends KmlObject {

  def this(tourPrimitive: TourPrimitive) = this(Option(Seq.empty :+ tourPrimitive))
  def this(tourPrimitiveGroup: Seq[TourPrimitive]) = this(Option(tourPrimitiveGroup))
  /**
   * returns a new object with value added to the sequence
   * @param value to add
   * @return a new object with value added to the sequence
   */
  def addToTourPrimitiveGroup(value: TourPrimitive) = {
    this.copy(tourPrimitiveGroup =
      tourPrimitiveGroup match {
        case Some(x) => if (x == Nil) Option(Seq.empty :+ value) else Option(x :+ value)
        case None => Option(Seq.empty :+ value)
      })
  }
}


case class SoundCue(href: Option[String] = None,
                    id: Option[String] = None,
                    targetId: Option[String] = None,
                    objectSimpleExtensionGroup: Seq[Any] = Nil) extends TourPrimitive {

  def this(href: String) = this(Option(href))

}


case class Tour(featurePart: FeaturePart = new FeaturePart(),
                playlist: Option[Playlist] = None,
                id: Option[String] = None,
                targetId: Option[String] = None,
                objectSimpleExtensionGroup: Seq[Any] = Nil) extends Feature {

  def this(playlist: Playlist) = this(new FeaturePart(), Option(playlist))
  def this(name: String, playlist: Playlist) = this(new FeaturePart(name = Option(name)), Option(playlist))
}

case class TourControl(playMode: Option[PlayMode] = None,
                    id: Option[String] = None,
                    targetId: Option[String] = None,
                    objectSimpleExtensionGroup: Seq[Any] = Nil) extends TourPrimitive {

  def this(playMode: PlayMode) = this(Option(playMode))

}


case class Wait(duration: Option[Double] = None,
                id: Option[String] = None,
                targetId: Option[String] = None,
                objectSimpleExtensionGroup: Seq[Any] = Nil) extends TourPrimitive {

  def this(duration: Double) = this(Option(duration))

}

case class LatLonQuad(coordinates: Option[Seq[Coordinate]] = None,
                      id: Option[String] = None,
                      targetId: Option[String] = None,
                      objectSimpleExtensionGroup: Seq[Any] = Nil) extends KmlObject {

  def this(coordinates: Seq[Coordinate]) = this(Option(coordinates))

  /**
   * returns a new object with a new Location added to the sequence of coordinates
   * @param value the new Location to add
   * @return a new object with a new Location added to the sequence of coordinates
   */
  def addToCoordinates(value: Coordinate) = {
    this.copy(coordinates =
      coordinates match {
        case Some(x) => if (x == Nil) Option(Seq.empty :+ value) else Option(x :+ value)
        case None => Option(Seq.empty :+ value)
      })
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
//}
//
//case class MultiTrack(id: Option[String] = None,
//                      targetId: Option[String] = None,
//                      geometrySimpleExtensionGroup: Seq[Any] = Nil,
//                      geometryObjectExtensionGroup: Seq[Any] = Nil,
//                      objectSimpleExtensionGroup: Seq[Any] = Nil) extends Geometry {
//}

