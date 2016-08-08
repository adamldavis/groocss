
# GrooCSS

The missing piece for full-stack Groovy. Like [Less](http://lesscss.org/) but without inventing a new language.

- Write CSS in Groovy, compile-time checked optionally
- Use a natural Groovy DSL for CSS with code completion if your IDE supports it
- Keyframes support!
- Automatically supports WebKit, MS, etc... extensions
- Color support
- Font-face support

## Examples

### Convert files

    import org.groocss.GrooCSS

    GrooCSS.convert('infile.groocss', 'outfile.css')

### Plain old Groovy

    import org.groocss.GrooCSS
    
    def css = GrooCSS.process {
        sg ('.class') { borderColor '#123456' }
        //OR
        sg '.class', { borderColor '#123456' }
        
    }.writeToFile('out.css')

### Styles DSL

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

### Keyframes DSL

    def css = GrooCSS.process {
    
        keyframes('bounce') {
            frame(40) {
                transform 'translateY(-30px)'
            }
            frame(60) {
                transform 'translateY(-15px)'
            }
            frame([0,20,50,80,100]) {
                transform 'translateY(0)'
            }
        }
    }
    
Produces:

    @keyframes bounce {
    40%{transform: translateY(-30px);}
    60%{transform: translateY(-15px);}
    0%, 20%, 50%, 80%, 100%{transform: translateY(0);}
    }

### Colors

Use the "c" method to create a color. For example:

    def css = GrooCSS.process {
        def sea = c('5512ab')
        sg('.sea') {
            color( sea.darker() )
            background( sea.brighter() )
        }
    }
    
### Font-face

    fontFace {
        fontFamily 'myFirstFont'
        fontWeight 'normal'
        src 'url(sensational.woff)'
    }
    
    
