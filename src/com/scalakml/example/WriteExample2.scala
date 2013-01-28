package com.scalakml.example

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
  def main(args: Array[String]): Unit = {
    println("....WriteExample2 start...\n")
    // create a Point at a location
    val point = Point() withAddedCoordinate new Location(151.21037, -33.8526)
    // create a Placemark with the point, a name and open
    val placemark = Placemark() withGeometry point withFeaturePart FeaturePart() withName("Sydney, OZ") withOpen(true)
    // create a kml root object with the placemark
    val kml = Kml() withFeature placemark
    // write the kml to the output file
    new KmlPrintWriter("./kml-files/Sydney-oz2.kml").write(Option(kml), new PrettyPrinter(80, 3))
    println("\n....WriteExample2 done...")
  }
}