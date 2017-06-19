package rs.ruta.client.datamapper;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import javax.xml.bind.*;
import javax.xml.namespace.QName;

import java.beans.XMLEncoder;

import rs.ruta.client.*;

public class BusinessPartyXMLMapper<T> extends XMLMapper<T>
{
	private Client client;
	private String filename;

	public BusinessPartyXMLMapper(Client client, String filename)
	{
		super(filename);
		this.filename = filename;
		this.packageList = "rs.ruta.client";
		this.client = client;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<T> findAll()
	{
		ArrayList<T> result = new ArrayList<T>(); // MMM: This List is not nessecery because there is always one element of type T, not a list of them
		Path path = Paths.get(filename); // MMM: set the Path and the document files to some sensible place
		if (Files.exists(path))
		{
			try
			{
				JAXBContext jc = JAXBContext.newInstance(MyParty.class);

				// create an Unmarshaller
				Unmarshaller u = jc.createUnmarshaller();

				// unmarshal instance document into a tree of Java content
				// objects composed of classes from the packageList
				try
				{
					Object party = u.unmarshal(new FileInputStream(filename));
					if(party != null)
						result.add((T)party);
				}
				catch (FileNotFoundException e)
				{
					System.out.println("Could not open document file " + filename);
				}
			}
			catch (JAXBException e)
			{
				e.printStackTrace();
			}
		}
		return result;
	}

	@Override
	public void insertAll()
	{
		try
		{
			JAXBContext jc = JAXBContext.newInstance(MyParty.class);

			// create an element for marshalling
			@SuppressWarnings("unchecked")
			JAXBElement<T> element = (JAXBElement<T>) getJAXBElement();

			Marshaller m = jc.createMarshaller();

			// marshal a tree of Java content objects to a file
			try
			{
				m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
				//m.marshal(element, System.out);
				m.marshal(element, new FileOutputStream(filename));
			}
			catch (FileNotFoundException e)
			{
				System.out.println("Could not open document file " + filename);
			}
		}
		catch (JAXBException e)
		{
			e.printStackTrace();
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
		return (T) client.getMyParty();
	}

}
