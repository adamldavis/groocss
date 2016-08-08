package org.groocss

import org.codehaus.groovy.control.CompilerConfiguration

import groovy.transform.*

/**
 * Entrance to DSL for converting code into CSS.
 */
@CompileStatic
class GrooCSS extends Script {
    
    static class Wrapper {
        String name
        List<FontFace> fonts = []
        List<StyleGroup> groups = []
        List<KeyFrames> kfs = []

        void leftShift(StyleGroup sg) { add sg }
        void leftShift(KeyFrames kf) { add kf }
        void add(StyleGroup sg) { groups << sg }
        void add(KeyFrames kf) { kfs << kf }
        void leftShift(FontFace ff) { add ff }
        void add(FontFace ff) { fonts << ff }

        String toString() {
            String str = ''
            if (fonts) str += fonts.join('\n') + '\n'
            if (name) str += "$name {\n${groups.join('\n')}\n}"
            else str += groups.join('\n')
            if (kfs) str += kfs.join('\n')
            str
        }

        void writeTo(PrintWriter writer) {
            if (fonts) writer.write (fonts.join('\n') + '\n')
            if (name) writer.write "$name {\n${groups.join('\n')}\n}"
            else writer.write (groups.join('\n'))
            if (kfs) writer.write (kfs.join('\n'))
        }
    }

    static void convert(Config conf = new Config(), String inFilename, String outFilename) {
        convert(conf, new File(inFilename), new File(outFilename))
    }
    
    static void convert(Config conf = new Config(), File inf, File out) {
        def binding = new Binding()
        def config = new CompilerConfiguration()
        config.scriptBaseClass = 'org.groocss.GrooCSS'
        GrooCSS.config = conf
        def shell = new GroovyShell(this.class.classLoader, binding, config)
        
        Wrapper css = (Wrapper) shell.evaluate(inf)

        out.withPrintWriter { pw -> css.writeTo(pw) }
    }
    
    static void main(String ... args) {
        if (args.length == 1)
            convert(args[0], args[0].replace('.groocss', '.css'))
    }
    
    Wrapper css = new Wrapper()
    static Config config

    public String toString() { css.toString() }

    /** Calls {@link #kf(java.lang.String, groovy.lang.Closure)}. */
    Wrapper keyframes(String name, @DelegatesTo(KeyFrames) Closure clos) {
        kf(name, clos)
    }

    /** Creates a new KeyFrames element and runs given closure on it. */
    Wrapper kf(String name, @DelegatesTo(KeyFrames) Closure clos) {
        KeyFrames frames = new KeyFrames(name: name, config: config)
        clos.delegate = frames
        clos()
        css << frames
        css
    }

    /** Creates a new StyleGroup element and runs given closure on it. */
    Wrapper sel(String selector, @DelegatesTo(StyleGroup) Closure clos) {
        StyleGroup sg = new StyleGroup(selector: selector, config: config)
        clos.delegate = sg
        clos()
        css << sg
        css
    }

    /** Creates a new @font-face element and runs given closure on it. */
    Wrapper fontFace(@DelegatesTo(FontFace) Closure clos) {
        FontFace ff = new FontFace()
        clos.delegate = ff
        clos()
        css.add ff
        css
    }

    /** Creates a new StyleGroup element and runs given closure on it. */
    Wrapper sg(String selector, @DelegatesTo(StyleGroup) Closure clos) {
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

    Color clr(String colorStr) { c(colorStr) }
    
    def run() {}

    /** Processes the given closure with given optional config. */
    static GrooCSS process(Config config = new Config(), @DelegatesTo(GrooCSS) Closure clos) {
        runBlock(config, clos)
    }

    /** Processes the given closure with given optional config. */
    static GrooCSS runBlock(Config conf = new Config(), @DelegatesTo(GrooCSS) Closure clos) {
        GrooCSS gcss = new GrooCSS()
        GrooCSS.config = conf
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
   
}

