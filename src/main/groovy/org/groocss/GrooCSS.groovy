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

import org.codehaus.groovy.control.CompilerConfiguration

import groovy.transform.*
import org.codehaus.groovy.control.customizers.ImportCustomizer

import javax.imageio.ImageIO
import java.lang.reflect.Type

/**
 * Entrance to DSL for converting code into CSS.
 */
class GrooCSS extends Script implements CurrentKeyFrameHolder {

    static final ThreadLocal<GrooCSS> threadLocalInstance = new ThreadLocal<>()

    static void convertFile(Config conf = new Config(), String inf, String outf) {
        convert conf, new File(inf), new File(outf)
    }
    static void convertFile(Config conf = new Config(), File inf, File out) { convert conf, inf, out }

    /** Converts files from CSS to GrooCSS (WARNING: EXPERIMENTAL, DO NOT ASSUME IT WORKS). */
    @Deprecated
    static void convertFromCSS(File inf, File out) {
        Translator.convertFromCSS inf, out
    }
    
    static void convert(Config conf = new Config(), File inf, File out) {
        convert(conf, inf.newInputStream(), out.newOutputStream())
    }

    /** Processes a given groocss string and outputs as CSS string. */
    static String convert(Config conf = new Config(), String groocss, String charset1 = "UTF-8") {
        def out = new ByteArrayOutputStream()
        convert conf, new ByteArrayInputStream(groocss.getBytes(charset1)), out
        out.toString()
    }

    private static GroovyShell makeShell() {
        def binding = new Binding()
        def compilerConfig = new CompilerConfiguration()
        def imports = new ImportCustomizer()
        def packg = 'org.groocss'
        imports.addStarImports(packg)
        compilerConfig.addCompilationCustomizers(imports)
        compilerConfig.scriptBaseClass = "${packg}.GrooCSS"

        new GroovyShell(GrooCSS.class.classLoader, binding, compilerConfig)
    }

    /** Processes a given InputStream and outputs to given OutputStream. */
    @TypeChecked
    static void convert(Config conf = new Config(), InputStream inf, OutputStream out, String charset1 = "UTF-8") {
        out.withPrintWriter { pw ->
            convert conf, new InputStreamReader(inf, charset1), pw
        }
    }

    /** Processes a given Reader and outputs to given PrintWriter. */
    @TypeChecked
    static void convert(Config conf = new Config(), Reader reader, PrintWriter writer) {
        def shell = makeShell()
        reader.withCloseable { input ->
            def script = shell.parse(input)
            script.invokeMethod('setConfig', conf)
            script.run()
            MediaCSS css = (MediaCSS) script.getProperty('css')
            css.doProcessing()
            writer.withCloseable { pw ->
                css.writeTo pw
                pw.flush()
            }
        }
    }

    /** Processes a given InputStream and outputs to given OutputStream. */
    static void process(Config conf = new Config(), InputStream ins, OutputStream out) { convert conf, ins, out }

    /** Processes a given Reader and outputs to given PrintWriter. */
    static void process(Config conf = new Config(), Reader reader, PrintWriter writer) { convert conf, reader, writer }

    /** Processes a given groocss string and outputs as CSS string. */
    static String process(Config conf = new Config(), String groocss, String charset1 = "UTF-8") {
        convert conf, groocss, charset1
    }

    @CompileStatic
    static class Configurer extends Config {

        Configurer convert(File inf, File out) {
            GrooCSS.convert(this, inf, out)
            this
        }

        /** Processes a given InputStream and outputs to given OutputStream. */
        Configurer convert(InputStream ins, OutputStream out, String charset1 = null) {
            charset1 ? GrooCSS.convert(this, ins, out, charset1) : GrooCSS.convert(this, ins, out)
            this
        }

        /** Processes a given Reader and outputs to given PrintWriter. */
        Configurer convert(Reader reader, PrintWriter writer) { GrooCSS.convert(this, reader, writer); this }

        /** Processes a given groocss string and outputs as CSS string. */
        String convert(String groocss, String charset1 = null) {
            charset1 ? GrooCSS.convert(this, groocss, charset1) : GrooCSS.convert(this, groocss)
        }

        /** Processes the given closure with built config. */
        GrooCSS process(@DelegatesTo(GrooCSS) Closure clos) { GrooCSS.runBlock(this, clos) }

        /** Processes a given InputStream and outputs to given OutputStream. */
        Configurer process(InputStream ins, OutputStream out, String charset1 = null) { convert ins, out, charset1 }

        /** Processes a given Reader and outputs to given PrintWriter. */
        Configurer process(Reader reader, PrintWriter writer) { convert reader, writer }

        /** Processes a given File and outputs to given out File. */
        Configurer process(File inf, File out) { convert inf, out }

        /** Processes a given groocss string and outputs as CSS string. */
        String process(String groocss, String charset1 = null) { convert groocss, charset1 }

        /** Processes the given closure with built config. */
        GrooCSS runBlock(@DelegatesTo(GrooCSS) Closure clos) { GrooCSS.runBlock(this, clos) }
    }

    static Configurer withConfig(@DelegatesTo(Configurer) Closure<Configurer> closure) {
        Configurer c = new Configurer()
        closure.delegate = c
        closure(c)
        c
    }

    /** Main config. */
    Config config = new Config()

    /** Main MediaCSS root.*/
    MediaCSS css = new MediaCSS(this, config)

    /** Makes sure that config passes through to root css. */
    void setConfig(Config config1) { config = css.config = config1 }

    /** Current MediaCSS object used for processing. */
    MediaCSS currentCss = css


    GrooCSS() {
        Number.metaClass.propertyMissing = { new Measurement(delegate, "$it") }
        threadLocalInstance.set(this) // set this instance for the current Thread
    }

    GrooCSS(Config config) {
        this()
        this.config = config
    }

    String toString() { css.toString() }

    MediaCSS media(String mediaRule, @DelegatesTo(GrooCSS) Closure clos) {
        MediaCSS mcss = new MediaCSS(this, mediaRule, config)
        MediaCSS oldCss = currentCss
        currentCss = mcss
        clos.delegate = this
        clos(mcss)
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
        KeyFrames frames = currentKf = new KeyFrames(name: name, config: currentCss.config)
        clos.delegate = frames
        clos(frames)
        currentKf = null
        currentCss << frames
        frames
    }

    /** Creates a new StyleGroup element and runs given closure on it. */
    StyleGroup sel(String selector, @DelegatesTo(StyleGroup) Closure<StyleGroup> clos) {
        currentCss.sel(selector, clos)
    }

    /** Creates a selector from any given argument. */
    Selector sel(selector) {
        new Selector("$selector", currentCss)
    }

    /** Creates a new StyleGroup element and runs given closure on it. */
    StyleGroup sel(Selector selector, @DelegatesTo(StyleGroup) Closure<StyleGroup> clos) {
        currentCss.sel("$selector", clos)
    }

    /** Creates an unattached StyleGroup object, useful for adding Styles to a StyleGroup conditionally
     * or for reusing a group of styles several times. */
    StyleGroup styles(@DelegatesTo(StyleGroup) Closure<StyleGroup> clos) {
        currentCss.sel('', clos, false)
    }

