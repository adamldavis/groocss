package org.groocss

import static org.groocss.StyleGroup.*
import static org.groocss.Style.toDashed

/**
 * Created by adavis on 10/20/17.
 */
class Transition implements CSSPart {

    String property
    def duration = ''
    String timing = ''
    def delay = ''

    /** Overridden to validate value if its a Measurement. */
    void setDuration(duration) {
        this.@duration = validateTime(duration)
    }

    /** Overridden to validate value if its a Measurement. */
    void setDelay(delay) {
        this.@delay = validateTime(delay)
    }

    static Transition newInstance() {
        new Transition()
    }
    
    String toString() {
        "transition: $value"
    }
    String getValue() {
        [toDashed(property), duration, toDashed(timing), delay].collect {"$it"}.join(' ')
    }

    @Override
    boolean isEmpty() {
        !property && !duration && !timing && !delay
    }

    static enum TransitionProperty {
        background,backgroundColor,backgroundPosition,backgroundSize,border,borderBottom,
        borderBottomColor,borderBottomLeftRadius,borderBottomRightRadius,borderBottomWidth,
        borderColor,borderLeft,borderLeftColor,borderLeftWidth,borderRight,borderRightColor,
        borderRightWidth,borderSpacing,borderTop,borderTopColor,borderTopLeftRadius,
        borderTopRightRadius,borderTopWidth,
        bottom,boxShadow,clip,color,columnCount,columnGap,columnRule,columnRuleColor,columnRuleWidth,columnWidth,
        columns,filter,flex,flexBasis,flexGrow,flexShrink,font,fontSize,fontSizeAdjust,
        fontStretch,fontWeight,height,left,letterSpacing,lineHeight,
        margin,marginBottom,marginLeft,marginRight,marginTop,maxHeight,maxWidth,
        minHeight,minWidth,opacity,order,outline,outlineColor,outlineOffset,outlineWidth,
        padding,paddingBottom,paddingLeft,paddingRight,paddingTop,perspective,perspectiveOrigin,
        right,textDecorationColor,textIndent,textShadow,top,transform,transformOrigin,verticalAlign,
        visibility, width, wordSpacing, zIndex
    }
    static enum TransitionTimingFunction {
        ease, linear, easeIn, easeOut, easeInOut
    }

    interface TransitionDelayDSL {
        TransitionTimingDSL delay (delayValue)
    }
    
    class TransitionTimingDSL implements TransitionDelayDSL {
        /** Specify the timing-function using the given enum. */
        TransitionDelayDSL timingFunction(TransitionTimingFunction ttf) {
            timing = ttf.name(); this
        }

        /** Specifies a transition effect with a slow start, then fast, then end slowly (this is default)*/
        TransitionDelayDSL ease (delayValue) { timing = 'ease'; delay = delayValue; timingDSL }
        
        /** Specifies a transition effect with the same speed from start to end*/
        TransitionDelayDSL linear (delayValue) { timing = 'linear'; delay = delayValue; timingDSL }
        
        /** Specifies a transition effect with a slow start*/
        TransitionDelayDSL easeIn (delayValue) { timing = 'ease-in'; delay = delayValue; timingDSL }
        
        /** Specifies a transition effect with a slow end*/
        TransitionDelayDSL easeOut (delayValue) { timing = 'ease-out'; delay = delayValue; timingDSL }
        
        /** Specifies a transition effect with a slow start and end*/
        TransitionDelayDSL easeInOut (delayValue) { timing = 'ease-in-out'; delay = delayValue; timingDSL }
        
        /** Lets you define your own values in a cubic-bezier function*/
        TransitionDelayDSL cubicBezier(n1,n2,n3,n4) {
            validateCubics(n1,n2,n3,n4)
            timing = "cubic-bezier($n1, $n2, $n3, $n4)"
            timingDSL
        }

        TransitionTimingDSL delay (delayValue) { delay = delayValue; timingDSL }
    }
    class TransitionDSL extends TransitionTimingDSL {
        TransitionTimingDSL duration (durationValue) { duration = durationValue; timingDSL }
    }

    /** Validates given values are between zero and one. */
    static void validateCubics(Object... vals) {
        for (num in vals) { assert ((!(num instanceof Number)) || (num >= 0 && num <= 1.0)) }
    }

