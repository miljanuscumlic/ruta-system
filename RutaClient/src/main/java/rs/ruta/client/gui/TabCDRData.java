package rs.ruta.client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.xml.ws.WebServiceException;

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
import rs.ruta.client.RutaClientFrameEvent;
import rs.ruta.client.gui.PartyListTableModel;
import rs.ruta.client.gui.PartySearchTableModel;
import rs.ruta.client.gui.PartyTreeModel;
import rs.ruta.client.Search;
import rs.ruta.client.gui.SearchListTableModel;
import rs.ruta.client.gui.SearchTreeModel;
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
	private static final String DEREGISTERED_PARTIES = "Deregistered Parties";
	private static final String ARCHIVED_PARTIES = "Archived Parties";
	private static final String OTHER_PARTIES = "Other Parties";
	private static final String BUSINESS_PARTNERS = "Business Partners";

	private final JTree partyTree;
	private final JTree searchTree;
	//	private final JScrollPane leftScroolPane;
	private PartySearchTableModel searchesPartyTableModel;
	private JTable searchesPartyTable;
	private CatalogueSearchTableModel searchesCatalogueTableModel;
	private JTable searchesCatalogueTable;
	private JTable searchesTable;
	private final TableRowSorter<DefaultTableModel> searchesPartySorter;
	private final TableRowSorter<DefaultTableModel> searchesCatalogueSorter;

	private CatalogueTableModel partnerCatalogueTableModel;
	private JTable partnerCatalogueTable;
	private PartyListTableModel partiesTableModel;
	private JTable partiesTable;
	private final TableRowSorter<DefaultTableModel> partnerCatalogueSorter;
	private final TableRowSorter<DefaultTableModel> partiesSorter;

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
		PartyTreeCellRenderer partyTreeCellRenderer = new PartyTreeCellRenderer();
		partyTree.setCellRenderer(partyTreeCellRenderer);
		DefaultTreeModel searchTreeModel = new SearchTreeModel(new DefaultMutableTreeNode(SEARCHES), myParty);
		searchTree = new JTree(searchTreeModel);
		JPanel treePanel = new JPanel(new BorderLayout());
		treePanel.add(partyTree, BorderLayout.NORTH);
		treePanel.add(searchTree, BorderLayout.CENTER);

