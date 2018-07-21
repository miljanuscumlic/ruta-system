package rs.ruta.client;

import java.awt.Color;
import java.awt.EventQueue;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nonnull;
import javax.swing.JOptionPane;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPBinding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sun.xml.ws.fault.ServerSOAPFaultException;

import oasis.names.specification.ubl.schema.xsd.applicationresponse_21.ApplicationResponseType;
import oasis.names.specification.ubl.schema.xsd.catalogue_21.CatalogueType;
import oasis.names.specification.ubl.schema.xsd.cataloguedeletion_21.CatalogueDeletionType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyIdentificationType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyNameType;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;
import oasis.names.specification.ubl.schema.xsd.order_21.OrderType;
import oasis.names.specification.ubl.schema.xsd.ordercancellation_21.OrderCancellationType;
import oasis.names.specification.ubl.schema.xsd.orderchange_21.OrderChangeType;
import oasis.names.specification.ubl.schema.xsd.orderresponse_21.OrderResponseType;
import oasis.names.specification.ubl.schema.xsd.orderresponsesimple_21.OrderResponseSimpleType;
import rs.ruta.ClientHandlerResolver;
import rs.ruta.client.correspondence.CatalogueCorrespondence;
import rs.ruta.client.correspondence.Correspondence;
import rs.ruta.client.correspondence.StateActivityException;
import rs.ruta.client.datamapper.ClientMapperRegistryFactory;
import rs.ruta.client.gui.RutaClientFrame;
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
import rs.ruta.common.datamapper.DatabaseException;
import rs.ruta.common.datamapper.DetailException;
import rs.ruta.common.datamapper.MapperRegistry;
import rs.ruta.services.RutaException;
import rs.ruta.services.CDRService;
import rs.ruta.services.DeleteCatalogueWithAppResponseResponse;
import rs.ruta.services.FindAllDocBoxDocumentIDsResponse;
import rs.ruta.services.FindDocBoxDocumentResponse;
import rs.ruta.services.FollowPartyResponse;
import rs.ruta.services.RegisterUserResponse;
import rs.ruta.services.SearchCatalogueResponse;
import rs.ruta.services.SearchPartyResponse;
import rs.ruta.services.Server;
import rs.ruta.services.UpdateCatalogueWithAppResponseResponse;
import rs.ruta.common.InstanceFactory;
import rs.ruta.common.PartnershipBreakup;
import rs.ruta.common.PartySearchCriterion;