    TransitionTimingDSL timingDSL = new TransitionTimingDSL()

    TransitionDSL property(TransitionProperty property) {
        this.property = property.name()
        new TransitionDSL()
    }

    /** Sets all the background properties in one declaration */
    TransitionTimingDSL background (durationVal) {
        property = 'background'
        duration = durationVal
        timingDSL
    }
    /** Sets the background-color of an element */
    TransitionTimingDSL backgroundColor (durationVal) {
        property = 'backgroundColor'
        duration = durationVal
        timingDSL
    }
    /** Sets the background-image for an element */
    TransitionTimingDSL backgroundImage (durationVal) {
        property = 'backgroundImage'
        duration = durationVal
        timingDSL
    }
    /** Sets the starting position of a background-image */
    TransitionTimingDSL backgroundPosition (durationVal) {
        property = 'backgroundPosition'
        duration = durationVal
        timingDSL
    }
    /** Sets how to repeat (tile) a background-image */
    TransitionTimingDSL backgroundRepeat (durationVal) {
        property = 'backgroundRepeat'
        duration = durationVal
        timingDSL
    }
    /** Sets the painting area of the background */
    TransitionTimingDSL backgroundClip (durationVal) {
        property = 'backgroundClip'
        duration = durationVal
        timingDSL
    }
    /** Sets the positioning area of the background images */
    TransitionTimingDSL backgroundOrigin (durationVal) {
        property = 'backgroundOrigin'
        duration = durationVal
        timingDSL
    }
    /** Sets the size of the background image */
    TransitionTimingDSL backgroundSize (durationVal) {
        property = 'backgroundSize'
        duration = durationVal
        timingDSL
    }
    /** Sets whether or not an element should be visible when not facing the screen */
    TransitionTimingDSL backfaceVisibility (durationVal) {
        property = 'backfaceVisibility'
        duration = durationVal
        timingDSL
    }
    /** Sets borderWidth, borderStyle, and borderColor in one declaration */
    TransitionTimingDSL border (durationVal) {
        property = 'border'
        duration = durationVal
        timingDSL
    }
    /** Sets all the borderBottom* properties in one declaration */
    TransitionTimingDSL borderBottom (durationVal) {
        property = 'borderBottom'
        duration = durationVal
        timingDSL
    }
    /** Sets the color of the bottom border */
    TransitionTimingDSL borderBottomColor (durationVal) {
        property = 'borderBottomColor'
        duration = durationVal
        timingDSL
    }
    /** Sets the shape of the border of the bottom-left corner */
    TransitionTimingDSL borderBottomLeftRadius (durationVal) {
        property = 'borderBottomLeftRadius'
        duration = durationVal
        timingDSL
    }
    /** Sets the shape of the border of the bottom-right corner */
    TransitionTimingDSL borderBottomRightRadius (durationVal) {
        property = 'borderBottomRightRadius'
        duration = durationVal
        timingDSL
    }
    /** Sets the style of the bottom border */
    TransitionTimingDSL borderBottomStyle (durationVal) {
        property = 'borderBottomStyle'
        duration = durationVal
        timingDSL
    }
    /** Sets the width of the bottom border */
    TransitionTimingDSL borderBottomWidth (durationVal) {
        property = 'borderBottomWidth'
        duration = durationVal
        timingDSL
    }
    /** Sets whether the table border should be collapsed into a single border, or not */
    TransitionTimingDSL borderCollapse (durationVal) {
        property = 'borderCollapse'
        duration = durationVal
        timingDSL
    }
    /** Sets the color of an element's border (can have up to four durationVals) */
    TransitionTimingDSL borderColor (durationVal) {
        property = 'borderColor'
        duration = durationVal
        timingDSL
    }
    /** A shorthand property for setting or returning all the borderImage* properties */
    TransitionTimingDSL borderImage (durationVal) {
        property = 'borderImage'
        duration = durationVal
        timingDSL
    }
    /** Sets the amount by which the border image area extends beyond the border box */
    TransitionTimingDSL borderImageOutset (durationVal) {
        property = 'borderImageOutset'
        duration = durationVal
        timingDSL
    }
    /** Sets whether the image-border should be repeated, rounded or stretched */
    TransitionTimingDSL borderImageRepeat (durationVal) {
        property = 'borderImageRepeat'
        duration = durationVal
        timingDSL
    }
    /** Sets the inward offsets of the image-border */
    TransitionTimingDSL borderImageSlice (durationVal) {
        property = 'borderImageSlice'
        duration = durationVal
        timingDSL
    }
    /** Sets the image to be used as a border */
    TransitionTimingDSL borderImageSource (durationVal) {
        property = 'borderImageSource'
        duration = durationVal
        timingDSL
    }
    /** Sets the widths of the image-border */
    TransitionTimingDSL borderImageWidth (durationVal) {
        property = 'borderImageWidth'
        duration = durationVal
        timingDSL
    }
    /** Sets all the borderLeft* properties in one declaration */
    TransitionTimingDSL borderLeft (durationVal) {
        property = 'borderLeft'
        duration = durationVal
        timingDSL
    }
    /** Sets the color of the left border */
    TransitionTimingDSL borderLeftColor (durationVal) {
        property = 'borderLeftColor'
        duration = durationVal
        timingDSL
    }
    /** Sets the style of the left border */
    TransitionTimingDSL borderLeftStyle (durationVal) {
        property = 'borderLeftStyle'
        duration = durationVal
        timingDSL
    }
    /** Sets the width of the left border */
    TransitionTimingDSL borderLeftWidth (durationVal) {
        property = 'borderLeftWidth'
        duration = durationVal
        timingDSL
    }
    /** A shorthand property for setting or returning all the four border*Radius properties */
    TransitionTimingDSL borderRadius (durationVal) {
        property = 'borderRadius'
        duration = durationVal
        timingDSL
    }
    /** Sets all the borderRight* properties in one declaration */
    TransitionTimingDSL borderRight (durationVal) {
        property = 'borderRight'
        duration = durationVal
        timingDSL
    }
    /** Sets the color of the right border */
    TransitionTimingDSL borderRightColor (durationVal) {
        property = 'borderRightColor'
        duration = durationVal
        timingDSL
    }
    /** Sets the style of the right border */
    TransitionTimingDSL borderRightStyle (durationVal) {
        property = 'borderRightStyle'
        duration = durationVal
        timingDSL
    }
    /** Sets the width of the right border */
    TransitionTimingDSL borderRightWidth (durationVal) {
        property = 'borderRightWidth'
        duration = durationVal
        timingDSL
    }
    /** Sets the space between cells in a table */
    TransitionTimingDSL borderSpacing (durationVal) {
        property = 'borderSpacing'
        duration = durationVal
        timingDSL
    }
    /** Sets the style of an element's border (can have up to four durationVals) */
    TransitionTimingDSL borderStyle (durationVal) {
        property = 'borderStyle'
        duration = durationVal
        timingDSL
    }
    /** Sets all the borderTop* properties in one declaration */
    TransitionTimingDSL borderTop (durationVal) {
        property = 'borderTop'
        duration = durationVal
        timingDSL
    }
    /** Sets the color of the top border */
    TransitionTimingDSL borderTopColor (durationVal) {
        property = 'borderTopColor'
        duration = durationVal
        timingDSL
    }
    /** Sets the shape of the border of the top-left corner */
    TransitionTimingDSL borderTopLeftRadius (durationVal) {
        property = 'borderTopLeftRadius'
        duration = durationVal
        timingDSL
    }
    /** Sets the shape of the border of the top-right corner */
    TransitionTimingDSL borderTopRightRadius (durationVal) {
        property = 'borderTopRightRadius'
        duration = durationVal
        timingDSL
    }
    /** Sets the style of the top border */
    TransitionTimingDSL borderTopStyle (durationVal) {
        property = 'borderTopStyle'
        duration = durationVal
        timingDSL
    }
    /** Sets the width of the top border */
    TransitionTimingDSL borderTopWidth (durationVal) {
        property = 'borderTopWidth'
        duration = durationVal
        timingDSL
    }
    /** Sets the width of an element's border (can have up to four durationVals) */
    TransitionTimingDSL borderWidth (durationVal) {
        property = 'borderWidth'
        duration = durationVal
        timingDSL
    }
    /** Sets the bottom position of a positioned element */
    TransitionTimingDSL bottom (durationVal) {
        property = 'bottom'
        duration = durationVal
        timingDSL
    }
    /** Sets the behaviour of the background and border of an element at page-break, or, for in-line elements, at line-break. */
    TransitionTimingDSL boxDecorationBreak (durationVal) {
        property = 'boxDecorationBreak'
        duration = durationVal
        timingDSL
    }
    /** Attaches one or more drop-shadows to the box */
    TransitionTimingDSL boxShadow (durationVal) {
        property = 'boxShadow'
        duration = durationVal
        timingDSL
    }

