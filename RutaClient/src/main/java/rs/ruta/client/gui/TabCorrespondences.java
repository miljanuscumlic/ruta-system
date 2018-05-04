package rs.ruta.client.gui;

import java.awt.BorderLayout;
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
import javax.swing.table.TableRowSorter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import rs.ruta.client.BusinessParty;
import rs.ruta.client.MyParty;
import rs.ruta.client.RutaClient;
import rs.ruta.client.correspondence.BuyingCorrespondence;

/**
 * Class for displaying of {@link Correspondence}s of My Party.
 */
public class TabCorrespondences extends TabComponent
{
	private static final long serialVersionUID = -7541063217643235335L;
	private static final String BUSINESS_PARTNERS = "Business Partners";
	private static final String MY_PARTY ="My Party";
	private static final String CORRESPONDECES = "Correspondences";
	private final JTree partyTree;

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
		final DefaultTreeModel partyTreeModel = new CorrespondenceTreeModel(new DefaultMutableTreeNode("Correspondences"), myParty);
		partyTree = new JTree(partyTreeModel);
		final PartyTreeCellRenderer partyTreeCellRenderer = new PartyTreeCellRenderer();
		partyTree.setCellRenderer(partyTreeCellRenderer);
		final JPanel treePanel = new JPanel(new BorderLayout());
		treePanel.add(partyTree, BorderLayout.CENTER);

		final JLabel blankLabel = new JLabel();

		leftPane = new JScrollPane(treePanel);
		leftPane.setPreferredSize(new Dimension(250, 500));

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
		partyTree.addTreeSelectionListener(event ->
		{
			final Object selectedParty = getSelectedUserObject(partyTree);
			if(selectedParty == null) return;
			if(selectedParty instanceof BusinessParty)
			{
				((BusinessParty) selectedParty).setRecentlyUpdated(false);

				final String partyID = ((BusinessParty) selectedParty).getPartyID();
				partnerCorrespondenceListTableModel.setCorrespondences(myParty.findAllCorrespondences(partyID));
				partnerCorrespondenceListTableModel.fireTableDataChanged();
				rightScrollPane.setViewportView(partnerCorrespondenceListTable);
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


				if(!CORRESPONDECES.equals((String) selectedParty))
				{
					partiesTableModel.setParties(partyList);
					partiesTableModel.fireTableDataChanged();
//					partiesSorter.allRowsChanged();
					rightScrollPane.setViewportView(partiesTable);
				}
				else
				{
					rightScrollPane.setViewportView(blankLabel);
				}
			}
			repaint();
		});

		final JPopupMenu partyTreePopupMenu = new JPopupMenu();
		final JMenuItem newOrderItem = new JMenuItem("New Order");

		newOrderItem.addActionListener(event ->
		{
			final BusinessParty selectedParty = (BusinessParty) getSelectedUserObject(partyTree);
			if(selectedParty == null) return;
			new Thread(() ->
			{
				final String correspondentID = selectedParty.getPartyID();
				final BuyingCorrespondence corr = BuyingCorrespondence.newInstance(client, correspondentID, true);
				myParty.addBuyingCorrespondence(corr);
				partnerCorrespondenceListTableModel.setCorrespondences(myParty.findAllCorrespondences(correspondentID));
				EventQueue.invokeLater(() -> { partnerCorrespondenceListTableModel.fireTableDataChanged(); });
//				corr.executeOrderingProcess();
				corr.start();
			}).start();
		});

		partyTree.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent event)
			{
				if(SwingUtilities.isRightMouseButton(event))
				{
					TreePath path = partyTree.getPathForLocation(event.getX(), event.getY());
					Object selectedParty = getSelectedUserObject(path);
					if(selectedParty == null) return;
					partyTree.setSelectionPath(path);
					if(!(selectedParty instanceof String) && selectedParty != clientFrame.getClient().getMyParty().getMyFollowingParty())
					{
						partyTreePopupMenu.removeAll();
						partyTreePopupMenu.add(newOrderItem);
						partyTreePopupMenu.show(partyTree, event.getX(), event.getY());
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
/*		table.setFillsViewportHeight(true);
		table.setAutoCreateRowSorter(true);

		table.getRowSorter();
		table.getRowSorter().getModelRowCount();
		((DefaultRowSorter<DefaultTableModel, Integer>) table.getRowSorter()).setSortable(0, false);*/

		table.getColumnModel().getColumn(0).setCellRenderer(new RowNumberRenderer());

		TableColumnModel colModel = table.getColumnModel();
		colModel.getColumn(0).setPreferredWidth(25);
		colModel.getColumn(1).setPreferredWidth(200);
		colModel.getColumn(2).setPreferredWidth(200);
		colModel.getColumn(3).setPreferredWidth(150);
		colModel.getColumn(4).setPreferredWidth(150);
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
/*		table.setFillsViewportHeight(true);
		table.setAutoCreateRowSorter(true);

		table.getRowSorter();
		table.getRowSorter().getModelRowCount();
		((DefaultRowSorter<DefaultTableModel, Integer>) table.getRowSorter()).setSortable(0, false);*/

		table.getColumnModel().getColumn(0).setCellRenderer(new RowNumberRenderer());

		TableColumnModel colModel = table.getColumnModel();
		colModel.getColumn(0).setPreferredWidth(20);
		colModel.getColumn(1).setPreferredWidth(200);
		colModel.getColumn(2).setPreferredWidth(200);
		colModel.getColumn(3).setPreferredWidth(200);
		colModel.getColumn(4).setPreferredWidth(200);

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
						EventQueue.invokeLater(() -> makeVisibleNode(partyTree, followedParty));
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
						EventQueue.invokeLater(() -> makeVisibleNode(partyTree, followedParty));
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
						EventQueue.invokeLater(() -> makeVisibleNode(partyTree, selectedParty));
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
			}*/
		});
		//MMM change this
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
							else
								partyTablePopupMenu.removeAll();
							partyTablePopupMenu.show(table, event.getX(), event.getY());
						}
					}
					else if(SwingUtilities.isLeftMouseButton(event) && event.getClickCount() == 2)
					{
						final PartyListTableModel partyListTableModel = (PartyListTableModel) table.getModel();
						final BusinessParty party = partyListTableModel.getParty(realRowIndex);
						final String partyID = party.getPartyID();
						partnerCorrespondenceListTableModel.setCorrespondences(myParty.findAllCorrespondences(partyID));
						rightScrollPane.setViewportView(partnerCorrespondenceListTable);
						selectNode(partyTree, party);
						repaint();
					}
				});
			}
		});
		return table;
	}
}
