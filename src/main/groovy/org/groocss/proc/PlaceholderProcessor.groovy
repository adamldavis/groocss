package org.groocss.proc

import groovy.transform.CompileStatic
import org.groocss.MediaCSS
import org.groocss.StyleGroup

/**
 * Finds any StyleGroup with placeholder in it and adds a copy of it with -webkit-input- prefix added to placeholder.
 */
@CompileStatic
class PlaceholderProcessor implements Processor<MediaCSS> {

    static final String webkitInputPrefix = '-webkit-input-'
    static final String placeholder = 'placeholder'

    @Override
    Optional<String> process(MediaCSS cssPart, Processor.Phase phase) {

        if (phase == Processor.Phase.POST_VALIDATE) {
            cssPart.groups.findAll {
                it instanceof StyleGroup && ((StyleGroup) it).selector.contains('placeholder')
            }.each {
                println "WARNING: $placeholder not supported by IE"
                def sg = (StyleGroup) it
                def selector = sg.selector
                def newSelector = selector.replace(placeholder, webkitInputPrefix + placeholder)

                def newSg = new StyleGroup(newSelector, sg.config, sg.owner)

                newSg.styleList.addAll sg.styleList

                cssPart.groups << newSg
            }
        }
        return Optional.empty()
    }
}
