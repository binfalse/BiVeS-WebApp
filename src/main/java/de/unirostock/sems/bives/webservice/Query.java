/**
 * 
 */
package de.unirostock.sems.bives.webservice;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.bives.Executer;
import de.unirostock.sems.bives.api.CellMLDiff;
import de.unirostock.sems.bives.api.Diff;
import de.unirostock.sems.bives.api.RegularDiff;
import de.unirostock.sems.bives.api.SBMLDiff;
import de.unirostock.sems.bives.ds.cellml.CellMLDocument;
import de.unirostock.sems.bives.ds.sbml.SBMLDocument;
import de.unirostock.sems.bives.ds.xml.TreeDocument;
import de.unirostock.sems.bives.tools.DocumentClassifier;
import de.unirostock.sems.bives.tools.FileRetriever;
import de.unirostock.sems.bives.tools.Tools;


/**
 * @author Martin Scharm
 *
 *
 * curl -d '{"get":["classify"],"files":["http://models.cellml.org/workspace/tyson_1991/rawfile/82d126a515dbaefaa0aeaad959be4293815492dc/tyson_1991.cellml","http://www.ebi.ac.uk/biomodels-main/download?mid=BIOMD0000000459","/file/to/b"]}' 'http://192.168.0.10:8080/bives/
 * curl -d '{"get":["xmldiff","classify"],"files":["http://models.cellml.org/workspace/tyson_1991/rawfile/82d126a515dbaefaa0aeaad959be4293815492dc/tyson_1991.cellml","http://www.ebi.ac.uk/biomodels-main/download?mid=BIOMD0000000459"]}' 'http://192.168.0.10:8080/bives/
 * curl -d '{"get":["xmldiff","classify"],"files":["http://models.cellml.org/workspace/tyson_1991/rawfile/82d126a515dbaefaa0aeaad959be4293815492dc/tyson_1991.cellml","http://models.cellml.org/workspace/aguda_b_1999/rawfile/56788658c953e1d0a6bc745b81bdb0c0c20e9821/aguda_b_1999.cellml"]}' http://192.168.0.10:8080/bives
 * curl -d '{"get":["xmldiff","classify"],"files":["http://models.cellml.org/workspace/tyson_1991/rawfile/82d126a515dbaefaa0aeaad959be4293815492dc/tyson_1991.cellml","http://models.cellml.org/workspace/aguda_b_1999/rawfile/56788658c953e1d0a6bc745b81bdb0c0c20e9821/aguda_b_1999.cellml"]}' http://192.168.0.10:8080/bives
 * curl -d '{"get":["xmldiff","classify"],"files":["http://budhat.sems.uni-rostock.de/download?downloadModel=24","http://budhat.sems.uni-rostock.de/download?downloadModel=25"]}' http://192.168.0.10:8080/bives
 * curl -d '{"get":["xmldiff","crngraphml", "classify"],"files":["http://budhat.sems.uni-rostock.de/download?downloadModel=24","http://budhat.sems.uni-rostock.de/download?downloadModel=25"]}' http://192.168.0.10:8080/bives
 * curl -d '{"get":["crngraphml","xmldiff","classify"],"files":["http://budhat.sems.uni-rostock.de/download?downloadModel=24","http://budhat.sems.uni-rostock.de/download?downloadModel=25"]}' http://bives.sems.uni-rostock.de
 * 
 * curl -d '{"get":["crngraphml","xmldiff","classify"],"files":[
 * 
 * ]}' http://bives.sems.uni-rostock.de
 * 
 * "http://www.ebi.ac.uk/biomodels-main/download?mid=BIOMD0000000459"
 * "http://budhat.sems.uni-rostock.de/download?downloadModel=24","http://budhat.sems.uni-rostock.de/download?downloadModel=25"
 * "http://models.cellml.org/workspace/aguda_b_1999/rawfile/dd3e88430ad4efcff336a505d779a3a6064cc6a8/aguda_b_1999.cellml"
 * "http://models.cellml.org/workspace/aguda_b_1999/rawfile/dd459c62947886aec9fbb50a97d243fd78e3c7ba/aguda_b_1999.cellml"
 * 
 */

