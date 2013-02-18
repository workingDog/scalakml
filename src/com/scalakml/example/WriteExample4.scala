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
  def main(args: Array[String])  {
    println("....WriteExample4 start...\n")

    val kml = new Kml(new Placemark("Sydney", new Point(RelativeToGround, 151.21037, -33.8526, 12345.0)))

    new KmlPrintWriter().write(Option(kml), new PrettyPrinter(80, 3))

    println("\n....WriteExample4 done...")
  }
}