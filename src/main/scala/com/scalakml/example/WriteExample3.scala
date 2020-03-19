package com.scalakml.example


import com.scalakml.kml.FeaturePart
import com.scalakml.kml.Placemark
import xml.PrettyPrinter
import com.scalakml.io.KmlPrintWriter
import com.scalakml.kml._

/**
 * Author: Ringo Wathelet
 * Date: 23/01/13 
 * Version: 1
 */

object WriteExample3 {
  def main(args: Array[String]): Unit = {
    println("....WriteExample3 start...\n")

    // create a writer to write to default System.out
    val writer = new KmlPrintWriter()

    // create a Placemark
    val placemark = Placemark(featurePart = new FeaturePart(name = Option("test_placemark")), id = Option("test_id"))
    // write the placemark
    writer.write(Option(placemark), new PrettyPrinter(80, 3))

    // create a Document with the placemark
    val doc = Document(features = (Seq.empty :+ placemark), featurePart = new FeaturePart(name = Option("test_document")))
    // write the document
    writer.write(Option(doc), new PrettyPrinter(80, 3))

    println("\n....WriteExample3 done...")
  }
}
