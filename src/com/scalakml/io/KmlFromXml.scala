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

import com.scalakml.gx._
import com.scalakml.kml._
import scala.xml._
import scala.reflect.runtime.universe._
import com.scalaxal.xAL.AddressDetails
import com.scalaxal.io.XalFromXml._
import xml.XML._


/**
 * @author Ringo Wathelet
 *         Date: 12/12/12
 *         Version: 1
 *
 *         Reference: OGC 07-147r2 Version: 2.2.0, Category: OGC Standard, Editor: Tim Wilson, at
 *         http://www.opengeospatial.org/standards/kml
 *         also
 *         Google developers KML Reference, at
 *         https://developers.google.com/kml/documentation/kmlreference
 *
 */

/** Factory for creating kml objects instances from scala xml NodeSeq */
object KmlFromXml extends KmlExtractor {

  import FeatureTypes._
  import GeometryTypes._
  import ContainerTypes._
  import KmlObjectTypes._
  import UpdateOptionTypes._
  import TourPrimitiveTypes._

  /**
   * creates a Kml root element from the XML NodeSeq, e.g. <kml> ... </kml>
   * The Kml consists of 0 or 1 Feature type object, and 0 or 1 NetworkLinkControl
   * The Kml can also have a hint attribute used as a signal to Google Earth to display the file as celestial data.
   *
   * @param nodeSeq the xml NodeSeq
   * @return a Kml Option
   */
  def makeKml(nodeSeq: NodeSeq): Option[Kml] = {
    if (nodeSeq.isEmpty) None
    else
      (nodeSeq \\ "kml") match {
        case x if (x.isEmpty) => None
        case x => Some(new Kml(makeNetworkLinkControl(x \ "NetworkLinkControl"), makeMainFeature(x), makeHint(x)))
      }
  }

  /**
   * Creates a NetworkLinkControl from the NodeSeq.
   *
   * @param nodeSeq the scala xml NodeSeq, e.g. <NetworkLinkControl> ... </NetworkLinkControl>
   * @return an NetworkLinkControl object
   */
  def makeNetworkLinkControl(nodeSeq: NodeSeq): Option[NetworkLinkControl] = {
    if (nodeSeq.isEmpty) None
    else Some(new NetworkLinkControl(
      minRefreshPeriod = getDouble(nodeSeq \ "minRefreshPeriod"),
      maxSessionLength = getDouble(nodeSeq \ "maxSessionLength"),
      cookie = getString(nodeSeq \ "cookie"),
      message = getString(nodeSeq \ "message"),
      linkName = getString(nodeSeq \ "linkName"),
      linkDescription = getString(nodeSeq \ "linkDescription"),
      linkSnippet = makeSnippetFromNode(nodeSeq \ "linkSnippet"),
      expires = getString(nodeSeq \ "expires"),
      update = makeUpdate(nodeSeq \ "Update"),
      abstractView = makeAbstractView(nodeSeq)))
  }

  /**
   * Creates a Hint attribute for the Kml root element from the NodeSeq <kml> ... </kml>
   *
   * @param nodeSeq the scala xml NodeSeq
   * @return an Hint attribute
   */
  def makeHint(nodeSeq: NodeSeq): Option[String] =
    if (nodeSeq.isEmpty) None else getString(nodeSeq \ "@hint")

  /**
   * Creates a "main" feature (for a Kml root element object) from the NodeSeq.
   * Returns the first found non empty Feature from amongst:
   * Document, Folder, Placemark, NetworkLink, PhotoOverlay, ScreenOverlay, GroundOverlay, Tour
   *
   * @param nodeSeq the scala xml NodeSeq, e.g. <kml> ... </kml>
   * @return an object that derives from Feature, one of: Document, Folder, Placemark, NetworkLink, PhotoOverlay, ScreenOverlay, GroundOverlay, Tour
   */
  def makeMainFeature(nodeSeq: NodeSeq): Option[Feature] = {
    if (nodeSeq.isEmpty) None
    else {
      for (x <- FeatureTypes.values) {
        val feature = makeFeature(nodeSeq \ x.toString, x)
        if (feature.isDefined) return feature
      }
    }
    None
  }

  /**
   * Creates an Update from the NodeSeq.
   *
   * @param nodeSeq the scala xml NodeSeq, e.g. <Update> ... </Update>
   * @return an Update object
   */
  def makeUpdate(nodeSeq: NodeSeq): Option[Update] = {
    if (nodeSeq.isEmpty) None
    else Some(new Update(targetHref = getString(nodeSeq \ "targetHref"), updateOption = makeUpdateOptions(nodeSeq)))
  }

  // TODO this does not check that there must be only one of the different types
  /**
   * Creates a Update option, one of: Delete, Create or Change from the NodeSeq.
   *
   * @param nodeSeq the scala xml NodeSeq, e.g. <Update> ... </Update>
   * @return an Update option sequence containing: Delete, Create or Change options
   */
  def makeUpdateOptions(nodeSeq: NodeSeq): Seq[UpdateOption] = {
    if (nodeSeq.isEmpty) Seq.empty
    else {
      val theSeq = UpdateOptionTypes.values collect {
        case x =>
          x match {
            case UpdateOptionTypes.Delete => makeDelete(nodeSeq \ x.toString)
            case UpdateOptionTypes.Create => makeCreate(nodeSeq \ x.toString)
            case UpdateOptionTypes.Change => makeChange(nodeSeq \ x.toString)
            case _ => None
          }
      }
      (theSeq flatMap (x => x) toSeq)
    }
  }

  def makeDelete(nodeSeq: NodeSeq): Option[UpdateOption] =
    if (nodeSeq.isEmpty) None else Some(new Delete(featureSet = makeFeatureSet(nodeSeq)))

  def makeCreate(nodeSeq: NodeSeq): Option[UpdateOption] =
    if (nodeSeq.isEmpty) None else Some(new Create(containerSet = makeContainerSet(nodeSeq)))

  def makeChange(nodeSeq: NodeSeq): Option[UpdateOption] =
    if (nodeSeq.isEmpty) None else Some(new Change(objectChangeSet = makeKmlObjectSet(nodeSeq)))

  def makeContainers(nodeSeq: NodeSeq, containerType: ContainerTypes): Seq[Option[Container]] = {
    (nodeSeq collect {
      case x => makeContainer(x, containerType)
    }) filter (_ != None)
  }

  def makeContainer(nodeSeq: NodeSeq, containerType: ContainerTypes): Option[Container] = {
    if (nodeSeq.isEmpty) None
    else
      containerType match {
        case ContainerTypes.Document => makeDocument(nodeSeq)
        case ContainerTypes.Folder => makeFolder(nodeSeq)
        case _ => None
      }
  }

  def makeContainerSet(nodeSeq: NodeSeq): Seq[Container] = {
    if (nodeSeq.isEmpty) Seq.empty
    else
      (ContainerTypes.values.flatMap(x => makeContainers(nodeSeq \ x.toString, x)).toSeq.flatten)
  }

