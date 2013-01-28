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

import java.io.{ PrintWriter, File }
import xml.{NodeSeq, dtd, XML, PrettyPrinter}
import com.scalakml.kml.Kml

/**
 * @author Ringo Wathelet
 * Date: 12/12/12
 * Version: 1
 */

/**
 * represents the extraction of an xml node sequence from a kml root element
 */
trait XmlExtractor {
  def getXmlFrom[A: KmlToXml](kml: A): NodeSeq
}

/**
 * writes the kml element object to xml representation
 *
 * @param writer the PrintWriter to use
 * @param encoding the encoding
 * @param xmlDecl if true, write xml declaration
 * @param doctype if not null, write doctype declaration
 */
class KmlPrintWriter(writer: Option[PrintWriter] = Some(new PrintWriter(System.out)),
                     xmlExtractor: Option[XmlExtractor] = Some(KmlToXml),
                     encoding: String = "UTF-8",
                     xmlDecl: Boolean = true,
                     doctype: dtd.DocType = null) {

// TODO maybe default should be to null device or None
  def this(fileName: Option[String]) = this(writer = Some(if (fileName.isDefined) new PrintWriter(new File(fileName.get)) else new PrintWriter(System.out)))

  def this(fileName: String) = this(Option(fileName))

  /**
   * writes the Kml element to xml
   *
   * @param value the Kml element option
   * @param pretty the pretty printer to use, default null
   */
  def write[A: KmlToXml](value: A, pretty: PrettyPrinter = null) = {
    if (writer.isDefined) {
     xmlExtractor match {
       case Some(extractor) => {
         if (pretty == null)
           extractor.getXmlFrom(value).foreach(x => XML.write(writer.get, x, encoding, xmlDecl, doctype))
         else
           extractor.getXmlFrom(value).foreach(x => XML.write(writer.get, XML.loadString(pretty.format(x)), encoding, xmlDecl, doctype))

         writer.get.flush()
       }
       case None => Unit
     }
    }
  }

  def close() = if (writer.isDefined) writer.get.close()

}
