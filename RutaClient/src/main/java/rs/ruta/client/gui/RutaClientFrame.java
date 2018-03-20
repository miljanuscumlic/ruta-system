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
import rs.ruta.client.Search;
import rs.ruta.client.datamapper.MyPartyXMLFileMapper;
import rs.ruta.common.BugReport;
import rs.ruta.common.BugReportSearchCriterion;
import rs.ruta.common.Followers;
import rs.ruta.common.InstanceFactory;

public class RutaClientFrame extends JFrame
{
	private static final long serialVersionUID = -6582749886269431483L;
	private static final String DEFAULT_WIDTH = "1000";
	private static final String DEFAULT_HEIGHT = "800";
	private static final Logger logger = LoggerFactory.getLogger("rs.ruta.client");
	public static final int CDR_DATA_TAB = 0;
	public static final int MY_PRODUCTS_TAB = 1;
	public static final int DOCUMENTS_TAB = 2;
	public static final int CORRESPONDENSCE_TAB = 3;

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
	//	private Component tab0RightPane;
	//	private Component tab0LeftPane;
	private Component leftPane;
	private Component rightPane;
	private JComponent tab0Pane;
	private JFileChooser chooser;
	private JTree partyTree;
	private JTree searchTree;

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

	private TabCDRData tabCDR;
	private TabProducts tabMyProducts;

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

