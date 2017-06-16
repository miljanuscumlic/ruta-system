package rs.ruta.client;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.*;
import java.util.*;
import java.util.prefs.Preferences;

import javax.xml.bind.*;
import javax.xml.ws.BindingProvider;

import oasis.names.specification.ubl.schema.xsd.catalogue_2.*;
import oasis.names.specification.ubl.schema.xsd.catalogue_2.ObjectFactory;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.*;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.*;

import rs.ruta.*;
import rs.ruta.client.datamapper.*;
import rs.ruta.server.*;

public class Client implements RutaNode
{
	private static String packageList = "oasis.names.specification.ubl.schema.xsd.catalogue_2"; // colon separated package list
	private ArrayList<ItemType> myProducts; //database alternative - MMM: to be replaced with real database
	private DataMapper itemDataMapper; // MMM: it should be only one data mapper - for the database, and many finders for each database table
	private long catalogueID;
	private PartyType myParty;
	private PartyTypeXMLMapper<PartyType> partyDataMapper;
	private PartyType CDRParty;
	private CDRPartyTypeXMLMapper<PartyType> CDRPartyDataMapper;
	private boolean dirtyCatalogue;
	private ClientFrame frame;
	private Preferences prefNode = Preferences.userNodeForPackage(this.getClass());
	private List<PartyType> businessPartners;
	private List<PartyType> followingParties;
	private List<PartyType> followerParties;

	public Client()
	{
		myProducts = new ArrayList<ItemType>();
		myParty = new PartyType();
		CDRParty = new PartyType();
		catalogueID = 0;
		dirtyCatalogue = prefNode.getBoolean("dirtyCatalogue", false);
		businessPartners = new ArrayList<PartyType>();
		followingParties = new ArrayList<PartyType>();;
		followerParties= new ArrayList<PartyType>();;

		itemDataMapper = new ItemTypeFileMapper<ItemType>(this);
		partyDataMapper = new PartyTypeXMLMapper<PartyType>(Client.this, "myparty.xml");
		CDRPartyDataMapper = new CDRPartyTypeXMLMapper<PartyType>(Client.this, "cdr.xml");

	}

	/** Initializes fields of Client object from the database.
	 * @param frame GUI frame of the client application
	 */
	public void initialize(ClientFrame frame)
	{
		this.frame = frame;

		// trying to load the party data from the XML file
		ArrayList<PartyType> parties = (ArrayList<PartyType>) partyDataMapper.findAll();
		//		myParty = new PartyType(); // MMM: complete empty PartyType as alternative to next statement - this line is for test purposes
		//		myParty = InstanceFactory.newInstance(PartyType.class); // Producing StackOverflowException for PartyType.class object
		//		myParty = InstanceFactory.newInstancePartyType(); // instatiation of new partialy empty PartyType object with not null fields used in this version of the Ruta
		//		myParty = InstanceFactory.newInstance(PartyType.class, 1);

		if(parties.size() == 0)
		{
			myParty = frame.showPartyDialog(myParty, "My Party"); //displaying My Party Data dialog
			insertMyParty();
		}
		else
			//myParty = InstanceFactory.newInstance(parties.get(0));
			InstanceFactory.copyInstance(parties.get(0), myParty);

		//MMM: this could be lazy called when needed, not at start
		ArrayList<PartyType> CDRParties = (ArrayList<PartyType>) CDRPartyDataMapper.findAll();

		if(CDRParties.size() != 0)
			InstanceFactory.copyInstance(CDRParties.get(0), CDRParty);

	}

