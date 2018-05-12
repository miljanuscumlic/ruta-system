package rs.ruta.client.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultRowSorter;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import rs.ruta.client.BusinessParty;
import rs.ruta.client.CorrespondenceEvent;
import rs.ruta.client.MyParty;
import rs.ruta.client.RutaClient;
import rs.ruta.client.RutaClientFrameEvent;
import rs.ruta.client.correspondence.BuyingCorrespondence;
import rs.ruta.client.correspondence.CatalogueCorrespondence;
import rs.ruta.client.correspondence.Correspondence;

/**
 * Class for displaying of {@link Correspondence}s of My Party.
 */
public class TabCorrespondences extends TabComponent
{
	private static final long serialVersionUID = -7541063217643235335L;
	private static final String ARCHIVED = "Archived";
	private static final String BUSINESS_PARTNERS = "Business Partners";
	private static final String CDR = "CDR";
	private static final String CORRESPONDECES = "Correspondences";
	private static final String MY_PARTY = "My Party";
	private final JTree correspondenceTree;

	private CorrespondenceListTableModel partnerCorrespondenceListTableModel;
	private CorrespondenceTableModel partnerCorrespondenceTableModel;
	private PartyListTableModel partiesTableModel;
	private JTable partnerCorrespondenceListTable;
	private JTable partnerCorrespondenceTable;
	private JTable partiesTable;
	private final TableRowSorter<DefaultTableModel> partnerCorrespondenceListSorter;
	private final TableRowSorter<DefaultTableModel> partnerCorrespondenceSorter;
	private final TableRowSorter<DefaultTableModel> partiesSorter;

