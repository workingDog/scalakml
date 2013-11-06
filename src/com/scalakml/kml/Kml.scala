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

package com.scalakml.kml

import com.scalakml.atom._
import com.scalaxal.xAL.AddressDetails

/**
 * package of classes and constructs for the Google KML Version 2.2 model
 *
 * @author Ringo Wathelet
 * Date: 12/12/12
 * Version: 1
 *
 * References: OGC 07-147r2 Version: 2.2.0, Category: OGC Standard, Editor: Tim Wilson, at
 * http://www.opengeospatial.org/standards/kml
 * also
 * Google developers KML Reference, at
 * https://developers.google.com/kml/documentation/kmlreference
 *
 * The documentations are taken from these two references.
 */


/**
  * Specifies how the <altitude> is interpreted.
  */
trait AltitudeMode

/**
 * Specifies how the <altitude> is interpreted. Also covers the the Google extension gx
 * Possible values are as follows:
 * <ul>
 * <li>relativeToGround - (default) Interprets the <altitude> as a value in meters above the ground.
 * If the point is over water, the <altitude> will be interpreted as a value in meters above sea level.
 * See <gx:altitudeMode> to specify points relative to the sea floor.
 * <li>clampToGround - For a camera, this setting also places the camera relativeToGround,
 * since putting the camera exactly at terrain height would mean that the eye would intersect
 * the terrain (and the view would be blocked).
 * <li>absolute - Interprets the <altitude> as a value in meters above sea level.
 * <li>from the gx package, relativeToSeaFloor - Interprets the altitude as a value in meters above the sea floor. If the KML feature is above land rather than sea, the altitude will be interpreted as being above the ground.
 * <li>from the gx package, clampToSeaFloor - The altitude specification is ignored, and the KML feature will be positioned on the sea floor. If the KML feature is on land rather than at sea, clampToSeaFloor will instead clamp to ground.
 * </ul>
 */
object AltitudeMode {
  def fromString(value: String): AltitudeMode =
    if (value == null) null
    else
      value.trim match {
        case "clampToSeaFloor" => ClampToSeaFloor
        case "relativeToSeaFloor" => RelativeToSeaFloor
        case "clampToGround" => ClampToGround
        case "relativeToGround" => RelativeToGround
        case "absolute" => Absolute
        case _ => null
      }
}

/**
 * This mode ignores any altitude value, and places the KML feature on the surface of the ground,
 * following the terrain. In this way, GroundOverlays can, for example, be 'draped' over the
 * surface of the Earth. If the feature is positioned over a major body of water, clampToGround
 * will place the feature at sea level.
 * @see  AltitudeMode
 */
case object ClampToGround extends AltitudeMode {
  /**
   *
   * @return "clampToGround"
   */
  override def toString = "clampToGround"
}

/**
 * Measures the altitude from the ground level directly below the coordinates.
 * @see  AltitudeMode
 */
case object RelativeToGround extends AltitudeMode {
  /**
   *
   * @return "relativeToGround"
   */
  override def toString = "relativeToGround"
}

/**
 * The absolute altitude mode measures altitude relative to sea level,
 * regardless of the actual elevation of the terrain beneath the feature.
 * In this way, features can be placed underground, and will not be visible.
 * Portions of a feature can extend underground.
 * Negative values are accepted, to place features below sea level.
 * This altitude mode is useful in situations where the altitude value is known precisely.
 * GPS tracks, for example, can use the absolute altitude mode to display paths
 * created while flying or diving.
 * @see  AltitudeMode
 */
case object Absolute extends AltitudeMode {
  /**
   *
   * @return "absolute"
   */
  override def toString = "absolute"
}

/**
 * The altitude specification is ignored, and the KML feature will be positioned on the sea floor.
 * If the KML feature is on land rather than at sea, clampToSeaFloor will instead clamp to ground.
 */
case object ClampToSeaFloor extends AltitudeMode {
  /**
   *
   * @return "clampToSeaFloor"
   */
  override def toString = "clampToSeaFloor"
}

/**
 * Interprets the altitude as a value in meters above the sea floor.
 * If the KML feature is above land rather than sea, the altitude will be interpreted as
 * being above the ground.
 */
case object RelativeToSeaFloor extends AltitudeMode {
  /**
   *
   * @return "relativeToSeaFloor"
   */
  override def toString = "relativeToSeaFloor"
}

trait ColorMode

/**
 * Values for <colorMode> are normal (no effect) and random.
 * <ul>
 * <li>A value of random applies a random linear scale to the base <color> as follows.
 * To achieve a truly random selection of colors, specify a base <color> of white (ffffffff).
 * <li>If you specify a single color component (for example, a value of ff0000ff for red),
 * random color values for that one component (red) will be selected. In this case,
 * the values would range from 00 (black) to ff (full red).
 * <li>If you specify values for two or for all three color components, a random linear scale is applied to each
 * color component, with results ranging from black to the maximum values specified for each component.
 * </ul>
 * The opacity of a color comes from the alpha component of <color> and is never randomized.
 *
 */
object ColorMode {
  /**
   * Creates a specific ColorMode object from the String value, either "normal" or "random"
   * @param value the name of the ColorMode to construct
   * @return a ColorMode object or null if the value parameter is not a correct ColorMode name or is null
   */
  def fromString(value: String): ColorMode = 
    if (value == null) null else 
    value.trim match {
    case "normal" => NormalValue
    case "random" => Random
    case _ => null
  }
}

/**
 * Specifies a single colour value.
 * @see  ColorMode
 */
case object NormalValue extends ColorMode {
  override def toString = "normal"
}

/**
 * Specifies to use a random colour value.
 * @see  ColorMode
 */
case object Random extends ColorMode {
  override def toString = "random"
}

/**
 * Specifies how the <displayMode> is interpreted.
 */
trait DisplayMode

/**
 * <ul>
 * <li>If <displayMode> is default, Google Earth uses the information supplied in <text> to create a balloon .
 * <li>If <displayMode> is hide, Google Earth does not display the balloon.
 * </ul>
 * In Google Earth, clicking the List View icon for a Placemark whose balloon's <displayMode> is hide
 * causes Google Earth to fly to the Placemark.
 */
object DisplayMode {
  /**
   * Creates a specific DisplayMode object from the String value, either "default" or "hide"
   * @param value the name of the DisplayMode to construct
   * @return a DisplayMode object or null if the value parameter is not a correct DisplayMode name or is null
   */
  def fromString(value: String): DisplayMode = 
    if (value == null) null else 
    value.trim match {
    case "default" => Default
    case "hide" => Hide
    case _ => null
  }
}

/**
 * Specifies to display the balloon.
 * @see  DisplayMode
 */
case object Default extends DisplayMode {
  /**
   *
   * @return "default"
   */
  override def toString = "default"
}

/**
 * Specifies to hide the balloon.
 * @see  DisplayMode
 */
case object Hide extends DisplayMode {
  /**
   *
   * @return "hide"
   */
  override def toString = "hide"
}

/**
 * Specifies where to begin numbering the tiles in each layer of the pyramid.
 */
trait GridOrigin

/**
 * Specifies where to begin numbering the tiles in each layer of the pyramid.
 * A value of lowerLeft specifies that row 1, column 1 of each layer is in the bottom left corner of the grid.
 */
object GridOrigin {
  /**
   * Creates a specific GridOrigin object from the String value, either "lowerLeft" or "upperLeft"
   * @param value the name of the GridOrigin to construct
   * @return a GridOrigin object or null if the value parameter is not a correct GridOrigin name or is null
   */
  def fromString(value: String): GridOrigin = 
    if (value == null) null else 
    value.trim match {
    case "lowerLeft" => LowerLeft
    case "upperLeft" => UpperLeft
    case _ => null
  }
}

/**
 * Specifies to begin numbering the tiles in a layer of a ImagePyramid from the lower left corner.
 */
case object LowerLeft extends GridOrigin {
  /**
   *
   * @return "lowerLeft"
   */
  override def toString = "lowerLeft"
}

/**
 * Specifies to begin numbering the tiles in a layer of ImagePyramid from the upper left corner.
 */
case object UpperLeft extends GridOrigin {
  /**
   *
   * @return "upperLeft"
   */
  override def toString = "upperLeft"
}

/**
 * Specifies the current state of a NetworkLink or Folder.
 */
trait ItemIconState

/**
 * Specifies the current state of the NetworkLink or Folder.
 * Possible values are open, closed, error, fetching0, fetching1, and fetching2.
 * These values can be combined by inserting a space between two values (no comma).
 */
object ItemIconState {
  /**
   * Creates a specific ItemIconState object from the String value, either
   * "open" "closed" "error" "fetching0" "fetching1" "fetching2"
   * @param value the name of the ItemIconState to construct
   * @return a ItemIconState object or null if the value parameter is not a correct ItemIconState name or is null
   */
  def fromString(value: String): ItemIconState =
    if (value == null) null else 
    value.trim match {
    case "open" => Open
    case "closed" => Closed
    case "error" => Error
    case "fetching0" => Fetching0
    case "fetching1" => Fetching1
    case "fetching2" => Fetching2
    case _ => null
  }
}

/**
 * Specifies the open state of the NetworkLink or Folder.
 */
case object Open extends ItemIconState {
  /**
   *
   * @return "open"
   */
  override def toString = "open"
}
/**
 * Specifies the closed state of the NetworkLink or Folder.
 */
case object Closed extends ItemIconState {
  /**
   *
   * @return "closed"
   */
  override def toString = "closed"
}
/**
 * Specifies the error state of the NetworkLink or Folder.
 */
case object Error extends ItemIconState {
  /**
   *
   * @return "error"
   */
  override def toString = "error"
}
/**
 * Specifies the Fetching0 state of the NetworkLink or Folder.
 */
case object Fetching0 extends ItemIconState {
  /**
   *
   * @return "fetching0"
   */
  override def toString = "fetching0"
}
/**
 * Specifies the fetching1 state of the NetworkLink or Folder.
 */
case object Fetching1 extends ItemIconState {
  /**
   *
   * @return "fetching1"
   */
  override def toString = "fetching1"
}
/**
 * Specifies the fetching2 state of the NetworkLink or Folder.
 */
case object Fetching2 extends ItemIconState {
  /**
   *
   * @return "fetching2"
   */
  override def toString = "fetching2"
}

/**
 * Specifies how a Feature is displayed in the list view.
 */
trait ListItemType

/**
 * Specifies how a Feature is displayed in the list view. Possible values are:
 * <ul>
 * <li>check (default) - The Feature's visibility is tied to its item's checkbox.
 * <li>radioFolder - When specified for a Container, only one of the Container's items is visible at a time
 * <li>checkOffOnly - When specified for a Container or Network Link, prevents all items from being made visible at once—that is, the user can turn everything in the Container or Network Link off but cannot turn everything on at the same time. This setting is useful for Containers or Network Links containing large amounts of data.
 * <li>checkHideChildren - Use a normal checkbox for visibility but do not display the Container or Network Link's children in the list view.
 * </ul>
 * A checkbox allows the user to toggle visibility of the child objects in the viewer.
 */
object ListItemType {
  /**
   * Creates a specific ListItemType object from the String value, either
   * "radioFolder" "check" "checkHideChildren" "checkOffOnly"
   * @param value the name of the ListItemType to construct
   * @return a ListItemType object or null if the value parameter is not a correct ListItemType name or is null
   */
  def fromString(value: String): ListItemType = 
    if (value == null) null else 
    value.trim match {
    case "radioFolder" => RadioFolder
    case "check" => Check
    case "checkHideChildren" => CheckHideChildren
    case "checkOffOnly" => CheckOffOnly
    case _ => null
  }
}
/**
 * Specifies the RadioFolder displayed in the list view.
 * Only one of the Container's items shall be visible at a time.
 */
case object RadioFolder extends ListItemType {
  /**
   *
   * @return "radioFolder"
   */
  override def toString = "radioFolder"
}
/**
 * Specifies the Check displayed in the list view.
 * The Feature's visibility is tied to its item's checkbox.
 */
case object Check extends ListItemType {
  /**
   *
   * @return "check"
   */
  override def toString = "check"
}
/**
 * Specifies the CheckHideChildren displayed in the list view.
 * Use a normal checkbox for visibility but do not display the
 * Container's children in the list view. A checkbox allows the user to toggle visibility
 * of the child objects in the viewer.
 */
case object CheckHideChildren extends ListItemType {
  /**
   *
   * @return "checkHideChildren"
   */
  override def toString = "checkHideChildren"
}
/**
 * Specifies the CheckOffOnly displayed in the list view.
 * Prevents all items from being made visible at once—that is,
 * the user can turn everything in the kml:AbstractContainerGroup off but
 * cannot turn everything on at the same time. This setting is useful
 * for Container's containing large amounts of data.
 */
case object CheckOffOnly extends ListItemType {
  /**
   *
   * @return "checkOffOnly"
   */
  override def toString = "checkOffOnly"
}

trait RefreshMode

/**
 * Specifies a time-based refresh mode, which can be one of the following:
 * <ul>
 * <li>onChange - refresh when the file is loaded and whenever the Link parameters change (the default).
 * <li>onInterval - refresh every n seconds (specified in <refreshInterval>).
 * <li>onExpire - refresh the file when the expiration time is reached.
 * If a fetched file has a NetworkLinkControl, the <expires> time takes precedence over expiration times
 * specified in HTTP headers. If no <expires> time is specified, the HTTP max-age header is used
 * (if present). If max-age is not present, the Expires HTTP header is used (if present).
 * (See Section RFC261b of the Hypertext Transfer Protocol - HTTP 1.1 for details on HTTP header fields.)
 * </ul>
 */
object RefreshMode {
  /**
   * Creates a specific RefreshMode object from the String value, either
   * "onChange" "onInterval" "onExpire"
   * @param value the name of the RefreshMode to construct
   * @return a RefreshMode object or null if the value parameter is not a correct RefreshMode name or is null
   */
  def fromString(value: String): RefreshMode = 
    if (value == null) null else 
    value.trim match {
    case "onChange" => OnChange
    case "onInterval" => OnInterval
    case "onExpire" => OnExpire
    case _ => null
  }
}
/**
 * Specifies the OnChange time-based refresh mode
 * Refresh when the resource is first loaded and whenever the Link parameters change.
 */
case object OnChange extends RefreshMode {
  /**
   *
   * @return "onChange"
   */
  override def toString = "onChange"
}
/**
 * Specifies the OnInterval time-based refresh mode
 * Refresh the resource every n seconds as specified in kml:refreshInterval.
 */
case object OnInterval extends RefreshMode {
  /**
   *
   * @return "onInterval"
   */
  override def toString = "onInterval"
}
/**
 * Specifies the OnExpire time-based refresh mode
 * Refresh the resource when the expiration time is reached.
 */
case object OnExpire extends RefreshMode {
  /**
   *
   * @return "onExpire"
   */
  override def toString = "onExpire"
}
/**
 * Specifies how the link is refreshed when the "camera" changes.
 */
trait ViewRefreshMode

/**
 * Specifies how the link is refreshed when the "camera" changes. Can be one of the following:
 * <ul>
 * <li>never (default) - Ignore changes in the view. Also ignore <viewFormat> parameters, if any.
 * <li>onStop - Refresh the file n seconds after movement stops, where n is specified in <viewRefreshTime>.
 * <li>onRequest - Refresh the file only when the user explicitly requests it.
 * (For example, in Google Earth, the user right-clicks and selects Refresh in the Context menu.)
 * <li>onRegion - Refresh the file when the Region becomes active. See <Region>.
 * </ul>
 */
object ViewRefreshMode {
  /**
   * Creates a specific ViewRefreshMode object from the String value, either
   * "never" "onRequest" "onStop" "onRegion"
   * @param value the name of the ViewRefreshMode to construct
   * @return a ViewRefreshMode object or null if the value parameter is not a correct ViewRefreshMode name or is null
   */
  def fromString(value: String): ViewRefreshMode = 
    if (value == null) null else 
    value.trim match {
    case "never" => Never
    case "onRequest" => OnRequest
    case "onStop" => OnStop
    case "onRegion" => OnRegion
    case _ => null
  }
}
/**
 * Specifies the Never link refresh when the "camera" changes
 * Ignore changes in the geographic view. Also ignore viewFormat parameters, if any.
 */
case object Never extends ViewRefreshMode {
  override def toString = "never"
}
/**
 * Specifies the OnRequest link refresh when the "camera" changes
 * Refresh the resource only when the user explicitly requests it.
 */
case object OnRequest extends ViewRefreshMode {
  /**
   *
   * @return "onRequest"
   */
  override def toString = "onRequest"
}
/**
 * Specifies the OnStop link refresh when the "camera" changes
 * Refresh the resource n seconds after movement stops, where n is specified in viewRefreshTime.
 */
case object OnStop extends ViewRefreshMode {
  /**
   *
   * @return "onStop"
   */
  override def toString = "onStop"
}
/**
 * Specifies the OnRegion link refresh when the "camera" changes
 * Refresh the resource if a Region becomes active.
 */
case object OnRegion extends ViewRefreshMode {
  /**
   *
   * @return "onRegion"
   */
  override def toString = "onRegion"
}

trait Shape

/**
 * The PhotoOverlay is projected onto the <shape>. The <shape> can be one of the following:
 * <ul>
 * <li>rectangle (default) - for an ordinary photo
 * <li>cylinder - for panoramas, which can be either partial or full cylinders
 * <li>sphere - for spherical panoramas
 * </ul>
 */
object Shape {
  /**
   * Creates a specific Shape object from the String value, either
   * "rectangle" "cylinder" "sphere"
   * @param value the name of the Shape to construct
   * @return a Shape object or null if the value parameter is not a correct Shape name or is null
   */
  def fromString(value: String): Shape = 
    if (value == null) null else 
    value.trim match {
    case "rectangle" => Rectangle
    case "cylinder" => Cylinder
    case "sphere" => Sphere
    case _ => null
  }
}

/**
 * The PhotoOverlay is projected onto a Rectangle
 * Used for an ordinary photo.
 */
case object Rectangle extends Shape {
  /**
   *
   * @return "rectangle"
   */
  override def toString = "rectangle"
}
/**
 * The PhotoOverlay is projected onto a Cylinder
 * Used for panoramas, which can be either partial or full cylinders.
 */
case object Cylinder extends Shape {
  /**
   *
   * @return "cylinder"
   */
  override def toString = "cylinder"
}
/**
 * The PhotoOverlay is projected onto a Sphere
 * Used for spherical panoramas.
 */
case object Sphere extends Shape {
  /**
   *
   * @return "sphere"
   */
  override def toString = "sphere"
}

/**
 * The StyleState in StyleMap Pair
 */
trait StyleState

/**
 * The StyleState in StyleMap Pair, can be normal or highlight
 * @see StyleMap and Pair
 */
object StyleState {
  /**
   * Creates a specific StyleState object from the String value, either
   * "normal" "highlight"
   * @param value the name of the StyleState to construct
   * @return a StyleState object or null if the value parameter is not a correct StyleState name or is null
   */
  def fromString(value: String): StyleState = 
    if (value == null) null else 
    value.trim match {
    case "normal" => Normal
    case "highlight" => Highlight
    case _ => null
  }
}

/**
 * The normal StyleState
 */
case object Normal extends StyleState {
  /**
   *
   * @return "normal"
   */
  override def toString = "normal"
}
/**
 * The highlight StyleState
 */
case object Highlight extends StyleState {
  /**
   *
   * @return "highlight"
   */
  override def toString = "highlight"
}

trait Units

/**
 *
 * Field types used in specifying an image coordinate system, see Vec2 class
 *
 * @see class Vec2 and <hotSpot> in <IconStyle>, <ScreenOverlay>
 */
object Units {
  /**
   * Creates a specific Units object from the String value, either
   * "fraction" "pixels" "insetPixels"
   * @param value the name of the Units to construct
   * @return a Units object or null if the value parameter is not a correct Units name or is null
   */
  def fromString(value: String): Units = 
    if (value == null) null else 
    value.trim match {
    case "fraction" => Fraction
    case "pixels" => Pixels
    case "insetPixels" => InsetPixels
    case _ => null
  }
}

/**
 * Value is a fraction of the icon.
 */
case object Fraction extends Units {
  /**
   *
   * @return "fraction"
   */
  override def toString = "fraction"
}

/**
 * Value is a specific pixel size.
 */
case object Pixels extends Units {
  /**
   *
   * @return "pixels"
   */
  override def toString = "pixels"
}

/**
 * Value is an offset in pixels from the upper right corner of the icon.
 */
case object InsetPixels extends Units {
  /**
   *
   * @return "insetPixels"
   */
  override def toString = "insetPixels"
}

/**
 * Enumeration of all Feature types.
 */
object FeatureTypes extends Enumeration {
  type FeatureTypes = Value
  val Document, Folder, Placemark, NetworkLink, PhotoOverlay, ScreenOverlay, GroundOverlay, Tour = Value
}
/**
 * Enumeration of all Container types.
 */
object ContainerTypes extends Enumeration {
  type ContainerTypes = Value
  val Document, Folder = Value
}
/**
 * Enumeration of all Geometry types.
 */
object GeometryTypes extends Enumeration {
  type GeometryTypes = Value
  val Point, LineString, LinearRing, Polygon, MultiGeometry, Model, Track, MultiTrack = Value
}
/**
 * Enumeration of all Style types.
 */
