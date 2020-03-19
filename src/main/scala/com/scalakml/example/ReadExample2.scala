package com.scalakml.example

import com.scalakml.io.{KmlFileReader}
import com.scalakml.kml.{Point, Placemark}

/**
 * Author: Ringo Wathelet
 * Date: 23/01/13 
 * Version: 1
 */

object ReadExample2 {
  def main(args: Array[String]): Unit = {
    println("....ReadExample2 start...\n")
    // read a kml file into a kml root object
    val kml = new KmlFileReader().getKmlFromFile("./kml-files/Sydney-oz.kml")
    // get the placemark
    val placemark = kml.get.feature.get.asInstanceOf[Placemark]
    // get the placemark point
    val point = placemark.geometry.get.asInstanceOf[Point]
    // print the point coordinates
    println("coordinate: " + point.coordinates)

    println("\n....ReadExample2 done...")
  }
}
