package rs.ruta.server.datamapper;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import rs.ruta.server.DatabaseException;

public class PartyIDXmlMapper extends XmlMapper<PartyID>
{
	final private static String docPrefix = "";
	final private static String collectionPath = "/system/party-id";
	final private static String objectPackageName = "rs.ruta.server.datamapper";

	public PartyIDXmlMapper() throws DatabaseException { super(); }

	@Override
	public Class<?> getObjectClass() { return PartyID.class; }

	@Override
	public String getObjectPackageName() { return objectPackageName; }

	@Override
	public String getCollectionPath() { return collectionPath; }

	@Override
	public String getDocumentPrefix() { return docPrefix; }

	@Override
	public PartyID getLoadedObject(String id) { return null; }

	@Override
	protected JAXBElement<PartyID> getJAXBElement(PartyID object)
	{
		QName _QNAME = new QName("urn:rs:ruta:services", "PartyID");
		JAXBElement<PartyID> jaxbElement = new JAXBElement<PartyID> (_QNAME, PartyID.class,  object);
		return jaxbElement;
	}

}
