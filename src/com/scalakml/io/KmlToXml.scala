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

import com.scalakml.kml._
import scala.collection.mutable.MutableList
import xml.{Null, Attribute, NodeSeq, Text}
import scala.None
import com.scalakml.gx._
import com.scalakml.atom.Author
import com.scalaxal.xAL.AddressDetails
import com.scalaxal.io.XalToXml

/**
 * @author Ringo Wathelet
 * Date: 12/12/12
 * Version: 1
 */

trait KmlToXml[A] {
  def toXml(value: A): NodeSeq
}

trait KmlToXmlSeq[A] {
  def toXml(value: A): Seq[NodeSeq]
}

/** Factory to convert kml objects instances to scala xml NodeSeq */
object KmlToXml extends XmlExtractor {

  // ------------------------------------------------------------    
  // -----------------------implicit-----------------------------  
  // ------------------------------------------------------------  

  implicit def StringToXmlText(valueOption: Option[String]): Option[xml.Text] = {
    valueOption match {
      case Some(value) => Some(Text(value.trim))
      case None => None
    }
  }

  implicit object AddressDetailsToXml extends KmlToXml[Option[AddressDetails]] {
    def toXml(addressDetailsOption: Option[AddressDetails]): NodeSeq = {
      addressDetailsOption match {
        case Some(addressDetails) => XalToXml.toXml(addressDetails)
        case None => NodeSeq.Empty
      }
    }
  }

  implicit object UpdateToXml extends KmlToXml[Option[Update]] {
    def toXml(updateOption: Option[Update]): NodeSeq = {
      updateOption match {
        case Some(update) => <Update targetHref={if (update.targetHref != null) update.targetHref else null}>
          {getXmlSeqFrom(Option(update.updateOption))}
        </Update>
        case None => NodeSeq.Empty
      }
    }
  }

  implicit object UpdateOptionToXml extends KmlToXml[Option[UpdateOption]] {
    def toXml(updateOption: Option[UpdateOption]): NodeSeq = {
      if (!updateOption.isDefined) NodeSeq.Empty
      else
        updateOption.get match {
          case delete: Delete => getXmlFrom(Option(delete))
          case create: Create => getXmlFrom(Option(create))
          case change: Change => getXmlFrom(Option(change))
          case _ => NodeSeq.Empty
        }
    }
  }

  implicit object DeleteToXml extends KmlToXml[Option[Delete]] {
    def toXml(deleteOption: Option[Delete]): NodeSeq = {
      deleteOption match {
        case Some(delete) =>
          if (delete.featureSet == Nil) NodeSeq.Empty
          else
            <Delete>
              {for (f <- delete.featureSet) yield getXmlFrom(Option(f))}
            </Delete>
        case None => NodeSeq.Empty
      }
    }
  }

  implicit object CreateToXml extends KmlToXml[Option[Create]] {
    def toXml(createOption: Option[Create]): NodeSeq = {
      createOption match {
        case Some(create) =>
          if (create.containerSet == Nil) NodeSeq.Empty
          else
            <Create>
              {for (f <- create.containerSet) yield getXmlFrom(Option(f))}
            </Create>
        case None => NodeSeq.Empty
      }
    }
  }

  implicit object KmlObjectToXml extends KmlToXml[Option[KmlObject]] {
    def toXml(kmlObject: Option[KmlObject]): NodeSeq = {
      if (!kmlObject.isDefined) NodeSeq.Empty
      else
        kmlObject.get match {
          case feature: Feature => getXmlFrom(Option(feature))
          case resourceMap: ResourceMap => getXmlFrom(Option(resourceMap))
          case alias: Alias => getXmlFrom(Option(alias))
          case viewVolume: ViewVolume => getXmlFrom(Option(viewVolume))
          case imagePyramid: ImagePyramid => getXmlFrom(Option(imagePyramid))
          case pair: Pair => getXmlFrom(Option(pair))
          case abstractView: AbstractView => getXmlFrom(Option(abstractView))
          case data: Data => getXmlFrom(Option(data))
          case schemaData: SchemaData => getXmlFrom(Option(schemaData))
          case timePrimitive: TimePrimitive => getXmlFrom(Option(timePrimitive))
          case region: Region => getXmlFrom(Option(region))
          case latLonAltBox: LatLonAltBox => getXmlFrom(Option(latLonAltBox))
          case lod: Lod => getXmlFrom(Option(lod))
          case icon: Icon => getXmlFrom(Option(icon))
          case link: Link => getXmlFrom(Option(link))
          case location: Location => getXmlFrom(Option(location))
          case orientation: Orientation => getXmlFrom(Option(orientation))
          case scale: Scale => getXmlFrom(Option(scale))
          case geometry: Geometry => getXmlFrom(Option(geometry))
          case style: Style => getXmlFrom(Option(style))
          case styleMap: StyleMap => getXmlFrom(Option(styleMap))
          case _ => NodeSeq.Empty
        }
    }
  }

  implicit object ChangeToXml extends KmlToXml[Option[Change]] {
    def toXml(changeOption: Option[Change]): NodeSeq = {
      changeOption match {
        case Some(change) =>
          if (change.objectChangeSet == Nil) NodeSeq.Empty
          else
            <Change>
              { for (kmlObject <- change.objectChangeSet) yield getXmlFrom(Option(kmlObject.asInstanceOf[KmlObject])) }
            </Change>
        case None => NodeSeq.Empty
      }
    }
  }

  implicit object networkLinkControlToXml extends KmlToXml[Option[NetworkLinkControl]] {
    def toXml(networkLinkControlOption: Option[NetworkLinkControl]): NodeSeq = {
      networkLinkControlOption match {
        case Some(networkLinkControl) => <NetworkLinkControl>
          {getNodeFromFieldName("minRefreshPeriod", networkLinkControlOption)}
          {getNodeFromFieldName("maxSessionLength", networkLinkControlOption)}
          {getNodeFromFieldName("cookie", networkLinkControlOption)}
          {getNodeFromFieldName("message", networkLinkControlOption)}
          {getNodeFromFieldName("linkName", networkLinkControlOption)}
          {getNodeFromFieldName("linkDescription", networkLinkControlOption)}
          {getNodeFromFieldName("expires", networkLinkControlOption)}
          {getXmlFrom(networkLinkControl.linkSnippet)}
          {getXmlFrom(networkLinkControl.update)}
          {getXmlFrom(networkLinkControl.abstractView)}
        </NetworkLinkControl>
        case None => NodeSeq.Empty
      }
    }
  }

