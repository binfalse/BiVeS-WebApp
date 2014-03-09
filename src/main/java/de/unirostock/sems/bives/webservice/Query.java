/**
 * 
 */
package de.unirostock.sems.bives.webservice;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.binfalse.bflog.LOGGER;
import de.binfalse.bfutils.FileRetriever;



/**
 * The Class Query to process a query.
 * 
 * @author Martin Scharm
 */

public class Query
	extends HttpServlet
{
	
	private static final long	serialVersionUID	= 6399318158697560244L;
	
	/** toReturn will contain the results. */
	private JSONObject				toReturn;
	
	/** err will -- wait for it -- collect errors. */
	private JSONArray					err;
	
	
	/**
	 * Parses the JSON request.
	 * 
	 * @param request
	 *          the request
	 * @return the supplied JSON object
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 * @throws ParseException
	 *           the parse exception
	 */
	private static final JSONObject parseRequest (HttpServletRequest request)
		throws IOException,
			ParseException
	{
		// read post stuff
		StringBuilder sb = new StringBuilder ();
		BufferedReader br = request.getReader ();
		String str;
		while ( (str = br.readLine ()) != null)
			sb.append (str);
		String s = sb.toString ();
		
		// parse json
		JSONParser parser = new JSONParser ();
		if (s.length () > 0)
			return (JSONObject) parser.parse (s);
		else
			return null;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@SuppressWarnings("unchecked")
	protected void doPost (HttpServletRequest request,
		HttpServletResponse response) throws ServletException, IOException
	{
		// we don't want to use local files.
		FileRetriever.FIND_LOCAL = false;
		LOGGER.setLogStackTrace (true);
		
		// here comes the magic :D
		request.setCharacterEncoding ("UTF-8");
		PrintWriter out = response.getWriter ();
		response.setContentType ("application/json");
		
		// toReturn will contain the results
		toReturn = new JSONObject ();
		// err will -- wait for it -- collect errors
		err = new JSONArray ();
		
		try
		{
			// read the request
			JSONObject req = parseRequest (request);
			if (req == null)
				throw new IOException ("no JSON stream found");
			// call the executer
			new WebQueryExecuter ().executeQuery (req, toReturn, err);
		}
		catch (Exception e)
		{
			LOGGER.error (e, "post request processing threw an error");
			err.add ("Error: " + e.getMessage ());
		}
		
		// anything unexpected?
		if (err.size () > 0)
		{
			err.add ("go to " + request.getRequestURL () + " to get the usage");
			toReturn.put ("error", err);
			LOGGER.error ("post request resulted in ", err.size (), " errors: "
				+ err);
		}
		
		out.println (toReturn);
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet (HttpServletRequest request, HttpServletResponse response)
		throws ServletException,
			IOException
	{
		// this will just print the usage
		response.setContentType ("text/html");
		request.setCharacterEncoding ("UTF-8");
		response.setStatus (HttpServletResponse.SC_BAD_REQUEST);
		
		// just to learn how a request looks like:
		// LOGGER.setMinLevel (LOGGER.DEBUG);
		if (LOGGER.isDebugEnabled ())
			debugRequest (request);
		
		new WebQueryExecuter ().usage (request);
		request.setAttribute ("url", request.getRequestURL());
		
		request.getRequestDispatcher ("/WEB-INF/Usage.jsp").forward (request, response);
	}
	
	
	/**
	 * Debug request, just to get some infos from the servlet.
	 * 
	 * @param request
	 *          the request
	 */
	@SuppressWarnings({"deprecation" })
	private void debugRequest (HttpServletRequest request)
	{
		if (!LOGGER.isDebugEnabled ())
			return;
		
		HashMap<String, String> vars = new HashMap<String, String> ();
		int max = 48;
		
		Enumeration<String> e = request.getHeaderNames ();
		while (e.hasMoreElements ())
		{
			String header = e.nextElement ();
			if (header.length () > max)
				max = header.length ();
			vars.put (header, request.getHeader (header));
		}
		
		e = request.getParameterNames ();
		while (e.hasMoreElements ())
		{
			String paramName = e.nextElement ();
			if (paramName.length () > max)
				max = paramName.length ();
			vars.put (paramName, request.getParameter (paramName));
		}
		
		Cookie[] cookies = request.getCookies ();
		if (cookies != null)
			for (Cookie cookie : cookies)
			{
				String name = "cookie: " + cookie.getDomain () + ":"
					+ cookie.getPath () + ":" + cookie.getName ();
				if (name.length () > max)
					max = name.length ();
				vars.put (name, cookie.getValue () + " (" + cookie.getMaxAge () + ","
					+ cookie.getSecure () + "," + cookie.getComment () + ")");
			}
		
		vars.put ("request.getRequestURI()", request.getRequestURI ());
		vars.put ("request.getRequestedSessionId()",
			request.getRequestedSessionId ());
		vars.put ("request.getRemoteUser()", request.getRemoteUser ());
		vars.put ("request.getRemoteAddr()", request.getRemoteAddr ());
		vars.put ("request.getRequestURL()", request.getRequestURL ().toString ());
		vars.put ("request.getQueryString()", request.getQueryString ());
		vars.put ("new File (\".\").getAbsolutePath ()",
			new File (".").getAbsolutePath ());
		vars.put ("request.getPathInfo ()", request.getPathInfo ());
		vars.put ("request.getPathTranslated ()", request.getPathTranslated ());
		vars.put ("request.getContextPath ()", request.getContextPath ());
		vars.put ("request.getRealPath (request.getServletPath ())",
			request.getRealPath (request.getServletPath ()));
		vars.put ("request.getServletPath ()", request.getServletPath ());
		vars.put ("getServletContext ().getContextPath ()", getServletContext ()
			.getContextPath ());
		vars.put ("getServletContext ().getRealPath (\".\")", getServletContext ()
			.getRealPath ("."));
		
		LOGGER.debug ("debugRequest:");
		SortedSet<String> keys = new TreeSet<String> (vars.keySet ());
		for (String key : keys)
			LOGGER
				.debug (String.format ("  %-" + max + "s  %s", key, vars.get (key)));
	}
	
}
