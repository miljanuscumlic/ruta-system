package rs.ruta.client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import oasis.names.specification.ubl.schema.xsd.catalogue_21.CatalogueType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyIdentificationType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyNameType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import rs.ruta.client.BusinessParty;
import rs.ruta.client.CatalogueSearch;
import rs.ruta.client.gui.CatalogueSearchTableModel;
import rs.ruta.client.gui.CatalogueTableModel;
import rs.ruta.client.MyParty;
import rs.ruta.client.Party;
import rs.ruta.client.PartySearch;
import rs.ruta.client.gui.PartyListTableModel;
import rs.ruta.client.gui.PartySearchTableModel;
import rs.ruta.client.gui.PartyTreeModel;
import rs.ruta.client.Search;
import rs.ruta.client.gui.SearchListTableModel;
import rs.ruta.client.gui.SearchTreeModel;
import rs.ruta.common.PartnershipRequest;
import rs.ruta.common.PartnershipResponse;
import rs.ruta.common.InstanceFactory;
import rs.ruta.common.datamapper.DetailException;

public class TabCDRData extends TabComponent
{
	private static final long serialVersionUID = -2833805682921078609L;
	private static final String CATALOGUES = "Catalogues";
	private static final String PARTIES = "Parties";
	private static final String MY_PARTY = "My Party";
	private static final String SEARCHES = "Searches";
	private static final String FOLLOWINGS = "Followings";
	private static final String ARCHIVED_PARTIES = "Archived Parties";
	private static final String OTHER_PARTIES = "Other Parties";
	private static final String BUSINESS_PARTNERS = "Business Partners";
	private static final String SENT = "Sent";
	private static final String RECEIVED = "Received";

	private final JTree partyTree;
	private final JTree searchTree;

	private JTable partnerCatalogueTable;
	private CatalogueTableModel partnerCatalogueTableModel;
	private final TableRowSorter<DefaultTableModel> partnerCatalogueSorter;
	private JTable partiesTable;
	private PartyListTableModel partiesTableModel;
	private final TableRowSorter<DefaultTableModel> partiesSorter;
	private JTable requestsTable;
	private PartnershipRequestListTableModel sentRequestsTableModel;
	private final TableRowSorter<DefaultTableModel> requestsSorter;

	private JTable searchListTable;
	private JTable partySearchTable;
	private PartySearchTableModel partySearchTableModel;
	private final TableRowSorter<DefaultTableModel> partySearchSorter;
	private JTable catalogueSearchTable;
	private CatalogueSearchTableModel catalogueSearchTableModel;
	private final TableRowSorter<DefaultTableModel> catalogueSearchSorter;

