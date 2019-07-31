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

import groovy.transform.Canonical
import groovy.transform.CompileStatic
import groovy.transform.builder.Builder
import org.groocss.proc.Processor

/**
 * Configuration for GrooCSS conversions. There are at least four different ways to configure GrooCSS:
 *
 * <li>Using the groovy constructor: new Config(compress: true)
 * <li>Using the builder syntax: Config.builder().compress(true).build()
 * <li>Using the DSL: GrooCSS.withConfig { noExts().compress().utf8() }...
 * <li>Using StringExtension with config use: 'main.css'.groocss(new Config()) { ... }.
 *
 * @see GrooCSS
 * @see org.groocss.ext.StringExtension
 */
@Canonical
@Builder
@CompileStatic
class Config {

    boolean addWebkit = true,
            addMs = true,
            addOpera = true,
            addMoz = true,
            prettyPrint = false,
            compress = false

    String charset = null

    /** Element-names that you only want to use as CSS classes. */
    final Set<String> styleClasses = []

    /** Whether or not convert under-scores in CSS classes into dashes (main_content becomes main-content).
     * Default is false. */
    boolean convertUnderline = false

    /** Custom processors/validators to use.
     * @see Processor
     * @see org.groocss.valid.DefaultValidator
     */
    final Collection<Processor> processors = []

    /** Variables to make available in the processed GrooCSS files.*/
    final Map<String, Object> variables = [:]

    Config() {}
    
    Config(Map map) {
        if (map.variables) variables.putAll(map.variables as Map<String, Object>)
        if (map.processors) processors.addAll(map.processors as Collection<Processor>)
        if (map.styleClasses) styleClasses.addAll(map.styleClasses as Collection<String>)
        if (map.convertUnderline instanceof Boolean) convertUnderline = map.convertUnderline
        if (map.addMs instanceof Boolean) addMs = map.addMs
        if (map.addMoz instanceof Boolean) addMoz = map.addMoz
        if (map.addWebkit instanceof Boolean) addWebkit = map.addWebkit
        if (map.addOpera instanceof Boolean) addOpera = map.addOpera
        if (map.prettyPrint instanceof Boolean) prettyPrint = map.prettyPrint
        if (map.compress instanceof Boolean) compress = map.compress
        if (map.charset) charset = map.charset as String
    }

    /** Loads the Config from the given Properties. Lists use comma separated values and boolean
     * values consider "true" true and everything else is false. Variables can be defined with the "variable." prefix.
     * The "processors" property should be a comma-separated list of full class names.
     * For example: processors=org.groocss.valid.RequireMeasurements.
     **/
    Config(Properties props) {
        loadFrom(props)
    }

    protected void loadFrom(Properties props) {
        props.propertyNames().findAll { ((String) it).startsWith('variable.') }.each { key ->
            def name = (key as String).substring('variable.'.length())
            variables.put(name, props.getProperty(key as String))
        }
        if (props.processors) withProcessorClasses(props.getProperty('processors')
                .split(/,/).collect { Class.forName(it as String) } as List<Class<? extends Processor>>)
        if (props.styleClasses) withStyleClasses(props.getProperty('styleClasses').split(/,/).collect{ it } as List<String>)
        if (props.convertUnderline != null) convertUnderline = truthy props.convertUnderline
        if (props.addMs != null) addMs = truthy props.addMs
        if (props.addMoz != null) addMoz = truthy props.addMoz
        if (props.addWebkit != null) addWebkit = truthy props.addWebkit
        if (props.addOpera != null) addOpera = truthy props.addOpera
        if (props.prettyPrint != null) prettyPrint = truthy props.prettyPrint
        if (props.compress != null) compress = truthy props.compress
        if (props.charset) charset = props.charset as String
    }

    /** Loads the Config from the given Properties File. */
    Config(File file) {
        file.withInputStream { stream ->
            Properties props = new Properties()
            props.load stream
            loadFrom(props)
        }
    }

    /** Loads the Config from the given Properties File with given path. */
    Config(String filePath) {
        this(new File(filePath))
    }

    private boolean truthy(value) {
        true == value || (String.valueOf(value) ==~ /TRUE|true|t|T|Y|y|yes|YES/)
    }

    /** If given map is not empty, adds to variables. */
    void setVariables(Map<String, Object> variables) {
        if (variables) this.variables.putAll variables
    }
    /** If given collection is not empty, adds to processors. */
    void setProcessors(Collection<Processor> list) {
        if (list) processors.addAll(list)
    }
    /** If given Set is not empty, adds to styleClasses. */
    void setStyleClasses(Set<String> set) {
        if (set) styleClasses.addAll(set)
    }

    Config withVariables(Map<String, Object> variables) {
        this.variables.putAll variables
        this
    }

    Config withProcessors(Collection<Processor> list) {
        processors.addAll(list)
        this
    }

    /** Takes a Collection of Classes which must extend Processor and have a public no-args constructor. */
    Config withProcessorClasses(Collection<Class<? extends Processor>> classes) {
        classes.collect { it.newInstance() }.each(processors.&add)
        this
    }

    /** Element-names that you only want to use as CSS classes. */
    Config withStyleClasses(Collection<String> list) {
        styleClasses.addAll list
        this
    }

    /** Add one Element-name, like 'link', that you only want to use as CSS class. */
    Config useAsClass(String name) {
        styleClasses.add name
        this
    }
    /** Add Element-names, like 'link', that you only want to use as CSS classes. */
    Config useAsClasses(Collection<String> classes) {
        styleClasses.addAll classes
        this
    }

    /** Set the convertUnderline flag to true. */
    Config convertUnderline() {
        convertUnderline = true
        this
    }

    /** Sets the compress flag to true. */
    Config compress() {
        compress = true
        this
    }

    /** Sets the prettyPrint flag to true. */
    Config prettyPrint() {
        prettyPrint = true
        this
    }

    /** Sets all extension adding flags to false. */
    Config noExts() {
        addWebkit = addMs = addOpera = addMoz = false
        this
    }

    /** Sets all extension adding flags to false except webkit. */
    Config onlyWebkit() {
        noExts(); addWebkit = true
        this
    }

    /** Sets all extension adding flags to false except ms. */
    Config onlyMs() {
        noExts(); addMs = true
        this
    }

    /** Sets all extension adding flags to false except opera. */
    Config onlyOpera() {
        noExts(); addOpera = true
        this
    }

    /** Sets all extension adding flags to false except moz. */
    Config onlyMoz() {
        noExts(); addMoz = true
        this
    }

    /** Sets the charset to UTF-8. */
    Config utf8() {
        charset = 'UTF-8'
        this
    }
}