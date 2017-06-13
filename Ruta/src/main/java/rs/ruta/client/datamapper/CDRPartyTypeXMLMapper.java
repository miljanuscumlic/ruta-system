package rs.ruta.client.datamapper;

import javax.xml.bind.JAXBElement;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.*;
import rs.ruta.client.*;

public class CDRPartyTypeXMLMapper<T> extends XMLMapper<T>
{
	private Client client;

	public CDRPartyTypeXMLMapper(Client client, String filename)
	{
		super(filename);
		this.client = client;
	}

	@Override
	public void insertAll()
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
		return (T) client.getCDRParty();
	}
}