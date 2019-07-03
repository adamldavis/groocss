package org.groocss

import org.gradle.api.file.CopySpec
import org.gradle.api.file.RelativePath
import org.gradle.api.internal.file.copy.FileCopyDetailsInternal
import org.gradle.internal.file.PathToFileResolver
import spock.lang.Specification
import spock.lang.Unroll

class GroocssTaskSpec extends Specification {

    @Unroll
    def "should convert regular groocss file #i"() {
        given:
        def resolver = Mock(PathToFileResolver)
        def copySpec = Mock(CopySpec)
        def details = Mock(FileCopyDetailsInternal)
        def path = Mock(RelativePath)
        def config = new Config().noExts().compress()
        def target = File.createTempFile("test", ".css.groovy")
        File newTarget = new File(target.parentFile, GroocssPlugin.toCssName(target.name))
        def testFile = File.createTempFile("input", ".css.groovy")
        when:
        testFile.text = groocss
        new GroocssTask.GroocssFileAction(resolver, config, copySpec).processFile(details)
        then:
        details.getRelativePath() >> { path }
        resolver.resolve(_) >> { target }
        details.copyTo(_) >> { target.text = testFile.text }

        assert newTarget.isFile()
        println(newTarget.text)
        assert newTarget.text == css
        where:
        i || groocss || css
        1 | '''import org.groocss.Config        
            'test'.groocss(new Config().noExts().compress()) {
            
                body { fontSize 2.em color 0.color }
            
                article { padding 2.em }
            
                'thing'.id { fontSize 200.percent }
            
                keyframes('test') {
                    from { color black } to { color red }
                }
            
            }
            ''' | "body{font-size: 2em;color: #000000;}article{padding: 2em;}#thing{font-size: 200%;}"+
                    "@keyframes test {from{color: Black;}to{color: Red;}}"
        2 | '''
            body { fontSize 2.em color 0.color }
        
            article { padding 2.em }
        
            'thing'.id { fontSize 200.percent }
        
            'test'.kf {
                from { color black } to { color red }
            }
            ''' | "body{font-size: 2em;color: #000000;}article{padding: 2em;}#thing{font-size: 200%;}"+
                "@keyframes test {from{color: Black;}to{color: Red;}}"
        3 | 'assert 1.initMetaClassesCalled()' | ''
    }

    def "should have default charset"() {
        given:
        def resolver = Mock(PathToFileResolver)
        def copySpec = Mock(CopySpec)
        def config = new Config()
        expect:
        def action = new GroocssTask.GroocssFileAction(resolver, config, copySpec)
        action.charset == 'UTF-8'
    }


}