  /**
   * Creates all features of the given featureType
   * @see FeatureTypes
   *
   * @param nodeSeq the node sequence to create the features from
   * @param featureType the type of features to extract from nodeSeq
   * @return a sequence of Feature Options of the chosen featureType
   */
  def makeFeatures(nodeSeq: NodeSeq, featureType: FeatureTypes): Seq[Option[Feature]] = {
    if (nodeSeq.isEmpty) Seq.empty
    else
      ((nodeSeq collect {
        case x => makeFeature(x, featureType)
      }) filter (_ != None))
  }

  /**
   * Creates one feature of the given featureType from the given NodeSeq
   *
   * @param nodeSeq the node sequence to create the feature from
   * @return a Feature Option
   */
  def makeFeature(nodeSeq: NodeSeq, featureType: FeatureTypes): Option[Feature] = {
    if (nodeSeq.isEmpty) None
    else {
      featureType match {
        case FeatureTypes.Placemark => makePlacemark(nodeSeq)
        case FeatureTypes.Document => makeDocument(nodeSeq)
        case FeatureTypes.Folder => makeFolder(nodeSeq)
        case FeatureTypes.NetworkLink => makeNetworkLink(nodeSeq)
        case FeatureTypes.PhotoOverlay => makePhotoOverlay(nodeSeq)
        case FeatureTypes.ScreenOverlay => makeScreenOverlay(nodeSeq)
        case FeatureTypes.GroundOverlay => makeGroundOverlay(nodeSeq)
        case FeatureTypes.Tour => makeTour(nodeSeq)
        case _ => None
      }
    }
  }

  /**
   * Creates all features from the given NodeSeq
   *
   * @param nodeSeq the node sequence to create the features from
   * @return a sequence of Features
   */
  def makeFeatureSet(nodeSeq: NodeSeq): Seq[Feature] = {
    if (nodeSeq.isEmpty) Seq.empty
    else
      (FeatureTypes.values.flatMap(x => makeFeatures(nodeSeq \ x.toString, x)).toSeq.flatten)
  }

  def makeKmlObjectSet(nodeSeq: NodeSeq): Seq[KmlObject] = {
    if (nodeSeq.isEmpty) Seq.empty
    else (KmlObjectTypes.values.flatMap(x => makeKmlObjects(nodeSeq \ x.toString, x)).toSeq.flatten)
  }

  def makeKmlObjects(nodeSeq: NodeSeq, kmlObjectType: KmlObjectTypes): Seq[Option[KmlObject]] = {
    (nodeSeq collect {
      case x => makeKmlObject(x, kmlObjectType)
    }) filter (_ != None)
  }

  def makeKmlObject(nodeSeq: NodeSeq, kmlObjectType: KmlObjectTypes): Option[KmlObject] = {
    if (nodeSeq.isEmpty) None
    else {
      kmlObjectType match {
        case KmlObjectTypes.ResourceMap => makeResourceMap(nodeSeq)
        case KmlObjectTypes.Alias => makeAlias(nodeSeq)
        case KmlObjectTypes.ViewVolume => makeViewVolume(nodeSeq)
        case KmlObjectTypes.ImagePyramid => makeImagePyramid(nodeSeq)
        case KmlObjectTypes.Pair => makePair(nodeSeq)
        case KmlObjectTypes.LineStyle => makeLineStyle(nodeSeq)
        case KmlObjectTypes.PolyStyle => makePolyStyle(nodeSeq)
        case KmlObjectTypes.IconStyle => makeIconStyle(nodeSeq)
        case KmlObjectTypes.LabelStyle => makeLabelStyle(nodeSeq)
        case KmlObjectTypes.BalloonStyle => makeBalloonStyle(nodeSeq)
        case KmlObjectTypes.ListStyle => makeListStyle(nodeSeq)
        case KmlObjectTypes.ItemIcon => makeItemIcon(nodeSeq)
        case KmlObjectTypes.Placemark => makePlacemark(nodeSeq)
        case KmlObjectTypes.Document => makeDocument(nodeSeq)
        case KmlObjectTypes.Folder => makeFolder(nodeSeq)
        case KmlObjectTypes.NetworkLink => makeNetworkLink(nodeSeq)
        case KmlObjectTypes.PhotoOverlay => makePhotoOverlay(nodeSeq)
        case KmlObjectTypes.ScreenOverlay => makeScreenOverlay(nodeSeq)
        case KmlObjectTypes.GroundOverlay => makeGroundOverlay(nodeSeq)
        case KmlObjectTypes.Tour => makeTour(nodeSeq)
        case KmlObjectTypes.Point => makePoint(nodeSeq)
        case KmlObjectTypes.LineString => makeLineString(nodeSeq)
        case KmlObjectTypes.LinearRing => makeLinearRing(nodeSeq)
        case KmlObjectTypes.Polygon => makePolygon(nodeSeq)
        case KmlObjectTypes.MultiGeometry => makeMultiGeometry(nodeSeq)
        case KmlObjectTypes.Model => makeModel(nodeSeq)
        case KmlObjectTypes.Camera => makeCamera(nodeSeq)
        case KmlObjectTypes.LookAt => makeLookAt(nodeSeq)
        case KmlObjectTypes.Data => makeData(nodeSeq)
        case KmlObjectTypes.SchemaData => makeSchemaData(nodeSeq)
        case KmlObjectTypes.Style => makeStyle(nodeSeq)
        case KmlObjectTypes.StyleMap => makeStyleMap(nodeSeq)
        case KmlObjectTypes.TimeStamp => makeTimeStamp(nodeSeq)
        case KmlObjectTypes.TimeSpan => makeTimeSpan(nodeSeq)
        case KmlObjectTypes.Region => makeRegion(nodeSeq)
        case KmlObjectTypes.LatLonAltBox => makeLatLonAltBox(nodeSeq)
        case KmlObjectTypes.LatLonBox => makeLatLonBox(nodeSeq)
        case KmlObjectTypes.Lod => makeCamera(nodeSeq)
        case KmlObjectTypes.Icon => makeIcon(nodeSeq)
        case KmlObjectTypes.Link => makeLinkFromNode(nodeSeq)
        case KmlObjectTypes.Location => makeLocation(nodeSeq)
        case KmlObjectTypes.Orientation => makeOrientation(nodeSeq)
        case KmlObjectTypes.Scale => makeScale(nodeSeq)
        case _ => None
      }
    }
  }

  def makeOrientation(nodeSeq: NodeSeq): Option[Orientation] = {
    if (nodeSeq.isEmpty) None
    else Some(new Orientation(
      id = getString(nodeSeq \ "@id"),
      targetId = getString(nodeSeq \ "@targetId"),
      heading = getDouble(nodeSeq \ "heading"),
      tilt = getDouble(nodeSeq \ "tilt"),
      roll = getDouble(nodeSeq \ "roll")))
  }

  type ModeType[A] = {def fromString(value: String): A}

  /**
   * create a Mode object such as AltitudeMode, ColorMode, GridOrigin, ItemIconState, RefreshMode etc..
   * @param nodeSeq the node sequence to create the Mode from
   * @param mode the Mode object to create
   * @tparam A the Mode type
   * @return an option of Mode of type A object
   */
  def makeMode[A](nodeSeq: NodeSeq, mode: ModeType[A]): Option[A] = {
    (getString(nodeSeq)).map(mode.fromString(_))
  }