	/**
	 * Creates tabbed pane for display of the CDR related data.
	 * @param clientFrame parent frame
	 */
	@SuppressWarnings("unchecked")
	public TabCDRData(RutaClientFrame clientFrame)
	{
		super(clientFrame);
		MyParty myParty = clientFrame.getClient().getMyParty();
		//constructing left pane
		DefaultTreeModel partyTreeModel = new PartyTreeModel(new DefaultMutableTreeNode(FOLLOWINGS), myParty);
		partyTree = new JTree(partyTreeModel);
		partyTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		PartyTreeCellRenderer partyTreeCellRenderer = new PartyTreeCellRenderer();
		partyTree.setCellRenderer(partyTreeCellRenderer);
		DefaultTreeModel searchTreeModel = new SearchTreeModel(new DefaultMutableTreeNode(SEARCHES), myParty);
		searchTree = new JTree(searchTreeModel);
		searchTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		selectNode(partyTree, FOLLOWINGS);
		JPanel treePanel = new JPanel(new BorderLayout());
		treePanel.add(partyTree, BorderLayout.NORTH);
		treePanel.add(searchTree, BorderLayout.CENTER);

		leftPane = new JScrollPane(treePanel);
		leftPane.setPreferredSize(new Dimension(325, 500));

		rightPane = new JPanel(new BorderLayout());
		rightScrollPane = new JScrollPane();
		rightPane.add(rightScrollPane);

		JLabel blankLabel = new JLabel();

		//partner (party) data table models
		final TabComponent.RowNumberRenderer rowNumberRenderer = new TabComponent.RowNumberRenderer();
		partnerCatalogueTableModel = new CatalogueTableModel();
		partnerCatalogueTable = createCatalogueTable(partnerCatalogueTableModel);
		partnerCatalogueTable.getColumnModel().getColumn(0).setCellRenderer(rowNumberRenderer);
		partiesTableModel = new PartyListTableModel();
		partiesTable = createPartyListTable(partiesTableModel);
		partiesTable.getColumnModel().getColumn(0).setCellRenderer(rowNumberRenderer);

		partnerCatalogueSorter = createTableRowSorter(partnerCatalogueTableModel);
		partnerCatalogueTable.setRowSorter(partnerCatalogueSorter);
		partiesSorter = createTableRowSorter(partiesTableModel);
		partiesTable.setRowSorter(partiesSorter);

		sentRequestsTableModel = new PartnershipRequestListTableModel();
		requestsTable = createRequestListTable(sentRequestsTableModel);
		requestsTable.getColumnModel().getColumn(0).setCellRenderer(rowNumberRenderer);
		requestsSorter = createTableRowSorter(sentRequestsTableModel, 3, true);
		requestsTable.setRowSorter(requestsSorter);

		//setting action listener for tab repaint on selection of the business party node
		partyTree.addTreeSelectionListener(event ->
		{
			final Object selectedParty = getSelectedUserObject(partyTree);
			if(selectedParty == null) return;
			searchTree.clearSelection();
			if (selectedParty instanceof BusinessParty)
			{
				((BusinessParty) selectedParty).setRecentlyUpdated(false);
				partnerCatalogueTableModel.setCatalogue(((BusinessParty) selectedParty).getCatalogue());
				partnerCatalogueTableModel.fireTableDataChanged();
				rightScrollPane.setViewportView(partnerCatalogueTable);
			}
			else //String
			{
				List<BusinessParty> partyList = null;
				List<PartnershipRequest> requestList = null;
				final String collectionNode = (String) selectedParty;
				if(MY_PARTY.equals(collectionNode))
				{
					final BusinessParty my = myParty.getMyFollowingParty();
					if(my != null)
					{
						partyList = new ArrayList<>();
						partyList.add(my);
					}
				}
				else if(BUSINESS_PARTNERS.equals(collectionNode))
					partyList = myParty.getBusinessPartners();
				else if(OTHER_PARTIES.equals(collectionNode))
					partyList = myParty.getOtherParties();
				else if(ARCHIVED_PARTIES.equals(collectionNode))
					partyList = myParty.getArchivedParties();
				else if(SENT.equals(collectionNode))
					requestList = myParty.getOutboundPartnershipRequests();
				else if(RECEIVED.equals(collectionNode))
					requestList = myParty.getInboundPartnershipRequests();

				if(partyList != null)
				{
					partiesTableModel.setParties(partyList);
					partiesSorter.allRowsChanged();
					rightScrollPane.setViewportView(partiesTable);
				}
				else if(requestList != null)
				{
					sentRequestsTableModel.setRequests(requestList);
					sentRequestsTableModel.fireTableDataChanged();
					rightScrollPane.setViewportView(requestsTable);
				}
				else
				{
					rightScrollPane.setViewportView(blankLabel);
				}
			}
			repaint();
		});

		JPopupMenu partyTreePopupMenu = new JPopupMenu();
		final JMenuItem followPartyItem = new JMenuItem("Follow Party");
		final JMenuItem unfollowPartyItem = new JMenuItem("Unfollow Party");
		final JMenuItem requestPartnershipItem = new JMenuItem("Request Business Partnership");
		final JMenuItem breakPartnershipItem = new JMenuItem("Break up Business Partnership");
		final JMenuItem deleteArchivedItem = new JMenuItem("Delete from Archived Parties");
		final JMenuItem viewPartyItem = new JMenuItem("View Party");

		viewPartyItem.addActionListener(event ->
		{
			final Object selectedParty = getSelectedUserObject(partyTree);
			if(selectedParty == null) return;
			new Thread(() ->
			{
				clientFrame.showPartyDialog(((BusinessParty) selectedParty).getCoreParty(), "View Party", false, false);
			}).start();
		});

		requestPartnershipItem.addActionListener(event ->
		{
			final Object selectedParty = getSelectedUserObject(partyTree);
			if(selectedParty == null) return;
			new Thread(() ->
			{
				clientFrame.getClient().requestPartnership(((BusinessParty) selectedParty).getCoreParty());
			}).start();
		});

		breakPartnershipItem.addActionListener(event ->
		{
			final Object selectedParty = getSelectedUserObject(partyTree);
			if(selectedParty == null) return;
			int option = JOptionPane.showConfirmDialog(clientFrame,
					"By breaking up the Partnership with " + ((BusinessParty) selectedParty).getPartySimpleName() +
					" party, you will not be able to do the business\nthrough the Ruta System with it." +
					" Do you want to proceed?",
							"Warning message", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if(option == JOptionPane.YES_OPTION)
			{
				new Thread(() ->
				{
					clientFrame.getClient().breakupPartnership(((BusinessParty) selectedParty).getCoreParty());
				}).start();
			}
		});

		followPartyItem.addActionListener(event ->
		{
			final Object selectedParty = getSelectedUserObject(partyTree);
			if(selectedParty == null) return;
			new Thread(() ->
			{
				final Party coreParty = ((BusinessParty) selectedParty).getCoreParty();
				final String followingName = InstanceFactory.
						getPropertyOrNull(coreParty.getPartyNameAtIndex(0), PartyNameType::getNameValue);
				final String followingID = InstanceFactory.
						getPropertyOrNull(coreParty.getPartyIdentificationAtIndex(0), PartyIdentificationType::getIDValue);
				try
				{
					clientFrame.getClient().followParty(followingName, followingID);
				}
				catch(Exception e)
				{
					clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder("Party ").
							append(((BusinessParty) selectedParty).getPartySimpleName()).
							append(" could not be added to the following parties!"));
				}
			}).start();
		});

		unfollowPartyItem.addActionListener(event ->
		{
			final BusinessParty selectedParty = (BusinessParty) getSelectedUserObject(partyTree);
			if(selectedParty == null) return;
			new Thread(()->
			{
				try
				{
					clientFrame.getClient().cdrUnfollowParty(selectedParty);
				}
				catch(Exception e)
				{
					clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder("Party ").
							append( selectedParty.getPartySimpleName()).
							append(" could not be removed from the following parties!"));
				}
			}).start();
		});

