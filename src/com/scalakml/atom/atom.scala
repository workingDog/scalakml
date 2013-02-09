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
}

case class Link(href: Option[String] = None,
                rel: Option[String] = None,
                typeValue: Option[String] = None,
                hrefLang: Option[String] = None,
                title: Option[String] = None,
                length: Option[String] = None) {

  def this() = this(None, None, None, None, None, None)

  /**
   * returns a copy of the original object with the designated fieldName changed to the newValue
   *
   * @param fieldName the name of the field to change
   * @param newValue the new value to be in the filedName
   * @return a new object with the designated fieldName changed to the newValue
   */
  def change(fieldName: String, newValue: Any) = {
    val theCopy = this.copy()
    val field = theCopy.getClass.getDeclaredField(fieldName)
    field.setAccessible(true)
    field.set(theCopy, newValue)
    theCopy
  }

}