	/**
	 * Saves the value of variables that should be kept beetwen two program invocations
	 */
	public void savePreferences()
	{
		prefNode.putBoolean("dirtyCatalogue", dirtyCatalogue);
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

	public ArrayList<ItemType> getMyProducts() { return myProducts; }

	public long getCatalogueID() { return catalogueID; }

	public void setCatalogueID(long ID) { catalogueID = ID; }

	public void synchroniseCatalogue()
	{
		if(dirtyCatalogue)
		{
			try
			{
				// MMM: maybe JAXBContext should be private class field, if it is used from multiple class methods
				JAXBContext jc = JAXBContext.newInstance("oasis.names.specification.ubl.schema.xsd.catalogue_2"); //packageList

				Marshaller m = jc.createMarshaller();
				m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

				//retrieving Catalogue document
				CatalogueType catalogue = createCatalogueTypeObject();

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
					dirtyCatalogue = false;
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

	/**Generates Catalogue Document from Items in the Product table.
	 * @return catalogue
	 */
	private CatalogueType createCatalogueTypeObject()
	{
		//forming Catalogue document
		CatalogueType catalogue = new CatalogueType();
		IDType catID = new IDType();
		catID.setValue(String.valueOf(catalogueID++));
		catalogue.setID(catID);
		IssueDateType date = new IssueDateType();
		date.setValue(InstanceFactory.getDate());
		catalogue.setIssueDate(date);

		// MMM: insert here Provider Party and Receiver Party

		int cnt = 0;
		for(ItemType prod : myProducts)
		{
			CatalogueLineType catLine = new CatalogueLineType();
			IDType catLineID = new IDType();
			catLineID.setValue(catID.getValue() + "-" + cnt++);
			catLine.setID(catLineID);
			catLine.setItem(prod);
			catalogue.getCatalogueLine().add(catLine);
		}
		return catalogue;
	}

	/**Checks if the cell value has changed. If it has changed dirtyCatalogue field is set to true
	 * @param oldOne old value of the cell
	 * @param newOne new value of the cell
	 * @return true if the values differs, false otherwise
	 */
	private <T> boolean hasCellValueChanged(T oldOne, T newOne)
	{
		boolean changed = ! newOne.equals(oldOne);
		dirtyCatalogue = dirtyCatalogue || changed;
		return changed;
	}

	public String getProductName(int index)
	{
		ItemType item = myProducts.get(index);
		/*NameType name = item.getName();
		if(name != null)
			return name.getValue();
		else
			return null;*/

		try
		{
			return InstanceFactory.getPropertyOrNull(item.getName(), NameType::getValue);
		}
		catch(Exception e) { return null; }

	}

	public void setProductName(int index, String value)
	{
		ItemType item = myProducts.get(index);
		if(item.getName() == null)
			item.setName(new NameType());
		if(hasCellValueChanged(item.getName().getValue(), value))
			item.getName().setValue(value);
	}

	public String getProductDescription(int index)
	{
		ItemType item = myProducts.get(index);
		/*List<DescriptionType> descriptions = item.getDescription();
		if(descriptions.size() != 0)
		{
			DescriptionType desc = descriptions.get(0);
			if(desc != null)
				return desc.getValue();
		}
		return null;*/

		try
		{
			return InstanceFactory.getPropertyOrNull(item.getDescription().get(0), DescriptionType::getValue);
		}
		catch(Exception e) { return null; }
	}

	public void setProductDescription(int index, String value)
	{
		ItemType item = myProducts.get(index);
		List<DescriptionType> descriptions = item.getDescription();
		if(descriptions.size() == 0)
			descriptions.add(new DescriptionType());
		if(hasCellValueChanged(descriptions.get(0).getValue(), value))
			descriptions.get(0).setValue(value);
	}

	public String getProductID(int index)
	{
		ItemType item = myProducts.get(index);
/*		ItemIdentificationType identification = item.getSellersItemIdentification();
		if(identification != null)
		{
			IDType id = identification.getID();
			if(id != null)
				return id.getValue();
		}
		return null;*/

		try
		{
			return InstanceFactory.getPropertyOrNull(item.getSellersItemIdentification().getID(), IDType::getValue);
		}
		catch(Exception e) { return null; }
	}

	public void setProductID(int index, String value)
	{
		ItemType item = myProducts.get(index);
		if(item.getSellersItemIdentification() == null)
			item.setSellersItemIdentification(new ItemIdentificationType());
		if(item.getSellersItemIdentification().getID() == null)
			item.getSellersItemIdentification().setID(new IDType());
		if(hasCellValueChanged(item.getSellersItemIdentification().getID().getValue(), value))
			item.getSellersItemIdentification().getID().setValue(value);
	}

	public String getProductBarcode(int index)
	{
		ItemType item = myProducts.get(index);
		/*ItemIdentificationType identification = item.getSellersItemIdentification();
		if(identification != null)
		{
			BarcodeSymbologyIDType barcode = identification.getBarcodeSymbologyID();
			if(barcode != null)
				return barcode.getValue();
		}
		return null;*/

		try
		{
			return InstanceFactory.getPropertyOrNull(item.getSellersItemIdentification().getBarcodeSymbologyID(), BarcodeSymbologyIDType::getValue);
		}
		catch(Exception e) { return null; }

	}

	public void setProductBarcode(int index, String value)
	{
		ItemType item = myProducts.get(index);
		if(item.getSellersItemIdentification() == null)
			item.setSellersItemIdentification(new ItemIdentificationType());
		if(item.getSellersItemIdentification().getBarcodeSymbologyID() == null)
			item.getSellersItemIdentification().setBarcodeSymbologyID(new BarcodeSymbologyIDType());
		if(hasCellValueChanged(item.getSellersItemIdentification().getBarcodeSymbologyID().getValue(), value))
			item.getSellersItemIdentification().getBarcodeSymbologyID().setValue(value);

	}

	public BigDecimal getProductPackSizeNumeric(int index) //MMM: check if return value should be String instead
	{
		ItemType item = myProducts.get(index);
		/*	PackSizeNumericType packSize = item.getPackSizeNumeric();
		if(packSize != null)
			return packSize.toString();
		return null;*/

		try
		{
			return InstanceFactory.getPropertyOrNull(item.getPackSizeNumeric(), PackSizeNumericType::getValue);
		}
		catch(Exception e) { return null; }
	}

	public void setProductPackSizeNumeric(int index, String value)
	{
		ItemType item = myProducts.get(index);
		if(item.getPackSizeNumeric() == null)
			item.setPackSizeNumeric(new PackSizeNumericType());
		if(hasCellValueChanged(item.getPackSizeNumeric().getValue().toString(), value))
			item.getPackSizeNumeric().setValue(BigDecimal.valueOf(Long.parseLong(value)));
	}

	public String getProductCommodityCode(int index)
	{
		ItemType item = myProducts.get(index);
		/*List<CommodityClassificationType> commodities = item.getCommodityClassification();
		if(commodities.size() != 0)
		{
			CommodityClassificationType commodityClass = commodities.get(0);
			if(commodityClass != null)
			{
				CommodityCodeType commodityCode = commodityClass.getCommodityCode();
				if(commodityCode != null)
					return commodityCode.toString();
			}
		}
		return null;*/


		try
		{
			return InstanceFactory.getPropertyOrNull(item.getCommodityClassification().get(0).getCommodityCode(), CommodityCodeType::getValue);
		}
		catch(Exception e) { return null; }
	}


	public void setProductCommodityCode(int index, String value)
	{
		ItemType item = myProducts.get(index);
		List<CommodityClassificationType> commodities = item.getCommodityClassification();
		if(commodities.size() == 0)
			commodities.add(new CommodityClassificationType());
		if(commodities.get(0).getCommodityCode() == null)
			commodities.get(0).setCommodityCode(new CommodityCodeType());
		if(hasCellValueChanged(commodities.get(0).getCommodityCode().getValue(), value))
			commodities.get(0).getCommodityCode().setValue(value);;
	}

	public String getProductItemClassificationCode(int index)
	{
		ItemType item = myProducts.get(index);
		/*List<CommodityClassificationType> commodities = item.getCommodityClassification();
		if(commodities.size() != 0)
		{
			CommodityClassificationType commodityClass = commodities.get(0);
			if(commodityClass != null)
			{
				ItemClassificationCodeType classificationCode = commodityClass.getItemClassificationCode();
				if(classificationCode != null)
					return classificationCode.toString();
			}
		}
		return null;*/

		try
		{
			return InstanceFactory.getPropertyOrNull(item.getCommodityClassification().get(0).getItemClassificationCode(), ItemClassificationCodeType::getValue);
		}
		catch(Exception e) { return null; }
	}


	public void setProductItemClassificationCode(int index, String value)
	{
		ItemType item = myProducts.get(index);
		List<CommodityClassificationType> commodities = item.getCommodityClassification();
		if(commodities.size() == 0)
			commodities.add(new CommodityClassificationType());
		if(commodities.get(0).getItemClassificationCode() == null)
			commodities.get(0).setItemClassificationCode(new ItemClassificationCodeType());
		if(hasCellValueChanged(commodities.get(0).getItemClassificationCode().getValue(), value))
			commodities.get(0).getItemClassificationCode().setValue(value);;
	}

	public void addNewEmptyProduct()
	{
//		myProducts.add(InstanceFactory.newInstanceItemType());
		myProducts.add(new ItemType());
	}

	public int getProductCount()
	{
		return myProducts.size();
	}

	@SuppressWarnings("unchecked")
	public void importMyProducts()
	{
		myProducts = (ArrayList<ItemType>) itemDataMapper.findAll();
		if(myProducts == null)
		{
			myProducts = new ArrayList<ItemType>();
			catalogueID = 0;
		}
	}

	public void exportMyProducts()
	{
		itemDataMapper.insertAll();
	}

	public void closeDataStreams()
	{
		itemDataMapper.closeConnection();
	}

	public DataMapper getItemDataMapper()
	{
		return itemDataMapper;
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

	public PartyType getMyParty()
	{
		return myParty;
	}

	public void setMyParty(PartyType party)
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


}
