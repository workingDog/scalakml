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


  /**
   * returns a copy of the original object with the designated fieldName changed to the newValue
   *
   * @param fieldName the name of the field to change
   * @param newValue the new value to be in the filedName
   * @return a new object with the designated fieldName changed to the newValue
   */
  def change(fieldName: String, newValue: Any) = {
    val theCopy = this.copy()
    val field = theCopy.getClass.getDeclaredField(fieldName)
    field.setAccessible(true)
    field.set(theCopy, newValue)
    theCopy
  }

  /**
   * returns a new object with the newValue added to the designated Seq of the fieldName field
   * Note: no check is performed on the type compatibility
   *
   * @param fieldName the name of the field to change
   * @param newValue the new value to be in the fieldName Seq
   * @tparam A the type of the Seq element
   * @return a new object with the newValue added to the designated Seq of fieldName
   */
  def addTo[A](fieldName: String, newValue: A) = {
    val theCopy = this.copy()
    val field = theCopy.getClass.getDeclaredField(fieldName)
    field.setAccessible(true)
    val theSeq = field.get(theCopy).asInstanceOf[Seq[A]] // assume ok
    val newSeq = if (theSeq == Nil) (Seq.empty :+ newValue) else (theSeq :+ newValue)
    field.set(theCopy, newSeq)
    theCopy
  }
}


case class FlyTo(duration: Option[Double] = None,
                 flyToMode: Option[FlyToMode] = None,
                 abstractView: Option[AbstractView] = None,
                 id: Option[String] = None,
                 targetId: Option[String] = None,
                 objectSimpleExtensionGroup: Seq[Any] = Nil) extends TourPrimitive {

  def this() = this(None,None,None,None, None, Nil)


  /**
   * returns a copy of the original object with the designated fieldName changed to the newValue
   *
   * @param fieldName the name of the field to change
   * @param newValue the new value to be in the filedName
   * @return a new object with the designated fieldName changed to the newValue
   */
  def change(fieldName: String, newValue: Any) = {
    val theCopy = this.copy()
    val field = theCopy.getClass.getDeclaredField(fieldName)
    field.setAccessible(true)
    field.set(theCopy, newValue)
    theCopy
  }

  /**
   * returns a new object with the newValue added to the designated Seq of the fieldName field
   * Note: no check is performed on the type compatibility
   *
   * @param fieldName the name of the field to change
   * @param newValue the new value to be in the fieldName Seq
   * @tparam A the type of the Seq element
   * @return a new object with the newValue added to the designated Seq of fieldName
   */
  def addTo[A](fieldName: String, newValue: A) = {
    val theCopy = this.copy()
    val field = theCopy.getClass.getDeclaredField(fieldName)
    field.setAccessible(true)
    val theSeq = field.get(theCopy).asInstanceOf[Seq[A]] // assume ok
    val newSeq = if (theSeq == Nil) (Seq.empty :+ newValue) else (theSeq :+ newValue)
    field.set(theCopy, newSeq)
    theCopy
  }

}


case class Playlist(tourPrimitiveGroup: Option[Seq[TourPrimitive]] = None,
                    id: Option[String] = None,
                    targetId: Option[String] = None,
                    objectSimpleExtensionGroup: Seq[Any] = Nil) extends KmlObject {

  def this() = this(None,None,None, Nil)


  /**
   * returns a copy of the original object with the designated fieldName changed to the newValue
   *
   * @param fieldName the name of the field to change
   * @param newValue the new value to be in the filedName
   * @return a new object with the designated fieldName changed to the newValue
   */
  def change(fieldName: String, newValue: Any) = {
    val theCopy = this.copy()
    val field = theCopy.getClass.getDeclaredField(fieldName)
    field.setAccessible(true)
    field.set(theCopy, newValue)
    theCopy
  }

  /**
   * returns a new object with the newValue added to the designated Seq of the fieldName field
   * Note: no check is performed on the type compatibility
   *
   * @param fieldName the name of the field to change
   * @param newValue the new value to be in the fieldName Seq
   * @tparam A the type of the Seq element
   * @return a new object with the newValue added to the designated Seq of fieldName
   */
  def addTo[A](fieldName: String, newValue: A) = {
    val theCopy = this.copy()
    val field = theCopy.getClass.getDeclaredField(fieldName)
    field.setAccessible(true)
    val theSeq = field.get(theCopy).asInstanceOf[Seq[A]] // assume ok
    val newSeq = if (theSeq == Nil) (Seq.empty :+ newValue) else (theSeq :+ newValue)
    field.set(theCopy, newSeq)
    theCopy
  }

  /**
   * returns a new object with the newValue added to the designated Option Seq of the fieldName field
   * Note: no check is performed on the type compatibility
   *
   * @param fieldName the name of the field to change
   * @param newValue the new value to be in the fieldName Seq
   * @tparam A the Seq element type
   * @return a new object with the newValue added to the designated Option Seq of fieldName
   */
  def addToOption[A](fieldName: String, newValue: A) = {
    val theCopy = this.copy()
    val field = theCopy.getClass.getDeclaredField(fieldName)
    field.setAccessible(true)
    val newSeqOption = field.get(theCopy).asInstanceOf[Option[Seq[_]]] match {
      case Some(theSeq) => {
        if (theSeq == Nil) Some(Seq.empty :+ newValue) else Some(theSeq.asInstanceOf[Seq[_]] :+ newValue)
      }
      case None => Some(Seq.empty :+ newValue)
    }
    field.set(theCopy, newSeqOption)
    theCopy
  }

}


