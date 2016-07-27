package org.groocss

import org.codehaus.groovy.control.CompilerConfiguration

import groovy.transform.*

@CompileStatic
class GrooCSS extends Script {
    
    static class CSS {
        List groups = []
        def leftShift(StyleGroup sg) { groups << sg }
        String toString() { groups.join('\n') }
    }

    static void convert(String inFilename, String outFilename) {
        convert(new File(inFilename), new File(outFilename))
    }
    
    static void convert(File inf, File out) {
        def binding = new Binding()
        def config = new CompilerConfiguration()
        config.scriptBaseClass = 'org.groocss.GrooCSS'
        def shell = new GroovyShell(this.class.classLoader, binding, config)
        
        CSS css = (CSS) shell.evaluate(inf)
        
        out.withPrintWriter { pw ->
            css.groups.each { pw.println it }
        }
    }
    
    static void main(String ... args) {
        if (args.length == 1)
            convert(args[0], args[0].replace('.groocss', '.css'))
    }
    
    CSS css = new CSS()

    public String toString() { css.toString() }

    CSS sel(String selector, @DelegatesTo(StyleGroup) Closure clos) {
        StyleGroup sg = new StyleGroup(selector: selector)
        clos.delegate = sg
        clos()
        css << sg
        css
    }
    
    CSS sg(String selector, @DelegatesTo(StyleGroup) Closure clos) {
        sel(selector, clos)
    }
    
    CSS css(String selector, @DelegatesTo(StyleGroup) Closure clos) {
        sel(selector, clos)
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