  implicit object placemarkToXml extends KmlToXml[Option[Placemark]] {
    def toXml(placemarkOption: Option[Placemark]): NodeSeq = {
      placemarkOption match {
        case Some(placemark) => <Placemark id={if (placemark.id.isDefined) placemark.id.get else null} targetId={if (placemark.targetId.isDefined) placemark.targetId.get else null}>
          {getXmlSeqFrom(Option(placemark.featurePart))}
          {getXmlFrom(placemark.geometry)}
        </Placemark>
        case None => NodeSeq.Empty
      }
    }
  }

  implicit object documentToXml extends KmlToXml[Option[Document]] {
    def toXml(documentOption: Option[Document]): NodeSeq = {
      documentOption match {
        case Some(document) => <Document id={if (document.id.isDefined) document.id.get else null} targetId={if (document.targetId.isDefined) document.targetId.get else null}>
          {getXmlSeqFrom(Option(document.featurePart))}
          {for (s <- document.schemas) yield getXmlFrom(Option(s))}
          {for (f <- document.features) yield getXmlFrom(Option(f))}
        </Document>
        case None => NodeSeq.Empty
      }
    }
  }

  implicit object SchemaToXml extends KmlToXml[Option[Schema]] {
    def toXml(schemaOption: Option[Schema]): NodeSeq = {
      schemaOption match {
        case Some(schema) => <Schema name={if (schema.name.isDefined) schema.name.get else null} id={if (schema.id.isDefined) schema.id.get else null}>
          {for (x <- schema.simpleField) yield getXmlFrom(Option(x))}
        </Schema>
        case None => NodeSeq.Empty
      }
    }
  }

  implicit object SimpleFieldToXml extends KmlToXml[Option[SimpleField]] {
    def toXml(simpleFieldOption: Option[SimpleField]): NodeSeq = {
      simpleFieldOption match {
        case Some(simpleField) => <SimpleField type={if (simpleField.typeValue.isDefined) simpleField.typeValue.get else null} name={if (simpleField.name.isDefined) simpleField.name.get else null}>
          {getNodeFromFieldName("displayName", simpleFieldOption)}
        </SimpleField>
        case None => NodeSeq.Empty
      }
    }
  }

  // TODO ability to make different namespaces
  implicit object kmlToXml extends KmlToXml[Option[Kml]] {
    def toXml(kmlOption: Option[Kml]): NodeSeq = {
      kmlOption match {
        case Some(kml) => <kml xmlns="http://www.opengis.net/kml/2.2" xmlns:atom="http://www.w3.org/2005/Atom" xmlns:xal="urn:oasis:names:tc:ciq:xsdschema:xAL:2.0" xmlns:gx="http://www.google.com/kml/ext/2.2" hint={if (kml.hint.isDefined) kml.hint.get else null}>
          {getXmlFrom(kml.networkLinkControl)}
          {getXmlFrom(kml.feature)}
        </kml>
        case None => NodeSeq.Empty
      }
    }
  }

  implicit object BalloonStyleToXml extends KmlToXml[Option[BalloonStyle]] {
    def toXml(balloonStyleOption: Option[BalloonStyle]): NodeSeq = {
      balloonStyleOption match {
        case Some(balloonStyle) => <BalloonStyle id={if (balloonStyle.id.isDefined) balloonStyle.id.get else null} targetId={if (balloonStyle.targetId.isDefined) balloonStyle.targetId.get else null}>
          {getXmlFrom(balloonStyle.color)}
          {getXmlFrom(balloonStyle.bgColor)}
          {getXmlFrom(balloonStyle.textColor)}
          {getNodeFromFieldName("displayMode", balloonStyleOption)}
          {getNodeFromFieldName("text", balloonStyleOption)}
        </BalloonStyle>
        case None => NodeSeq.Empty
      }
    }
  }

  implicit object IconStyleToXml extends KmlToXml[Option[IconStyle]] {
    def toXml(iconStyleOption: Option[IconStyle]): NodeSeq = {
      iconStyleOption match {
        case Some(iconStyle) => <IconStyle id={if (iconStyle.id.isDefined) iconStyle.id.get else null} targetId={if (iconStyle.targetId.isDefined) iconStyle.targetId.get else null}>
          {getNodeFromFieldName("scale", iconStyleOption)}
          {getNodeFromFieldName("heading", iconStyleOption)}
          {getXmlFrom(iconStyle.color)}
          {getNodeFromFieldName("colorMode", iconStyleOption)}
          {getXmlFrom(iconStyle.icon)}
        </IconStyle>
        case None => NodeSeq.Empty
      }
    }
  }

  implicit object LabelStyleToXml extends KmlToXml[Option[LabelStyle]] {
    def toXml(labelStyleOption: Option[LabelStyle]): NodeSeq = {
      labelStyleOption match {
        case Some(labelStyle) => <LabelStyle id={if (labelStyle.id.isDefined) labelStyle.id.get else null} targetId={if (labelStyle.targetId.isDefined) labelStyle.targetId.get else null}>
          {getNodeFromFieldName("scale", labelStyleOption)}
          {getXmlFrom(labelStyle.color)}
          {getNodeFromFieldName("colorMode", labelStyleOption)}
        </LabelStyle>
        case None => NodeSeq.Empty
      }
    }
  }

  implicit object LineStyleToXml extends KmlToXml[Option[LineStyle]] {
    def toXml(lineStyleOption: Option[LineStyle]): NodeSeq = {
      lineStyleOption match {
        case Some(lineStyle) => <LineStyle id={if (lineStyle.id.isDefined) lineStyle.id.get else null} targetId={if (lineStyle.targetId.isDefined) lineStyle.targetId.get else null}>
          {getNodeFromFieldName("width", lineStyleOption)}
          {getXmlFrom(lineStyle.color)}
          {getNodeFromFieldName("colorMode", lineStyleOption)}
        </LineStyle>
        case None => NodeSeq.Empty
      }
    }
  }

  implicit object ListStyleToXml extends KmlToXml[Option[ListStyle]] {
    def toXml(listStyleOption: Option[ListStyle]): NodeSeq = {
      listStyleOption match {
        case Some(listStyle) => <ListStyle id={if (listStyle.id.isDefined) listStyle.id.get else null} targetId={if (listStyle.targetId.isDefined) listStyle.targetId.get else null}>
          {getNodeFromFieldName("listItemType", listStyleOption)}
          {getXmlSeqFrom(Option(listStyle.itemIcon))}
          {getXmlFrom(listStyle.bgColor)}
          {getNodeFromFieldName("maxSnippetLines", listStyleOption)}
        </ListStyle>
        case None => NodeSeq.Empty
      }
    }
  }

