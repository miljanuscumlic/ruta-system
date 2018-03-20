package rs.ruta.client;

import java.awt.Color;
import java.awt.EventQueue;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.swing.JOptionPane;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPBinding;

import org.exist.util.MimeTable;
import org.exist.util.MimeType;
import org.exist.xmldb.DatabaseInstanceManager;
import org.exist.xmldb.EXistResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.CollectionManagementService;

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
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.SupplierPartyType;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;
import rs.ruta.ClientHandlerResolver;
import rs.ruta.RutaNode;
import rs.ruta.client.datamapper.ClientMapperRegistry;
import rs.ruta.client.datamapper.LocalExistConnector;
import rs.ruta.client.gui.RutaClientFrame;
import rs.ruta.common.ReportAttachment;
import rs.ruta.common.ReportComment;
import rs.ruta.common.RutaUser;
import rs.ruta.common.BugReport;
import rs.ruta.common.BugReportSearchCriterion;
import rs.ruta.common.RutaVersion;
import rs.ruta.common.SearchCriterion;
import rs.ruta.common.CatalogueSearchCriterion;
import rs.ruta.common.DeregistrationNotice;
import rs.ruta.common.DocBoxAllIDsSearchCriterion;
import rs.ruta.common.DocBoxDocumentSearchCriterion;
import rs.ruta.common.datamapper.DataMapper;
import rs.ruta.common.datamapper.DatabaseException;
import rs.ruta.common.datamapper.DetailException;
import rs.ruta.common.datamapper.ExistConnector;
import rs.ruta.common.datamapper.MapperRegistry;
import rs.ruta.common.datamapper.RutaException;
import rs.ruta.services.CDRService;
import rs.ruta.services.FindAllDocBoxDocumentIDsResponse;
import rs.ruta.services.FindCatalogueResponse;
import rs.ruta.services.FindDocBoxDocumentResponse;
import rs.ruta.services.FollowPartyResponse;
import rs.ruta.services.NewRegisterUserResponse;
import rs.ruta.services.SearchCatalogueResponse;
import rs.ruta.services.SearchPartyResponse;
import rs.ruta.services.Server;
import rs.ruta.common.InstanceFactory;
import rs.ruta.common.PartySearchCriterion;

public class RutaClient implements RutaNode
{
	private static String packageList = "oasis.names.specification.ubl.schema.xsd.catalogue_21"; // colon separated package list
	final private static String defaultEndPoint = "http://ruta.sytes.net:9009/ruta-server-0.2.0-SNAPSHOT/CDR";
	private static String cdrEndPoint = defaultEndPoint;
	final private static String eclipseMonitorEndPoint = "http://localhost:7709/ruta-server-0.2.0-SNAPSHOT/CDR";
	MyParty myParty;
	//	private MyPartyXMLFileMapper<MyParty> myPartyDataMapper; //former store to myparty.xml
	private DataMapper<MyParty, String> myPartyDataMapper;
	private Party CDRParty;
	//	private CDRPartyTypeXMLFileMapper<Party> CDRPartyDataMapper; //MMM: not used anymore
	private RutaClientFrame frame;
	private static RutaVersion version = new RutaVersion("Client", "0.2.0-SNAPSHOT", "0.1.0", null);
	private Properties properties;
	private MapperRegistry mapperRegistry; //MMM: would be used instead of temporary ClientMapperRegistry and ExistConnector (see: constructor)
	private List<BugReport> bugReports;
	private static Logger logger = LoggerFactory.getLogger("rs.ruta.client");
	private String initialUsername;

	/**
	 * Constructs a {@code RutaClient} object.
	 * @param force if true it tells the constructor to try to create an object despite the fact that
	 * one instance of it had already been created and invoked by {@code Ruta Client application} from the same OS directory
	 * @throws DetailException if object could not be created or, when {@code force} is true and one instance of it already exists
	 */
	public RutaClient(boolean force) throws DetailException
	{
		logger.info("Opening Ruta Client Application");
		properties = new Properties();
		loadProperties();
		try
		{
			checkClientInstantiated();
			properties.setProperty("started", "true");
			storeProperties(false);
		}
		catch(DetailException e)
		{
			if(!force)
				throw e;
		}

		//myPartyDataMapper = new MyPartyXMLFileMapper<MyParty>(Client.this, "myparty.xml");
//		ExistConnector connector = new LocalExistConnector();
		mapperRegistry = new ClientMapperRegistry();
		//		MapperRegistry.initialize(mapperRegistry);
		//myPartyDataMapper = new RutaMyPartyExistMapper(this, connector);
		myPartyDataMapper = mapperRegistry.getMapper(MyParty.class);
		initialUsername = null;
		checkInstallation();
		myParty = new MyParty();
		CDRParty = getCDRParty();

		addShutDownHook();
	}

