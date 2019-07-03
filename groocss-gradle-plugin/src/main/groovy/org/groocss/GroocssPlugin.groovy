/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.groocss

import groovy.transform.*
import org.gradle.api.Plugin
import org.gradle.api.Project

class GroocssPlugin implements Plugin<Project> {

    public static final String GROUP = "org.groocss"

    @TypeChecked
    void apply(Project project) {

        project.extensions.create("groocss", GroocssExtension)

        def cssFiles = project.container(GrooCssFile)
        project.extensions.add('groocssfiles', cssFiles)

        def buildTask = project.tasks.findByName("build")
        def processTask = project.tasks.findByName("processResources")
        def extension = project.extensions.getByType(GroocssExtension)

        def convertFile = {File inFile, File out ->
            Config config = new Config(extension.properties)
            GrooCSS.convert(config, inFile, out, extension.charset ?: 'UTF-8', true)
        }

        def convertCss = project.task("convertCss")
        convertCss.doFirst {
            cssFiles.each { GrooCssFile cssFile ->
                if (cssFile.inFile.file) convertFile(cssFile.inFile, cssFile.outFile)
                else if (cssFile.inFile.isDirectory()) {
                    cssFile.outFile.mkdirs()
                    cssFile.inFile.listFiles(
                        { File f -> f.name.endsWith('.groovy') || f.name.endsWith('.groocss') } as FileFilter
                    ).each {
                        File file -> convertFile file, new File(cssFile.outFile, toCssName(file.name))
                    }
                }
            }
        }
        buildTask?.dependsOn convertCss
        processTask?.dependsOn convertCss
    }

    static String toCssName(String name) {
        name.replace('.css.groovy', '.css').replace('.groovy', '.css').replace('.groocss', '.css')
    }

}

