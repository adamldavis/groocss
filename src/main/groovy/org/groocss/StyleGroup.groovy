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

import groovy.transform.*

/** A group of styles as represented in a CSS file. */
@CompileStatic
class StyleGroup extends Selectable implements CSSPart {

    List<StyleGroup> extenders = []

    List<Style> styleList = []
    class Styles {
        Styles leftShift(Style s) { current.styleList.add s ; this }
        Style getAt(int i) { current.styleList[i] }
        void addAll(Collection<Style> list) { current.styleList.addAll(list) }
        void removeAll(Collection<Style> list) { current.styleList.removeAll(list) }
    }
    Styles styles = new Styles()

    Config config

    MediaCSS owner
    StyleGroup current = this

    @Delegate
    final ColorMethods colorMethods = new ColorMethods()

    /** Adds given style to this StyleGroup.
     * @return this StyleGroup. */
    StyleGroup add(Style style) { styles << style; this }

    /** Synonym for add. Adds given style to this StyleGroup.
     * @return this StyleGroup. */
    StyleGroup leftShift(Style style) { add style }

    /** Adds all styles from given StyleGroup to this StyleGroup. Returns this StyleGroup. */
    StyleGroup add(StyleGroup sg) { styles.addAll sg.styleList; this }

    /** Synonymous to add. Adds all styles from given StyleGroup to this StyleGroup. */
    StyleGroup leftShift(StyleGroup sg) { add sg }

    StyleGroup(String selector, Config config1, MediaCSS owner1) {
        this.selector = (owner?.config?.convertUnderline) ? selector.replaceAll(/_/, '-') : selector
        config = config1
        owner = owner1
    }

    /** Creates a new StyleGroup with all styles from given StyleGroup and this StyleGroup. */
    StyleGroup plus(StyleGroup other) {
        def sg = new StyleGroup(selector, config, owner)
        sg.styles.addAll styleList
        sg.styles.addAll other.styleList
        sg
    }

    /** Creates a new StyleGroup with all styles from given StyleGroup removed. */
    StyleGroup minus(StyleGroup other) {
        def sg = new StyleGroup(selector, config, owner)
        sg.styles.addAll styleList
        sg.styles.removeAll other.styleList
        sg
    }

    StyleGroup bitwiseNegate() { (StyleGroup) resetSelector("~ $selector") }

    /** Appends the given text to the selector. */
    StyleGroup subselect(String sel) {
        selector += sel
        this
    }

    /** Appends to selector with additional subselector, adds a new StyleGroup element, and runs given closure on it.
     * If subselector is a psuedo-class of style-class (starts with : or .) it is appended without space. */
    StyleGroup add(String subselector, @DelegatesTo(StyleGroup) Closure<StyleGroup> closure) {
        boolean mod = subselector.startsWith(':') || subselector.startsWith('.')
        StyleGroup sg = new StyleGroup(selector + (mod?'':' ') + subselector, config, owner)
        StyleGroup old = this
        current = sg
        closure.delegate = sg
        closure(sg)
        owner << sg
        current = old
        sg
    }

    /** See #add(String, Closure). */
    StyleGroup add(Selector subselector, @DelegatesTo(StyleGroup) Closure<StyleGroup> closure) {
        add("$subselector", closure)
    }

    /** See #extend(String). */
    StyleGroup extend(Selector other) {
        extend("$other")
    }

    /** See #extend(String). */
    StyleGroup extend(PseudoClass other) {
        extend("$other")
    }

    /** Finds an existing StyleGroup with given selector and appends [comma selector] to its selector.*/
    StyleGroup extend(String otherSelector) {
        StyleGroup other = (StyleGroup) owner.groups.findAll{it instanceof StyleGroup}.find{
            ((StyleGroup) it).selector == otherSelector}
        if (other) other.extenders << this
        other
    }

    /** Gets the current selector plus the selectors of extenders, if any, separated by commas. */
    private String getSelectorPlus() {
        selector + (extenders ? (',' + extenders.collect{it.selector}.join(',')) : '')
    }

    String toString() {
        def delim = config.compress ? '' : '\n\t'
        Style tranStyle = transform ? new Style('transform', transform.join(' ')) : null
        List<Style> tss = tranStyle ? [tranStyle] : new ArrayList<Style>()
        if (tranStyle) {
            tss.addAll(createWebkitMozOpera(tranStyle))
            if (config.addMs) tss << cloneMs(tranStyle)
        }
        if (isEmpty()) ''
        else if (config.prettyPrint) {
            delim = '\n    '
            selectorPlus + ' {' + delim + (styleList + tss).join(delim) + '\n}'
        }
        else selectorPlus + '{' + (styleList + tss).join(delim) +'}'
    }

    /** Reports true if both styleList and transform are empty. */
    boolean isEmpty() {
        styleList.empty && transform.empty
    }