	/**
	 * Initializes fields of {@code RutaClient} object by retrieving data from local data store.
	 * This phase of data model initialization is before the view is initialized, so that the view could
	 * be populated with data if it exists in the data store.
	 * @throws Exception if retrieving data from the data store has been unsuccessful
	 */
	public void preInitialize() throws Exception
	{
		// trying to load party data from database
/*		ArrayList<MyParty> parties = (ArrayList<MyParty>) myPartyDataMapper.findAll();
		if(parties != null && parties.size() != 0)
		{
			myParty = parties.get(0);
			//TODO myParty.initialize(); //populating all lists of MyParty object by calling their respective dataMappers
			Search.setSearchNumber(myParty.getSearchNumber());
		}*/

		try
		{
			if(isUserRegistrated())
			{
				MyParty retrievedParty = myPartyDataMapper.findByUsername(initialUsername);
				if(retrievedParty != null)
				{
					myParty = retrievedParty;
					Search.setSearchNumber(myParty.getSearchNumber());

					Party coreParty = myParty.getCoreParty();
					if(!myParty.hasCoreParty())
						myParty.setCoreParty(coreParty);
					myParty.loadData();
				}
			}
		}
		catch(DetailException e)
		{ } //it's OK if user is not registered //MMM: maybe it should some error message be displayed???
	}

	/**
	 * Shows dialog for inputing {@code Party} data if data are not already initialized in the {@link #preInitialize} method.
	 * This phase of data model initialization is after the view is initialized.
	 * @throws DetailException if data could be read from the databse
	 */
	public void initialize() throws DetailException
	{
		Party coreParty = myParty.getCoreParty();
		if(!myParty.hasCoreParty() || coreParty.verifyParty() != null)
			myParty.setCoreParty(frame.showPartyDialog(coreParty, "My Party", true)); //displaying My Party Data dialog
		frame.updateTitle(myParty.getCoreParty().getPartySimpleName());
		if(initialUsername == null && !myParty.isRegisteredWithLocalDatastore())
		{
			initialUsername = frame.showLocalSignUpDialog("Local database registration");
			new Thread(() ->
			{
				frame.showCDRSignUpDialog("CDR registration");
			}).start();
		}
	}

	/**
	 * Gets the username of the localy registered user. This field is merely an indication whether the user has been
	 * registered with the local datastore. At the begining of the {@code Ruta Client} initialization process
	 * this property has a {@code null} value. It could be later reassigned from the {@code .properties} file or
	 * it could be retrieved after local registration process is done.
	 * @return username or {@code null}
	 */
	public String getInitialUsername()
	{
		return initialUsername;
	}

	/**
	 * Sets the value of field that is an indication whether the user has been registered with the local datastore.
	 * @param initialUsername
	 */
	public void setInitialUsername(String initialUsername)
	{
		this.initialUsername = initialUsername;
	}