object StyleTypes extends Enumeration {
  type StyleTypes = Value
  val LineStyle, PolyStyle, IconStyle, LabelStyle, BalloonStyle, ListStyle = Value
}
/**
 * Enumeration of all UpdateOption types.
 */
object UpdateOptionTypes extends Enumeration {
  type UpdateOptionTypes = Value
  val Create, Delete, Change = Value
}
/**
 * Enumeration of all Kml Object types.
 */
object KmlObjectTypes extends Enumeration {
  type KmlObjectTypes = Value
  val Document, Folder, Placemark, NetworkLink, PhotoOverlay, ScreenOverlay, GroundOverlay, Tour,
  Camera, LookAt, Data, SchemaData, MultiGeometry, Point, LineString, LinearRing,
  Polygon, Model, Style, StyleMap, TimeStamp, TimeSpan, Region, LatLonAltBox, Lod,
  Icon, Link, Location, Orientation, Scale, ResourceMap, Alias, ViewVolume,
  ImagePyramid, Pair, LineStyle, PolyStyle, IconStyle, LabelStyle, BalloonStyle,
  ListStyle, ItemIcon = Value
}

/**
 *
 * Specifies an image coordinate system.
 * The x and y values may each be specified in three different ways:
 * as pixels (pixels), as fractions of the icon (fraction), or as inset pixels (insetPixels),
 * which is an offset in pixels from the upper right corner of the icon.
 * They may or may not be specified in a consistent manner -
 * for example, x can be specified in pixels and y as a fraction.
 *
 * @see Object Units and <hotSpot> in <IconStyle>, <ScreenOverlay>
 */
case class Vec2(val x: Double, val y: Double, val xunits: Units, val yunits: Units) {
  def this() = this(0.0, 0.0, Fraction, Fraction)
}


// TODO  could be more scala like
object HexColor {

  def colorFromHex(hexVal: String): java.awt.Color = {
    if ((hexVal == null) || (hexVal.length() != 8)) java.awt.Color.white
    else {
      val alpha = Integer.valueOf(hexVal.substring(0, 2), 16).intValue()
      val r = Integer.valueOf(hexVal.substring(2, 4), 16).intValue()
      val g = Integer.valueOf(hexVal.substring(4, 6), 16).intValue()
      val b = Integer.valueOf(hexVal.substring(6, 8), 16).intValue()
      new java.awt.Color(r, g, b, alpha)
    }
  }

  def colorToHex(color: java.awt.Color): String = {
    if (color == null) "ffffffff" else Integer.toHexString( 0x100000 | color.getRGB )
  }

}

/**
 * color class for a hex string
 * @param hexString the hex string representing the color, default white ffffffff
 */
case class HexColor(hexString: String = "ffffffff") {
  def this() = this("ffffffff")
  def asColor = HexColor.colorFromHex(hexString)
}



/**
 * This is an abstract base class and cannot be used directly in a KML file.
 * It provides the id attribute, which allows unique identification of a KML element,
 * and the targetId attribute, which is used to reference objects that have already been
 * loaded into Google Earth. The id attribute must be assigned if the <Update> mechanism is to be used.
 * The following elements can be used wherever this element is referenced:
 * Document, Folder, Placemark, NetworkLink, PhotoOverlay, ScreenOverlay, GroundOverlay,
 * Tour,Camera, LookAt, Data, SchemaData, MultiGeometry, Point, LineString, LinearRing,
 * Polygon, Model, Style, StyleMap, TimeStamp, TimeSpan, Region, LatLonAltBox, Lod,Icon,
 * Link, Location, Orientation, Scale, ResourceMap, Alias, ViewVolume,ImagePyramid, Pair,
 * LineStyle, PolyStyle, IconStyle, LabelStyle, BalloonStyle,ListStyle, ItemIcon
 */
trait KmlObject {
  val id: Option[String]
  val targetId: Option[String]
  val objectSimpleExtensionGroup: Seq[Any]
}

/**
 * This is a base class and cannot be used directly in a KML file. FeaturePart is a field in
 * the Feature trait. That trait is extended by: Document, Folder, Placemark, NetworkLink, PhotoOverlay, ScreenOverlay, GroundOverlay, Tour.
 * In these classes FeaturePart is a component field.
 * Note: do not confuse FeaturePart and a Feature that encapsulate it, that is the concrete classes that extend Feature trait.
 * @see Feature
 *
 * @param name User-defined text displayed in the 3D viewer as the label for the object
 *             (for example, for a Placemark, Folder, or NetworkLink).
 * @param visibility Specifies whether the feature is drawn in the 3D viewer when it is initially loaded. In order for a feature to be
 * visible, the visibility of all its ancestors must also be set to true. Defaults to true.
 * @param open Specifies whether a Document or Folder appears closed or open when first loaded into the Places panel.
 * False=collapsed (the default), true=expanded. This element applies only to Document, Folder, and NetworkLink.
 * @see ListStyle
 * @param atomAuthor KML "2.2" supports new elements for including data about the author and related website in your KML file. This
 * information is displayed in geo search results, both in Earth browsers such as Google Earth, and in other
 * applications such as Google Maps. The ascription elements used in KML are as follows:
 * <ul>
 *  <li> atom:author element - parent element for atom:name
 *  <li> atom:name element - the name of the author
 *  <li> atom:link element - contains the href attribute
 *  <li> href attribute - URL of the web page containing the KML/KMZ file
 *  </ul>
 * These elements are defined in the Atom Syndication Format. The complete specification is found at
 * http://atompub.org.
 *
 * The &lt;atom:author&gt; element is the parent element for &lt;atom:name&gt, which specifies the author of the KML
 * feature.
 * @param atomLink Specifies the URL of the website containing this KML or KMZ file.
 * Implementation note: Be sure to include the namespace for this element in any KML file that uses it:
 * xmlns:atom="http://www.w3.org/2005/Atom" .
 * @param address A string value representing an unstructured address written as a standard street, city, state address, and/or as a
 * postal code. You can use the <address> tag to specify the location of a point instead of using latitude and
 * longitude coordinates. (However, if a <Point> is provided, it takes precedence over the <address>.) To find out
 * which locales are supported for this tag in Google Earth, go to the Google Maps Help.
 * @param addressDetails A string value representing a telephone number. This element is used by Google Maps Mobile only. The industry
 * standard for Java-enabled cellular phones is RFC2806. For more information,
 * see http://www.ietf.org/rfc /rfc2806.txt.
 * @param phoneNumber A string value representing a telephone number. This element is used by Google Maps Mobile only. The industry
 * standard for Java-enabled cellular phones is RFC2806.
 * For more information, see http://www.ietf.org/rfc /rfc2806.txt.
 * @param extendedData Allows you to add custom data to a KML file. This data can be:
 * <ul>
 * <li>(1) data that references an external XML schema,
 * <li>(2) untyped data/value pairs, or
 * <li>(3) typed data. A given KML Feature can contain a combination of these types of custom data.
 * </ul>
 * @param description User-supplied content that appears in the description balloon.
 * The supported content for the <description> element changed from Google Earth 4.3 to 5.0. See the on-line
 * documentation for extensive details.
 * @param snippet A short description of the feature. In Google Earth, this description is displayed in the Places panel under the
 * name of the feature. If a Snippet is not supplied, the first two lines of the <description> are used. In Google
 * Earth, if a Placemark contains both a description and a Snippet, the <Snippet> appears beneath the Placemark in
 * the Places panel, and the <description> appears in the Placemark's description balloon. This tag does not support
 * HTML markup. <Snippet> has a maxLines attribute, an integer that specifies the maximum
 * number of lines to display.
 * @param abstractView  Defines a viewpoint associated with any element derived from Feature.
 * @see Camera and LookAt
 * @param timePrimitive Associates this Feature with a period of time (<TimeSpan>) or a point in time (<TimeStamp>).
 * @param styleUrl URL of a <Style> or <StyleMap> defined in a Document. If the style is in the same file, use a # reference. If the
 * style is defined in an external file, use a full URL along with # referencing.
 * @param styleSelector One or more Styles and StyleMaps can be defined to customize the appearance of any element derived from Feature or
 * of the Geometry in a Placemark.
 * A style defined within a Feature is called an "inline style" and applies only to the Feature that contains it. A
 * style defined as the child of a &lt;Document&gt; is called a "shared style." A shared style must have an id
 * defined for it. This id is referenced by one or more Features within the &lt;Document&gt;. In cases where a style
 * element is defined both in a shared style and in an inline style for a Feature—that is, a Folder, GroundOverlay,
 * NetworkLink, Placemark, or ScreenOverlay—the value for the Feature's inline style takes precedence over the value
 * for the shared style.
 * @see BalloonStyle, ListStyle, StyleSelector, ColorStyle
 * @param region  Features and geometry associated with a Region are drawn only when the Region is active.
 * @param featureSimpleExtensionGroup  Simple Element Substitution.
 * This is an abstract base class and cannot be used directly in a KML file. It provides
 * the id attribute, which allows unique identification of a KML element, and the targetId
 * attribute, which is used to reference objects that have already been loaded into
 * Google Earth. The id attribute must be assigned if the <Update> mechanism is to
 * be used.
 * @param featureObjectExtensionGroup  Complex Element Substitution.
 * This is an abstract base class and cannot be used directly in a KML file. It provides
 * the id attribute, which allows unique identification of a KML element, and the targetId
 * attribute, which is used to reference objects that have already been loaded into
 * Google Earth. The id attribute must be assigned if the <Update> mechanism is to
 * be used.
 */
case class FeaturePart(
  name: Option[String] = None,
  visibility: Option[Boolean] = None,
  open: Option[Boolean] = None,
  atomAuthor: Option[com.scalakml.atom.Author] = None,
  atomLink: Option[com.scalakml.atom.Link] = None,
  address: Option[String] = None,
  addressDetails: Option[AddressDetails] = None, // <---- from com.scalaxal.xAL.xAL
  phoneNumber: Option[String] = None,
  extendedData: Option[ExtendedData] = None,
  description: Option[String] = None,
  snippet: Option[Snippet] = None,
  abstractView: Option[AbstractView] = None,
  timePrimitive: Option[TimePrimitive] = None,
  styleUrl: Option[String] = None,
  styleSelector: Seq[StyleSelector] = Nil,
  region: Option[Region] = None,
  featureSimpleExtensionGroup: Seq[Any] = Nil,
  featureObjectExtensionGroup: Seq[Any] = Nil) {

  def this(name: String) = this(Option(name))
  def this(name: String, visibility: Boolean) = this(Option(name), Option(visibility))

  def this(name: String, visibility: Boolean, open: Boolean, atomAuthor: com.scalakml.atom.Author, atomLink: com.scalakml.atom.Link,
           address: String, addressDetails: AddressDetails, phoneNumber:String, extendedData: ExtendedData,
           description: String, snippet: Snippet, abstractView: AbstractView, timePrimitive: TimePrimitive,
           styleUrl: String, styleSelector: Seq[StyleSelector], region: Region) =
    this(Option(name), Option(visibility), Option(open), Option(atomAuthor), Option(atomLink), Option(address),
      Option(addressDetails),Option(phoneNumber), Option(extendedData), Option(description), Option(snippet),
      Option(abstractView), Option(timePrimitive), Option(styleUrl),
      styleSelector, Option(region))

  /**
   * returns a new object with value added to the sequence
   * @param value to add
   * @return a new object with value added to the sequence
   */
  def addToStyleSelector(value: StyleSelector) = {
    this.copy(styleSelector = if (styleSelector == Nil) (Seq.empty :+ value) else (styleSelector :+ value))
  }

}
/**
 * This is an abstract element and cannot be used directly in a KML file.
 * This trait is extended by: Document, Folder, Placemark, NetworkLink, PhotoOverlay, ScreenOverlay, GroundOverlay, Tour
 *
 * @see FeaturePart
 */
trait Feature extends KmlObject {
  /**
   * featurePart the feature part of this object
   * @see FeaturePart
   */
  val featurePart: FeaturePart
  /**
   * a unique identification of this KML element
   */
  val id: Option[String]
  /**
   * a reference to objects that have already been loaded into Google Earth
   */
  val targetId: Option[String]
  val objectSimpleExtensionGroup: Seq[Any]
}

/**
 * A Placemark is a Feature with associated Geometry. In Google Earth, a Placemark appears as a list item
 * in the Places panel. A Placemark with a Point has an icon associated with it that marks a point on
 * the Earth in the 3D viewer. (In the Google Earth 3D viewer, a Point Placemark is the only object you
 * can click or roll over. Other Geometry objects do not have an icon in the 3D viewer. To give the user
 * something to click in the 3D viewer, you would need to create a MultiGeometry object that contains
 * both a Point and the other Geometry object.)
 *
 * @param geometry 0 or 1 <Geometry> elements, from Point, LineString, LinearRing, Polygon, MultiGeometry, Model, Track, MultiTrack
 * @param featurePart the feature part of this object
 * @param id a unique identification of this KML element
 * @param targetId a reference to objects that have already been loaded into Google Earth
 * @param placemarkSimpleExtensionGroup  placemarkSimpleExtensionGroup
 * @param placemarkObjectExtensionGroup  placemarkObjectExtensionGroup
 * @param objectSimpleExtensionGroup  objectSimpleExtensionGroup
 */
case class Placemark(
  geometry: Option[Geometry] = None,
  featurePart: FeaturePart = new FeaturePart(),
  id: Option[String] = None,
  targetId: Option[String] = None,
  placemarkSimpleExtensionGroup: Seq[Any] = Nil,
  placemarkObjectExtensionGroup: Seq[Any] = Nil,
  objectSimpleExtensionGroup: Seq[Any] = Nil) extends Feature {

  def this(geom: Geometry) = this(Option(geom))
  def this(name: String) = this(None, new FeaturePart(name = Option(name)))
  def this(name: String, geom: Geometry) = this(Option(geom), new FeaturePart(name = Option(name)))
  def this(name: String, geom: Geometry, id: String) = this(Option(geom), new FeaturePart(name = Option(name)), Option(id))
}

/**
 * A short description of the feature. In Google Earth, this description is displayed in the Places panel
 * under the name of the feature. If a Snippet is not supplied, the first two lines of the <description> are used.
 * In Google Earth, if a Placemark contains both a description and a Snippet, the <Snippet> appears beneath
 * the Placemark in the Places panel, and the <description> appears in the Placemark's description balloon.
 * This tag does not support HTML markup. <Snippet> has a maxLines attribute, an integer that specifies the
 * maximum number of lines to display.
 *
 * @param value a short text description of the feature
 * @param maxLines maximum number of lines to display
 */
case class Snippet(value: String = "", maxLines: Int = 0)

/**
 * This is an abstract element and cannot be used directly in a KML file.
 * This element is extended by the <Camera> and <LookAt> elements.
 */
trait AbstractView extends KmlObject {
  val longitude: Option[Double]
  val latitude: Option[Double]
  val altitude: Option[Double]
  val heading: Option[Double]
  val tilt: Option[Double]
  val altitudeMode: Option[AltitudeMode]
  val id: Option[String]
  val targetId: Option[String]
  val abstractViewSimpleExtensionGroup: Seq[Any]
  val abstractViewObjectExtensionGroup: Seq[Any]
  val objectSimpleExtensionGroup: Seq[Any]
}

/**
 * Defines a virtual camera that is associated with any element derived from Feature.
 * The LookAt element positions the "camera" in relation to the object that is being viewed.
 * In Google Earth, the view "flies to" this LookAt viewpoint when the user double-clicks
 * an item in the Places panel or double-clicks an icon in the 3D viewer.
 *
 * @param range Distance in meters from the point specified by <longitude>, <latitude>, and <altitude> to the LookAt position.
 * @param longitude Longitude of the point the camera is looking at. Angular distance in degrees, relative to
 *                  the Prime Meridian. Values west of the Meridian range from −180 to 0 degrees. Values east of the Meridian range from 0 to 180 degrees.
 * @param latitude Latitude of the point the camera is looking at. Degrees north or south of the Equator (0 degrees).
 *                 Values range from −90 degrees to 90 degrees.
 * @param altitude Distance from the earth's surface, in meters. Interpreted according to the LookAt's altitude mode.
 * @param heading Direction (that is, North, South, East, West), in degrees. Default=0 (North). (See diagram below.) Values range from 0 to 360 degrees.
 * @param tilt Angle between the direction of the LookAt position and the normal to the surface of the earth.
 *             Values range from 0 to 90 degrees. Values for <tilt> cannot be negative. A <tilt> value of 0 degrees indicates viewing from directly above. A <tilt> value of 90 degrees indicates viewing along the horizon.
 * @param altitudeMode Specifies how the <altitude> specified for the LookAt point is interpreted.
 *                     Possible values are as follows:
 *                     <ul>
 *  <li>clampToGround - (default) Indicates to ignore the <altitude> specification and place the LookAt position on the ground.
 *  <li>relativeToGround - Interprets the <altitude> as a value in meters above the ground.
 *  <li>absolute - Interprets the <altitude> as a value in meters above sea level.
 *  </ul>
 * @param id
 * @param targetId
 * @param lookAtSimpleExtensionGroup
 * @param lookAtObjectExtensionGroup
 * @param abstractViewSimpleExtensionGroup
 * @param abstractViewObjectExtensionGroup
 * @param objectSimpleExtensionGroup
 */
case class LookAt(
  range: Option[Double] = None,
  longitude: Option[Double] = None,
  latitude: Option[Double] = None,
  altitude: Option[Double] = None,
  heading: Option[Double] = None,
  tilt: Option[Double] = None,
  altitudeMode: Option[AltitudeMode] = None,
  id: Option[String] = None,
  targetId: Option[String] = None,
  lookAtSimpleExtensionGroup: Seq[Any] = Nil,
  lookAtObjectExtensionGroup: Seq[Any] = Nil,
  abstractViewSimpleExtensionGroup: Seq[Any] = Nil,
  abstractViewObjectExtensionGroup: Seq[Any] = Nil,
  objectSimpleExtensionGroup: Seq[Any] = Nil) extends AbstractView {

  def this(range: Double) = this(Option(range))

  def this(range: Double, longitude: Double, latitude: Double, altitude: Double) =
    this(Option(range), Option(longitude), Option(latitude), Option(altitude))

  def this(range: Double, longitude: Double, latitude: Double, altitude: Double, heading: Double,
           tilt: Double, altitudeMode: AltitudeMode, id: String) =
    this(Option(range), Option(longitude), Option(latitude), Option(altitude), Option(heading), Option(tilt), Option(altitudeMode) ,Option(id))

}

/**
 * Camera Defines the virtual camera that views the scene. This element defines the position of the camera
 * relative to the Earth's surface as well as the viewing direction of the camera.
 * The camera position is defined by <longitude>, <latitude>, <altitude>, and either <altitudeMode> or
 * <gx:altitudeMode>. The viewing direction of the camera is defined by <heading>, <tilt>, and <roll>.
 * <Camera> can be a child element of any Feature or of <NetworkLinkControl>.
 * A parent element cannot contain both a <Camera> and a <LookAt> at the same time.
 *
 * <Camera> provides full six-degrees-of-freedom control over the view, so you can position the Camera
 * in space and then rotate it around the X, Y, and Z axes. Most importantly, you can tilt the camera
 * view so that you're looking above the horizon into the sky.
 *
 * <Camera> can also contain a TimePrimitive (<gx:TimeSpan> or <gx:TimeStamp>).
 * Time values in Camera affect historical imagery, sunlight, and the display of time-stamped features.
 * For more information, read Time with AbstractViews in the Time and Animation chapter of the Developer's Guide.
 *
 * @param roll Rotation, in degrees, of the camera around the Z axis. Values range from −180 to +180 degrees.
 * @param longitude Longitude of the point the camera is looking at. Angular distance in degrees, relative to
 *                  the Prime Meridian. Values west of the Meridian range from −180 to 0 degrees. Values east of the Meridian range from 0 to 180 degrees.
 * @param latitude Latitude of the point the camera is looking at. Degrees north or south of the Equator (0 degrees).
 *                 Values range from −90 degrees to 90 degrees.
 * @param altitude Distance from the earth's surface, in meters. Interpreted according to the LookAt's altitude mode.
 * @param heading Direction (that is, North, South, East, West), in degrees. Default=0 (North). (See diagram below.) Values range from 0 to 360 degrees.
 * @param tilt Angle between the direction of the LookAt position and the normal to the surface of the earth.
 *             Values range from 0 to 90 degrees. Values for <tilt> cannot be negative. A <tilt> value of 0 degrees indicates viewing from directly above. A <tilt> value of 90 degrees indicates viewing along the horizon.
 * @param altitudeMode Specifies how the <altitude> specified for the LookAt point is interpreted.
 *                     Possible values are as follows:
 *                     <ul>
 *  <li>clampToGround - (default) Indicates to ignore the <altitude> specification and place the LookAt position on the ground.
 *  <li>relativeToGround - Interprets the <altitude> as a value in meters above the ground.
 *  <li>absolute - Interprets the <altitude> as a value in meters above sea level.
 *  </ul>
 *
 * @param id
 * @param targetId
 * @param cameraSimpleExtensionGroup
 * @param cameraObjectExtensionGroup
 * @param abstractViewSimpleExtensionGroup
 * @param abstractViewObjectExtensionGroup
 * @param objectSimpleExtensionGroup
 */
