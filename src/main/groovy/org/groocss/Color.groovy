package org.groocss

import groovy.transform.EqualsAndHashCode

import java.math.MathContext

/**
 * Controls Color for CSS styles and has methods for brighter, darker, etc.
 */
@EqualsAndHashCode
class Color {

    Color(String name, String colorStr) {
        this.name = name
        setColor(colorStr)
    }
    Color(String colorStr) {
        setColor(colorStr)
    }
    Color(java.awt.Color color1) {
        color = color1
    }
    Color(int r, int g, int b) {
        color = new java.awt.Color(r, g, b)
    }
    Color(int r, int g, int b, double a) {
        color = new java.awt.Color(r, g, b, (int) (a * 255))
    }
    Color(int rgb) {
        color = new java.awt.Color(rgb)
    }
    Color(int hue, double s, double b) {
        color = java.awt.Color.getHSBColor((float) (hue / 360.0f), (float) s, (float) b)
    }

    /** Actual color. */
    java.awt.Color color
    /** Optional Color name. */
    String name

    void setColor(String colorStr) {
        if (colorStr.length() == 6 || colorStr.length() == 3)
            setColor('#' + colorStr)
        else if (colorStr.length() == 7)
            color = new java.awt.Color(
                    Integer.valueOf(colorStr[1..2], 16),
                    Integer.valueOf(colorStr[3..4], 16),
                    Integer.valueOf(colorStr[5..6], 16) );
        else if (colorStr.length() == 4)
            color = new java.awt.Color(
                    Integer.valueOf(colorStr[1]*2, 16),
                    Integer.valueOf(colorStr[2]*2, 16),
                    Integer.valueOf(colorStr[3]*2, 16) );
        else
            throw new IllegalArgumentException("Illegal color format $colorStr")
    }

    String toHex() { toString() }

    String toString() {
        if (name) name
        else if (color.alpha < 255) {
            def alpha = (color.alpha / 255.0).round(new MathContext(2))
            "rgba(${color.red}, ${color.green}, ${color.blue}, ${GrooCSS.stringify alpha})"
        } else
            '#' + hex2(color.red) + hex2(color.green) + hex2(color.blue)
    }
    private String hex2(int n) {
        String str = Integer.toHexString(n)
        str.length() == 2  ? str : ('0' + str)
    }

    /** A new color with brighter lumosity. */
    Color brighter() {
        new Color(color.brighter())
    }

    /** A new color with darker lumosity. */
    Color darker() {
        new Color(color.darker())
    }

    /** Returns a copy of this color but with given 0-1 alpha value. */
    Color alpha(double alpha) {
        int r = color.red, g = color.green, b = color.blue
        new Color(r, g, b, Math.max( 0d, Math.min(alpha, 1.0d) ))
    }

    Color saturate(float percent) {
        int alpha = color.alpha
        float[] hsb = java.awt.Color.RGBtoHSB(color.red, color.green, color.blue, null)

        new Color((int) (360 * hsb[0]), Math.min(1.0f, hsb[1] + percent), hsb[2]).alpha(alpha / 255d)
    }

    Color desaturate(float percent) {
        int alpha = color.alpha
        float[] hsb = java.awt.Color.RGBtoHSB(color.red, color.green, color.blue, null)

        new Color((int) (360 * hsb[0]), Math.max(0f, hsb[1] - percent), hsb[2]).alpha(alpha / 255d)
    }

    /** Gets Hue component of color as a number 0-360. */
    int getHue() { (int) (java.awt.Color.RGBtoHSB(color.red, color.green, color.blue, null)[0] * 360) }

    float getSaturation() { java.awt.Color.RGBtoHSB(color.red, color.green, color.blue, null)[1] }

    float getBrightness() { java.awt.Color.RGBtoHSB(color.red, color.green, color.blue, null)[2] }

    /** Red channel 0-255. */
    int getRed() {color.red}
    /** Green channel 0-255. */
    int getGreen() {color.green}
    /** Blue channel 0-255. */
    int getBlue() {color.blue}

    /** Alpha of this color as a number between 0 and 1, inclusive. */
    double getAlpha() { color.alpha / 255.0d }

    /** Returns a copy of this color mixed with other Color with optional 0-1 weight value. */
    Color mix(Color color2, double w = 0.5) {
        int r1 = color.red, g1 = color.green, b1 = color.blue
        int r2 = color2.red, g2 = color2.green, b2 = color2.blue

        new Color(mix2(r1, r2, w), mix2(g1, g2, w), mix2(b1, b2, w), mixd(alpha, color2.alpha, w))
    }

    private int mix2(int n1, int n2, double w) {            (int)    ((n1 * (1d - w)) + (n2 * w)) }
    private double mixd(double n1, double n2, double w) {   (double) ((n1 * (1d - w)) + (n2 * w)) }

}