    /** Creates a new @font-face element and runs given closure on it. */
    FontFace fontFace(@DelegatesTo(FontFace) Closure clos) {
        FontFace ff = new FontFace()
        clos.delegate = ff
        clos(ff)
        currentCss.add ff
        ff
    }

    /** Creates a new StyleGroup element and runs given closure on it. */
    StyleGroup sg(String selector, @DelegatesTo(StyleGroup) Closure clos) {
        sel(selector, clos)
    }

    /** Creates a new StyleGroup element and runs given closure on it. */
    StyleGroup $(String selector, @DelegatesTo(StyleGroup) Closure clos) {
        sel(selector, clos)
    }

    /** Creates a new StyleGroup element and runs given closure on it. */
    StyleGroup sg(Selector selector, @DelegatesTo(StyleGroup) Closure clos) {
        sel(selector, clos)
    }

    /** Creates a new StyleGroup element, with all selectors in the given list joined with commas.
     * If given list is empty, this method has the same behaviour as styles(closure). */
    StyleGroup sg(List selectors, @DelegatesTo(StyleGroup) Closure clos) {
        if (selectors.isEmpty()) {
            return styles(clos)
        }
        sg(selectors.tail().inject(selectors[0]) {a,b -> a|b}, clos)
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

    @Delegate
    final ColorMethods colorMethods = new ColorMethods()

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
        gcss.css.doProcessing()
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

    //------------------------------------------------------------------> HTML5 elements
    /** Math element. */
    StyleGroup math(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('math' + sel, clos) }

    /** Scalable vector graphics. */
    StyleGroup svg(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('svg' + sel, clos) }

    /** Hyperlink. */
    StyleGroup a(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('a' + sel, clos) }

    /** Hyperlink a:hover. */
    StyleGroup a_hover(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('a:hover' + sel, clos) }

    /** Hyperlink a:focus. */
    StyleGroup a_focus(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('a:focus' + sel, clos) }

    /** Hyperlink a:active. */
    StyleGroup a_active(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('a:active' + sel, clos) }

    /** Hyperlink a:visited. */
    StyleGroup a_visited(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('a:visited' + sel, clos) }
    
    /** Abbreviation. */
    StyleGroup abbr(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('abbr' + sel, clos) }

    /** Contact information. */
    StyleGroup address(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('address' + sel, clos) }

    /** Image-map hyperlink. */
    StyleGroup area(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('area' + sel, clos) }

    /** Article. */
    StyleGroup article(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('article' + sel, clos) }

    /** Tangential content. */
    StyleGroup aside(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('aside' + sel, clos) }

    /** Audio stream. */
    StyleGroup audio(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('audio' + sel, clos) }

    /** Offset text conventionally styled in bold. */
    StyleGroup b(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('b' + sel, clos) }

    /** Base URL. */
    StyleGroup base(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('base' + sel, clos) }

    /** BiDi isolate. */
    StyleGroup bdi(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('bdi' + sel, clos) }

    /** BiDi override. */
    StyleGroup bdo(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('bdo' + sel, clos) }

    /** Block quotation. */
    StyleGroup blockquote(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('blockquote' + sel, clos) }

    /** Document body. */
    StyleGroup body(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('body' + sel, clos) }

    /** Line break. */
    StyleGroup br(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('br' + sel, clos) }

    /** Button. */
    StyleGroup button(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('button' + sel, clos) }

    /** Submit button. */
    StyleGroup buttonSubmit(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('button [type="submit"]' + sel, clos) }

    /** Reset button. */
    StyleGroup buttonReset(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('button [type="reset"]' + sel, clos) }

    /** Button with no additional semantics. */
    StyleGroup buttonButton(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('button [type="button"]' + sel, clos) }

    /** Canvas for dynamic graphics. */
    StyleGroup canvas(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('canvas' + sel, clos) }

    /** Table title. */
    StyleGroup caption(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('caption' + sel, clos) }

    /** Cited title of a work. */
    StyleGroup cite(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('cite' + sel, clos) }

    /** Code fragment. */
    StyleGroup code(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('code' + sel, clos) }

    /** Table column. */
    StyleGroup col(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('col' + sel, clos) }

    /** Table column group. */
    StyleGroup colgroup(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('colgroup' + sel, clos) }

    /** Command. */
    StyleGroup command(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('command' + sel, clos) }

    /** Command with an associated action. */
    StyleGroup commandCommand(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('command [type="command"]' + sel, clos) }

    /** Selection of one item from a list of items. */
    StyleGroup commandRadio(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('command [type="radio"]' + sel, clos) }

    /** State or option that can be toggled. */
    StyleGroup commandCheckbox(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('command [type="checkbox"]' + sel, clos) }

    /** Predefined options for other controls. */
    StyleGroup datalist(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('datalist' + sel, clos) }

    /** Description or value. */
    StyleGroup dd(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('dd' + sel, clos) }

    /** Deleted text. */
    StyleGroup del(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('del' + sel, clos) }

    /** Control for additional on-demand information. */
    StyleGroup details(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('details' + sel, clos) }

    /** Defining instance. */
    StyleGroup dfn(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('dfn' + sel, clos) }

    /** Defines a dialog box or window*/
    StyleGroup dialog(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('dialog' + sel, clos) }

    /** Generic flow container. */
    StyleGroup div(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('div' + sel, clos) }

    /** Description list. */
    StyleGroup dl(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('dl' + sel, clos) }

    /** Term or name. */
    StyleGroup dt(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('dt' + sel, clos) }

    /** Emphatic stress. */
    StyleGroup em(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('em' + sel, clos) }

    /** Integration point for plugins. */
    StyleGroup embed(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('embed' + sel, clos) }

    /** Set of related form controls. */
    StyleGroup fieldset(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('fieldset' + sel, clos) }

    /** Figure caption. */
    StyleGroup figcaption(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('figcaption' + sel, clos) }

    /** Figure with optional caption. */
    StyleGroup figure(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('figure' + sel, clos) }

    /** Footer. */
    StyleGroup footer(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('footer' + sel, clos) }

    /** User-submittable form. */
    StyleGroup form(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('form' + sel, clos) }

    /** Heading. */
    StyleGroup h1(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('h1' + sel, clos) }

    /** Heading. */
    StyleGroup h2(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('h2' + sel, clos) }

    /** Heading. */
    StyleGroup h3(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('h3' + sel, clos) }

    /** Heading. */
    StyleGroup h4(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('h4' + sel, clos) }

    /** Heading. */
    StyleGroup h5(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('h5' + sel, clos) }

    /** Heading. */
    StyleGroup h6(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('h6' + sel, clos) }

    /** Document metadata container. */
    StyleGroup head(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('head' + sel, clos) }

    /** Header. */
    StyleGroup header(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('header' + sel, clos) }

    /** Heading group. */
    StyleGroup hgroup(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('hgroup' + sel, clos) }

    /** Thematic break. */
    StyleGroup hr(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('hr' + sel, clos) }

    /** Root element. */
    StyleGroup html(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('html' + sel, clos) }

    /** Offset text conventionally styled in italic. */
    StyleGroup i(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('i' + sel, clos) }

    /** Nested browsing context (inline frame). */
    StyleGroup iframe(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('iframe' + sel, clos) }

    /** Image. */
    StyleGroup img(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('img' + sel, clos) }

    /** Input control. */
    StyleGroup input(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('input' + sel, clos) }

    /** Text-input field. */
    StyleGroup inputText(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('input [type="text"]' + sel, clos) }

    /** Password-input field. */
    StyleGroup inputPassword(String sel='', @DelegatesTo(StyleGroup) Closure clos) {
        sg('input [type="password"]' + sel, clos) }

    /** Checkbox. */
    StyleGroup inputCheckbox(String sel='', @DelegatesTo(StyleGroup) Closure clos) {
        sg('input [type="checkbox"]' + sel, clos) }

    /** Radio button. */
    StyleGroup inputRadio(String sel='', @DelegatesTo(StyleGroup) Closure clos) {
        sg('input [type="radio"]' + sel, clos) }

    /** Button. */
    StyleGroup inputButton(String sel='', @DelegatesTo(StyleGroup) Closure clos) {
        sg('input [type="button"]' + sel, clos) }

    /** Submit button. */
    StyleGroup inputSubmit(String sel='', @DelegatesTo(StyleGroup) Closure clos) {
        sg('input [type="submit"]' + sel, clos) }

    /** Reset button. */
    StyleGroup inputReset(String sel='', @DelegatesTo(StyleGroup) Closure clos) {
        sg('input [type="reset"]' + sel, clos) }

    /** File upload control. */
    StyleGroup inputFile(String sel='', @DelegatesTo(StyleGroup) Closure clos) {
        sg('input [type="file"]' + sel, clos) }

    /** Hidden input control. */
    StyleGroup inputHidden(String sel='', @DelegatesTo(StyleGroup) Closure clos) {
        sg('input [type="hidden"]' + sel, clos) }

    /** Image-coordinates input control. */
    StyleGroup inputImage(String sel='', @DelegatesTo(StyleGroup) Closure clos) {
        sg('input [type="image"]' + sel, clos) }

    /** Global date-and-time input control. */
    StyleGroup inputDatetime(String sel='', @DelegatesTo(StyleGroup) Closure clos) {
        sg('input [type="datetime"]' + sel, clos) }

    /** Local date-and-time input control. */
    StyleGroup inputDatetimeLocal(String sel='', @DelegatesTo(StyleGroup) Closure clos) {
        sg('input [type="datetime-local"]' + sel, clos) }

    /** Date input control. */
    StyleGroup inputDate(String sel='', @DelegatesTo(StyleGroup) Closure clos) {
        sg('input [type="date"]' + sel, clos) }

    /** Year-and-month input control. */
    StyleGroup inputMonth(String sel='', @DelegatesTo(StyleGroup) Closure clos) {
        sg('input [type="month"]' + sel, clos) }

    /** Time input control. */
    StyleGroup inputTime(String sel='', @DelegatesTo(StyleGroup) Closure clos) {
        sg('input [type="time"]' + sel, clos) }

    /** Year-and-week input control. */
    StyleGroup inputWeek(String sel='', @DelegatesTo(StyleGroup) Closure clos) {
        sg('input [type="week"]' + sel, clos) }

    /** Number input control. */
    StyleGroup inputNumber(String sel='', @DelegatesTo(StyleGroup) Closure clos) {
        sg('input [type="number"]' + sel, clos) }

    /** Imprecise number-input control. */
    StyleGroup inputRange(String sel='', @DelegatesTo(StyleGroup) Closure clos) {
        sg('input [type="range"]' + sel, clos) }

    /** E-mail address input control. */
    StyleGroup inputEmail(String sel='', @DelegatesTo(StyleGroup) Closure clos) {
        sg('input [type="email"]' + sel, clos) }

    /** URL input control. */
    StyleGroup inputUrl(String sel='', @DelegatesTo(StyleGroup) Closure clos) {
        sg('input [type="url"]' + sel, clos) }

    /** Search field. */
    StyleGroup inputSearch(String sel='', @DelegatesTo(StyleGroup) Closure clos) {
        sg('input [type="search"]' + sel, clos) }

    /** Telephone-number-input field. */
    StyleGroup inputTel(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('input [type="tel"]' + sel, clos) }

    /** Color-well control. */
    StyleGroup inputColor(String sel='', @DelegatesTo(StyleGroup) Closure clos){ sg('input [type="color"]' + sel, clos)}

    /** Inserted text. */
    StyleGroup ins(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('ins' + sel, clos) }

    /** User input. */
    StyleGroup kbd(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('kbd' + sel, clos) }

    /** Key-pair generator/input control. */
    StyleGroup keygen(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('keygen' + sel, clos) }

    /** Caption for a form control. */
    StyleGroup label(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('label' + sel, clos) }

    /** Title or explanatory caption. */
    StyleGroup legend(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('legend' + sel, clos) }

    /** List item. */
    StyleGroup li(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('li' + sel, clos) }

    /** Inter-document relationship metadata. */
    StyleGroup link(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('link' + sel, clos) }

    /** Main definition. */
    StyleGroup main(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('main' + sel, clos) }

    /** Image-map definition. */
    StyleGroup map(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('map' + sel, clos) }

    /** Marked (highlighted) text. */
    StyleGroup mark(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('mark' + sel, clos) }

    /** List of commands. */
    StyleGroup menu(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('menu' + sel, clos) }

    /** Scalar gauge. */
    StyleGroup meter(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('meter' + sel, clos) }

    /** Group of navigational links. */
    StyleGroup nav(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('nav' + sel, clos) }

    /** Fallback content for script. */
    StyleGroup noscript(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('noscript' + sel, clos) }

    /** Generic external content. */
    StyleGroup object(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('object' + sel, clos) }

    /** Ordered list. */
    StyleGroup ol(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('ol' + sel, clos) }

    /** Group of options. */
    StyleGroup optgroup(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('optgroup' + sel, clos) }

    /** Option. */
    StyleGroup option(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('option' + sel, clos) }

    /** Result of a calculation in a form. */
    StyleGroup output(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('output' + sel, clos) }

    /** Paragraph. */
    StyleGroup p(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('p' + sel, clos) }

    /** Initialization parameters for plugins. */
    StyleGroup param(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('param' + sel, clos) }

    /** Preformatted text. */
    StyleGroup pre(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('pre' + sel, clos) }

    /** Progress indicator. */
    StyleGroup progress(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('progress' + sel, clos) }

    /** Quoted text. */
    StyleGroup q(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('q' + sel, clos) }

    /** Ruby parenthesis. */
    StyleGroup rp(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('rp' + sel, clos) }

    /** Ruby text. */
    StyleGroup rt(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('rt' + sel, clos) }

    /** Ruby annotation. */
    StyleGroup ruby(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('ruby' + sel, clos) }

    /** Struck text. */
    StyleGroup s(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('s' + sel, clos) }

    /** (sample) output. */
    StyleGroup samp(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('samp' + sel, clos) }

    /** Section. */
    StyleGroup section(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('section' + sel, clos) }

    /** Option-selection form control. */
    StyleGroup select(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('select' + sel, clos) }

    /** Small print. */
    StyleGroup small(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('small' + sel, clos) }

    /** Media source. */
    StyleGroup source(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('source' + sel, clos) }

    /** Generic span. */
    StyleGroup span(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('span' + sel, clos) }

    /** Strong importance. */
    StyleGroup strong(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('strong' + sel, clos) }

    /** Subscript. */
    StyleGroup sub(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('sub' + sel, clos) }

    /** Summary, caption, or legend for a details control. */
    StyleGroup summary(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('summary' + sel, clos) }

    /** Superscript. */
    StyleGroup sup(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('sup' + sel, clos) }

    /** Table. */
    StyleGroup table(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('table' + sel, clos) }

    /** Table row group. */
    StyleGroup tbody(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('tbody' + sel, clos) }

    /** Table cell. */
    StyleGroup td(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('td' + sel, clos) }

    /** Text input area. */
    StyleGroup textarea(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('textarea' + sel, clos) }

    /** Table footer row group. */
    StyleGroup tfoot(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('tfoot' + sel, clos) }

    /** Table header cell. */
    StyleGroup th(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('th' + sel, clos) }

    /** Table heading group. */
    StyleGroup thead(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('thead' + sel, clos) }

    /** Date and/or time. */
    StyleGroup time(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('time' + sel, clos) }

    /** Document title. */
    StyleGroup title(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('title' + sel, clos) }

    /** Table row. */
    StyleGroup tr(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('tr' + sel, clos) }

    /** Supplementary media track. */
    StyleGroup track(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('track' + sel, clos) }

    /** Offset text conventionally styled with an underline. */
    StyleGroup u(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('u' + sel, clos) }

    /** Unordered list. */
    StyleGroup ul(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('ul' + sel, clos) }

    /** Variable or placeholder text. */
    StyleGroup var(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('var' + sel, clos) }

    /** Video. */
    StyleGroup video(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('video' + sel, clos) }

    /** Line-break opportunity. */
    StyleGroup wbr(String sel='', @DelegatesTo(StyleGroup) Closure clos) { sg('wbr' + sel, clos) }

    // --Just for space and tilde:
    /** Math element. */
    Selectable math(Selectable sel) { sel.resetSelector("math $sel.selector") }

    /** Scalable vector graphics. */
    Selectable svg(Selectable sel) { sel.resetSelector("svg $sel.selector") }

    /** Hyperlink. */
    Selectable a(Selectable sel) { sel.resetSelector("a $sel.selector") }

    /** Abbreviation. */
    Selectable abbr(Selectable sel) { sel.resetSelector("abbr $sel.selector") }

    /** Contact information. */
    Selectable address(Selectable sel) { sel.resetSelector("address $sel.selector") }

    /** Image-map hyperlink. */
    Selectable area(Selectable sel) { sel.resetSelector("area $sel.selector") }

    /** Article. */
    Selectable article(Selectable sel) { sel.resetSelector("article $sel.selector") }

    /** Tangential content. */
    Selectable aside(Selectable sel) { sel.resetSelector("aside $sel.selector") }

    /** Audio stream. */
    Selectable audio(Selectable sel) { sel.resetSelector("audio $sel.selector") }

    /** Offset text conventionally styled in bold. */
    Selectable b(Selectable sel) { sel.resetSelector("b $sel.selector") }

    /** Base URL. */
    Selectable base(Selectable sel) { sel.resetSelector("base $sel.selector") }

    /** BiDi isolate. */
    Selectable bdi(Selectable sel) { sel.resetSelector("bdi $sel.selector") }

    /** BiDi override. */
    Selectable bdo(Selectable sel) { sel.resetSelector("bdo $sel.selector") }

    /** Block quotation. */
    Selectable blockquote(Selectable sel) { sel.resetSelector("blockquote $sel.selector") }

    /** Document body. */
    Selectable body(Selectable sel) { sel.resetSelector("body $sel.selector") }

    /** Line break. */
    Selectable br(Selectable sel) { sel.resetSelector("br $sel.selector") }

    /** Button. */
    Selectable button(Selectable sel) { sel.resetSelector("button $sel.selector") }

    /** Canvas for dynamic graphics. */
    Selectable canvas(Selectable sel) { sel.resetSelector("canvas $sel.selector") }

    /** Table title. */
    Selectable caption(Selectable sel) { sel.resetSelector("caption $sel.selector") }

    /** Cited title of a work. */
    Selectable cite(Selectable sel) { sel.resetSelector("cite $sel.selector") }

    /** Code fragment. */
    Selectable code(Selectable sel) { sel.resetSelector("code $sel.selector") }

    /** Table column. */
    Selectable col(Selectable sel) { sel.resetSelector("col $sel.selector") }

    /** Table column group. */
    Selectable colgroup(Selectable sel) { sel.resetSelector("colgroup $sel.selector") }

    /** Command. */
    Selectable command(Selectable sel) { sel.resetSelector("command $sel.selector") }

    /** Predefined options for other controls. */
    Selectable datalist(Selectable sel) { sel.resetSelector("datalist $sel.selector") }

    /** Description or value. */
    Selectable dd(Selectable sel) { sel.resetSelector("dd $sel.selector") }

    /** Deleted text. */
    Selectable del(Selectable sel) { sel.resetSelector("del $sel.selector") }

    /** Control for additional on-demand information. */
    Selectable details(Selectable sel) { sel.resetSelector("details $sel.selector") }

    /** Defining instance. */
    Selectable dfn(Selectable sel) { sel.resetSelector("dfn $sel.selector") }

    /** Defines a dialog box or window. */
    Selectable dialog(Selectable sel) { sel.resetSelector("dialog $sel.selector") }

    /** Generic flow container. */
    Selectable div(Selectable sel) { sel.resetSelector("div $sel.selector") }

    /** Description list. */
    Selectable dl(Selectable sel) { sel.resetSelector("dl $sel.selector") }

    /** Term or name. */
    Selectable dt(Selectable sel) { sel.resetSelector("dt $sel.selector") }

    /** Emphatic stress. */
    Selectable em(Selectable sel) { sel.resetSelector("em $sel.selector") }

    /** Integration point for plugins. */
    Selectable embed(Selectable sel) { sel.resetSelector("embed $sel.selector") }

    /** Set of related form controls. */
    Selectable fieldset(Selectable sel) { sel.resetSelector("fieldset $sel.selector") }

    /** Figure caption. */
    Selectable figcaption(Selectable sel) { sel.resetSelector("figcaption $sel.selector") }

    /** Figure with optional caption. */
    Selectable figure(Selectable sel) { sel.resetSelector("figure $sel.selector") }

    /** Footer. */
    Selectable footer(Selectable sel) { sel.resetSelector("footer $sel.selector") }

    /** User-submittable form. */
    Selectable form(Selectable sel) { sel.resetSelector("form $sel.selector") }

    /** Heading. */
    Selectable h1(Selectable sel) { sel.resetSelector("h1 $sel.selector") }

    /** Heading. */
    Selectable h2(Selectable sel) { sel.resetSelector("h2 $sel.selector") }

    /** Heading. */
    Selectable h3(Selectable sel) { sel.resetSelector("h3 $sel.selector") }

    /** Heading. */
    Selectable h4(Selectable sel) { sel.resetSelector("h4 $sel.selector") }

    /** Heading. */
    Selectable h5(Selectable sel) { sel.resetSelector("h5 $sel.selector") }

    /** Heading. */
    Selectable h6(Selectable sel) { sel.resetSelector("h6 $sel.selector") }

    /** Document metadata container. */
    Selectable head(Selectable sel) { sel.resetSelector("head $sel.selector") }

    /** Header. */
    Selectable header(Selectable sel) { sel.resetSelector("header $sel.selector") }

    /** Heading group. */
    Selectable hgroup(Selectable sel) { sel.resetSelector("hgroup $sel.selector") }

    /** Thematic break. */
    Selectable hr(Selectable sel) { sel.resetSelector("hr $sel.selector") }

    /** Root element. */
    Selectable html(Selectable sel) { sel.resetSelector("html $sel.selector") }

    /** Offset text conventionally styled in italic. */
    Selectable i(Selectable sel) { sel.resetSelector("i $sel.selector") }

    /** Nested browsing context (inline frame). */
    Selectable iframe(Selectable sel) { sel.resetSelector("iframe $sel.selector") }

    /** Image. */
    Selectable img(Selectable sel) { sel.resetSelector("img $sel.selector") }

    /** Input control. */
    Selectable input(Selectable sel) { sel.resetSelector("input $sel.selector") }

    /** Inserted text. */
    Selectable ins(Selectable sel) { sel.resetSelector("ins $sel.selector") }

    /** User input. */
    Selectable kbd(Selectable sel) { sel.resetSelector("kbd $sel.selector") }

    /** Key-pair generator/input control. */
    Selectable keygen(Selectable sel) { sel.resetSelector("keygen $sel.selector") }

    /** Caption for a form control. */
    Selectable label(Selectable sel) { sel.resetSelector("label $sel.selector") }

    /** Title or explanatory caption. */
    Selectable legend(Selectable sel) { sel.resetSelector("legend $sel.selector") }

    /** List item. */
    Selectable li(Selectable sel) { sel.resetSelector("li $sel.selector") }

    /** Inter-document relationship metadata. */
    Selectable link(Selectable sel) { sel.resetSelector("link $sel.selector") }

    /** Main definition. */
    Selectable main(Selectable sel) { sel.resetSelector("main $sel.selector") }

    /** Image-map definition. */
    Selectable map(Selectable sel) { sel.resetSelector("map $sel.selector") }

    /** Marked (highlighted) text. */
    Selectable mark(Selectable sel) { sel.resetSelector("mark $sel.selector") }

    /** List of commands. */
    Selectable menu(Selectable sel) { sel.resetSelector("menu $sel.selector") }

    /** Scalar gauge. */
    Selectable meter(Selectable sel) { sel.resetSelector("meter $sel.selector") }

    /** Group of navigational links. */
    Selectable nav(Selectable sel) { sel.resetSelector("nav $sel.selector") }

    /** Fallback content for script. */
    Selectable noscript(Selectable sel) { sel.resetSelector("noscript $sel.selector") }

    /** Generic external content. */
    Selectable object(Selectable sel) { sel.resetSelector("object $sel.selector") }

    /** Ordered list. */
    Selectable ol(Selectable sel) { sel.resetSelector("ol $sel.selector") }

    /** Group of options. */
    Selectable optgroup(Selectable sel) { sel.resetSelector("optgroup $sel.selector") }

    /** Option. */
    Selectable option(Selectable sel) { sel.resetSelector("option $sel.selector") }

    /** Result of a calculation in a form. */
    Selectable output(Selectable sel) { sel.resetSelector("output $sel.selector") }

    /** Paragraph. */
    Selectable p(Selectable sel) { sel.resetSelector("p $sel.selector") }

    /** Initialization parameters for plugins. */
    Selectable param(Selectable sel) { sel.resetSelector("param $sel.selector") }

    /** Preformatted text. */
    Selectable pre(Selectable sel) { sel.resetSelector("pre $sel.selector") }

    /** Progress indicator. */
    Selectable progress(Selectable sel) { sel.resetSelector("progress $sel.selector") }

    /** Quoted text. */
    Selectable q(Selectable sel) { sel.resetSelector("q $sel.selector") }

    /** Ruby parenthesis. */
    Selectable rp(Selectable sel) { sel.resetSelector("rp $sel.selector") }

    /** Ruby text. */
    Selectable rt(Selectable sel) { sel.resetSelector("rt $sel.selector") }

    /** Ruby annotation. */
    Selectable ruby(Selectable sel) { sel.resetSelector("ruby $sel.selector") }

    /** Struck text. */
    Selectable s(Selectable sel) { sel.resetSelector("s $sel.selector") }

    /** (sample) output. */
    Selectable samp(Selectable sel) { sel.resetSelector("samp $sel.selector") }

    /** Section. */
    Selectable section(Selectable sel) { sel.resetSelector("section $sel.selector") }

    /** Option-selection form control. */
    Selectable select(Selectable sel) { sel.resetSelector("select $sel.selector") }

    /** Small print. */
    Selectable small(Selectable sel) { sel.resetSelector("small $sel.selector") }

    /** Media source. */
    Selectable source(Selectable sel) { sel.resetSelector("source $sel.selector") }

    /** Generic span. */
    Selectable span(Selectable sel) { sel.resetSelector("span $sel.selector") }

    /** Strong importance. */
    Selectable strong(Selectable sel) { sel.resetSelector("strong $sel.selector") }

    /** Subscript. */
    Selectable sub(Selectable sel) { sel.resetSelector("sub $sel.selector") }

    /** Summary, caption, or legend for a details control. */
    Selectable summary(Selectable sel) { sel.resetSelector("summary $sel.selector") }

    /** Superscript. */
    Selectable sup(Selectable sel) { sel.resetSelector("sup $sel.selector") }

    /** Table. */
    Selectable table(Selectable sel) { sel.resetSelector("table $sel.selector") }

    /** Table row group. */
    Selectable tbody(Selectable sel) { sel.resetSelector("tbody $sel.selector") }

    /** Table cell. */
    Selectable td(Selectable sel) { sel.resetSelector("td $sel.selector") }

    /** Text input area. */
    Selectable textarea(Selectable sel) { sel.resetSelector("textarea $sel.selector") }

    /** Table footer row group. */
    Selectable tfoot(Selectable sel) { sel.resetSelector("tfoot $sel.selector") }

    /** Table header cell. */
    Selectable th(Selectable sel) { sel.resetSelector("th $sel.selector") }

    /** Table heading group. */
    Selectable thead(Selectable sel) { sel.resetSelector("thead $sel.selector") }

    /** Date and/or time. */
    Selectable time(Selectable sel) { sel.resetSelector("time $sel.selector") }

    /** Document title. */
    Selectable title(Selectable sel) { sel.resetSelector("title $sel.selector") }

    /** Table row. */
    Selectable tr(Selectable sel) { sel.resetSelector("tr $sel.selector") }

    /** Supplementary media track. */
    Selectable track(Selectable sel) { sel.resetSelector("track $sel.selector") }

    /** Offset text conventionally styled with an underline. */
    Selectable u(Selectable sel) { sel.resetSelector("u $sel.selector") }

    /** Unordered list. */
    Selectable ul(Selectable sel) { sel.resetSelector("ul $sel.selector") }

    /** Variable or placeholder text. */
    Selectable var(Selectable sel) { sel.resetSelector("var $sel.selector") }

    /** Video. */
    Selectable video(Selectable sel) { sel.resetSelector("video $sel.selector") }

    /** Line-break opportunity. */
    Selectable wbr(Selectable sel) { sel.resetSelector("wbr $sel.selector") }
    
    
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
    double toDegrees(Number angrad) { Measurement.toDegrees(angrad) }

    /**Converts an angle measured in degrees to an approximately equivalent angle measured in radians.*/
    double toRadians(Number angdeg) { Measurement.toRadians(angdeg) }

    //------------------------------------------------------------------> Units
    /** Returns units of a number. For example: em,px,mm,cm,ms,s. */
    String getUnit(value) {
        if (value instanceof Measurement) return value.unit
        def match = (value =~ /\d*\.?\d*(\w+)/)
        if(match.matches()) match?.group(1)
        else ''
    }

    /** Remove or change the unit of a dimension. */
    def unit(value, units = null) {
        if (units) {
            if (value instanceof Number) new Measurement(value, "$units")
            else "$value$units"
        }
        else {
            def match = (value =~ /(\d*\.?\d*)\w+/)
            if (match.matches()) {
                def num = match?.group(1)
                if (value ==~ /\d+/) num as Integer
                num as BigDecimal
            }
            else value
        }
    }

    /** Convert a number from one unit into another. Supports sizes (in,pt,pc,mm,cm,m), time (m,ms) and rad/deg. */
    def convert(value, units) {
        Number num = unit(value) as BigDecimal
        def conversion = getUnit(value) + "-$units"
        def converted = convertNum num, conversion

        "${stringify converted}$units"
    }

    /** Converts number to string in a sensible format. */
    static String stringify(Number converted) {
        converted.toString().contains("E") ? "${converted as Double}" : "$converted"
    }

    @TypeChecked
    Number convertNum(Number num, String conversion) {
        return Measurement.convertNum(num, conversion)
    }

    //------------------------------------------------------------------> Images
    Measurement getImageWidth(String filename) {
        def img = ImageIO.read new File(filename)
        new Measurement(img.width, 'px')
    }

    Measurement getImageHeight(String filename) {
        def img = ImageIO.read new File(filename)
        new Measurement(img.height, 'px')
    }

    String getImageSize(String filename) {
        def img = ImageIO.read new File(filename)
        "${img.width}px ${img.height}px"
    }

    //------------------------------------------------------------------> Elements
    Selector getMain() { newElement('main') }
    Selector getMath() { newElement('math') }
    Selector getSvg() { newElement('svg') }
    Selector getA() { newElement('a') }
    Selector getAbbr() { newElement('abbr') }
    Selector getAddress() { newElement('address') }
    Selector getArea() { newElement('area') }
    Selector getArticle() { newElement('article') }
    Selector getAside() { newElement('aside') }
    Selector getAudio() { newElement('audio') }
    Selector getB() { newElement('b') }
    Selector getBase() { newElement('base') }
    Selector getBdi() { newElement('bdi') }
    Selector getBdo() { newElement('bdo') }
    Selector getBlockquote() { newElement('blockquote') }
    Selector getBody() { newElement('body') }
    Selector getBr() { newElement('br') }
    Selector getButton() { newElement('button') }
    Selector getCanvas() { newElement('canvas') }
    Selector getCaption() { newElement('caption') }
    Selector getCite() { newElement('cite') }
    Selector getCode() { newElement('code') }
    Selector getCol() { newElement('col') }
    Selector getColgroup() { newElement('colgroup') }
    Selector getCommand() { newElement('command') }
    Selector getDatalist() { newElement('datalist') }
    Selector getDd() { newElement('dd') }
    Selector getDel() { newElement('del') }
    Selector getDetails() { newElement('details') }
    Selector getDfn() { newElement('dfn') }
    Selector getDiv() { newElement('div') }
    Selector getDl() { newElement('dl') }
    Selector getDt() { newElement('dt') }
    Selector getEm() { newElement('em') }
    Selector getEmbed() { newElement('embed') }
    Selector getFieldset() { newElement('fieldset') }
    Selector getFigcaption() { newElement('figcaption') }
    Selector getFigure() { newElement('figure') }
    Selector getFooter() { newElement('footer') }
    Selector getForm() { newElement('form') }
    Selector getH1() { newElement('h1') }
    Selector getH2() { newElement('h2') }
    Selector getH3() { newElement('h3') }
    Selector getH4() { newElement('h4') }
    Selector getH5() { newElement('h5') }
    Selector getH6() { newElement('h6') }
    Selector getHeader() { newElement('header') }
    Selector getHgroup() { newElement('hgroup') }
    Selector getHr() { newElement('hr') }
    Selector getHtml() { newElement('html') }
    Selector getI() { newElement('i') }
    Selector getIframe() { newElement('iframe') }
    Selector getImg() { newElement('img') }
    Selector getInput() { newElement('input') }
    Selector getIns() { newElement('ins') }
    Selector getKbd() { newElement('kbd') }
    Selector getKeygen() { newElement('keygen') }
    Selector getLabel() { newElement('label') }
    Selector getLegend() { newElement('legend') }
    Selector getLi() { newElement('li') }
    Selector getMap() { newElement('map') }
    Selector getMark() { newElement('mark') }
    Selector getMenu() { newElement('menu') }
    Selector getMeter() { newElement('meter') }
    Selector getNav() { newElement('nav') }
    Selector getNoscript() { newElement('noscript') }
    Selector getObject() { newElement('object') }
    Selector getOl() { newElement('ol') }
    Selector getOptgroup() { newElement('optgroup') }
    Selector getOption() { newElement('option') }
    Selector getOutput() { newElement('output') }
    Selector getP() { newElement('p') }
    Selector getParam() { newElement('param') }
    Selector getPre() { newElement('pre') }
    Selector getProgress() { newElement('progress') }
    Selector getQ() { newElement('q') }
    Selector getRp() { newElement('rp') }
    Selector getRt() { newElement('rt') }
    Selector getRuby() { newElement('ruby') }
    Selector getS() { newElement('s') }
    Selector getSamp() { newElement('samp') }
    Selector getScript() { newElement('script') }
    Selector getSection() { newElement('section') }
    Selector getSelect() { newElement('select') }
    Selector getSmall() { newElement('small') }
    Selector getSource() { newElement('source') }
    Selector getSpan() { newElement('span') }
    Selector getStrong() { newElement('strong') }
    Selector getStyle() { newElement('style') }
    Selector getSub() { newElement('sub') }
    Selector getSummary() { newElement('summary') }
    Selector getSup() { newElement('sup') }
    Selector getTable() { newElement('table') }
    Selector getTbody() { newElement('tbody') }
    Selector getTd() { newElement('td') }
    Selector getTextarea() { newElement('textarea') }
    Selector getTfoot() { newElement('tfoot') }
    Selector getTh() { newElement('th') }
    Selector getThead() { newElement('thead') }
    Selector getTime() { newElement('time') }
    Selector getTitle() { newElement('title') }
    Selector getTr() { newElement('tr') }
    Selector getTrack() { newElement('track') }
    Selector getU() { newElement('u') }
    Selector getUl() { newElement('ul') }
    Selector getVar() { newElement('var') }
    Selector getVideo() { newElement('video') }
    Selector getWbr() { newElement('wbr') }

    Selector newElement(String name) {
        new Selector(name, currentCss)
    }

    //------------------------------------------------------------------> Underscore
    MediaCSS get_() { currentCss }

    //---> Pseudo-classes

    /** Pseudo-class: :active. */
    PseudoClass.StyleGroup active(@DelegatesTo(StyleGroup) Closure closure) { withPseudoClass('active', closure) }

    /** Pseudo-class: :checked. */
    PseudoClass.StyleGroup checked(@DelegatesTo(StyleGroup) Closure closure) { withPseudoClass('checked', closure) }

    /** Pseudo-class: :default. */
    PseudoClass.StyleGroup defaultPC(@DelegatesTo(StyleGroup) Closure closure) { withPseudoClass('default', closure) }

    /** Pseudo-class: :disabled. */
    PseudoClass.StyleGroup disabled(@DelegatesTo(StyleGroup) Closure closure) { withPseudoClass('disabled', closure) }

    /** Pseudo-class: :empty. */
    PseudoClass.StyleGroup empty(@DelegatesTo(StyleGroup) Closure closure) { withPseudoClass('empty', closure) }

    /** Pseudo-class: :enabled. */
    PseudoClass.StyleGroup enabled(@DelegatesTo(StyleGroup) Closure closure) { withPseudoClass('enabled', closure) }

    /** Pseudo-class: :first-child. */
    PseudoClass.StyleGroup firstChild(@DelegatesTo(StyleGroup) Closure closure) { withPseudoClass('first-child', closure) }

    /** Pseudo-class: :first-of-type. */
    PseudoClass.StyleGroup firstOfType(@DelegatesTo(StyleGroup) Closure closure) { withPseudoClass('first-of-type', closure) }

    /** Pseudo-class: :focus. */
    PseudoClass.StyleGroup focus(@DelegatesTo(StyleGroup) Closure closure) { withPseudoClass('focus', closure) }

    /** Pseudo-class: :hover. */
    PseudoClass.StyleGroup hover(@DelegatesTo(StyleGroup) Closure closure) { withPseudoClass('hover', closure) }

    /** Pseudo-class: :indeterminate. */
    PseudoClass.StyleGroup indeterminate(@DelegatesTo(StyleGroup) Closure closure) { withPseudoClass('indeterminate', closure) }

    /** Pseudo-class: :in-range. */
    PseudoClass.StyleGroup inRange(@DelegatesTo(StyleGroup) Closure closure) { withPseudoClass('in-range', closure) }

    /** Pseudo-class: :invalid. */
    PseudoClass.StyleGroup invalid(@DelegatesTo(StyleGroup) Closure closure) { withPseudoClass('invalid', closure) }

    /** Pseudo-class: :lang. */
    PseudoClass.StyleGroup lang(languageCode, @DelegatesTo(StyleGroup) Closure clos) { withPseudoClass "lang($languageCode)", clos }

    /** Pseudo-class: :last-child. */
    PseudoClass.StyleGroup lastChild(@DelegatesTo(StyleGroup) Closure closure) { withPseudoClass('last-child', closure) }

    /** Pseudo-class: :last-of-type. */
    PseudoClass.StyleGroup lastOfType(@DelegatesTo(StyleGroup) Closure closure) { withPseudoClass('last-of-type', closure) }

    /** Pseudo-class: :link. */
    PseudoClass.StyleGroup linkPseudoClass(@DelegatesTo(StyleGroup) Closure closure) { withPseudoClass('link', closure) }

    /** Pseudo-class: :not(@DelegatesTo(StyleGroup) Closure closure). */
    PseudoClass.StyleGroup not(notStyleGroup, @DelegatesTo(StyleGroup) Closure clos) { withPseudoClass "not($notStyleGroup)", clos }

    /** Pseudo-class: :nth-child. */
    PseudoClass.StyleGroup nthChild(n, @DelegatesTo(StyleGroup) Closure closure) { withPseudoClass "nth-child($n)", closure }

    /** Pseudo-class: :nth-last-child. */
    PseudoClass.StyleGroup nthLastChild(n, @DelegatesTo(StyleGroup) Closure clos) { withPseudoClass "nth-last-child($n)", clos }

    /** Pseudo-class: :nth-last-of-type. */
    PseudoClass.StyleGroup nthLastOfType(n, @DelegatesTo(StyleGroup) Closure clos) { withPseudoClass "nth-last-of-type($n)", clos }

    /** Pseudo-class: :nth-of-type. */
    PseudoClass.StyleGroup nthOfType(n, @DelegatesTo(StyleGroup) Closure closure) { withPseudoClass "nth-of-type($n)", closure }

    /** Pseudo-class: "nth-child(odd)". */
    PseudoClass.StyleGroup odd(@DelegatesTo(StyleGroup) Closure closure) { withPseudoClass "nth-child(odd)", closure }

    /** Pseudo-class: "nth-child(even)". */
    PseudoClass.StyleGroup even(@DelegatesTo(StyleGroup) Closure closure) { withPseudoClass "nth-child(even)", closure }

    /** Pseudo-class: :only-child. */
    PseudoClass.StyleGroup onlyChild(@DelegatesTo(StyleGroup) Closure closure) { withPseudoClass('only-child', closure) }

    /** Pseudo-class: :only-of-type. */
    PseudoClass.StyleGroup onlyOfType(@DelegatesTo(StyleGroup) Closure closure) { withPseudoClass('only-of-type', closure) }

    /** Pseudo-class: :optional. */
    PseudoClass.StyleGroup optional(@DelegatesTo(StyleGroup) Closure closure) { withPseudoClass('optional', closure) }

    /** Pseudo-class: :out-of-range. */
    PseudoClass.StyleGroup outOfRange(@DelegatesTo(StyleGroup) Closure closure) { withPseudoClass('out-of-range', closure) }

    /** Pseudo-class: :read-only. */
    PseudoClass.StyleGroup readOnly(@DelegatesTo(StyleGroup) Closure closure) { withPseudoClass('read-only', closure) }

    /** Pseudo-class: :read-write. */
    PseudoClass.StyleGroup readWrite(@DelegatesTo(StyleGroup) Closure closure) { withPseudoClass('read-write', closure) }

    /** Pseudo-class: :required. */
    PseudoClass.StyleGroup required(@DelegatesTo(StyleGroup) Closure closure) { withPseudoClass('required', closure) }

    /** Pseudo-class: :root. */
    PseudoClass.StyleGroup root(@DelegatesTo(StyleGroup) Closure closure) { withPseudoClass('root', closure) }

    /** Pseudo-class: :target. */
    PseudoClass.StyleGroup target(@DelegatesTo(StyleGroup) Closure closure) { withPseudoClass('target', closure) }

    /** Pseudo-class: :valid. */
    PseudoClass.StyleGroup valid(@DelegatesTo(StyleGroup) Closure closure) { withPseudoClass('valid', closure) }

    /** Pseudo-class: :visited. */
    PseudoClass.StyleGroup visited(@DelegatesTo(StyleGroup) Closure closure) { withPseudoClass('visited', closure) }

    @TypeChecked
    PseudoClass.StyleGroup withPseudoClass(String pseudoClass, @DelegatesTo(StyleGroup) Closure closure) {
        def sg = new PseudoClass.StyleGroup(":$pseudoClass", config, currentCss)
        closure.delegate = sg
        closure(sg)
        currentCss.add sg
        sg
    }
    
    /** Pseudo-class: :active. */
    PseudoClass getActive() { newPseudoClass('active') }

    /** Pseudo-class: :checked. */
    PseudoClass getChecked() { newPseudoClass('checked') }

    /** Pseudo-class: :default. */
    PseudoClass getDefault() { newPseudoClass('default') }

    /** Pseudo-class: :disabled. */
    PseudoClass getDisabled() { newPseudoClass('disabled') }

    /** Pseudo-class: :empty. */
    PseudoClass getEmpty() { newPseudoClass('empty') }

    /** Pseudo-class: :enabled. */
    PseudoClass getEnabled() { newPseudoClass('enabled') }

    /** Pseudo-class: :first-child. */
    PseudoClass getFirstChild() { newPseudoClass('first-child') }

    /** Pseudo-class: :first-of-type. */
    PseudoClass getFirstOfType() { newPseudoClass('first-of-type') }

    /** Pseudo-class: :focus. */
    PseudoClass getFocus() { newPseudoClass('focus') }

    /** Pseudo-class: :hover. */
    PseudoClass getHover() { newPseudoClass('hover') }

    /** Pseudo-class: :indeterminate. */
    PseudoClass getIndeterminate() { newPseudoClass('indeterminate') }

    /** Pseudo-class: :in-range. */
    PseudoClass getInRange() { newPseudoClass('in-range') }

    /** Pseudo-class: :invalid. */
    PseudoClass getInvalid() { newPseudoClass('invalid') }

    /** Pseudo-class: :lang. */
    PseudoClass lang(languageCode) { newPseudoClass "lang($languageCode)" }

    /** Pseudo-class: :last-child. */
    PseudoClass getLastChild() { newPseudoClass('last-child') }

    /** Pseudo-class: :last-of-type. */
    PseudoClass getLastOfType() { newPseudoClass('last-of-type') }

    /** Pseudo-class: :link. */
    PseudoClass getLink() { newPseudoClass('link') }

    /** Pseudo-class: :not(). */
    PseudoClass not(notStyleGroup) { newPseudoClass "not($notStyleGroup)" }

    /** Pseudo-class: :nth-child. */
    PseudoClass nthChild(n) { newPseudoClass "nth-child($n)" }

    /** Pseudo-class: :nth-child(odd). */
    PseudoClass getOdd() { newPseudoClass "nth-child(odd)" }

    /** Pseudo-class: :nth-child(even). */
    PseudoClass getEven() { newPseudoClass "nth-child(even)" }

    /** Pseudo-class: :nth-last-child. */
    PseudoClass nthLastChild(n) { newPseudoClass "nth-last-child($n)" }

    /** Pseudo-class: :nth-last-of-type. */
    PseudoClass nthLastOfType(n) { newPseudoClass "nth-last-of-type($n)" }

    /** Pseudo-class: :nth-of-type. */
    PseudoClass nthOfType(n) { newPseudoClass "nth-of-type($n)" }

    /** Pseudo-class: :only-child. */
    PseudoClass getOnlyChild() { newPseudoClass('only-child') }

    /** Pseudo-class: :only-of-type. */
    PseudoClass getOnlyOfType() { newPseudoClass('only-of-type') }

    /** Pseudo-class: :optional. */
    PseudoClass getOptional() { newPseudoClass('optional') }

    /** Pseudo-class: :out-of-range. */
    PseudoClass getOutOfRange() { newPseudoClass('out-of-range') }

    /** Pseudo-class: :read-only. */
    PseudoClass getReadOnly() { newPseudoClass('read-only') }

    /** Pseudo-class: :read-write. */
    PseudoClass getReadWrite() { newPseudoClass('read-write') }

    /** Pseudo-class: :required. */
    PseudoClass getRequired() { newPseudoClass('required') }

    /** Pseudo-class: :root. */
    PseudoClass getRoot() { newPseudoClass('root') }

    /** Pseudo-class: :target. */
    PseudoClass getTarget() { newPseudoClass('target') }

    /** Pseudo-class: :valid. */
    PseudoClass getValid() { newPseudoClass('valid') }

    /** Pseudo-class: :visited. */
    PseudoClass getVisited() { newPseudoClass('visited') }

    PseudoClass newPseudoClass(String value) {
        new PseudoClass(value)
    }

    Raw raw(String raw) {
        def r = new Raw(raw)
        currentCss << r
        r
    }

    /** Adds a comment to be included in output. */
    Comment comment(String comment) {
        def com = new Comment(comment)
        currentCss << com
        com
    }

    /** Imports given Groocss file at filename. */
    MediaCSS importFile(Map params = [:], String filename) { importReader params, new File(filename).newReader() }

    /** Imports given Groocss file. */
    MediaCSS importFile(Map params = [:], File file) { importReader params, file.newReader() }

    /** Imports given Groocss. */
    MediaCSS importString(Map params = [:], String groocss) { importReader(params, new StringReader(groocss)) }

    /** Imports given Groocss input using given Reader. */
    MediaCSS importReader(Map params = [:], Reader reader) {
        def shell = makeShell()
        def script = shell.parse(reader)
        def binding = new Binding()
        params.each { binding.setVariable(it.key, it.value) }
        script.binding = binding
        script.invokeMethod('setConfig', css.config)
        script.run()
        MediaCSS other = (MediaCSS) script.getProperty('css')
        currentCss.add other
    }

    /** Imports given Groocss file using given InputStream. */
    MediaCSS importStream(Map params = [:], InputStream stream) {
        importReader params, new InputStreamReader(stream)
    }

}
