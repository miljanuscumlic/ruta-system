package rs.ruta.common;

import oasis.names.specification.ubl.schema.xsd.applicationresponse_21.ApplicationResponseType;
import oasis.names.specification.ubl.schema.xsd.catalogue_21.CatalogueType;
import oasis.names.specification.ubl.schema.xsd.cataloguedeletion_21.CatalogueDeletionType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import oasis.names.specification.ubl.schema.xsd.order_21.OrderType;
import oasis.names.specification.ubl.schema.xsd.ordercancellation_21.OrderCancellationType;
import oasis.names.specification.ubl.schema.xsd.orderchange_21.OrderChangeType;
import oasis.names.specification.ubl.schema.xsd.orderresponse_21.OrderResponseType;
import oasis.names.specification.ubl.schema.xsd.orderresponsesimple_21.OrderResponseSimpleType;

public class DocumentDistribution
{
	private Associates associates;
	private Object document;

	public DocumentDistribution() { }

	/**
	 * Constructs {@code DocumentDistribution} object
	 * @param document document to distribute
	 * @param associates parties the document to be distributed to
	 * @throws DocumentException if document's type is not permissible for the distribution
	 */
	public DocumentDistribution(Object document, Associates associates) throws DocumentException
	{
		setDocument(document);
		this.associates = associates;
	}

	public Associates getAssociates()
	{
		return associates;
	}

	public void setAssociates(Associates associates)
	{
		this.associates = associates;
	}

	public Object getDocument()
	{
		return document;
	}

	/**
	 * Sets the document.
	 * @param document
	 * @throws DocumentException if document's type is not permissible for the distribution
	 */
	public void setDocument(Object document) throws DocumentException
	{
		if(!isValidDocumentType(document))
			throw new DocumentException("Not a valid document type for the distribution: " + document.getClass().toString() + ".");
		this.document = document;
	}

	/**
	 * Checks whether the document type is a valid one for the distribution.
	 * @param document document which type is to be checked
	 * @result true if document type is a valid one
	 */
	private boolean isValidDocumentType(Object document)
	{
		final Class<?> documentClazz = document.getClass();
		boolean valid = false;
		if(documentClazz == CatalogueType.class ||
				documentClazz == PartyType.class ||
				documentClazz == CatalogueDeletionType.class ||
				documentClazz == DeregistrationNotice.class ||
				documentClazz == OrderType.class ||
				documentClazz == OrderResponseType.class ||
				documentClazz == OrderResponseSimpleType.class ||
				documentClazz == OrderChangeType.class ||
				documentClazz == OrderCancellationType.class ||
				documentClazz == ApplicationResponseType.class)
			//MMM other document types
			valid = true;
		return valid;
	}

	/**
	 * Gets the {@link Class} of distributed document.
	 * @return {@code Class} object of the document
	 */
	public Class<?> getDocumentClass()
	{
		return document.getClass();
	}

}