    /** Sets which part of a positioned element is visible */
    TransitionTimingDSL clip (durationVal) {
        property = 'clip'
        duration = durationVal
        timingDSL
    }
    /** Sets the color of the text */
    TransitionTimingDSL color (durationVal) {
        property = 'color'
        duration = durationVal
        timingDSL
    }
    /** Sets the number of columns an element should be divided into */
    TransitionTimingDSL columnCount (durationVal) {
        property = 'columnCount'
        duration = durationVal
        timingDSL
    }
    /** Sets how to fill columns */
    TransitionTimingDSL columnFill (durationVal) {
        property = 'columnFill'
        duration = durationVal
        timingDSL
    }
    /** Sets the gap between the columns */
    TransitionTimingDSL columnGap (durationVal) {
        property = 'columnGap'
        duration = durationVal
        timingDSL
    }
    /** A shorthand property for setting or returning all the columnRule* properties */
    TransitionTimingDSL columnRule (durationVal) {
        property = 'columnRule'
        duration = durationVal
        timingDSL
    }
    /** Sets the color of the rule between columns */
    TransitionTimingDSL columnRuleColor (durationVal) {
        property = 'columnRuleColor'
        duration = durationVal
        timingDSL
    }
    /** Sets the style of the rule between columns */
    TransitionTimingDSL columnRuleStyle (durationVal) {
        property = 'columnRuleStyle'
        duration = durationVal
        timingDSL
    }
    /** Sets the width of the rule between columns */
    TransitionTimingDSL columnRuleWidth (durationVal) {
        property = 'columnRuleWidth'
        duration = durationVal
        timingDSL
    }
    /** A shorthand property for setting or returning columnWidth and columnCount */
    TransitionTimingDSL columns (durationVal) {
        property = 'columns'
        duration = durationVal
        timingDSL
    }
    /** Sets how many columns an element should span across */
    TransitionTimingDSL columnSpan (durationVal) {
        property = 'columnSpan'
        duration = durationVal
        timingDSL
    }
    /** Sets the width of the columns */
    TransitionTimingDSL columnWidth (durationVal) {
        property = 'columnWidth'
        duration = durationVal
        timingDSL
    }
    /** Used with the :before and :after pseudo-elements, to insert generated content */
    TransitionTimingDSL content (durationVal) {
        property = 'content'
        duration = durationVal
        timingDSL
    }

