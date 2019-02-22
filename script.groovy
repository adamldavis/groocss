@Grab('org.groocss:groocss:0.12')
import org.groocss.GrooCSS

def css = GrooCSS.withConfig { prettyPrint() }.process {
    a { textDecoration 'none' }
    body _.content {
        fontSize 20.px
        width 400.px
        display 'flex'
    }
}
new File('main.css').text = "$css"
