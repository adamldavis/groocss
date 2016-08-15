package org.groocss

import org.codehaus.groovy.control.CompilerConfiguration

import groovy.transform.*

/**
 * Entrance to DSL for converting code into CSS.
 */
@CompileStatic
class GrooCSS extends Script {


    static void convert(Config conf = new Config(), String inFilename, String outFilename) {
        convert(conf, new File(inFilename), new File(outFilename))
    }
    
    static void convert(Config conf = new Config(), File inf, File out) {
        def binding = new Binding()
        binding.setProperty('_config', conf)
        binding.setProperty('root', null)
        def compilerConfig = new CompilerConfiguration()
        compilerConfig.scriptBaseClass = 'org.groocss.GrooCSS'

        def shell = new GroovyShell(this.class.classLoader, binding, compilerConfig)

        shell.evaluate("config = css.config = _config;\nroot = css;\n${inf.text}")

        MediaCSS css = (MediaCSS) binding.getProperty('root')

        out.withPrintWriter { pw -> css.writeTo(pw) }
    }
    
    static void main(String ... args) {
        if (args.length == 1)
            convert(args[0], args[0].replace('.groocss', '.css'))
    }

    static class Configurer extends Config {

        Configurer convert(File inf, File out) {
            GrooCSS.convert(this, inf, out)
            this
        }

        Configurer convert(String inFilename, String outFilename) {
            GrooCSS.convert(this, inFilename, outFilename)
            this
        }

        /** Processes the given closure with built config. */
        GrooCSS process(@DelegatesTo(GrooCSS) Closure clos) { GrooCSS.runBlock(this, clos) }

        /** Processes the given closure with built config. */
        GrooCSS runBlock(@DelegatesTo(GrooCSS) Closure clos) { GrooCSS.runBlock(this, clos) }
    }

    static Configurer withConfig(@DelegatesTo(Configurer) Closure<Configurer> closure) {
        Configurer c = new Configurer()
        closure.delegate = c
        closure(c)
        c
    }

    /** Main MediaCSS root.*/
    Config config = new Config()
    MediaCSS css = new MediaCSS(config: config)
    MediaCSS currentCss = css

    public String toString() { css.toString() }

    MediaCSS media(String mediaRule, @DelegatesTo(GrooCSS) Closure clos) {
        MediaCSS mcss = new MediaCSS(mediaRule, config)
        MediaCSS oldCss = currentCss
        currentCss = mcss
        clos.delegate = this
        clos()
        oldCss.add mcss
        currentCss = oldCss
        mcss
    }

    /** Calls {@link #kf(java.lang.String, groovy.lang.Closure)}. */
    KeyFrames keyframes(String name, @DelegatesTo(KeyFrames) Closure clos) {
        kf(name, clos)
    }

    /** Creates a new KeyFrames element and runs given closure on it. */
    KeyFrames kf(String name, @DelegatesTo(KeyFrames) Closure clos) {
        KeyFrames frames = new KeyFrames(name: name, config: config)
        clos.delegate = frames
        clos()
        currentCss << frames
        frames
    }

    /** Creates a new StyleGroup element and runs given closure on it. */
    StyleGroup sel(String selector, @DelegatesTo(StyleGroup) Closure<StyleGroup> clos) {
        StyleGroup sg = new StyleGroup(selector: selector, config: config, owner: currentCss)
        clos.delegate = sg
        clos(sg)
        currentCss << sg
        sg
    }

    /** Creates a new @font-face element and runs given closure on it. */
    FontFace fontFace(@DelegatesTo(FontFace) Closure clos) {
        FontFace ff = new FontFace()
        clos.delegate = ff
        clos()
        currentCss.add ff
        ff
    }

    /** Creates a new StyleGroup element and runs given closure on it. */
    StyleGroup sg(String selector, @DelegatesTo(StyleGroup) Closure clos) {
        sel(selector, clos)
    }

    Style style(@DelegatesTo(Style) Closure clos) {
        Style s = new Style()
        clos.delegate = s
        clos()
        s
    }

    /** Creates a Style with given name and value. */
    Style style(String name, Object value) {
        new Style(name: name, value: "$value")
    }

