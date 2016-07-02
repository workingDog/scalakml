# scalakml is a KML Version 2.2 library written in scala.


## KML an Open Geospatial Consortium (OGC) standard

From reference 1, "KML is an XML language focused on geographic visualization, including annotation of maps and images. Geographic visualization includes not only the presentation of graphical data on the globe, but also the control of the user's navigation in the sense of where to go and where to look."

Specifically KML is used in Google Earth to display various geographic elements, such as; images, 
place marks, polygon shapes, 3D models, etc...

The **scalakml** library provides the KML elements as scala classes. 

## References
 
1) [OGC 07-147r2 Version: 2.2.0, Category: OGC Standard, Editor: Tim Wilson](http://www.opengeospatial.org/standards/kml)

2) [Google developers KML Reference](https://developers.google.com/kml/documentation/kmlreference)

## Packages

The **scalakml** library consists of 4 major packages:
- 1) com.scalakml.kml, the set of KML classes
- 2) com.scalakml.gx, the Google's GX extensions classes derived from the official kml22gx.xsd
- 3) com.scalakml.io, the reading and writing of kml elements
- 4) com.scalakml.atom, the Atom classes

## Installation

Add the following dependency to build.sbt:

    libraryDependencies += "com.github.workingDog" %% "scalakml" % "1.0"

To compile and generate a jar file from the source:

    sbt package

The jar file (scalakml_2.11-1.0.jar) will be in the "./target/scala-2.11" directory.

## Dependencies

**scalakml** depends on a companion library called [scalaxal](https://github.com/workingDog/scalaxal). 
**scalaxal** has the xAL classes needed for the AddressDetails. 

**scalakml** depends also on the [Scala XML library](https://github.com/scala/scala-xml).

See also build.sbt.

## Documentation

See reference 1 and 2 for the full documentation of kml.
Most of the kml documentation are included the code documentation.

Most kml elements described in the references have been implemented with the same or similar class name.
There is one exception, and that is the FeaturePart.
This class represents the abstract feature part described in the references.
Here it is a concrete class called FeaturePart and holds the elements of the abstract feature.
Such that, for example, Placemark has a featurePart element, so to get the name of the placemark
you could write:
- placemark.featurePart.name

The following (Feature) classes use FeaturePart:
Document, Folder, Placemark, NetworkLink, PhotoOverlay, ScreenOverlay, GroundOverlay and Tour.
A FeaturePart can be created and is typically added as an element to: Document, Folder, Placemark, etc...

Example [kml files] (http://code.google.com/p/kml-samples/) are included in the kml-files directory.
The file KML_Samples.kml contains CDATA elements. The same file without
the CDATA elements (example-no-cdata.kml) is also included.

To generate the scaladoc, type: sbt doc

## Usage

    object WriteExample4 {
      def main(args: Array[String])  {
        // create a scala Kml object with a Placemark that contains a Point
        val kml = new Kml(new Placemark("Sydney", new Point(RelativeToGround, 151.21037, -33.8526, 12345.0)))
        // write the kml object to System.out 
        new KmlPrintWriter().write(kml, new PrettyPrinter(80, 3))
      }
    }

Note, in some situations it maybe an overkill to depend on the **scalaxal** library just for the "addressDetails".
It can sometimes be adequate to use just the "address" field.

To remove the dependency on **scalaxal** and hence remove the "addressDetails" field from the FeaturePart,

in Kml.scala:

    comment out line 34 and remove addressDetails field at line 983, 1000 and 1004

in KmlFromXml.scala:

    comment out line 36 and 448.

in KmlToXml.scala:

    comment out line 38, 39 and 1022 and the AddressDetailsToXml implicit starting at line 79

in KmlImplicits:

    comment out line 6 and the StringToAddressDetails and StringToAddressDetailsOp implicits.


## Status

Stable.

The gx: extension has what is in the official kml22gx.xsd schema,
it does not include some of the gx: elements from Google listed in reference 2.

Using scala 2.11.8 and java 8 

