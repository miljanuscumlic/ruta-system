package rs.ruta.client.datamapper;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.file.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

import javax.xml.bind.*;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.*;

public abstract class XMLMapper<T> implements DataMapper
{
	private static String packageList = "oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2"; // colon separated package list
	private String filename;

	public XMLMapper(String filename)
	{
		this.filename = filename;
	}

	@Override
	public ArrayList<T> findAll()
	{
		ArrayList<T> result = new ArrayList<T>(); // MMM:This List is not nessecery because there is always one element of type T, not a list of them
		Path path = Paths.get(filename); // MMM: set the Path and the document files to some sensible place
		if (Files.exists(path))
		{
			try
			{
				JAXBContext jc = JAXBContext.newInstance(packageList);

				// create an Unmarshaller
				Unmarshaller u = jc.createUnmarshaller();

				// unmarshal instance document into a tree of Java content
				// objects composed of classes from the packageList
				try
				{
					@SuppressWarnings("unchecked")
					JAXBElement<T> partyElement = (JAXBElement<T>)u.unmarshal(new FileInputStream(filename));
					T party = partyElement.getValue();
					if(party != null)
						result.add(party);
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

	@SuppressWarnings("unchecked")
	@Override
	public void insertAll()
	{
//		Path path = Paths.get(filename); // MMM: set the Path and the document files to some sensible place
//		if (Files.exists(path))
		{
			try
			{
				JAXBContext jc = JAXBContext.newInstance(packageList);

				// create an element for marshalling

				JAXBElement<T> element = (JAXBElement<T>) getJAXBElement();

				//**** this is SLOWER alternative to the upper statement and delegation to a subclass*****
				//JAXBElement<T> element = getJAXBElementWithReflection();
				//****************************************************************************************

		        // create a Marshaller and marshal to System.out - alternative to down below code
		        //JAXB.marshal( partyElement, System.out );

				// create an Marshaller
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
	}

	private JAXBElement<T> getJAXBElementWithReflection()
	{
		JAXBElement<T> element = null;
		for(int i = 0; i< 1000; i++)
		{
			try
			{
				T obj = getObject();
				ObjectFactory objFactory = new ObjectFactory();
				String methodName = synthetizeMethodName(obj);
				Method method = objFactory.getClass().getMethod(methodName, obj.getClass());
				element = (JAXBElement<T>) method.invoke(objFactory, obj);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return element;
	}

	abstract public T getObject();

	private String synthetizeMethodName(Object obj)
	{
		//pattern: "create" + className - "Party"

		StringBuilder sb = new StringBuilder("create");
		String methodName = obj.getClass().getSimpleName().replaceFirst("Type", "");
		sb.append(methodName);

		return sb.toString();
	}

	abstract protected JAXBElement<?> getJAXBElement();

	@Override
	public void closeConnection()
	{

	}

}