  implicit object IconStateToXml extends KmlToXml[Option[ItemIconState]] {
    def toXml(iconStateOption: Option[ItemIconState]): NodeSeq = {
      iconStateOption match {
        case Some(iconState) => <state>
          {getXmlFrom(iconStateOption)}
        </state>
        case None => NodeSeq.Empty
      }
    }
  }

  implicit object ItemIconToXml extends KmlToXml[Option[ItemIcon]] {
    def toXml(itemIconOption: Option[ItemIcon]): NodeSeq = {
      itemIconOption match {
        case Some(itemIcon) => <ItemIcon id={if (itemIcon.id.isDefined) itemIcon.id.get else null} targetId={if (itemIcon.targetId.isDefined) itemIcon.targetId.get else null}>
          {getNodeFromFieldName("href", itemIconOption)}{getXmlSeqFrom(Option(itemIcon.state))}
        </ItemIcon>
        case None => NodeSeq.Empty
      }
    }
  }

  implicit object PolyStyleToXml extends KmlToXml[Option[PolyStyle]] {
    def toXml(polyStyleOption: Option[PolyStyle]): NodeSeq = {
      polyStyleOption match {
        case Some(polyStyle) => <PolyStyle id={if (polyStyle.id.isDefined) polyStyle.id.get else null} targetId={if (polyStyle.targetId.isDefined) polyStyle.targetId.get else null}>
          {getNodeFromFieldName("fill", polyStyleOption)}
          {getNodeFromFieldName("outline", polyStyleOption)}
          {getXmlFrom(polyStyle.color)}
          {getNodeFromFieldName("colorMode", polyStyleOption)}
        </PolyStyle>
        case None => NodeSeq.Empty
      }
    }
  }

  implicit object StyleToXml extends KmlToXml[Option[Style]] {
    def toXml(styleOption: Option[Style]): NodeSeq = {
      styleOption match {
        case Some(style) => <Style id={if (style.id.isDefined) style.id.get else null} targetId={if (style.targetId.isDefined) style.targetId.get else null}>
          {getXmlFrom(style.iconStyle)}
          {getXmlFrom(style.labelStyle)}
          {getXmlFrom(style.lineStyle)}
          {getXmlFrom(style.listStyle)}
          {getXmlFrom(style.polyStyle)}
          {getXmlFrom(style.balloonStyle)}
        </Style>
        case None => NodeSeq.Empty
      }
    }
  }

  implicit object StyleSelectorToXml extends KmlToXml[Option[StyleSelector]] {
    def toXml(styleSelector: Option[StyleSelector]): NodeSeq = {
      if (!styleSelector.isDefined) NodeSeq.Empty
      else
        styleSelector.get match {
          case style: Style => getXmlFrom(Option(style))
          case styleMap: StyleMap => getXmlFrom(Option(styleMap))
          case _ => NodeSeq.Empty
        }
    }
  }

  implicit object PairToXml extends KmlToXml[Option[Pair]] {
    def toXml(pairOption: Option[Pair]): NodeSeq = {
      pairOption match {
        case Some(pair) => <Pair id={if (pair.id.isDefined) pair.id.get else null} targetId={if (pair.targetId.isDefined) pair.targetId.get else null}>
          {getNodeFromFieldName("key", pairOption)}{if (pair.styleUrl.isDefined) getNodeFromFieldName("styleUrl", pairOption)
          else getXmlFrom(pair.styleSelector)}
        </Pair>
        case None => NodeSeq.Empty
      }
    }
  }

  implicit object StyleMapToXml extends KmlToXml[Option[StyleMap]] {
    def toXml(styleMapOption: Option[StyleMap]): NodeSeq = {
      styleMapOption match {
        case Some(styleMap) => <StyleMap id={if (styleMap.id.isDefined) styleMap.id.get else null} targetId={if (styleMap.targetId.isDefined) styleMap.targetId.get else null}>
          {getXmlSeqFrom(Option(styleMap.pair))}
        </StyleMap>
        case None => NodeSeq.Empty
      }
    }
  }

  implicit object AtomAuthorToXml extends KmlToXml[Option[Author]] {
    def toXml(authorOption: Option[Author]): NodeSeq = {
      authorOption match {
        case Some(author) => <atom:author>
          <atom:name>
            {if ((author.name != null) && (!author.name.isEmpty)) author.name else null}
          </atom:name>
        </atom:author>
        case None => NodeSeq.Empty
      }
    }
  }

  implicit object AtomLinkToXml extends KmlToXml[Option[com.scalakml.atom.Link]] {
    def toXml(linkOption: Option[com.scalakml.atom.Link]): NodeSeq = {
      linkOption match {
        case Some(link) => <atom:link>
          {(link.getClass.getDeclaredFields.map(x => getNodeFromFieldName(x.getName, linkOption)) filter (_ != null) toSeq)}
        </atom:link>
        case None => NodeSeq.Empty
      }
    }
  }

  implicit object AbstractViewToXml extends KmlToXml[Option[AbstractView]] {
    def toXml(abstractView: Option[AbstractView]): NodeSeq = {
      if (!abstractView.isDefined) NodeSeq.Empty
      else
        abstractView.get match {
          case camera: Camera =>
            <Camera>
              {(camera.getClass.getDeclaredFields.map(x => getNodeFromFieldName(x.getName, Option(camera))) filter (_ != null) toSeq)}
            </Camera>
          case lookAt: LookAt =>
            <LookAt>
              {(lookAt.getClass.getDeclaredFields.map(x => getNodeFromFieldName(x.getName, Option(lookAt))) filter (_ != null) toSeq)}
            </LookAt>
        }
    }
  }

  implicit object TimePrimitiveToXml extends KmlToXml[Option[TimePrimitive]] {
    def toXml(timePrimitive: Option[TimePrimitive]): NodeSeq = {
      if (!timePrimitive.isDefined) NodeSeq.Empty
      else
        timePrimitive.get match {
          case timeStamp: TimeStamp =>
            <TimeStamp id={if (timeStamp.id.isDefined) timeStamp.id.get else null} targetId={if (timeStamp.targetId.isDefined) timeStamp.targetId.get else null}>
              {getNodeFromFieldName("when", Option(timeStamp))}
            </TimeStamp>
          case timeSpan: TimeSpan =>
            <TimeSpan id={if (timeSpan.id.isDefined) timeSpan.id.get else null} targetId={if (timeSpan.targetId.isDefined) timeSpan.targetId.get else null}>
              {getNodeFromFieldName("begin", Option(timeSpan))}
              {getNodeFromFieldName("end", Option(timeSpan))}
            </TimeSpan>
        }
    }
  }

