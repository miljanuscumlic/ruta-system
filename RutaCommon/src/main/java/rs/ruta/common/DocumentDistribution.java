package rs.ruta.common;

import oasis.names.specification.ubl.schema.xsd.catalogue_21.CatalogueType;
import oasis.names.specification.ubl.schema.xsd.cataloguedeletion_21.CatalogueDeletionType;
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

	/**Sets the document.
	 * @param document
	 * @throws DocumentException if document's type is not permissible
	 */
	public void setDocument(Object document) throws DocumentException
	{
		if(!isValidDocumentType(document))
			throw new DocumentException("Not a valid document type for the distribution.");
		this.document = document;
	}

	/**Checks whether the document type is a valid one for the distribution.
	 * @param document document which type is to be checked
	 */
	private boolean isValidDocumentType(Object document)
	{
		Class<?> documentClazz = document.getClass();
		if(documentClazz == CatalogueType.class || documentClazz == PartyType.class
				|| documentClazz == CatalogueDeletionType.class
				|| documentClazz == DeregistrationNotice.class) //TODO put all other valid document types
			return true;
		else
			return false;
	}

	public Class<?> getDocumentClass()
	{
		return document.getClass();
	}

}