		//file chooser
		chooser = new JFileChooser();
		final FileFilter filter = new FileNameExtensionFilter("XML files", "xml");
		chooser.setFileFilter(filter);

		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent event)
			{
				sendDummyEvent();
				System.exit(0);
			}

			@Override
			public void windowIconified(WindowEvent e)
			{
				sendDummyEvent();
			}

			@Override
			public void windowLostFocus(WindowEvent e)
			{
				sendDummyEvent();
			}

			@Override
			public void windowDeactivated(WindowEvent e)
			{
				sendDummyEvent();
			}

			/**
			 * Sends dummy {@link MouseEvent mouse event} to trigger {@code focusTracker} event listener which will save
			 * the data of a last edited cell of the table in current view if it is still in editing state.
			 */
			private void sendDummyEvent()
			{
				tabbedPane.getComponent(tabbedPane.getSelectedIndex()).dispatchEvent(
						new MouseEvent(RutaClientFrame.this, MouseEvent.MOUSE_CLICKED, 1, 0, 0, 0, 1, false));
			}
		});

		//setting tabs
		tabbedPane = new JTabbedPane();
		tabCDR = new TabCDRData(this);
		tabbedPane.addTab("CDR Data", tabCDR.getComponent());
		tabMyProducts = new TabProducts(this);
		tabbedPane.addTab("Products & Services", tabMyProducts.getComponent());
		tabbedPane.addTab("Documents", null);
		tabbedPane.addTab("Correspondences", null);

		tabbedPane.addChangeListener(event ->
		{
			loadTab(tabbedPane.getSelectedIndex(), null, false, false);
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
		JMenu cdrMenu = new JMenu("Central Data Repository");
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
			tabbedPane.setSelectedIndex(1);
		});

		saveDataItem.addActionListener(event ->
		{
			appendToConsole("Storing data to the local data store...", Color.BLACK);
			new Thread(() ->
			{
				try
				{
					if(client.getMyParty().isRegisteredWithLocalDatastore())
					{
						client.getMyParty().storeData();
						appendToConsole("Data has been saved to the local data store.", Color.GREEN);
					}
					else
						appendToConsole("Could not save data. Party is not registered with the local data store!", Color.RED);
				}
				catch (Exception e)
				{
					appendToConsole("There has been an error. Could not save data to the local data store!", Color.RED);
					logger.error("Could not save data to the local data store!", e);
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
						final MyPartyXMLFileMapper<MyParty> partyDataMapper = new MyPartyXMLFileMapper<MyParty>(client.getMyParty(), filePath);
						final ArrayList<MyParty> parties = (ArrayList<MyParty>) partyDataMapper.findAll();
						MyParty myParty;
						if(parties.size() != 0)
						{
							//temporary code for importing core Party only
							myParty = client.getMyParty();
							myParty.setCoreParty(parties.get(0).getCoreParty());
							updateTitle(myParty.getPartySimpleName());

/*							myParty = parties.get(0);
							client.setMyParty(myParty);
							updateTitle(myParty.getPartySimpleName());
							Search.setSearchNumber(myParty.getSearchNumber());*/

							repaintTabbedPane(null, false, false); // frame update
							appendToConsole("Local data have been successfully imported from the file: " + filePath, Color.GREEN);
						}
					}
					catch(JAXBException e)
					{
						appendToConsole("Could not import data from the chosen file. The file is corrupt!", Color.RED);
					}
					catch (Exception e)
					{
						appendToConsole("There has been an error. " + e.getMessage(), Color.RED);
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
					appendToConsole("Local data have been successfully exported to the file: " + filePath, Color.GREEN);
				}
				catch (JAXBException e)
				{
					appendToConsole("There has been an error. Local data could not be exported to the file: " + filePath, Color.RED);
				}
				catch (Exception e)
				{
					appendToConsole("There has been an error. " + e.getMessage(), Color.RED);
					//					JOptionPane.showMessageDialog(RutaClientFrame.this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		localRegisterPartyItem.addActionListener(event ->
		{
			final MyParty myParty = client.getMyParty();
			if(myParty.isRegisteredWithLocalDatastore())
				appendToConsole("My Party is already registered with the local datastore!", Color.BLUE);
			else
			{
				disablePartyMenuItems();
				new Thread(() ->
				{
//					if(client.getInitialUsername() == null)
					if(!myParty.isRegisteredWithLocalDatastore())
						client.setInitialUsername(showLocalSignUpDialog("Local database registration"));
				}).start();
			}
		});

		localDeregisterPartyItem.addActionListener(event ->
		{
			MyParty myParty = client.getMyParty();
			if(myParty.isRegisteredWithCDR())
				appendToConsole("Deregistration request of My Party has not been sent to the local datastore."
						+ " If you want to deregister My Party locally you have to deregister it from the CDR service first!", Color.RED);
			else if(myParty.isRegisteredWithLocalDatastore())
			{
				EventQueue.invokeLater(() ->
				{
					int option = JOptionPane.showConfirmDialog(RutaClientFrame.this,
							"By deregistering My Party from the local datastore, all your data in the store will be deleted.\n" +
									"Do you want to proceed?", "Warning message", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if(option == JOptionPane.YES_OPTION)
					{
						disablePartyMenuItems();
						new Thread(() ->
						{
							client.localDeregisterMyParty();
						}).start();
					}
				});
			}
			else
				appendToConsole("Deregistration request of My Party has not been sent to the local datastore."
						+ " My Party is not registered with the local datastore!", Color.RED);
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
				appendToConsole("Request for new documents has not been sent to the CDR service."
						+ " My Party should be registered with the CDR service first!", Color.RED);
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
				appendToConsole("Search request can not be composed. My Party should be registered with the CDR service first!",
						Color.RED);
		});

		cdrRegisterPartyItem.addActionListener(event ->
		{
			final MyParty myParty = client.getMyParty();
			if(myParty.isRegisteredWithCDR())
				appendToConsole("My Party is already registered with the CDR service!", Color.BLUE);
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
					disablePartyMenuItems();
					new Thread(() ->
					{
						client.cdrSynchroniseMyParty();

					}).start();
				}
				else
					appendToConsole("My Party is already updated on the CDR service!", Color.BLUE);
			}
			else
				appendToConsole("Update request of My Party has not been sent to the CDR service."
						+ " My Party should be registered with the CDR service first!", Color.RED);
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
				appendToConsole("Deregistration request of My Party has not been sent to the CDR service."
						+ " My Party is not registered with the CDR service!", Color.RED);
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
						client.cdrSynchroniseMyCatalogue();
					}).start();
				}
				else
					appendToConsole("Catalogue is already updated on the CDR service.", Color.BLUE);
			}
			else
				appendToConsole("Update request of My Catalogue has not been sent to the CDR service."
						+ " My Party should be registered with the CDR service first!", Color.RED);
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
				appendToConsole("Pull request for My Catalogue has not been sent to the CDR service."
						+ " My Party should be registered with the CDR service first!", Color.RED);
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
								"By deleting your catalogue from the CDR all your followers\nwill be notified about the catalogue deletion. Do you want to proceed?",
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
					appendToConsole("Deletion request of My Catalogue has not been sent to the CDR service."
							+ " My Catalogue is not present in the CDR!", Color.RED);
			}
			else
				appendToConsole("Deletion request of My Catalogue has not been sent to the CDR service."
						+ " My Party should be registered with the CDR service first!", Color.RED);
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
		JMenuItem reportBugItem = new JMenuItem("Report a Bug");
		helpMenu.add(reportBugItem);
		JMenuItem exploreBugItem = new JMenuItem("Explore the Bugs");
		helpMenu.add(exploreBugItem);
		JMenuItem fileItem = new JMenuItem("Send a File");
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
				appendToConsole("Bug reports cannot be explored by non-registered party.", Color.RED);
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
			appendToConsole("Bug report cannot be issued by non-registered party.", Color.RED);
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
			appendToConsole("Bug report list cannot be requested by non-registered party.", Color.RED);
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
			appendToConsole("Bug report list cannot be requested by non-registered party.", Color.RED);
		return future;
	}

	/**
	 * Sends the follow request to the CDR if the party to be followed is not My Party or already has been followed.
	 * If argument {@code partner} is set to {@code true} following party is set as a {@code Business Parter}.
	 * @param followingName name of the party to follow
	 * @param followingID ID of the party to follow
	 * @param partner whether following party should be set as a {@code Business Parter}
	 */
	public void followParty(String followingName, String followingID, boolean partner)
	{
		MyParty myParty = client.getMyParty();
		if(followingID.equals(myParty.getPartyID()))
			appendToConsole("My Party is already in the following list.", Color.BLACK);
		else
		{
			BusinessParty following = myParty.getFollowingParty(followingID);
			if(following == null)
				client.cdrFollowParty(followingName, followingID, partner);
			else if(partner && !following.isPartner())
			{
				//move following to the business partner list
				following.setPartner(true);
				myParty.followParty(following);
				appendToConsole("Party " + followingName + " is already in the following list. But from now on it is marked as a business partner.",
						Color.GREEN);

				repaintTabbedPane(null, false, false);

			}
			else
				appendToConsole("Party " + followingName + " is already in the following list.", Color.BLACK);
		}
	};

	/**
	 * Enables menu items regarding Search after client gets the response from the CDR service.
	 * Method code is executed in the {@link EventQueue}.
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
	 * Method code is executed in the {@link EventQueue}.
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
	 * Method code is executed in the {@link EventQueue}.
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
	 * Method code is executed in the {@link EventQueue}.
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
	 * Method code is executed in the {@link EventQueue}.
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
	 * Method code is executed in the {@link EventQueue}.
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
	 * Repaints selected tab.
	 * @param tabIndex tab's index
	 * @param userObject user object of the node that is making the change in the model or {@code null}
	 * @param added true if node is to be added to the view or to be changed, false if not
	 * @param removed true if node is to be removed from the view or to be changed, false if not
	 */
	private void loadTab(int tabIndex,@Nullable Object userObject, boolean added, boolean removed)
	{
		String title = tabbedPane.getTitleAt(tabIndex);
		JComponent component = null;
		switch(tabIndex)
		{
		case CDR_DATA_TAB:
//			tabCDR.repaint(userObject, added, removed);
			component = (JComponent) tabCDR.getComponent();
			break;
		case MY_PRODUCTS_TAB:
//			tabMyProducts.repaint(userObject, added, removed);
			component = (JComponent) tabMyProducts.getComponent();
			break;
		case DOCUMENTS_TAB:
			component = new JLabel(title);
			break;
		case CORRESPONDENSCE_TAB:
			component = new JLabel(title);
			break;
		}
		tabbedPane.setComponentAt(tabIndex, component);
	}

	private JTable createCatalogueTable(AbstractTableModel tableModel)
	{
		JTable table = new JTable(tableModel)
		{
			private static final long serialVersionUID = -2879401192820075582L;
			//implementing column header's tooltips
			@Override
			protected JTableHeader createDefaultTableHeader()
			{
				return new JTableHeader(columnModel)
				{
					private static final long serialVersionUID = -2681152311259025964L;
					@Override
					public String getToolTipText(MouseEvent e)
					{
						java.awt.Point p = e.getPoint();
						int index = columnModel.getColumnIndexAtX(p.x);
						int realIndex = columnModel.getColumn(index).getModelIndex();
						switch(realIndex)
						{
						case 3:
							return "integer numbers; 0 for field deletion";
						case 4:
							return "ID field is mandatory if Barcode is going to be entered";
						case 7:
							return "comma separeted values";
						default:
							return null;
						}
					}
				};
			}
		};

		table.setFillsViewportHeight(true);

		TableColumnModel colModel = table.getColumnModel();
		colModel.getColumn(0).setPreferredWidth(20);
		colModel.getColumn(1).setPreferredWidth(200);
		colModel.getColumn(2).setPreferredWidth(200);
		colModel.getColumn(3).setPreferredWidth(20);
		colModel.getColumn(7).setPreferredWidth(200);
		return table;
	}

	/**
	 * Creates and formats the view of empty table that would contain data from the list of parties.
	 * @param tableModel model representing the list of parties to be displayed
	 * @return created table
	 */
	private JTable newEmptyPartyListTable(AbstractTableModel tableModel)
	{
		JTable table = new JTable(tableModel);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		TableColumnModel colModel = table.getColumnModel();
		colModel.getColumn(0).setPreferredWidth(20);
		colModel.getColumn(1).setPreferredWidth(150);
		colModel.getColumn(2).setPreferredWidth(200);
		colModel.getColumn(3).setPreferredWidth(100);
		colModel.getColumn(4).setPreferredWidth(100);
		colModel.getColumn(5).setPreferredWidth(100);
		colModel.getColumn(6).setPreferredWidth(100);
		colModel.getColumn(7).setPreferredWidth(100);
		colModel.getColumn(8).setPreferredWidth(100);
		colModel.getColumn(9).setPreferredWidth(100);
		colModel.getColumn(10).setPreferredWidth(100);
		colModel.getColumn(11).setPreferredWidth(100);
		table.setFillsViewportHeight(true);
		return table;
	}

	/**
	 * Creates table showing list of parties e.g. Business Partners, Other Parties etc.
	 * @param tableModel model containing party data to display
	 * @param search true if the table displays search results, and false if it displays list of parties
	 * @return constructed table object
	 */
	private JTable createPartyListTable(AbstractTableModel tableModel)
	{
		final JTable table = newEmptyPartyListTable(tableModel);

		final JPopupMenu partyTablePopupMenu = new JPopupMenu();
		final JMenuItem unfollowPartyItem = new JMenuItem("Unfollow party");
		final JMenuItem addPartnerItem = new JMenuItem("Add to Business Partners");
		final JMenuItem removePartnerItem = new JMenuItem("Remove from Business Partners");
		final MyParty myParty = client.getMyParty();

		unfollowPartyItem.addActionListener(event ->
		{
			final int rowIndex = table.rowAtPoint(partyTablePopupMenu.getBounds().getLocation());
			final BusinessParty selectedParty = ((PartyListTableModel) table.getModel()).getParty(rowIndex);
			new Thread(()->
			{
				client.cdrUnfollowParty(selectedParty);
			}).start();
		});

		addPartnerItem.addActionListener(event ->
		{
			final int rowIndex = table.rowAtPoint(partyTablePopupMenu.getBounds().getLocation());
			final BusinessParty selectedParty = ((PartyListTableModel) table.getModel()).getParty(rowIndex);
			{
				selectedParty.setPartner(true);
				myParty.followParty(selectedParty);
				appendToConsole("Party " + selectedParty.getPartySimpleName() + " has been moved from Other Parties to Business Partners. "
						+ "Party is still followed by My Party.", Color.GREEN);
				repaintTabbedPane(null, false, false);
				//				((AbstractTableModel) table.getModel()).fireTableDataChanged();
			}
		});

		removePartnerItem.addActionListener(event ->
		{
			final int rowIndex = table.rowAtPoint(partyTablePopupMenu.getBounds().getLocation());
			final BusinessParty selectedParty = ((PartyListTableModel) table.getModel()).getParty(rowIndex);
			{
				selectedParty.setPartner(false);
				myParty.followParty(selectedParty);
				appendToConsole("Party " + selectedParty.getPartySimpleName() + " has been moved from Business Partners to Other Parties. "
						+ "Party is still followed by My Party.", Color.GREEN);
				repaintTabbedPane(null, false, false);
				//((AbstractTableModel) table.getModel()).fireTableDataChanged();
			}
		});

		table.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent event)
			{
				EventQueue.invokeLater(() ->
				{
					final int rowIndex = table.rowAtPoint(event.getPoint());
					if(SwingUtilities.isRightMouseButton(event))
					{
						table.setRowSelectionInterval(rowIndex, rowIndex);

						final TreePath path = partyTree.getSelectionPath();
						if(path == null) return;
						final DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
						final Object selectedParty = selectedNode.getUserObject();
						if(selectedParty instanceof String)
						{
							final String nodeTitle = (String) selectedParty;
							if("Business Partners".equals(nodeTitle))
							{
								partyTablePopupMenu.remove(unfollowPartyItem);
								partyTablePopupMenu.remove(addPartnerItem);
								partyTablePopupMenu.add(removePartnerItem);
							}
							else if("Other Parties".equals(nodeTitle))
							{
								partyTablePopupMenu.remove(removePartnerItem);
								partyTablePopupMenu.add(addPartnerItem);
								partyTablePopupMenu.add(unfollowPartyItem);
							}
							partyTablePopupMenu.show(table, event.getX(), event.getY());
						}
					}
					else if(SwingUtilities.isLeftMouseButton(event) && event.getClickCount() == 2)
					{
						final CatalogueTableModel catalogueTableModel = new CatalogueTableModel(); //MMM: this is alternative to ProductTableModel
						//						final ProductTableModel catalogueTableModel = new ProductTableModel(false);
						final PartyListTableModel partyListTableModel = (PartyListTableModel) table.getModel();
						final BusinessParty party = partyListTableModel.getParty(rowIndex);
						catalogueTableModel.setParty(party);
						final JTable catalogueTable = createCatalogueTable(catalogueTableModel);
						selectNode(partyTree, party);
						rightPane =  new JScrollPane(catalogueTable);
						arrangeTab0(leftPane, rightPane);
					}
				});
			}
		});

		return table;
	}

	/**
	 * Selects party's node in the party tree.
	 * @param party party which node should be selected
	 */
	@Deprecated
	private void selectPartyNode(final BusinessParty party)
	{
		final DefaultMutableTreeNode nodeToSelect = searchPartyNode(party);
		if(nodeToSelect != null)
		{
			final TreeNode[] nodes = ((DefaultTreeModel) partyTree.getModel()).getPathToRoot(nodeToSelect);
			final TreePath treePath = new TreePath(nodes);
			partyTree.scrollPathToVisible(treePath);
			partyTree.setSelectionPath(treePath);
		}
	}

	/**
	 * Gets the {@link DefaultMutableTreeNode node} from the tree for the {@link BusinessParty} user object.
	 * @param party node's user object
	 * @return tree node
	 */
	@Deprecated
	private DefaultMutableTreeNode searchPartyNode(BusinessParty party)
	{
		DefaultMutableTreeNode node = null;
		@SuppressWarnings("unchecked")
		final Enumeration<DefaultMutableTreeNode> enumeration = ((DefaultMutableTreeNode) partyTree.getModel().getRoot()).breadthFirstEnumeration();
		while(enumeration.hasMoreElements())
		{
			node = enumeration.nextElement();
			if(party == node.getUserObject())
				break;
		}
		return node;
	}

	/**
	 * Selects the tree node containing the object.
	 * @param tree tree which node should be selected
	 * @param object object contained in the tree node
	 */
	private void selectNode(final JTree tree, final Object object)
	{
		final DefaultMutableTreeNode nodeToSelect = searchNode(tree, object);
		if(nodeToSelect != null)
		{
			final TreeNode[] nodes = ((DefaultTreeModel) tree.getModel()).getPathToRoot(nodeToSelect);
			final TreePath treePath = new TreePath(nodes);
			tree.scrollPathToVisible(treePath);
			tree.setSelectionPath(treePath);
		}
	}

	/**
	 * Searches for an object in the tree.
	 * @param tree tree to be searched
	 * @param object object to be searched for
	 * @return {@link DefaultMutableTreeNode node} containing searched object
	 */
	private DefaultMutableTreeNode searchNode(JTree tree, Object object)
	{
		DefaultMutableTreeNode node = null;
		@SuppressWarnings("unchecked")
		final Enumeration<DefaultMutableTreeNode> enumeration = ((DefaultMutableTreeNode) tree.getModel().getRoot()).breadthFirstEnumeration();
		while(enumeration.hasMoreElements())
		{
			node = enumeration.nextElement();
			if(object == node.getUserObject())
				break;
		}
		return node;
	}

	/**
	 * Creates table displaying list of parties that are the result of quering the CDR.
	 * @param tableModel model containing party data to display
	 * @return constructed table object
	 */
	private JTable createSearchPartyTable(AbstractTableModel tableModel)
	{
		final JTable table = newEmptyPartyListTable(tableModel);

		final JPopupMenu popupMenu = new JPopupMenu();
		final JMenuItem followBusinessPartner = new JMenuItem("Follow as Business Partner");
		final JMenuItem followParty = new JMenuItem("Follow as Party");
		popupMenu.add(followBusinessPartner);
		popupMenu.add(followParty);

		followBusinessPartner.addActionListener(event ->
		{
			int rowIndex = table.getSelectedRow();
			final PartyType followingParty = ((PartySearchTableModel) table.getModel()).getParty(rowIndex);
			final String followingName = InstanceFactory.getPropertyOrNull(followingParty.getPartyNameAtIndex(0), PartyNameType::getNameValue);
			final String followingID = InstanceFactory.getPropertyOrNull(followingParty.getPartyIdentificationAtIndex(0),
					PartyIdentificationType::getIDValue);
			new Thread(()->
			{
				followParty(followingName, followingID, true);
			}).start();
		});

		followParty.addActionListener(event ->
		{
			int rowIndex = table.getSelectedRow();
			final PartyType followingParty = ((PartySearchTableModel) table.getModel()).getParty(rowIndex);
			final String followingName = InstanceFactory.getPropertyOrNull(followingParty.getPartyNameAtIndex(0), PartyNameType::getNameValue);
			final String followingID = InstanceFactory.getPropertyOrNull(followingParty.getPartyIdentificationAtIndex(0),
					PartyIdentificationType::getIDValue);
			new Thread(()->
			{
				followParty(followingName, followingID, false);
			}).start();
		});

		table.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent event)
			{
				EventQueue.invokeLater(() ->
				{
					if(SwingUtilities.isRightMouseButton(event))
					{
						final int rowIndex = table.rowAtPoint(event.getPoint());
						table.setRowSelectionInterval(rowIndex, rowIndex);
						popupMenu.show(table, event.getX(), event.getY());
					}
				});
			}
		});

		return table;
	}

	@SuppressWarnings("unchecked")
	private JTable createSearchListTable(AbstractTableModel tableModel)
	{
		final MyParty myParty = client.getMyParty();
		if(tableModel == null) return null;
		final JTable table =  new JTable(tableModel);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		TableColumnModel colModel = table.getColumnModel();
		colModel.getColumn(0).setPreferredWidth(20);
		colModel.getColumn(1).setPreferredWidth(150);
		colModel.getColumn(2).setPreferredWidth(200);
		colModel.getColumn(3).setPreferredWidth(100);
		table.setFillsViewportHeight(true);

		JPopupMenu searchTablePopupMenu = new JPopupMenu();
		JMenuItem searchAgainItem = new JMenuItem("Search Again");
		searchTablePopupMenu.add(searchAgainItem);
		searchTablePopupMenu.addSeparator();
		JMenuItem renameSearchItem = new JMenuItem("Rename");
		searchTablePopupMenu.add(renameSearchItem);
		JMenuItem deleteSearchItem = new JMenuItem("Delete");
		searchTablePopupMenu.add(deleteSearchItem);

		searchAgainItem.addActionListener(event ->
		{
			int rowIndex = table.getSelectedRow();
			Search<?> selectedSearch = (Search<?>) ((SearchListTableModel<?>) tableModel).getSearches().get(rowIndex);
			new Thread(()->
			{
				client.cdrSearch(selectedSearch, true);
				arrangeTab0(leftPane, rightPane);
			}).start();
		});

		renameSearchItem.addActionListener(event ->
		{
			int rowIndex = table.getSelectedRow();
			Search<?> selectedSearch = (Search<?>) ((SearchListTableModel<?>) tableModel).getSearches().get(rowIndex);
			String newName = (String) JOptionPane.showInputDialog(this, "Enter new name: ", "Rename a search",
					JOptionPane.PLAIN_MESSAGE, null, null, selectedSearch.getSearchName());
			if(newName != null)
				selectedSearch.setSearchName(newName);
			arrangeTab0(leftPane, rightPane);
		});

		deleteSearchItem.addActionListener( event ->
		{
			int rowIndex = table.getSelectedRow();
			Search<?> selectedSearch = (Search<?>) ((SearchListTableModel<?>) tableModel).getSearches().get(rowIndex);

			Class<?> searchClazz = selectedSearch.getResultType();
			if(searchClazz == PartyType.class)
			{
				if (myParty.getPartySearches().remove((Search<PartyType>) selectedSearch))
					arrangeTab0(leftPane, rightPane); // loadTab(tabbedPane.getSelectedIndex());
			}
			else if(searchClazz == CatalogueType.class)
			{
				if (myParty.getCatalogueSearches().remove((Search<CatalogueType>) selectedSearch))
					arrangeTab0(leftPane, rightPane); //loadTab(tabbedPane.getSelectedIndex());
			}
		});

		table.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent event)
			{
				final int rowIndex = table.rowAtPoint(event.getPoint());
				if(SwingUtilities.isLeftMouseButton(event) && event.getClickCount() == 2)
				{
					final TreePath path = searchTree.getSelectionPath();
					if(path == null) return;
					final DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
					final Object selectedSearch = selectedNode.getUserObject();
					if(selectedSearch instanceof String)
					{
						final String nodeTitle = (String) selectedSearch;
						if("Parties".equals(nodeTitle))
						{
							final PartySearchTableModel tableModel = new PartySearchTableModel(false);
							final Search<PartyType> search = myParty.getPartySearches().get(rowIndex);
							tableModel.setSearch(search);
							final JTable searchTable = createSearchPartyTable(tableModel);
							selectNode(searchTree, search);
							rightPane = new JScrollPane(searchTable);
							arrangeTab0(leftPane, rightPane);
						}
						else if("Catalogues".equals(nodeTitle))
						{
							final CatalogueSearchTableModel tableModel = new CatalogueSearchTableModel(false);
							final Search<CatalogueType> search = myParty.getCatalogueSearches().get(rowIndex);
							tableModel.setSearch(search);
							final JTable searchTable = createSearchCatalogueTable(tableModel);
							selectNode(searchTree, search);
							rightPane = new JScrollPane(searchTable);
							arrangeTab0(leftPane, rightPane);
						}
					}
				}
				else
					if(SwingUtilities.isRightMouseButton(event))
					{
						table.setRowSelectionInterval(rowIndex, rowIndex);
						searchTablePopupMenu.show(table, event.getX(), event.getY());
					}
			}
		});

		return table;
	}

	private JTable createSearchCatalogueTable(AbstractTableModel tableModel)
	{
		JTable table = new JTable(tableModel);
		TableColumnModel colModel = table.getColumnModel();
		colModel.getColumn(0).setPreferredWidth(20);
		colModel.getColumn(1).setPreferredWidth(150);
		colModel.getColumn(2).setPreferredWidth(200);
		colModel.getColumn(3).setPreferredWidth(100);
		colModel.getColumn(4).setPreferredWidth(100);
		colModel.getColumn(5).setPreferredWidth(300);
		colModel.getColumn(6).setPreferredWidth(150);
		table.setFillsViewportHeight(true);
		return table;
	}

	/**
	 * Sets the left and right {@code Component} of the tab0's pane.
	 * @param left
	 * @param right
	 */
	private void arrangeTab0(Component left, Component right) //MMM: Does not collapse the tree views if left pane has not changed :)
	{
		if(tab0Pane == null)
			tab0Pane = new JSplitPane();
		if(tab0Pane != null  && tab0Pane.getComponentCount() != 0)
			tab0Pane.removeAll();
		tab0Pane.add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right));
		//repaint();
		/*		tabPane.repaint();
		repaint();*/
	}

	/**
	 * Shows dialog with {@link Party} data which could be changed. If changes have been made to the {@code Party}
	 * object they are saved within this method.
	 * @param party {@code Party} object which data are to be shown
	 * @param title title of the dialog
	 * @param registration whether the dialog is shown during local database registration
	 * @return {@code Party} with potentially changed data
	 */
	//MMM: boolean editable could be added; = false if party should only be displayed not changed
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
	 * @param str string to be shown on the console
	 * @param c colour of the string
	 */
	public void appendToConsole(String str, Color c)
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
				aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);
				doc.insertString(doc.getLength(), str + "\n", aset);
			}
			catch (BadLocationException e)
			{
				logger.error("Exception is ", e);
			}
			consolePane.setCaretPosition(consolePane.getDocument().getLength());
		});
	}

	/**
	 *Repaints currently selected tab.
	 * @param userObject user object of the node
	 * @param added true if node is to be added to the view or to be changed, false if not
	 * @param removed true if node is to be removed from the view or to be changed, false if not
	 */
	public void repaintTabbedPane(Object userObject, boolean added, boolean removed)
	{
		int selectedTab = tabbedPane.getSelectedIndex();
		loadTab(selectedTab, userObject, added, removed);
	}

	/**
	 *Repaints tab with passed index.
	 * @param tabIndex index of the tab to repaint
	 * @param userObject user object of the node
	 * @param added true if node is to be added to the view or to be changed, false if not
	 * @param removed true if node is to be removed from the view or to be changed, false if not
	 */
	public void repaintTabbedPane(int tabIndex, Object userObject, boolean added, boolean removed)
	{
		tabbedPane.setSelectedIndex(tabIndex);
		loadTab(tabIndex, userObject, added, removed);
	}

	/**
	 * Update the main frame's title.
	 * @param partyName party name that should be shown as a part of the title
	 */
	public void updateTitle(String partyName)
	{
		setTitle("Ruta Client - " + partyName);
	}
}