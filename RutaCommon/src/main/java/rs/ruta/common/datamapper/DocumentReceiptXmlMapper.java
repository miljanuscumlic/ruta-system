package rs.ruta.common.datamapper;

import javax.xml.bind.JAXBElement;

import rs.ruta.common.DocumentReceipt;
import rs.ruta.common.ObjectFactory;

public class DocumentReceiptXmlMapper extends XmlMapper<DocumentReceipt>
{
	final private static String objectPackageName = "rs.ruta.common";
	final private static String collectionPath = "/document-receipt";

	public DocumentReceiptXmlMapper(DatastoreConnector connector) throws DetailException
	{
		super(connector);
	}

	@Override
	protected Class<DocumentReceipt> getObjectClass()
	{
		return DocumentReceipt.class;
	}

	@Override
	protected String getObjectPackageName()
	{
		return objectPackageName;
	}

	@Override
	protected String getCollectionPath()
	{
		return collectionPath;
	}

	@Override
	protected JAXBElement<DocumentReceipt> getJAXBElement(DocumentReceipt object)
	{
		return new ObjectFactory().createDocumentReceipt(object);
	}

	@Override
	protected String doPrepareAndGetID(DocumentReceipt docReceipt, String username, DSTransaction transaction)
			throws DetailException
	{
		return docReceipt.getIDValue();
	}
}