package com.scalakml.example

/**
 * Author: Ringo Wathelet
 * Date: 18/02/13 
 * Version: 1
 */

import xml.PrettyPrinter
import com.scalakml.io.KmlPrintWriter
import com.scalakml.kml._


object WriteExample4 {
  def main(args: Array[String]): Unit = {
    // create a scala Kml object with a Placemark that contains a Point
    val kml = new Kml(new Placemark("Sydney", new Point(RelativeToGround, 151.21037, -33.8526, 12345.0)))
    // write the kml object to System.out as xml
    new KmlPrintWriter().write(kml, new PrettyPrinter(80, 3))
  }
}