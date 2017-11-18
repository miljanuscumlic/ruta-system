package rs.ruta.client;

import java.awt.Color;
import java.awt.EventQueue;
import java.io.*;
import java.math.BigDecimal;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.Future;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;
import javax.xml.bind.*;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;

import com.helger.commons.state.ESuccess;
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
import rs.ruta.*;
import rs.ruta.client.datamapper.*;
import rs.ruta.common.RutaVersion;
import rs.ruta.common.SearchCriterion;
import rs.ruta.services.*;

public class Client implements RutaNode
{
	private static String packageList = "oasis.names.specification.ubl.schema.xsd.catalogue_21"; // colon separated package list
	final private static String defaultEndPoint = "http://localhost:8080/ruta-server-0.0.1/CDR";
	private static String cdrEndPoint = defaultEndPoint;
	final private static String eclipseMonitorEndPoint = "http://localhost:7777/ruta-server-0.0.1/CDR";
	private MyParty myParty;
	private MyPartyXMLFileMapper<MyParty> partyDataMapper;//MMM: it should be one data mapper - for the database, and many finders - extended classes for each database table
	private Party CDRParty;
	private CDRPartyTypeXMLFileMapper<Party> CDRPartyDataMapper;
	private ClientFrame frame;
	private Preferences prefNode;
	private static RutaVersion version = new RutaVersion("Client", "0.0.1", "0.0.1", null);

	public Client()
	{
		myParty = new MyParty();
		/*		myParty.setItemDataMapper("client-products.dat");
		myParty.setDirtyCatalogue(prefNode.getBoolean("dirtyCatalogue", false));*/
		CDRParty = getCDRParty();
		partyDataMapper = new MyPartyXMLFileMapper<MyParty>(Client.this, "myparty.xml");
		CDRPartyDataMapper = new CDRPartyTypeXMLFileMapper<Party>(Client.this, "cdr.xml");
		prefNode = Preferences.userNodeForPackage(this.getClass());
	}

	/**Gets the {@link Version} object describing the version of the {@code Ruta Client} application
	 * @return {@code Version} object of the {@code Ruta Client} application
	 */
	public static RutaVersion getVersion() { return version;}

	/**Sets the {@link Version} object describing the version of the {@code Ruta Client} application
	 * @param version {@code Version} object of the {@code Ruta Client} application
	 */
	public static void setVersion(RutaVersion version) { Client.version = version; }

	/** Initializes fields of Client object from local data store.
	 * @throws JAXBException if importing data from the data store is unsuccessful
	 */
	public void preInitialize() throws JAXBException
	{
		// trying to load the party data from the XML file
		ArrayList<MyParty> parties = (ArrayList<MyParty>) partyDataMapper.findAll();
		//		myParty = new PartyType(); // MMM: complete empty PartyType as alternative to next statement - this line is for test purposes
		//		myParty = InstanceFactory.newInstance(PartyType.class); // Producing StackOverflowException for PartyType.class object
		//		myParty = InstanceFactory.newInstancePartyType(); // instatiation of new partialy empty PartyType object with not null fields used in this version of the Ruta
		//		myParty = InstanceFactory.newInstance(PartyType.class, 1);
		loadPreferences();
		if(parties.size() != 0)
		{
			myParty = parties.get(0);
			myParty.setItemDataMapper("client-products.dat");
//			myParty.setDirtyCatalogue(prefNode.getBoolean("dirtyCatalogue", false));
			Search.setSearchNumber(myParty.getSearchNumber());
		}

		//MMM: this could/should be lazy called when needed, not at start
		/*		ArrayList<Party> CDRParties = (ArrayList<Party>) CDRPartyDataMapper.findAll();

		if(CDRParties.size() != 0)
			InstanceFactory.copyInstance(CDRParties.get(0), CDRParty);*/
	}

