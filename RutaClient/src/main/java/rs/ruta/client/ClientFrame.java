package rs.ruta.client;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.prefs.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class ClientFrame extends JFrame
{
	private static final int DEFAULT_WIDTH = 1000;
	private static final int DEFAULT_HEIGHT = 800;
	//private static Logger logger = LoggerFactory.getLogger("rs.ruta.client");
	private Preferences prefNode = Preferences.userNodeForPackage(this.getClass());

	private Client client;
	private JTabbedPane tabbedPane;
	private JSplitPane splitPane;
	private JTextPane consolePane;
	private AboutDialog aboutDialog;
	private PartyDialog partyDialog;
	private RegisterDialog registerDialog;
	private SearchDialog searchDialog;

	public ClientFrame(Client client)
	{
		this.client = client;
		this.client.setFrame(this);

		// get position, size, title from preferences
		int left = prefNode.getInt("left", 0);
		int top = prefNode.getInt("top", 0);
		int width = prefNode.getInt("width", DEFAULT_WIDTH);
		int height = prefNode.getInt("height", DEFAULT_HEIGHT);
		setBounds(left, top, width, height);
		String title = prefNode.get("title", "Ruta Client");
		setTitle(title);
		//save window position on exit
		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent event)
			{
				client.getMyParty().exportMyProducts();
				//				client.closeDataStreams();
				savePreferences();
				client.savePreferences();
				//MMM: should be deleted when db comes into play. Calling here just because is saves also the catalogue ID and catalogueDirty
				client.insertMyParty();
				System.exit(0);
			}
		});

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

		tabbedPane.setSelectedIndex(0);
		loadTab(0);

		consolePane = new JTextPane();
		consolePane.setSize(3,50);
		consolePane.setCaretColor(Color.white);
		consolePane.setEditable(false);

		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tabbedPane, new JScrollPane(consolePane));

		add(splitPane, BorderLayout.CENTER);

		//setting the menu
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu cdrMenu = new JMenu("Central Data Repository");
		menuBar.add(cdrMenu);
		JMenuItem cdrRegisterPartyItem = new JMenuItem("Register My Party");
		cdrMenu.add(cdrRegisterPartyItem);
		JMenuItem cdrSynchPartyItem = new JMenuItem("Synchronise My Party");
		cdrMenu.add(cdrSynchPartyItem);
		JMenuItem cdrDeregisterPartyItem = new JMenuItem("Deregister My Party");
		cdrMenu.add(cdrDeregisterPartyItem);
		cdrMenu.addSeparator();
		JMenuItem cdrSynchCatalogueItem = new JMenuItem("Synchronise My Catalogue");
		cdrMenu.add(cdrSynchCatalogueItem);
		JMenuItem cdrPullCatalogueItem = new JMenuItem("Pull My Catalogue");
		cdrMenu.add(cdrPullCatalogueItem);
		JMenuItem cdrDeleteCatalogueItem = new JMenuItem("Delete My Catalogue");
		cdrMenu.add(cdrDeleteCatalogueItem);
		cdrMenu.addSeparator();
