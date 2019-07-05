<#include "header.ftl">
	
	<#include "menu.ftl">

	<img src="images/code groocss small.png" alt="GrooCSS - code CSS in Groovy" style="float: left">

	<p><a href="https://bintray.com/adamldavis/maven/GrooCSS/_latestVersion">
			<img src="https://api.bintray.com/packages/adamldavis/maven/GrooCSS/images/download.svg" alt="Download"> </a>
	</p>

	<p class="project-tagline">GrooCSS let's you code your CSS in Groovy, using a natural Groovy DSL with optional code completion.</p>

	<p>It was created by Adam L. Davis (<a href="https://github.com/adamldavis" class="user-mention">@adamldavis</a>)
		and inspired by the many other Groovy-based projects out there, like
	Gradle, Grails, Spock, Ratpack, and grooscript.</p>

	<p>Want to know more? Check out <a href="about.html">the about page.</a>

	<h1>Blog</h1>
	<#list posts as post>
  		<#if (post.status == "published")>
  			<a href="${post.uri}"><h2><#escape x as x?xml>${post.title}</#escape></h2></a>
  			<p>${post.date?string("dd MMMM yyyy")}</p>
  			<!--<p>{post.body}</p>-->
  		</#if>
  	</#list>

	<hr />
	
	<p>Older posts are available in the <a href="${content.rootpath}${config.archive_file}">archive</a>.</p>

<#include "footer.ftl">