  // TODO  how to get dataExtension
  implicit object DataToXml extends KmlToXml[Option[Data]] {
    def toXml(dataOption: Option[Data]): NodeSeq = {
      dataOption match {
        case Some(data) => <Data name={if (data.name.isDefined) data.name.get else null} id={if (data.id.isDefined) data.id.get else null} targetId={if (data.targetId.isDefined) data.targetId.get else null}>
          {getNodeFromFieldName("value", dataOption)}
          {getNodeFromFieldName("displayName", dataOption)}
        </Data>
        case None => NodeSeq.Empty
      }
    }
  }

  // TODO how to write other
  implicit object ExtendedDataToXml extends KmlToXml[Option[ExtendedData]] {
    def toXml(extendedDataOption: Option[ExtendedData]): NodeSeq = {
      extendedDataOption match {
        case Some(extendedData) => <ExtendedData>
          {if (extendedData.data != Nil) for (x <- extendedData.data) yield getXmlFrom(Option(x))}
          {if (extendedData.schemaData != Nil) for (x <- extendedData.schemaData) yield getXmlFrom(Option(x))}
        </ExtendedData>
        case None => NodeSeq.Empty
      }
    }
  }

  implicit object SchemaDataToXml extends KmlToXml[Option[SchemaData]] {
    def toXml(schemaDataOption: Option[SchemaData]): NodeSeq = {
      schemaDataOption match {
        case Some(schemaData) => <SchemaData id={if (schemaData.id.isDefined) schemaData.id.get else null} targetId={if (schemaData.targetId.isDefined) schemaData.targetId.get else null}>
          {if (schemaData.simpleData != Nil) for (x <- schemaData.simpleData) yield getXmlFrom(Option(x))}
          {getNodeFromFieldName("schemaUrl", schemaDataOption)}
        </SchemaData>
        case None => NodeSeq.Empty
      }
    }
  }

  implicit object SimpleDataToXml extends KmlToXml[Option[SimpleData]] {
    def toXml(simpleDataOption: Option[SimpleData]): NodeSeq = {
      simpleDataOption match {
        case Some(simpleData) => <simpleData>
          {for (field <- simpleData.getClass.getDeclaredFields) yield getNodeFromFieldName(field.getName, simpleDataOption)}
        </simpleData>
        case None => NodeSeq.Empty
      }
    }
  }

  implicit object FolderToXml extends KmlToXml[Option[Folder]] {
    def toXml(folderOption: Option[Folder]): NodeSeq = {
      folderOption match {
        case Some(folder) => <Folder id={if (folder.id.isDefined) folder.id.get else null} targetId={if (folder.targetId.isDefined) folder.targetId.get else null}>
          {getXmlSeqFrom(Option(folder.featurePart))}
          {for (f <- folder.features) yield getXmlFrom(Option(f))}
        </Folder>
        case None => NodeSeq.Empty
      }
    }
  }

  implicit object LatLonBoxToXml extends KmlToXml[Option[LatLonBox]] {
    def toXml(latLonBoxOption: Option[LatLonBox]): NodeSeq = {
      latLonBoxOption match {
        case Some(latLonBox) => <LatLonBox id={if (latLonBox.id.isDefined) latLonBox.id.get else null} targetId={if (latLonBox.targetId.isDefined) latLonBox.targetId.get else null}>
          {for (field <- latLonBox.getClass.getDeclaredFields) yield getNodeFromFieldName(field.getName, latLonBoxOption)}
        </LatLonBox>
        case None => NodeSeq.Empty
      }
    }
  }

  implicit object LatLonAltBoxToXml extends KmlToXml[Option[LatLonAltBox]] {
    def toXml(latLonAltBoxOption: Option[LatLonAltBox]): NodeSeq = {
      latLonAltBoxOption match {
        case Some(latLonAltBox) => <LatLonAltBox id={if (latLonAltBox.id.isDefined) latLonAltBox.id.get else null} targetId={if (latLonAltBox.targetId.isDefined) latLonAltBox.targetId.get else null}>
          {for (field <- latLonAltBox.getClass.getDeclaredFields) yield getNodeFromFieldName(field.getName, latLonAltBoxOption)}
        </LatLonAltBox>
        case None => NodeSeq.Empty
      }
    }
  }

  implicit object GroundOverlayToXml extends KmlToXml[Option[GroundOverlay]] {
    def toXml(overlayOption: Option[GroundOverlay]): NodeSeq = {
      overlayOption match {
        case Some(overlay) => <GroundOverlay id={if (overlay.id.isDefined) overlay.id.get else null} targetId={if (overlay.targetId.isDefined) overlay.targetId.get else null}>
          {getXmlSeqFrom(Option(overlay.featurePart))}
          {getNodeFromFieldName("altitude", overlayOption)}
          {getNodeFromFieldName("drawOrder", overlayOption)}
          {getNodeFromFieldName("altitudeMode", overlayOption)}
          {getXmlFrom(overlay.latLonBox)}
          {getXmlFrom(overlay.color)}
          {getNodeFromFieldName("drawOrder", overlayOption)}
          {getXmlFrom(overlay.icon)}
        </GroundOverlay>
        case None => NodeSeq.Empty
      }
    }
  }

  implicit object ViewVolumeToXml extends KmlToXml[Option[ViewVolume]] {
    def toXml(viewVolumeOption: Option[ViewVolume]): NodeSeq = {
      viewVolumeOption match {
        case Some(viewVolume) =>  <ViewVolume id={if (viewVolume.id.isDefined) viewVolume.id.get else null} targetId={if (viewVolume.targetId.isDefined) viewVolume.targetId.get else null}>
          {getNodeFromFieldName("leftFov", viewVolumeOption)}
          {getNodeFromFieldName("rightFov", viewVolumeOption)}
          {getNodeFromFieldName("bottomFov", viewVolumeOption)}
          {getNodeFromFieldName("topFov", viewVolumeOption)}
          {getNodeFromFieldName("near", viewVolumeOption)}
        </ViewVolume>
        case None => NodeSeq.Empty
      }
    }
  }

