package rs.ruta.client.datamapper;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.file.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

import javax.xml.bind.*;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.*;

public abstract class XMLFileMapper<T> implements OLDDataMapper
{
	private String filename;
	protected String packageList; // colon separated package list

	public XMLFileMapper(String filename)
	{
		this.filename = filename;
	}

	@Override
	public ArrayList<T> findAll() throws Exception
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
				logger.error("Exception is ", e);
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void insertAll() throws Exception
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
				logger.error("Exception is ", e);
			}
		}
	}

	@SuppressWarnings("unchecked")
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
				logger.error("Exception is ", e);
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
