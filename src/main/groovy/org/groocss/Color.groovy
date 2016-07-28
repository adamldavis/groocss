package org.groocss

/**
 * Created by adavis on 7/28/16.
 */
class Color {

    Color(String colorStr) {
        setColor(colorStr)
    }
    Color(java.awt.Color color1) {
        color = color1
    }
    Color(int r, int g, int b) {
        color = new java.awt.Color(r, g, b)
    }
    Color(int r, int g, int b, int a) {
        color = new java.awt.Color(r, g, b, a)
    }

    java.awt.Color color

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
        '#' + hex2(color.red) + hex2(color.green) + hex2(color.blue)
    }
    private String hex2(int n) {
        String str = Integer.toHexString(n)
        str.length() == 2  ? str : ('0' + str)
    }

    Color brighter() {
        new Color(color.brighter())
    }

    Color darker() {
        new Color(color.darker())
    }

    Color alpha(double alpha) {
        int r = color.red, g = color.green, b = color.blue, a = 256*alpha;
        new Color(r, g, b, a)
    }
}