  implicit object ImagePyramidToXml extends KmlToXml[Option[ImagePyramid]] {
    def toXml(imagePyramidOption: Option[ImagePyramid]): NodeSeq = {
      imagePyramidOption match {
        case Some(imagePyramid) => <ImagePyramid id={if (imagePyramid.id.isDefined) imagePyramid.id.get else null} targetId={if (imagePyramid.targetId.isDefined) imagePyramid.targetId.get else null}>
          {getNodeFromFieldName("tileSize", imagePyramidOption)}
          {getNodeFromFieldName("maxWidth", imagePyramidOption)}
          {getNodeFromFieldName("maxHeight", imagePyramidOption)}
          {getNodeFromFieldName("gridOrigin", imagePyramidOption)}
        </ImagePyramid>
        case None => NodeSeq.Empty
      }
    }
  }

  implicit object PhotoOverlayToXml extends KmlToXml[Option[PhotoOverlay]] {
    def toXml(overlayOption: Option[PhotoOverlay]): NodeSeq = {
      overlayOption match {
        case Some(overlay) => <PhotoOverlay id={if (overlay.id.isDefined) overlay.id.get else null} targetId={if (overlay.targetId.isDefined) overlay.targetId.get else null}>
          {getXmlSeqFrom(Option(overlay.featurePart))}
          {getNodeFromFieldName("rotation", overlayOption)}
          {getXmlFrom(overlay.viewVolume)}
          {getXmlFrom(overlay.imagePyramid)}
          {getXmlFrom(overlay.point.asInstanceOf[Option[Geometry]])}
          {getNodeFromFieldName("shape", overlayOption)}
          {getXmlFrom(overlay.color)}
          {getNodeFromFieldName("drawOrder", overlayOption)}
          {getXmlFrom(overlay.icon)}
        </PhotoOverlay>
        case None => NodeSeq.Empty
      }
    }
  }

  implicit object ScreenOverlayToXml extends KmlToXml[Option[ScreenOverlay]] {
    def toXml(overlayOption: Option[ScreenOverlay]): NodeSeq = {
      overlayOption match {
        case Some(overlay) => <ScreenOverlay id={if (overlay.id.isDefined) overlay.id.get else null} targetId={if (overlay.targetId.isDefined) overlay.targetId.get else null}>
          {getXmlSeqFrom(Option(overlay.featurePart))}
          {getNodeFromFieldName("overlayXY", overlayOption)}
          {getNodeFromFieldName("screenXY", overlayOption)}
          {getNodeFromFieldName("rotationXY", overlayOption)}
          {getNodeFromFieldName("size", overlayOption)}
          {getNodeFromFieldName("rotation", overlayOption)}
          {getXmlFrom(overlay.color)}
          {getNodeFromFieldName("drawOrder", overlayOption)}
          {getXmlFrom(overlay.icon)}
        </ScreenOverlay>
        case None => NodeSeq.Empty
      }
    }
  }

  implicit object makeNetworkLinkToXml extends KmlToXml[Option[NetworkLink]] {
    def toXml(networkLinkOption: Option[NetworkLink]): NodeSeq = {
      networkLinkOption match {
        case Some(networkLink) => <NetworkLink id={if (networkLink.id.isDefined) networkLink.id.get else null} targetId={if (networkLink.targetId.isDefined) networkLink.targetId.get else null}>
          {getXmlSeqFrom(Option(networkLink.featurePart))}
          {getNodeFromFieldName("refreshVisibility", networkLinkOption)}
          {getNodeFromFieldName("flyToView", networkLinkOption)}
          {getNodeFromFieldName("refreshVisibility", networkLinkOption)}
          {getXmlFrom(networkLink.link)}
        </NetworkLink>
        case None => NodeSeq.Empty
      }
    }
  }

  implicit object LinkToXml extends KmlToXml[Option[com.scalakml.kml.Link]] {
    def toXml(linkOption: Option[com.scalakml.kml.Link]): NodeSeq = {
      linkOption match {
        case Some(link) => <Link id={if (link.id.isDefined) link.id.get else null} targetId={if (link.targetId.isDefined) link.targetId.get else null}>
          {for (field <- link.getClass.getDeclaredFields) yield getNodeFromFieldName(field.getName, linkOption)}
        </Link>
        case None => NodeSeq.Empty
      }
    }
  }

  implicit object IconTypeToXml extends KmlToXml[Option[Icon]] {
    def toXml(linkOption: Option[Icon]): NodeSeq = {
      linkOption match {
        case Some(link) => <Icon id={if (link.id.isDefined) link.id.get else null} targetId={if (link.targetId.isDefined) link.targetId.get else null}>
          {for (field <- link.getClass.getDeclaredFields) yield getNodeFromFieldName(field.getName, linkOption)}
        </Icon>
        case None => NodeSeq.Empty
      }
    }
  }

  implicit object AliasToXml extends KmlToXml[Option[Alias]] {
    def toXml(aliasOption: Option[Alias]): NodeSeq = {
      aliasOption match {
        case Some(alias) => <Alias id={if (alias.id.isDefined) alias.id.get else null} targetId={if (alias.targetId.isDefined) alias.targetId.get else null}>
          {getNodeFromFieldName("targetHref", aliasOption)}
          {getNodeFromFieldName("sourceHref", aliasOption)}
        </Alias>
        case None => NodeSeq.Empty
      }
    }
  }

  implicit object ResourceMapToXml extends KmlToXml[Option[ResourceMap]] {
    def toXml(resourceMapOption: Option[ResourceMap]): NodeSeq = {
      resourceMapOption match {
        case Some(resourceMap) => <ResourceMap id={if (resourceMap.id.isDefined) resourceMap.id.get else null} targetId={if (resourceMap.targetId.isDefined) resourceMap.targetId.get else null}>
          {for (x <- resourceMap.alias) yield getXmlFrom(Option(x))}
        </ResourceMap>
        case None => NodeSeq.Empty
      }
    }
  }

  implicit object ScaleToXml extends KmlToXml[Option[Scale]] {
    def toXml(scaleOption: Option[Scale]): NodeSeq = {
      scaleOption match {
        case Some(scale) => <Scale>
          { for (field <- scale.getClass.getDeclaredFields) yield getNodeFromFieldName(field.getName, scaleOption) }
        </Scale>
        case None => NodeSeq.Empty
      }
    }
  }

  implicit object OrientationToXml extends KmlToXml[Option[Orientation]] {
    def toXml(orientationOption: Option[Orientation]): NodeSeq = {
      orientationOption match {
        case Some(orientation) => <Orientation id={if (orientation.id.isDefined) orientation.id.get else null} targetId={if (orientation.targetId.isDefined) orientation.targetId.get else null}>
          { for (field <- orientation.getClass.getDeclaredFields) yield getNodeFromFieldName(field.getName, orientationOption) }
        </Orientation>
        case None => NodeSeq.Empty
      }
    }
  }

