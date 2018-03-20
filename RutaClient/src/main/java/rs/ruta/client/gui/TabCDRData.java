package rs.ruta.client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultRowSorter;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import oasis.names.specification.ubl.schema.xsd.catalogue_21.CatalogueType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyIdentificationType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyNameType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import rs.ruta.client.BusinessParty;
import rs.ruta.client.CatalogueSearch;
import rs.ruta.client.gui.CatalogueSearchTableModel;
import rs.ruta.client.gui.CatalogueTableModel;
import rs.ruta.client.MyParty;
import rs.ruta.client.PartySearch;
import rs.ruta.client.gui.PartyListTableModel;
import rs.ruta.client.gui.PartySearchTableModel;
import rs.ruta.client.gui.PartyTreeModel;
import rs.ruta.client.Search;
import rs.ruta.client.gui.SearchListTableModel;
import rs.ruta.client.gui.SearchTreeModel;
import rs.ruta.common.InstanceFactory;

public class TabCDRData extends TabComponent
{
	private final JComponent leftPane;
	private final JComponent rightPane;
	private JComponent tabPane;
	private final JTree partyTree;
	private final JTree searchTree;
	private final JScrollPane rightScrollPane;
//	private final JScrollPane leftScroolPane;
	private PartySearchTableModel searchesPartyTableModel;
	private JTable searchesPartyTable;
	private CatalogueSearchTableModel searchesCatalogueTableModel;
	private JTable searchesCatalogueTable;
	private JTable searchesTable;
	private final TableRowSorter<AbstractTableModel> searchesPartySorter;
	private final TableRowSorter<AbstractTableModel> searchesCatalogueSorter;

