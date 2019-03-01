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
 * Contains the current KeyFrames object, used by dynamic method added to Integer class. Using a trait
 * for encapsulation. Created by adavis on 10/20/17.
 */
trait CurrentKeyFrameHolder {

    /** Used by mod method added to Integer class. */
    KeyFrames currentKf

}