case class Camera(
  roll: Option[Double] = None,
  longitude: Option[Double] = None,
  latitude: Option[Double] = None,
  altitude: Option[Double] = None,
  heading: Option[Double] = None,
  tilt: Option[Double] = None,
  altitudeMode: Option[AltitudeMode] = None,
  id: Option[String] = None,
  targetId: Option[String] = None,
  cameraSimpleExtensionGroup: Seq[Any] = Nil,
  cameraObjectExtensionGroup: Seq[Any] = Nil,
  abstractViewSimpleExtensionGroup: Seq[Any] = Nil,
  abstractViewObjectExtensionGroup: Seq[Any] = Nil,
  objectSimpleExtensionGroup: Seq[Any] = Nil) extends AbstractView {

  def this(roll: Double) = this(Option(roll))

  def this(roll: Double, longitude: Double, latitude: Double, altitude: Double) =
    this(Option(roll), Option(longitude), Option(latitude), Option(altitude))

  def this(roll: Double, longitude: Double, latitude: Double, altitude: Double, heading: Double, tilt: Double,
           altitudeMode: AltitudeMode, id: String) =
    this(Option(roll), Option(longitude), Option(latitude), Option(altitude), Option(heading), Option(tilt), Option(altitudeMode) ,Option(id))
}

/**
 * The ExtendedData element offers three techniques for adding custom data to a KML Feature (NetworkLink, Placemark, GroundOverlay, PhotoOverlay, ScreenOverlay, Document, Folder). These techniques are
 * <ul>
 * <li>Adding untyped data/value pairs using the <Data> element (basic)
 * <li>Declaring new typed fields using the <Schema> element and then instancing them using the <SchemaData> element (advanced)
 * <li>Referring to XML elements defined in other namespaces by referencing the external namespace within the KML file (basic)
 * </ul>
 * These techniques can be combined within a single KML file or Feature for different pieces of data.
 * For more information, see Adding Custom Data in "Topics in KML."
 *
 * @param data
 * @param schemaData This element is used in conjunction with <Schema> to add typed custom data to a KML Feature.
 * @param other
 */
case class ExtendedData(data: Seq[Data] = Nil, schemaData: Seq[SchemaData] = Nil, other: Seq[Any] = Nil)

/**
 *  This element is used in conjunction with <Schema> to add typed custom data to a KML Feature.
 *  The Schema element (identified by the schemaUrl attribute) declares the custom data type.
 *  The actual data objects ("instances" of the custom data) are defined using the SchemaData element.
 *  The <schemaURL> can be a full URL, a reference to a Schema ID defined in an external KML file,
 *  or a reference to a Schema ID defined in the same KML file. All of the following specifications are acceptable:
 *  <ul>
 *  <li> schemaUrl="http://host.com/PlacesIHaveLived.kml#my-schema-id"
 *  <li> schemaUrl="AnotherFile.kml#my-schema-id"
 *  <li> schemaUrl="#schema-id"   <!-- same file -->
 *  </ul>
 * The Schema element is always a child of Document. The ExtendedData element is a child of the Feature that contains the custom data.
 *
 * @param simpleData This element assigns a value to the custom data field identified by the name attribute.
 * @param schemaUrl
 * @param id
 * @param targetId
 * @param schemaDataExtension
 * @param objectSimpleExtensionGroup
 */
case class SchemaData(simpleData: Seq[SimpleData] = Nil,
  schemaUrl: Option[String] = None,
  id: Option[String] = None,
  targetId: Option[String] = None,
  schemaDataExtension: Seq[Any] = Nil,
  objectSimpleExtensionGroup: Seq[Any] = Nil) extends KmlObject {

  def this(simpleData: SimpleData) = this(Seq.empty :+ simpleData)
  def this(simpleData: SimpleData, schemaUrl: String) = this((Seq.empty :+ simpleData), Option(schemaUrl))
  def this(simpleData: SimpleData, schemaUrl: String, id: String) = this((Seq.empty :+ simpleData), Option(schemaUrl), Option(id))

  /**
   * returns a new object with value added to the sequence
   * @param value to add
   * @return a new object with value added to the sequence
   */
  def addToSimpleData(value: SimpleData) = {
    this.copy(simpleData = if (simpleData == Nil) (Seq.empty :+ value) else (simpleData :+ value))
  }

}

/**
 * The Schema element is always a child of Document. The ExtendedData element is a child of the Feature
 * that contains the custom data. <SimpleData name="string">
 * This element assigns a value to the custom data field identified by the name attribute.
 * The type and name of this custom data field are declared in the <Schema> element.
 *
 * @param value
 * @param name
 */
case class SimpleData(value: Option[String] = None, name: Option[String] = None) {
  def this(value: String, name: String) = this(Option(value), Option(name))
}

/**
 * Creates an untyped name/value pair. The name can have two versions: name and displayName.
 * The name attribute is used to identify the data pair within the KML file.
 * The displayName element is used when a properly formatted name, with spaces and HTML formatting,
 * is displayed in Google Earth. In the <text> element of <BalloonStyle>, the notation $[name/displayName]
 * is replaced with <displayName>. If you substitute the value of the name attribute of the <Data> element
 * in this format (for example, $[holeYardage], the attribute value is replaced with <value>. By default,
 * the Placemark's balloon displays the name/value pairs associated with it.
 *
 * @param displayName An optional formatted version of name, to be used for display purposes.
 * @param value the value of the Data
 * @param name the name of the Data
 * @param dataExtension
 * @param id
 * @param targetId
 * @param objectSimpleExtensionGroup
 */
case class Data(displayName: Option[String] = None,
  value: Option[String] = None,
  name: Option[String] = None,
  dataExtension: Seq[Any] = Nil,
  id: Option[String] = None,
  targetId: Option[String] = None,
  objectSimpleExtensionGroup: Seq[Any] = Nil) extends KmlObject {

  def this(displayName: String) = this(Option(displayName))
  def this(displayName: String, value: String) = this(Option(displayName), Option(value))
  def this(displayName: String, value: String, name: String) = this(Option(displayName), Option(value), Option(name))

}

trait Container extends Feature {
  val containerSimpleExtensionGroup: Seq[Any]
  val containerObjectExtensionGroup: Seq[Any]
  val featurePart: FeaturePart
  val id: Option[String]
  val targetId: Option[String]
  val objectSimpleExtensionGroup: Seq[Any]
}

trait Geometry extends KmlObject {
  val geometrySimpleExtensionGroup: Seq[Any]
  val geometryObjectExtensionGroup: Seq[Any]
  val id: Option[String]
  val targetId: Option[String]
  val objectSimpleExtensionGroup: Seq[Any]
}

trait Overlay extends Feature {
  val color: Option[HexColor]
  val drawOrder: Option[Int]
  val icon: Option[Icon]
  val overlaySimpleExtensionGroup: Seq[Any]
  val overlayObjectExtensionGroup: Seq[Any]
  val featurePart: FeaturePart
}

trait StyleSelector extends KmlObject {
  val id: Option[String]
  val targetId: Option[String]
  val styleSelectorSimpleExtensionGroup: Seq[Any]
  val styleSelectorObjectExtensionGroup: Seq[Any]
  val objectSimpleExtensionGroup: Seq[Any]
}

trait TimePrimitive extends KmlObject {
  val timePrimitiveSimpleExtensionGroup: Seq[Any]
  val timePrimitiveObjectExtensionGroup: Seq[Any]
  val id: Option[String]
  val targetId: Option[String]
  val objectSimpleExtensionGroup: Seq[Any]
}

/**
 * Kml is the root element of a KML file. This element is required.
 * It follows the xml declaration at the beginning of the file.
 * The hint attribute is used as a signal to Google Earth to display the file as celestial data.
 * The <kml> element may also include the namespace for any external XML schemas that are
 * referenced within the file. A basic <kml> element contains 0 or 1 Feature and 0 or 1 NetworkLinkControl
 *
 * @param networkLinkControl Controls the behavior of files fetched by a <NetworkLink>.
 * @param feature one of Document, Folder, Placemark, NetworkLink, PhotoOverlay, ScreenOverlay, GroundOverlay, Tour
 * @param hint attribute is used as a signal to Google Earth to display the file as celestial data.
 * @param kmlSimpleExtensionGroup
 * @param kmlObjectExtensionGroup
 */
case class Kml(networkLinkControl: Option[NetworkLinkControl] = None,
  feature: Option[Feature] = None,
  hint: Option[String] = None,
  kmlSimpleExtensionGroup: Seq[Any] = Nil,
  kmlObjectExtensionGroup: Seq[Any] = Nil) {

  def this(networkLinkControl: NetworkLinkControl, feature: Feature) = this(Option(networkLinkControl), Option(feature))
  def this(networkLinkControl: NetworkLinkControl) = this(Option(networkLinkControl))
  def this(feature: Feature) = this(None, Option(feature))
}

/**
 * Controls the behavior of files fetched by a <NetworkLink>.
 *
 * @param minRefreshPeriod Specified in seconds, <minRefreshPeriod> is the minimum allowed time between fetches of the file. This parameter allows servers to throttle fetches of a particular file and to tailor refresh rates to the expected rate of change to the data. For example, a user might set a link refresh to 5 seconds, but you could set your minimum refresh period to 3600 to limit refresh updates to once every hour.
 * @param maxSessionLength Specified in seconds, <maxSessionLength> is the maximum amount of time for which the client NetworkLink can remain connected. The default value of -1 indicates not to terminate the session explicitly.
 * @param cookie Use this element to append a string to the URL query on the next refresh of the network link. You can use this data in your script to provide more intelligent handling on the server side, including version querying and conditional file delivery.
 * @param message You can deliver a pop-up message, such as usage guidelines for your network link. The message appears when the network link is first loaded into Google Earth, or when it is changed in the network link control.
 * @param linkName You can control the name of the network link from the server, so that changes made to the name on the client side are overridden by the server.
 * @param linkDescription You can control the description of the network link from the server, so that changes made to the description on the client side are overridden by the server.
 * @param linkSnippet You can control the snippet for the network link from the server, so that changes made to the snippet on the client side are overridden by the server. <linkSnippet> has a maxLines attribute, an integer that specifies the maximum number of lines to display.
 * @param expires You can specify a date/time at which the link should be refreshed. This specification takes effect only if the <refreshMode> in <Link> has a value of onExpire. See <refreshMode>
 * @param update with <Update>, you can specify any number of Change, Create, and Delete tags for a .kml file or .kmz archive that has previously been loaded with a network link. See <Update>.
 * @param abstractView a LookAt or a Camera
 * @param networkLinkControlSimpleExtensionGroup
 * @param networkLinkControlObjectExtensionGroup
 */
case class NetworkLinkControl(minRefreshPeriod: Option[Double] = None,
  maxSessionLength: Option[Double] = None,
  cookie: Option[String] = None,
  message: Option[String] = None,
  linkName: Option[String] = None,
  linkDescription: Option[String] = None,
  linkSnippet: Option[Snippet] = None,
  expires: Option[String] = None,
  update: Option[Update] = None,
  abstractView: Option[AbstractView] = None,
  networkLinkControlSimpleExtensionGroup: Seq[Any] = Nil,
  networkLinkControlObjectExtensionGroup: Seq[Any] = Nil) {

  def this(minRefreshPeriod: Double, maxSessionLength: Double) =  this(Option(minRefreshPeriod), Option(maxSessionLength))
  def this(minRefreshPeriod: Double, maxSessionLength: Double, expires: String, update: Update) =
    this(Option(minRefreshPeriod), Option(maxSessionLength), None, None, None, None, None, Option(expires), Option(update))

}

/**
 * A Document is a container for features and styles.
 * This element is required if your KML file uses shared styles.
 * It is recommended that you use shared styles, which require the following steps:
 * Define all Styles in a Document. Assign a unique ID to each Style.
 * Within a given Feature or StyleMap, reference the Style's ID using a <styleUrl> element.
 *
 * Note that shared styles are not inherited by the Features in the Document.
 * Each Feature must explicitly reference the styles it uses in a <styleUrl> element. For a Style that applies to a Document (such as ListStyle), the Document itself must explicitly reference the <styleUrl>.
 *
 * @param featurePart the feature part of this Document
 * @param schemas set of schema for this Document
 * @param features set of Features for this Document
 * @param id
 * @param targetId
 * @param documentSimpleExtensionGroup
 * @param documentObjectExtensionGroup
 * @param containerSimpleExtensionGroup
 * @param containerObjectExtensionGroup
 * @param objectSimpleExtensionGroup
 */
case class Document(featurePart: FeaturePart = new FeaturePart(),
  schemas: Seq[Schema] = Nil,
  features: Seq[Feature] = Nil,
  id: Option[String] = None,
  targetId: Option[String] = None,
  documentSimpleExtensionGroup: Seq[Any] = Nil,
  documentObjectExtensionGroup: Seq[Any] = Nil,
  containerSimpleExtensionGroup: Seq[Any] = Nil,
  containerObjectExtensionGroup: Seq[Any] = Nil,
  objectSimpleExtensionGroup: Seq[Any] = Nil) extends Container {

  def this(feature: Feature) = this(new FeaturePart(), Nil, (Seq.empty :+ feature))
  def this(name: String, feature: Feature) = this(new FeaturePart(name = Option(name)), Nil, (Seq.empty :+ feature))
  def this(name: String, feature: Feature, schema: Schema) =
    this(new FeaturePart(name = Option(name)), (Seq.empty :+ schema), (Seq.empty :+ feature))

  /**
   * returns a new object with value added to the sequence
   * @param value to add
   * @return a new object with value added to the sequence
   */
  def addToSchemas(value: Schema) = {
    this.copy(schemas = if (schemas == Nil) (Seq.empty :+ value) else (schemas :+ value))
  }

  /**
   * returns a new object with value added to the sequence
   * @param value to add
   * @return a new object with value added to the sequence
   */
  def addToFeatures(value: Feature) = {
    this.copy(features = if (features == Nil) (Seq.empty :+ value) else (features :+ value))
  }
}

/**
 * Specifies a custom KML schema that is used to add custom data to KML Features.
 * The "id" attribute is required and must be unique within the KML file. <Schema> is always a child of <Document>.
 *
 * @param simpleField
 * @param name
 * @param id
 * @param schemaExtensionGroup
 */
case class Schema(simpleField: Seq[SimpleField] = Nil,
  name: Option[String] = None,
  id: Option[String] = None,
  schemaExtensionGroup: Seq[Any] = Nil) {

  def this(simpleField: SimpleField) = this(Seq.empty :+ simpleField)
  def this(name: String, simpleField: SimpleField) = this((Seq.empty :+ simpleField), Option(name))
  def this(name: String, simpleField: SimpleField, id: String) = this((Seq.empty :+ simpleField), Option(name), Option(id))

  /**
   * returns a new object with value added to the sequence
   * @param value to add
   * @return a new object with value added to the sequence
   */
  def addToSimpleField(value: SimpleField) = {
    this.copy(simpleField = if (simpleField == Nil) (Seq.empty :+ value) else (simpleField :+ value))
  }
}

/**
 * A Schema element contains one or more SimpleField elements.
 * In the SimpleField, the Schema declares the type and name of the custom field.
 * It optionally specifies a displayName (the user-friendly form, with spaces and
 * proper punctuation used for display in Google Earth) for this custom field.
 *
 * @param displayName The name, if any, to be used when the field name is displayed to the Google Earth user. Use the [CDATA] element to escape standard HTML markup.
 * @param typeValue The declaration of the custom field, which must specify both the type and the name of this field.
 *                  If either the type or the name is omitted, the field is ignored.
 * @param name
 * @param simpleFieldExtensionGroup
 */
case class SimpleField(displayName: Option[String] = None,
  typeValue: Option[String] = None,
  name: Option[String] = None,
  simpleFieldExtensionGroup: Seq[Any] = Nil) {

  def this(typeValue: String) = this(None, Option(typeValue))
  def this(typeValue: String, name: String) = this(None, Option(typeValue), Option(name))
  def this(displayName: String, typeValue: String, name: String) = this(Option(displayName), Option(typeValue), Option(name))

}

/**
 * A Folder is used to arrange other Features hierarchically (Folders, Placemarks, NetworkLinks, or Overlays).
 * A Feature is visible only if it and all its ancestors are visible.
 *
 * @param features set of Features for this Folder
 * @param featurePart the FeaturePart of this Folder
 * @param id
 * @param targetId
 * @param folderSimpleExtensionGroup
 * @param folderObjectExtensionGroup
 * @param containerSimpleExtensionGroup
 * @param containerObjectExtensionGroup
 * @param objectSimpleExtensionGroup
 */
case class Folder(features: Seq[Feature] = Nil,
  featurePart: FeaturePart = new FeaturePart(),
  id: Option[String] = None,
  targetId: Option[String] = None,
  folderSimpleExtensionGroup: Seq[Any] = Nil,
  folderObjectExtensionGroup: Seq[Any] = Nil,
  containerSimpleExtensionGroup: Seq[Any] = Nil,
  containerObjectExtensionGroup: Seq[Any] = Nil,
  objectSimpleExtensionGroup: Seq[Any] = Nil) extends Container {

  def this(feature: Feature) = this(Seq.empty :+ feature)
  def this(name: String, feature: Feature) = this((Seq.empty :+ feature), new FeaturePart(name = Option(name)))
  def this(name: String, feature: Feature, id: String) =
    this((Seq.empty :+ feature), new FeaturePart(name = Option(name)), Option(id))

  /**
   * returns a new object with value added to the sequence
   * @param value to add
   * @return a new object with value added to the sequence
   */
  def addToFeatures(value: Feature) = {
    this.copy(features = if (features == Nil) (Seq.empty :+ value) else (features :+ value))
  }
}



/**
 * Features and geometry associated with a Region are drawn only when the Region is active.
 *
 * @param latLonAltBox describes an area of interest defined by geographic coordinates and altitudes.
 * @param lod defines a validity range of the associated the Region in terms of projected screen size.
 * @param id
 * @param targetId
 * @param regionSimpleExtensionGroup
 * @param regionObjectExtensionGroup
 * @param objectSimpleExtensionGroup
 */
case class Region(latLonAltBox: Option[LatLonAltBox] = None,
  lod: Option[Lod] = None,
  id: Option[String] = None,
  targetId: Option[String] = None,
  regionSimpleExtensionGroup: Seq[Any] = Nil,
  regionObjectExtensionGroup: Seq[Any] = Nil,
  objectSimpleExtensionGroup: Seq[Any] = Nil) extends KmlObject {

  def this(latLonAltBox: LatLonAltBox) = this(Option(latLonAltBox))
  def this(latLonAltBox: LatLonAltBox, lod: Lod) = this(Option(latLonAltBox), Option(lod))
  def this(latLonAltBox: LatLonAltBox, lod: Lod, id: String) = this(Option(latLonAltBox), Option(lod), Option(id))
}

trait LatLonBoxType extends KmlObject {
  val north: Option[Double]
  val south: Option[Double]
  val east: Option[Double]
  val west: Option[Double]
  val latLonBoxTypeSimpleExtensionGroup: Seq[Any]
  val latLonBoxTypeObjectExtensionGroup: Seq[Any]
  val id: Option[String]
  val targetId: Option[String]
  val objectSimpleExtensionGroup: Seq[Any]
}

/**
 * Specifies where the top, bottom, right, and left sides of a bounding box for the ground overlay are aligned.
 *
 * @param rotation Specifies a rotation of the overlay about its center, in degrees. Values can be ±180. The default is 0 (north). Rotations are specified in a counterclockwise direction.
 * @param north Specifies the latitude of the north edge of the bounding box, in decimal degrees from 0 to ±90.
 * @param south Specifies the latitude of the south edge of the bounding box, in decimal degrees from 0 to ±90.
 * @param east Specifies the longitude of the east edge of the bounding box, in decimal degrees from 0 to ±180. (For overlays that overlap the meridian of 180° longitude, values can extend beyond that range.)
 * @param west Specifies the longitude of the west edge of the bounding box, in decimal degrees from 0 to ±180. (For overlays that overlap the meridian of 180° longitude, values can extend beyond that range.)
 * @param id
 * @param targetId
 * @param latLonBoxTypeSimpleExtensionGroup
 * @param latLonBoxTypeObjectExtensionGroup
 * @param objectSimpleExtensionGroup
 */
case class LatLonBox(rotation: Option[Double] = None,
  north: Option[Double] = None,
  south: Option[Double] = None,
  east: Option[Double] = None,
  west: Option[Double] = None,
  id: Option[String] = None,
  targetId: Option[String] = None,
  latLonBoxTypeSimpleExtensionGroup: Seq[Any] = Nil,
  latLonBoxTypeObjectExtensionGroup: Seq[Any] = Nil,
  objectSimpleExtensionGroup: Seq[Any] = Nil) extends LatLonBoxType {

  def this(rotation: Double, north: Double, south: Double, east: Double, west: Double) =
    this(Option(rotation), Option(north), Option(south), Option(east), Option(west))

  def this(rotation: Double, north: Double, south: Double, east: Double, west: Double, id: String) =
    this(Option(rotation), Option(north), Option(south), Option(east), Option(west), Option(id))

}