    /** Sets the type of cursor to display for the mouse pointer */
    TransitionTimingDSL cursor (durationVal) {
        property = 'cursor'
        duration = durationVal
        timingDSL
    }

    //TODO filter,flex,flexBasis,flexGrow,flexShrink

    /** Sets fontStyle, fontVariant, fontWeight, fontSize, lineHeight, and fontFamily in one declaration */
    TransitionTimingDSL font (durationVal) {
        property = 'font'
        duration = durationVal
        timingDSL
    }

    /** Sets the font size of the text */
    TransitionTimingDSL fontSize (durationVal) {
        property = 'fontSize'
        duration = durationVal
        timingDSL
    }
    /** Sets whether the style of the font is normal, italic or oblique */
    TransitionTimingDSL fontStyle (durationVal) {
        property = 'fontStyle'
        duration = durationVal
        timingDSL
    }
    /** Sets whether the font should be displayed in small capital letters */
    TransitionTimingDSL fontVariant (durationVal) {
        property = 'fontVariant'
        duration = durationVal
        timingDSL
    }
    /** Sets the boldness of the font */
    TransitionTimingDSL fontWeight (durationVal) {
        property = 'fontWeight'
        duration = durationVal
        timingDSL
    }
    /** Preserves the readability of text when font fallback occurs */
    TransitionTimingDSL fontSizeAdjust (durationVal) {
        property = 'fontSizeAdjust'
        duration = durationVal
        timingDSL
    }
    /** Selects a normal, condensed, or expanded face from a font family */
    TransitionTimingDSL fontStretch (durationVal) {
        property = 'fontStretch'
        duration = durationVal
        timingDSL
    }

