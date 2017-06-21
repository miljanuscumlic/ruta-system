package rs.ruta.client;

import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.prefs.*;
import java.util.List;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PartyType;

@SuppressWarnings("serial")
public class ClientFrame extends JFrame
{
	private static final int DEFAULT_WIDTH = 1000;
	private static final int DEFAULT_HEIGHT = 800;
	private Preferences prefNode = Preferences.userNodeForPackage(this.getClass());

	private Client client;
	private JTabbedPane tabbedPane;
	private JSplitPane splitPane;
	private JTextArea consolePane;
	private AboutDialog aboutDialog;
	private PartyDialog partyDialog;

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

		consolePane = new JTextArea(3, 50);
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tabbedPane, new JScrollPane(consolePane));

		add(splitPane, BorderLayout.CENTER);

		//setting the menu
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu cdrMenu = new JMenu("Central Repository");
		menuBar.add(cdrMenu);

		JMenuItem cdrSynchCatalogueItem = new JMenuItem("Synchronise Catalogue");
		cdrMenu.add(cdrSynchCatalogueItem);
		cdrSynchCatalogueItem.addActionListener(event ->
		{
			//sending Catalogue to CDR
			client.synchroniseCatalogue(); // MMM: this should be done in another Thread
//			DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.MEDIUM);
//			consolePane.append("Catalogue sent at: " + formatter.format(LocalDateTime.now()) + "\n"); // thread-safe method JTextArea.append
		});

		JMenuItem cdrPullCatalogueItem = new JMenuItem("Pull Catalogue");
		cdrMenu.add(cdrPullCatalogueItem);
		cdrPullCatalogueItem.addActionListener(event ->
		{
			//pulling Catalogue from CDR
			client.pullCatalogue();
		});

		JMenu partyMenu = new JMenu("Party");
		JMenuItem myPartyItem = new JMenuItem("My Party");
		myPartyItem.addActionListener(event ->
		{
			PartyType oldCoreParty = client.getMyParty().getCoreParty();
			PartyType newCoreParty = showPartyDialog(oldCoreParty, "My Party");
			if(! oldCoreParty.equals(newCoreParty))
			{
				client.getMyParty().setCoreParty(newCoreParty);
				client.insertMyParty();
			}
		});
		partyMenu.add(myPartyItem);

		JMenuItem CDRPartyItem = new JMenuItem("CDR Party");
		CDRPartyItem.addActionListener(event ->
		{
			PartyType oldCDRParty = client.getCDRParty();
			PartyType newCDRParty = showPartyDialog(oldCDRParty, "CDR Party");
			if(! oldCDRParty.equals(newCDRParty))
			{
				client.setCDRParty(newCDRParty);
				client.insertCDRParty();
			}
		});
		partyMenu.add(CDRPartyItem);


		JMenuItem FollowPartyItem = new JMenuItem("Follow Party");
		FollowPartyItem.addActionListener(event ->
		{
			//MMM: here should be called WebMethod requesting the follow for selected party

			//This implementaion is for the test purpose only  - adding myParty without products as following party
			BusinessParty tempParty = new BusinessParty();
			PartyType tempCoreParty = InstanceFactory.newInstance(client.getMyParty().getCoreParty());
			tempParty.setCoreParty(tempCoreParty);
			client.getMyParty().addFollowingParty(tempParty);
			//*****************
			//adding Bussines Partners for the test purposes
			List<BusinessParty> bp = client.getMyParty().getBusinessPartners();
			List<BusinessParty> fp = client.getMyParty().getFollowingParties();
			for(int i = 9; i>=0; i--)
			{
				PartyType p = InstanceFactory.newInstancePartyType();
				p.getPartyName().get(0).getName().setValue("Partner #" + i);
				BusinessParty biz = new BusinessParty();
				biz.setCoreParty(p);
				bp.add(biz);
				fp.add(biz);
			}
			for(int i = 15; i>=10; i--)
			{
				PartyType p = InstanceFactory.newInstancePartyType();
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
		prefNode.put("title", "Ruta System");
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

	public PartyType showPartyDialog(PartyType party, String title)
	{
		PartyType partyCopy = InstanceFactory.<PartyType>newInstance(party);

		if(partyDialog == null)
			partyDialog = new PartyDialog(ClientFrame.this);
		partyDialog.setTitle(title);
		partyDialog.setParty(partyCopy);
		partyDialog.setVisible(true);
		if(partyDialog.isChanged())
		{
			party = partyDialog.getParty();
			partyDialog.setChanged(false);
		}
		return party;
	}

	public Client getClient()
	{
		return client;
	}

	public void appendToConsole(String str)
	{
		DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.MEDIUM);
		consolePane.append(formatter.format(LocalDateTime.now()) + ": " + str + "\n"); // thread-safe method JTextArea.append
	}
}