/**
 * A bounding box that describes an area of interest defined by geographic coordinates and altitudes.
 *
 * @param minAltitude Specified in meters (and is affected by the altitude mode specification).
 * @param maxAltitude Specified in meters (and is affected by the altitude mode specification).
 * @param altitudeMode Possible values for <altitudeMode> are clampToGround, relativeToGround, and absolute. Possible values for <gx:altitudeMode> are clampToSeaFloor and relativeToSeaFloor. Also see <LatLonBox>.
 * @param north Specifies the latitude of the north edge of the bounding box, in decimal degrees from 0 to ±90.
 * @param south Specifies the latitude of the south edge of the bounding box, in decimal degrees from 0 to ±90.
 * @param east Specifies the longitude of the east edge of the bounding box, in decimal degrees from 0 to ±180. (For overlays that overlap the meridian of 180° longitude, values can extend beyond that range.)
 * @param west Specifies the longitude of the west edge of the bounding box, in decimal degrees from 0 to ±180. (For overlays that overlap the meridian of 180° longitude, values can extend beyond that range.)
 * @param id
 * @param targetId
 * @param latLonAltBoxSimpleExtensionGroup
 * @param latLonAltBoxObjectExtensionGroup
 * @param latLonBoxTypeSimpleExtensionGroup
 * @param latLonBoxTypeObjectExtensionGroup
 * @param objectSimpleExtensionGroup
 */
case class LatLonAltBox(minAltitude: Option[Double] = None,
  maxAltitude: Option[Double] = None,
  altitudeMode: Option[AltitudeMode] = None,
  north: Option[Double] = None,
  south: Option[Double] = None,
  east: Option[Double] = None,
  west: Option[Double] = None,
  id: Option[String] = None,
  targetId: Option[String] = None,
  latLonAltBoxSimpleExtensionGroup: Seq[Any] = Nil,
  latLonAltBoxObjectExtensionGroup: Seq[Any] = Nil,
  latLonBoxTypeSimpleExtensionGroup: Seq[Any] = Nil,
  latLonBoxTypeObjectExtensionGroup: Seq[Any] = Nil,
  objectSimpleExtensionGroup: Seq[Any] = Nil) extends LatLonBoxType {

  def this(minAltitude: Double, maxAltitude: Double, altitudeMode: AltitudeMode,
           north: Double, south: Double, east: Double, west: Double) =
    this(Option(minAltitude), Option(maxAltitude), Option(altitudeMode),
      Option(north), Option(south), Option(east), Option(west))

  def this(minAltitude: Double, maxAltitude: Double, altitudeMode: AltitudeMode,
           north: Double, south: Double, east: Double, west: Double, id: String) =
    this(Option(minAltitude), Option(maxAltitude), Option(altitudeMode),
      Option(north), Option(south), Option(east), Option(west), Option(id))

}

/**
 * Lod is an abbreviation for Level of Detail.
 * <Lod> describes the size of the projected region on the screen that is required in order
 * for the region to be considered "active."
 * Also specifies the size of the pixel ramp used for fading in (from transparent to opaque)
 * and fading out (from opaque to transparent). See diagram below for a visual representation
 * of these parameters.
 *
 * @param minLodPixels Measurement in screen pixels that represents the minimum limit of the visibility range for a given Region. Google Earth calculates the size of the Region when projected onto screen space. Then it computes the square root of the Region's area (if, for example, the Region is square and the viewpoint is directly above the Region, and the Region is not tilted, this measurement is equal to the width of the projected Region). If this measurement falls within the limits defined by <minLodPixels> and <maxLodPixels> (and if the <LatLonAltBox> is in view), the Region is active. If this limit is not reached, the associated geometry is considered to be too far from the user's viewpoint to be drawn.
 * @param maxLodPixels Measurement in screen pixels that represents the maximum limit of the visibility range for a given Region. A value of −1, the default, indicates "active to infinite size."
 * @param minFadeExtent Distance over which the geometry fades, from fully opaque to fully transparent. This ramp value, expressed in screen pixels, is applied at the minimum end of the LOD (visibility) limits.
 * @param maxFadeExtent Distance over which the geometry fades, from fully transparent to fully opaque. This ramp value, expressed in screen pixels, is applied at the maximum end of the LOD (visibility) limits.
 * @param id
 * @param targetId
 * @param lodSimpleExtensionGroup
 * @param lodObjectExtensionGroup
 * @param objectSimpleExtensionGroup
 */
case class Lod(minLodPixels: Option[Double] = None,
  maxLodPixels: Option[Double] = None,
  minFadeExtent: Option[Double] = None,
  maxFadeExtent: Option[Double] = None,
  id: Option[String] = None,
  targetId: Option[String] = None,
  lodSimpleExtensionGroup: Seq[Any] = Nil,
  lodObjectExtensionGroup: Seq[Any] = Nil,
  objectSimpleExtensionGroup: Seq[Any] = Nil) extends KmlObject {

  def this(minLodPixels: Double, maxLodPixels: Double) =
    this(Option(minLodPixels), Option(maxLodPixels))

  def this(minLodPixels: Double, maxLodPixels: Double, minFadeExtent: Double, maxFadeExtent: Double) =
    this(Option(minLodPixels), Option(maxLodPixels), Option(minFadeExtent), Option(maxFadeExtent))

  def this(minLodPixels: Double, maxLodPixels: Double, minFadeExtent: Double, maxFadeExtent: Double, id: String) =
    this(Option(minLodPixels), Option(maxLodPixels), Option(minFadeExtent), Option(maxFadeExtent), Option(id))

}

/**
 * References a KML file or KMZ archive on a local or remote network. Use the <Link> element to specify the location of the KML file. Within that element, you can define the refresh options for updating the file, based on time and camera change. NetworkLinks can be used in combination with Regions to handle very large datasets efficiently.
 *
 * @param featurePart the Feature part of this object
 * @param refreshVisibility Boolean value. A value of 0 leaves the visibility of features within the control of the Google Earth user. Set the value to 1 to reset the visibility of features each time the NetworkLink is refreshed. For example, suppose a Placemark within the linked KML file has <visibility> set to 1 and the NetworkLink has <refreshVisibility> set to 1. When the file is first loaded into Google Earth, the user can clear the check box next to the item to turn off display in the 3D viewer. However, when the NetworkLink is refreshed, the Placemark will be made visible again, since its original visibility state was TRUE.
 * @param flyToView Boolean value. A value of 1 causes Google Earth to fly to the view of the LookAt or Camera in the NetworkLinkControl (if it exists). If the NetworkLinkControl does not contain an AbstractView element, Google Earth flies to the LookAt or Camera element in the Feature child within the <kml> element in the refreshed file. If the <kml> element does not have a LookAt or Camera specified, the view is unchanged. For example, Google Earth would fly to the <LookAt> view of the parent Document, not the <LookAt> of the Placemarks contained within the Document.
 * @param link Spcifies the location of the KML resource fetched by NetworkLink.
 * @param id
 * @param targetId
 * @param networkLinkSimpleExtensionGroup
 * @param networkLinkObjectExtensionGroup
 * @param objectSimpleExtensionGroup
 */
case class NetworkLink(
  featurePart: FeaturePart = new FeaturePart(),
  refreshVisibility: Option[Boolean] = None,
  flyToView: Option[Boolean] = None,
  link: Option[Link] = None,
  id: Option[String] = None,
  targetId: Option[String] = None,
  networkLinkSimpleExtensionGroup: Seq[Any] = Nil,
  networkLinkObjectExtensionGroup: Seq[Any] = Nil,
  objectSimpleExtensionGroup: Seq[Any] = Nil) extends Feature {

  def this(link: Link) =
    this(new FeaturePart(), None, None, Option(link))

  def this(name: String, link: Link) =
    this(new FeaturePart(name = Option(name)), None, None, Option(link))

  def this(name: String, refreshVisibility: Boolean, link: Link) =
    this(new FeaturePart(name = Option(name)), Option(refreshVisibility), None, Option(link))

  def this(name: String, refreshVisibility: Boolean, flyToView: Boolean, link: Link) =
    this(new FeaturePart(name = Option(name)), Option(refreshVisibility), Option(flyToView), Option(link))

  def this(featurePart: FeaturePart, refreshVisibility: Boolean, flyToView: Boolean, link: Link, id: String) =
    this(featurePart, Option(refreshVisibility), Option(flyToView), Option(link), Option(id))

}

/**
 * specifies the location of any of the following:
 * <ul>
 * <li>KML files fetched by network links
 * <li>Image files used in any Overlay (the <Icon> element specifies the image in an Overlay; <Icon> has the same fields as <Link>)
 * <li>Model files used in the <Model> element
 * <li>The file is conditionally loaded and refreshed, depending on the refresh parameters supplied here. Two different sets of refresh parameters can be specified: one set is based on time (<refreshMode> and <refreshInterval>) and one is based on the current "camera" view (<viewRefreshMode> and <viewRefreshTime>). In addition, Link specifies whether to scale the bounding box parameters that are sent to the server (<viewBoundScale> and provides a set of optional viewing parameters that can be sent to the server (<viewFormat>) as well as a set of optional parameters containing version and language information.
 * </ul>
 * When a file is fetched, the URL that is sent to the server is composed of three pieces of information:
 * <ul>
 * <li>the href (Hypertext Reference) that specifies the file to load.
 * <li>an arbitrary format string that is created from (a) parameters that you specify in the <viewFormat> element or (b) bounding box parameters (this is the default and is used if no <viewFormat> element is included in the file).
 * <li>a second format string that is specified in the <httpQuery> element.
 * <li>If the file specified in <href> is a local file, the <viewFormat> and <httpQuery> elements are not used.
 * </ul>
 * The <Link> element replaces the <Url> element of <NetworkLink> contained in earlier KML releases and adds functionality for the <Region> element (introduced in KML 2.1). In Google Earth releases 3.0 and earlier, the <Link> element is ignored.
 *
 * @param href A URL (either an HTTP address or a local file specification). When the parent of <Link> is a NetworkLink, <href> is a KML file. When the parent of <Link> is a Model, <href> is a COLLADA file. When the parent of <Icon> (same fields as <Link>) is an Overlay, <href> is an image. Relative URLs can be used in this tag and are evaluated relative to the enclosing KML file. See KMZ Files for details on constructing relative references in KML and KMZ files.
 * @param refreshMode Specifies a time-based refresh mode, which can be one of the following:
onChange - refresh when the file is loaded and whenever the Link parameters change (the default).
onInterval - refresh every n seconds (specified in <refreshInterval>).
onExpire - refresh the file when the expiration time is reached. If a fetched file has a NetworkLinkControl, the <expires> time takes precedence over expiration times specified in HTTP headers. If no <expires> time is specified, the HTTP max-age header is used (if present). If max-age is not present, the Expires HTTP header is used (if present). (See Section RFC261b of the Hypertext Transfer Protocol - HTTP 1.1 for details on HTTP header fields.)
 * @param refreshInterval Indicates to refresh the file every n seconds.
 * @param viewRefreshMode Specifies how the link is refreshed when the "camera" changes.
Can be one of the following:
never (default) - Ignore changes in the view. Also ignore <viewFormat> parameters, if any.
onStop - Refresh the file n seconds after movement stops, where n is specified in <viewRefreshTime>.
onRequest - Refresh the file only when the user explicitly requests it. (For example, in Google Earth, the user right-clicks and selects Refresh in the Context menu.)
onRegion - Refresh the file when the Region becomes active. See <Region>.
 * @param viewRefreshTime After camera movement stops, specifies the number of seconds to wait before refreshing the view. (See <viewRefreshMode> and onStop above.)
 * @param viewBoundScale Scales the BBOX parameters before sending them to the server. A value less than 1 specifies to use less than the full view (screen). A value greater than 1 specifies to fetch an area that extends beyond the edges of the current view.
 * @param viewFormat Specifies the format of the query string that is appended to the Link's <href> before the file is fetched.(If the <href> specifies a local file, this element is ignored.)
If you specify a <viewRefreshMode> of onStop and do not include the <viewFormat> tag in the file, the following information is automatically appended to the query string:
BBOX=[bboxWest],[bboxSouth],[bboxEast],[bboxNorth]

This information matches the Web Map Service (WMS) bounding box specification.
If you specify an empty <viewFormat> tag, no information is appended to the query string.
You can also specify a custom set of viewing parameters to add to the query string. If you supply a format string, it is used instead of the BBOX information. If you also want the BBOX information, you need to add those parameters along with the custom parameters.
You can use any of the following parameters in your format string (and Google Earth will substitute the appropriate current value at the time it creates the query string):
[lookatLon], [lookatLat] - longitude and latitude of the point that <LookAt> is viewing
[lookatRange], [lookatTilt], [lookatHeading] - values used by the <LookAt> element (see descriptions of <range>, <tilt>, and <heading> in <LookAt>)
[lookatTerrainLon], [lookatTerrainLat], [lookatTerrainAlt] - point on the terrain in degrees/meters that <LookAt> is viewing
[cameraLon], [cameraLat], [cameraAlt] - degrees/meters of the eyepoint for the camera
[horizFov], [vertFov] - horizontal, vertical field of view for the camera
[horizPixels], [vertPixels] - size in pixels of the 3D viewer
[terrainEnabled] - indicates whether the 3D viewer is showing terrain
 * @param httpQuery Appends information to the query string, based on the parameters specified. (Google Earth substitutes the appropriate current value at the time it creates the query string.) The following parameters are supported:
[clientVersion]
[kmlVersion]
[clientName]
[language]
 * @param id
 * @param targetId
 * @param linkSimpleExtensionGroup
 * @param linkObjectExtensionGroup
 * @param basicLinkSimpleExtensionGroup
 * @param basicLinkObjectExtensionGroup
 * @param objectSimpleExtensionGroup
 */
case class Icon(href: Option[String] = None,
                refreshMode: Option[RefreshMode] = None,
                refreshInterval: Option[Double] = None,
                viewRefreshMode: Option[ViewRefreshMode] = None,
                viewRefreshTime: Option[Double] = None,
                viewBoundScale: Option[Double] = None,
                viewFormat: Option[String] = None,
                httpQuery: Option[String] = None,
                id: Option[String] = None,
                targetId: Option[String] = None,
                linkSimpleExtensionGroup: Seq[Any] = Nil,
                linkObjectExtensionGroup: Seq[Any] = Nil,
                basicLinkSimpleExtensionGroup: Seq[Any] = Nil,
                basicLinkObjectExtensionGroup: Seq[Any] = Nil,
                objectSimpleExtensionGroup: Seq[Any] = Nil) extends BasicLinkType {

  def this(href: String) = this(Option(href))
  def this(href: String, refreshMode: RefreshMode) = this(Option(href), Option(refreshMode))
  def this(href: String, refreshMode: RefreshMode, refreshInterval: Double) = this(Option(href), Option(refreshMode), Option(refreshInterval))

  def this(href: String, refreshMode: RefreshMode, refreshInterval: Double, viewRefreshMode: ViewRefreshMode, viewFreshTime: Double) =
    this(Option(href), Option(refreshMode), Option(refreshInterval), Option(viewRefreshMode), Option(viewFreshTime))

  def this(href: String, refreshMode: RefreshMode, refreshInterval: Double, viewRefreshMode: ViewRefreshMode, viewFreshTime: Double,
           viewBoundScale: Double, viewFormat: String, httpQuery: String, id: String) =
    this(Option(href), Option(refreshMode), Option(refreshInterval), Option(viewRefreshMode), Option(viewFreshTime),
      Option(viewBoundScale), Option(viewFormat), Option(httpQuery), Option(id))

}

/**
 * specifies the location of any of the following:
 * <ul>
 * <li>KML files fetched by network links
 * <li>Image files used in any Overlay (the <Icon> element specifies the image in an Overlay; <Icon> has the same fields as <Link>)
 * <li>Model files used in the <Model> element
 * <li>The file is conditionally loaded and refreshed, depending on the refresh parameters supplied here. Two different sets of refresh parameters can be specified: one set is based on time (<refreshMode> and <refreshInterval>) and one is based on the current "camera" view (<viewRefreshMode> and <viewRefreshTime>). In addition, Link specifies whether to scale the bounding box parameters that are sent to the server (<viewBoundScale> and provides a set of optional viewing parameters that can be sent to the server (<viewFormat>) as well as a set of optional parameters containing version and language information.
 * </ul>
 * When a file is fetched, the URL that is sent to the server is composed of three pieces of information:
 * <ul>
 * <li>the href (Hypertext Reference) that specifies the file to load.
 * <li>an arbitrary format string that is created from (a) parameters that you specify in the <viewFormat> element or (b) bounding box parameters (this is the default and is used if no <viewFormat> element is included in the file).
 * <li>a second format string that is specified in the <httpQuery> element.
 * <li>If the file specified in <href> is a local file, the <viewFormat> and <httpQuery> elements are not used.
 * </ul>
 * The <Link> element replaces the <Url> element of <NetworkLink> contained in earlier KML releases and adds functionality for the <Region> element (introduced in KML 2.1). In Google Earth releases 3.0 and earlier, the <Link> element is ignored.
 *
 * @param href A URL (either an HTTP address or a local file specification). When the parent of <Link> is a NetworkLink, <href> is a KML file. When the parent of <Link> is a Model, <href> is a COLLADA file. When the parent of <Icon> (same fields as <Link>) is an Overlay, <href> is an image. Relative URLs can be used in this tag and are evaluated relative to the enclosing KML file. See KMZ Files for details on constructing relative references in KML and KMZ files.
 * @param refreshMode Specifies a time-based refresh mode, which can be one of the following:
onChange - refresh when the file is loaded and whenever the Link parameters change (the default).
onInterval - refresh every n seconds (specified in <refreshInterval>).
onExpire - refresh the file when the expiration time is reached. If a fetched file has a NetworkLinkControl, the <expires> time takes precedence over expiration times specified in HTTP headers. If no <expires> time is specified, the HTTP max-age header is used (if present). If max-age is not present, the Expires HTTP header is used (if present). (See Section RFC261b of the Hypertext Transfer Protocol - HTTP 1.1 for details on HTTP header fields.)
 * @param refreshInterval Indicates to refresh the file every n seconds.
 * @param viewRefreshMode Specifies how the link is refreshed when the "camera" changes.
Can be one of the following:
never (default) - Ignore changes in the view. Also ignore <viewFormat> parameters, if any.
onStop - Refresh the file n seconds after movement stops, where n is specified in <viewRefreshTime>.
onRequest - Refresh the file only when the user explicitly requests it. (For example, in Google Earth, the user right-clicks and selects Refresh in the Context menu.)
onRegion - Refresh the file when the Region becomes active. See <Region>.
 * @param viewRefreshTime After camera movement stops, specifies the number of seconds to wait before refreshing the view. (See <viewRefreshMode> and onStop above.)
 * @param viewBoundScale Scales the BBOX parameters before sending them to the server. A value less than 1 specifies to use less than the full view (screen). A value greater than 1 specifies to fetch an area that extends beyond the edges of the current view.
 * @param viewFormat Specifies the format of the query string that is appended to the Link's <href> before the file is fetched.(If the <href> specifies a local file, this element is ignored.)
If you specify a <viewRefreshMode> of onStop and do not include the <viewFormat> tag in the file, the following information is automatically appended to the query string:
BBOX=[bboxWest],[bboxSouth],[bboxEast],[bboxNorth]

This information matches the Web Map Service (WMS) bounding box specification.
If you specify an empty <viewFormat> tag, no information is appended to the query string.
You can also specify a custom set of viewing parameters to add to the query string. If you supply a format string, it is used instead of the BBOX information. If you also want the BBOX information, you need to add those parameters along with the custom parameters.
You can use any of the following parameters in your format string (and Google Earth will substitute the appropriate current value at the time it creates the query string):
[lookatLon], [lookatLat] - longitude and latitude of the point that <LookAt> is viewing
[lookatRange], [lookatTilt], [lookatHeading] - values used by the <LookAt> element (see descriptions of <range>, <tilt>, and <heading> in <LookAt>)
[lookatTerrainLon], [lookatTerrainLat], [lookatTerrainAlt] - point on the terrain in degrees/meters that <LookAt> is viewing
[cameraLon], [cameraLat], [cameraAlt] - degrees/meters of the eyepoint for the camera
[horizFov], [vertFov] - horizontal, vertical field of view for the camera
[horizPixels], [vertPixels] - size in pixels of the 3D viewer
[terrainEnabled] - indicates whether the 3D viewer is showing terrain
 * @param httpQuery Appends information to the query string, based on the parameters specified. (Google Earth substitutes the appropriate current value at the time it creates the query string.) The following parameters are supported:
[clientVersion]
[kmlVersion]
[clientName]
[language]
 * @param id
 * @param targetId
 * @param linkSimpleExtensionGroup
 * @param linkObjectExtensionGroup
 * @param basicLinkSimpleExtensionGroup
 * @param basicLinkObjectExtensionGroup
 * @param objectSimpleExtensionGroup
 */
