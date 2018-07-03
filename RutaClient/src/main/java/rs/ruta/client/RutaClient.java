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
import java.io.StringReader;
import java.math.BigDecimal;
import java.net.SocketTimeoutException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.annotation.Nonnull;
import javax.swing.JFrame;
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
import com.sun.xml.ws.fault.ServerSOAPFaultException;

import oasis.names.specification.ubl.schema.xsd.applicationresponse_21.ApplicationResponseType;
import oasis.names.specification.ubl.schema.xsd.catalogue_21.CatalogueType;
import oasis.names.specification.ubl.schema.xsd.cataloguedeletion_21.CatalogueDeletionType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.CustomerPartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.InvoiceLineType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.ItemType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.MonetaryTotalType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyIdentificationType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyNameType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.SupplierPartyType;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;
import oasis.names.specification.ubl.schema.xsd.order_21.OrderType;
import oasis.names.specification.ubl.schema.xsd.ordercancellation_21.OrderCancellationType;
import oasis.names.specification.ubl.schema.xsd.orderchange_21.OrderChangeType;
import oasis.names.specification.ubl.schema.xsd.orderresponse_21.OrderResponseType;
import oasis.names.specification.ubl.schema.xsd.orderresponsesimple_21.OrderResponseSimpleType;
import rs.ruta.ClientHandlerResolver;
import rs.ruta.RutaNode;
import rs.ruta.client.correspondence.CatalogueCorrespondence;
import rs.ruta.client.correspondence.DecideOnActionState;
import rs.ruta.client.correspondence.ClosingState;
import rs.ruta.client.correspondence.Correspondence;
import rs.ruta.client.correspondence.RutaProcessState;
import rs.ruta.client.correspondence.StateActivityException;
import rs.ruta.client.datamapper.ClientMapperRegistry;
import rs.ruta.client.datamapper.ClientMapperRegistryFactory;
import rs.ruta.client.gui.BusinessPartyEvent;
import rs.ruta.client.gui.CorrespondenceEvent;
import rs.ruta.client.gui.ItemEvent;
import rs.ruta.client.gui.PartnershipEvent;
import rs.ruta.client.gui.RutaClientFrame;
import rs.ruta.client.gui.RutaClientFrameEvent;
import rs.ruta.client.gui.SearchEvent;
import rs.ruta.common.ReportAttachment;
import rs.ruta.common.ReportComment;
import rs.ruta.common.RutaUser;
import rs.ruta.common.BugReport;
import rs.ruta.common.BugReportSearchCriterion;
import rs.ruta.common.PartnershipRequest;
import rs.ruta.common.PartnershipResolution;
import rs.ruta.common.PartnershipResponse;
import rs.ruta.common.RutaVersion;
import rs.ruta.common.SearchCriterion;
import rs.ruta.common.CatalogueSearchCriterion;
import rs.ruta.common.DeregistrationNotice;
import rs.ruta.common.DocBoxAllIDsSearchCriterion;
import rs.ruta.common.DocBoxDocumentSearchCriterion;
import rs.ruta.common.DocumentReceipt;
import rs.ruta.common.DocumentReference;
import rs.ruta.common.datamapper.DataMapper;
import rs.ruta.common.datamapper.DatabaseException;
import rs.ruta.common.datamapper.DetailException;
import rs.ruta.common.datamapper.MapperRegistry;
import rs.ruta.services.RutaException;
import rs.ruta.services.CDRService;
import rs.ruta.services.ClearCacheResponse;
import rs.ruta.services.DeleteCatalogueWithAppResponseResponse;
import rs.ruta.services.FindAllDocBoxDocumentIDsResponse;
import rs.ruta.services.FindCatalogueResponse;
import rs.ruta.services.FindDocBoxDocumentResponse;
import rs.ruta.services.FollowPartyResponse;
import rs.ruta.services.RegisterUserResponse;
import rs.ruta.services.RequestBusinessPartnershipResponse;
import rs.ruta.services.SearchCatalogueResponse;
import rs.ruta.services.SearchPartyResponse;
import rs.ruta.services.Server;
import rs.ruta.services.UpdateCatalogueWithAppResponseResponse;
import rs.ruta.common.InstanceFactory;
import rs.ruta.common.PartnershipBreakup;
import rs.ruta.common.PartySearchCriterion;

public class RutaClient implements RutaNode
{
	private static String packageList = "oasis.names.specification.ubl.schema.xsd.catalogue_21"; // colon separated package list
	final private static String defaultEndPoint = "http://ruta.sytes.net:9009/ruta-server-0.2.0-SNAPSHOT/CDR";
	private static String cdrEndPoint = defaultEndPoint;
	final private static String eclipseMonitorEndPoint = "http://localhost:7709/ruta-server-0.2.0-SNAPSHOT/CDR";
	private static int CONNECT_TIMEOUT = 0;
	private static int REQUEST_TIMEOUT = 0;
	private MyParty myParty;
	//	private MyPartyXMLFileMapper<MyParty> myPartyDataMapper; //former store to myparty.xml
	//	private DataMapper<MyParty, String> myPartyDataMapper;
	private Party CDRParty;
	private RutaClientFrame clientFrame;
	private static RutaVersion version = new RutaVersion("Client", "0.2.0-SNAPSHOT", "0.1.0", null);
	private Properties properties;
	//	private MapperRegistry mapperRegistry; //MMM: would be used instead of temporary ClientMapperRegistry and ExistConnector (see: constructor)
	private List<BugReport> bugReports;
	public static Logger logger = LoggerFactory.getLogger("rs.ruta.client");
	private String initialUsername;
	private String initialPassword;
	/**
	 * Exception to be wrapped in StateActivityException and throw to the state machine.
	 */
	private Exception exceptionRethrown = null;
	private boolean enableStoringProperties;

	//MMM maybe JAXBContext should be private class field, if it is used from multiple class methods

	/**
	 * Constructs a {@code RutaClient} object.
	 * @param clientFrame main frame of the application
	 * @param force if true it tells the constructor to try to create an object despite the fact that
	 * one instance of it had already been created and invoked by {@code Ruta Client application} from the same OS directory
	 * @throws DetailException if object could not be created or, when {@code force} is true and one instance of it already exists
	 */
	public RutaClient(RutaClientFrame clientFrame, boolean force) throws DetailException
	{
		this.clientFrame = clientFrame;
		logger.info("Opening Ruta Client Application");
		enableStoringProperties = true;
		properties = new Properties();
		loadProperties();
		try
		{
			checkClientInstantiated();
		}
		catch(DetailException e)
		{
			if(!force)
				throw e;
		}
		MapperRegistry.initialize(new ClientMapperRegistryFactory());
		MapperRegistry.getConnector().connectToDatabase();
		initialUsername = initialPassword = null;
		checkInstallation();
		myParty = new MyParty();
		myParty.setClient(this);
		CDRParty = getCDRParty();
		addShutDownHook();
	}

	/**
	 * Initializes fields of {@code RutaClient} object by retrieving data from local data store.
	 * This phase of data model initialization is before the view is initialized, so that the view could
	 * be populated with data if they exist in the data store.
	 * <p>{@link #clientFrame} field is not initialized in this method.</p>
	 * @throws Exception if retrieving data from the data store has been unsuccessful
	 */
	@Deprecated
	public void preInitialize() //throws Exception
	{
		// trying to load party data from database
		try
		{
			if(isLocalUserRegistеred())
			{
				final MyParty retrievedParty = MapperRegistry.getInstance().getMapper(MyParty.class).findByUsername(initialUsername);
				if(retrievedParty != null)
				{
					myParty = retrievedParty;
					myParty.setClient(this);
					Search.setSearchNumber(myParty.getSearchNumber());

					final Party coreParty = myParty.getCoreParty();
					if(!myParty.hasCoreParty())
						myParty.setCoreParty(coreParty);
					myParty.loadData();
				}
			}
		}
		catch(DetailException e)
		{ } //it's OK if user is not registered //MMM maybe it should some error message to be displayed???
		//MMM but it is not OK if it cannot load the data -> display some error message

	}

	/**
	 * Initializes RutaClient data and saves properties.
	 * @throws DetailException if there is some error in communication with the databas
	 */
	public void initialize() throws DetailException
	{
		properties.setProperty("started", "true");
		storeProperties(false);
		initializeMyParty();
	}

	/**
	 * Initializes MyParty object populating its structures and field from the database.
	 * @throws DetailException if there is some error in communication with the database
	 */
	private void initializeMyParty() throws DetailException
	{
		final MyParty retrievedParty = MapperRegistry.getInstance().getMapper(MyParty.class).findByUsername(initialUsername);
		if(retrievedParty != null)
		{
			myParty = retrievedParty;
			myParty.setClient(this);
			Search.setSearchNumber(myParty.getSearchNumber());

			final Party coreParty = myParty.getCoreParty();
			if(!myParty.hasCoreParty())
				myParty.setCoreParty(coreParty);
			myParty.loadData();
		}
		if(myParty.getCatalogueCorrespondence() == null)
			myParty.addCatalogueCorrespondence(CatalogueCorrespondence.newInstance(this));
	}

	/**
	 * Authorizes access to the database by trying saved credentials and/or requesting new ones.
	 * @return true if authorization was successful; false otherwise
	 * @throws DetailException
	 */
	public boolean authorizeUserAccess() throws DetailException
	{
		boolean success = true;
		final Semaphore edtSync = new Semaphore(0);
		if(!isAnyLocalUserRegistеred())
		{
			EventQueue.invokeLater(() ->
			{
				JOptionPane.showMessageDialog(clientFrame,
						"Welcome to Ruta Client Application. To start using Ruta System\n" +
								"you have to register yourself and your Party with it.",
								"Welcome message", JOptionPane.PLAIN_MESSAGE);
				edtSync.release();
			});
			try
			{
				edtSync.acquire();
			}
			catch (InterruptedException e)
			{
				throw new DetailException("Unable to synchronize with EDT thread.", e);
			}

			final Party coreParty = myParty.getCoreParty();
			if(!myParty.hasCoreParty() || coreParty.verifyParty() != null)
				myParty.setCoreParty(clientFrame.showPartyDialog(coreParty, "My Party", true, true));
			clientFrame.updateTitle(myParty.getCoreParty().getPartySimpleName());
			initialUsername = clientFrame.showLocalSignUpDialog("Local database registration", false);
			EventQueue.invokeLater(() ->
			{
				if(initialUsername != null)
				{
					JOptionPane.showMessageDialog(null, "Party has been successfully registered with local database.\n" +
							"Next step is a registration with the Central Data Repository.", "Local database registration",
							JOptionPane.PLAIN_MESSAGE);
					edtSync.release();
				}
			});
			try
			{
				edtSync.acquire();
				clientFrame.showCDRSignUpDialog("CDR registration");
			}
			catch (InterruptedException e)
			{
				throw new DetailException("Unable to synchronize with EDT thread.", e);
			}

		}
		else if(!isLocalUserRegistеred() && !myParty.checkLocalUser(initialUsername, initialPassword))
		{
			final AtomicBoolean loop = new AtomicBoolean(true);
			while(loop.get() && !clientFrame.showLocalLogInDialog("Log in"))
			{
				EventQueue.invokeLater(() ->
				{
					int option = JOptionPane.showConfirmDialog(clientFrame, "Log-in credentials are wrong. Do you want to try again?",
							"Log-in Error", JOptionPane.YES_NO_OPTION);
					loop.set(option == JOptionPane.YES_OPTION);
					edtSync.release();
				});
				try
				{
					edtSync.acquire();
				}
				catch (InterruptedException e)
				{
					throw new DetailException("Unable to synchronize with EDT thread.", e);
				}
			}
			if(!loop.get())
			{
				success = false;
			}
		}
		return success;
	}

