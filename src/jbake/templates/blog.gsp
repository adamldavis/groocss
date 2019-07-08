<%include "header.gsp"%>

	<%include "menu.gsp"%>

	<% def blog_posts = published_posts.size() > 5 ? published_posts[0..<5] : published_posts %>
	<%blog_posts.each {post ->%>
		<a href="${post.uri}"><h1>${post.title}</h1></a>
		<p>${new java.text.SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH).format(post.date)}</p>
		<p>${post.body}</p>
  	<%}%>
	
	<hr />
	
	<p>Older posts are available in the <a href="${content.rootpath}${config.archive_file}">archive</a>.</p>

<%include "footer.gsp"%>