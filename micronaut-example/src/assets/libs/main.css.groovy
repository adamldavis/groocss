
html {
    fontFamily '"Open Sans", "Helvetica Neue", Helvetica, Arial, sans-serif'
    textAlign 'justify'
}
pre, code {
    fontFamily '"Courier New", monospace, serif'
    fontSize 1.em
}
body {
    background c(0x1a1a1a) color white.alpha(0.77)
    fontSize 1.em
}
def w = 600.px

article | footer {
    position 'relative'
    left '50%'
    marginLeft (-w/2)
    width w
}
nav {
    display 'flex'
    flexDirection 'horizontal'
}
nav a {
    padding 1.em
    textDecoration 'none'
    color c(0x7ddcfb)
    borderRadius '1em 1em 0 0'
    transition {background 1.s} {color 250.ms}
}
a % hover { background white.alpha(0.9) color blue }

sg '#main', {
    border '1px solid black'
    borderRadius '0 1em 1em 1em'
    padding 2.em
    backgroundColor c('a0a0a0').alpha(0.5)
}
footer {
    fontSize 14.px
}

