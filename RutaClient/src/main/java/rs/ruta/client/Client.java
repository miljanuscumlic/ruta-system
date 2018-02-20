package rs.ruta.client;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Image;
import java.io.*;
import java.math.BigDecimal;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.xml.bind.*;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPBinding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.error.list.IErrorList;
import com.helger.commons.state.ESuccess;
import com.helger.ubl21.UBL21Validator;
import com.helger.ubl21.UBL21ValidatorBuilder;
import com.helger.ubl21.UBL21Writer;

import oasis.names.specification.ubl.schema.xsd.catalogue_21.CatalogueType;
import oasis.names.specification.ubl.schema.xsd.catalogue_21.ObjectFactory;
import oasis.names.specification.ubl.schema.xsd.cataloguedeletion_21.CatalogueDeletionType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.CustomerPartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.InvoiceLineType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.ItemType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.MonetaryTotalType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyNameType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.SupplierPartyType;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;
import rs.ruta.*;
import rs.ruta.client.datamapper.*;
import rs.ruta.common.ReportAttachment;
import rs.ruta.common.ReportComment;
import rs.ruta.common.BugReport;
import rs.ruta.common.BugReportSearchCriterion;
import rs.ruta.common.RutaVersion;
import rs.ruta.common.CatalogueSearchCriterion;
import rs.ruta.common.DeregistrationNotice;
import rs.ruta.common.DocBoxAllIDsSearchCriterion;
import rs.ruta.common.DocBoxDocumentSearchCriterion;
import rs.ruta.common.Followers;
import rs.ruta.common.datamapper.DetailException;
import rs.ruta.common.datamapper.ExistConnector;
import rs.ruta.common.datamapper.MapperRegistry;
import rs.ruta.services.*;
import rs.ruta.common.InstanceFactory;

public class Client implements RutaNode
{
	private static String packageList = "oasis.names.specification.ubl.schema.xsd.catalogue_21"; // colon separated package list
	final private static String defaultEndPoint = "http://ruta.sytes.net:9009/ruta-server-0.2.0-SNAPSHOT/CDR";
	private static String cdrEndPoint = defaultEndPoint;
	final private static String eclipseMonitorEndPoint = "http://localhost:7709/ruta-server-0.2.0-SNAPSHOT/CDR";
	private MyParty myParty;
	//	private MyPartyXMLFileMapper<MyParty> partyDataMapper; //former store to myparty.xml
	private MyPartyExistMapper partyDataMapper;//MMM: it should be one data mapper - for the database, and many finders - extended classes for each database table
	private Party CDRParty;
	//	private CDRPartyTypeXMLFileMapper<Party> CDRPartyDataMapper; //MMM: not used anymore
	private ClientFrame frame;
	private static RutaVersion version = new RutaVersion("Client", "0.2.0-SNAPSHOT", "0.1.0", null);
	private Properties properties;
	private MapperRegistry mapperRegistry; //MMM: would be used instead of temporary ClientMapperRegistry and ExistConnector (see: constructor)
	private List<BugReport> bugReports;
	private static Logger logger = LoggerFactory.getLogger(Client.class.getName());

	/**
	 * Constructs a {@code Client} object.
	 * @param force if true it tells the constructor to try to create an object despite the fact that
	 * one instance of it invoked earlier by {@code Ruta Client application} from the same OS directory
	 * had already been created
	 * @throws DetailException if object could not be created or, when {@code force} is true and one instance of it already exists
	 */
	public Client(boolean force) throws DetailException
	{
		logger.info("Constructing Client");
		properties = new Properties();
		loadProperties();
		try
		{
			isClientInstantiated();
			properties.setProperty("started", "true");
			storeProperties(false);
		}
		catch(DetailException e)
		{
			if(!force)
				throw e;
		}

		myParty = new MyParty();
		CDRParty = getCDRParty();

		//partyDataMapper = new MyPartyXMLFileMapper<MyParty>(Client.this, "myparty.xml");
		//MMM: temporary connector and mapper - would be replaced with MapperRegistry
		ExistConnector connector = new LocalExistConnector();
		new ClientMapperRegistry(); //just to initialize the registry. No reference needed later.
		//		MapperRegistry.initialize(mapperRegistry);
		partyDataMapper = new MyPartyExistMapper(Client.this, connector);

		addShutDownHook();
	}

	/** Initializes fields of Client object from local data store.
	 * @throws JAXBException if importing data from the data store is unsuccessful
	 */
	public void preInitialize() throws Exception
	{
		// trying to load the party data from the XML file
		ArrayList<MyParty> parties = (ArrayList<MyParty>) partyDataMapper.findAll();
		//		myParty = new PartyType(); // MMM: complete empty PartyType as alternative to next statement - this line is for test purposes
		//		myParty = InstanceFactory.newInstance(PartyType.class); // Producing StackOverflowException for PartyType.class object
		//		myParty = InstanceFactory.newInstancePartyType(); // instatiation of new partialy empty PartyType object with not null fields used in this version of the Ruta
		//		myParty = InstanceFactory.newInstance(PartyType.class, 1);

		if(parties != null && parties.size() != 0)
		{
			myParty = parties.get(0);
			Search.setSearchNumber(myParty.getSearchNumber());
		}
	}

	/**If not already initialized in the preInitialize method, shows dialog for inputing Party data.
	 */
	public void initialize()
	{
		if(!myParty.hasCoreParty() || myParty.getCoreParty().verifyParty() != null)
			myParty.setCoreParty(frame.showPartyDialog(myParty.getCoreParty(), "My Party")); //displaying My Party Data dialog
		frame.updateTitle(myParty.getCoreParty().getPartySimpleName());
		/*		else
		{
			// trying to load the party data from the XML file
						ArrayList<MyParty> parties = (ArrayList<MyParty>) partyDataMapper.findAll();

			//InstanceFactory.copyInstance(parties.get(0), myParty); // MMM: check why I am coping the party into the new object
			myParty = parties.get(0);
		}*/
	}

