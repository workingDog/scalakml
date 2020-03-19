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

import com.scalakml.kml._
import org.xml.sax.InputSource

import scala.xml.XML._
import java.io.{File, FileDescriptor}

import scala.xml.Source._
import scala.language.postfixOps
import scala.xml.{NodeSeq, XML}
import scala.jdk.CollectionConverters._

/**
 * @author Ringo Wathelet
 * Date: 12/12/12
 * Version: 1
 */

/**
 * Reads into a kml root element (Kml) from various file, string and NodeSeq input sources
 *
 * @param kmlExtractor the KmlExtractor object used to extract kml from xml, default KmlFromXml
 * @param parser the SAX XML parser, default scala.xml.XML.parser
 * @see KmlFromXml
 */

class KmlFileReader(kmlExtractor: Option[KmlExtractor] = Some(KmlFromXml),
                    parser: scala.xml.SAXParser = scala.xml.XML.parser) {

  /**
   * get a Kml root element from the input source
   *
   * @param source kml input source, such as a file, a file name, a file descriptor
   * @return a Kml root element option
   */
  def loadKml(source: InputSource): Option[Kml] = {
    Some(loadXML(source, parser)) match {
      case Some(nodeSeq) => getKml(nodeSeq)
      case _ => None
    }
  }

  /**
   * get a Kml root element from the inputStream
   *
   * @param is kml inputStream
   * @return a Kml root element option
   */
  def loadKml(is: java.io.InputStream): Option[Kml] = loadKml(fromInputStream(is))

  /**
   * get a Kml root element from the input file
   * @param file the input xml file
   * @return a Kml root element option
   */
  def getKmlFromFile(file: File): Option[Kml] = loadKml(fromFile(file))

  /**
   * get a Kml root element from the input file descriptor
   * @param fd the input xml file descriptor
   * @return a Kml root element option
   */
  def getKmlFromFile(fd: FileDescriptor): Option[Kml] = loadKml(fromFile(fd))

  /**
   * get a Kml root element from the input file name
   * @param name the input file name
   * @return a Kml root element option
   */
  def getKmlFromFile(name: String): Option[Kml] = loadKml(fromFile(name))

  /**
   * get a Kml root element from its input string representation
   * @param xmlString the input xml string
   * @return a Kml root element option
   */
  def getKmlFromString(xmlString: String): Option[Kml] = getKml(XML.loadString(xmlString))

  /**
   * get a Kml root element from its kml NodeSeq
   * @param nodeSeq the input xml node sequence
   * @return a Kml root element option
   */
  def getKmlFromNodeSeq(nodeSeq: scala.xml.NodeSeq): Option[Kml] = getKml(nodeSeq)

  /**
   * creates a Kml root element from the Node Sequence
   * @param nodeSeq the xml node sequence
   * @return a Kml root element option
   */
  private def getKml(nodeSeq: scala.xml.NodeSeq): Option[Kml] = {
    kmlExtractor match {
      case Some(extractor) => extractor.makeKml(nodeSeq)
      case _ => None
    }
  }
}

