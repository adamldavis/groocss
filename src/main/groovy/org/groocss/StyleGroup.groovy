package org.groocss

import groovy.transform.*

/** A group of styles as represented in a CSS file. */
@CompileStatic
class StyleGroup extends Selectable {

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
        StyleGroup other = owner.groups.find {it.selector == otherSelector}
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

    Style cloneWebkit(Style s) { new Style(name: '-webkit-' + s.name, value: s.value) }
    Style cloneMoz(Style s) { new Style(name: '-moz-' + s.name, value: s.value) }
    Style cloneMs(Style s) { new Style(name: '-ms-' + s.name, value: s.value) }
    Style cloneOpera(Style s) { new Style(name: '-o-' + s.name, value: s.value) }
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
        styles << new Style(name: 'alignContent', value: "$value")
        this
    }
    /** Sets the alignment for items inside a flexible container */
    StyleGroup alignItems (value) {
        styles << new Style(name: 'alignItems', value: "$value")
        this
    }
    /** Sets the alignment for selected items inside a flexible container */
    StyleGroup alignSelf (value) {
        styles << new Style(name: 'alignSelf', value: "$value")
        this
    }
    /** A shorthand property for all the animation properties below, except the animationPlayState property */
    StyleGroup animation (value) {
        styles << new Style(name: 'animation', value: "$value")
        cloneTrio(styles[-1])
        this
    }
    /** Sets when the animation will start */
    StyleGroup animationDelay (value) {
        styles << new Style(name: 'animationDelay', value: "${validateTime value}")
        cloneTrio(styles[-1])
        this
    }
    /** Sets whether or not the animation should play in reverse on alternate cycles */
    StyleGroup animationDirection (value) {
        styles << new Style(name: 'animationDirection', value: "$value")
        cloneTrio(styles[-1])
        this
    }
    /** Sets how many seconds or milliseconds an animation takes to complete one cycle */
    StyleGroup animationDuration (value) {
        styles << new Style(name: 'animationDuration', value: "${validateTime value}")
        cloneTrio(styles[-1])
        this
    }
    /** Sets what values are applied by the animation outside the time it is executing */
    StyleGroup animationFillMode (value) {
        styles << new Style(name: 'animationFillMode', value: "$value")
        cloneTrio(styles[-1])
        this
    }
    /** Sets the number of times an animation should be played */
    StyleGroup animationIterationCount (value) {
        styles << new Style(name: 'animationIterationCount', value: "$value")
        cloneTrio(styles[-1])
        this
    }
    /** Sets a name for the @keyframes animation */
    StyleGroup animationName (value) {
        styles << new Style(name: 'animationName', value: "$value")
        cloneTrio(styles[-1])
        this
    }
    /** Sets the speed curve of the animation */
    StyleGroup animationTimingFunction (value) {
        styles << new Style(name: 'animationTimingFunction', value: "$value")
        cloneTrio(styles[-1])
        this
    }
    /** Sets whether the animation is running or paused */
    StyleGroup animationPlayState (value) {
        styles << new Style(name: 'animationPlayState', value: "$value")
        cloneTrio(styles[-1])
        this
    }
    /** Sets all the background properties in one declaration */
    StyleGroup background (value) {
        styles << new Style(name: 'background', value: "$value")
        this
    }
    /** Sets whether a background-image is fixed or scrolls with the page */
    StyleGroup backgroundAttachment (value) {
        styles << new Style(name: 'backgroundAttachment', value: "$value")
        this
    }
    /** Sets the background-color of an element */
    StyleGroup backgroundColor (value) {
        styles << new Style(name: 'backgroundColor', value: handleColor(value))
        this
    }
    /** Sets the background-image for an element */
    StyleGroup backgroundImage (value) {
        styles << new Style(name: 'backgroundImage', value: "$value")
        this
    }
    /** Sets the starting position of a background-image */
    StyleGroup backgroundPosition (value) {
        styles << new Style(name: 'backgroundPosition', value: "$value")
        this
    }
    /** Sets how to repeat (tile) a background-image */
    StyleGroup backgroundRepeat (value) {
        styles << new Style(name: 'backgroundRepeat', value: "$value")
        this
    }
    /** Sets the painting area of the background */
    StyleGroup backgroundClip (value) {
        styles << new Style(name: 'backgroundClip', value: "$value")
        this
    }
    /** Sets the positioning area of the background images */
    StyleGroup backgroundOrigin (value) {
        styles << new Style(name: 'backgroundOrigin', value: "$value")
        this
    }
    /** Sets the size of the background image */
    StyleGroup backgroundSize (value) {
        styles << new Style(name: 'backgroundSize', value: "$value")
        this
    }
    /** Sets whether or not an element should be visible when not facing the screen */
    StyleGroup backfaceVisibility (value) {
        styles << new Style(name: 'backfaceVisibility', value: "$value")
        this
    }
    /** Sets borderWidth, borderStyle, and borderColor in one declaration */
    StyleGroup border (value) {
        styles << new Style(name: 'border', value: "$value")
        this
    }
    /** Sets all the borderBottom* properties in one declaration */
    StyleGroup borderBottom (value) {
        styles << new Style(name: 'borderBottom', value: "$value")
        this
    }
    /** Sets the color of the bottom border */
    StyleGroup borderBottomColor (value) {
        styles << new Style(name: 'borderBottomColor', value: handleColor(value))
        this
    }
    /** Sets the shape of the border of the bottom-left corner */
    StyleGroup borderBottomLeftRadius (value) {
        styles << new Style(name: 'borderBottomLeftRadius', value: "$value")
        this
    }
    /** Sets the shape of the border of the bottom-right corner */
    StyleGroup borderBottomRightRadius (value) {
        styles << new Style(name: 'borderBottomRightRadius', value: "$value")
        this
    }
    /** Sets the style of the bottom border */
    StyleGroup borderBottomStyle (value) {
        styles << new Style(name: 'borderBottomStyle', value: "$value")
        this
    }
    /** Sets the width of the bottom border */
    StyleGroup borderBottomWidth (value) {
        styles << new Style(name: 'borderBottomWidth', value: "${validateLength(value)}")
        this
    }
    /** Sets whether the table border should be collapsed into a single border, or not */
    StyleGroup borderCollapse (value) {
        styles << new Style(name: 'borderCollapse', value: "$value")
        this
    }
    /** Sets the color of an element's border (can have up to four values) */
    StyleGroup borderColor (value) {
        styles << new Style(name: 'borderColor', value: handleColor(value))
        this
    }
    /** A shorthand property for setting or returning all the borderImage* properties */
    StyleGroup borderImage (value) {
        styles << new Style(name: 'borderImage', value: "$value")
        this
    }
    /** Sets the amount by which the border image area extends beyond the border box */
    StyleGroup borderImageOutset (value) {
        styles << new Style(name: 'borderImageOutset', value: "$value")
        this
    }
    /** Sets whether the image-border should be repeated, rounded or stretched */
    StyleGroup borderImageRepeat (value) {
        styles << new Style(name: 'borderImageRepeat', value: "$value")
        this
    }
    /** Sets the inward offsets of the image-border */
    StyleGroup borderImageSlice (value) {
        styles << new Style(name: 'borderImageSlice', value: "$value")
        this
    }
    /** Sets the image to be used as a border */
    StyleGroup borderImageSource (value) {
        styles << new Style(name: 'borderImageSource', value: "$value")
        this
    }
    /** Sets the widths of the image-border */
    StyleGroup borderImageWidth (value) {
        styles << new Style(name: 'borderImageWidth', value: "$value")
        this
    }
    /** Sets all the borderLeft* properties in one declaration */
    StyleGroup borderLeft (value) {
        styles << new Style(name: 'borderLeft', value: "$value")
        this
    }
    /** Sets the color of the left border */
    StyleGroup borderLeftColor (value) {
        styles << new Style(name: 'borderLeftColor', value: handleColor(value))
        this
    }
    /** Sets the style of the left border */
    StyleGroup borderLeftStyle (value) {
        styles << new Style(name: 'borderLeftStyle', value: "$value")
        this
    }
    /** Sets the width of the left border */
    StyleGroup borderLeftWidth (value) {
        styles << new Style(name: 'borderLeftWidth', value: "${validateLength(value)}")
        this
    }
    /** A shorthand property for setting or returning all the four border*Radius properties */
    StyleGroup borderRadius (value) {
        Style style = new Style(name: 'borderRadius', value: "${validateLength(value)}")
        styles << style
        if (config.addWebkit) styles << cloneWebkit(style)
        if (config.addMoz) styles << cloneMoz(style)
        this
    }
    /** Sets all the borderRight* properties in one declaration */
    StyleGroup borderRight (value) {
        styles << new Style(name: 'borderRight', value: "$value")
        this
    }
    /** Sets the color of the right border */
    StyleGroup borderRightColor (value) {
        styles << new Style(name: 'borderRightColor', value: handleColor(value))
        this
    }
    /** Sets the style of the right border */
    StyleGroup borderRightStyle (value) {
        styles << new Style(name: 'borderRightStyle', value: "$value")
        this
    }
    /** Sets the width of the right border */
    StyleGroup borderRightWidth (value) {
        styles << new Style(name: 'borderRightWidth', value: "${validateLength(value)}")
        this
    }
    /** Sets the space between cells in a table */
    StyleGroup borderSpacing (value) {
        styles << new Style(name: 'borderSpacing', value: "$value")
        this
    }
    /** Sets the style of an element's border (can have up to four values) */
    StyleGroup borderStyle (value) {
        styles << new Style(name: 'borderStyle', value: "$value")
        this
    }
    /** Sets all the borderTop* properties in one declaration */
    StyleGroup borderTop (value) {
        styles << new Style(name: 'borderTop', value: "$value")
        this
    }
    /** Sets the color of the top border */
    StyleGroup borderTopColor (value) {
        styles << new Style(name: 'borderTopColor', value: handleColor(value))
        this
    }
    /** Sets the shape of the border of the top-left corner */
    StyleGroup borderTopLeftRadius (value) {
        styles << new Style(name: 'borderTopLeftRadius', value: "$value")
        this
    }
    /** Sets the shape of the border of the top-right corner */
    StyleGroup borderTopRightRadius (value) {
        styles << new Style(name: 'borderTopRightRadius', value: "$value")
        this
    }
    /** Sets the style of the top border */
    StyleGroup borderTopStyle (value) {
        styles << new Style(name: 'borderTopStyle', value: "$value")
        this
    }
    /** Sets the width of the top border */
    StyleGroup borderTopWidth (value) {
        styles << new Style(name: 'borderTopWidth', value: "${validateLength(value)}")
        this
    }
    /** Sets the width of an element's border (can have up to four values) */
    StyleGroup borderWidth (value) {
        styles << new Style(name: 'borderWidth', value: "$value")
        this
    }
    /** Sets the bottom position of a positioned element */
    StyleGroup bottom (value) {
        styles << new Style(name: 'bottom', value: "$value")
        this
    }
    /** Sets the behaviour of the background and border of an element at page-break, or, for in-line elements, at line-break. */
    StyleGroup boxDecorationBreak (value) {
        styles << new Style(name: 'boxDecorationBreak', value: "$value")
        this
    }
    /** Attaches one or more drop-shadows to the box */
    StyleGroup boxShadow (value) {
        def style = new Style(name: 'boxShadow', value: "$value")
        styles << style
        if (config.addWebkit) styles << cloneWebkit(style)
        if (config.addMoz) styles << cloneMoz(style)
        this
    }
    /** Allows you to define certain elements to fit an area in a certain way */
    StyleGroup boxSizing (value) {
        styles << new Style(name: 'boxSizing', value: "$value")
        this
    }
    /** Sets the position of the table caption */
    StyleGroup captionSide (value) {
        styles << new Style(name: 'captionSide', value: "$value")
        this
    }
    /** Sets the position of the element relative to floating objects */
    StyleGroup clear (value) {
        styles << new Style(name: 'clear', value: "$value")
        this
    }
    /** Sets which part of a positioned element is visible */
    StyleGroup clip (value) {
        styles << new Style(name: 'clip', value: "$value")
        this
    }
    /** Sets the color of the text */
    StyleGroup color (value) {
        styles << new Style(name: 'color', value: handleColor(value))
        this
    }
    /** Sets the number of columns an element should be divided into */
    StyleGroup columnCount (value) {
        styles << new Style(name: 'columnCount', value: "$value")
        this
    }
    /** Sets how to fill columns */
    StyleGroup columnFill (value) {
        styles << new Style(name: 'columnFill', value: "$value")
        this
    }
    /** Sets the gap between the columns */
    StyleGroup columnGap (value) {
        styles << new Style(name: 'columnGap', value: "$value")
        this
    }
    /** A shorthand property for setting or returning all the columnRule* properties */
    StyleGroup columnRule (value) {
        styles << new Style(name: 'columnRule', value: "$value")
        this
    }
    /** Sets the color of the rule between columns */
    StyleGroup columnRuleColor (value) {
        styles << new Style(name: 'columnRuleColor', value: handleColor(value))
        this
    }
    /** Sets the style of the rule between columns */
    StyleGroup columnRuleStyle (value) {
        styles << new Style(name: 'columnRuleStyle', value: "$value")
        this
    }
    /** Sets the width of the rule between columns */
    StyleGroup columnRuleWidth (value) {
        styles << new Style(name: 'columnRuleWidth', value: "$value")
        this
    }
    /** A shorthand property for setting or returning columnWidth and columnCount */
    StyleGroup columns (value) {
        styles << new Style(name: 'columns', value: "$value")
        this
    }
    /** Sets how many columns an element should span across */
    StyleGroup columnSpan (value) {
        styles << new Style(name: 'columnSpan', value: "$value")
        this
    }
    /** Sets the width of the columns */
    StyleGroup columnWidth (value) {
        styles << new Style(name: 'columnWidth', value: "$value")
        this
    }
    /** Used with the :before and :after pseudo-elements, to insert generated content */
    StyleGroup content (value) {
        styles << new Style(name: 'content', value: "$value")
        this
    }
    /** Increments one or more counters */
    StyleGroup counterIncrement (value) {
        styles << new Style(name: 'counterIncrement', value: "$value")
        this
    }
    /** Creates or resets one or more counters */
    StyleGroup counterReset (value) {
        styles << new Style(name: 'counterReset', value: "$value")
        this
    }
    /** Sets the type of cursor to display for the mouse pointer */
    StyleGroup cursor (value) {
        styles << new Style(name: 'cursor', value: "$value")
        this
    }
    /** Sets the text direction */
    StyleGroup direction (value) {
        styles << new Style(name: 'direction', value: "$value")
        this
    }
    /** Sets an element's display type */
    StyleGroup display (value) {
        styles << new Style(name: 'display', value: "$value")
        this
    }
    /** Sets whether to show the border and background of empty cells, or not */
    StyleGroup emptyCells (value) {
        styles << new Style(name: 'emptyCells', value: "$value")
        this
    }
    /** Sets image filters (visual effects, like blur and saturation) */
    StyleGroup filter (value) {
        styles << new Style(name: 'filter', value: "$value")
        if (config.addMs) styles << cloneMs(styles[-1])
        this
    }
    /** Sets the length of the item, relative to the rest */
    StyleGroup flex (value) {
        styles << new Style(name: 'flex', value: "$value")
        if (config.addWebkit) styles << cloneWebkit(styles[-1])
        this
    }
    /** Sets the initial length of a flexible item */
    StyleGroup flexBasis (value) {
        styles << new Style(name: 'flexBasis', value: "$value")
        if (config.addWebkit) styles << cloneWebkit(styles[-1])
        this
    }
    /** Sets the direction of the flexible items */
    StyleGroup flexDirection (value) {
        styles << new Style(name: 'flexDirection', value: "$value")
        if (config.addWebkit) styles << cloneWebkit(styles[-1])
        this
    }
    /** A shorthand property for the flexDirection and the flexWrap properties */
    StyleGroup flexFlow (value) {
        styles << new Style(name: 'flexFlow', value: "$value")
        if (config.addWebkit) styles << cloneWebkit(styles[-1])
        this
    }
    /** Sets how much the item will grow relative to the rest */
    StyleGroup flexGrow (value) {
        styles << new Style(name: 'flexGrow', value: "$value")
        if (config.addWebkit) styles << cloneWebkit(styles[-1])
        this
    }
    /** Sets how the item will shrink relative to the rest */
    StyleGroup flexShrink (value) {
        styles << new Style(name: 'flexShrink', value: "$value")
        if (config.addWebkit) styles << cloneWebkit(styles[-1])
        this
    }
    /** Sets whether the flexible items should wrap or not */
    StyleGroup flexWrap (value) {
        styles << new Style(name: 'flexWrap', value: "$value")
        if (config.addWebkit) styles << cloneWebkit(styles[-1])
        this
    }
    /** Sets the horizontal alignment of an element */
    StyleGroup cssFloat (value) {
        styles << new Style(name: 'float', value: "$value")
        this
    }
    /** Sets fontStyle, fontVariant, fontWeight, fontSize, lineHeight, and fontFamily in one declaration */
    StyleGroup font (value) {
        styles << new Style(name: 'font', value: "$value")
        this
    }
    /** Sets the font family for text */
    StyleGroup fontFamily (value) {
        styles << new Style(name: 'fontFamily', value: "$value")
        this
    }
    /** Sets the font size of the text */
    StyleGroup fontSize (value) {
        styles << new Style(name: 'fontSize', value: "${validateLength value}")
        this
    }
    /** Sets whether the style of the font is normal, italic or oblique */
    StyleGroup fontStyle (value) {
        styles << new Style(name: 'fontStyle', value: "$value")
        this
    }
    /** Sets whether the font should be displayed in small capital letters */
    StyleGroup fontVariant (value) {
        styles << new Style(name: 'fontVariant', value: "$value")
        this
    }
    /** Sets the boldness of the font */
    StyleGroup fontWeight (value) {
        styles << new Style(name: 'fontWeight', value: "$value")
        this
    }
    /** Preserves the readability of text when font fallback occurs */
    StyleGroup fontSizeAdjust (value) {
        styles << new Style(name: 'fontSizeAdjust', value: "$value")
        this
    }
    /** Selects a normal, condensed, or expanded face from a font family */
    StyleGroup fontStretch (value) {
        styles << new Style(name: 'fontStretch', value: "$value")
        this
    }
    /** Specifies whether a punctuation character may be placed outside the line box */
    StyleGroup hangingPunctuation (value) {
        styles << new Style(name: 'hangingPunctuation', value: "$value")
        this
    }
    /** Sets the height of an element */
    StyleGroup height (value) {
        styles << new Style(name: 'height', value: "$value")
        this
    }
    /** Sets how to split words to improve the layout of paragraphs */
    StyleGroup hyphens (value) {
        styles << new Style(name: 'hyphens', value: "$value")
        this
    }
    /** Provides the author the ability to style an element with an iconic equivalent */
    StyleGroup icon (value) {
        styles << new Style(name: 'icon', value: "$value")
        this
    }
    /** Specifies a rotation in the right or clockwise direction that a user agent applies to an image */
    StyleGroup imageOrientation (value) {
        styles << new Style(name: 'imageOrientation', value: "$value")
        this
    }
    /** Sets the alignment between the items inside a flexible container when the items do not use all available space. */
    StyleGroup justifyContent (value) {
        styles << new Style(name: 'justifyContent', value: "$value")
        this
    }
    /** Sets the left position of a positioned element */
    StyleGroup left (value) {
        styles << new Style(name: 'left', value: "$value")
        this
    }
    /** Sets the space between characters in a text */
    StyleGroup letterSpacing (value) {
        styles << new Style(name: 'letterSpacing', value: "$value")
        this
    }
    /** Sets the distance between lines in a text */
    StyleGroup lineHeight (value) {
        styles << new Style(name: 'lineHeight', value: "$value")
        this
    }
    /** Sets listStyleImage, listStylePosition, and listStyleType in one declaration */
    StyleGroup listStyle (value) {
        styles << new Style(name: 'listStyle', value: "$value")
        this
    }
    /** Sets an image as the list-item marker */
    StyleGroup listStyleImage (value) {
        styles << new Style(name: 'listStyleImage', value: "$value")
        this
    }
    /** Sets the position of the list-item marker */
    StyleGroup listStylePosition (value) {
        styles << new Style(name: 'listStylePosition', value: "$value")
        this
    }
    /** Sets the list-item marker type */
    StyleGroup listStyleType (value) {
        styles << new Style(name: 'listStyleType', value: "$value")
        this
    }
    /** Sets the margins of an element (can have up to four values) */
    StyleGroup margin (value) {
        styles << new Style(name: 'margin', value: "$value")
        this
    }
    /** Sets the bottom margin of an element */
    StyleGroup marginBottom (value) {
        styles << new Style(name: 'marginBottom', value: "${validateLength value}")
        this
    }
    /** Sets the left margin of an element */
    StyleGroup marginLeft (value) {
        styles << new Style(name: 'marginLeft', value: "${validateLength value}")
        this
    }
    /** Sets the right margin of an element */
    StyleGroup marginRight (value) {
        styles << new Style(name: 'marginRight', value: "${validateLength value}")
        this
    }
    /** Sets the top margin of an element */
    StyleGroup marginTop (value) {
        styles << new Style(name: 'marginTop', value: "${validateLength value}")
        this
    }
    /** Sets the maximum height of an element */
    StyleGroup maxHeight (value) {
        styles << new Style(name: 'maxHeight', value: "${validateLength value}")
        this
    }
    /** Sets the maximum width of an element */
    StyleGroup maxWidth (value) {
        styles << new Style(name: 'maxWidth', value: "${validateLength value}")
        this
    }
    /** Sets the minimum height of an element */
    StyleGroup minHeight (value) {
        styles << new Style(name: 'minHeight', value: "${validateLength value}")
        this
    }
    /** Sets the minimum width of an element */
    StyleGroup minWidth (value) {
        styles << new Style(name: 'minWidth', value: "${validateLength value}")
        this
    }
    /** Sets where to navigate when using the arrow-down navigation key */
    StyleGroup navDown (value) {
        styles << new Style(name: 'navDown', value: "$value")
        this
    }
    /** Sets the tabbing order for an element */
    StyleGroup navIndex (value) {
        styles << new Style(name: 'navIndex', value: "$value")
        this
    }
    /** Sets where to navigate when using the arrow-left navigation key */
    StyleGroup navLeft (value) {
        styles << new Style(name: 'navLeft', value: "$value")
        this
    }
    /** Sets where to navigate when using the arrow-right navigation key */
    StyleGroup navRight (value) {
        styles << new Style(name: 'navRight', value: "$value")
        this
    }
    /** Sets where to navigate when using the arrow-up navigation key */
    StyleGroup navUp (value) {
        styles << new Style(name: 'navUp', value: "$value")
        this
    }
    /** Sets the opacity level for an element */
    StyleGroup opacity (value) {
        styles << new Style(name: 'opacity', value: "$value")
        this
    }
    /** Sets the order of the flexible item, relative to the rest */
    StyleGroup order (value) {
        styles << new Style(name: 'order', value: "$value")
        this
    }
    /** Sets the minimum number of lines for an element that must be left at the bottom of a page when a page break occurs inside an element */
    StyleGroup orphans (value) {
        styles << new Style(name: 'orphans', value: "$value")
        this
    }
    /** Sets all the outline properties in one declaration */
    StyleGroup outline (value) {
        styles << new Style(name: 'outline', value: "$value")
        this
    }
    /** Sets the color of the outline around a element */
    StyleGroup outlineColor (value) {
        styles << new Style(name: 'outlineColor', value: handleColor(value))
        this
    }
    /** Offsets an outline, and draws it beyond the border edge */
    StyleGroup outlineOffset (value) {
        styles << new Style(name: 'outlineOffset', value: "$value")
        this
    }
    /** Sets the style of the outline around an element */
    StyleGroup outlineStyle (value) {
        styles << new Style(name: 'outlineStyle', value: "$value")
        this
    }
    /** Sets the width of the outline around an element */
    StyleGroup outlineWidth (value) {
        styles << new Style(name: 'outlineWidth', value: "$value")
        this
    }
    /** Sets what to do with content that renders outside the element box */
    StyleGroup overflow (value) {
        styles << new Style(name: 'overflow', value: "$value")
        this
    }
    /** Specifies what to do with the left/right edges of the content, if it overflows the element's content area */
    StyleGroup overflowX (value) {
        styles << new Style(name: 'overflowX', value: "$value")
        this
    }
    /** Specifies what to do with the top/bottom edges of the content, if it overflows the element's content area */
    StyleGroup overflowY (value) {
        styles << new Style(name: 'overflowY', value: "$value")
        this
    }
    /** Sets the padding of an element (can have up to four values) */
    StyleGroup padding (value) {
        styles << new Style(name: 'padding', value: "$value")
        this
    }
    /** Sets the bottom padding of an element */
    StyleGroup paddingBottom (value) {
        styles << new Style(name: 'paddingBottom', value: "$value")
        this
    }
    /** Sets the left padding of an element */
    StyleGroup paddingLeft (value) {
        styles << new Style(name: 'paddingLeft', value: "$value")
        this
    }
    /** Sets the right padding of an element */
    StyleGroup paddingRight (value) {
        styles << new Style(name: 'paddingRight', value: "$value")
        this
    }
    /** Sets the top padding of an element */
    StyleGroup paddingTop (value) {
        styles << new Style(name: 'paddingTop', value: "$value")
        this
    }
    /** Sets the page-break behavior after an element */
    StyleGroup pageBreakAfter (value) {
        styles << new Style(name: 'pageBreakAfter', value: "$value")
        this
    }
    /** Sets the page-break behavior before an element */
    StyleGroup pageBreakBefore (value) {
        styles << new Style(name: 'pageBreakBefore', value: "$value")
        this
    }
    /** Sets the page-break behavior inside an element */
    StyleGroup pageBreakInside (value) {
        styles << new Style(name: 'pageBreakInside', value: "$value")
        this
    }
    /** Sets the perspective on how 3D elements are viewed */
    StyleGroup perspective (value) {
        styles << new Style(name: 'perspective', value: "$value")
        this
    }
    /** Sets the bottom position of 3D elements */
    StyleGroup perspectiveOrigin (value) {
        styles << new Style(name: 'perspectiveOrigin', value: "$value")
        this
    }
    /** Sets the type of positioning method used for an element (static, relative, absolute or fixed) */
    StyleGroup position (value) {
        styles << new Style(name: 'position', value: "$value")
        this
    }
    /** Sets the type of quotation marks for embedded quotations */
    StyleGroup quotes (value) {
        styles << new Style(name: 'quotes', value: "$value")
        this
    }
    /** Sets whether or not an element is resizable by the user */
    StyleGroup resize (value) {
        styles << new Style(name: 'resize', value: "$value")
        this
    }
    /** Sets the right position of a positioned element */
    StyleGroup right (value) {
        styles << new Style(name: 'right', value: "$value")
        this
    }
    /** Sets the way to lay out table cells, rows, and columns */
    StyleGroup tableLayout (value) {
        styles << new Style(name: 'tableLayout', value: "$value")
        this
    }
    /** Sets the length of the tab-character */
    StyleGroup tabSize (value) {
        styles << new Style(name: 'tabSize', value: "$value")
        this
    }
    /** Sets the horizontal alignment of text */
    StyleGroup textAlign (value) {
        styles << new Style(name: 'textAlign', value: "$value")
        this
    }
    /** Sets how the last line of a block or a line right before a forced line break is aligned when text-align is ""justify"" */
    StyleGroup textAlignLast (value) {
        styles << new Style(name: 'textAlignLast', value: "$value")
        this
    }
    /** Sets the decoration of a text */
    StyleGroup textDecoration (value) {
        styles << new Style(name: 'textDecoration', value: "$value")
        this
    }
    /** Sets the color of the text-decoration */
    StyleGroup textDecorationColor (value) {
        styles << new Style(name: 'textDecorationColor', value: handleColor(value))
        this
    }
    /** Sets the type of line in a text-decoration */
    StyleGroup textDecorationLine (value) {
        styles << new Style(name: 'textDecorationLine', value: "$value")
        this
    }
    /** Sets the style of the line in a text decoration */
    StyleGroup textDecorationStyle (value) {
        styles << new Style(name: 'textDecorationStyle', value: "$value")
        this
    }
    /** Sets the indentation of the first line of text */
    StyleGroup textIndent (value) {
        styles << new Style(name: 'textIndent', value: "$value")
        this
    }
    /** Sets the justification method used when text-align is ""justify"" */
    StyleGroup textJustify (value) {
        styles << new Style(name: 'textJustify', value: "$value")
        this
    }
    /** Sets what should happen when text overflows the containing element */
    StyleGroup textOverflow (value) {
        styles << new Style(name: 'textOverflow', value: "$value")
        this
    }
    /** Sets the shadow effect of a text */
    StyleGroup textShadow (value) {
        styles << new Style(name: 'textShadow', value: "$value")
        this
    }
    /** Sets the capitalization of a text */
    StyleGroup textTransform (value) {
        styles << new Style(name: 'textTransform', value: "$value")
        this
    }
    /** Sets the top position of a positioned element */
    StyleGroup top (value) {
        styles << new Style(name: 'top', value: "$value")
        this
    }
    /** Applies a 2D or 3D transformation to an element */
    StyleGroup transform (value) {
        def style = new Style(name: 'transform', value: "$value")
        styles << style
        cloneTrio(style)
        if (config.addMs) styles << cloneMs(style)
        this
    }
    /** Sets the position of transformed elements */
    StyleGroup transformOrigin (value) {
        def style = new Style(name: 'transformOrigin', value: "$value")
        styles << style
        cloneTrio(style)
        if (config.addMs) styles << cloneMs(style)
        this
    }
    /** Sets how nested elements are rendered in 3D space */
    StyleGroup transformStyle (value) {
        def style = new Style(name: 'transformStyle', value: "$value")
        styles << style
        cloneTrio(style)
        if (config.addMs) styles << cloneMs(style)
        this
    }
    /** A shorthand property for setting or returning the four transition properties */
    StyleGroup transition (value) {
        styles << new Style(name: 'transition', value: "$value")
        cloneTrio(styles[-1])
        this
    }
    /** Sets the CSS property that the transition effect is for */
    StyleGroup transitionProperty (value) {
        styles << new Style(name: 'transitionProperty', value: "$value")
        cloneTrio(styles[-1])
        this
    }
    /** Sets how many seconds or milliseconds a transition effect takes to complete */
    StyleGroup transitionDuration (value) {
        styles << new Style(name: 'transitionDuration', value: "$value")
        cloneTrio(styles[-1])
        this
    }
    /** Sets the speed curve of the transition effect */
    StyleGroup transitionTimingFunction (value) {
        styles << new Style(name: 'transitionTimingFunction', value: "$value")
        cloneTrio(styles[-1])
        this
    }
    /** Sets when the transition effect will start */
    StyleGroup transitionDelay (value) {
        styles << new Style(name: 'transitionDelay', value: "$value")
        cloneTrio(styles[-1])
        this
    }
    /** Sets whether the text should be overridden to support multiple languages in the same document */
    StyleGroup unicodeBidi (value) {
        styles << new Style(name: 'unicodeBidi', value: "$value")
        this
    }
    /** Sets the vertical alignment of the content in an element */
    StyleGroup verticalAlign (value) {
        styles << new Style(name: 'verticalAlign', value: "$value")
        this
    }
    /** Sets whether an element should be visible */
    StyleGroup visibility (value) {
        styles << new Style(name: 'visibility', value: "$value")
        this
    }
    /** Sets how to handle tabs, line breaks and whitespace in a text */
    StyleGroup whiteSpace (value) {
        styles << new Style(name: 'whiteSpace', value: "$value")
        this
    }
    /** Sets the width of an element */
    StyleGroup width (value) {
        styles << new Style(name: 'width', value: "${validateLength value}")
        this
    }
    /** Sets line breaking rules for non-CJK scripts */
    StyleGroup wordBreak (value) {
        styles << new Style(name: 'wordBreak', value: "$value")
        this
    }
    /** Sets the spacing between words in a text */
    StyleGroup wordSpacing (value) {
        styles << new Style(name: 'wordSpacing', value: "$value")
        this
    }
    /** Allows long, unbreakable words to be broken and wrap to the next line */
    StyleGroup wordWrap (value) {
        styles << new Style(name: 'wordWrap', value: "$value")
        this
    }
    /** Sets the minimum number of lines for an element that must be visible at the top of a page */
    StyleGroup widows (value) {
        styles << new Style(name: 'widows', value: "$value")
        this
    }
    /** Sets the stack order of a positioned element */
    StyleGroup zIndex (value) {
        styles << new Style(name: 'zIndex', value: "$value")
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

    def validateAngle(angle) {
        if (angle instanceof  Measurement) assert angle.trig
        angle
    }

    def validateLength(x) {
        if (x instanceof Measurement) assert x.distance || x.relative || x.percent || x.pixel
        x
    }

    def validateTime(x) {
        if (x instanceof Measurement) assert x.time
        x
    }

    private String handleColor(value) {
        (value instanceof Number) ? "${new Color((Number) value)}" : "$value"
    }

}

