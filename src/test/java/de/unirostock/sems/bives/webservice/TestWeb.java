/**
 * 
 */
package de.unirostock.sems.bives.webservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Test;

import de.binfalse.bflog.LOGGER;


/**
 * The Class TestWeb.
 *
 * @author Martin Scharm
 */
public class TestWeb
{
	
	/** The Constant FILE1. */
	public static final String FILE1 = "http://budhat.sems.uni-rostock.de/download?downloadModel=24";
	
	/** The Constant FILE2. */
	public static final String FILE2 = "http://budhat.sems.uni-rostock.de/download?downloadModel=25";
	
	/**
	 * Instantiates a new test class.
	 */
	public TestWeb ()
	{
		// here we want to see stack traces...
		LOGGER.setLogStackTrace (true);
	}

	/**
	 * Test local files by URI -> file:/path/to/file.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testLocalFilesTwo ()
	{
		File f = null;
		try
		{
			f = File.createTempFile ("bivesws", "test");
			f.deleteOnExit ();
		}
		catch (IOException e)
		{
			LOGGER.error (e, "cannot run test because i cannot create a temp file");
		}
		
		JSONObject json = new JSONObject ();
		
		JSONArray array = new JSONArray ();
		array.add (f.toURI ().toString ());
		array.add (f.toURI ().toString ());
		json.put ("files", array);
		
		array = new JSONArray ();
		array.add ("SBML");
		array.add ("crnDot");
		array.add ("reportHtml");
		json.put ("commands", array);

		JSONObject toReturn = new JSONObject ();
		JSONArray err = new JSONArray ();
		
		try
		{
			new WebQueryExecuter ().executeQuery (json, toReturn, err);
			fail ("we shouldn't be able to read local files!");
		}
		catch (Exception e)
		{
			assertTrue ("query executer should have produced an error", 0 < err.size ());
		}
	}
	
	/**
	 * Test local files by path -> /path/to/file.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testLocalFilesOne ()
	{
		File f = null;
		try
		{
			f = File.createTempFile ("bivesws", "test");
			f.deleteOnExit ();
		}
		catch (IOException e)
		{
			LOGGER.error (e, "cannot run test because i cannot create a temp file");
		}
		
		JSONObject json = new JSONObject ();
		
		JSONArray array = new JSONArray ();
		array.add (f.getAbsolutePath ());
		array.add (f.getAbsolutePath ());
		json.put ("files", array);
		
		array = new JSONArray ();
		array.add ("SBML");
		array.add ("crnDot");
		array.add ("reportHtml");
		json.put ("commands", array);

		JSONObject toReturn = new JSONObject ();
		JSONArray err = new JSONArray ();
		
		try
		{
			new WebQueryExecuter ().executeQuery (json, toReturn, err);
			fail ("we shouldn't be able to read local files!");
		}
		catch (Exception e)
		{
			assertTrue ("query executer should have produced an error", 0 < err.size ());
		}
	}
	
	
	/**
	 * Test url downloads.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testUrls ()
	{
		JSONObject json = new JSONObject ();
		
		JSONArray array = new JSONArray ();
		array.add (FILE1);
		array.add (FILE2);
		json.put ("files", array);
		
		array = new JSONArray ();
		array.add ("SBML");
		array.add ("crnDot");
		array.add ("reportHtml");
		json.put ("commands", array);

		JSONObject toReturn = new JSONObject ();
		JSONArray err = new JSONArray ();
		
		try
		{
			new WebQueryExecuter ().executeQuery (json, toReturn, err);
			assertEquals ("err should be empty, but apparently there was an error", 0, err.size ());
			// for backwards compatibility -> array should contain 3 results (crnDot + reactionsDot = same)
			assertEquals ("toReturn should contain exactly 3 results", 3, toReturn.size ());
			
			assertNotNull ("toReturn should return a dot graph", toReturn.get ("crnDot"));
			assertTrue ("toReturn should return a non-empty dot graph", ((String) toReturn.get ("crnDot")).length () > 0);
			assertTrue ("toReturn should return a dot graph of type digraph", ((String) toReturn.get ("crnDot")).toLowerCase ().contains ("digraph"));

			assertNotNull ("toReturn should return an html report", toReturn.get ("reportHtml"));
			assertTrue ("toReturn should return a non-empty html report", ((String) toReturn.get ("reportHtml")).length () > 0);
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			fail ("couldn't execute web query: " + e.getMessage ());
		}
		
	}
	
	
	/**
	 * Test url downloads.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testOldAndNewChemicalRN ()
	{
		JSONObject json = new JSONObject ();
		
		JSONArray array = new JSONArray ();
		array.add (FILE1);
		array.add (FILE2);
		json.put ("files", array);
		
		array = new JSONArray ();
		array.add ("SBML");
		array.add ("crnDot");
		array.add ("reportHtml");
		json.put ("commands", array);

		JSONObject toReturn = new JSONObject ();
		JSONArray err = new JSONArray ();
		
		try
		{
			new WebQueryExecuter ().executeQuery (json, toReturn, err);
			assertEquals ("err should be empty, but apparently there was an error", 0, err.size ());
			// for backwards compatibility -> array should contain 3 results (crnDot + reactionsDot = same)
			assertEquals ("toReturn should contain exactly 3 results", 3, toReturn.size ());
			
			assertNotNull ("toReturn should return a dot graph", toReturn.get ("crnDot"));
			assertTrue ("toReturn should return a non-empty dot graph", ((String) toReturn.get ("crnDot")).length () > 0);
			assertTrue ("toReturn should return a dot graph of type digraph", ((String) toReturn.get ("crnDot")).toLowerCase ().contains ("digraph"));

			assertNotNull ("toReturn should return an html report", toReturn.get ("reportHtml"));
			assertTrue ("toReturn should return a non-empty html report", ((String) toReturn.get ("reportHtml")).length () > 0);
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			fail ("couldn't execute web query: " + e.getMessage ());
		}
		

		
		array = new JSONArray ();
		array.add ("SBML");
		array.add ("reactionsDot");
		array.add ("reportHtml");
		json.put ("commands", array);

		toReturn = new JSONObject ();
		err = new JSONArray ();
		
		try
		{
			new WebQueryExecuter ().executeQuery (json, toReturn, err);
			assertEquals ("err should be empty, but apparently there was an error", 0, err.size ());
			assertEquals ("toReturn should contain exactly 2 results", 2, toReturn.size ());
			
			assertNotNull ("toReturn should return a dot graph", toReturn.get ("reactionsDot"));
			assertTrue ("toReturn should return a non-empty dot graph", ((String) toReturn.get ("reactionsDot")).length () > 0);
			assertTrue ("toReturn should return a dot graph of type digraph", ((String) toReturn.get ("reactionsDot")).toLowerCase ().contains ("digraph"));

			assertNotNull ("toReturn should return an html report", toReturn.get ("reportHtml"));
			assertTrue ("toReturn should return a non-empty html report", ((String) toReturn.get ("reportHtml")).length () > 0);
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			fail ("couldn't execute web query: " + e.getMessage ());
		}
		
		
		
	}
	
	/**
	 * Test xml strings.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testStrings ()
	{
		// download file and write to string
		StringBuilder file = new StringBuilder ();
		try
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(new BufferedInputStream(new URL(FILE1).openStream())));
			while (br.ready ())
			{
				file.append (br.readLine () + "\n");
			}
			br.close ();
		}
		catch (IOException e)
		{
			System.err.println ("WARNING: cannot perform check because i'm not able to download " + FILE1);
		}
		
		// create json object that contains xml as strings
		JSONObject json = new JSONObject ();
		
		JSONArray array = new JSONArray ();
		array.add (file.toString ());
		array.add (file.toString ());
		json.put ("files", array);
		
		array = new JSONArray ();
		array.add ("SBML");
		array.add ("crnDot");
		array.add ("reportHtml");
		json.put ("commands", array);

		JSONObject toReturn = new JSONObject ();
		JSONArray err = new JSONArray ();
		
		// run tests
		try
		{
			new WebQueryExecuter ().executeQuery (json, toReturn, err);
			assertEquals ("err should be empty, but apparently there was an error", 0, err.size ());
			// for backwards compatibility -> array should contain 3 results (crnDot + reactionsDot = same)
			assertEquals ("toReturn should contain exactly 3 results", 3, toReturn.size ());
			
			assertNotNull ("toReturn should return a dot graph", toReturn.get ("crnDot"));
			assertTrue ("toReturn should return a non-empty dot graph", ((String) toReturn.get ("crnDot")).length () > 0);
			assertTrue ("toReturn should return a dot graph of type digraph", ((String) toReturn.get ("crnDot")).toLowerCase ().contains ("digraph"));

			assertNotNull ("toReturn should return an html report", toReturn.get ("reportHtml"));
			assertTrue ("toReturn should return a non-empty html report", ((String) toReturn.get ("reportHtml")).length () > 0);
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			fail ("couldn't execute web query: " + e.getMessage ());
		}
	}
	
}
