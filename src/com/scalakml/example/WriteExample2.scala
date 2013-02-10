package com.scalakml.example

import com.scalakml.io.KmlPrintWriter
import com.scalakml.kml._
import com.scalakml.kml.FeaturePart
import com.scalakml.kml.Point
import com.scalakml.kml.Placemark
import com.scalakml.io.KmlPrintWriter
import scala.Some
import xml.PrettyPrinter

/**
 * Author: Ringo Wathelet
 * Date: 23/01/13 
 * Version: 1
 */

object WriteExample2 {
  def main(args: Array[String])  {
    println("....WriteExample2 start...\n")
    // create a Point at a location
    val point = Point() addToCoordinates (new Location(151.21037, -33.8526))
    // create a Placemark with the point as geometry, a name and open
    val placemark = Placemark().copy(geometry = Some(point))
    // create a kml root object with the placemark as feature
    val kml = Kml().copy(feature = Some(placemark))
    // write the kml to the output file
    new KmlPrintWriter("./kml-files/Sydney-oz2.kml").write(Option(kml), new PrettyPrinter(80, 3))
    println("\n....WriteExample2 done...")
  }
}