  def makeLinkFromNode(nodeSeq: NodeSeq): Option[com.scalakml.kml.Link] = {
    if (nodeSeq.isEmpty) None
    else Some(new com.scalakml.kml.Link(id = getString(nodeSeq \ "@id"),
      targetId = getString(nodeSeq \ "@targetId"),
      refreshMode = makeMode[RefreshMode](nodeSeq \ "refreshMode", RefreshMode),
      refreshInterval = getDouble(nodeSeq \ "refreshInterval"),
      viewRefreshMode = makeMode[ViewRefreshMode](nodeSeq \ "viewRefreshMode", ViewRefreshMode),
      viewRefreshTime = getDouble(nodeSeq \ "viewRefreshTime"),
      viewBoundScale = getDouble(nodeSeq \ "viewBoundScale"),
      viewFormat = getString(nodeSeq \ "viewFormat"),
      httpQuery = getString(nodeSeq \ "httpQuery"),
      href = getString(nodeSeq \ "href")))
  }

  def makeLink(nodeSeq: NodeSeq): Option[com.scalakml.kml.Link] = {
    if (nodeSeq.isEmpty) None
    else {
      for (x <- List("Link", "Url")) {
        // <--- possible labels, "Url" is not part of the formal reference
        val link = makeLinkFromNode(nodeSeq \ x)
        if (link.isDefined) return link
      }
    }
    None
  }

  def makeIcon(nodeSeq: NodeSeq): Option[Icon] = {
    if (nodeSeq.isEmpty) None
    else Some(new Icon(href = getString(nodeSeq \ "href"),
      id = getString(nodeSeq \ "@id"),
      targetId = getString(nodeSeq \ "@targetId"),
      refreshMode = makeMode[RefreshMode](nodeSeq \ "refreshMode", RefreshMode),
      refreshInterval = getDouble(nodeSeq \ "refreshInterval"),
      viewRefreshMode = makeMode[ViewRefreshMode](nodeSeq \ "viewRefreshMode", ViewRefreshMode),
      viewRefreshTime = getDouble(nodeSeq \ "viewRefreshTime"),
      viewBoundScale = getDouble(nodeSeq \ "viewBoundScale"),
      viewFormat = getString(nodeSeq \ "viewFormat"),
      httpQuery = getString(nodeSeq \ "httpQuery")))
  }

  def makeNetworkLink(nodeSeq: NodeSeq): Option[NetworkLink] = {
    if (nodeSeq.isEmpty) None
    else Some(new NetworkLink(id = getString(nodeSeq \ "@id"),
      targetId = getString(nodeSeq \ "@targetId"),
      refreshVisibility = getBoolean(nodeSeq \ "refreshVisibility"),
      flyToView = getBoolean(nodeSeq \ "flyToView"),
      link = makeLink(nodeSeq),
      featurePart = makeFeaturePart(nodeSeq)))
  }

  def getFromNode[A: TypeTag](nodeSeq: NodeSeq): Option[A] = {
    if (nodeSeq.isEmpty) None
    else {
      val node = nodeSeq.text.trim
      if (node.isEmpty) None
      else {
        typeOf[A] match {
          case x if x == typeOf[String] => Some(node).asInstanceOf[Option[A]]
          case x if x == typeOf[Double] => try {
            Some(node.toDouble).asInstanceOf[Option[A]]
          } catch {
            case _: Throwable => None
          }
          case x if x == typeOf[Int] => try {
            Some(node.toInt).asInstanceOf[Option[A]]
          } catch {
            case _: Throwable => None
          }
          // TODO is this correct use of 1 and 0 or should it be true and false
          case x if x == typeOf[Boolean] => node.toLowerCase match {
            case "1" | "true" => Some(true).asInstanceOf[Option[A]]
            case "0" | "false" => Some(false).asInstanceOf[Option[A]]
            case _ => None
          }
          case _ => None
        }
      }
    }
  }

  def getString(nodeSeq: NodeSeq): Option[String] = {
    getFromNode[String](nodeSeq)
  }

  def getBoolean(nodeSeq: NodeSeq): Option[Boolean] = {
    getFromNode[Boolean](nodeSeq)
  }

  def getDouble(nodeSeq: NodeSeq): Option[Double] = {
    getFromNode[Double](nodeSeq)
  }

  def getInt(nodeSeq: NodeSeq): Option[Int] = {
    getFromNode[Int](nodeSeq)
  }

  def makeAtomLink(nodeSeq: NodeSeq): Option[com.scalakml.atom.Link] = {
    if (nodeSeq.isEmpty) None
    else Some(new com.scalakml.atom.Link(
      href = getString(nodeSeq \ "@href"),
      rel = getString(nodeSeq \ "@rel"),
      typeValue = getString(nodeSeq \ "@type"),
      hrefLang = getString(nodeSeq \ "@hrefLang"),
      title = getString(nodeSeq \ "@title"),
      length = getString(nodeSeq \ "@length")))
  }

  def makeAtomAuthor(nodeSeq: NodeSeq): Option[com.scalakml.atom.Author] = {
    if (nodeSeq.isEmpty) None
    else Some(new com.scalakml.atom.Author(
      name = getString(nodeSeq \ "name"),
      uri = getString(nodeSeq \ "uri"),
      email = getString(nodeSeq \ "email")))
  }

  def makeFeaturePart(nodeSeq: NodeSeq): FeaturePart =
    if (nodeSeq.isEmpty) new FeaturePart()
    else new FeaturePart(
      name = getString(nodeSeq \ "name"),
      visibility = getBoolean(nodeSeq \ "visibility"),
      open = getBoolean(nodeSeq \ "open"),
      atomAuthor = makeAtomAuthor(nodeSeq \ "author"),
      atomLink = makeAtomLink(nodeSeq \ "link"),
      address = getString(nodeSeq \ "address"),
      addressDetails = makeAddressDetails(nodeSeq \ "AddressDetails"), // <---- from com.scalaxal.io.XalFromXml
      phoneNumber = getString(nodeSeq \ "phoneNumber"),
      extendedData = makeExtendedData(nodeSeq \ "ExtendedData"),
      description = getString(nodeSeq \ "description"),
      snippet = makeSnippet(nodeSeq),
      abstractView = makeAbstractView(nodeSeq),
      timePrimitive = makeTimePrimitive(nodeSeq),
      styleUrl = getString(nodeSeq \ "styleUrl"),
      styleSelector = makeStyleSet(nodeSeq),
      region = makeRegion(nodeSeq \ "Region"))

  def makeStyle(nodeSeq: NodeSeq): Option[StyleSelector] = {
    if (nodeSeq.isEmpty) None
    else Some(new Style(
      id = getString(nodeSeq \ "@id"), targetId = getString(nodeSeq \ "@targetId"),
      iconStyle = makeIconStyle(nodeSeq \ "IconStyle"),
      labelStyle = makeLabelStyle(nodeSeq \ "LabelStyle"),
      lineStyle = makeLineStyle(nodeSeq \ "LineStyle"),
      polyStyle = makePolyStyle(nodeSeq \ "PolyStyle"),
      balloonStyle = makeBalloonStyle(nodeSeq \ "BalloonStyle"),
      listStyle = makeListStyle(nodeSeq \ "ListStyle")))
  }