    Style cloneWebkit(Style s) { new Style('-webkit-' + s.name, s.value) }
    Style cloneMoz(Style s) { new Style('-moz-' + s.name,  s.value) }
    Style cloneMs(Style s) { new Style('-ms-' + s.name, s.value) }
    Style cloneOpera(Style s) { new Style('-o-' + s.name,  s.value) }
    void cloneTrio(Style style) {
        styles.addAll createWebkitMozOpera(style)
    }
    List<Style> createWebkitMozOpera(Style style) {
        List<Style> styles = []
        if (config.addWebkit) styles << cloneWebkit(style)
        if (config.addMoz) styles << cloneMoz(style)
        if (config.addOpera) styles << cloneOpera(style)
        styles
    }

    /** Sets the alignment between the lines inside a flexible container when the items do not use all available space */
    StyleGroup alignContent (value) {
        styles << new Style('alignContent', value)
        this
    }
    /** Sets the alignment for items inside a flexible container */
    StyleGroup alignItems (value) {
        styles << new Style('alignItems', value)
        this
    }
    /** Sets the alignment for selected items inside a flexible container */
    StyleGroup alignSelf (value) {
        styles << new Style('alignSelf', value)
        this
    }
    /** A shorthand property for all the animation properties below, except the animationPlayState property */
    StyleGroup animation (value) {
        styles << new Style('animation', value)
        cloneTrio(styles[-1])
        this
    }

    /** A shorthand property for all the animation properties below, plus a closure to define keyframes. */
    StyleGroup animation (value, @DelegatesTo(value=KeyFrames, strategy=Closure.DELEGATE_FIRST) Closure closure) {
        String strValue = value
        styles << new Style(name: 'animation', value: strValue)
        cloneTrio(styles[-1])
        String name = strValue =~ /\s/ ? strValue.split(/\s/)[0] : strValue
        KeyFrames kf = owner.currentKeyFrames = new KeyFrames(config: owner.config, name: name)
        closure.delegate = kf
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure()
        owner.add kf
        this
    }

