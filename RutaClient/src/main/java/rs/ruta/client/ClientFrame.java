package rs.ruta.client;

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
import rs.ruta.client.datamapper.MyPartyXMLFileMapper;
import rs.ruta.common.BugReport;
import rs.ruta.common.BugReportSearchCriterion;
import rs.ruta.common.Followers;
import rs.ruta.common.InstanceFactory;

public class ClientFrame extends JFrame
{
	private static final long serialVersionUID = 7189003953286046899L;
	private static final String DEFAULT_WIDTH = "1000";
	private static final String DEFAULT_HEIGHT = "800";
	private static Logger logger = LoggerFactory.getLogger("rs.ruta.client");

	private Client client;
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
	private JPopupMenu partyTreePopupMenu;
	private JPopupMenu searchTreePopupMenu;
	private JPopupMenu cataloguePopupMenu;
	private JFileChooser chooser;
	private JTree partyTree;
	private JTree searchTree;

	private JMenuItem myPartyItem = new JMenuItem("My Party");
	private JMenuItem myCatalogueItem = new JMenuItem("My Products");
	private JMenuItem saveDataItem = new JMenuItem("Save");
	private JMenuItem exportDataItem = new JMenuItem("Export");
	private JMenuItem importDataItem = new JMenuItem("Import");

	private JMenuItem cdrRegisterPartyItem = new JMenuItem("Register My Party");
	private JMenuItem cdrSynchPartyItem = new JMenuItem("Synchronise My Party");
	private JMenuItem cdrDeregisterPartyItem = new JMenuItem("Deregister My Party");
	private JMenuItem cdrSynchCatalogueItem = new JMenuItem("Synchronise My Catalogue");
	private JMenuItem cdrPullCatalogueItem = new JMenuItem("Pull My Catalogue");
	private JMenuItem cdrDeleteCatalogueItem = new JMenuItem("Delete My Catalogue");
	private JMenuItem cdrSearchItem = new JMenuItem("Search");
	private JMenuItem cdrSettingsItem = new JMenuItem("Settings");