  def makeIconStyle(nodeSeq: NodeSeq): Option[IconStyle] = {
    if (nodeSeq.isEmpty) None
    else Some(new IconStyle(
      id = getString(nodeSeq \ "@id"), targetId = getString(nodeSeq \ "@targetId"),
      scale = getDouble(nodeSeq \ "scale"),
      heading = getDouble(nodeSeq \ "heading"),
      icon = makeIcon(nodeSeq \ "Icon"),
      hotSpot = makeVec2(nodeSeq \ "hotSpot"),
      color = makeColor(nodeSeq \ "color"),
      colorMode = makeMode[ColorMode](nodeSeq \ "colorMode", ColorMode)))
  }

  def makeLineStyle(nodeSeq: NodeSeq): Option[LineStyle] = {
    if (nodeSeq.isEmpty) None
    else Some(new LineStyle(
      id = getString(nodeSeq \ "@id"), targetId = getString(nodeSeq \ "@targetId"),
      width = getDouble(nodeSeq \ "width"),
      color = makeColor(nodeSeq \ "color"),
      colorMode = makeMode[ColorMode](nodeSeq \ "colorMode", ColorMode)))
  }

  def makeLabelStyle(nodeSeq: NodeSeq): Option[LabelStyle] = {
    if (nodeSeq.isEmpty) None
    else Some(new LabelStyle(
      id = getString(nodeSeq \ "@id"), targetId = getString(nodeSeq \ "@targetId"),
      scale = getDouble(nodeSeq \ "scale"),
      color = makeColor(nodeSeq \ "color"),
      colorMode = makeMode[ColorMode](nodeSeq \ "colorMode", ColorMode)))
  }

  def makePolyStyle(nodeSeq: NodeSeq): Option[PolyStyle] = {
    if (nodeSeq.isEmpty) None
    else Some(new PolyStyle(
      id = getString(nodeSeq \ "@id"), targetId = getString(nodeSeq \ "@targetId"),
      fill = getBoolean(nodeSeq \ "fill"),
      outline = getBoolean(nodeSeq \ "outline"),
      color = makeColor(nodeSeq \ "color"),
      colorMode = makeMode[ColorMode](nodeSeq \ "colorMode", ColorMode)))
  }

  def makeBalloonStyle(nodeSeq: NodeSeq): Option[BalloonStyle] = {
    if (nodeSeq.isEmpty) None
    else Some(new BalloonStyle(
      id = getString(nodeSeq \ "@id"), targetId = getString(nodeSeq \ "@targetId"),
      bgColor = makeColor(nodeSeq \ "bgColor"),
      textColor = makeColor(nodeSeq \ "textColor"),
      text = getString(nodeSeq \ "text"),
      displayMode = makeMode[DisplayMode](nodeSeq \ "displayMode", DisplayMode)))
  }

  def makeListStyle(nodeSeq: NodeSeq): Option[ListStyle] = {
    if (nodeSeq.isEmpty) None
    else Some(new ListStyle(
      id = getString(nodeSeq \ "@id"), targetId = getString(nodeSeq \ "@targetId"),
      listItemType = makeMode[ListItemType](nodeSeq \ "listItemType", ListItemType),
      bgColor = makeColor(nodeSeq \ "bgColor"),
      itemIcon = makeItemIconSet(nodeSeq \ "ItemIcon"),
      maxSnippetLines = getInt(nodeSeq \ "maxSnippetLines")))
  }

  def makeItemIconSet(nodeSeq: NodeSeq): Seq[ItemIcon] = {
    if (nodeSeq.isEmpty) Seq.empty
    else (nodeSeq collect {
      case x => makeItemIcon(x)
    } flatten)
  }

  def makeItemIcon(nodeSeq: NodeSeq): Option[ItemIcon] = {
    if (nodeSeq.isEmpty) None
    else {
      val test =Some(new ItemIcon(
        id = getString(nodeSeq \ "@id"), targetId = getString(nodeSeq \ "@targetId"),
        state = makeItemIconStates(nodeSeq \ "state"),
        href = getString(nodeSeq \ "href")))
      test
    }
  }

  def makeItemIconStates(nodeSeq: NodeSeq): Seq[ItemIconState] = {
    if (nodeSeq.isEmpty) Seq.empty
    else {
      getString(nodeSeq) match {
        case Some(modeOption) =>
          modeOption match {
            case x if (x.isEmpty) => Seq.empty
            case stateString => (stateString split "\\s+").map(x => {ItemIconState.fromString(x.trim)}).toSeq
          }
        case _ => Seq.empty
      }
    }
  }

  def makeStyleMap(nodeSeq: NodeSeq): Option[StyleSelector] = {
    if (nodeSeq.isEmpty) None
    else Some(new StyleMap(
      id = getString(nodeSeq \ "@id"), targetId = getString(nodeSeq \ "@targetId"),
      pair = makePairSet(nodeSeq \ "Pair")))
  }

  // TODO looks like a circular reference
  def makePair(nodeSeq: NodeSeq): Option[Pair] = {
    if (nodeSeq.isEmpty) None
    else Some(new Pair(
      id = getString(nodeSeq \ "@id"), targetId = getString(nodeSeq \ "@targetId"),
      key = makeMode[StyleState](nodeSeq \ "key", StyleState),
      styleUrl = getString(nodeSeq \ "styleUrl"),
      styleSelector = makeStyleSelector(nodeSeq))) // <----
  }

  def makePairSet(nodeSeq: NodeSeq): Seq[Pair] = {
    if (nodeSeq.isEmpty) Seq.empty
    else (nodeSeq collect {
      case x => makePair(x)
    } flatten)
  }

  /**
   * Creates a Style or StyleMap from the given NodeSeq
   *
   * @param nodeSeq the node sequence to create the Style or StyleMap from
   * @return a StyleSelector, that is a Style or StyleMap Option
   */
  def makeStyleSelector(nodeSeq: NodeSeq): Option[StyleSelector] = {
    if (nodeSeq.isEmpty) None
    else {
      val style = (nodeSeq \ "Style")
      style match {
        case x if (!x.isEmpty) => makeStyle(style)
        case _ => {
          val styleMap = (nodeSeq \ "StyleMap")
          styleMap match {
            case x if (!x.isEmpty) => makeStyleMap(styleMap)
            case _ => None
          }
        }
      }
    }
  }

  def makeStyleSet(nodeSeq: NodeSeq): Seq[StyleSelector] = {
    if (nodeSeq.isEmpty) Seq.empty
    else {
      val styleList = (nodeSeq \ "Style") match {
        case s if (!s.isEmpty) => (s collect {
          case x => makeStyle(x)
        } flatten)
        case _ => Seq.empty
      }
      val styleMapList = (nodeSeq \ "StyleMap") match {
        case s if (!s.isEmpty) => (s collect {
          case x => makeStyleMap(x)
        } flatten)
        case _ => Seq.empty
      }
      (styleList ++ styleMapList)
    }
  }