case class Link(href: Option[String] = None,
                refreshMode: Option[RefreshMode] = None,
                refreshInterval: Option[Double] = None,
                viewRefreshMode: Option[ViewRefreshMode] = None,
                viewRefreshTime: Option[Double] = None,
                viewBoundScale: Option[Double] = None,
                viewFormat: Option[String] = None,
                httpQuery: Option[String] = None,
                id: Option[String] = None,
                targetId: Option[String] = None,
                linkSimpleExtensionGroup: Seq[Any] = Nil,
                linkObjectExtensionGroup: Seq[Any] = Nil,
                basicLinkSimpleExtensionGroup: Seq[Any] = Nil,
                basicLinkObjectExtensionGroup: Seq[Any] = Nil,
                objectSimpleExtensionGroup: Seq[Any] = Nil) extends BasicLinkType {

  def this(href: String) = this(Option(href))
  def this(href: String, refreshMode: RefreshMode) = this(Option(href), Option(refreshMode))
  def this(href: String, refreshMode: RefreshMode, refreshInterval: Double) = this(Option(href), Option(refreshMode), Option(refreshInterval))

  def this(href: String, refreshMode: RefreshMode, refreshInterval: Double, viewRefreshMode: ViewRefreshMode, viewFreshTime: Double) =
    this(Option(href), Option(refreshMode), Option(refreshInterval), Option(viewRefreshMode), Option(viewFreshTime))

  def this(href: String, refreshMode: RefreshMode, refreshInterval: Double, viewRefreshMode: ViewRefreshMode, viewFreshTime: Double,
           viewBoundScale: Double, viewFormat: String, httpQuery: String, id: String) =
    this(Option(href), Option(refreshMode), Option(refreshInterval), Option(viewRefreshMode), Option(viewFreshTime),
      Option(viewBoundScale), Option(viewFormat), Option(httpQuery), Option(id))

}

/**
 * A container for zero or more geometry primitives associated with the same feature.
 *
 * @param geometries a set of Geometry
 * @param id
 * @param targetId
 * @param multiGeometrySimpleExtensionGroup
 * @param multiGeometryObjectExtensionGroup
 * @param geometrySimpleExtensionGroup
 * @param geometryObjectExtensionGroup
 * @param objectSimpleExtensionGroup
 */
case class MultiGeometry(geometries: Seq[Geometry] = Nil,
  id: Option[String] = None,
  targetId: Option[String] = None,
  multiGeometrySimpleExtensionGroup: Seq[Any] = Nil,
  multiGeometryObjectExtensionGroup: Seq[Any] = Nil,
  geometrySimpleExtensionGroup: Seq[Any] = Nil,
  geometryObjectExtensionGroup: Seq[Any] = Nil,
  objectSimpleExtensionGroup: Seq[Any] = Nil) extends Geometry {

  def this(geom: Geometry) = this(Seq.empty :+ geom)

  /**
   * returns a new object with value added to the sequence
   * @param value to add
   * @return a new object with value added to the sequence
   */
  def addToGeometries(value: Geometry) = {
    this.copy(geometries = if (geometries == Nil) (Seq.empty :+ value) else (geometries :+ value))
  }
}

/**
 * A geographic location defined by longitude, latitude, and (optional) altitude. When a Point is contained by a Placemark, the point itself determines the position of the Placemark's name and icon. When a Point is extruded, it is connected to the ground with a line. This "tether" uses the current LineStyle.
 *
 * @param extrude Boolean value. Specifies whether to connect the point to the ground with a line. To extrude a Point, the value for <altitudeMode> must be either relativeToGround, relativeToSeaFloor, or absolute. The point is extruded toward the center of the Earth's sphere.
 * @param altitudeMode Specifies how altitude components in the <coordinates> element are interpreted. Possible values are
clampToGround - (default) Indicates to ignore an altitude specification (for example, in the <coordinates> tag).
relativeToGround - Sets the altitude of the element relative to the actual ground elevation of a particular location. For example, if the ground elevation of a location is exactly at sea level and the altitude for a point is set to 9 meters, then the elevation for the icon of a point placemark elevation is 9 meters with this mode. However, if the same coordinate is set over a location where the ground elevation is 10 meters above sea level, then the elevation of the coordinate is 19 meters. A typical use of this mode is for placing telephone poles or a ski lift.
absolute - Sets the altitude of the coordinate relative to sea level, regardless of the actual elevation of the terrain beneath the element. For example, if you set the altitude of a coordinate to 10 meters with an absolute altitude mode, the icon of a point placemark will appear to be at ground level if the terrain beneath is also 10 meters above sea level. If the terrain is 3 meters above sea level, the placemark will appear elevated above the terrain by 7 meters. A typical use of this mode is for aircraft placement.
 * @param coordinates A single tuple consisting of floating point values for longitude, latitude, and altitude (in that order). Longitude and latitude values are in degrees, where
longitude ≥ −180 and <= 180
latitude ≥ −90 and ≤ 90
altitude values (optional) are in meters above sea level
Do not include spaces between the three values that describe a coordinate
 * @param id
 * @param targetId
 * @param pointSimpleExtensionGroup
 * @param pointObjectExtensionGroup
 * @param geometrySimpleExtensionGroup
 * @param geometryObjectExtensionGroup
 * @param objectSimpleExtensionGroup
 */
case class Point(extrude: Option[Boolean] = None,
  altitudeMode: Option[AltitudeMode] = None,
  coordinates: Option[Seq[Location]] = None,
  id: Option[String] = None,
  targetId: Option[String] = None,
  pointSimpleExtensionGroup: Seq[Any] = Nil,
  pointObjectExtensionGroup: Seq[Any] = Nil,
  geometrySimpleExtensionGroup: Seq[Any] = Nil,
  geometryObjectExtensionGroup: Seq[Any] = Nil,
  objectSimpleExtensionGroup: Seq[Any] = Nil) extends Geometry {

  def this(lon: Double, lat: Double) = this(None, None, Option(Seq.empty :+ new Location(lon, lat)))
  def this(lon: Double, lat: Double, alt: Double) = this(None, None, Option(Seq.empty :+ new Location(lon, lat, alt)))
  def this(location: Location) = this(None, None, Option(Seq.empty :+ location))
  def this(altMode: AltitudeMode, lon: Double, lat: Double, alt: Double) = this(None, Option(altMode), Option(Seq.empty :+ new Location(lon, lat, alt)))
  def this(coordinates: Seq[Location]) = this(None, None, Option(coordinates))
  def this(altMode: AltitudeMode, locations: Seq[Location]) = this(None,  Option(altMode), Option(locations))
  def this(extrude: Boolean, altMode: AltitudeMode, coordinates: Seq[Location]) = this(Option(extrude),  Option(altMode), Option(coordinates))

  /**
   * returns a new object with a new Location added to the sequence of coordinates
   * @param value the new Location to add
   * @return a new object with a new Location added to the sequence of coordinates
   */
  def addToCoordinates(value: Location) = {
    this.copy(coordinates =
      coordinates match {
        case Some(x) => if (x == Nil) Option(Seq.empty :+ value) else Option(x :+ value)
        case None => Option(Seq.empty :+ value)
      })
  }

}

/**
 * Defines a connected set of line segments. Use <LineStyle> to specify the color, color mode, and width of the line. When a LineString is extruded, the line is extended to the ground, forming a polygon that looks somewhat like a wall or fence. For extruded LineStrings, the line itself uses the current LineStyle, and the extrusion uses the current PolyStyle. See the KML Tutorial for examples of LineStrings (or paths).
 *
 * @param extrude Boolean value. Specifies whether to connect the LineString to the ground. To extrude a LineString, the altitude mode must be either relativeToGround, relativeToSeaFloor, or absolute. The vertices in the LineString are extruded toward the center of the Earth's sphere.
 * @param tessellate Boolean value. Specifies whether to allow the LineString to follow the terrain. To enable tessellation, the altitude mode must be clampToGround or clampToSeaFloor. Very large LineStrings should enable tessellation so that they follow the curvature of the earth (otherwise, they may go underground and be hidden).
 * @param altitudeMode Specifies how altitude components in the <coordinates> element are interpreted. Possible values are
clampToGround - (default) Indicates to ignore an altitude specification (for example, in the <coordinates> tag).
relativeToGround - Sets the altitude of the element relative to the actual ground elevation of a particular location. For example, if the ground elevation of a location is exactly at sea level and the altitude for a point is set to 9 meters, then the elevation for the icon of a point placemark elevation is 9 meters with this mode. However, if the same coordinate is set over a location where the ground elevation is 10 meters above sea level, then the elevation of the coordinate is 19 meters. A typical use of this mode is for placing telephone poles or a ski lift.
absolute - Sets the altitude of the coordinate relative to sea level, regardless of the actual elevation of the terrain beneath the element. For example, if you set the altitude of a coordinate to 10 meters with an absolute altitude mode, the icon of a point placemark will appear to be at ground level if the terrain beneath is also 10 meters above sea level. If the terrain is 3 meters above sea level, the placemark will appear elevated above the terrain by 7 meters. A typical use of this mode is for aircraft placement.
 * @param coordinates Two or more coordinate tuples, each consisting of floating point values for longitude, latitude, and altitude. The altitude component is optional. Insert a space between tuples. Do not include spaces within a tuple.
 * @param id
 * @param targetId
 * @param lineStringSimpleExtensionGroup
 * @param lineStringObjectExtensionGroup
 * @param geometrySimpleExtensionGroup
 * @param geometryObjectExtensionGroup
 * @param objectSimpleExtensionGroup
 */
case class LineString(extrude: Option[Boolean] = None,
  tessellate: Option[Boolean] = None,
  altitudeMode: Option[AltitudeMode] = None,
  coordinates: Option[Seq[Location]] = None,
  id: Option[String] = None,
  targetId: Option[String] = None,
  lineStringSimpleExtensionGroup: Seq[Any] = Nil,
  lineStringObjectExtensionGroup: Seq[Any] = Nil,
  geometrySimpleExtensionGroup: Seq[Any] = Nil,
  geometryObjectExtensionGroup: Seq[Any] = Nil,
  objectSimpleExtensionGroup: Seq[Any] = Nil) extends Geometry {

  def this(coordinates: Seq[Location]) = this(None, None, None, Option(coordinates))
  def this(altMode: AltitudeMode, coordinates: Seq[Location]) = this(None, None, Option(altMode), Option(coordinates))
  def this(extrude: Boolean, altMode: AltitudeMode, locations: Seq[Location]) = this(Option(extrude),  None, Option(altMode), Option(locations))

  def this(extrude: Boolean, tessellate: Boolean, altitudeMode: AltitudeMode, coordinates: Seq[Location]) =
    this(Option(extrude), Option(tessellate), Option(altitudeMode), Option(coordinates))

  def this(extrude: Boolean, tessellate: Boolean, altitudeMode: AltitudeMode, coordinates: Seq[Location], id: String) =
    this(Option(extrude), Option(tessellate), Option(altitudeMode), Option(coordinates), Option(id))

  /**
   * returns a new object with a new Location added to the sequence of coordinates
   * @param value the new Location to add
   * @return a new object with a new Location added to the sequence of coordinates
   */
  def addToCoordinates(value: Location) = {
    this.copy(coordinates =
      coordinates match {
        case Some(x) => if (x == Nil) Option(Seq.empty :+ value) else Option(x :+ value)
        case None => Option(Seq.empty :+ value)
      })
  }
}

/**
 * Defines a closed line string, typically the outer boundary of a Polygon. Optionally, a LinearRing can also be used as the inner boundary of a Polygon to create holes in the Polygon. A Polygon can contain multiple <LinearRing> elements used as inner boundaries.
 *
 * @param extrude Boolean value. Specifies whether to connect the LinearRing to the ground. To extrude this geometry, the altitude mode must be either relativeToGround, relativeToSeaFloor, or absolute. Only the vertices of the LinearRing are extruded, not the center of the geometry. The vertices are extruded toward the center of the Earth's sphere.
 * @param tessellate Boolean value. Specifies whether to allow the LinearRing to follow the terrain. To enable tessellation, the value for <altitudeMode> must be clampToGround or clampToSeaFloor. Very large LinearRings should enable tessellation so that they follow the curvature of the earth (otherwise, they may go underground and be hidden).
 * @param altitudeMode Specifies how altitude components in the <coordinates> element are interpreted. Possible values are
clampToGround - (default) Indicates to ignore an altitude specification (for example, in the <coordinates> tag).
relativeToGround - Sets the altitude of the element relative to the actual ground elevation of a particular location. For example, if the ground elevation of a location is exactly at sea level and the altitude for a point is set to 9 meters, then the elevation for the icon of a point placemark elevation is 9 meters with this mode. However, if the same coordinate is set over a location where the ground elevation is 10 meters above sea level, then the elevation of the coordinate is 19 meters. A typical use of this mode is for placing telephone poles or a ski lift.
absolute - Sets the altitude of the coordinate relative to sea level, regardless of the actual elevation of the terrain beneath the element. For example, if you set the altitude of a coordinate to 10 meters with an absolute altitude mode, the icon of a point placemark will appear to be at ground level if the terrain beneath is also 10 meters above sea level. If the terrain is 3 meters above sea level, the placemark will appear elevated above the terrain by 7 meters. A typical use of this mode is for aircraft placement.
 * @param coordinates Four or more tuples, each consisting of floating point values for longitude, latitude, and altitude. The altitude component is optional. Do not include spaces within a tuple. The last coordinate must be the same as the first coordinate. Coordinates are expressed in decimal degrees only.
 * @param id
 * @param targetId
 * @param linearRingSimpleExtensionGroup
 * @param linearRingObjectExtensionGroup
 * @param geometrySimpleExtensionGroup
 * @param geometryObjectExtensionGroup
 * @param objectSimpleExtensionGroup
 */
case class LinearRing(extrude: Option[Boolean] = None,
  tessellate: Option[Boolean] = None,
  altitudeMode: Option[AltitudeMode] = None,
  coordinates: Option[Seq[Location]] = None,
  id: Option[String] = None,
  targetId: Option[String] = None,
  linearRingSimpleExtensionGroup: Seq[Any] = Nil,
  linearRingObjectExtensionGroup: Seq[Any] = Nil,
  geometrySimpleExtensionGroup: Seq[Any] = Nil,
  geometryObjectExtensionGroup: Seq[Any] = Nil,
  objectSimpleExtensionGroup: Seq[Any] = Nil) extends Geometry {

  def this(coordinates: Seq[Location]) = this(None, None, None, Option(coordinates))
  def this(altMode: AltitudeMode, coordinates: Seq[Location]) = this(None, None, Option(altMode), Option(coordinates))
  def this(extrude: Boolean, altMode: AltitudeMode, locations: Seq[Location]) = this(Option(extrude),  None, Option(altMode), Option(locations))

  def this(extrude: Boolean, tessellate: Boolean, altitudeMode: AltitudeMode, coordinates: Seq[Location]) =
    this(Option(extrude), Option(tessellate), Option(altitudeMode), Option(coordinates))

  def this(extrude: Boolean, tessellate: Boolean, altitudeMode: AltitudeMode, coordinates: Seq[Location], id: String) =
    this(Option(extrude), Option(tessellate), Option(altitudeMode), Option(coordinates), Option(id))

  /**
   * returns a new object with a new Location added to the sequence of coordinates
   * @param value the new Location to add
   * @return a new object with a new Location added to the sequence of coordinates
   */
  def addToCoordinates(value: Location) = {
    this.copy(coordinates =
      coordinates match {
        case Some(x) => if (x == Nil) Option(Seq.empty :+ value) else Option(x :+ value)
        case None => Option(Seq.empty :+ value)
      })
  }
}

/**
 * A Polygon is defined by an outer boundary and 0 or more inner boundaries. The boundaries, in turn, are defined by LinearRings. When a Polygon is extruded, its boundaries are connected to the ground to form additional polygons, which gives the appearance of a building or a box. Extruded Polygons use <PolyStyle> for their color, color mode, and fill.

The <coordinates> for polygons must be specified in counterclockwise order. Polygons follow the "right-hand rule," which states that if you place the fingers of your right hand in the direction in which the coordinates are specified, your thumb points in the general direction of the geometric normal for the polygon. (In 3D graphics, the geometric normal is used for lighting and points away from the front face of the polygon.) Since Google Earth fills only the front face of polygons, you will achieve the desired effect only when the coordinates are specified in the proper order. Otherwise, the polygon will be gray.
 *
 * @param extrude Boolean value. Specifies whether to connect the Polygon to the ground. To extrude a Polygon, the altitude mode must be either relativeToGround, relativeToSeaFloor, or absolute. Only the vertices are extruded, not the geometry itself (for example, a rectangle turns into a box with five faces. The vertices of the Polygon are extruded toward the center of the Earth's sphere.
 * @param tessellate This field is not used by Polygon. To allow a Polygon to follow the terrain (that is, to enable tessellation) specify an altitude mode of clampToGround or clampToSeaFloor.
 * @param altitudeMode Specifies how altitude components in the <coordinates> element are interpreted. Possible values are
clampToGround - (default) Indicates to ignore an altitude specification (for example, in the <coordinates> tag).
relativeToGround - Sets the altitude of the element relative to the actual ground elevation of a particular location. For example, if the ground elevation of a location is exactly at sea level and the altitude for a point is set to 9 meters, then the elevation for the icon of a point placemark elevation is 9 meters with this mode. However, if the same coordinate is set over a location where the ground elevation is 10 meters above sea level, then the elevation of the coordinate is 19 meters. A typical use of this mode is for placing telephone poles or a ski lift.
absolute - Sets the altitude of the coordinate relative to sea level, regardless of the actual elevation of the terrain beneath the element. For example, if you set the altitude of a coordinate to 10 meters with an absolute altitude mode, the icon of a point placemark will appear to be at ground level if the terrain beneath is also 10 meters above sea level. If the terrain is 3 meters above sea level, the placemark will appear elevated above the terrain by 7 meters. A typical use of this mode is for aircraft placement.
 * @param outerBoundaryIs Contains a <LinearRing> element.
 * @param innerBoundaryIs Contains a <LinearRing> element. A Polygon can contain multiple <innerBoundaryIs> elements, which create multiple cut-outs inside the Polygon.
 * @param id
 * @param targetId
 * @param polygonSimpleExtensionGroup
 * @param polygonObjectExtensionGroup
 * @param geometrySimpleExtensionGroup
 * @param geometryObjectExtensionGroup
 * @param objectSimpleExtensionGroup
 */
case class Polygon(extrude: Option[Boolean] = None,
  tessellate: Option[Boolean] = None,
  altitudeMode: Option[AltitudeMode] = None,
  outerBoundaryIs: Option[Boundary] = None,
  innerBoundaryIs: Seq[Boundary] = Nil,
  id: Option[String] = None,
  targetId: Option[String] = None,
  polygonSimpleExtensionGroup: Seq[Any] = Nil,
  polygonObjectExtensionGroup: Seq[Any] = Nil,
  geometrySimpleExtensionGroup: Seq[Any] = Nil,
  geometryObjectExtensionGroup: Seq[Any] = Nil,
  objectSimpleExtensionGroup: Seq[Any] = Nil) extends Geometry {

  def this(outerBoundaryIs: Boundary) = this(None, None, None, Option(outerBoundaryIs))
  def this(outerBoundaryIs: Boundary, innerBoundaryIs: Boundary) =
    this(None, None, None, Option(outerBoundaryIs), (Seq.empty :+ innerBoundaryIs))

  def this(outerBoundaryIs: Boundary, innerBoundaryIsSet: Seq[Boundary]) =
    this(None, None, None, Option(outerBoundaryIs), innerBoundaryIsSet)

  def this(extrude: Boolean, tessellate: Boolean, altitudeMode: AltitudeMode, outerBoundaryIs: Boundary, innerBoundaryIsSet: Seq[Boundary]) =
    this(Option(extrude), Option(tessellate), Option(altitudeMode),  Option(outerBoundaryIs), innerBoundaryIsSet)

  def this(extrude: Boolean, tessellate: Boolean, altitudeMode: AltitudeMode,
           outerBoundaryIs: Boundary, innerBoundaryIsSet: Seq[Boundary], id: String) =
    this(Option(extrude), Option(tessellate), Option(altitudeMode),  Option(outerBoundaryIs), innerBoundaryIsSet, Option(id))

  /**
   * returns a new object with value added to the sequence
   * @param value to add
   * @return a new object with value added to the sequence
   */
  def addToInnerBoundaryIs(value: Boundary) = {
    this.copy(innerBoundaryIs = if (innerBoundaryIs == Nil) (Seq.empty :+ value) else (innerBoundaryIs :+ value))
  }
}

/**
 * Describes a boundary using a LinearRing
 *
 * @param linearRing the LinearRing
 * @param boundarySimpleExtensionGroup
 * @param boundaryObjectExtensionGroup
 */
case class Boundary(linearRing: Option[LinearRing] = None,
                    boundarySimpleExtensionGroup: Seq[Any] = Nil,
                    boundaryObjectExtensionGroup: Seq[Any] = Nil) {

  def this(linearRing: LinearRing) = this(Option(linearRing))

}