	private CatalogueTableModel partnerCatalogueTableModel;
	private JTable partnerCatalogueTable;
	private PartyListTableModel partiesTableModel;
	private JTable partiesTable;
	private final TableRowSorter<AbstractTableModel> partnerCatalogueSorter;
	private final TableRowSorter<AbstractTableModel> partiesSorter;
	/**
	 * @param clientFrame
	 */
	@SuppressWarnings("unchecked")
	public TabCDRData(RutaClientFrame clientFrame)
	{
		super(clientFrame);
		MyParty myParty = clientFrame.getClient().getMyParty();
		//constructing left pane
		DefaultTreeModel partyTreeModel = new PartyTreeModel(new DefaultMutableTreeNode("Followings"), myParty);
		partyTree = new JTree(partyTreeModel);
		DefaultTreeModel searchTreeModel = new SearchTreeModel(new DefaultMutableTreeNode("Searches"), myParty);
		searchTree = new JTree(searchTreeModel);
		JPanel treePanel = new JPanel(new BorderLayout());
		treePanel.add(partyTree, BorderLayout.NORTH);
		treePanel.add(searchTree, BorderLayout.CENTER);

		leftPane = new JScrollPane(treePanel);
		leftPane.setPreferredSize(new Dimension(250, 500));

		rightPane = new JPanel(new BorderLayout());
		rightScrollPane = new JScrollPane();
		rightPane.add(rightScrollPane);

		JLabel blankLabel = new JLabel();

		//partner (party) data table model
		partnerCatalogueTableModel = new CatalogueTableModel();
		partnerCatalogueTable = createCatalogueTable(partnerCatalogueTableModel);
		partiesTableModel = new PartyListTableModel();
		partiesTable = createPartyListTable(partiesTableModel);

		partnerCatalogueSorter = getTableRowSorter(partnerCatalogueTableModel, partnerCatalogueTable);
		partnerCatalogueTable.setRowSorter(partnerCatalogueSorter);
		partiesSorter = getTableRowSorter(partiesTableModel, partiesTable);
		partiesTable.setRowSorter(partiesSorter);

		JPopupMenu partyTreePopupMenu = new JPopupMenu();
		JPopupMenu searchTreePopupMenu = new JPopupMenu();

		//setting action listener for tab repaint on selection of the business party node
		partyTree.addTreeSelectionListener(event ->
		{
			final TreePath path = partyTree.getSelectionPath();
			if(path == null) return;
			searchTree.clearSelection();
			final DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
			final Object selectedParty = selectedNode.getUserObject();
			if (selectedParty instanceof BusinessParty)
			{
				partnerCatalogueTableModel.setParty((BusinessParty) selectedParty);
				/*rightPane.removeAll();
				rightPane.add(new JScrollPane(partnerCatalogueTable));*/

				partnerCatalogueSorter.allRowsChanged();
//				partnerCatalogueSorter.setSortable(0, false);
				rightScrollPane.setViewportView(partnerCatalogueTable);
			}
			else //String
			{
				List<BusinessParty> partyList = new ArrayList<>();
				if("My Party".equals((String) selectedParty))
				{
					final BusinessParty my = myParty.getMyFollowingParty();
					if(my != null)
						partyList.add(my);
				}
				else if("Business Partners".equals((String) selectedParty))
					partyList = myParty.getBusinessPartners();
				else if("Other Parties".equals((String) selectedParty))
					partyList = myParty.getOtherParties();
				else if("Archived Parties".equals((String) selectedParty))
					partyList = myParty.getArchivedParties();
				else if("Deregistered Parties".equals((String) selectedParty))
					partyList = myParty.getDeregisteredParties();

				if(!"Followings".equals((String) selectedParty))
				{
					partiesTableModel.setParties(partyList);
//					rightPane.add(new JScrollPane(partiesTable));
					partiesSorter.allRowsChanged();
//					partiesSorter.setSortable(0, false);
					rightScrollPane.setViewportView(partiesTable);
				}
				else
				{
					rightScrollPane.setViewportView(blankLabel);
				}
			}
			repaint();
		});

		JMenuItem unfollowPartyItem = new JMenuItem("Unfollow party");
		JMenuItem addPartnerItem = new JMenuItem("Add to Business Partners");
		JMenuItem removePartnerItem = new JMenuItem("Remove from Business Partners");
		JMenuItem deleteArchivedItem = new JMenuItem("Delete from Archived Parties");
		JMenuItem deleteDeregisteredItem = new JMenuItem("Delete from Deregistered Parties");

		unfollowPartyItem.addActionListener(event ->
		{
			final TreePath path = partyTree.getSelectionPath();
			if(path == null) return;
			final DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
			final Object selectedParty = selectedNode.getUserObject();
			if(selectedParty instanceof String)
				return;
			else // BusinessParty
				new Thread(()->
				{
					clientFrame.getClient().cdrUnfollowParty((BusinessParty) selectedParty);
				}).start();
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
				myParty.followParty(otherParty);
				clientFrame.appendToConsole("Party " + otherParty.getPartySimpleName() +
						" has been moved from Other Parties to Business Partners. " +
						"Party is still followed by My Party.", Color.GREEN);
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
				myParty.followParty(otherParty);
				clientFrame.appendToConsole("Party " + otherParty.getPartySimpleName() +
						" has been moved from Business Partners to Other Parties. " +
						"Party is still followed by My Party.", Color.GREEN);
			}
		});

		deleteArchivedItem.addActionListener(event ->
		{
			final TreePath path = partyTree.getSelectionPath();
			if(path == null) return;
			final DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
			final BusinessParty selectedParty = (BusinessParty) selectedNode.getUserObject();
			myParty.purgeParty(selectedParty);
			clientFrame.appendToConsole("Party " + selectedParty.getPartySimpleName() +
					" has been deleted from Archived Parties.", Color.GREEN);
		});

