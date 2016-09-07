/*! styles from github | MIT License */
/* Converted to groocss by Adam L. Davis, 2016 */
sg '*', {
  boxSizing 'border-box'
}

body {
  padding 0
  margin 0
  fontFamily '"open sans", "helvetica neue", helvetica, arial, sans-serif'
  fontSize '16px'
  lineHeight 1.5
  color '#606c71'
}

a {
  color '#1e6bb8'
  textDecoration 'none'
}
a%hover {
  textDecoration 'underline'
}

_.btn {
  display 'inline-block'
  marginBottom '1rem'
  color rgba(255, 255, 255, 0.7)
  backgroundColor rgba(255, 255, 255, 0.08)
  borderColor rgba(255, 255, 255, 0.2)
  borderStyle 'solid'
  borderWidth '1px'
  borderRadius '0.3rem'
  transition 'color 0.2s, background-color 0.2s, border-color 0.2s'
}
_.btn + _.btn {
  marginLeft '1rem'
}

sg '.btn:hover', {
  color rgba(255, 255, 255, 0.8)
  textDecoration 'none'
  backgroundColor rgba(255, 255, 255, 0.2)
  borderColor rgba(255, 255, 255, 0.3)
}

media 'screen and (min-width: 64em)', {
_.btn {
    padding '0.75rem 1rem'; } }

media 'screen and (min-width: 42em) and (max-width: 64em)', {
_.btn {
  padding '0.6rem 0.9rem'
    fontSize 0.9.rem } }

media 'screen and (max-width: 42em)', {
  _.btn {
    display 'block'
    width '100%'
    padding '0.75rem'
    fontSize '0.9rem'
  }
  _.btn + _.btn {
    marginTop '1rem'
        marginLeft 0; } 
}

sg '.page-header', {
  color '#fff'
  textAlign 'center'
  backgroundColor '#159957'
  backgroundImage 'linear-gradient(120deg, #112233, #a5b9c7)'
}

media 'screen and (min-width: 64em)', {
  sg '.page-header', {
    padding '5rem 6rem' } }

media 'screen and (min-width: 42em) and (max-width: 64em)', {
  sg '.page-header', {
    padding '3rem 4rem' } }

media 'screen and (max-width: 42em)', {
  sg '.page-header', {
    padding '2rem 1rem' } }

sg '.project-name', {
  marginTop 0
  marginBottom '0.1rem'
}

media 'screen and (min-width: 64em)', {
  sg '.project-name', {
    fontSize 3.25.rem } }

media 'screen and (min-width: 42em) and (max-width: 64em)', {
  sg '.project-name', {
    fontSize 2.25.rem } }

media 'screen and (max-width: 42em)', {
  sg '.project-name', {
    fontSize 1.75.rem } }

sg '.project-tagline', {
  marginBottom '2rem'
  fontWeight 'normal'
  opacity 0.7
}

media 'screen and (min-width: 64em)', {
  sg '.project-tagline', {
    fontSize 1.25.rem } }

media 'screen and (min-width: 42em) and (max-width: 64em)', {
  sg '.project-tagline', {
    fontSize 1.15.rem } }

media 'screen and (max-width: 42em)', {
  sg '.project-tagline', {
    fontSize 1.rem } }

sg '.main-content :first-child', {
  marginTop 0
}
sg '.main-content img', {
  maxWidth '100%'
}
sg '.main-content h1 ,.main-content h2 ,.main-content h3 ,.main-content h4 ,.main-content h5 ,.main-content h6',{
  marginTop '2rem'
  marginBottom '1rem'
  fontWeight 'normal'
  color '#159957'
}
sg '.main-content p', {
  marginBottom '1em'
}
sg '.main-content code', {
  padding '2px 4px'
  fontFamily 'consolas, "liberation mono", menlo, courier, monospace'
  fontSize '0.9rem'
  color '#383e41'
  backgroundColor '#f3f6fa'
  borderRadius '0.3rem'
}
sg '.main-content pre', {
  padding '0.8rem'
  marginTop 0
  marginBottom '1rem'
  font '1rem consolas, "liberation mono", menlo, courier, monospace'
  color '#567482'
  wordWrap 'normal'
  backgroundColor '#f3f6fa'
  border 'solid 1px #dce6f0'
  borderRadius '0.3rem'
}
sg '.main-content pre > code', {
  padding 0
  margin 0
  fontSize '0.9rem'
  color '#567482'
  wordBreak 'normal'
  whiteSpace 'pre'
  background 'transparent'
  border 0
}
sg '.main-content .highlight',{
  marginBottom '1rem'
}
sg '.main-content .highlight pre',{
  marginBottom 0
  wordBreak 'normal'
}
sg '.main-content .highlight pre, .main-content pre',{
  padding '0.8rem'
  overflow 'auto'
  fontSize '0.9rem'
  lineHeight 1.45
  borderRadius '0.3rem'
}
sg '.main-content pre code, .main-content pre tt',{
  display 'inline'
  maxWidth 'initial'
  padding 0
  margin 0
  overflow 'initial'
  lineHeight 'inherit'
  wordWrap 'normal'
  backgroundColor 'transparent'
  border 0
}
sg '.main-content pre code:before,.main-content pre code:after,.main-content pre tt:before,.main-content pre tt:after',{
  content 'normal'
}
sg '.main-content ul | .main-content ol',{
  marginTop 0
}
sg '.main-content blockquote',{
  padding '0 1rem'
  marginLeft 0
  color '#819198'
  borderLeft '0.3rem solid #dce6f0'
}
sg '.main-content blockquote > :first-child', {
  marginTop 0
}
sg '.main-content blockquote > :last-child', {
  marginBottom 0
}
sg '.main-content table',{
  display 'block'
  width '100%'
  overflow 'auto'
  wordBreak 'normal'
  wordBreak 'keep-all'
}
sg '.main-content table th',{
  fontWeight 'bold'
}
sg '.main-content table th, .main-content table td', {
  padding '0.5rem 1rem'
  border '1px solid #e9ebec'
}
sg '.main-content dl',{
  padding 0
}
sg '.main-content dl dt',{
  padding 0
  marginTop '1rem'
  fontSize '1rem'
  fontWeight 'bold'
}
sg '.main-content dl dd',{
  padding 0
  marginBottom '1rem'
}
sg '.main-content hr',{
  height '2px'
  padding 0
  margin '1rem 0'
  backgroundColor '#eff0f1'
  border 0
}

media 'screen and (min-width: 64em)', {
sg '.main-content', {
  maxWidth '64rem'
  padding '2rem 6rem'
  margin '0 auto'
    fontSize 1.1.rem } }

media 'screen and (min-width: 42em) and (max-width: 64em)', {
sg '.main-content', {
  padding '2rem 4rem'
    fontSize 1.1.rem } }

media 'screen and (max-width: 42em)', {
sg '.main-content', {
  padding '2rem 1rem'
    fontSize 1.rem } }

sg '.site-footer', {
  paddingTop '2rem'
  marginTop '2rem'
  borderTop 'solid 1px #eff0f1'
}

sg '.site-footer-owner', {
  display 'block'
  fontWeight 'bold'
}

sg '.site-footer-credits', {
  color '#819198'
}

media 'screen and (min-width: 64em)', {
sg '.site-footer', {
    fontSize 1.rem } }

media 'screen and (min-width: 42em) and (max-width: 64em)', {
sg '.site-footer', {
    fontSize 1.rem } }

media 'screen and (max-width: 42em)', {
sg '.site-footer', {
    fontSize 0.9.rem } }