case class SoundCue(href: Option[String] = None,
                    id: Option[String] = None,
                    targetId: Option[String] = None,
                    objectSimpleExtensionGroup: Seq[Any] = Nil) extends TourPrimitive {

  def this() = this(None,None,None, Nil)


  /**
   * returns a copy of the original object with the designated fieldName changed to the newValue
   *
   * @param fieldName the name of the field to change
   * @param newValue the new value to be in the filedName
   * @return a new object with the designated fieldName changed to the newValue
   */
  def change(fieldName: String, newValue: Any) = {
    val theCopy = this.copy()
    val field = theCopy.getClass.getDeclaredField(fieldName)
    field.setAccessible(true)
    field.set(theCopy, newValue)
    theCopy
  }

  /**
   * returns a new object with the newValue added to the designated Seq of the fieldName field
   * Note: no check is performed on the type compatibility
   *
   * @param fieldName the name of the field to change
   * @param newValue the new value to be in the fieldName Seq
   * @tparam A the type of the Seq element
   * @return a new object with the newValue added to the designated Seq of fieldName
   */
  def addTo[A](fieldName: String, newValue: A) = {
    val theCopy = this.copy()
    val field = theCopy.getClass.getDeclaredField(fieldName)
    field.setAccessible(true)
    val theSeq = field.get(theCopy).asInstanceOf[Seq[A]] // assume ok
    val newSeq = if (theSeq == Nil) (Seq.empty :+ newValue) else (theSeq :+ newValue)
    field.set(theCopy, newSeq)
    theCopy
  }
}


case class Tour(featurePart: FeaturePart = new FeaturePart(),
                playlist: Option[Playlist] = None,
                id: Option[String] = None,
                targetId: Option[String] = None,
                objectSimpleExtensionGroup: Seq[Any] = Nil) extends Feature {

  def this() = this(new FeaturePart(), None,None,None, Nil)


  /**
   * returns a copy of the original object with the designated fieldName changed to the newValue
   *
   * @param fieldName the name of the field to change
   * @param newValue the new value to be in the filedName
   * @return a new object with the designated fieldName changed to the newValue
   */
  def change(fieldName: String, newValue: Any) = {
    val theCopy = this.copy()
    val field = theCopy.getClass.getDeclaredField(fieldName)
    field.setAccessible(true)
    field.set(theCopy, newValue)
    theCopy
  }

  /**
   * returns a new object with the newValue added to the designated Seq of the fieldName field
   * Note: no check is performed on the type compatibility
   *
   * @param fieldName the name of the field to change
   * @param newValue the new value to be in the fieldName Seq
   * @tparam A the type of the Seq element
   * @return a new object with the newValue added to the designated Seq of fieldName
   */
  def addTo[A](fieldName: String, newValue: A) = {
    val theCopy = this.copy()
    val field = theCopy.getClass.getDeclaredField(fieldName)
    field.setAccessible(true)
    val theSeq = field.get(theCopy).asInstanceOf[Seq[A]] // assume ok
    val newSeq = if (theSeq == Nil) (Seq.empty :+ newValue) else (theSeq :+ newValue)
    field.set(theCopy, newSeq)
    theCopy
  }
}

case class TourControl(playMode: Option[PlayMode] = None,
                    id: Option[String] = None,
                    targetId: Option[String] = None,
                    objectSimpleExtensionGroup: Seq[Any] = Nil) extends TourPrimitive {

  def this() = this(None,None,None, Nil)


  /**
   * returns a copy of the original object with the designated fieldName changed to the newValue
   *
   * @param fieldName the name of the field to change
   * @param newValue the new value to be in the filedName
   * @return a new object with the designated fieldName changed to the newValue
   */
  def change(fieldName: String, newValue: Any) = {
    val theCopy = this.copy()
    val field = theCopy.getClass.getDeclaredField(fieldName)
    field.setAccessible(true)
    field.set(theCopy, newValue)
    theCopy
  }

  /**
   * returns a new object with the newValue added to the designated Seq of the fieldName field
   * Note: no check is performed on the type compatibility
   *
   * @param fieldName the name of the field to change
   * @param newValue the new value to be in the fieldName Seq
   * @tparam A the type of the Seq element
   * @return a new object with the newValue added to the designated Seq of fieldName
   */
  def addTo[A](fieldName: String, newValue: A) = {
    val theCopy = this.copy()
    val field = theCopy.getClass.getDeclaredField(fieldName)
    field.setAccessible(true)
    val theSeq = field.get(theCopy).asInstanceOf[Seq[A]] // assume ok
    val newSeq = if (theSeq == Nil) (Seq.empty :+ newValue) else (theSeq :+ newValue)
    field.set(theCopy, newSeq)
    theCopy
  }
}