  def makeLod(nodeSeq: NodeSeq): Option[Lod] = {
    if (nodeSeq.isEmpty) None
    else Some(new Lod(
      id = getString(nodeSeq \ "@id"), targetId = getString(nodeSeq \ "@targetId"),
      minLodPixels = getDouble(nodeSeq \ "minLodPixels"),
      maxLodPixels = getDouble(nodeSeq \ "maxLodPixels"),
      minFadeExtent = getDouble(nodeSeq \ "minFadeExtent"),
      maxFadeExtent = getDouble(nodeSeq \ "maxFadeExtent")))
  }

  def makeRegion(nodeSeq: NodeSeq): Option[Region] = {
    if (nodeSeq.isEmpty) None
    else Some(new Region(
      id = getString(nodeSeq \ "@id"), targetId = getString(nodeSeq \ "@targetId"),
      latLonAltBox = makeLatLonAltBox(nodeSeq \ "LatLonAltBox"),
      lod = makeLod(nodeSeq \ "Lod")))
  }

  def makeLatLonAltBox(nodeSeq: NodeSeq): Option[LatLonAltBox] = {
    if (nodeSeq.isEmpty) None
    else Some(new LatLonAltBox(
      id = getString(nodeSeq \ "@id"), targetId = getString(nodeSeq \ "@targetId"),
      minAltitude = getDouble(nodeSeq \ "minAltitude"),
      maxAltitude = getDouble(nodeSeq \ "maxAltitude"),
      altitudeMode = makeMode[AltitudeMode](nodeSeq \ "altitudeMode", AltitudeMode),
      north = getDouble(nodeSeq \ "north"),
      south = getDouble(nodeSeq \ "south"),
      east = getDouble(nodeSeq \ "east"),
      west = getDouble(nodeSeq \ "west")))
  }

  def makeLatLonBox(nodeSeq: NodeSeq): Option[LatLonBox] = {
    if (nodeSeq.isEmpty) None
    else Some(new LatLonBox(
        id = getString(nodeSeq \ "@id"), targetId = getString(nodeSeq \ "@targetId"),
        rotation = getDouble(nodeSeq \ "rotation"),
        north = getDouble(nodeSeq \ "north"),
        south = getDouble(nodeSeq \ "south"),
        east = getDouble(nodeSeq \ "east"),
        west = getDouble(nodeSeq \ "west")))
  }

  // makeCoordinates

  /**
   * Creates a snippet from the given snippet NodeSeq
   *
   * @param nodeSeq the snippet node sequence
   * @return a Snippet Option
   */
  def makeSnippetFromNode(nodeSeq: NodeSeq): Option[Snippet] = {
    if (nodeSeq.isEmpty) None
    else Some(new Snippet(
      value = getString(nodeSeq).getOrElse(""),
      maxLines = getInt(nodeSeq \ "@maxLines").getOrElse(0)))
  }

  /**
   * Creates a snippet from the given parent NodeSeq
   *
   * @param nodeSeq the parent nodeSeq of snippet or Snippet
   * @return a Snippet Option
   */
  def makeSnippet(nodeSeq: NodeSeq): Option[Snippet] = {
    // pick the first node with something in it
    for (x <- List("snippet", "Snippet")) {
      // reference is snippet, but lots of Snippet around
      val node = (nodeSeq \ x)
      if (!node.isEmpty) return makeSnippetFromNode(node)
    }
    None
  }

  // TODO how to read other
  def makeExtendedData(nodeSeq: NodeSeq): Option[ExtendedData] = {
    if (nodeSeq.isEmpty) None
    else Some(new ExtendedData(
      data = makeDataSet(nodeSeq \ "Data"),
      schemaData = makeSchemaDataSet(nodeSeq \ "SchemaData")))
    // other: Seq[Any]     how to get this from xml
  }

  def makeData(nodeSeq: NodeSeq): Option[Data] = {
    if (nodeSeq.isEmpty) None
    else Some(new Data(name = getString(nodeSeq \ "@name"),
      id = getString(nodeSeq \ "@id"), targetId = getString(nodeSeq \ "@targetId"),
      displayName = getString(nodeSeq \ "displayName"),
      value = getString(nodeSeq \ "value")))
  }

  def makeDataSet(nodeSeq: NodeSeq): Seq[Data] = {
    if (nodeSeq.isEmpty) Seq.empty
    else (nodeSeq collect {
      case x => makeData(x)
    } flatten)
  }

  def makeSimpleData(nodeSeq: NodeSeq): Option[SimpleData] = {
    if (nodeSeq.isEmpty) None
    else Some(new SimpleData(name = getString(nodeSeq \ "@name"),
      value = getString(nodeSeq)))
  }

  def makeSimpleDataSet(nodeSeq: NodeSeq): Seq[SimpleData] = {
    if (nodeSeq.isEmpty) Seq.empty
    else (nodeSeq collect {
      case x => makeSimpleData(x)
    } flatten)
  }

  def makeSchemaData(nodeSeq: NodeSeq): Option[SchemaData] = {
    if (nodeSeq.isEmpty) None
    else Some(new SchemaData(
      id = getString(nodeSeq \ "@id"), targetId = getString(nodeSeq \ "@targetId"),
      schemaUrl = getString(nodeSeq \ "@schemaUrl"),
      simpleData = makeSimpleDataSet(nodeSeq \ "SimpleData")))
  }

  def makeSchemaDataSet(nodeSeq: NodeSeq): Seq[SchemaData] = {
    if (nodeSeq.isEmpty) Seq.empty
    else (nodeSeq collect {
      case x => makeSchemaData(x)
    } flatten)
  }

  // either a TimeSpan or a TimeStamp or None
  def makeTimePrimitive(nodeSeq: NodeSeq): Option[TimePrimitive] = {
    if (nodeSeq.isEmpty) None
    else
      (nodeSeq \ "TimeSpan") match {
        case x if (!x.isEmpty) => makeTimeSpan(x)
        case _ => {
          (nodeSeq \ "TimeStamp") match {
            case x if (!x.isEmpty) => makeTimeStamp(x)
            case _ => None
          }
        }
      }
  }

  def makeTimeStamp(nodeSeq: NodeSeq): Option[TimePrimitive] = {
    if (nodeSeq.isEmpty) None
    else Some(new TimeStamp(
      id = getString(nodeSeq \ "@id"), targetId = getString(nodeSeq \ "@targetId"),
      when = getString(nodeSeq \ "when")))
  }

  def makeTimeSpan(nodeSeq: NodeSeq): Option[TimePrimitive] = {
    if (nodeSeq.isEmpty) None
    else Some(new TimeSpan(
      id = getString(nodeSeq \ "@id"), targetId = getString(nodeSeq \ "@targetId"),
      begin = getString(nodeSeq \ "begin"),
      end = getString(nodeSeq \ "end")))
  }

