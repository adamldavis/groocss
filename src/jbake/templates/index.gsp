<%include "header.gsp"%>
	
	<%include "menu.gsp"%>

	<img src="images/code groocss small.png" alt="GrooCSS - code CSS in Groovy" style="float: left">

	<p><a href="https://bintray.com/adamldavis/maven/GrooCSS/_latestVersion">
			<img src="https://api.bintray.com/packages/adamldavis/maven/GrooCSS/images/download.svg" alt="Download"> </a>
	</p>

	<p class="project-tagline">GrooCSS let's you code your CSS in Groovy, using a natural Groovy DSL with optional code completion.</p>

	<p>It was created by Adam L. Davis (<a href="https://github.com/adamldavis" class="user-mention">@adamldavis</a>)
		and inspired by the many other Groovy-based projects out there, like
		<a href="https://gradle.org/">Gradle</a>,
		<a href="https://grails.org/">Grails</a>,
		<a href="http://spockframework.org/">Spock</a>,
		<a href="https://ratpack.io/">Ratpack</a>, and
		<a href="https://www.grooscript.org/">grooscript.</a></p>

	<p>Want to know more? Check out <a href="about.html">the about page.</a>

	<div class="container">
		<div class="col-md-3 salmon">
			<h2><span class="glyphicon glyphicon-ok-circle"></span>Open Source</h2>
			GrooCSS is free to use, open source, and licensed under the
			<a href="https://github.com/adamldavis/groocss/blob/master/LICENSE">Apache License, Version 2.0.</a>
		</div>
		<div class="col-md-3 salmon">
			<h2><span class="glyphicon glyphicon-th"></span>Cross Platform</h2>
			Runs on the <a href="https://www.java.com/en/">JVM</a> so it works on any platform; Linux, Windows, Mac, etc.
		</div>
		<div class="col-md-3 salmon">
			<h2><span class="glyphicon glyphicon-fire"></span>Powerful</h2>
			Tons of features included and anything you can do in <a href="http://groovy-lang.org/">Groovy</a> available.
		</div>
		<div class="col-md-3 salmon">
			<h2><span class="glyphicon glyphicon-upload"></span>Extensible</h2>
			Designed from the ground up to be extensible.
			Includes the ability to programmatically modify styles and insert custom code for processing and validation.
		</div>
	</div>

	<h2>Documentation</h2>

	<div class="container">
		<% def latest = published_manuals.find { it.title == "Manual ${config.latest_version}" }
			def old_manuals = published_manuals - latest %>

		<div class="md-12">
			<h3><span class="glyphicon glyphicon-star"></span> Latest: <a href="${latest.uri}">${latest.title}</a> |
				<a href="${latest.uri.replace('manual','docs')}">API</a></h3>
		</div>

		<h3>Archive:</h3>
		<%old_manuals.sort {a,b -> b.title <=> a.title }.each {manual ->%>
			<div class="col-md-2">
				<a href="${manual.uri}">${manual.title}</a> |
				<a href="${manual.uri.replace('manual','docs')}">API</a>
			</div>
		<%}%>
	</div>
	<hr />

	<h1><a href="blog.html">Blog</a></h1>

	<% def blog_posts = published_posts.size() > 5 ? published_posts[0..<5] : published_posts %>
	<%blog_posts.each {post ->%>
	<a href="${post.uri}"><h4>${post.title}</h4></a>
	<i>${new java.text.SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH).format(post.date)}</i>
	<%}%>

<%include "footer.gsp"%>