		deleteArchivedItem.addActionListener(event ->
		{
			final BusinessParty selectedParty = (BusinessParty) getSelectedUserObject(partyTree);
			if(selectedParty == null) return;
			new Thread(() ->
			{
				final String partyName = selectedParty.getPartySimpleName();
				try
				{
					myParty.purgeParty(selectedParty);
					clientFrame.appendToConsole(new StringBuilder("Party ").append(partyName).
							append(" has been deleted from Archived Parties."), Color.GREEN);
				}
				catch (DetailException e)
				{
					clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder("Party ").append(partyName).
							append(" could not be deleted from Archived Parties."));
					if(e.getMessage() != null && e.getMessage().contains("Party is a former"))
						logger.info(new StringBuilder("Party ").append(partyName).
								append(" could not be deleted from Archived Parties.").toString());
					else
						logger.error(new StringBuilder("Party ").append(partyName).
								append(" could not be deleted from Archived Parties.").toString(), e);
				}
			}).start();
		});

		partyTree.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent event)
			{
				if(SwingUtilities.isRightMouseButton(event))
				{
					final TreePath path = partyTree.getPathForLocation(event.getX(), event.getY());
					final Object selectedParty = getSelectedUserObject(path);
					if(selectedParty == null) return;
					partyTree.setSelectionPath(path);
					if(!(selectedParty instanceof String) && selectedParty != clientFrame.getClient().getMyParty().getMyFollowingParty())
					{
						partyTreePopupMenu.removeAll();
						partyTreePopupMenu.add(viewPartyItem);
						if(((BusinessParty) selectedParty).isPartner())
						{
							partyTreePopupMenu.add(breakPartnershipItem);
						}
						else if(((BusinessParty) selectedParty).isArchived())
						{
							partyTreePopupMenu.add(requestPartnershipItem);
							partyTreePopupMenu.add(followPartyItem);
							partyTreePopupMenu.addSeparator();
							partyTreePopupMenu.add(deleteArchivedItem);
						}
						else //Other Parties
						{
							partyTreePopupMenu.add(requestPartnershipItem);
							partyTreePopupMenu.add(unfollowPartyItem);
						}
						partyTreePopupMenu.show(partyTree, event.getX(), event.getY());
					}
				}
			}
		});

		//searches tables
		partySearchTableModel = new PartySearchTableModel();
		partySearchTable = createPartySearchTable(partySearchTableModel);
		partySearchTable.getColumnModel().getColumn(0).setCellRenderer(rowNumberRenderer);
		catalogueSearchTableModel = new CatalogueSearchTableModel();
		catalogueSearchTable = createCatalogueSearchTable(catalogueSearchTableModel);
		catalogueSearchTable.getColumnModel().getColumn(0).setCellRenderer(rowNumberRenderer);
		searchListTable = createSearchListTable(new SearchListTableModel<>());

		partySearchSorter = createTableRowSorter(partySearchTableModel);
		partySearchTable.setRowSorter(partySearchSorter);
		catalogueSearchSorter = createTableRowSorter(catalogueSearchTableModel);
		catalogueSearchTable.setRowSorter(catalogueSearchSorter);
		//there is no searchesTableModel and searchesTableSorter because there are a few different models that
		//could be instantiated based on the type of the object that is searched for: PartyType, CatalogueType

		//setting action listener for tab repaint on selection of the search nodes
		searchTree.addTreeSelectionListener(event ->
		{
			final Object selectedSearch = getSelectedUserObject(searchTree);
			if(selectedSearch == null) return;
			partyTree.clearSelection();
			if(selectedSearch instanceof PartySearch)
			{
				partySearchTableModel.setSearch((Search<PartyType>) selectedSearch);
				partySearchSorter.allRowsChanged();
				rightScrollPane.setViewportView(partySearchTable);
			}
			else if(selectedSearch instanceof CatalogueSearch)
			{
				catalogueSearchTableModel.setSearch((Search<CatalogueType>) selectedSearch);
				catalogueSearchSorter.allRowsChanged();
				rightScrollPane.setViewportView(catalogueSearchTable);
			}
			else //String
			{
				SearchListTableModel<?> searchesTableModel = null;
				if(PARTIES.equals((String) selectedSearch))
				{
					searchesTableModel = new SearchListTableModel<PartyType>();
					((SearchListTableModel<PartyType>) searchesTableModel).setSearches(myParty.getPartySearches());
				}
				else if(CATALOGUES.equals((String) selectedSearch))
				{
					searchesTableModel = new SearchListTableModel<CatalogueType>();
					((SearchListTableModel<CatalogueType>) searchesTableModel).setSearches(myParty.getCatalogueSearches());
				}
				if(!SEARCHES.equals((String) selectedSearch))
				{
					searchListTable.setModel(searchesTableModel);
					searchListTable.getColumnModel().getColumn(0).setCellRenderer(rowNumberRenderer);
					searchListTable.setRowSorter(createTableRowSorter(searchesTableModel, 3, true));
					rightScrollPane.setViewportView(searchListTable);
				}
				else
				{
					rightScrollPane.setViewportView(blankLabel);
				}
			}
			repaint();
		});

		JPopupMenu searchTreePopupMenu = new JPopupMenu();
		JMenuItem againSearchItem = new JMenuItem("Search Again");
		JMenuItem renameSearchItem = new JMenuItem("Rename");
		JMenuItem deleteSearchItem = new JMenuItem("Delete");
		searchTreePopupMenu.add(againSearchItem);
		searchTreePopupMenu.addSeparator();
		searchTreePopupMenu.add(renameSearchItem);
		searchTreePopupMenu.add(deleteSearchItem);

		againSearchItem.addActionListener(event ->
		{
			final Object selectedSearch = getSelectedUserObject(searchTree);
			if(selectedSearch == null) return;
			new Thread(() ->
			{
				try
				{
					clientFrame.getClient().cdrSearch((Search<?>) selectedSearch, true);
				}
				catch(Exception e)
				{
					clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder("Search could not be processed!"));
				}
			}).start();
		});

		renameSearchItem.addActionListener(event ->
		{
			final Search<?> selectedSearch = (Search<?>) getSelectedUserObject(searchTree);
			if(selectedSearch == null) return;

			final SearchNameDialog newNameDialog = new SearchNameDialog(clientFrame, selectedSearch.getSearchName());
			newNameDialog.setVisible(true);
			final String newName = newNameDialog.getSearchName();
			if(newName != null)
			{
				new Thread(() ->
				{
					try
					{
						selectedSearch.setSearchName(newName);
						myParty.updateSearch(selectedSearch);
					}
					catch(Exception e)
					{
						clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder("Search could not be processed!"));
					}
				}).start();
			}
		});

		deleteSearchItem.addActionListener( event ->
		{
			final Search<?> selectedSearch = (Search<?>) getSelectedUserObject(searchTree);
			if(selectedSearch == null) return;
			new Thread(() ->
			{
				try
				{
					myParty.removeSearch(selectedSearch);
				}
				catch(Exception e)
				{
					clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder("Search could not be processed!"));
				}
			}).start();
		});

		//mouse listener for the right click
		searchTree.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent event)
			{
				if(SwingUtilities.isRightMouseButton(event))
				{
					final TreePath path = searchTree.getPathForLocation(event.getX(), event.getY());
					searchTree.setSelectionPath(path);
					final Object selectedSearch = getSelectedUserObject(path);
					if(selectedSearch == null) return;
					if(!(selectedSearch instanceof String))
						searchTreePopupMenu.show(searchTree, event.getX(), event.getY());
				}
			}
		});

		arrangeTab();
	}

	/**
	 * Creates table containing list of parties e.g. Business Partners, Other Parties etc.
	 * @param tableModel model containing party data
	 * @return constructed table object
	 */
	private JTable createPartyListTable(DefaultTableModel tableModel)
	{
		final JTable table = newEmptyPartyListTable(tableModel);
		final JPopupMenu partyTablePopupMenu = new JPopupMenu();
		final JMenuItem unfollowPartyItem = new JMenuItem("Unfollow party");
		final JMenuItem deleteArchivedItem = new JMenuItem("Delete from Archived Parties");
		final JMenuItem followPartyItem = new JMenuItem("Follow Party");
		final JMenuItem requestPartnershipItem = new JMenuItem("Request Business Partnership");
		final JMenuItem breakPartnershipItem = new JMenuItem("Break up Business Partnership");
		final JMenuItem viewPartyItem = new JMenuItem("View Party");

		final MyParty myParty = clientFrame.getClient().getMyParty();

		viewPartyItem.addActionListener(event ->
		{
			new Thread(() ->
			{
				final int modelRowIndex = table.convertRowIndexToModel(table.getSelectedRow());
				final BusinessParty selectedParty = partiesTableModel.getPartyAtIndex(modelRowIndex);
				clientFrame.showPartyDialog(selectedParty.getCoreParty(), "View Party", false, false);
			}).start();
		});

		requestPartnershipItem.addActionListener(event ->
		{
			new Thread(() ->
			{
				final int modelRowIndex = table.convertRowIndexToModel(table.getSelectedRow());
				final BusinessParty selectedParty = partiesTableModel.getPartyAtIndex(modelRowIndex);
				clientFrame.getClient().requestPartnership(selectedParty.getCoreParty());
			}).start();
		});

		breakPartnershipItem.addActionListener(event ->
		{
			new Thread(() ->
			{
				final int modelRowIndex = table.convertRowIndexToModel(table.getSelectedRow());
				final BusinessParty selectedParty = partiesTableModel.getPartyAtIndex(modelRowIndex);
				clientFrame.getClient().breakupPartnership(selectedParty.getCoreParty());
			}).start();
		});

		followPartyItem.addActionListener(event ->
		{
			final int modelRowIndex = table.convertRowIndexToModel(table.getSelectedRow());
			final BusinessParty selectedParty = partiesTableModel.getPartyAtIndex(modelRowIndex);
			final Party coreParty = selectedParty.getCoreParty();
			final String followingName = InstanceFactory.
					getPropertyOrNull(coreParty.getPartyNameAtIndex(0), PartyNameType::getNameValue);
			final String followingID = InstanceFactory.getPropertyOrNull(coreParty.getPartyIdentificationAtIndex(0),
					PartyIdentificationType::getIDValue);
			new Thread(()->
			{
				try
				{
					clientFrame.getClient().followParty(followingName, followingID);
				}
				catch(Exception e)
				{
					clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder("Party ").
							append(((BusinessParty) selectedParty).getPartySimpleName()).
							append(" could not be removed from the following parties!"));
				}
			}).start();
		});

		unfollowPartyItem.addActionListener(event ->
		{
			final int modelRowIndex = table.convertRowIndexToModel(table.getSelectedRow());
			final BusinessParty selectedParty = ((PartyListTableModel) table.getModel()).getPartyAtIndex(modelRowIndex);
			new Thread(()->
			{
				try
				{
					clientFrame.getClient().cdrUnfollowParty(selectedParty);
				}
				catch(Exception e)
				{
					clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder("Party ").
							append(((BusinessParty) selectedParty).getPartySimpleName()).
							append(" could not be removed from the following parties!"));
				}
			}).start();
		});

		deleteArchivedItem.addActionListener(event ->
		{
			final int modelRowIndex = table.convertRowIndexToModel(table.getSelectedRow());
			final BusinessParty selectedParty = partiesTableModel.getPartyAtIndex(modelRowIndex);
			new Thread(() ->
			{
				final String partyName = selectedParty.getPartySimpleName();
				try
				{
					myParty.purgeParty(selectedParty);
					clientFrame.appendToConsole(new StringBuilder("Party ").append(partyName).
							append(" has been deleted from Archived Parties."), Color.GREEN);
				}
				catch (DetailException e)
				{
					clientFrame.appendToConsole(new StringBuilder("Party ").append(partyName).
							append(" could not be deleted from Archived Parties."), Color.GREEN);
					logger.error(new StringBuilder("Party ").append(partyName).
							append(" could not be deleted from Archived Parties.").toString(), e);
				}
			}).start();
		});

		table.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent event)
			{
				EventQueue.invokeLater(() ->
				{
					final int viewRowIndex = table.rowAtPoint(event.getPoint());
					if(viewRowIndex != -1)
					{
						final int modelRowIndex = table.convertRowIndexToModel(viewRowIndex);
						if(SwingUtilities.isRightMouseButton(event))
						{
							table.setRowSelectionInterval(viewRowIndex, viewRowIndex);

							final Object selectedParty = getSelectedUserObject(partyTree);
							if(selectedParty == null) return;
							if(selectedParty instanceof String)
							{
								partyTablePopupMenu.removeAll();
								final String nodeTitle = (String) selectedParty;
								if(BUSINESS_PARTNERS.equals(nodeTitle))
								{
									partyTablePopupMenu.add(viewPartyItem);
									partyTablePopupMenu.add(breakPartnershipItem);
								}
								else if(OTHER_PARTIES.equals(nodeTitle))
								{
									partyTablePopupMenu.add(viewPartyItem);
									partyTablePopupMenu.add(requestPartnershipItem);
									partyTablePopupMenu.add(unfollowPartyItem);
								}
								else if(ARCHIVED_PARTIES.equals(nodeTitle))
								{
									partyTablePopupMenu.add(viewPartyItem);
									partyTablePopupMenu.add(requestPartnershipItem);
									partyTablePopupMenu.add(followPartyItem);
									partyTablePopupMenu.addSeparator();
									partyTablePopupMenu.add(deleteArchivedItem);
								}
								partyTablePopupMenu.show(table, event.getX(), event.getY());
							}
						}
						else if(SwingUtilities.isLeftMouseButton(event) && event.getClickCount() == 2)
						{
							final PartyListTableModel partyListTableModel = (PartyListTableModel) table.getModel();
							final BusinessParty party = partyListTableModel.getPartyAtIndex(modelRowIndex);
							partnerCatalogueTableModel.setCatalogue(party.getCatalogue());
							rightScrollPane.setViewportView(partnerCatalogueTable);
							selectNode(partyTree, party);
							repaint();
						}
					}
				});
			}
		});
		return table;
	}

	/**
	 * Creates table containing list of Partnership Requests.
	 * @param tableModel model containing requests' data
	 * @return constructed table object
	 */
	private JTable createRequestListTable(DefaultTableModel tableModel)
	{
		JTable table = new JTable(tableModel);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setFillsViewportHeight(true);
		TableColumnModel colModel = table.getColumnModel();
		colModel.getColumn(0).setPreferredWidth(20);
		colModel.getColumn(1).setPreferredWidth(150);
		colModel.getColumn(2).setPreferredWidth(150);
		colModel.getColumn(3).setPreferredWidth(100);
		colModel.getColumn(4).setPreferredWidth(100);

		final JPopupMenu requestTablePopupMenu = new JPopupMenu();
		final JMenuItem viewRequsterPartyItem = new JMenuItem("View Requester Party");
		final JMenuItem viewRequstedPartyItem = new JMenuItem("View Requested Party");
		final JMenuItem processItem = new JMenuItem("Process Request");

		final MyParty myParty = clientFrame.getClient().getMyParty();

		viewRequsterPartyItem.addActionListener(event ->
		{
			final int modelRowIndex = table.convertRowIndexToModel(table.getSelectedRow());
			new Thread(() ->
			{
				PartnershipRequest selectedRequest = null;
				selectedRequest = sentRequestsTableModel.getRequestAtIndex(modelRowIndex);
				if(selectedRequest == null) return;
				clientFrame.showPartyDialog(new Party(selectedRequest.getRequesterParty()), "View Requester Party", false, false);
			}).start();
		});

		viewRequstedPartyItem.addActionListener(event ->
		{
			final int modelRowIndex = table.convertRowIndexToModel(table.getSelectedRow());
			new Thread(() ->
			{
				PartnershipRequest selectedRequest = null;
				selectedRequest = sentRequestsTableModel.getRequestAtIndex(modelRowIndex);
				if(selectedRequest == null) return;
				clientFrame.showPartyDialog(new Party(selectedRequest.getRequestedParty()), "View Requested Party", false, false);
			}).start();
		});

		processItem.addActionListener(event ->
		{
			final int modelRowIndex = table.convertRowIndexToModel(table.getSelectedRow());
			new Thread(() ->
			{
				PartnershipRequest selectedRequest = null;
				selectedRequest = sentRequestsTableModel.getRequestAtIndex(modelRowIndex);
				if(selectedRequest == null) return;
				final String requesterPartyName = selectedRequest.getRequesterPartyName();
				final Semaphore waiter = new Semaphore(0);
				final AtomicInteger aOption = new AtomicInteger();
				EventQueue.invokeLater( () ->
				{
					int option = JOptionPane.showConfirmDialog(clientFrame,
							"Do you accept Business Partnership with " + requesterPartyName + "?",
							"Process Business Partnership Request", JOptionPane.YES_NO_CANCEL_OPTION,
							JOptionPane.QUESTION_MESSAGE);
					aOption.set(option);
					waiter.release();
				});
				try
				{
					waiter.acquire();
					PartnershipResponse partnershipResponse;
					if(aOption.get() == JOptionPane.YES_OPTION)
					{
						partnershipResponse = InstanceFactory.createPartnershipResponse(selectedRequest, true);
						myParty.getClient().cdrResponsePartnership(partnershipResponse);
					}
					else if(aOption.get() == JOptionPane.NO_OPTION)
					{
						partnershipResponse = InstanceFactory.createPartnershipResponse(selectedRequest, false);
						myParty.getClient().cdrResponsePartnership(partnershipResponse);
					}
				}
				catch (InterruptedException e)
				{
					logger.error("Unable to synchronise with the Event Dispatch Thread.\n", e);
				}
			}).start();
		});


		table.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent event)
			{
				EventQueue.invokeLater(() ->
				{
					final int viewRowIndex = table.rowAtPoint(event.getPoint());
					if(viewRowIndex != -1)
					{
						final int modelRowIndex = table.convertRowIndexToModel(viewRowIndex);
						if(SwingUtilities.isRightMouseButton(event))
						{
							table.setRowSelectionInterval(viewRowIndex, viewRowIndex);

							final Object selectedObject = getSelectedUserObject(partyTree);
							if(selectedObject == null) return;
							if(selectedObject.getClass() == String.class)
							{
								final String nodeTitle = (String) selectedObject;
								if(SENT.equals(nodeTitle))
								{
									requestTablePopupMenu.removeAll();
									requestTablePopupMenu.add(viewRequstedPartyItem);
								}
								else if(RECEIVED.equals(nodeTitle))
								{
									requestTablePopupMenu.removeAll();
									requestTablePopupMenu.add(viewRequsterPartyItem);
									final PartnershipRequest selectedRequest = sentRequestsTableModel.getRequestAtIndex(modelRowIndex);
									if(selectedRequest != null && !selectedRequest.isResolved())
										requestTablePopupMenu.add(processItem);
								}
								else
									requestTablePopupMenu.removeAll();
								requestTablePopupMenu.show(table, event.getX(), event.getY());
							}
						}
					}
				});
			}
		});
		return table;
	}

	/**
	 * Creates table showing list of all searches of the CDR service.
	 * @param tableModel model containing data
	 * @return constructed table object
	 */
	private JTable createSearchListTable(DefaultTableModel tableModel)
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
		JMenuItem againSearchItem = new JMenuItem("Search Again");
		JMenuItem renameSearchItem = new JMenuItem("Rename");
		JMenuItem deleteSearchItem = new JMenuItem("Delete");
		searchTablePopupMenu.add(againSearchItem);
		searchTablePopupMenu.addSeparator();
		searchTablePopupMenu.add(renameSearchItem);
		searchTablePopupMenu.add(deleteSearchItem);

		//in all listeners table model must be get with table.getModel() in orderLines to have a current instance of it
		againSearchItem.addActionListener(event ->
		{
			final int modelRowIndex = table.convertRowIndexToModel(table.getSelectedRow());
			Search<?> selectedSearch = (Search<?>) ((SearchListTableModel<?>) table.getModel()).getSearches().get(modelRowIndex);
			new Thread(()->
			{
				try
				{
					final Future<?> ret = clientFrame.getClient().cdrSearch((Search<?>) selectedSearch, true);
					if(ret != null)
					{
						ret.get();
						EventQueue.invokeLater(() ->
						{
							selectNode(searchTree, selectedSearch);
						});
					}
				}
				catch(Exception e)
				{
					clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder("Search could not be processed!"));
				}
			}).start();
		});

		renameSearchItem.addActionListener(event ->
		{
			final int modelRowIndex = table.convertRowIndexToModel(table.getSelectedRow());
			Search<?> selectedSearch = (Search<?>) ((SearchListTableModel<?>) table.getModel()).getSearches().get(modelRowIndex);
			String newName = (String) JOptionPane.showInputDialog(clientFrame, "Enter new name: ", "Rename a search",
					JOptionPane.PLAIN_MESSAGE, null, null, selectedSearch.getSearchName());
			if(newName != null)
			{
				new Thread(() ->
				{
					try
					{
						selectedSearch.setSearchName(newName);
						myParty.updateSearch(selectedSearch);
						((DefaultTableModel) table.getModel()).fireTableDataChanged();
					}
					catch(Exception e)
					{
						clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder("Search could not be processed!"));
					}
				}).start();
			}
		});

		deleteSearchItem.addActionListener( event ->
		{
			final int modelRowIndex = table.convertRowIndexToModel(table.getSelectedRow());
			Search<?> selectedSearch = (Search<?>) ((SearchListTableModel<?>) table.getModel()).getSearches().get(modelRowIndex);
			new Thread(() ->
			{
				try
				{
					myParty.removeSearch(selectedSearch);
					((DefaultTableModel) table.getModel()).fireTableDataChanged();
				}
				catch(Exception e)
				{
					clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder("Search could not be processed!"));
				}
			}).start();
		});

		table.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent event)
			{
				final int rowIndex = table.rowAtPoint(event.getPoint());
				if(rowIndex != -1)
				{
					final int modelRowIndex = table.convertRowIndexToModel(rowIndex);
					if(SwingUtilities.isLeftMouseButton(event) && event.getClickCount() == 2)
					{
						final Object selectedSearchListNode = getSelectedUserObject(searchTree);
						if(selectedSearchListNode == null) return;
						if(selectedSearchListNode instanceof String)
						{
							final String nodeTitle = (String) selectedSearchListNode;
							if(PARTIES.equals(nodeTitle))
							{
								final Search<PartyType> selectedSearch = myParty.getPartySearches().get(modelRowIndex);
								partySearchTableModel.setSearch(selectedSearch);
								selectNode(searchTree, selectedSearch);
								rightScrollPane.setViewportView(partySearchTable);
							}
							else if(CATALOGUES.equals(nodeTitle))
							{
								final Search<CatalogueType> selectedSearch = myParty.getCatalogueSearches().get(modelRowIndex);
								catalogueSearchTableModel.setSearch(selectedSearch);
								selectNode(searchTree, selectedSearch);
								rightScrollPane.setViewportView(catalogueSearchTable);
							}
						}
					}
					else
					{
						if(SwingUtilities.isRightMouseButton(event))
						{
							table.setRowSelectionInterval(rowIndex, rowIndex);
							searchTablePopupMenu.show(table, event.getX(), event.getY());
						}
					}
				}
			}
		});

		return table;
	}

	/**
	 * Creates table containing list of parties that are the result of quering the CDR.
	 * @param tableModel model containing party data
	 * @return constructed table object
	 */
	private JTable createPartySearchTable(DefaultTableModel tableModel)
	{
		final JTable table = newEmptyPartyListTable(tableModel);
		final JPopupMenu popupMenu = new JPopupMenu();
		final JMenuItem followPartyItem = new JMenuItem("Follow Party");
		final JMenuItem requestPartnershipItem = new JMenuItem("Request Business Partnership");
		final JMenuItem viewPartyItem = new JMenuItem("View Party");

		popupMenu.add(viewPartyItem);
		popupMenu.add(requestPartnershipItem);
		popupMenu.add(followPartyItem);

		viewPartyItem.addActionListener(event ->
		{
			final int modelRowIndex = table.convertRowIndexToModel(table.getSelectedRow());
			new Thread(() ->
			{
				final PartyType selectedParty = ((PartySearchTableModel) table.getModel()).getParty(modelRowIndex);
				clientFrame.showPartyDialog(new Party(selectedParty), "View Party", false, false);
			}).start();
		});

		requestPartnershipItem.addActionListener(event ->
		{
			new Thread(() ->
			{
				final int modelRowIndex = table.convertRowIndexToModel(table.getSelectedRow());
				final PartyType selectedParty = ((PartySearchTableModel) table.getModel()).getParty(modelRowIndex);
				clientFrame.getClient().requestPartnership(selectedParty);
			}).start();
		});

		followPartyItem.addActionListener(event ->
		{
			final int modelRowIndex = table.convertRowIndexToModel(table.getSelectedRow());
			final PartyType selectedParty = ((PartySearchTableModel) table.getModel()).getParty(modelRowIndex);
			final String followingName =
					InstanceFactory.getPropertyOrNull(selectedParty.getPartyNameAtIndex(0), PartyNameType::getNameValue);
			final String followingID = InstanceFactory.getPropertyOrNull(selectedParty.getPartyIdentificationAtIndex(0),
					PartyIdentificationType::getIDValue);
			new Thread(()->
			{
				try
				{
					clientFrame.getClient().followParty(followingName, followingID);
				}
				catch(Exception e)
				{
					clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder("Party ").
							append(followingName).append(" could not be added to the following parties!"));
				}
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
						if(rowIndex != -1)
						{
							table.setRowSelectionInterval(rowIndex, rowIndex);
							popupMenu.show(table, event.getX(), event.getY());
						}
					}
				});
			}
		});

		return table;
	}

	/**
	 * Creates table containing list of catalogue items that are the result of quering the CDR.
	 * @param tableModel model containing catalogue data
	 * @return constructed table object
	 */
	private JTable createCatalogueSearchTable(DefaultTableModel tableModel)
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

		final JPopupMenu popupMenu = new JPopupMenu();
		final JMenuItem followPartyItem = new JMenuItem("Follow Party");
		final JMenuItem requestPartnershipItem = new JMenuItem("Request Business Partnership");
		final JMenuItem viewPartyItem = new JMenuItem("View Party");
		popupMenu.add(viewPartyItem);
		popupMenu.add(requestPartnershipItem);
		popupMenu.add(followPartyItem);

		viewPartyItem.addActionListener(event ->
		{
			new Thread(() ->
			{
				final int modelRowIndex = table.convertRowIndexToModel(table.getSelectedRow());
				final PartyType selectedParty = catalogueSearchTableModel.getParty(modelRowIndex);
				clientFrame.showPartyDialog(new Party(selectedParty), "View Party", false, false);
			}).start();
		});

		requestPartnershipItem.addActionListener(event ->
		{
			new Thread(() ->
			{
				final int modelRowIndex = table.convertRowIndexToModel(table.getSelectedRow());
				final PartyType selectedParty = catalogueSearchTableModel.getParty(modelRowIndex);
				clientFrame.getClient().requestPartnership(selectedParty);
			}).start();
		});

		followPartyItem.addActionListener(event ->
		{
			final int modelRowIndex = table.convertRowIndexToModel(table.getSelectedRow());
			final PartyType selectedParty = catalogueSearchTableModel.getParty(modelRowIndex);
			final String followingName = InstanceFactory.getPropertyOrNull(selectedParty.getPartyNameAtIndex(0),
					PartyNameType::getNameValue);
			final String followingID = InstanceFactory.getPropertyOrNull(selectedParty.getPartyIdentificationAtIndex(0),
					PartyIdentificationType::getIDValue);
			new Thread(() ->
			{
				try
				{
					clientFrame.getClient().followParty(followingName, followingID);
				}
				catch(Exception e)
				{
					clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder("Party ").append(followingName).
							append(" could not be added to the following parties!"));
				}
			}).start();
		});

		table.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent event)
			{
				if(SwingUtilities.isRightMouseButton(event))
				{
					final int rowIndex = table.rowAtPoint(event.getPoint());
					if(rowIndex != -1)
					{
						table.addRowSelectionInterval(rowIndex, rowIndex);
						popupMenu.show(table, event.getX(), event.getY());
					}
				}
			}
		});
		return table;
	}

	@Override
	protected void doDispatchEvent(ActionEvent event)
	{
		final Object source = event.getSource();
		final String command = event.getActionCommand();
		if(source.getClass() == BusinessParty.class)
		{
			BusinessParty party = (BusinessParty) source;
			if(BusinessPartyEvent.CATALOGUE_UPDATED.equals(command))
			{
				makeVisibleNode(partyTree, party);
				final Object selectedUserObject = getSelectedUserObject(partyTree);
				if(selectedUserObject instanceof BusinessParty)
				{
					if(selectedUserObject == party)
						partnerCatalogueTableModel.setCatalogue(party.getCatalogue());
					partnerCatalogueTableModel.fireTableDataChanged();
				}
			}
			else if(BusinessPartyEvent.BUSINESS_PARTNER_ADDED.equals(command))
			{
				makeVisibleNode(partyTree, party);
				final Object selectedUserObject = getSelectedUserObject(partyTree);
				if(selectedUserObject instanceof String)
					partiesTableModel.fireTableDataChanged();
			}
			else if(BusinessPartyEvent.BUSINESS_PARTNER_TRANSFERED.equals(command))
			{
				makeVisibleNode(partyTree, party);
				final Object selectedUserObject = getSelectedUserObject(partyTree);
				if(selectedUserObject instanceof String)
					partiesTableModel.fireTableDataChanged();
			}
			else if(BusinessPartyEvent.BUSINESS_PARTNER_REMOVED.equals(command))
			{
				makeVisibleNode(partyTree, party);
				final Object selectedUserObject = getSelectedUserObject(partyTree);
				if(selectedUserObject instanceof String)
					partiesTableModel.fireTableDataChanged();
			}
			else if(BusinessPartyEvent.OTHER_PARTY_ADDED.equals(command))
			{
				makeVisibleNode(partyTree, party);
				final Object selectedUserObject = getSelectedUserObject(partyTree);
				if(selectedUserObject instanceof String)
					partiesTableModel.fireTableDataChanged();
			}
			else if(BusinessPartyEvent.OTHER_PARTY_TRANSFERED.equals(command))
			{
				makeVisibleNode(partyTree, party);
				final Object selectedUserObject = getSelectedUserObject(partyTree);
				if(selectedUserObject instanceof String)
					partiesTableModel.fireTableDataChanged();
			}
			else if(BusinessPartyEvent.OTHER_PARTY_REMOVED.equals(command))
			{
//				makeVisibleNode(partyTree, party);
				final Object selectedUserObject = getSelectedUserObject(partyTree);
				if(selectedUserObject instanceof String)
					partiesTableModel.fireTableDataChanged();
			}
			else if(BusinessPartyEvent.ARCHIVED_PARTY_ADDED.equals(command))
			{
				makeVisibleNode(partyTree, party);
				final Object selectedUserObject = getSelectedUserObject(partyTree);
				if(selectedUserObject instanceof String)
					partiesTableModel.fireTableDataChanged();
			}
			else if(BusinessPartyEvent.ARCHIVED_PARTY_REMOVED.equals(command) ||
					BusinessPartyEvent.ARCHIVED_PARTY_TRANSFERED.equals(command))
			{
				makeVisibleNode(partyTree, party);
				final Object selectedUserObject = getSelectedUserObject(partyTree);
				if(selectedUserObject instanceof String)
					partiesTableModel.fireTableDataChanged();
			}
			else if(BusinessPartyEvent.PARTY_UPDATED.equals(command))
			{
				makeVisibleNode(partyTree, party);
				final Object selectedUserObject = getSelectedUserObject(partyTree);
				if(!(selectedUserObject instanceof BusinessParty))
					partiesTableModel.fireTableDataChanged();
			}
			else if(BusinessPartyEvent.PARTY_MOVED.equals(command))
			{
				makeVisibleNode(partyTree, party);
			}
			else if(RutaClientFrameEvent.SELECT_NEXT.equals(command))
			{
				selectNextNode(partyTree, party);
			}
		}
		else if(source instanceof PartnershipRequest)
		{
			if(PartnershipEvent.INBOUND_PARTNERSHIP_REQUEST_ADDED.equals(command))
			{
				makeVisibleNode(partyTree, RECEIVED);
				final Object selectedUserObject = getSelectedUserObject(partyTree);
				if(selectedUserObject instanceof String)
					sentRequestsTableModel.fireTableDataChanged();
			}
			else if(PartnershipEvent.OUTBOUND_PARTNERSHIP_REQUEST_ADDED.equals(command))
			{
				makeVisibleNode(partyTree, SENT);
				final Object selectedUserObject = getSelectedUserObject(partyTree);
				if(selectedUserObject instanceof String)
					sentRequestsTableModel.fireTableDataChanged();
			}
			else if(PartnershipEvent.INBOUND_PARTNERSHIP_REQUEST_UPDATED.equals(command))
			{
				makeVisibleNode(partyTree, RECEIVED);
				final Object selectedUserObject = getSelectedUserObject(partyTree);
				if(selectedUserObject instanceof String)
					sentRequestsTableModel.fireTableDataChanged();
			}
			else if(PartnershipEvent.OUTBOUND_PARTNERSHIP_REQUEST_UPDATED.equals(command))
			{
				makeVisibleNode(partyTree, SENT);
				final Object selectedUserObject = getSelectedUserObject(partyTree);
				if(selectedUserObject instanceof String)
					sentRequestsTableModel.fireTableDataChanged();
			}
			else if(PartnershipEvent.INBOUND_PARTNERSHIP_REQUEST_REMOVED.equals(command))
			{
				makeVisibleNode(partyTree, RECEIVED);
				final Object selectedUserObject = getSelectedUserObject(partyTree);
				if(selectedUserObject instanceof String)
					sentRequestsTableModel.fireTableDataChanged();
			}
			else if(PartnershipEvent.OUTBOUND_PARTNERSHIP_REQUEST_REMOVED.equals(command))
			{
				makeVisibleNode(partyTree, SENT);
				final Object selectedUserObject = getSelectedUserObject(partyTree);
				if(selectedUserObject instanceof String)
					sentRequestsTableModel.fireTableDataChanged();
			}
		}
		else if(source instanceof Search)
		{
			if(SearchEvent.PARTY_SEARCH_ADDED.equals(command) ||
					SearchEvent.CATALOGUE_SEARCH_ADDED.equals(command))
			{
				selectNode(searchTree, source);
			}
			else if(SearchEvent.PARTY_SEARCH_UPDATED.equals(command))
			{
				makeVisibleNode(searchTree, source);
				partySearchTableModel.fireTableDataChanged();
				selectNode(searchTree, source);
			}
			else if(SearchEvent.CATALOGUE_SEARCH_UPDATED.equals(command))
			{
				makeVisibleNode(searchTree, source);
				catalogueSearchTableModel.fireTableDataChanged();
				selectNode(searchTree, source);
			}
			else if(SearchEvent.PARTY_SEARCH_REMOVED.equals(command) ||
					SearchEvent.CATALOGUE_SEARCH_REMOVED.equals(command))
			{
				((AbstractTableModel) searchListTable.getModel()).fireTableDataChanged();
			}
			else if(RutaClientFrameEvent.SELECT_NEXT.equals(command))
			{
				selectNextNode(searchTree, source);
			}
		}
		else if(source.getClass() == ArrayList.class)
		{
			if(SearchEvent.ALL_CATALOGUE_SEARCHES_REMOVED.equals(command))
			{
				selectNode(partyTree, FOLLOWINGS);
			}
			else if(BusinessPartyEvent.ALL_PARTIES_REMOVED.equals(command))
			{
				selectNode(partyTree, FOLLOWINGS);
			}
		}
	}
}