package rs.ruta.common;

import oasis.names.specification.ubl.schema.xsd.catalogue_21.CatalogueType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;

public class DocumentDistribution
{
	private Followers followers;
	private Object document;

	public DocumentDistribution() { }

	public DocumentDistribution(Object document, Followers followers) throws DocumentException
	{
		setDocument(document);
		this.followers = followers;
	}
	public Followers getFollowers()
	{
		return followers;
	}
	public void setFollowers(Followers followers)
	{
		this.followers = followers;
	}
	public Object getDocument()
	{
		return document;
	}

	public void setDocument(Object document) throws DocumentException
	{
		if(!isValidDocumentType(document))
			throw new DocumentException("Not a valid document type for the distribution.");
		this.document = document;
	}

	/**Checks whether the document type is a valid one for the distribution.
	 * @param document
	 */
	private boolean isValidDocumentType(Object document)
	{
		Class<?> documentClazz = document.getClass();
		if(documentClazz == CatalogueType.class || documentClazz == PartyType.class)
			return true;
		else
			return false;
	}

	public Class<?> getDocumnetClass()
	{
		return document.getClass();
	}

}