/**
 * A 3D object described in a COLLADA file (referenced in the <Link> tag). COLLADA files have a .dae file extension. Models are created in their own coordinate space and then located, positioned, and scaled in Google Earth. See the "Topics in KML" page on Models for more detail.

Google Earth supports the COLLADA common profile, with the following exceptions:

Google Earth supports only triangles and lines as primitive types. The maximum number of triangles allowed is 21845.
Google Earth does not support animation or skinning.
Google Earth does not support external geometry references.

 * @param altitudeMode Specifies how the <altitude> specified in <Location> is interpreted. Possible values are as follows:
clampToGround - (default) Indicates to ignore the <altitude> specification and place the Model on the ground.
relativeToGround - Interprets the <altitude> as a value in meters above the ground.
absolute - Interprets the <altitude> as a value in meters above sea level.
 * @param location Specifies the exact coordinates of the Model's origin in latitude, longitude, and altitude. Latitude and longitude measurements are standard lat-lon projection with WGS84 datum. Altitude is distance above the earth's surface, in meters, and is interpreted according to <altitudeMode> or <gx:altitudeMode>.
 * @param orientation Describes rotation of a 3D model's coordinate system to position the object in Google Earth.
 * @param scale Scales a model along the x, y, and z axes in the model's coordinate space.
 * @param link Specifies the file to load and optional refresh parameters. See <Link>.
 * @param resourceMap Specifies 0 or more <Alias> elements, each of which is a mapping for the texture file path from the original Collada file to the KML or KMZ file that contains the Model. This element allows you to move and rename texture files without having to update the original Collada file that references those textures. One <ResourceMap> element can contain multiple mappings from different (source) Collada files into the same (target) KMZ file.
 * @param id
 * @param targetId
 * @param modelSimpleExtensionGroup
 * @param modelObjectExtensionGroup
 * @param geometrySimpleExtensionGroup
 * @param geometryObjectExtensionGroup
 * @param objectSimpleExtensionGroup
 */
case class Model(altitudeMode: Option[AltitudeMode] = None,
  location: Option[Location] = None,
  orientation: Option[Orientation] = None,
  scale: Option[Scale] = None,
  link: Option[Link] = None,
  resourceMap: Option[ResourceMap] = None,
  id: Option[String] = None,
  targetId: Option[String] = None,
  modelSimpleExtensionGroup: Seq[Any] = Nil,
  modelObjectExtensionGroup: Seq[Any] = Nil,
  geometrySimpleExtensionGroup: Seq[Any] = Nil,
  geometryObjectExtensionGroup: Seq[Any] = Nil,
  objectSimpleExtensionGroup: Seq[Any] = Nil) extends Geometry {

  def this(altitudeMode: AltitudeMode, location: Location, orientation: Orientation, scale: Scale, link: Link) =
    this(Option(altitudeMode), Option(location), Option(orientation), Option(scale), Option(link))

  def this(altitudeMode: AltitudeMode, location: Location, orientation: Orientation,
           scale: Scale, link: Link, resourceMap: ResourceMap, id: String) =
    this(Option(altitudeMode), Option(location), Option(orientation), Option(scale), Option(link),
      Option(resourceMap), Option(id))

}

/**
 * Specifies the exact coordinates of the Model's origin in latitude, longitude, and altitude.
 * Latitude and longitude measurements are standard lat-lon projection with WGS84 datum.
 * Altitude is distance above the earth's surface, in meters, and is interpreted according
 * to <altitudeMode> or <gx:altitudeMode>.
 *
 */
object Location {

  private def fromStringArray(lonLatAlt: Array[String]) = {
    if (lonLatAlt.length < 2) None
    else {
      val lon = if (lonLatAlt.isDefinedAt(0)) getOptionDouble(lonLatAlt(0)) else None
      val lat = if (lonLatAlt.isDefinedAt(1)) getOptionDouble(lonLatAlt(1)) else None
      var alt = if (lonLatAlt.isDefinedAt(2)) getOptionDouble(lonLatAlt(2)) else None
      if (!lon.isDefined || !lat.isDefined) None
      else {
        if (!alt.isDefined) alt = Option(0.0)
        Option(Location(lon, lat, alt))
      }
    }
  }

  private def getOptionDouble(s: String): Option[Double] = {
    if (s == null) None
    else
    if (s.trim.isEmpty) None
    else try {
      Option(s.trim.toDouble)
    } catch {
      case _: Throwable => None
    }
  }

  /**
   * get a location from a comma separated string of lon, lat, alt (optional)
   * @param coordString a comma separated string
   * @return a Location with lon, lat, alt (optional)
   */
  def fromCsString(coordString: String) = fromStringArray(coordString split ",")
  /**
   * get a location from a blank separated string of lon, lat, alt (optional)
   * @param coordString a blank separated string
   * @return a Location with lon, lat, alt (optional)
   */
  def fromBsString(coordString: String) = fromStringArray(coordString split "s+")

//  def fromMGRS(coord: String) = {
//    // TODO
//  }
//
//  def fromUTM(coord: String) = {
//    // TODO
//  }
//
//  def fromECEF(coord: String) = {
//    // TODO
//  }

}

/**
 *
 * Specifies the exact coordinates of the Model's origin in latitude, longitude, and altitude.
 * Latitude and longitude measurements are standard lat-lon projection with WGS84 datum.
 * Altitude is distance above the earth's surface, in meters, and is interpreted according
 * to <altitudeMode> or <gx:altitudeMode>.
 *
 *
 * @param longitude in decimal degrees
 * @param latitude in decimal degrees
 * @param altitude is distance above the earth's surface, in meters, and is interpreted according to <altitudeMode> or <gx:altitudeMode>.
 * @param id
 * @param targetId
 * @param locationSimpleExtensionGroup
 * @param locationObjectExtensionGroup
 * @param objectSimpleExtensionGroup
 */
case class Location(longitude: Option[Double] = None,
  latitude: Option[Double] = None,
  altitude: Option[Double] = None,
  id: Option[String] = None,
  targetId: Option[String] = None,
  locationSimpleExtensionGroup: Seq[Any] = Nil,
  locationObjectExtensionGroup: Seq[Any] = Nil,
  objectSimpleExtensionGroup: Seq[Any] = Nil) extends KmlObject {

  def this(longitude: Double, latitude: Double, altitude: Double) =
    this(Option(longitude), Option(latitude), Option(altitude))

  def this(longitude: Double, latitude: Double) = this(Option(longitude), Option(latitude))

  def llaToString() = this.longitude.getOrElse("") + "," + this.latitude.getOrElse("") + "," + this.altitude.getOrElse("")

}

/**
 * Describes rotation of a 3D model's coordinate system to position the object in Google Earth. See diagrams below.
 *
 * @param heading Rotation about the z axis (normal to the Earth's surface). A value of 0 (the default) equals North. A positive rotation is clockwise around the z axis and specified in degrees from 0 to 360.
 * @param tilt Rotation about the x axis. A positive rotation is clockwise around the x axis and specified in degrees from 0 to 180.
 * @param roll Rotation about the y axis. A positive rotation is clockwise around the y axis and specified in degrees from 0 to 180.
 * @param id
 * @param targetId
 * @param orientationSimpleExtensionGroup
 * @param orientationObjectExtensionGroup
 * @param objectSimpleExtensionGroup
 */
case class Orientation(heading: Option[Double] = None,
  tilt: Option[Double] = None,
  roll: Option[Double] = None,
  id: Option[String] = None,
  targetId: Option[String] = None,
  orientationSimpleExtensionGroup: Seq[Any] = Nil,
  orientationObjectExtensionGroup: Seq[Any] = Nil,
  objectSimpleExtensionGroup: Seq[Any] = Nil) extends KmlObject {

  def this(heading: Double) = this(Option(heading))

  def this(heading: Double, tilt: Double, roll: Double) = this(Option(heading), Option(tilt), Option(roll))

}

/**
 * Scales a model along the x, y, and z axes in the model's coordinate space.
 *
 * @param x scale in the x axis
 * @param y scale in the y axis
 * @param z scale in the z axis
 * @param id
 * @param targetId
 * @param scaleSimpleExtensionGroup
 * @param scaleObjectExtensionGroup
 * @param objectSimpleExtensionGroup
 */
case class Scale(x: Option[Double] = None,
  y: Option[Double] = None,
  z: Option[Double] = None,
  id: Option[String] = None,
  targetId: Option[String] = None,
  scaleSimpleExtensionGroup: Seq[Any] = Nil,
  scaleObjectExtensionGroup: Seq[Any] = Nil,
  objectSimpleExtensionGroup: Seq[Any] = Nil) extends KmlObject {

  def this(x: Double, y: Double, z: Double) = this(Option(x), Option(y), Option(z))

}

/**
 * Specifies 0 or more <Alias> elements, each of which is a mapping for the texture file path from the original Collada file to the KML or KMZ file that contains the Model. This element allows you to move and rename texture files without having to update the original Collada file that references those textures. One <ResourceMap> element can contain multiple mappings from different (source) Collada files into the same (target) KMZ file.
 *
 * @param alias contains a mapping from a <sourceHref> to a <targetHref>
 * @param id
 * @param targetId
 * @param resourceMapSimpleExtensionGroup
 * @param resourceMapObjectExtensionGroup
 * @param objectSimpleExtensionGroup
 */
case class ResourceMap(alias: Seq[Alias] = Nil,
  id: Option[String] = None,
  targetId: Option[String] = None,
  resourceMapSimpleExtensionGroup: Seq[Any] = Nil,
  resourceMapObjectExtensionGroup: Seq[Any] = Nil,
  objectSimpleExtensionGroup: Seq[Any] = Nil) extends KmlObject {

  def this(alias: Alias) = this(Seq.empty :+ alias)
  def this(alias: Alias, id: String) = this((Seq.empty :+ alias), Option(id))
  def this(alias: Alias, id: String, targetId: String) = this((Seq.empty :+ alias), Option(id), Option(targetId))

  /**
   * returns a new object with value added to the sequence
   * @param value to add
   * @return a new object with value added to the sequence
   */
  def addToAlias(value: Alias) = {
    this.copy(alias = if (alias == Nil) (Seq.empty :+ value) else (alias :+ value))
  }
}

/**
 * contains a mapping from a <sourceHref> to a <targetHref>
 *
 * @param targetHref Specifies the texture file to be fetched by Google Earth. This reference can be a relative reference to an image file within the .kmz archive, or it can be an absolute reference to the file (for example, a URL).
 * @param sourceHref Is the path specified for the texture file in the Collada .dae file.
In Google Earth, if this mapping is not supplied, the following rules are used to locate the textures referenced in the Collada (.dae) file:
No path: If the texture name does not include a path, Google Earth looks for the texture in the same directory as the .dae file that references it.
Relative path: If the texture name includes a relative path (for example, ../images/mytexture.jpg), Google Earth interprets the path as being relative to the .dae file that references it.
Absolute path: If the texture name is an absolute path (c:\mytexture.jpg) or a network path (for example, http://myserver.com/mytexture.jpg), Google Earth looks for the file in the specified location, regardless of where the .dae file is located.
 * @param id
 * @param targetId
 * @param aliasSimpleExtensionGroup
 * @param aliasObjectExtensionGroup
 * @param objectSimpleExtensionGroup
 */
case class Alias(targetHref: Option[String] = None,
  sourceHref: Option[String] = None,
  id: Option[String] = None,
  targetId: Option[String] = None,
  aliasSimpleExtensionGroup: Seq[Any] = Nil,
  aliasObjectExtensionGroup: Seq[Any] = Nil,
  objectSimpleExtensionGroup: Seq[Any] = Nil) extends KmlObject {

  def this(tgtHref: String, srcHref: String) = this(Option(tgtHref), Option(srcHref))
  def this(tgtHref: String, srcHref: String, id: String) = this(Option(tgtHref), Option(srcHref), Option(id))

}


/**
 * This element draws an image overlay draped onto the terrain.
 * The <href> child of <Icon> specifies the image to be used as the overlay.
 * This file can be either on a local file system or on a web server.
 * If this element is omitted or contains no <href>, a rectangle is drawn using
 * the color and LatLonBox bounds defined by the ground overlay.
 *
 * @param altitude Distance from the earth's surface, in meters. Interpreted according to the LookAt's altitude mode.
 * @param altitudeMode Specifies how the <altitude> specified for the LookAt point is interpreted.
 *                     Possible values are as follows:
 *                     <ul>
 *  <li>clampToGround - (default) Indicates to ignore the <altitude> specification and place the LookAt position on the ground.
 *  <li>relativeToGround - Interprets the <altitude> as a value in meters above the ground.
 *  <li>absolute - Interprets the <altitude> as a value in meters above sea level.
 *  </ul>
 * @param latLonBox Specifies where the top, bottom, right, and left sides of a bounding box for the ground overlay are aligned.
 * <ul>
 *   <li> <north> Specifies the latitude of the north edge of the bounding box, in decimal degrees from 0 to ±90.
 *   <li> <south> Specifies the latitude of the south edge of the bounding box, in decimal degrees from 0 to ±90.
 *   <li> <east> Specifies the longitude of the east edge of the bounding box, in decimal degrees from 0 to ±180. (For overlays that overlap the meridian of 180° longitude, values can extend beyond that range.)
 *   <li> <west> Specifies the longitude of the west edge of the bounding box, in decimal degrees from 0 to ±180. (For overlays that overlap the meridian of 180° longitude, values can extend beyond that range.)
 *   <li> <rotation> Specifies a rotation of the overlay about its center, in degrees. Values can be ±180. The default is 0 (north). Rotations are specified in a counterclockwise direction.
 * </ul>
 *
 * @param featurePart the feature part of this object
 * @param color Color values are expressed in hexadecimal notation, including opacity (alpha) values. The order of expression is alpha, blue, green, red (aabbggrr). The range of values for any one color is 0 to 255 (00 to ff). For opacity, 00 is fully transparent and ff is fully opaque. For example, if you want to apply a blue color with 50 percent opacity to an overlay, you would specify the following: <color>7fff0000</color>
 * @param drawOrder This element defines the stacking order for the images in overlapping overlays. Overlays with higher <drawOrder> values are drawn on top of overlays with lower <drawOrder> values.
 * @param icon Defines the image associated with the Overlay. The <href> element defines the location of the image to be used as the Overlay. This location can be either on a local file system or on a web server. If this element is omitted or contains no <href>, a rectangle is drawn using the color and size defined by the ground or screen overlay.
 * @param id
 * @param targetId
 * @param groundOverlaySimpleExtensionGroup
 * @param groundOverlayObjectExtensionGroup
 * @param overlaySimpleExtensionGroup
 * @param overlayObjectExtensionGroup  see gx LatLonQuad
 * @param objectSimpleExtensionGroup
 */
case class GroundOverlay(altitude: Option[Double] = None,
  altitudeMode: Option[AltitudeMode] = None,
  latLonBox: Option[LatLonBox] = None,
  featurePart: FeaturePart = new FeaturePart(),
  color: Option[HexColor] = None,
  drawOrder: Option[Int] = None,
  icon: Option[Icon] = None,
  id: Option[String] = None,
  targetId: Option[String] = None,
  groundOverlaySimpleExtensionGroup: Seq[Any] = Nil,
  groundOverlayObjectExtensionGroup: Seq[Any] = Nil,
  overlaySimpleExtensionGroup: Seq[Any] = Nil,
  overlayObjectExtensionGroup: Seq[Any] = Nil,
  objectSimpleExtensionGroup: Seq[Any] = Nil) extends Overlay {

  def this(name: String, altitude: Double, altitudeMode: AltitudeMode, latLonBox: LatLonBox) =
    this(Option(altitude),Option(altitudeMode),Option(latLonBox), new FeaturePart(name = Option(name)))

  def this(altitude: Double, altitudeMode: AltitudeMode, latLonBox: LatLonBox) =
    this(Option(altitude),Option(altitudeMode),Option(latLonBox), new FeaturePart())

  def this(altitude: Double, altitudeMode: AltitudeMode, latLonBox: LatLonBox, color: HexColor, drawOrder: Int, icon: Icon) =
    this(Option(altitude),Option(altitudeMode),Option(latLonBox), new FeaturePart(), Option(color), Option(drawOrder), Option(icon))

  def this(altitude: Double, altitudeMode: AltitudeMode, latLonBox: LatLonBox, icon: Icon) =
    this(Option(altitude),Option(altitudeMode),Option(latLonBox), new FeaturePart(), None, None, Option(icon))

//  lazy val name = featurePart.name
//  lazy val visibility = featurePart.visibility
//  lazy val open = featurePart.open
//  lazy val atomAuthor = featurePart.atomAuthor
//  lazy val atomLink = featurePart.atomLink
//  lazy val address = featurePart.address
//  lazy val addressDetails = featurePart.addressDetails
//  lazy val phoneNumber = featurePart.phoneNumber
//  lazy val extendedData = featurePart.extendedData
//  lazy val description = featurePart.description
//  lazy val snippet = featurePart.snippet
//  lazy val abstractView = featurePart.abstractView
//  lazy val timePrimitive = featurePart.timePrimitive
//  lazy val styleUrl = featurePart.styleUrl
//  lazy val styleSelector = featurePart.styleSelector
//  lazy val region = featurePart.region
//
//  lazy val featureSimpleExtensionGroup = featurePart.featureSimpleExtensionGroup
//  lazy val featureObjectExtensionGroup = featurePart.featureObjectExtensionGroup

}

/**
 * This element draws an image overlay fixed to the screen. Sample uses for ScreenOverlays
 * are compasses, logos, and heads-up displays. ScreenOverlay sizing is determined by
 * the <size> element. Positioning of the overlay is handled by mapping a point in the
 * image specified by <overlayXY> to a point on the screen specified by <screenXY>.
 * Then the image is rotated by <rotation> degrees about a point relative to the screen
 * specified by <rotationXY>.
 * The <href> child of <Icon> specifies the image to be used as the overlay.
 * This file can be either on a local file system or on a web server.
 * If this element is omitted or contains no <href>, a rectangle is drawn using the
 * color and size defined by the screen overlay.
 *
 *
 * @param overlayXY Specifies a point on (or outside of) the overlay image that is mapped to the screen coordinate (<screenXY>). It requires x and y values, and the units for those values.
The x and y values can be specified in three different ways: as pixels ("pixels"), as fractions of the image ("fraction"), or as inset pixels ("insetPixels"), which is an offset in pixels from the upper right corner of the image. The x and y positions can be specified in different ways—for example, x can be in pixels and y can be a fraction. The origin of the coordinate system is in the lower left corner of the image.
x - Either the number of pixels, a fractional component of the image, or a pixel inset indicating the x component of a point on the overlay image.
y - Either the number of pixels, a fractional component of the image, or a pixel inset indicating the y component of a point on the overlay image.
xunits - Units in which the x value is specified. A value of "fraction" indicates the x value is a fraction of the image. A value of "pixels" indicates the x value in pixels. A value of "insetPixels" indicates the indent from the right edge of the image.
yunits - Units in which the y value is specified. A value of "fraction" indicates the y value is a fraction of the image. A value of "pixels" indicates the y value in pixels. A value of "insetPixels" indicates the indent from the top edge of the image.
 * @param screenXY Specifies a point relative to the screen origin that the overlay image is mapped to. The x and y values can be specified in three different ways: as pixels ("pixels"), as fractions of the screen ("fraction"), or as inset pixels ("insetPixels"), which is an offset in pixels from the upper right corner of the screen. The x and y positions can be specified in different ways—for example, x can be in pixels and y can be a fraction. The origin of the coordinate system is in the lower left corner of the screen.
x - Either the number of pixels, a fractional component of the screen, or a pixel inset indicating the x component of a point on the screen.
y - Either the number of pixels, a fractional component of the screen, or a pixel inset indicating the y component of a point on the screen.
xunits - Units in which the x value is specified. A value of "fraction" indicates the x value is a fraction of the screen. A value of "pixels" indicates the x value in pixels. A value of "insetPixels" indicates the indent from the right edge of the screen.
yunits - Units in which the y value is specified. A value of fraction indicates the y value is a fraction of the screen. A value of "pixels" indicates the y value in pixels. A value of "insetPixels" indicates the indent from the top edge of the screen.
 * @param rotationXY Point relative to the screen about which the screen overlay is rotated.
 * @param size Specifies the size of the image for the screen overlay, as follows:
A value of −1 indicates to use the native dimension
A value of 0 indicates to maintain the aspect ratio
A value of n sets the value of the dimension
 * @param rotation Indicates the angle of rotation of the parent object. A value of 0 means no rotation. The value is an angle in degrees counterclockwise starting from north. Use ±180 to indicate the rotation of the parent object from 0. The center of the <rotation>, if not (.5,.5), is specified in <rotationXY>.
 * @param featurePart
 * @param color
 * @param drawOrder
 * @param icon
 * @param id
 * @param targetId
 * @param screenOverlaySimpleExtensionGroup
 * @param screenOverlayObjectExtensionGroup
 * @param overlaySimpleExtensionGroup
 * @param overlayObjectExtensionGroup
 * @param objectSimpleExtensionGroup
 */
case class ScreenOverlay(overlayXY: Option[Vec2] = None,
  screenXY: Option[Vec2] = None,
  rotationXY: Option[Vec2] = None,
  size: Option[Vec2] = None,
  rotation: Option[Double] = None,
  featurePart: FeaturePart = new FeaturePart(),
  color: Option[HexColor] = None,
  drawOrder: Option[Int] = None,
  icon: Option[Icon] = None,
  id: Option[String] = None,
  targetId: Option[String] = None,
  screenOverlaySimpleExtensionGroup: Seq[Any] = Nil,
  screenOverlayObjectExtensionGroup: Seq[Any] = Nil,
  overlaySimpleExtensionGroup: Seq[Any] = Nil,
  overlayObjectExtensionGroup: Seq[Any] = Nil,
  objectSimpleExtensionGroup: Seq[Any] = Nil) extends Overlay {

  def this(overlayXY: Vec2, screenXY: Vec2, rotationXY: Vec2, size:  Vec2, rotation: Double) =
    this(Option(overlayXY),Option(screenXY),Option(rotationXY), Option(size), Option(rotation))

  def this(name: String, overlayXY: Vec2, screenXY: Vec2, rotationXY: Vec2, size:  Vec2, rotation: Double) =
    this(Option(overlayXY),Option(screenXY),Option(rotationXY), Option(size), Option(rotation), new FeaturePart(name = Option(name)))

}