/*		JMenuItem cdrTest = new JMenuItem("Test WebMethod");
		cdrMenu.add(cdrTest);
		JMenuItem cdrTestPhax = new JMenuItem("Test Phax");
		cdrMenu.add(cdrTestPhax);*/
		JMenuItem queryPartyItem = new JMenuItem("Search");
		cdrMenu.add(queryPartyItem);
		JMenuItem findAllPartiesItem = new JMenuItem("Find All Parties");
		cdrMenu.add(findAllPartiesItem);

		cdrRegisterPartyItem.addActionListener(event ->
		{
			MyParty party = client.getMyParty();
			if(party.hasSecretKey())
			{
				appendToConsole("My Party is already registered with the CDR service!", Color.BLUE);
				//JOptionPane.showMessageDialog(null, "Party is already registered with the CDR!", "Sign Up to CDR", JOptionPane.ERROR_MESSAGE);
			}
			else
			{
				cdrSynchPartyItem.setEnabled(false);
				cdrDeregisterPartyItem.setEnabled(false);
				new Thread(() ->
				{
					showSignUpDialog(party, "Sign Up to CDR");
					EventQueue.invokeLater(() ->
					{
						cdrSynchPartyItem.setEnabled(true);
						cdrDeregisterPartyItem.setEnabled(true);
					});
				}).start();
			}
		});

		cdrSynchPartyItem.addActionListener(event ->
		{
			MyParty party = client.getMyParty();
			if(party.getSecretKey() != null) // My Party has passed first phase of registration
			{
				if(((MyParty)party).isDirtyMyParty())
				{
					cdrSynchPartyItem.setEnabled(false);
					cdrDeregisterPartyItem.setEnabled(false);
					new Thread(() ->
					{
						client.cdrSynchroniseMyParty();
						EventQueue.invokeLater(() ->
						{
							cdrSynchPartyItem.setEnabled(true);
							cdrDeregisterPartyItem.setEnabled(true);
						});
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
			MyParty party = client.getMyParty();
			//if(party.isRegisteredWithCDR())
			if(party.hasSecretKey())
			{
				cdrSynchPartyItem.setEnabled(false);
				cdrDeregisterPartyItem.setEnabled(false);
				new Thread(() ->
				{
					client.cdrDeregisterMyParty();
					EventQueue.invokeLater(() ->
					{
						cdrSynchPartyItem.setEnabled(true);
						cdrDeregisterPartyItem.setEnabled(true);
					});
				}).start();
			}
			else
				appendToConsole("Request for the deregistration of My Party has not been sent to the CDR service."
						+ " My Party should be registered with the CDR service first!", Color.RED);
		});

/*		cdrTest.addActionListener( event ->
		{
			MyParty party = client.getMyParty();
			client.testParty();
		});

		cdrTestPhax.addActionListener(event ->
		{
			client.testPhax();
		});*/

		findAllPartiesItem.addActionListener( event ->
		{
			client.findAllParties();
		});

		cdrSynchCatalogueItem.addActionListener(event ->
		{
			MyParty party = client.getMyParty();
			if(party.isRegisteredWithCDR())
			{
				if(client.getMyParty().isDirtyCatalogue())
				{
					cdrPullCatalogueItem.setEnabled(false);
					cdrSynchCatalogueItem.setEnabled(false);
					cdrDeleteCatalogueItem.setEnabled(false);
					//sending Catalogue to CDR
					new Thread(()->
					{
						client.cdrSynchroniseMyCatalogue();
						//when done enable menu items
						EventQueue.invokeLater(()->
						{
							cdrPullCatalogueItem.setEnabled(true);
							cdrSynchCatalogueItem.setEnabled(true);
							cdrDeleteCatalogueItem.setEnabled(true);
						});
					}).start();
				}
				else
				{
					appendToConsole("Catalogue is already synchronized with the CDR service.", Color.BLUE);
				}
			}
			else
				appendToConsole("Request for the update of My Catalogue has not been sent to the CDR service."
						+ " My Party should be both registered and synchronised with the CDR service first!", Color.RED);
		});

		cdrPullCatalogueItem.addActionListener(event ->
		{
			MyParty party = client.getMyParty();
			if(party.isRegisteredWithCDR())
			{
				if(client.getMyParty().getFollowingParties().size() != 0)
				{
					cdrPullCatalogueItem.setEnabled(false);
					cdrSynchCatalogueItem.setEnabled(false);
					cdrDeleteCatalogueItem.setEnabled(false);
					//pulling Catalogue from CDR
					new Thread(() ->
					{
						client.cdrPullMyCatalogue();
						EventQueue.invokeLater(() ->
						{
							cdrPullCatalogueItem.setEnabled(true);
							cdrSynchCatalogueItem.setEnabled(true);
							cdrDeleteCatalogueItem.setEnabled(true);
							//repaint table - neccesery for the case when the former version of the catalogue is displayed
							//MMM: this should be done better - without colapsing all nodes on repaint
							loadTab(tabbedPane.getSelectedIndex());
						});
					}).start();
				}
				else
					JOptionPane.showMessageDialog(null, "My Party not set as following party", "Synchronising Catalogue",
							JOptionPane.INFORMATION_MESSAGE);
			}
			else
				appendToConsole("Request for the pull of My Catalogue has not been sent to the CDR service."
						+ " My Party should be both registered and synchronised with the CDR service first!", Color.RED);
		});

		cdrDeleteCatalogueItem.addActionListener(event ->
		{
			MyParty party = client.getMyParty();
			if(party.isRegisteredWithCDR())
			{
				cdrPullCatalogueItem.setEnabled(false);
				cdrSynchCatalogueItem.setEnabled(false);
				cdrDeleteCatalogueItem.setEnabled(false);
				new Thread( () ->
				{
					client.cdrDeleteMyCatalogue();
					EventQueue.invokeLater(() ->
					{
						cdrPullCatalogueItem.setEnabled(true);
						cdrSynchCatalogueItem.setEnabled(true);
						cdrDeleteCatalogueItem.setEnabled(true);
					});
				}).start();
			}
			else
				appendToConsole("Request for the deletion of My Catalogue has not been sent to the CDR service."
						+ " My Party should be both registered and synchronised with the CDR service first!", Color.RED);
		});

		queryPartyItem.addActionListener(event ->
		{
			MyParty party = client.getMyParty();
			if(party.isRegisteredWithCDR())
			{
				queryPartyItem.setEnabled(false);
				new Thread(() ->
				{
					showSearchDialog(party, "Search CDR");
					EventQueue.invokeLater(() ->
						queryPartyItem.setEnabled(true)
					);
				}).start();
			}
			else
				appendToConsole("Search request has not been sent to the CDR service."
						+ " My Party should be both registered and synchronized with the CDR service first!", Color.RED);
		});

		JMenu partyMenu = new JMenu("Party");
		JMenuItem myPartyItem = new JMenuItem("My Party");
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
		partyMenu.add(myPartyItem);

		/*		JMenuItem CDRPartyItem = new JMenuItem("CDR Party");
		CDRPartyItem.addActionListener(event ->
		{
			Party oldCDRParty = client.getCDRParty();
			Party newCDRParty = showPartyDialog(oldCDRParty, "CDR Party");
			if(! oldCDRParty.equals(newCDRParty))
			{
				client.setCDRParty(newCDRParty);
				client.insertCDRParty();
			}
		});
		partyMenu.add(CDRPartyItem);*/


		JMenuItem FollowPartyItem = new JMenuItem("Follow Party");
		FollowPartyItem.addActionListener(event ->
		{
			//MMM: here should be called WebMethod requesting the follow for selected party
			//MMM: this should be place in the Client class
			//This implementaion is for the test purpose only  - adding myParty without products as following party
			BusinessParty tempParty = new BusinessParty();
			Party tempCoreParty = InstanceFactory.newInstance(client.getMyParty().getCoreParty());
			tempParty.setCoreParty(tempCoreParty);
			client.getMyParty().addFollowingParty(tempParty);
			//*****************
			//adding Bussines Partners for the test purposes
			List<BusinessParty> bp = client.getMyParty().getBusinessPartners();
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
			}
			//*****************

			loadTab(tabbedPane.getSelectedIndex());
		});
		partyMenu.add(FollowPartyItem);

		menuBar.add(partyMenu);

		JMenu helpMenu = new JMenu("Help");
		JMenuItem aboutItem = new JMenuItem("About");
		aboutItem.addActionListener(event ->
		{
			if(aboutDialog == null)
				aboutDialog = new AboutDialog(ClientFrame.this);
			aboutDialog.setVisible(true);
		});
		helpMenu.add(aboutItem);
		menuBar.add(helpMenu);
	}



	private void savePreferences()
	{
		prefNode.putInt("left", getX());
		prefNode.putInt("top", getY());
		prefNode.putInt("width", getWidth());
		prefNode.putInt("height", getHeight());
		prefNode.put("title", "Ruta Client - " + client.getMyParty().getCoreParty().getSimpleName());
		//MMM: add column sizes for all tabs
	}

	private void loadTab(int tabIndex)
	{
		String title = tabbedPane.getTitleAt(tabIndex);
		JComponent component = null;
		switch(tabIndex)
		{
		case 0:
			TreeModel treeModel = new PartyTreeModel(new DefaultMutableTreeNode("Following"), client.getMyParty());
			JTree tree = new JTree(treeModel);

			JScrollPane leftPane = new JScrollPane(tree);
			leftPane.setPreferredSize(new Dimension(250, 500));

			AbstractTableModel partnerTableModel = new ProductTableModel(false);
			JTable partnerTable = new JTable(partnerTableModel);
			partnerTable.setFillsViewportHeight(true); // filling the viewport with white background color
			JScrollPane rightPane = new JScrollPane(partnerTable);

			//specifing preferred column sizes
			int columnIndex = 0;
			TableColumnModel columnModel = partnerTable.getColumnModel();
			TableColumn tColumn = columnModel.getColumn(columnIndex);
			tColumn.setPreferredWidth(10);
			tColumn = columnModel.getColumn(1);
			tColumn.setPreferredWidth(200);
			tColumn = columnModel.getColumn(2);
			tColumn.setPreferredWidth(200);
			tColumn = columnModel.getColumn(3);
			tColumn.setPreferredWidth(20);
			//setting cell editor
			partnerTable.setDefaultEditor(Object.class, new ProductTableCellEditor(partnerTable));

			//setting action listener for tab repaint on selection of the business party node
			tree.addTreeSelectionListener(event ->
			{
				TreePath path = tree.getSelectionPath();
				if(path == null) return;
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
				Object selectedParty = selectedNode.getUserObject();
				if (selectedParty instanceof BusinessParty)
				{
					((ProductTableModel)partnerTableModel).setBusinessParty((BusinessParty)selectedParty);
					repaint();
				}
			});

			JSplitPane treeSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPane, rightPane);

			component = treeSplitPane;

			break;
		case 1:
			AbstractTableModel tableModel = new ProductTableModel(client.getMyParty(), true);
			JTable table = new JTable(tableModel);
			table.setFillsViewportHeight(true); // filling the viewport with white background color

			//specifing preferred column sizes
			int cIndex = 0;
			TableColumnModel tableColumnModel = table.getColumnModel();
			TableColumn tableColumn = tableColumnModel.getColumn(cIndex);
			tableColumn.setPreferredWidth(10);
			tableColumn = tableColumnModel.getColumn(1);
			tableColumn.setPreferredWidth(200);
			tableColumn = tableColumnModel.getColumn(2);
			tableColumn.setPreferredWidth(200);
			tableColumn = tableColumnModel.getColumn(3);
			tableColumn.setPreferredWidth(20);
			//setting cell editor
			table.setDefaultEditor(Object.class, new ProductTableCellEditor(table));

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
			setTitle("Ruta Client - " + party.getSimpleName());
			MyParty myParty = client.getMyParty();
			myParty.setCoreParty(party);
			myParty.setDirtyMyParty(true);
			new Thread(()-> client.insertMyParty()).start();
			partyDialog.setChanged(false);
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
			client.cdrSearch(searchDialog.getCriterion().nullEmptyFields());
		}
	}

	public Client getClient()
	{
		return client;
	}

	/**Appends current date and time and coloured the passed string to the console.
	 * @param str string to be shown on the console
	 * @param c colour of the string
	 */
	public void appendToConsole(String str, Color c)
	{
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.BLACK);

		/*		aSet = sCtx.addAttribute(aSet, StyleConstants.FontFamily, "Lucida Console");
		aSet = sCtx.addAttribute(aSet, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);
		DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.MEDIUM);
		consolePane.setCaretPosition(consolePane.getDocument().getLength());
		consolePane.setCharacterAttributes(aSet, false);
		consolePane.replaceSelection(str);*/

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
			e.printStackTrace();
		}
		consolePane.setCaretPosition(consolePane.getDocument().getLength());
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
				e.printStackTrace();
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
			throw new Exception("Error in starting the database.");
		}
		else
			throw new Exception("Invalid type of the input stream.");
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
