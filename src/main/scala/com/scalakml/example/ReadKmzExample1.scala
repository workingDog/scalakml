package com.scalakml.example

import com.scalakml.io.{KmzFileReader, KmlPrintWriter}
import xml.PrettyPrinter

/**
 * Author: Ringo Wathelet
 * Date: 14/02/13
 * Version: 1
 */

object ReadKmzExample1 {
  def main(args: Array[String]): Unit = {
    println("....ReadKmzExample1 start...\n")

    val pretty = new PrettyPrinter(80, 3)
    //read a kmz file into a sequence of kml objects
    val kmlSeq = new KmzFileReader().getKmlFromKmzFile("./kml-files/Sydney-oz.kmz")
    // setup a writer to write to System.out
    val writer = new KmlPrintWriter()
    // write each kml object to System.out
    kmlSeq.foreach(kml => writer.write(kml, pretty))

    println("\n....ReadKmzExample1 done...")
  }
}
