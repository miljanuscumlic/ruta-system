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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oasis.names.specification.ubl.schema.xsd.catalogue_21.CatalogueType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyIdentificationType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyNameType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import rs.ruta.client.BusinessParty;
import rs.ruta.client.MyParty;
import rs.ruta.client.Party;
import rs.ruta.client.RutaClient;
import rs.ruta.client.RutaClientFrameEvent;
import rs.ruta.client.Search;
import rs.ruta.client.datamapper.MyPartyXMLFileMapper;
import rs.ruta.common.BugReport;
import rs.ruta.common.BugReportSearchCriterion;
import rs.ruta.common.Associates;
import rs.ruta.common.InstanceFactory;
import rs.ruta.common.datamapper.RutaException;

public class RutaClientFrame extends JFrame implements ActionListener
{
	private static final long serialVersionUID = -6582749886269431483L;
	private static final String DEFAULT_WIDTH = "1000";
	private static final String DEFAULT_HEIGHT = "800";
	private static final Logger logger = LoggerFactory.getLogger("rs.ruta.client");
	public static final int CDR_DATA_TAB = 0;
	public static final int MY_PRODUCTS_TAB = 1;
	public static final int CORRESPONDENSCE_TAB = 2;

	private RutaClient client;
	private JTabbedPane tabbedPane;
	private JSplitPane splitPane;
	private JTextPane consolePane;
	private AboutDialog aboutDialog;
	private UpdateDialog updateDialog;
	private PartyDialog partyDialog;
	private RegisterDialog registerDialog;
	private SearchDialog searchDialog;
	private CDRSettingsDialog settingsDialog;
	private NotifyDialog notifyDialog;
	private BugReportDialog bugReportDialog;
	private BugExploreDialog bugExploreDialog;
	private JFileChooser chooser;

	private JMenuItem myPartyItem = new JMenuItem("My Party");
	private JMenuItem myCatalogueItem = new JMenuItem("My Products");
	private JMenuItem saveDataItem = new JMenuItem("Save");
	private JMenuItem exportDataItem = new JMenuItem("Export");
	private JMenuItem importDataItem = new JMenuItem("Import");
	private JMenuItem localRegisterPartyItem = new JMenuItem("Register My Party");
	private JMenuItem localDeregisterPartyItem = new JMenuItem("Deregister My Party");

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
	private TabComponent tabMyProducts;
	private TabComponent tabCorrespondences;

