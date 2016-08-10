
# GrooCSS

Like [Less](http://lesscss.org/) but without inventing a new language. The missing piece for full-stack Groovy. 

- Write CSS in Groovy, compile-time checked optionally
- Use a natural Groovy DSL for CSS with code completion if your IDE supports it
- Keyframes support!
- Automatically adds -webkit, -ms, -moz, -o extensions! (configurable)
- Color support with rgb, rgba, hex, and named colors
- Font-face support
- Minimization (compress: true)
- @charset support

## Coming in 0.5

- Support for transforms directly (transformX, etc), 
- Support for @media, math functions, 
- element names (div, a, input, etc.), 
- More methods (unit, getUnit, convert, etc.), 
- Ability to extend style-groups

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

    sg '.box', {
      color myColor
      borderColor '#fdcdea'
    }
    sg '.box div', {
      boxShadow '0 0 5px rgba(0, 0, 0, 0.3)'
    }
    sg 'table', {
        color myColor
    }

### Keyframes DSL

    def css = GrooCSS.process(new Config(addWebkit: false, addMoz: false, addOpera: false)) {
    
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

Use the "c", "clr", "rgb" or "rgba" methods to create a color. For example:

    def css = GrooCSS.process {
        def sea = c('5512ab') //OR rgb(85, 18, 171)
        sg('.sea') {
            color( sea.darker() )
            background( sea.brighter() )
            border "5px solid ${sea.alpha(0.5)}"
        }
    }
    
You can also use named colors:

    sg '.blue', {
        color darkBlue
        background aliceBlue
    }

### Font-face

    fontFace {
        fontFamily 'myFirstFont'
        fontWeight 'normal'
        src 'url(sensational.woff)'
    }
    
### Custom styles

	sg 'body', {
		add style('-webkit-touch-callout', 'none')
		add style('-webkit-textSize-adjust', 'none')
		add style('-webkit-user-select', 'none')
	}
	
## Compressing (Minimization)

To "compress" the output (no new-lines), just pass in a Config object:

    GrooCSS.process(new Config(compress: true))
    //OR
    GrooCSS.convert(new Config(compress: true), infile, outfile)


    