	public ClientFrame(Client client)
	{
		this.client = client;
		this.client.setFrame(this);

		//get frame related properties
		Properties properties = client.getProperties();
		int left = Integer.parseInt(properties.getProperty("mainFrame.left", "0"));
		int top = Integer.parseInt(properties.getProperty("mainFrame.top", "0"));
		int width = Integer.parseInt(properties.getProperty("mainFrame.width", DEFAULT_WIDTH));
		int height = Integer.parseInt(properties.getProperty("mainFrame.height", DEFAULT_HEIGHT));
		setBounds(left, top, width, height);
		String title = properties.getProperty("mainFrame.title", "Ruta Client");
		setTitle(title);

		//file chooser
		chooser = new JFileChooser();
		FileFilter filter = new FileNameExtensionFilter("XML files", "xml");
		chooser.setFileFilter(filter);

		//save properties and local data on exit
/*		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent event)
			{
				client.shutdownApplication();
//				System.exit(1); // testing shutdownhook
			}
		});*/

		//setting tabs
		tabbedPane = new JTabbedPane();
		tabbedPane.addTab("CDR", null);
		tabbedPane.addTab("My Products", null);
		tabbedPane.addTab("Documents", null);
		tabbedPane.addTab("Correspondence", null);

		tabbedPane.addChangeListener(event ->
		{
			// check if this tab still has a null component
			//if(tabbedPane.getSelectedComponent() == null)

			loadTab(tabbedPane.getSelectedIndex());
		});

		tabbedPane.setSelectedIndex(1);
		loadTab(1);

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

		localDataMenu.add(myPartyItem);
		localDataMenu.add(myCatalogueItem);
		localDataMenu.addSeparator();
		localDataMenu.add(saveDataItem);
		localDataMenu.add(exportDataItem);
		localDataMenu.add(importDataItem);

		myPartyItem.addActionListener(event ->
		{
			Party oldCoreParty = client.getMyParty().getCoreParty();
			Party newCoreParty = showPartyDialog(oldCoreParty, "My Party");
			/*if(! oldCoreParty.equals(newCoreParty))
			{
				client.getMyParty().setCoreParty(newCoreParty);
				client.insertMyParty();
			}*/
		});

		myCatalogueItem.addActionListener(event ->
		{
			tabbedPane.setSelectedIndex(1);
			loadTab(1);
		});

		saveDataItem.addActionListener(event ->
		{
			try
			{
				client.insertMyParty();
				appendToConsole("Data has been saved to the local data store.", Color.GREEN);
			}
			catch (Exception e)
			{
				appendToConsole("There has been an error. Could not save data to the local data store!", Color.RED);
			}
		});

		importDataItem.addActionListener(event ->
		{
			chooser.setCurrentDirectory(new File("."));
			chooser.setDialogTitle("Local Data Import");
			int result = chooser.showOpenDialog(this);
			if(result == JFileChooser.APPROVE_OPTION)
			{
				String filePath = chooser.getSelectedFile().getPath();
				//importing data
				try
				{
					MyPartyXMLFileMapper<MyParty> partyDataMapper = new MyPartyXMLFileMapper<MyParty>(client, filePath);
					ArrayList<MyParty> parties = (ArrayList<MyParty>) partyDataMapper.findAll();
					MyParty myParty;
					if(parties.size() != 0)
					{
						myParty = parties.get(0);
						Search.setSearchNumber(myParty.getSearchNumber());
						updateTitle(myParty.getPartyName());
						client.setMyParty(myParty);
						//client.insertMyParty();
						repaintTabbedPane(); // frame update
						appendToConsole("Local data have been successfully imported from the file: " + filePath, Color.GREEN);
					}
				}
				catch(JAXBException e)
				{
					JOptionPane.showMessageDialog(chooser, "Could not import data from the chosen file. The file is corrupt!",
							"Importing local data", JOptionPane.ERROR_MESSAGE);
				}
/*				catch (Exception e)
				{
					JOptionPane.showMessageDialog(ClientFrame.this, "Could not save data to the local data store!",
							"Saving data to the local data store", JOptionPane.ERROR_MESSAGE);
				}*/
			}
		});

		exportDataItem.addActionListener(event ->
		{
			chooser.setCurrentDirectory(new File("."));
			chooser.setDialogTitle("Local Data Export");
			int result = chooser.showSaveDialog(this);
			if(result == JFileChooser.APPROVE_OPTION)
			{
				StringBuilder filePath = new StringBuilder(chooser.getSelectedFile().getPath());
				if(!filePath.toString().endsWith(".xml"))
					filePath = filePath.append(".xml");
				//exporting data
				MyPartyXMLFileMapper<MyParty> partyMapper = new MyPartyXMLFileMapper<MyParty>(client, filePath.toString());
				try
				{
					partyMapper.insertAll();
					appendToConsole("Local data have been successfully exported to the file: " + filePath, Color.GREEN);
				}
				catch (JAXBException e)
				{
					appendToConsole("There has been an error. Local data could not be exported to the file: " + filePath, Color.RED);
					/*JOptionPane.showMessageDialog(ClientFrame.this, "Could not export data to the local data store!",
							"Saving data to the local data store", JOptionPane.ERROR_MESSAGE);*/
				}
			}
		});

		JMenuItem followPartyItem = new JMenuItem("Follow My Party");
		followPartyItem.addActionListener(event ->
		{
			//MMM: here should be called WebMethod requesting the follow for selected party
			//MMM: this should be placed in the Client class
			//This implementaion is for the test purpose only - adding myParty without products as following party
			/*BusinessParty tempParty = new BusinessParty();
			Party tempCoreParty = InstanceFactory.newInstance(client.getMyParty().getCoreParty());
			tempParty.setCoreParty(tempCoreParty);*/
			//*****************
			//adding Bussines Partners for the test purposes
/*			List<BusinessParty> bp = client.getMyParty().getBusinessPartners();
			List<BusinessParty> fp = client.getMyParty().getFollowingParties();
			for(int i = 9; i>=0; i--)
			{
				Party p = InstanceFactory.newInstanceParty();
				p.getPartyName().get(0).getName().setValue("Partner #" + i);
				BusinessParty biz = new BusinessParty();
				biz.setCoreParty(p);
				bp.add(biz);
				fp.add(biz);
			}
			for(int i = 15; i>=10; i--)
			{
				Party p = InstanceFactory.newInstanceParty();
				p.getPartyName().get(0).getName().setValue("Party #" + i);
				BusinessParty biz = new BusinessParty();
				biz.setCoreParty(p);
				fp.add(biz);
			}*/
			//*****************

			client.getMyParty().followMyself();

			loadTab(tabbedPane.getSelectedIndex());
		});
//		localDataMenu.add(followPartyItem);

		menuBar.add(localDataMenu);

		menuBar.add(cdrMenu);
		cdrMenu.add(cdrRegisterPartyItem);
		cdrMenu.add(cdrSynchPartyItem);
		cdrMenu.add(cdrDeregisterPartyItem);
		cdrMenu.addSeparator();
		cdrMenu.add(cdrSynchCatalogueItem);
		cdrMenu.add(cdrPullCatalogueItem);
		cdrMenu.add(cdrDeleteCatalogueItem);
		cdrMenu.addSeparator();
		cdrMenu.add(cdrSearchItem);
		cdrMenu.addSeparator();
		cdrMenu.add(cdrSettingsItem);

		cdrRegisterPartyItem.addActionListener(event ->
		{
			MyParty myParty = client.getMyParty();
			if(myParty.hasSecretKey())
			{
				appendToConsole("My Party is already registered with the CDR service!", Color.BLUE);
				//JOptionPane.showMessageDialog(null, "Party is already registered with the CDR!", "Sign Up to CDR", JOptionPane.ERROR_MESSAGE);
			}
			else
			{
				disablePartyMenuItems();
				new Thread(() ->
				{
					showSignUpDialog(myParty, "Sign Up to CDR");
				}).start();
			}
		});

		cdrSynchPartyItem.addActionListener(event ->
		{
			MyParty myParty = client.getMyParty();
			if(myParty.getSecretKey() != null) // My Party has passed first phase of registration
			{
				if(((MyParty)myParty).isDirtyMyParty())
				{
					disablePartyMenuItems();
					new Thread(() ->
					{
						client.cdrSynchroniseMyParty();

					}).start();
				}
				else
					appendToConsole("My Party is already synchronized with the CDR service!", Color.BLUE);
			}
			else
				appendToConsole("Request for the synchronisation of My Party has not been sent to the CDR service."
						+ " My Party should be both registered and synchronised with the CDR service first!", Color.RED);
		});

		cdrDeregisterPartyItem.addActionListener(event ->
		{
			MyParty myParty = client.getMyParty();
			//if(party.isRegisteredWithCDR())
			if(myParty.hasSecretKey())
			{
				disablePartyMenuItems();
				new Thread(() ->
				{
					client.cdrDeregisterMyParty();
				}).start();
			}
			else
				appendToConsole("Deregistration request of My Party has not been sent to the CDR service."
						+ " My Party is not registered with the CDR service!", Color.RED);
		});

		cdrSynchCatalogueItem.addActionListener(event ->
		{
			MyParty myParty = client.getMyParty();
			if(myParty.isRegisteredWithCDR())
			{
				if(client.getMyParty().isDirtyCatalogue())
				{
					EventQueue.invokeLater(() ->
					{
						disableCatalogueMenuItems();
					});
					//sending Catalogue to CDR
					new Thread(()->
					{
						client.cdrSynchroniseMyCatalogue();
					}).start();
				}
				else
				{
					appendToConsole("Catalogue is already synchronized with the CDR service.", Color.BLUE);
				}
			}
			else
				appendToConsole("Update request of My Catalogue has not been sent to the CDR service."
						+ " My Party should be both registered and synchronised with the CDR service first!", Color.RED);
		});

		cdrPullCatalogueItem.addActionListener(event ->
		{
			MyParty myParty = client.getMyParty();
			if(myParty.isRegisteredWithCDR())
			{
				if(client.getMyParty().getMyFollowingParty() != null)
				{
					disableCatalogueMenuItems();
					//pulling Catalogue from CDR
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
						+ " My Party should be both registered and synchronised with the CDR service first!", Color.RED);
		});

		cdrDeleteCatalogueItem.addActionListener(event ->
		{
			MyParty myParty = client.getMyParty();
			if(myParty.isRegisteredWithCDR())
			{
				if(myParty.isCatalogueInCDR())
				{
					disableCatalogueMenuItems();
					new Thread( () ->
					{
						client.cdrDeleteMyCatalogue();
					}).start();
				}
				else
					appendToConsole("Deletion request of My Catalogue has not been sent to the CDR service."
							+ " My Catalogue is not present in the CDR service!", Color.RED);
			}
			else
				appendToConsole("Deletion request of My Catalogue has not been sent to the CDR service."
						+ " My Party should be both registered and synchronised with the CDR service first!", Color.RED);
		});

		cdrSearchItem.addActionListener(event ->
		{
			MyParty myParty = client.getMyParty();
			if(myParty.isRegisteredWithCDR())
			{
				new Thread(() ->
				{
					showSearchDialog(myParty, "Search CDR");
				}).start();
			}
			else
				appendToConsole("Search request has not been sent to the CDR service."
						+ " My Party should be both registered and synchronized with the CDR service first!", Color.RED);
		});

		cdrSettingsItem.addActionListener(event ->
		{
			settingsDialog = new CDRSettingsDialog(ClientFrame.this);
			settingsDialog.setTitle("CDR Settings");
			settingsDialog.setVisible(true);
			if(settingsDialog.isApplyPressed())
			{
				String endPoint = settingsDialog.getServiceLocation();
				Client.setCDREndPoint(endPoint);
				settingsDialog.setApplyPressed(false);
			}
		});

		JMenu helpMenu = new JMenu("Help");
		JMenuItem aboutItem = new JMenuItem("About");
		helpMenu.add(aboutItem);
		JMenuItem updateItem = new JMenuItem("Check for Updates");
		helpMenu.add(updateItem);
		JMenuItem notifyItem = new JMenuItem("Send Update Notification");
//		MMM: Comment notifyItem before new version of Ruta Client is released
//		helpMenu.add(notifyItem);
		JMenuItem reportBugItem = new JMenuItem("Report a Bug");
		helpMenu.add(reportBugItem);
		JMenuItem exploreBugItem = new JMenuItem("Explore the Bugs");
		helpMenu.add(exploreBugItem);
		JMenuItem fileItem = new JMenuItem("Send a File");
//		helpMenu.add(fileItem);

		aboutItem.addActionListener(event ->
		{
			if(aboutDialog == null)
				aboutDialog = new AboutDialog(ClientFrame.this);
			aboutDialog.setVisible(true);
		});

		updateItem.addActionListener(event ->
		{
			if(updateDialog == null)
			{
				StringBuilder msg = new StringBuilder("Update request has failed! ");
/*				try
				{*/
					updateDialog = new UpdateDialog(ClientFrame.this, client);
/*				}
				catch (Exception e)
				{
					msg.append("Server responds: ");
					Throwable cause = e.getCause();
					if(cause instanceof RutaException)
						msg.append(cause.getMessage()).append(" ").append(((RutaException) cause).getFaultInfo().getDetail());
					else
						msg.append(Client.trimSOAPFaultMessage(cause.getMessage()));
					appendToConsole(msg.toString(), Color.RED);
				}*/
			}
			updateDialog.setVisible(true);
		});

		notifyItem.addActionListener(event ->
		{
			if(notifyDialog == null)
				notifyDialog = new NotifyDialog(ClientFrame.this);
			notifyDialog.setVisible(true);
			if(notifyDialog.isNotifyPressed())
			{
				notifyDialog.setNotifyPressed(false);
				client.cdrUpdateNotification(notifyDialog.getVersion());
			}
		});

		reportBugItem.addActionListener(event ->
		{
			sendBugReport();
		});

		exploreBugItem.addActionListener(event ->
		{
			if(client.getMyParty().isRegisteredWithCDR())
			{
				//if(bugExploreDialog == null)
					bugExploreDialog = new BugExploreDialog(ClientFrame.this);
				bugExploreDialog.setVisible(true);
			}
			else
			{
				appendToConsole("Bugs cannot be explored by non-registered parties.", Color.RED);
			}
		});

		fileItem.addActionListener(event ->
		{
			client.cdrInsertAttachment();
		});

		menuBar.add(helpMenu);
	}

	/**Checks if MyParty is registered with the CDR, then opens {@link BugReportDialog}
	 * and sends {@link BugReport} to the CDR by calling apropriate method in the {@link Client}
	 * class. This method is extracted from the {@code ActionListener} because it is used in
	 * more than one place.
	 */
	public void sendBugReport()
	{
		if(client.getMyParty().isRegisteredWithCDR())
		{
			bugReportDialog = new BugReportDialog(ClientFrame.this);
			bugReportDialog.setVisible(true);
			if(bugReportDialog.isReportPressed())
			{
				bugReportDialog.clearData();
				client.cdrReportBug(bugReportDialog.getBugReport());
			}
		}
		else
			appendToConsole("Bug report cannot be issued by non-registered parties.", Color.RED);
	}

	/**Checks if MyParty is registered with the CDR, then sends a request to the CDR for the list of
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
			appendToConsole("Bug report list cannot be issued by non-registered parties.", Color.RED);
		return future;
	}

	/**Checks if MyParty is registered with the CDR, then sends a request for the list of all
	 * {@link BugReport bugs reported} to the CDR.
	 * @return {@link Future} object representing the response.
	 */
	public Future<?> findAllBugs()
	{
		Future<?> future = null;
		if(client.getMyParty().isRegisteredWithCDR())
			future = client.cdrFindAllBugs();
		else
			appendToConsole("Bug report list cannot be issued by non-registered parties.", Color.RED);
		return future;
	}

	public void followParty(String followingName, String followingID, boolean partner)
	{
		BusinessParty following = client.getMyParty().getFollowingParty(followingID);
		if(following == null)
		{
			client.cdrFollowParty(followingName, followingID, partner);
		}
		else if(partner && !following.isPartner())
		{
			//move following to the business partner list
			following.setPartner(true);
			client.getMyParty().addFollowingParty(following);
			appendToConsole("Party " + followingName + " is already in the following list. But from now it is marked as a business partner.", Color.GREEN);
			repaintTabbedPane();
		}
		else
			appendToConsole("Party " + followingName + " is already in the following list.", Color.BLACK);
	};

	/**Enables menu items regarding Search after client gets the response from the CDR service.
	 * All method calls are made through the {@link EventQueue}.
	 */
	public void enableSearchMenuItems()
	{
		cdrSearchItem.setEnabled(true);
		cdrDeregisterPartyItem.setEnabled(true);
	}

	/**Disables menu items regarding Search after client sends the request to the CDR service.
	 * All method calls are made through the {@link EventQueue}.
	 */
	public void disableSearchMenuItems()
	{
		cdrSearchItem.setEnabled(false);
		cdrDeregisterPartyItem.setEnabled(false);
	}

	/**Enables menu items regarding My Party after client gets the response from the CDR service.
	 * All method calls are made through the {@link EventQueue}.
	 */
	public void enablePartyMenuItems()
	{
		EventQueue.invokeLater(() ->
		{
			cdrRegisterPartyItem.setEnabled(true);
			cdrSynchPartyItem.setEnabled(true);
			cdrDeregisterPartyItem.setEnabled(true);
		});
	}

	/**Disables menu items regarding My Party after client sends the request to the CDR service.
	 * All method calls are made through the {@link EventQueue}.
	 */
	public void disablePartyMenuItems()
	{
		EventQueue.invokeLater(() ->
		{
			cdrRegisterPartyItem.setEnabled(false);
			cdrSynchPartyItem.setEnabled(false);
			cdrDeregisterPartyItem.setEnabled(false);
		});
	}

	/**Enables menu items regarding My Catalogue after client gets the response from the CDR service.
	 * All method calls are made through the {@link EventQueue}.
	 */
	public void enableCatalogueMenuItems()
	{
		cdrPullCatalogueItem.setEnabled(true);
		cdrSynchCatalogueItem.setEnabled(true);
		cdrDeleteCatalogueItem.setEnabled(true);
		cdrDeregisterPartyItem.setEnabled(true);
	}

	/**Disables menu items regarding My Catalogue after client sends the request to the CDR service.
	 * All method calls are made through the {@link EventQueue}.
	 */
	public void disableCatalogueMenuItems()
	{
		cdrPullCatalogueItem.setEnabled(false);
		cdrSynchCatalogueItem.setEnabled(false);
		cdrDeleteCatalogueItem.setEnabled(false);
		cdrDeregisterPartyItem.setEnabled(false);
	}

	/**Saves properties from {@code ClientFrame} class fields to {@link Properties} object.
	 */
	public void saveProperties()
	{
		Properties properties = client.getProperties();
		properties.put("mainFrame.left", String.valueOf(getX()));
		properties.put("mainFrame.top", String.valueOf(getY()));
		properties.put("mainFrame.width", String.valueOf(getWidth()));
		properties.put("mainFrame.height", String.valueOf(getHeight()));
		properties.put("mainFrame.title", "Ruta Client - " + client.getMyParty().getCoreParty().getSimpleName());

		//MMM: add column sizes for all tabs
	}

	@SuppressWarnings("unchecked")
	private void loadTab(int tabIndex)
	{
		String title = tabbedPane.getTitleAt(tabIndex);
		JComponent component = null;
		switch(tabIndex)
		{
		case 0:
			//constructing the left pane
			DefaultTreeModel partyTreeModel = new PartyTreeModel(new DefaultMutableTreeNode("Followings"), client.getMyParty());
			partyTree = new JTree(partyTreeModel);
			DefaultTreeModel searchTreeModel = new SearchTreeModel(new DefaultMutableTreeNode("Searches"), client.getMyParty());
			searchTree = new JTree(searchTreeModel);
			JPanel treePanel = new JPanel(new BorderLayout());
			treePanel.add(partyTree, BorderLayout.NORTH);
			treePanel.add(searchTree, BorderLayout.CENTER);

			leftPane = new JScrollPane(treePanel);
			leftPane.setPreferredSize(new Dimension(250, 500));

			rightPane = new JLabel();

			JLabel blankPane = new JLabel();
/*			blankPane.setOpaque(true);
			blankPane.setBackground(Color.WHITE);*/

			//setting action listener for tab repaint on selection of the business party node
			partyTree.addTreeSelectionListener(event ->
			{
				TreePath path = partyTree.getSelectionPath();
				if(path == null) return;
				searchTree.clearSelection();
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
				Object selectedParty = selectedNode.getUserObject();
				if (selectedParty instanceof BusinessParty)
				{
					ProductTableModel partnerTableModel = new ProductTableModel(false);
					JTable partnerTable = createCatalogueTable(partnerTableModel); //MMM: partnerTable.setModel(partnerTableModel) but before this partnerTable should be created one in constructor
					partnerTableModel.setBusinessParty((BusinessParty) selectedParty);
					rightPane = new JScrollPane(partnerTable);
					arrangeTab0(leftPane, rightPane);
				}
				else //String
				{
					PartyListTableModel partiesTableModel = new PartyListTableModel();
					JTable partiesTable = createPartyListTable(partiesTableModel); //MMM: partnerTable.setModel(partnerTableModel) but before this partnerTable should be created one in constructor

					if("Business Partners".equals((String)selectedParty))
						partiesTableModel.setParties(client.getMyParty().getBusinessPartners());
					else if("Other Parties".equals((String)selectedParty))
						partiesTableModel.setParties(client.getMyParty().getOtherParties());
					else if("Archived Parties".equals((String)selectedParty))
						partiesTableModel.setParties(client.getMyParty().getArchivedParties());
					rightPane = new JScrollPane(partiesTable);
					arrangeTab0(leftPane, rightPane);
				}
			});

			//setting action listener for tab repaint on selection of the search nodes
			searchTree.addTreeSelectionListener(event ->
			{
				TreePath path = searchTree.getSelectionPath();
				if(path == null) return;
				partyTree.clearSelection();
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
				Object selectedSearch = selectedNode.getUserObject();
				if(selectedSearch instanceof Search)
				{
					Class<?> searchClazz = ((Search<?>) selectedSearch).getResultClass();
					if(searchClazz == PartyType.class)
					{
						AbstractTableModel searchPartyTableModel = new PartySearchTableModel(false);
						((PartySearchTableModel) searchPartyTableModel).setSearch((Search<PartyType>) selectedSearch);
						JTable searchTable = createSearchPartyTable(searchPartyTableModel);
						rightPane = new JScrollPane(searchTable);
						arrangeTab0(leftPane, rightPane);
					}
					else if(searchClazz == CatalogueType.class)
					{
						AbstractTableModel searchCatalogueTableModel = new CatalogueSearchTableModel(false);
						((CatalogueSearchTableModel) searchCatalogueTableModel).setSearch((Search<CatalogueType>) selectedSearch);
						JTable searchTable = createSearchCatalogueTable(searchCatalogueTableModel);
						rightPane = new JScrollPane(searchTable);
						arrangeTab0(leftPane, rightPane);
					}
				}
				else //String
				{
					SearchListTableModel searchesTableModel = null;
					JTable searchesTable;

					if("Parties".equals((String)selectedSearch))
					{
						searchesTableModel = new SearchListTableModel<PartyType>();
						searchesTableModel.setSearches(client.getMyParty().getPartySearches());
					}
					else if("Catalogues".equals((String)selectedSearch))
					{
						searchesTableModel = new SearchListTableModel<CatalogueType>();
						searchesTableModel.setSearches(client.getMyParty().getCatalogueSearches());
					}

					searchesTable = createSearchListTable(searchesTableModel);
					rightPane = new JScrollPane(searchesTable);
					arrangeTab0(leftPane, rightPane);
				}
			});

			//mouse listener for the right click
			searchTree.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mouseClicked(MouseEvent event)
				{
					if(SwingUtilities.isRightMouseButton(event))
					{
						/*						int selRow = searchTree.getRowForLocation(event.getX(), event.getY());
						if (selRow > -1)
							searchTree.setSelectionRow(selRow);*/
						TreePath path = searchTree.getPathForLocation(event.getX(), event.getY());
						searchTree.setSelectionPath(path);
						DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
						Object selectedSearch = selectedNode.getUserObject();
						if(!(selectedSearch instanceof String))
							searchTreePopupMenu.show(searchTree, event.getX(), event.getY());
					}
				}
			});

			//popup menus
			partyTreePopupMenu = new JPopupMenu();
			JMenuItem unfollowPartyItem = new JMenuItem("Unfollow party");
			JMenuItem addPartnerItem = new JMenuItem("Add to Business Partners");
			JMenuItem removePartnerItem = new JMenuItem("Remove from Business Partners");

			unfollowPartyItem.addActionListener(event ->
			{
				final TreePath path = partyTree.getSelectionPath();
				if(path == null) return;
				final DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
				final Object selectedParty = selectedNode.getUserObject();
				if(selectedParty instanceof String)
					return;
				else // BusinessParty
					client.cdrUnfollowParty((BusinessParty) selectedParty);
			});

			addPartnerItem.addActionListener(event ->
			{
				final TreePath path = partyTree.getSelectionPath();
				if(path == null) return;
				final DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
				final Object selectedParty = selectedNode.getUserObject();
				if(selectedParty instanceof String)
					return;
				else //BusinessParty
				{
					BusinessParty otherParty = ((BusinessParty) selectedParty);
					otherParty.setPartner(true);
					client.getMyParty().addFollowingParty(otherParty);
					appendToConsole("Party " + otherParty.getPartyName() + " has been moved from Other Parties to Business Partners. "
							+ "Party is still followed by My Party.", Color.GREEN);
					repaintTabbedPane();
				}
			});

			removePartnerItem.addActionListener(event ->
			{
				final TreePath path = partyTree.getSelectionPath();
				if(path == null) return;
				final DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
				final Object selectedParty = selectedNode.getUserObject();
				if(selectedParty instanceof String)
					return;
				else //BusinessParty
				{
					BusinessParty otherParty = ((BusinessParty) selectedParty);
					otherParty.setPartner(false);
					client.getMyParty().addFollowingParty(otherParty);
					appendToConsole("Party " + otherParty.getPartyName() + " has been moved from Business Partners to Other Parties. "
							+ "Party is still followed by My Party.", Color.GREEN);
					repaintTabbedPane();
				}
			});

			partyTree.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mouseClicked(MouseEvent event)
				{
					if(SwingUtilities.isRightMouseButton(event))
					{
						TreePath path = partyTree.getPathForLocation(event.getX(), event.getY());
						partyTree.setSelectionPath(path);
						DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
						Object selectedParty = selectedNode.getUserObject();
						if(!(selectedParty instanceof String))
						{
							if(((BusinessParty) selectedParty).isPartner())
							{
								partyTreePopupMenu.remove(unfollowPartyItem);
								partyTreePopupMenu.add(removePartnerItem);
							}
							else
							{
								partyTreePopupMenu.remove(removePartnerItem);
								partyTreePopupMenu.add(addPartnerItem);
								partyTreePopupMenu.add(unfollowPartyItem);
							}
							partyTreePopupMenu.show(partyTree, event.getX(), event.getY());
						}
					}
				}
			});

			searchTreePopupMenu = new JPopupMenu();
			JMenuItem againSearchItem = new JMenuItem("Search Again");
			searchTreePopupMenu.add(againSearchItem);
			searchTreePopupMenu.addSeparator();
			JMenuItem renameSearchItem = new JMenuItem("Rename");
			searchTreePopupMenu.add(renameSearchItem);
			JMenuItem deleteSearchItem = new JMenuItem("Delete");
			searchTreePopupMenu.add(deleteSearchItem);