    /**
     * Creates a new {@link org.groocss.Color} object.
     * @param colorStr e.g. "#123456"
     * @return A Color object.
     */
    Color c(String colorStr) {
        new Color(colorStr)
    }
    /** Creates a new {@link org.groocss.Color} object with a name. */
    Color c(String name, String colorStr) { new Color(name, colorStr) }

    /** Creates a new {@link org.groocss.Color} object. */
    Color clr(String colorStr) { c(colorStr) }

    /** Creates a new {@link org.groocss.Color} object from a Java Color. */
    Color c(java.awt.Color color) { new Color(color) }

    /** Creates a new {@link org.groocss.Color} object from a Java Color. */
    Color clr(java.awt.Color color) { c(color) }

    /** Creates a new {@link org.groocss.Color} object from red,green,blue (0-255) values. */
    Color rgb(int r, int g, int b) { new Color(r, g, b) }

    /** Creates a new {@link org.groocss.Color} object from red,green,blue,alpha (0-255) values. */
    Color rgba(int r, int g, int b, double a) { new Color(r, g, b, a) }

    def run() {}

    /** Processes the given closure with given optional config. */
    static GrooCSS process(Config config = new Config(), @DelegatesTo(GrooCSS) Closure clos) {
        runBlock(config, clos)
    }

    /** Processes the given closure with given optional config. */
    static GrooCSS runBlock(Config conf = new Config(), @DelegatesTo(GrooCSS) Closure clos) {
        GrooCSS gcss = new GrooCSS(config: conf)
        gcss.css.config = conf
        clos.delegate = gcss
        clos()
        gcss
    }

    /** Writes the CSS to the given file. */
    void writeTo(File f) {
        f.withPrintWriter { pw -> css.writeTo pw }
    }

    /** Writes the CSS to the given file. */
    void writeToFile(String filename) {
        writeTo(new File(filename))
    }

    void charset(String charset) {
        config.charset = charset
    }
    String getUtf8() { 'UTF-8' }
    String getUtf16() { 'UTF-16' }
    String getIso8859() { 'ISO-8859-1' }

