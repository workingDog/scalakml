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


case class Author(name: String) {
  def this() = this("")
  def withName(newValue: String) = { this.copy(name = newValue) }
}

case class Link(href: Option[String] = None,
                rel: Option[String] = None,
                typeValue: Option[String] = None,
                hrefLang: Option[String] = None,
                title: Option[String] = None,
                length: Option[String] = None) {

  def this() = this(None, None, None, None, None, None)
  def withHref(newValue: String) = { this.copy(href = Some(newValue)) }
  def withRel(newValue: String) = { this.copy(rel = Some(newValue)) }
  def withTypeValue(newValue: String) = { this.copy(typeValue = Some(newValue)) }
  def withHrefLang(newValue: String) = { this.copy(hrefLang = Some(newValue)) }
  def withTitle(newValue: String) = { this.copy(title = Some(newValue)) }
  def withLength(newValue: String) = { this.copy(length = Some(newValue)) }

}
