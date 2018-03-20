package rs.ruta.client.datamapper;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import javax.xml.bind.*;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;

import rs.ruta.client.*;

public class MyPartyXMLFileMapper<T> extends XMLFileMapper<T>
{
	private MyParty myParty;
	private String filePath;

	public MyPartyXMLFileMapper(MyParty myParty, String filePath) throws Exception
	{
		super(filePath);
		this.filePath = filePath;
		this.packageList = "rs.ruta.client";
		this.myParty = myParty;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<T> findAll() throws JAXBException
	{
		ArrayList<T> result = new ArrayList<T>(); // MMM: This List is not nessecery because there is always one element of type T, not a list of them
		Path path = Paths.get(filePath); // MMM: set the Path and the document files to some sensible place
		if (Files.exists(path))
		{
			JAXBContext jc = JAXBContext.newInstance(MyParty.class);

	//		checkVersion();

			// create an Unmarshaller
			Unmarshaller u = jc.createUnmarshaller();

			// unmarshal instance document into a tree of Java content
			// objects composed of classes from the packageList
			try
			{
				Object party = u.unmarshal(new FileInputStream(filePath));
				if(party != null)
					result.add((T)party);
			}
			catch (FileNotFoundException e)
			{
				System.out.println("Could not open document file " + filePath);
			}
		}
		return result;
	}

	@Override
	public void insertAll() throws JAXBException
	{
		JAXBContext jc = JAXBContext.newInstance(MyParty.class);

		// create an element for marshalling
		@SuppressWarnings("unchecked")
		JAXBElement<T> element = (JAXBElement<T>) getJAXBElement();

		Marshaller m = jc.createMarshaller();

		// marshal a tree of Java content objects to a file
		try(FileOutputStream fos = new FileOutputStream(filePath))
		{
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			//m.marshal(element, System.out);
			m.marshal(element, fos);
		}
		catch (IOException e)
		{
			System.out.println("Could not open document file " + filePath);
		}
	}

	@Override
	protected JAXBElement<?> getJAXBElement()
	{
		QName _Party_QNAME = new QName("urn:rs:ruta:client", "MyParty");
		JAXBElement<MyParty> partyElement = new JAXBElement<MyParty>(_Party_QNAME, MyParty.class, (MyParty) getObject());
		return partyElement;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getObject()
	{
		return (T) myParty;
	}

	//MMM: not finished method yet
	private void checkVersion() throws XMLStreamException
	{
		// Create an XMLStreamReader on XML input
        XMLInputFactory xif = XMLInputFactory.newFactory();
        StreamSource xml = new StreamSource(filePath);
        XMLStreamReader xsr = xif.createXMLStreamReader(xml);

        // Check the version attribute
        xsr.nextTag(); // Advance to root element
        String version = xsr.getAttributeValue("", "VERSION");
//        if(! Client.getVersion().getJaxbVersion().equals(version))
//        {
//            // Do something if the version is incompatible
//            throw new RuntimeException("VERSION MISMATCH");
//        }
	}

}
