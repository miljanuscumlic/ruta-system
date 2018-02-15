package rs.ruta.common;

import java.util.List;

public class DocBox
{
	private Object document;
	private String docID;
	List<String> docCollectionPaths;

	public DocBox()
	{

	}

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
