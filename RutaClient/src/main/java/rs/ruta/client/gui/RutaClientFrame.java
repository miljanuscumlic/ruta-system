package rs.ruta.client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.Nullable;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.validation.constraints.NotNull;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oasis.names.specification.ubl.schema.xsd.applicationresponse_21.ApplicationResponseType;
import oasis.names.specification.ubl.schema.xsd.catalogue_21.CatalogueType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyIdentificationType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyNameType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;
import oasis.names.specification.ubl.schema.xsd.order_21.OrderType;
import oasis.names.specification.ubl.schema.xsd.ordercancellation_21.OrderCancellationType;
import oasis.names.specification.ubl.schema.xsd.orderchange_21.OrderChangeType;
import oasis.names.specification.ubl.schema.xsd.orderresponse_21.OrderResponseType;
import oasis.names.specification.ubl.schema.xsd.orderresponsesimple_21.OrderResponseSimpleType;
import rs.ruta.client.BusinessParty;
import rs.ruta.client.Catalogue;
import rs.ruta.client.Item;
import rs.ruta.client.MyParty;
import rs.ruta.client.Party;
import rs.ruta.client.RutaClient;
import rs.ruta.client.Search;
import rs.ruta.client.correspondence.BuyingCorrespondence;
import rs.ruta.client.correspondence.Correspondence;
import rs.ruta.client.datamapper.MyPartyXMLFileMapper;
import rs.ruta.common.BugReport;
import rs.ruta.common.BugReportSearchCriterion;
import rs.ruta.common.PartnershipRequest;
import rs.ruta.common.datamapper.DatabaseException;
import rs.ruta.common.datamapper.DetailException;
import rs.ruta.common.Associates;
import rs.ruta.common.InstanceFactory;
import rs.ruta.services.RutaException;

public class RutaClientFrame extends JFrame implements ActionListener
{
	private static final long serialVersionUID = -6582749886269431483L;
	private static final String DEFAULT_WIDTH = "1000";
	private static final String DEFAULT_HEIGHT = "800";
	private static final Logger logger = LoggerFactory.getLogger("rs.ruta.client");
	public static final int TAB_PRODUCTS = 0;
	public static final int TAB_CORRESPONDENSCES = 1;
	public static final int TAB_CDR_DATA = 2;

	private RutaClient client;
	private JTabbedPane tabbedPane;
	private JSplitPane splitPane;
	private JTextPane consolePane;
	private AboutDialog aboutDialog;
	private UpdateDialog updateDialog;
	private PartyDialog partyDialog;
	private ProductDialog productDialog;
	private RegisterDialog registerDialog;
	private SearchDialog searchDialog;
	private CDRSettingsDialog settingsDialog;
	private NotifyDialog notifyDialog;
	private BugReportDialog bugReportDialog;
	private BugExploreDialog bugExploreDialog;
	private JFileChooser chooser;

	private JMenuItem myPartyItem = new JMenuItem("My Party");
	private JMenuItem myCatalogueItem = new JMenuItem("My Products & Services");
	private JMenuItem newProductItem = new JMenuItem("New Product or Service");
	private JMenuItem saveDataItem = new JMenuItem("Save");
	private JMenuItem exportDataItem = new JMenuItem("Export");
	private JMenuItem importDataItem = new JMenuItem("Import");
	private JMenuItem localRegisterPartyItem = new JMenuItem("Register My Party");
	private JMenuItem localDeregisterPartyItem = new JMenuItem("Deregister My Party");
	private JMenuItem exitItem = new JMenuItem("Exit");

	private JMenuItem cdrGetDocumentsItem = new JMenuItem("Get New Documents");
	private JMenuItem cdrSearchItem = new JMenuItem("Search");
	private JMenuItem cdrUpdateCatalogueItem = new JMenuItem("Update My Catalogue");
	@Deprecated
	private JMenuItem cdrPullCatalogueItem = new JMenuItem("Pull My Catalogue");
	private JMenuItem cdrDeleteCatalogueItem = new JMenuItem("Delete My Catalogue");
	private JMenuItem cdrUpdatePartyItem = new JMenuItem("Update My Party");
	private JMenuItem cdrRegisterPartyItem = new JMenuItem("Register My Party");
	private JMenuItem cdrDeregisterPartyItem = new JMenuItem("Deregister My Party");
	private JMenuItem cdrSettingsItem = new JMenuItem("Settings");

	private TabComponent tabCDR;
	private TabComponent tabProducts;
	private TabComponent tabCorrespondences;

	public RutaClientFrame() {}

	public void setClient(RutaClient client)
	{
		this.client = client;
	}

	public void initialize()
	{
		final MyParty myParty = client.getMyParty();

		//get frame related properties
		final Properties properties = client.getProperties();
		final int left = Integer.parseInt(properties.getProperty("mainFrame.left", "0"));
		final int top = Integer.parseInt(properties.getProperty("mainFrame.top", "0"));
		final int width = Integer.parseInt(properties.getProperty("mainFrame.width", DEFAULT_WIDTH));
		final int height = Integer.parseInt(properties.getProperty("mainFrame.height", DEFAULT_HEIGHT));
		setBounds(left, top, width, height);
		setTitle("Ruta Client - " + client.getMyParty().getPartySimpleName());

		chooser = new JFileChooser();
		final FileFilter filter = new FileNameExtensionFilter("XML files", "xml");
		chooser.setFileFilter(filter);

		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent event)
			{
				dispatchFalseMouseEvent();
				System.exit(0);
			}

			@Override
			public void windowIconified(WindowEvent e)
			{
				dispatchFalseMouseEvent();
			}

			@Override
			public void windowLostFocus(WindowEvent e)
			{
				dispatchFalseMouseEvent();
			}