/**
 * The <PhotoOverlay> element allows you to geographically locate a photograph on the Earth
 * and to specify viewing parameters for this PhotoOverlay. The PhotoOverlay can be a
 * simple 2D rectangle, a partial or full cylinder, or a sphere (for spherical panoramas).
 * The overlay is placed at the specified location and oriented toward the viewpoint.
 * Because <PhotoOverlay> is derived from <Feature>, it can contain one of the two elements
 * derived from <AbstractView>—either <Camera> or <LookAt>. The Camera (or LookAt) specifies
 * a viewpoint and a viewing direction (also referred to as a view vector).
 * The PhotoOverlay is positioned in relation to the viewpoint. Specifically, the plane
 * of a 2D rectangular image is orthogonal (at right angles to) the view vector.
 * The normal of this plane—that is, its front, which is the part with the photo—is
 * oriented toward the viewpoint.
 *
 * The URL for the PhotoOverlay image is specified in the <Icon> tag, which is inherited
 * from <Overlay>. The <Icon> tag must contain an <href> element that specifies the image
 * file to use for the PhotoOverlay. In the case of a very large image, the <href> is a
 * special URL that indexes into a pyramid of images of varying resolutions (see ImagePyramid).
 *
 * For more information, see the "Topics in KML" page on PhotoOverlay.
 *
 * @param rotation Adjusts how the photo is placed inside the field of view. This element is useful if your photo has been rotated and deviates slightly from a desired horizontal view.
 * @param viewVolume Defines how much of the current scene is visible. Specifying the field of view is analogous to specifying the lens opening in a physical camera. A small field of view, like a telephoto lens, focuses on a small part of the scene. A large field of view, like a wide-angle lens, focuses on a large part of the scene.
 * @param imagePyramid For very large images, you'll need to construct an image pyramid, which is a hierarchical set of images, each of which is an increasingly lower resolution version of the original image. Each image in the pyramid is subdivided into tiles, so that only the portions in view need to be loaded. Google Earth calculates the current viewpoint and loads the tiles that are appropriate to the user's distance from the image. As the viewpoint moves closer to the PhotoOverlay, Google Earth loads higher resolution tiles. Since all the pixels in the original image can't be viewed on the screen at once, this preprocessing allows Google Earth to achieve maximum performance because it loads only the portions of the image that are in view, and only the pixel details that can be discerned by the user at the current viewpoint.
When you specify an image pyramid, you also modify the <href> in the <Icon> element to include specifications for which tiles to load.
 * @param point The <Point> element acts as a <Point> inside a <Placemark> element. It draws an icon to mark the position of the PhotoOverlay. The icon drawn is specified by the <styleUrl> and <StyleSelector> fields, just as it is for <Placemark>.
 * @param shape The PhotoOverlay is projected onto the <shape>. The <shape> can be one of the following:
rectangle (default) - for an ordinary photo

cylinder - for panoramas, which can be either partial or full cylinders

sphere - for spherical panoramas
 * @param featurePart
 * @param color
 * @param drawOrder
 * @param icon
 * @param id
 * @param targetId
 * @param photoOverlaySimpleExtensionGroup
 * @param photoOverlayObjectExtensionGroup
 * @param overlaySimpleExtensionGroup
 * @param overlayObjectExtensionGroup
 * @param objectSimpleExtensionGroup
 */
case class PhotoOverlay(rotation: Option[Double] = None,
  viewVolume: Option[ViewVolume] = None,
  imagePyramid: Option[ImagePyramid] = None,
  point: Option[Point] = None,
  shape: Option[Shape] = None,
  featurePart: FeaturePart = new FeaturePart(),
  color: Option[HexColor] = None,
  drawOrder: Option[Int] = None,
  icon: Option[Icon] = None,
  id: Option[String] = None,
  targetId: Option[String] = None,
  photoOverlaySimpleExtensionGroup: Seq[Any] = Nil,
  photoOverlayObjectExtensionGroup: Seq[Any] = Nil,
  overlaySimpleExtensionGroup: Seq[Any] = Nil,
  overlayObjectExtensionGroup: Seq[Any] = Nil,
  objectSimpleExtensionGroup: Seq[Any] = Nil) extends Overlay {

  def this(rotation: Double, viewVolume: ViewVolume, imagePyramid: ImagePyramid, point: Point, shape: Shape) =
    this(Option(rotation),Option(viewVolume),Option(imagePyramid),Option(point),Option(shape))

  def this(name: String, rotation: Double, viewVolume: ViewVolume, imagePyramid: ImagePyramid, point: Point, shape: Shape) =
    this(Option(rotation),Option(viewVolume),Option(imagePyramid),Option(point),Option(shape), new FeaturePart(name = Option(name)))

}

/**
 * Defines how much of the current scene is visible. Specifying the field of view
 * is analogous to specifying the lens opening in a physical camera.
 * A small field of view, like a telephoto lens, focuses on a small part of the scene.
 * A large field of view, like a wide-angle lens, focuses on a large part of the scene.
 *
 * @param leftFov Angle, in degrees, between the camera's viewing direction and the left side of the view volume.
 * @param rightFov Angle, in degrees, between the camera's viewing direction and the right side of the view volume.
 * @param bottomFov Angle, in degrees, between the camera's viewing direction and the bottom side of the view volume.
 * @param topFov Angle, in degrees, between the camera's viewing direction and the top side of the view volume.
 * @param near Measurement in meters along the viewing direction from the camera viewpoint to the PhotoOverlay shape.
 * @param id
 * @param targetId
 * @param viewVolumeSimpleExtensionGroup
 * @param viewVolumeObjectExtensionGroup
 * @param objectSimpleExtensionGroup
 */
case class ViewVolume(leftFov: Option[Double] = None,
  rightFov: Option[Double] = None,
  bottomFov: Option[Double] = None,
  topFov: Option[Double] = None,
  near: Option[Double] = None,
  id: Option[String] = None,
  targetId: Option[String] = None,
  viewVolumeSimpleExtensionGroup: Seq[Any] = Nil,
  viewVolumeObjectExtensionGroup: Seq[Any] = Nil,
  objectSimpleExtensionGroup: Seq[Any] = Nil) extends KmlObject {

  def this(leftFov: Double, rightFov: Double, bottomFov: Double, near: Double) =
    this(Option(leftFov),Option(rightFov),Option(bottomFov),Option(near))

}

/**
 * For very large images, you'll need to construct an image pyramid, which is a hierarchical set of images,
 * each of which is an increasingly lower resolution version of the original image.
 * Each image in the pyramid is subdivided into tiles, so that only the portions in view need to be loaded.
 * Google Earth calculates the current viewpoint and loads the tiles that are appropriate to
 * the user's distance from the image. As the viewpoint moves closer to the PhotoOverlay,
 * Google Earth loads higher resolution tiles. Since all the pixels in the original image can't be viewed
 * on the screen at once, this preprocessing allows Google Earth to achieve maximum performance
 * because it loads only the portions of the image that are in view, and only the pixel details
 * that can be discerned by the user at the current viewpoint.
 * When you specify an image pyramid, you also modify the <href> in the <Icon> element
 * to include specifications for which tiles to load.
 *
 * @param tileSize Size of the tiles, in pixels. Tiles must be square, and <tileSize> must be a power of 2. A tile size of 256 (the default) or 512 is recommended. The original image is divided into tiles of this size, at varying resolutions.
 * @param maxWidth Width in pixels of the original image.
 * @param maxHeight Height in pixels of the original image.
 * @param gridOrigin Specifies where to begin numbering the tiles in each layer of the pyramid. A value of lowerLeft specifies that row 1, column 1 of each layer is in the bottom left corner of the grid.
 * @param id
 * @param targetId
 * @param imagePyramidSimpleExtensionGroup
 * @param imagePyramidObjectExtensionGroup
 * @param objectSimpleExtensionGroup
 */
case class ImagePyramid(tileSize: Option[Int] = None,
  maxWidth: Option[Int] = None,
  maxHeight: Option[Int] = None,
  gridOrigin: Option[GridOrigin] = None,
  id: Option[String] = None,
  targetId: Option[String] = None,
  imagePyramidSimpleExtensionGroup: Seq[Any] = Nil,
  imagePyramidObjectExtensionGroup: Seq[Any] = Nil,
  objectSimpleExtensionGroup: Seq[Any] = Nil) extends KmlObject {

  def this(tileSize: Int, maxWidth: Int, maxHeight: Int, gridOrigin: GridOrigin) =
    this(Option(tileSize),Option(maxWidth),Option(maxHeight),Option(gridOrigin))

}

/**
 * A Style defines an addressable style group that can be referenced by StyleMaps and Features.
 * Styles affect how Geometry is presented in the 3D viewer and how Features appear in the Places panel
 * of the List view. Shared styles are collected in a <Document> and must have an id defined for them
 * so that they can be referenced by the individual Features that use them.
 *
 * @param iconStyle
 * @param labelStyle
 * @param lineStyle
 * @param polyStyle
 * @param balloonStyle
 * @param listStyle
 * @param id
 * @param targetId
 * @param styleSimpleExtensionGroup
 * @param styleObjectExtensionGroup
 * @param styleSelectorSimpleExtensionGroup
 * @param styleSelectorObjectExtensionGroup
 * @param objectSimpleExtensionGroup
 */
case class Style(iconStyle: Option[IconStyle] = None,
  labelStyle: Option[LabelStyle] = None,
  lineStyle: Option[LineStyle] = None,
  polyStyle: Option[PolyStyle] = None,
  balloonStyle: Option[BalloonStyle] = None,
  listStyle: Option[ListStyle] = None,
  id: Option[String] = None,
  targetId: Option[String] = None,
  styleSimpleExtensionGroup: Seq[Any] = Nil,
  styleObjectExtensionGroup: Seq[Any] = Nil,
  styleSelectorSimpleExtensionGroup: Seq[Any] = Nil,
  styleSelectorObjectExtensionGroup: Seq[Any] = Nil,
  objectSimpleExtensionGroup: Seq[Any] = Nil) extends StyleSelector {

  def this(id: String) = this(None, None, None, None, None, None, Option(id))
  def this(iconStyle: IconStyle) = this(Option(iconStyle))
  def this(labelStyle: LabelStyle) = this(None, Option(labelStyle))
  def this(lineStyle: LineStyle) = this(None, None, Option(lineStyle))
  def this(polyStyle: PolyStyle) = this(None, None, None, Option(polyStyle))
  def this(balloonStyle: BalloonStyle) = this(None, None, None, None, Option(balloonStyle))
  def this(listStyle: ListStyle) = this(None, None, None, None, None, Option(listStyle))

}

/**
 * A <StyleMap> maps between two different Styles. Typically a <StyleMap> element is used to
 * provide separate normal and highlighted styles for a placemark, so that the highlighted version
 * appears when the user mouses over the icon in Google Earth.
 *
 * @param pair Defines a key/value pair that maps a mode (normal or highlight) to the predefined <styleUrl>. <Pair> contains two elements (both are required):
<key>, which identifies the key
<styleUrl> or <Style>, which references the style. In <styleUrl>, for referenced style elements that are local to the KML document, a simple # referencing is used. For styles that are contained in external files, use a full URL along with # referencing.
 * @param id
 * @param targetId
 * @param styleMapSimpleExtensionGroup
 * @param styleMapObjectExtensionGroup
 * @param styleSelectorSimpleExtensionGroup
 * @param styleSelectorObjectExtensionGroup
 * @param objectSimpleExtensionGroup
 */
case class StyleMap(pair: Seq[Pair] = Nil,
  id: Option[String] = None,
  targetId: Option[String] = None,
  styleMapSimpleExtensionGroup: Seq[Any] = Nil,
  styleMapObjectExtensionGroup: Seq[Any] = Nil,
  styleSelectorSimpleExtensionGroup: Seq[Any] = Nil,
  styleSelectorObjectExtensionGroup: Seq[Any] = Nil,
  objectSimpleExtensionGroup: Seq[Any] = Nil) extends StyleSelector {

  def this(pair: Pair) = this(Seq.empty :+ pair)
  def this(pair: Pair, id: String) = this((Seq.empty :+ pair), Option(id))

  /**
   * returns a new object with value added to the sequence
   * @param value to add
   * @return a new object with value added to the sequence
   */
  def addToPair(value: Pair) = {
    this.copy(pair = if (pair == Nil) (Seq.empty :+ value) else (pair :+ value))
  }
}

/**
 * Defines a key/value pair that maps a mode (normal or highlight) to the predefined <styleUrl>.
 * <Pair> contains two elements (both are required):
 * <key>, which identifies the key
 * <styleUrl> or <Style>, which references the style. In <styleUrl>, for referenced style elements that are local to the KML document, a simple # referencing is used. For styles that are contained in external files, use a full URL along with # referencing.
 *
 *
 * @param key  identifies the key
 * @param styleUrl references the style. For referenced style elements that are local to the KML document, a simple # referencing is used. For styles that are contained in external files, use a full URL along with # referencing.
 * @param styleSelector
 * @param id
 * @param targetId
 * @param pairSimpleExtensionGroup
 * @param pairObjectExtensionGroup
 * @param objectSimpleExtensionGroup
 */
case class Pair(key: Option[StyleState] = None,
  styleUrl: Option[String] = None,
  styleSelector: Option[StyleSelector] = None,
  id: Option[String] = None,
  targetId: Option[String] = None,
  pairSimpleExtensionGroup: Seq[Any] = Nil,
  pairObjectExtensionGroup: Seq[Any] = Nil,
  objectSimpleExtensionGroup: Seq[Any] = Nil) extends KmlObject {

  def this(key: StyleState, styleUrl: String) = this(Option(key), Option(styleUrl))
  def this(key: StyleState, styleUrl: String, styleSelector: StyleSelector, id: String) =
    this(Option(key), Option(styleUrl), Option(styleSelector), Option(id))

}

trait SubStyle extends KmlObject {
  val subStyleSimpleExtensionGroup: Seq[Any]
  val subStyleObjectExtensionGroup: Seq[Any]
}

trait ColorStyle extends SubStyle {
  val color: Option[HexColor]
  val colorMode: Option[ColorMode]
  val colorStyleSimpleExtensionGroup: Seq[Any]
  val colorStyleObjectExtensionGroup: Seq[Any]
}

/**
 * Specifies how icons for point Placemarks are drawn, both in the Places panel and in the 3D viewer
 * of Google Earth. The <Icon> element specifies the icon image. The <scale> element specifies
 * the x, y scaling of the icon. The color specified in the <color> element of <IconStyle> is
 * blended with the color of the <Icon>.
 *
 * @param scale Resizes the icon.
 * @param heading Direction (that is, North, South, East, West), in degrees. Default=0 (North)
 * @param icon A custom Icon. In <IconStyle>, the only child element of <Icon> is <href>:
<href>: An HTTP address or a local file specification used to load an icon.
 * @param hotSpot Specifies the position within the Icon that is "anchored" to the <Point> specified in the Placemark. The x and y values can be specified in three different ways: as pixels ("pixels"), as fractions of the icon ("fraction"), or as inset pixels ("insetPixels"), which is an offset in pixels from the upper right corner of the icon. The x and y positions can be specified in different ways—for example, x can be in pixels and y can be a fraction. The origin of the coordinate system is in the lower left corner of the icon.
x - Either the number of pixels, a fractional component of the icon, or a pixel inset indicating the x component of a point on the icon.
y - Either the number of pixels, a fractional component of the icon, or a pixel inset indicating the y component of a point on the icon.
xunits - Units in which the x value is specified. A value of fraction indicates the x value is a fraction of the icon. A value of pixels indicates the x value in pixels. A value of insetPixels indicates the indent from the right edge of the icon.
yunits - Units in which the y value is specified. A value of fraction indicates the y value is a fraction of the icon. A value of pixels indicates the y value in pixels. A value of insetPixels indicates the indent from the top edge of the icon.
 * @param color Color and opacity (alpha) values are expressed in hexadecimal notation. The range of values for any one color is 0 to 255 (00 to ff). For alpha, 00 is fully transparent and ff is fully opaque. The order of expression is aabbggrr, where aa=alpha (00 to ff); bb=blue (00 to ff); gg=green (00 to ff); rr=red (00 to ff). For example, if you want to apply a blue color with 50 percent opacity to an overlay, you would specify the following: <color>7fff0000</color>, where alpha=0x7f, blue=0xff, green=0x00, and red=0x00.
 * @param colorMode Values for <colorMode> are normal (no effect) and random. A value of random applies a random linear scale to the base <color> as follows.
To achieve a truly random selection of colors, specify a base <color> of white (ffffffff).
If you specify a single color component (for example, a value of ff0000ff for red), random color values for that one component (red) will be selected. In this case, the values would range from 00 (black) to ff (full red).
If you specify values for two or for all three color components, a random linear scale is applied to each color component, with results ranging from black to the maximum values specified for each component.
The opacity of a color comes from the alpha component of <color> and is never randomized.
 * @param id
 * @param targetId
 * @param iconStyleSimpleExtensionGroup
 * @param iconStyleObjectExtensionGroup
 * @param colorStyleSimpleExtensionGroup
 * @param colorStyleObjectExtensionGroup
 * @param subStyleSimpleExtensionGroup
 * @param subStyleObjectExtensionGroup
 * @param objectSimpleExtensionGroup
 */
case class IconStyle(scale: Option[Double] = None,
  heading: Option[Double] = None,
  icon: Option[Icon] = None,
  hotSpot: Option[Vec2] = None,
  color: Option[HexColor] = None,
  colorMode: Option[ColorMode] = None,
  id: Option[String] = None,
  targetId: Option[String] = None,
  iconStyleSimpleExtensionGroup: Seq[Any] = Nil,
  iconStyleObjectExtensionGroup: Seq[Any] = Nil,
  colorStyleSimpleExtensionGroup: Seq[Any] = Nil,
  colorStyleObjectExtensionGroup: Seq[Any] = Nil,
  subStyleSimpleExtensionGroup: Seq[Any] = Nil,
  subStyleObjectExtensionGroup: Seq[Any] = Nil,
  objectSimpleExtensionGroup: Seq[Any] = Nil) extends ColorStyle {

  def this(scale: Double, heading: Double, icon: Icon) =
    this(Option(scale), Option(heading), Option(icon))

  def this(scale: Double, heading: Double, icon: Icon, hotSpot: Vec2) =
    this(Option(scale), Option(heading), Option(icon), Option(hotSpot))

}

trait BasicLinkType extends KmlObject {
  val href: Option[String]
  val id: Option[String]
  val targetId: Option[String]
  val basicLinkSimpleExtensionGroup: Seq[Any]
  val basicLinkObjectExtensionGroup: Seq[Any]
  val objectSimpleExtensionGroup: Seq[Any]
}

/**
 * Specifies how the <name> of a Feature is drawn in the 3D viewer.
 * A custom color, color mode, and scale for the label (name) can be specified.
 *
 * @param scale Resizes the label.
 * @param color Color and opacity (alpha) values are expressed in hexadecimal notation. The range of values for any one color is 0 to 255 (00 to ff). For alpha, 00 is fully transparent and ff is fully opaque. The order of expression is aabbggrr, where aa=alpha (00 to ff); bb=blue (00 to ff); gg=green (00 to ff); rr=red (00 to ff). For example, if you want to apply a blue color with 50 percent opacity to an overlay, you would specify the following: <color>7fff0000</color>, where alpha=0x7f, blue=0xff, green=0x00, and red=0x00.
 * @param colorMode Values for <colorMode> are normal (no effect) and random. A value of random applies a random linear scale to the base <color> as follows.
To achieve a truly random selection of colors, specify a base <color> of white (ffffffff).
If you specify a single color component (for example, a value of ff0000ff for red), random color values for that one component (red) will be selected. In this case, the values would range from 00 (black) to ff (full red).
If you specify values for two or for all three color components, a random linear scale is applied to each color component, with results ranging from black to the maximum values specified for each component.
The opacity of a color comes from the alpha component of <color> and is never randomized.
 * @param id
 * @param targetId
 * @param labelStyleSimpleExtensionGroup
 * @param labelStyleObjectExtensionGroup
 * @param colorStyleSimpleExtensionGroup
 * @param colorStyleObjectExtensionGroup
 * @param subStyleSimpleExtensionGroup
 * @param subStyleObjectExtensionGroup
 * @param objectSimpleExtensionGroup
 */
case class LabelStyle(scale: Option[Double] = None,
  color: Option[HexColor] = None,
  colorMode: Option[ColorMode] = None,
  id: Option[String] = None,
  targetId: Option[String] = None,
  labelStyleSimpleExtensionGroup: Seq[Any] = Nil,
  labelStyleObjectExtensionGroup: Seq[Any] = Nil,
  colorStyleSimpleExtensionGroup: Seq[Any] = Nil,
  colorStyleObjectExtensionGroup: Seq[Any] = Nil,
  subStyleSimpleExtensionGroup: Seq[Any] = Nil,
  subStyleObjectExtensionGroup: Seq[Any] = Nil,
  objectSimpleExtensionGroup: Seq[Any] = Nil) extends ColorStyle {

  def this(scale: Double) = this(Option(scale))
  def this(color: HexColor) = this(None, Option(color))
  def this(scale: Double, color: HexColor, colorMode: ColorMode) =
    this(Option(scale), Option(color), Option(colorMode))
}

