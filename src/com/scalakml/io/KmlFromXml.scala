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
 * Date: 12/12/12
 * Version: 1
 *
 * Reference: OGC 07-147r2 Version: 2.2.0, Category: OGC Standard, Editor: Tim Wilson, at
 * http://www.opengeospatial.org/standards/kml
 * also
 * Google developers KML Reference, at
 * https://developers.google.com/kml/documentation/kmlreference
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
    if (nodeSeq.isEmpty) None else
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
    if (nodeSeq.isEmpty) None else Some(new NetworkLinkControl(
      minRefreshPeriod = getFromNode[Double](nodeSeq \ "minRefreshPeriod"),
      maxSessionLength = getFromNode[Double](nodeSeq \ "maxSessionLength"),
      cookie = getFromNode[String](nodeSeq \ "cookie"),
      message = getFromNode[String](nodeSeq \ "message"),
      linkName = getFromNode[String](nodeSeq \ "linkName"),
      linkDescription = getFromNode[String](nodeSeq \ "linkDescription"),
      linkSnippet = makeSnippetFromNode(nodeSeq \ "linkSnippet"),
      expires = getFromNode[String](nodeSeq \ "expires"),
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
    if (nodeSeq.isEmpty) None else getFromNode[String](nodeSeq \ "@hint")

  /**
   * Creates a "main" feature (for a Kml root element object) from the NodeSeq.
   * Returns the first found non empty Feature from amongst:
   * Document, Folder, Placemark, NetworkLink, PhotoOverlay, ScreenOverlay, GroundOverlay, Tour
   *
   * @param nodeSeq the scala xml NodeSeq, e.g. <kml> ... </kml>
   * @return an object that derives from Feature, one of: Document, Folder, Placemark, NetworkLink, PhotoOverlay, ScreenOverlay, GroundOverlay, Tour
   */
  def makeMainFeature(nodeSeq: NodeSeq): Option[Feature] = {
    if (nodeSeq.isEmpty) None else {
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
    if (nodeSeq.isEmpty) None else
      (getFromNode[String](nodeSeq \ "targetHref")).map(x => new Update(targetHref = x, updateOption = makeUpdateOptions(nodeSeq)))
  }

  // TODO this does not check that there must be only one of the different types
  /**
   * Creates a Update option, one of: Delete, Create or Change from the NodeSeq.
   *
   * @param nodeSeq the scala xml NodeSeq, e.g. <Update> ... </Update>
   * @return an Update option sequence containing: Delete, Create or Change options
   */
  def makeUpdateOptions(nodeSeq: NodeSeq): Seq[UpdateOption] = {
    if (nodeSeq.isEmpty) Seq.empty else {
      val theSeq = UpdateOptionTypes.values collect { case x =>
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

  def makeContainers(nodeSeq: NodeSeq, containerType: ContainerTypes): Seq[Option[Container]] =
    (nodeSeq collect { case x => makeContainer(x, containerType) }) filter (_ != None)

  def makeContainer(nodeSeq: NodeSeq, containerType: ContainerTypes): Option[Container] = {
    if (nodeSeq.isEmpty) None else
      containerType match {
        case ContainerTypes.Document => makeDocument(nodeSeq \ "Document")
        case ContainerTypes.Folder => makeFolder(nodeSeq \ "Folder")
        case _ => None
      }
  }

  def makeContainerSet(nodeSeq: NodeSeq): Seq[Container] = {
    if (nodeSeq.isEmpty) Seq.empty else
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
    if (nodeSeq.isEmpty) Seq.empty else
      ((nodeSeq collect { case x => makeFeature(x, featureType) }) filter (_ != None))
  }

  /**
   * Creates one feature of the given featureType from the given NodeSeq
   *
   * @param nodeSeq the node sequence to create the feature from
   * @return a Feature Option
   */
  def makeFeature(nodeSeq: NodeSeq, featureType: FeatureTypes): Option[Feature] = {
    if (nodeSeq.isEmpty) None else {
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
    if (nodeSeq.isEmpty) Seq.empty else
      (FeatureTypes.values.flatMap(x => makeFeatures(nodeSeq \ x.toString, x)).toSeq.flatten)
  }

  def makeKmlObjectSet(nodeSeq: NodeSeq): Seq[KmlObject] = {
    if (nodeSeq.isEmpty) Seq.empty else
      (KmlObjectTypes.values.flatMap(x => makeKmlObjects(nodeSeq \ x.toString, x)).toSeq.flatten)
  }

  def makeKmlObjects(nodeSeq: NodeSeq, kmlObjectType: KmlObjectTypes): Seq[Option[KmlObject]] = {
    (nodeSeq collect { case x => makeKmlObject(x, kmlObjectType) }) filter (_ != None)
  }

  def makeKmlObject(nodeSeq: NodeSeq, kmlObjectType: KmlObjectTypes): Option[KmlObject] = {
    if (nodeSeq.isEmpty) None else {
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
    if (nodeSeq.isEmpty) None else Some(new Orientation(
      id = getFromNode[String](nodeSeq \ "@id"),
      targetId = getFromNode[String](nodeSeq \ "@targetId"),
      heading = getFromNode[Double](nodeSeq \ "heading"),
      tilt = getFromNode[Double](nodeSeq \ "tilt"),
      roll = getFromNode[Double](nodeSeq \ "roll")))
  }

  type ModeType[A] = { def fromString(value: String): A }

  /**
   * create a Mode object such as AltitudeMode, ColorMode, GridOrigin, ItemIconState, RefreshMode etc..
   * @param nodeSeq the node sequence to create the Mode from
   * @param mode the Mode object to create
   * @tparam A the Mode type
   * @return an option of Mode of type A object
   */
  def makeMode[A](nodeSeq: NodeSeq, mode: ModeType[A]): Option[A] = {
    (getFromNode[String](nodeSeq)).map(mode.fromString(_))
  }

  def makeLinkFromNode(nodeSeq: NodeSeq): Option[com.scalakml.kml.Link] = {
    if (nodeSeq.isEmpty) None else Some(new com.scalakml.kml.Link(id = getFromNode[String](nodeSeq \ "@id"),
      targetId = getFromNode[String](nodeSeq \ "@targetId"),
      refreshMode = makeMode[RefreshMode](nodeSeq \ "refreshMode", RefreshMode),
      refreshInterval = getFromNode[Double](nodeSeq \ "refreshInterval"),
      viewRefreshMode = makeMode[ViewRefreshMode](nodeSeq \ "viewRefreshMode", ViewRefreshMode),
      viewRefreshTime = getFromNode[Double](nodeSeq \ "viewRefreshTime"),
      viewBoundScale = getFromNode[Double](nodeSeq \ "viewBoundScale"),
      viewFormat = getFromNode[String](nodeSeq \ "viewFormat"),
      httpQuery = getFromNode[String](nodeSeq \ "httpQuery"),
      href = getFromNode[String](nodeSeq \ "href")))
  }

  def makeLink(nodeSeq: NodeSeq): Option[com.scalakml.kml.Link] = {
    if (nodeSeq.isEmpty) None else {
      for (x <- List("Link", "Url")) {  // <--- possible labels, "Url" is not part of the formal reference
         val link = makeLinkFromNode(nodeSeq \ x)
         if(link.isDefined) return link
      }
    }
    None
  }

  def makeIcon(nodeSeq: NodeSeq): Option[Icon] = {
    if (nodeSeq.isEmpty) None else Some(new Icon(href = getFromNode[String](nodeSeq \ "href"),
      id = getFromNode[String](nodeSeq \ "@id"),
      targetId = getFromNode[String](nodeSeq \ "@targetId"),
      refreshMode = makeMode[RefreshMode](nodeSeq \ "refreshMode", RefreshMode),
      refreshInterval = getFromNode[Double](nodeSeq \ "refreshInterval"),
      viewRefreshMode = makeMode[ViewRefreshMode](nodeSeq \ "viewRefreshMode", ViewRefreshMode),
      viewRefreshTime = getFromNode[Double](nodeSeq \ "viewRefreshTime"),
      viewBoundScale = getFromNode[Double](nodeSeq \ "viewBoundScale"),
      viewFormat = getFromNode[String](nodeSeq \ "viewFormat"),
      httpQuery = getFromNode[String](nodeSeq \ "httpQuery")))
  }

  def makeNetworkLink(nodeSeq: NodeSeq): Option[NetworkLink] = {
    if (nodeSeq.isEmpty) None else Some(new NetworkLink(id = getFromNode[String](nodeSeq \ "@id"),
      targetId = getFromNode[String](nodeSeq \ "@targetId"),
      refreshVisibility = getFromNode[Boolean](nodeSeq \ "refreshVisibility"),
      flyToView = getFromNode[Boolean](nodeSeq \ "flyToView"),
      link = makeLink(nodeSeq),
      featurePart = makeFeaturePart(nodeSeq)))
  }

  def getFromNode[A: TypeTag](nodeSeq: NodeSeq): Option[A] = {
    if (nodeSeq.isEmpty) None else {
      val node = nodeSeq.text.trim
      if(node.isEmpty) None else {
      typeOf[A] match {
        case x if x == typeOf[String] => Some(node).asInstanceOf[Option[A]]
        case x if x == typeOf[Double] => try { Some(node.toDouble).asInstanceOf[Option[A]] } catch { case _: Throwable => None }
        case x if x == typeOf[Int] => try { Some(node.toInt).asInstanceOf[Option[A]] } catch { case _: Throwable => None }
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

  def makeAtomLink(nodeSeq: NodeSeq): Option[com.scalakml.atom.Link] = {
    if (nodeSeq.isEmpty) None else Some(new com.scalakml.atom.Link(
      href = getFromNode[String](nodeSeq \ "@href"),
      rel = getFromNode[String](nodeSeq \ "rel"),
      typeValue = getFromNode[String](nodeSeq \ "typeValue"),
      hrefLang = getFromNode[String](nodeSeq \ "hrefLang"),
      title = getFromNode[String](nodeSeq \ "title"),
      length = getFromNode[String](nodeSeq \ "length")))
  }

  def makeAtomAuthor(nodeSeq: NodeSeq): Option[com.scalakml.atom.Author] = {
    if (nodeSeq.isEmpty) None else (getFromNode[String](nodeSeq \ "name")).map(x => new com.scalakml.atom.Author(name = x))
  }

  def makeFeaturePart(nodeSeq: NodeSeq): FeaturePart =
    if (nodeSeq.isEmpty) new FeaturePart() else new FeaturePart(
      name = getFromNode[String](nodeSeq \ "name"),
      visibility = getFromNode[Boolean](nodeSeq \ "visibility"),
      open = getFromNode[Boolean](nodeSeq \ "open"),
      atomAuthor = makeAtomAuthor(nodeSeq \ "author"),
      atomLink = makeAtomLink(nodeSeq \ "link"),
      address = getFromNode[String](nodeSeq \ "address"),
      addressDetails = makeAddressDetails(nodeSeq \ "AddressDetails"), // <---- from com.scalaxal.io.XalFromXml
      phoneNumber = getFromNode[String](nodeSeq \ "phoneNumber"),
      extendedData = makeExtendedData(nodeSeq \ "ExtendedData"),
      description = getFromNode[String](nodeSeq \ "description"),
      snippet = makeSnippet(nodeSeq),
      abstractView = makeAbstractView(nodeSeq),
      timePrimitive = makeTimePrimitive(nodeSeq),
      styleUrl = getFromNode[String](nodeSeq \ "styleUrl"),
      styleSelector = makeStyleSet(nodeSeq),
      region = makeRegion(nodeSeq \ "Region"))

  def makeStyle(nodeSeq: NodeSeq): Option[StyleSelector] = {
    if (nodeSeq.isEmpty) None else Some(new Style(
      id = getFromNode[String](nodeSeq \ "@id"), targetId = getFromNode[String](nodeSeq \ "@targetId"),
      iconStyle = makeIconStyle(nodeSeq \ "IconStyle"),
      labelStyle = makeLabelStyle(nodeSeq \ "LabelStyle"),
      lineStyle = makeLineStyle(nodeSeq \ "LineStyle"),
      polyStyle = makePolyStyle(nodeSeq \ "PolyStyle"),
      balloonStyle = makeBalloonStyle(nodeSeq \ "BalloonStyle"),
      listStyle = makeListStyle(nodeSeq \ "ListStyle")))
  }

  def makeIconStyle(nodeSeq: NodeSeq): Option[IconStyle] = {
    if (nodeSeq.isEmpty) None else Some(new IconStyle(
      id = getFromNode[String](nodeSeq \ "@id"), targetId = getFromNode[String](nodeSeq \ "@targetId"),
      scale = getFromNode[Double](nodeSeq \ "scale"),
      heading = getFromNode[Double](nodeSeq \ "heading"),
      icon = makeIcon(nodeSeq \ "Icon"),
      hotSpot = makeVec2(nodeSeq \ "hotSpot"),
      color = makeColor(nodeSeq \ "color"),
      colorMode = makeMode[ColorMode](nodeSeq \ "colorMode", ColorMode)))
  }

  def makeLineStyle(nodeSeq: NodeSeq): Option[LineStyle] = {
    if (nodeSeq.isEmpty) None else Some(new LineStyle(
      id = getFromNode[String](nodeSeq \ "@id"), targetId = getFromNode[String](nodeSeq \ "@targetId"),
      width = getFromNode[Double](nodeSeq \ "width"),
      color = makeColor(nodeSeq \ "color"),
      colorMode = makeMode[ColorMode](nodeSeq \ "colorMode", ColorMode)))
  }

  def makeLabelStyle(nodeSeq: NodeSeq): Option[LabelStyle] = {
    if (nodeSeq.isEmpty) None else Some(new LabelStyle(
      id = getFromNode[String](nodeSeq \ "@id"), targetId = getFromNode[String](nodeSeq \ "@targetId"),
      scale = getFromNode[Double](nodeSeq \ "scale"),
      color = makeColor(nodeSeq \ "color"),
      colorMode = makeMode[ColorMode](nodeSeq \ "colorMode", ColorMode)))
  }

  def makePolyStyle(nodeSeq: NodeSeq): Option[PolyStyle] = {
    if (nodeSeq.isEmpty) None else Some(new PolyStyle(
      id = getFromNode[String](nodeSeq \ "@id"), targetId = getFromNode[String](nodeSeq \ "@targetId"),
      fill = getFromNode[Boolean](nodeSeq \ "fill"),
      outline = getFromNode[Boolean](nodeSeq \ "outline"),
      color = makeColor(nodeSeq \ "color"),
      colorMode = makeMode[ColorMode](nodeSeq \ "colorMode", ColorMode)))
  }

  def makeBalloonStyle(nodeSeq: NodeSeq): Option[BalloonStyle] = {
    if (nodeSeq.isEmpty) None else Some(new BalloonStyle(
      id = getFromNode[String](nodeSeq \ "@id"), targetId = getFromNode[String](nodeSeq \ "@targetId"),
      bgColor = makeColor(nodeSeq \ "bgColor"),
      textColor = makeColor(nodeSeq \ "textColor"),
      text = getFromNode[String](nodeSeq \ "text"),
      displayMode = makeMode[DisplayMode](nodeSeq \ "displayMode", DisplayMode)))
  }

  def makeListStyle(nodeSeq: NodeSeq): Option[ListStyle] = {
    if (nodeSeq.isEmpty) None else Some(new ListStyle(
        id = getFromNode[String](nodeSeq \ "@id"), targetId = getFromNode[String](nodeSeq \ "@targetId"),
        listItemType = makeMode[ListItemType](nodeSeq \ "listItemType", ListItemType),
        bgColor = makeColor(nodeSeq \ "bgColor"),
        itemIcon = makeItemIconSet(nodeSeq \ "ItemIcon"),
        maxSnippetLines = getFromNode[Int](nodeSeq \ "maxSnippetLines")))
  }

  def makeItemIconSet(nodeSeq: NodeSeq): Seq[ItemIcon] = {
    if (nodeSeq.isEmpty) Seq.empty else (nodeSeq collect { case x => makeItemIcon(x) } flatten)
  }

  def makeItemIcon(nodeSeq: NodeSeq): Option[ItemIcon] = {
    if (nodeSeq.isEmpty) None else Some(new ItemIcon(
      id = getFromNode[String](nodeSeq \ "@id"), targetId = getFromNode[String](nodeSeq \ "@targetId"),
      state = makeItemIconStates(nodeSeq \ "state"),
      href = getFromNode[String](nodeSeq \ "href")))
  }

  def makeItemIconStates(nodeSeq: NodeSeq): Seq[ItemIconState] = {
    if (nodeSeq.isEmpty) Seq.empty else {
      getFromNode[String](nodeSeq) match {
        case Some(modeOption) =>
          modeOption match {
            case x if (x.isEmpty) => Seq.empty
            case modeString => ((modeString split "s+").map(x => ItemIconState.fromString(x)).toSeq)
          }
        case _ => Seq.empty
      }
    }
  }

  def makeStyleMap(nodeSeq: NodeSeq): Option[StyleSelector] = {
    if (nodeSeq.isEmpty) None else Some(new StyleMap(
      id = getFromNode[String](nodeSeq \ "@id"), targetId = getFromNode[String](nodeSeq \ "@targetId"),
      pair = makePairSet(nodeSeq \ "Pair")))
  }

  // TODO looks like a circular reference
  def makePair(nodeSeq: NodeSeq): Option[Pair] = {
    if (nodeSeq.isEmpty) None else Some(new Pair(
      id = getFromNode[String](nodeSeq \ "@id"), targetId = getFromNode[String](nodeSeq \ "@targetId"),
      key = makeMode[StyleState](nodeSeq \ "key", StyleState),
      styleUrl = getFromNode[String](nodeSeq \ "styleUrl"),
      styleSelector = makeStyleSelector(nodeSeq))) // <----
  }

  def makePairSet(nodeSeq: NodeSeq): Seq[Pair] = {
    if (nodeSeq.isEmpty) Seq.empty else (nodeSeq collect { case x => makePair(x) } flatten)
  }

  /**
   * Creates a Style or StyleMap from the given NodeSeq
   *
   * @param nodeSeq the node sequence to create the Style or StyleMap from
   * @return a StyleSelector, that is a Style or StyleMap Option
   */
  def makeStyleSelector(nodeSeq: NodeSeq): Option[StyleSelector] = {
    if (nodeSeq.isEmpty) None else {
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
    if (nodeSeq.isEmpty) Seq.empty else {
      val styleList = (nodeSeq \ "Style") match {
          case s if (!s.isEmpty) => (s collect { case x => makeStyle(x) } flatten)
          case _ => Seq.empty
        }
      val styleMapList = (nodeSeq \ "StyleMap") match {
          case s if (!s.isEmpty) => (s collect { case x => makeStyleMap(x) } flatten)
          case _ => Seq.empty
         }
      (styleList ++ styleMapList)
    }
  }

  def makeLod(nodeSeq: NodeSeq): Option[Lod] = {
    if (nodeSeq.isEmpty) None else Some(new Lod(
      id = getFromNode[String](nodeSeq \ "@id"), targetId = getFromNode[String](nodeSeq \ "@targetId"),
      minLodPixels = getFromNode[Double](nodeSeq \ "minLodPixels"),
      maxLodPixels = getFromNode[Double](nodeSeq \ "maxLodPixels"),
      minFadeExtent = getFromNode[Double](nodeSeq \ "minFadeExtent"),
      maxFadeExtent = getFromNode[Double](nodeSeq \ "maxFadeExtent")))
  }

  def makeRegion(nodeSeq: NodeSeq): Option[Region] = {
    if (nodeSeq.isEmpty) None else Some(new Region(
      id = getFromNode[String](nodeSeq \ "@id"), targetId = getFromNode[String](nodeSeq \ "@targetId"),
      latLonAltBox = makeLatLonAltBox(nodeSeq \ "LatLonAltBox"),
      lod = makeLod(nodeSeq \ "Lod")))
  }

  def makeLatLonAltBox(nodeSeq: NodeSeq): Option[LatLonAltBox] = {
    if (nodeSeq.isEmpty) None else Some(new LatLonAltBox(
      id = getFromNode[String](nodeSeq \ "@id"), targetId = getFromNode[String](nodeSeq \ "@targetId"),
      minAltitude = getFromNode[Double](nodeSeq \ "minAltitude"),
      maxAltitude = getFromNode[Double](nodeSeq \ "maxAltitude"),
      altitudeMode = makeMode[AltitudeMode](nodeSeq \ "altitudeMode", AltitudeMode),
      north = getFromNode[Double](nodeSeq \ "north"),
      south = getFromNode[Double](nodeSeq \ "south"),
      east = getFromNode[Double](nodeSeq \ "east"),
      west = getFromNode[Double](nodeSeq \ "west")))
  }

  def makeLatLonBox(nodeSeq: NodeSeq): Option[LatLonBox] = {
    if (nodeSeq.isEmpty) None else Some(new LatLonBox(
      id = getFromNode[String](nodeSeq \ "@id"), targetId = getFromNode[String](nodeSeq \ "@targetId"),
      rotation = getFromNode[Double](nodeSeq \ "rotation"),
      north = getFromNode[Double](nodeSeq \ "north"),
      south = getFromNode[Double](nodeSeq \ "south"),
      east = getFromNode[Double](nodeSeq \ "east"),
      west = getFromNode[Double](nodeSeq \ "west")))
  }

  /**
   * Creates a snippet from the given snippet NodeSeq
   *
   * @param nodeSeq the snippet node sequence
   * @return a Snippet Option
   */
  def makeSnippetFromNode(nodeSeq: NodeSeq): Option[Snippet] = {
    if (nodeSeq.isEmpty) None else Some(new Snippet(
        value = getFromNode[String](nodeSeq).getOrElse(""),
        maxLines = getFromNode[Int](nodeSeq \ "@maxLines").getOrElse(0)))
  }

  /**
   * Creates a snippet from the given parent NodeSeq
   *
   * @param nodeSeq the parent nodeSeq of snippet or Snippet
   * @return a Snippet Option
   */
  def makeSnippet(nodeSeq: NodeSeq): Option[Snippet] = {
    // pick the first node with something in it
    for (x <- List("snippet", "Snippet")) {  // reference is snippet, but lots of Snippet around
      val node = (nodeSeq \ x)
      if (!node.isEmpty) return makeSnippetFromNode(node)
    }
    None
  }

  // TODO how to read other
  def makeExtendedData(nodeSeq: NodeSeq): Option[ExtendedData] = {
    if (nodeSeq.isEmpty) None else Some(new ExtendedData(
      data = makeDataSet(nodeSeq \ "Data"),
      schemaData = makeSchemaDataSet(nodeSeq \ "SchemaData")))
    // other: Seq[Any]     how to get this from xml
  }

  def makeData(nodeSeq: NodeSeq): Option[Data] = {
    if (nodeSeq.isEmpty) None else Some(new Data(name = getFromNode[String](nodeSeq \ "@name"),
      id = getFromNode[String](nodeSeq \ "@id"), targetId = getFromNode[String](nodeSeq \ "@targetId"),
      displayName = getFromNode[String](nodeSeq \ "displayName"),
      value = getFromNode[String](nodeSeq \ "value")))
  }

  def makeDataSet(nodeSeq: NodeSeq): Seq[Data] = {
    if (nodeSeq.isEmpty) Seq.empty else (nodeSeq collect { case x => makeData(x) } flatten)
  }

  def makeSimpleData(nodeSeq: NodeSeq): Option[SimpleData] = {
    if (nodeSeq.isEmpty) None else Some(new SimpleData(name = getFromNode[String](nodeSeq \ "@name"),
      value = getFromNode[String](nodeSeq \ "value")))
  }

  def makeSimpleDataSet(nodeSeq: NodeSeq): Seq[SimpleData] = {
    if (nodeSeq.isEmpty) Seq.empty else (nodeSeq collect { case x => makeSimpleData(x) } flatten)
  }

  def makeSchemaData(nodeSeq: NodeSeq): Option[SchemaData] = {
    if (nodeSeq.isEmpty) None else Some(new SchemaData(
      id = getFromNode[String](nodeSeq \ "@id"), targetId = getFromNode[String](nodeSeq \ "@targetId"),
      simpleData = makeSimpleDataSet(nodeSeq \ "SimpleData"),
      schemaUrl = getFromNode[String](nodeSeq \ "schemaUrl")))
  }

  def makeSchemaDataSet(nodeSeq: NodeSeq): Seq[SchemaData] = {
    if (nodeSeq.isEmpty) Seq.empty else (nodeSeq collect { case x => makeSchemaData(x) } flatten)
  }

  // either a TimeSpan or a TimeStamp or None
  def makeTimePrimitive(nodeSeq: NodeSeq): Option[TimePrimitive] = {
      if (nodeSeq.isEmpty) None else
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
    if (nodeSeq.isEmpty) None else Some(new TimeStamp(
      id = getFromNode[String](nodeSeq \ "@id"), targetId = getFromNode[String](nodeSeq \ "@targetId"),
      when = getFromNode[String](nodeSeq \ "when")))
  }

  def makeTimeSpan(nodeSeq: NodeSeq): Option[TimePrimitive] = {
    if (nodeSeq.isEmpty) None else Some(new TimeSpan(
      id = getFromNode[String](nodeSeq \ "@id"), targetId = getFromNode[String](nodeSeq \ "@targetId"),
      begin = getFromNode[String](nodeSeq \ "begin"),
      end = getFromNode[String](nodeSeq \ "end")))
  }

  // either a Camera or a LookAt or None
  def makeAbstractView(nodeSeq: NodeSeq): Option[AbstractView] = {
    if (nodeSeq.isEmpty) None else
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
    if (nodeSeq.isEmpty) None else Some(new Camera(
      id = getFromNode[String](nodeSeq \ "@id"), targetId = getFromNode[String](nodeSeq \ "@targetId"),
      roll = getFromNode[Double](nodeSeq \ "roll"),
      longitude = getFromNode[Double](nodeSeq \ "longitude"),
      latitude = getFromNode[Double](nodeSeq \ "latitude"),
      altitude = getFromNode[Double](nodeSeq \ "altitude"),
      heading = getFromNode[Double](nodeSeq \ "heading"),
      tilt = getFromNode[Double](nodeSeq \ "tilt"),
      altitudeMode = makeMode[AltitudeMode](nodeSeq \ "altitudeMode", AltitudeMode)))
  }

  def makeLookAt(nodeSeq: NodeSeq): Option[LookAt] = {
    if (nodeSeq.isEmpty) None else Some(new LookAt(
      id = getFromNode[String](nodeSeq \ "@id"), targetId = getFromNode[String](nodeSeq \ "@targetId"),
      range = getFromNode[Double](nodeSeq \ "range"),
      longitude = getFromNode[Double](nodeSeq \ "longitude"),
      latitude = getFromNode[Double](nodeSeq \ "latitude"),
      altitude = getFromNode[Double](nodeSeq \ "altitude"),
      heading = getFromNode[Double](nodeSeq \ "heading"),
      tilt = getFromNode[Double](nodeSeq \ "tilt"),
      altitudeMode = makeMode[AltitudeMode](nodeSeq \ "altitudeMode", AltitudeMode)))
  }

  def makePlacemark(nodeSeq: NodeSeq): Option[Placemark] = {
    if (nodeSeq.isEmpty) None else Some(new Placemark(
        id = getFromNode[String](nodeSeq \ "@id"), targetId = getFromNode[String](nodeSeq \ "@targetId"),
        geometry = makeGeometry(nodeSeq),
        featurePart = makeFeaturePart(nodeSeq)))
  }

  def makeDocument(nodeSeq: NodeSeq): Option[com.scalakml.kml.Document] = {
    if (nodeSeq.isEmpty) None else Some(new com.scalakml.kml.Document(
        id = getFromNode[String](nodeSeq \ "@id"), targetId = getFromNode[String](nodeSeq \ "@targetId"),
        schemas = makeSchemaSet(nodeSeq \ "Schema"),
        features = makeFeatureSet(nodeSeq),
        featurePart = makeFeaturePart(nodeSeq)))
  }

  def makeSchemaSet(nodeSeq: NodeSeq): Seq[Schema] = {
    if (nodeSeq.isEmpty) Seq.empty else (nodeSeq collect { case x => makeSchema(x) } flatten)
  }

  def makeSchema(nodeSeq: NodeSeq): Option[Schema] = {
    if (nodeSeq.isEmpty) None else Some(new Schema(
      simpleField = makeSimpleFieldSet(nodeSeq \ "SimpleField"),
      name = getFromNode[String](nodeSeq \ "@name"),
      id = getFromNode[String](nodeSeq \ "@id")))
  }

  def makeSimpleField(nodeSeq: NodeSeq): Option[SimpleField] = {
    if (nodeSeq.isEmpty) None else Some(new SimpleField(
      name = getFromNode[String](nodeSeq \ "@name"),
      typeValue = getFromNode[String](nodeSeq \ "@type"),
      displayName = getFromNode[String](nodeSeq \ "displayName")))
  }

  def makeSimpleFieldSet(nodeSeq: NodeSeq): Seq[SimpleField] = {
    if (nodeSeq.isEmpty) Seq.empty else (nodeSeq collect { case x => makeSimpleField(x) } flatten)
  }

  def makeFolder(nodeSeq: NodeSeq): Option[Folder] = {
    if (nodeSeq.isEmpty) None else Some(new Folder(
        id = getFromNode[String](nodeSeq \ "@id"), targetId = getFromNode[String](nodeSeq \ "@targetId"),
        features = makeFeatureSet(nodeSeq),
        featurePart = makeFeaturePart(nodeSeq)))
  }

  def makePoint(nodeSeq: NodeSeq): Option[Point] = {
    if (nodeSeq.isEmpty) None else Some(new Point(
        id = getFromNode[String](nodeSeq \ "@id"), targetId = getFromNode[String](nodeSeq \ "@targetId"),
        coordinates = makeCoordinates(nodeSeq \ "coordinates"),
        extrude = getFromNode[Boolean](nodeSeq \ "extrude"),
        altitudeMode = makeMode[AltitudeMode](nodeSeq \ "altitudeMode", AltitudeMode)))
  }

  def makeCoordinates(nodeSeq: NodeSeq): Option[Seq[Location]] = {
    if (nodeSeq.isEmpty) None else
      Some((nodeSeq.text.trim split "\\s+").map(x => com.scalakml.kml.Location.fromCsString(x)) flatMap(x => x) toSeq )
  }

  def makeLocation(nodeSeq: NodeSeq): Option[Location] = {
    if (nodeSeq.isEmpty) None else Some(new Location(
      id = getFromNode[String](nodeSeq \ "@id"), targetId = getFromNode[String](nodeSeq \ "@targetId"),
      longitude = getFromNode[Double](nodeSeq \ "longitude"),
      latitude = getFromNode[Double](nodeSeq \ "latitude"),
      altitude = getFromNode[Double](nodeSeq \ "altitude")))
  }

  def makeLineString(nodeSeq: NodeSeq): Option[LineString] = {
    if (nodeSeq.isEmpty) None else Some(new LineString(
        id = getFromNode[String](nodeSeq \ "@id"), targetId = getFromNode[String](nodeSeq \ "@targetId"),
        extrude = getFromNode[Boolean](nodeSeq \ "extrude"),
        tessellate = getFromNode[Boolean](nodeSeq \ "tessellate"),
        altitudeMode = makeMode[AltitudeMode](nodeSeq \ "altitudeMode", AltitudeMode),
        coordinates = makeCoordinates(nodeSeq \ "coordinates")))
  }

  def makeLinearRing(nodeSeq: NodeSeq): Option[LinearRing] = {
    if (nodeSeq.isEmpty) None else Some(new LinearRing(
        id = getFromNode[String](nodeSeq \ "@id"), targetId = getFromNode[String](nodeSeq \ "@targetId"),
        extrude = getFromNode[Boolean](nodeSeq \ "extrude"),
        tessellate = getFromNode[Boolean](nodeSeq \ "tessellate"),
        altitudeMode = makeMode[AltitudeMode](nodeSeq \ "altitudeMode", AltitudeMode),
        coordinates = makeCoordinates(nodeSeq \ "coordinates")))
  }

  def makeBoundary(nodeSeq: NodeSeq): Option[Boundary] = {
    if (nodeSeq.isEmpty) None else Some(new Boundary(linearRing = makeLinearRing(nodeSeq \ "LinearRing")))
  }

  def makeBoundaries(nodeSeq: NodeSeq): Seq[Boundary] = {
    if (nodeSeq.isEmpty) Seq.empty else (nodeSeq collect { case x => makeBoundary(x) } flatten)
  }

  def makePolygon(nodeSeq: NodeSeq): Option[Polygon] = {
    if (nodeSeq.isEmpty) None else Some(new Polygon(
        id = getFromNode[String](nodeSeq \ "@id"), targetId = getFromNode[String](nodeSeq \ "@targetId"),
        extrude = getFromNode[Boolean](nodeSeq \ "extrude"),
        tessellate = getFromNode[Boolean](nodeSeq \ "tessellate"),
        altitudeMode = makeMode[AltitudeMode](nodeSeq \ "altitudeMode", AltitudeMode),
        outerBoundaryIs = makeBoundary(nodeSeq \ "outerBoundaryIs"),
        innerBoundaryIs = makeBoundaries(nodeSeq \ "innerBoundaryIs")))
  }

  def makeMultiGeometry(nodeSeq: NodeSeq): Option[MultiGeometry] = {
    if (nodeSeq.isEmpty) None else Some(new MultiGeometry(
      id = getFromNode[String](nodeSeq \ "@id"), targetId = getFromNode[String](nodeSeq \ "@targetId"),
      geometries = makeGeometrySet(nodeSeq)))
  }

  def makeModel(nodeSeq: NodeSeq): Option[Model] = {
    if (nodeSeq.isEmpty) None else Some(new Model(
        id = getFromNode[String](nodeSeq \ "@id"), targetId = getFromNode[String](nodeSeq \ "@targetId"),
        altitudeMode = makeMode[AltitudeMode](nodeSeq \ "altitudeMode", AltitudeMode),
        location = makeLocation(nodeSeq \ "Location"),
        scale = makeScale(nodeSeq \ "Scale"),
        link = makeLink(nodeSeq),
        resourceMap = makeResourceMap(nodeSeq \ "ResourceMap")))
  }

  def makeResourceMap(nodeSeq: NodeSeq): Option[ResourceMap] = {
    if (nodeSeq.isEmpty) None else Some(new ResourceMap(
      id = getFromNode[String](nodeSeq \ "@id"), targetId = getFromNode[String](nodeSeq \ "@targetId"),
      alias = makeAliasSet(nodeSeq \ "Alias")))
  }

  def makeAliasSet(nodeSeq: NodeSeq): Seq[Alias] = {
    if (nodeSeq.isEmpty) Seq.empty else (nodeSeq collect { case x => makeAlias(x) } flatten)
  }

  def makeAlias(nodeSeq: NodeSeq): Option[Alias] = {
    if (nodeSeq.isEmpty) None else Some(new Alias(
      id = getFromNode[String](nodeSeq \ "@id"), targetId = getFromNode[String](nodeSeq \ "@targetId"),
      targetHref = getFromNode[String](nodeSeq \ "targetHref"),
      sourceHref = getFromNode[String](nodeSeq \ "sourceHref")))
  }

  def makeScale(nodeSeq: NodeSeq): Option[Scale] = {
    if (nodeSeq.isEmpty) None else Some(new Scale(
      id = getFromNode[String](nodeSeq \ "@id"), targetId = getFromNode[String](nodeSeq \ "@targetId"),
      x = getFromNode[Double](nodeSeq \ "x"),
      y = getFromNode[Double](nodeSeq \ "y"),
      z = getFromNode[Double](nodeSeq \ "z")))
  }

  def makeGeometry(nodeSeq: NodeSeq, geomType: GeometryTypes): Option[Geometry] = {
    if (nodeSeq.isEmpty) None else {
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
    if (nodeSeq.isEmpty) None else {
      // just pick the first match
    for (x <- GeometryTypes.values) {
        val geom = makeGeometry(nodeSeq \ x.toString, x)
        if(geom.isDefined) return geom
      }
    }
    None
  }

  def makeGeometrySet(nodeSeq: NodeSeq): Seq[Geometry] = {
    if (nodeSeq.isEmpty) Seq.empty else
      (GeometryTypes.values.flatMap(x => makeGeometries(nodeSeq \ x.toString, x)).toSeq.flatten)
  }

  def makeGeometries(nodeSeq: NodeSeq, geometryTypes: GeometryTypes): Seq[Option[Geometry]] = {
    if (nodeSeq.isEmpty) Seq.empty else
      (nodeSeq collect { case x => makeGeometry(x, geometryTypes) }) filter (_ != None)
  }

  def makeViewVolume(nodeSeq: NodeSeq): Option[ViewVolume] = {
    if (nodeSeq.isEmpty) None else
      Some(new ViewVolume(id = getFromNode[String](nodeSeq \ "@id"), targetId = getFromNode[String](nodeSeq \ "@targetId"),
        leftFov = getFromNode[Double](nodeSeq \ "leftFov"),
        rightFov = getFromNode[Double](nodeSeq \ "rightFov"),
        bottomFov = getFromNode[Double](nodeSeq \ "bottomFov"),
        topFov = getFromNode[Double](nodeSeq \ "topFov"),
        near = getFromNode[Double](nodeSeq \ "near")))
  }

  def makeImagePyramid(nodeSeq: NodeSeq): Option[ImagePyramid] = {
    if (nodeSeq.isEmpty) None else
      Some(new ImagePyramid(id = getFromNode[String](nodeSeq \ "@id"), targetId = getFromNode[String](nodeSeq \ "@targetId"),
        tileSize = getFromNode[Int](nodeSeq \ "tileSize"),
        maxWidth = getFromNode[Int](nodeSeq \ "maxWidth"),
        maxHeight = getFromNode[Int](nodeSeq \ "maxHeight"),
        gridOrigin = makeMode[GridOrigin](nodeSeq \ "gridOrigin", GridOrigin)))
  }

  def makeColor(nodeSeq: NodeSeq): Option[HexColor] = {
    if (nodeSeq.isEmpty) None else (getFromNode[String](nodeSeq)).map(x => new HexColor(hexString = x))
  }

  def makePhotoOverlay(nodeSeq: NodeSeq): Option[PhotoOverlay] = {
    if (nodeSeq.isEmpty) None else
      Some(new PhotoOverlay(id = getFromNode[String](nodeSeq \ "@id"), targetId = getFromNode[String](nodeSeq \ "@targetId"),
        rotation = getFromNode[Double](nodeSeq \ "rotation"),
        viewVolume = makeViewVolume(nodeSeq \ "ViewVolume"),
        imagePyramid = makeImagePyramid(nodeSeq \ "ImagePyramid"),
        point = makePoint(nodeSeq \ "Point"),
        shape = makeMode[Shape](nodeSeq \ "shape", Shape),
        color = makeColor(nodeSeq \ "color"),
        drawOrder = getFromNode[Int](nodeSeq \ "drawOrder"),
        icon = makeIcon(nodeSeq \ "Icon"),
        featurePart = makeFeaturePart(nodeSeq)))
  }

  def makeGroundOverlay(nodeSeq: NodeSeq): Option[GroundOverlay] = {
    if (nodeSeq.isEmpty) None else
      Some(new GroundOverlay(id = getFromNode[String](nodeSeq \ "@id"), targetId = getFromNode[String](nodeSeq \ "@targetId"),
        altitude = getFromNode[Double](nodeSeq \ "altitude"),
        altitudeMode = makeMode[AltitudeMode](nodeSeq \ "altitudeMode", AltitudeMode),
        latLonBox = makeLatLonBox(nodeSeq \ "LatLonBox"),
        color = makeColor(nodeSeq \ "color"),
        drawOrder = getFromNode[Int](nodeSeq \ "drawOrder"),
        icon = makeIcon(nodeSeq \ "Icon"),
        featurePart = makeFeaturePart(nodeSeq)))
  }

  def makeVec2(nodeSeq: NodeSeq): Option[Vec2] = {
    if (nodeSeq.isEmpty) None else Some(new Vec2(
        x = getFromNode[Double](nodeSeq \ "@x").getOrElse(0.0),
        y = getFromNode[Double](nodeSeq \ "@y").getOrElse(0.0),
        xunits = makeMode[Units](nodeSeq \ "@xunits", Units).getOrElse(Fraction),
        yunits = makeMode[Units](nodeSeq \ "@yunits", Units).getOrElse(Fraction)))
  }

  def makeScreenOverlay(nodeSeq: NodeSeq): Option[ScreenOverlay] = {
    if (nodeSeq.isEmpty) None else Some(new ScreenOverlay(
        id = getFromNode[String](nodeSeq \ "@id"), targetId = getFromNode[String](nodeSeq \ "@targetId"),
        overlayXY = makeVec2(nodeSeq \ "overlayXY"),
        screenXY = makeVec2(nodeSeq \ "screenXY"),
        rotationXY = makeVec2(nodeSeq \ "rotationXY"),
        size = makeVec2(nodeSeq \ "size"),
        rotation = getFromNode[Double](nodeSeq \ "rotation"),
        color = makeColor(nodeSeq \ "color"),
        drawOrder = getFromNode[Int](nodeSeq \ "drawOrder"),
        icon = makeIcon(nodeSeq \ "Icon"),
        featurePart = makeFeaturePart(nodeSeq)))
  }

//-----------------------------------------------------------------------------------------------
//----------------------------------gx-----------------------------------------------------------
//-----------------------------------------------------------------------------------------------

  def makePlaylist(nodeSeq: NodeSeq): Option[Playlist] = {
    if (nodeSeq.isEmpty) None else Some(new Playlist(
      id = getFromNode[String](nodeSeq \ "@id"), targetId = getFromNode[String](nodeSeq \ "@targetId"),
      tourPrimitiveGroup = Some(makeTourPrimitiveSet(nodeSeq))))
  }

  def makeTour(nodeSeq: NodeSeq): Option[Tour] = {
    if (nodeSeq.isEmpty) None else Some(new Tour(
      id = getFromNode[String](nodeSeq \ "@id"), targetId = getFromNode[String](nodeSeq \ "@targetId"),
      playlist = makePlaylist(nodeSeq \ "Playlist"),
      featurePart = makeFeaturePart(nodeSeq)))
  }

  def makeTourPrimitiveSet(nodeSeq: NodeSeq): Seq[TourPrimitive] = {
    if (nodeSeq.isEmpty) Seq.empty else
      (TourPrimitiveTypes.values.flatMap(x => makeTourPrimitives(nodeSeq \ x.toString, x)).toSeq.flatten)
  }

  def makeTourPrimitives(nodeSeq: NodeSeq, tourPrimitiveType: TourPrimitiveTypes): Seq[Option[TourPrimitive]] = {
    (nodeSeq collect { case x => makeTourPrimitive(x, tourPrimitiveType) }) filter (_ != None)
  }

  def makeTourPrimitive(nodeSeq: NodeSeq, tourPrimitiveType: TourPrimitiveTypes): Option[TourPrimitive] = {
    if (nodeSeq.isEmpty) None else {
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
    if (nodeSeq.isEmpty) None else Some(new AnimatedUpdate(
      id = getFromNode[String](nodeSeq \ "@id"), targetId = getFromNode[String](nodeSeq \ "@targetId"),
      duration = getFromNode[Double](nodeSeq \ "duration"),
      update = makeUpdate(nodeSeq \ "Update")))
  }

  def makeFlyTo(nodeSeq: NodeSeq): Option[FlyTo] = {
    if (nodeSeq.isEmpty) None else Some(new FlyTo(
      id = getFromNode[String](nodeSeq \ "@id"), targetId = getFromNode[String](nodeSeq \ "@targetId"),
      duration = getFromNode[Double](nodeSeq \ "duration"),
      flyToMode = makeMode[FlyToMode](nodeSeq \ "flyToMode", FlyToMode),
      abstractView = makeAbstractView(nodeSeq)))
  }

  def makeSoundCue(nodeSeq: NodeSeq): Option[SoundCue] = {
    if (nodeSeq.isEmpty) None else Some(new SoundCue(
      id = getFromNode[String](nodeSeq \ "@id"), targetId = getFromNode[String](nodeSeq \ "@targetId"),
      href = getFromNode[String](nodeSeq \ "href")))
  }

  def makeWait(nodeSeq: NodeSeq): Option[Wait] = {
    if (nodeSeq.isEmpty) None else Some(new Wait(
      id = getFromNode[String](nodeSeq \ "@id"), targetId = getFromNode[String](nodeSeq \ "@targetId"),
      duration = getFromNode[Double](nodeSeq \ "duration")))
  }

  def makeTourControl(nodeSeq: NodeSeq): Option[TourControl] = {
    if (nodeSeq.isEmpty) None else Some(new TourControl(
      id = getFromNode[String](nodeSeq \ "@id"), targetId = getFromNode[String](nodeSeq \ "@targetId"),
      playMode = makeMode[PlayMode](nodeSeq \ "playMode", PlayMode)))
  }

}