    Color getAliceBlue() { c('AliceBlue', '#F0F8FF') }
    Color getAntiqueWhite() { c('AntiqueWhite', '#FAEBD7') }
    Color getAqua() { c('Aqua', '#00FFFF') }
    Color getAquamarine() { c('Aquamarine', '#7FFFD4') }
    Color getAzure() { c('Azure', '#F0FFFF') }
    Color getBeige() { c('Beige', '#F5F5DC') }
    Color getBisque() { c('Bisque', '#FFE4C4') }
    Color getBlack() { c('Black', '#000000') }
    Color getBlanchedAlmond() { c('BlanchedAlmond', '#FFEBCD') }
    Color getBlue() { c('Blue', '#0000FF') }
    Color getBlueViolet() { c('BlueViolet', '#8A2BE2') }
    Color getBrown() { c('Brown', '#A52A2A') }
    Color getBurlyWood() { c('BurlyWood', '#DEB887') }
    Color getCadetBlue() { c('CadetBlue', '#5F9EA0') }
    Color getChartreuse() { c('Chartreuse', '#7FFF00') }
    Color getChocolate() { c('Chocolate', '#D2691E') }
    Color getCoral() { c('Coral', '#FF7F50') }
    Color getCornflowerBlue() { c('CornflowerBlue', '#6495ED') }
    Color getCornsilk() { c('Cornsilk', '#FFF8DC') }
    Color getCrimson() { c('Crimson', '#DC143C') }
    Color getCyan() { c('Cyan', '#00FFFF') }
    Color getDarkBlue() { c('DarkBlue', '#00008B') }
    Color getDarkCyan() { c('DarkCyan', '#008B8B') }
    Color getDarkGoldenRod() { c('DarkGoldenRod', '#B8860B') }
    Color getDarkGray() { c('DarkGray', '#A9A9A9') }
    Color getDarkGrey() { c('DarkGrey', '#A9A9A9') }
    Color getDarkGreen() { c('DarkGreen', '#006400') }
    Color getDarkKhaki() { c('DarkKhaki', '#BDB76B') }
    Color getDarkMagenta() { c('DarkMagenta', '#8B008B') }
    Color getDarkOliveGreen() { c('DarkOliveGreen', '#556B2F') }
    Color getDarkorange() { c('Darkorange', '#FF8C00') }
    Color getDarkOrchid() { c('DarkOrchid', '#9932CC') }
    Color getDarkRed() { c('DarkRed', '#8B0000') }
    Color getDarkSalmon() { c('DarkSalmon', '#E9967A') }
    Color getDarkSeaGreen() { c('DarkSeaGreen', '#8FBC8F') }
    Color getDarkSlateBlue() { c('DarkSlateBlue', '#483D8B') }
    Color getDarkSlateGray() { c('DarkSlateGray', '#2F4F4F') }
    Color getDarkSlateGrey() { c('DarkSlateGrey', '#2F4F4F') }
    Color getDarkTurquoise() { c('DarkTurquoise', '#00CED1') }
    Color getDarkViolet() { c('DarkViolet', '#9400D3') }
    Color getDeepPink() { c('DeepPink', '#FF1493') }
    Color getDeepSkyBlue() { c('DeepSkyBlue', '#00BFFF') }
    Color getDimGray() { c('DimGray', '#696969') }
    Color getDimGrey() { c('DimGrey', '#696969') }
    Color getDodgerBlue() { c('DodgerBlue', '#1E90FF') }
    Color getFireBrick() { c('FireBrick', '#B22222') }
    Color getFloralWhite() { c('FloralWhite', '#FFFAF0') }
    Color getForestGreen() { c('ForestGreen', '#228B22') }
    Color getFuchsia() { c('Fuchsia', '#FF00FF') }
    Color getGainsboro() { c('Gainsboro', '#DCDCDC') }
    Color getGhostWhite() { c('GhostWhite', '#F8F8FF') }
    Color getGold() { c('Gold', '#FFD700') }
    Color getGoldenRod() { c('GoldenRod', '#DAA520') }
    Color getGray() { c('Gray', '#808080') }
    Color getGrey() { c('Grey', '#808080') }
    Color getGreen() { c('Green', '#008000') }
    Color getGreenYellow() { c('GreenYellow', '#ADFF2F') }
    Color getHoneyDew() { c('HoneyDew', '#F0FFF0') }
    Color getHotPink() { c('HotPink', '#FF69B4') }
    Color getIndianRed() { c('IndianRed', ' #CD5C5C') }
    Color getIndigo() { c('Indigo', ' #4B0082') }
    Color getIvory() { c('Ivory', '#FFFFF0') }
    Color getKhaki() { c('Khaki', '#F0E68C') }
    Color getLavender() { c('Lavender', '#E6E6FA') }
    Color getLavenderBlush() { c('LavenderBlush', '#FFF0F5') }
    Color getLawnGreen() { c('LawnGreen', '#7CFC00') }
    Color getLemonChiffon() { c('LemonChiffon', '#FFFACD') }
    Color getLightBlue() { c('LightBlue', '#ADD8E6') }
    Color getLightCoral() { c('LightCoral', '#F08080') }
    Color getLightCyan() { c('LightCyan', '#E0FFFF') }
    Color getLightGoldenRodYellow() { c('LightGoldenRodYellow', '#FAFAD2') }
    Color getLightGray() { c('LightGray', '#D3D3D3') }
    Color getLightGrey() { c('LightGrey', '#D3D3D3') }
    Color getLightGreen() { c('LightGreen', '#90EE90') }
    Color getLightPink() { c('LightPink', '#FFB6C1') }
    Color getLightSalmon() { c('LightSalmon', '#FFA07A') }
    Color getLightSeaGreen() { c('LightSeaGreen', '#20B2AA') }
    Color getLightSkyBlue() { c('LightSkyBlue', '#87CEFA') }
    Color getLightSlateGray() { c('LightSlateGray', '#778899') }
    Color getLightSlateGrey() { c('LightSlateGrey', '#778899') }
    Color getLightSteelBlue() { c('LightSteelBlue', '#B0C4DE') }
    Color getLightYellow() { c('LightYellow', '#FFFFE0') }
    Color getLime() { c('Lime', '#00FF00') }
    Color getLimeGreen() { c('LimeGreen', '#32CD32') }
    Color getLinen() { c('Linen', '#FAF0E6') }
    Color getMagenta() { c('Magenta', '#FF00FF') }
    Color getMaroon() { c('Maroon', '#800000') }
    Color getMediumAquaMarine() { c('MediumAquaMarine', '#66CDAA') }
    Color getMediumBlue() { c('MediumBlue', '#0000CD') }
    Color getMediumOrchid() { c('MediumOrchid', '#BA55D3') }
    Color getMediumPurple() { c('MediumPurple', '#9370D8') }
    Color getMediumSeaGreen() { c('MediumSeaGreen', '#3CB371') }
    Color getMediumSlateBlue() { c('MediumSlateBlue', '#7B68EE') }
    Color getMediumSpringGreen() { c('MediumSpringGreen', '#00FA9A') }
    Color getMediumTurquoise() { c('MediumTurquoise', '#48D1CC') }
    Color getMediumVioletRed() { c('MediumVioletRed', '#C71585') }
    Color getMidnightBlue() { c('MidnightBlue', '#191970') }
    Color getMintCream() { c('MintCream', '#F5FFFA') }
    Color getMistyRose() { c('MistyRose', '#FFE4E1') }
    Color getMoccasin() { c('Moccasin', '#FFE4B5') }
    Color getNavajoWhite() { c('NavajoWhite', '#FFDEAD') }
    Color getNavy() { c('Navy', '#000080') }
    Color getOldLace() { c('OldLace', '#FDF5E6') }
    Color getOlive() { c('Olive', '#808000') }
    Color getOliveDrab() { c('OliveDrab', '#6B8E23') }
    Color getOrange() { c('Orange', '#FFA500') }
    Color getOrangeRed() { c('OrangeRed', '#FF4500') }
    Color getOrchid() { c('Orchid', '#DA70D6') }
    Color getPaleGoldenRod() { c('PaleGoldenRod', '#EEE8AA') }
    Color getPaleGreen() { c('PaleGreen', '#98FB98') }
    Color getPaleTurquoise() { c('PaleTurquoise', '#AFEEEE') }
    Color getPaleVioletRed() { c('PaleVioletRed', '#D87093') }
    Color getPapayaWhip() { c('PapayaWhip', '#FFEFD5') }
    Color getPeachPuff() { c('PeachPuff', '#FFDAB9') }
    Color getPeru() { c('Peru', '#CD853F') }
    Color getPink() { c('Pink', '#FFC0CB') }
    Color getPlum() { c('Plum', '#DDA0DD') }
    Color getPowderBlue() { c('PowderBlue', '#B0E0E6') }
    Color getPurple() { c('Purple', '#800080') }
    Color getRed() { c('Red', '#FF0000') }
    Color getRosyBrown() { c('RosyBrown', '#BC8F8F') }
    Color getRoyalBlue() { c('RoyalBlue', '#4169E1') }
    Color getSaddleBrown() { c('SaddleBrown', '#8B4513') }
    Color getSalmon() { c('Salmon', '#FA8072') }
    Color getSandyBrown() { c('SandyBrown', '#F4A460') }
    Color getSeaGreen() { c('SeaGreen', '#2E8B57') }
    Color getSeaShell() { c('SeaShell', '#FFF5EE') }
    Color getSienna() { c('Sienna', '#A0522D') }
    Color getSilver() { c('Silver', '#C0C0C0') }
    Color getSkyBlue() { c('SkyBlue', '#87CEEB') }
    Color getSlateBlue() { c('SlateBlue', '#6A5ACD') }
    Color getSlateGray() { c('SlateGray', '#708090') }
    Color getSlateGrey() { c('SlateGrey', '#708090') }
    Color getSnow() { c('Snow', '#FFFAFA') }
    Color getSpringGreen() { c('SpringGreen', '#00FF7F') }
    Color getSteelBlue() { c('SteelBlue', '#4682B4') }
    Color getTan() { c('Tan', '#D2B48C') }
    Color getTeal() { c('Teal', '#008080') }
    Color getThistle() { c('Thistle', '#D8BFD8') }
    Color getTomato() { c('Tomato', '#FF6347') }
    Color getTurquoise() { c('Turquoise', '#40E0D0') }
    Color getViolet() { c('Violet', '#EE82EE') }
    Color getWheat() { c('Wheat', '#F5DEB3') }
    Color getWhite() { c('White', '#FFFFFF') }
    Color getWhiteSmoke() { c('WhiteSmoke', '#F5F5F5') }
    Color getYellow() { c('Yellow', '#FFFF00') }
    Color getYellowGreen() { c('YellowGreen', '#9ACD32') }
}

