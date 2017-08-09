package org.groocss

/**
 * Created by adavis on 8/9/17.
 */
class Main {

    static void main(String ... args) {
        if (args.length == 1) {
            def f = args[0]
            GrooCSS.convertFile(f,
                    f.replace('.css.groovy', '.css').replace('.groocss', '.css'))
        } else
            println "Usage: org.groocss.Main <groocss_file>"
    }
}
