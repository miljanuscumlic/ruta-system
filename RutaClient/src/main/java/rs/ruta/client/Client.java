package rs.ruta.client;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.prefs.Preferences;

import javax.xml.bind.*;
import javax.xml.ws.BindingProvider;

import oasis.names.specification.ubl.schema.xsd.catalogue_2.*;
import oasis.names.specification.ubl.schema.xsd.catalogue_2.ObjectFactory;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.*;
import rs.ruta.*;
import rs.ruta.client.datamapper.*;
import rs.ruta.server.*;

public class Client implements RutaNode
{
	private static String packageList = "oasis.names.specification.ubl.schema.xsd.catalogue_2"; // colon separated package list

	private BusinessParty myParty;
	private BusinessPartyXMLMapper<BusinessParty> partyDataMapper;// MMM: it should be only one data mapper - for the database, and many finders for each database table
	private PartyType CDRParty;
	private CDRPartyTypeXMLMapper<PartyType> CDRPartyDataMapper;
	private ClientFrame frame;
	private Preferences prefNode = Preferences.userNodeForPackage(this.getClass());

	public Client()
	{
		myParty = new BusinessParty();
		CDRParty = new PartyType();
		myParty.setDirtyCatalogue(prefNode.getBoolean("dirtyCatalogue", false));

		partyDataMapper = new BusinessPartyXMLMapper<BusinessParty>(Client.this, "myparty.xml");
		CDRPartyDataMapper = new CDRPartyTypeXMLMapper<PartyType>(Client.this, "cdr.xml");
	}

	/** Initializes fields of Client object from the database.
	 *
	 */
	public void preInitialize()
	{
		// trying to load the party data from the XML file
		ArrayList<BusinessParty> parties = (ArrayList<BusinessParty>) partyDataMapper.findAll();
		//		myParty = new PartyType(); // MMM: complete empty PartyType as alternative to next statement - this line is for test purposes
		//		myParty = InstanceFactory.newInstance(PartyType.class); // Producing StackOverflowException for PartyType.class object
		//		myParty = InstanceFactory.newInstancePartyType(); // instatiation of new partialy empty PartyType object with not null fields used in this version of the Ruta
		//		myParty = InstanceFactory.newInstance(PartyType.class, 1);

		if(parties.size() != 0)
			InstanceFactory.copyInstance(parties.get(0), myParty); // MMM: check why I am coping the party into the new object

		//MMM: this could/should be lazy called when needed, not at start
		ArrayList<PartyType> CDRParties = (ArrayList<PartyType>) CDRPartyDataMapper.findAll();

		if(CDRParties.size() != 0)
			InstanceFactory.copyInstance(CDRParties.get(0), CDRParty);


		//*****************
		//adding Bussines Partners for the test purposes
		List<PartyType> bp = ((BusinessParty) myParty).getBusinessPartners();
		List<PartyType> fp = ((BusinessParty) myParty).getFollowingParties();
		for(int i = 9; i>=0; i--)
		{
			PartyType p = InstanceFactory.newInstancePartyType();
			p.getPartyName().get(0).getName().setValue("Partner #" + i);
			bp.add(p);
			fp.add(p);
		}
		for(int i = 15; i>=10; i--)
		{
			PartyType p = InstanceFactory.newInstancePartyType();
			p.getPartyName().get(0).getName().setValue("Party #" + i);
			fp.add(p);
		}

		//*****************

	}

	/** Initializes the Party field of Client object from the database, if not already initialized in the initialize method.
	 *
	 */
	public void initialize()
	{
		// trying to load the party data from the XML file
		ArrayList<BusinessParty> parties = (ArrayList<BusinessParty>) partyDataMapper.findAll();
		//		myParty = new PartyType(); // MMM: complete empty PartyType as alternative to next statement - this line is for test purposes
		//		myParty = InstanceFactory.newInstance(PartyType.class); // Producing StackOverflowException for PartyType.class object
		//		myParty = InstanceFactory.newInstancePartyType(); // instatiation of new partialy empty PartyType object with not null fields used in this version of the Ruta
		//		myParty = InstanceFactory.newInstance(PartyType.class, 1);

		if(parties.size() == 0)
		{
			myParty = (BusinessParty) frame.showPartyDialog(myParty, "My Party"); //displaying My Party Data dialog
			insertMyParty();
		}
		else
			InstanceFactory.copyInstance(parties.get(0), myParty); // MMM: check why I am coping the party into the new object
	}