			@Override
			public void windowDeactivated(WindowEvent e)
			{
				dispatchFalseMouseEvent();
			}
		});

		//setting tabs
		tabbedPane = new JTabbedPane();
		tabProducts = new TabProducts(this);
		tabbedPane.addTab("Products & Services", tabProducts);
		tabCorrespondences = new TabCorrespondences(this);
		tabbedPane.addTab("Correspondences", tabCorrespondences);
		tabCDR = new TabCDRData(this);
		tabbedPane.addTab("CDR Data", tabCDR);

		tabbedPane.addChangeListener(event ->
		{
			loadTab(tabbedPane.getSelectedIndex());
		});

		consolePane = new JTextPane();
		consolePane.setSize(3,50);
		consolePane.setCaretColor(Color.white);
		consolePane.setEditable(false);

		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tabbedPane, new JScrollPane(consolePane));
		add(splitPane, BorderLayout.CENTER);

		//setting view listeners after model listeners of tabbedPane
		myParty.addActionListener(this, RutaClientFrameEvent.class);
		myParty.addActionListener(this, SearchEvent.class);
		myParty.addActionListener(this, CorrespondenceEvent.class);
		myParty.addActionListener(this, BusinessPartyEvent.class);
		myParty.addActionListener(this, PartnershipEvent.class);
		myParty.addActionListener(this, ItemEvent.class);

		//setting the menu
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		JMenu localDataMenu = new JMenu("Local Data");
		JMenu cdrMenu = new JMenu("Central Data");
		JMenu helpMenu = new JMenu("Help");
		menuBar.add(localDataMenu);
		menuBar.add(cdrMenu);
		menuBar.add(helpMenu);

		localDataMenu.add(myPartyItem);
		localDataMenu.add(myCatalogueItem);
		localDataMenu.add(newProductItem);
		localDataMenu.addSeparator();
		localDataMenu.add(saveDataItem);
		localDataMenu.add(exportDataItem);
		localDataMenu.add(importDataItem);
		localDataMenu.addSeparator();
		localDataMenu.add(localRegisterPartyItem);
		localDataMenu.add(localDeregisterPartyItem);
		localDataMenu.addSeparator();
		localDataMenu.add(exitItem);

		myPartyItem.addActionListener(event ->
		{
			showPartyDialog(client.getMyParty().getCoreParty(), "My Party", true, false);
		});

		myCatalogueItem.addActionListener(event ->
		{
			tabbedPane.setSelectedIndex(TAB_PRODUCTS);
		});

		newProductItem.addActionListener(event ->
		{
			Item product = showProductDialog(client.getMyParty().createEmptyProduct(), "Add New Product or Service", true);
			if (product != null)
			{
				try
				{
					client.getMyParty().addProduct(product);
					tabbedPane.setSelectedIndex(TAB_PRODUCTS);
				}
				catch (DetailException e)
				{
					logger.error("Could not insert new product in the database! Exception is: ", e);
					EventQueue.invokeLater(() ->
					JOptionPane.showMessageDialog(null, "Could not insert new product in the database!",
							"Database Error", JOptionPane.ERROR_MESSAGE));
				}
			}
			else
			{
				client.getMyParty().decreaseProductID();
			}
		});

		saveDataItem.addActionListener(event ->
		{
			appendToConsole(new StringBuilder("Storing data to the local data store..."), Color.BLACK);
			new Thread(() ->
			{
				try
				{
					if(client.getMyParty().isRegisteredWithLocalDatastore())
					{
						client.getMyParty().storeAllData();
						appendToConsole(new StringBuilder("Data has been saved to the local data store."), Color.GREEN);
					}
					else
						appendToConsole(new StringBuilder("Could not save data. Party is not registered with the local data store!"),
								Color.RED);
				}
				catch(Exception e)
				{
					appendToConsole(new StringBuilder("There has been an error. Could not save all the data to the local data store!"),
							Color.RED);
					getLogger().error("Could not save data to the local data store!", e);
				}
			}).start();
		});

		importDataItem.addActionListener(event ->
		{
			int option = JOptionPane.showConfirmDialog(RutaClientFrame.this,
					"By importing the data from an external file all or part of you local data will be overriden!\nDo you want to proceed?",
					"Warning message", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if(option == JOptionPane.YES_OPTION)
			{
				chooser.setCurrentDirectory(new File("."));
				chooser.setDialogTitle("Local Data Import");
				int result = chooser.showOpenDialog(this);
				if(result == JFileChooser.APPROVE_OPTION)
				{
					final String filePath = chooser.getSelectedFile().getPath();
					//importing data
					try
					{
						final MyPartyXMLFileMapper<MyParty> partyDataMapper =
								new MyPartyXMLFileMapper<MyParty>(client.getMyParty(), filePath);
						final ArrayList<MyParty> parties = (ArrayList<MyParty>) partyDataMapper.findAll();
//						MyParty myParty;
						if(parties.size() != 0)
						{
							//temporary code for importing core Party only
//							myParty = client.getMyParty();
							String oldPartyID = myParty.getPartyID();
							myParty.setCoreParty(parties.get(0).getCoreParty());
							myParty.setPartyID(oldPartyID);
							updateTitle(myParty.getPartySimpleName());

							/*							//MMM old code for importing data - should be improved for the release version
							myParty = parties.get(0);
							client.setMyParty(myParty);
							updateTitle(myParty.getPartySimpleName());
							Search.setSearchNumber(myParty.getSearchNumber());*/

							repaint();
							appendToConsole(new StringBuilder("Local data have been successfully imported from the file: ").
									append(filePath), Color.GREEN);
						}
					}
					catch(JAXBException e)
					{
						appendToConsole(new StringBuilder("Could not import data from the chosen file. The file is corrupt!"), Color.RED);
					}
					catch(Exception e)
					{
						appendToConsole(new StringBuilder("There has been an error. ").append(e.getMessage()), Color.RED);
					}
				}
			}
		});

		exportDataItem.addActionListener(event ->
		{
			chooser.setCurrentDirectory(new File("."));
			chooser.setDialogTitle("Local Data Export");
			int result = chooser.showSaveDialog(this);
			if(result == JFileChooser.APPROVE_OPTION)
			{
				StringBuilder filePathBuilder = new StringBuilder(chooser.getSelectedFile().getPath());
				if(!filePathBuilder.toString().endsWith(".xml"))
					filePathBuilder = filePathBuilder.append(".xml");
				final String filePath = filePathBuilder.toString();
				try
				{
					//file must be created because MyPartyXMLFileMapper would throw an exception if file doesn't exist
					final Path path = Paths.get(filePath);
					if(Files.notExists(path))
						Files.createFile(path);

					MyPartyXMLFileMapper<MyParty> fileMapper = new MyPartyXMLFileMapper<MyParty>(client.getMyParty(), filePath);
					fileMapper.insertAll();
					appendToConsole(new StringBuilder("Local data have been successfully exported to the file: ").append(filePath),
							Color.GREEN);
				}
				catch (JAXBException e)
				{
					appendToConsole(new StringBuilder("There has been an error. Local data could not be exported to the file: ").
							append(filePath), Color.RED);
				}
				catch (Exception e)
				{
					appendToConsole(new StringBuilder("There has been an error. ").append(e.getMessage()), Color.RED);
				}
			}
		});

		localRegisterPartyItem.addActionListener(event ->
		{
//			final MyParty myParty = client.getMyParty();
			if(myParty.isRegisteredWithLocalDatastore())
				appendToConsole(new StringBuilder("My Party is already registered with the local datastore!"), Color.BLUE);
			else
			{
				disablePartyMenuItems();
				new Thread(() ->
				{
						try
						{
							if(!myParty.isRegisteredWithLocalDatastore())
								client.setInitialUsername(showLocalSignUpDialog("Local database registration", true));
						}
						catch (DetailException e1)
						{
							processExceptionAndAppendToConsole(e1,
									new StringBuilder("My Party is could not be registered with the local datastore!"));
						}
				}).start();
			}
		});

		localDeregisterPartyItem.addActionListener(event ->
		{
//			MyParty myParty = client.getMyParty();
			if(myParty.isRegisteredWithCDR())
				appendToConsole(new StringBuilder("Deregistration request of My Party has not been sent to the local datastore.").
						append(" If you want to deregister My Party locally you have to deregister it from the CDR service first!")
						, Color.RED);
			else if(myParty.isRegisteredWithLocalDatastore())
			{
				int option = JOptionPane.showConfirmDialog(RutaClientFrame.this,
						"By deregistering My Party from the local datastore, all your data in the store will be deleted.\n" +
								"Do you want to proceed?", "Warning message", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if(option == JOptionPane.YES_OPTION)
				{
					disablePartyMenuItems();
					new Thread(() ->
					{
						appendToConsole(new StringBuilder("Request for deregistration of My Party has been sent to the local ").
								append("datastore. Waiting for a response..."), Color.BLACK);
						client.localDeregisterMyParty();
						updateTitle("");
						appendToConsole(new StringBuilder("My Party has been successfully deregistered from the local datastore."),
								Color.GREEN);
						repaint();
						EventQueue.invokeLater(() ->
						{
							JOptionPane.showMessageDialog(RutaClientFrame.this, "All data are deleted. Ruta Client Application will be closed!");
							System.exit(0);
						});
					}).start();
				}
			}
			else
				appendToConsole(new StringBuilder("Deregistration request of My Party has not been sent to the local datastore.").
						append(" My Party is not registered with the local datastore!"), Color.RED);
		});

		exitItem.addActionListener(event ->
		{
			dispatchFalseMouseEvent();
			System.exit(0);
		});

		cdrMenu.add(cdrGetDocumentsItem);
		cdrMenu.add(cdrSearchItem);
		cdrMenu.addSeparator();
		cdrMenu.add(cdrUpdateCatalogueItem);
		//		cdrMenu.add(cdrPullCatalogueItem);
		cdrMenu.add(cdrDeleteCatalogueItem);
		cdrMenu.addSeparator();
		cdrMenu.add(cdrUpdatePartyItem);
		cdrMenu.add(cdrRegisterPartyItem);
		cdrMenu.add(cdrDeregisterPartyItem);
		cdrMenu.addSeparator();
		cdrMenu.add(cdrSettingsItem);

		cdrGetDocumentsItem.addActionListener(event ->
		{
			if(client.getMyParty().isRegisteredWithCDR())
			{
				disableGetDocumentsMenuItem();
				new Thread(()->
				{
					client.cdrGetNewDocuments();
				}).start();
			}
			else
				appendToConsole(new StringBuilder("Request for new documents has not been sent to the CDR service.").
						append(" My Party should be registered with the CDR service first!"), Color.RED);
		});

		cdrSearchItem.addActionListener(event ->
		{
			if(client.getMyParty().isRegisteredWithCDR())
			{
				new Thread(() ->
				{
					showSearchDialog("Search CDR");
				}).start();
			}
			else
				appendToConsole(new StringBuilder("Search request can not be composed. My Party should be registered with the ").
						append("CDR service first!"), Color.RED);
		});

		cdrRegisterPartyItem.addActionListener(event ->
		{
//			final MyParty myParty = client.getMyParty();
			if(myParty.isRegisteredWithCDR())
				appendToConsole(new StringBuilder("My Party is already registered with the CDR service!"), Color.BLUE);
			else
			{
				disablePartyMenuItems();
				new Thread(() ->
				{
					boolean cdrRegistration = true;
					if(client.getInitialUsername() == null || !myParty.isRegisteredWithLocalDatastore())
					{
						JOptionPane.showMessageDialog(RutaClientFrame.this, "My Party is not registered with local database.\n"
								+ "That will be the first step.", "Information", JOptionPane.INFORMATION_MESSAGE);
						try
						{
							final String username = showLocalSignUpDialog("Local database registration", true);
							if(username != null)
								client.setInitialUsername(username);
							else
								cdrRegistration = false;
						}
						catch (DetailException e)
						{
							processExceptionAndAppendToConsole(e,
									new StringBuilder("My Party could not be registered with the local datastore!"));
						}
					}
					if(cdrRegistration)
						showCDRSignUpDialog("CDR registration");
				}).start();
			}
		});

		cdrDeregisterPartyItem.addActionListener(event ->
		{
//			MyParty myParty = client.getMyParty();
			if(myParty.isRegisteredWithCDR())
			{
				if(!myParty.getBusinessPartners().isEmpty())
				{
					JOptionPane.showMessageDialog(RutaClientFrame.this, "All Business Partnerships should be broken first!",
							"Information message", JOptionPane.INFORMATION_MESSAGE);
				}
				else
				{
					int option = JOptionPane.showConfirmDialog(RutaClientFrame.this,
							"By deregistering My Party from the CDR service, all your data in the CDR will be deleted\n" +
									"and all your followers will be notified about your deregistration.\n" +
									"Do you want to proceed?",
									"Warning message", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if(option == JOptionPane.YES_OPTION)
					{
						disablePartyMenuItems();
						new Thread(() ->
						{
							client.cdrDeregisterMyParty();
						}).start();
					}
				}
			}
			else
				appendToConsole(new StringBuilder("Deregistration request of My Party has not been sent to the CDR service.").
						append(" My Party is not registered with the CDR service!"), Color.RED);
		});

		cdrUpdatePartyItem.addActionListener(event ->
		{
//			final MyParty myParty = client.getMyParty();
			if(myParty.isRegisteredWithCDR())
			{
				if(myParty.isDirtyMyParty())
				{
					appendToConsole(new StringBuilder("Preparing update request of My Party..."), Color.BLACK);
					disablePartyMenuItems();
					new Thread(() ->
					{
						client.cdrUpdateMyParty();

					}).start();
				}
				else
					appendToConsole(new StringBuilder("My Party is already updated on the CDR service!"), Color.BLUE);
			}
			else
				appendToConsole(new StringBuilder("Update request of My Party has not been sent to the CDR service.").
						append(" My Party should be registered with the CDR service first!"), Color.RED);
		});

		cdrUpdateCatalogueItem.addActionListener(event ->
		{
//			MyParty myParty = client.getMyParty();
			if(myParty.isRegisteredWithCDR())
			{
				if(myParty.isDirtyCatalogue())
				{
					disableCatalogueMenuItems();
					new Thread(()->
					{
						try
						{
//							client.getMyParty().cdrUpdateMyCatalogue();
							client.cdrSynchroniseMyCatalogue();
						}
						catch(Exception e)
						{
							appendToConsole(new StringBuilder("My Catalogue has not been sent to the CDR service. ").
									append(e.getMessage()), Color.RED);
							logger.error("My Catalogue has not been sent to the CDR service. Exception is: ", e);
							enableCatalogueMenuItems();
						}
					}).start();
				}
				else
					appendToConsole(new StringBuilder("My Catalogue is already updated on the CDR service."), Color.BLUE);
			}
			else
				appendToConsole(new StringBuilder("Update request of My Catalogue has not been sent to the CDR service.").
						append(" My Party should be registered with the CDR service first!"), Color.RED);
		});

		cdrPullCatalogueItem.addActionListener(event ->
		{
//			MyParty myParty = client.getMyParty();
			if(myParty.isRegisteredWithCDR())
			{
				if(myParty.getMyFollowingParty() != null)
				{
					disableCatalogueMenuItems();
					new Thread(() ->
					{
						client.cdrPullMyCatalogue();
					}).start();
				}
				else
					JOptionPane.showMessageDialog(null, "My Party is not set as the following party", "Synchronising Catalogue",
							JOptionPane.INFORMATION_MESSAGE);
			}
			else
				appendToConsole(new StringBuilder("Pull request for My Catalogue has not been sent to the CDR service.").
						append(" My Party should be registered with the CDR service first!"), Color.RED);
		});

		cdrDeleteCatalogueItem.addActionListener(event ->
		{
//			MyParty myParty = client.getMyParty();
			if(myParty.isRegisteredWithCDR())
			{
				if(myParty.isCatalogueInCDR())
				{
					int option = JOptionPane.showConfirmDialog(RutaClientFrame.this,
							"By deleting your catalogue from the CDR all your business partners and followers\n"
							+ "will be notified about the catalogue deletion. Do you want to proceed?",
							"Warning message", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if(option == JOptionPane.YES_OPTION)
					{
						disableCatalogueMenuItems();
						new Thread( () ->
						{
							try
							{
								client.cdrDeleteMyCatalogue();
							}
							catch(Exception e)
							{
								appendToConsole(new StringBuilder("Deletion request of My Catalogue has not been sent to the CDR service. ").
										append(e.getMessage()), Color.RED);
								enableCatalogueMenuItems();
							}
						}).start();
					}
				}
				else
					appendToConsole(new StringBuilder("Deletion request of My Catalogue has not been sent to the CDR service.").
							append(" My Catalogue is not present in the CDR!"), Color.RED);
			}
			else
				appendToConsole(new StringBuilder("Deletion request of My Catalogue has not been sent to the CDR service.").
						append(" My Party should be registered with the CDR service first!"), Color.RED);
		});

		cdrSettingsItem.addActionListener(event ->
		{
			settingsDialog = new CDRSettingsDialog(RutaClientFrame.this);
			settingsDialog.setTitle("CDR Settings");
			settingsDialog.setVisible(true);
			if(settingsDialog.isApplyPressed())
			{
				RutaClient.setCDREndPoint(settingsDialog.getServiceLocation());
				RutaClient.setConnectTimeout(Integer.valueOf(settingsDialog.getConnectTimeout()) * 1000);
				RutaClient.setRequestTimeout(Integer.valueOf(settingsDialog.getRequestTimeout()) * 1000);
				settingsDialog.setApplyPressed(false);
			}
		});

		JMenuItem aboutItem = new JMenuItem("About");
		helpMenu.add(aboutItem);
		JMenuItem updateItem = new JMenuItem("Check for Updates");
		helpMenu.add(updateItem);
		JMenuItem notifyItem = new JMenuItem("Send Update Notification");
		//		MMM: Comment notifyItem before new version of Ruta Client is released!
		helpMenu.add(notifyItem);
		JMenuItem reportBugItem = new JMenuItem("Report Bug");
		helpMenu.add(reportBugItem);
		JMenuItem exploreBugItem = new JMenuItem("Explore Bugs");
		helpMenu.add(exploreBugItem);
		JMenuItem fileItem = new JMenuItem("Send File");
		//		helpMenu.add(fileItem);
		JMenuItem clearCacheItem = new JMenuItem("Clear Service Cache");
		helpMenu.add(clearCacheItem);

		aboutItem.addActionListener(event ->
		{
			if(aboutDialog == null)
				aboutDialog = new AboutDialog(RutaClientFrame.this);
			aboutDialog.setVisible(true);
		});

		updateItem.addActionListener(event ->
		{
			if(updateDialog == null)
				updateDialog = new UpdateDialog(RutaClientFrame.this, client);
			updateDialog.setVisible(true);
		});

		notifyItem.addActionListener(event ->
		{
			if(notifyDialog == null)
				notifyDialog = new NotifyDialog(RutaClientFrame.this);
			notifyDialog.setVisible(true);
			if(notifyDialog.isNotifyPressed())
			{
				notifyDialog.setNotifyPressed(false);
				new Thread(()->
				{
					client.cdrUpdateNotification(notifyDialog.getVersion());
				}).start();
			}
		});

		reportBugItem.addActionListener(event ->
		{
			new Thread(()->
			{
				sendBugReport();
			}).start();
		});

		exploreBugItem.addActionListener(event ->
		{
			if(client.getMyParty().isRegisteredWithCDR())
			{
				bugExploreDialog = new BugExploreDialog(RutaClientFrame.this);
				bugExploreDialog.setVisible(true);
			}
			else
			{
				appendToConsole(new StringBuilder("Bug reports cannot be explored by non-registered party."), Color.RED);
			}
		});

		fileItem.addActionListener(event ->
		{
			new Thread(()->
			{
				client.cdrInsertAttachment();
			}).start();
		});

		clearCacheItem.addActionListener(event ->
		{
			new Thread(()->
			{
				Future<?> future = null;
				long time1 = System.currentTimeMillis();
				try
				{
					future = client.cdrClearCache();
//					future.get(1, TimeUnit.SECONDS);
					future.get();
					long time2 = System.currentTimeMillis();
					appendToConsole(new StringBuilder("No exception. time elapsed: " + (time2-time1) / 1000), Color.BLACK);
				}
				catch (InterruptedException | ExecutionException e1)
				{
					future.cancel(true);
					long time2 = System.currentTimeMillis();
					appendToConsole(new StringBuilder(e1.getClass().getSimpleName() +  ". time elapsed: " + (time2-time1) / 1000), Color.BLACK);
					e1.printStackTrace();
				}
//				catch(TimeoutException e)
//				{
//					future.cancel(true);
//					long time2 = System.currentTimeMillis();
//					appendToConsole(new StringBuilder("TimeoutException. time elapsed: " + (time2-time1) / 1000), Color.BLACK);
//				}

			}).start();
		});

		tabbedPane.setSelectedIndex(TAB_PRODUCTS);
		setVisible(true);
	}

	/**
	 * Gets the {@link Logger} of the {@code RutaClientFrame}.
	 * @return logger
	 */
	public static Logger getLogger()
	{
		return logger;
	}


	/**
	 * Initialize tabbed pane
	 */
	public void initializeTabbedPane()
	{
		//setting tabs
		tabbedPane = new JTabbedPane();
		tabProducts = new TabProducts(this);
		tabbedPane.addTab("Products & Services", tabProducts);
		tabCorrespondences = new TabCorrespondences(this);
		tabbedPane.addTab("Correspondences", tabCorrespondences);
		tabCDR = new TabCDRData(this);
		tabbedPane.addTab("CDR Data", tabCDR);

		tabbedPane.addChangeListener(event ->
		{
			loadTab(tabbedPane.getSelectedIndex());
		});
	}

	/**
	 * Dispatches false {@link MouseEvent mouse event} to trigger {@code focusTracker}
	 * event listener which will save the data of a last edited cell of the table
	 * in current view if it is still in editing state.
	 */
	private void dispatchFalseMouseEvent()
	{
		tabbedPane.getComponent(tabbedPane.getSelectedIndex()).dispatchEvent(
				new MouseEvent(RutaClientFrame.this, MouseEvent.MOUSE_CLICKED, 1, 0, 0, 0, 1, false));
	}

	/**
	 * Checks whether My Party is registered with the CDR, then opens {@link BugReportDialog}
	 * and sends {@link BugReport} to the CDR by calling appropriate method in the {@link Client}
	 * class. {@code sendBugReport} method is extracted from the {@code ActionListener} because it is used in
	 * more than one place.
	 */
	public void sendBugReport()
	{
		if(client.getMyParty().isRegisteredWithCDR())
		{
			bugReportDialog = new BugReportDialog(RutaClientFrame.this);
			bugReportDialog.setVisible(true);
			if(bugReportDialog.isReportPressed())
			{
				bugReportDialog.clearData();
				client.cdrReportBug(bugReportDialog.getBugReport());
			}
		}
		else
			appendToConsole(new StringBuilder("Bug report cannot be issued by non-registered party."), Color.RED);
	}

	/**
	 * Checks whether My Party is registered with the CDR, then sends a request to the CDR for the list of
	 * {@link BugReport}s based on some search criterion.
	 * @param criterion search criterion
	 * @return {@link Future} object representing the response.
	 */
	public Future<?> searchBugReport(BugReportSearchCriterion criterion)
	{
		Future<?> future = null;
		if(client.getMyParty().isRegisteredWithCDR())
			future = client.cdrSearchBugReport(criterion);
		else
			appendToConsole(new StringBuilder("Bug report list cannot be requested by non-registered party."), Color.RED);
		return future;
	}

	/**
	 * Checks whether My Party is registered with the CDR, then sends a request for the list of all
	 * {@link BugReport bugs reported} to the CDR.
	 * @return {@link Future} object representing the response.
	 */
	public Future<?> findAllBugs()
	{
		Future<?> future = null;
		if(client.getMyParty().isRegisteredWithCDR())
			future = client.cdrFindAllBugs();
		else
			appendToConsole(new StringBuilder("Bug report list cannot be requested by non-registered party."), Color.RED);
		return future;
	}

	/**
	 * Enables menu items regarding Search. Method is called after client gets the response from the CDR service.
	 * Method's body is executed in the {@link EventQueue}.
	 */
	public void enableSearchMenuItems()
	{
		EventQueue.invokeLater(() ->
		{
			cdrSearchItem.setEnabled(true);
			cdrDeregisterPartyItem.setEnabled(true);
		});
	}

	/**
	 * Disables menu items regarding Search. Method is called after client sends the request to the CDR service.
	 * Method's body is executed in the {@link EventQueue}.
	 */
	public void disableSearchMenuItems()
	{
		EventQueue.invokeLater(() ->
		{
			cdrSearchItem.setEnabled(false);
			cdrDeregisterPartyItem.setEnabled(false);
		});
	}

	/**
	 * Enables menu items regarding My Party. Method is called after client gets the response from the CDR service.
	 * Method's body is executed in the {@link EventQueue}.
	 */
	public void enablePartyMenuItems()
	{
		EventQueue.invokeLater(() ->
		{
			cdrRegisterPartyItem.setEnabled(true);
			cdrUpdatePartyItem.setEnabled(true);
			cdrDeregisterPartyItem.setEnabled(true);
		});
	}

	/**
	 * Disables menu items regarding My Party. Method is called after client sends the request to the CDR service.
	 * Method's body is executed in the {@link EventQueue}.
	 */
	public void disablePartyMenuItems()
	{
		EventQueue.invokeLater(() ->
		{
			cdrRegisterPartyItem.setEnabled(false);
			cdrUpdatePartyItem.setEnabled(false);
			cdrDeregisterPartyItem.setEnabled(false);
		});
	}

	/**
	 * Enables menu items regarding My Catalogue.. Method is called after client gets the response from the CDR service.
	 * Method's body is executed in the {@link EventQueue}.
	 */
	public void enableCatalogueMenuItems()
	{
		EventQueue.invokeLater(() ->
		{
			cdrUpdateCatalogueItem.setEnabled(true);
			cdrDeleteCatalogueItem.setEnabled(true);
			cdrDeregisterPartyItem.setEnabled(true);
		});
	}

	/**
	 * Disables menu items regarding My Catalogue. Method is called after client sends the request to the CDR service.
	 * Method's body is executed in the {@link EventQueue}.
	 */
	public void disableCatalogueMenuItems()
	{
		EventQueue.invokeLater(() ->
		{
			cdrUpdateCatalogueItem.setEnabled(false);
			cdrDeleteCatalogueItem.setEnabled(false);
			cdrDeregisterPartyItem.setEnabled(false);
		});
	}

	/**
	 * Enables Get New Documents menu item.
	 * Method's body is executed in the {@link EventQueue}.
	 */
	public void enableGetDocumentsMenuItem()
	{
		EventQueue.invokeLater(() ->
		{
			cdrGetDocumentsItem.setEnabled(true);
		});
	}

	/**
	 * Disables Get New Documents menu item.
	 * Method's body is executed in the {@link EventQueue}.
	 */
	public void disableGetDocumentsMenuItem()
	{
		EventQueue.invokeLater(() ->
		{
			cdrGetDocumentsItem.setEnabled(false);
		});
	}

	/**
	 * Saves properties from {@code RutaClientFrame} class fields to the {@link Properties} object.
	 */
	public void saveProperties()
	{
		Properties properties = client.getProperties();
		properties.put("mainFrame.left", String.valueOf(getX()));
		properties.put("mainFrame.top", String.valueOf(getY()));
		properties.put("mainFrame.width", String.valueOf(getWidth()));
		properties.put("mainFrame.height", String.valueOf(getHeight()));

		//MMM: add column sizes for all the tabs
	}

	/**
	 * Repaints tab.
	 * @param tabIndex index of the tab to repaint
	 */
	private void loadTab(int tabIndex)
	{
		Component component = null;
		switch(tabIndex)
		{
		case TAB_CDR_DATA:
			component = tabCDR;
			break;
		case TAB_PRODUCTS:
			component = tabProducts;
			break;
		case TAB_CORRESPONDENSCES:
			component = tabCorrespondences;
			break;
		}
		tabbedPane.setComponentAt(tabIndex, component);
	}

	/**
	 * Shows dialog with {@link Party} data which could be changed. If changes have been made to the {@code Party}
	 * object they are saved within this method.
	 * @param party {@code Party} object which data are to be shown
	 * @param title title of the dialog
	 * @param editable whether Party dialog's dadat are editable
	 * @param registration whether the dialog is shown during local database registration
	 * @return {@code Party} with potentially changed data
	 */
	public Party showPartyDialog(Party party, String title, boolean editable, boolean registration)
	{
		partyDialog = new PartyDialog(RutaClientFrame.this, editable, registration);
		partyDialog.setTitle(title);

		//setting clone and not original object as a dialog's party field because the changes to the party will be rejected
		//if they are not accepted by pressing the dialog's OK button. If original object is set instead, changes remain
		//no matter what button was pressed
		partyDialog.setParty(party.clone());
		partyDialog.setVisible(true);
		if(partyDialog.isChanged())
		{
			party = partyDialog.getParty();
			updateTitle(party.getPartySimpleName());
			final MyParty myParty = client.getMyParty();
			myParty.setCoreParty(party);
			myParty.setDirtyMyParty(true);
			partyDialog.setChanged(false);
		}
		return party;
	}

	/**
	 * Shows dialog with {@link Item}'s.
	 * @param product {@code Item product} which data are to be shown/amended
	 * @param title title of the dialog
	 * @param editable whether the data are editable
	 * @return {@code Item product} with potentially changed data or {@code null} if data are not changed
	 */
	public Item showProductDialog(@NotNull Item product, String title, boolean editable)
	{
		productDialog = new ProductDialog(RutaClientFrame.this, client.getMyParty(),
				product.clone(), editable);
		productDialog.setTitle(title);
		//setting clone and not original object as a dialog's party field because the changes to the party will be rejected
		//if they are not accepted by pressing the dialog's OK button. If original object is set instead, changes remain
		//no matter what button was pressed
		productDialog.setVisible(true);
		if(productDialog.isChanged())
		{
			product = productDialog.getProduct();
			productDialog.setChanged(false);
			return product;
		}
		else
			return null;
	}

	/**
	 * Shows dialog for the sign up request with the CDR service.
	 * @param title title of the dialog
	 */
	public void showCDRSignUpDialog(String title)
	{
		boolean registerPressed = false;
		registerDialog = new RegisterDialog(RutaClientFrame.this, false, false, true);
		registerDialog.setTitle("Ruta Client - " + title);
		registerDialog.setVisible(true);
		registerPressed = registerDialog.isOKPressed();
		registerDialog.setOKPressed(false);
		if(registerPressed)
		{
			final String username = registerDialog.getUsername();
			final String password = registerDialog.getPassword();
			client.cdrRegisterMyParty(username, password);
		}
		else
			enablePartyMenuItems();
	}

	/**
	 * Shows dialog for the sign up request with the local database.
	 * @param title title of the dialog
	 * @param mayExit true when dialog can be discarded
	 * @return username
	 * @throws DetailException if Party could not be registered
	 */
	public String showLocalSignUpDialog(String title, boolean mayExit) throws DetailException
	{
		String username = null;
		registerDialog = new RegisterDialog(RutaClientFrame.this, false, true, mayExit);
		registerDialog.setTitle("Ruta Client - " + title);
		registerDialog.setVisible(true);
		if(registerDialog.isOKPressed())
		{
			registerDialog.setOKPressed(false);
			username = registerDialog.getUsername();
			final String password = registerDialog.getPassword();
			client.localRegisterMyParty(username, password);
			final Properties properties = client.getProperties();
			if(registerDialog.isRememberMe())
			{
				properties.setProperty("username", username);
				properties.setProperty("password", password);
			}
			else
			{
				properties.remove("username");
				properties.remove("password");
			}
		}
		EventQueue.invokeLater(() -> enablePartyMenuItems());
		return username;
	}

	/**
	 * Shows dialog for the log in request with the local database.
	 * @param title title of the dialog
	 * @return true if log in was successful
	 * @throws DatabaseException due to database connectivity issues
	 */
	public boolean showLocalLogInDialog(String title) throws DatabaseException
	{
		boolean success = false;
		registerDialog = new RegisterDialog(RutaClientFrame.this, true, true, false);
		registerDialog.setTitle("Ruta Client - " + title);
		registerDialog.setVisible(true);
		if(registerDialog.isOKPressed())
		{
			registerDialog.setOKPressed(false);
			registerDialog.setVisible(false);
			final String username = registerDialog.getUsername();
			final String password = registerDialog.getPassword();
			final Properties properties = client.getProperties();
			properties.setProperty("username", username);
			properties.setProperty("password", password);
			success = client.isLocalUserRegistеred();

			if(!registerDialog.isRememberMe())
			{
				properties.remove("username");
				properties.remove("password");
			}
		}
		return success;
	}

	/**
	 * Shows {@link SearchDialog} for entering the search criterion of the request and calls a method
	 * that makes the acctual request to the CDR.
	 * @param title {@code SearchDialog}'s title
	 */
	private void showSearchDialog(String title)
	{
		searchDialog = new SearchDialog(RutaClientFrame.this);
		searchDialog.setTitle(title);
		searchDialog.setVisible(true);
		if(searchDialog.isSearchPressed())
		{
			searchDialog.setSearchPressed(false);
			client.cdrSearch(searchDialog.getSearch(), false);
		}
	}

	/**
	 * Shows {@link OrderDialog} for making new {@link OrderType order}. {@code corr} argument should be set to {@code null}
	 * when new {@code Order} is to be created or old one viewed and to appropriate non-{@code null} value only when
	 * some old {@code Order} failed to be delievered and new sending atempt of it could be tried.
	 * @param title {@code OrderDialog}'s title
	 * @param order {@code Order} to display
	 * @param editable whether the Order is editable i.e. its quantity column
	 * @param corr {@link Correspondence} of the {@link OrderType}
	 * @return {@code OrderType} or {@code null} if user aborts Order creation
	 */
	public OrderType showOrderDialog(String title, OrderType order, boolean editable, Correspondence corr)
	{
		OrderDialog orderDialog = new OrderDialog(RutaClientFrame.this, order, editable, corr);
		orderDialog.setTitle(title);
		orderDialog.setVisible(true);
		if(orderDialog.isSendPressed())
		{
			order = orderDialog.getOrder();
			orderDialog.setSendPressed(false);
		}
		else
			order = null;
		return order;
	}

	/**
	 * Shows {@link PreviewOrderDialog} for displaying an {@link OrderType order}.
	 * @param title {@code OrderDialog}'s title
	 * @param order Order to display
	 */
	@Deprecated
	public void showPreviewOrderDialog(String title, OrderType order)
	{
		PreviewOrderDialog orderDialog = new PreviewOrderDialog(RutaClientFrame.this, order);
		orderDialog.setTitle(title);
		orderDialog.setVisible(true);
	}

	/**
	 * Shows {@link ProcessOrderDialog} for making a decision about a response to an {@link OrderType Order}.
	 * @param title dialog's title
	 * @param order order to make decision upon about the response
	 * @result {@code String} representing the decision
	 */
	public String showProcessOrderDialog(String title, OrderType order)
	{
		final ProcessOrderDialog processOrderDialog = new ProcessOrderDialog(RutaClientFrame.this, order);
		processOrderDialog.setTitle(title);
		processOrderDialog.setVisible(true);
		return processOrderDialog.getDecision();
	}

	/**
	 * Shows {@link OrderResponseDialog} for making new {@link OrderResponseType} document.  {@code corr}
	 * argument should be set to {@code null}
	 * when new {@code Order Response} is to be created or old one viewed and to appropriate non-{@code null} value only when
	 * some old {@code Order Response} failed to be delievered and new sending atempt of it could be tried.
	 * @param title dialog's title
	 * @param orderResponse Order Response to show and/or amend
	 * @param editable true if Order Response is to be created/changed, false if it is just a preview of one
	 * @param corr {@link Correspondence} if {@link OrderResponseType} is already a part of it; {@code null}
	 * otherwise i.e. should be created and appended to it
	 * @return Order Response or {@code null} if user has decided to abort the creation of it
	 */
	public OrderResponseType showOrderResponseDialog(String title, OrderResponseType orderResponse,
			boolean editable, Correspondence corr)
	{
		final OrderResponseDialog orderResponseDialog =
				new OrderResponseDialog(RutaClientFrame.this, orderResponse, editable, corr);
		orderResponseDialog.setTitle(title);
		orderResponseDialog.setVisible(true);
		if(orderResponseDialog.isSendPressed())
		{
			orderResponse = orderResponseDialog.getOrderResponse();
			orderResponseDialog.setSendPressed(false);
		}
		else
			orderResponse = null;
		return orderResponse;
	}

	/**
	 * Shows {@link ProcessOrderResponseDialog} for making a deciosion about a response to an
	 * {@link OrderResponseType Order Response}.
	 * @param orderResponse Order Response to make decision upon about the response
	 * @result {@code String} representing the decision
	 */
	public String showProcessOrderResponseDialog(OrderResponseType orderResponse)
	{
		final ProcessOrderResponseDialog processDialog = new ProcessOrderResponseDialog(RutaClientFrame.this, orderResponse);
		processDialog.setTitle("Process Order Response");
		processDialog.setVisible(true);
		return processDialog.getDecision();
	}

	/**
	 * Shows {@link ProcessOrderResponseSimpleDialog} for making a deciosion about a response to an
	 * {@link OrderResponseSimpleType Order Response Simple}.
	 * @param applicationResponse Order Response Simple to make decision upon about the response
	 * @result {@code String} representing the decision
	 */
	public String showProcessOrderResponseSimpleDialog(OrderResponseSimpleType orderResponseSimple)
	{
		final ProcessOrderResponseSimpleDialog processDialog =
				new ProcessOrderResponseSimpleDialog(RutaClientFrame.this, orderResponseSimple,
						orderResponseSimple.isAcceptedIndicatorValue(false));
		processDialog.setTitle("Process Order Response Simple");
		processDialog.setVisible(true);
		return processDialog.getDecision();
	}

	/**
	 * Shows {@link OrderResponseSimpleDialog} for making new {@link OrderResponseSimpleType} document.
	 * {@code corr} argument should be set to {@code null}
	 * when new {@code Order Response} is to be created or old one viewed and to appropriate non-{@code null} value only when
	 * some old {@code Order Response} failed to be delievered and new sending atempt of it could be tried.
	 * @param title dialog's title
	 * @param orderResponseSimple Order Response Simple to show and/or amend
	 * @param editable true if dialog data could be amended
	 * @param obsoleteCatalogue true if Order has been sent with the reference to some previous version
	 * of the Catalogue
	 * @param corr {@link Correspondence} of the {@link OrderResponseSimpleType}
	 * @return Order Response Simple or {@code null} if user has decided to discard the creation of it
	 */
	public OrderResponseSimpleType showOrderResponseSimpleDialog(String title,
			OrderResponseSimpleType orderResponseSimple, boolean editable, boolean obsoleteCatalogue, Correspondence corr)
	{
		final OrderResponseSimpleDialog orderResponseDialog =
				new OrderResponseSimpleDialog(RutaClientFrame.this, orderResponseSimple, editable, obsoleteCatalogue, corr);
		if(title != null)
			orderResponseDialog.setTitle(title);
		else if(orderResponseSimple.isAcceptedIndicatorValue(false))
			orderResponseDialog.setTitle("Accept Order");
		else
			orderResponseDialog.setTitle("Reject Order");
		orderResponseDialog.setVisible(true);
		if(orderResponseDialog.isSendPressed())
		{
			orderResponseDialog.setSendPressed(false);
			orderResponseSimple = orderResponseDialog.getOrderResponseSimple();
		}
		else
			orderResponseSimple = null;
		return orderResponseSimple;
	}

	/**
	 * Shows {@link OrderChangeDialog} for making new {@link OrderChangeType} document.
	 * @param title dialog's title
	 * @param orderChange Order Change to show and/or amend
	 * @param editable true if Order Change is to be created/changed, false if it is just a preview of one
	 * @param corr {@link Correspondence} if {@link OrderChangeType} is already a part of it; {@code null}
	 * otherwise i.e. should be created and appended to it
	 * @return Order Change or {@code null} if user has decided to abort the creation of it
	 */
	public OrderChangeType showOrderChangeDialog(String title, OrderChangeType orderChange,
			boolean editable, Correspondence corr)
	{
		final OrderChangeDialog orderChangeDialog =
				new OrderChangeDialog(RutaClientFrame.this, orderChange, editable, corr);
		orderChangeDialog.setTitle(title);
		orderChangeDialog.setVisible(true);
		if(orderChangeDialog.isSendPressed())
		{
			orderChange = orderChangeDialog.getOrderChange();
			orderChangeDialog.setSendPressed(false);
		}
		else
			orderChange = null;
		return orderChange;
	}

	/**
	 * Shows {@link OrderCancellationDialog} for making new {@link OrderCancellationType} document.
	 * @param title dialog's title
	 * @param orderCancellation Order Cancellation to show and/or amend
	 * @param editable true if Order Cancellation is to be created/changed, false if it is just a preview of one
	 * @param corr {@link Correspondence} if {@link OrderCancellationType} is already a part of it; {@code null}
	 * otherwise i.e. should be created and appended to it
	 * @return Order Cancellation or {@code null} if user has decided to abort the creation of it
	 */
	public OrderCancellationType showOrderCancellationDialog(String title, OrderCancellationType orderCancellation,
			boolean editable, Correspondence corr)
	{
		final OrderCancellationDialog orderCancellationDialog =
				new OrderCancellationDialog(RutaClientFrame.this, orderCancellation, editable, corr);
		orderCancellationDialog.setTitle(title);
		orderCancellationDialog.setVisible(true);
		if(orderCancellationDialog.isSendPressed())
		{
			orderCancellation = orderCancellationDialog.getOrderCancellation();
			orderCancellationDialog.setSendPressed(false);
		}
		else
			orderCancellation = null;
		return orderCancellation;
	}

	/**
	 * Shows {@link ApplicationResponseDialog} for making new {@link ApplicationResponseType} document.
	 * @param title dialog's title
	 * @param applicationResponse Application Response to show and/or amend
	 * @param editable true if Application Response is to be created/changed, false if it is just a preview of one
	 * @param corr {@link Correspondence} if {@link ApplicationResponseType} is already a part of it; {@code null}
	 * otherwise i.e. should be created and appended to it
	 * @return Application Resposne or {@code null} if user has decided to abort the creation of it
	 */
	public ApplicationResponseType showApplicationResponseDialog(String title, ApplicationResponseType applicationResponse,
			boolean editable, Correspondence corr)
	{
		final ApplicationResponseDialog appResponseDialog =
				new ApplicationResponseDialog(RutaClientFrame.this, applicationResponse, editable, corr);
		appResponseDialog.setTitle(title);
		appResponseDialog.setVisible(true);
		if(appResponseDialog.isSendPressed())
		{
			applicationResponse = appResponseDialog.getApplicationResponse();
			appResponseDialog.setSendPressed(false);
		}
		else
			applicationResponse = null;
		return applicationResponse;
	}

	/**
	 * Shows {@link ProcessApplicationResponseDialog} for making a deciosion about a response to an
	 * {@link ApplicationResponseType Application Response}.
	 * @param applicationResponse Application Response to make decision upon about the response
	 * @result {@code String} representing the decision
	 */
	public String showProcessApplicationResponseDialog(ApplicationResponseType applicationResponse)
	{
		final ProcessApplicationResponseDialog processDialog =
				new ProcessApplicationResponseDialog(RutaClientFrame.this, applicationResponse);
		processDialog.setTitle("Process Application Response");
		processDialog.setVisible(true);
		return processDialog.getDecision();
	}

	/**
	 * Shows {@link InvoiceDialog} for making new {@link InvoiceType Invoice}. {@code corr} argument should be set to {@code null}
	 * when new {@code Invoice} is to be created or old one viewed and to appropriate non-{@code null} value only when
	 * some old {@code Invoice} failed to be delievered and new sending atempt of it could be tried.
	 * @param title {@code InvoiceDialog}'s title
	 * @param invoice {@code Invoice} to display
	 * @param editable whether the Invoice is editable
	 * @param corr {@link Correspondence} of the {@link InvoiceType}
	 * @return {@code InvoiceType} or {@code null} if user aborts Invoice creation
	 */
	public InvoiceType showInvoiceDialog(String title, InvoiceType invoice, boolean editable, Correspondence corr)
	{
		InvoiceDialog invoiceDialog = new InvoiceDialog(RutaClientFrame.this, invoice, editable, corr);
		invoiceDialog.setTitle(title);
		invoiceDialog.setVisible(true);
		if(invoiceDialog.isSendPressed())
		{
			invoice = invoiceDialog.getInvoice();
			invoiceDialog.setSendPressed(false);
		}
		else
			invoice = null;
		return invoice;
	}

	/**
	 * Shows {@link ProcessInvoiceDialog} for making a decision about a response to an {@link InvoiceType Invoice}.
	 * @param invoice Invoice to make decision upon about the response
	 * @result {@code String} representing the decision
	 */
	public String showProcessInvoiceDialog(InvoiceType invoice)
	{
		final ProcessInvoiceDialog processInvoiceDialog = new ProcessInvoiceDialog(RutaClientFrame.this, invoice);
		processInvoiceDialog.setTitle("Process Invoice");
		processInvoiceDialog.setVisible(true);
		return processInvoiceDialog.getDecision();
	}

	public RutaClient getClient()
	{
		return client;
	}

	/**
	 * Repaints currently selected tab.
	 */
	@Override
	public void repaint()
	{
		repaint(tabbedPane.getSelectedIndex());
	}

	/**
	 * Repaints tab with passed index.
	 * @param tabIndex index of the tab to repaint
	 */
	public void repaint(int tabIndex)
	{
		super.repaint();
		tabbedPane.setSelectedIndex(tabIndex);
		loadTab(tabIndex);
	}

	/**
	 * Updates the main frame's title.
	 * @param partyName party name that should be shown as a part of the title
	 */
	public void updateTitle(String partyName)
	{
		setTitle("Ruta Client - " + partyName);
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		final Object source = event.getSource();
		final Class<? extends ActionEvent> eventClazz = event.getClass();
		if(eventClazz == BusinessPartyEvent.class)
		{
			tabCDR.dispatchEvent(event);
			tabCorrespondences.dispatchEvent(event);
			if(tabbedPane.getSelectedIndex() == TAB_CDR_DATA ||
					tabbedPane.getSelectedIndex() == TAB_CORRESPONDENSCES)
				repaint();
		}
		else if(eventClazz == PartnershipEvent.class)
		{
			tabCDR.dispatchEvent(event);
			if(tabbedPane.getSelectedIndex() == TAB_CDR_DATA)
				repaint();
		}
		else if(eventClazz == CorrespondenceEvent.class)
		{
			tabCorrespondences.dispatchEvent(event);
			if(tabbedPane.getSelectedIndex() == TAB_CORRESPONDENSCES)
				repaint();
		}
		else if(eventClazz == SearchEvent.class)
		{
			tabCDR.dispatchEvent(event);
			if(source instanceof Search) // do not show TAB_CDR_DATA only when whole list is deleted
				repaint(TAB_CDR_DATA);
		}
		else if(eventClazz == ItemEvent.class)
		{
			tabProducts.dispatchEvent(event);
			if(tabbedPane.getSelectedIndex() == TAB_PRODUCTS)
				repaint();
		}
	}

	/**
	 * Appends current date and time and passed coloured string to the console. All this is done inside the
	 * {@link EventQueue}.
	 * @param textBuilder {@link StringBuilder string} to be shown on the console
	 * @param color colour of the string
	 */
	public void appendToConsole(StringBuilder textBuilder, Color color)
	{
		if(consolePane != null) // do not append to console anything before it is instantiated
			EventQueue.invokeLater(()->
			{
				StyleContext sc = StyleContext.getDefaultStyleContext();
				AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.BLACK);

				DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.MEDIUM);
				StyledDocument doc = consolePane.getStyledDocument();
				try
				{
					doc.insertString(doc.getLength(), formatter.format(LocalDateTime.now()) + ": ", aset);
					aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, color);
					doc.insertString(doc.getLength(), textBuilder.append("\n").toString(), aset);
				}
				catch (BadLocationException e)
				{
					getLogger().error("Exception is ", e);
				}
				consolePane.setCaretPosition(consolePane.getDocument().getLength());
			});
	}

	/**
	 * Processes exception thrown by called webmethod or some local one.
	 * @param e exception to be processed
	 * @param msgBuilder {@link StringBuilder message} to be processed for display on the console
	 * @param recursion true when the recursive method call is permissable
	 * @return message to be displayed on the console
	 */
	private StringBuilder processException(Exception e, StringBuilder msgBuilder, boolean recursion)
	{
		msgBuilder = msgBuilder.append(" ");
		final Throwable cause = e.getCause();
		final String errorMessage = e.getMessage();
		if(errorMessage != null && (cause == null || (cause != null && cause.getClass() != RutaException.class)))
			if(e instanceof RutaException)
				msgBuilder.append(" Caused by: ").append(errorMessage).append(" ").
				append(((RutaException) e).getFaultInfo().getDetail());
			else
				msgBuilder.append(" Caused by: ").append(trimSOAPFaultMessage(errorMessage));
		if(cause != null)
		{
			if(cause instanceof RutaException)
				msgBuilder.append(" Caused by: ").append(cause.getMessage()).append(" ").
				append(((RutaException) cause).getFaultInfo().getDetail());
			else if(cause.getMessage() != null)
				msgBuilder.append(" Caused by: ").append(trimSOAPFaultMessage(cause.getMessage()));
		if(cause.getCause() != null)
			processException((Exception) cause.getCause(), msgBuilder, false);
		}
		return msgBuilder;
	}

	/**
	 * Processes exception in a way that depends on whether it is thrown by webmethod or some local method and displays
	 * exception message on the console.
	 * @param e exception to be processed
	 * @param msg {@link StringBuilder message} to be displayed on the console
	 */
	public void processExceptionAndAppendToConsole(Exception e, StringBuilder msgBuilder)
	{
/*		msgBuilder = msgBuilder.append(" ");
		final Throwable cause = e.getCause();
		final String errorMessage = e.getMessage();
		if(errorMessage != null)
			msgBuilder.append(errorMessage);
		if(cause != null)
		{
			if(cause instanceof RutaException)
				msgBuilder.append("Server responds: ").append(cause.getMessage()).append(" ").
				append(((RutaException) cause).getFaultInfo().getDetail());
			else
				msgBuilder.append(" Cause is: ").append(trimSOAPFaultMessage(cause.getMessage()));
		}*/
		processException(e, msgBuilder, true);
		appendToConsole(msgBuilder, Color.RED);
		getLogger().error(msgBuilder.toString() + "\nException is ", e);
	}

	/**
	 * Removes automatically prepended and appended portion of the SOAPFault detail string.
	 * @param message string to be processed
	 * @return trimmed string
	 */
	private String trimSOAPFaultMessage(String message)
	{
		return message.replaceFirst("(.*?)Client received SOAP Fault from server: (.+) "
				+ "Please see the server log to find more detail regarding exact cause of the failure.", "$2");
	}



}