  implicit object LocationToXml extends KmlToXml[Option[Location]] {
    def toXml(locationOption: Option[Location]): NodeSeq = {
      locationOption match {
        case Some(location) => <Location id={if (location.id.isDefined) location.id.get else null} targetId={if (location.targetId.isDefined) location.targetId.get else null}>
          { for (field <- location.getClass.getDeclaredFields) yield getNodeFromFieldName(field.getName, locationOption) }
        </Location>
        case None => NodeSeq.Empty
      }
    }
  }

  implicit object CoordinatesToXml extends KmlToXml[Option[Seq[Location]]] {
    def toXml(coordsOption: Option[Seq[Location]]): NodeSeq = {
      coordsOption match {
        case Some(coords) =>
          <coordinates>
            {for (x <- coords) yield {
            if (x.longitude.isDefined && x.latitude.isDefined) {
              x.longitude.get.toString + "," + x.latitude.get.toString + (if (x.altitude.isDefined) "," + x.altitude.get.toString else "") + "\n"
            }
          }}
          </coordinates>
        case None => NodeSeq.Empty
      }
    }
  }

  implicit object HexColorToXml extends KmlToXml[Option[HexColor]] {
    def toXml(colorOption: Option[HexColor]): NodeSeq = {
      colorOption match {
        case Some(color) => <color>
          {if ((color.hexString != null) && !color.hexString.isEmpty) color.hexString else null}
        </color>
        case None => NodeSeq.Empty
      }
    }
  }

  // TODO check the default value
  implicit object SnippetToXml extends KmlToXml[Option[Snippet]] {
    def toXml(snippetOption: Option[Snippet]): NodeSeq = {
      snippetOption match {
        case Some(snippet) => <snippet maxLines={if (snippet.maxLines >= 0) snippet.maxLines.toString else null}>
          {if ((snippet.value != null) && (!snippet.value.isEmpty)) snippet.value else null}
        </snippet>
        case None => NodeSeq.Empty
      }
    }
  }

  implicit object LodToXml extends KmlToXml[Option[Lod]] {
    def toXml(lodOption: Option[Lod]): NodeSeq = {
      lodOption match {
        case Some(lod) => <Lod id={if (lod.id.isDefined) lod.id.get else null} targetId={if (lod.targetId.isDefined) lod.targetId.get else null}>
          {for (field <- lod.getClass.getDeclaredFields) yield getNodeFromFieldName(field.getName, lodOption)}
        </Lod>
        case None => NodeSeq.Empty
      }
    }
  }

  implicit object RegionToXml extends KmlToXml[Option[Region]] {
    def toXml(regionOption: Option[Region]): NodeSeq = {
      regionOption match {
        case Some(region) => <Region id={if (region.id.isDefined) region.id.get else null} targetId={if (region.targetId.isDefined) region.targetId.get else null}>
          {getXmlFrom(region.latLonAltBox)}
          {getXmlFrom(region.lod)}
        </Region>
        case None => NodeSeq.Empty
      }
    }
  }

  implicit object GeometryToXml extends KmlToXml[Option[Geometry]] {
    def toXml(geometry: Option[Geometry]): NodeSeq = {
      if (!geometry.isDefined) NodeSeq.Empty
      else
        geometry.get match {

          case point: Point => {
            <Point id={if (point.id.isDefined) point.id.get else null} targetId={if (point.targetId.isDefined) point.targetId.get else null}>
              {getNodeFromFieldName("extrude", Option(point))}
              {getNodeFromFieldName("altitudeMode", Option(point))}
              {getXmlFrom(point.coordinates)}
            </Point>
          }

          case lineString: LineString =>
            <LineString id={if (lineString.id.isDefined) lineString.id.get else null} targetId={if (lineString.targetId.isDefined) lineString.targetId.get else null}>
              {getNodeFromFieldName("extrude", Option(lineString))}
              {getNodeFromFieldName("tessellate", Option(lineString))}
              {getNodeFromFieldName("altitudeMode", Option(lineString))}
              {getXmlFrom(lineString.coordinates)}
            </LineString>

          case linearRing: LinearRing =>
            <LinearRing id={if (linearRing.id.isDefined) linearRing.id.get else null} targetId={if (linearRing.targetId.isDefined) linearRing.targetId.get else null}>
              {getNodeFromFieldName("extrude", Option(linearRing))}
              {getNodeFromFieldName("tessellate", Option(linearRing))}
              {getNodeFromFieldName("altitudeMode", Option(linearRing))}
              {getXmlFrom(linearRing.coordinates)}
            </LinearRing>

          case polygon: Polygon =>
            <Polygon id={if (polygon.id.isDefined) polygon.id.get else null} targetId={if (polygon.targetId.isDefined) polygon.targetId.get else null}>
              {getNodeFromFieldName("extrude", Option(polygon))}
              {getNodeFromFieldName("tessellate", Option(polygon))}
              {getNodeFromFieldName("altitudeMode", Option(polygon))}
              {if (polygon.outerBoundaryIs.isDefined)
              <outerBoundaryIs>
                {getXmlFrom(Option(polygon.outerBoundaryIs.get.linearRing.get.asInstanceOf[Geometry]))}
              </outerBoundaryIs>}
              {if (polygon.innerBoundaryIs != Nil)
              <innerBoundaryIs>
                {for (s <- polygon.innerBoundaryIs) yield getXmlFrom(Option(s.linearRing.get.asInstanceOf[Geometry]))}
              </innerBoundaryIs>}
            </Polygon>

          case multiGeometry: MultiGeometry =>
            <MultiGeometry id={if (multiGeometry.id.isDefined) multiGeometry.id.get else null} targetId={if (multiGeometry.targetId.isDefined) multiGeometry.targetId.get else null}>
              {for (x <- multiGeometry.geometries) yield getXmlFrom(Option(x))}
            </MultiGeometry>

          case model: Model =>
            <Model id={if (model.id.isDefined) model.id.get else null} targetId={if (model.targetId.isDefined) model.targetId.get else null}>
              {getNodeFromFieldName("altitudeMode", Option(model))}
              {getXmlFrom(model.location)}
              {getXmlFrom(model.orientation)}
              {getXmlFrom(model.scale)}
              {getXmlFrom(model.link)}
              {getXmlFrom(model.resourceMap)}
            </Model>

          case _ => NodeSeq.Empty
        }
    }
  }

