package com.scalakml.example

import java.io.StringWriter

import xml.PrettyPrinter
import com.scalakml.io.KmlPrintWriter
import com.scalakml.kml._
import com.scalakml.KmlImplicits._


/**
  * an example showing how to get an XML string using the KmlPrintWriter
  *
  */
object WriteExample5 {

  def main(args: Array[String]): Unit = {
    // create a string writer, into which the data will be written
    val stringer = new StringWriter()
    // create a point
    val point = new Point(coordinates = new Coordinate(151.21037, -33.8526))
    // create a Placemark
    val placemark = new Placemark(point, new FeaturePart(name = "Sydney"), id = "test_id")
    // write the placemark object to the stringer as xml
    new KmlPrintWriter(stringer).write(Option(placemark), new PrettyPrinter(80, 3))
    // print the string representation of just the placemark
    println("placemark: \n" + stringer.toString)
    // clear the stringer data
    stringer.getBuffer.delete(0, stringer.getBuffer.length())
    // create a kml root object with the placemark
    val kml = Kml(feature = Option(placemark))
    // write the kml object to the stringer as xml
    new KmlPrintWriter(stringer).write(Option(kml), new PrettyPrinter(80, 3))
    // print the string representation of the kml
    println("\nkml: \n" + stringer.toString)
  }

}