  // either a Camera or a LookAt or None
  def makeAbstractView(nodeSeq: NodeSeq): Option[AbstractView] = {
    if (nodeSeq.isEmpty) None
    else
      (nodeSeq \ "Camera") match {
        case x if (!x.isEmpty) => makeCamera(x)
        case _ => {
          (nodeSeq \ "LookAt") match {
            case x if (!x.isEmpty) => makeLookAt(x)
            case _ => None
          }
        }
      }
  }

  def makeCamera(nodeSeq: NodeSeq): Option[Camera] = {
    if (nodeSeq.isEmpty) None
    else Some(new Camera(
      id = getString(nodeSeq \ "@id"), targetId = getString(nodeSeq \ "@targetId"),
      roll = getDouble(nodeSeq \ "roll"),
      longitude = getDouble(nodeSeq \ "longitude"),
      latitude = getDouble(nodeSeq \ "latitude"),
      altitude = getDouble(nodeSeq \ "altitude"),
      heading = getDouble(nodeSeq \ "heading"),
      tilt = getDouble(nodeSeq \ "tilt"),
      altitudeMode = makeMode[AltitudeMode](nodeSeq \ "altitudeMode", AltitudeMode)))
  }

  def makeLookAt(nodeSeq: NodeSeq): Option[LookAt] = {
    if (nodeSeq.isEmpty) None
    else Some(new LookAt(
      id = getString(nodeSeq \ "@id"), targetId = getString(nodeSeq \ "@targetId"),
      range = getDouble(nodeSeq \ "range"),
      longitude = getDouble(nodeSeq \ "longitude"),
      latitude = getDouble(nodeSeq \ "latitude"),
      altitude = getDouble(nodeSeq \ "altitude"),
      heading = getDouble(nodeSeq \ "heading"),
      tilt = getDouble(nodeSeq \ "tilt"),
      altitudeMode = makeMode[AltitudeMode](nodeSeq \ "altitudeMode", AltitudeMode)))
  }

  def makePlacemark(nodeSeq: NodeSeq): Option[Placemark] = {
    if (nodeSeq.isEmpty) None
    else Some(new Placemark(
      id = getString(nodeSeq \ "@id"), targetId = getString(nodeSeq \ "@targetId"),
      geometry = makeGeometry(nodeSeq),
      featurePart = makeFeaturePart(nodeSeq)))
  }

  def makeDocument(nodeSeq: NodeSeq): Option[com.scalakml.kml.Document] = {
    if (nodeSeq.isEmpty) None
    else Some(new com.scalakml.kml.Document(
      id = getString(nodeSeq \ "@id"), targetId = getString(nodeSeq \ "@targetId"),
      schemas = makeSchemaSet(nodeSeq \ "Schema"),
      features = makeFeatureSet(nodeSeq),
      featurePart = makeFeaturePart(nodeSeq)))
  }

  def makeSchemaSet(nodeSeq: NodeSeq): Seq[Schema] = {
    if (nodeSeq.isEmpty) Seq.empty
    else (nodeSeq collect {
      case x => makeSchema(x)
    } flatten)
  }

  def makeSchema(nodeSeq: NodeSeq): Option[Schema] = {
    if (nodeSeq.isEmpty) None
    else Some(new Schema(
      simpleField = makeSimpleFieldSet(nodeSeq \ "SimpleField"),
      name = getString(nodeSeq \ "@name"),
      id = getString(nodeSeq \ "@id")))
  }

  def makeSimpleField(nodeSeq: NodeSeq): Option[SimpleField] = {
    if (nodeSeq.isEmpty) None
    else Some(new SimpleField(
      name = getString(nodeSeq \ "@name"),
      typeValue = getString(nodeSeq \ "@type"),
      displayName = getString(nodeSeq \ "displayName")))
  }

  def makeSimpleFieldSet(nodeSeq: NodeSeq): Seq[SimpleField] = {
    if (nodeSeq.isEmpty) Seq.empty
    else (nodeSeq collect {
      case x => makeSimpleField(x)
    } flatten)
  }

  def makeFolder(nodeSeq: NodeSeq): Option[Folder] = {
    if (nodeSeq.isEmpty) None
    else Some(new Folder(
        id = getString(nodeSeq \ "@id"), targetId = getString(nodeSeq \ "@targetId"),
        features = makeFeatureSet(nodeSeq),
        featurePart = makeFeaturePart(nodeSeq)))
  }

  def makePoint(nodeSeq: NodeSeq): Option[Point] = {
    if (nodeSeq.isEmpty) None
    else Some(new Point(
      id = getString(nodeSeq \ "@id"), targetId = getString(nodeSeq \ "@targetId"),
      coordinates = makeCoordinates(nodeSeq \ "coordinates"),
      extrude = getBoolean(nodeSeq \ "extrude"),
      altitudeMode = makeMode[AltitudeMode](nodeSeq \ "altitudeMode", AltitudeMode)))
  }

  def makeCoordinates(nodeSeq: NodeSeq): Option[Seq[Location]] = {
    if (nodeSeq.isEmpty) None
    else
      Some((nodeSeq.text.trim split "\\s+").map(x => com.scalakml.kml.Location.fromCsString(x)) flatMap (x => x) toSeq)
  }

  def makeLocation(nodeSeq: NodeSeq): Option[Location] = {
    if (nodeSeq.isEmpty) None
    else Some(new Location(
      id = getString(nodeSeq \ "@id"), targetId = getString(nodeSeq \ "@targetId"),
      longitude = getDouble(nodeSeq \ "longitude"),
      latitude = getDouble(nodeSeq \ "latitude"),
      altitude = getDouble(nodeSeq \ "altitude")))
  }

  def makeLineString(nodeSeq: NodeSeq): Option[LineString] = {
    if (nodeSeq.isEmpty) None
    else Some(new LineString(
      id = getString(nodeSeq \ "@id"), targetId = getString(nodeSeq \ "@targetId"),
      extrude = getBoolean(nodeSeq \ "extrude"),
      tessellate = getBoolean(nodeSeq \ "tessellate"),
      altitudeMode = makeMode[AltitudeMode](nodeSeq \ "altitudeMode", AltitudeMode),
      coordinates = makeCoordinates(nodeSeq \ "coordinates")))
  }

  def makeLinearRing(nodeSeq: NodeSeq): Option[LinearRing] = {
    if (nodeSeq.isEmpty) None
    else Some(new LinearRing(
      id = getString(nodeSeq \ "@id"), targetId = getString(nodeSeq \ "@targetId"),
      extrude = getBoolean(nodeSeq \ "extrude"),
      tessellate = getBoolean(nodeSeq \ "tessellate"),
      altitudeMode = makeMode[AltitudeMode](nodeSeq \ "altitudeMode", AltitudeMode),
      coordinates = makeCoordinates(nodeSeq \ "coordinates")))
  }

  def makeBoundary(nodeSeq: NodeSeq): Option[Boundary] = {
    if (nodeSeq.isEmpty) None else Some(new Boundary(linearRing = makeLinearRing(nodeSeq \ "LinearRing")))
  }

