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

    //------------------------------------------------------------------> Colors
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

    //------------------------------------------------------------------> HTML5 elements
    /** Math element. */
    StyleGroup math(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('math' + sel, clos) }

    /** Scalable vector graphics. */
    StyleGroup svg(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('svg' + sel, clos) }

    /** Hyperlink. */
    StyleGroup a(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('a' + sel, clos) }

    /** Hyperlink a:hover. */
    StyleGroup a_hover(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('a:hover' + sel, clos) }

    /** Hyperlink a:focus. */
    StyleGroup a_focus(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('a:focus' + sel, clos) }

    /** Hyperlink a:active. */
    StyleGroup a_active(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('a:active' + sel, clos) }

    /** Hyperlink a:visited. */
    StyleGroup a_visited(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('a:visited' + sel, clos) }
    
    /** Abbreviation. */
    StyleGroup abbr(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('abbr' + sel, clos) }

    /** Contact information. */
    StyleGroup address(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('address' + sel, clos) }

    /** Image-map hyperlink. */
    StyleGroup area(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('area' + sel, clos) }

    /** Article. */
    StyleGroup article(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('article' + sel, clos) }

    /** Tangential content. */
    StyleGroup aside(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('aside' + sel, clos) }

    /** Audio stream. */
    StyleGroup audio(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('audio' + sel, clos) }

    /** Offset text conventionally styled in bold. */
    StyleGroup b(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('b' + sel, clos) }

    /** Base URL. */
    StyleGroup base(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('base' + sel, clos) }

    /** BiDi isolate. */
    StyleGroup bdi(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('bdi' + sel, clos) }

    /** BiDi override. */
    StyleGroup bdo(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('bdo' + sel, clos) }

    /** Block quotation. */
    StyleGroup blockquote(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('blockquote' + sel, clos) }

    /** Document body. */
    StyleGroup body(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('body' + sel, clos) }

    /** Line break. */
    StyleGroup br(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('br' + sel, clos) }

    /** Button. */
    StyleGroup button(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('button' + sel, clos) }

    /** Submit button. */
    StyleGroup buttonSubmit(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('button [type="submit"]' + sel, clos) }

    /** Reset button. */
    StyleGroup buttonReset(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('button [type="reset"]' + sel, clos) }

    /** Button with no additional semantics. */
    StyleGroup buttonButton(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('button [type="button"]' + sel, clos) }

    /** Canvas for dynamic graphics. */
    StyleGroup canvas(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('canvas' + sel, clos) }

    /** Table title. */
    StyleGroup caption(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('caption' + sel, clos) }

    /** Cited title of a work. */
    StyleGroup cite(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('cite' + sel, clos) }

    /** Code fragment. */
    StyleGroup code(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('code' + sel, clos) }

    /** Table column. */
    StyleGroup col(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('col' + sel, clos) }

    /** Table column group. */
    StyleGroup colgroup(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('colgroup' + sel, clos) }

    /** Command. */
    StyleGroup command(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('command' + sel, clos) }

    /** Command with an associated action. */
    StyleGroup commandCommand(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('command [type="command"]' + sel, clos) }

    /** Selection of one item from a list of items. */
    StyleGroup commandRadio(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('command [type="radio"]' + sel, clos) }

    /** State or option that can be toggled. */
    StyleGroup commandCheckbox(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('command [type="checkbox"]' + sel, clos) }

    /** Predefined options for other controls. */
    StyleGroup datalist(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('datalist' + sel, clos) }

    /** Description or value. */
    StyleGroup dd(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('dd' + sel, clos) }

    /** Deleted text. */
    StyleGroup del(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('del' + sel, clos) }

    /** Control for additional on-demand information. */
    StyleGroup details(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('details' + sel, clos) }

    /** Defining instance. */
    StyleGroup dfn(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('dfn' + sel, clos) }

    /** Generic flow container. */
    StyleGroup div(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('div' + sel, clos) }

    /** Description list. */
    StyleGroup dl(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('dl' + sel, clos) }

    /** Term or name. */
    StyleGroup dt(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('dt' + sel, clos) }

    /** Emphatic stress. */
    StyleGroup em(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('em' + sel, clos) }

    /** Integration point for plugins. */
    StyleGroup embed(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('embed' + sel, clos) }

    /** Set of related form controls. */
    StyleGroup fieldset(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('fieldset' + sel, clos) }

    /** Figure caption. */
    StyleGroup figcaption(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('figcaption' + sel, clos) }

    /** Figure with optional caption. */
    StyleGroup figure(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('figure' + sel, clos) }

    /** Footer. */
    StyleGroup footer(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('footer' + sel, clos) }

    /** User-submittable form. */
    StyleGroup form(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('form' + sel, clos) }

    /** Heading. */
    StyleGroup h1(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('h1' + sel, clos) }

    /** Heading. */
    StyleGroup h2(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('h2' + sel, clos) }

    /** Heading. */
    StyleGroup h3(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('h3' + sel, clos) }

    /** Heading. */
    StyleGroup h4(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('h4' + sel, clos) }

    /** Heading. */
    StyleGroup h5(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('h5' + sel, clos) }

    /** Heading. */
    StyleGroup h6(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('h6' + sel, clos) }

    /** Document metadata container. */
    StyleGroup head(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('head' + sel, clos) }

    /** Header. */
    StyleGroup header(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('header' + sel, clos) }

    /** Heading group. */
    StyleGroup hgroup(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('hgroup' + sel, clos) }

    /** Thematic break. */
    StyleGroup hr(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('hr' + sel, clos) }

    /** Root element. */
    StyleGroup html(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('html' + sel, clos) }

    /** Offset text conventionally styled in italic. */
    StyleGroup i(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('i' + sel, clos) }

    /** Nested browsing context (inline frame). */
    StyleGroup iframe(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('iframe' + sel, clos) }

    /** Image. */
    StyleGroup img(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('img' + sel, clos) }

    /** Input control. */
    StyleGroup input(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('input' + sel, clos) }

    /** Text-input field. */
    StyleGroup inputText(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('input [type="text"]' + sel, clos) }

    /** Password-input field. */
    StyleGroup inputPassword(@DelegatesTo(StyleGroup) Closure clos, String sel='') {
        sg('input [type="password"]' + sel, clos) }

    /** Checkbox. */
    StyleGroup inputCheckbox(@DelegatesTo(StyleGroup) Closure clos, String sel='') {
        sg('input [type="checkbox"]' + sel, clos) }

    /** Radio button. */
    StyleGroup inputRadio(@DelegatesTo(StyleGroup) Closure clos, String sel='') {
        sg('input [type="radio"]' + sel, clos) }

    /** Button. */
    StyleGroup inputButton(@DelegatesTo(StyleGroup) Closure clos, String sel='') {
        sg('input [type="button"]' + sel, clos) }

    /** Submit button. */
    StyleGroup inputSubmit(@DelegatesTo(StyleGroup) Closure clos, String sel='') {
        sg('input [type="submit"]' + sel, clos) }

    /** Reset button. */
    StyleGroup inputReset(@DelegatesTo(StyleGroup) Closure clos, String sel='') {
        sg('input [type="reset"]' + sel, clos) }

    /** File upload control. */
    StyleGroup inputFile(@DelegatesTo(StyleGroup) Closure clos, String sel='') {
        sg('input [type="file"]' + sel, clos) }

    /** Hidden input control. */
    StyleGroup inputHidden(@DelegatesTo(StyleGroup) Closure clos, String sel='') {
        sg('input [type="hidden"]' + sel, clos) }

    /** Image-coordinates input control. */
    StyleGroup inputImage(@DelegatesTo(StyleGroup) Closure clos, String sel='') {
        sg('input [type="image"]' + sel, clos) }

    /** Global date-and-time input control. */
    StyleGroup inputDatetime(@DelegatesTo(StyleGroup) Closure clos, String sel='') {
        sg('input [type="datetime"]' + sel, clos) }

    /** Local date-and-time input control. */
    StyleGroup inputDatetimeLocal(@DelegatesTo(StyleGroup) Closure clos, String sel='') {
        sg('input [type="datetime-local"]' + sel, clos) }

    /** Date input control. */
    StyleGroup inputDate(@DelegatesTo(StyleGroup) Closure clos, String sel='') {
        sg('input [type="date"]' + sel, clos) }

    /** Year-and-month input control. */
    StyleGroup inputMonth(@DelegatesTo(StyleGroup) Closure clos, String sel='') {
        sg('input [type="month"]' + sel, clos) }

    /** Time input control. */
    StyleGroup inputTime(@DelegatesTo(StyleGroup) Closure clos, String sel='') {
        sg('input [type="time"]' + sel, clos) }

    /** Year-and-week input control. */
    StyleGroup inputWeek(@DelegatesTo(StyleGroup) Closure clos, String sel='') {
        sg('input [type="week"]' + sel, clos) }

    /** Number input control. */
    StyleGroup inputNumber(@DelegatesTo(StyleGroup) Closure clos, String sel='') {
        sg('input [type="number"]' + sel, clos) }

    /** Imprecise number-input control. */
    StyleGroup inputRange(@DelegatesTo(StyleGroup) Closure clos, String sel='') {
        sg('input [type="range"]' + sel, clos) }

    /** E-mail address input control. */
    StyleGroup inputEmail(@DelegatesTo(StyleGroup) Closure clos, String sel='') {
        sg('input [type="email"]' + sel, clos) }

    /** URL input control. */
    StyleGroup inputUrl(@DelegatesTo(StyleGroup) Closure clos, String sel='') {
        sg('input [type="url"]' + sel, clos) }

    /** Search field. */
    StyleGroup inputSearch(@DelegatesTo(StyleGroup) Closure clos, String sel='') {
        sg('input [type="search"]' + sel, clos) }

    /** Telephone-number-input field. */
    StyleGroup inputTel(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('input [type="tel"]' + sel, clos) }

    /** Color-well control. */
    StyleGroup inputColor(@DelegatesTo(StyleGroup) Closure clos, String sel=''){ sg('input [type="color"]' + sel, clos)}

    /** Inserted text. */
    StyleGroup ins(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('ins' + sel, clos) }

    /** User input. */
    StyleGroup kbd(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('kbd' + sel, clos) }

    /** Key-pair generator/input control. */
    StyleGroup keygen(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('keygen' + sel, clos) }

    /** Caption for a form control. */
    StyleGroup label(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('label' + sel, clos) }

    /** Title or explanatory caption. */
    StyleGroup legend(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('legend' + sel, clos) }

    /** List item. */
    StyleGroup li(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('li' + sel, clos) }

    /** Inter-document relationship metadata. */
    StyleGroup link(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('link' + sel, clos) }

    /** Image-map definition. */
    StyleGroup map(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('map' + sel, clos) }

    /** Marked (highlighted) text. */
    StyleGroup mark(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('mark' + sel, clos) }

    /** List of commands. */
    StyleGroup menu(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('menu' + sel, clos) }

    /** Scalar gauge. */
    StyleGroup meter(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('meter' + sel, clos) }

    /** Group of navigational links. */
    StyleGroup nav(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('nav' + sel, clos) }

    /** Fallback content for script. */
    StyleGroup noscript(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('noscript' + sel, clos) }

    /** Generic external content. */
    StyleGroup object(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('object' + sel, clos) }

    /** Ordered list. */
    StyleGroup ol(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('ol' + sel, clos) }

    /** Group of options. */
    StyleGroup optgroup(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('optgroup' + sel, clos) }

    /** Option. */
    StyleGroup option(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('option' + sel, clos) }

    /** Result of a calculation in a form. */
    StyleGroup output(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('output' + sel, clos) }

    /** Paragraph. */
    StyleGroup p(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('p' + sel, clos) }

    /** Initialization parameters for plugins. */
    StyleGroup param(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('param' + sel, clos) }

    /** Preformatted text. */
    StyleGroup pre(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('pre' + sel, clos) }

    /** Progress indicator. */
    StyleGroup progress(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('progress' + sel, clos) }

    /** Quoted text. */
    StyleGroup q(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('q' + sel, clos) }

    /** Ruby parenthesis. */
    StyleGroup rp(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('rp' + sel, clos) }

    /** Ruby text. */
    StyleGroup rt(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('rt' + sel, clos) }

    /** Ruby annotation. */
    StyleGroup ruby(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('ruby' + sel, clos) }

    /** Struck text. */
    StyleGroup s(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('s' + sel, clos) }

    /** (sample) output. */
    StyleGroup samp(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('samp' + sel, clos) }

    /** Section. */
    StyleGroup section(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('section' + sel, clos) }

    /** Option-selection form control. */
    StyleGroup select(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('select' + sel, clos) }

    /** Small print. */
    StyleGroup small(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('small' + sel, clos) }

    /** Media source. */
    StyleGroup source(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('source' + sel, clos) }

    /** Generic span. */
    StyleGroup span(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('span' + sel, clos) }

    /** Strong importance. */
    StyleGroup strong(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('strong' + sel, clos) }

    /** Subscript. */
    StyleGroup sub(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('sub' + sel, clos) }

    /** Summary, caption, or legend for a details control. */
    StyleGroup summary(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('summary' + sel, clos) }

    /** Superscript. */
    StyleGroup sup(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('sup' + sel, clos) }

    /** Table. */
    StyleGroup table(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('table' + sel, clos) }

    /** Table row group. */
    StyleGroup tbody(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('tbody' + sel, clos) }

    /** Table cell. */
    StyleGroup td(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('td' + sel, clos) }

    /** Text input area. */
    StyleGroup textarea(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('textarea' + sel, clos) }

    /** Table footer row group. */
    StyleGroup tfoot(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('tfoot' + sel, clos) }

    /** Table header cell. */
    StyleGroup th(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('th' + sel, clos) }

    /** Table heading group. */
    StyleGroup thead(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('thead' + sel, clos) }

    /** Date and/or time. */
    StyleGroup time(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('time' + sel, clos) }

    /** Document title. */
    StyleGroup title(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('title' + sel, clos) }

    /** Table row. */
    StyleGroup tr(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('tr' + sel, clos) }

    /** Supplementary media track. */
    StyleGroup track(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('track' + sel, clos) }

    /** Offset text conventionally styled with an underline. */
    StyleGroup u(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('u' + sel, clos) }

    /** Unordered list. */
    StyleGroup ul(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('ul' + sel, clos) }

    /** Variable or placeholder text. */
    StyleGroup var(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('var' + sel, clos) }

    /** Video. */
    StyleGroup video(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('video' + sel, clos) }

    /** Line-break opportunity. */
    StyleGroup wbr(@DelegatesTo(StyleGroup) Closure clos, String sel='') { sg('wbr' + sel, clos) }

    //------------------------------------------------------------------> Math
    /** Returns the absolute value of a value.*/
    double abs(Number n) { (n instanceof Integer) ? n.abs() : Math.abs(n.doubleValue()) }

    /** Returns the arc tangent of a value; the returned angle is in the range -pi/2 through pi/2.*/
    double atan(Number n) { Math.atan(n.doubleValue()) }

    /** Returns the angle theta from the conversion of rectangular coordinates (x, y) to polar coordinates (r, theta).*/
    double atan2(Number y, Number x) { Math.atan2(y.doubleValue(), x.doubleValue()) }

    /** Returns the arc cosine of a value; the returned angle is in the range 0.0 through pi.*/
    double acos(Number n) { Math.acos(n.doubleValue()) }

    /** Returns the arc sine of a value; the returned angle is in the range -pi/2 through pi/2. */
    double asin(Number n) { Math.asin(n.doubleValue()) }

    /** Returns the trigonometric cosine of an angle (in radians).*/
    double cos(Number angle) { Math.cos(angle.doubleValue()) }

    /** Returns the trigonometric sine of an angle (in radians).*/
    double sin(Number angle) { Math.sin(angle.doubleValue()) }

    /** Returns the natural logarithm (base e) of a double value.*/
    double log(Number n) { Math.log(n.doubleValue()) }

    /** Returns the base 10 logarithm of a double value.*/
    double log10(Number n) { Math.log10(n.doubleValue()) }

    /** Returns the smallest (closest to negative infinity) double value that is greater than or equal to the argument
     * and is equal to a mathematical integer.*/
    int ceiling(Number n) { Math.ceil(n.doubleValue()) as int }

    /** Returns the largest (closest to positive infinity) double value that is less than or equal to the argument and
     * is equal to a mathematical integer.*/
    int floor(Number n) { Math.floor(n.doubleValue()) as int }

    /** Returns the value of the first argument raised to the power of the second argument.*/
    double pow(Number n, Number pow) { Math.pow(n.doubleValue(), pow.doubleValue()) }

    /** Returns the correctly rounded positive square root of a double value.*/
    double sqrt(Number n) { Math.sqrt(n.doubleValue()) }

    /** Returns the cube root of a double value.*/
    double cbrt(Number n) { Math.cbrt(n.doubleValue()) }

    /** Returns the trigonometric tangent of an angle (in radians).*/
    double tan(Number angle) { Math.tan(angle.doubleValue()) }

    /** Converts an angle measured in radians to an approximately equivalent angle measured in degrees.*/
    double toDegrees(Number angrad) { Math.toDegrees(angrad.doubleValue()) }

    /**Converts an angle measured in degrees to an approximately equivalent angle measured in radians.*/
    double toRadians(Number angdeg) { Math.toRadians(angdeg.doubleValue()) }

}