	/**If not already initialized in the preInitialize method, shows dialog for inputing Party data.
	 */
	public void initialize()
	{
		if(! myParty.hasCoreParty())
			//		if(parties.size() == 0)
		{
			myParty.setItemDataMapper("client-products.dat");
//			myParty.setDirtyCatalogue(prefNode.getBoolean("dirtyCatalogue", false));
			myParty.setCoreParty(frame.showPartyDialog(myParty.getCoreParty(), "My Party")); //displaying My Party Data dialog
			//			insertMyParty();
		}
		frame.updateTitle(myParty.getCoreParty().getSimpleName());
		/*		else
		{
			// trying to load the party data from the XML file
						ArrayList<MyParty> parties = (ArrayList<MyParty>) partyDataMapper.findAll();

			//InstanceFactory.copyInstance(parties.get(0), myParty); // MMM: check why I am coping the party into the new object
			myParty = parties.get(0);
			myParty.setItemDataMapper("client-products.dat");
			myParty.setDirtyCatalogue(prefNode.getBoolean("dirtyCatalogue", false));
		}*/
	}

	/**
	 * Saves the value of variables that should be kept beetwen two program invocations
	 */
	public void savePreferences()
	{
		prefNode.put("cdrEndPoint", Client.cdrEndPoint);
//		prefNode.putBoolean("dirtyCatalogue", myParty.isDirtyCatalogue());
	}

	public void loadPreferences()
	{
		Client.cdrEndPoint = prefNode.get("cdrEndPoint", Client.defaultEndPoint);
	}

	/**
	 * Inserts My Party data to the local data store.
	 * @throws Exception if data could not be inserted in the data store
	 */
	public void insertMyParty() throws Exception
	{
		myParty.setSearchNumber(Search.getSearchNum());
		getPartyDataMapper().insertAll();
		//MMM: inserting in embedded exist database - not working at the moment
		//MapperRegistry.getMapper(MyParty.class);
	}

	/**
	 * Inserts CDR Party data to the data store
	 * @throws Exception
	 */
	@Deprecated
	public void insertCDRParty() throws Exception
	{
		getCDRPartyDataMapper().insertAll();
	}