  def makeBoundaries(nodeSeq: NodeSeq): Seq[Boundary] = {
    if (nodeSeq.isEmpty) Seq.empty
    else (nodeSeq collect {
      case x => makeBoundary(x)
    } flatten)
  }

  def makePolygon(nodeSeq: NodeSeq): Option[Polygon] = {
    if (nodeSeq.isEmpty) None
    else Some(new Polygon(
      id = getString(nodeSeq \ "@id"), targetId = getString(nodeSeq \ "@targetId"),
      extrude = getBoolean(nodeSeq \ "extrude"),
      tessellate = getBoolean(nodeSeq \ "tessellate"),
      altitudeMode = makeMode[AltitudeMode](nodeSeq \ "altitudeMode", AltitudeMode),
      outerBoundaryIs = makeBoundary(nodeSeq \ "outerBoundaryIs"),
      innerBoundaryIs = makeBoundaries(nodeSeq \ "innerBoundaryIs")))
  }

  def makeMultiGeometry(nodeSeq: NodeSeq): Option[MultiGeometry] = {
    if (nodeSeq.isEmpty) None
    else Some(new MultiGeometry(
      id = getString(nodeSeq \ "@id"), targetId = getString(nodeSeq \ "@targetId"),
      geometries = makeGeometrySet(nodeSeq)))
  }

  def makeModel(nodeSeq: NodeSeq): Option[Model] = {
    if (nodeSeq.isEmpty) None
    else Some(new Model(
      id = getString(nodeSeq \ "@id"), targetId = getString(nodeSeq \ "@targetId"),
      altitudeMode = makeMode[AltitudeMode](nodeSeq \ "altitudeMode", AltitudeMode),
      location = makeLocation(nodeSeq \ "Location"),
      scale = makeScale(nodeSeq \ "Scale"),
      link = makeLink(nodeSeq),
      resourceMap = makeResourceMap(nodeSeq \ "ResourceMap")))
  }

  def makeResourceMap(nodeSeq: NodeSeq): Option[ResourceMap] = {
    if (nodeSeq.isEmpty) None
    else Some(new ResourceMap(
      id = getString(nodeSeq \ "@id"), targetId = getString(nodeSeq \ "@targetId"),
      alias = makeAliasSet(nodeSeq \ "Alias")))
  }

  def makeAliasSet(nodeSeq: NodeSeq): Seq[Alias] = {
    if (nodeSeq.isEmpty) Seq.empty
    else (nodeSeq collect {
      case x => makeAlias(x)
    } flatten)
  }

  def makeAlias(nodeSeq: NodeSeq): Option[Alias] = {
    if (nodeSeq.isEmpty) None
    else Some(new Alias(
      id = getString(nodeSeq \ "@id"), targetId = getString(nodeSeq \ "@targetId"),
      targetHref = getString(nodeSeq \ "targetHref"),
      sourceHref = getString(nodeSeq \ "sourceHref")))
  }

  def makeScale(nodeSeq: NodeSeq): Option[Scale] = {
    if (nodeSeq.isEmpty) None
    else Some(new Scale(
      id = getString(nodeSeq \ "@id"), targetId = getString(nodeSeq \ "@targetId"),
      x = getDouble(nodeSeq \ "x"),
      y = getDouble(nodeSeq \ "y"),
      z = getDouble(nodeSeq \ "z")))
  }

  def makeGeometry(nodeSeq: NodeSeq, geomType: GeometryTypes): Option[Geometry] = {
    if (nodeSeq.isEmpty) None
    else {
      geomType match {
        case GeometryTypes.Point => makePoint(nodeSeq)
        case GeometryTypes.LineString => makeLineString(nodeSeq)
        case GeometryTypes.LinearRing => makeLinearRing(nodeSeq)
        case GeometryTypes.Polygon => makePolygon(nodeSeq)
        case GeometryTypes.MultiGeometry => makeMultiGeometry(nodeSeq)
        case GeometryTypes.Model => makeModel(nodeSeq)
        //      case GeometryTypes.Track => makeTrack(nodeSeq)
        //      case GeometryTypes.MultiTrack => makeMultiTrack(nodeSeq)
        case _ => None
      }
    }
  }

  def makeGeometry(nodeSeq: NodeSeq): Option[Geometry] = {
    if (nodeSeq.isEmpty) None
    else {
      // just pick the first match
      for (x <- GeometryTypes.values) {
        val geom = makeGeometry(nodeSeq \ x.toString, x)
        if (geom.isDefined) return geom
      }
    }
    None
  }

  def makeGeometrySet(nodeSeq: NodeSeq): Seq[Geometry] = {
    if (nodeSeq.isEmpty) Seq.empty
    else
      (GeometryTypes.values.flatMap(x => makeGeometries(nodeSeq \ x.toString, x)).toSeq.flatten)
  }

  def makeGeometries(nodeSeq: NodeSeq, geometryTypes: GeometryTypes): Seq[Option[Geometry]] = {
    if (nodeSeq.isEmpty) Seq.empty
    else
      (nodeSeq collect {
        case x => makeGeometry(x, geometryTypes)
      }) filter (_ != None)
  }

  def makeViewVolume(nodeSeq: NodeSeq): Option[ViewVolume] = {
    if (nodeSeq.isEmpty) None
    else
      Some(new ViewVolume(id = getString(nodeSeq \ "@id"), targetId = getString(nodeSeq \ "@targetId"),
        leftFov = getDouble(nodeSeq \ "leftFov"),
        rightFov = getDouble(nodeSeq \ "rightFov"),
        bottomFov = getDouble(nodeSeq \ "bottomFov"),
        topFov = getDouble(nodeSeq \ "topFov"),
        near = getDouble(nodeSeq \ "near")))
  }

  def makeImagePyramid(nodeSeq: NodeSeq): Option[ImagePyramid] = {
    if (nodeSeq.isEmpty) None
    else
      Some(new ImagePyramid(id = getString(nodeSeq \ "@id"), targetId = getString(nodeSeq \ "@targetId"),
        tileSize = getInt(nodeSeq \ "tileSize"),
        maxWidth = getInt(nodeSeq \ "maxWidth"),
        maxHeight = getInt(nodeSeq \ "maxHeight"),
        gridOrigin = makeMode[GridOrigin](nodeSeq \ "gridOrigin", GridOrigin)))
  }

  def makeColor(nodeSeq: NodeSeq): Option[HexColor] = {
    if (nodeSeq.isEmpty) None else (getString(nodeSeq)).map(x => new HexColor(hexString = x))
  }

  def makePhotoOverlay(nodeSeq: NodeSeq): Option[PhotoOverlay] = {
    if (nodeSeq.isEmpty) None
    else
      Some(new PhotoOverlay(id = getString(nodeSeq \ "@id"), targetId = getString(nodeSeq \ "@targetId"),
        rotation = getDouble(nodeSeq \ "rotation"),
        viewVolume = makeViewVolume(nodeSeq \ "ViewVolume"),
        imagePyramid = makeImagePyramid(nodeSeq \ "ImagePyramid"),
        point = makePoint(nodeSeq \ "Point"),
        shape = makeMode[Shape](nodeSeq \ "shape", Shape),
        color = makeColor(nodeSeq \ "color"),
        drawOrder = getInt(nodeSeq \ "drawOrder"),
        icon = makeIcon(nodeSeq \ "Icon"),
        featurePart = makeFeaturePart(nodeSeq)))
  }

