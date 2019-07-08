		</div>
		<div id="push"></div>
    </div>

    <div id="footer">
        <div class="container">
            <p class="muted credit">&copy; Adam L. Davis 2019 | GrooCSS is free to use, open source, and licensed under the
                <a href="https://github.com/adamldavis/groocss/blob/master/LICENSE">Apache License, Version 2.0.</a>
                | Mixed with <a href="http://getbootstrap.com/">Bootstrap v3.1.1</a>
                | Baked with <a href="http://jbake.org">JBake ${version}</a></p>
        </div>
    </div>
    
    <!-- Le javascript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="<%if (content.rootpath) {%>${content.rootpath}<% } else { %><% }%>js/jquery-1.11.1.min.js"></script>
    <script src="<%if (content.rootpath) {%>${content.rootpath}<% } else { %><% }%>js/bootstrap.min.js"></script>
    <script src="<%if (content.rootpath) {%>${content.rootpath}<% } else { %><% }%>js/prettify.js"></script>
    
  </body>
</html>