    /** Sets the height of an element */
    TransitionTimingDSL height (durationVal) {
        property = 'height'
        duration = durationVal
        timingDSL
    }
    /** Sets how to split words to improve the layout of paragraphs */
    TransitionTimingDSL hyphens (durationVal) {
        property = 'hyphens'
        duration = durationVal
        timingDSL
    }
    /** Provides the author the ability to style an element with an iconic equivalent */
    TransitionTimingDSL icon (durationVal) {
        property = 'icon'
        duration = durationVal
        timingDSL
    }
    /** Specifies a rotation in the right or clockwise direction that a user agent applies to an image */
    TransitionTimingDSL imageOrientation (durationVal) {
        property = 'imageOrientation'
        duration = durationVal
        timingDSL
    }
    /** Sets the alignment between the items inside a flexible container when the items do not use all available space. */
    TransitionTimingDSL justifyContent (durationVal) {
        property = 'justifyContent'
        duration = durationVal
        timingDSL
    }
    /** Sets the left position of a positioned element */
    TransitionTimingDSL left (durationVal) {
        property = 'left'
        duration = durationVal
        timingDSL
    }
    /** Sets the space between characters in a text */
    TransitionTimingDSL letterSpacing (durationVal) {
        property = 'letterSpacing'
        duration = durationVal
        timingDSL
    }
    /** Sets the distance between lines in a text */
    TransitionTimingDSL lineHeight (durationVal) {
        property = 'lineHeight'
        duration = durationVal
        timingDSL
    }
    /** Sets listStyleImage, listStylePosition, and listStyleType in one declaration */
    TransitionTimingDSL listStyle (durationVal) {
        property = 'listStyle'
        duration = durationVal
        timingDSL
    }
    /** Sets an image as the list-item marker */
    TransitionTimingDSL listStyleImage (durationVal) {
        property = 'listStyleImage'
        duration = durationVal
        timingDSL
    }
    /** Sets the position of the list-item marker */
    TransitionTimingDSL listStylePosition (durationVal) {
        property = 'listStylePosition'
        duration = durationVal
        timingDSL
    }
    /** Sets the list-item marker type */
    TransitionTimingDSL listStyleType (durationVal) {
        property = 'listStyleType'
        duration = durationVal
        timingDSL
    }
    /** Sets the margins of an element (can have up to four durationVals) */
    TransitionTimingDSL margin (durationVal) {
        property = 'margin'
        duration = durationVal
        timingDSL
    }
    /** Sets the bottom margin of an element */
    TransitionTimingDSL marginBottom (durationVal) {
        property = 'marginBottom'
        duration = durationVal
        timingDSL
    }
    /** Sets the left margin of an element */
    TransitionTimingDSL marginLeft (durationVal) {
        property = 'marginLeft'
        duration = durationVal
        timingDSL
    }
    /** Sets the right margin of an element */
    TransitionTimingDSL marginRight (durationVal) {
        property = 'marginRight'
        duration = durationVal
        timingDSL
    }
    /** Sets the top margin of an element */
    TransitionTimingDSL marginTop (durationVal) {
        property = 'marginTop'
        duration = durationVal
        timingDSL
    }
    /** Sets the maximum height of an element */
    TransitionTimingDSL maxHeight (durationVal) {
        property = 'maxHeight'
        duration = durationVal
        timingDSL
    }
    /** Sets the maximum width of an element */
    TransitionTimingDSL maxWidth (durationVal) {
        property = 'maxWidth'
        duration = durationVal
        timingDSL
    }
    /** Sets the minimum height of an element */
    TransitionTimingDSL minHeight (durationVal) {
        property = 'minHeight'
        duration = durationVal
        timingDSL
    }
    /** Sets the minimum width of an element */
    TransitionTimingDSL minWidth (durationVal) {
        property = 'minWidth'
        duration = durationVal
        timingDSL
    }

