<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8"/>
    <title>GrooCSS - <%if (content.title) {%>${content.title}<%} else { %>Main<% } %></title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="Adam L Davis">
    <meta name="keywords" content="groovy,css,css3,html,dsl,groocss">
    <meta name="generator" content="JBake">

    <!-- Le styles -->
    <link rel="stylesheet" type="text/css" href="<%if (content.rootpath) {%>${content.rootpath}<% } else { %><% }%>stylesheets/normalize.css" media="screen">
    <link href='https://fonts.googleapis.com/css?family=Open+Sans:400,700' rel='stylesheet' type='text/css'>
    <link rel="stylesheet" type="text/css" href="<%if (content.rootpath) {%>${content.rootpath}<% } else { %><% }%>stylesheets/stylesheet.css" media="screen">
    <link rel="stylesheet" type="text/css" href="<%if (content.rootpath) {%>${content.rootpath}<% } else { %><% }%>stylesheets/github-light.css" media="screen">
    <link rel="stylesheet" type="text/css" href="<%if (content.rootpath) {%>${content.rootpath}<% } else { %><% }%>stylesheets/style.css" media="screen">

    <link href="<%if (content.rootpath) {%>${content.rootpath}<% } else { %><% }%>css/bootstrap.min.css" rel="stylesheet">
    <link href="<%if (content.rootpath) {%>${content.rootpath}<% } else { %><% }%>css/asciidoctor.css" rel="stylesheet">
    <link href="<%if (content.rootpath) {%>${content.rootpath}<% } else { %><% }%>css/base.css" rel="stylesheet">
    <link href="<%if (content.rootpath) {%>${content.rootpath}<% } else { %><% }%>css/prettify.css" rel="stylesheet">
    <style>html { font-size: 20px }</style>

    <!-- HTML5 shim, for IE6-8 support of HTML5 elements -->
    <!--[if lt IE 9]>
      <script src="<%if (content.rootpath) {%>${content.rootpath}<% } else { %><% }%>js/html5shiv.min.js"></script>
    <![endif]-->

    <!-- Fav and touch icons -->
    <!--<link rel="apple-touch-icon-precomposed" sizes="144x144" href="../assets/ico/apple-touch-icon-144-precomposed.png">
    <link rel="apple-touch-icon-precomposed" sizes="114x114" href="../assets/ico/apple-touch-icon-114-precomposed.png">
    <link rel="apple-touch-icon-precomposed" sizes="72x72" href="../assets/ico/apple-touch-icon-72-precomposed.png">
    <link rel="apple-touch-icon-precomposed" href="../assets/ico/apple-touch-icon-57-precomposed.png">-->
    <link rel="shortcut icon" href="<%if (content.rootpath) {%>${content.rootpath}<% } else { %><% }%>favicon.ico">
  </head>
  <body onload="prettyPrint()">
    <div id="wrap">

      <header class="page-header">
        <img src="<%if (content.rootpath) {%>${content.rootpath}<% } else { %><% }%>images/groocss2_256.png" width="128" height="128">
        <h1 class="project-name">GrooCSS</h1>
        <h2 class="project-tagline">Code CSS in Groovy</h2>
      </header>