/*		JPanel treePanel = new JPanel(new GridLayout(2,1));
		treePanel.add(partyTree);
		treePanel.add(searchTree);*/

		leftPane = new JScrollPane(treePanel);
		leftPane.setPreferredSize(new Dimension(250, 500));

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
//				partnerCatalogueSorter.allRowsChanged();
				partnerCatalogueTableModel.fireTableDataChanged();
				rightScrollPane.setViewportView(partnerCatalogueTable);
			}
			else //String
			{
				List<BusinessParty> partyList = new ArrayList<>();
				if(MY_PARTY.equals((String) selectedParty))
				{
					final BusinessParty my = myParty.getMyFollowingParty();
					if(my != null)
						partyList.add(my);
				}
				else if(BUSINESS_PARTNERS.equals((String) selectedParty))
					partyList = myParty.getBusinessPartners();
				else if(OTHER_PARTIES.equals((String) selectedParty))
					partyList = myParty.getOtherParties();
				else if(ARCHIVED_PARTIES.equals((String) selectedParty))
					partyList = myParty.getArchivedParties();
				else if(DEREGISTERED_PARTIES.equals((String) selectedParty))
					partyList = myParty.getDeregisteredParties();

				if(!FOLLOWINGS.equals((String) selectedParty))
				{
					partiesTableModel.setParties(partyList);
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

		JPopupMenu partyTreePopupMenu = new JPopupMenu();
		final JMenuItem followPartnerItem = new JMenuItem("Follow as Business Partner");
		final JMenuItem followPartyItem = new JMenuItem("Follow as Party");
		final JMenuItem unfollowPartyItem = new JMenuItem("Unfollow party");
		final JMenuItem addPartnerItem = new JMenuItem("Add to Business Partners");
		final JMenuItem removePartnerItem = new JMenuItem("Remove from Business Partners");
		final JMenuItem deleteArchivedItem = new JMenuItem("Delete from Archived Parties");
		final JMenuItem deleteDeregisteredItem = new JMenuItem("Delete from Deregistered Parties");

		followPartnerItem.addActionListener(event ->
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
					Future<?> ret = clientFrame.getClient().followParty(followingName, followingID, true);
					if(ret != null)
					{
						ret.get();
						final BusinessParty followedParty = clientFrame.getClient().getMyParty().getBusinessPartner(followingID);
						//						EventQueue.invokeLater(() -> selectNode(partyTree, followedParty));
						EventQueue.invokeLater(() -> makeVisibleNode(partyTree, followedParty));
					}
				}
				catch(Exception e)
				{
					clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder("Party ").
							append(((BusinessParty) selectedParty).getPartySimpleName()).
							append(" could not be added to the following parties as a business partner!"));
				}
			}).start();
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
					Future<?> ret = clientFrame.getClient().followParty(followingName, followingID, false);
					if(ret != null)
					{
						ret.get();
						final BusinessParty followedParty = clientFrame.getClient().getMyParty().getOtherParty(followingID);
						//						EventQueue.invokeLater(() -> selectNode(partyTree, followedParty));
						EventQueue.invokeLater(() -> makeVisibleNode(partyTree, followedParty));
					}
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
			final Object selectedParty = getSelectedUserObject(partyTree);
			if(selectedParty == null) return;
			new Thread(()->
			{
				DefaultMutableTreeNode nodeToSelect = getNextNodeForSelection(partyTree, selectedParty);
				Future<?> ret = clientFrame.getClient().cdrUnfollowParty((BusinessParty) selectedParty);
				try
				{
					if(ret != null)
					{
						ret.get();
						EventQueue.invokeLater(() ->
						{
							selectNode(partyTree, nodeToSelect);
							makeVisibleNode(partyTree, selectedParty);
						});
					}
				}
				catch(Exception e)
				{
					clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder("Party ").
							append(((BusinessParty) selectedParty).getPartySimpleName()).
							append(" could not be removed from the following parties!"));
				}
			}).start();
			//				MMM: commented below is a not completed alternative with the SwingWorker
			//				new UnfollowPartyWorker((BusinessParty) selectedParty).execute();

		});

		addPartnerItem.addActionListener(event ->
		{
			final Object selectedParty = getSelectedUserObject(partyTree);
			if(selectedParty == null) return;
			new Thread(() ->
			{
				BusinessParty otherParty = ((BusinessParty) selectedParty);
				otherParty.setPartner(true);
				try
				{
					myParty.followParty(otherParty);
					EventQueue.invokeLater(() -> selectNode(partyTree, otherParty));
					clientFrame.appendToConsole(new StringBuilder("Party ").append(otherParty.getPartySimpleName()).
							append(" has been moved from Other Parties to Business Partners.").
							append(" Party is still followed by My Party."), Color.GREEN);
				}
				catch(Exception e)
				{
					clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder("Party ").
							append(otherParty.getPartySimpleName()).append(" could not be removed from the following parties!"));
				}
			}).start();
		});

		removePartnerItem.addActionListener(event ->
		{
			final Object selectedParty = getSelectedUserObject(partyTree);
			if(selectedParty == null) return;
			new Thread( () ->
			{
				BusinessParty otherParty = ((BusinessParty) selectedParty);
				otherParty.setPartner(false);
				try
				{
					myParty.followParty(otherParty);
					EventQueue.invokeLater(() -> selectNode(partyTree, otherParty));
					clientFrame.appendToConsole(new StringBuilder("Party ").append(otherParty.getPartySimpleName()).
							append(" has been moved from Business Partners to Other Parties. ").
							append("Party is still followed by My Party."), Color.GREEN);
				}
				catch(Exception e)
				{
					clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder("Party ").
							append(otherParty.getPartySimpleName()).append(" could not be removed from the following parties!"));
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
					//commented line is a privious variant of a selection
//					selectNextNode(partyTree, selectedParty);
					DefaultMutableTreeNode nodeToSelect = getNextNodeForSelection(partyTree, selectedParty);
					myParty.purgeParty(selectedParty);
					partiesSorter.allRowsChanged();
					EventQueue.invokeLater(() -> selectNode(partyTree, nodeToSelect));
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

		deleteDeregisteredItem.addActionListener(event ->
		{
			final BusinessParty selectedParty = (BusinessParty) getSelectedUserObject(partyTree);
			if(selectedParty == null) return;
			new Thread(() ->
			{
				final String partyName = selectedParty.getPartySimpleName();
				try
				{
//					selectNextNode(partyTree, selectedParty);
					DefaultMutableTreeNode nodeToSelect = getNextNodeForSelection(partyTree, selectedParty);
					myParty.purgeParty(selectedParty);
					partiesSorter.allRowsChanged();
					EventQueue.invokeLater(() -> selectNode(partyTree, nodeToSelect));
					clientFrame.appendToConsole(new StringBuilder("Party ").append(partyName).
							append(" has been deleted from Deregistered Parties."), Color.GREEN);
				}
				catch (DetailException e)
				{
					clientFrame.appendToConsole(new StringBuilder("Party ").append(partyName).
							append(" could not be deleted from Deregistered Parties."), Color.GREEN);
					logger.error(new StringBuilder("Party ").append(partyName).
							append(" could not be deleted from Deregistered Parties.").toString(), e);
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
						if(((BusinessParty) selectedParty).isPartner())
						{
							partyTreePopupMenu.removeAll();
							partyTreePopupMenu.add(unfollowPartyItem);
							partyTreePopupMenu.add(removePartnerItem);
						}
						else if(((BusinessParty) selectedParty).isArchived())
						{
							partyTreePopupMenu.removeAll();
							partyTreePopupMenu.add(followPartnerItem);
							partyTreePopupMenu.add(followPartyItem);
							partyTreePopupMenu.addSeparator();
							partyTreePopupMenu.add(deleteArchivedItem);
						}
						else if(((BusinessParty) selectedParty).isDeregistered())
						{
							partyTreePopupMenu.removeAll();
							partyTreePopupMenu.add(deleteDeregisteredItem);
						}
						else //Other Parties
						{
							partyTreePopupMenu.removeAll();
							partyTreePopupMenu.add(addPartnerItem);
							partyTreePopupMenu.add(unfollowPartyItem);
						}
						partyTreePopupMenu.show(partyTree, event.getX(), event.getY());
					}
				}
			}
		});

		//searches tables
		searchesPartyTableModel = new PartySearchTableModel();
		searchesPartyTable = createSearchPartyTable(searchesPartyTableModel);
		searchesPartyTable.getColumnModel().getColumn(0).setCellRenderer(rowNumberRenderer);
		searchesCatalogueTableModel = new CatalogueSearchTableModel();
		searchesCatalogueTable = createSearchCatalogueTable(searchesCatalogueTableModel);
		searchesCatalogueTable.getColumnModel().getColumn(0).setCellRenderer(rowNumberRenderer);
		searchesTable = createSearchListTable(new SearchListTableModel<>());

		searchesPartySorter = createTableRowSorter(searchesPartyTableModel);
		searchesPartyTable.setRowSorter(searchesPartySorter);
		searchesCatalogueSorter = createTableRowSorter(searchesCatalogueTableModel);
		searchesCatalogueTable.setRowSorter(searchesCatalogueSorter);
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
				searchesPartyTableModel.setSearch((Search<PartyType>) selectedSearch);
				searchesPartySorter.allRowsChanged();
				rightScrollPane.setViewportView(searchesPartyTable);
			}
			else if(selectedSearch instanceof CatalogueSearch)
			{
				searchesCatalogueTableModel.setSearch((Search<CatalogueType>) selectedSearch);
				searchesCatalogueSorter.allRowsChanged();
				rightScrollPane.setViewportView(searchesCatalogueTable);
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
					searchesTable.setModel(searchesTableModel);
					searchesTable.getColumnModel().getColumn(0).setCellRenderer(rowNumberRenderer);
					searchesTable.setRowSorter(createTableRowSorter(searchesTableModel));
					rightScrollPane.setViewportView(searchesTable);
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
					final Future<?> ret = clientFrame.getClient().cdrSearch((Search<?>) selectedSearch, true);
					if(ret != null)
					{
						ret.get();
						EventQueue.invokeLater(() ->
						{
							selectNode(searchTree, selectedSearch);
							searchesPartySorter.allRowsChanged();
							repaint();
						});
					}

					/*					((SearchTreeModel) searchTreeModel).changeNode((Search<?>) selectedSearch);
					selectNode(searchTree, selectedSearch);
					repaint(null, false, false);*/
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
					final DefaultMutableTreeNode nodeToSelect = getNextNodeForSelection(searchTree, selectedSearch);
					myParty.removeSearch(selectedSearch);
					selectNode(searchTree, nodeToSelect);
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
		final JMenuItem addPartnerItem = new JMenuItem("Add to Business Partners");
		final JMenuItem removePartnerItem = new JMenuItem("Remove from Business Partners");
		final JMenuItem deleteArchivedItem = new JMenuItem("Delete from Archived Parties");
		final JMenuItem deleteDeregisteredItem = new JMenuItem("Delete from Deregistered Parties");
		final JMenuItem followPartnerItem = new JMenuItem("Follow as Business Partner");
		final JMenuItem followPartyItem = new JMenuItem("Follow as Party");

		final MyParty myParty = clientFrame.getClient().getMyParty();

		followPartnerItem.addActionListener(event ->
		{
			//			final int rowIndex = table.getSelectedRow()*/;
			final int realRowIndex = table.convertRowIndexToModel(table.getSelectedRow());
			final BusinessParty selectedParty = partiesTableModel.getPartyAtIndex(realRowIndex);
			final Party coreParty = selectedParty.getCoreParty();
			final String followingName = InstanceFactory.
					getPropertyOrNull(coreParty.getPartyNameAtIndex(0), PartyNameType::getNameValue);
			final String followingID = InstanceFactory.getPropertyOrNull(coreParty.getPartyIdentificationAtIndex(0),
					PartyIdentificationType::getIDValue);
			new Thread(()->
			{
				try
				{
					Future<?> ret = clientFrame.getClient().followParty(followingName, followingID, true);
					if(ret != null)
					{
						ret.get();
						partiesTableModel.fireTableDataChanged();
						final BusinessParty followedParty = clientFrame.getClient().getMyParty().getBusinessPartner(followingID);
						EventQueue.invokeLater(() -> makeVisibleNode(partyTree, followedParty));
					}
				}
				catch(Exception e)
				{
					clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder("Party ").
							append(((BusinessParty) selectedParty).getPartySimpleName()).
							append(" could not be removed from the following parties!"));
				}
			}).start();
		});

		followPartyItem.addActionListener(event ->
		{
			//			final int rowIndex = table.getSelectedRow()*/;
			final int realRowIndex = table.convertRowIndexToModel(table.getSelectedRow());
			final BusinessParty selectedParty = partiesTableModel.getPartyAtIndex(realRowIndex);
			final Party coreParty = selectedParty.getCoreParty();
			final String followingName = InstanceFactory.
					getPropertyOrNull(coreParty.getPartyNameAtIndex(0), PartyNameType::getNameValue);
			final String followingID = InstanceFactory.getPropertyOrNull(coreParty.getPartyIdentificationAtIndex(0),
					PartyIdentificationType::getIDValue);
			new Thread(()->
			{
				try
				{
					Future<?> ret = clientFrame.getClient().followParty(followingName, followingID, false);
					if(ret != null)
					{
						ret.get();
						partiesTableModel.fireTableDataChanged();
						final BusinessParty followedParty = clientFrame.getClient().getMyParty().getOtherParty(followingID);
						EventQueue.invokeLater(() -> makeVisibleNode(partyTree, followedParty));
					}
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
			//			final int rowIndex = table.getSelectedRow()*/;
			final int realRowIndex = table.convertRowIndexToModel(table.getSelectedRow());
			final BusinessParty selectedParty = ((PartyListTableModel) table.getModel()).getPartyAtIndex(realRowIndex);
			new Thread(()->
			{
				try
				{
					Future<?> ret = clientFrame.getClient().cdrUnfollowParty(selectedParty);
					if(ret != null)
					{
						ret.get();
						partiesTableModel.fireTableDataChanged();
						EventQueue.invokeLater(() -> makeVisibleNode(partyTree, selectedParty));
					}
				}
				catch(Exception e)
				{
					clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder("Party ").
							append(((BusinessParty) selectedParty).getPartySimpleName()).
							append(" could not be removed from the following parties!"));
				}
			}).start();
		});

		addPartnerItem.addActionListener(event ->
		{
			//			final int rowIndex = table.getSelectedRow()*/;
			final int realRowIndex = table.convertRowIndexToModel(table.getSelectedRow());
			final BusinessParty selectedParty = ((PartyListTableModel) table.getModel()).getPartyAtIndex(realRowIndex);
			new Thread(() ->
			{
				selectedParty.setPartner(true);
				try
				{
					myParty.followParty(selectedParty);
					partiesTableModel.fireTableDataChanged();
					EventQueue.invokeLater(() -> makeVisibleNode(partyTree, selectedParty));
					clientFrame.appendToConsole(new StringBuilder("Party ").append(selectedParty.getPartySimpleName()).
							append(" has been moved from Other Parties to Business Partners.").
							append(" Party is still followed by My Party."), Color.GREEN);
				}
				catch(Exception e)
				{
					clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder("Party ").
							append(selectedParty.getPartySimpleName()).append(" could not be removed from the following parties!"));
				}
			}).start();
		});

		removePartnerItem.addActionListener(event ->
		{
			final int realRowIndex = table.convertRowIndexToModel(table.getSelectedRow());

			final BusinessParty selectedParty = ((PartyListTableModel) table.getModel()).getPartyAtIndex(realRowIndex);
			{
				selectedParty.setPartner(false);
				try
				{
					myParty.followParty(selectedParty);
					partiesTableModel.fireTableDataChanged();
					EventQueue.invokeLater(() -> makeVisibleNode(partyTree, selectedParty));
					clientFrame.appendToConsole(new StringBuilder("Party ").append(selectedParty.getPartySimpleName()).
							append(" has been moved from Business Partners to Other Parties.").
							append(" Party is still followed by My Party."), Color.GREEN);
				}
				catch(Exception e)
				{
					clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder("Party ").
							append(selectedParty.getPartySimpleName()).append(" could not be removed from the following parties!"));
				}
			}
		});

		deleteArchivedItem.addActionListener(event ->
		{
			final int realRowIndex = table.convertRowIndexToModel(table.getSelectedRow());
			final BusinessParty selectedParty = partiesTableModel.getPartyAtIndex(realRowIndex);
			new Thread(() ->
			{
				final String partyName = selectedParty.getPartySimpleName();
				try
				{
					myParty.purgeParty(selectedParty);
					partiesTableModel.fireTableDataChanged();
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

		deleteDeregisteredItem.addActionListener(event ->
		{
			final int realRowIndex = table.convertRowIndexToModel(table.getSelectedRow());
			final BusinessParty selectedParty = partiesTableModel.getPartyAtIndex(realRowIndex);
			new Thread(() ->
			{
				final String partyName = selectedParty.getPartySimpleName();
				try
				{
					myParty.purgeParty(selectedParty);
					partiesTableModel.fireTableDataChanged();
					clientFrame.appendToConsole(new StringBuilder("Party ").append(partyName).
							append(" has been deleted from Deregistered Parties."), Color.GREEN);
				}
				catch (DetailException e)
				{
					clientFrame.appendToConsole(new StringBuilder("Party ").append(partyName).
							append(" could not be deleted from Deregistered Parties."), Color.GREEN);
					logger.error(new StringBuilder("Party ").append(partyName).
							append(" could not be deleted from Deregistered Parties.").toString(), e);
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
					final int rowIndex = table.rowAtPoint(event.getPoint());
					final int realRowIndex = table.convertRowIndexToModel(rowIndex);
					if(SwingUtilities.isRightMouseButton(event))
					{
						table.setRowSelectionInterval(rowIndex, rowIndex);

						final Object selectedParty = getSelectedUserObject(partyTree);
						if(selectedParty == null) return;
						if(selectedParty instanceof String)
						{
							final String nodeTitle = (String) selectedParty;
							if(BUSINESS_PARTNERS.equals(nodeTitle))
							{
								partyTablePopupMenu.removeAll();
								partyTablePopupMenu.add(removePartnerItem);
								partyTablePopupMenu.add(unfollowPartyItem);
							}
							else if(OTHER_PARTIES.equals(nodeTitle))
							{
								partyTablePopupMenu.removeAll();
								partyTablePopupMenu.add(addPartnerItem);
								partyTablePopupMenu.add(unfollowPartyItem);
							}
							else if(ARCHIVED_PARTIES.equals(nodeTitle))
							{
								partyTablePopupMenu.removeAll();
								partyTablePopupMenu.add(followPartnerItem);
								partyTablePopupMenu.add(followPartyItem);
								partyTablePopupMenu.addSeparator();
								partyTablePopupMenu.add(deleteArchivedItem);
							}
							else if(DEREGISTERED_PARTIES.equals(nodeTitle))
							{
								partyTablePopupMenu.removeAll();
								partyTablePopupMenu.add(deleteDeregisteredItem);
							}
							else
								partyTablePopupMenu.removeAll();
							partyTablePopupMenu.show(table, event.getX(), event.getY());
						}
					}
					else if(SwingUtilities.isLeftMouseButton(event) && event.getClickCount() == 2)
					{
						final PartyListTableModel partyListTableModel = (PartyListTableModel) table.getModel();
						final BusinessParty party = partyListTableModel.getPartyAtIndex(realRowIndex);
						partnerCatalogueTableModel.setCatalogue(party.getCatalogue());
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

		//in all listeners table model must be get with table.getModel() in order to have a current instance of it
		againSearchItem.addActionListener(event ->
		{
			final int realRowIndex = table.convertRowIndexToModel(table.getSelectedRow());
			Search<?> selectedSearch = (Search<?>) ((SearchListTableModel<?>) table.getModel()).getSearches().get(realRowIndex);
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
//						((DefaultTableModel) table.getModel()).fireTableDataChanged();
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
			final int realRowIndex = table.convertRowIndexToModel(table.getSelectedRow());
			Search<?> selectedSearch = (Search<?>) ((SearchListTableModel<?>) table.getModel()).getSearches().get(realRowIndex);
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
			final int realRowIndex = table.convertRowIndexToModel(table.getSelectedRow());
			Search<?> selectedSearch = (Search<?>) ((SearchListTableModel<?>) table.getModel()).getSearches().get(realRowIndex);
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
				final int realRowIndex = table.convertRowIndexToModel(rowIndex);
				if(SwingUtilities.isLeftMouseButton(event) && event.getClickCount() == 2)
				{
					final Object selectedSearchListNode = getSelectedUserObject(searchTree);
					if(selectedSearchListNode == null) return;
					if(selectedSearchListNode instanceof String)
					{
						final String nodeTitle = (String) selectedSearchListNode;
						if(PARTIES.equals(nodeTitle))
						{
							final Search<PartyType> selectedSearch = myParty.getPartySearches().get(realRowIndex);
							searchesPartyTableModel.setSearch(selectedSearch);
							selectNode(searchTree, selectedSearch);
							rightScrollPane.setViewportView(searchesPartyTable);
						}
						else if(CATALOGUES.equals(nodeTitle))
						{
							final Search<CatalogueType> selectedSearch = myParty.getCatalogueSearches().get(realRowIndex);
							searchesCatalogueTableModel.setSearch(selectedSearch);
							selectNode(searchTree, selectedSearch);
							rightScrollPane.setViewportView(searchesCatalogueTable);
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
	 * Creates table containing list of parties that are the result of quering the CDR.
	 * @param tableModel model containing party data
	 * @return constructed table object
	 */
	private JTable createSearchPartyTable(DefaultTableModel tableModel)
	{
		final JTable table = newEmptyPartyListTable(tableModel);
		final JPopupMenu popupMenu = new JPopupMenu();
		final JMenuItem followPartnerItem = new JMenuItem("Follow as Business Partner");
		final JMenuItem followPartyItem = new JMenuItem("Follow as Party");
		popupMenu.add(followPartnerItem);
		popupMenu.add(followPartyItem);

		followPartnerItem.addActionListener(event ->
		{
			//			int rowIndex = table.getSelectedRow();
			final int realRowIndex = table.convertRowIndexToModel(table.getSelectedRow());
			final PartyType selectedParty = ((PartySearchTableModel) table.getModel()).getParty(realRowIndex);
			final String followingName = InstanceFactory.getPropertyOrNull(selectedParty.getPartyNameAtIndex(0), PartyNameType::getNameValue);
			final String followingID = InstanceFactory.getPropertyOrNull(selectedParty.getPartyIdentificationAtIndex(0),
					PartyIdentificationType::getIDValue);
			new Thread(()->
			{
				try
				{
					Future<?> ret = clientFrame.getClient().followParty(followingName, followingID, true);
					if(ret != null)
					{
						ret.get();
						final BusinessParty followedParty = clientFrame.getClient().getMyParty().getBusinessPartner(followingID);
						EventQueue.invokeLater(() -> makeVisibleNode(partyTree, followedParty));
					}
				}
				catch(Exception e)
				{
					clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder("Party ").append(followingName).
							append(" could not be added to the following parties as a business partner!"));
				}
			}).start();
		});

		followPartyItem.addActionListener(event ->
		{
			//			int rowIndex = table.getSelectedRow();
			final int realRowIndex = table.convertRowIndexToModel(table.getSelectedRow());
			final PartyType selectedParty = ((PartySearchTableModel) table.getModel()).getParty(realRowIndex);
			final String followingName = InstanceFactory.getPropertyOrNull(selectedParty.getPartyNameAtIndex(0), PartyNameType::getNameValue);
			final String followingID = InstanceFactory.getPropertyOrNull(selectedParty.getPartyIdentificationAtIndex(0),
					PartyIdentificationType::getIDValue);
			new Thread(()->
			{
				try
				{
					Future<?> ret = clientFrame.getClient().followParty(followingName, followingID, false);
					if(ret != null)
					{
						ret.get();
						final BusinessParty followedParty = clientFrame.getClient().getMyParty().getBusinessPartner(followingID);
						EventQueue.invokeLater(() -> makeVisibleNode(partyTree, followedParty));
					}
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
						table.setRowSelectionInterval(rowIndex, rowIndex);
						popupMenu.show(table, event.getX(), event.getY());
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
	private JTable createSearchCatalogueTable(DefaultTableModel tableModel)
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
		final JMenuItem followPartnerItem = new JMenuItem("Follow as Business Partner");
		final JMenuItem followPartyItem = new JMenuItem("Follow as Party");
		popupMenu.add(followPartnerItem);
		popupMenu.add(followPartyItem);

		followPartnerItem.addActionListener(event ->
		{
			final int realRowIndex = table.convertRowIndexToModel(table.getSelectedRow());
			final PartyType selectedParty = searchesCatalogueTableModel.getParty(realRowIndex);
			final String followingName = InstanceFactory.getPropertyOrNull(selectedParty.getPartyNameAtIndex(0),
					PartyNameType::getNameValue);
			final String followingID = InstanceFactory.getPropertyOrNull(selectedParty.getPartyIdentificationAtIndex(0),
					PartyIdentificationType::getIDValue);
			new Thread(() ->
			{
				try
				{
					Future<?> ret = clientFrame.getClient().followParty(followingName, followingID, true);
					if(ret != null)
					{
						ret.get();
						final BusinessParty followedParty = clientFrame.getClient().getMyParty().getBusinessPartner(followingID);
						EventQueue.invokeLater(() -> makeVisibleNode(partyTree, followedParty));
					}
				}
				catch(Exception e)
				{
					clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder("Party ").append(followingName).
							append(" could not be added to the following parties as a business partner!"));
				}
			}).start();
		});

		followPartyItem.addActionListener(event ->
		{
			final int realRowIndex = table.convertRowIndexToModel(table.getSelectedRow());
			final PartyType selectedParty = searchesCatalogueTableModel.getParty(realRowIndex);
			final String followingName = InstanceFactory.getPropertyOrNull(selectedParty.getPartyNameAtIndex(0),
					PartyNameType::getNameValue);
			final String followingID = InstanceFactory.getPropertyOrNull(selectedParty.getPartyIdentificationAtIndex(0),
					PartyIdentificationType::getIDValue);
			new Thread(() ->
			{
				try
				{
					Future<?> ret = clientFrame.getClient().followParty(followingName, followingID, false);
					if(ret != null)
					{
						ret.get();
						final BusinessParty followedParty = clientFrame.getClient().getMyParty().getBusinessPartner(followingID);
						EventQueue.invokeLater(() -> makeVisibleNode(partyTree, followedParty));
					}
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
					table.addRowSelectionInterval(rowIndex, rowIndex);
					popupMenu.show(table, event.getX(), event.getY());
				}
			}
		});

		return table;
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		final Object source = event.getSource();
		final String command = event.getActionCommand();
		if(source.getClass() == BusinessParty.class)
		{
			BusinessParty party = (BusinessParty) source;
			//MMM: there should be more commands; every command responsible for an update of a specific table
			if(RutaClientFrameEvent.CATALOGUE_UPDATED.equals(command))
			{
				EventQueue.invokeLater(() -> {
					makeVisibleNode(partyTree, party);
					final Object selectedUserObject = getSelectedUserObject(partyTree);
					if(selectedUserObject instanceof BusinessParty)
					{
						if(selectedUserObject == party)
							partnerCatalogueTableModel.setCatalogue(party.getCatalogue());
						partnerCatalogueTableModel.fireTableDataChanged();
					}
				});
			}
			else if(RutaClientFrameEvent.PARTY_UPDATED.equals(command))
			{
				EventQueue.invokeLater(() -> {
					makeVisibleNode(partyTree, party);
					final Object selectedUserObject = getSelectedUserObject(partyTree);
					if(!(selectedUserObject instanceof BusinessParty))
						partiesTableModel.fireTableDataChanged();
				});
			}
			else if(RutaClientFrameEvent.PARTY_MOVED.equals(command))
			{
				EventQueue.invokeLater(() ->
				{
					makeVisibleNode(partyTree, party);
/*					final Object selectedUserObject = getSelectedUserObject(partyTree);
					if(!(selectedUserObject instanceof BusinessParty))
						partiesTableModel.fireTableDataChanged();*/
				});
			}
			else if(RutaClientFrameEvent.SELECT_NEXT.equals(command))
			{
				EventQueue.invokeLater(() ->
				{
					selectNextNode(partyTree, party);
				});
			}
		}
	}

	public class UnfollowPartyWorker extends SwingWorker<Void, ConsoleData>
	{
		private BusinessParty party;
		private boolean success;

		public UnfollowPartyWorker(BusinessParty party)
		{
			this.party = party;
			success = false;
		}

		@Override
		protected Void doInBackground() throws Exception
		{
			try
			{
				Future<?> ret = clientFrame.getClient().cdrNonBlockingUnfollowParty(party, this);
				if(ret != null)
				{
					ret.get();
					success = true;
				}
			}
			catch(WebServiceException e)
			{
				publish(new ConsoleData(new StringBuilder("Unfollow request has not been sent to the CDR service!").
						append(" Server is not accessible. Please try again later."), Color.RED));
			}
			catch(Exception e)
			{
				publish(new ConsoleData(clientFrame.processException(e, new StringBuilder("Party ").append(party.getPartySimpleName()).
						append(" could not be removed from the following parties!")), Color.RED));
			}
			return null;
		}

		@Override
		protected void process(List<ConsoleData> chunks)
		{
			for(ConsoleData data : chunks)
				clientFrame.appendToConsole(data.getMsg(), data.getColor());
		}

		@Override
		protected void done()
		{
			if(success)
			{
				EventQueue.invokeLater(() -> selectNode(partyTree, party));
				clientFrame.appendToConsole(new StringBuilder("Party ").append(party.getPartySimpleName()).
						append(" has been moved to archived parties."), Color.GREEN);
			}
		}

		public void publish(ConsoleData data)
		{
			super.publish(data);
		}

	}

}