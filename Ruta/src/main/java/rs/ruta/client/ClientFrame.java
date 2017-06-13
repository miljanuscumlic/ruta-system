package rs.ruta.client;

import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.prefs.*;

import javax.swing.*;
import javax.swing.table.*;

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
				client.exportMyProducts();
//				client.closeDataStreams();
				savePreferences();
				client.savePreferences();
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
			if(tabbedPane.getSelectedComponent() == null)
				loadTab(tabbedPane.getSelectedIndex());
		});

		tabbedPane.setSelectedIndex(1);
		loadTab(1);

		consolePane = new JTextArea(3, 50);
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tabbedPane, new JScrollPane(consolePane));

		add(splitPane, BorderLayout.CENTER);

		//setting the menu
		JMenu cdrMenu = new JMenu("Central Repository");
		JMenuItem cdrSynchItem = new JMenuItem("Synchronise Catalogue");
		cdrMenu.add(cdrSynchItem);

		cdrSynchItem.addActionListener(event ->
		{
			//sending Catalogue to CDR
			client.synchroniseCatalogue(); // MMM: this should be done in another Thread
//			DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.MEDIUM);
//			consolePane.append("Catalogue sent at: " + formatter.format(LocalDateTime.now()) + "\n"); // thread-safe method JTextArea.append
		});

		JMenuBar menuBar = new JMenuBar();
		menuBar.add(cdrMenu);
		setJMenuBar(menuBar);

		JMenu partyMenu = new JMenu("Party");
		JMenuItem myPartyItem = new JMenuItem("My Party");
		myPartyItem.addActionListener(event ->
		{
			PartyType oldParty = client.getMyParty();
			PartyType newParty = showPartyDialog(oldParty, "My Party");
			if(! oldParty.equals(newParty))
			{
				client.setMyParty(newParty);
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
			component = new JLabel(title);
			break;
		case 1:
			client.importMyProducts();
			AbstractTableModel tableModel = new ProductTableModel(client);
			JTable table = new JTable(tableModel);
			table.setFillsViewportHeight(true); // filling the viewport with white background color

			//specifing preferred column sizes
			int columnIndex = 0;
			TableColumnModel tableColumnModel = table.getColumnModel();
			TableColumn tableColumn = tableColumnModel.getColumn(columnIndex);
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
		tabbedPane.setComponentAt(tabIndex, new JScrollPane(component));
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
