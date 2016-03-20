/**
 * 
 */
package de.unirostock.sems.bives.webservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import de.binfalse.bflog.LOGGER;
import de.binfalse.bfutils.FileRetriever;
import de.binfalse.bfutils.GeneralTools;
import de.unirostock.sems.bives.Executer;
import de.unirostock.sems.bives.Main;



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
	
	/** Pattern to distinguish xml files from URLs. */
	public static final Pattern	XML_PATTERN	= Pattern.compile ("^\\s*<.*",
																						Pattern.DOTALL);
	
	/** The Constant NEWLINE. */
	public static final String NEWLINE = System.getProperty("line.separator");
	
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
		JSONArray jArr = (JSONArray) jObj.get (REQ_WANT);
    List<String> args = new ArrayList<String> ();
    
		LOGGER.setMinLevel (LOGGER.ERROR);
		for (int i = 0; i < jArr.size (); i++)
		{
			String arg = ((String) jArr.get (i));
			
			
			args.add ("--" + arg);
		}
		
		// which files to use?
		jArr = (JSONArray) jObj.get (REQ_FILES);
		LOGGER.info ("following files to be used: " + jArr);
		
		// some general checks
		if (jArr.size () < 1)
			throw new IllegalArgumentException ("found no files.");
		if (jArr.size () > 2)
			throw new IllegalArgumentException (
				"found more than 2 files, not supported.");
		
		for (Object o : jArr)
			args.add ((String) o);
		
		String [] arguments = new String [args.size ()];
		for (int i = 0; i < arguments.length; i++)
			arguments[i] = args.get (i);
		CommandLine line = Main.parseCommandLine (arguments, exe);
		
		/*if (wanted < 1)
			throw new IllegalArgumentException (
				"nothing to do. (no options provided?)");*/

    List<Exception> errors = new ArrayList<Exception> ();
		// single or compare mode?
    try
    {
			if (jArr.size () == 1)
			{
				exe.executeSingle ((String) jArr.get (0), toReturn, line, errors);
			}
			else
			{
				exe.executeCompare ((String) jArr.get (0), (String) jArr.get (1), toReturn,
					line, errors);
			}
    }
    catch (Exception e)
    {
    	LOGGER.error (e, "cannot execute query on bives web service");
    	err.add ("cannot execute: " + e);
    	throw e;
    }
    
  	for (Exception e : errors)
  	{
  		LOGGER.error (e, "experienced errors");
  		err.add ("ERROR: " + e);
  	}
		// done...
	}
	
	
	/**
	 * Prepares the usage including an example.
	 *
	 * @return the string
	 */
	public final String usage ()
	{
		
		Options options = exe.getOptions ();
		HashMap<String, Option> comparison = new HashMap<String, Option> ();
		HashMap<String, Option> single = new HashMap<String, Option> ();
		HashMap<String, Option> server = new HashMap<String, Option> ();
		HashMap<String, Option> general = new HashMap<String, Option> ();
		
		for (Option o : options.getOptions ())
		{
			if (
				o.getLongOpt ().equals (Executer.REQ_DEBUG) || 
				o.getLongOpt ().equals (Executer.REQ_JSON) || 
				o.getLongOpt ().equals (Executer.REQ_XML) || 
				o.getLongOpt ().equals (Executer.REQ_OUT) || 
				o.getLongOpt ().equals (Executer.REQ_HELP) || 
				o.getLongOpt ().equals (Executer.REQ_DEBUGG)
				)
				server.put (o.getLongOpt (), o);
			
			
			else if (
				o.getLongOpt ().equals (Executer.REQ_WANT_CELLML) ||
				o.getLongOpt ().equals (Executer.REQ_WANT_REGULAR) ||
				o.getLongOpt ().equals (Executer.REQ_WANT_DOCUMENTTYPE) ||
				o.getLongOpt ().equals (Executer.REQ_WANT_META) ||
				o.getLongOpt ().equals (Executer.REQ_WANT_SBML)
				)
				general.put (o.getLongOpt (), o);
			
			
			else if (
				o.getLongOpt ().equals (Executer.REQ_WANT_SINGLE_COMP_HIERARCHY_DOT) ||
				o.getLongOpt ().equals (Executer.REQ_WANT_SINGLE_COMP_HIERARCHY_GRAPHML) ||
				o.getLongOpt ().equals (Executer.REQ_WANT_SINGLE_COMP_HIERARCHY_JSON) ||
				o.getLongOpt ().equals (Executer.REQ_WANT_SINGLE_FLATTEN) ||
				o.getLongOpt ().equals (Executer.REQ_WANT_SINGLE_REACTIONS_DOT) ||
				o.getLongOpt ().equals (Executer.REQ_WANT_SINGLE_REACTIONS_GRAPHML) ||
				o.getLongOpt ().equals (Executer.REQ_WANT_SINGLE_REACTIONS_JSON) ||
				o.getLongOpt ().equals (Executer.REQ_WANT_SINGLE_REACTIONS_DOT2) ||
				o.getLongOpt ().equals (Executer.REQ_WANT_SINGLE_REACTIONS_GRAPHML2) ||
				o.getLongOpt ().equals (Executer.REQ_WANT_SINGLE_REACTIONS_JSON2) ||
				o.getLongOpt ().equals (Executer.REQ_WANT_SINGLE_COMP_HIERARCHY_DOT)
				)
				single.put (o.getLongOpt (), o);
			
			
			else if (
				o.getLongOpt ().equals (Executer.REQ_WANT_DIFF) ||
				o.getLongOpt ().equals (Executer.REQ_INC_ANNO) ||
				o.getLongOpt ().equals (Executer.REQ_SEP_ANNO) ||
				o.getLongOpt ().equals (Executer.REQ_WANT_REPORT_MD) ||
				o.getLongOpt ().equals (Executer.REQ_WANT_REPORT_RST) ||
				o.getLongOpt ().equals (Executer.REQ_WANT_REPORT_HTML) ||
				o.getLongOpt ().equals (Executer.REQ_WANT_REPORT_HTML_FP) ||
				o.getLongOpt ().equals (Executer.REQ_WANT_REACTIONS_DOT) ||
				o.getLongOpt ().equals (Executer.REQ_WANT_REACTIONS_GRAPHML) ||
				o.getLongOpt ().equals (Executer.REQ_WANT_REACTIONS_JSON) ||
				o.getLongOpt ().equals (Executer.REQ_WANT_REACTIONS_DOT2) ||
				o.getLongOpt ().equals (Executer.REQ_WANT_REACTIONS_GRAPHML2) ||
				o.getLongOpt ().equals (Executer.REQ_WANT_REACTIONS_JSON2) ||
				o.getLongOpt ().equals (Executer.REQ_WANT_COMP_HIERARCHY_DOT) ||
				o.getLongOpt ().equals (Executer.REQ_WANT_COMP_HIERARCHY_GRAPHML) ||
				o.getLongOpt ().equals (Executer.REQ_WANT_COMP_HIERARCHY_JSON) ||
				o.getLongOpt ().equals (Executer.REQ_WANT_NEGLECT_NAMES) ||
				o.getLongOpt ().equals (Executer.REQ_WANT_MATCHING_IDS) ||
				o.getLongOpt ().equals (Executer.REQ_WANT_STRICT_NAMES) ||
				o.getLongOpt ().equals (Executer.REQ_WANT_COMP_HIERARCHY_DOT)
				
				)
				comparison.put (o.getLongOpt (), o);
			
			
			else
				LOGGER.error ("didn't process all possible options : " + o);
		}
		
		StringBuilder str = new StringBuilder (NEWLINE);
		
		SortedSet<String> compOptions = new TreeSet<String> (comparison.keySet ());
		int longest = 0;
		for (String key : compOptions)
		{
			if (key.length () > longest)
				longest = key.length ();
		}
		SortedSet<String> singleOptions = new TreeSet<String> (single.keySet ());
		for (String key : singleOptions)
		{
			if (key.length () > longest)
				longest = key.length ();
		}
		SortedSet<String> generalOptions = new TreeSet<String> (general.keySet ());
		for (String key : generalOptions)
		{
			if (key.length () > longest)
				longest = key.length ();
		}
		
		longest += 2;
		
		
		str.append ("COMPARISON COMMANDS").append (NEWLINE);
		
		for (String key : compOptions)
			str.append ("\t")
			.append (key)
			.append (GeneralTools.repeat (" ", longest - key.length ()))
			.append (comparison.get (key).getDescription ())
			.append (NEWLINE);
		str.append (NEWLINE);
		
		str.append ("ADDITIONAL COMMANDS for single files").append (NEWLINE);
		for (String key : singleOptions)
		{
			str.append ("\t")
			.append (key)
			.append (GeneralTools.repeat (" ", longest - key.length ()))
			.append (single.get (key).getDescription ())
			.append (NEWLINE);
		}
		str.append (NEWLINE);

		str.append ("GENERAL OPTIONS").append (NEWLINE);
		
		for (String key : generalOptions)
		{
			str.append ("\t")
			.append (key)
			.append (GeneralTools.repeat (" ", longest - key.length ()))
			.append (general.get (key).getDescription ())
			.append (NEWLINE);
		}
		
		return str.toString ();
	}
}