    /** Sets when the animation will start */
    StyleGroup animationDelay (value) {
        styles << new Style(name: 'animationDelay', value: validateTime(value))
        cloneTrio(styles[-1])
        this
    }
    /** Sets whether or not the animation should play in reverse on alternate cycles */
    StyleGroup animationDirection (value) {
        styles << new Style('animationDirection', value)
        cloneTrio(styles[-1])
        this
    }
    /** Sets how many seconds or milliseconds an animation takes to complete one cycle */
    StyleGroup animationDuration (value) {
        styles << new Style(name: 'animationDuration', value: validateTime(value))
        cloneTrio(styles[-1])
        this
    }
    /** Sets what values are applied by the animation outside the time it is executing */
    StyleGroup animationFillMode (value) {
        styles << new Style('animationFillMode', value)
        cloneTrio(styles[-1])
        this
    }
    /** Sets the number of times an animation should be played */
    StyleGroup animationIterationCount (value) {
        styles << new Style('animationIterationCount', value)
        cloneTrio(styles[-1])
        this
    }
    /** Sets a name for the @keyframes animation */
    StyleGroup animationName (value) {
        styles << new Style('animationName', value)
        cloneTrio(styles[-1])
        this
    }
    /** Sets the speed curve of the animation */
    StyleGroup animationTimingFunction (value) {
        styles << new Style('animationTimingFunction', value)
        cloneTrio(styles[-1])
        this
    }
    /** Sets whether the animation is running or paused */
    StyleGroup animationPlayState (value) {
        styles << new Style('animationPlayState', value)
        cloneTrio(styles[-1])
        this
    }
    /** Sets all the background properties in one declaration */
    StyleGroup background (value) {
        styles << new Style('background', value)
        this
    }
    /** Sets whether a background-image is fixed or scrolls with the page */
    StyleGroup backgroundAttachment (value) {
        styles << new Style('backgroundAttachment', value)
        this
    }
    /** Sets the background-color of an element */
    StyleGroup backgroundColor (value) {
        styles << new Style(name: 'backgroundColor', value: handleColor(value))
        this
    }
    /** Sets the background-image for an element */
    StyleGroup backgroundImage (value) {
        styles << new Style('backgroundImage', value)
        this
    }
    /** Sets the starting position of a background-image */
    StyleGroup backgroundPosition (value) {
        styles << new Style('backgroundPosition', value)
        this
    }
    /** Sets how to repeat (tile) a background-image */
    StyleGroup backgroundRepeat (value) {
        styles << new Style('backgroundRepeat', value)
        this
    }
    /** Sets the painting area of the background */
    StyleGroup backgroundClip (value) {
        styles << new Style('backgroundClip', value)
        this
    }
    /** Sets the positioning area of the background images */
    StyleGroup backgroundOrigin (value) {
        styles << new Style('backgroundOrigin', value)
        this
    }
    /** Sets the size of the background image */
    StyleGroup backgroundSize (value) {
        styles << new Style('backgroundSize', value)
        this
    }
    /** Sets whether or not an element should be visible when not facing the screen */
    StyleGroup backfaceVisibility (value) {
        styles << new Style('backfaceVisibility', value)
        this
    }
    /** Sets borderWidth, borderStyle, and borderColor in one declaration */
    StyleGroup border (value) {
        styles << new Style('border', value)
        this
    }
    /** Sets all the borderBottom* properties in one declaration */
    StyleGroup borderBottom (value) {
        styles << new Style('borderBottom', value)
        this
    }
    /** Sets the color of the bottom border */
    StyleGroup borderBottomColor (value) {
        styles << new Style(name: 'borderBottomColor', value: handleColor(value))
        this
    }
    /** Sets the shape of the border of the bottom-left corner */
    StyleGroup borderBottomLeftRadius (value) {
        styles << new Style('borderBottomLeftRadius', value)
        this
    }
    /** Sets the shape of the border of the bottom-right corner */
    StyleGroup borderBottomRightRadius (value) {
        styles << new Style('borderBottomRightRadius', value)
        this
    }
    /** Sets the style of the bottom border */
    StyleGroup borderBottomStyle (value) {
        styles << new Style('borderBottomStyle', value)
        this
    }
    /** Sets the width of the bottom border */
    StyleGroup borderBottomWidth (value) {
        styles << new Style(name: 'borderBottomWidth', value: validateLength(value))
        this
    }
    /** Sets whether the table border should be collapsed into a single border, or not */
    StyleGroup borderCollapse (value) {
        styles << new Style('borderCollapse', value)
        this
    }
    /** Sets the color of an element's border (can have up to four values) */
    StyleGroup borderColor (value) {
        styles << new Style(name: 'borderColor', value: handleColor(value))
        this
    }
    /** A shorthand property for setting or returning all the borderImage* properties */
    StyleGroup borderImage (value) {
        styles << new Style('borderImage', value)
        this
    }
    /** Sets the amount by which the border image area extends beyond the border box */
    StyleGroup borderImageOutset (value) {
        styles << new Style('borderImageOutset', value)
        this
    }
    /** Sets whether the image-border should be repeated, rounded or stretched */
    StyleGroup borderImageRepeat (value) {
        styles << new Style('borderImageRepeat', value)
        this
    }
    /** Sets the inward offsets of the image-border */
    StyleGroup borderImageSlice (value) {
        styles << new Style('borderImageSlice', value)
        this
    }
    /** Sets the image to be used as a border */
    StyleGroup borderImageSource (value) {
        styles << new Style('borderImageSource', value)
        this
    }
    /** Sets the widths of the image-border */
    StyleGroup borderImageWidth (value) {
        styles << new Style('borderImageWidth', value)
        this
    }
    /** Sets all the borderLeft* properties in one declaration */
    StyleGroup borderLeft (value) {
        styles << new Style('borderLeft', value)
        this
    }
    /** Sets the color of the left border */
    StyleGroup borderLeftColor (value) {
        styles << new Style(name: 'borderLeftColor', value: handleColor(value))
        this
    }
    /** Sets the style of the left border */
    StyleGroup borderLeftStyle (value) {
        styles << new Style('borderLeftStyle', value)
        this
    }
    /** Sets the width of the left border */
    StyleGroup borderLeftWidth (value) {
        styles << new Style(name: 'borderLeftWidth', value: validateLength(value))
        this
    }
    /** A shorthand property for setting or returning all the four border*Radius properties */
    StyleGroup borderRadius (value) {
        Style style = new Style(name: 'borderRadius', value: validateLength(value))
        styles << style
        if (config.addWebkit) styles << cloneWebkit(style)
        if (config.addMoz) styles << cloneMoz(style)
        this
    }
    /** Sets all the borderRight* properties in one declaration */
    StyleGroup borderRight (value) {
        styles << new Style('borderRight', value)
        this
    }
    /** Sets the color of the right border */
    StyleGroup borderRightColor (value) {
        styles << new Style(name: 'borderRightColor', value: handleColor(value))
        this
    }
    /** Sets the style of the right border */
    StyleGroup borderRightStyle (value) {
        styles << new Style('borderRightStyle', value)
        this
    }
    /** Sets the width of the right border */
    StyleGroup borderRightWidth (value) {
        styles << new Style(name: 'borderRightWidth', value: validateLength(value))
        this
    }
    /** Sets the space between cells in a table */
    StyleGroup borderSpacing (value) {
        styles << new Style('borderSpacing', value)
        this
    }
    /** Sets the style of an element's border (can have up to four values) */
    StyleGroup borderStyle (value) {
        styles << new Style('borderStyle', value)
        this
    }
    /** Sets all the borderTop* properties in one declaration */
    StyleGroup borderTop (value) {
        styles << new Style('borderTop', value)
        this
    }
    /** Sets the color of the top border */
    StyleGroup borderTopColor (value) {
        styles << new Style(name: 'borderTopColor', value: handleColor(value))
        this
    }
    /** Sets the shape of the border of the top-left corner */
    StyleGroup borderTopLeftRadius (value) {
        styles << new Style('borderTopLeftRadius', value)
        this
    }
    /** Sets the shape of the border of the top-right corner */
    StyleGroup borderTopRightRadius (value) {
        styles << new Style('borderTopRightRadius', value)
        this
    }
    /** Sets the style of the top border */
    StyleGroup borderTopStyle (value) {
        styles << new Style('borderTopStyle', value)
        this
    }
    /** Sets the width of the top border */
    StyleGroup borderTopWidth (value) {
        styles << new Style(name: 'borderTopWidth', value: validateLength(value))
        this
    }
    /** Sets the width of an element's border (can have up to four values) */
    StyleGroup borderWidth (value) {
        styles << new Style('borderWidth', value)
        this
    }
    /** Sets the bottom position of a positioned element */
    StyleGroup bottom (value) {
        styles << new Style('bottom', value)
        this
    }
    /** Sets the behaviour of the background and border of an element at page-break, or, for in-line elements, at line-break. */
    StyleGroup boxDecorationBreak (value) {
        styles << new Style('boxDecorationBreak', value)
        this
    }
    /** Attaches one or more drop-shadows to the box */
    StyleGroup boxShadow (value) {
        def style = new Style('boxShadow', value)
        styles << style
        if (config.addWebkit) styles << cloneWebkit(style)
        if (config.addMoz) styles << cloneMoz(style)
        this
    }
    /** Allows you to define certain elements to fit an area in a certain way */
    StyleGroup boxSizing (value) {
        styles << new Style('boxSizing', value)
        this
    }
    /** Sets the position of the table caption */
    StyleGroup captionSide (value) {
        styles << new Style('captionSide', value)
        this
    }
    /** Sets the position of the element relative to floating objects */
    StyleGroup clear (value) {
        styles << new Style('clear', value)
        this
    }
    /** Sets which part of a positioned element is visible */
    StyleGroup clip (value) {
        styles << new Style('clip', value)
        this
    }
    /** Sets the color of the text */
    StyleGroup color (value) {
        styles << new Style(name: 'color', value: handleColor(value))
        this
    }
    /** Sets the number of columns an element should be divided into */
    StyleGroup columnCount (value) {
        styles << new Style('columnCount', value)
        this
    }
    /** Sets how to fill columns */
    StyleGroup columnFill (value) {
        styles << new Style('columnFill', value)
        this
    }
    /** Sets the gap between the columns */
    StyleGroup columnGap (value) {
        styles << new Style('columnGap', value)
        this
    }
    /** A shorthand property for setting or returning all the columnRule* properties */
    StyleGroup columnRule (value) {
        styles << new Style('columnRule', value)
        this
    }
    /** Sets the color of the rule between columns */
    StyleGroup columnRuleColor (value) {
        styles << new Style(name: 'columnRuleColor', value: handleColor(value))
        this
    }
    /** Sets the style of the rule between columns */
    StyleGroup columnRuleStyle (value) {
        styles << new Style('columnRuleStyle', value)
        this
    }
    /** Sets the width of the rule between columns */
    StyleGroup columnRuleWidth (value) {
        styles << new Style('columnRuleWidth', value)
        this
    }
    /** A shorthand property for setting or returning columnWidth and columnCount */
    StyleGroup columns (value) {
        styles << new Style('columns', value)
        this
    }
    /** Sets how many columns an element should span across */
    StyleGroup columnSpan (value) {
        styles << new Style('columnSpan', value)
        this
    }
    /** Sets the width of the columns */
    StyleGroup columnWidth (value) {
        styles << new Style('columnWidth', value)
        this
    }
    /** Used with the :before and :after pseudo-elements, to insert generated content */
    StyleGroup content (value) {
        styles << new Style('content', value)
        this
    }
    /** Increments one or more counters */
    StyleGroup counterIncrement (value) {
        styles << new Style('counterIncrement', value)
        this
    }
    /** Creates or resets one or more counters */
    StyleGroup counterReset (value) {
        styles << new Style('counterReset', value)
        this
    }
    /** Sets the type of cursor to display for the mouse pointer */
    StyleGroup cursor (value) {
        styles << new Style('cursor', value)
        this
    }
    /** Sets the text direction */
    StyleGroup direction (value) {
        styles << new Style('direction', value)
        this
    }
    /** Sets an element's display type */
    StyleGroup display (value) {
        styles << new Style('display', value)
        this
    }
    /** Sets whether to show the border and background of empty cells, or not */
    StyleGroup emptyCells (value) {
        styles << new Style('emptyCells', value)
        this
    }
    /** Sets image filters (visual effects, like blur and saturation) */
    StyleGroup filter (value) {
        styles << new Style('filter', value)
        if (config.addMs) styles << cloneMs(styles[-1])
        this
    }
    /** Sets the length of the item, relative to the rest */
    StyleGroup flex (value) {
        styles << new Style('flex', value)
        if (config.addWebkit) styles << cloneWebkit(styles[-1])
        this
    }
    /** Sets the initial length of a flexible item */
    StyleGroup flexBasis (value) {
        styles << new Style('flexBasis', value)
        if (config.addWebkit) styles << cloneWebkit(styles[-1])
        this
    }
    /** Sets the direction of the flexible items */
    StyleGroup flexDirection (value) {
        styles << new Style('flexDirection', value)
        if (config.addWebkit) styles << cloneWebkit(styles[-1])
        this
    }
    /** A shorthand property for the flexDirection and the flexWrap properties */
    StyleGroup flexFlow (value) {
        styles << new Style('flexFlow', value)
        if (config.addWebkit) styles << cloneWebkit(styles[-1])
        this
    }
    /** Sets how much the item will grow relative to the rest */
    StyleGroup flexGrow (value) {
        styles << new Style('flexGrow', value)
        if (config.addWebkit) styles << cloneWebkit(styles[-1])
        this
    }
    /** Sets how the item will shrink relative to the rest */
    StyleGroup flexShrink (value) {
        styles << new Style('flexShrink', value)
        if (config.addWebkit) styles << cloneWebkit(styles[-1])
        this
    }
    /** Sets whether the flexible items should wrap or not */
    StyleGroup flexWrap (value) {
        styles << new Style('flexWrap', value)
        if (config.addWebkit) styles << cloneWebkit(styles[-1])
        this
    }
    /** Sets the horizontal alignment of an element */
    StyleGroup cssFloat (value) {
        styles << new Style('float', value)
        this
    }
    /** Sets fontStyle, fontVariant, fontWeight, fontSize, lineHeight, and fontFamily in one declaration */
    StyleGroup font (value) {
        styles << new Style('font', value)
        this
    }
    /** Sets the font family for text */
    StyleGroup fontFamily (value) {
        styles << new Style('fontFamily', value)
        this
    }
    /** Sets the font size of the text */
    StyleGroup fontSize (value) {
        styles << new Style(name: 'fontSize', value: value)
        this
    }
    /** Sets whether the style of the font is normal, italic or oblique */
    StyleGroup fontStyle (value) {
        styles << new Style('fontStyle', value)
        this
    }
    /** Sets whether the font should be displayed in small capital letters */
    StyleGroup fontVariant (value) {
        styles << new Style('fontVariant', value)
        this
    }
    /** Sets the boldness of the font */
    StyleGroup fontWeight (value) {
        styles << new Style('fontWeight', value)
        this
    }
    /** Preserves the readability of text when font fallback occurs */
    StyleGroup fontSizeAdjust (value) {
        styles << new Style('fontSizeAdjust', value)
        this
    }
    /** Selects a normal, condensed, or expanded face from a font family */
    StyleGroup fontStretch (value) {
        styles << new Style('fontStretch', value)
        this
    }
    /** Specifies whether a punctuation character may be placed outside the line box */
    StyleGroup hangingPunctuation (value) {
        styles << new Style('hangingPunctuation', value)
        this
    }
    /** Sets the height of an element */
    StyleGroup height (value) {
        styles << new Style('height', value)
        this
    }
    /** Sets how to split words to improve the layout of paragraphs */
    StyleGroup hyphens (value) {
        styles << new Style('hyphens', value)
        this
    }
    /** Provides the author the ability to style an element with an iconic equivalent */
    StyleGroup icon (value) {
        styles << new Style('icon', value)
        this
    }
    /** Specifies a rotation in the right or clockwise direction that a user agent applies to an image */
    StyleGroup imageOrientation (value) {
        styles << new Style('imageOrientation', value)
        this
    }
    /** Sets the alignment between the items inside a flexible container when the items do not use all available space. */
    StyleGroup justifyContent (value) {
        styles << new Style('justifyContent', value)
        this
    }
    /** Sets the left position of a positioned element */
    StyleGroup left (value) {
        styles << new Style('left', value)
        this
    }
    /** Sets the space between characters in a text */
    StyleGroup letterSpacing (value) {
        styles << new Style('letterSpacing', value)
        this
    }
    /** Sets the distance between lines in a text */
    StyleGroup lineHeight (value) {
        styles << new Style('lineHeight', value)
        this
    }
    /** Sets listStyleImage, listStylePosition, and listStyleType in one declaration */
    StyleGroup listStyle (value) {
        styles << new Style('listStyle', value)
        this
    }
    /** Sets an image as the list-item marker */
    StyleGroup listStyleImage (value) {
        styles << new Style('listStyleImage', value)
        this
    }
    /** Sets the position of the list-item marker */
    StyleGroup listStylePosition (value) {
        styles << new Style('listStylePosition', value)
        this
    }
    /** Sets the list-item marker type */
    StyleGroup listStyleType (value) {
        styles << new Style('listStyleType', value)
        this
    }
    /** Sets the margins of an element (can have up to four values) */
    StyleGroup margin (value) {
        styles << new Style('margin', value)
        this
    }
    /** Sets the bottom margin of an element */
    StyleGroup marginBottom (value) {
        styles << new Style(name: 'marginBottom', value: value)
        this
    }
    /** Sets the left margin of an element */
    StyleGroup marginLeft (value) {
        styles << new Style(name: 'marginLeft', value: value)
        this
    }
    /** Sets the right margin of an element */
    StyleGroup marginRight (value) {
        styles << new Style(name: 'marginRight', value: value)
        this
    }
    /** Sets the top margin of an element */
    StyleGroup marginTop (value) {
        styles << new Style(name: 'marginTop', value: value)
        this
    }
    /** Sets the maximum height of an element */
    StyleGroup maxHeight (value) {
        styles << new Style(name: 'maxHeight', value: value)
        this
    }
    /** Sets the maximum width of an element */
    StyleGroup maxWidth (value) {
        styles << new Style(name: 'maxWidth', value: value)
        this
    }
    /** Sets the minimum height of an element */
    StyleGroup minHeight (value) {
        styles << new Style(name: 'minHeight', value: value)
        this
    }
    /** Sets the minimum width of an element */
    StyleGroup minWidth (value) {
        styles << new Style(name: 'minWidth', value: value)
        this
    }
    /** Sets where to navigate when using the arrow-down navigation key */
    StyleGroup navDown (value) {
        styles << new Style('navDown', value)
        this
    }
    /** Sets the tabbing order for an element */
    StyleGroup navIndex (value) {
        styles << new Style('navIndex', value)
        this
    }
    /** Sets where to navigate when using the arrow-left navigation key */
    StyleGroup navLeft (value) {
        styles << new Style('navLeft', value)
        this
    }
    /** Sets where to navigate when using the arrow-right navigation key */
    StyleGroup navRight (value) {
        styles << new Style('navRight', value)
        this
    }
    /** Sets where to navigate when using the arrow-up navigation key */
    StyleGroup navUp (value) {
        styles << new Style('navUp', value)
        this
    }
    /** Sets the opacity level for an element */
    StyleGroup opacity (value) {
        styles << new Style('opacity', value)
        this
    }
    /** Sets the order of the flexible item, relative to the rest */
    StyleGroup order (value) {
        styles << new Style('order', value)
        this
    }
    /** Sets the minimum number of lines for an element that must be left at the bottom of a page when a page break occurs inside an element */
    StyleGroup orphans (value) {
        styles << new Style('orphans', value)
        this
    }
    /** Sets all the outline properties in one declaration */
    StyleGroup outline (value) {
        styles << new Style('outline', value)
        this
    }
    /** Sets the color of the outline around a element */
    StyleGroup outlineColor (value) {
        styles << new Style(name: 'outlineColor', value: handleColor(value))
        this
    }
    /** Offsets an outline, and draws it beyond the border edge */
    StyleGroup outlineOffset (value) {
        styles << new Style('outlineOffset', value)
        this
    }
    /** Sets the style of the outline around an element */
    StyleGroup outlineStyle (value) {
        styles << new Style('outlineStyle', value)
        this
    }
    /** Sets the width of the outline around an element */
    StyleGroup outlineWidth (value) {
        styles << new Style('outlineWidth', value)
        this
    }
    /** Sets what to do with content that renders outside the element box */
    StyleGroup overflow (value) {
        styles << new Style('overflow', value)
        this
    }
    /** Specifies what to do with the left/right edges of the content, if it overflows the element's content area */
    StyleGroup overflowX (value) {
        styles << new Style('overflowX', value)
        this
    }
    /** Specifies what to do with the top/bottom edges of the content, if it overflows the element's content area */
    StyleGroup overflowY (value) {
        styles << new Style('overflowY', value)
        this
    }
    /** Sets the padding of an element (can have up to four values) */
    StyleGroup padding (value) {
        styles << new Style('padding', value)
        this
    }
    /** Sets the bottom padding of an element */
    StyleGroup paddingBottom (value) {
        styles << new Style('paddingBottom', value)
        this
    }
    /** Sets the left padding of an element */
    StyleGroup paddingLeft (value) {
        styles << new Style('paddingLeft', value)
        this
    }
    /** Sets the right padding of an element */
    StyleGroup paddingRight (value) {
        styles << new Style('paddingRight', value)
        this
    }
    /** Sets the top padding of an element */
    StyleGroup paddingTop (value) {
        styles << new Style('paddingTop', value)
        this
    }
    /** Sets the page-break behavior after an element */
    StyleGroup pageBreakAfter (value) {
        styles << new Style('pageBreakAfter', value)
        this
    }
    /** Sets the page-break behavior before an element */
    StyleGroup pageBreakBefore (value) {
        styles << new Style('pageBreakBefore', value)
        this
    }
    /** Sets the page-break behavior inside an element */
    StyleGroup pageBreakInside (value) {
        styles << new Style('pageBreakInside', value)
        this
    }
    /** Sets the perspective on how 3D elements are viewed */
    StyleGroup perspective (value) {
        styles << new Style('perspective', value)
        this
    }
    /** Sets the bottom position of 3D elements */
    StyleGroup perspectiveOrigin (value) {
        styles << new Style('perspectiveOrigin', value)
        this
    }
    /** Sets the type of positioning method used for an element (static, relative, absolute or fixed) */
    StyleGroup position (value) {
        styles << new Style('position', value)
        this
    }
    /** Sets the type of quotation marks for embedded quotations */
    StyleGroup quotes (value) {
        styles << new Style('quotes', value)
        this
    }
    /** Sets whether or not an element is resizable by the user */
    StyleGroup resize (value) {
        styles << new Style('resize', value)
        this
    }
    /** Sets the right position of a positioned element */
    StyleGroup right (value) {
        styles << new Style('right', value)
        this
    }
    /** Sets the way to lay out table cells, rows, and columns */
    StyleGroup tableLayout (value) {
        styles << new Style('tableLayout', value)
        this
    }
    /** Sets the length of the tab-character */
    StyleGroup tabSize (value) {
        styles << new Style('tabSize', value)
        this
    }
    /** Sets the horizontal alignment of text */
    StyleGroup textAlign (value) {
        styles << new Style('textAlign', value)
        this
    }
    /** Sets how the last line of a block or a line right before a forced line break is aligned when text-align is ""justify"" */
    StyleGroup textAlignLast (value) {
        styles << new Style('textAlignLast', value)
        this
    }
    /** Sets the decoration of a text */
    StyleGroup textDecoration (value) {
        styles << new Style('textDecoration', value)
        this
    }
    /** Sets the color of the text-decoration */
    StyleGroup textDecorationColor (value) {
        styles << new Style(name: 'textDecorationColor', value: handleColor(value))
        this
    }
    /** Sets the type of line in a text-decoration */
    StyleGroup textDecorationLine (value) {
        styles << new Style('textDecorationLine', value)
        this
    }
    /** Sets the style of the line in a text decoration */
    StyleGroup textDecorationStyle (value) {
        styles << new Style('textDecorationStyle', value)
        this
    }
    /** Sets the indentation of the first line of text */
    StyleGroup textIndent (value) {
        styles << new Style('textIndent', value)
        this
    }
    /** Sets the justification method used when text-align is ""justify"" */
    StyleGroup textJustify (value) {
        styles << new Style('textJustify', value)
        this
    }
    /** Sets what should happen when text overflows the containing element */
    StyleGroup textOverflow (value) {
        styles << new Style('textOverflow', value)
        this
    }
    /** Sets the shadow effect of a text */
    StyleGroup textShadow (value) {
        styles << new Style('textShadow', value)
        this
    }
    /** Sets the capitalization of a text */
    StyleGroup textTransform (value) {
        styles << new Style('textTransform', value)
        this
    }
    /** Sets the top position of a positioned element */
    StyleGroup top (value) {
        styles << new Style('top', value)
        this
    }
    /** Applies a 2D or 3D transformation to an element */
    StyleGroup transform (value) {
        def style = new Style('transform', value)
        styles << style
        cloneTrio(style)
        if (config.addMs) styles << cloneMs(style)
        this
    }
    /** Sets the position of transformed elements */
    StyleGroup transformOrigin (value) {
        def style = new Style('transformOrigin', value)
        styles << style
        cloneTrio(style)
        if (config.addMs) styles << cloneMs(style)
        this
    }
    /** Sets how nested elements are rendered in 3D space */
    StyleGroup transformStyle (value) {
        def style = new Style('transformStyle', value)
        styles << style
        cloneTrio(style)
        if (config.addMs) styles << cloneMs(style)
        this
    }