	/**
	 * Adds shutdown hook to the {@link Runtime} object of the application.
	 */
	private void addShutDownHook()
	{
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			@Override
			public void run()
			{
				try
				{
					logger.info("Shutdown hook started.");
					//					properties.put("started", false);
					shutdownApplication();
					logger.info("Shutdown hook ended.");
					logger.info("Opening Ruta Client Application");
				}
				catch(Exception e)
				{
					logger.error("Exception is ", e);
				}
			}
		});
	}

	/**
	 * Checks whether xquery files are present in the database and writes them to the database if they are not present.
	 */
	public void checkInstallation()
	{
		if(!"true".equals(properties.getProperty("installed")))
		{
			try
			{
				mapperRegistry.getMapper(RutaUser.class).checkDatastoreSetup();
				properties.setProperty("installed", "true");
			}
			catch(DetailException e)
			{
				properties.setProperty("installed", "false");
				logger.warn("Failed to check the installation. Presence of xquery files is not confirmed.", e);
			}
		}
	}

	/**
	 * Checks whether the user is registered with the local database and sets {@code initialUsername} field
	 * if it can find it. If not {@code initialUsername} has {@code null} value.
	 * @return true if user is registered
	 * @throws DatabaseException due to database connectivity issues
	 */
	private boolean isUserRegistrated() throws DatabaseException
	{
		initialUsername = properties.getProperty("username");
		if(initialUsername == null)
		{
			List<String> usernames = MapperRegistry.getAccountUsernames();
			if(usernames != null && !usernames.isEmpty())
				initialUsername = usernames.get(0);
		}
		return initialUsername != null ? true : false;
	}

	public Properties getProperties()
	{
		return properties;
	}

	public void setProperties(Properties properties)
	{
		this.properties = properties;
	}

	/**
	 * Loads {@link Properties} from the {@code .properties} file.
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
		RutaClient.cdrEndPoint = properties.getProperty("cdrEndPoint", RutaClient.defaultEndPoint);
	}

	/**
	 * Stores {@link Properties} to the {@code .properties} file.
	 * @param end true if this method is invoked just before the program termination. Parameter
	 * is due to not so necessary optimisation enabling not to save properties to a file not needed
	 * to be saved before the application shutdown is invoked.
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

	/**
	 * Saves properties from {@code RutaClient} class fields to the {@link Properties} object.
	 */
	private void saveProperties()
	{
		properties.put("cdrEndPoint", RutaClient.cdrEndPoint);
		properties.put("started", "false");
	}

	/**
	 * Checks whether the instance of {@code RutaClient} has been already created and invoked from the same OS directory.
	 * @throws DetailException if the {@code RutaClient} has been already created
	 */
	private void checkClientInstantiated() throws DetailException
	{
		String prop = "started";
		boolean started = false;
		if(properties.get(prop) != null)
			started = Boolean.parseBoolean(properties.get(prop).toString());
		if(started)
			throw new DetailException("Ruta Client application has been already started.");
	}

	/**
	 * Gets the {@link Version} object describing the version of the {@code Ruta Client application}.
	 * @return {@code Version} object of the {@code Ruta Client application}
	 */
	public static RutaVersion getVersion() { return version;}

	/**
	 * Sets the {@link Version} object describing the version of the {@code Ruta Client application}.
	 * @param version {@code Version} object of the {@code Ruta Client application}
	 */
	public static void setVersion(RutaVersion version) { RutaClient.version = version; }

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

	/**
	 * Inserts My Party data to the local data store.
	 * @throws Exception if data could not be inserted in the data store
	 */
	//Moved to MyParty
/*	public void insertMyParty() throws Exception
	{
		myParty.setSearchNumber(Search.getSearchNum());
		myPartyDataMapper.insert(myParty.getLocalUsername(), myParty);
		mapperRegistry.getMapper(ItemType.class).insertAll(null, myParty.getProducts());
	}*/

	/**Sends request for registration of My party with the Central Data Repository.
	 * @param party Party object that should be registered
	 * @param username username of the party
	 * @param password password of the party
	 */