  implicit object FeatureToXml extends KmlToXml[Option[Feature]] {
    def toXml(feature: Option[Feature]): NodeSeq = {
      import TourToXml._
      if (!feature.isDefined) NodeSeq.Empty
      else
        feature.get match {
          case placemark: Placemark => getXmlFrom(Option(placemark))
          case networkLink: NetworkLink => getXmlFrom(Option(networkLink))
          case tour: Tour => getXmlFrom(Option(tour))
          case document: Document => getXmlFrom(Option(document))
          case folder: Folder => getXmlFrom(Option(folder))
          case groundOverlay: GroundOverlay => getXmlFrom(Option(groundOverlay))
          case screenOverlay: ScreenOverlay => getXmlFrom(Option(screenOverlay))
          case photoOverlay: PhotoOverlay => getXmlFrom(Option(photoOverlay))
          case _ => NodeSeq.Empty
        }
    }
  }

  implicit object ContainerToXml extends KmlToXml[Option[Container]] {
    def toXml(container: Option[Container]): NodeSeq = {
      if (!container.isDefined) NodeSeq.Empty
      else
        container.get match {
          case document: Document => getXmlFrom(container)
          case folder: Folder => getXmlFrom(container)
          case _ => NodeSeq.Empty
        }
    }
  }

  implicit object StyleSelectorSeqToXml extends KmlToXmlSeq[Option[Seq[StyleSelector]]] {
    def toXml(styleSet: Option[Seq[StyleSelector]]): Seq[NodeSeq] = {
      styleSet match {
        case Some(styles) => (styles collect {
          case x => getXmlFrom(Option(x.asInstanceOf[StyleSelector]))
        } filter (x => (x != null) && (x != None)) toSeq)
        case None => Seq.empty
      }
    }
  }

  implicit object PairSeqToXml extends KmlToXmlSeq[Option[Seq[Pair]]] {
    def toXml(pairSet: Option[Seq[Pair]]): Seq[NodeSeq] = {
      pairSet match {
        case Some(pSet) => (pSet collect {
          case x => getXmlFrom(Option(x.asInstanceOf[Pair]))
        } filter (x => (x != null) && (x != None)) toSeq)
        case None => Seq.empty
      }
    }
  }

  implicit object UpdateOptionSeqToXml extends KmlToXmlSeq[Option[Seq[UpdateOption]]] {
    def toXml(updateOptionSet: Option[Seq[UpdateOption]]): Seq[NodeSeq] = {
      updateOptionSet match {
        case Some(uSet) => (uSet collect {
          case x => getXmlFrom(Option(x.asInstanceOf[UpdateOption]))
        } filter (x => (x != null) && (x != None)) toSeq)
        case None => Seq.empty
      }
    }
  }

  implicit object ItemIconSeqToXml extends KmlToXmlSeq[Option[Seq[ItemIcon]]] {
    def toXml(itemIconSet: Option[Seq[ItemIcon]]): Seq[NodeSeq] = {
      itemIconSet match {
        case Some(iSet) => (iSet collect {
          case x => getXmlFrom(Option(x.asInstanceOf[ItemIcon]))
        } filter (x => (x != null) && (x != None)) toSeq)
        case None => Seq.empty
      }
    }
  }

  implicit object IconStateSeqToXml extends KmlToXmlSeq[Option[Seq[ItemIconState]]] {
    def toXml(itemIconStateSet: Option[Seq[ItemIconState]]): Seq[NodeSeq] = {
      itemIconStateSet match {
        case Some(iSet) => (iSet collect {
          case x => getXmlFrom(Option(x.asInstanceOf[ItemIconState]))
        } filter (x => (x != null) && (x != None)) toSeq)
        case None => Seq.empty
      }
    }
  }

  //TODO AddressDetails from the scalaxal library
  implicit object FeaturePartToXml extends KmlToXmlSeq[Option[FeaturePart]] {
    def toXml(featurePart: Option[FeaturePart]): Seq[NodeSeq] = {
      if (!featurePart.isDefined || (featurePart.get == null)) Seq.empty
      else {
        val list = new MutableList[NodeSeq]()

        list += getNodeFromFieldName("name", featurePart)
        list += getNodeFromFieldName("visibility", featurePart)
        list += getNodeFromFieldName("open", featurePart)
        list += getXmlFrom(featurePart.get.atomAuthor)
        list += getXmlFrom(featurePart.get.atomLink)
        list += getNodeFromFieldName("address", featurePart)
        list += getXmlFrom(featurePart.get.addressDetails)
        list += getNodeFromFieldName("phoneNumber", featurePart)
        list += getNodeFromFieldName("description", featurePart)
        list += getXmlFrom(featurePart.get.extendedData)
        list += getXmlFrom(featurePart.get.snippet)
        list += getXmlFrom(featurePart.get.region)
        list += getXmlFrom(featurePart.get.timePrimitive)
        list += getNodeFromFieldName("styleUrl", featurePart)
        list ++= getXmlSeqFrom(Option(featurePart.get.styleSelector))
        list += getXmlFrom(featurePart.get.abstractView)

        (list filter (x => (x != null) && (x != None)) toSeq)
      }
    }
  }

  // ------------------------------------------------------------
  // -----------------------def----------------------------------  
  // ------------------------------------------------------------  

  /** this is the crux of getting xml from the kml objects */
  def getXmlFrom[A: KmlToXml](kml: A) = implicitly[KmlToXml[A]].toXml(kml)

  def getXmlSeqFrom[A: KmlToXmlSeq](kml: A) = implicitly[KmlToXmlSeq[A]].toXml(kml)

  def getNodeFromFieldName(name: String, objOption: Option[Any]): NodeSeq = {
    val baseName = if(name.startsWith("gx:")) name.substring(3) else name
    objOption match {
      case Some(obj) => {
        Some(obj.getClass.getDeclaredField(baseName)) match {
          case Some(field) => {
            field.setAccessible(true)
            val fieldValue = field.get(obj)
            if (fieldValue == null || !fieldValue.isInstanceOf[Option[_]]) NodeSeq.Empty
            else makeXmlNode(name, fieldValue.asInstanceOf[Option[_]])
          }
          case _ => NodeSeq.Empty
        }
      }
      case None => NodeSeq.Empty
    }
  }

