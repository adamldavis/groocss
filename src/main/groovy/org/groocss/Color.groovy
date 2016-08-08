package org.groocss

import java.math.MathContext

/**
 * Controls Color for CSS styles and has methods for brighter, darker, etc.
 */
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
            "rgba(${color.red}, ${color.green}, ${color.blue}, $alpha)"
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
        new Color(r, g, b, alpha)
    }
}