/*	@Deprecated
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
	}*/

	/**
	 * Sends request for registration of My party with the Central Data Repository. Username and password
	 * that are used are those of the local data store registration.
	 */
	public void cdrRegisterMyParty()
	{
		cdrRegisterMyParty(myParty.getLocalUsername(), myParty.getLocalPassword());
	}

	/**
	 * Sends request for registration of My Party with the Central Data Repository.
	 * @param username username of the party
	 * @param password password of the party
	 */
	public void cdrRegisterMyParty(String username, String password)
	{
		try
		{
			Server port = getCDRPort();

			//validating UBL conformance
			final String missingPartyField = myParty.getCoreParty().verifyParty();
			if(missingPartyField != null)
			{
				frame.appendToConsole("Request for the registration of My Party has not been sent to the CDR service because"
						+ " Party is missing mandatory field: " + missingPartyField +
						". Please populate My Party data with all mandatory fields and try again.", Color.RED);
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
							myParty.setCDRUsername(username);
							myParty.setCDRPassword(password);
							myParty.setCDRSecretKey(key);
							myParty.setDirtyMyParty(false);
							myParty.followMyself();
							frame.appendToConsole("My Party has been successfully registered with the CDR service.", Color.GREEN);
							frame.appendToConsole("My Party has been added to the Following parties.", Color.BLACK);
							frame.appendToConsole("Please update My Catalogue on the CDR service for everyone to be able to see your products.",
									Color.GREEN);
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
	//MMM: with the new registration process there is no difference with the insert and update of the Party on the client side - theay are the same!!!
	public void cdrSynchroniseMyParty()
	{
		cdrUpdateMyParty();
		frame.repaintTabbedPane(null, false, false);
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
			port.insertPartyAsync(myParty.getCDRUsername(), myParty.getCoreParty(), futureParty ->
			{
				try
				{
					futureParty.get();
					myParty.setDirtyMyParty(false);
					frame.appendToConsole("My Party has been successfully updated with the CDR service.", Color.GREEN);
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
			frame.appendToConsole("My Party has been sent to the CDR service. Waiting for a response...", Color.BLACK);
		}
		catch(WebServiceException e)
		{
			frame.appendToConsole("My Party has not been updated with the CDR service!"
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
			port.updatePartyAsync(myParty.getCDRUsername(), myParty.getCoreParty(), future ->
			{
				try
				{
					future.get();
					myParty.setDirtyMyParty(false);
					frame.appendToConsole("My Party has been successfully updated with the CDR service.", Color.GREEN);
				}
				catch(Exception e)
				{
					processException(e, "My Party has not been updated with the CDR service!");
				}
				finally
				{
					frame.enablePartyMenuItems();
				}
			});
			frame.appendToConsole("My Party has been sent to the CDR service. Waiting for a response...", Color.BLACK);
		}
		catch(WebServiceException e)
		{
			frame.appendToConsole("My Party has not been updated with the CDR service!"
					+ " Server is not accessible. Please try again later.", Color.RED);
			frame.enablePartyMenuItems();
		}
	}

	/**
	 * Deregister My Party from the CDR service. All documents corresponding to the My Party deposited in the CDR database
	 * are retrieved from the CDR service prior the deregistration.
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
			port.deregisterUserAsync(myParty.getCDRUsername(), notice, future ->
			{
				try
				{
					future.get();
					myParty.clearCDRRelatedData();
					frame.appendToConsole("My Party has been successfully deregistered from the CDR service.", Color.GREEN);
					frame.appendToConsole("All CDR related data in regard with parties has been deleted from the local data store.",
							Color.GREEN);
					frame.repaintTabbedPane(null, false, false); //MMM: shoould be called method for repainting whole frame - to be implemented
				}
				catch (InterruptedException | ExecutionException e)
				{
					processException(e, "My Party has not been deregistered from the CDR service! ");
				}
				catch(Exception e)
				{
					processException(e, "My Party has been deregistered from the CDR service, but some local data is not deleted! ");
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

	/**
	 * Synchronise Catalogue with the CDR service. If catalogue is empty calls deleteCatalogue method.
	 * Method sends the catalogue if it is nonempty and has been changed since the last synchronisation
	 * with the CDR service.
	 */
	//MMM: insert and update of My Catalogue is now effectively the same, accept the insertMyCatalogue boolean variable which is not used anymore - should be deleted - check this
	public void cdrSynchroniseMyCatalogue()
	{
		if(myParty.isInsertMyCatalogue() == true) //first time sending catalogue
			cdrInsertMyCatalogue();
		else
			if(myParty.getProductCount() == 0) // delete My Catalogue from CDR
				cdrDeleteMyCatalogue();
			else
				cdrUpdateMyCatalogue();
	}

	/**
	 * Inserts My Catalogue for the first time in the CDR service. Updates are done with the
	 * <code>cdrUpdateMyCatalogue</code> method.
	 * @see RutaClient#cdrUpdateMyCatalogue <code>cdrUpdateMyCatalogue</code>
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
					logger.error(errors.toString());
				}
				else
				{
					myParty.setCatalogue(catalogue);
					Server port = getCDRPort();
					String username = myParty.getCDRUsername();
					port.insertCatalogueAsync(username, catalogue, future ->
					{
						try
						{
							future.get();
							frame.appendToConsole("My Catalogue has been successfully deposited to the CDR service.", Color.GREEN);
							myParty.setDirtyCatalogue(false);
							myParty.setInsertMyCatalogue(false);
							frame.repaintTabbedPane(null, false, false); //MMM: shoould be called method for repainting whole frame - to be implemented
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
					logger.error(errors.toString());
				}
				else
				{
					myParty.setCatalogue(catalogue);
					Server port = getCDRPort();
					String username = myParty.getCDRUsername();
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
						myFollowingParty.setCatalogue(catalogue);
					}
					else
					{
						StringBuilder consoleMsg = new StringBuilder("Catalogue does not exist.");
						if(myFollowingParty.getCatalogue().getCatalogueLineCount() != 0)
						{
							myFollowingParty.getCatalogue().setCatalogueLine(null);
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
					frame.repaintTabbedPane(null, false, false);
				}
			});
			frame.appendToConsole("Request for catalogue has been sent to the CDR service. Waiting for a response...", Color.BLACK);
		}
		catch(WebServiceException e)
		{
			frame.appendToConsole("My Catlogue has not been retrieved from the CDR service!"
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
			String username = myParty.getCDRUsername();
			port.deleteCatalogueAsync(username, catalogueDeletion, future ->
			{
				try
				{
					future.get();
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

					if(party != null)
					{
//						BusinessParty newFollowing = myParty.addFollowingParty(party, partner);

						BusinessParty newFollowing = new BusinessParty();
						newFollowing.setCoreParty(party);
						newFollowing.setPartner(partner);
						myParty.followParty(newFollowing);

						StringBuilder msg = new StringBuilder("Party " + followingName + " has been successfully added to the following parties");
						if(partner)
							msg.append(" as a business partner.");
						else
							msg.append(".");
						frame.appendToConsole(msg.toString(), Color.GREEN);
						frame.repaintTabbedPane(newFollowing, true, false);
					}
					else
						frame.appendToConsole("Party " + followingName + " could not be added to the following parties!", Color.RED);
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

	/**
	 * Sends unfollow request to the CDR service.
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
//					frame.repaintTabbedPane(followingParty, false, true);
					myParty.unfollowParty(followingParty);
					StringBuilder msg = new StringBuilder("Party " + followingName + " has been successfully removed from the following parties.");
					frame.appendToConsole(msg.toString(), Color.GREEN);
					frame.repaintTabbedPane(followingParty, true, false);
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
	 * @return {@link Semaphore} object that enables this kind of CDR service calls to be sequentally
	 * ordered, so they can be executed one by one. After the end of the method call, number of permits in
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
						frame.appendToConsole("Started download of " + plural + ". Waiting...", Color.BLACK);
						AtomicInteger downloadCount = new AtomicInteger(0);
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
									if(document != null)
									{
										downloadCount.incrementAndGet();
										placeDocBoxDocument(document, docID);
										frame.repaintTabbedPane(null, false, false);
										port.deleteDocBoxDocumentAsync(myParty.getCDRUsername(), docID, deleteFuture -> {});
									}
									else
										frame.appendToConsole("Document " + docID + " could not be downloaded!", Color.RED);
								}
								catch (InterruptedException | ExecutionException e)
								{
									oneAtATime.release();
									processException(e, "Document " + docID + " could not be downloaded!");
								}
								catch (Exception e)
								{
									oneAtATime.release();
									processException(e, "Document " + docID + " could not be placed where it belogs in the data model!");
								}
								finally
								{
									finished.countDown();
								}
							});
						}
						finished.await();
						String failed = null;
						int downCount = downloadCount.get();
						if(downCount != docCount)
						{
							if(downCount != 1)
								plural = downCount + " documents";
							else
								plural = "1 document";

							if(docCount - downCount != 1)
								failed = "Failed download of " + (docCount - downCount) + " documents.";
							else
								failed = "Failed download of 1 document.";
						}
						if(failed != null)
							frame.appendToConsole("Successful download of " + plural + ". " + failed, Color.BLACK);
						else
							frame.appendToConsole("Successful download of " + plural + ".", Color.BLACK);
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
		RutaClient.cdrEndPoint = endPoint;
	}

	/**Reverts to default value of the service end point location.
	 */
	public static void resetCDREndPoint()
	{
		RutaClient.cdrEndPoint = defaultEndPoint;
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
		return myPartyDataMapper;
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

	public void setFrame(RutaClientFrame clientFrame)
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
					futureResult.get();
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
	public void cdrSearch(String searchName, SearchCriterion criterion)
	{
		try
		{
			Server port = getCDRPort();
			if(((CatalogueSearchCriterion) criterion).isCatalogueSearchedFor()) //querying parties and catalogues
			{
				port.searchCatalogueAsync((CatalogueSearchCriterion) criterion, futureResult ->
				{
					try
					{
						SearchCatalogueResponse res = futureResult.get();
						//handle the Catalogue list
						List<CatalogueType> results = res.getReturn();
						/*						for(CatalogueType cat : results)
							frame.appendToConsole("Party: " + cat.getProviderParty().getPartyName().get(0).getNameValue() +
									" items: " + cat.getCatalogueLineCount(), Color.GREEN);*/
						final Search<CatalogueType> search = new Search<CatalogueType>(searchName, criterion, results, CatalogueType.class);
						if(search.getResultCount() != 0)
						{
							frame.appendToConsole("Search results for search request \"" + searchName + "\" have been successfully retrieved from the CDR service.", Color.GREEN);
							myParty.listCatalogueSearch(search);
							frame.repaintTabbedPane(null, false, false);
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

				port.searchPartyAsync((PartySearchCriterion) criterion, futureResult ->
				{
					try
					{
						SearchPartyResponse res = futureResult.get();

						List<PartyType> results = res.getReturn();
						final Search<PartyType> search = new Search<PartyType>(searchName, criterion , results, PartyType.class);
						if(search.getResultCount() != 0)
						{
							frame.appendToConsole("Search results for search request \"" + searchName + "\" have been successfully retrieved from the CDR service.", Color.GREEN);
							myParty.getPartySearches().add(0, search);
							frame.repaintTabbedPane(null, false, false);
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
			SearchCriterion criterion = search.getCriterion();
			if(((CatalogueSearchCriterion) criterion).isCatalogueSearchedFor()) //querying parties and catalogues
			{
				port.searchCatalogueAsync((CatalogueSearchCriterion) criterion, futureResult ->
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
							frame.repaintTabbedPane(null, false, false);
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
				port.searchPartyAsync((PartySearchCriterion) criterion, futureResult ->
				{
					try
					{
						SearchPartyResponse res = futureResult.get();

						List<PartyType> results = res.getReturn();
						if(results.size() != 0)
						{
							((Search<PartyType>)search).setResults(results);
							frame.appendToConsole("Search results have been successfully retrieved from the CDR service.", Color.GREEN);
							frame.repaintTabbedPane(null, false, false);
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

	/**
	 * Sends search request for parties and catalogues to the CDR service.
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
			SearchCriterion criterion = search.getCriterion();
			if(criterion instanceof CatalogueSearchCriterion) //querying parties and catalogues
			{
				port.searchCatalogueAsync((CatalogueSearchCriterion) criterion, futureResult ->
				{
					try
					{
						SearchCatalogueResponse res = futureResult.get();
						List<CatalogueType> results = res.getReturn();
						final Search<CatalogueType> newSearch = (Search<CatalogueType>) search;
						newSearch.setResults(results);

						if(results == null || results.isEmpty())
						{
							frame.appendToConsole("Nothing found at CDR service that conforms to your search request \"" +
									newSearch.getSearchName() + "\".", Color.GREEN);
							int option = JOptionPane.showConfirmDialog(frame, "Nothing found at CDR service that conforms to your search request\n\"" +
									newSearch.getSearchName() + "\". Do you want to save this search with no results?", "No search results",
									JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
							if(option == JOptionPane.YES_OPTION)
							{
								//data model should be up to a date before gui repaintTabbedPane method is invoked
								if(exist)
									myParty.relistCatalogueSearch(newSearch);
								else
									myParty.listCatalogueSearch(newSearch);

								if(exist)
									frame.repaintTabbedPane(RutaClientFrame.CDR_DATA_TAB, newSearch, true, true);
								else
									frame.repaintTabbedPane(RutaClientFrame.CDR_DATA_TAB, newSearch, true, false);
							}
							else
								if(exist)
								{
									myParty.delistCatalogueSearch(newSearch);
									frame.repaintTabbedPane(RutaClientFrame.CDR_DATA_TAB, newSearch, false, true);
									frame.appendToConsole("Search \"" + newSearch.getSearchName() + "\" has been deleted.", Color.GREEN);
								}
						}
						else
						{
							//data model should be up to a date before gui repaintTabbedPane method is invoked
							if(exist)
								myParty.relistCatalogueSearch(newSearch);
							else
								myParty.listCatalogueSearch(newSearch);

							frame.appendToConsole("Search results for search request \"" + newSearch.getSearchName() +
									"\" have been successfully retrieved from the CDR service.", Color.GREEN);
							if(exist)
								frame.repaintTabbedPane(RutaClientFrame.CDR_DATA_TAB, newSearch, true, true);
							else
								frame.repaintTabbedPane(RutaClientFrame.CDR_DATA_TAB, newSearch, true, false);
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
				port.searchPartyAsync((PartySearchCriterion) criterion, futureResult ->
				{
					try
					{
						SearchPartyResponse res = futureResult.get();
						List<PartyType> results = res.getReturn();
//						final Search<PartyType> newSearch = (Search<PartyType>) search;
						final PartySearch newSearch = (PartySearch) search;
						newSearch.setResults(results);

						if(results == null || results.isEmpty())
						{
							frame.appendToConsole("Nothing found at CDR service that conforms to your search request \"" +
									newSearch.getSearchName() + "\".", Color.GREEN);
							int option = JOptionPane.showConfirmDialog(frame, "Nothing found at CDR service that conforms to your search request\n\"" +
									newSearch.getSearchName() + "\". Do you want to save this search with no results?", "No search results",
									JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
							if(option == JOptionPane.YES_OPTION)
							{
								//data model should be up to a date before gui repaintTabbedPane method is invoked
								if(exist)
									myParty.relistPartySearch(newSearch);
								else
									myParty.listPartySearch(newSearch);

								if(exist)
									frame.repaintTabbedPane(RutaClientFrame.CDR_DATA_TAB, newSearch, true, true);
								else
									frame.repaintTabbedPane(RutaClientFrame.CDR_DATA_TAB, newSearch, true, false);
							}
							else
								if(exist)
								{
									myParty.delistPartySearch(newSearch);
									frame.repaintTabbedPane(RutaClientFrame.CDR_DATA_TAB, newSearch, false, true);
									frame.appendToConsole("Search \"" + newSearch.getSearchName() + "\" has been deleted.", Color.GREEN);
								}
						}
						else
						{
							//data model should be up to a date before gui repaintTabbedPane method is invoked
							if(exist)
								myParty.relistPartySearch(newSearch);
							else
								myParty.listPartySearch(newSearch);

							frame.appendToConsole("Search results for search request \"" + newSearch.getSearchName() +
									"\" have been successfully retrieved from the CDR service.", Color.GREEN);
							if(exist)
								frame.repaintTabbedPane(RutaClientFrame.CDR_DATA_TAB, newSearch, true, true);
							else
								frame.repaintTabbedPane(RutaClientFrame.CDR_DATA_TAB, newSearch, true, false);
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
			bug.setReportedBy(myParty.getCDRUsername());
			Server port = getCDRPort();
			port.insertBugReportAsync(bug, futureResult ->
			{
				try
				{
					futureResult.get();
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
			future = port.searchBugReportAsync(criterion, futureResult -> { }); //MMM: replace the futureResult with null
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
			future = port.addBugReportCommentAsync(id, comment, futureResult -> { });
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
					futureResult.get();
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
					futureResult.get();
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
				futureResult.get();
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

	/**
	 * Safely shuts down the data store.
	 * @throws Exception if data store could not be disconnected from
	 */
	public void shutdownDataStore() throws Exception
	{
		MapperRegistry.getConnector().shutdownDatabase();
	}

	/**
	 * Gracefully shuts down {@code Ruta Client application}.
	 */
	public void shutdownApplication()
	{
//		boolean exit = true; //  = false when window should not be closed after exception is thrown
//		boolean exception = false;
		saveProperties();
		frame.saveProperties();
		storeProperties(true);
		MyParty myParty= getMyParty();
		try
		{
			if(myParty.getLocalUser() != null && myParty.getLocalUsername() != null)
				myParty.storeData();
		}
		catch(Exception e)
		{
			logger.error(e.getMessage());
//			exception = true;
/*			EventQueue.invokeLater( () ->
			{
				String[] options = {"YES", "NO"};
				int choice = JOptionPane.showOptionDialog(frame, "Data could not be saved to the local data store! "
						+ "Do yo want to close the program anyway?", "Fatal error", JOptionPane.YES_NO_OPTION,
						JOptionPane.ERROR_MESSAGE, null, options, options[0]);
				if(choice == 1)
					exit.set(false);
			});*/
		}

		try
		{
			shutdownDataStore();
		}
		catch(Exception e)
		{
			final String errMsg = e.getMessage();
			if(errMsg == null)
				logger.error(e.getCause().getMessage());
			else
				logger.error(errMsg);
		}


/*		if(exception)
		{

			String[] options = {"YES", "NO"};
			int choice = JOptionPane.showOptionDialog(frame, "Data could not be saved to the local data store! "
					+ "Do yo want to close the program anyway?", "Fatal error", JOptionPane.YES_NO_OPTION,
					JOptionPane.ERROR_MESSAGE, null, options, options[0]);
			if(choice == 1)
				exit.set(false);
		}


//		EventQueue.invokeLater( () ->
		{
			{
				try
				{
					shutdownDataStore();
				}
				catch(Exception e)
				{
					final String errMsg = e.getMessage();
					if(errMsg == null)
						logger.error(e.getCause().getMessage());
					else
						logger.error(errMsg);
					String[] options = {"YES", "NO"};
					int choice = JOptionPane.showOptionDialog(frame, "Data store could not be disconnected from! "
							+ "Do yo want to close the program anyway?", "Fatal error", JOptionPane.YES_NO_OPTION,
							JOptionPane.ERROR_MESSAGE, null, options, options[0]);
					if(choice == 1)
						System.exit(0);
				}
			}
		}//);
*/	}

	/**
	 * Registering My Party with the local datastore.
	 * @param username user's username
	 * @param password user's password
	 * @return user's secret key
	 */
	public String localRegisterMyParty(String username, String password)
	{
		String secretKey = null;
		try
		{
			//validating UBL conformance
			final String missingPartyField = myParty.getCoreParty().verifyParty();
			if(missingPartyField != null)
			{
				frame.appendToConsole("Request for the registration of My Party has been canceled because"
						+ " My Party is missing mandatory field: " + missingPartyField +
						". Please populate My Party data with all mandatory fields and try again.", Color.RED);
				frame.enablePartyMenuItems();
			}
			else
			{
				frame.appendToConsole("Request for the registration of My Party with the local data store has been sent.", Color.BLACK);
				myParty.setPartyID();
				secretKey = (String) mapperRegistry.getMapper(RutaUser.class).registerUser(username, password, myParty.getCoreParty());
				myParty.setLocalUser(new RutaUser(username, password, secretKey));
				/*myParty.setLocalUsername(username);
			myParty.setLocalPassword(password);
			myParty.setLocalSecretKey(secretKey);*/
				frame.appendToConsole("My Party has been successfully registered with the local data store.", Color.GREEN);
			}
		}
		catch (Exception e)
		{
			frame.enablePartyMenuItems();
			processException(e, "My Party has not been registered with the local data store! Please try again.");
		}
		return secretKey;
	}

	/**
	 * Deletes all data from the local data store.
	 * @throws DetailException if data could not be deleted
	 */
	private void localDeleteData() throws DetailException
	{
		myParty.deleteData();
	}

	/**
	 * Deregister My Party from the local datastore. All documents corresponding to the My Party deposited
	 * in the local datastore are deleted.
	 */
	public void localDeregisterMyParty()
	{
		try
		{
			localDeleteData();
			mapperRegistry.getMapper(RutaUser.class).deleteUser(myParty.getLocalUsername());
			properties.remove("username");
			storeProperties(false);
			frame.appendToConsole("Request for deregistration of My Party has been sent to the local datastore. Waiting for a response...", Color.BLACK);
			myParty.setLocalUser(null); //MMM check this!!!
			frame.updateTitle("");
			frame.appendToConsole("My Party has been successfully deregistered from the local datastore.", Color.GREEN);
			frame.repaintTabbedPane(null, false, false); //MMM: shoould be called method for repainting whole frame - to be implemented
		}
		catch(Exception e)
		{
			processException(e, "My Party has not been deregistered from the local datastore! ");
		}
		finally
		{
			frame.enablePartyMenuItems();
		}
	}
}