
[![Build Status](https://snap-ci.com/adamldavis/groocss/branch/master/build_image)](https://snap-ci.com/adamldavis/groocss/branch/master)
[ ![Download](https://api.bintray.com/packages/adamldavis/maven/GrooCSS/images/download.svg) ](https://bintray.com/adamldavis/maven/GrooCSS/_latestVersion)
,[Gradle Plugin](https://plugins.gradle.org/plugin/org.groocss.groocss-gradle-plugin)
,[Build Scan](https://scans.gradle.com/s/voi3o46bkjbcg)

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
- Support for transforms directly (transformX, etc), 
- Support for @media, 
- Math functions (sqrt, sin, cos, toRadians, etc.)
- Element names (div, a, input, span, etc.)
- Unit methods (unit, getUnit, convert, etc.)
- Ability to extend style-groups and add internal
- Pseudo-classes in DSL (nthChild, etc.)
- New ways to configure: Config.builder() or using withConfig

## New in 0.6

- Closer to CSS syntax using getAt, putAt, operator-overloading, underscore, methodMissing, and propertyMissing
- Translator to convert from existing CSS
- Available pretty print (using Config)

## New in 0.7

- Better pseudo-class support with %
- Measurements are now fully supported including math between different compatible types.
- Added [Gradle Plugin](https://plugins.gradle.org/plugin/org.groocss.groocss-gradle-plugin)

## Using Gradle with Plugin

Using Gradle 2.1 or later, you simply apply the plugin, provide any optional configuration, and provide a list of files to convert.
The plugin adds a `convertCss` task for converting your groocss files into css. 
For example:

    plugins {
      id "org.groocss.groocss-gradle-plugin" version "0.7.1"
    }
    def cssDir = "$parent.buildDir/../www/css"

    groocss { // any config
        addOpera = false
        prettyPrint = true
    }
    groocssfiles { // a list of in/out files
        index {
            inFile = file('index.groocss')
            outFile = file("$cssDir/index.css")
        }
    }

If you have a lot of files, `inFile` and `outFile` can be directories (it will assume groocss files end in `.groocss`).


## Using Gradle without Plugin

    import org.groocss.GrooCSS

    buildscript {
        repositories { jcenter() }
        dependencies { classpath 'org.groocss:groocss:0.7' }
    }
    task css << {
        def file = file('css/out.css')
        GrooCSS.process {
            // DSL goes here
        }.writeTo(file)
    }

## Examples

### Using convert methods

    import org.groocss.GrooCSS

    GrooCSS.convertFile('infile.groocss', 'outfile.css')
    //or
    GrooCSS.convert(new File('in'), new File('out'))

### Styles DSL

    def myColor = '#fe33ac'

    _.box {
      color myColor
      borderColor '#fdcdea'
    }
    sg '.box div', {
      boxShadow '0 0 5px rgba(0, 0, 0, 0.3)'
    }
    table {
        color myColor
    }
    table.myClass {
        color myColor.darker()
    }
    input['class$="test"'] = {
        background yellow
    }
    p + div {
        border '1px solid black'
    }
    p.red | a.red { color red } // | => ,
    p >> a { color blue }       //>> => >
    p * a { color blue }        // * => *
    p - a { color blue }        // - => ~(tilde)
    p ^ a { color blue }        // ^ =>  (space)

### Extending

    _.warn { color red }
    _.error {
        extend '.warn'
        background black
    }
    
Produces:

    .warn,.error {color: Red;}
    .error {background: Black;}

### Nesting

    a {
        color '#000'
        add ':hover', { color blue }
    }
    div {
        add '> p', { color '#eee' }
    }

Produces:

    a { color: #000; }
    a:hover { color: Blue; }
    div > p { color: #eee; }

### Keyframes DSL

    def css = GrooCSS.process(new Config(addWebkit: false, addMoz: false, addOpera: false)) {
    
        keyframes('bounce') {
            frame 40, {
                transform 'translateY(-30px)'
            }
            frame 60, {
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
        _.sea {
            color sea.darker()
            background sea.brighter()
            border "5px solid ${sea.alpha(0.5)}"
        }
    }
    
You can also use named colors:

    _.blue {
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

    input % hover {
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

## Converting from CSS

You can use the Translator to convert existing CSS into GrooCSS syntax:

    GrooCSS.convertFromCSS(File inFile, File outFile)

This allows you to get started quickly with GrooCSS in existing projects.