	/**
	 * Saves the value of variables that should be kept beetwen two program invocations
	 */
	public void savePreferences()
	{
		prefNode.putBoolean("dirtyCatalogue", myParty.isDirtyCatalogue());
	}

	/**
	 * Inserts My Party data to the database
	 */
	public void insertMyParty()
	{
		getPartyDataMapper().insertAll();

	}

	/**
	 * Inserts CDR Party data to the database
	 */
	public void insertCDRParty()
	{
		getCDRPartyDataMapper().insertAll();

	}

	public void synchroniseCatalogue()
	{
		if(myParty.isDirtyCatalogue())
		{
			try
			{
				// MMM: maybe JAXBContext should be private class field, if it is used from multiple class methods
				JAXBContext jc = JAXBContext.newInstance("oasis.names.specification.ubl.schema.xsd.catalogue_2"); //packageList

				Marshaller m = jc.createMarshaller();
				m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

				//retrieving Catalogue document
				CatalogueType catalogue = myParty.createCatalogueTypeObject();

				//calling webservice
				CDRService service = new CDRService();
				Server port = service.getCDRPort();

				//temporary setting for TCP/IP Monitor in Eclipse
				((BindingProvider) port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,"http://localhost:7777/Ruta-0.0.1/CDR");

				port.putDocument(catalogue);

				CatalogueType newCat = port.getDocument();


				//creating XML document - for test purpose only
				ObjectFactory objFactory = new ObjectFactory();
				JAXBElement<CatalogueType> catalogueElement = objFactory.createCatalogue(newCat);
				try
				{
					JAXB.marshal(catalogueElement, System.out );
					JAXB.marshal(catalogueElement, new FileOutputStream("catalogue.xml"));
					frame.appendToConsole("Catalogue successfully sent to CDR."); //MMM: this should be messaged after the acctual sending of the Catalogue
					myParty.setDirtyCatalogue(false);
				}
				catch (FileNotFoundException e)
				{
					System.out.println("Could not save Catalogue document to the file catalogue.xml");
				}
			}
			catch (JAXBException e)
			{
				e.printStackTrace();
			}
			//JOptionPane.showMessageDialog(null, "Synchronising Catalogue with CDR", "Synchronising Catalogue", JOptionPane.PLAIN_MESSAGE);
		}
		else
		{
			frame.appendToConsole("Catalogue is already synchronized with the CDR.");
		}
	}

	public DataMapper getPartyDataMapper()
	{
		return partyDataMapper;
	}

	public DataMapper getCDRPartyDataMapper()
	{
		return CDRPartyDataMapper;
	}

	private void importXMLDocument() // MMM: to be finished later for use of XML documents import
	{
		Path path = Paths.get("catalogue.xml"); // MMM: set the Path and the document files to some sensible place
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
					JAXBElement<?> catalogueElement = (JAXBElement<?>)u.unmarshal(new FileInputStream("catalogue.xml"));
					CatalogueType cat = (CatalogueType)catalogueElement.getValue();

				}
				catch (FileNotFoundException e)
				{
					System.out.println("Could not save Catalogue document to the file catalogue.xml");
				}

			} catch (JAXBException e)
			{
				e.printStackTrace();
			}
		}
	}

	public BusinessParty getMyParty()
	{
		return myParty;
	}

	public void setMyParty(BusinessParty party)
	{
		myParty = party;
	}

	public PartyType getCDRParty()
	{
		return CDRParty;
	}

	public void setCDRParty(PartyType party)
	{
		CDRParty = party;
	}

	public void setFrame(ClientFrame clientFrame)
	{
		frame = clientFrame;
	}


}
