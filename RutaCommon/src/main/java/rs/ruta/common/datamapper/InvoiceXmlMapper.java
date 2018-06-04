package rs.ruta.common.datamapper;

import javax.xml.bind.JAXBElement;

import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;
import oasis.names.specification.ubl.schema.xsd.invoice_21.ObjectFactory;

public class InvoiceXmlMapper extends XmlMapper<InvoiceType>
{
	final private static String objectPackageName = "oasis.names.specification.ubl.schema.xsd.invoice_21";
	final private static String collectionPath = "/invoice";

	public InvoiceXmlMapper(DatastoreConnector connector) throws DetailException
	{
		super(connector);
	}

	@Override
	protected Class<InvoiceType> getObjectClass()
	{
		return InvoiceType.class;
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
	protected JAXBElement<InvoiceType> getJAXBElement(InvoiceType object)
	{
		return new ObjectFactory().createInvoice(object);
	}

	@Override
	protected String doPrepareAndGetID(InvoiceType invoice, String username, DSTransaction transaction)
			throws DetailException
	{
		return invoice.getUUIDValue();
	}

}