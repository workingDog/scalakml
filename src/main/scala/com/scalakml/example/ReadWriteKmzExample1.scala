package com.scalakml.example

import com.scalakml.io.{KmzPrintWriter, KmzFileReader, KmlPrintWriter}
import xml.{PrettyPrinter}
import com.scalakml.kml.Kml
import util.Random

/**
 * Author: Ringo Wathelet
 * Date: 14/02/13
 * Version: 1
 */

object ReadWriteKmzExample1 {
  def main(args: Array[String]): Unit = {
    println("....ReadWriteKmzExample1 start...\n")

    val pretty = new PrettyPrinter(80, 3)
    //read a kmz file into a sequence of kml objects
    val kmlSeq = new KmzFileReader().getKmlFromKmzFile("./kml-files/KML_Samples.kmz")
    // setup a writer to write to System.out
    val writer = new KmlPrintWriter()
    // write each kml object to System.out
    kmlSeq.foreach(kml => writer.write(kml, pretty))

    println("\n....ReadWriteKmzExample1 writing to kmz...")

    // make a map of (fileName -> kml object) with the kml objects
    var kmlMap = Map.empty[String, Option[Kml]]
    kmlSeq.foreach(kmlObj => (kmlMap += ("kml_"+math.abs(Random.nextInt).toString) -> kmlObj))

    // create a kmz file writer
    val pWriter = new KmzPrintWriter("./kml-files/test.kmz")
    // add a resource file to the kmz file
  //  pWriter.addResourceFile("test_picture","./kml-files/testPicture.png")
    // write all kml objects (including the resource files) to a kmz file, each kml object is in a separate kml file
    pWriter.writeAllToKmz(kmlMap, pretty)

    // write a single kml object to a kmz file
    //    new KmzPrintWriter("./kml-files/test_single.kmz").writeToKmz(kmlSeq.head, pretty)

    println("\n....ReadWriteKmzExample1 done...")
  }
}