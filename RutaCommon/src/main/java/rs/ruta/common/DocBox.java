package rs.ruta.common;

import java.util.List;

/**
 *	{@code DocBox} class encapsulates all docBoxes that the document should be inserted into.
 */
public class DocBox
{
	private Object document;
	private String docID;
	/**
	 * Collection paths of all docBoxes.
	 */
	List<String> docCollectionPaths;

	public DocBox() { }

	public DocBox(Object document, List<String> docCollectionPaths, String docID)
	{
		this.document = document;
		this.docCollectionPaths = docCollectionPaths;
		this.docID = docID;
	}

	public Object getDocument()
	{
		return document;
	}

	public void setDocument(Object document)
	{
		this.document = document;
	}

	/**
	 * Gets the list of all docBoxes' paths in which the document should be inserted into.
	 * @return list of collection paths
	 */
	public List<String> getDocCollectionPaths()
	{
		return docCollectionPaths;
	}

	public void setDocCollectionPaths(List<String> docCollectionPaths)
	{
		this.docCollectionPaths = docCollectionPaths;
	}

	public String getDocID()
	{
		return docID;
	}

	public void setDocID(String docID)
	{
		this.docID = docID;
	}

}
