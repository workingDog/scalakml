# scalakml is a KML Version 2.2 library written in scala.

## References
 
1) OGC 07-147r2 Version: 2.2.0, Category: OGC Standard, Editor: Tim Wilson, at http://www.opengeospatial.org/standards/kml

2) Google developers KML Reference, at https://developers.google.com/kml/documentation/kmlreference

## History

Scalakml is an exercise I'm doing to learn the basics of scala, the result of which may be useful to others.
After reading a book on scala I wanted to learn it by coding something. So I decided to write 
a KML 2.2 library in scala. I quickly found a way to produce such a library without any coding, 
by using the excellent scalaxb, the XML data-binding tool for Scala, see http://scalaxb.org/. 

However after looking at the generated class names and the way they would have to be used,
I decided to make a number of changes to the generated code.
I renamed a lot of the classes to be more like reference 2. 
As a result of the changes I broke the generated reading and writing of kml. 

To pursue the original idea of coding something to learn scala, I started coding (longhand) the reading and writing of the kml objects.
From this I learned a lot about scala (and cutting and pasting of code).

## Packages

The scalakml library is in 4 major parts:
- 1) package com.scalakml.kml, the set of KML classes
- 2) package com.scalakml.gx, the Google's GX extensions from the official kml22gx.xsd 
- 3) package com.scalakml.io, the simple reading and writing of kml from/to xml
- 4) package com.scalakml.atom, the Atom classes

## Dependencies

scalakml depends on a companion library called scalaxal. This library has the xAL classes 
needed for the AddressDetails. scalaxal is under developemnt in 
another repository, see https://github.com/workingDog/scalaxal. 
The scalaxal.jar is included here in the lib directory.

## Documentation

The documentation is from reference 1 and 2.
I've copied and pasted some documentation from the references into the classes, 
but there is still more to be done. 

Most kml elements described in the references have been implemented (I think) with the same or similar class name. 
There is one major exception, and that is the FeaturePart. 
This class represents the abstract Feature part (AbstractFeatureGroup) described in the references.
Here it is a concrete class called FeaturePart and holds the elements of the abstract Feature.
Such that, for example, Placemark has a featurePart, so to get the name of the placemark 
you would write:   
- placemark.featurePart.name

The following (Feature) classes use FeaturePart: 
Document, Folder, Placemark, NetworkLink, PhotoOverlay, ScreenOverlay, GroundOverlay and Tour.
Note a FeaturePart is not a Feature, but it is part of the Feature trait. A FeaturePart can be created and is typically added to a 
Feature element, such as Document, Folder, Placemark, etc...

In addition to the base classes is a bunch of experimental helper methods of the type withXXX(). 
These methods return a new object with the XXX changed. For example:
- placemark withName("someName")

returns a new copy of the placemark object with the name changed to "someName",
all other fields are the same as before. See the examples.

# Usage

    object WriteExample1 {
    def main(args: Array[String]) {
    // create a Point at a location
    val point = Point(coordinates = Some(Seq.empty :+ new Location(151.21037, -33.8526)))
    // create a Placemark with the point, a name and open
    val placemark = Placemark(Some(point), FeaturePart(name = Some("Sydney, OZ"), open = Some(true)))
    // create a kml root object with the placemark
    val kml = Kml(feature = Some(placemark))
    // write the kml to the output file
    new KmlPrintWriter("./kml-files/Sydney-oz.kml").write(Option(kml), new PrettyPrinter(80, 3))
    } }

see also WriteExample2 for a variation of WriteExample1.

## Issues:
  One current major issue is scala does not preserve CDATA, 
  such that angle brackets and ampersands are corrupted, Google Earth for example cannot process that.

## Status

This scalakml library needs a bit more work, in particular testing and more documentation.  

The gx: extension has only what is in the official kml22gx.xsd schema,
it does not include many of the gx: elements from Google listed in reference 2.

Some very basic examples and kml files from http://code.google.com/p/kml-samples/ are included. 

I've used scala 2.10.0 and java 1.7 SDK, with IntelliJ IDEA 12.

Very little testing has been done to date.


Ringo Wathelet
