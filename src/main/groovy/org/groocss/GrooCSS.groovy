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
        List<StyleGroup> groups = []
        List<KeyFrames> kfs = []
        void leftShift(StyleGroup sg) { groups << sg }
        void leftShift(KeyFrames kf) { kfs << kf }
        String toString() {
            String str = ''
            if (name) str += "$name {\n${groups.join('\n')}\n}"
            else str += groups.join('\n')
            if (kfs) str += kfs.join('\n')
            str
        }
    }

    static class KeyFrames {
        String name
        List<StyleGroup> groups = []
        Config config

        void leftShift(StyleGroup sg) { groups << sg }
        String toString() {
            if (name) "@keyframes $name {\n${groups.join('\n')}\n}" +
                    (config.addWebkit ? "@-webkit-keyframes $name {\n${groups.join('\n')}\n}" : '')
            else groups.join('\n')
        }

        KeyFrames frame(int percent, @DelegatesTo(StyleGroup) Closure clos) {
            frame([percent], clos)
        }

        KeyFrames frame(List<Integer> percents, @DelegatesTo(StyleGroup) Closure clos) {
            StyleGroup sg = new StyleGroup(selector: percents.collect{"${it}%"}.join(", "), config: config)
            clos.delegate = sg
            clos()
            this << sg
            this
        }
        KeyFrames from(@DelegatesTo(StyleGroup) Closure clos) {
            StyleGroup sg = new StyleGroup(selector: "from", config: config)
            clos.delegate = sg
            clos()
            this << sg
            this
        }
        KeyFrames to(@DelegatesTo(StyleGroup) Closure clos) {
            StyleGroup sg = new StyleGroup(selector: "to", config: config)
            clos.delegate = sg
            clos()
            this << sg
            this
        }
    }

    static void convert(String inFilename, String outFilename) {
        convert(new File(inFilename), new File(outFilename))
    }
    
    static void convert(File inf, File out) {
        def binding = new Binding()
        def config = new CompilerConfiguration()
        config.scriptBaseClass = 'org.groocss.GrooCSS'
        def shell = new GroovyShell(this.class.classLoader, binding, config)
        
        Wrapper css = (Wrapper) shell.evaluate(inf)
        
        out.withPrintWriter { pw ->
            css.groups.each { pw.println it }
        }
    }
    
    static void main(String ... args) {
        if (args.length == 1)
            convert(args[0], args[0].replace('.groocss', '.css'))
    }
    
    Wrapper css = new Wrapper()
    Config config

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

    /**
     * Creates a new {@link org.groocss.Color} object.
     * @param colorStr e.g. "#123456"
     * @return A Color object.
     */
    Color c(String colorStr) {
        new Color(colorStr)
    }
    
    def run() {}

    /** Processes the given closure with given optional config. */
    static GrooCSS process(Config config = new Config(), @DelegatesTo(GrooCSS) Closure clos) {
        runBlock(config, clos)
    }

    /** Processes the given closure with given optional config. */
    static GrooCSS runBlock(Config config = new Config(), @DelegatesTo(GrooCSS) Closure clos) {
        GrooCSS gcss = new GrooCSS(config: config)
        clos.delegate = gcss
        clos()
        gcss
    }
   
}

