package com.scalakml.example

import com.scalakml.io.{KmlPrintWriter, KmlFileReader}
import xml.PrettyPrinter

/**
 * author: Ringo Wathelet
 * Date: 23/01/13 
 * Version: 1
 */

object ReadWriteExample1 {
  def main(args: Array[String]) {
    println("....ReadWriteExample1 start...\n")
    // read a kml file into a kml root object
    val kml = new KmlFileReader().getKmlFromFile("./kml-files/KML_Samples.kml")

    // write the kml to an output file.
//    if (kml.isDefined) new KmlPrintWriter("./kml-files/scalakml_KML_Samples.kml").write(kml, new PrettyPrinter(80, 3))

    // write the kml to System.out
    if (kml.isDefined) new KmlPrintWriter().write(kml, new PrettyPrinter(80, 3))

    println("\n....ReadWriteExample1 done...")
  }
}
