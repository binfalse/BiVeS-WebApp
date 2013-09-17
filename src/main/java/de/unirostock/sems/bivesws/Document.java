/**
 * 
 */
package de.unirostock.sems.bivesws;

import java.io.File;

import de.unirostock.sems.bives.ds.cellml.CellMLDocument;
import de.unirostock.sems.bives.ds.sbml.SBMLDocument;
import de.unirostock.sems.bives.ds.xml.TreeDocument;


/**
 * @author martin
 *
 */
public class Document
{
		public File file;
		public int type;
		public TreeDocument xml;
		public SBMLDocument sbml;
		public CellMLDocument cellml;
		public String id;
		public String content;
		public Document (String id, String content)
		{
			this.id = id;
			this.content = content;
		}
	
}
