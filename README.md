
[ ![Download](https://api.bintray.com/packages/adamldavis/maven/GrooCSS/images/download.svg) ](https://bintray.com/adamldavis/maven/GrooCSS/_latestVersion)

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

## New in 0.5

- Support for transforms directly (transformX, etc), 
- Support for @media, 
- Math functions (sqrt, sin, cos, toRadians, etc.)
- Element names (div, a, input, span, etc.)
- Unit methods (unit, getUnit, convert, etc.)
- Ability to extend style-groups and add internal
- Pseudo-classes in DSL (nthChild, etc.)
- New ways to configure: Config.builder() or using withConfig

## Coming soon in 0.6

- Mimic CSS syntax using underscore, methodMissing, and propertyMissing
- Translator to convert from existing CSS
- Available pretty print (using Config)

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
    table {
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

	body {
		add style('-webkit-touch-callout', 'none')
		add style('-webkit-textSize-adjust', 'none')
		add style('-webkit-user-select', 'none')
	}
	
## Compressing (Minimization)

To "compress" the output (no new-lines), just pass in a Config object:

    GrooCSS.process(new Config(compress: true))
    //OR
    GrooCSS.convert(new Config(compress: true), infile, outfile)

## Media

    media 'screen', {
        body { width '100%' }
    }
    
Produces:

    @media screen {
        body { width: 100%; }
    }

## Pseudo-classes

    input { hover()
        color blue}

Produces:

    input:hover { color: Blue; }

## Config

There are three different ways to configure GrooCSS:

- Using the groovy constructor: new Config(compress: true)
- Using the builder syntax: Config.builder().compress(true).build()
- Using the DSL: GrooCSS.withConfig { noExts().compress().utf8() }.process {}

Of these options, the third is most recommended.
With the DSL there are several chainable methods available to easily configure your CSS:
- noExts() - sets all extension flags to false (addOpera, etc.)
- onlyMs(), onlyWebkit(), etc. - sets all extensions flags to false except one.
- utf8() - sets the charset to UTF-8.
- compress() - sets compress flag to true.