    /** Sets the opacity level for an element */
    TransitionTimingDSL opacity (durationVal) {
        property = 'opacity'
        duration = durationVal
        timingDSL
    }
    /** Sets the order of the flexible item, relative to the rest */
    TransitionTimingDSL order (durationVal) {
        property = 'order'
        duration = durationVal
        timingDSL
    }

    /** Sets all the outline properties in one declaration */
    TransitionTimingDSL outline (durationVal) {
        property = 'outline'
        duration = durationVal
        timingDSL
    }
    /** Sets the color of the outline around a element */
    TransitionTimingDSL outlineColor (durationVal) {
        property = 'outlineColor'
        duration = durationVal
        timingDSL
    }
    /** Offsets an outline, and draws it beyond the border edge */
    TransitionTimingDSL outlineOffset (durationVal) {
        property = 'outlineOffset'
        duration = durationVal
        timingDSL
    }
    /** Sets the style of the outline around an element */
    TransitionTimingDSL outlineStyle (durationVal) {
        property = 'outlineStyle'
        duration = durationVal
        timingDSL
    }
    /** Sets the width of the outline around an element */
    TransitionTimingDSL outlineWidth (durationVal) {
        property = 'outlineWidth'
        duration = durationVal
        timingDSL
    }
    /** Sets what to do with content that renders outside the element box */
    TransitionTimingDSL overflow (durationVal) {
        property = 'overflow'
        duration = durationVal
        timingDSL
    }
    /** Specifies what to do with the left/right edges of the content, if it overflows the element's content area */
    TransitionTimingDSL overflowX (durationVal) {
        property = 'overflowX'
        duration = durationVal
        timingDSL
    }
    /** Specifies what to do with the top/bottom edges of the content, if it overflows the element's content area */
    TransitionTimingDSL overflowY (durationVal) {
        property = 'overflowY'
        duration = durationVal
        timingDSL
    }
    /** Sets the padding of an element (can have up to four durationVals) */
    TransitionTimingDSL padding (durationVal) {
        property = 'padding'
        duration = durationVal
        timingDSL
    }
    /** Sets the bottom padding of an element */
    TransitionTimingDSL paddingBottom (durationVal) {
        property = 'paddingBottom'
        duration = durationVal
        timingDSL
    }
    /** Sets the left padding of an element */
    TransitionTimingDSL paddingLeft (durationVal) {
        property = 'paddingLeft'
        duration = durationVal
        timingDSL
    }
    /** Sets the right padding of an element */
    TransitionTimingDSL paddingRight (durationVal) {
        property = 'paddingRight'
        duration = durationVal
        timingDSL
    }
    /** Sets the top padding of an element */
    TransitionTimingDSL paddingTop (durationVal) {
        property = 'paddingTop'
        duration = durationVal
        timingDSL
    }

    /** Sets the perspective on how 3D elements are viewed */
    TransitionTimingDSL perspective (durationVal) {
        property = 'perspective'
        duration = durationVal
        timingDSL
    }
    /** Sets the bottom position of 3D elements */
    TransitionTimingDSL perspectiveOrigin (durationVal) {
        property = 'perspectiveOrigin'
        duration = durationVal
        timingDSL
    }
    /** Sets the type of positioning method used for an element (static, relative, absolute or fixed) */
    TransitionTimingDSL position (durationVal) {
        property = 'position'
        duration = durationVal
        timingDSL
    }

