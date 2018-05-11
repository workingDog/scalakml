package com.scalakml

import java.io.Writer

import com.scalakml.kml._
//import com.scalaxal.xAL.{AddressLine, AddressLines, Address, AddressDetails}

import scala.language.implicitConversions

/**
  * a set of implicits. Use with care.
  *
  */
object KmlImplicits {


//  implicit def StringToAddressDetails(value: String): AddressDetails =
//    new AddressDetails(addressDetailsType = new AddressLines(Seq.empty :+ new AddressLine(value)))
//
//  implicit def StringToAddressDetailsOp(value: String): Option[AddressDetails] =
//    Option(new AddressDetails(addressDetailsType = Option(new AddressLines(Seq.empty :+ new AddressLine(value)))))

  // ------------------X to Option[X]-------------------------------------------------------------------------

  implicit def StringToStringOp(value: String): Option[String] = Option(value)

  implicit def DoubleToDoubleOp(value: Double): Option[Double] = Option(value)

  implicit def IntToIntOp(value: Int): Option[Int] = Option(value)

  implicit def BooleanToBooleanOp(value: Boolean): Option[Boolean] = Option(value)

  implicit def Vec2ToVec2Op(value: Vec2): Option[Vec2] = Option(value)

  implicit def RegionToRegionOp(value: Region): Option[Region] = Option(value)

  implicit def LatLonAltBoxToLatLonAltBoxOp(value: LatLonAltBox): Option[LatLonAltBox] = Option(value)

  implicit def LodToLodOp(value: Lod): Option[Lod] = Option(value)

  implicit def AltitudeModeToAltitudeModeOp(value: AltitudeMode): Option[AltitudeMode] = Option(value)

  implicit def LatLonBoxToLatLonBoxOp(value: LatLonBox): Option[LatLonBox] = Option(value)

  implicit def NetworkLinkToNetworkLinkOp(value: NetworkLink): Option[NetworkLink] = Option(value)

  implicit def IconToIconOp(value: Icon): Option[Icon] = Option(value)

  implicit def RefreshModeToRefreshModeOp(value: RefreshMode): Option[RefreshMode] = Option(value)

  implicit def ViewRefreshModeToViewRefreshModeOp(value: ViewRefreshMode): Option[ViewRefreshMode] = Option(value)

  implicit def LinkToLinkOp(value: Link): Option[Link] = Option(value)

  implicit def MultiGeometryToMultiGeometryOp(value: MultiGeometry): Option[MultiGeometry] = Option(value)

  implicit def PointToPointOp(value: Point): Option[Point] = Option(value)

  implicit def CoordinateToCoordinateOp(value: Coordinate): Option[Coordinate] = Option(value)

  implicit def LineStringToLineStringOp(value: LineString): Option[LineString] = Option(value)

  implicit def LinearRingToLinearRingOp(value: LinearRing): Option[LinearRing] = Option(value)

  implicit def PolygonToPolygonOp(value: Polygon): Option[Polygon] = Option(value)

  implicit def BoundaryToBoundaryOp(value: Boundary): Option[Boundary] = Option(value)

  implicit def ModelToModelOp(value: Model): Option[Model] = Option(value)

  implicit def LocationToLocationOp(value: Location): Option[Location] = Option(value)

  implicit def OrientationToOrientationOp(value: Orientation): Option[Orientation] = Option(value)

  implicit def ScaleToScaleOp(value: Scale): Option[Scale] = Option(value)

  implicit def ResourceMapToResourceMapOp(value: ResourceMap): Option[ResourceMap] = Option(value)

  implicit def AliasToAliasOp(value: Alias): Option[Alias] = Option(value)

  implicit def GroundOverlayToGroundOverlayOp(value: GroundOverlay): Option[GroundOverlay] = Option(value)

  implicit def HexColorToHexColorOp(value: HexColor): Option[HexColor] = Option(value)

  implicit def ScreenOverlayToScreenOverlayOp(value: ScreenOverlay): Option[ScreenOverlay] = Option(value)

  implicit def PhotoOverlayToPhotoOverlayOp(value: PhotoOverlay): Option[PhotoOverlay] = Option(value)

  implicit def ViewVolumeToViewVolumeOp(value: ViewVolume): Option[ViewVolume] = Option(value)

  implicit def ImagePyramidToImagePyramidOp(value: ImagePyramid): Option[ImagePyramid] = Option(value)

  implicit def StyleToStyleOp(value: Style): Option[Style] = Option(value)

  implicit def IconStyleToIconStyleOp(value: IconStyle): Option[IconStyle] = Option(value)

  implicit def LabelStyleToLabelStyleOp(value: LabelStyle): Option[LabelStyle] = Option(value)

  implicit def LineStyleToLineStyleOp(value: LineStyle): Option[LineStyle] = Option(value)

  implicit def PolyStyleToPolyStyleOp(value: PolyStyle): Option[PolyStyle] = Option(value)

  implicit def BalloonStyleToBalloonStyleOp(value: BalloonStyle): Option[BalloonStyle] = Option(value)

  implicit def ListStyleToListStyleOp(value: ListStyle): Option[ListStyle] = Option(value)

  implicit def StyleMapToStyleMapOp(value: StyleMap): Option[StyleMap] = Option(value)

  implicit def StyleStateToStyleStateOp(value: StyleState): Option[StyleState] = Option(value)

  implicit def StyleSelectorToStyleSelectorOp(value: StyleSelector): Option[StyleSelector] = Option(value)

  implicit def ColorModeToColorModeOp(value: ColorMode): Option[ColorMode] = Option(value)

  implicit def DisplayModeToDisplayModeOp(value: DisplayMode): Option[DisplayMode] = Option(value)

  implicit def ListItemTypeToListItemTypeOp(value: ListItemType): Option[ListItemType] = Option(value)

  implicit def ItemIconToItemIconOp(value: ItemIcon): Option[ItemIcon] = Option(value)

  implicit def TimeStampToTimeStampOp(value: TimeStamp): Option[TimeStamp] = Option(value)

  implicit def TimeSpanToTimeSpanOp(value: TimeSpan): Option[TimeSpan] = Option(value)

  implicit def UpdateToUpdateOp(value: Update): Option[Update] = Option(value)

  implicit def PlacemarkToPlacemarkOp(value: Placemark): Option[Placemark] = Option(value)

  implicit def GeometryToGeometryOp(value: Geometry): Option[Geometry] = Option(value)

  implicit def DocumentToDocumentOp(value: Document): Option[Document] = Option(value)

  implicit def FolderToFolderOp(value: Folder): Option[Folder] = Option(value)

  implicit def LookAtToLookAtOp(value: LookAt): Option[LookAt] = Option(value)

  implicit def CameraToCameraOp(value: Camera): Option[Camera] = Option(value)

  implicit def ExtendedDataToExtendedDataOp(value: ExtendedData): Option[ExtendedData] = Option(value)

  implicit def SchemaDataToSchemaDataOp(value: SchemaData): Option[SchemaData] = Option(value)

  implicit def DataToDataOp(value: Data): Option[Data] = Option(value)

  implicit def WriterToWriterOp(value: Writer): Option[Writer] = Option(value)

}