//			((JComponent) searchTree).setComponentPopupMenu(searchTreePopupMenu);

			againSearchItem.addActionListener(event ->
			{
				final TreePath path = searchTree.getSelectionPath();
				if(path == null) return;
				final DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
				final Object selectedSearch = selectedNode.getUserObject();
				if(selectedSearch instanceof String)
					return;
				else
					client.cdrSearch((Search<?>) selectedSearch, true);
			});

			renameSearchItem.addActionListener(event ->
			{
				//MMM: should be in a new method of the new class for TabbedPane
				final  TreePath path = searchTree.getSelectionPath();
				if(path == null) return;
				final DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
				final Search<?> selectedSearch = (Search<?>) selectedNode.getUserObject();
				String newName = (String) JOptionPane.showInputDialog(this, "Enter new name: ", "Rename a search",
						JOptionPane.PLAIN_MESSAGE, null, null, selectedSearch.getSearchName());
				if(newName != null)
					selectedSearch.setSearchName(newName);
				searchTree.invalidate();
				searchTree.revalidate();
				searchTree.repaint();
				//arrangeTab0(leftPane, rightPane);
			});

			deleteSearchItem.addActionListener( event ->
			{
				//MMM: should be in a new method of the new class for TabbedPane
				TreePath path = searchTree.getSelectionPath();
				if(path == null) return;
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
				Object selectedSearch = selectedNode.getUserObject();

				Class<?> nodeClass = null; // Class object of the node in the tree
				if(!(selectedSearch instanceof String))
					nodeClass = ((Search<?>) selectedSearch).getResultClass();
				else
					return;
				MyParty myParty = client.getMyParty();
				if(nodeClass == PartyType.class)
				{
					if (myParty.getPartySearches().remove((Search<CatalogueType>) selectedSearch))
						loadTab(tabbedPane.getSelectedIndex());
				}
				else if(nodeClass == CatalogueType.class)
				{
					if (myParty.getCatalogueSearches().remove((Search<CatalogueType>) selectedSearch))
						loadTab(tabbedPane.getSelectedIndex());
				}
			});

			arrangeTab0(leftPane, rightPane);
			component = tab0Pane;

			break;
		case 1:
			AbstractTableModel tableModel = new ProductTableModel(client.getMyParty(), true);
			JTable table = createCatalogueTable(tableModel);
			table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

			table.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mouseClicked(MouseEvent event)
				{
					if(SwingUtilities.isRightMouseButton(event))
					{
						int row = table.rowAtPoint(event.getPoint());
						if(row < table.getRowCount() - 1) //except the last row
						{
							table.setRowSelectionInterval(row, row);
							cataloguePopupMenu.show(table, event.getX(), event.getY());
						}
					}
				}
			});

			cataloguePopupMenu = new JPopupMenu();
			JMenuItem deleteItem = new JMenuItem("Delete item");
			cataloguePopupMenu.add(deleteItem);

			deleteItem.addActionListener( event ->
			{
				//MMM: should be in a new method of the new class for TabbedPane
				int row = table.getSelectedRow();
				int col = table.getSelectedColumn();

				MyParty myParty = client.getMyParty();
				myParty.removeProduct(row);
				loadTab(tabbedPane.getSelectedIndex());
			});
			component = new JScrollPane(table);
			break;
		case 2:
			component = new JLabel(title);
			break;
		case 3:
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
			//implementing column header's tooltip
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
						if(realIndex == 7)
							return "comma separeted values";
						else
							return null;
					}
				};
			}
		};

		table.setFillsViewportHeight(true); // filling the viewport with white background color

		TableColumnModel colModel = table.getColumnModel();
		colModel.getColumn(0).setPreferredWidth(20);
		colModel.getColumn(1).setPreferredWidth(200);
		colModel.getColumn(2).setPreferredWidth(200);
		colModel.getColumn(3).setPreferredWidth(20);
		colModel.getColumn(7).setPreferredWidth(200);
		//setting cell editor
	//	table.setDefaultEditor(Object.class, new ProductTableCellEditor(table));
		//table.setDefaultRenderer(List.class, new ProductTableCellRenderer());
		//table.setDefaultEditor(table.getColumnModel().getColumn(7).getClass(), new ProductTableCellEditor(table));
	//	table.getColumnModel().getColumn(7).setCellEditor(new ProductTableCellEditor(table));
		return table;
	}

	/**Creates and formats the view of empty table that would contain data from the list of parties.
	 * @param tableModel model representing list of parties
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

	/**Creates table for showing list of parties e.g. Business Partnres, Other Parties etc.
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

		unfollowPartyItem.addActionListener(event ->
		{
			final int rowIndex = table.rowAtPoint(partyTablePopupMenu.getBounds().getLocation());
			final BusinessParty selectedParty = ((PartyListTableModel) table.getModel()).getParty(rowIndex);
			client.cdrUnfollowParty(selectedParty);
		});

		addPartnerItem.addActionListener(event ->
		{
			final int rowIndex = table.rowAtPoint(partyTablePopupMenu.getBounds().getLocation());
			final BusinessParty selectedParty = ((PartyListTableModel) table.getModel()).getParty(rowIndex);
			{
				selectedParty.setPartner(true);
				client.getMyParty().addFollowingParty(selectedParty);
				appendToConsole("Party " + selectedParty.getPartyName() + " has been moved from Other Parties to Business Partners. "
						+ "Party is still followed by My Party.", Color.GREEN);
				repaintTabbedPane();
//				((AbstractTableModel) table.getModel()).fireTableDataChanged();
			}
		});

		removePartnerItem.addActionListener(event ->
		{
			final int rowIndex = table.rowAtPoint(partyTablePopupMenu.getBounds().getLocation());
			final BusinessParty selectedParty = ((PartyListTableModel) table.getModel()).getParty(rowIndex);
			{
				selectedParty.setPartner(false);
				client.getMyParty().addFollowingParty(selectedParty);
				appendToConsole("Party " + selectedParty.getPartyName() + " has been moved from Business Partners to Other Parties. "
						+ "Party is still followed by My Party.", Color.GREEN);
				repaintTabbedPane();
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
						final DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
						final Object selectedParty = selectedNode.getUserObject();
						if(selectedParty instanceof String)
						{
							final String nodeTitle = (String) selectedParty;
							if("Business Partners".equals(nodeTitle))
							{
								partyTablePopupMenu.remove(unfollowPartyItem);
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
						final CatalogueTableModel catalogueTableModel = new CatalogueTableModel();
						final PartyListTableModel partyListTableModel = (PartyListTableModel) table.getModel();
						final BusinessParty party = partyListTableModel.getParty(rowIndex);
						catalogueTableModel.setParty(party);
						final JTable catalogueTable = createCatalogueTable(catalogueTableModel);
						selectPartyNode(party);
						rightPane =  new JScrollPane(catalogueTable);
						arrangeTab0(leftPane, rightPane);
					}
				});
			}
		});

		return table;
	}

	/**Selects party's node in the party tree.
	 * @param party party which node should be selected
	 */
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

	/**Gets the {@link DefaultMutableTreeNode node} from the tree for the {@link BusinessParty} user object.
	 * @param party node's user object
	 * @return tree node
	 */
	private DefaultMutableTreeNode searchPartyNode(BusinessParty party)
	{
		DefaultMutableTreeNode node = null;
		final Enumeration enumeration = ((DefaultMutableTreeNode) partyTree.getModel().getRoot()).breadthFirstEnumeration();
		while(enumeration.hasMoreElements())
		{
			node = (DefaultMutableTreeNode) enumeration.nextElement();
			if(party == node.getUserObject())
				break;
		}
		return node;
	}

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

	private DefaultMutableTreeNode searchNode(JTree tree, Object object)
	{
		DefaultMutableTreeNode node = null;
		final Enumeration enumeration = ((DefaultMutableTreeNode) tree.getModel().getRoot()).breadthFirstEnumeration();
		while(enumeration.hasMoreElements())
		{
			node = (DefaultMutableTreeNode) enumeration.nextElement();
			if(object == node.getUserObject())
				break;
		}
		return node;
	}

	/**Creates table for showing list of parties that are the result of quering the CDR service data store.
	 * @param tableModel model containing party data to display
	 * @param search true if the table displays search results, and false if it displays list of parties
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
			followParty(followingName, followingID, true);
		});

		followParty.addActionListener(event ->
		{
			int rowIndex = table.getSelectedRow();
			final PartyType followingParty = ((PartySearchTableModel) table.getModel()).getParty(rowIndex);
			final String followingName = InstanceFactory.getPropertyOrNull(followingParty.getPartyNameAtIndex(0), PartyNameType::getNameValue);
			final String followingID = InstanceFactory.getPropertyOrNull(followingParty.getPartyIdentificationAtIndex(0),
					PartyIdentificationType::getIDValue);
			followParty(followingName, followingID, false);
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
		JMenuItem againSearchItem = new JMenuItem("Search Again");
		searchTablePopupMenu.add(againSearchItem);
		searchTablePopupMenu.addSeparator();
		JMenuItem renameSearchItem = new JMenuItem("Rename");
		searchTablePopupMenu.add(renameSearchItem);
		JMenuItem deleteSearchItem = new JMenuItem("Delete");
		searchTablePopupMenu.add(deleteSearchItem);

		againSearchItem.addActionListener(event ->
		{
			int rowIndex = table.getSelectedRow();
			Search<?> selectedSearch = (Search<?>) ((SearchListTableModel<?>)tableModel).getSearches().get(rowIndex);
			client.cdrSearch(selectedSearch, true);
			arrangeTab0(leftPane, rightPane);
		});

		renameSearchItem.addActionListener(event ->
		{
			int rowIndex = table.getSelectedRow();
			Search<?> selectedSearch = (Search<?>) ((SearchListTableModel<?>)tableModel).getSearches().get(rowIndex);
			String newName = (String) JOptionPane.showInputDialog(this, "Enter new name: ", "Rename a search",
					JOptionPane.PLAIN_MESSAGE, null, null, selectedSearch.getSearchName());
			if(newName != null)
				selectedSearch.setSearchName(newName);
			arrangeTab0(leftPane, rightPane);
		});

		deleteSearchItem.addActionListener( event ->
		{
			int rowIndex = table.getSelectedRow();
			Search<?> selectedSearch = (Search<?>) ((SearchListTableModel<?>)tableModel).getSearches().get(rowIndex);

			Class<?> searchClazz = selectedSearch.getResultClass();
			final MyParty myParty = client.getMyParty();
			if(searchClazz == PartyType.class)
			{
				if (myParty.getPartySearches().remove((Search<CatalogueType>) selectedSearch))
					loadTab(tabbedPane.getSelectedIndex());
			}
			else if(searchClazz == CatalogueType.class)
			{
				if (myParty.getCatalogueSearches().remove((Search<CatalogueType>) selectedSearch))
					loadTab(tabbedPane.getSelectedIndex());
			}
			arrangeTab0(leftPane, rightPane);
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
					final DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
					final Object selectedSearch = selectedNode.getUserObject();
					if(selectedSearch instanceof String)
					{
						final String nodeTitle = (String) selectedSearch;
						if("Parties".equals(nodeTitle))
						{
							final PartySearchTableModel tableModel = new PartySearchTableModel(false);
							final Search<PartyType> search = client.getMyParty().getPartySearches().get(rowIndex);
							tableModel.setSearch(search);
							final JTable searchTable = createSearchPartyTable(tableModel);
							selectNode(searchTree, search);
							rightPane = new JScrollPane(searchTable);
							arrangeTab0(leftPane, rightPane);
						}
						else if("Catalogues".equals(nodeTitle))
						{
							final CatalogueSearchTableModel tableModel = new CatalogueSearchTableModel(false);
							final Search<CatalogueType> search = client.getMyParty().getCatalogueSearches().get(rowIndex);
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

	/**Sets the left and right {@code Component} of the tab0's pane.
	 * @param left
	 * @param right
	 */
	private void arrangeTab0(Component left, Component right) //MMM: Does not colapse the tree views :)
	{
		if(tab0Pane == null)
			tab0Pane = new JSplitPane();
		if(tab0Pane != null  && tab0Pane.getComponentCount() != 0)
			tab0Pane.removeAll();
		tab0Pane.add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right));
		//repaint();
