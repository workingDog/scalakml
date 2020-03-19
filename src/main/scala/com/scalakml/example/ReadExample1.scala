package com.scalakml.example

import com.scalakml.io.KmlFileReader
import com.scalakml.kml.{Placemark}

//import scala.xml.PrettyPrinter

/**
 * Author: Ringo Wathelet
 * Date: 23/01/13
 * Version: 1
 */

object ReadExample1 {
  def main(args: Array[String]): Unit = {
    println("....ReadExample1 start...\n")

    // read a kml file into a kml root object.
    // Note the default extractor (as shown) can be replaced by your own KmlExtractor
    // see KmlExtractor trait in KmlFileReader
    val kml = new KmlFileReader().getKmlFromFile("./kml-files/Sydney-oz.kml")
    // get the placemark
    val placemark = kml.get.feature.get.asInstanceOf[Placemark]

    val address = placemark.featurePart.addressDetails
    println("address: " + address)


    // get the placemark point
  //  val point = placemark.geometry.get.asInstanceOf[Point]
    // print the point coordinates
  //  println("coordinate: " + point.coordinates)

    println("\n....ReadExample1 done...")

  }

}