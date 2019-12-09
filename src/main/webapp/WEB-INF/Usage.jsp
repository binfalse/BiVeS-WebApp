<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<html>
	<head>
		<title>BiVeS WebApp USAGE</title>
		<style>
			p{max-width:50em;}
			pre{font-size:.9em;background-color: #ddd;padding: 20px;}
		</style>
	</head>
	<body>
	
<h1><a href="https://sems.uni-rostock.de/trac/bivesws">BiVeS WebService</a> USAGE</h1>

<p>To use this web service send a JSON object via post request.
The sent JSON object must be of following format:</p>
<pre>
{
	"files":
	[
		"FILE1",
		"FILE2"
	],
	"commands":
	[
		"OPTION",
		"OPTION",
		[...]
	]
}
</pre>
<p>files is an array of max. two files, either defined by plain XML or URLs to retrieve the files.</p>


<p>and the following commands are available:</p>
<pre>
${commands}
</pre>


<p>an example call to compute the diff between two <code>SBML</code> files and ask for the highlighted reaction network encoded in DOT language (<code>reactionsDot</code>) and the report encoded in HTML (<code>reportHtml</code>)
using curl might look like:</p>

<pre>
curl -d '{
	"files":
	[
		"http://budhat.sems.uni-rostock.de/download?downloadModel=24",
		"http://budhat.sems.uni-rostock.de/download?downloadModel=25"
	],
	"commands":
	[
		"SBML",
		"reactionsDot",
		"reportHtml"
	]
}' ${url} | python -mjson.tool
</pre>

<p>the result will be a JSON object like:</p>
<pre>
{
	"reactionsDot": "digraph BiVeSexport {[...]}",
	"reportHtml": "SBML Differences[...]"
}
</pre>

<h3>Privacy and data protection</h3>
<p>BiVeS is not tracking the user.
The maintainer of this application may however decide to e.g. keep log files or integrate other kinds of tracking.<br>
Please be aware, that BiVeS will retrieve a copy of the data that you send to BiVeS, as BiVeS needs to process the data.
This data is, however, only used for the processing of your request and will be deleted immediately after you get the results.
We still strongly recommend to not upload sensitive data!
Instead, consider <a href="https://github.com/binfalse/bives-webapp">running your own instance of the BiVeS WebApp</a>, which gives you much more control over your data.</p> 
<p>This application is maintained by <a href="${maintainerurl}">${maintainer}</a> &mdash; see <a href="${imprint}">imprint</a>.</p>


<p>This is version ${webappversion} of the BiVeS WebApp, including ${bivesversion}</p>
</body>
</html>