	public RutaClientFrame(RutaClient client)
	{
		this.client = client;
		this.client.setFrame(this);

		//get frame related properties
		final Properties properties = client.getProperties();
		final int left = Integer.parseInt(properties.getProperty("mainFrame.left", "0"));
		final int top = Integer.parseInt(properties.getProperty("mainFrame.top", "0"));
		final int width = Integer.parseInt(properties.getProperty("mainFrame.width", DEFAULT_WIDTH));
		final int height = Integer.parseInt(properties.getProperty("mainFrame.height", DEFAULT_HEIGHT));
		setBounds(left, top, width, height);
		final String title = properties.getProperty("mainFrame.title", "Ruta Client");
		setTitle(title);

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
		});

		//setting tabs
		tabbedPane = new JTabbedPane();
		tabCDR = new TabCDRData(this);
		tabbedPane.addTab("CDR Data", tabCDR);
		tabMyProducts = new TabProducts(this);
		tabbedPane.addTab("Products & Services", tabMyProducts);
		tabCorrespondences = new TabCorrespondences(this);
		tabbedPane.addTab("Correspondences", tabCorrespondences);

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
		localDataMenu.addSeparator();
		localDataMenu.add(saveDataItem);
		localDataMenu.add(exportDataItem);
		localDataMenu.add(importDataItem);
		localDataMenu.addSeparator();
		localDataMenu.add(localRegisterPartyItem);
		localDataMenu.add(localDeregisterPartyItem);

		myPartyItem.addActionListener(event ->
		{
			showPartyDialog(client.getMyParty().getCoreParty(), "My Party", false);
		});

		myCatalogueItem.addActionListener(event ->
		{
			tabbedPane.setSelectedIndex(MY_PRODUCTS_TAB);
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
						MyParty myParty;
						if(parties.size() != 0)
						{
							//temporary code for importing core Party only
							myParty = client.getMyParty();
							String oldPartyID = myParty.getPartyID();
							myParty.setCoreParty(parties.get(0).getCoreParty());
							myParty.setPartyID(oldPartyID);
							updateTitle(myParty.getPartySimpleName());

							/*							//MMM:old code for importing data - should be improved for the release version
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
			final MyParty myParty = client.getMyParty();
			if(myParty.isRegisteredWithLocalDatastore())
				appendToConsole(new StringBuilder("My Party is already registered with the local datastore!"), Color.BLUE);
			else
			{
				disablePartyMenuItems();
				new Thread(() ->
				{
					if(!myParty.isRegisteredWithLocalDatastore())
						client.setInitialUsername(showLocalSignUpDialog("Local database registration"));
				}).start();
			}
		});

		localDeregisterPartyItem.addActionListener(event ->
		{
			MyParty myParty = client.getMyParty();
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
					}).start();
				}
			}
			else
				appendToConsole(new StringBuilder("Deregistration request of My Party has not been sent to the local datastore.").
						append(" My Party is not registered with the local datastore!"), Color.RED);
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
			final MyParty myParty = client.getMyParty();
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
						final String username = showLocalSignUpDialog("Local database registration");
						if(username == null)
							cdrRegistration = false;
						else
							client.setInitialUsername(username);
					}
					if(cdrRegistration)
						showCDRSignUpDialog("CDR registration");
				}).start();
			}
		});

		cdrUpdatePartyItem.addActionListener(event ->
		{
			final MyParty myParty = client.getMyParty();
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

		cdrDeregisterPartyItem.addActionListener(event ->
		{
			MyParty myParty = client.getMyParty();
			if(myParty.isRegisteredWithCDR())
			{
				EventQueue.invokeLater(() ->
				{
					int option = JOptionPane.showConfirmDialog(RutaClientFrame.this,
							"By deregistering My Party from the CDR service, all your data in the CDR will be deleted\n" +
									"and all your followers will be notified about your deregistration.\nDo you want to proceed?",
									"Warning message", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if(option == JOptionPane.YES_OPTION)
					{
						disablePartyMenuItems();
						new Thread(() ->
						{
							client.cdrDeregisterMyParty();
						}).start();
					}
				});
			}
			else
				appendToConsole(new StringBuilder("Deregistration request of My Party has not been sent to the CDR service.").
						append(" My Party is not registered with the CDR service!"), Color.RED);
		});

		cdrUpdateCatalogueItem.addActionListener(event ->
		{
			MyParty myParty = client.getMyParty();
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
							appendToConsole(new StringBuilder("My Catalogue has not been sent to the CDR service because it is malformed. ").
									append(e.getMessage()), Color.RED);
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
			MyParty myParty = client.getMyParty();
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
			MyParty myParty = client.getMyParty();
			if(myParty.isRegisteredWithCDR())
			{
				if(myParty.isCatalogueInCDR())
				{
					EventQueue.invokeLater(() ->
					{
						int option = JOptionPane.showConfirmDialog(RutaClientFrame.this,
								"By deleting your catalogue from the CDR all your followers will be\nnotified about the catalogue deletion. Do you want to proceed?",
								"Warning message", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
						if(option == JOptionPane.YES_OPTION)
						{
							disableCatalogueMenuItems();
							new Thread( () ->
							{
								client.cdrDeleteMyCatalogue();
							}).start();
						}
					});
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
				String endPoint = settingsDialog.getServiceLocation();
				RutaClient.setCDREndPoint(endPoint);
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
				client.cdrClearCache();
			}).start();
		});

		tabbedPane.setSelectedIndex(1);
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
	 * Enables menu items regarding Search after client gets the response from the CDR service.
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
	 * Disables menu items regarding Search after client sends the request to the CDR service.
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
	 * Enables menu items regarding My Party after client gets the response from the CDR service.
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
	 * Disables menu items regarding My Party after client sends the request to the CDR service.
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
	 * Enables menu items regarding My Catalogue after client gets the response from the CDR service.
	 * Method's body is executed in the {@link EventQueue}.
	 */
	public void enableCatalogueMenuItems()
	{
		EventQueue.invokeLater(() ->
		{
			cdrPullCatalogueItem.setEnabled(true);
			cdrUpdateCatalogueItem.setEnabled(true);
			cdrDeleteCatalogueItem.setEnabled(true);
			cdrDeregisterPartyItem.setEnabled(true);
		});
	}

	/**
	 * Disables menu items regarding My Catalogue after client sends the request to the CDR service.
	 * Method's body is executed in the {@link EventQueue}.
	 */
	public void disableCatalogueMenuItems()
	{
		EventQueue.invokeLater(() ->
		{
			cdrPullCatalogueItem.setEnabled(false);
			cdrUpdateCatalogueItem.setEnabled(false);
			cdrDeleteCatalogueItem.setEnabled(false);
			cdrDeregisterPartyItem.setEnabled(false);
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
		properties.put("mainFrame.title", "Ruta Client - " + getTitle());

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
		case CDR_DATA_TAB:
			component = tabCDR;
			break;
		case MY_PRODUCTS_TAB:
			component = tabMyProducts;
			break;
		case CORRESPONDENSCE_TAB:
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
	 * @param registration whether the dialog is shown during local database registration
	 * @return {@code Party} with potentially changed data
	 */
	//MMM: boolean argument editable could be added; = false if party should only be displayed not changed
	public Party showPartyDialog(Party party, String title, boolean registration)
	{
		partyDialog = new PartyDialog(RutaClientFrame.this, registration);
		partyDialog.setTitle(title);
		if(registration)
		{
			partyDialog.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
			partyDialog.addWindowListener(new WindowAdapter()
			{
				@Override
				public void windowClosing(WindowEvent e)
				{
					EventQueue.invokeLater( () ->
					JOptionPane.showMessageDialog(RutaClientFrame.this,
							"Entering My Party data is mandatory step during application setup."));
				}
			});
		}
		//setting clone not original object as a dialog's party field because the changes to the party will be rejected
		//if they are not accepted by pressing the dialog's OK button. If original object is set, changes remain
		//no matter what button is pressed
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

			/*			MMM: not necessary to write to local database
			 * 			new Thread(()->
			{
				try
				{
					client.insertMyParty();
				}
				catch (Exception e)
				{
					JOptionPane.showMessageDialog(RutaClientFrame.this, "Could not save data to the local data store!",
							"Saving data to the local data store", JOptionPane.ERROR_MESSAGE);
				}
			}).start();*/
		}
		return party;
	}

	/**
	 * Shows dialog for the sign up request with the CDR service.
	 * @param title title of the dialog
	 */
	public void showCDRSignUpDialog(String title)
	{
		boolean registerPressed = false;
		registerDialog = new RegisterDialog(RutaClientFrame.this);
		registerDialog.setTitle(title);
		registerDialog.setVisible(true);
		registerPressed = registerDialog.isRegisterPressed();
		registerDialog.setRegisterPressed(false);
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
	 * Shows dialog for the sign up/log in request with the local database.
	 * @param title title of the dialog
	 * @return username
	 */
	public String showLocalSignUpDialog(String title)
	{
		String username = null;
		boolean registerPressed = false;
		registerDialog = new RegisterDialog(RutaClientFrame.this);
		registerDialog.setTitle(title);
		registerDialog.setVisible(true);
		registerPressed = registerDialog.isRegisterPressed();
		registerDialog.setRegisterPressed(false);
		if(registerPressed)
		{
			username = registerDialog.getUsername();
			client.getProperties().setProperty("username", username);
			final String password = registerDialog.getPassword();
			client.localRegisterMyParty(username, password);
		}
		enablePartyMenuItems();
		return username;
	}

	/**
	 * Shows {@link SearchDialog} for entering the search criterion of the request and calls a method
	 * that makes the acctual request to the CDR.
	 * @param title {@code SearchDialog} title
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

	public RutaClient getClient()
	{
		return client;
	}

	/**
	 * Appends current date and time and passed coloured string to the console. All this is done inside the
	 * {@link EventQueue}.
	 * @param textBuilder {@link StringBuilder string} to be shown on the console
	 * @param color colour of the string
	 */
	public void appendToConsole(StringBuilder textBuilder, Color color)
	{
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
		final String command = event.getActionCommand();
		if(source.getClass() == BusinessParty.class)
		{
			BusinessParty party = (BusinessParty) source;

			if(RutaClientFrameEvent.PARTY_UPDATED.equals(command) ||
					RutaClientFrameEvent.CATALOGUE_UPDATED.equals(command) ||
					RutaClientFrameEvent.PARTY_MOVED.equals(command) ||
					RutaClientFrameEvent.SELECT_NEXT.equals(command))
			{
				tabCDR.dispatchEvent(event);
				repaint(CDR_DATA_TAB);
			}


		}

	}

	/**
	 * Processes exception thrown by called webmethod or some local one.
	 * @param e exception to be processed
	 * @param msg {@link StringBuilder message} to be displayed on the console
	 * @return TODO
	 */
	public StringBuilder processException(Exception e, StringBuilder msgBuilder)
	{
		getLogger().error("Exception is ", e);
		msgBuilder = msgBuilder.append(" ");
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
		getLogger().error("Exception is ", e);
		msgBuilder = msgBuilder.append(" ");
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
		appendToConsole(msgBuilder, Color.RED);
	}

	/**
	 * Removes automatically prepended and appended portion of the SOAPFault detail string.
	 * @param message string to be processed
	 * @return trimmed string
	 */
	private String trimSOAPFaultMessage(String message)
	{
		return message.replaceFirst("Client received SOAP Fault from server: (.+) "
				+ "Please see the server log to find more detail regarding exact cause of the failure.", "$1");
	}
}