public class RutaClient
{
	private static String packageList = "oasis.names.specification.ubl.schema.xsd.catalogue_21"; // colon separated package list
	final private static String defaultEndPoint = "http://ruta.sytes.net:9009/ruta-server-0.2.0-SNAPSHOT/CDR";
	private static String cdrEndPoint = defaultEndPoint;
	final private static String eclipseMonitorEndPoint = "http://localhost:7709/ruta-server-0.2.0-SNAPSHOT/CDR";
	private static int CONNECT_TIMEOUT = 0;
	private static int REQUEST_TIMEOUT = 0;
	private MyParty myParty;
	private Party CDRParty;
	private RutaClientFrame clientFrame;
	private static RutaVersion version = new RutaVersion("Client", "0.2.0", "0.1.0", null);
	private Properties properties;
	private List<BugReport> bugReports;
	public static Logger logger = LoggerFactory.getLogger("rs.ruta.client");
	private String initialUsername;
	private String initialPassword;
	/**
	 * Exception to be wrapped in StateActivityException and thrown to the state machine.
	 */
	private Exception exceptionRethrown = null;
	private boolean enableStoringProperties;

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
		logger.info(Messages.getString("RutaClient.7"));
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
						Messages.getString("RutaClient.10"),
								Messages.getString("RutaClient.11"), JOptionPane.PLAIN_MESSAGE);
				edtSync.release();
			});
			try
			{
				edtSync.acquire();
			}
			catch (InterruptedException e)
			{
				throw new DetailException(Messages.getString("RutaClient.12"), e);
			}

			final Party coreParty = myParty.getCoreParty();
			if(!myParty.hasCoreParty() || coreParty.verifyParty() != null)
				myParty.setCoreParty(clientFrame.showPartyDialog(coreParty, Messages.getString("RutaClient.13"), true, true));
			clientFrame.updateTitle(myParty.getCoreParty().getPartySimpleName());
			initialUsername = clientFrame.showLocalSignUpDialog(Messages.getString("RutaClient.14"), false);
			EventQueue.invokeLater(() ->
			{
				if(initialUsername != null)
				{
					JOptionPane.showMessageDialog(null, Messages.getString("RutaClient.15"), Messages.getString("RutaClient.16"),
							JOptionPane.PLAIN_MESSAGE);
					edtSync.release();
				}
			});
			try
			{
				edtSync.acquire();
				clientFrame.showCDRSignUpDialog(Messages.getString("RutaClient.17"));
			}
			catch (InterruptedException e)
			{
				throw new DetailException(Messages.getString("RutaClient.12"), e);
			}

		}
		else if(!isLocalUserRegistеred() && !myParty.checkLocalUser(initialUsername, initialPassword))
		{
			final AtomicBoolean loop = new AtomicBoolean(true);
			while(loop.get() && !clientFrame.showLocalLogInDialog(Messages.getString("RutaClient.19")))
			{
				EventQueue.invokeLater(() ->
				{
					int option = JOptionPane.showConfirmDialog(clientFrame, Messages.getString("RutaClient.20"),
							Messages.getString("RutaClient.21"), JOptionPane.YES_NO_OPTION);
					loop.set(option == JOptionPane.YES_OPTION);
					edtSync.release();
				});
				try
				{
					edtSync.acquire();
				}
				catch (InterruptedException e)
				{
					throw new DetailException(Messages.getString("RutaClient.12"), e);
				}
			}
			if(!loop.get())
			{
				success = false;
			}
		}
		return success;
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
					logger.info(Messages.getString("RutaClient.23"));
					shutdownApplication();
					logger.info(Messages.getString("RutaClient.24"));
					logger.info(Messages.getString("RutaClient.25"));
				}
				catch(Exception e)
				{
					logger.error(Messages.getString("RutaClient.26"), e);
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
				logger.warn(Messages.getString("RutaClient.0"), e);
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
		try(InputStream input = new FileInputStream("ruta.properties"))
		{
			properties.load(input);
		}
		catch (IOException | NullPointerException e)
		{
			JOptionPane.showMessageDialog(null, Messages.getString("RutaClient.37"),
					Messages.getString("RutaClient.38"), JOptionPane.INFORMATION_MESSAGE);
			logger.warn(Messages.getString("RutaClient.39") + e.getMessage());
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
			JOptionPane.showMessageDialog(null, Messages.getString("RutaClient.47"),
					Messages.getString("RutaClient.48"), JOptionPane.ERROR_MESSAGE);
			logger.warn(Messages.getString("RutaClient.49"));
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
			throw new DetailException(Messages.getString("RutaClient.56"));
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
				clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.57") ).append(missingPartyField).
						append(Messages.getString("RutaClient.58")), Color.RED);
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
						clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.59")),
								Color.GREEN);
						clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.60")), Color.BLACK);
						clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.61")), Color.BLACK);
					}
					catch(Exception e)
					{
						myParty.clearPartyID();
						clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder(Messages.getString("RutaClient.62")));
					}
					finally
					{
						clientFrame.enablePartyMenuItems();
					}
				});
				clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.63")), Color.BLACK);
			}
		}
		catch(WebServiceException e)
		{
			logger.error(Messages.getString("RutaClient.64"), e);
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.65")), Color.RED);
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
					clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.66")), Color.GREEN);
				}
				catch(Exception e)
				{
					clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder(Messages.getString("RutaClient.67")));
				}
				finally
				{
					clientFrame.enablePartyMenuItems();
				}
			});
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.68")), Color.BLACK);
		}
		catch(WebServiceException e)
		{
			logger.error(Messages.getString("RutaClient.69"), e);
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.70")), Color.RED);
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
		clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.71")), Color.BLACK);
		Semaphore sequential = cdrGetNewDocuments();
		try
		{
			sequential.acquire();
		}
		catch (InterruptedException e)
		{
			logger.error(Messages.getString("RutaClient.72"), e);
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
					clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.73")),
							Color.GREEN);
					clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.74")), Color.BLACK);
				}
				catch (InterruptedException | ExecutionException e)
				{
					clientFrame.processExceptionAndAppendToConsole(
							e, new StringBuilder(Messages.getString("RutaClient.75")));
				}
				catch(Exception e)
				{
					clientFrame.processExceptionAndAppendToConsole(
							e, new StringBuilder(Messages.getString("RutaClient.76")));
				}
				finally
				{
					clientFrame.enablePartyMenuItems();
				}
			});
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.77")), Color.BLACK);
		}
		catch(WebServiceException e)
		{
			logger.error(Messages.getString("RutaClient.78"), e);
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.79")), Color.RED);
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
				clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.94")), Color.BLACK);
				myParty.setCatalogue(catalogue);
				Server port = getCDRPort();
				String username = myParty.getCDRUsername();
				ret = port.updateCatalogueWithAppResponseAsync(username, catalogue, null);
				clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.95")),
						Color.BLACK);
				correspondence.updateDocumentStatus(documentReference, DocumentReference.Status.CLIENT_SENT);
			}
			catch(WebServiceException e)
			{
				correspondence.updateDocumentStatus(documentReference, DocumentReference.Status.CDR_DOWN);
				logger.error(Messages.getString("RutaClient.96"), e);
				clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.97")).
						append(Messages.getString("RutaClient.98")), Color.RED);
				clientFrame.enableCatalogueMenuItems();
			}
		}
		else
		{
			correspondence.updateDocumentStatus(documentReference, DocumentReference.Status.CLIENT_FAILED);
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.99")), Color.RED);
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
				clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.100")),
						Color.GREEN);
				positiveResponse = Boolean.TRUE;
			}
			else if(InstanceFactory.APP_RESPONSE_NEGATIVE.equals(responseCode))
			{
				clientFrame.appendToConsole(
						new StringBuilder(Messages.getString("RutaClient.101")),
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
				clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.103")), Color.BLACK);
				Server port = getCDRPort();
				String username = myParty.getCDRUsername();
				ret = port.deleteCatalogueWithAppResponseAsync(username, catalogueDeletion, null);
				clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.104")), Color.BLACK);
				correspondence.updateDocumentStatus(documentReference, DocumentReference.Status.CLIENT_SENT);
			}
			catch(WebServiceException e)
			{
				correspondence.updateDocumentStatus(documentReference, DocumentReference.Status.CDR_DOWN);
				logger.error(Messages.getString("RutaClient.105"), e);
				clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.106")), Color.RED);
				clientFrame.enableCatalogueMenuItems();
			}
		}
		else
		{
			correspondence.updateDocumentStatus(documentReference, DocumentReference.Status.CLIENT_FAILED);
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.107")),
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
				clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.108")), Color.GREEN);
				positiveResponse = Boolean.TRUE;
			}
			else if(InstanceFactory.APP_RESPONSE_NEGATIVE.equals(responseCode))
			{
				clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.109")),
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
		clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.111") + documentName + " " + documentID +
				Messages.getString("RutaClient.113")), Color.BLACK);
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
							Messages.getString("RutaClient.115")), Color.GREEN);
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
					Messages.getString("RutaClient.118")), Color.BLACK);
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
							Messages.getString("RutaClient.120"), exceptionCopy);
				}
			}
			catch (InterruptedException e)
			{
				throw new StateActivityException(Messages.getString("RutaClient.121"), e);
			}
		}
		catch(WebServiceException e)
		{
			correspondence.updateDocumentStatus(documentReference, DocumentReference.Status.CDR_DOWN);
			throw new StateActivityException(documentName + " " + documentID +
					Messages.getString("RutaClient.123"), e);
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

						StringBuilder msg = new StringBuilder(Messages.getString("RutaClient.146")).append(followingName).
								append(Messages.getString("RutaClient.138"));
						clientFrame.appendToConsole(msg, Color.GREEN);
					}
					else
						clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.146")).append(followingName).
								append(Messages.getString("RutaClient.140")), Color.RED);
				}
				catch(Exception e)
				{
					clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder(Messages.getString("RutaClient.146")).
							append(followingName).append(Messages.getString("RutaClient.142")));
				}
			});
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.143")),
					Color.BLACK);
		}
		catch(WebServiceException e)
		{
			logger.error(Messages.getString("RutaClient.144"), e);
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.145")), Color.RED);
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
					StringBuilder msg = new StringBuilder(Messages.getString("RutaClient.146") + followingName + Messages.getString("RutaClient.147"));
					clientFrame.appendToConsole(msg, Color.GREEN);
				}
				catch(Exception e)
				{
					clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder(Messages.getString("RutaClient.146")).append(followingName).
							append(Messages.getString("RutaClient.149")));
				}
			});
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.150")), Color.BLACK);
		}
		catch(WebServiceException e)
		{
			logger.error(Messages.getString("RutaClient.151"), e);
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.152")), Color.RED);
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
						String plural = docCount + Messages.getString("RutaClient.153");
						String there = Messages.getString("RutaClient.154");
						if(docCount == 1)
						{
							plural = docCount + Messages.getString("RutaClient.155");
							there = Messages.getString("RutaClient.156");
						}
						clientFrame.appendToConsole(new StringBuilder(there).append(plural).append(Messages.getString("RutaClient.157")), Color.BLACK);
						clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.158")).append(plural).append(Messages.getString("RutaClient.159")), Color.BLACK);
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
										clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.160")).append(docID).
												append(Messages.getString("RutaClient.161")), Color.RED);
								}
								catch (InterruptedException | ExecutionException e)
								{
									oneAtATime.release();
									clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder(Messages.getString("RutaClient.162")).
											append(docID).append(Messages.getString("RutaClient.163")));
									logger.error(new StringBuilder(Messages.getString("RutaClient.164")).
											append(docID).append(Messages.getString("RutaClient.165")).toString(), e);
								}
								catch (Exception e)
								{
									oneAtATime.release();
									final String exceptionMessage = e.getMessage();
									if(exceptionMessage != null)
									{
										if(exceptionMessage.contains(Messages.getString("RutaClient.166")))
										{
											try
											{
												port.distributeDocumentAsync(DocumentReceipt.newInstance(document));
											}
											catch (DetailException e1)
											{
											}
										}
										else if(exceptionMessage.contains(Messages.getString("RutaClient.167")))
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
									clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder(Messages.getString("RutaClient.168")).
											append(docID).append(Messages.getString("RutaClient.169")));
									logger.error(new StringBuilder(Messages.getString("RutaClient.170")).
											append(docID).append(Messages.getString("RutaClient.171")).toString(), e);
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
								plural = downCount + Messages.getString("RutaClient.172");
							else
								plural = Messages.getString("RutaClient.173");

							if(docCount - downCount != 1)
								failed = Messages.getString("RutaClient.174") + (docCount - downCount) + Messages.getString("RutaClient.175");
							else
								failed = Messages.getString("RutaClient.176");
						}
						if(failed != null)
							clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.177")).append(plural).
									append(".").append(failed),
									Color.BLACK);
						else
							clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.179")).append(plural).
									append("."), Color.BLACK);
					}
					else
						clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.181")), Color.GREEN);
				}
				catch(Exception e)
				{
					clientFrame.processExceptionAndAppendToConsole(e,
							new StringBuilder(Messages.getString("RutaClient.182")));
				}
				finally
				{
					sequential.release();
					clientFrame.enableGetDocumentsMenuItem();
				}
			});
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.183")).
					append(Messages.getString("RutaClient.184")), Color.BLACK);
		}
		catch(WebServiceException e)
		{
			logger.error(Messages.getString("RutaClient.185"), e);
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.186")), Color.RED);
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
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.187")).
					append(((DocumentReceipt) document).getIDValue()).
					append(Messages.getString("RutaClient.188")), Color.GREEN);
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
				partyName = Messages.getString("RutaClient.189");
			}
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.190")).append(partyName).
					append(" ").append(((DocumentReceipt) document).getIDValue() + Messages.getString("RutaClient.191")),
					Color.BLACK);
		}
		else if(documentClazz == CatalogueType.class)
		{
			createDocumentReceipt = false;
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.192")).append(docID).
					append(Messages.getString("RutaClient.193")), Color.GREEN);
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
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.195")).
					append(((CatalogueType) document).getIDValue()).
					append(Messages.getString("RutaClient.196")).append(partyName).append(Messages.getString("RutaClient.197")),
					Color.BLACK);
		}
		else if(documentClazz == PartyType.class)
		{
			createDocumentReceipt = false;
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.198")).append(docID).
					append(Messages.getString("RutaClient.199")), Color.GREEN);
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
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.146")).
					append(partyName).append(Messages.getString("RutaClient.202")), Color.BLACK);
		}
		else if(documentClazz == CatalogueDeletionType.class)
		{
			createDocumentReceipt = false;
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.203")).append(docID).
					append(Messages.getString("RutaClient.204")), Color.GREEN);
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
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.206")).
					append(((CatalogueDeletionType) document).getIDValue()).
					append(Messages.getString("RutaClient.207")).append(partyName).append(Messages.getString("RutaClient.208")),
					Color.BLACK);
		}
		else if(documentClazz == DeregistrationNotice.class)
		{
			createDocumentReceipt = false;
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.209")).append(docID).
					append(Messages.getString("RutaClient.210")), Color.GREEN);
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
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.146")).append(partyName).append(Messages.getString("RutaClient.213")), Color.BLACK);
		}
		else if(documentClazz == OrderType.class)
		{
			final OrderType order = (OrderType) document;
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.214")).append(order.getIDValue()).
					append(Messages.getString("RutaClient.215")), Color.GREEN);
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
				partyName = Messages.getString("RutaClient.216");
			}
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.217")).append(order.getIDValue()).
					append(Messages.getString("RutaClient.218")).append(partyName).
					append(Messages.getString("RutaClient.219")),
					Color.BLACK);
		}
		else if(documentClazz == OrderResponseType.class)
		{
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.220")).
					append(((OrderResponseType) document).getIDValue()).
					append(Messages.getString("RutaClient.221")), Color.GREEN);
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
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.223")).
					append(((OrderResponseType) document).getIDValue()).
					append(Messages.getString("RutaClient.224")).append(partyName).
					append(Messages.getString("RutaClient.225")),
					Color.BLACK);
		}
		else if(documentClazz == OrderResponseSimpleType.class)
		{
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.226")).
					append(((OrderResponseSimpleType) document).getIDValue()).
					append(Messages.getString("RutaClient.227")), Color.GREEN);
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
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.229")).
					append(((OrderResponseSimpleType) document).getIDValue()).
					append(Messages.getString("RutaClient.230")).append(partyName).
					append(Messages.getString("RutaClient.231")),
					Color.BLACK);
		}
		else if(documentClazz == OrderChangeType.class)
		{
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.232")).
					append(((OrderChangeType) document).getIDValue()).
					append(Messages.getString("RutaClient.233")), Color.GREEN);
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
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.235")).
					append(((OrderChangeType) document).getIDValue()).
					append(Messages.getString("RutaClient.236")).append(partyName).
					append(Messages.getString("RutaClient.237")),
					Color.BLACK);
		}
		else if(documentClazz == OrderCancellationType.class)
		{
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.238")).
					append(((OrderCancellationType) document).getIDValue()).
					append(Messages.getString("RutaClient.239")), Color.GREEN);
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
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.241")).
					append(((OrderCancellationType) document).getIDValue()).append(Messages.getString("RutaClient.242")).append(partyName).
					append(Messages.getString("RutaClient.243")),
					Color.BLACK);
		}
		else if(documentClazz == ApplicationResponseType.class)
		{
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.244")).
					append(((ApplicationResponseType) document).getIDValue()).
					append(Messages.getString("RutaClient.245")), Color.GREEN);
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
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.247")).
					append(((ApplicationResponseType) document).getIDValue()).append(Messages.getString("RutaClient.248")).append(partyName).
					append(Messages.getString("RutaClient.249")),
					Color.BLACK);
		}
		else if(documentClazz == InvoiceType.class)
		{
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.250")).
					append(((InvoiceType) document).getIDValue()).
					append(Messages.getString("RutaClient.251")), Color.GREEN);
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
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.253")).
					append(((InvoiceType) document).getIDValue()).
					append(Messages.getString("RutaClient.254")).append(partyName).
					append(Messages.getString("RutaClient.255")),
					Color.BLACK);
		}
		else if(documentClazz == PartnershipRequest.class)
		{
			createDocumentReceipt = false;
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.256")).
					append(((PartnershipRequest) document).getIDValue()).
					append(Messages.getString("RutaClient.257")), Color.GREEN);
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
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.259")).
					append(((PartnershipRequest) document).getIDValue()).append(Messages.getString("RutaClient.260")).append(partyName).
					append(Messages.getString("RutaClient.261")),
					Color.BLACK);
		}
		else if(documentClazz == PartnershipResolution.class)
		{
			createDocumentReceipt = false;
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.262")).
					append(((PartnershipResolution) document).getIDValue()).
					append(Messages.getString("RutaClient.263")), Color.GREEN);
			myParty.processDocBoxPartnershipResolution((PartnershipResolution) document);
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.264")).
					append(((PartnershipResolution) document).getIDValue()).
					append(Messages.getString("RutaClient.265")),
					Color.BLACK);
		}
		else if(documentClazz == PartnershipBreakup.class)
		{
			createDocumentReceipt = false;
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.266")).
					append(((PartnershipBreakup) document).getIDValue()).
					append(Messages.getString("RutaClient.267")), Color.GREEN);
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
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.269")).append(partyName).
					append(" ").append(((PartnershipBreakup) document).getIDValue()).
					append(Messages.getString("RutaClient.271")),
					Color.BLACK);
		}
		else
		{
			createDocumentReceipt = false;
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.272")).append(docID).
					append(Messages.getString("RutaClient.273")).append(document.getClass().getSimpleName()).
					append(Messages.getString("RutaClient.274")),
					Color.BLACK);
		}
		if(createDocumentReceipt)
			documentReceipt = DocumentReceipt.newInstance(document);

		return documentReceipt;
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
			CDRParty.setPartySimpleName(Messages.getString("RutaClient.400"));
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
		final CDRService service = new CDRService();
		service.setHandlerResolver(new ClientHandlerResolver(myParty));
		final Server port = service.getCDRPort();
		//temporary setting for TCP/IP Monitor in Eclipse
		//bindEclipseEndPoint(port);
		if(!defaultEndPoint.equals(cdrEndPoint))
			bindCDREndPoint(port);
		final BindingProvider bindingProvider = (BindingProvider) port;
		//Set timeout until a connection is established
		bindingProvider.getRequestContext().put(com.sun.xml.ws.client.BindingProviderProperties.CONNECT_TIMEOUT, CONNECT_TIMEOUT);
		//Set timeout until the response is received
		bindingProvider.getRequestContext().put(com.sun.xml.ws.client.BindingProviderProperties.REQUEST_TIMEOUT, REQUEST_TIMEOUT);

		final SOAPBinding binding = (SOAPBinding) bindingProvider.getBinding();
		binding.setMTOMEnabled(true);

		return port;
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
							clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.285")).append(newSearch.getSearchName()).append("\"."), Color.GREEN);
							int option = JOptionPane.showConfirmDialog(clientFrame, Messages.getString("RutaClient.287") +
									newSearch.getSearchName() + Messages.getString("RutaClient.288"), Messages.getString("RutaClient.289"),
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
									clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.290")).append(newSearch.getSearchName()).
											append(Messages.getString("RutaClient.291")), Color.GREEN);
								}
						}
						else
						{
							if(exist)
								myParty.updateCatalogueSearch(newSearch);
							else
								myParty.addCatalogueSearch(newSearch);
							clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.292")).
									append(newSearch.getSearchName()).append(Messages.getString("RutaClient.293")),
									Color.GREEN);
						}
					}
					catch(Exception e)
					{
						clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder(Messages.getString("RutaClient.294")));
					}
					finally
					{
						clientFrame.enableSearchMenuItems();
					}
				});
				clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.295")).append(search.getSearchName()).
						append(Messages.getString("RutaClient.296")), Color.BLACK);
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
							clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.297")).
									append(newSearch.getSearchName()).append("\"."), Color.GREEN);
							int option = JOptionPane.showConfirmDialog(clientFrame, Messages.getString("RutaClient.299") +
									newSearch.getSearchName() + Messages.getString("RutaClient.300"), Messages.getString("RutaClient.301"),
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
									clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.302")).
											append(newSearch.getSearchName()).
											append(Messages.getString("RutaClient.303")), Color.GREEN);
								}
						}
						else
						{
							if(exist)
								myParty.updatePartySearch(newSearch);
							else
								myParty.addPartySearch(newSearch);
							clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.304")).
									append(newSearch.getSearchName()).append(Messages.getString("RutaClient.305")),
									Color.GREEN);
						}
					}
					catch(Exception e)
					{
						clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder(Messages.getString("RutaClient.306")));
					}
					finally
					{
						clientFrame.enableSearchMenuItems();
					}
				});
				clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.307")).append(search.getSearchName()).
						append(Messages.getString("RutaClient.308")), Color.BLACK);
			}
		}
		catch(WebServiceException e)
		{
			logger.error(Messages.getString("RutaClient.309") + search.getSearchName() +
				Messages.getString("RutaClient.310"), e);
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.311")).append(search.getSearchName()).
					append(Messages.getString("RutaClient.312")), Color.RED);
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
			logger.error(Messages.getString("RutaClient.313"), e);
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.314")),
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
					clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.315")),
							Color.GREEN);
				}
				catch(Exception e)
				{
					clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder(Messages.getString("RutaClient.316")));
				}
			});
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.317")),
					Color.BLACK);
		}
		catch(WebServiceException e)
		{
			logger.error(Messages.getString("RutaClient.318"), e);
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.319")),
					Color.RED);
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
					clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.324")),
							Color.GREEN);
				}
				catch(Exception e)
				{
					clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder(Messages.getString("RutaClient.325")));
				}
			});
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.326")),
					Color.BLACK);
		}
		catch(WebServiceException e)
		{
			logger.error(Messages.getString("RutaClient.327"), e);
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.328")), Color.RED);
		}
		return ret;
	}

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
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.329")), Color.BLACK);
		}
		catch(WebServiceException e)
		{
			logger.error(Messages.getString("RutaClient.330"), e);
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.331")), Color.RED);
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
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.329")), Color.BLACK);
		}
		catch(WebServiceException e)
		{
			logger.error(Messages.getString("RutaClient.333"), e);
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.331")), Color.RED);
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
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.335")),
					Color.BLACK);
		}
		catch(WebServiceException e)
		{
			logger.error(Messages.getString("RutaClient.336"), e);
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.337")), Color.RED);
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
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.338")), Color.BLACK);
		}
		catch(WebServiceException e)
		{
			logger.error(Messages.getString("RutaClient.339"), e);
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.340")),Color.RED);
		}
		return ret;
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
			logger.error(Messages.getString("RutaClient.351"), e);
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
				throw new DetailException(new StringBuilder(Messages.getString("RutaClient.352")).append(missingPartyField).
						append(Messages.getString("RutaClient.353")).toString());
			}
			else
			{
				clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.354")),
						Color.BLACK);
				myParty.setPartyID();
				secretKey = (String) MapperRegistry.getInstance().getMapper(RutaUser.class).
						registerUser(username, password, myParty.getCoreParty());
				myParty.setLocalUser(new RutaUser(username, password, secretKey));
				clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.355")),
						Color.GREEN);
			}
		}
		catch (DetailException e)
		{
			clientFrame.enablePartyMenuItems();
			throw e;
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
			properties.remove(Messages.getString("username"));
			properties.remove(Messages.getString("password"));
			storeProperties(false);
		}
		catch(Exception e)
		{
			clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder(Messages.getString("RutaClient.358")));
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
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.359")), Color.BLACK);
		else
		{
			final BusinessParty following = myParty.getFollowingParty(followingID);
			if(following == null)
				ret = cdrFollowParty(followingName, followingID);
			else if(following.isPartner())
				clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.146")).append(followingName).
						append(Messages.getString("RutaClient.361")), Color.BLACK);
			else
				clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.146")).append(followingName).
						append(Messages.getString("RutaClient.363")), Color.BLACK);
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
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.359")), Color.BLACK);
		else
		{
			final PartnershipRequest outboundRequest = myParty.getOutboundPartnershipRequest(requestedParty);
			if(outboundRequest != null &&
					(!outboundRequest.isResolved() || (outboundRequest.isResolved() && outboundRequest.isAccepted())))
				clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.365")).
						append(requestedName).append(Messages.getString("RutaClient.366")),
						Color.BLACK);
			else
			{
				final PartnershipRequest inboundRequest = myParty.getInboundPartnershipRequest(requestedParty);
				if(inboundRequest != null &&
						(!inboundRequest.isResolved() || (inboundRequest.isResolved() && inboundRequest.isAccepted())))
					clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.367")).append(requestedName).
							append(Messages.getString("RutaClient.368")),
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
						clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.146")).append(requestedName).
								append(Messages.getString("RutaClient.370")), Color.BLACK);
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
					clientFrame.appendToConsole( new StringBuilder(Messages.getString("RutaClient.371")).
							append(requestedName).append(Messages.getString("RutaClient.372")).
							append(requestedName).append(Messages.getString("RutaClient.373")),
							Color.GREEN);
				}
				catch(DetailException e)
				{
					clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder(Messages.getString("RutaClient.374")).append(requestedName).
							append(Messages.getString("RutaClient.375")));
				}
				catch(Exception e)
				{
					clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder(Messages.getString("RutaClient.376")).append(requestedName).
							append(Messages.getString("RutaClient.377")));
				}
			});

			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.378")),
					Color.BLACK);
		}
		catch(WebServiceException e)
		{
			logger.error(Messages.getString("RutaClient.379"), e);
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.380")), Color.RED);
		}
	}

	/**
	 * Sends a {@link PartnershipResponse Partnership Response} to the CDR service.
	 * @param businessResponse response to sent
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
					clientFrame.appendToConsole( new StringBuilder(Messages.getString("RutaClient.381")).
							append(requestedName).append(Messages.getString("RutaClient.382")),
							Color.GREEN);
				}
				catch(Exception e)
				{
					clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder(Messages.getString("RutaClient.383")).append(requestedName).
							append(Messages.getString("RutaClient.384")));
				}
			});

			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.385")),
					Color.BLACK);
		}
		catch(WebServiceException e)
		{
			logger.error(Messages.getString("RutaClient.386"), e);
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.387")), Color.RED);
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
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.388")), Color.BLACK);
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
				clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.146")).append(requestedName).
						append(Messages.getString("RutaClient.390")), Color.BLACK);
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
					clientFrame.appendToConsole( new StringBuilder(Messages.getString("RutaClient.391")).
							append(requestedName).append(Messages.getString("RutaClient.392")),
							Color.GREEN);
				}
				catch(DetailException e)
				{
					clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder(Messages.getString("RutaClient.393")).append(requestedName).
							append(Messages.getString("RutaClient.394")));
				}
				catch(Exception e)
				{
					clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder(Messages.getString("RutaClient.395")).append(requestedName).
							append(Messages.getString("RutaClient.396")));
				}
			});

			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.397")),
					Color.BLACK);
		}
		catch(WebServiceException e)
		{
			logger.error(Messages.getString("RutaClient.398"), e);
			clientFrame.appendToConsole(new StringBuilder(Messages.getString("RutaClient.399")), Color.RED);
		}
	}
}