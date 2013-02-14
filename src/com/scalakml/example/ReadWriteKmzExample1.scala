package com.scalakml.example

import com.scalakml.io.{KmzPrintWriter, KmzFileReader, KmlPrintWriter}
import xml.PrettyPrinter
import com.scalakml.kml.Kml
import util.Random

/**
 * Author: Ringo Wathelet
 * Date: 14/02/13
 * Version: 1
 */

object ReadWriteKmzExample1 {
  def main(args: Array[String])  {
    println("....ReadWriteKmzExample1 start...\n")

    val pretty = new PrettyPrinter(80, 3)
    //read a kmz file into a sequence of kml objects
    val kmlSeq = new KmzFileReader().getKmlFromKmzFile("./kml-files/Sydney-oz.kmz")
    // setup a writer to write to System.out
    val writer = new KmlPrintWriter()
    // write each kml object to System.out
    kmlSeq.foreach(kml => writer.write(kml, pretty))

    println("\n....ReadWriteKmzExample1 writing to kmz...")

    // make a map of (fileName -> kml object) with the read kml objects
    val kmlMap = scala.collection.mutable.Map.empty[String, Option[Kml]]
    kmlSeq.foreach(kmlObj => (kmlMap += ("kml_"+Random.nextInt.toString) -> kmlObj))
    // write all kml objects into a kmz file, each kml object is in a separate kml file
    new KmzPrintWriter("./kml-files/test.kmz").writeAllToKmz(kmlMap, pretty)

    // write a single kml object to a kmz file
    //    new KmzPrintWriter("./kml-files/test_single.kmz").writeKmz(kmlSeq.head, pretty)

    println("\n....ReadWriteKmzExample1 done...")
  }
}