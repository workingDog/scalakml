package com.scalakml.example

import com.scalakml.io.{KmzPrintWriter, KmzFileReader, KmlPrintWriter}
import xml.PrettyPrinter

/**
 * Author: Ringo Wathelet
 * Date: 23/01/13 
 * Version: 1
 */

object ReadKmzExample1 {
  def main(args: Array[String])  {
    println("....ReadKmzExample1 start...\n")

    val kmlSeq = new KmzFileReader().getKmlFromKmzFile("./kml-files/Sydney-oz.kmz")
    val writer = new KmlPrintWriter()
    kmlSeq.foreach(kml => writer.write(kml, new PrettyPrinter(80, 3)))

    println("\n....ReadKmzExample1 to kmz...")

    new KmzPrintWriter("./kml-files/test.kmz").writeKmz(kmlSeq.head, new PrettyPrinter(80, 3))

    println("\n....ReadKmzExample1 done...")
  }
}