case class Wait(duration: Option[Double] = None,
                id: Option[String] = None,
                targetId: Option[String] = None,
                objectSimpleExtensionGroup: Seq[Any] = Nil) extends TourPrimitive {

  def this() = this(None,None,None, Nil)


  /**
   * returns a copy of the original object with the designated fieldName changed to the newValue
   *
   * @param fieldName the name of the field to change
   * @param newValue the new value to be in the filedName
   * @return a new object with the designated fieldName changed to the newValue
   */
  def change(fieldName: String, newValue: Any) = {
    val theCopy = this.copy()
    val field = theCopy.getClass.getDeclaredField(fieldName)
    field.setAccessible(true)
    field.set(theCopy, newValue)
    theCopy
  }

  /**
   * returns a new object with the newValue added to the designated Seq of the fieldName field
   * Note: no check is performed on the type compatibility
   *
   * @param fieldName the name of the field to change
   * @param newValue the new value to be in the fieldName Seq
   * @tparam A the type of the Seq element
   * @return a new object with the newValue added to the designated Seq of fieldName
   */
  def addTo[A](fieldName: String, newValue: A) = {
    val theCopy = this.copy()
    val field = theCopy.getClass.getDeclaredField(fieldName)
    field.setAccessible(true)
    val theSeq = field.get(theCopy).asInstanceOf[Seq[A]] // assume ok
    val newSeq = if (theSeq == Nil) (Seq.empty :+ newValue) else (theSeq :+ newValue)
    field.set(theCopy, newSeq)
    theCopy
  }
}


case class LatLonQuad(coordinates: Option[Seq[Location]] = None,
                      id: Option[String] = None,
                      targetId: Option[String] = None,
                      objectSimpleExtensionGroup: Seq[Any] = Nil) extends KmlObject {

  def this() = this(None,None,None, Nil)


  /**
   * returns a copy of the original object with the designated fieldName changed to the newValue
   *
   * @param fieldName the name of the field to change
   * @param newValue the new value to be in the filedName
   * @return a new object with the designated fieldName changed to the newValue
   */
  def change(fieldName: String, newValue: Any) = {
    val theCopy = this.copy()
    val field = theCopy.getClass.getDeclaredField(fieldName)
    field.setAccessible(true)
    field.set(theCopy, newValue)
    theCopy
  }

  /**
   * returns a new object with the newValue added to the designated Seq of the fieldName field
   * Note: no check is performed on the type compatibility
   *
   * @param fieldName the name of the field to change
   * @param newValue the new value to be in the fieldName Seq
   * @tparam A the type of the Seq element
   * @return a new object with the newValue added to the designated Seq of fieldName
   */
  def addTo[A](fieldName: String, newValue: A) = {
    val theCopy = this.copy()
    val field = theCopy.getClass.getDeclaredField(fieldName)
    field.setAccessible(true)
    val theSeq = field.get(theCopy).asInstanceOf[Seq[A]] // assume ok
    val newSeq = if (theSeq == Nil) (Seq.empty :+ newValue) else (theSeq :+ newValue)
    field.set(theCopy, newSeq)
    theCopy
  }

  /**
   * returns a new object with the newValue added to the designated Option Seq of the fieldName field
   * Note: no check is performed on the type compatibility
   *
   * @param fieldName the name of the field to change
   * @param newValue the new value to be in the fieldName Seq
   * @tparam A the Seq element type
   * @return a new object with the newValue added to the designated Option Seq of fieldName
   */
  def addToOption[A](fieldName: String, newValue: A) = {
    val theCopy = this.copy()
    val field = theCopy.getClass.getDeclaredField(fieldName)
    field.setAccessible(true)
    val newSeqOption = field.get(theCopy).asInstanceOf[Option[Seq[_]]] match {
      case Some(theSeq) => {
        if (theSeq == Nil) Some(Seq.empty :+ newValue) else Some(theSeq.asInstanceOf[Seq[_]] :+ newValue)
      }
      case None => Some(Seq.empty :+ newValue)
    }
    field.set(theCopy, newSeqOption)
    theCopy
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

