@Grab('org.groocss:groocss:1.0-M3')
import org.groocss.GrooCSS

def css = GrooCSS.withConfig { prettyPrint() }.process {
    a { textDecoration 'none' }
    body _.content {
        fontSize 20.deg
        width 400.px
        display 'flex'
    }
}
new File('main.css').text = "$css"