    /** A shorthand property for setting or returning the four transition properties */
    StyleGroup transition (String value) {
        transitionInternal(value)
    }
    StyleGroup transition (CSSPart part) {
        transitionInternal(part)
    }
    private StyleGroup transitionInternal (value) {
        styles << new Style('transition', value)
        cloneTrio(styles[-1])
        this
    }

    /** A DSL for creating a Transition with a single closure defining a transition value. */
    StyleGroup transition(@DelegatesTo(value=Transition, strategy = Closure.DELEGATE_ONLY) Closure closure) {
        Transition tran = Transition.newInstance()
        closure.delegate = tran
        closure.resolveStrategy = Closure.DELEGATE_ONLY
        closure()
        transitionInternal(tran.value)
    }

    /** A DSL for creating a Transition with more than one closure. */
    StyleGroup transition(@DelegatesTo(value=Transition, strategy = Closure.DELEGATE_ONLY) Closure ... closures) {
        List<Transition> trans = []
        for (closure in closures) {
            Transition tran = Transition.newInstance()
            closure.delegate = tran
            closure.resolveStrategy = Closure.DELEGATE_ONLY
            closure()
            trans.add tran
        }
        transitionInternal(trans.collect{ it.value }.join(','))
    }

    /** Sets the CSS property that the transition effect is for */
    StyleGroup transitionProperty (value) {
        styles << new Style('transitionProperty', value)
        cloneTrio(styles[-1])
        this
    }
    /** Sets how many seconds or milliseconds a transition effect takes to complete */
    StyleGroup transitionDuration (value) {
        styles << new Style('transitionDuration', value)
        cloneTrio(styles[-1])
        this
    }
    /** Sets the speed curve of the transition effect */
    StyleGroup transitionTimingFunction (value) {
        styles << new Style('transitionTimingFunction', value)
        cloneTrio(styles[-1])
        this
    }
    /** Sets when the transition effect will start */
    StyleGroup transitionDelay (value) {
        styles << new Style('transitionDelay', value)
        cloneTrio(styles[-1])
        this
    }
    /** Sets whether the text should be overridden to support multiple languages in the same document */
    StyleGroup unicodeBidi (value) {
        styles << new Style('unicodeBidi', value)
        this
    }
    /** Sets the vertical alignment of the content in an element */
    StyleGroup verticalAlign (value) {
        styles << new Style('verticalAlign', value)
        this
    }
    /** Sets whether an element should be visible */
    StyleGroup visibility (value) {
        styles << new Style('visibility', value)
        this
    }
    /** Sets how to handle tabs, line breaks and whitespace in a text */
    StyleGroup whiteSpace (value) {
        styles << new Style('whiteSpace', value)
        this
    }
    /** Sets the width of an element */
    StyleGroup width (value) {
        styles << new Style(name: 'width', value: value)
        this
    }
    /** Sets line breaking rules for non-CJK scripts */
    StyleGroup wordBreak (value) {
        styles << new Style('wordBreak', value)
        this
    }
    /** Sets the spacing between words in a text */
    StyleGroup wordSpacing (value) {
        styles << new Style('wordSpacing', value)
        this
    }
    /** Allows long, unbreakable words to be broken and wrap to the next line */
    StyleGroup wordWrap (value) {
        styles << new Style('wordWrap', value)
        this
    }
    /** Sets the minimum number of lines for an element that must be visible at the top of a page */
    StyleGroup widows (value) {
        styles << new Style('widows', value)
        this
    }
    /** Sets the stack order of a positioned element */
    StyleGroup zIndex (value) {
        styles << new Style('zIndex', value)
        this
    }