		deleteDeregisteredItem.addActionListener(event ->
		{
			final TreePath path = partyTree.getSelectionPath();
			if(path == null) return;
			final DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
			final BusinessParty selectedParty = (BusinessParty) selectedNode.getUserObject();
//			myParty.deleteDeregisteredParty(selectedParty);
			myParty.purgeParty(selectedParty);
			clientFrame.appendToConsole("Party " + selectedParty.getPartySimpleName() +
					" has been deleted from Deregistered Parties.", Color.GREEN);
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
							partyTreePopupMenu.remove(deleteArchivedItem);
							partyTreePopupMenu.remove(deleteDeregisteredItem);
							partyTreePopupMenu.remove(addPartnerItem);
							partyTreePopupMenu.add(unfollowPartyItem);
							partyTreePopupMenu.add(removePartnerItem);
						}
						else if(((BusinessParty) selectedParty).isArchived())
						{
							partyTreePopupMenu.remove(addPartnerItem);
							partyTreePopupMenu.remove(removePartnerItem);
							partyTreePopupMenu.remove(unfollowPartyItem);
							partyTreePopupMenu.remove(deleteDeregisteredItem);
							partyTreePopupMenu.add(deleteArchivedItem);
						}
						else if(((BusinessParty) selectedParty).isDeregistered())
						{
							partyTreePopupMenu.remove(addPartnerItem);
							partyTreePopupMenu.remove(removePartnerItem);
							partyTreePopupMenu.remove(unfollowPartyItem);
							partyTreePopupMenu.remove(deleteArchivedItem);
							partyTreePopupMenu.add(deleteDeregisteredItem);
						}
						else //Other Parties
						{
							partyTreePopupMenu.remove(removePartnerItem);
							partyTreePopupMenu.remove(deleteArchivedItem);
							partyTreePopupMenu.remove(deleteDeregisteredItem);
							partyTreePopupMenu.add(addPartnerItem);
							partyTreePopupMenu.add(unfollowPartyItem);
						}
						partyTreePopupMenu.show(partyTree, event.getX(), event.getY());
					}
				}
			}
		});

		final TabComponent.RowNumberRenderer rowNumberRenderer = new TabComponent.RowNumberRenderer();
		searchesPartyTableModel = new PartySearchTableModel(false);
		searchesPartyTable = createSearchPartyTable(searchesPartyTableModel);
		searchesPartyTable.getColumnModel().getColumn(0).setCellRenderer(rowNumberRenderer);
		searchesCatalogueTableModel = new CatalogueSearchTableModel(false);
		searchesCatalogueTable = createSearchCatalogueTable(searchesCatalogueTableModel);
		searchesCatalogueTable.getColumnModel().getColumn(0).setCellRenderer(rowNumberRenderer);
		searchesTable = createSearchListTable(new SearchListTableModel<>());

		searchesPartySorter = getTableRowSorter(searchesPartyTableModel, searchesPartyTable);
		searchesPartyTable.setRowSorter(searchesPartySorter);
		searchesCatalogueSorter = getTableRowSorter(searchesCatalogueTableModel, searchesCatalogueTable);
		searchesCatalogueTable.setRowSorter(searchesCatalogueSorter);
		//there is no searchesTableModel and searchesTableSorter because there are a few different models that
		//could be instantiated based on the type of the object that is searched for: PartyType, CatalogueType

		//setting action listener for tab repaint on selection of the search nodes
		searchTree.addTreeSelectionListener(event ->
		{
			final TreePath path = searchTree.getSelectionPath();
			if(path == null) return;
			partyTree.clearSelection();
			final DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
			final Object selectedSearch = selectedNode.getUserObject();
			if(selectedSearch instanceof PartySearch)
			{
				searchesPartyTableModel.setSearch((Search<PartyType>) selectedSearch);
/*				searchesCatalogueTable.setAutoCreateRowSorter(true);
				((DefaultRowSorter<AbstractTableModel, Integer>) searchesCatalogueTable.getRowSorter()).setSortable(0, false);*/
				searchesPartySorter.allRowsChanged();
				searchesPartySorter.setSortable(0, false);
				rightScrollPane.setViewportView(searchesPartyTable);
			}
			else if(selectedSearch instanceof CatalogueSearch)
			{
				searchesCatalogueTableModel.setSearch((Search<CatalogueType>) selectedSearch);
				searchesCatalogueSorter.allRowsChanged();
				searchesCatalogueSorter.setSortable(0, false);
				rightScrollPane.setViewportView(searchesCatalogueTable);
			}
			else //String
			{
				SearchListTableModel<?> searchesTableModel = null;
				if("Parties".equals((String) selectedSearch))
				{
					searchesTableModel = new SearchListTableModel<PartyType>();
					((SearchListTableModel<PartyType>) searchesTableModel).setSearches(myParty.getPartySearches());
				}
				else if("Catalogues".equals((String) selectedSearch))
				{
					searchesTableModel = new SearchListTableModel<CatalogueType>();
					((SearchListTableModel<CatalogueType>) searchesTableModel).setSearches(myParty.getCatalogueSearches());
				}
				if(!"Searches".equals((String) selectedSearch))
				{
					searchesTable.setModel(searchesTableModel);
					searchesTable.getColumnModel().getColumn(0).setCellRenderer(rowNumberRenderer);
//					searchesTable.setAutoCreateRowSorter(true);
//					((DefaultRowSorter<AbstractTableModel, Integer>) searchesTable.getRowSorter()).setSortable(0, false);
					searchesTable.setRowSorter(getTableRowSorter(searchesTableModel, searchesTable));
					rightScrollPane.setViewportView(searchesTable);
				}
				else
				{
					rightScrollPane.setViewportView(blankLabel);
				}
			}
			repaint();
		});

		//mouse listener for the right click
		searchTree.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent event)
			{
				if(SwingUtilities.isRightMouseButton(event))
				{
					TreePath path = searchTree.getPathForLocation(event.getX(), event.getY());
					searchTree.setSelectionPath(path);
					DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
					Object selectedSearch = selectedNode.getUserObject();
					if(!(selectedSearch instanceof String))
						searchTreePopupMenu.show(searchTree, event.getX(), event.getY());
				}
			}
		});

		JMenuItem againSearchItem = new JMenuItem("Search Again");
		searchTreePopupMenu.add(againSearchItem);
		searchTreePopupMenu.addSeparator();
		JMenuItem renameSearchItem = new JMenuItem("Rename");
		searchTreePopupMenu.add(renameSearchItem);
		JMenuItem deleteSearchItem = new JMenuItem("Delete");
		searchTreePopupMenu.add(deleteSearchItem);

		againSearchItem.addActionListener(event ->
		{
			final TreePath path = searchTree.getSelectionPath();
			if(path == null) return;
			final DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
			final Object selectedSearch = selectedNode.getUserObject();
			if(selectedSearch instanceof String)
				return;
			else
				new Thread(()->
				{
					clientFrame.getClient().cdrSearch((Search<?>) selectedSearch, true);
/*					((SearchTreeModel) searchTreeModel).changeNode((Search<?>) selectedSearch);
					selectNode(searchTree, selectedSearch);
					repaint(null, false, false);*/
				}).start();
		});

		renameSearchItem.addActionListener(event ->
		{
			//MMM: should be in a new method of the new class for TabbedPane
			final  TreePath path = searchTree.getSelectionPath();
			if(path == null) return;
			final DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
			final Search<?> selectedSearch = (Search<?>) selectedNode.getUserObject();
			String newName = (String) JOptionPane.showInputDialog(clientFrame, "Enter new name: ", "Rename a search",
					JOptionPane.PLAIN_MESSAGE, null, null, selectedSearch.getSearchName());
			if(newName != null)
				selectedSearch.setSearchName(newName);
			searchTreeModel.nodeChanged(selectedNode);
		});

		deleteSearchItem.addActionListener( event ->
		{
			//MMM: should be in a new method of the new class for TabbedPane
			TreePath path = searchTree.getSelectionPath();
			int selectedRow = searchTree.getMinSelectionRow();
			if(path == null) return;
			DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
			Object selectedSearch = selectedNode.getUserObject();

			Class<?> nodeClass = null; // Class object of the node in the tree
			if(!(selectedSearch instanceof String))
			{

/*				nodeClass = ((Search<?>) selectedSearch).getResultType();
				if(nodeClass == PartyType.class)
					myParty.getPartySearches().remove((Search<PartyType>) selectedSearch);
				else if(nodeClass == CatalogueType.class)
					myParty.getCatalogueSearches().remove((Search<CatalogueType>) selectedSearch);
				searchTreeModel.removeNodeFromParent(selectedNode);*/
				if(((SearchTreeModel) searchTreeModel).isLastObject((Search<?>) selectedSearch))
					--selectedRow;
				((SearchTreeModel) searchTreeModel).deleteNode((Search<?>) selectedSearch, true);
				searchTree.setSelectionRow(selectedRow);
//				repaint(null, false);
			}
		});

		arrangeTab(leftPane, rightPane);
		component = tabPane;
	}

	/**
	 * Creates {@link TableRowSorter} and sets first column not to be sortable.
	 * @param tableModel
	 * @param table
	 * @return sorter
	 */
	private TableRowSorter<AbstractTableModel> getTableRowSorter(AbstractTableModel tableModel, JTable table)
	{
		TableRowSorter<AbstractTableModel> sorter = new TableRowSorter<AbstractTableModel>(tableModel);
		sorter.setSortable(0, false);
		sorter.addRowSorterListener(new RowSorterListener()
		{
		    @Override
		    public void sorterChanged(RowSorterEvent evt)
		    {
		    	//doesn't work in searchTree???!!!
		        int columnIndex = 0;
		        for (int i = 0; i < table.getRowCount(); i++)
		        	table.setValueAt(i + 1, i, columnIndex);
		    }
		});
		return sorter;
	}

	/**
	 * Notifies {@link TableRowSorter} about model change.
	 * @param sorter
	 */
	private void notifyRowSorter(TableRowSorter<AbstractTableModel> sorter, JTable table)
	{
		sorter.setSortable(0, false);
		sorter.allRowsChanged();
/*		sorter.addRowSorterListener(new RowSorterListener()
		{
		    @Override
		    public void sorterChanged(RowSorterEvent evt)
		    {
		        int columnIndex = 0;
		        for (int i = 0; i < table.getRowCount(); i++)
		        	table.setValueAt(i + 1, i, columnIndex);
		    	EventQueue.invokeLater(() -> JOptionPane.showMessageDialog(clientFrame, "Sorter changed!"));

		    }
		});*/
	}

	/**
	 * Sets the left and right {@code Component}s of the tab's pane.
	 * @param left
	 * @param right
	 */
	public void arrangeTab(Component left, Component right)
	{
		if(tabPane == null)
			tabPane = new JPanel(new BorderLayout());
		if(tabPane != null  && tabPane.getComponentCount() != 0)
			tabPane.removeAll();
		tabPane.add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right));
		repaint();
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
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		final JPopupMenu partyTablePopupMenu = new JPopupMenu();
		final JMenuItem unfollowPartyItem = new JMenuItem("Unfollow party");
		final JMenuItem addPartnerItem = new JMenuItem("Add to Business Partners");
		final JMenuItem removePartnerItem = new JMenuItem("Remove from Business Partners");
		final MyParty myParty = clientFrame.getClient().getMyParty();

		unfollowPartyItem.addActionListener(event ->
		{
			final int rowIndex = table.rowAtPoint(partyTablePopupMenu.getBounds().getLocation());
			final BusinessParty selectedParty = ((PartyListTableModel) table.getModel()).getParty(rowIndex);
			new Thread(()->
			{
				clientFrame.getClient().cdrUnfollowParty(selectedParty);
			}).start();
		});

		addPartnerItem.addActionListener(event ->
		{
			final int rowIndex = table.rowAtPoint(partyTablePopupMenu.getBounds().getLocation());
			final BusinessParty selectedParty = ((PartyListTableModel) table.getModel()).getParty(rowIndex);
			{
				selectedParty.setPartner(true);
				myParty.followParty(selectedParty);
				clientFrame.appendToConsole("Party " + selectedParty.getPartySimpleName() + " has been moved from Other Parties to Business Partners. "
						+ "Party is still followed by My Party.", Color.GREEN);
				repaint();
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
				clientFrame.appendToConsole("Party " + selectedParty.getPartySimpleName() + " has been moved from Business Partners to Other Parties. "
						+ "Party is still followed by My Party.", Color.GREEN);
				repaint();
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
						final PartyListTableModel partyListTableModel = (PartyListTableModel) table.getModel();
						final BusinessParty party = partyListTableModel.getParty(rowIndex);
						partnerCatalogueTableModel.setParty(party);
						rightScrollPane.setViewportView(partnerCatalogueTable);
						selectNode(partyTree, party);
						repaint();
					}
				});
			}
		});
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

	private JTable createSearchListTable(AbstractTableModel tableModel)
	{
		final MyParty myParty = clientFrame.getClient().getMyParty();
		if(tableModel == null) return null;
		final JTable table =  new JTable(tableModel);
		table.setFillsViewportHeight(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		TableColumnModel colModel = table.getColumnModel();
		colModel.getColumn(0).setMinWidth(20);
		colModel.getColumn(0).setPreferredWidth(20);
		colModel.getColumn(0).setMaxWidth(25);
		colModel.getColumn(1).setPreferredWidth(200);
		colModel.getColumn(2).setPreferredWidth(200);
		colModel.getColumn(3).setPreferredWidth(200);

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
				clientFrame.getClient().cdrSearch(selectedSearch, true);
//				((SearchTreeModel) searchTree.getModel()).deleteNode(selectedSearch);
				repaint();
			}).start();
		});

		renameSearchItem.addActionListener(event ->
		{
			int rowIndex = table.getSelectedRow();
			Search<?> selectedSearch = (Search<?>) ((SearchListTableModel<?>) tableModel).getSearches().get(rowIndex);
			String newName = (String) JOptionPane.showInputDialog(clientFrame, "Enter new name: ", "Rename a search",
					JOptionPane.PLAIN_MESSAGE, null, null, selectedSearch.getSearchName());
			if(newName != null)
				selectedSearch.setSearchName(newName);
			repaint();
		});

		deleteSearchItem.addActionListener( event ->
		{
			int rowIndex = table.getSelectedRow();
			Search<?> selectedSearch = (Search<?>) ((SearchListTableModel<?>) tableModel).getSearches().get(rowIndex);

/*			Class<?> searchClazz = selectedSearch.getResultType();
			if(searchClazz == PartyType.class)
			{
				if (myParty.getPartySearches().remove((Search<PartyType>) selectedSearch)) ;
//					repaint(selectedSearch, false, true);  // loadTab(tabbedPane.getSelectedIndex());
			}
			else if(searchClazz == CatalogueType.class)
			{
				if (myParty.getCatalogueSearches().remove((Search<CatalogueType>) selectedSearch)) ;
//					repaint(selectedSearch, false, true);  // loadTab(tabbedPane.getSelectedIndex());
			}*/

			Class<?> searchClazz = selectedSearch.getClass();
			if(searchClazz == PartySearch.class)
			{
				if (myParty.getPartySearches().remove(selectedSearch))
					repaint();  // loadTab(tabbedPane.getSelectedIndex());
			}
			else if(searchClazz == CatalogueSearch.class)
			{
				if (myParty.getCatalogueSearches().remove(selectedSearch))
					repaint();  // loadTab(tabbedPane.getSelectedIndex());
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
/*							final PartySearchTableModel tableModel = new PartySearchTableModel(false);
							final Search<PartyType> search = myParty.getPartySearches().get(rowIndex);
							tableModel.setSearch(search);
							final JTable searchTable = createSearchPartyTable(tableModel);
							selectNode(searchTree, search);
							rightPane.removeAll();
							rightPane.add(new JScrollPane(searchTable));
							repaint();*/

							final Search<PartyType> search = myParty.getPartySearches().get(rowIndex);
							searchesPartyTableModel.setSearch(search);
							selectNode(searchTree, search);
							searchesPartySorter.allRowsChanged();
							searchesPartySorter.setSortable(0, false);
							rightScrollPane.setViewportView(searchesPartyTable);
							repaint();

						}
						else if("Catalogues".equals(nodeTitle))
						{
/*							final CatalogueSearchTableModel tableModel = new CatalogueSearchTableModel(false);
							final Search<CatalogueType> search = myParty.getCatalogueSearches().get(rowIndex);
							tableModel.setSearch(search);
							final JTable searchTable = createSearchCatalogueTable(tableModel);
							selectNode(searchTree, search);
							rightPane.removeAll();
							rightPane.add(new JScrollPane(searchTable));
							repaint();*/

							final Search<CatalogueType> search = myParty.getCatalogueSearches().get(rowIndex);
							searchesCatalogueTableModel.setSearch(search);
							selectNode(searchTree, search);
							searchesCatalogueSorter.allRowsChanged();
							searchesCatalogueSorter.setSortable(0, false);
							rightScrollPane.setViewportView(searchesCatalogueTable);
							repaint();
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

	/**
	 * Creates table displaying list of parties that are the result of quering the CDR.
	 * @param tableModel model containing party data to display
	 * @return constructed table object
	 */
	private JTable createSearchPartyTable(AbstractTableModel tableModel)
	{
		final JTable table = newEmptyPartyListTable(tableModel);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
				clientFrame.followParty(followingName, followingID, true);
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
				clientFrame.followParty(followingName, followingID, false);
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

	private JTable createSearchCatalogueTable(AbstractTableModel tableModel)
	{
		JTable table = new JTable(tableModel);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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

/*	@Override
	public void repaint()
	{
		//MMM: check whether this repaints are nedded
//		super.repaint();
		rightPane.revalidate();
		rightPane.repaint();
		leftPane.revalidate();
		leftPane.repaint();



		for (Component comp: rightPane.getComponents())
		{
			comp.revalidate();
			comp.repaint();
		}
		for (Component comp: leftPane.getComponents())
		{
			comp.revalidate();
			comp.repaint();
		}

	}*/

}