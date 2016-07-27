
# GrooCSS

The missing piece for full-stack Groovy. Like [Less](http://lesscss.org/) but without inventing a new language.

- Write compile-time checked CSS in Groovy
- Use a natural Groovy DSL for CSS with code completion if your IDE supports it
- _(coming) Automatically supports WebKit, MS, etc... extensions_

## Examples

### Convert files

    import org.groocss.GrooCSS

    GrooCSS.convert('infile.groocss', 'outfile.css')

### Plain old Groovy

    import org.groocss.GrooCSS
    
    def css = GrooCSS.runBlock {
        sg('.class') { borderColor '#123456' }
    }
    new File('out.css').text = "$css"

### DSL

    def myColor = '#fe33ac'

    sg('.box') {
      color myColor
      borderColor '#fdcdea'
    }
    sg('.box div') {
      boxShadow '0 0 5px rgba(0, 0, 0, 0.3)'
    }
    sg('table') {
        color myColor
    }

