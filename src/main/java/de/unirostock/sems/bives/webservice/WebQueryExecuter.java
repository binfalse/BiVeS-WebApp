/**
 * 
 */
package de.unirostock.sems.bives.webservice;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import de.unirostock.sems.bives.Executer;
import de.unirostock.sems.bives.tools.FileRetriever;
import de.unirostock.sems.bives.tools.Tools;



/**
 * Execute a web query using BiVeS.
 * 
 * @author martin
 * 
 */
public class WebQueryExecuter
{
	
	/** JSON key carrying the files. */
	public static final String	REQ_FILES		= "files";
	
	/** JSON key carrying the requests. */
	public static final String	REQ_WANT		= "commands";
	
	/** Pattern to distinguish xml files from URLs */
	public static final Pattern	XML_PATTERN	= Pattern.compile ("^\\s*<.*",
																						Pattern.DOTALL);
	
	/** The executer. */
	private Executer						exe;
	
	
	/**
	 * Instantiates a new web query executer.
	 */
	public WebQueryExecuter ()
	{
		// we don't want to use local files.
		FileRetriever.FIND_LOCAL = false;
		exe = new Executer ();
	}
	
	
	/**
	 * Execute a query.
	 * 
	 * @param jObj
	 *          the JSON object containing the request
	 * @param toReturn
	 *          the object to store the results
	 * @param err
	 *          the object to store errors
	 * @throws Exception
	 *           the exception
	 */
	@SuppressWarnings("unchecked")
	public void executeQuery (JSONObject jObj, JSONObject toReturn, JSONArray err)
		throws Exception
	{
		// check what we have to do
		int wanted = 0;
		JSONArray jArr = (JSONArray) jObj.get (REQ_WANT);
		for (int i = 0; i < jArr.size (); i++)
		{
			Executer.Option o = exe.get ((String) jArr.get (i));
			if (o == null)
				throw new IllegalArgumentException ("don't understand option: "
					+ jArr.get (i));
			wanted |= o.value;
		}
		
		// which files to use?
		Vector<File> files = new Vector<File> ();
		jArr = (JSONArray) jObj.get (REQ_FILES);
		for (int i = 0; i < jArr.size (); i++)
		{
			File f = getFile ((String) jArr.get (i), err);
			if (f != null)
				files.add (f);
		}
		
		// some general checks
		if (files.size () < 1)
			throw new IllegalArgumentException ("found no files.");
		if (files.size () > 2)
			throw new IllegalArgumentException (
				"found more than 2 files, not supported.");
		
		if (wanted < 1)
			throw new IllegalArgumentException (
				"nothing to do. (no options provided?)");
		
		// single or compare mode?
		if (files.size () == 1)
			exe.executeSingle (files.firstElement (), toReturn, wanted);
		else
			exe.executeCompare (files.firstElement (), files.get (1), toReturn,
				wanted);
		
		// done...
		for (File f : files)
			f.delete ();
	}
	
	
	/**
	 * Get the file.
	 * 
	 * @param content
	 *          the content
	 * @param err
	 *          the errors
	 * @return the file
	 */
	@SuppressWarnings("unchecked")
	private File getFile (String content, JSONArray err)
	{
		try
		{
			File f = File.createTempFile ("bives-webservice", "xml");
			f.deleteOnExit ();
			if (XML_PATTERN.matcher (content).find ())
			{
				// string
				PrintWriter out = new PrintWriter (f);
				out.print (content);
				out.close ();
			}
			else
			{
				// download
				URI fileUri = FileRetriever.getUri (content, null);
				FileRetriever.getFile (fileUri, f);
			}
			return f;
		}
		catch (IOException | URISyntaxException e)
		{
			err.add ("cannot read " + content);
		}
		return null;
	}
	
	
	/**
	 * Prints the usage including an example.
	 * 
	 * @param msg
	 *          the error message
	 * @param out
	 *          the stream to the user
	 */
	public final void printUsage (String msg, PrintWriter out)
	{
		
		if (msg != null && msg.length () > 0)
			out.println (msg);
		
		HashMap<String, Executer.Option> options = exe.getOptions ();
		HashMap<String, Executer.Option> addOptions = exe.getAddOptions ();
		
		out
			.println ("<html><head><title>BiVeS WebService USAGE</title></head><body>");
		out
			.println ("<style>p{max-width:50em;}	pre{font-size:.9em;background-color: #ddd;padding: 20px;}	</style>");
		out
			.println ("<h1><a href=\"https://sems.uni-rostock.de/projects/bives/bives-webservice/\">BiVeS WebService</a> USAGE</h1>");
		out
			.println ("<p>To use this web service send a JSON object via post request.");
		out.println ("The sent JSON object must be of following format:</p>");
		out.println ("<pre>");
		out.println ("{");
		out.println ("\t\"files\":");
		out.println ("\t[");
		out.println ("\t\t\"FILE1\",");
		out.println ("\t\t\"FILE2\"");
		out.println ("\t],");
		out.println ("\t\"commands\":");
		out.println ("\t[");
		out.println ("\t\t\"OPTION\",");
		out.println ("\t\t\"OPTION\",");
		out.println ("\t\t[...]");
		out.println ("\t]");
		out.println ("}");
		out.println ("</pre>");
		out.println ();
		out
			.println ("<p>files is an array of max. two files, either defined by plain XML or URLs to retrieve the files.</p>");
		out.println ();
		out.println ();
		out.println ("<p>and the following commands are available:</p>");
		out.println ("<pre>");
		SortedSet<String> keys = new TreeSet<String> (options.keySet ());
		int longest = 0;
		for (String key : keys)
		{
			if (key.length () > longest)
				longest = key.length ();
		}
		SortedSet<String> addKeys = new TreeSet<String> (addOptions.keySet ());
		for (String key : addKeys)
		{
			if (key.length () > longest)
				longest = key.length ();
		}
		
		longest += 2;
		
		out.println ();
		out.println ("COMPARISON COMMANDS");
		
		for (String key : keys)
			out.println ("\t" + key + Tools.repeat (" ", longest - key.length ())
				+ options.get (key).description);
		out.println ();
		
		out.println ("ADDITIONAL COMMANDS for single files");
		for (String key : addKeys)
			out.println ("\t" + key + Tools.repeat (" ", longest - key.length ())
				+ addOptions.get (key).description);
		out.println ("</pre>");
		out.println ();
		out.println ();
		
		out.println ("<p>an example call to compute the diff between two"
			+ " <code>SBML</code> files and ask for the highlighted"
			+ " chemical reaction network encoded in DOT language"
			+ " (<code>crnDot</code>) and the report encoded in HTML"
			+ " (<code>reportHtml</code>)");
		out.println ("using curl might look like:</p>");
		out.println ();
		out.println ("<pre>");
		out.println ("curl -d '{");
		out.println ("\t\"files\":");
		out.println ("\t[");
		out
			.println ("\t\t\"http://budhat.sems.uni-rostock.de/download?downloadModel=24\",");
		out
			.println ("\t\t\"http://budhat.sems.uni-rostock.de/download?downloadModel=25\"");
		out.println ("\t],");
		out.println ("\t\"commands\":");
		out.println ("\t[");
		out.println ("\t\t\"SBML\",");
		out.println ("\t\t\"crnDot\",");
		out.println ("\t\t\"reportHtml\"");
		out.println ("\t]");
		out.println ("}' http://bives.sems.uni-rostock.de | python -mjson.tool");
		out.println ("</pre>");
		out.println ();
		
		out.println ("<p>the result will be a JSON object like:</p>");
		out.println ("<pre>");
		out.println ("{");
		out.println ("\t\"crnDot\": \"digraph BiVeSexport {[...]}\",");
		out.println ("\t\"reportHtml\": \"SBML Differences[...]\"");
		out.println ("}");
		out.println ("</pre>");
		
		out.println ("</body></html>");
	}
}
