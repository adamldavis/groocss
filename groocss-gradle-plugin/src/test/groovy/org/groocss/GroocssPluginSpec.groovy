package org.groocss

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.tasks.TaskContainer
import spock.lang.Specification

class GroocssPluginSpec extends Specification {


    def "GroocssPlugin should work as a plugin" () {
        given:
        def project = Mock(Project.class)
        def extensions = Mock(ExtensionContainer)
        def files = Mock(NamedDomainObjectContainer)
        def tasks = Mock(TaskContainer)
        def convertCss = Mock(Task)
        when:
        def p = new GroocssPlugin()

        p.apply(project)
        then:
        project.getExtensions() >> { extensions }
        1 * extensions.create("groocss", GroocssExtension)
        project.container(GrooCssFile) >> files
        project.getTasks() >> { tasks }
        1* tasks.findByName("build")
        1* tasks.findByName("processResources")
        project.task("convertCss") >> { convertCss }
        1* convertCss.doFirst(_)
    }

}
