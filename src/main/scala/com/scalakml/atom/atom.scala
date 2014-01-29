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
package com.scalakml.atom

import java.net.URI

/**
 * package of classes and constructs for the Google KML Version 2.2 model
 *
 * @author Ringo Wathelet
 * Date: 12/12/12
 * Version: 1
 *
 * Reference: OGC 07-147r2 Version: 2.2.0, Category: OGC� Standard, Editor: Tim Wilson, at
 * http://www.opengeospatial.org/standards/kml
 * also
 * Google developers KML Reference, at
 * https://developers.google.com/kml/documentation/kmlreference
 *
 * The documentations are taken from these two references.
 */

/**
 * KML 2.2 supports new elements for including data about the author and related website in your KML file.
 * This information is displayed in geo search results, both in Earth browsers such as Google Earth,
 * and in other applications such as Google Maps. The ascription elements used in KML are as follows:
 * atom:author element - parent element for atom:name
 * atom:name element - the name of the author
 * atom:link element - contains the href attribute
 * href attribute - URL of the web page containing the KML/KMZ file
 * These elements are defined in the Atom Syndication Format.
 *
 * The <atom:author> element is the parent element for <atom:name>, which specifies the author of the KML feature.
 * @param name the name of the author
 */
case class Author(name: Option[String] = None, uri: Option[String] = None, email: Option[String] = None) {

  def this(name: String) = this(Option(name), None, None)
  def this(name: String, uri: String) = this(Option(name), Option(uri), None)
  def this(name: String, uri: String, email: String) = this(Option(name), Option(uri), Option(email))
}

/**
 * Specifies the URL of the website containing this KML or KMZ file.
 * Be sure to include the namespace for this element in any KML file that uses it:
 * xmlns:atom="http://www.w3.org/2005/Atom"
 *
 * @param href URL of the web page containing the KML/KMZ file
 * @param rel
 * @param typeValue
 * @param hrefLang
 * @param title
 * @param length
 */
case class Link(href: Option[String] = None,
                rel: Option[String] = None,
                typeValue: Option[String] = None,
                hrefLang: Option[String] = None,
                title: Option[String] = None,
                length: Option[String] = None) {

  def this(href: String) = this(Option(href))
  def this(href: String, rel: String) = this(Option(href), Option(rel))
  def this(href: String, rel: String, typeValue: String) = this(Option(href), Option(rel), Option(typeValue))
}
