package org.groocss

import spock.lang.Specification

class GroocssExtensionSpec extends Specification {

    def "should have basic properties" () {
        expect:
        def ext = new GroocssExtension(compress: true, prettyPrint: true)
        ext.compress
        ext.prettyPrint
        ext.addMoz
        ext.addMs
        ext.addOpera
        ext.addWebkit
    }

    def "should have charset default of null" () {
        expect:
        def ext = new GroocssExtension()
        ext.charset == null
    }

    def "should have processors which is empty list by default" () {
        expect:
        def ext = new GroocssExtension()
        ext.processors.empty
        ext.processors instanceof Set
    }
}