/*		tab0Pane.repaint();
		repaint();*/
	}

/*	private Component getTab0RightPane()
	{
		return tab0RightPane;
	}

	private void setTab0RightPane(Component component)
	{
		this.tab0RightPane = tab0RightPane;
		repaint();
		if(tab0RightPane.getComponentCount() != 0)
			tab0RightPane.removeAll();
		tab0RightPane.add(component);
	}*/

	/**Shows dialog with Party data which could be changed.
	 * @param party Party object which data are to be changed
	 * @param title title of the dialog
	 * @return party object with changes if any
	 */
	public Party showPartyDialog(Party party, String title)
	{
		//		Party partyCopy = InstanceFactory.<Party>newInstance(party);
		Party partyCopy = party.clone();

		partyDialog = new PartyDialog(ClientFrame.this);
		partyDialog.setTitle(title);
		partyDialog.setParty(partyCopy);
		partyDialog.setVisible(true);
		if(partyDialog.isChanged())
		{
			party = partyDialog.getParty();
			updateTitle(party.getSimpleName());
			MyParty myParty = client.getMyParty();
			myParty.setCoreParty(party);
			myParty.setDirtyMyParty(true);
			new Thread(()->
			{
				try
				{
					client.insertMyParty();
					partyDialog.setChanged(false);
				}
				catch (Exception e)
				{
					JOptionPane.showMessageDialog(ClientFrame.this, "Could not save data to the local data store!",
							"Saving data to the local data store", JOptionPane.ERROR_MESSAGE);
				}
			}).start();

		}
		return party;
	}

	/**Shows dialog for the sign up reguest with the CDR.
	 * @param party Party object with wich the request will be made
	 * @param title titel of the dialog
	 * @return bolean value that shows if the sign up procedure is canceled or not
	 */
	public boolean showSignUpDialog(MyParty party, String title)
	{
		boolean registerPressed = false;
		//		Party corePartyCopy = InstanceFactory.<Party>newInstance(party.getCoreParty());
		//		Party corePartyCopy = party.getCoreParty().clone();
		//		signUpDialog = new RegisterWithPartyDialog(ClientFrame.this, corePartyCopy);
		registerDialog = new RegisterDialog(ClientFrame.this);
		registerDialog.setTitle(title);
		registerDialog.setVisible(true);
		registerPressed = registerDialog.isRegisterPressed();
		registerDialog.setRegisterPressed(false);
		if(registerPressed)
			client.cdrRegisterMyParty(client.getMyParty().getCoreParty(), registerDialog.getUsername(), registerDialog.getPassword());
		else
			enablePartyMenuItems();
		return registerPressed;
	}

	private void showSearchDialog(MyParty party, String title)
	{
		searchDialog = new SearchDialog(ClientFrame.this);
		searchDialog.setTitle(title);
		searchDialog.setVisible(true);
		if(searchDialog.isSearchPressed())
		{
			searchDialog.setSearchPressed(false);
		//	client.cdrSearch(searchDialog.getSearchName(), searchDialog.getCriterion());
			client.cdrSearch(searchDialog.getSearch(), false);
		}
	}

	public Client getClient()
	{
		return client;
	}

	/**Appends current date and time and coloured passed string to the console. All this is done inside the
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

	//***************** TESTING APPLICATION CALL *********************
	/**Constructs eXist database instance and registers it at the <code>DatabaseManager</code>, enabling
	 * the application to communicate with it.
	 */
	/*	public static void connectDatabase()
	{
		try
		{
			@SuppressWarnings("unchecked")
			final Class<Database> dbClass = (Class<Database>) Class.forName("org.exist.xmldb.DatabaseImpl");
			final Database database = dbClass.newInstance();
			database.setProperty("create-database", "true");
			DatabaseManager.registerDatabase(database);
		}
		catch(Exception e)
		{
			logger.error(e.getStackTrace().toString());
		}
	}*/

	/**Starts the eXist database application in its own jetty server.
	 * @throws Exception thrown if eXist could not be started
	 */
	private static void startDatabase() throws Exception
	{
		new Thread( () ->
		{
			Process process;
			try
			{
				System.out.println("User directory: " + System.getProperty("user.dir"));
				System.out.println("User home: " + System.getProperty("user.home"));
				/*			String command2 = "java -jar ruta-client.jar";
			logger.info(command2);
			process = Runtime.getRuntime().exec(command2);
			printLines("info", process.getInputStream());
		    printLines("error", process.getErrorStream());
		    process.waitFor();
		    logger.info("exitValue() " + process.exitValue());*/

				String[] commands = {"cmd", "/c", "start", "cmd", "/k", "java", "-Xmx1024M", "-Dexist.home=C:\\Programs\\exist-db",
						"-Djava.endorsed.dirs=lib/endorsed", "-Djetty.port=8888", "-jar", "C:\\Programs\\exist-db\\start.jar", "jetty"};
				ProcessBuilder probuilder = new ProcessBuilder(commands);
				process = probuilder.start();


				/*			String command = "cmd /k java -Xmx1024M -Dexist.home=C:\\Programs\\exist-db -Djava.endorsed.dirs=lib/endorsed -Djetty.port=8888 -jar C:\\Programs\\exist-db\\start.jar jetty";
			System.out.println("Exist home: " + System.getProperty("exist.home"));
			//logger.info(command);
			System.out.println(command);
			process = Runtime.getRuntime().exec(command);*/

				printLines("info", process.getInputStream());
				printLines("error", process.getErrorStream());
				//process.waitFor(2000, TimeUnit.SECONDS);
				process.destroyForcibly();

				//process.waitFor();
				//logger.info("exitValue() " + process.exitValue());
				//System.out.println("exitValue() " + process.exitValue());
			}
			catch (Exception e)
			{
				logger.error("Exception is ", e);
				//throw e;
			}
		}).start();
	}

	/**Sends <code>Inpustream</code> text to the console window.
	 * @param type type of the message
	 * @param ins <code>Inpustream</code> that would be sent to the console window
	 * @throws Exception thrown if there is the error in starting the database or reading the input stream
	 */
	private static void printLines(String type, InputStream ins) throws Exception
	{
		String line = null;
		BufferedReader in = new BufferedReader(new InputStreamReader(ins));
		if("info".equals(type))
			while ((line = in.readLine()) != null)
				System.out.println(line);
		else if("error".equals(type))
		{
			while((line = in.readLine()) != null)
				System.err.println(line);
			throw new Exception("Error while starting the database.");
		}
		else
			throw new Exception("Invalid type of the input stream.");
	}

	//MMM: should be implemented to update view of trees with no path collapsing as it is now
	public void repaintTabbedPane()
	{
		int selectedTab = tabbedPane.getSelectedIndex();
		loadTab(selectedTab);

/*		if(selectedTab == 0)
		{
			leftPane.revalidate();
			leftPane.repaint();
			rightPane.revalidate();
			rightPane.repaint();

			arrangeTab0(leftPane, rightPane);
		}
		else
			loadTab(selectedTab);*/


/*		Component treeContainer = ((JComponent) ((JComponent)((JComponent) tab0Pane.getComponent(0)).getComponent(0)).getComponent(0)).getComponent(0);
		JTree partyTree = (JTree) ((JComponent) treeContainer).getComponent(0);
		JTree searchTree = (JTree) ((JComponent) treeContainer).getComponent(1);
		((DefaultTreeModel) partyTree.getModel()).reload();
		((DefaultTreeModel) searchTree.getModel()).reload();*/
	}

	/**Update the main frame's title.
	 * @param partyName party naem that should be shown in the title bar
	 */
	public void updateTitle(String partyName)
	{
		this.setTitle("Ruta Client - " + partyName);
	}

	/**Shuts down the eXist database, its application program and jetty server as its container.
	 * @throws Exception thrown if database could not be stopped
	 */
	/*	private static void shutdownDatabase() throws Exception
	{
		Collection root = null;
		try
		{
			root = getRootCollection();
			final DatabaseInstanceManager dbm = (DatabaseInstanceManager) root.getService("DatabaseInstanceManager", "1.0");
			dbm.shutdown();
		}
		catch (XMLDBException e)
		{
			logger.error(e.getStackTrace().toString());
			throw e;
		}
	}*/

	//****************************************************************

}