  def makeGroundOverlay(nodeSeq: NodeSeq): Option[GroundOverlay] = {
    if (nodeSeq.isEmpty) None
    else
      Some(new GroundOverlay(id = getString(nodeSeq \ "@id"), targetId = getString(nodeSeq \ "@targetId"),
        altitude = getDouble(nodeSeq \ "altitude"),
        altitudeMode = makeMode[AltitudeMode](nodeSeq \ "altitudeMode", AltitudeMode),
        latLonBox = makeLatLonBox(nodeSeq \ "LatLonBox"),
        latLonQuad = makeLatLonQuad(nodeSeq \ "LatLonQuad"),
        color = makeColor(nodeSeq \ "color"),
        drawOrder = getInt(nodeSeq \ "drawOrder"),
        icon = makeIcon(nodeSeq \ "Icon"),
        featurePart = makeFeaturePart(nodeSeq)))
  }

  def makeVec2(nodeSeq: NodeSeq): Option[Vec2] = {
    if (nodeSeq.isEmpty) None
    else Some(new Vec2(
      x = getDouble(nodeSeq \ "@x").getOrElse(0.0),
      y = getDouble(nodeSeq \ "@y").getOrElse(0.0),
      xunits = makeMode[Units](nodeSeq \ "@xunits", Units).getOrElse(Fraction),
      yunits = makeMode[Units](nodeSeq \ "@yunits", Units).getOrElse(Fraction)))
  }

  def makeScreenOverlay(nodeSeq: NodeSeq): Option[ScreenOverlay] = {
    if (nodeSeq.isEmpty) None
    else Some(new ScreenOverlay(
      id = getString(nodeSeq \ "@id"), targetId = getString(nodeSeq \ "@targetId"),
      overlayXY = makeVec2(nodeSeq \ "overlayXY"),
      screenXY = makeVec2(nodeSeq \ "screenXY"),
      rotationXY = makeVec2(nodeSeq \ "rotationXY"),
      size = makeVec2(nodeSeq \ "size"),
      rotation = getDouble(nodeSeq \ "rotation"),
      color = makeColor(nodeSeq \ "color"),
      drawOrder = getInt(nodeSeq \ "drawOrder"),
      icon = makeIcon(nodeSeq \ "Icon"),
      featurePart = makeFeaturePart(nodeSeq)))
  }

  //-----------------------------------------------------------------------------------------------
  //----------------------------------gx-----------------------------------------------------------
  //-----------------------------------------------------------------------------------------------

  def makePlaylist(nodeSeq: NodeSeq): Option[Playlist] = {
    if (nodeSeq.isEmpty) None
    else Some(new Playlist(
      id = getString(nodeSeq \ "@id"), targetId = getString(nodeSeq \ "@targetId"),
      tourPrimitiveGroup = Some(makeTourPrimitiveSet(nodeSeq))))
  }

  def makeTour(nodeSeq: NodeSeq): Option[Tour] = {
    if (nodeSeq.isEmpty) None
    else Some(new Tour(
      id = getString(nodeSeq \ "@id"), targetId = getString(nodeSeq \ "@targetId"),
      playlist = makePlaylist(nodeSeq \ "Playlist"),
      featurePart = makeFeaturePart(nodeSeq)))
  }

  def makeTourPrimitiveSet(nodeSeq: NodeSeq): Seq[TourPrimitive] = {
    if (nodeSeq.isEmpty) Seq.empty
    else
      (TourPrimitiveTypes.values.flatMap(x => makeTourPrimitives(nodeSeq \ x.toString, x)).toSeq.flatten)
  }

  def makeTourPrimitives(nodeSeq: NodeSeq, tourPrimitiveType: TourPrimitiveTypes): Seq[Option[TourPrimitive]] = {
    (nodeSeq collect {
      case x => makeTourPrimitive(x, tourPrimitiveType)
    }) filter (_ != None)
  }

  def makeTourPrimitive(nodeSeq: NodeSeq, tourPrimitiveType: TourPrimitiveTypes): Option[TourPrimitive] = {
    if (nodeSeq.isEmpty) None
    else {
      tourPrimitiveType match {
        case TourPrimitiveTypes.AnimatedUpdate => makeAnimatedUpdate(nodeSeq)
        case TourPrimitiveTypes.FlyTo => makeFlyTo(nodeSeq)
        case TourPrimitiveTypes.SoundCue => makeSoundCue(nodeSeq)
        case TourPrimitiveTypes.Wait => makeWait(nodeSeq)
        case TourPrimitiveTypes.TourControl => makeTourControl(nodeSeq)
        case _ => None
      }
    }
  }

  def makeAnimatedUpdate(nodeSeq: NodeSeq): Option[AnimatedUpdate] = {
    if (nodeSeq.isEmpty) None
    else Some(new AnimatedUpdate(
      id = getString(nodeSeq \ "@id"), targetId = getString(nodeSeq \ "@targetId"),
      duration = getDouble(nodeSeq \ "duration"),
      update = makeUpdate(nodeSeq \ "Update")))
  }

  def makeFlyTo(nodeSeq: NodeSeq): Option[FlyTo] = {
    if (nodeSeq.isEmpty) None
    else Some(new FlyTo(
      id = getString(nodeSeq \ "@id"), targetId = getString(nodeSeq \ "@targetId"),
      duration = getDouble(nodeSeq \ "duration"),
      flyToMode = makeMode[FlyToMode](nodeSeq \ "flyToMode", FlyToMode),
      abstractView = makeAbstractView(nodeSeq)))
  }

  def makeSoundCue(nodeSeq: NodeSeq): Option[SoundCue] = {
    if (nodeSeq.isEmpty) None
    else Some(new SoundCue(
      id = getString(nodeSeq \ "@id"), targetId = getString(nodeSeq \ "@targetId"),
      href = getString(nodeSeq \ "href")))
  }

  def makeWait(nodeSeq: NodeSeq): Option[Wait] = {
    if (nodeSeq.isEmpty) None
    else Some(new Wait(
      id = getString(nodeSeq \ "@id"), targetId = getString(nodeSeq \ "@targetId"),
      duration = getDouble(nodeSeq \ "duration")))
  }

  def makeTourControl(nodeSeq: NodeSeq): Option[TourControl] = {
    if (nodeSeq.isEmpty) None
    else Some(new TourControl(
      id = getString(nodeSeq \ "@id"), targetId = getString(nodeSeq \ "@targetId"),
      playMode = makeMode[PlayMode](nodeSeq \ "playMode", PlayMode)))
  }

  def makeLatLonQuad(nodeSeq: NodeSeq): Option[LatLonQuad] = {
    if (nodeSeq.isEmpty) None
    else Some(new LatLonQuad(
      id = getString(nodeSeq \ "@id"), targetId = getString(nodeSeq \ "@targetId"),
      coordinates = makeCoordinates(nodeSeq)))
  }

}
