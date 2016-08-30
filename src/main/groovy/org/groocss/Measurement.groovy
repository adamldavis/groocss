package org.groocss

import groovy.transform.TypeChecked

/**
 * Represents some type of number value with a unit, such as 2 seconds or 20 pixels.
 */
@TypeChecked
class Measurement {

    def times = ['ms', 's']
    def distances = ['pt','pc','mm','cm','in','m']
    def trigs = ['rad','deg']
    def relatives = ['em','ex','ch','rem','vh','vw','vmin','vmax']

    final Number value
    final String unit

    Measurement(Number value, String unit) {
        this.value = value
        this.unit = unit
    }

    String toString() { "$value$unit" }

    /** Is a time measurement (s, ms). */
    boolean isTime() { times.any {it==unit} }

    /** Is a distance measurement (pt,pc,in,mm,cm,m). */
    boolean isDistance() { distances.any {it==unit} }

    /** Trigonometric. */
    boolean isTrig() { trigs.any {it==unit} }

    /** Unit is pixel. */
    boolean isPixel() { 'px' == unit }

    /** Is a percentage. */
    boolean isPercent() { '%' == unit }

    /** Relative length units. */
    boolean isRelative() { relatives.any {it == unit} }

    boolean isZero() { this.equals(0) }

    @Override
    boolean equals(other) {
        if (other instanceof Measurement) {
            if (unit == other.unit)
                equals other.value
            else if ((time && other.time) || (distance && other.distance) || (trig && other.trig))
                equals convertValue(other)
            else
                false
        } else if (other instanceof BigDecimal) {
            other.compareTo(value as BigDecimal) == 0
        } else if (other instanceof Number) {
            other == value
        } else
            false
    }

    Measurement plus(Measurement other) {
        new Measurement(value + convertValue(other), unit)
    }

    Measurement minus(Measurement other) {
        new Measurement(value - convertValue(other), unit)
    }

    Measurement multiply(Number other) {
        new Measurement(value * other, unit)
    }

    Measurement mod(Number other) {
        new Measurement(value % other, unit)
    }

    Number div(Measurement other) {
        value / convertValue(other)
    }

    Measurement div(Number number) {
        new Measurement(value / number, unit)
    }

    Number convertValue(Measurement other) {
        if (unit == other.unit) other.value
        else convertNum(other.value, "${other.unit}-$unit")
    }

    static Number convertNum(Number num, String conversion) {
        def split = conversion.split('-')
        if (split.length == 2 && split[0] == split[1]) {
            return num
        }
        switch (conversion) {
            case 'ms-s': return num / 1000 as BigDecimal
            case 's-ms': return num * 1000 as Integer
            case 'rad-deg': return toDegrees(num) as Double
            case 'deg-rad': return toRadians(num) as Double
            case 'mm-cm': return num / 10 as BigDecimal
            case 'mm-m': return num / 1000 as BigDecimal
            case 'cm-m': return num / 100 as BigDecimal
            case 'cm-mm': return num * 10 as Integer
            case 'm-mm': return num * 1000 as Integer
            case 'm-cm': return num * 100 as Integer
            case 'in-m': return 0.0254 * num
            case 'in-cm': return 2.54 * num
            case 'in-mm': return 25.4 * num
            case 'm-in': return num / 0.0254
            case 'cm-in': return num / 2.54
            case 'mm-in': return num / 25.4
            case 'pt-in': return num / 72.0
            case 'pc-in': return num * 12 / 72.0
            case 'pt-pc': return num / 12.0
            case 'pc-pt': return num * 12 as Integer
            case 'in-pt': return num * 72 as Integer
            case 'in-pc': return num * 6 as Integer
            case 'pt-m': return convertNum(convertNum(num, 'pt-in'), 'in-m')
            case 'pc-m': return convertNum(convertNum(num, 'pc-in'), 'in-m')
            case 'pt-cm': return convertNum(convertNum(num, 'pt-in'), 'in-cm')
            case 'pc-cm': return convertNum(convertNum(num, 'pc-in'), 'in-cm')
            case 'pt-mm': return convertNum(convertNum(num, 'pt-in'), 'in-mm')
            case 'pc-mm': return convertNum(convertNum(num, 'pc-in'), 'in-mm')
            case 'mm-pt': return convertNum(convertNum(num, 'mm-in'), 'in-pt')
            case 'mm-pc': return convertNum(convertNum(num, 'mm-in'), 'in-pc')
            case 'cm-pt': return convertNum(convertNum(num, 'cm-in'), 'in-pt')
            case 'cm-pc': return convertNum(convertNum(num, 'cm-in'), 'in-pc')
            case 'm-pt': return convertNum(convertNum(num, 'm-in'), 'in-pt')
            case 'm-pc': return convertNum(convertNum(num, 'm-in'), 'in-pc')
            default: throw new IllegalArgumentException("Unknown conversion: $conversion")
        }
    }

    /** Converts an angle measured in radians to an approximately equivalent angle measured in degrees.*/
    static double toDegrees(Number angrad) { Math.toDegrees(angrad.doubleValue()) }

    /**Converts an angle measured in degrees to an approximately equivalent angle measured in radians.*/
    static double toRadians(Number angdeg) { Math.toRadians(angdeg.doubleValue()) }

}
