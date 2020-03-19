package com.scalakml.example

import com.scalakml.io.{KmlPrintWriter}
import xml.PrettyPrinter
import com.scalakml.kml._
import com.scalakml.kml.FeaturePart
import com.scalakml.kml.Point
import com.scalakml.kml.Placemark


/**
 * Author: Ringo Wathelet
 * Date: 23/01/13 
 * Version: 1
 */

object WriteExample1 {
  def main(args: Array[String]): Unit = {
    println("....WriteExample1 start...\n")

    // create a Point at a location
    val point = Point(coordinates = Option(new Coordinate(151.21037, -33.8526)))
    // create a Placemark with the point, and a name
    val placemark = Placemark(Option(point), FeaturePart(name = Option("Sydney")))
    // create a kml root object with the placemark
    val kml = Kml(feature = Option(placemark))
    // write the kml to the output file
    new KmlPrintWriter("./kml-files/Sydney-oz.kml").write(Option(kml), new PrettyPrinter(80, 3))

    println("\n....WriteExample1 done...")
  }
}