    List transform = []

    /** Defines a 2D transformation, using a matrix of six values. */
    StyleGroup matrix(n0, n1, n2, n3, n4, n5) {
        transform << "matrix($n0,$n1,$n2,$n3,$n4,$n5)"
        this
    }
    /** Defines a 3D transformation, using a 4x4 matrix of 16 values. */
    StyleGroup matrix3d(n0, n1, n2, n3, n4, n5, n6, n7, n8, n9, a, b, c, d, e, f) {
        transform << "matrix3d($n0,$n1,$n2,$n3,$n4,$n5,$n6,$n7,$n8,$n9,$a,$b,$c,$d,$e,$f)"
        this
    }
    /** Defines a 2D translation. */
    StyleGroup translate(x,y) {
        transform << "translate(${validateLength x},${validateLength y})"
        this
    }
    /** Defines a 3D translation. */
    StyleGroup translate3d(x,y,z) {
        transform << "translate3d(${validateLength x},${validateLength y},${validateLength z})"
        this
    }
    /** Defines a translation, using only the value for the X-axis. */
    StyleGroup translateX(x) {
        transform << "translateX(${validateLength x})"
        this
    }
    /** Defines a translation, using only the value for the Y-axis. */
    StyleGroup translateY(y) {
        transform << "translateY(${validateLength y})"
        this
    }
    /** Defines a 3D translation, using only the value for the Z-axis. */
    StyleGroup translateZ(z) {
        transform << "translateZ(${validateLength z})"
        this
    }

