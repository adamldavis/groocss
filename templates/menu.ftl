	<!-- Fixed navbar -->
    <div class="navbar navbar-default navbar-fixed-top" role="navigation">
      <div class="container">
        <div class="navbar-header">
          <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a class="navbar-brand" href="http://groocss.org">GrooCSS</a>
        </div>
        <div class="navbar-collapse collapse">
          <ul class="nav navbar-nav">
            <li><a href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>index.html">Home</a></li>
            <li><a href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>about.html">About</a></li>
            <li><a href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>intro.html">Getting Started</a></li>
            <li><a href="https://github.com/adamldavis/groocss"  rel="me">GitHub</a></li>
            <li><a href="https://bintray.com/adamldavis/maven/GrooCSS"  rel="me">Bintray</a></li>
            <li><a href="docs/1.0-M3/index.html" >Docs</a></li>

            <li class="dropdown">
              <a href="#" class="dropdown-toggle" data-toggle="dropdown">Other links <b class="caret"></b></a>
              <ul class="dropdown-menu">
                <li><a href="https://twitter.com/groocss"  rel="me">Twitter</a></li>
                <li><a href="https://plugins.gradle.org/plugin/org.groocss.groocss-gradle-plugin" >Gradle Plugin</a></li>
                <li class="divider"></li>
                <li class="dropdown-header">Old stuff</li>
                <li><a href="http://blag.groocss.org" >Old Blog</a></li>
                <li><a href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>${config.feed_file}">Subscribe</a></li>
              </ul>
            </li>
          </ul>
        </div><!--/.nav-collapse -->
      </div>
    </div>
    <div class="container">