package rs.ruta.client.datamapper;

import javax.xml.bind.JAXBElement;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.ObjectFactory;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import rs.ruta.client.*;

public class PartyTypeXMLFileMapper<T> extends XMLFileMapper<T>
{
	private Client client;

	public PartyTypeXMLFileMapper(Client client, String filename) throws Exception
	{
		super(filename);
		this.packageList = "oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21";
		this.client = client;
	}

	@Override
	public void insertAll() throws Exception
	{
		super.insertAll();
	}

	@Override
	protected JAXBElement<?> getJAXBElement()
	{
		JAXBElement<PartyType> partyElement = (new ObjectFactory()).createParty((PartyType) getObject());
		return partyElement;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getObject()
	{
		return (T) client.getMyParty();
	}

}