<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<html>
	<head>
		<title>BiVeS WebService USAGE</title>
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
</body>
</html>