    /** Defines a 2D scale transformation. */
    StyleGroup scale(x,y) {
        transform << "scale($x,$y)"
        this
    }
    /** Defines a 3D scale transformation. */
    StyleGroup scale3d(x,y,z) {
        transform << "scale3d($x,$y,$z)"
        this
    }
    /** Defines a scale transformation by giving a value for the X-axis. */
    StyleGroup scaleX(x) {
        transform << "scaleX($x)"
        this
    }
    /** Defines a scale transformation by giving a value for the Y-axis. */
    StyleGroup scaleY(y) {
        transform << "scaleY($y)"
        this
    }
    /** Defines a 3D scale transformation by giving a value for the Z-axis. */
    StyleGroup scaleZ(z) {
        transform << "scaleZ($z)"
        this
    }

    /** Defines a 2D rotation, the angle is specified in the parameter. */
    StyleGroup rotate(angle) {
        validateAngle angle
        transform << "rotate($angle)"
        this
    }
    /** Defines a 3D rotation. */
    StyleGroup rotate3d(x,y,z,angle) {
        validateAngle angle
        transform << "rotate3d($x,$y,$z,$angle)"
        this
    }
    /** Defines a 3D rotation along the X-axis. */
    StyleGroup rotateX(angle) {
        validateAngle angle
        transform << "rotateX($angle)"
        this
    }
    /** Defines a 3D rotation along the Y-axis. */
    StyleGroup rotateY(angle) {
        validateAngle angle
        transform << "rotateY($angle)"
        this
    }
    /** Defines a 3D rotation along the Z-axis. */
    StyleGroup rotateZ(angle) {
        validateAngle angle
        transform << "rotateZ($angle)"
        this
    }
    /** Defines a 2D skew transformation along the X- and the Y-axis. */
    StyleGroup skew(xangle,yangle) {
        validateAngle xangle
        validateAngle yangle
        transform << "skew($xangle,$yangle)"
        this
    }
    /** Defines a 2D skew transformation along the X-axis. */
    StyleGroup skewX(angle) {
        validateAngle angle
        transform << "skewX($angle)"
        this
    }
    /** Defines a 2D skew transformation along the Y-axis. */
    StyleGroup skewY(angle) {
        validateAngle angle
        transform << "skewY($angle)"
        this
    }

    static def validateAngle(angle) {
        if (angle instanceof  Measurement) assert angle.trig
        angle
    }

    static def validateLength(x) {
        if (x instanceof Measurement) assert x.distance || x.relative || x.percent || x.pixel
        x
    }

    static def validateTime(x) {
        if (x instanceof Measurement) assert x.time
        x
    }

    private String handleColor(value) {
        (value instanceof Number) ? new Color((Number) value) : value
    }

}