  def makeXmlNode[_](name: String, value: Option[_]): NodeSeq = {
    if (value.isDefined) {
      value.get match {
        case bool: Boolean => <a> {if (bool) "1" else "0"} </a>.copy(label = name)
        case vec2: Vec2 => {
          val theNode = <a/> % Attribute(None, "x", Text(vec2.x.toString), Null) % Attribute(None, "y", Text(vec2.y.toString), Null) % Attribute(None, "xunits", Text(vec2.xunits.toString), Null) % Attribute(None, "yunits", Text(vec2.yunits.toString), Null)
          theNode.copy(label = name)
        }
        case _ => <a> {value.get} </a>.copy(label = name)
      }
    } else NodeSeq.Empty
  }

//-----------------------------------------------------------------------------------------------
//----------------------------------gx-----------------------------------------------------------
//-----------------------------------------------------------------------------------------------

// Note: the AlitudeMode currently does not have the gx: namespace

  implicit object PlaylistToXml extends KmlToXml[Option[Playlist]] {
    def toXml(playlistOption: Option[Playlist]): NodeSeq = {
      import TourPrimitiveToXml._
      playlistOption match {
        case Some(playlist) =>
          if ((!playlist.tourPrimitiveGroup.isDefined) || (playlist.tourPrimitiveGroup.get == Nil)) NodeSeq.Empty
          else
            <gx:Playlist id={if (playlist.id.isDefined) playlist.id.get else null} targetId={if (playlist.targetId.isDefined) playlist.targetId.get else null}>
              {for (tourPrimitive <- playlist.tourPrimitiveGroup.get) yield getXmlFrom(Option(tourPrimitive))}
            </gx:Playlist>
        case None => NodeSeq.Empty
      }
    }
  }

  implicit object TourToXml extends KmlToXml[Option[Tour]] {
    def toXml(tourOption: Option[Tour]): NodeSeq = {
      tourOption match {
        case Some(tour) => <gx:Tour id={if (tour.id.isDefined) tour.id.get else null} targetId={if (tour.targetId.isDefined) tour.targetId.get else null}>
          {getXmlFrom(tour.playlist)}
          {getXmlSeqFrom(Option(tour.featurePart))}
        </gx:Tour>
        case None => NodeSeq.Empty
      }
    }
  }

  implicit object AnimatedUpdateToXml extends KmlToXml[Option[AnimatedUpdate]] {
    def toXml(animatedUpdateOption: Option[AnimatedUpdate]): NodeSeq = {
      animatedUpdateOption match {
        case Some(animatedUpdate) => <gx:AnimatedUpdate id={if (animatedUpdate.id.isDefined) animatedUpdate.id.get else null} targetId={if (animatedUpdate.targetId.isDefined) animatedUpdate.targetId.get else null}>
          {getNodeFromFieldName("gx:duration", animatedUpdateOption)}
          {getXmlFrom(animatedUpdate.update)}
        </gx:AnimatedUpdate>
        case None => NodeSeq.Empty
      }
    }
  }

  implicit object FlyToToXml extends KmlToXml[Option[FlyTo]] {
    def toXml(flyToOption: Option[FlyTo]): NodeSeq = {
      flyToOption match {
        case Some(flyTo) => <gx:FlyTo id={if (flyTo.id.isDefined) flyTo.id.get else null} targetId={if (flyTo.targetId.isDefined) flyTo.targetId.get else null}>
          {getNodeFromFieldName("gx:duration", flyToOption)}
          {getNodeFromFieldName("gx:flyToMode", flyToOption)}
          {getXmlFrom(flyTo.abstractView)}
        </gx:FlyTo>
        case None => NodeSeq.Empty
      }
    }
  }

  implicit object SoundCueToXml extends KmlToXml[Option[SoundCue]] {
    def toXml(soundCueOption: Option[SoundCue]): NodeSeq = {
      soundCueOption match {
        case Some(soundCue) => <gx:FlyTo id={if (soundCue.id.isDefined) soundCue.id.get else null} targetId={if (soundCue.targetId.isDefined) soundCue.targetId.get else null}>
          {getNodeFromFieldName("href", soundCueOption)}
        </gx:FlyTo>
        case None => NodeSeq.Empty
      }
    }
  }

  implicit object TourControlToXml extends KmlToXml[Option[TourControl]] {
    def toXml(tourControlOption: Option[TourControl]): NodeSeq = {
      tourControlOption match {
        case Some(tourControl) => <gx:TourControl id={if (tourControl.id.isDefined) tourControl.id.get else null} targetId={if (tourControl.targetId.isDefined) tourControl.targetId.get else null}>
          {getNodeFromFieldName("gx:playMode", tourControlOption)}
        </gx:TourControl>
        case None => NodeSeq.Empty
      }
    }
  }

  implicit object WaitToXml extends KmlToXml[Option[Wait]] {
    def toXml(waitOption: Option[Wait]): NodeSeq = {
      waitOption match {
        case Some(waitt) => <gx:Wait id={if (waitt.id.isDefined) waitt.id.get else null} targetId={if (waitt.targetId.isDefined) waitt.targetId.get else null}>
          {getNodeFromFieldName("gx:duration", waitOption)}
        </gx:Wait>
        case None => NodeSeq.Empty
      }
    }
  }

  implicit object LatLonQuadToXml extends KmlToXml[Option[LatLonQuad]] {
    def toXml(latLonQuadOption: Option[LatLonQuad]): NodeSeq = {
      latLonQuadOption match {
        case Some(latLonQuad) => <gx:LatLonQuad id={if (latLonQuad.id.isDefined) latLonQuad.id.get else null} targetId={if (latLonQuad.targetId.isDefined) latLonQuad.targetId.get else null}>
          {getXmlFrom(latLonQuad.coordinates)}
        </gx:LatLonQuad>
        case None => NodeSeq.Empty
      }
    }
  }

  implicit object TourPrimitiveToXml extends KmlToXml[Option[TourPrimitive]] {
    def toXml(tourPrimitive: Option[TourPrimitive]): NodeSeq = {
      if (!tourPrimitive.isDefined) NodeSeq.Empty
      else
        tourPrimitive.get match {
          case animatedUpdate: AnimatedUpdate => getXmlFrom(Option(animatedUpdate))
          case flyTo: FlyTo => getXmlFrom(Option(flyTo))
          case soundCue: SoundCue => getXmlFrom(Option(soundCue))
          case waitt: Wait => getXmlFrom(Option(waitt))
          case tourControl: TourControl => getXmlFrom(Option(tourControl))
          case _ => NodeSeq.Empty
        }
    }
  }

}
