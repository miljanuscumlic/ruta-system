package rs.ruta.client.datamapper;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import rs.ruta.client.*;

public class CDRPartyTypeXMLFileMapper<T> extends XMLFileMapper<T>
{
	private RutaClient client;

	public CDRPartyTypeXMLFileMapper(RutaClient client, String filename) throws Exception
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
/*		JAXBElement<PartyType> partyElement = (new ObjectFactory()).createParty((PartyType) getObject());
		return partyElement;*/

		QName _Party_QNAME = new QName("urn:rs:ruta:client", "CDRParty");
		JAXBElement<Party> partyElement = new JAXBElement<Party>(_Party_QNAME, Party.class, (Party) getObject());
		return partyElement;


	}

	@SuppressWarnings("unchecked")
	@Override
	public T getObject()
	{
		return (T) client.getCDRParty();
	}
}