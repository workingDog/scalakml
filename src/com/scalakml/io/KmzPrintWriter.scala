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

import io.Source
import java.io._
import xml.{dtd, XML, PrettyPrinter}
import java.util.zip.{ZipEntry, ZipOutputStream}
import scala.Some


/**
 * @author Ringo Wathelet
 * Date: 14/02/13
 * Version: 1
 */

/**
 * writes the kml elements as xml to a kmz file
 *
 * @param kmzFileName the kmz File name to write to
 * @param encoding the encoding
 * @param xmlDecl if true, write xml declaration
 * @param doctype if not null, write doctype declaration
 */
class KmzPrintWriter(kmzFileName: Option[String] = None,
                     xmlExtractor: Option[XmlExtractor] = Some(KmlToXml),
                     encoding: String = "UTF-8",
                     xmlDecl: Boolean = true,
                     doctype: dtd.DocType = null)  {

  private val resourcesFiles = collection.mutable.Map.empty[String, FileInputStream]
  private val zipFile = if (kmzFileName.isDefined) Some(new ZipOutputStream(new FileOutputStream(kmzFileName.get))) else None

  def this(fileName: String) = this(Option(fileName))

  /**
   * adds a resource file to the kmz file.
   * This needs to be done before the kmz file is written.
   *
   * @param filenameInKmzFile the name the resource file will have in the kmz file
   * @param resourceFilename the file containing the resource
   */
  def addResourceFile(filenameInKmzFile: String, resourceFilename: String)  {
    resourcesFiles += (filenameInKmzFile -> new FileInputStream(resourceFilename))
  }

  /**
   * writes the kml object to a kmz file.
   * The kmz file has to exist, it is set during construction of a KmzPrintWriter()
   * For example: new KmzPrintWriter("test.kmz")
   * The kml file inside the kmz will be named the same as the kmz file but with the kml extension.
   * Following from the example, test.kml
   *
   * @param kml the kml object to write to the kmz file
   * @param pretty the pretty printer to use
   */
  def writeKmz[A: KmlToXml](kml: A, pretty: PrettyPrinter = null) {
      kmzFileName match {
        case Some(fileName) => {
          val baseName = if (!fileName.isEmpty && (fileName.length > 4)) fileName.substring(0, fileName.length-4) else fileName
          writeAllToKmz(scala.collection.mutable.Map(baseName+".kml" -> kml), pretty)
        }
        case None => Unit
      }
  }

  /**
   * writes all input kml objects to the designated kmz file.
   * The kmz file has to exist, it is set during construction of a KmzPrintWriter().
   * For example: new KmzPrintWriter("test.kmz").
   * Each kml object must have a corresponding file name in the input kmlMap.
   *
   * @param kmlMap the Map of kml objects and file names
   * @param pretty the pretty printer to use
   */
  def writeAllToKmz[A: KmlToXml](kmlMap: scala.collection.mutable.Map[String, A], pretty: PrettyPrinter = null) {
  if ((kmzFileName.isDefined) && (zipFile.isDefined) && (kmlMap != Nil) && (!kmlMap.isEmpty)) {
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

          addToZip(kmlObj._1, outputStream)
        }
        addResourceFilesToZip()
        zipFile.get.close()
      }
      case None => Unit
    }
  }
  }

  // TODO replace this, looks inefficient
  private def readByte(bufferedReader: BufferedReader): Stream[Int] = {
    bufferedReader.read() #:: readByte(bufferedReader)
  }

  private def addResourceFilesToZip() {
    zipFile match {
      case Some(zip) => {
        for (file <- resourcesFiles) {
          zip.putNextEntry(new ZipEntry(file._1))
          val in = Source.fromInputStream(file._2).bufferedReader()
          try {
            readByte(in).takeWhile(_ > -1).toList.foreach(zip.write(_))
          }
          finally {
            in.close()
          }
          zip.closeEntry()
        }
      }
      case None => Unit
    }
  }

  private def addToZip(kmlFileName: String, kmlStream: ByteArrayOutputStream) {
    zipFile match {
      case Some(zip) => {
        try {
          zip.putNextEntry(new ZipEntry(kmlFileName))
          val in = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(kmlStream.toByteArray())))
          try {
            readByte(in).takeWhile(_ > -1).toList.foreach(zip.write(_))
          }
          finally {
            in.close()
          }
          zip.closeEntry()
        }
      }
      case None => Unit
    }
  }

}