	/**
	 * Creates tabbed pane for display of correspondence related data.
	 * @param clientFrame parent frame
	 */
	public TabCorrespondences(RutaClientFrame clientFrame)
	{
		super(clientFrame);
		final RutaClient client = clientFrame.getClient();
		final MyParty myParty = client.getMyParty();
		final BusinessParty cdrParty = new BusinessParty();
		cdrParty.setCoreParty(client.getCDRParty());
		final DefaultTreeModel correspondenceTreeModel =
				new CorrespondenceTreeModel(new DefaultMutableTreeNode("Correspondences"), myParty, cdrParty);
		correspondenceTree = new JTree(correspondenceTreeModel);
		final CorrespondenceTreeCellRenderer correspondenceTreeCellRenderer = new CorrespondenceTreeCellRenderer();
		correspondenceTree.setCellRenderer(correspondenceTreeCellRenderer);
		final JPanel treePanel = new JPanel(new BorderLayout());
		treePanel.add(correspondenceTree, BorderLayout.CENTER);

		final JLabel blankLabel = new JLabel();

		leftPane = new JScrollPane(treePanel);
		leftPane.setPreferredSize(new Dimension(320, 500));

		rightPane = new JPanel(new BorderLayout());
		rightScrollPane = new JScrollPane();
		rightPane.add(rightScrollPane);

		final TabComponent.RowNumberRenderer rowNumberRenderer = new TabComponent.RowNumberRenderer();
		partnerCorrespondenceListTableModel = new CorrespondenceListTableModel();
		partnerCorrespondenceListTable = createCorrespondenceListTable(partnerCorrespondenceListTableModel);
		partnerCorrespondenceListTable.getColumnModel().getColumn(0).setCellRenderer(rowNumberRenderer);
		partnerCorrespondenceTableModel = new CorrespondenceTableModel();
		partnerCorrespondenceTable = createCorrespondenceTable(partnerCorrespondenceTableModel);
		partnerCorrespondenceTable.getColumnModel().getColumn(0).setCellRenderer(rowNumberRenderer);
		partiesTableModel = new PartyListTableModel();
		partiesTable = createPartyListTable(partiesTableModel);
		partiesTable.getColumnModel().getColumn(0).setCellRenderer(rowNumberRenderer);

		partnerCorrespondenceListSorter = createTableRowSorter(partnerCorrespondenceListTableModel, 4, true);
		partnerCorrespondenceListTable.setRowSorter(partnerCorrespondenceListSorter);
		partnerCorrespondenceSorter = createTableRowSorter(partnerCorrespondenceTableModel, 4, true);
		partnerCorrespondenceTable.setRowSorter(partnerCorrespondenceSorter);
		partiesSorter = createTableRowSorter(partiesTableModel);
		partiesTable.setRowSorter(partiesSorter);

		//setting action listener for tab repaint on selection of the business party node
		correspondenceTree.addTreeSelectionListener(event ->
		{
			final Object selectedObject = getSelectedUserObject(correspondenceTree);
			if(selectedObject == null) return;
			if(selectedObject instanceof BusinessParty)
			{
				List<Correspondence> correspondences = null;
				if(((BusinessParty) selectedObject).getPartySimpleName().equals(CDR))
				{
					final CatalogueCorrespondence catalogueCorrespondence = myParty.getCatalogueCorrespondence();
					if(catalogueCorrespondence != null)
					{
						correspondences = new ArrayList<>();
						correspondences.add(catalogueCorrespondence);
					}
				}
				else
				{
					final String partyID = ((BusinessParty) selectedObject).getPartyID();
					correspondences = myParty.findAllCorrespondences(partyID);
				}
//				if(correspondences != null)
				partnerCorrespondenceListTableModel.setCorrespondences(correspondences);
				partnerCorrespondenceListTableModel.fireTableDataChanged();
				rightScrollPane.setViewportView(partnerCorrespondenceListTable);
			}
			else if(selectedObject instanceof Correspondence)
			{
				((Correspondence) selectedObject).setRecentlyUpdated(false);
				final Correspondence corr = ((Correspondence) selectedObject);
				partnerCorrespondenceTableModel.setCorrespondence(corr);
				((DefaultTableModel) partnerCorrespondenceTableModel).fireTableDataChanged();
				rightScrollPane.setViewportView(partnerCorrespondenceTable);
			}
			else if(selectedObject instanceof String)
			{
				List<BusinessParty> partyList = new ArrayList<>();
				final String secondLevelObject = (String) selectedObject;
				if(MY_PARTY.equals(secondLevelObject))
				{
					final BusinessParty my = myParty.getMyFollowingParty();
					if(my != null)
						partyList.add(my);
				}
				else if(BUSINESS_PARTNERS.equals(secondLevelObject))
					partyList = myParty.getBusinessPartners();

/*				MMM TODO
 				else if(ARCHIVED.equals((String) selectedObject))
					partyList = myParty.getArchivedParties();*/

				if(!CORRESPONDECES.equals(secondLevelObject))
				{
					partiesTableModel.setParties(partyList);
					partiesTableModel.fireTableDataChanged();
					rightScrollPane.setViewportView(partiesTable);
				}
				else
					rightScrollPane.setViewportView(blankLabel);
			}
			repaint();
		});

		final JPopupMenu partyTreePopupMenu = new JPopupMenu();
		final JMenuItem newOrderItem = new JMenuItem("New Order");

		newOrderItem.addActionListener(event ->
		{
			final BusinessParty selectedParty = (BusinessParty) getSelectedUserObject(correspondenceTree);
			if(selectedParty == null) return;
			new Thread(() ->
			{
				final String correspondentID = selectedParty.getPartyID();
				final PartyType correspondentParty = selectedParty.getCoreParty();
				final BuyingCorrespondence corr = BuyingCorrespondence.newInstance(client, correspondentParty, correspondentID, true);
				myParty.addBuyingCorrespondence(corr);
				partnerCorrespondenceListTableModel.setCorrespondences(myParty.findAllCorrespondences(correspondentID));
				corr.start();
			}).start();
		});

		correspondenceTree.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent event)
			{
				if(SwingUtilities.isRightMouseButton(event))
				{
					TreePath path = correspondenceTree.getPathForLocation(event.getX(), event.getY());
					Object selectedParty = getSelectedUserObject(path);
					if(selectedParty == null) return;
					correspondenceTree.setSelectionPath(path);
					if(selectedParty instanceof BusinessParty)
					{
						partyTreePopupMenu.removeAll();
						partyTreePopupMenu.add(newOrderItem);
						partyTreePopupMenu.show(correspondenceTree, event.getX(), event.getY());
					}
				}
			}
		});

		arrangeTab();
	}

	@SuppressWarnings("unchecked")
	private JTable createCorrespondenceListTable(DefaultTableModel tableModel)
	{
		JTable table = new JTable(tableModel);
		/*		{
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
							return "Integer numbers; 0 for field deletion";
						case 4:
							return "ID field is mandatory and must be unique";
						case 9:
							return "Comma separated values";
						default:
							return null;
						}
					}
				};
			}
		};*/
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setFillsViewportHeight(true);
		table.getColumnModel().getColumn(0).setCellRenderer(new RowNumberRenderer());

		TableColumnModel colModel = table.getColumnModel();
		colModel.getColumn(0).setPreferredWidth(25);
		colModel.getColumn(1).setPreferredWidth(200);
		colModel.getColumn(2).setPreferredWidth(150);
		colModel.getColumn(3).setPreferredWidth(150);
		colModel.getColumn(4).setPreferredWidth(150);
		colModel.getColumn(5).setPreferredWidth(150);

		table.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent event)
			{
				final int rowIndex = table.rowAtPoint(event.getPoint());
				if(rowIndex != -1)
				{
					final int realRowIndex = table.convertRowIndexToModel(rowIndex);
					if(SwingUtilities.isLeftMouseButton(event) && event.getClickCount() == 2)
					{
						//					final Correspondence corr = ((CorrespondenceListTableModel) tableModel).getCorrespondenceAtIndex(realRowIndex);
						final Correspondence corr = ((CorrespondenceListTableModel) table.getModel()).getCorrespondenceAtIndex(realRowIndex);
						partnerCorrespondenceTableModel.setCorrespondence(corr);
						((DefaultTableModel) partnerCorrespondenceTableModel).fireTableDataChanged();
						rightScrollPane.setViewportView(partnerCorrespondenceTable);
						selectNode(correspondenceTree, corr);
						repaint();
					}
				}
			}
		});

		return table;
	}

	@SuppressWarnings("unchecked")
	private JTable createCorrespondenceTable(DefaultTableModel tableModel)
	{
		JTable table = new JTable(tableModel);
		/*		{
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
							return "Integer numbers; 0 for field deletion";
						case 4:
							return "ID field is mandatory and must be unique";
						case 9:
							return "Comma separated values";
						default:
							return null;
						}
					}
				};
			}
		};*/
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setFillsViewportHeight(true);
		table.getColumnModel().getColumn(0).setCellRenderer(new RowNumberRenderer());

		TableColumnModel colModel = table.getColumnModel();
		colModel.getColumn(0).setPreferredWidth(20);
		colModel.getColumn(1).setPreferredWidth(200);
		colModel.getColumn(2).setPreferredWidth(200);
		colModel.getColumn(3).setPreferredWidth(200);
		colModel.getColumn(4).setPreferredWidth(150);
		colModel.getColumn(5).setPreferredWidth(150);
		return table;
	}

	/**
	 * Creates table showing list of Business Partners.
	 * @param tableModel model containing party data to display
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
			/*			final int realRowIndex = table.convertRowIndexToModel(table.getSelectedRow());
			final BusinessParty selectedParty = partiesTableModel.getParty(realRowIndex);
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
						EventQueue.invokeLater(() -> makeVisibleNode(correspondenceTree, followedParty));
					}
				}
				catch(Exception e)
				{
					clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder("Party ").
							append(((BusinessParty) selectedParty).getPartySimpleName()).
							append(" could not be removed from the following parties!"));
				}
			}).start();*/
		});

		followPartyItem.addActionListener(event ->
		{
			/*			final int realRowIndex = table.convertRowIndexToModel(table.getSelectedRow());
			final BusinessParty selectedParty = partiesTableModel.getParty(realRowIndex);
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
						EventQueue.invokeLater(() -> makeVisibleNode(correspondenceTree, followedParty));
					}
				}
				catch(Exception e)
				{
					clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder("Party ").
							append(((BusinessParty) selectedParty).getPartySimpleName()).
							append(" could not be removed from the following parties!"));
				}
			}).start();*/
		});

		unfollowPartyItem.addActionListener(event ->
		{
			/*			final int realRowIndex = table.convertRowIndexToModel(table.getSelectedRow());
			final BusinessParty selectedParty = ((PartyListTableModel) table.getModel()).getParty(realRowIndex);
			new Thread(()->
			{
				try
				{
					Future<?> ret = clientFrame.getClient().cdrUnfollowParty(selectedParty);
					if(ret != null)
					{
						ret.get();
						partiesTableModel.fireTableDataChanged();
						EventQueue.invokeLater(() -> makeVisibleNode(correspondenceTree, selectedParty));
					}
				}
				catch(Exception e)
				{
					clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder("Party ").
							append(((BusinessParty) selectedParty).getPartySimpleName()).
							append(" could not be removed from the following parties!"));
				}
			}).start();*/
		});

		addPartnerItem.addActionListener(event ->
		{
			/*			final int realRowIndex = table.convertRowIndexToModel(table.getSelectedRow());
			final BusinessParty selectedParty = ((PartyListTableModel) table.getModel()).getParty(realRowIndex);
			new Thread(() ->
			{
				selectedParty.setPartner(true);
				try
				{
					myParty.followParty(selectedParty);
					partiesTableModel.fireTableDataChanged();
					EventQueue.invokeLater(() -> makeVisibleNode(correspondenceTree, selectedParty));
					clientFrame.appendToConsole(new StringBuilder("Party ").append(selectedParty.getPartySimpleName()).
							append(" has been moved from Other Parties to Business Partners.").
							append(" Party is still followed by My Party."), Color.GREEN);
				}
				catch(Exception e)
				{
					clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder("Party ").
							append(selectedParty.getPartySimpleName()).append(" could not be removed from the following parties!"));
				}
			}).start();*/
		});

		removePartnerItem.addActionListener(event ->
		{
			/*			final int realRowIndex = table.convertRowIndexToModel(table.getSelectedRow());

			final BusinessParty selectedParty = ((PartyListTableModel) table.getModel()).getParty(realRowIndex);
			{
				selectedParty.setPartner(false);
				try
				{
					myParty.followParty(selectedParty);
					partiesTableModel.fireTableDataChanged();
					EventQueue.invokeLater(() -> makeVisibleNode(correspondenceTree, selectedParty));
					clientFrame.appendToConsole(new StringBuilder("Party ").append(selectedParty.getPartySimpleName()).
							append(" has been moved from Business Partners to Other Parties.").
							append(" Party is still followed by My Party."), Color.GREEN);
				}
				catch(Exception e)
				{
					clientFrame.processExceptionAndAppendToConsole(e, new StringBuilder("Party ").
							append(selectedParty.getPartySimpleName()).append(" could not be removed from the following parties!"));
				}
			}*/
		});
		//MMM change this
		table.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent event)
			{
				final int rowIndex = table.rowAtPoint(event.getPoint());
				if(rowIndex != -1)
				{
					final int realRowIndex = table.convertRowIndexToModel(rowIndex);
					if(SwingUtilities.isRightMouseButton(event))
					{
						table.setRowSelectionInterval(rowIndex, rowIndex);

						final Object selectedParty = getSelectedUserObject(correspondenceTree);
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
							else
								partyTablePopupMenu.removeAll();
							partyTablePopupMenu.show(table, event.getX(), event.getY());
						}
					}
					else if(SwingUtilities.isLeftMouseButton(event) && event.getClickCount() == 2)
					{
						//					final BusinessParty party = ((PartyListTableModel) tableModel).getPartyAtIndex(realRowIndex);
						final BusinessParty party = ((PartyListTableModel) table.getModel()).getPartyAtIndex(realRowIndex);
						final String partyID = party.getPartyID();
						partnerCorrespondenceListTableModel.setCorrespondences(myParty.findAllCorrespondences(partyID));
						((DefaultTableModel) partnerCorrespondenceListTableModel).fireTableDataChanged();
						rightScrollPane.setViewportView(partnerCorrespondenceListTable);
						selectNode(correspondenceTree, party);
						repaint();
					}
				}
			}
		});
		return table;
	}

	@Override
	public void dispatchEvent(ActionEvent event)
	{
		Object source = event.getSource();
		String command = event.getActionCommand();
		if(source instanceof Correspondence)
		{
			Correspondence corr = (Correspondence) source;
			if(CorrespondenceEvent.CORRESPONDENCE_ADDED.equals(command))
			{
				EventQueue.invokeLater(() ->
				{
					makeVisibleNode(correspondenceTree, corr);
					final Object selectedUserObject = getSelectedUserObject(correspondenceTree);
					if(selectedUserObject instanceof BusinessParty)
					{
						if(((BusinessParty) selectedUserObject).getPartyID().equals(corr.getCorrespondentID()))
						{
							if(corr.getCorrespondentPartyName().equals(CDR))
							{
								List<Correspondence> correspondeces = new ArrayList<>();
								correspondeces.add(corr);
								partnerCorrespondenceListTableModel.setCorrespondences(correspondeces);
							}
							partnerCorrespondenceListTableModel.fireTableDataChanged();
						}
					}
					else if(selectedUserObject instanceof Correspondence)
					{
						if(corr == selectedUserObject)
							partnerCorrespondenceTableModel.fireTableDataChanged();
					}
					repaint();
				});
			}
			else if(CorrespondenceEvent.CORRESPONDENCE_UPDATED.equals(command))
			{
				EventQueue.invokeLater(() ->
				{
					makeVisibleNode(correspondenceTree, corr);
					final Object selectedUserObject = getSelectedUserObject(correspondenceTree);
					if(selectedUserObject instanceof Correspondence)
						if(corr == selectedUserObject)
							partnerCorrespondenceTableModel.fireTableDataChanged();
					repaint();
				});
			}
		}
	}
}