	/**Sends request for registration to the Central Data Repository.
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
				StringBuilder msg = new StringBuilder("My Party has not been registered with the CDR service! ");
				try
				{
					RegisterUserResponse response = futureUser.get();
					String key = response.getReturn();
					myParty.setUsername(username);
					myParty.setPassword(password);
					myParty.setSecretKey(key);
					frame.appendToConsole("Party has been successfully registered with the CDR service."
							+ " Please synchronise My Party with the CDR service to be able to use it.", Color.GREEN);
					/*					port.insertPartyAsync(username, party, futureParty ->
					{
						StringBuilder msg1 = new StringBuilder("My Party has not been registered with the CDR service! ");
						try
						{
							InsertPartyResponse res = futureParty.get();
							String partyID = res.getReturn();
							frame.appendToConsole("Party has been successfully registered with the CDR service.", Color.GREEN);
							myParty.getCoreParty().setPartyID(partyID);
							myParty.setDirtyMyParty(false);
						}
						catch (Exception e)
						{
														//delete set properties after registerUserAsync call
							myParty.setUsername(null);
							myParty.setPassword(null);
							myParty.setSecretKey(null);

							msg1.append("Server responds: ");
							Throwable cause = e.getCause();
							if(cause instanceof RutaException)
								msg1.append(cause.getMessage()).append(" ").append(((RutaException) cause).getFaultInfo().getDetail());
							else
								msg1.append(trimSOAPFaultMessage(cause.getMessage()));
							frame.appendToConsole(msg1.toString(), Color.RED);
						}
					});*/
				}
				catch (Exception e)
				{
					msg.append("Server responds: ");
					Throwable cause = e.getCause();
					if(cause instanceof RutaException)
						msg.append(cause.getMessage()).append(" ").append(((RutaException) cause).getFaultInfo().getDetail());
					else
						msg.append(trimSOAPFaultMessage(cause.getMessage()));
					frame.appendToConsole(msg.toString(), Color.RED);
				}
				finally
				{
					frame.enablePartyMenuItems();
				}
			});
			frame.appendToConsole("Request for the registration of My Party has been sent to the CDR service. Waiting for response...", Color.BLACK);
		}
		catch(WebServiceException e) //might be thrown by getServicePort
		{
			frame.appendToConsole("My Party has not been registered with the CDR service!"
					+ " Server is not accessible. Please try again later.", Color.RED);
		}
	}

	/**Synchronise My Party data with the CDR service. Method sends the data
	 * if they have been changed since the last synchronisation.
	 */
	public void cdrSynchroniseMyParty()
	{
		if(myParty.isRegisteredWithCDR()) // update
		{
			cdrUpdateMyParty();
			myParty.updateMyself();
			frame.repaintTabbedPane();
		}
		else // first time input
			cdrInsertMyParty();
	}

	/**Inserts My Party data to the CDR service. This method is called only ones, after registration of
	 * My Party with the CDR service.
	 */
	private void cdrInsertMyParty()
	{
		try
		{
			Server port = getCDRPort();
			port.insertPartyAsync(myParty.getUsername(), myParty.getCoreParty(), futureParty ->
			{
				StringBuilder msg = new StringBuilder("My Party has not been registered with the CDR service! ");
				try
				{
					InsertPartyResponse res = futureParty.get();
					String partyID = res.getReturn();
					myParty.getCoreParty().setPartyID(partyID);
					myParty.setDirtyMyParty(false);
					frame.appendToConsole("My Party has been successfully synchronised with the CDR service.", Color.GREEN);
				}
				catch (Exception e)
				{
					msg.append("Server responds: ");
					Throwable cause = e.getCause();
					if(cause instanceof RutaException)
						msg.append(cause.getMessage()).append(" ").append(((RutaException) cause).getFaultInfo().getDetail());
					else
						msg.append(trimSOAPFaultMessage(cause.getMessage()));
					frame.appendToConsole(msg.toString(), Color.RED);
				}
				finally
				{
					frame.enablePartyMenuItems();
				}
			});
			frame.appendToConsole("Request for the synchronisation of My Party has been sent to the CDR service. Waiting for response...", Color.BLACK);
		}
		catch(WebServiceException e) //might be thrown by getServicePort
		{
			frame.appendToConsole("My Party has not been synchronised with the CDR service!"
					+ " Server is not accessible. Please try again later.", Color.RED);
		}
	}

	/**Update My Party data with the CDR service.
	 *
	 */
	private void cdrUpdateMyParty()
	{
		try
		{
			Server port = getCDRPort();
			port.updatePartyAsync(myParty.getUsername(), myParty.getCoreParty(), future ->
			{
				String msg = "My Party has not been synchronised with the CDR service! ";
				try
				{
					UpdatePartyResponse response = future.get();
					myParty.setDirtyMyParty(false);
					frame.appendToConsole("My Party has been successfully updated with the CDR service.", Color.GREEN);
				}
				catch (Exception e)
				{
					msg += "Server responds: ";
					Throwable cause = e.getCause();
					if(cause instanceof RutaException)
						msg += cause.getMessage() + " " + ((RutaException) cause).getFaultInfo().getDetail();
					else
						msg += trimSOAPFaultMessage(cause.getMessage());
					frame.appendToConsole(msg, Color.RED);
				}
				finally
				{
					frame.enablePartyMenuItems();
				}
			});
			frame.appendToConsole("Request for the synchronisation of My Party has been sent to the CDR service. Waiting for response...", Color.BLACK);
		}
		catch(WebServiceException e) //might be thrown by getServicePort
		{
			frame.appendToConsole("My Party has not been synchronised with the CDR service!"
					+ " Server is not accessible. Please try again later.", Color.RED);
		}
	}

	/**Deregister My Party from the CDR service. All documents corresponding to the My Party deposited in the CDR database
	 * are deleted from the CDR service.
	 */
	public void cdrDeregisterMyParty()
	{
		try
		{
			Server port = getCDRPort();
			port.deleteUserAsync(myParty.getUsername(), future ->
			{
				StringBuilder msg = new StringBuilder("My Party has not been deleted from the CDR service! ");
				try
				{
					DeleteUserResponse response = future.get();
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
					frame.repaintTabbedPane(); //MMM: shoould be called method for repainting whole frame - to be implemented
					frame.appendToConsole("My Party has been successfully deregistered from the CDR service.", Color.GREEN);
				}
				catch (Exception e)
				{
					msg.append("Server responds: ");
					Throwable cause = e.getCause();
					if(cause instanceof RutaException)
						msg.append(cause.getMessage()).append(" ").append(((RutaException) cause).getFaultInfo().getDetail());
					else
						msg.append(trimSOAPFaultMessage(cause.getMessage()));
					frame.appendToConsole(msg.toString(), Color.RED);
				}
				finally
				{
					frame.enablePartyMenuItems();
				}
			});
			frame.appendToConsole("Request for deregistration of My Party has been sent to the CDR service. Waiting for response...", Color.BLACK);
		}
		catch(WebServiceException e) //might be thrown by getServicePort
		{
			frame.appendToConsole("My Party has not been deregistered from the CDR service!"
					+ " Server is not accessible. Please try again later.", Color.RED);
		}
	}

	/**Synchronise Catalogue with the CDR service. If catalogue is empty calls deleteCatalogue method.
	 * Method sends the catalogue if it is nonempty and has been changed since the last synchronisation
	 * with the CDR service.
	 */
	void cdrSynchroniseMyCatalogue()
	{
		if(myParty.getProductCount() == 0) // delete My Catalogue from CDR
			cdrDeleteMyCatalogue();
		else
		{
			if(myParty.isInsertMyCatalogue() == true) //first time sending catalogue
				cdrInsertMyCatalogue();
			else
				cdrUpdateMyCatalogue();
		}
	}

	/**Inserts My Catalogue for the first time in the CDR service. Updates are done with the
	 * <code>cdrUpdateMyCatalogue</code> method.
	 * @see Client#cdrUpdateMyCatalogue <code>cdrUpdateMyCatalogue</code>
	 */
	private void cdrInsertMyCatalogue()
	{
		try
		{
			// MMM: maybe JAXBContext should be private class field, if it is used from multiple class methods
			JAXBContext jc = JAXBContext.newInstance("oasis.names.specification.ubl.schema.xsd.catalogue_21"); //packageList

			Marshaller m = jc.createMarshaller();
			//m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			//creating Catalogue document
			CatalogueType catalogue = myParty.createCatalogue(CDRParty);
			if(catalogue != null)
			{
				Server port = getCDRPort();
				String username = myParty.getUsername();
				//port.putDocument(catalogue);
				port.insertCatalogueAsync(username, catalogue, future ->
				{
					StringBuilder msg = new StringBuilder("My Catalogue has not been deposited to the CDR service! ");
					try
					{
						InsertCatalogueResponse response =  future.get();
						frame.appendToConsole("My Catalogue has been successfully deposited to the CDR service.", Color.GREEN);
						myParty.setDirtyCatalogue(false);
						myParty.setInsertMyCatalogue(false);
						myParty.followMyself();
						frame.repaintTabbedPane(); //MMM: shoould be called method for repainting whole frame - to be implemented
						frame.appendToConsole("My party has been added to the Following parties.", Color.BLACK);
					}
					catch (Exception e)
					{
						msg.append("Server responds: ");
						Throwable cause = e.getCause();
						if(cause instanceof RutaException)
							msg.append(cause.getMessage()).append(" ").append(((RutaException) cause).getFaultInfo().getDetail());
						else
							msg.append(trimSOAPFaultMessage(cause.getMessage()));
						frame.appendToConsole(msg.toString(), Color.RED);
					}
					finally
					{
						frame.enableCatalogueMenuItems();
					}
				});
				frame.appendToConsole("My Catalogue has been sent to the CDR service. Waiting for response...", Color.BLACK);

/*				//creating XML document - for test purpose only
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
				}*/
			}
			else
			{
				frame.appendToConsole("My Catalogue has not been sent to the CDR service because it is malformed. "
						+ "All catalogue items should have a name and catalogue has to have at least one item.", Color.RED);
			}
		}
		catch (JAXBException e)
		{
			e.printStackTrace();
		}
		catch(WebServiceException e) //might be thrown by getServicePort
		{
			frame.appendToConsole("My Catalogue has not been deposited to the CDR service!"
					+ " Server is not accessible. Please try again later.", Color.RED);
		}
	}

	/**Updates My Catalogue in the CDR service.
	 */
	void cdrUpdateMyCatalogue()
	{
		try
		{
			// MMM: maybe JAXBContext should be private class field, if it is used from multiple class methods
			JAXBContext jc = JAXBContext.newInstance("oasis.names.specification.ubl.schema.xsd.catalogue_21"); //packageList

			Marshaller m = jc.createMarshaller();
			//m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			//creating Catalogue document
			CatalogueType catalogue = myParty.createCatalogue(CDRParty);
			if(catalogue != null)
			{
				Server port = getCDRPort();
				String username = myParty.getUsername();

				port.updateCatalogueAsync(username, catalogue, future ->
				{
					StringBuilder msg = new StringBuilder("My Catalogue has not been updated by the CDR service! ");
					try
					{
						UpdateCatalogueResponse response =  future.get();
						myParty.setDirtyCatalogue(false);
						frame.appendToConsole("My Catalogue has been successfully updated by the CDR service.", Color.GREEN);
					}
					catch (Exception e)
					{
						msg.append("Server responds: ");
						Throwable cause = e.getCause();
						if(cause instanceof RutaException)
							msg.append(cause.getMessage()).append(" ").append(((RutaException) cause).getFaultInfo().getDetail());
						else
							msg.append(trimSOAPFaultMessage(cause.getMessage()));
						frame.appendToConsole(msg.toString(), Color.RED);
					}
					finally
					{
						frame.enableCatalogueMenuItems();
					}
				});

				frame.appendToConsole("My Catalogue has been sent to the CDR service. Waiting for response...", Color.BLACK);

/*				//creating XML document - for test purpose only
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
				}*/
			}
			else
			{
				frame.appendToConsole("My Catalogue has not been sent to the CDR service because it is malformed. "
						+ "All catalogue items should have a name and catalogue has to have at least one item.", Color.RED);
			}
		}
		catch (JAXBException e)
		{
			e.printStackTrace();
		}
		catch(WebServiceException e) //might be thrown by getServicePort
		{
			frame.appendToConsole("My Catalogue has not been updated by the CDR service!"
					+ " Server is not accessible. Please try again later.", Color.RED);
		}
	}

	/**Pulls my Catalogue from the Central Data Repository.
	 *
	 */
	public void cdrPullMyCatalogue()
	{
		try
		{
			// MMM: maybe JAXBContext should be private class field, if it is used from multiple class methods
			JAXBContext jc = JAXBContext.newInstance("oasis.names.specification.ubl.schema.xsd.catalogue_21"); //packageList

			Server port = getCDRPort();

			//CatalogueType catalogue = port.getDocument();
			port.findCatalogueAsync(myParty.getCoreParty().getPartyID(), future ->
			{
				try
				{
					FindCatalogueResponse response = future.get();
					CatalogueType catalogue = response.getReturn();
					if(catalogue != null)
					{
						frame.appendToConsole("Catalogue has been successfully retrieved from the CDR service.", Color.GREEN);
						myParty.getFollowingParties().get(0).setMyProducts(catalogue);

/*						//creating XML document - for test purpose only
						ObjectFactory objFactory = new ObjectFactory();
						JAXBElement<CatalogueType> catalogueElement = objFactory.createCatalogue(catalogue);
						try
						{
							//JAXB.marshal(catalogueElement, System.out );
							JAXB.marshal(catalogueElement, new FileOutputStream("catalogue-from-CDR.xml"));
						}
						catch (FileNotFoundException e)
						{
							System.out.println("Could not save Catalogue document to the file catalogue-from-CDR.xml");
						}*/
					}
					else
						frame.appendToConsole("Could not retrieve the catalogue from the CDR service!. Catalogue does not exist.", Color.RED);
				}
				catch (Exception e)
				{
					StringBuilder msg = new StringBuilder("Catalogue could not be retrieved from the CDR service! Server responds:");
					Throwable cause = e.getCause();
					if(cause instanceof RutaException)
						msg.append(cause.getMessage()).append(" ").append(((RutaException) cause).getFaultInfo().getDetail());
					else
						msg.append(trimSOAPFaultMessage(cause.getMessage()));
					frame.appendToConsole(msg.toString(), Color.RED);
				}
				finally
				{
					frame.enableCatalogueMenuItems();
					frame.repaintTabbedPane();
				}
			});
			frame.appendToConsole("Request for catalogue has been sent to the CDR service. Waiting for response...", Color.BLACK);
		}
		catch (JAXBException e)
		{
			e.printStackTrace();
		}
		catch(WebServiceException e) //might be thrown by getServicePort
		{
			frame.appendToConsole("My Party has not been synchronised with the CDR service!"
					+ " Server is not accessible. Please try again later.", Color.RED);
		}
		/*catch (WebServiceException e)
			{
				JOptionPane.showMessageDialog(null, "Cannot connect to CDR!\nPlease try again later.", "Synchronising Catalogue", JOptionPane.PLAIN_MESSAGE);
			}*/
	}

	/**Sends request for deletion of the Catalogue from the CDR service.
	 *
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
				StringBuilder msg = new StringBuilder("Catalogue has not been deleted from the CDR service! ");
				try
				{
					DeleteCatalogueResponse response = future.get();
					myParty.setDirtyCatalogue(true);
					myParty.setInsertMyCatalogue(true);
					myParty.removeCatalogueIssueDate();
					frame.appendToConsole("Catalogue has been successfully deleted from the CDR service.", Color.GREEN);
				}
				catch (Exception e)
				{
					msg.append("Server responds: ");
					Throwable cause = e.getCause();
					if(cause instanceof RutaException)
						msg.append(cause.getMessage()).append(" ").append(((RutaException) cause).getFaultInfo().getDetail());
					else
						msg.append(trimSOAPFaultMessage(cause.getMessage()));
					frame.appendToConsole(msg.toString(), Color.RED);
				}
				finally
				{
					frame.enableCatalogueMenuItems();
				}
			});
			frame.appendToConsole("Request for the Catalogue deletion has been sent to the CDR service. Waiting for response...", Color.BLACK);
		}
		catch(WebServiceException e) //might be thrown by getServicePort
		{
			frame.appendToConsole("Catalogue has not been deleted from the CDR service!"
					+ " Server is not accessible. Please try again later.", Color.RED);
		}
	}

	/**Removes automatically prepended and appended portion of the SOAPFault detail string.
	 * @param message string to be processed
	 * @return trimmed string
	 */
	public static String trimSOAPFaultMessage(String message)
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
			catch (Exception e)
			{
				msg += "Server responds: ";
				if(e.getCause() instanceof RutaException)
					frame.appendToConsole(msg + e.getCause().getMessage() + " " +
							((RutaException)e.getCause()).getFaultInfo().getDetail(), Color.MAGENTA);
				else
					frame.appendToConsole(msg + e.getCause().getMessage(), Color.MAGENTA);
				e.printStackTrace();
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

	public OLDDataMapper getPartyDataMapper()
	{
		return partyDataMapper;
	}

	@Deprecated
	public OLDDataMapper getCDRPartyDataMapper()
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
			CDRParty.setSimpleName("CDR");
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
	 */
	private Server getCDRPort()
	{
		//getting webservice port
		CDRService service = new CDRService();
		service.setHandlerResolver(new ClientHandlerResolver(myParty));
		Server port = service.getCDRPort();
		//temporary setting for TCP/IP Monitor in Eclipse
		//bindEclipseEndPoint(port);
		if(!defaultEndPoint.equals(cdrEndPoint))
			bindCDREndPoint(port);
		return port;
	}

	//Method may be used for some testing purposes
	@Deprecated
	public void cdrSearchParty(SearchCriterion criterion)
	{
		try
		{
			Server port = getCDRPort();
			port.searchCatalogueAsync(myParty.getUsername(), criterion, futureResult ->
			{
				StringBuilder msg = new StringBuilder("Search request could not be processed! ");
				try
				{
					SearchCatalogueResponse res = futureResult.get();
					//handle the Catalogue list
					frame.appendToConsole("Search results have been successfully retrieved from the CDR service.", Color.GREEN);
				}
				catch (Exception e)
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
			frame.appendToConsole("Search request has been sent to the CDR service. Waiting for response...", Color.BLACK);
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
			if(criterion.isCatalogueSearchedFor()) //querying parties and catalogues
			{
				port.searchCatalogueAsync(myParty.getUsername(), criterion, futureResult ->
				{
					StringBuilder msg = new StringBuilder("Search request could not be processed! ");
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
					catch (Exception e)
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
				frame.appendToConsole("Search request \"" + searchName + "\" has been sent to the CDR service. Waiting for response...", Color.BLACK);
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
				port.searchPartyAsync(myParty.getUsername(), criterion, futureResult ->
				{
					StringBuilder msg = new StringBuilder("Search request could not be processed! ");
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
					catch (Exception e)
					{
						msg.append("Server responds: ");
						Throwable cause = e.getCause();
						if(cause instanceof RutaException)
							msg.append(cause.getMessage()).append(" ").append(((RutaException) cause).getFaultInfo().getDetail());
						else
							msg.append(trimSOAPFaultMessage(cause.getMessage()));
						frame.appendToConsole(msg.toString(), Color.RED);
					}
					finally
					{
						frame.enableSearchMenuItems();
					}
				});
				frame.appendToConsole("Search request \"" + searchName + "\" has been sent to the CDR service. Waiting for response...", Color.BLACK);
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
			if(criterion.isCatalogueSearchedFor()) //querying parties and catalogues
			{
				port.searchCatalogueAsync(myParty.getUsername(), criterion, futureResult ->
				{
					StringBuilder msg = new StringBuilder("Search request could not be processed! ");
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
					catch (Exception e)
					{
						msg.append("Server responds: ");
						Throwable cause = e.getCause();
						if(cause instanceof RutaException)
							msg.append(cause.getMessage()).append(" ").append(((RutaException) cause).getFaultInfo().getDetail());
						else
							msg.append(trimSOAPFaultMessage(cause.getMessage()));
						frame.appendToConsole(msg.toString(), Color.RED);
					}
					finally
					{
						frame.enableSearchMenuItems();
					}
				});
				frame.appendToConsole("Search request has been sent to the CDR service. Waiting for response...", Color.BLACK);
			}
			else // querying only parties
			{
				port.searchPartyAsync(myParty.getUsername(), criterion, futureResult ->
				{
					StringBuilder msg = new StringBuilder("Search request could not be processed! ");
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
					catch (Exception e)
					{
						msg.append("Server responds: ");
						Throwable cause = e.getCause();
						if(cause instanceof RutaException)
							msg.append(cause.getMessage()).append(" ").append(((RutaException) cause).getFaultInfo().getDetail());
						else
							msg.append(trimSOAPFaultMessage(cause.getMessage()));
						frame.appendToConsole(msg.toString(), Color.RED);
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
		}
	}

	@SuppressWarnings("unchecked")
	public void cdrSearch(Search<?> search, boolean exist)
	{
		try
		{
			Server port = getCDRPort();
			SearchCriterion criterion = search.getCriterion();
			if(criterion.isCatalogueSearchedFor()) //querying parties and catalogues
			{
				port.searchCatalogueAsync(myParty.getUsername(), criterion, futureResult ->
				{
					StringBuilder msg = new StringBuilder("Search request could not be processed! ");
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
					catch (Exception e)
					{
						msg.append("Server responds: ");
						Throwable cause = e.getCause();
						if(cause instanceof RutaException)
							msg.append(cause.getMessage()).append(" ").append(((RutaException) cause).getFaultInfo().getDetail());
						else
							msg.append(trimSOAPFaultMessage(cause.getMessage()));
						frame.appendToConsole(msg.toString(), Color.RED);
					}
					finally
					{
						frame.enableSearchMenuItems();
					}
				});
				frame.appendToConsole("Search request \"" + search.getSearchName() +
						"\" has been sent to the CDR service. Waiting for response...", Color.BLACK);
			}
			else // querying only parties
			{
				port.searchPartyAsync(myParty.getUsername(), criterion, futureResult ->
				{
					StringBuilder msg = new StringBuilder("Search request could not be processed! ");
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
					catch (Exception e)
					{
						msg.append("Server responds: ");
						Throwable cause = e.getCause();
						if(cause instanceof RutaException)
							msg.append(cause.getMessage()).append(" ").append(((RutaException) cause).getFaultInfo().getDetail());
						else
							msg.append(trimSOAPFaultMessage(cause.getMessage()));
						frame.appendToConsole(msg.toString(), Color.RED);
					}
					finally
					{
						frame.enableSearchMenuItems();
					}
				});
				frame.appendToConsole("Search request \"" + search.getSearchName() +
						"\" has been sent to the CDR service. Waiting for response...", Color.BLACK);
			}
		}
		catch(WebServiceException e)
		{
			frame.appendToConsole("Search request \"" + search.getSearchName() +
					"\"  has not been processed! Server is not accessible. Please try again later.", Color.RED);
		}
	}

	public Future<?> cdrUpdateClient()
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

	public void cdrUpdateNotification(RutaVersion version)
	{
		try
		{
			Server port = getCDRPort();
			port.notifyUpdateAsync(version, futureResult ->
			{
				StringBuilder msg = new StringBuilder("CDR service could not be notified! ");
				try
				{
					NotifyUpdateResponse res = futureResult.get();
					frame.appendToConsole("CDR service has been successfully notified about new Ruta Client version.", Color.GREEN);
				}
				catch (Exception e)
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
			frame.appendToConsole("Update notification has been sent to the CDR service. Waiting for response...", Color.BLACK);
		}
		catch(WebServiceException e)
		{
			frame.appendToConsole("CDR service could not be notified! Server is not accessible. Please try again later.", Color.RED);
		}
	}


	public void findAllParties()
	{
		Server port = getCDRPort();
		port.findAllPartiesAsync(futureResult ->
		{
			StringBuilder msg = new StringBuilder("Search request could not be processed! ");
			try
			{
				FindAllPartiesResponse res = futureResult.get();
				//handle the Catalogue list
				frame.appendToConsole("Search results have been successfully retrieved from the CDR service.", Color.GREEN);
			}
			catch (Exception e)
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
		frame.appendToConsole("Search request has been sent to the CDR service.", Color.BLACK);
	}

}