/**
 * Specifies the drawing style (color, color mode, and line width) for all line geometry.
 * Line geometry includes the outlines of outlined polygons and the extruded "tether" of
 * Placemark icons (if extrusion is enabled).
 *
 * @param width Width of the line, in pixels.
 * @param color Color and opacity (alpha) values are expressed in hexadecimal notation. The range of values for any one color is 0 to 255 (00 to ff). For alpha, 00 is fully transparent and ff is fully opaque. The order of expression is aabbggrr, where aa=alpha (00 to ff); bb=blue (00 to ff); gg=green (00 to ff); rr=red (00 to ff). For example, if you want to apply a blue color with 50 percent opacity to an overlay, you would specify the following: <color>7fff0000</color>, where alpha=0x7f, blue=0xff, green=0x00, and red=0x00.
 * @param colorMode Values for <colorMode> are normal (no effect) and random. A value of random applies a random linear scale to the base <color> as follows.
To achieve a truly random selection of colors, specify a base <color> of white (ffffffff).
If you specify a single color component (for example, a value of ff0000ff for red), random color values for that one component (red) will be selected. In this case, the values would range from 00 (black) to ff (full red).
If you specify values for two or for all three color components, a random linear scale is applied to each color component, with results ranging from black to the maximum values specified for each component.
The opacity of a color comes from the alpha component of <color> and is never randomized.
 * @param id
 * @param targetId
 * @param lineStyleSimpleExtensionGroup
 * @param lineStyleObjectExtensionGroup
 * @param colorStyleSimpleExtensionGroup
 * @param colorStyleObjectExtensionGroup
 * @param subStyleSimpleExtensionGroup
 * @param subStyleObjectExtensionGroup
 * @param objectSimpleExtensionGroup
 */
case class LineStyle(width: Option[Double] = None,
  color: Option[HexColor] = None,
  colorMode: Option[ColorMode] = None,
  id: Option[String] = None,
  targetId: Option[String] = None,
  lineStyleSimpleExtensionGroup: Seq[Any] = Nil,
  lineStyleObjectExtensionGroup: Seq[Any] = Nil,
  colorStyleSimpleExtensionGroup: Seq[Any] = Nil,
  colorStyleObjectExtensionGroup: Seq[Any] = Nil,
  subStyleSimpleExtensionGroup: Seq[Any] = Nil,
  subStyleObjectExtensionGroup: Seq[Any] = Nil,
  objectSimpleExtensionGroup: Seq[Any] = Nil) extends ColorStyle {

  def this(width: Double) = this(Option(width))
  def this(color: HexColor) = this(None, Option(color))
  def this(width: Double, color: HexColor, colorMode: ColorMode) =
    this(Option(width), Option(color), Option(colorMode))
}

/**
 * Specifies the drawing style for all polygons, including polygon extrusions
 * (which look like the walls of buildings) and line extrusions (which look like solid fences).
 *
 * @param fill Boolean value. Specifies whether to fill the polygon.
 * @param outline Boolean value. Specifies whether to outline the polygon. Polygon outlines use the current LineStyle.
 * @param color Color and opacity (alpha) values are expressed in hexadecimal notation. The range of values for any one color is 0 to 255 (00 to ff). For alpha, 00 is fully transparent and ff is fully opaque. The order of expression is aabbggrr, where aa=alpha (00 to ff); bb=blue (00 to ff); gg=green (00 to ff); rr=red (00 to ff). For example, if you want to apply a blue color with 50 percent opacity to an overlay, you would specify the following: <color>7fff0000</color>, where alpha=0x7f, blue=0xff, green=0x00, and red=0x00.
 * @param colorMode Values for <colorMode> are normal (no effect) and random. A value of random applies a random linear scale to the base <color> as follows.
To achieve a truly random selection of colors, specify a base <color> of white (ffffffff).
If you specify a single color component (for example, a value of ff0000ff for red), random color values for that one component (red) will be selected. In this case, the values would range from 00 (black) to ff (full red).
If you specify values for two or for all three color components, a random linear scale is applied to each color component, with results ranging from black to the maximum values specified for each component.
The opacity of a color comes from the alpha component of <color> and is never randomized.
 * @param id
 * @param targetId
 * @param polyStyleSimpleExtensionGroup
 * @param polyStyleObjectExtensionGroup
 * @param colorStyleSimpleExtensionGroup
 * @param colorStyleObjectExtensionGroup
 * @param subStyleSimpleExtensionGroup
 * @param subStyleObjectExtensionGroup
 * @param objectSimpleExtensionGroup
 */
case class PolyStyle(fill: Option[Boolean] = None,
  outline: Option[Boolean] = None,
  color: Option[HexColor] = None,
  colorMode: Option[ColorMode] = None,
  id: Option[String] = None,
  targetId: Option[String] = None,
  polyStyleSimpleExtensionGroup: Seq[Any] = Nil,
  polyStyleObjectExtensionGroup: Seq[Any] = Nil,
  colorStyleSimpleExtensionGroup: Seq[Any] = Nil,
  colorStyleObjectExtensionGroup: Seq[Any] = Nil,
  subStyleSimpleExtensionGroup: Seq[Any] = Nil,
  subStyleObjectExtensionGroup: Seq[Any] = Nil,
  objectSimpleExtensionGroup: Seq[Any] = Nil) extends ColorStyle {

  def this(color: HexColor) = this(None, None, Option(color))

  def this(fill: Boolean) = this(Option(fill))

  def this(fill: Boolean, outLine: Boolean) = this(Option(fill), Option(outLine))

  def this(fill: Boolean, outLine: Boolean, color: HexColor, colorMode: ColorMode) =
    this(Option(fill), Option(outLine), Option(color), Option(colorMode))

}

/**
 * Specifies how the description balloon for placemarks is drawn.
 * The <bgColor>, if specified, is used as the background color of the balloon.
 * See <Feature> for a diagram illustrating how the default description balloon appears in Google Earth.
 *
 * @param bgColor Background color of the balloon (optional). Color and opacity (alpha) values are expressed in hexadecimal notation. The range of values for any one color is 0 to 255 (00 to ff). The order of expression is aabbggrr, where aa=alpha (00 to ff); bb=blue (00 to ff); gg=green (00 to ff); rr=red (00 to ff). For alpha, 00 is fully transparent and ff is fully opaque. For example, if you want to apply a blue color with 50 percent opacity to an overlay, you would specify the following: <bgColor>7fff0000</bgColor>, where alpha=0x7f, blue=0xff, green=0x00, and red=0x00. The default is opaque white (ffffffff).
 * @param textColor Foreground color for text. The default is black (ff000000).
 * @param text Text displayed in the balloon. If no text is specified, Google Earth draws the default balloon (with the Feature <name> in boldface, the Feature <description>, links for driving directions, a white background, and a tail that is attached to the point coordinates of the Feature, if specified).
You can add entities to the <text> tag using the following format to refer to a child element of Feature: $[name], $[description], $[address], $[id], $[Snippet]. Google Earth looks in the current Feature for the corresponding string entity and substitutes that information in the balloon. To include To here - From here driving directions in the balloon, use the $[geDirections] tag. To prevent the driving directions links from appearing in a balloon, include the <text> element with some content, or with $[description] to substitute the basic Feature <description>.
 * @param displayMode If <displayMode> is default, Google Earth uses the information supplied in <text> to create a balloon . If <displayMode> is hide, Google Earth does not display the balloon. In Google Earth, clicking the List View icon for a Placemark whose balloon's <displayMode> is hide causes Google Earth to fly to the Placemark.
 * @param id
 * @param targetId
 * @param balloonStyleSimpleExtensionGroup
 * @param balloonStyleObjectExtensionGroup
 * @param subStyleSimpleExtensionGroup
 * @param subStyleObjectExtensionGroup
 * @param objectSimpleExtensionGroup
 */
case class BalloonStyle(bgColor: Option[HexColor] = None,
  textColor: Option[HexColor] = None,
  text: Option[String] = None,
  displayMode: Option[DisplayMode] = None,
  id: Option[String] = None,
  targetId: Option[String] = None,
  balloonStyleSimpleExtensionGroup: Seq[Any] = Nil,
  balloonStyleObjectExtensionGroup: Seq[Any] = Nil,
  subStyleSimpleExtensionGroup: Seq[Any] = Nil,
  subStyleObjectExtensionGroup: Seq[Any] = Nil,
  objectSimpleExtensionGroup: Seq[Any] = Nil) extends SubStyle {

  def this(text: String) = this(None, None, Option(text))
  def this(text: String, displayMode: DisplayMode) = this(None, None, Option(text), Option(displayMode))
  def this(text: String, color: HexColor, bgColor: HexColor, textColor: HexColor) =
    this(Option(bgColor), Option(textColor), Option(text))

  def this(text: String, color: HexColor) = this(None, None, Option(text))

}

/**
 * Specifies how a Feature is displayed in the list view.
 * The list view is a hierarchy of containers and children; in Google Earth, this is the Places panel.
 *
 * @param listItemType Specifies how a Feature is displayed in the list view. Possible values are:
check (default) - The Feature's visibility is tied to its item's checkbox.
radioFolder - When specified for a Container, only one of the Container's items is visible at a time
checkOffOnly - When specified for a Container or Network Link, prevents all items from being made visible at once—that is, the user can turn everything in the Container or Network Link off but cannot turn everything on at the same time. This setting is useful for Containers or Network Links containing large amounts of data.
checkHideChildren - Use a normal checkbox for visibility but do not display the Container or Network Link's children in the list view. A checkbox allows the user to toggle visibility of the child objects in the viewer.
 * @param bgColor Background color for the Snippet. Color and opacity values are expressed in hexadecimal notation. The range of values for any one color is 0 to 255 (00 to ff). For alpha, 00 is fully transparent and ff is fully opaque. The order of expression is aabbggrr, where aa=alpha (00 to ff); bb=blue (00 to ff); gg=green (00 to ff); rr=red (00 to ff). For example, if you want to apply a blue color with 50 percent opacity to an overlay, you would specify the following: <color>7fff0000</color>, where alpha=0x7f, blue=0xff, green=0x00, and red=0x00.
 * @param itemIcon Icon used in the List view that reflects the state of a Folder or Link fetch. Icons associated with the open and closed modes are used for Folders and Network Links. Icons associated with the error and fetching0, fetching1, and fetching2 modes are used for Network Links.
 * @param maxSnippetLines Specifies the maximum number of lines to display for the kml:AbstractFeatureGroup kml:snippet value in the list view.
 * @param id
 * @param targetId
 * @param listStyleSimpleExtensionGroup
 * @param listStyleObjectExtensionGroup
 * @param subStyleSimpleExtensionGroup
 * @param subStyleObjectExtensionGroup
 * @param objectSimpleExtensionGroup
 */
case class ListStyle(listItemType: Option[ListItemType] = None,
  bgColor: Option[HexColor] = None,
  itemIcon: Seq[ItemIcon] = Nil,
  maxSnippetLines: Option[Int] = None,
  id: Option[String] = None,
  targetId: Option[String] = None,
  listStyleSimpleExtensionGroup: Seq[Any] = Nil,
  listStyleObjectExtensionGroup: Seq[Any] = Nil,
  subStyleSimpleExtensionGroup: Seq[Any] = Nil,
  subStyleObjectExtensionGroup: Seq[Any] = Nil,
  objectSimpleExtensionGroup: Seq[Any] = Nil) extends SubStyle {

  def this(bgColor: HexColor) = this(None, Option(bgColor))
  def this(listItemType: ListItemType) = this(Option(listItemType))
  def this(listItemType: ListItemType, bgColor: HexColor) = this(Option(listItemType), Option(bgColor))
  def this(listItemType: ListItemType, bgColor: HexColor, itemIcon: ItemIcon) =
    this(Option(listItemType), Option(bgColor), (Seq.empty :+ itemIcon))

  /**
   * returns a new object with value added to the sequence
   * @param value to add
   * @return a new object with value added to the sequence
   */
  def addToItemIcon(value: ItemIcon) = {
    this.copy(itemIcon = if (itemIcon == Nil) (Seq.empty :+ value) else (itemIcon :+ value))
  }
}

/**
 * Icon used in the List view that reflects the state of a Folder or Link fetch.
 * Icons associated with the open and closed modes are used for Folders and Network Links.
 * Icons associated with the error and fetching0, fetching1, and fetching2 modes are used
 * for Network Links.
 *
 * @param objectSimpleExtensionGroup
 * @param state Specifies the current state of the NetworkLink or Folder. Possible values are open, closed, error, fetching0, fetching1, and fetching2. These values can be combined by inserting a space between two values (no comma).
 * @param href Specifies the URI of the image used in the List View for the Feature.
 * @param id
 * @param itemIconSimpleExtensionGroup
 * @param itemIconObjectExtensionGroup
 * @param targetId
 */
case class ItemIcon(objectSimpleExtensionGroup: Seq[Any] = Nil,
  state: Seq[ItemIconState] = Nil,
  href: Option[String] = None,
  id: Option[String] = None,
  itemIconSimpleExtensionGroup: Seq[Any] = Nil,
  itemIconObjectExtensionGroup: Seq[Any] = Nil,
  targetId: Option[String] = None) extends KmlObject {

  def this(href: String) = this(Nil, Nil, Option(href))
  def this(href: String, state: ItemIconState) = this(Nil, (Seq.empty :+ state), Option(href))
  def this(href: String, state: Seq[ItemIconState]) = this(Nil, state, Option(href))

  /**
   * returns a new object with value added to the sequence
   * @param value to add
   * @return a new object with value added to the sequence
   */
  def addToState(value: ItemIconState) = {
    this.copy(state = if (state == Nil) (Seq.empty :+ value) else (state :+ value))
  }
}

/**
 * Represents a single moment in time. This is a simple element and contains no children.
 * Its value is a dateTime, specified in XML time (see XML Schema Part 2: Datatypes Second Edition).
 * The precision of the TimeStamp is dictated by the dateTime value in the <when> element.
 *
 * @param when Specifies a single moment in time. The value is a dateTime, which can be one of the following:
dateTime gives second resolution
date gives day resolution
gYearMonth gives month resolution
gYear gives year resolution
 * @param id
 * @param targetId
 * @param timeStampSimpleExtensionGroup
 * @param timeStampObjectExtensionGroup
 * @param timePrimitiveSimpleExtensionGroup
 * @param timePrimitiveObjectExtensionGroup
 * @param objectSimpleExtensionGroup
 */
case class TimeStamp(when: Option[String] = None,
  id: Option[String] = None,
  targetId: Option[String] = None,
  timeStampSimpleExtensionGroup: Seq[Any] = Nil,
  timeStampObjectExtensionGroup: Seq[Any] = Nil,
  timePrimitiveSimpleExtensionGroup: Seq[Any] = Nil,
  timePrimitiveObjectExtensionGroup: Seq[Any] = Nil,
  objectSimpleExtensionGroup: Seq[Any] = Nil) extends TimePrimitive {

  def this(when: String) = this(Option(when))

}

/**
 * Represents an extent in time bounded by begin and end dateTimes.
 * If <begin> or <end> is missing, then that end of the period is unbounded (see Example below).
 * The dateTime is defined according to XML Schema time (see XML Schema Part 2: Datatypes Second Edition).
 * The value can be expressed as yyyy-mm-ddThh:mm:ss.ssszzzzzz, where T is the separator between
 * the date and the time, and the time zone is either Z (for UTC) or zzzzzz, which represents
 * ±hh:mm in relation to UTC. Additionally, the value can be expressed as a date only.
 * See <TimeStamp> for examples.

 * @param begin Describes the beginning instant of a time period. If absent, the beginning of the period is unbounded.
 * @param end Describes the ending instant of a time period. If absent, the end of the period is unbounded.
 * @param id
 * @param targetId
 * @param timeSpanSimpleExtensionGroup
 * @param timeSpanObjectExtensionGroup
 * @param timePrimitiveSimpleExtensionGroup
 * @param timePrimitiveObjectExtensionGroup
 * @param objectSimpleExtensionGroup
 */
case class TimeSpan(begin: Option[String] = None,
  end: Option[String] = None,
  id: Option[String] = None,
  targetId: Option[String] = None,
  timeSpanSimpleExtensionGroup: Seq[Any] = Nil,
  timeSpanObjectExtensionGroup: Seq[Any] = Nil,
  timePrimitiveSimpleExtensionGroup: Seq[Any] = Nil,
  timePrimitiveObjectExtensionGroup: Seq[Any] = Nil,
  objectSimpleExtensionGroup: Seq[Any] = Nil) extends TimePrimitive {

  def this(begin: String, end: String) = this(Option(begin), Option(end))

}

/**
 * With <Update>, you can specify any number of Change, Create, and Delete tags for a .kml file or .kmz archive
 * that has previously been loaded with a network link.
 *
 * @param targetHref Specifies the URL for the target KML resource that has been previously retrieved via kml:NetworkLink.
 * @param updateOption Create,Change and Delete options
 * @param updateOpExtensionGroup
 * @param updateExtensionGroup
 */
case class Update(targetHref: String,
  updateOption: Seq[UpdateOption] = Nil,
  updateOpExtensionGroup: Seq[Any] = Nil,
  updateExtensionGroup: Seq[Any] = Nil) {

  def this(targetHref: String, updateOption: UpdateOption) = this(targetHref, (Seq.empty :+ updateOption))

  /**
   * returns a new object with value added to the sequence
   * @param value to add
   * @return a new object with value added to the sequence
   */
  def addToUpdateOption(value: UpdateOption) = {
    this.copy(updateOption = if (updateOption == Nil) (Seq.empty :+ value) else (updateOption :+ value))
  }
}

trait UpdateOption

/**
 * Adds new elements to a Folder or Document that has already been loaded via a <NetworkLink>.
 * The <targetHref> element in <Update> specifies the URL of the .kml or .kmz file that
 * contained the original Folder or Document. Within that file, the Folder or Document that
 * is to contain the new data must already have an explicit id defined for it.
 * This id is referenced as the targetId attribute of the Folder or Document within <Create> that
 * contains the element to be added.
 * Once an object has been created and loaded into Google Earth, it takes on the URL of the original
 * parent Document of Folder. To perform subsequent updates to objects added with this
 * Update/Create mechanism, set <targetHref> to the URL of the original Document or Folder
 * (not the URL of the file that loaded the intervening updates).
 *
 * @param containerSet
 */
case class Create(containerSet: Seq[Container]) extends UpdateOption {

  def this(container: Container) = this(Seq.empty :+ container)

  /**
   * returns a new object with value added to the sequence
   * @param value to add
   * @return a new object with value added to the sequence
   */
  def addToContainerSet(value: Container) = {
    this.copy(containerSet = if (containerSet == Nil) (Seq.empty :+ value) else (containerSet :+ value))
  }
}

/**
 * Deletes features from a complex element that has already been loaded via a <NetworkLink>.
 * The <targetHref> element in <Update> specifies the .kml or .kmz file containing the data
 * to be deleted. Within that file, the element to be deleted must already have an explicit id
 * defined for it. The <Delete> element references this id in the targetId attribute.
 * Child elements for <Delete>, which are the only elements that can be deleted, are
 * Document, Folder, GroundOverlay, Placemark, and ScreenOverlay.

 * @param featureSet
 */
case class Delete(featureSet: Seq[Feature]) extends UpdateOption  {

  def this(feature: Feature) = this(Seq.empty :+ feature)

  /**
   * returns a new object with value added to the sequence
   * @param value to add
   * @return a new object with value added to the sequence
   */
  def addToFeatureSet(value: Feature) = {
    this.copy(featureSet = if (featureSet == Nil) (Seq.empty :+ value) else (featureSet :+ value))
  }
}

/**
 * Modifies the values in an element that has already been loaded with a <NetworkLink>.
 * Within the Change element, the child to be modified must include a targetId attribute that
 * references the original element's id.
 * This update can be considered a "sparse update": in the modified element, only the values listed
 * in <Change> are replaced; all other values remained untouched. When <Change> is applied to a set
 * of coordinates, the new coordinates replace the current coordinates.
 * Children of this element are the element(s) to be modified, which are identified by the targetId attribute.

 * @param objectChangeSet
 */
case class Change(objectChangeSet: Seq[Any]) extends UpdateOption  {

  def this(objectChange: Any) = this(Seq.empty :+ objectChange)

  /**
   * returns a new object with value added to the sequence
   * @param value to add
   * @return a new object with value added to the sequence
   */
  def addToObjectChangeSet(value: Any) = {
    this.copy(objectChangeSet = if (objectChangeSet == Nil) (Seq.empty :+ value) else (objectChangeSet :+ value))
  }
}

/**
 * represents the attribute id and targetId
 *
 * @param id the attribute, which allows unique identification of a KML element
 * @param targetId attribute, which is used to reference objects that have already been loaded into Google Earth
 */
case class IdAttributes(id: Option[String] = None, targetId: Option[String] = None) {

  def this(id: String, targetId: String) = this(Option(id), Option(targetId))

}

