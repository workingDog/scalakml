/*
 * Copyright (c) 2013, Ringo Wathelet
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice, this
 *   list of conditions and the following disclaimer in the documentation and/or
 *   other materials provided with the distribution.
 *
 * - Neither the name of "scalakml" nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.scalakml.io

import java.io._
import xml.{dtd, XML, PrettyPrinter}
import java.util.zip.{ZipEntry, ZipOutputStream}
import scala.language.postfixOps

/**
 * @author Ringo Wathelet
 *         Date: 14/02/13
 *         Version: 1
 */

/**
 * Writer to write the kml elements as xml to a kmz file
 *
 * @param kmzFileName the kmz file name to write to
 * @param encoding the encoding
 * @param xmlDecl if true, write xml declaration
 * @param doctype if not null, write doctype declaration
 */
class KmzPrintWriter(kmzFileName: Option[String] = None,
                     xmlExtractor: Option[XmlExtractor] = Some(KmlToXml),
                     encoding: String = "UTF-8",
                     xmlDecl: Boolean = true,
                     doctype: dtd.DocType = null) {

  private val resourcesFiles = collection.mutable.Map.empty[String, FileInputStream]
  private val kmzFile = if (kmzFileName.isDefined) Some(new ZipOutputStream(new FileOutputStream(kmzFileName.get))) else None

  /**
   * Writer to write the kml elements as xml to a kmz file
   * @param kmzFileName the kmz file name to write to
   */
  def this(kmzFileName: String) = this(Option(kmzFileName))

  /**
   * add a resource file to the set that will be written to the kmz file.
   * This needs to be done before the kmz file is written.
   *
   * @param filenameInKmzFile the name the resource file will have in the kmz file
   * @param resourceFilename the name of the file containing the resource
   */
  def addResourceFile(filenameInKmzFile: String, resourceFilename: String): Unit = {
    resourcesFiles += (filenameInKmzFile -> new FileInputStream(resourceFilename))
  }

  /**
   * returns the resource files
   * @return the resource files as a collection.mutable.Map.empty[String, FileInput]
   */
  def getResourceFiles = resourcesFiles

  /**
   * writes the kml object and any resource files to a kmz file.
   * The kmz file has to exist, it is set during construction of a KmzPrintWriter.
   * For example: new KmzPrintWriter("test.kmz"), cannot have new KmzPrintWriter().
   * The kml file inside the kmz will be named the same as the kmz file but with the ".kml" extension.
   * Following from the example, "test.kml"
   *
   * @param kml the kml object to write to the kmz file
   * @param pretty the optional pretty printer to use
   */
  def writeToKmz[A: KmlToXml](kml: A, pretty: PrettyPrinter = null): Unit = {
    kmzFileName match {
      case Some(fileName) => {
        val baseName = if (!fileName.isEmpty && (fileName.length > 4)) fileName.substring(0, fileName.length - 4) else fileName
        writeAllToKmz(Map(baseName + ".kml" -> kml), pretty)
      }
      case None => ()
    }
  }

  /**
   * writes all input kml objects and any resource files to the designated kmz file.
   * The kmz file has to exist, it is set during construction of a KmzPrintWriter.
   * For example: new KmzPrintWriter("test.kmz"), cannot have new KmzPrintWriter().
   * Each kml object must have a corresponding file name in the input kmlMap.
   *
   * Note: if the input kmlMap is empty, only the resource files will be written to the kmz file.
   *
   * @param kmlMap the Map of file names (keys) and kml objects (values)
   * @param pretty the optional pretty printer to use
   */
  def writeAllToKmz[A: KmlToXml](kmlMap: Map[String, A] = Map.empty[String, A], pretty: PrettyPrinter = null): Unit = {
    if ((kmzFileName.isDefined) && (kmzFile.isDefined)) {
      xmlExtractor match {
        case Some(extractor) => {
          for (kmlObj <- kmlMap) {
            val outputStream = new ByteArrayOutputStream()
            val tempWriter = new PrintWriter(outputStream)

            if (pretty == null)
              extractor.getXmlFrom(kmlObj._2).foreach(x => XML.write(tempWriter, x, encoding, xmlDecl, doctype))
            else
              extractor.getXmlFrom(kmlObj._2).foreach(x => XML.write(tempWriter, XML.loadString(pretty.format(x)), encoding, xmlDecl, doctype))

            tempWriter.flush()
            tempWriter.close()
            outputStream.close()

            addToKmz(kmlObj._1, outputStream)
          }
          addResourceFilesToKmz()
          kmzFile.get.close()
        }
        case None => ()
      }
    }
  }

  private def addResourceFilesToKmz(): Unit = {
    kmzFile match {
      case Some(zip) => {
        for (file <- resourcesFiles) {
          zip.putNextEntry(new ZipEntry(file._1))
          try {
            zip.write(LazyList.continually(file._2.read).takeWhile(-1 !=).map(_.toByte).toArray)
          }
          finally {
            file._2.close()
          }
          zip.closeEntry()
        }
      }
      case None => ()
    }
  }

  private def addToKmz(kmlFileName: String, kmlStream: ByteArrayOutputStream): Unit = {
    kmzFile match {
      case Some(zip) => {
        try {
          zip.putNextEntry(new ZipEntry(kmlFileName))
          val in = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(kmlStream.toByteArray())))
          try {
            zip.write(LazyList.continually(in.read).takeWhile(-1 !=).map(_.toByte).toArray)
          }
          finally {
            in.close()
          }
          zip.closeEntry()
        } catch {
          case e: Exception => ()
        }
      }
      case None => ()
    }
  }

}
