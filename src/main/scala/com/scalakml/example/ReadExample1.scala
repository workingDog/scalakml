package com.scalakml.example


import com.scalakml.io.{KmlToXml, KmlFromXml, KmlPrintWriter, KmlFileReader}
import xml.{XML, PrettyPrinter}
import com.scalakml.kml.{Point, Placemark}

/**
 * Author: Ringo Wathelet
 * Date: 23/01/13
 * Version: 1
 */

object ReadExample1 {
  def main(args: Array[String]) {
    println("....ReadExample1 start...\n")

    // read a kml file into a kml root object.
    // Note the default extractor (as shown) can be replaced by your own KmlExtractor
    // see KmlExtractor trait in KmlFileReader
    val kml = new KmlFileReader().getKmlFromFile("./kml-files/Sydney-oz.kml")
    // get the placemark
    val placemark = kml.get.feature.get.asInstanceOf[Placemark]
    // get the placemark point
    val point = placemark.geometry.get.asInstanceOf[Point]
    // print the point coordinates
    for (x <- point.coordinates.get) println("coordinate: " + x.llaToString)

    println("\n....ReadExample1 done...")
  }
}