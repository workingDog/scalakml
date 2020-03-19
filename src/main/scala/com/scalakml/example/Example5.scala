package com.scalakml.example


import com.scalakml.KmlImplicits._
import com.scalakml.kml.FeaturePart
import com.scalakml.kml.Placemark
import xml.PrettyPrinter
import com.scalakml.io.KmlPrintWriter
import com.scalakml.kml._


/**
  * example using KmlImplicits
  *
  * Author: Ringo Wathelet
  * Date:
  * Version: 1
  */

object Example5 {
  def main(args: Array[String]): Unit = {
    println("....Example5 start...\n")

    // create a writer to write to default System.out
    val writer = new KmlPrintWriter()
    // create a Point at a location
    val point = Point(coordinates = new Coordinate(151.21037, -33.8526))
    // create a Placemark
    val placemark = Placemark(point, new FeaturePart(name = "Sydney"), id = "test_id")
    // create a Document with the placemark
    val doc = Document(features = (Seq.empty :+ placemark),
      featurePart = new FeaturePart(name = "test_document", addressDetails = "some address"))
    // write the document
    writer.write(Option(doc), new PrettyPrinter(80, 3))

    println("\n....Example5 done...")
  }
}