    /** Sets the right position of a positioned element */
    TransitionTimingDSL right (durationVal) {
        property = 'right'
        duration = durationVal
        timingDSL
    }
    /** Sets the way to lay out table cells, rows, and columns */
    TransitionTimingDSL tableLayout (durationVal) {
        property = 'tableLayout'
        duration = durationVal
        timingDSL
    }
    /** Sets the length of the tab-character */
    TransitionTimingDSL tabSize (durationVal) {
        property = 'tabSize'
        duration = durationVal
        timingDSL
    }
    /** Sets the horizontal alignment of text */
    TransitionTimingDSL textAlign (durationVal) {
        property = 'textAlign'
        duration = durationVal
        timingDSL
    }
    /** Sets how the last line of a block or a line right before a forced line break is aligned when text-align is ""justify"" */
    TransitionTimingDSL textAlignLast (durationVal) {
        property = 'textAlignLast'
        duration = durationVal
        timingDSL
    }
    /** Sets the decoration of a text */
    TransitionTimingDSL textDecoration (durationVal) {
        property = 'textDecoration'
        duration = durationVal
        timingDSL
    }
    /** Sets the color of the text-decoration */
    TransitionTimingDSL textDecorationColor (durationVal) {
        property = 'textDecorationColor'
        duration = durationVal
        timingDSL
    }
    /** Sets the type of line in a text-decoration */
    TransitionTimingDSL textDecorationLine (durationVal) {
        property = 'textDecorationLine'
        duration = durationVal
        timingDSL
    }
    /** Sets the style of the line in a text decoration */
    TransitionTimingDSL textDecorationStyle (durationVal) {
        property = 'textDecorationStyle'
        duration = durationVal
        timingDSL
    }
    /** Sets the indentation of the first line of text */
    TransitionTimingDSL textIndent (durationVal) {
        property = 'textIndent'
        duration = durationVal
        timingDSL
    }
    /** Sets the justification method used when text-align is ""justify"" */
    TransitionTimingDSL textJustify (durationVal) {
        property = 'textJustify'
        duration = durationVal
        timingDSL
    }
    /** Sets what should happen when text overflows the containing element */
    TransitionTimingDSL textOverflow (durationVal) {
        property = 'textOverflow'
        duration = durationVal
        timingDSL
    }
    /** Sets the shadow effect of a text */
    TransitionTimingDSL textShadow (durationVal) {
        property = 'textShadow'
        duration = durationVal
        timingDSL
    }
    /** Sets the capitalization of a text */
    TransitionTimingDSL textTransform (durationVal) {
        property = 'textTransform'
        duration = durationVal
        timingDSL
    }
    /** Sets the top position of a positioned element */
    TransitionTimingDSL top (durationVal) {
        property = 'top'
        duration = durationVal
        timingDSL
    }
    
    /** Applies a 2D or 3D transformation to an element */
    TransitionTimingDSL transform (durationVal) {
        property = 'transform'
        duration = durationVal
        timingDSL
    }
    /** Sets the position of transformed elements */
    TransitionTimingDSL transformOrigin (durationVal) {
        property = 'transformOrigin'
        duration = durationVal
        timingDSL
    }
    /** Sets how nested elements are rendered in 3D space */
    TransitionTimingDSL transformStyle (durationVal) {
        property = 'transformStyle'
        duration = durationVal
        timingDSL
    }
    
    /** Sets the vertical alignment of the content in an element */
    TransitionTimingDSL verticalAlign (durationVal) {
        property = 'verticalAlign'
        duration = durationVal
        timingDSL
    }
    /** Sets whether an element should be visible */
    TransitionTimingDSL visibility (durationVal) {
        property = 'visibility'
        duration = durationVal
        timingDSL
    }
    /** Sets how to handle tabs, line breaks and whitespace in a text */
    TransitionTimingDSL whiteSpace (durationVal) {
        property = 'whiteSpace'
        duration = durationVal
        timingDSL
    }
    /** Sets the width of an element */
    TransitionTimingDSL width (durationVal) {
        property = 'width'
        duration = durationVal
        timingDSL
    }
    /** Sets line breaking rules for non-CJK scripts */
    TransitionTimingDSL wordBreak (durationVal) {
        property = 'wordBreak'
        duration = durationVal
        timingDSL
    }
    /** Sets the spacing between words in a text */
    TransitionTimingDSL wordSpacing (durationVal) {
        property = 'wordSpacing'
        duration = durationVal
        timingDSL
    }
    /** Allows long, unbreakable words to be broken and wrap to the next line */
    TransitionTimingDSL wordWrap (durationVal) {
        property = 'wordWrap'
        duration = durationVal
        timingDSL
    }
    /** Sets the minimum number of lines for an element that must be visible at the top of a page */
    TransitionTimingDSL widows (durationVal) {
        property = 'widows'
        duration = durationVal
        timingDSL
    }
    /** Sets the stack order of a positioned element */
    TransitionTimingDSL zIndex (durationVal) {
        property = 'zIndex'
        duration = durationVal
        timingDSL
    }

}
