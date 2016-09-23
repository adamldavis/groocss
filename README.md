
[![Build Status](https://snap-ci.com/adamldavis/groocss/branch/master/build_image)](https://snap-ci.com/adamldavis/groocss/branch/master)
[ ![Download](https://api.bintray.com/packages/adamldavis/maven/GrooCSS/images/download.svg) ](https://bintray.com/adamldavis/maven/GrooCSS/_latestVersion)
/ [Gradle Plugin](https://plugins.gradle.org/plugin/org.groocss.groocss-gradle-plugin)

# GrooCSS

Like [Less](http://lesscss.org/) but without inventing a new language.

GrooCSS lets you code your CSS in Groovy, using a natural Groovy DSL with code completion if your IDE supports it.

It was created by Adam L. Davis (@adamldavis) and inspired by the many other Groovy-based projects out there, like 
[Gradle](gradle.org), [Grails](https://grails.org/), 
[Spock](https://github.com/spockframework/spock), [Ratpack](https://ratpack.io/), and [grooscript](http://grooscript.org/).

- DSL similar to CSS but with camel-case and some modifications to make it valid Groovy.
- Keyframes, media, charset, and font-face support.
- Automatically adds -webkit, -ms, -moz, -o extensions! (configurable)
- Color support with rgb, rgba, hex, and named colors
- Minimization (compress)
- Support for transforms directly (transformX, etc),
- Math functions (sqrt, sin, cos, toRadians, etc.) and built-in Measurement math.
- Unit methods (unit, getUnit, convert)
- Ability to extend style-groups and add internal groups.
- Pseudo-classes in DSL (nthChild, etc.)
- Multiple ways to configure: Config.builder() or using withConfig
- Close to CSS syntax using getAt, putAt, operator-overloading, underscore, methodMissing, and propertyMissing
- Translator to convert from existing CSS.
- Available pretty print (using Config)

## New in 0.7.x

- Better pseudo-class support with %
- Measurements are now fully supported including math between different compatible types.
- Added [Gradle Plugin](https://plugins.gradle.org/plugin/org.groocss.groocss-gradle-plugin)
- Some measurement values are validated (for example, passing 10.deg to maxWidth will throw an AssertionError).

## Using Gradle with Plugin

Using Gradle 2.1 or later, you simply apply the plugin, provide any optional configuration, and provide a list of files to convert.
The plugin adds a `convertCss` task for converting your groocss files into css. 
For example:

    plugins {
      id "org.groocss.groocss-gradle-plugin" version "0.7.2"
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

There's also a `GroocssTask` available if you want to have finer-grained control. Here's an example using a task:

    task css(type: org.groocss.GroocssTask, dependsOn: convertCss) {
        conf = new org.groocss.Config(compress: true, addOpera: false)
        inFile = file('index.groocss')
        outFile = file("$cssDir/index.css.min")
    }

## Using Gradle without Plugin

    import org.groocss.GrooCSS

    buildscript {
        repositories { jcenter() }
        dependencies { classpath 'org.groocss:groocss:0.7.2' }
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

    def myColor = c('#fe33ac')

    _.box {
      color myColor
      borderColor '#fdcdea'
    }
    _.box ^ div {
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
    sg '#formId', {     // sg useful for ID's
        minWidth 100.px // resolves to 100px
    }
    p + div {
        border '1px solid black'
    }
    p.red | a.red { color red } // | => ,
    p >> a { color blue }       //>> => >
    p * a { color blue }        // * => *
    p - a { color blue }        // - => ~(tilde)
    p ^ a { color blue }        // ^ =>  (space)

### Measurement Math

    def myWidth = 100.pt + 1.in // converts to pt
    def myDelay = 100.ms + 1.s     // converts to ms
    def mySize = myWidth / 2    // you can multiply/divide with any number
    def doubleSize = myWidth * 2

### Extending

    _.warn { color red }
    _.error {
        extend(_.warn) // extend '.warn' also works
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

### Keyframes and Transforms DSL

    def css = GrooCSS.process(new Config(addWebkit: false, addMoz: false, addOpera: false)) {
    
        keyframes('bounce') {
            40 % {
                translateY(-30.px)
            }
            60 % {
                translateY(-15.px)
            }
            frame([0,20,50,80,100]) {
                translateY(0)
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
    
Resolves to:

    @font-face { font-family: myFirstFont; font-weight: normal; src:url(sensational.woff); }
    
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
    //OR
    groocss { compress = true } // using Gradle plugin

## Media

    media 'screen', {
        body { width '100%' }
    }
    
Produces:

    @media screen {
        body { width: 100%; }
    }

## Pseudo-classes

    input % hover { color blue }
    li % nthChild('3n') { color blue }

Produces:

    input:hover { color: Blue; }
    li:nth-child(3n) { color: Blue; }

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

