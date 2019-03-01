/*
   Copyright 2019 Adam L. Davis

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
limitations under the License.
 */
package org.groocss

/**
 * Represents an @font-face in CSS. Created by adavis on 8/8/16.
 */
class FontFace implements CSSPart {

    List<Style> styles = []

    /** Adds a Style to this FontFace. */
    void leftShift(Style style) { styles << style }
    void add(Style style) { styles << style }

    String toString() {
        "@font-face { ${styles.join(' ')} }"
    }

    /** Sets or returns the font family for text */
    FontFace fontFamily (value) {
        styles << new Style(name: 'fontFamily', value: "$value")
        this
    }
    /** Optional. Defines how the font should be stretched. Default value is "normal" */
    FontFace fontStretch (value) {
        styles << new Style(name: 'fontStretch', value: "$value")
        this
    }
    /** Sets or returns whether the style of the font is normal, italic or oblique */
    FontFace fontStyle (value) {
        styles << new Style(name: 'fontStyle', value: "$value")
        this
    }
    /** Sets or returns the boldness of the font */
    FontFace fontWeight (value) {
        styles << new Style(name: 'fontWeight', value: "$value")
        this
    }

    /** Required. Defines the URL(s) where the font should be downloaded from. */
    FontFace src (value) {
        styles << new Style(name: 'src', value: "$value")
        this
    }

    /** Optional. Defines the range of unicode characters the font supports. Default value is "U+0-10FFFF". */
    FontFace unicodeRange (value) {
        styles << new Style(name: 'unicodeRange', value: "$value")
        this
    }

    @Override
    boolean isEmpty() {
        styles.empty
    }
}