	//MMM only for test
	public static void main(String... args)
	{
		try
		{
			final JAXBContext jaxbContext = JAXBContext.newInstance("rs.ruta.client.correspondence");
			final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			String xml = "<ns0:CatalogueCorrespondence xmlns:ns6=\"urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2\" xmlns:ns7=\"http://www.ruta.rs/ns/common\" xmlns:ns0=\"http://www.ruta.rs/ns/client\" xmlns:ns1=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">\r\n" +
					"    <ns0:CreateCatalogueProcess>\r\n" +
					"        <ns0:DistributeCatalogueState/>\r\n" +
					"        <ns0:UUID>4dc1adbc-834c-410e-b818-7dd23566468e</ns0:UUID>\r\n" +
					"        <ns0:Active>true</ns0:Active>\r\n" +
					"    </ns0:CreateCatalogueProcess>\r\n" +
					"    <ns0:UUID>fc7e4074-a65c-492b-a8a8-96b6a1086419</ns0:UUID>\r\n" +
					"    <ns0:Active>true</ns0:Active>\r\n" +
					"    <ns0:RecentlyUpdated>true</ns0:RecentlyUpdated>\r\n" +
					"    <ns0:CorrespondentParty>\r\n" +
					"        <ns6:PartyIdentification>\r\n" +
					"            <ns1:ID>CDR1234567890</ns1:ID>\r\n" +
					"        </ns6:PartyIdentification>\r\n" +
					"        <ns6:PartyName>\r\n" +
					"            <ns1:Name>CDR</ns1:Name>\r\n" +
					"        </ns6:PartyName>\r\n" +
					"    </ns0:CorrespondentParty>\r\n" +
					"    <ns0:CorrespondenceName>fc7e4074-a65c-492b-a8a8-96b6a1086419</ns0:CorrespondenceName>\r\n" +
					"    <ns0:CreationTime>2018-06-13T13:34:17.046+02:00</ns0:CreationTime>\r\n" +
					"    <ns0:LastActivityTime>2018-06-13T13:37:45.781+02:00</ns0:LastActivityTime>\r\n" +
					"    <ns0:CreateCatalogueProcess>true</ns0:CreateCatalogueProcess>\r\n" +
					"</ns0:CatalogueCorrespondence>";
			Object object = unmarshaller.unmarshal(new StringReader(xml));
			int i = 1;
		}
		catch (JAXBException e)
		{
			e.printStackTrace();
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
	 * The value to be set is actual username.
	 * @param initialUsername username to be set
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
					shutdownApplication();
					logger.info("Shutdown hook ended.");
					logger.info("Closing Ruta Client Application");
				}
				catch(Exception e)
				{
					logger.error("Exception is ", e);
				}
			}
		});
	}

	/**
	 * Checks whether xquery files are present in the database and stores them to the database if they are not.
	 */
	private void checkInstallation()
	{
		if(!"true".equals(properties.getProperty("installed")))
		{
			try
			{
				MapperRegistry.getInstance().getMapper(RutaUser.class).checkDatastoreSetup();
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
	 * Checks whether the user is registered with the local data store retrieving user crendentials
	 * from the properties object and checking them with the data store.
	 * @return true if user is registered
	 * @throws DatabaseException due to database connectivity issues
	 */
	public boolean isLocalUserRegistеred() throws DatabaseException
	{
		initialUsername = properties.getProperty("username");
		initialPassword = properties.getProperty("password");
		if(initialUsername == null || initialPassword == null)
			return false;
		return MapperRegistry.getConnector().checkUser(initialUsername, initialPassword);
	}

	/**
	 * Checks whether any user is registered with the local data store.
	 * @return true if at least one user is registered
	 * @throws DatabaseException due to database connectivity issues
	 */
	private boolean isAnyLocalUserRegistеred() throws DatabaseException
	{
		final List<String> usernames = MapperRegistry.getAccountUsernames();
		return usernames != null && !usernames.isEmpty();
	}

	public boolean isEnableStoringProperties()
	{
		return enableStoringProperties;
	}

	public void setEnableStoringProperties(boolean enableStoringProperties)
	{
		this.enableStoringProperties = enableStoringProperties;
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
			JOptionPane.showMessageDialog(null, "Properties could not be read from the ruta.properties file!\n" +
					"Reverting to default settings.",
					"Information", JOptionPane.INFORMATION_MESSAGE);
			logger.warn("Properties could not be read from the ruta.properties file! " + e.getMessage());
		}
		RutaClient.cdrEndPoint = properties.getProperty("cdrEndPoint", RutaClient.defaultEndPoint);
		RutaClient.CONNECT_TIMEOUT = Integer.valueOf(properties.getProperty("connectTimeout", "0"));
		RutaClient.REQUEST_TIMEOUT = Integer.valueOf(properties.getProperty("requestTimeout", "0"));
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
			JOptionPane.showMessageDialog(null, "Properties could not be stored to the ruta.properties file!",
					"Error", JOptionPane.ERROR_MESSAGE);
			logger.warn("Properties could not be stored to the ruta.properties file!");
		}
	}

	/**
	 * Saves properties from {@code RutaClient} class fields to the {@link Properties} object.
	 */
	private void saveProperties()
	{
		properties.put("cdrEndPoint", RutaClient.cdrEndPoint);
		properties.put("connectTimeout", String.valueOf(RutaClient.CONNECT_TIMEOUT));
		properties.put("requestTimeout", String.valueOf(RutaClient.REQUEST_TIMEOUT));
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

	public RutaClientFrame getClientFrame()
	{
		return clientFrame;
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

	/**
	 * Populates the list of {@link BugReport}s in a way that if some {@code BugReport} already exists in the list
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

	/**
	 * Sends request for registration of My party with the Central Data Repository.
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
					clientFrame.appendToConsole(new StringBuilder("Party has been successfully registered with the CDR service."
							+ " Please synchronise My Party with the CDR service to be able to use it."), Color.GREEN);
				}
				catch(Exception exception)
				{
					processException(exception, "My Party has not been registered with the CDR service!");
				}
				finally
				{
					clientFrame.enablePartyMenuItems();
				}
			});
			clientFrame.appendToConsole(new StringBuilder("Request for the registration of My Party has been sent to the CDR service. Waiting for a response...",
					Color.BLACK);
		}
		catch(WebServiceException exception)
		{
			logger.error("Exception is ", exception);
			clientFrame.appendToConsole(new StringBuilder("My Party has not been registered with the CDR service!"
					+ " Server is not accessible. Please try again later."), Color.RED);
			clientFrame.enablePartyMenuItems();
		}
	}*/

	/**
	 * Sends request for registration of My Party with the Central Data Repository.
	 * @param username username of the party
	 * @param password password of the party
	 * @return {@link Future} representing the CDR response, that enables calling method to wait for its completion
	 * or {@code null} if no CDR request has been made during method invocation
	 */
	public Future<?> cdrRegisterMyParty(String username, String password)
	{
		Future<?> ret = null;
		try
		{
			final Server port = getCDRPort();

			//validating UBL conformance
			final String missingPartyField = myParty.getCoreParty().verifyParty();
			if(missingPartyField != null)
			{
				clientFrame.appendToConsole(new StringBuilder("Request for the registration of My Party has not been sent to the CDR").
						append(" service because Party is missing mandatory field: " ).append(missingPartyField).
						append(". Please populate My Party data with all mandatory fields and try again."), Color.RED);
				clientFrame.enablePartyMenuItems();
			}
			else
			{
				myParty.setPartyID();
				PartyType coreParty = myParty.getCoreParty();

				ret = port.registerUserAsync(username, password, coreParty, futureUser ->
				{
					try
					{
						RegisterUserResponse response = futureUser.get();
						String key = response.getReturn();
						myParty.setCDRUsername(username);
						myParty.setCDRPassword(password);
						myParty.setCDRSecretKey(key);
						myParty.setDirtyMyParty(false);
						myParty.followMyself();
						clientFrame.appendToConsole(new StringBuilder("My Party has been successfully registered with the CDR service."),
								Color.GREEN);
						clientFrame.appendToConsole(new StringBuilder("My Party has been added to the Following parties."), Color.BLACK);
						clientFrame.appendToConsole(new StringBuilder("Please upload My Catalogue on the CDR service for Ruta users").
								append(" to be able to see your products."), Color.BLACK);
					}
					catch(Exception e)
					{
						myParty.clearPartyID();
						clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder("My Party has not been registered with the CDR service!"));
					}
					finally
					{
						clientFrame.enablePartyMenuItems();
					}
				});
				clientFrame.appendToConsole(new StringBuilder("Request for the registration of My Party has been sent to the CDR service.").
						append(" Waiting for a response..."), Color.BLACK);
			}
		}
		catch(WebServiceException e)
		{
			logger.error("My Party has not been registered with the CDR service!" +
					" Server is not accessible. Exception is ", e);
			clientFrame.appendToConsole(new StringBuilder("My Party has not been registered with the CDR service!").
					append(" Server is not accessible. Please try again later."), Color.RED);
			clientFrame.enablePartyMenuItems();
		}
		return ret;
	}

	/**
	 * Updates My Party data with the CDR service.
	 * @return {@link Future} representing the CDR response, that enables calling method to wait for its completion
	 * or {@code null} if no CDR request has been made during method invocation
	 */
	public Future<?> cdrUpdateMyParty()
	{
		Future<?> ret = null;
		try
		{
			Server port = getCDRPort();
			ret = port.updatePartyAsync(myParty.getCDRUsername(), myParty.getCoreParty(), future ->
			{
				try
				{
					future.get();
					myParty.setDirtyMyParty(false);
					clientFrame.appendToConsole(new StringBuilder("My Party has been successfully updated with the CDR service."), Color.GREEN);
				}
				catch(Exception e)
				{
					clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder("My Party has not been updated with the CDR service!"));
				}
				finally
				{
					clientFrame.enablePartyMenuItems();
				}
			});
			clientFrame.appendToConsole(new StringBuilder("My Party has been sent to the CDR service. Waiting for a response..."), Color.BLACK);
		}
		catch(WebServiceException e)
		{
			logger.error("My Party has not been updated with the CDR service!" +
					" Server is not accessible. Exception is ", e);
			clientFrame.appendToConsole(new StringBuilder("My Party has not been updated with the CDR service!").
					append(" Server is not accessible. Please try again later."), Color.RED);
			clientFrame.enablePartyMenuItems();
		}
		return ret;
	}

	/**
	 * Deregisteres My Party from the CDR service. All documents corresponding to the My Party
	 * deposited in the CDR database are retrieved from the CDR service prior the deregistration.
	 * @return {@link Future} representing the CDR response, that enables calling method to wait for its completion
	 * or {@code null} if no CDR request has been made during method invocation
	 */
	public Future<?> cdrDeregisterMyParty()
	{
		Future<?> ret = null;
		clientFrame.appendToConsole(new StringBuilder("Checking whether there are new documents in the DocBox."), Color.BLACK);
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
			ret = port.deregisterUserAsync(myParty.getCDRUsername(), notice, future ->
			{
				try
				{
					future.get();
					myParty.deleteCDRRelatedData();
					clientFrame.appendToConsole(new StringBuilder("My Party has been successfully deregistered from the CDR service."),
							Color.GREEN);
					clientFrame.appendToConsole(new StringBuilder("All CDR related data in regard with parties has been deleted from").
							append(" the local data store."), Color.BLACK);
				}
				catch (InterruptedException | ExecutionException e)
				{
					clientFrame.processExceptionAndAppendToConsole(
							e, new StringBuilder("My Party has not been deregistered from the CDR service! "));
				}
				catch(Exception e)
				{
					clientFrame.processExceptionAndAppendToConsole(
							e, new StringBuilder("My Party has been deregistered from the CDR service, but some local data is not deleted! "));
				}
				finally
				{
					clientFrame.enablePartyMenuItems();
				}
			});
			clientFrame.appendToConsole(new StringBuilder("Request for deregistration of My Party has been sent to the CDR service.").
					append(" Waiting for a response..."), Color.BLACK);
		}
		catch(WebServiceException e)
		{
			logger.error("My Party has not been deregistered from the CDR service!" +
					" Server is not accessible. Exception is ", e);
			clientFrame.appendToConsole(new StringBuilder("My Party has not been deregistered from the CDR service!").
					append(" Server is not accessible. Please try again later."), Color.RED);
			clientFrame.enablePartyMenuItems();
		}
		return ret;
	}

	/**
	 * Synchronise Catalogue with the CDR service. If catalogue is empty Catalogue Deletion Process is invoked.
	 * @throws InterruptedException if correspondence thread is interrupted while being blocked
	 */
	public void cdrSynchroniseMyCatalogue() throws InterruptedException
	{
		//MMM check whether is this first upload of My Catalogue: if(myParty.isInsertMyCatalogue() == true) - first time sending catalogue ???
		if(myParty.getProductCount() == 0) // delete My Catalogue from CDR
			myParty.executeDeleteCatalogueProcess();
		else
			myParty.executeCreateCatalogueProcess();
	}

	/**
	 * Inserts My Catalogue for the first time in the CDR service. Updates are done with the
	 * <code>cdrUpdateMyCatalogue</code> method.
	 * @see RutaClient#cdrUpdateMyCatalogue <code>cdrUpdateMyCatalogue</code>
	 * @return {@link Future} representing the CDR response, that enables calling method to wait for its completion
	 * or {@code null} if no CDR request has been made during method invocation
	 */
	@Deprecated
	private Future<?> cdrInsertMyCatalogue()
	{
		Future<?> ret = null;
		try
		{
			//creating Catalogue document
			CatalogueType catalogue = myParty.produceCatalogue(CDRParty);
			if(catalogue != null)
			{
				//validating UBL conformance
				IErrorList errors = UBL21Validator.catalogue().validate(catalogue);
				if(errors.containsAtLeastOneFailure())
				{
					clientFrame.appendToConsole(new StringBuilder("My Catalogue has not been sent to the CDR service because it is malformed.").
							append(" UBL validation has failed."), Color.RED);
					clientFrame.enableCatalogueMenuItems();
					logger.error(errors.toString());
				}
				else
				{
					myParty.setCatalogue(catalogue);
					Server port = getCDRPort();
					String username = myParty.getCDRUsername();
					ret = port.insertCatalogueAsync(username, catalogue, future ->
					{
						try
						{
							future.get();
							clientFrame.appendToConsole(new StringBuilder("My Catalogue has been successfully deposited into the CDR service."),
									Color.GREEN);
							myParty.setDirtyCatalogue(false);
						}
						catch(Exception e)
						{
							clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder("My Catalogue has not been deposited into the CDR service! "));
						}
						finally
						{
							clientFrame.enableCatalogueMenuItems();
						}
					});
					clientFrame.appendToConsole(new StringBuilder("My Catalogue has been sent to the CDR service. Waiting for a response..."),
							Color.BLACK);
				}
			}
			else
			{
				clientFrame.appendToConsole(new StringBuilder("My Catalogue has not been sent to the CDR service because it is malformed.").
						append(" All catalogue items should have a name and catalogue has to have at least one item."), Color.RED);
				clientFrame.enableCatalogueMenuItems();
			}
		}
		catch(WebServiceException e)
		{
			logger.error("My Catalogue has not been deposited into the CDR service!" +
					" Server is not accessible. Exception is ", e);
			clientFrame.appendToConsole(new StringBuilder("My Catalogue has not been deposited into the CDR service!").
					append(" Server is not accessible. Please try again later."), Color.RED);
			clientFrame.enableCatalogueMenuItems();
		}
		return ret;
	}

	/**
	 * Updates My Catalogue with the CDR service.
	 * @return {@link Future} representing the CDR response, that enables calling method to wait for its completion
	 * or {@code null} if no CDR request has been made during method invocation
	 */
	@Deprecated
	public Future<?> cdrUpdateMyCatalogue()
	{
		Future<?> ret = null;
		try
		{
			//creating Catalogue document
			CatalogueType catalogue = myParty.produceCatalogue(CDRParty);
			if(catalogue != null)
			{
				//validating UBL conformance
				IErrorList errors = UBL21Validator.catalogue().validate(catalogue);
				if(errors.containsAtLeastOneFailure())
				{
					clientFrame.appendToConsole(new StringBuilder("My Catalogue has not been sent to the CDR service because it is malformed.").
							append(" UBL validation has failed."), Color.RED);
					clientFrame.enableCatalogueMenuItems();
					logger.error(errors.toString());
				}
				else
				{
					myParty.setCatalogue(catalogue);
					Server port = getCDRPort();
					String username = myParty.getCDRUsername();
					ret = port.updateCatalogueAsync(username, catalogue, future ->
					{
						try
						{
							future.get();
							myParty.setDirtyCatalogue(false);
							clientFrame.appendToConsole(new StringBuilder("My Catalogue has been successfully updated by the CDR service."), Color.GREEN);
						}
						catch(Exception e)
						{
							clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder("My Catalogue has not been updated by the CDR service! "));
						}
						finally
						{
							clientFrame.enableCatalogueMenuItems();
						}
					});

					clientFrame.appendToConsole(new StringBuilder("My Catalogue has been sent to the CDR service. Waiting for a response..."),
							Color.BLACK);
				}
			}
			else
			{
				clientFrame.appendToConsole(new StringBuilder("My Catalogue has not been sent to the CDR service because it is malformed. ").
						append("All catalogue items should have a name and catalogue has to have at least one item."), Color.RED);
				clientFrame.enableCatalogueMenuItems();
			}
		}
		catch(WebServiceException e)
		{
			logger.error("My Catalogue has not been updated by the CDR service!" +
					" Server is not accessible. Exception is ", e);
			clientFrame.appendToConsole(new StringBuilder("My Catalogue has not been updated by the CDR service!").
					append(" Server is not accessible. Please try again later."), Color.RED);
			clientFrame.enableCatalogueMenuItems();
		}
		return ret;
	}

	/**
	 * Sends update request of My Catalogue to the CDR service.
	 * @param catalogue Catalogue to send
	 * @param documentReference {@link DocumentReference} to the catalogue
	 * @param correspondence {@link Correspondence} which this request is part of
	 * @return {@link Future} representing the CDR response that enables calling method to wait for its completion,
	 * or {@code null} if no CDR request has been made during the method invocation
	 */
	public Future<?> cdrSendMyCatalogueUpdateRequest(CatalogueType catalogue,
			DocumentReference documentReference, Correspondence correspondence)
	{
		Future<?> ret = null;
		if(catalogue != null)
		{
			try
			{
				clientFrame.appendToConsole(new StringBuilder("Sending My Catalogue update to the CDR service..."), Color.BLACK);
				myParty.setCatalogue(catalogue);
				Server port = getCDRPort();
				String username = myParty.getCDRUsername();
				ret = port.updateCatalogueWithAppResponseAsync(username, catalogue, null);
				clientFrame.appendToConsole(new StringBuilder("My Catalogue has been sent to the CDR service. Waiting for a response..."),
						Color.BLACK);
				correspondence.updateDocumentStatus(documentReference, DocumentReference.Status.CLIENT_SENT);
			}
			catch(WebServiceException e)
			{
				correspondence.updateDocumentStatus(documentReference, DocumentReference.Status.CDR_DOWN);
				logger.error("My Catalogue has not been updated by the CDR service!" +
						" Server is not accessible. Exception is ", e);
				clientFrame.appendToConsole(new StringBuilder("My Catalogue has not been updated by the CDR service!").
						append(" Server is not accessible. Please try again later."), Color.RED);
				clientFrame.enableCatalogueMenuItems();
			}
		}
		else
		{
			correspondence.updateDocumentStatus(documentReference, DocumentReference.Status.CLIENT_FAILED);
			clientFrame.appendToConsole(new StringBuilder("My Catalogue has not been sent to the CDR service because it is malformed. ").
					append("All catalogue items should have a name and catalogue has to have at least one item."), Color.RED);
			clientFrame.enableCatalogueMenuItems();
		}
		return ret;
	}

	/**
	 * Waits for the {@link ApplicationResponseType} document after {@code CatalogueType} update request had been
	 * sent to the CDR service.
	 * @param future {@link Future} on which is waited for a CDR response
	 * @param documentReference {@link DocumentReference} to the catalogue
	 * @param correspondence {@link Correspondence} which this response is part of
	 * @return true when CDR accepts sent Catalogue, false otherwise
	 * @throws Exception if something goes wrong during geting the response
	 */
	public Boolean cdrReceiveMyCatalogueUpdateAppResponse(Future<?> future, DocumentReference documentReference,
			Correspondence correspondence) throws Exception
	{
		Boolean positiveResponse = null;
		try
		{
			final UpdateCatalogueWithAppResponseResponse response = (UpdateCatalogueWithAppResponseResponse) future.get();
			final ApplicationResponseType appResponse = response.getReturn();
			final String responseCode = appResponse.getDocumentResponseAtIndex(0).getResponse().getResponseCodeValue();
			correspondence.updateDocumentStatus(documentReference, DocumentReference.Status.CDR_RECEIVED);
			if(InstanceFactory.APP_RESPONSE_POSITIVE.equals(responseCode))
			{
				myParty.setDirtyCatalogue(false);
				clientFrame.appendToConsole(new StringBuilder("My Catalogue has been successfully updated in the CDR service."),
						Color.GREEN);
				positiveResponse = Boolean.TRUE;
			}
			else if(InstanceFactory.APP_RESPONSE_NEGATIVE.equals(responseCode))
			{
				clientFrame.appendToConsole(
						new StringBuilder("My Catalogue has not been updated in the CDR service! Check My Catalogue data and try again."),
						Color.RED);
				positiveResponse = Boolean.FALSE;
			}
		}
		catch (Exception e)
		{
			if(e instanceof InterruptedException)
				correspondence.updateDocumentStatus(documentReference, DocumentReference.Status.CLIENT_FAILED);
			else
			{
				final Throwable cause = e.getCause();
				final String message = e.getMessage();
				if(( cause != null && (cause instanceof RutaException || cause instanceof ServerSOAPFaultException))
						|| (message != null && message.contains("SocketTimeoutException")))
					correspondence.updateDocumentStatus(documentReference, DocumentReference.Status.CDR_FAILED);
				else
					correspondence.updateDocumentStatus(documentReference, DocumentReference.Status.CLIENT_FAILED);
			}
			throw e;
		}
		finally
		{
			clientFrame.enableCatalogueMenuItems();
		}
		return positiveResponse;
	}

	/**
	 * Sends update request of My Catalogue to the CDR service.
	 * @param documentReference {@link DocumentReference} to the catalogue
	 * @param correspondence {@link Correspondence} which this request is part of
	 * @return {@link Future} representing the CDR response that enables calling method to wait for its completion,
	 * or {@code null} if no CDR request has been made during the method invocation
	 */
	public Future<?> cdrSendMyCatalogueDeletionRequest(CatalogueDeletionType catalogueDeletion,
			DocumentReference documentReference, Correspondence correspondence)
	{
		Future<?> ret = null;
		if(catalogueDeletion != null)
		{
			try
			{
				clientFrame.appendToConsole(new StringBuilder("Sending Request for My Catalogue deletion to the CDR service..."), Color.BLACK);
				Server port = getCDRPort();
				String username = myParty.getCDRUsername();
				ret = port.deleteCatalogueWithAppResponseAsync(username, catalogueDeletion, null);
				clientFrame.appendToConsole(new StringBuilder("Request for My Catalogue deletion has been sent to the CDR service.").
						append(" Waiting for a response..."), Color.BLACK);
				correspondence.updateDocumentStatus(documentReference, DocumentReference.Status.CLIENT_SENT);
			}
			catch(WebServiceException e)
			{
				correspondence.updateDocumentStatus(documentReference, DocumentReference.Status.CDR_DOWN);
				logger.error("My Catalogue has not been deleted from the CDR service!" +
						" Server is not accessible. Exception is ", e);
				clientFrame.appendToConsole(new StringBuilder("My Catalogue has not been deleted from the CDR service!").
						append(" Server is not accessible. Please try again later."), Color.RED);
				clientFrame.enableCatalogueMenuItems();
			}
		}
		else
		{
			correspondence.updateDocumentStatus(documentReference, DocumentReference.Status.CLIENT_FAILED);
			clientFrame.appendToConsole(new StringBuilder("Request for My Catalogue deletion has not been sent to the CDR service because it is malformed."),
					Color.RED);
			clientFrame.enableCatalogueMenuItems();
		}
		return ret;
	}

	/**
	 * Waits for the {@link ApplicationResponseType} document after {@link CatalogueDeletionType} request had been
	 * sent to the CDR service.
	 * @param future {@link Future} on which is waited for a CDR response
	 * @param documentReference {@link DocumentReference} to the catalogue
	 * @param correspondence {@link Correspondence} which this response is part of
	 * @return true when CDR accepts sent Catalogue Deletion, false otherwise and {@code null} if exception
	 * is thrown during calling the service
	 * @throws Exception
	 */
	public Boolean cdrReceiveMyCatalogueDeletionAppResponse(Future<?> future,
			DocumentReference documentReference, Correspondence correspondence) throws Exception
	{
		Boolean positiveResponse = null;
		try
		{
			final DeleteCatalogueWithAppResponseResponse response = (DeleteCatalogueWithAppResponseResponse) future.get();
			final ApplicationResponseType appResponse = response.getReturn();
			final String responseCode = appResponse.getDocumentResponseAtIndex(0).getResponse().getResponseCodeValue();
			correspondence.updateDocumentStatus(documentReference, DocumentReference.Status.CDR_RECEIVED);
			if(InstanceFactory.APP_RESPONSE_POSITIVE.equals(responseCode))
			{
				clientFrame.appendToConsole(new StringBuilder("My Catalogue has been successfully deleted from the CDR."), Color.GREEN);
				positiveResponse = Boolean.TRUE;
//				myParty.setDirtyCatalogue(false);
//				myParty.setInsertMyCatalogue(true);
//				myParty.removeCatalogueIssueDate();
			}
			else if(InstanceFactory.APP_RESPONSE_NEGATIVE.equals(responseCode))
			{
				clientFrame.appendToConsole(new StringBuilder("My Catalogue has not been deleted from the CDR! There has been some kind of data error."),
						Color.RED);
				positiveResponse = Boolean.FALSE;
			}
		}
		catch (Exception e)
		{
			if(e instanceof InterruptedException)
				correspondence.updateDocumentStatus(documentReference, DocumentReference.Status.CLIENT_FAILED);
			else
			{
				final Throwable cause = e.getCause();
				final String message = e.getMessage();
				if(( cause != null && (cause instanceof RutaException || cause instanceof ServerSOAPFaultException))
						|| (message != null && message.contains("SocketTimeoutException")))
					correspondence.updateDocumentStatus(documentReference, DocumentReference.Status.CDR_FAILED);
				else
					correspondence.updateDocumentStatus(documentReference, DocumentReference.Status.CLIENT_FAILED);
			}
			throw e;
		}
		finally
		{
			clientFrame.enableCatalogueMenuItems();
		}
		return positiveResponse;
	}

	/**
	 * Sets exception thrown in JAX-WS thread, so it can later be wrapped to {@link
	 * StateActivityException} and rethrown to the state machine.
	 * @param exception
	 */
	private void setExceptionRethrown(Exception exception)
	{
		this.exceptionRethrown = exception;
	}

	/**
	 * Sends {@code UBL document} to the CDR service.
	 * @param document document to send
	 * @param documentReference {@link DocumentReference document reference}
	 * @param correspondence correspondence which document is part of
	 */
	public void cdrSendDocument(@Nonnull Object document, DocumentReference documentReference, Correspondence correspondence)
	{
		final String documentName = InstanceFactory.getDocumentName(documentReference.getDocumentTypeValue());
		final String documentID = documentReference.getIDValue();
		clientFrame.appendToConsole(new StringBuilder("Sending " + documentName + " " + documentID +
				" to the CDR service..."), Color.BLACK);
		final Semaphore serviceCallFinished = new Semaphore(0);
		try
		{
			Server port = getCDRPort();
			port.distributeDocumentAsync(document, future ->
			{
				try
				{
					future.get();
					clientFrame.appendToConsole(new StringBuilder(documentName + " " + documentID +
							" has been successfully deposited into the CDR."), Color.GREEN);
					correspondence.updateDocumentStatus(documentReference, DocumentReference.Status.CDR_RECEIVED);
				}
				catch (Exception e)
				{
					if(e instanceof InterruptedException)
						correspondence.updateDocumentStatus(documentReference, DocumentReference.Status.CLIENT_FAILED);
					else
					{
						final Throwable cause = e.getCause();
						final String message = e.getMessage();
						if((cause != null && (cause instanceof RutaException || cause instanceof ServerSOAPFaultException))
								|| (message != null && message.contains("SocketTimeoutException")))
							correspondence.updateDocumentStatus(documentReference, DocumentReference.Status.CDR_FAILED);
						else
							correspondence.updateDocumentStatus(documentReference, DocumentReference.Status.CLIENT_FAILED);
					}
					setExceptionRethrown(e);
				}
				finally
				{
					serviceCallFinished.release();
				}
			});
			clientFrame.appendToConsole(new StringBuilder(documentName + " " + documentID +
					" has been sent to the CDR service. Waiting for a response..."), Color.BLACK);
			correspondence.updateDocumentStatus(documentReference, DocumentReference.Status.CLIENT_SENT);
			try
			{
				serviceCallFinished.acquire();
				if(documentReference.getStatus() == DocumentReference.Status.CLIENT_FAILED ||
						documentReference.getStatus() == DocumentReference.Status.CDR_FAILED)
				{
					final Exception exceptionCopy = exceptionRethrown;
					exceptionRethrown = null;
					throw new StateActivityException(documentName + " " + documentID +
							" might not be deposited into the CDR due to this failure! Please try resending it.", exceptionCopy);
				}
			}
			catch (InterruptedException e)
			{
				throw new StateActivityException("Interrupted execution of sending the document!", e);
			}
		}
		catch(WebServiceException e)
		{
			correspondence.updateDocumentStatus(documentReference, DocumentReference.Status.CDR_DOWN);
			throw new StateActivityException(documentName + " " + documentID +
					" has not been sent to the CDR service! Server is not accessible. Please try again later.", e);
		}
		catch(StateActivityException e)
		{
			throw e;
		}
		catch(Exception e)
		{
			correspondence.updateDocumentStatus(documentReference, DocumentReference.Status.CDR_FAILED);
			throw e;
		}
	}

	/**
	 * Waits for the {@link OrderResponseType} document after {@code OrderType} document had been
	 * sent to the CDR service.
	 * @param future {@link Future} on which is waited for a CDR response
	 * @return new state which correspondence should transition to
	 */
	@Deprecated
	public RutaProcessState cdrReceiveOrderResponse(Future<?> future)
	{
		RutaProcessState newState = null;
		try
		{
			final UpdateCatalogueWithAppResponseResponse response = (UpdateCatalogueWithAppResponseResponse) future.get();
			final ApplicationResponseType appResponse = response.getReturn();
			final String responseCode = appResponse.getDocumentResponseAtIndex(0).getResponse().getResponseCodeValue();

			if(InstanceFactory.APP_RESPONSE_POSITIVE.equals(responseCode))
			{
				myParty.setDirtyCatalogue(false);
				clientFrame.appendToConsole(new StringBuilder("My Catalogue has been successfully updated in the CDR service."),
						Color.GREEN);
				//				newState = CreateCatalogueEndOfProcessState.getInstance();
				newState = ClosingState.getInstance();
			}
			else if(InstanceFactory.APP_RESPONSE_NEGATIVE.equals(responseCode))
			{
				clientFrame.appendToConsole(new StringBuilder("My Catalogue has not been updated in the CDR service! Check My Catalogue data and try again."),
						Color.RED);
				newState = DecideOnActionState.getInstance();
			}
		}
		catch(Exception e)
		{
			clientFrame.processExceptionAndAppendToConsole(e,
					new StringBuilder("My Catalogue has not been updated in the CDR service! "));
		}
		finally
		{
			clientFrame.enableCatalogueMenuItems();
		}
		return newState;
	}

	/**
	 * Pulls my Catalogue from the Central Data Repository.
	 */
	@Deprecated
	public Future<?> cdrPullMyCatalogue()
	{
		Future<?> ret = null;
		try
		{
			Server port = getCDRPort();

			//CatalogueType catalogue = port.getDocument();
			ret = port.findCatalogueAsync(myParty.getCoreParty().getPartyID(), future ->
			{
				try
				{
					final FindCatalogueResponse response = future.get();
					final CatalogueType catalogue = response.getReturn();
					final BusinessParty myFollowingParty = myParty.getMyFollowingParty();
					if(catalogue != null)
					{
						clientFrame.appendToConsole(new StringBuilder("Catalogue has been successfully retrieved from the CDR service."),
								Color.GREEN);
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
						clientFrame.appendToConsole(consoleMsg, Color.GREEN);
					}
				}
				catch(Exception e)
				{
					clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder("Catalogue could not be retrieved from the CDR service!"));
				}
				finally
				{
					clientFrame.enableCatalogueMenuItems();
				}
			});
			clientFrame.appendToConsole(new StringBuilder("Request for catalogue has been sent to the CDR service. Waiting for a response..."),
					Color.BLACK);
		}
		catch(WebServiceException e)
		{
			clientFrame.appendToConsole(new StringBuilder("My Catlogue has not been retrieved from the CDR service!").
					append(" Server is not accessible. Please try again later."), Color.RED);
			clientFrame.enableCatalogueMenuItems();
		}
		return ret;
	}

	/**
	 * Sends Catalogue deletion request to the CDR service.
	 * @return {@link Future} representing the CDR response, that enables calling method to wait for its completion
	 * or {@code null} if no CDR request has been made during method invocation
	 */
	@Deprecated
	public Future<?> cdrDeleteMyCatalogueOLD()
	{
		Future<?> ret = null;
		try
		{
			CatalogueDeletionType catalogueDeletion = myParty.produceCatalogueDeletion(CDRParty);

			Server port = getCDRPort();
			String username = myParty.getCDRUsername();
			ret = port.deleteCatalogueAsync(username, catalogueDeletion, future ->
			{
				try
				{
					future.get();
					myParty.setDirtyCatalogue(true);
					myParty.removeCatalogueIssueDate();
					clientFrame.appendToConsole(new StringBuilder("Catalogue has been successfully deleted from the CDR service."), Color.GREEN);
				}
				catch(Exception e)
				{
					clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder("Catalogue has not been deleted from the CDR service!"));
				}
				finally
				{
					clientFrame.enableCatalogueMenuItems();
				}
			});
			clientFrame.appendToConsole(new StringBuilder("Request for the Catalogue deletion has been sent to the CDR service.").
					append(" Waiting for a response..."), Color.BLACK);
		}
		catch(WebServiceException e)
		{
			clientFrame.appendToConsole(new StringBuilder("Catalogue has not been deleted from the CDR service!").
					append(" Server is not accessible. Please try again later."), Color.RED);
			clientFrame.enableCatalogueMenuItems();
		}
		return ret;
	}

	/**
	 * Sends Catalogue deletion request to the CDR service.
	 * @throws InterruptedException if correspondence thread is interrupted while being blocked
	 */
	public void cdrDeleteMyCatalogue() throws InterruptedException
	{
		myParty.executeDeleteCatalogueProcess();
	}

	/**
	 * Sends a follow request to the CDR service.
	 * @param followingName name of the party to follow
	 * @param followingID ID of the party to follow
	 * @return {@link Future} representing the CDR response, that enables calling method to wait for its completion
	 * or {@code null} if no CDR request has been made during method invocation
	 */
	public Future<?> cdrFollowParty(String followingName, String followingID)
	{
		Future<?> ret = null;
		try
		{
			Server port = getCDRPort();
			String myPartyId = myParty.getPartyID();

			ret = port.followPartyAsync(myPartyId, followingID, future ->
			{
				try
				{
					FollowPartyResponse response = future.get();
					PartyType party = response.getReturn();
					if(party != null)
					{
						BusinessParty newFollowing = new BusinessParty();
						newFollowing.setCoreParty(party);
						newFollowing.setPartner(false);
						newFollowing.setRecentlyUpdated(true);
						newFollowing.setTimestamp(InstanceFactory.getDate());
						myParty.followParty(newFollowing);

						StringBuilder msg = new StringBuilder("Party ").append(followingName).
								append(" has been successfully added to the following parties.");
						clientFrame.appendToConsole(msg, Color.GREEN);
					}
					else
						clientFrame.appendToConsole(new StringBuilder("Party ").append(followingName).
								append(" could not be added to the following parties!"), Color.RED);
				}
				catch(Exception e)
				{
					clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder("Party ").append(followingName).
							append(" could not be added to the following parties!"));
				}
			});
			clientFrame.appendToConsole(new StringBuilder("Follow request has been sent to the CDR service. Waiting for a response..."),
					Color.BLACK);
		}
		catch(WebServiceException e)
		{
			logger.error("Follow request has not been sent to the CDR service!" +
					" Server is not accessible. Exception is ", e);
			clientFrame.appendToConsole(new StringBuilder("Follow request has not been sent to the CDR service!").
					append(" Server is not accessible. Please try again later."), Color.RED);
		}
		return ret;
	}

	/**
	 * Sends nonblocking unfollow request to the CDR service.
	 * @param myPartyId MyParty's ID
	 * @param followingID Id of the party to follow
	 * @return {@link Future} representing the CDR response, that enables calling method to wait for its completion
	 * or {@code null} if no CDR request has been made during method invocation
	 */
	public Future<?> cdrUnfollowParty(BusinessParty followingParty)
	{
		Future<?> ret = null;
		try
		{
			Server port = getCDRPort();
			final String myPartyId = myParty.getPartyID();
			final String followingName = followingParty.getPartySimpleName();
			final String followingID = followingParty.getPartyID();

			ret = port.unfollowPartyAsync(myPartyId, followingID, future ->
			{
				try
				{
					future.get();
					myParty.unfollowParty(followingParty);
					StringBuilder msg = new StringBuilder("Party " + followingName + " has been unfollowed.");
					clientFrame.appendToConsole(msg, Color.GREEN);
				}
				catch(Exception e)
				{
					clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder("Party ").append(followingName).
							append(" could not be removed from the following parties!"));
				}
			});
			clientFrame.appendToConsole(new StringBuilder("Unfollow request has been sent to the CDR service. Waiting for a response..."), Color.BLACK);
		}
		catch(WebServiceException e)
		{
			logger.error("Unfollow request has not been sent to the CDR service!" +
					" Server is not accessible. Exception is ", e);
			clientFrame.appendToConsole(new StringBuilder("Unfollow request has not been sent to the CDR service!"
					+ " Server is not accessible. Please try again later."), Color.RED);
		}
		return ret;
	}

	/**
	 * Sends request to the CDR service for all new DocBox documents.
	 * @return {@link Semaphore} object that enables this kind of CDR service calls to be sequentally
	 * ordered with some other business logic that invokes the service. That logic can invoke this method
	 * and then wait for it to be finished, so the logic could continue with its execution upon method ends.
	 * After the end of the method call, number of permits in this {@code Semaphore} is 1.
	 */
	public Semaphore cdrGetNewDocuments()
	{
		Semaphore sequential = new Semaphore(0);
		try
		{
			final Server port = getCDRPort();
			final String myPartyId = myParty.getPartyID();
			final DocBoxAllIDsSearchCriterion criterion = new DocBoxAllIDsSearchCriterion();
			criterion.setPartyID(myPartyId);
			port.findAllDocBoxDocumentIDsAsync(criterion, future ->
			{
				try
				{
					final FindAllDocBoxDocumentIDsResponse response = future.get();
					final List<String> docBoxDocumentIDs = response.getReturn();
					final int docCount = docBoxDocumentIDs.size();
					if(docCount != 0)
					{
						String plural = docCount + " documents";
						String there = "There are ";
						if(docCount == 1)
						{
							plural = docCount + " document";
							there = "There is ";
						}
						clientFrame.appendToConsole(new StringBuilder(there).append(plural).append(" in my DocBox."), Color.BLACK);
						clientFrame.appendToConsole(new StringBuilder("Started download of ").append(plural).append(". Waiting..."), Color.BLACK);
						AtomicInteger downloadCount = new AtomicInteger(0);
						CountDownLatch finished = new CountDownLatch(docCount);
						Semaphore oneAtATime = new Semaphore(1);
						for(String docID : docBoxDocumentIDs)
						{
							oneAtATime.acquire();
							final DocBoxDocumentSearchCriterion docCriterion = new DocBoxDocumentSearchCriterion();
							docCriterion.setPartyID(myPartyId);
							docCriterion.setDocumentID(docID);
							port.findDocBoxDocumentAsync(docCriterion, docFuture ->
							{
								DocumentReceipt documentReceipt = null;
								Object document = null;
								try
								{
									final FindDocBoxDocumentResponse res = docFuture.get();
									document = res.getReturn();
									oneAtATime.release();
									if(document != null)
									{
										downloadCount.incrementAndGet();
										documentReceipt = processDocBoxDocument(document, docID);
									}
									else
										clientFrame.appendToConsole(new StringBuilder("Document ").append(docID).
												append(" could not be downloaded!"), Color.RED);
								}
								catch (InterruptedException | ExecutionException e)
								{
									oneAtATime.release();
									clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder("Document ").
											append(docID).append(" could not be downloaded!"));
									logger.error(new StringBuilder("Document ").
											append(docID).append(" could not be downloaded!").toString(), e);
								}
								catch (Exception e)
								{
									oneAtATime.release();
									final String exceptionMessage = e.getMessage();
									if(exceptionMessage != null)
									{
										if(exceptionMessage.contains("has been already received and processed"))
										{
											try
											{
												port.distributeDocumentAsync(DocumentReceipt.newInstance(document));
											}
											catch (DetailException e1)
											{
											}
										}
										else if(exceptionMessage.contains("does not belong to the current state of the correspondence"))
										{
											try
											{
												port.distributeDocumentAsync(DocumentReceipt.newInstance(document,
														DocumentReference.Status.CORR_FAILED));
											}
											catch (DetailException e1)
											{
											}
										}
									}
									clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder("Document ").
											append(docID).append(" could not be placed where it belogs in the data model!"));
									logger.error(new StringBuilder("Document ").
											append(docID).append(" could not be placed where it belogs in the data model!").toString(), e);
								}
								finally
								{
									if(document != null)
										port.deleteDocBoxDocumentWithDocumentReceiptAsync(myParty.getCDRUsername(), docID, documentReceipt);
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
							clientFrame.appendToConsole(new StringBuilder("Successful download of ").append(plural).append(". ").append(failed),
									Color.BLACK);
						else
							clientFrame.appendToConsole(new StringBuilder("Successful download of ").append(plural).append("."), Color.BLACK);
					}
					else
						clientFrame.appendToConsole(new StringBuilder("There are no new documents in my DocBox."), Color.GREEN);
				}
				catch(Exception e)
				{
					clientFrame.processExceptionAndAppendToConsole(e,
							new StringBuilder("Download request for new documents has not been successcully processed!"));
				}
				finally
				{
					sequential.release();
					clientFrame.enableGetDocumentsMenuItem();
				}
			});
			clientFrame.appendToConsole(new StringBuilder("Download request for new documents has been sent to the CDR service.").
					append(" Waiting for a response..."), Color.BLACK);
		}
		catch(WebServiceException e)
		{
			logger.error("Request for new documents has not been sent to the CDR service!" +
					" Server is not accessible. Exception is ", e);
			clientFrame.appendToConsole(new StringBuilder("Request for new documents has not been sent to the CDR service!")
					.append(" Server is not accessible. Please try again later."), Color.RED);
			clientFrame.enableGetDocumentsMenuItem();
			sequential.release();
		}
		return sequential;
	}

	/**
	 * Processes DocBox document by placing it on the proper place within local domain model and/or
	 * executing procedure in conection with it.
	 * @param document document to be processed and placed
	 * @param docID document's ID get from the service side
	 * @return {@link DocumentReceipt} describing the receipt of processed document or {@code null} if a
	 * document is of type of {@code DocumentReceipt} or of an unexpected type
	 * @throws DetailException due to the connectivity issues with the data store
	 * @throws InterruptedException if correspondence thread is interrupted while being blocked
	 */
	private DocumentReceipt processDocBoxDocument(Object document, String docID) throws DetailException, InterruptedException
	{
		DocumentReceipt documentReceipt = null;
		final Class<?> documentClazz = document.getClass();
		boolean createDocumentReceipt = true; // true for documents for which DocumentReceipt should be returned back
		if(documentClazz == DocumentReceipt.class)
		{
			createDocumentReceipt = false;
			clientFrame.appendToConsole(new StringBuilder("Document Receipt ").
					append(((DocumentReceipt) document).getIDValue()).
					append(" has been successfully retrieved."), Color.GREEN);
			myParty.processDocBoxDocumentReceipt((DocumentReceipt) document);
			String partyName;
			try
			{
				partyName = InstanceFactory.getPropertyOrNull(
						((DocumentReceipt) document).getSenderParty().getPartyName().get(0),
						PartyNameType::getNameValue);
			}
			catch(Exception e)
			{
				partyName = "";
			}
			clientFrame.appendToConsole(new StringBuilder("Party ").append(partyName).
					append("'s Document Receipt " + ((DocumentReceipt) document).getIDValue() +
							" has been updated its document status."),
					Color.BLACK);
		}
		else if(documentClazz == CatalogueType.class)
		{
			createDocumentReceipt = false;
			clientFrame.appendToConsole(new StringBuilder("Catalogue document ").append(docID).
					append(" has been successfully retrieved."), Color.GREEN);
			myParty.processDocBoxCatalogue((CatalogueType) document);
			String partyName;
			try
			{
				partyName = InstanceFactory.getPropertyOrNull(((CatalogueType) document).getProviderParty().getPartyName().get(0),
						PartyNameType::getNameValue);
			}
			catch(Exception e)
			{
				partyName = "";
			}
			clientFrame.appendToConsole(new StringBuilder("Catalogue of the party ").append(partyName).append(" has been updated."),
					Color.BLACK);
		}
		else if(documentClazz == PartyType.class)
		{
			createDocumentReceipt = false;
			clientFrame.appendToConsole(new StringBuilder("Party document ").append(docID).
					append(" has been successfully retrieved."), Color.GREEN);
			myParty.processDocBoxParty((PartyType) document);
			String partyName;
			try
			{
				partyName = InstanceFactory.getPropertyOrNull(((PartyType) document).getPartyName().get(0),
						PartyNameType::getNameValue);
			}
			catch(Exception e)
			{
				partyName = "";
			}
			clientFrame.appendToConsole(new StringBuilder("Party ").append(partyName).append(" has been updated."), Color.BLACK);
		}
		else if(documentClazz == CatalogueDeletionType.class)
		{
			createDocumentReceipt = false;
			clientFrame.appendToConsole(new StringBuilder("CatalogueDeletion document ").append(docID).
					append(" has been successfully retrieved."), Color.GREEN);
			myParty.processDocBoxCatalogueDeletion((CatalogueDeletionType) document);
			String partyName;
			try
			{
				partyName = InstanceFactory.getPropertyOrNull(((CatalogueDeletionType) document).getProviderParty().getPartyName().get(0),
						PartyNameType::getNameValue);
			}
			catch(Exception e)
			{
				partyName = "";
			}
			clientFrame.appendToConsole(new StringBuilder("Catalogue of the party ").append(partyName).append(" has been deleted."),
					Color.BLACK);
		}
		else if(documentClazz == DeregistrationNotice.class)
		{
			createDocumentReceipt = false;
			clientFrame.appendToConsole(new StringBuilder("DeregistrationNotice document ").append(docID).
					append(" has been successfully retrieved."), Color.GREEN);
			myParty.processDocBoxDeregistrationNotice((DeregistrationNotice) document);
			String partyName;
			try
			{
				partyName = InstanceFactory.getPropertyOrNull(((DeregistrationNotice) document).getParty().getPartyName().get(0),
						PartyNameType::getNameValue);
			}
			catch(Exception e)
			{
				partyName = "";
			}
			clientFrame.appendToConsole(new StringBuilder("Party ").append(partyName).append(" has been archived."), Color.BLACK);
		}
		else if(documentClazz == OrderType.class)
		{
			final OrderType order = (OrderType) document;
			clientFrame.appendToConsole(new StringBuilder("Order ").append(order.getIDValue()).
					append(" has been successfully retrieved."), Color.GREEN);
			myParty.processDocBoxOrder(order);
			String partyName;
			try
			{
				partyName = InstanceFactory.getPropertyOrNull(
						order.getBuyerCustomerParty().getParty().getPartyName().get(0),
						PartyNameType::getNameValue);
			}
			catch(Exception e)
			{
				partyName = "";
			}
			clientFrame.appendToConsole(new StringBuilder("Party ").append(partyName).
					append("'s Order " + order.getIDValue() + " has been appended to its correspondence."),
					Color.BLACK);
		}
		else if(documentClazz == OrderResponseType.class)
		{
			clientFrame.appendToConsole(new StringBuilder("Order Response ").
					append(((OrderResponseType) document).getIDValue()).
					append(" has been successfully retrieved."), Color.GREEN);
			myParty.processDocBoxOrderResponse((OrderResponseType) document);
			String partyName;
			try
			{
				partyName = InstanceFactory.getPropertyOrNull(
						((OrderResponseType) document).getSellerSupplierParty().getParty().getPartyName().get(0),
						PartyNameType::getNameValue);
			}
			catch(Exception e)
			{
				partyName = "";
			}
			clientFrame.appendToConsole(new StringBuilder("Party ").append(partyName).
					append("'s Order Response " + ((OrderResponseType) document).getIDValue() +
							" has been appended to its correspondence."),
					Color.BLACK);
		}
		else if(documentClazz == OrderResponseSimpleType.class)
		{
			clientFrame.appendToConsole(new StringBuilder("Order Response Simple ").
					append(((OrderResponseSimpleType) document).getIDValue()).
					append(" has been successfully retrieved."), Color.GREEN);
			myParty.processDocBoxOrderResponseSimple((OrderResponseSimpleType) document);
			String partyName;
			try
			{
				partyName = InstanceFactory.getPropertyOrNull(
						((OrderResponseSimpleType) document).getSellerSupplierParty().getParty().getPartyName().get(0),
						PartyNameType::getNameValue);
			}
			catch(Exception e)
			{
				partyName = "";
			}
			clientFrame.appendToConsole(new StringBuilder("Party ").append(partyName).
					append("'s Order Response Simple " + ((OrderResponseSimpleType) document).getIDValue() +
							" has been appended to its correspondence."),
					Color.BLACK);
		}
		else if(documentClazz == OrderChangeType.class)
		{
			clientFrame.appendToConsole(new StringBuilder("Order Change ").
					append(((OrderChangeType) document).getIDValue()).
					append(" has been successfully retrieved."), Color.GREEN);
			myParty.processDocBoxOrderChange((OrderChangeType) document);
			String partyName;
			try
			{
				partyName = InstanceFactory.getPropertyOrNull(
						((OrderChangeType) document).getSellerSupplierParty().getParty().getPartyName().get(0),
						PartyNameType::getNameValue);
			}
			catch(Exception e)
			{
				partyName = "";
			}
			clientFrame.appendToConsole(new StringBuilder("Party ").append(partyName).
					append("'s Order Response Simple ").append(((OrderChangeType) document).getIDValue()).
					append(" has been appended to its correspondence."),
					Color.BLACK);
		}
		else if(documentClazz == OrderCancellationType.class)
		{
			clientFrame.appendToConsole(new StringBuilder("Order Cancellation ").
					append(((OrderCancellationType) document).getIDValue()).
					append(" has been successfully retrieved."), Color.GREEN);
			myParty.processDocBoxOrderCancellation((OrderCancellationType) document);
			String partyName;
			try
			{
				partyName = InstanceFactory.getPropertyOrNull(
						((OrderCancellationType) document).getSellerSupplierParty().getParty().getPartyName().get(0),
						PartyNameType::getNameValue);
			}
			catch(Exception e)
			{
				partyName = "";
			}
			clientFrame.appendToConsole(new StringBuilder("Party ").append(partyName).
					append("'s Order Response Simple ").append(((OrderCancellationType) document).getIDValue()).
					append(" has been appended to its correspondence."),
					Color.BLACK);
		}
		else if(documentClazz == ApplicationResponseType.class)
		{
			clientFrame.appendToConsole(new StringBuilder("Application Response ").
					append(((ApplicationResponseType) document).getIDValue()).
					append(" has been successfully retrieved."), Color.GREEN);
			myParty.processDocBoxApplicationResponse((ApplicationResponseType) document);
			String partyName;
			try
			{
				partyName = InstanceFactory.getPropertyOrNull(
						((ApplicationResponseType) document).getSenderParty().getPartyName().get(0),
						PartyNameType::getNameValue);
			}
			catch(Exception e)
			{
				partyName = "";
			}
			clientFrame.appendToConsole(new StringBuilder("Party ").append(partyName).
					append("'s Application Response ").append(((ApplicationResponseType) document).getIDValue()).
					append(" has been appended to its correspondence."),
					Color.BLACK);
		}
		else if(documentClazz == InvoiceType.class)
		{
			clientFrame.appendToConsole(new StringBuilder("Invoice ").
					append(((InvoiceType) document).getIDValue()).
					append(" has been successfully retrieved."), Color.GREEN);
			myParty.processDocBoxInvoice((InvoiceType) document);
			String partyName;
			try
			{
				partyName = InstanceFactory.getPropertyOrNull(
						((InvoiceType) document).getAccountingSupplierParty().getParty().getPartyName().get(0),
						PartyNameType::getNameValue);
			}
			catch(Exception e)
			{
				partyName = "";
			}
			clientFrame.appendToConsole(new StringBuilder("Party ").append(partyName).
					append("'s Invoice ").append(((InvoiceType) document).getIDValue()).
					append(" has been appended to its correspondence."),
					Color.BLACK);
		}
		else if(documentClazz == PartnershipRequest.class)
		{
			createDocumentReceipt = false;
			clientFrame.appendToConsole(new StringBuilder("Business Partnership Request ").
					append(((PartnershipRequest) document).getIDValue()).
					append(" has been successfully retrieved."), Color.GREEN);
			myParty.processDocBoxPartnershipRequest((PartnershipRequest) document);
			String partyName;
			try
			{
				partyName = InstanceFactory.getPropertyOrNull(
						((PartnershipRequest) document).getRequesterParty().getPartyName().get(0),
						PartyNameType::getNameValue);
			}
			catch(Exception e)
			{
				partyName = "";
			}
			clientFrame.appendToConsole(new StringBuilder("Party ").append(partyName).
					append("'s Business Partnership Request ").append(((PartnershipRequest) document).getIDValue()).
					append(" has been received and set in the data model."),
					Color.BLACK);
		}
		else if(documentClazz == PartnershipResolution.class)
		{
			createDocumentReceipt = false;
			clientFrame.appendToConsole(new StringBuilder("Business Partnership Resolution ").
					append(((PartnershipResolution) document).getIDValue()).
					append(" has been successfully retrieved."), Color.GREEN);
			myParty.processDocBoxPartnershipResolution((PartnershipResolution) document);
			String partyName;
			try
			{
				partyName = InstanceFactory.getPropertyOrNull(
						((PartnershipResolution) document).getRequesterParty().getPartyName().get(0),
						PartyNameType::getNameValue);
			}
			catch(Exception e)
			{
				partyName = "";
			}
			clientFrame.appendToConsole(new StringBuilder("Business Partnership Resolution ").
					append(((PartnershipResolution) document).getIDValue()).
					append(" has been received and set in the data model."),
					Color.BLACK);
		}
		else if(documentClazz == PartnershipBreakup.class)
		{
			createDocumentReceipt = false;
			clientFrame.appendToConsole(new StringBuilder("Business Partnership Breakup ").
					append(((PartnershipBreakup) document).getIDValue()).
					append(" has been successfully retrieved."), Color.GREEN);
			myParty.processDocBoxPartnershipBreakup((PartnershipBreakup) document);
			String partyName;
			try
			{
				partyName = InstanceFactory.getPropertyOrNull(
						((PartnershipBreakup) document).getRequesterParty().getPartyName().get(0),
						PartyNameType::getNameValue);
				if(myParty.getPartySimpleName().equals(partyName))
					partyName = InstanceFactory.getPropertyOrNull(
							((PartnershipBreakup) document).getRequestedParty().getPartyName().get(0),
							PartyNameType::getNameValue);
			}
			catch(Exception e)
			{
				partyName = "";
			}
			clientFrame.appendToConsole(new StringBuilder("Business Partnership Breakup with Party ").append(partyName).
					append(" ").append(((PartnershipBreakup) document).getIDValue()).
					append(" has been received and set in the data model."),
					Color.BLACK);
		}
		else
		{
			createDocumentReceipt = false;
			clientFrame.appendToConsole(new StringBuilder("Document ").append(docID).
					append(" of an unexpected type: ").append(document.getClass().getSimpleName()).
					append(" has been successfully retrieved. Don't know what to do with it. Moving it to the trash."),
					Color.BLACK);
		}
		if(createDocumentReceipt)
			documentReceipt = DocumentReceipt.newInstance(document);

		return documentReceipt;
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

	/**
	 * Gets the default end point of the CDR service.
	 * @return {@code String} representing the end point
	 */
	public static String getDefaultEndPoint()
	{
		return defaultEndPoint;
	}

	/**
	 * Gets the end point of the CDR service.
	 * @return {@code String} representing the end point
	 */
	public static String getCDREndPoint()
	{
		return cdrEndPoint;
	}

	/**
	 * Sets the end point of the CDR service.
	 * @param endPoint {@code String} representing the end point
	 */
	public static void setCDREndPoint(String endPoint)
	{
		RutaClient.cdrEndPoint = endPoint;
	}

	/**
	 * Reverts to default value of the service end point location.
	 */
	public static void resetCDREndPoint()
	{
		RutaClient.cdrEndPoint = defaultEndPoint;
	}

	/**
	 * Temporary setting of the endpoint address property for TCP/IP Monitor in Eclipse.
	 * @param port port of the service on which the endpoint address property is set
	 */
	private static void bindEclipseEndPoint(Server port)
	{
		((BindingProvider) port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, eclipseMonitorEndPoint);
	}

	/**
	 * Binds the endpoint address of the CDR service to the {@code endPoint} static field. If {@code endPoint}
	 * field is {@code null} then default value (i.e. generated by the {@codeJAX-WS}) is used.
	 * @param port
	 */
	private static void bindCDREndPoint(Server port)
	{
		((BindingProvider) port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, cdrEndPoint);
	}

	public static int getConnectTimeout()
	{
		return CONNECT_TIMEOUT;
	}

	public static void setConnectTimeout(int timeout)
	{
		CONNECT_TIMEOUT = timeout;
	}

	public static int getRequestTimeout()
	{
		return REQUEST_TIMEOUT;
	}

	public static void setRequestTimeout(int timeout)
	{
		REQUEST_TIMEOUT = timeout;
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
				logger.error("Exception is ", e);
			}
		}
	}

	/**
	 * Gets {@link MyParty} object.
	 * @return
	 */
	public MyParty getMyParty()
	{
		return myParty;
	}

	public void setMyParty(MyParty party)
	{
		myParty = party;
	}

	/**
	 * Gets the {@code CDR} party instance. Creates it if it has a {@code null} value prior this method invocation.
	 * @return {@code CDR} party
	 */
	public Party getCDRParty()
	{
		if (CDRParty == null)
		{
			CDRParty = new Party();
			CDRParty.setPartyID("CDR1234567890");
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
		this.clientFrame = clientFrame;
	}

	/**
	 * Gets the CDR web service port.
	 * @return web service port
	 * @throws WebServiceException if could not connect to the CDR service
	 */
	private Server getCDRPort()
	{
		/*try
		{*/
			//getting webservice port
			final CDRService service = new CDRService();
			service.setHandlerResolver(new ClientHandlerResolver(myParty));
			final Server port = service.getCDRPort();
			//temporary setting for TCP/IP Monitor in Eclipse
			//bindEclipseEndPoint(port);
			if(!defaultEndPoint.equals(cdrEndPoint))
				bindCDREndPoint(port);

			final BindingProvider bindingProvider = (BindingProvider) port;
			//			System.out.println(System.getProperty(com.sun.xml.ws.client.BindingProviderProperties.CONNECT_TIMEOUT));
			//			System.out.println(System.getProperty(com.sun.xml.ws.client.BindingProviderProperties.REQUEST_TIMEOUT));

			//Set timeout until a connection is established
			bindingProvider.getRequestContext().put(com.sun.xml.ws.client.BindingProviderProperties.CONNECT_TIMEOUT, CONNECT_TIMEOUT);

			//Set timeout until the response is received
			bindingProvider.getRequestContext().put(com.sun.xml.ws.client.BindingProviderProperties.REQUEST_TIMEOUT, REQUEST_TIMEOUT);

			final SOAPBinding binding = (SOAPBinding) bindingProvider.getBinding();
			binding.setMTOMEnabled(true);

			return port;
	/*	}
		catch(Exception e)
		{
			logger.error("Exception is ", e);
			throw e;
		}*/
	}

	/**
	 * Sends search request for parties and catalogues to the CDR service.
	 * @param search {@link Search} object representing the search and results
	 * @param exist true if the search is repeated, i.e. it is not a new one
	 * @return {@link Future} representing the CDR response, that enables calling method to wait for its completion
	 * or {@code null} if no CDR request has been made during method invocation
	 */
	@SuppressWarnings("unchecked")
	public Future<?> cdrSearch(Search<?> search, boolean exist)
	{
		Future<?> ret = null;
		try
		{
			Server port = getCDRPort();
			search.setTimestamp();
			SearchCriterion criterion = search.getCriterion();
			if(criterion instanceof CatalogueSearchCriterion) //querying catalogues
			{
				ret = port.searchCatalogueAsync((CatalogueSearchCriterion) criterion, futureResult ->
				{
					try
					{
						SearchCatalogueResponse res = futureResult.get();
						List<CatalogueType> results = res.getReturn();
						final Search<CatalogueType> newSearch = (Search<CatalogueType>) search;
						newSearch.setResults(results);

						if(results == null || results.isEmpty())
						{
							clientFrame.appendToConsole(new StringBuilder("Nothing found at CDR service that conforms to your search").
									append(" request \"").append(newSearch.getSearchName()).append("\"."), Color.GREEN);
							int option = JOptionPane.showConfirmDialog(clientFrame, "Nothing found at CDR service that conforms to your search request\n\"" +
									newSearch.getSearchName() + "\". Do you want to save this search with no results?", "No search results",
									JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
							if(option == JOptionPane.YES_OPTION)
							{
								if(exist)
									myParty.updateCatalogueSearch(newSearch);
								else
									myParty.addCatalogueSearch(newSearch);
							}
							else
								if(exist)
								{
									myParty.removeCatalogueSearch(newSearch);
									clientFrame.appendToConsole(new StringBuilder("Search \"").append(newSearch.getSearchName()).
											append("\" has been deleted."), Color.GREEN);
								}
						}
						else
						{
							if(exist)
								myParty.updateCatalogueSearch(newSearch);
							else
								myParty.addCatalogueSearch(newSearch);
							clientFrame.appendToConsole(new StringBuilder("Search results for search request \"").
									append(newSearch.getSearchName()).append("\" have been successfully retrieved from the CDR service."),
									Color.GREEN);
						}
					}
					catch(Exception e)
					{
						clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder("Search request could not be processed! "));
					}
					finally
					{
						clientFrame.enableSearchMenuItems();
					}
				});
				clientFrame.appendToConsole(new StringBuilder("Search request \"").append(search.getSearchName()).
						append("\" has been sent to the CDR service. Waiting for a response..."), Color.BLACK);
			}
			else // querying only parties
			{
				ret = port.searchPartyAsync((PartySearchCriterion) criterion, futureResult ->
				{
					try
					{
						SearchPartyResponse res = futureResult.get();
						List<PartyType> results = res.getReturn();
						final PartySearch newSearch = (PartySearch) search;
						newSearch.setResults(results);

						if(results == null || results.isEmpty())
						{
							clientFrame.appendToConsole(new StringBuilder("Nothing found at CDR service that conforms to your search request \"").
									append(newSearch.getSearchName()).append("\"."), Color.GREEN);
							int option = JOptionPane.showConfirmDialog(clientFrame, "Nothing found at CDR service that conforms to your search request\n\"" +
									newSearch.getSearchName() + "\". Do you want to save this search with no results?", "No search results",
									JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
							if(option == JOptionPane.YES_OPTION)
							{
								if(exist)
									myParty.updatePartySearch(newSearch);
								else
									myParty.addPartySearch(newSearch);
							}
							else
								if(exist)
								{
									myParty.removePartySearch(newSearch);
									clientFrame.appendToConsole(new StringBuilder("Search \"").append(newSearch.getSearchName()).
											append("\" has been deleted."), Color.GREEN);
								}
						}
						else
						{
							if(exist)
								myParty.updatePartySearch(newSearch);
							else
								myParty.addPartySearch(newSearch);
							clientFrame.appendToConsole(new StringBuilder("Search results for search request \"").
									append(newSearch.getSearchName()).append("\" have been successfully retrieved from the CDR service."),
									Color.GREEN);
						}
					}
					catch(Exception e)
					{
						clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder("Search request could not be processed!"));
					}
					finally
					{
						clientFrame.enableSearchMenuItems();
					}
				});
				clientFrame.appendToConsole(new StringBuilder("Search request \"").append(search.getSearchName()).
						append("\" has been sent to the CDR service. Waiting for a response..."), Color.BLACK);
			}
		}
		catch(WebServiceException e)
		{
			logger.error("Search request " + search.getSearchName() +
				" has not been processed! Server is not accessible. Exception is ", e);
			clientFrame.appendToConsole(new StringBuilder("Search request \"").append(search.getSearchName()).
					append("\" has not been processed! Server is not accessible. Please try again later."), Color.RED);
			clientFrame.enableSearchMenuItems();
			if(!exist)
				Search.decreaseSearchNumber();
		}
		return ret;
	}

	/**
	 * Sends request to the CDR service to see if there is an available update of the Ruta Client Application.
	 * @return instance of the {@link Future} interface returned by the webmethod invoked for this service
	 * @return {@link Future} representing the CDR response, that enables calling method to wait for its completion
	 * or {@code null} if no CDR request has been made during method invocation
	 */
	public Future<?> cdrUpdateRutaClient()
	{
		Future<?> ret = null;
		try
		{
			Server port = getCDRPort();
			ret = port.updateRutaClientAsync(version.getVersion(), futureResult -> { });
		}
		catch(WebServiceException e)
		{
			logger.error("Exception is ", e);
			clientFrame.appendToConsole(new StringBuilder("Update request has hot been sent! Server is not accessible. Please try again later."),
					Color.RED);
		}
		return ret;
	}

	/**
	 * Sends update notification to the CDR service. This notification tells other {@code Ruta Client}s if there is new
	 * version of the Ruta Client Application.
	 * @param version version of the new Ruta Client Application
	 * @return {@link Future} representing the CDR response, that enables calling method to wait for its completion
	 * or {@code null} if no CDR request has been made during method invocation
	 */
	public Future<?> cdrUpdateNotification(RutaVersion version)
	{
		Future<?> ret = null;
		try
		{
			Server port = getCDRPort();
			ret = port.notifyUpdateAsync(version, futureResult ->
			{
				try
				{
					futureResult.get();
					clientFrame.appendToConsole(new StringBuilder("CDR service has been successfully notified about new Ruta Client version."),
							Color.GREEN);
				}
				catch(Exception e)
				{
					clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder("CDR service could not be notified! "));
				}
			});
			clientFrame.appendToConsole(new StringBuilder("Update notification has been sent to the CDR service. Waiting for a response..."),
					Color.BLACK);
		}
		catch(WebServiceException e)
		{
			logger.error("CDR service could not be notified! Server is not accessible. Exception is ", e);
			clientFrame.appendToConsole(new StringBuilder("CDR service could not be notified! Server is not accessible. Please try again later."),
					Color.RED);
		}
		return ret;
	}

	/**
	 * MMM This method should be moved to new project Ruta ServiceInterface <br/>
	 * Sends request to CDr service to clear in-memory cache object.
	 * @return {@link Future} representing the CDR response, that enables calling method to wait for its completion
	 * or {@code null} if no CDR request has been made during method invocation
	 */
	public Future<?> cdrClearCache()
	{
		Future<?> ret = null;
		try
		{
			Server port = getCDRPort();
			ret = port.clearCacheAsync(future ->
			{
				try
				{
					future.get();
				}
				catch (Exception e)
				{
					clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder("CDR service could not clear its cache! "));
				}

			});
			clientFrame.appendToConsole(new StringBuilder("Request for clearing the CDR service's cache has been sent.").
					append(" Waiting for a response..."), Color.BLACK);
		}
		catch(WebServiceException e)
		{
			logger.error("CDR service could not clear its cache! Server is not accessible. Exception is ", e);
			clientFrame.appendToConsole(new StringBuilder("CDR service could not clear its cache! Server is not accessible.").
					append(" Please try again later."), Color.RED);
		}
		return ret;
	}

	public Future<?> cdrReportBug(BugReport bug)
	{
		Future<?> ret = null;
		try
		{
			bug.setReportedBy(myParty.getCDRUsername());
			Server port = getCDRPort();
			ret = port.insertBugReportAsync(bug, futureResult ->
			{
				try
				{
					futureResult.get();
					clientFrame.appendToConsole(new StringBuilder("Bug report has been successfully deposited into the CDR service."),
							Color.GREEN);
				}
				catch(Exception e)
				{
					clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder("Bug could not be reported! "));
				}
			});
			clientFrame.appendToConsole(new StringBuilder("Bug report has been sent to the CDR service. Waiting for a response..."),
					Color.BLACK);
		}
		catch(WebServiceException e)
		{
			logger.error("Bug could not be reported to the CDR service! Server is not accessible. Exception is ", e);
			clientFrame.appendToConsole(new StringBuilder("Bug could not be reported to the CDR service! Server is not accessible.").
					append(" Please try again later."), Color.RED);
		}
		return ret;
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
					clientFrame.appendToConsole(new StringBuilder("Bug report list has been successfully retrieved from the CDR service."), Color.GREEN);
					setBugReports(res.getReturn());
				}
				catch(Exception exception)
				{
					msg.append("Server responds: ");
					Throwable cause = exception.getCause();
					if(cause instanceof RutaException)
						msg.append(cause.getMessage()).append(" ").append(((RutaException) cause).getFaultInfo().getDetail());
					else
						msg.append(trimSOAPFaultMessage(cause.getMessage()));
					clientFrame.appendToConsole(msg.toString(), Color.RED);
				}
			});
			clientFrame.appendToConsole(new StringBuilder("Request for the list of all bug reports has been sent to the CDR service. Waiting for a response..."), Color.BLACK);

		}
		catch(WebServiceException exception)
		{
			clientFrame.appendToConsole(new StringBuilder("Bug list not be retrived from the CDR service! Server is not accessible. Please try again later."), Color.RED);
		}
	}*/

	/**
	 * Sends a request to the CDR for the list of all {@link BugReport reported bugs}. Method returns
	 * a {@link Future} object which can be inspected for the result of the response of the CDR service.
	 * @return {@code Future} object representing the CDR response
	 */
	public Future<?> cdrFindAllBugs()
	{
		Future<?> ret = null;
		try
		{
			Server port = getCDRPort();
			ret = port.findAllBugReportsAsync(futureResult -> { });
			clientFrame.appendToConsole(new StringBuilder("Request for the list of all bug reports has been sent to the CDR service.").
					append(" Waiting for a response..."), Color.BLACK);
		}
		catch(WebServiceException e)
		{
			logger.error("Exception is ", e);
			clientFrame.appendToConsole(new StringBuilder("Bug list not be retrived from the CDR service! Server is not accessible.").
					append(" Please try again later."), Color.RED);
		}
		return ret;
	}

	/**
	 * Sends a request to the CDR for the list of {@link BugReport}s based on some search criterion. Method returns
	 * a {@link Future} object which can be inspected for the result of the returned request from the CDR.
	 * @param criterion
	 * @return {@code Future} object representing the CDR response
	 */
	public Future<?> cdrSearchBugReport(BugReportSearchCriterion criterion)
	{
		Future<?> ret = null;
		try
		{
			Server port = getCDRPort();
			ret = port.searchBugReportAsync(criterion, futureResult -> { }); //MMM replace the futureResult with null
			clientFrame.appendToConsole(new StringBuilder("Request for the list of all bug reports has been sent to the CDR service.").
					append(" Waiting for a response..."), Color.BLACK);
		}
		catch(WebServiceException e)
		{
			logger.error("Bug list could not be retrived from the CDR service! Server is not accessible. Exception is ", e);
			clientFrame.appendToConsole(new StringBuilder("Bug list could not be retrived from the CDR service! Server is not accessible.").
					append(" Please try again later."), Color.RED);
		}
		return ret;
	}

	/**
	 * Sends a request to the CDR for the {@link BugReport}.
	 * @param uuid {@code BugReport}'s uuid
	 * @return {@code Future} object representing the CDR response
	 */
	public Future<?> cdrFindBug(String id)
	{
		Future<?> ret = null;
		try
		{
			Server port = getCDRPort();
			ret = port.findBugReportAsync(id, futureResult -> { });
			clientFrame.appendToConsole(new StringBuilder("Request for the bug report has been sent to the CDR service. Waiting for a response..."),
					Color.BLACK);
		}
		catch(WebServiceException e)
		{
			logger.error("Bug report could not be retrived from the CDR service!" +
				" Server is not accessible. Exception is ", e);
			clientFrame.appendToConsole(new StringBuilder("Bug report could not be retrived from the CDR service!").
					append(" Server is not accessible. Please try again later."), Color.RED);
		}
		return ret;
	}

	/**Sends a request to the CDR for adding a {@link ReportComment} to the {@link BugReport}.
	 * @param uuid {@code BugReport}'s uuid
	 * @param comment comment to be added
	 * @return {@code Future} object representing the CDR response
	 */
	public Future<?> cdrAddBugReportComment(String id, ReportComment comment)
	{
		Future<?> ret = null;
		try
		{
			Server port = getCDRPort();
			ret = port.addBugReportCommentAsync(id, comment, futureResult -> { });
			clientFrame.appendToConsole(new StringBuilder("Comment has been sent to the CDR service. Waiting for a response..."), Color.BLACK);
		}
		catch(WebServiceException e)
		{
			logger.error("Comment could not be sent to the CDR service! Server is not accessible. Exception is ", e);
			clientFrame.appendToConsole(new StringBuilder("Comment could not be sent to the CDR service! Server is not accessible.").
					append(" Please try again later."),Color.RED);
		}
		return ret;
	}

	/**
	 * Temporary method. Should be deleted.
	 */
	@Deprecated
	public Future<?> cdrInsertFile()
	{
		Future<?> ret = null;
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

			ret = port.insertFileAsync(dataHandler, "test.jpg", futureResult ->
			{
				try
				{
					futureResult.get();
					clientFrame.appendToConsole(new StringBuilder("File has been successfully deposited into the CDR service."), Color.GREEN);
				}
				catch(Exception e)
				{
					clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder("File could not be inserted! "));
				}
			});
			clientFrame.appendToConsole(new StringBuilder("File has been sent to the CDR service. Waiting for a response..."),
					Color.BLACK);
		}
		catch(WebServiceException e)
		{
			clientFrame.appendToConsole(new StringBuilder("File not be deposited into the CDR service! Server is not accessible.").
					append(" Please try again later."), Color.RED);
		}
		catch(Exception e)
		{
			clientFrame.appendToConsole(new StringBuilder("File could not be deposited into the CDR service! Error is on the client's side."),
					Color.RED);
		}
		return ret;
	}

	//MMM: should be deleted, or left for testing purposes of sending a file
	@Deprecated
	public Future<?> cdrInsertAttachment()
	{
		Future<?> ret = null;
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

			ret = port.insertAttachmentAsync(att, "test.jpg", futureResult ->
			{
				try
				{
					futureResult.get();
					clientFrame.appendToConsole(new StringBuilder("File has been successfully deposited into the CDR service."), Color.GREEN);
				}
				catch(Exception e)
				{
					clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder("File could not be inserted! "));
				}
			});
			clientFrame.appendToConsole(new StringBuilder("File has been sent to the CDR service. Waiting for a response..."), Color.BLACK);
		}
		catch(WebServiceException e)
		{
			clientFrame.appendToConsole(new StringBuilder("File not be deposited into the CDR service! Server is not accessible.").
					append(" Please try again later."), Color.RED);
		}
		catch(Exception e)
		{
			clientFrame.appendToConsole(new StringBuilder("File could not be deposited into the CDR service! Error is on the client's side."),
					Color.RED);
		}
		return ret;
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
				clientFrame.appendToConsole(new StringBuilder("Search results have been successfully retrieved from the CDR service."),
						Color.GREEN);
			}
			catch(Exception e)
			{
				clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder("Search request could not be processed! "));
			}
		});
		clientFrame.appendToConsole(new StringBuilder("Search request has been sent to the CDR service."), Color.BLACK);
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
		if(enableStoringProperties)
		{
			saveProperties();
			clientFrame.saveProperties();
			storeProperties(true);
		}
		final MyParty myParty= getMyParty();
		try
		{
			if(myParty.getLocalUser() != null && myParty.getLocalUsername() != null)
				myParty.storeAllData();
		}
		catch(Exception e)
		{
			logger.error("Data could not be stored to the local data store!", e);
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
	}

	/**
	 * Registering My Party with the local datastore.
	 * @param username user's username
	 * @param password user's password
	 * @return user's secret key
	 * @throws DetailException if Party could not be registered
	 */
	public String localRegisterMyParty(String username, String password) throws DetailException
	{
		String secretKey = null;
		try
		{
			final String missingPartyField = myParty.getCoreParty().verifyParty();
			if(missingPartyField != null)
			{
				throw new DetailException(new StringBuilder("Request for the registration of My Party has been discarded because").
						append(" My Party is missing mandatory field: ").append(missingPartyField).
						append(". Please populate My Party data with all mandatory fields and try again.").toString());
			}
			else
			{
				clientFrame.appendToConsole(new StringBuilder("Request for the registration of My Party with the local data store has been sent."),
						Color.BLACK);
				myParty.setPartyID();
				secretKey = (String) MapperRegistry.getInstance().getMapper(RutaUser.class).
						registerUser(username, password, myParty.getCoreParty());
				myParty.setLocalUser(new RutaUser(username, password, secretKey));
				clientFrame.appendToConsole(new StringBuilder("My Party has been successfully registered with the local data store."),
						Color.GREEN);
			}
		}
		catch (DetailException e)
		{
			clientFrame.enablePartyMenuItems();
			throw e;
//			clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder("My Party has not been registered with the local data store! Please try again."));
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
			final String localUsername = myParty.getLocalUsername();
			localDeleteData();
			MapperRegistry.getInstance().getMapper(RutaUser.class).deleteUser(localUsername);
			properties.remove("username");
			properties.remove("password");
			storeProperties(false);
		}
		catch(Exception e)
		{
			clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder("There has been an error! My Party has not been deregistered from the local datastore! "));
		}
		finally
		{
			clientFrame.enablePartyMenuItems();
		}
	}

	/**
	 * Sends follow request to the CDR if the party to be followed is not My Party or is not among the
	 * following parties. If argument {@code partner} is set to {@code true} following party is set to be a
	 * {@code Business Parter}, otherwise a regular following {@code Party}.
	 * @param followingName name of the party to follow
	 * @param followingID ID of the party to follow
	 * @return {@link Future} representing the CDR response, that enables calling method to wait for its completion
	 * or {@code null} if no CDR request has been made during method invocation
	 * @throws DetailException due to the issues with the data store
	 */
	public Future<?> followParty(final String followingName, final String followingID) throws DetailException
	{
		Future<?> ret = null;
		if(followingID.equals(myParty.getPartyID()))
			clientFrame.appendToConsole(new StringBuilder("My Party is already in the following list."), Color.BLACK);
		else
		{
			final BusinessParty following = myParty.getFollowingParty(followingID);
			if(following == null)
				ret = cdrFollowParty(followingName, followingID);
			else if(following.isPartner())
				clientFrame.appendToConsole(new StringBuilder("Party ").append(followingName).
						append(" is already in the business partner list."), Color.BLACK);
			else
				clientFrame.appendToConsole(new StringBuilder("Party ").append(followingName).
						append(" is already in the following list."), Color.BLACK);
		}
		return ret;
	}

	/**
	 * Sends business partnership request to the CDR if the party to be followed is not My Party,is not among the
	 * following parties nor the request has already been sent for that party.
	 * @param requestedParty party that request should be sent to
	 * @return {@link Future} representing the CDR response, that enables calling method to wait for its completion
	 * or {@code null} if no CDR request has been made during method invocation
	 */
	public void requestPartnership(final PartyType requestedParty)
	{
		final String requestedName = InstanceFactory.
				getPropertyOrNull(requestedParty.getPartyNameAtIndex(0), PartyNameType::getNameValue);
		final String requestedID = InstanceFactory.
				getPropertyOrNull(requestedParty.getPartyIdentificationAtIndex(0), PartyIdentificationType::getIDValue);
		if(requestedID.equals(myParty.getPartyID()))
			clientFrame.appendToConsole(new StringBuilder("My Party is already in the following list."), Color.BLACK);
		else
		{
			final PartnershipRequest outboundRequest = myParty.getOutboundPartnershipRequest(requestedParty);
			if(outboundRequest != null &&
					(!outboundRequest.isResolved() || (outboundRequest.isResolved() && outboundRequest.isAccepted())))
				clientFrame.appendToConsole(new StringBuilder("Business Partnership Request had been already sent to the ").
						append(requestedName).append(" party. Because of the state of the current request another one is not alowed."),
						Color.BLACK);
			else
			{
				final PartnershipRequest inboundRequest = myParty.getInboundPartnershipRequest(requestedParty);
				if(inboundRequest != null &&
						(!inboundRequest.isResolved() || (inboundRequest.isResolved() && inboundRequest.isAccepted())))
					clientFrame.appendToConsole(new StringBuilder("Business Partnership Request had been already received ").
							append("from the ").append(requestedName).
							append(" party. Because of the state of the current request another one is not alowed."),
							Color.BLACK);
				else
				{
					BusinessParty following = myParty.getFollowingParty(requestedID);
					if(following == null || !following.isPartner())
					{
						final PartnershipRequest businessRequest = new PartnershipRequest();
						businessRequest.setID(UUID.randomUUID().toString());
						businessRequest.setIssueTime(InstanceFactory.getDate());
						businessRequest.setInbound(false);
						businessRequest.setRequesterParty(myParty.getCoreParty());
						businessRequest.setRequestedParty(requestedParty);
						cdrRequestPartnership(businessRequest);
					}
					else if(following.isPartner())
						clientFrame.appendToConsole(new StringBuilder("Party ").append(requestedName).
								append(" is already in the business partner list."), Color.BLACK);
					//				else
					//					clientFrame.appendToConsole(new StringBuilder("Party ").append(requestedName).
					//							append(" is already in the following list."), Color.BLACK);
				}
			}
		}
	}

	/**
	 * Sends a {@link PartnershipRequest Business Partnership Request} to the CDR service.
	 * @param businessRequest request to sent
	 * @return {@link Future} representing the CDR response, that enables calling method to wait for its completion
	 * or {@code null} if no CDR request has been made during method invocation
	 */
	public void cdrRequestPartnership(PartnershipRequest businessRequest)
	{
		try
		{
			Server port = getCDRPort();
			port.requestBusinessPartnershipAsync(businessRequest, future ->
			{
				String requestedName = businessRequest.getRequestedPartyName();
				try
				{
					future.get();
					myParty.excludePartnershipRequest(businessRequest.getRequestedParty());
					myParty.includeOutboundPartnershipRequest(businessRequest);
					clientFrame.appendToConsole( new StringBuilder("Business Partnership Request for the Party ").
							append(requestedName).append(" has been successfully deposited into the CDR service. Party ").
							append(requestedName).append(" should respond to it before any further actions on your behalf."),
							Color.GREEN);
				}
				catch(DetailException e)
				{
					clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder("Business Partnership request").
							append(" for the Party ").append(requestedName).
							append(" could not be successfully inserted to the local data store!"));
				}
				catch(Exception e)
				{
					clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder("Business Partnership Request").
							append(" for the Party ").append(requestedName).
							append(" could not be successfully deposited into the CDR service!"));
				}
			});

			clientFrame.appendToConsole(new StringBuilder("Business Partnership Request has been sent to the CDR service. Waiting for a response..."),
					Color.BLACK);
		}
		catch(WebServiceException e)
		{
			logger.error("Business partnership request has not been sent to the CDR service!" +
					" Server is not accessible. Exception is ", e);
			clientFrame.appendToConsole(new StringBuilder("Business partnership request has not been sent to the CDR service!").
					append(" Server is not accessible. Please try again later."), Color.RED);
		}
	}

	/**
	 * Sends a {@link PartnershipResponse Partnership Response} to the CDR service.
	 * @param businessResponse resposne to sent
	 * @return {@link Future} representing the CDR response, that enables calling method to wait for its completion
	 * or {@code null} if no CDR request has been made during method invocation
	 */
	public void cdrResponsePartnership(PartnershipResponse businessResponse)
	{
		try
		{
			Server port = getCDRPort();
			port.responseBusinessPartnershipAsync(businessResponse, future ->
			{
				String requestedName = businessResponse.getRequestedPartyName();
				try
				{
					future.get();
					//					myParty.includeBusinessPartnershipResponse(businessResponse);
					clientFrame.appendToConsole( new StringBuilder("Business Partnership Response for the Party ").
							append(requestedName).append(" has been successfully deposited into the CDR service. ").
							append("Get New Documents from the CDR to get a Business Partnership Resolution."),
							Color.GREEN);
				}
				//				catch(DetailException e)
				//				{
				//					clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder("Business Partnership Response ").
				//							append(" for the Party ").append(requestedName).
				//							append(" could not be successfully inserted to the local data store!"));
				//				}
				catch(Exception e)
				{
					clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder("Business Partnership Response").
							append(" for the Party ").append(requestedName).
							append(" could not be successfully deposited into the CDR service!"));
				}
			});

			clientFrame.appendToConsole(new StringBuilder("Business Partnership Response has been sent to the CDR service. Waiting for a response..."),
					Color.BLACK);
		}
		catch(WebServiceException e)
		{
			logger.error("Business partnership Resposne has not been sent to the CDR service!" +
					" Server is not accessible. Exception is ", e);
			clientFrame.appendToConsole(new StringBuilder("Business partnership Resposne has not been sent to the CDR service!").
					append(" Server is not accessible. Please try again later."), Color.RED);
		}
	}

	/**
	 * Sends Business Partnership Breakup request to the CDR if the request has not already been sent for that party,
	 * or the party is not a business partner of MyParty.
	 * @param requestedParty party that request should be sent to
	 */
	public void breakupPartnership(final PartyType requestedParty)
	{
		final String requestedName = InstanceFactory.
				getPropertyOrNull(requestedParty.getPartyNameAtIndex(0), PartyNameType::getNameValue);
		final String requestedID = InstanceFactory.
				getPropertyOrNull(requestedParty.getPartyIdentificationAtIndex(0), PartyIdentificationType::getIDValue);
		if(myParty.getPartnershipBreakup(requestedParty) != null)
			clientFrame.appendToConsole(new StringBuilder("Business Partnership Breakup had been already sent ").
					append("to the CDR service."), Color.BLACK);
		else
		{
			BusinessParty breakupParty = myParty.getFollowingParty(requestedID);
			if(breakupParty != null)
			{
				final PartnershipBreakup breakup = new PartnershipBreakup();
				breakup.setID(UUID.randomUUID().toString());
				breakup.setIssueTime(InstanceFactory.getDate());
				breakup.setRequesterParty(myParty.getCoreParty());
				breakup.setRequestedParty(requestedParty);
				cdrBreakupPartnership(breakup);
			}
			else
				clientFrame.appendToConsole(new StringBuilder("Party ").append(requestedName).
						append(" is not a Business Partner."), Color.BLACK);
		}
	}

	/**
	 * Sends a {@link PartnershipBreakup Partnership Breakup} to the CDR service.
	 * @param breakup breakup request to sent
	 */
	public void cdrBreakupPartnership(PartnershipBreakup breakup)
	{
		try
		{
			Server port = getCDRPort();
			port.breakupBusinessPartnershipAsync(breakup, future ->
			{
				String requestedName = breakup.getRequestedPartyName();
				try
				{
					future.get();
					myParty.includePartnershipBreakup(breakup);
					clientFrame.appendToConsole( new StringBuilder("Business Partnership Breakup with Party ").
							append(requestedName).append(" has been successfully processed by the CDR service. ").
							append("Get New Documents from the CDR to get a response document to it."),
							Color.GREEN);
				}
				catch(DetailException e)
				{
					clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder("Business Partnership Breakup").
							append(" with Party ").append(requestedName).
							append(" could not be successfully inserted in local data store!"));
				}
				catch(Exception e)
				{
					clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder("Business Partnership Breakup").
							append(" with Party ").append(requestedName).
							append(" could not be successfully processed by the CDR service!"));
				}
			});

			clientFrame.appendToConsole(new StringBuilder("Business Partnership Breakup has been sent to the CDR service. Waiting for a response..."),
					Color.BLACK);
		}
		catch(WebServiceException e)
		{
			logger.error("Business Partnership Breakup has not been sent to the CDR service!" +
					" Server is not accessible. Exception is ", e);
			clientFrame.appendToConsole(new StringBuilder("Business Partnership Breakup has not been sent to the CDR service!").
					append(" Server is not accessible. Please try again later."), Color.RED);
		}
	}

}