public class Query
extends HttpServlet
{
	private static final long	serialVersionUID	= 6399318158697560244L;
	protected PrintWriter				out;
	

	private static final JSONObject parseRequest (HttpServletRequest request) throws IOException, ParseException
	{
		StringBuilder sb = new StringBuilder();
	  BufferedReader br = request.getReader();
    String str;
    while ((str = br.readLine()) != null)
        sb.append(str);
    
    JSONParser parser = new JSONParser();
    
		String s = sb.toString ();
		if (s.length () > 0)
			return (JSONObject)parser.parse(s);
		else
			return null;
	}
	

	private JSONObject toReturn;
	private JSONArray err;
	
	@SuppressWarnings("unchecked")
	protected void doPost (HttpServletRequest request, HttpServletResponse response)
		throws ServletException,
			IOException
	{
		// we don't want to use local files.
		FileRetriever.FIND_LOCAL = false;
		
		
		// here comes the magic :D
		request.setCharacterEncoding ("UTF-8");
		out = response.getWriter ();
		response.setContentType("application/json");

  	toReturn = new JSONObject ();
  	err = new JSONArray ();

		QueryExecuter qe = new QueryExecuter ();
		try
		{
		
			JSONObject req = parseRequest (request);
			qe.executeQuery (req, toReturn, err);

		}
		catch (Exception e)
		{
			LOGGER.error ("post request processing threw an error", e);
			err.add ("Error: " + e.getMessage ());
		}
		
		if (err.size () > 0)
		{
			err.add ("send get request to see a usage.");
			toReturn.put ("error", err);
			LOGGER.error ("post request resulted in " + err.size () + " errors: " + err);
		}
		
		out.println (toReturn);
	}
	
	protected void doGet (HttpServletRequest request, HttpServletResponse response)
		throws ServletException,
			IOException
	{
    response.setContentType ("text/html");
    request.setCharacterEncoding ("UTF-8");
		response.setStatus (HttpServletResponse.SC_BAD_REQUEST);
		
		out = response.getWriter ();
		
		// just to learn how a requst looks like:
		// LOGGER.setMinLevel (LOGGER.DEBUG);
		if (LOGGER.isDebugEnabled ())
			debugRequest (request);
		
		new QueryExecuter ().printUsage ("", out);
	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	private void debugRequest (HttpServletRequest request)
	{
		if (!LOGGER.isDebugEnabled ())
			return;
		
		LOGGER.debug ("debugRequest:");
		
		class S2
		{
			public S2 (String key, String value)
			{
				this.key = key;
				this.value = value;
			}
			public String key, value;
		}
		
		Vector<S2> vars = new Vector<S2> ();
		int max = 48;

	  Enumeration<String> e = request.getHeaderNames();
    while (e.hasMoreElements())
    {
      String header = e.nextElement();
      if (header.length () > max)
      	max = header.length ();
      vars.add (new S2 (header, request.getHeader(header)));
    }
    
		e = request.getParameterNames();
    while (e.hasMoreElements())
    {
      String paramName = e.nextElement();
      if (paramName.length () > max)
      	max = paramName.length ();
      vars.add (new S2 (paramName, request.getParameter(paramName)));
    }
    
    Cookie[] cookies  = request.getCookies ();
    if (cookies != null)
	    for (Cookie cookie : cookies)
	    {
	    	String name = "cookie: " + cookie.getDomain () + ":" + cookie.getPath () + ":" + cookie.getName ();
	      if (name.length () > max)
	      	max = name.length ();
	      vars.add (new S2 (name, cookie.getValue () + " ("+cookie.getMaxAge ()+","+cookie.getSecure ()+","+cookie.getComment ()+")"));
	    }

    vars.add (new S2 ("request.getRequestURI()", request.getRequestURI()));
    vars.add (new S2 ("request.getRequestedSessionId()", request.getRequestedSessionId()));
    vars.add (new S2 ("request.getRemoteUser()", request.getRemoteUser()));
    vars.add (new S2 ("request.getRemoteAddr()", request.getRemoteAddr()));
    vars.add (new S2 ("request.getRequestURL()", request.getRequestURL().toString ()));
		vars.add (new S2 ("request.getQueryString()", request.getQueryString()));
		vars.add (new S2 ("new File (\".\").getAbsolutePath ()", new File (".").getAbsolutePath ()));
		vars.add (new S2 ("request.getPathInfo ()", request.getPathInfo ()));
		vars.add (new S2 ("request.getPathTranslated ()", request.getPathTranslated ()));
		vars.add (new S2 ("request.getContextPath ()", request.getContextPath ()));
		vars.add (new S2 ("request.getRealPath (request.getServletPath ())", request.getRealPath (request.getServletPath ())));
		vars.add (new S2 ("request.getServletPath ()", request.getServletPath ()));
		vars.add (new S2 ("getServletContext ().getContextPath ()", getServletContext ().getContextPath ()));
		vars.add (new S2 ("getServletContext ().getRealPath (\".\")", getServletContext ().getRealPath (".")));
		
		for (S2 s2 : vars)
			LOGGER.debug (String.format ("  %-"+max+"s  %s", s2.key, s2.value));
	}
	
}
