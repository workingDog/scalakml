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
import scala.xml.{NodeSeq, XML}
import java.io.{ File }
import scala.language.postfixOps

/**
 * @author Ringo Wathelet
 * Date: 14/02/13
 * Version: 1
 */

/**
 * Reads into a kml root element (Kml) from kmz file
 *
 * @param kmlExtractor the KmlExtractor object used to extract kml from xml, default KmlFromXml
 * @param parser the SAX XML parser, default scala.xml.XML.parser
 * @see KmlFromXml
 */

class KmzFileReader(kmlExtractor: Option[KmlExtractor] = Some(KmlFromXml),
                    parser: scala.xml.SAXParser = scala.xml.XML.parser) extends KmlFileReader {


  /**
   * get a sequence of Kml root elements from the input kmz file
   * @param file the input kmz file
   * @return a sequence of Kml root element options, one for each of the input file entries
   */
  def getKmlFromKmzFile(file: File): Seq[Option[Kml]] = {
    import scala.collection.JavaConversions._
    if (!file.getName.toLowerCase.endsWith(".kmz")) Seq.empty
    else {
      val rootKmz = new java.util.zip.ZipFile(file)
      (rootKmz.entries.
        filter(_.getName.toLowerCase.endsWith(".kml")).
        collect { case kmlFile => loadKml(rootKmz.getInputStream(kmlFile)) } toSeq)
    }
  }

  /**
   * get a sequence of Kml root elements from the input kmz file name
   * @param fileName the input kmz file name
   * @return a sequence of Kml root element options, one for each of the input file entries
   */
  def getKmlFromKmzFile(fileName: String): Seq[Option[Kml]] = getKmlFromKmzFile(new File(fileName))

}

