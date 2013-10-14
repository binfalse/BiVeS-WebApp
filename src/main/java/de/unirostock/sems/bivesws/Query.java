/**
 * 
 */
package de.unirostock.sems.bivesws;

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

import de.unirostock.sems.bives.api.CellMLDiff;
import de.unirostock.sems.bives.api.Diff;
import de.unirostock.sems.bives.api.RegularDiff;
import de.unirostock.sems.bives.api.SBMLDiff;
import de.unirostock.sems.bives.ds.cellml.CellMLDocument;
import de.unirostock.sems.bives.ds.sbml.SBMLDocument;
import de.unirostock.sems.bives.ds.xml.TreeDocument;
import de.unirostock.sems.bives.tools.DocumentClassifier;
import de.unirostock.sems.bives.tools.FileRetriever;


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
	protected PrintWriter				out;
	private JSONObject toReturn;
	private JSONArray err;

	
	
	
	@SuppressWarnings("unchecked")
	protected void doPost (HttpServletRequest request, HttpServletResponse response)
		throws ServletException,
			IOException
	{
		FileRetriever.FIND_LOCAL = false;
		
		
		// here comes the magic :D
		request.setCharacterEncoding ("UTF-8");
		out = response.getWriter ();
		response.setContentType("application/json");
  	toReturn = new JSONObject ();
  	err = new JSONArray ();
		//Vector<Input> readableFiles = new Vector<Input> ();
  	
		try
		{
			Request r = new Request (parseRequest (request));

			Vector<Document> files = r.getFiles ();
			int wanted = r.getWanted ();
			System.out.println (files);
			System.out.println (wanted);
			
			if (files.size () < 1)
				throw new IllegalArgumentException ("found no files.");
			
			if (wanted < 1)
				throw new IllegalArgumentException ("nothing to do. (no get?)");
			
			
			
			
			getFiles (files);
			
			try
			{
				DocumentClassifier classifier = new DocumentClassifier ();
				for (Document in : files)
				{
							in.type = classifier.classify (in.file);
							in.xml = classifier.getXmlDocument ();
							in.cellml = classifier.getCellMlDocument ();
							in.sbml = classifier.getSbmlDocument ();
				}
			}
			catch (ParserConfigurationException e)
			{
				err.add ("cannot instantiate DocumentClassifier");
			}
			

			if ((wanted & Request.WANT_DOCUMENTTYPE) > 0)
			{
				JSONObject classRes = new JSONObject ();
				for (Document in : files)
				{
					JSONArray res = new JSONArray ();
					if ((in.type & DocumentClassifier.XML) != 0)
						res.add ("XML");
					if ((in.type & DocumentClassifier.CELLML) != 0)
						res.add ("CellML");
					if ((in.type & DocumentClassifier.SBML) != 0)
						res.add ("SBML");
					classRes.put (in.id, res);
				}
				toReturn.put ("documentType", classRes);
			}
			
			if ((wanted & Request.WANT_META) > 0)
			{
				JSONObject classRes = new JSONObject ();
				for (Document in : files)
				{
					JSONObject res = new JSONObject ();
					if (in.sbml != null)
					{
						res.put ("sbmlVersion", in.sbml.getVersion ());
						res.put ("sbmlLevel", in.sbml.getLevel ());
						res.put ("modelId", in.sbml.getModel ().getID ());
						res.put ("modelName", in.sbml.getModel ().getName ());
					}
					if (in.cellml != null)
					{
						res.put ("containsImports", in.cellml.containsImports ());
						res.put ("modelName", in.cellml.getModel ().getName ());
					}
					if (in.xml != null)
					{
						res.put ("nodeStats", in.xml.getNodeStats ());
					}
					classRes.put (in.id, res);
				}
				toReturn.put ("meta", classRes);
			}
			
			if ((wanted & ~(Request.WANT_DOCUMENTTYPE | Request.WANT_META)) > 0)
			{
				if (files.size () == 2)
				{
					Document a = files.elementAt (0);
					Document b = files.elementAt (1);
					
					int common = a.type & b.type;
					

					if ((common & DocumentClassifier.CELLML) != 0)
					{
						// do CellML
						System.out.println ("doing CELLML");
						CellMLDiff diff = new CellMLDiff (a.cellml, b.cellml);
						diff.mapTrees ();
						answer (diff, wanted);
					}
					else if ((common & DocumentClassifier.SBML) != 0)
					{
						// do SBML
						System.out.println ("doing SBML");
						SBMLDiff diff = new SBMLDiff (a.sbml, b.sbml);
						diff.mapTrees ();
						answer (diff, wanted);
					}
					else if ((common & DocumentClassifier.XML) != 0)
					{
						// do XML
						System.out.println ("doing XML");
						RegularDiff diff = new RegularDiff (a.xml, b.xml);
						diff.mapTrees ();
						answer (diff, wanted);
					}
				}
				else
					throw new IllegalArgumentException ("need exactly 2 files to do a diff.");
			}
			
			
			
			
			
			
			

			/*Vector<Integer> classification = classify (readableFiles);
			int common = Integer.MAX_VALUE;
			JSONObject classRes = new JSONObject ();
			for (int i = 0; i < readableFiles.size (); i++)
			{
				JSONArray res = new JSONArray ();
				int type = classification.get (i);
				common &= type;
				if ((type & DocumentClassifier.XML) != 0)
					res.add ("XML");
				if ((type & DocumentClassifier.CELLML) != 0)
					res.add ("CellML");
				if ((type & DocumentClassifier.SBML) != 0)
					res.add ("SBML");
				classRes.put (i, res);
			}
			if ((wanted & Request.WANT_DOCUMENTTYPE) > 0)
				toReturn.put ("documenttype", classRes);
			
			
			
			
			if (files.size () == 2 || wanted == Request.WANT_DOCUMENTTYPE)
			{
				// download files
				//System.out.println ("getting files:");
				
				//System.out.println ("num readable: " + readableFiles.size ());
				
				/*Vector<Integer> classification = classify (readableFiles);
				int common = Integer.MAX_VALUE;
				if ((wanted & Request.WANT_DOCUMENTTYPE) > 0)
				{
					
				}*/
				
				/*if (readableFiles.size () == 2)
				{
					if ((common & DocumentClassifier.CELLML) != 0)
					{
						// do CellML
						System.out.println ("doing CELLML");
						CellMLDiff diff = new CellMLDiff (readableFiles.elementAt (0), readableFiles.elementAt (1));
						diff.mapTrees ();
						answer (diff, wanted);
						//toReturn.put ("xmldiff", diff.getDiff ());
					}
					else if ((common & DocumentClassifier.SBML) != 0)
					{
						// do SBML
						System.out.println ("doing SBML");
						SBMLDiff diff = new SBMLDiff (readableFiles.elementAt (0), readableFiles.elementAt (1));
						diff.mapTrees ();
						answer (diff, wanted);
						//toReturn.put ("xmldiff", diff.getDiff ());
					}
					else if ((common & DocumentClassifier.XML) != 0)
					{
						// do XML
						System.out.println ("doing XML");
						RegularDiff diff = new RegularDiff (readableFiles.elementAt (0), readableFiles.elementAt (1));
						diff.mapTrees ();
						answer (diff, wanted);
						//toReturn.put ("xmldiff", diff.getDiff ());
					}
				}
			}
			else
				throw new IllegalArgumentException ("need exactly 2 files to do a diff.");*/
			
				
			
			for (Document f : files)
				f.file.delete ();
			
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			err.add ("Error: " + e.getMessage ());
		}
		
		if (err.size () > 0)
		{
			err.add ("send get request to see a usage.");
			/*String [] errs = new String [err.size ()];
			for (int i = 0; i < err.size (); i++)
				errs[i] = err.elementAt (i);*/
			toReturn.put ("error", err);
		}
		
		out.println (toReturn);
	}
	
	@SuppressWarnings("unchecked")
	private void answer (Diff diff, int wanted) throws ParserConfigurationException
	{
		if ((wanted & Request.WANT_DIFF) > 0)
			toReturn.put (Request.REQ_WANT_DIFF, diff.getDiff ());
		
		if ((wanted & Request.WANT_CRN_GRAPHML) > 0)
			toReturn.put (Request.REQ_WANT_CRN_GRAPHML, diff.getCRNGraphML ());
		
		if ((wanted & Request.WANT_CRN_DOT) > 0)
			toReturn.put (Request.REQ_WANT_CRN_DOT, diff.getCRNDotGraph ());
		
		if ((wanted & Request.WANT_CRN_JSON) > 0)
			toReturn.put (Request.REQ_WANT_CRN_JSON, diff.getCRNJsonGraph ());
		
		if ((wanted & Request.WANT_COMP_HIERARCHY_DOT) > 0)
			toReturn.put (Request.REQ_WANT_COMP_HIERARCHY_DOT, diff.getHierarchyDotGraph ());
		
		if ((wanted & Request.WANT_COMP_HIERARCHY_JSON) > 0)
			toReturn.put (Request.REQ_WANT_COMP_HIERARCHY_JSON, diff.getHierarchyJsonGraph ());
		
		if ((wanted & Request.WANT_COMP_HIERARCHY_GRAPHML) > 0)
			toReturn.put (Request.REQ_WANT_COMP_HIERARCHY_GRAPHML, diff.getHierarchyGraphML ());
		
		if ((wanted & Request.WANT_REPORT_HTML) > 0)
			toReturn.put (Request.REQ_WANT_REPORT_HTML, diff.getHTMLReport ());
		
		if ((wanted & Request.WANT_REPORT_MD) > 0)
			toReturn.put (Request.REQ_WANT_REPORT_MD, diff.getMarkDownReport ());
		
		if ((wanted & Request.WANT_REPORT_RST) > 0)
			toReturn.put (Request.REQ_WANT_REPORT_RST, diff.getReStructuredTextReport ());
	}
	
	@SuppressWarnings("unchecked")
	private void getFiles (Vector<Document> files)
	{
		for (Document file : files)
		{
			try
			{
				File f = File.createTempFile ("bives-webservice", "xml");
				f.deleteOnExit ();
				if (file.content.matches ("\\s*<\\?"))
				{
					// string
					PrintWriter out = new PrintWriter (f);
					out.print (file);
					out.close ();
				}
				else
				{
					// download
					URI fileUri = FileRetriever.getUri (file.content, null);
					FileRetriever.getFile (fileUri, f);
				}
				file.file = f;
			}
			catch (IOException | URISyntaxException e)
			{
				err.add ("cannot read " + file);
				files.remove (file);
			}
		}
		//return readables;
	}

	/*@SuppressWarnings("unchecked")
	private Vector<Integer> classify (Vector<File> files)
	{
		Vector<Integer> classification = new Vector<Integer> ();
		try
		{
			//JSONObject classRes = new JSONObject ();
			DocumentClassifier classifier = new DocumentClassifier ();
			for (int i = 0; i < files.size (); i++)
			{
				//JSONArray res = new JSONArray ();
				int type = classifier.classify (files.elementAt (i));
				classification.add (type);
				/*if ((type & DocumentClassifier.XML) != 0)
					res.add ("XML");
				if ((type & DocumentClassifier.CELLML) != 0)
					res.add ("CellML");
				if ((type & DocumentClassifier.SBML) != 0)
					res.add ("SBML");
				classRes.put (i, res);*/
			/*}
			//toReturn.put ("classification", classRes);
		}
		catch (ParserConfigurationException e)
		{
			err.add ("cannot instantiate DocumentClassifier");
		}
		return classification;
	}*/
	

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
	
	
	
	private static final void printUsage (PrintWriter out)
	{
		out.println ("to learn how to use this web service please take a look at http://sems.uni-rostock.de/");
	}
	
	protected void doGet (HttpServletRequest request, HttpServletResponse response)
		throws ServletException,
			IOException
	{
    response.setContentType ("text/html");
    request.setCharacterEncoding ("UTF-8");
		out = response.getWriter ();
		
		debugRequest (request);
		printUsage (out);
		
		
	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	private void debugRequest (HttpServletRequest request)
	{
		System.out.println ("debugRequest:");
		
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
		{
			System.out.println(String.format ("  %-"+max+"s  %s", s2.key, s2.value));
		}
	}
	
}
