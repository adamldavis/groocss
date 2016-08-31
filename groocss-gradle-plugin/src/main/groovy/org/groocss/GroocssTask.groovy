package org.groocss

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class GroocssTask extends DefaultTask {

    Config conf
    File inFile
    File outFile

    /** Converts the given GrooCSS file with current config, and outputs to given file. */
    @TaskAction
    def convert() {
        if (!conf) {
            conf = new Config()
        }
        GrooCSS.convert(conf, inFile, outFile)
    }

}
