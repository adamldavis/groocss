package org.groocss

import org.codehaus.groovy.control.CompilerConfiguration

import groovy.transform.*

@CompileStatic
class GrooCSS extends Script {
    
    static class Wrapper {
        String name
        List<StyleGroup> groups = []
        List<Wrapper> children = []
        void leftShift(StyleGroup sg) { groups << sg }
        void leftShift(Wrapper w) { children << w }
        String toString() {
            if (name) "$name {\n ${groups.join('\n')} \n}"
            else groups.join('\n')
        }

        Wrapper sel(String selector, @DelegatesTo(StyleGroup) Closure clos) {
            StyleGroup sg = new StyleGroup(selector: selector)
            clos.delegate = sg
            clos()
            this << sg
            this
        }

        Wrapper sg(String selector, @DelegatesTo(StyleGroup) Closure clos) {
            sel(selector, clos)
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

    public String toString() { css.toString() }

    Wrapper media(String spec, @DelegatesTo(Wrapper) Closure clos) {
        wrap("@media $spec", clos)
    }

    Wrapper keyframes(String name, @DelegatesTo(Wrapper) Closure clos) {
        wrap("@keyframes $name", clos)
    }

    Wrapper wrap(String spec, @DelegatesTo(Wrapper) Closure clos) {
        Wrapper wrapper = new Wrapper()
        clos.delegate = wrapper
        clos()
        css << wrapper
        wrapper
    }

    Wrapper sel(String selector, @DelegatesTo(StyleGroup) Closure clos) {
        css.sel(selector, clos)
    }

    Wrapper sg(String selector, @DelegatesTo(StyleGroup) Closure clos) {
        css.sel(selector, clos)
    }
    
    Style style(@DelegatesTo(Style) Closure clos) {
        Style s = new Style()
        clos.delegate = s
        clos()
        s
    }
    
    def run() {}

    static GrooCSS runBlock(@DelegatesTo(GrooCSS) Closure clos) {
        GrooCSS gcss = new GrooCSS()
        clos.delegate = gcss
        clos()
        gcss
    }
   
}