	private void addShutDownHook()
	{
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			@Override
			public void run()
			{
				try
				{
					System.out.println("Shutdown hook started.");
					//					properties.put("started", false);
					shutdownApplication();
					System.out.println("Shutdown hook ended.");
				}
				catch(Exception e)
				{
					logger.error("Exception is ", e);
				}
			}
		});
	}

	public Properties getProperties()
	{
		return properties;
	}

	public void setProperties(Properties properties)
	{
		this.properties = properties;
	}

	/**Loads {@link Properties} field object from the {@code .properties} file.
	 */
	public void loadProperties()
	{
		//ClassLoader classLoader = getClass().getClassLoader();
		try(InputStream input = new FileInputStream("ruta.properties") /*classLoader.getResourceAsStream("ruta.properties")*/)
		{
			properties.load(input);
		}
		catch (IOException | NullPointerException e)
		{
			JOptionPane.showMessageDialog(null, "Properties could not be read from the file!\nReverting to default settings.",
					"Information", JOptionPane.INFORMATION_MESSAGE);
			logger.warn("Exception is ", e);
		}
		Client.cdrEndPoint = properties.getProperty("cdrEndPoint", Client.defaultEndPoint);
	}

	/**Stores {@link Properties} field object to the {@code .properties} file.
	 * @param end true if this method is invoked just before the program termination
	 */
	public void storeProperties(boolean end)
	{
		if(end)
			saveProperties();
		try(OutputStream output = new FileOutputStream("ruta.properties"))
		{
			properties.store(output, "Ruta Client properties");
		}
		catch (IOException | NullPointerException e)
		{
			JOptionPane.showMessageDialog(null, "Properties could not be read from the file!\nReverting to default settings.",
					"Error", JOptionPane.ERROR_MESSAGE);
			//logger.error("Exception is ", e);
		}
	}

	/**Saves properties from {@code Client} class fields to {@link Properties} object.
	 */
	private void saveProperties()
	{
		properties.put("cdrEndPoint", Client.cdrEndPoint);
		properties.put("started", "false");
	}

	/**Checks whether the instance of {@code Client} has been already created within the same OS directory.
	 * @throws DetailException if the {@code Client} has been already created
	 */
	private void isClientInstantiated() throws DetailException
	{
		String prop = "started";
		boolean started = false;
		if(properties.get(prop) != null)
			started = Boolean.parseBoolean((properties.get(prop).toString()));
		if(started)
			throw new DetailException("Ruta Client application has been already started.");
	}

	/**Gets the {@link Version} object describing the version of the {@code Ruta Client} application
	 * @return {@code Version} object of the {@code Ruta Client} application
	 */
	public static RutaVersion getVersion() { return version;}

	/**Sets the {@link Version} object describing the version of the {@code Ruta Client} application
	 * @param version {@code Version} object of the {@code Ruta Client} application
	 */
	public static void setVersion(RutaVersion version) { Client.version = version; }

	public List<BugReport> getBugReports()
	{
		if(bugReports == null)
			bugReports = new ArrayList<BugReport>();
		return bugReports;
	}

	/**Populates the list of {@link BugReport}s in a way that if some {@code BugReport} already exists in the list
	 * it is overridden only if its modification date is older than that of his newly retrived counterpart.
	 * @param bugReports list of {@code BugReport}s
	 */
	public void setBugReports(List<BugReport> bugReports)
	{
		//TODO functionality stated in the documentation comment should be implemented here
		this.bugReports = bugReports;
	}

	/**Inserts My Party data to the local data store.
	 * @throws Exception if data could not be inserted in the data store
	 */
	public void insertMyParty() throws Exception
	{
		myParty.setSearchNumber(Search.getSearchNum());
		partyDataMapper.insertAll();
		//MMM: inserting in embedded exist database - not working at the moment
		//MapperRegistry.getMapper(MyParty.class);
	}

	/**Sends request for registration of My party with the Central Data Repository.
	 * @param party Party object that should be registered
	 * @param username username of the party
	 * @param password password of the party
	 */
	public void cdrRegisterMyParty(PartyType party, String username, String password)
	{
		try
		{
			Server port = getCDRPort();
			port.registerUserAsync(username, password, futureUser ->
			{
				try
				{
					RegisterUserResponse response = futureUser.get();
					String key = response.getReturn();
					myParty.setUsername(username);
					myParty.setPassword(password);
					myParty.setSecretKey(key);
					frame.appendToConsole("Party has been successfully registered with the CDR service."
							+ " Please synchronise My Party with the CDR service to be able to use it.", Color.GREEN);
				}
				catch(Exception e)
				{
					processException(e, "My Party has not been registered with the CDR service!");
				}
				finally
				{
					frame.enablePartyMenuItems();
				}
			});
			frame.appendToConsole("Request for the registration of My Party has been sent to the CDR service. Waiting for a response...",
					Color.BLACK);
		}
		catch(WebServiceException e)
		{
			logger.error("Exception is ", e);
			frame.appendToConsole("My Party has not been registered with the CDR service!"
					+ " Server is not accessible. Please try again later.", Color.RED);
			frame.enablePartyMenuItems();
		}
	}

	/**
	 * Sends request for registration of My party with the Central Data Repository.
	 * @param username username of the party
	 * @param password password of the party
	 * @param party {@link PartyType} object that should be registered
	 */
	public void cdrNewRegisterMyParty(String username, String password)
	{
		try
		{
			Server port = getCDRPort();

			//validating UBL conformance
			final String missingPartyField = myParty.getCoreParty().verifyParty();
			if(missingPartyField != null)
			{
				frame.appendToConsole("Request for the registration of My Party has not been sent to the CDR service because"
						+ " Party is missing mandatory field: " + missingPartyField + ".", Color.RED);
				frame.enablePartyMenuItems();
			}
			else
			{
				myParty.setPartyID();
				PartyType coreParty = myParty.getCoreParty();

				/*			//MMM: not working because ph-ubl does not validate components that are not UBL documents
				UBL21ValidatorBuilder<PartyType> validatorBuilder = UBL21ValidatorBuilder.create(PartyType.class);
				IErrorList errors = validatorBuilder.validate(coreParty);
				if(errors.containsAtLeastOneFailure())
				{
					frame.appendToConsole("My Party has not been sent to the CDR service because it is malformed. "
							+ "UBL validation has failed.", Color.RED);
					frame.enableCatalogueMenuItems();
				}
				else*/
				{
					port.newRegisterUserAsync(username, password, coreParty, futureUser ->
					{
						try
						{
							NewRegisterUserResponse response = futureUser.get();
							String key = response.getReturn();
							myParty.setUsername(username);
							myParty.setPassword(password);
							myParty.setSecretKey(key);
							myParty.setDirtyMyParty(false);
							frame.appendToConsole("My Party has been successfully registered with the CDR service."
									+ " Please synchronise My Catalogue with the CDR service to be able to use it.", Color.GREEN);
						}
						catch(Exception e)
						{
							myParty.clearPartyID();
							processException(e, "My Party has not been registered with the CDR service!");
						}
						finally
						{
							frame.enablePartyMenuItems();
						}
					});
					frame.appendToConsole("Request for the registration of My Party has been sent to the CDR service. Waiting for a response...",
							Color.BLACK);
				}
			}
		}
		catch(WebServiceException e)
		{
			logger.error("Exception is ", e);
			frame.appendToConsole("My Party has not been registered with the CDR service!"
					+ " Server is not accessible. Please try again later.", Color.RED);
			frame.enablePartyMenuItems();
		}
	}

	/**
	 * Synchronise My Party data with the CDR service. Method sends the data
	 * if they have been changed since the last synchronisation.
	 */
	//MMM: with the new registration process there is no difference with the insert and update of the Party on the client side
	public void cdrSynchroniseMyParty()
	{
		cdrUpdateMyParty();
		frame.repaintTabbedPane();
	}

	/**
	 * Inserts My Party data to the CDR service. This method is called only ones, after registration of
	 * My Party with the CDR service.
	 */
	private void cdrInsertMyParty()
	{
		try
		{
			Server port = getCDRPort();
			port.insertPartyAsync(myParty.getUsername(), myParty.getCoreParty(), futureParty ->
			{
				try
				{
					InsertPartyResponse res = futureParty.get();
					String partyID = res.getReturn();
					myParty.setDirtyMyParty(false);
					frame.appendToConsole("My Party has been successfully synchronised with the CDR service.", Color.GREEN);
				}
				catch(Exception e)
				{
					processException(e, "My Party has not been registered with the CDR service!");
				}
				finally
				{
					frame.enablePartyMenuItems();
				}
			});
			frame.appendToConsole("Request for the synchronisation of My Party has been sent to the CDR service. Waiting for a response...", Color.BLACK);
		}
		catch(WebServiceException e)
		{
			frame.appendToConsole("My Party has not been synchronised with the CDR service!"
					+ " Server is not accessible. Please try again later.", Color.RED);
			frame.enablePartyMenuItems();
		}
	}

	/**
	 * Updates My Party data with the CDR service.
	 */
	private void cdrUpdateMyParty()
	{
		try
		{
			Server port = getCDRPort();
			port.updatePartyAsync(myParty.getUsername(), myParty.getCoreParty(), future ->
			{
				try
				{
					UpdatePartyResponse response = future.get();
					myParty.setDirtyMyParty(false);
					frame.appendToConsole("My Party has been successfully updated with the CDR service.", Color.GREEN);
				}
				catch(Exception e)
				{
					processException(e, "My Party has not been synchronised with the CDR service!");
				}
				finally
				{
					frame.enablePartyMenuItems();
				}
			});
			frame.appendToConsole("Request for the synchronisation of My Party has been sent to the CDR service. Waiting for a response...", Color.BLACK);
		}
		catch(WebServiceException e)
		{
			frame.appendToConsole("My Party has not been synchronised with the CDR service!"
					+ " Server is not accessible. Please try again later.", Color.RED);
			frame.enablePartyMenuItems();
		}
	}

	/**Deregister My Party from the CDR service. All documents corresponding to the My Party deposited in the CDR database
	 * are deleted from the CDR service.
	 */
	public void cdrDeregisterMyParty()
	{
		frame.appendToConsole("Checking whether there are new documents in the DocBox.", Color.BLACK);
		Semaphore sequential = cdrGetNewDocuments();
		try
		{
			sequential.acquire();
		}
		catch (InterruptedException e)
		{
			logger.error("Unable to make sequental calls of the CDR service.", e);
		}
		try
		{
			final Server port = getCDRPort();
			final DeregistrationNotice notice = new DeregistrationNotice(myParty.getCoreParty());
			port.deregisterUserAsync(myParty.getUsername(), notice, future ->
			{
				try
				{
					future.get();
					myParty.setDirtyMyParty(true);
					myParty.setDirtyCatalogue(true);
					myParty.setInsertMyCatalogue(true);
					myParty.setSecretKey(null);
					myParty.setUsername(null);
					myParty.setPassword(null);
					myParty.getCoreParty().setPartyID(null);
					myParty.setCatalogueID(0);
					myParty.setCatalogueDeletionID(0);
					myParty.removeCatalogueIssueDate();
					myParty.unfollowMyself();
					myParty.setFollowingParties(null);
					frame.appendToConsole("My Party has been successfully deregistered from the CDR service.", Color.GREEN);
					frame.repaintTabbedPane(); //MMM: shoould be called method for repainting whole frame - to be implemented
				}
				catch(Exception e)
				{
					processException(e, "My Party has not been deregistered from the CDR service! ");
				}
				finally
				{
					frame.enablePartyMenuItems();
				}
			});
			frame.appendToConsole("Request for deregistration of My Party has been sent to the CDR service. Waiting for a response...", Color.BLACK);
		}
		catch(WebServiceException e)
		{
			frame.appendToConsole("My Party has not been deregistered from the CDR service!"
					+ " Server is not accessible. Please try again later.", Color.RED);
			frame.enablePartyMenuItems();
		}
	}

	/**Synchronise Catalogue with the CDR service. If catalogue is empty calls deleteCatalogue method.
	 * Method sends the catalogue if it is nonempty and has been changed since the last synchronisation
	 * with the CDR service.
	 */
	void cdrSynchroniseMyCatalogue()
	{
		if(myParty.isInsertMyCatalogue() == true) //first time sending catalogue
			cdrInsertMyCatalogue();
		else
			if(myParty.getProductCount() == 0) // delete My Catalogue from CDR
				cdrDeleteMyCatalogue();
			else
				cdrUpdateMyCatalogue();
	}

	/**Inserts My Catalogue for the first time in the CDR service. Updates are done with the
	 * <code>cdrUpdateMyCatalogue</code> method.
	 * @see Client#cdrUpdateMyCatalogue <code>cdrUpdateMyCatalogue</code>
	 */
	private void cdrInsertMyCatalogue()
	{
		try
		{
			//creating Catalogue document
			CatalogueType catalogue = myParty.createCatalogue(CDRParty);
			if(catalogue != null)
			{
				//validating UBL conformance
				IErrorList errors = UBL21Validator.catalogue().validate(catalogue);
				if(errors.containsAtLeastOneFailure())
				{
					frame.appendToConsole("My Catalogue has not been sent to the CDR service because it is malformed. "
							+ "UBL validation has failed.", Color.RED);
					frame.enableCatalogueMenuItems();
				}
				else
				{
					myParty.setCatalogue(catalogue);
					Server port = getCDRPort();
					String username = myParty.getUsername();
					port.insertCatalogueAsync(username, catalogue, future ->
					{
						try
						{
							future.get();
							frame.appendToConsole("My Catalogue has been successfully deposited to the CDR service.", Color.GREEN);
							myParty.setDirtyCatalogue(false);
							myParty.setInsertMyCatalogue(false);
							myParty.followMyself();
							frame.repaintTabbedPane(); //MMM: shoould be called method for repainting whole frame - to be implemented
							frame.appendToConsole("My Party has been added to the Following parties.", Color.BLACK);
						}
						catch(Exception e)
						{
							processException(e, "My Catalogue has not been deposited to the CDR service! ");
						}
						finally
						{
							frame.enableCatalogueMenuItems();
						}
					});
					frame.appendToConsole("My Catalogue has been sent to the CDR service. Waiting for a response...", Color.BLACK);

/*				// MMM: maybe JAXBContext should be private class field, if it is used from multiple class methods
				JAXBContext jc = JAXBContext.newInstance("oasis.names.specification.ubl.schema.xsd.catalogue_21"); //packageList
 				Marshaller m = jc.createMarshaller();
				//m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
				//creating XML document - for test purpose only
				ObjectFactory objFactory = new ObjectFactory();
				JAXBElement<CatalogueType> catalogueElement = objFactory.createCatalogue(catalogue);
				try
				{
					//JAXB.marshal(catalogueElement, System.out );
					JAXB.marshal(catalogueElement, new FileOutputStream("catalogue.xml"));
				}
				catch (FileNotFoundException e)
				{
					System.out.println("Could not save Catalogue document to the file catalogue.xml");
				}
				catch (JAXBException e)
				{
					logger.error("Exception is ", e);
					frame.enableCatalogueMenuItems();
				}*/
				}
			}
			else
			{
				frame.appendToConsole("My Catalogue has not been sent to the CDR service because it is malformed. "
						+ "All catalogue items should have a name and catalogue has to have at least one item.", Color.RED);
				frame.enableCatalogueMenuItems();
			}
		}
		catch(WebServiceException e)
		{
			frame.appendToConsole("My Catalogue has not been deposited to the CDR service!"
					+ " Server is not accessible. Please try again later.", Color.RED);
			frame.enableCatalogueMenuItems();
		}
	}

	/**
	 * Updates My Catalogue with the CDR service.
	 */
	void cdrUpdateMyCatalogue()
	{
		try
		{
			//creating Catalogue document
			CatalogueType catalogue = myParty.createCatalogue(CDRParty);
			if(catalogue != null)
			{
				//validating UBL conformance
				IErrorList errors = UBL21Validator.catalogue().validate(catalogue);
				if(errors.containsAtLeastOneFailure())
				{
					frame.appendToConsole("My Catalogue has not been sent to the CDR service because it is malformed. "
							+ "UBL validation has failed.", Color.RED);
					frame.enableCatalogueMenuItems();
				}
				else
				{
					myParty.setCatalogue(catalogue);
					Server port = getCDRPort();
					String username = myParty.getUsername();

					port.updateCatalogueAsync(username, catalogue, future ->
					{
						try
						{
							future.get();
							myParty.setDirtyCatalogue(false);
							frame.appendToConsole("My Catalogue has been successfully updated by the CDR service.", Color.GREEN);
						}
						catch(Exception e)
						{
							processException(e, "My Catalogue has not been updated by the CDR service! ");
						}
						finally
						{
							frame.enableCatalogueMenuItems();
						}
					});

					frame.appendToConsole("My Catalogue has been sent to the CDR service. Waiting for a response...", Color.BLACK);

/*				//creating XML document - for test purpose only
 				// MMM: maybe JAXBContext should be private class field, if it is used from multiple class methods
				JAXBContext jc = JAXBContext.newInstance("oasis.names.specification.ubl.schema.xsd.catalogue_21"); //packageList
				Marshaller m = jc.createMarshaller();
				//m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
				ObjectFactory objFactory = new ObjectFactory();
				JAXBElement<CatalogueType> catalogueElement = objFactory.createCatalogue(catalogue);
				try
				{
					//JAXB.marshal(catalogueElement, System.out );
					JAXB.marshal(catalogueElement, new FileOutputStream("catalogue.xml"));
				}
				catch (FileNotFoundException e)
				{
					System.out.println("Could not save Catalogue document to the file catalogue.xml");
				}
				catch (JAXBException e)
				{
					logger.error("Exception is ", e);
					frame.enableCatalogueMenuItems();
				}*/
				}
			}
			else
			{
				frame.appendToConsole("My Catalogue has not been sent to the CDR service because it is malformed. "
						+ "All catalogue items should have a name and catalogue has to have at least one item.", Color.RED);
				frame.enableCatalogueMenuItems();
			}
		}
		catch(WebServiceException e)
		{
			logger.error("Exception is ", e);
			frame.appendToConsole("My Catalogue has not been updated by the CDR service!"
					+ " Server is not accessible. Please try again later.", Color.RED);
			frame.enableCatalogueMenuItems();
		}
	}

	/**
	 * Pulls my Catalogue from the Central Data Repository.
	 */
	@Deprecated
	public void cdrPullMyCatalogue()
	{
		try
		{
			Server port = getCDRPort();

			//CatalogueType catalogue = port.getDocument();
			port.findCatalogueAsync(myParty.getCoreParty().getPartyID(), future ->
			{
				try
				{
					final FindCatalogueResponse response = future.get();
					final CatalogueType catalogue = response.getReturn();
					final BusinessParty myFollowingParty = myParty.getMyFollowingParty();
					if(catalogue != null)
					{
						frame.appendToConsole("Catalogue has been successfully retrieved from the CDR service.", Color.GREEN);
						myFollowingParty.setProducts(catalogue);
					}
					else
					{
						StringBuilder consoleMsg = new StringBuilder("Catalogue does not exist.");
						if(myFollowingParty.getProductCount() != 0)
						{
							myFollowingParty.clearProducts();
							consoleMsg.append(" My Catalogue has been removed from My Party in the Following parties.");
						}
						frame.appendToConsole(consoleMsg.toString(), Color.GREEN);
					}
				}
				catch(Exception e)
				{
					processException(e, "Catalogue could not be retrieved from the CDR service! Server responds:");
				}
				finally
				{
					frame.enableCatalogueMenuItems();
					frame.repaintTabbedPane();
				}
			});
			frame.appendToConsole("Request for catalogue has been sent to the CDR service. Waiting for a response...", Color.BLACK);
		}
		catch(WebServiceException e)
		{
			frame.appendToConsole("My Party has not been synchronised with the CDR service!"
					+ " Server is not accessible. Please try again later.", Color.RED);
			frame.enableCatalogueMenuItems();
		}
	}

	/**
	 * Sends Catalogue deletion request from the CDR service.
	 */
	public void cdrDeleteMyCatalogue()
	{
		try
		{
			CatalogueDeletionType catalogueDeletion = myParty.createCatalogueDeletion(CDRParty);

			Server port = getCDRPort();
			String username = myParty.getUsername();
			port.deleteCatalogueAsync(username, catalogueDeletion, future ->
			{
				try
				{
					DeleteCatalogueResponse response = future.get();
					myParty.setDirtyCatalogue(true);
					myParty.setInsertMyCatalogue(true);
					myParty.removeCatalogueIssueDate();
					frame.appendToConsole("Catalogue has been successfully deleted from the CDR service.", Color.GREEN);
				}
				catch(Exception e)
				{
					processException(e, "Catalogue has not been deleted from the CDR service!");
				}
				finally
				{
					frame.enableCatalogueMenuItems();
				}
			});
			frame.appendToConsole("Request for the Catalogue deletion has been sent to the CDR service. Waiting for a response...", Color.BLACK);
		}
		catch(WebServiceException e)
		{
			frame.appendToConsole("Catalogue has not been deleted from the CDR service!"
					+ " Server is not accessible. Please try again later.", Color.RED);
			frame.enableCatalogueMenuItems();
		}
	}

	/**
	 * Sends a follow request to the CDR service.
	 * @param followingName name of the party to follow
	 * @param followingID ID of the party to follow
	 * @param partner true when party to be followed is set to be a business partner
	 */
	public void cdrFollowParty(String followingName, String followingID, boolean partner)
	{
		try
		{
			Server port = getCDRPort();
			String myPartyId = myParty.getPartyID();

			port.followPartyAsync(myPartyId, followingID, future ->
			{
				try
				{
					FollowPartyResponse response = future.get();
					PartyType party = response.getReturn();
					myParty.addFollowingParty(party, partner);
					StringBuilder msg = new StringBuilder("Party " + followingName + " has been successfully added to the following parties");
					if(partner)
						msg.append(" as a business partner.");
					else
						msg.append(".");
					frame.appendToConsole(msg.toString(), Color.GREEN);
					frame.repaintTabbedPane();
				}
				catch(Exception e)
				{
					processException(e, "Party " + followingName + " could not be added to the following parties!");
				}
			});
			frame.appendToConsole("Follow request has been sent to the CDR service. Waiting for a response...", Color.BLACK);
		}
		catch(WebServiceException e)
		{
			frame.appendToConsole("Follow request has not been sent to the CDR service!"
					+ " Server is not accessible. Please try again later.", Color.RED);
		}
	}

	/**Sends a unfollow request to the CDR service.
	 * @param myPartyId MyParty's ID
	 * @param followingID Id of the party to follow
	 */
	public void cdrUnfollowParty(BusinessParty followingParty)
	{
		try
		{
			Server port = getCDRPort();
			final String myPartyId = myParty.getPartyID();
			final String followingName = followingParty.getPartySimpleName();
			final String followingID = followingParty.getPartyID();

			port.unfollowPartyAsync(myPartyId, followingID, future ->
			{
				try
				{
					future.get();
					myParty.removeFollowingParty(followingParty);
					myParty.addArchivedParty(followingParty);
					StringBuilder msg = new StringBuilder("Party " + followingName + " has been successfully removed from the following parties.");
					frame.appendToConsole(msg.toString(), Color.GREEN);
					frame.repaintTabbedPane();
				}
				catch(Exception e)
				{
					processException(e, "Party " + followingName + " could not be removed from the following parties!");
				}
			});
			frame.appendToConsole("Unfollow request has been sent to the CDR service. Waiting for a response...", Color.BLACK);
		}
		catch(WebServiceException e)
		{
			frame.appendToConsole("Unfollow request has not been sent to the CDR service!"
					+ " Server is not accessible. Please try again later.", Color.RED);
		}
	}

	/**
	 * Sends request to the CDR service for all IDs of new DocBox documents.
	 * @return {@link Semaphore} object that enables this CDR service call to be sequentally
	 * ordered with other service calls. After the end of this method number of permits in
	 * this {@code Semaphore} is 1.
	 */
	public Semaphore cdrGetNewDocuments()
	{
		Semaphore sequential = new Semaphore(0);
		try
		{
			Server port = getCDRPort();
			final String myPartyId = myParty.getPartyID();
			final DocBoxAllIDsSearchCriterion criterion = new DocBoxAllIDsSearchCriterion();
			criterion.setPartyID(myPartyId);
			port.findAllDocBoxDocumentIDsAsync(criterion, future ->
			{
				try
				{
					final FindAllDocBoxDocumentIDsResponse response = future.get();
					final List<String> docBoxIDs = response.getReturn();
					final int docCount = docBoxIDs.size();
					if(docBoxIDs != null && docCount != 0)
					{
						String plural = docCount + " documents";
						String there = "There are ";
						if(docCount == 1)
						{
							plural = docCount + " document";
							there = "There is ";
						}
						frame.appendToConsole(there + plural + " in my DocBox.", Color.BLACK);
						frame.appendToConsole("Started download of " + plural + ".", Color.BLACK);
						CountDownLatch finished = new CountDownLatch(docCount);
						Semaphore oneAtATime = new Semaphore(1);
						for(String docID : docBoxIDs)
						{
							oneAtATime.acquire();
							DocBoxDocumentSearchCriterion docCriterion = new DocBoxDocumentSearchCriterion();
							docCriterion.setPartyID(myPartyId);
							docCriterion.setDocumentID(docID);
							port.findDocBoxDocumentAsync(docCriterion, docFuture ->
							{
								try
								{
									final FindDocBoxDocumentResponse res = docFuture.get();
									final Object document = res.getReturn();
									oneAtATime.release();
									placeDocBoxDocument(document, docID);
									frame.repaintTabbedPane();
									port.deleteDocBoxDocumentAsync(myParty.getUsername(), docID, deleteFuture -> {});
								}
								catch (Exception e)
								{
									oneAtATime.release();
									processException(e, "Document " + docID + " could not be downloaded!");
								}
								finally
								{
									finished.countDown();
								}
							});
						}
						finished.await();
						frame.appendToConsole("Finished download of " + plural + ".", Color.BLACK);
					}
					else
						frame.appendToConsole("There are no new documents in my DocBox.", Color.GREEN);
				}
				catch(Exception e)
				{
					processException(e, "Download request of new documents has not been successcully processed!");
				}
				finally
				{
					sequential.release();
				}
			});
			frame.appendToConsole("Download request of new documents has been sent to the CDR service. Waiting for a response...", Color.BLACK);
		}
		catch(WebServiceException e)
		{
			frame.appendToConsole("Request for new documents has not been sent to the CDR service!"
					+ " Server is not accessible. Please try again later.", Color.RED);
		}
		return sequential;
	}

	/**Place DocBox document in the proper place within local domain model.
	 * @param document document to be processed and placed
	 */
	private void placeDocBoxDocument(Object document, String docID)
	{
		final Class<?> documentClazz = document.getClass();
		if(documentClazz == CatalogueType.class)
		{
			frame.appendToConsole("Catalogue document with the ID: " + docID + " has been successfully retrieved.", Color.GREEN);
			myParty.placeDocBoxCatalogue((CatalogueType) document, docID);
		}
		else if(documentClazz == PartyType.class)
		{
			frame.appendToConsole("Party document with the ID: " + docID + " has been successfully retrieved.", Color.GREEN);
			myParty.placeDocBoxParty((PartyType) document, docID);
		}
		else if(documentClazz == CatalogueDeletionType.class)
		{
			frame.appendToConsole("CatalogueDeletion document with the ID: " + docID + " has been successfully retrieved.", Color.GREEN);
			myParty.placeDocBoxCatalogueDeletion((CatalogueDeletionType) document, docID);
		}
		else if(documentClazz == DeregistrationNotice.class)
		{
			frame.appendToConsole("DeregistrationNotice document with the ID: " + docID + " has been successfully retrieved.", Color.GREEN);
			myParty.placeDocBoxDeregistrationNotice((DeregistrationNotice) document, docID);
		}
		else
			frame.appendToConsole("Document with the ID: " + docID +
					"of the unkwown type has been successfully retrieved.", Color.GREEN);

	}

	/**Processes exception thrown by called webmethod or some local one and displays
	 * exception message on the console.
	 * @param e exception to be processed
	 * @param msg message to be displayed on the console
	 */
	private void processException(Exception e, String msg) //MMM: put StringBuilder instead of the String argument
	{
		logger.error("Exception is ", e);
		StringBuilder msgBuilder = new StringBuilder(msg).append(" ");
		Throwable cause = e.getCause();
		if(cause == null)
			msgBuilder.append(e.getMessage());
		else
		{
			msgBuilder.append("Server responds: ");
			if(cause instanceof RutaException)
				msgBuilder.append(cause.getMessage()).append(" ").append(((RutaException) cause).getFaultInfo().getDetail());
			else
				msgBuilder.append(trimSOAPFaultMessage(cause.getMessage()));
		}
		frame.appendToConsole(msgBuilder.toString(), Color.RED);
	}

	/**Removes automatically prepended and appended portion of the SOAPFault detail string.
	 * @param message string to be processed
	 * @return trimmed string
	 */
	private static String trimSOAPFaultMessage(String message)
	{
		return message.replaceFirst("Client received SOAP Fault from server: (.+) "
				+ "Please see the server log to find more detail regarding exact cause of the failure.", "$1");
	}

	/**Testing of storing two parties to the CDR database.
	 *
	 */
	public void testParty()
	{
		/*		Server port = getCDRPort();
		frame.appendToConsole("TEST: Request for the storing of the two parties has been sent to the CDR service.", Color.MAGENTA);
		port.testPartyAsync(myParty.getUsername(), "amanas", myParty.getCoreParty(), future ->
		{
			String msg = "TEST has failed! ";
			try
			{
				future.get();
				msg = "TEST has succeded.";
				frame.appendToConsole(msg, Color.MAGENTA);
			}
			catch(Exception e)
			{
				msg += "Server responds: ";
				if(e.getCause() instanceof RutaException)
					frame.appendToConsole(msg + e.getCause().getMessage() + " " +
							((RutaException)e.getCause()).getFaultInfo().getDetail(), Color.MAGENTA);
				else
					frame.appendToConsole(msg + e.getCause().getMessage(), Color.MAGENTA);
				logger.error("Exception is ", e);
			}
		});*/
	}

	public void testPhax()
	{
		final String sCurrency = "EUR";

		// Create domain object
		final InvoiceType aInvoice = new InvoiceType ();

		// Fill it
		aInvoice.setID ("Dummy Invoice number");
		aInvoice.setIssueDate(InstanceFactory.getDate());

		final SupplierPartyType aSupplier = new SupplierPartyType ();
		aInvoice.setAccountingSupplierParty (aSupplier);

		final CustomerPartyType aCustomer = new CustomerPartyType ();
		aInvoice.setAccountingCustomerParty (aCustomer);

		final MonetaryTotalType aMT = new MonetaryTotalType ();
		aMT.setPayableAmount (BigDecimal.TEN).setCurrencyID (sCurrency);
		aInvoice.setLegalMonetaryTotal (aMT);

		final InvoiceLineType aLine = new InvoiceLineType ();
		aLine.setID ("1");

		final ItemType aItem = new ItemType ();
		aLine.setItem (aItem);

		aLine.setLineExtensionAmount (BigDecimal.TEN).setCurrencyID (sCurrency);

		aInvoice.addInvoiceLine (aLine);

		// Write to disk
		final ESuccess eSuccess = UBL21Writer.invoice ().write (aInvoice, new File ("target/dummy-invoice.xml"));
	}

	public RutaVersion getClientVersion()
	{
		return version;
	}

	/**Gets the default end point of the CDR service.
	 * @return {@code String} representing the end point
	 */
	public static String getDefaultEndPoint()
	{
		return defaultEndPoint;
	}

	/**Gets the end point of the CDR service.
	 * @return {@code String} representing the end point
	 */
	public static String getCDREndPoint()
	{
		return cdrEndPoint;
	}

	/**Sets the end point of the CDR service.
	 * @param endPoint {@code String} representing the end point
	 */
	public static void setCDREndPoint(String endPoint)
	{
		Client.cdrEndPoint = endPoint;
	}

	/**Reverts to default value of the service end point location.
	 */
	public static void resetCDREndPoint()
	{
		Client.cdrEndPoint = defaultEndPoint;
	}

	/**Temporary setting of the endpoint address property for TCP/IP Monitor in Eclipse.
	 * @param port port of the service on which the endpoint address property is set
	 */
	private static void bindEclipseEndPoint(Server port)
	{
		((BindingProvider) port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, eclipseMonitorEndPoint);
	}

	/**Binds the endpoint address of the CDR service to the {@code endPoint} static field. If {@code endPoint}
	 * field is {@code null} then default value (i.e. generated by the {@codeJAX-WS}) is used.
	 * @param port
	 */
	private static void bindCDREndPoint(Server port)
	{
		((BindingProvider) port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, cdrEndPoint);
	}

	/*	public OLDDataMapper getPartyDataMapper()
	{
		return partyDataMapper;
	}*/

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
				logger.error("Exception is ", e);
			}
		}
	}

	public MyParty getMyParty()
	{
		return myParty;
	}

	public void setMyParty(MyParty party)
	{
		myParty = party;
	}

	public Party getCDRParty()
	{
		if (CDRParty == null)
		{
			CDRParty = new Party();
			CDRParty.setPartySimpleName("CDR");
		}
		return CDRParty;
	}

	public void setCDRParty(Party party)
	{
		CDRParty = party;
	}

	public void setFrame(ClientFrame clientFrame)
	{
		frame = clientFrame;
	}

	/**Gets the CDR web service port.
	 * @return web service port
	 * @throws WebServiceException if could not connect to the CDR service
	 */
	private Server getCDRPort()
	{
		try
		{
			//getting webservice port
			CDRService service = new CDRService();
			service.setHandlerResolver(new ClientHandlerResolver(myParty));
			Server port = service.getCDRPort();
			//temporary setting for TCP/IP Monitor in Eclipse
			//bindEclipseEndPoint(port);
			if(!defaultEndPoint.equals(cdrEndPoint))
				bindCDREndPoint(port);


			BindingProvider bp = (BindingProvider) port;
			SOAPBinding binding = (SOAPBinding) bp.getBinding();
			binding.setMTOMEnabled(true);
			return port;
		}
		catch(Exception e)
		{
			logger.error("Exception is ", e);
			throw e;
		}
	}

	//Method may be used for some testing purposes
	@Deprecated
	public void cdrSearchParty(CatalogueSearchCriterion criterion)
	{
		try
		{
			Server port = getCDRPort();
			port.searchCatalogueAsync(criterion, futureResult ->
			{
				try
				{
					SearchCatalogueResponse res = futureResult.get();
					//handle the Catalogue list
					frame.appendToConsole("Search results have been successfully retrieved from the CDR service.", Color.GREEN);
				}
				catch(Exception e)
				{
					processException(e, "Search request could not be processed! ");
				}
			});
			frame.appendToConsole("Search request has been sent to the CDR service. Waiting for a response...", Color.BLACK);
		}
		catch(WebServiceException e)
		{
			frame.appendToConsole("The search request has not been processed!" +
					"Server is not accessible. Please try again later.", Color.RED);
		}
	}

	@Deprecated
	public void cdrSearch(String searchName, CatalogueSearchCriterion criterion)
	{
		try
		{
			Server port = getCDRPort();
			if(criterion.isCatalogueSearchedFor()) //querying parties and catalogues
			{
				port.searchCatalogueAsync(criterion, futureResult ->
				{
					try
					{
						SearchCatalogueResponse res = futureResult.get();
						//handle the Catalogue list
						List<CatalogueType> results = res.getReturn();
						/*						for(CatalogueType cat : results)
							frame.appendToConsole("Party: " + cat.getProviderParty().getPartyName().get(0).getNameValue() +
									" items: " + cat.getCatalogueLineCount(), Color.GREEN);*/
						final Search<CatalogueType> search = new Search<CatalogueType>(searchName, criterion, results);
						if(search.getResultCount() != 0)
						{
							frame.appendToConsole("Search results for search request \"" + searchName + "\" have been successfully retrieved from the CDR service.", Color.GREEN);
							myParty.getCatalogueSearches().add(0, search);
							frame.repaintTabbedPane();
						}
						else
						{
							Search.decreaseSearchNumber();
							frame.appendToConsole("Nothing found at CDR service that conforms to your search criterion.", Color.GREEN);
						}
					}
					catch(Exception e)
					{
						processException(e, "Search request could not be processed! ");
					}
				});
				frame.appendToConsole("Search request \"" + searchName + "\" has been sent to the CDR service. Waiting for a response...", Color.BLACK);
			}
			else // querying only parties
			{
				//*******************TEST*************************
				/*				String query = "declare variable $party-name external := (); \n" +
						"declare variable $party-company-id external := (); \n" +
						"declare variable $party-class-code external := ();\n" +
						"declare variable $party-city external := ();\n" +
						"declare variable $party-country external := ();\n" +
						"declare variable $party-all external := true();";

				String partyName = criterion.getPartyName();
				String partyCompanyID = criterion.getPartyCompanyID();
				String partyClassCode = criterion.getPartyClassCode();
				String partyCity = criterion.getPartyCity();
				String partyCountry = criterion.getPartyCountry();
				boolean partyAll = criterion.isPartyAll();

				String itemName = criterion.getItemName();
				String itemBarcode = criterion.getItemBarcode();
				String itemCommCode = criterion.getItemCommCode();
				boolean itemAll = criterion.isItemAll();

				String preparedQuery = query;
				if(partyName != null)
					preparedQuery = preparedQuery.replaceFirst("party-name( )+external( )*:=( )*[(][)]",
							(new StringBuilder("party-name := '").append(partyName).append("'")).toString());
				if(partyCompanyID != null)
					preparedQuery = preparedQuery.replaceFirst("party-company-id( )+external( )*:=( )*[(][)]",
							(new StringBuilder("party-company-id := '").append(partyCompanyID).append("'")).toString());
				if(partyClassCode != null)
					preparedQuery = preparedQuery.replaceFirst("party-class-code( )+external( )*:=( )*[(][)]",
							(new StringBuilder("party-class-code := '").append(partyClassCode).append("'")).toString());
				if(partyCity != null)
					preparedQuery = preparedQuery.replaceFirst("party-city( )+external( )*:=( )*[(][)]",
							(new StringBuilder("party-city := '").append(partyCity).append("'")).toString());
				if(partyCountry != null)
					preparedQuery = preparedQuery.replaceFirst("party-country( )+external( )*:=( )*[(][)]",
							(new StringBuilder("party-country := '").append(partyCountry).append("'")).toString());
				if(!partyAll)
					preparedQuery = preparedQuery.replaceFirst("party-all( )+external( )*:=( )*true",
							(new StringBuilder("party-all := false")).toString());*/

				//*******************TEST*************************
				port.searchPartyAsync(criterion, futureResult ->
				{
					try
					{
						SearchPartyResponse res = futureResult.get();
						//handle the Party list

						List<PartyType> results = res.getReturn();
						/*						for(PartyType p : results)
							frame.appendToConsole(p.getPartyName().get(0).getNameValue(), Color.GREEN);*/
						final Search<PartyType> search = new Search<PartyType>(searchName, criterion , results);
						if(search.getResultCount() != 0)
						{
							frame.appendToConsole("Search results for search request \"" + searchName + "\" have been successfully retrieved from the CDR service.", Color.GREEN);
							myParty.getPartySearches().add(0, search);
							frame.repaintTabbedPane();
						}
						else
						{
							Search.decreaseSearchNumber();
							frame.appendToConsole("Nothing found at CDR service that conforms to your search criterion.", Color.GREEN);
						}
					}
					catch(Exception e)
					{
						processException(e, "Search request could not be processed! ");
					}
					finally
					{
						frame.enableSearchMenuItems();
					}
				});
				frame.appendToConsole("Search request \"" + searchName + "\" has been sent to the CDR service. Waiting for a response...", Color.BLACK);
			}
		}
		catch(WebServiceException e)
		{
			frame.appendToConsole("The search request has not been processed! Server is not accessible. Please try again later.", Color.RED);
		}
	}

	@Deprecated
	@SuppressWarnings("unchecked")
	public void cdrSearch(Search<?> search)
	{
		try
		{
			Server port = getCDRPort();
			CatalogueSearchCriterion criterion = search.getCriterion();
			if(criterion.isCatalogueSearchedFor()) //querying parties and catalogues
			{
				port.searchCatalogueAsync(criterion, futureResult ->
				{
					try
					{
						SearchCatalogueResponse res = futureResult.get();
						//handle the Catalogue list
						List<CatalogueType> results = res.getReturn();
						if(results.size() != 0)
						{
							((Search<CatalogueType>) search).setResults(results);
							frame.appendToConsole("Search results have been successfully retrieved from the CDR service.", Color.GREEN);
							frame.repaintTabbedPane();
						}
						else
							frame.appendToConsole("Nothing found at CDR service that conforms to your search criterion.", Color.GREEN);
					}
					catch(Exception e)
					{
						processException(e, "Search request could not be processed! ");
					}
					finally
					{
						frame.enableSearchMenuItems();
					}
				});
				frame.appendToConsole("Search request has been sent to the CDR service. Waiting for a response...", Color.BLACK);
			}
			else // querying only parties
			{
				port.searchPartyAsync(criterion, futureResult ->
				{
					try
					{
						SearchPartyResponse res = futureResult.get();

						List<PartyType> results = res.getReturn();
						if(results.size() != 0)
						{
							((Search<PartyType>)search).setResults(results);
							frame.appendToConsole("Search results have been successfully retrieved from the CDR service.", Color.GREEN);
							frame.repaintTabbedPane();
						}
						else
							frame.appendToConsole("Nothing found at CDR service that conforms to your search criterion.", Color.GREEN);
					}
					catch(Exception e)
					{
						processException(e, "Search request could not be processed! ");
					}
					finally
					{
						frame.enableSearchMenuItems();
					}
				});
				frame.appendToConsole("Search request has been sent to the CDR service.", Color.BLACK);
			}
		}
		catch(WebServiceException e)
		{
			frame.appendToConsole("Search request has not been processed! Server is not accessible. Please try again later.", Color.RED);
			frame.enableSearchMenuItems();
		}
	}

	/**Sends search request to the CDR service.
	 * @param search {@link Search} object representing the search and results
	 * @param exist true if the search is repeated, i.e. it is not a new one
	 */
	@SuppressWarnings("unchecked")
	public void cdrSearch(Search<?> search, boolean exist)
	{
		try
		{
			Server port = getCDRPort();
			search.setTimestamp();
			CatalogueSearchCriterion criterion = search.getCriterion();
			if(criterion.isCatalogueSearchedFor()) //querying parties and catalogues
			{
				port.searchCatalogueAsync(criterion, futureResult ->
				{
					try
					{
						SearchCatalogueResponse res = futureResult.get();
						List<CatalogueType> results = res.getReturn();
						if(exist)
							myParty.getCatalogueSearches().remove(search);
						if(results.size() != 0)
						{
							final Search<CatalogueType> newSearch = (Search<CatalogueType>) search;
							newSearch.setResults(results);
							myParty.getCatalogueSearches().add(0, newSearch);
							frame.appendToConsole("Search results for search request \"" + newSearch.getSearchName() +
									"\" have been successfully retrieved from the CDR service.", Color.GREEN);
							frame.repaintTabbedPane();
						}
						else
						{
							if(!exist)
								Search.decreaseSearchNumber();
							frame.appendToConsole("Nothing found at CDR service that conforms to your search criterion.", Color.GREEN);
						}
					}
					catch(Exception e)
					{
						processException(e, "Search request could not be processed! ");
					}
					finally
					{
						frame.enableSearchMenuItems();
					}
				});
				frame.appendToConsole("Search request \"" + search.getSearchName() +
						"\" has been sent to the CDR service. Waiting for a response...", Color.BLACK);
			}
			else // querying only parties
			{
				port.searchPartyAsync(criterion, futureResult ->
				{
					try
					{
						SearchPartyResponse res = futureResult.get();
						List<PartyType> results = res.getReturn();
						if(exist)
							myParty.getPartySearches().remove(search);
						if(results.size() != 0)
						{
							final Search<PartyType> newSearch = (Search<PartyType>) search;
							newSearch.setResults(results);
							myParty.getPartySearches().add(0, newSearch);
							frame.appendToConsole("Search results for search request \"" + newSearch.getSearchName() +
									"\" have been successfully retrieved from the CDR service.", Color.GREEN);
							frame.repaintTabbedPane();
						}
						else
						{
							if(!exist)
								Search.decreaseSearchNumber();
							frame.appendToConsole("Nothing found at CDR service that conforms to your search criterion.", Color.GREEN);
						}
					}
					catch(Exception e)
					{
						processException(e, "Search request could not be processed!");
					}
					finally
					{
						frame.enableSearchMenuItems();
					}
				});
				frame.appendToConsole("Search request \"" + search.getSearchName() +
						"\" has been sent to the CDR service. Waiting for a response...", Color.BLACK);
			}
		}
		catch(WebServiceException e)
		{
			frame.appendToConsole("Search request \"" + search.getSearchName() +
					"\" has not been processed! Server is not accessible. Please try again later.", Color.RED);
			frame.enableSearchMenuItems();
			if(!exist)
				Search.decreaseSearchNumber();
		}
	}

	/**Sends request to the CDR service to see if there is an available update of the Ruta Client Application.
	 * @return instance of the {@link Future} interface returned by the webmethod invoked for this service
	 */
	public Future<?> cdrUpdateRutaClient()
	{
		Future<?> future = null;
		try
		{
			Server port = getCDRPort();
			future = port.updateRutaClientAsync(version.getVersion(), futureResult -> { });
		}
		catch(WebServiceException e)
		{
			frame.appendToConsole("Update request has hot been sent! Server is not accessible. Please try again later.", Color.RED);
		}
		return future;
	}

	/**Sends update notification to the CDR service. This notification tells other {@code Ruta Client}s if there is new
	 * version of the Ruta Client Application.
	 * @param version version of the new Ruta Client Application
	 */
	public void cdrUpdateNotification(RutaVersion version)
	{
		try
		{
			Server port = getCDRPort();
			port.notifyUpdateAsync(version, futureResult ->
			{
				try
				{
					futureResult.get();
					frame.appendToConsole("CDR service has been successfully notified about new Ruta Client version.", Color.GREEN);
				}
				catch(Exception e)
				{
					processException(e, "CDR service could not be notified! ");
				}
			});
			frame.appendToConsole("Update notification has been sent to the CDR service. Waiting for a response...", Color.BLACK);
		}
		catch(WebServiceException e)
		{
			frame.appendToConsole("CDR service could not be notified! Server is not accessible. Please try again later.", Color.RED);
		}
	}

	public void cdrClearCache()
	{
		try
		{
			Server port = getCDRPort();
			port.clearCacheAsync(future ->
			{
				try
				{
					future.get();
					frame.appendToConsole("CDR service successfully cleared its cache.", Color.GREEN);
				}
				catch (Exception e)
				{
					processException(e, "CDR service could not clear its cache! ");
				}

			});
			frame.appendToConsole("Request for clearing the CDR service's cache has been sent. Waiting for a response...", Color.BLACK);
		}
		catch(WebServiceException e)
		{
			frame.appendToConsole("CDR service could not clear its cache! Server is not accessible. Please try again later.", Color.RED);
		}

	}

	public void cdrReportBug(BugReport bug)
	{
		try
		{
			bug.setReportedBy(myParty.getUsername());
			Server port = getCDRPort();
			port.insertBugReportAsync(bug, futureResult ->
			{
				try
				{
					InsertBugReportResponse res = futureResult.get();
					frame.appendToConsole("Bug report has been successfully deposited to the CDR service.", Color.GREEN);
				}
				catch(Exception e)
				{
					processException(e, "Bug could not be reported! ");
				}
			});
			frame.appendToConsole("Bug report has been sent to the CDR service. Waiting for a response...", Color.BLACK);
		}
		catch(WebServiceException e)
		{
			frame.appendToConsole("Bug could not be reported to the CDR service! Server is not accessible. Please try again later.", Color.RED);
		}
	}

	/*	public void cdrFindAllBugs()
	{
		try
		{
			Server port = getCDRPort();
			port.findAllBugReportsAsync(futureResult ->
			{
				StringBuilder msg = new StringBuilder("Bug report list could not be retrieved! ");
				try
				{
					FindAllBugReportsResponse res = futureResult.get();
					frame.appendToConsole("Bug report list has been successfully retrieved from the CDR service.", Color.GREEN);
					setBugReports(res.getReturn());
				}
				catch(Exception e)
				{
					msg.append("Server responds: ");
					Throwable cause = e.getCause();
					if(cause instanceof RutaException)
						msg.append(cause.getMessage()).append(" ").append(((RutaException) cause).getFaultInfo().getDetail());
					else
						msg.append(trimSOAPFaultMessage(cause.getMessage()));
					frame.appendToConsole(msg.toString(), Color.RED);
				}
			});
			frame.appendToConsole("Request for the list of all bug reports has been sent to the CDR service. Waiting for a response...", Color.BLACK);

		}
		catch(WebServiceException e)
		{
			frame.appendToConsole("Bug list not be retrived from the CDR service! Server is not accessible. Please try again later.", Color.RED);
		}
	}*/

	/**Sends a request to the CDR for the list of all {@link BugReport reported bugs}. Method returns
	 * a {@link Future} object which can be inspected for the result of the returned request from the CDR.
	 * @return {@code Future} object representing the response.
	 */
	public Future<?> cdrFindAllBugs()
	{
		Future<?> future = null;
		try
		{
			Server port = getCDRPort();
			future = port.findAllBugReportsAsync(futureResult -> { });
			frame.appendToConsole("Request for the list of all bug reports has been sent to the CDR service. Waiting for a response...",
					Color.BLACK);
		}
		catch(WebServiceException e)
		{
			frame.appendToConsole("Bug list not be retrived from the CDR service! Server is not accessible. Please try again later.",
					Color.RED);
		}
		return future;
	}

	/**Sends a request to the CDR for the list of {@link BugReport}s based on some search criterion. Method returns
	 * a {@link Future} object which can be inspected for the result of the returned request from the CDR.
	 * @param criterion
	 * @return {@code Future} object representing the response
	 */
	public Future<?> cdrSearchBugReport(BugReportSearchCriterion criterion)
	{
		Future<?> future = null;
		try
		{
			Server port = getCDRPort();
			future = port.searchBugReportAsync(criterion, futureResult -> { });
			frame.appendToConsole("Request for the list of all bug reports has been sent to the CDR service. Waiting for a response...",
					Color.BLACK);
		}
		catch(WebServiceException e)
		{
			frame.appendToConsole("Bug list could not be retrived from the CDR service! Server is not accessible. Please try again later.",
					Color.RED);
		}
		return future;
	}

	/**Send a request to the CDR for the {@link BugReport}.
	 * @param id {@code BugReport}'s id
	 * @return {@code Future} object representing the response
	 */
	public Future<?> cdrFindBug(String id)
	{
		Future<?> future = null;
		try
		{
			Server port = getCDRPort();
			future = port.findBugReportAsync(id, futureResult -> { });
			frame.appendToConsole("Request for the bug report has been sent to the CDR service. Waiting for a response...",
					Color.BLACK);
		}
		catch(WebServiceException e)
		{
			frame.appendToConsole("Bug report could not be retrived from the CDR service! Server is not accessible. Please try again later.",
					Color.RED);
		}
		return future;
	}

	/**Send a request to the CDR for adding a {@link ReportComment} to the {@link BugReport}.
	 * @param id {@code BugReport}'s id
	 * @param comment comment to be added
	 * @return {@code Future} object representing the response
	 */
	public Future<?> cdrAddBugReportComment(String id, ReportComment comment)
	{
		Future<?> future = null;
		try
		{
			Server port = getCDRPort();
			future = port.addBugReportCommentAsync(id, comment, futureREsult -> { });
			frame.appendToConsole("Comment has been sent to the CDR service. Waiting for a response...", Color.BLACK);
		}
		catch(WebServiceException e)
		{
			frame.appendToConsole("Comment could not be sent to the CDR service! Server is not accessible. Please try again later.",
					Color.RED);
		}
		return future;
	}

	/**Temporary method. Should be deleted.
	 *
	 */
	public void cdrInsertFile()
	{
		try
		{

			Server port = getCDRPort();
			File image = new File("test.jpg");
			long length = image.length();
			String path = image.getPath();
			FileDataSource fileDataSource = new FileDataSource(path);
			DataHandler dataHandler = new DataHandler(fileDataSource);

			/*	        BufferedInputStream bin = new BufferedInputStream(dataHandler.getInputStream());
	        byte buffer[] = new byte[(int)length];
	        bin.read(buffer);
	        bin.close();*/

			port.insertFileAsync(dataHandler, "test.jpg", futureResult ->
			{
				try
				{
					InsertFileResponse res = futureResult.get();
					frame.appendToConsole("File has been successfully deposited to the CDR service.", Color.GREEN);
				}
				catch(Exception e)
				{
					processException(e, "File could not be inserted! ");
				}
			});
			frame.appendToConsole("File has been sent to the CDR service. Waiting for a response...", Color.BLACK);
		}
		catch(WebServiceException e)
		{
			frame.appendToConsole("File not be deposited to the CDR service! Server is not accessible. Please try again later.", Color.RED);
		}
		catch(Exception e)
		{
			frame.appendToConsole("File could not be deposited to the CDR service! Error is on the client's side.", Color.RED);
		}
	}

	public void cdrInsertAttachment()
	{
		try
		{
			Server port = getCDRPort();
			File image = new File("test.jpg");
			/*			long length = image.length();
			String path = image.getPath();
	        FileDataSource fileDataSource = new FileDataSource(path);
	        DataHandler dataHandler = new DataHandler(fileDataSource);*/
			ReportAttachment att = new ReportAttachment(image, image.getName());

			/*	        BufferedInputStream bin = new BufferedInputStream(dataHandler.getInputStream());
	        byte buffer[] = new byte[(int)length];
	        bin.read(buffer);
	        bin.close();*/

			port.insertAttachmentAsync(att, "test.jpg", futureResult ->
			{
				try
				{
					InsertAttachmentResponse res = futureResult.get();
					frame.appendToConsole("File has been successfully deposited to the CDR service.", Color.GREEN);
				}
				catch(Exception e)
				{
					processException(e, "File could not be inserted! ");
				}
			});
			frame.appendToConsole("File has been sent to the CDR service. Waiting for a response...", Color.BLACK);
		}
		catch(WebServiceException e)
		{
			frame.appendToConsole("File not be deposited to the CDR service! Server is not accessible. Please try again later.", Color.RED);
		}
		catch(Exception e)
		{
			frame.appendToConsole("File could not be deposited to the CDR service! Error is on the client's side.", Color.RED);
		}
	}

	public void findAllParties()
	{
		Server port = getCDRPort();
		port.findAllPartiesAsync(futureResult ->
		{
			try
			{
				FindAllPartiesResponse res = futureResult.get();
				//handle the Catalogue list
				frame.appendToConsole("Search results have been successfully retrieved from the CDR service.", Color.GREEN);
			}
			catch(Exception e)
			{
				processException(e, "Search request could not be processed! ");
			}
		});
		frame.appendToConsole("Search request has been sent to the CDR service.", Color.BLACK);
	}

	/**Safely shuts down the data store.
	 * @throws Exception if data store could not be disconnected from
	 */
	public void shutdownDataStore() throws Exception
	{
		partyDataMapper.getConnector().shutdownDatabase();
	}

	/**Gracefully shuts down Ruta Client application.
	 */
	public void shutdownApplication()
	{
		boolean exit = true; //  = false when window should not be closed after exception is thrown
		saveProperties();
		frame.saveProperties();
		storeProperties(true);

		try
		{
			insertMyParty();
		}
		catch(Exception e)
		{
			logger.error(e.getMessage());
			String[] options = {"YES", "NO"};
			int choice = JOptionPane.showOptionDialog(frame, "Data could not be saved to the local data store! "
					+ "Do yo want to close the program anyway?", "Fatal error", JOptionPane.YES_NO_OPTION,
					JOptionPane.ERROR_MESSAGE, null, options, options[0]);
			if(choice == 1)
				exit = false;
		}
		if(exit)
		{
			try
			{
				shutdownDataStore();
			}
			catch(Exception e)
			{
				logger.error(e.getMessage());
				String[] options = {"YES", "NO"};
				int choice = JOptionPane.showOptionDialog(frame, "Data store could not be disconnected from! "
						+ "Do yo want to close the program anyway?", "Fatal error", JOptionPane.YES_NO_OPTION,
						JOptionPane.ERROR_MESSAGE, null, options, options[0]);
				if(choice == 1)
					System.exit(0);
			}
		}
	}

}
