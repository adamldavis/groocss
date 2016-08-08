package org.groocss

import groovy.transform.*

/** A group of styles as represented in a CSS file. */
@CompileStatic
class StyleGroup {

    String selector

    List<Style> styles = []

    Config config
    
    public void add(Style style) { styles << style }
    public void leftShift(Style style) { add style }

    StyleGroup selector(String sel) {
        selector = sel
        this
    }
    
    String toString() {
        selector + '{' + styles.join('\n\t') + '}'
    }
    Style cloneWebkit(Style s) { new Style(name: '-webkit-' + s.name, value: s.value) }
    Style cloneMoz(Style s) { new Style(name: '-moz-' + s.name, value: s.value) }
    Style cloneMs(Style s) { new Style(name: '-ms-' + s.name, value: s.value) }
    Style cloneOpera(Style s) { new Style(name: '-o-' + s.name, value: s.value) }
    void cloneTrio(Style style) {
        if (config.addWebkit) styles << cloneWebkit(style)
        if (config.addMoz) styles << cloneMoz(style)
        if (config.addOpera) styles << cloneOpera(style)
    }
    
    /** Sets or returns the alignment between the lines inside a flexible container when the items do not use all available space */
    StyleGroup alignContent (value) {
        styles << new Style(name: 'alignContent', value: "$value")
        this
    }
    /** Sets or returns the alignment for items inside a flexible container */
    StyleGroup alignItems (value) {
        styles << new Style(name: 'alignItems', value: "$value")
        this
    }
    /** Sets or returns the alignment for selected items inside a flexible container */
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
    /** Sets or returns when the animation will start */
    StyleGroup animationDelay (value) {
        styles << new Style(name: 'animationDelay', value: "$value")
        cloneTrio(styles[-1])
        this
    }
    /** Sets or returns whether or not the animation should play in reverse on alternate cycles */
    StyleGroup animationDirection (value) {
        styles << new Style(name: 'animationDirection', value: "$value")
        cloneTrio(styles[-1])
        this
    }
    /** Sets or returns how many seconds or milliseconds an animation takes to complete one cycle */
    StyleGroup animationDuration (value) {
        styles << new Style(name: 'animationDuration', value: "$value")
        cloneTrio(styles[-1])
        this
    }
    /** Sets or returns what values are applied by the animation outside the time it is executing */
    StyleGroup animationFillMode (value) {
        styles << new Style(name: 'animationFillMode', value: "$value")
        cloneTrio(styles[-1])
        this
    }
    /** Sets or returns the number of times an animation should be played */
    StyleGroup animationIterationCount (value) {
        styles << new Style(name: 'animationIterationCount', value: "$value")
        cloneTrio(styles[-1])
        this
    }
    /** Sets or returns a name for the @keyframes animation */
    StyleGroup animationName (value) {
        styles << new Style(name: 'animationName', value: "$value")
        cloneTrio(styles[-1])
        this
    }
    /** Sets or returns the speed curve of the animation */
    StyleGroup animationTimingFunction (value) {
        styles << new Style(name: 'animationTimingFunction', value: "$value")
        cloneTrio(styles[-1])
        this
    }
    /** Sets or returns whether the animation is running or paused */
    StyleGroup animationPlayState (value) {
        styles << new Style(name: 'animationPlayState', value: "$value")
        cloneTrio(styles[-1])
        this
    }
    /** Sets or returns all the background properties in one declaration */
    StyleGroup background (value) {
        styles << new Style(name: 'background', value: "$value")
        this
    }
    /** Sets or returns whether a background-image is fixed or scrolls with the page */
    StyleGroup backgroundAttachment (value) {
        styles << new Style(name: 'backgroundAttachment', value: "$value")
        this
    }
    /** Sets or returns the background-color of an element */
    StyleGroup backgroundColor (value) {
        styles << new Style(name: 'backgroundColor', value: "$value")
        this
    }
    /** Sets or returns the background-image for an element */
    StyleGroup backgroundImage (value) {
        styles << new Style(name: 'backgroundImage', value: "$value")
        this
    }
    /** Sets or returns the starting position of a background-image */
    StyleGroup backgroundPosition (value) {
        styles << new Style(name: 'backgroundPosition', value: "$value")
        this
    }
    /** Sets or returns how to repeat (tile) a background-image */
    StyleGroup backgroundRepeat (value) {
        styles << new Style(name: 'backgroundRepeat', value: "$value")
        this
    }
    /** Sets or returns the painting area of the background */
    StyleGroup backgroundClip (value) {
        styles << new Style(name: 'backgroundClip', value: "$value")
        this
    }
    /** Sets or returns the positioning area of the background images */
    StyleGroup backgroundOrigin (value) {
        styles << new Style(name: 'backgroundOrigin', value: "$value")
        this
    }
    /** Sets or returns the size of the background image */
    StyleGroup backgroundSize (value) {
        styles << new Style(name: 'backgroundSize', value: "$value")
        this
    }
    /** Sets or returns whether or not an element should be visible when not facing the screen */
    StyleGroup backfaceVisibility (value) {
        styles << new Style(name: 'backfaceVisibility', value: "$value")
        this
    }
    /** Sets or returns borderWidth, borderStyle, and borderColor in one declaration */
    StyleGroup border (value) {
        styles << new Style(name: 'border', value: "$value")
        this
    }
    /** Sets or returns all the borderBottom* properties in one declaration */
    StyleGroup borderBottom (value) {
        styles << new Style(name: 'borderBottom', value: "$value")
        this
    }
    /** Sets or returns the color of the bottom border */
    StyleGroup borderBottomColor (value) {
        styles << new Style(name: 'borderBottomColor', value: "$value")
        this
    }
    /** Sets or returns the shape of the border of the bottom-left corner */
    StyleGroup borderBottomLeftRadius (value) {
        styles << new Style(name: 'borderBottomLeftRadius', value: "$value")
        this
    }
    /** Sets or returns the shape of the border of the bottom-right corner */
    StyleGroup borderBottomRightRadius (value) {
        styles << new Style(name: 'borderBottomRightRadius', value: "$value")
        this
    }
    /** Sets or returns the style of the bottom border */
    StyleGroup borderBottomStyle (value) {
        styles << new Style(name: 'borderBottomStyle', value: "$value")
        this
    }
    /** Sets or returns the width of the bottom border */
    StyleGroup borderBottomWidth (value) {
        styles << new Style(name: 'borderBottomWidth', value: "$value")
        this
    }
    /** Sets or returns whether the table border should be collapsed into a single border, or not */
    StyleGroup borderCollapse (value) {
        styles << new Style(name: 'borderCollapse', value: "$value")
        this
    }
    /** Sets or returns the color of an element's border (can have up to four values) */
    StyleGroup borderColor (value) {
        styles << new Style(name: 'borderColor', value: "$value")
        this
    }
    /** A shorthand property for setting or returning all the borderImage* properties */
    StyleGroup borderImage (value) {
        styles << new Style(name: 'borderImage', value: "$value")
        this
    }
    /** Sets or returns the amount by which the border image area extends beyond the border box */
    StyleGroup borderImageOutset (value) {
        styles << new Style(name: 'borderImageOutset', value: "$value")
        this
    }
    /** Sets or returns whether the image-border should be repeated, rounded or stretched */
    StyleGroup borderImageRepeat (value) {
        styles << new Style(name: 'borderImageRepeat', value: "$value")
        this
    }
    /** Sets or returns the inward offsets of the image-border */
    StyleGroup borderImageSlice (value) {
        styles << new Style(name: 'borderImageSlice', value: "$value")
        this
    }
    /** Sets or returns the image to be used as a border */
    StyleGroup borderImageSource (value) {
        styles << new Style(name: 'borderImageSource', value: "$value")
        this
    }
    /** Sets or returns the widths of the image-border */
    StyleGroup borderImageWidth (value) {
        styles << new Style(name: 'borderImageWidth', value: "$value")
        this
    }
    /** Sets or returns all the borderLeft* properties in one declaration */
    StyleGroup borderLeft (value) {
        styles << new Style(name: 'borderLeft', value: "$value")
        this
    }
    /** Sets or returns the color of the left border */
    StyleGroup borderLeftColor (value) {
        styles << new Style(name: 'borderLeftColor', value: "$value")
        this
    }
    /** Sets or returns the style of the left border */
    StyleGroup borderLeftStyle (value) {
        styles << new Style(name: 'borderLeftStyle', value: "$value")
        this
    }
    /** Sets or returns the width of the left border */
    StyleGroup borderLeftWidth (value) {
        styles << new Style(name: 'borderLeftWidth', value: "$value")
        this
    }
    /** A shorthand property for setting or returning all the four border*Radius properties */
    StyleGroup borderRadius (value) {
        styles << new Style(name: 'borderRadius', value: "$value")
        this
    }
    /** Sets or returns all the borderRight* properties in one declaration */
    StyleGroup borderRight (value) {
        styles << new Style(name: 'borderRight', value: "$value")
        this
    }
    /** Sets or returns the color of the right border */
    StyleGroup borderRightColor (value) {
        styles << new Style(name: 'borderRightColor', value: "$value")
        this
    }
    /** Sets or returns the style of the right border */
    StyleGroup borderRightStyle (value) {
        styles << new Style(name: 'borderRightStyle', value: "$value")
        this
    }
    /** Sets or returns the width of the right border */
    StyleGroup borderRightWidth (value) {
        styles << new Style(name: 'borderRightWidth', value: "$value")
        this
    }
    /** Sets or returns the space between cells in a table */
    StyleGroup borderSpacing (value) {
        styles << new Style(name: 'borderSpacing', value: "$value")
        this
    }
    /** Sets or returns the style of an element's border (can have up to four values) */
    StyleGroup borderStyle (value) {
        styles << new Style(name: 'borderStyle', value: "$value")
        this
    }
    /** Sets or returns all the borderTop* properties in one declaration */
    StyleGroup borderTop (value) {
        styles << new Style(name: 'borderTop', value: "$value")
        this
    }
    /** Sets or returns the color of the top border */
    StyleGroup borderTopColor (value) {
        styles << new Style(name: 'borderTopColor', value: "$value")
        this
    }
    /** Sets or returns the shape of the border of the top-left corner */
    StyleGroup borderTopLeftRadius (value) {
        styles << new Style(name: 'borderTopLeftRadius', value: "$value")
        this
    }
    /** Sets or returns the shape of the border of the top-right corner */
    StyleGroup borderTopRightRadius (value) {
        styles << new Style(name: 'borderTopRightRadius', value: "$value")
        this
    }
    /** Sets or returns the style of the top border */
    StyleGroup borderTopStyle (value) {
        styles << new Style(name: 'borderTopStyle', value: "$value")
        this
    }
    /** Sets or returns the width of the top border */
    StyleGroup borderTopWidth (value) {
        styles << new Style(name: 'borderTopWidth', value: "$value")
        this
    }
    /** Sets or returns the width of an element's border (can have up to four values) */
    StyleGroup borderWidth (value) {
        styles << new Style(name: 'borderWidth', value: "$value")
        this
    }
    /** Sets or returns the bottom position of a positioned element */
    StyleGroup bottom (value) {
        styles << new Style(name: 'bottom', value: "$value")
        this
    }
    /** Sets or returns the behaviour of the background and border of an element at page-break, or, for in-line elements, at line-break. */
    StyleGroup boxDecorationBreak (value) {
        styles << new Style(name: 'boxDecorationBreak', value: "$value")
        this
    }
    /** Attaches one or more drop-shadows to the box */
    StyleGroup boxShadow (value) {
        styles << new Style(name: 'boxShadow', value: "$value")
        this
    }
    /** Allows you to define certain elements to fit an area in a certain way */
    StyleGroup boxSizing (value) {
        styles << new Style(name: 'boxSizing', value: "$value")
        this
    }
    /** Sets or returns the position of the table caption */
    StyleGroup captionSide (value) {
        styles << new Style(name: 'captionSide', value: "$value")
        this
    }
    /** Sets or returns the position of the element relative to floating objects */
    StyleGroup clear (value) {
        styles << new Style(name: 'clear', value: "$value")
        this
    }
    /** Sets or returns which part of a positioned element is visible */
    StyleGroup clip (value) {
        styles << new Style(name: 'clip', value: "$value")
        this
    }
    /** Sets or returns the color of the text */
    StyleGroup color (value) {
        styles << new Style(name: 'color', value: "$value")
        this
    }
    /** Sets or returns the number of columns an element should be divided into */
    StyleGroup columnCount (value) {
        styles << new Style(name: 'columnCount', value: "$value")
        this
    }
    /** Sets or returns how to fill columns */
    StyleGroup columnFill (value) {
        styles << new Style(name: 'columnFill', value: "$value")
        this
    }
    /** Sets or returns the gap between the columns */
    StyleGroup columnGap (value) {
        styles << new Style(name: 'columnGap', value: "$value")
        this
    }
    /** A shorthand property for setting or returning all the columnRule* properties */
    StyleGroup columnRule (value) {
        styles << new Style(name: 'columnRule', value: "$value")
        this
    }
    /** Sets or returns the color of the rule between columns */
    StyleGroup columnRuleColor (value) {
        styles << new Style(name: 'columnRuleColor', value: "$value")
        this
    }
    /** Sets or returns the style of the rule between columns */
    StyleGroup columnRuleStyle (value) {
        styles << new Style(name: 'columnRuleStyle', value: "$value")
        this
    }
    /** Sets or returns the width of the rule between columns */
    StyleGroup columnRuleWidth (value) {
        styles << new Style(name: 'columnRuleWidth', value: "$value")
        this
    }
    /** A shorthand property for setting or returning columnWidth and columnCount */
    StyleGroup columns (value) {
        styles << new Style(name: 'columns', value: "$value")
        this
    }
    /** Sets or returns how many columns an element should span across */
    StyleGroup columnSpan (value) {
        styles << new Style(name: 'columnSpan', value: "$value")
        this
    }
    /** Sets or returns the width of the columns */
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
    /** Sets or returns the type of cursor to display for the mouse pointer */
    StyleGroup cursor (value) {
        styles << new Style(name: 'cursor', value: "$value")
        this
    }
    /** Sets or returns the text direction */
    StyleGroup direction (value) {
        styles << new Style(name: 'direction', value: "$value")
        this
    }
    /** Sets or returns an element's display type */
    StyleGroup display (value) {
        styles << new Style(name: 'display', value: "$value")
        this
    }
    /** Sets or returns whether to show the border and background of empty cells, or not */
    StyleGroup emptyCells (value) {
        styles << new Style(name: 'emptyCells', value: "$value")
        this
    }
    /** Sets or returns image filters (visual effects, like blur and saturation) */
    StyleGroup filter (value) {
        styles << new Style(name: 'filter', value: "$value")
        if (config.addMs) styles << cloneMs(styles[-1])
        this
    }
    /** Sets or returns the length of the item, relative to the rest */
    StyleGroup flex (value) {
        styles << new Style(name: 'flex', value: "$value")
        if (config.addWebkit) styles << cloneWebkit(styles[-1])
        this
    }
    /** Sets or returns the initial length of a flexible item */
    StyleGroup flexBasis (value) {
        styles << new Style(name: 'flexBasis', value: "$value")
        if (config.addWebkit) styles << cloneWebkit(styles[-1])
        this
    }
    /** Sets or returns the direction of the flexible items */
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
    /** Sets or returns how much the item will grow relative to the rest */
    StyleGroup flexGrow (value) {
        styles << new Style(name: 'flexGrow', value: "$value")
        if (config.addWebkit) styles << cloneWebkit(styles[-1])
        this
    }
    /** Sets or returns how the item will shrink relative to the rest */
    StyleGroup flexShrink (value) {
        styles << new Style(name: 'flexShrink', value: "$value")
        if (config.addWebkit) styles << cloneWebkit(styles[-1])
        this
    }
    /** Sets or returns whether the flexible items should wrap or not */
    StyleGroup flexWrap (value) {
        styles << new Style(name: 'flexWrap', value: "$value")
        if (config.addWebkit) styles << cloneWebkit(styles[-1])
        this
    }
    /** Sets or returns the horizontal alignment of an element */
    StyleGroup cssFloat (value) {
        styles << new Style(name: 'float', value: "$value")
        this
    }
    /** Sets or returns fontStyle, fontVariant, fontWeight, fontSize, lineHeight, and fontFamily in one declaration */
    StyleGroup font (value) {
        styles << new Style(name: 'font', value: "$value")
        this
    }
    /** Sets or returns the font family for text */
    StyleGroup fontFamily (value) {
        styles << new Style(name: 'fontFamily', value: "$value")
        this
    }
    /** Sets or returns the font size of the text */
    StyleGroup fontSize (value) {
        styles << new Style(name: 'fontSize', value: "$value")
        this
    }
    /** Sets or returns whether the style of the font is normal, italic or oblique */
    StyleGroup fontStyle (value) {
        styles << new Style(name: 'fontStyle', value: "$value")
        this
    }
    /** Sets or returns whether the font should be displayed in small capital letters */
    StyleGroup fontVariant (value) {
        styles << new Style(name: 'fontVariant', value: "$value")
        this
    }
    /** Sets or returns the boldness of the font */
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
    /** Sets or returns the height of an element */
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
    /** Sets or returns the alignment between the items inside a flexible container when the items do not use all available space. */
    StyleGroup justifyContent (value) {
        styles << new Style(name: 'justifyContent', value: "$value")
        this
    }
    /** Sets or returns the left position of a positioned element */
    StyleGroup left (value) {
        styles << new Style(name: 'left', value: "$value")
        this
    }
    /** Sets or returns the space between characters in a text */
    StyleGroup letterSpacing (value) {
        styles << new Style(name: 'letterSpacing', value: "$value")
        this
    }
    /** Sets or returns the distance between lines in a text */
    StyleGroup lineHeight (value) {
        styles << new Style(name: 'lineHeight', value: "$value")
        this
    }
    /** Sets or returns listStyleImage, listStylePosition, and listStyleType in one declaration */
    StyleGroup listStyle (value) {
        styles << new Style(name: 'listStyle', value: "$value")
        this
    }
    /** Sets or returns an image as the list-item marker */
    StyleGroup listStyleImage (value) {
        styles << new Style(name: 'listStyleImage', value: "$value")
        this
    }
    /** Sets or returns the position of the list-item marker */
    StyleGroup listStylePosition (value) {
        styles << new Style(name: 'listStylePosition', value: "$value")
        this
    }
    /** Sets or returns the list-item marker type */
    StyleGroup listStyleType (value) {
        styles << new Style(name: 'listStyleType', value: "$value")
        this
    }
    /** Sets or returns the margins of an element (can have up to four values) */
    StyleGroup margin (value) {
        styles << new Style(name: 'margin', value: "$value")
        this
    }
    /** Sets or returns the bottom margin of an element */
    StyleGroup marginBottom (value) {
        styles << new Style(name: 'marginBottom', value: "$value")
        this
    }
    /** Sets or returns the left margin of an element */
    StyleGroup marginLeft (value) {
        styles << new Style(name: 'marginLeft', value: "$value")
        this
    }
    /** Sets or returns the right margin of an element */
    StyleGroup marginRight (value) {
        styles << new Style(name: 'marginRight', value: "$value")
        this
    }
    /** Sets or returns the top margin of an element */
    StyleGroup marginTop (value) {
        styles << new Style(name: 'marginTop', value: "$value")
        this
    }
    /** Sets or returns the maximum height of an element */
    StyleGroup maxHeight (value) {
        styles << new Style(name: 'maxHeight', value: "$value")
        this
    }
    /** Sets or returns the maximum width of an element */
    StyleGroup maxWidth (value) {
        styles << new Style(name: 'maxWidth', value: "$value")
        this
    }
    /** Sets or returns the minimum height of an element */
    StyleGroup minHeight (value) {
        styles << new Style(name: 'minHeight', value: "$value")
        this
    }
    /** Sets or returns the minimum width of an element */
    StyleGroup minWidth (value) {
        styles << new Style(name: 'minWidth', value: "$value")
        this
    }
    /** Sets or returns where to navigate when using the arrow-down navigation key */
    StyleGroup navDown (value) {
        styles << new Style(name: 'navDown', value: "$value")
        this
    }
    /** Sets or returns the tabbing order for an element */
    StyleGroup navIndex (value) {
        styles << new Style(name: 'navIndex', value: "$value")
        this
    }
    /** Sets or returns where to navigate when using the arrow-left navigation key */
    StyleGroup navLeft (value) {
        styles << new Style(name: 'navLeft', value: "$value")
        this
    }
    /** Sets or returns where to navigate when using the arrow-right navigation key */
    StyleGroup navRight (value) {
        styles << new Style(name: 'navRight', value: "$value")
        this
    }
    /** Sets or returns where to navigate when using the arrow-up navigation key */
    StyleGroup navUp (value) {
        styles << new Style(name: 'navUp', value: "$value")
        this
    }
    /** Sets or returns the opacity level for an element */
    StyleGroup opacity (value) {
        styles << new Style(name: 'opacity', value: "$value")
        this
    }
    /** Sets or returns the order of the flexible item, relative to the rest */
    StyleGroup order (value) {
        styles << new Style(name: 'order', value: "$value")
        this
    }
    /** Sets or returns the minimum number of lines for an element that must be left at the bottom of a page when a page break occurs inside an element */
    StyleGroup orphans (value) {
        styles << new Style(name: 'orphans', value: "$value")
        this
    }
    /** Sets or returns all the outline properties in one declaration */
    StyleGroup outline (value) {
        styles << new Style(name: 'outline', value: "$value")
        this
    }
    /** Sets or returns the color of the outline around a element */
    StyleGroup outlineColor (value) {
        styles << new Style(name: 'outlineColor', value: "$value")
        this
    }
    /** Offsets an outline, and draws it beyond the border edge */
    StyleGroup outlineOffset (value) {
        styles << new Style(name: 'outlineOffset', value: "$value")
        this
    }
    /** Sets or returns the style of the outline around an element */
    StyleGroup outlineStyle (value) {
        styles << new Style(name: 'outlineStyle', value: "$value")
        this
    }
    /** Sets or returns the width of the outline around an element */
    StyleGroup outlineWidth (value) {
        styles << new Style(name: 'outlineWidth', value: "$value")
        this
    }
    /** Sets or returns what to do with content that renders outside the element box */
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
    /** Sets or returns the padding of an element (can have up to four values) */
    StyleGroup padding (value) {
        styles << new Style(name: 'padding', value: "$value")
        this
    }
    /** Sets or returns the bottom padding of an element */
    StyleGroup paddingBottom (value) {
        styles << new Style(name: 'paddingBottom', value: "$value")
        this
    }
    /** Sets or returns the left padding of an element */
    StyleGroup paddingLeft (value) {
        styles << new Style(name: 'paddingLeft', value: "$value")
        this
    }
    /** Sets or returns the right padding of an element */
    StyleGroup paddingRight (value) {
        styles << new Style(name: 'paddingRight', value: "$value")
        this
    }
    /** Sets or returns the top padding of an element */
    StyleGroup paddingTop (value) {
        styles << new Style(name: 'paddingTop', value: "$value")
        this
    }
    /** Sets or returns the page-break behavior after an element */
    StyleGroup pageBreakAfter (value) {
        styles << new Style(name: 'pageBreakAfter', value: "$value")
        this
    }
    /** Sets or returns the page-break behavior before an element */
    StyleGroup pageBreakBefore (value) {
        styles << new Style(name: 'pageBreakBefore', value: "$value")
        this
    }
    /** Sets or returns the page-break behavior inside an element */
    StyleGroup pageBreakInside (value) {
        styles << new Style(name: 'pageBreakInside', value: "$value")
        this
    }
    /** Sets or returns the perspective on how 3D elements are viewed */
    StyleGroup perspective (value) {
        styles << new Style(name: 'perspective', value: "$value")
        this
    }
    /** Sets or returns the bottom position of 3D elements */
    StyleGroup perspectiveOrigin (value) {
        styles << new Style(name: 'perspectiveOrigin', value: "$value")
        this
    }
    /** Sets or returns the type of positioning method used for an element (static, relative, absolute or fixed) */
    StyleGroup position (value) {
        styles << new Style(name: 'position', value: "$value")
        this
    }
    /** Sets or returns the type of quotation marks for embedded quotations */
    StyleGroup quotes (value) {
        styles << new Style(name: 'quotes', value: "$value")
        this
    }
    /** Sets or returns whether or not an element is resizable by the user */
    StyleGroup resize (value) {
        styles << new Style(name: 'resize', value: "$value")
        this
    }
    /** Sets or returns the right position of a positioned element */
    StyleGroup right (value) {
        styles << new Style(name: 'right', value: "$value")
        this
    }
    /** Sets or returns the way to lay out table cells, rows, and columns */
    StyleGroup tableLayout (value) {
        styles << new Style(name: 'tableLayout', value: "$value")
        this
    }
    /** Sets or returns the length of the tab-character */
    StyleGroup tabSize (value) {
        styles << new Style(name: 'tabSize', value: "$value")
        this
    }
    /** Sets or returns the horizontal alignment of text */
    StyleGroup textAlign (value) {
        styles << new Style(name: 'textAlign', value: "$value")
        this
    }
    /** Sets or returns how the last line of a block or a line right before a forced line break is aligned when text-align is ""justify"" */
    StyleGroup textAlignLast (value) {
        styles << new Style(name: 'textAlignLast', value: "$value")
        this
    }
    /** Sets or returns the decoration of a text */
    StyleGroup textDecoration (value) {
        styles << new Style(name: 'textDecoration', value: "$value")
        this
    }
    /** Sets or returns the color of the text-decoration */
    StyleGroup textDecorationColor (value) {
        styles << new Style(name: 'textDecorationColor', value: "$value")
        this
    }
    /** Sets or returns the type of line in a text-decoration */
    StyleGroup textDecorationLine (value) {
        styles << new Style(name: 'textDecorationLine', value: "$value")
        this
    }
    /** Sets or returns the style of the line in a text decoration */
    StyleGroup textDecorationStyle (value) {
        styles << new Style(name: 'textDecorationStyle', value: "$value")
        this
    }
    /** Sets or returns the indentation of the first line of text */
    StyleGroup textIndent (value) {
        styles << new Style(name: 'textIndent', value: "$value")
        this
    }
    /** Sets or returns the justification method used when text-align is ""justify"" */
    StyleGroup textJustify (value) {
        styles << new Style(name: 'textJustify', value: "$value")
        this
    }
    /** Sets or returns what should happen when text overflows the containing element */
    StyleGroup textOverflow (value) {
        styles << new Style(name: 'textOverflow', value: "$value")
        this
    }
    /** Sets or returns the shadow effect of a text */
    StyleGroup textShadow (value) {
        styles << new Style(name: 'textShadow', value: "$value")
        this
    }
    /** Sets or returns the capitalization of a text */
    StyleGroup textTransform (value) {
        styles << new Style(name: 'textTransform', value: "$value")
        this
    }
    /** Sets or returns the top position of a positioned element */
    StyleGroup top (value) {
        styles << new Style(name: 'top', value: "$value")
        this
    }
    /** Applies a 2D or 3D transformation to an element */
    StyleGroup transform (value) {
        styles << new Style(name: 'transform', value: "$value")
        cloneTrio(styles[-1])
        if (config.addMs) styles << cloneMs(styles[-1])
        this
    }
    /** Sets or returns the position of transformed elements */
    StyleGroup transformOrigin (value) {
        styles << new Style(name: 'transformOrigin', value: "$value")
        cloneTrio(styles[-1])
        if (config.addMs) styles << cloneMs(styles[-1])
        this
    }
    /** Sets or returns how nested elements are rendered in 3D space */
    StyleGroup transformStyle (value) {
        styles << new Style(name: 'transformStyle', value: "$value")
        cloneTrio(styles[-1])
        if (config.addMs) styles << cloneMs(styles[-1])
        this
    }
    /** A shorthand property for setting or returning the four transition properties */
    StyleGroup transition (value) {
        styles << new Style(name: 'transition', value: "$value")
        cloneTrio(styles[-1])
        this
    }
    /** Sets or returns the CSS property that the transition effect is for */
    StyleGroup transitionProperty (value) {
        styles << new Style(name: 'transitionProperty', value: "$value")
        cloneTrio(styles[-1])
        this
    }
    /** Sets or returns how many seconds or milliseconds a transition effect takes to complete */
    StyleGroup transitionDuration (value) {
        styles << new Style(name: 'transitionDuration', value: "$value")
        cloneTrio(styles[-1])
        this
    }
    /** Sets or returns the speed curve of the transition effect */
    StyleGroup transitionTimingFunction (value) {
        styles << new Style(name: 'transitionTimingFunction', value: "$value")
        cloneTrio(styles[-1])
        this
    }
    /** Sets or returns when the transition effect will start */
    StyleGroup transitionDelay (value) {
        styles << new Style(name: 'transitionDelay', value: "$value")
        cloneTrio(styles[-1])
        this
    }
    /** Sets or returns whether the text should be overridden to support multiple languages in the same document */
    StyleGroup unicodeBidi (value) {
        styles << new Style(name: 'unicodeBidi', value: "$value")
        this
    }
    /** Sets or returns the vertical alignment of the content in an element */
    StyleGroup verticalAlign (value) {
        styles << new Style(name: 'verticalAlign', value: "$value")
        this
    }
    /** Sets or returns whether an element should be visible */
    StyleGroup visibility (value) {
        styles << new Style(name: 'visibility', value: "$value")
        this
    }
    /** Sets or returns how to handle tabs, line breaks and whitespace in a text */
    StyleGroup whiteSpace (value) {
        styles << new Style(name: 'whiteSpace', value: "$value")
        this
    }
    /** Sets or returns the width of an element */
    StyleGroup width (value) {
        styles << new Style(name: 'width', value: "$value")
        this
    }
    /** Sets or returns line breaking rules for non-CJK scripts */
    StyleGroup wordBreak (value) {
        styles << new Style(name: 'wordBreak', value: "$value")
        this
    }
    /** Sets or returns the spacing between words in a text */
    StyleGroup wordSpacing (value) {
        styles << new Style(name: 'wordSpacing', value: "$value")
        this
    }
    /** Allows long, unbreakable words to be broken and wrap to the next line */
    StyleGroup wordWrap (value) {
        styles << new Style(name: 'wordWrap', value: "$value")
        this
    }
    /** Sets or returns the minimum number of lines for an element that must be visible at the top of a page */
    StyleGroup widows (value) {
        styles << new Style(name: 'widows', value: "$value")
        this
    }
    /** Sets or returns the stack order of a positioned element */
    StyleGroup zIndex (value) {
        styles << new Style(name: 'zIndex', value: "$value")
        this
    }
}

