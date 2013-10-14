/**
 * 
 */
package de.unirostock.sems.bivesws;

import java.util.Vector;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


/**
 * @author Martin Scharm
 *
 */
public class Request
{
	public static final int WANT_DIFF = 1;
	public static final int WANT_DOCUMENTTYPE = 2;
	public static final int WANT_META = 4;
	public static final int WANT_REPORT_MD = 8;
	public static final int WANT_REPORT_HTML = 16;
	public static final int WANT_CRN_GRAPHML = 32;
	public static final int WANT_CRN_DOT = 64;
	public static final int WANT_COMP_HIERARCHY_GRAPHML = 128;
	public static final int WANT_COMP_HIERARCHY_DOT = 256;
	public static final int WANT_REPORT_RST = 512;
	public static final int WANT_COMP_HIERARCHY_JSON = 1024;
	public static final int WANT_CRN_JSON = 2048;

	public static final String REQ_FILES = "files";
	public static final String REQ_WANT = "get";
	public static final String REQ_WANT_META = "meta";
	public static final String REQ_WANT_DOCUMENTTYPE = "documentType";
	public static final String REQ_WANT_DIFF = "xmlDiff";
	public static final String REQ_WANT_REPORT_MD = "reportMd";
	public static final String REQ_WANT_REPORT_RST = "reportRST";
	public static final String REQ_WANT_REPORT_HTML = "reportHtml";
	public static final String REQ_WANT_CRN_GRAPHML = "crnGraphml";
	public static final String REQ_WANT_CRN_DOT = "crnDot";
	public static final String REQ_WANT_CRN_JSON = "crnJson";
	public static final String REQ_WANT_COMP_HIERARCHY_GRAPHML = "compHierarchyGraphml";
	public static final String REQ_WANT_COMP_HIERARCHY_DOT = "compHierarchyDot";
	public static final String REQ_WANT_COMP_HIERARCHY_JSON = "compHierarchyJson";
	
	private Vector<Document> files;
	
	private int wanted;
	
	
	public Request (JSONObject json)
	{
		files = new Vector<Document> ();
		wanted = 0;

		for (Object s : json.keySet ())
		{
			String str = (String) s;
			if (str.equals (REQ_WANT))
			{
				parseWant ((JSONArray) json.get (s));
			}
			else if (str.equals (REQ_FILES))
			{
				parseFiles ((JSONObject) json.get (s));
			}
		}
	}
	
	public Vector<Document> getFiles ()
	{
		return files;
	}
	
	
	public int getWanted ()
	{
		return wanted;
	}
	
	
	private void parseWant (JSONArray json)
	{

		for (int i = 0; i < json.size (); i++)
		{
			String w = (String) json.get (i);
			if (w.equals (REQ_WANT_DIFF))
				wanted |= WANT_DIFF;
			
			else if (w.equals (REQ_WANT_REPORT_MD))
				wanted |= WANT_REPORT_MD;
			else if (w.equals (REQ_WANT_REPORT_RST))
				wanted |= WANT_REPORT_RST;
			else if (w.equals (REQ_WANT_REPORT_HTML))
				wanted |= WANT_REPORT_HTML;
			
			else if (w.equals (REQ_WANT_CRN_GRAPHML))
				wanted |= WANT_CRN_GRAPHML;
			else if (w.equals (REQ_WANT_CRN_DOT))
				wanted |= WANT_CRN_DOT;
			else if (w.equals (REQ_WANT_CRN_JSON))
				wanted |= WANT_CRN_JSON;
			
			else if (w.equals (REQ_WANT_COMP_HIERARCHY_GRAPHML))
				wanted |= WANT_COMP_HIERARCHY_GRAPHML;
			else if (w.equals (REQ_WANT_COMP_HIERARCHY_DOT))
				wanted |= WANT_COMP_HIERARCHY_DOT;
			else if (w.equals (REQ_WANT_COMP_HIERARCHY_JSON))
				wanted |= WANT_COMP_HIERARCHY_JSON;
			
			else if (w.equals (REQ_WANT_DOCUMENTTYPE))
				wanted |= WANT_DOCUMENTTYPE;
			else if (w.equals (REQ_WANT_META))
				wanted |= WANT_META;
			
			/*else if (w.equals ())
				wanted |= ;
			else if (w.equals ())
				wanted |= ;*/
			else
				throw new IllegalArgumentException ("don't understand " + w);
		}
		
		
		
		
	}
	
	private void parseFiles (JSONObject json)
	{
		for (Object id : json.keySet ())
			files.add (new Document ((String) id, (String) json.get (id)));
	}
}
