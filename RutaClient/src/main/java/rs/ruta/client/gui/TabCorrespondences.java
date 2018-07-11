package rs.ruta.client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

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
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import oasis.names.specification.ubl.schema.xsd.applicationresponse_21.ApplicationResponseType;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;
import oasis.names.specification.ubl.schema.xsd.order_21.OrderType;
import oasis.names.specification.ubl.schema.xsd.ordercancellation_21.OrderCancellationType;
import oasis.names.specification.ubl.schema.xsd.orderchange_21.OrderChangeType;
import oasis.names.specification.ubl.schema.xsd.orderresponse_21.OrderResponseType;
import oasis.names.specification.ubl.schema.xsd.orderresponsesimple_21.OrderResponseSimpleType;
import rs.ruta.client.BusinessParty;
import rs.ruta.client.MyParty;
import rs.ruta.client.RutaClient;
import rs.ruta.client.correspondence.BuyerOrderingProcess;
import rs.ruta.client.correspondence.BuyerProcessOrderResponseSimpleState;
import rs.ruta.client.correspondence.BuyerProcessOrderResponseState;
import rs.ruta.client.correspondence.BuyingCorrespondence;
import rs.ruta.client.correspondence.CatalogueCorrespondence;
import rs.ruta.client.correspondence.ClosingProcess;
import rs.ruta.client.correspondence.Correspondence;
import rs.ruta.client.correspondence.CustomerBillingProcess;
import rs.ruta.client.correspondence.CustomerReconcileChargesState;
import rs.ruta.client.correspondence.RutaProcess;
import rs.ruta.client.correspondence.SellerOrderingProcess;
import rs.ruta.client.correspondence.SellerProcessOrderState;
import rs.ruta.client.correspondence.SupplierBillingProcess;
import rs.ruta.client.correspondence.SupplierRaiseInvoiceState;
import rs.ruta.client.correspondence.SupplierValidateResponseState;
import rs.ruta.common.DocumentReference;
import rs.ruta.common.DocumentReference.Status;
import rs.ruta.common.datamapper.DetailException;

/**
 * Class for displaying of {@link Correspondence}s of My Party.
 */
public class TabCorrespondences extends TabComponent
{
	private static final long serialVersionUID = -7541063217643235335L;
	private static final String ARCHIVED_PARTNERS = Messages.getString("TabCorrespondences.0"); //$NON-NLS-1$
	private static final String BUSINESS_PARTNERS = Messages.getString("TabCorrespondences.1"); //$NON-NLS-1$
	private static final String CDR = Messages.getString("TabCorrespondences.2"); //$NON-NLS-1$
	private static final String CORRESPONDECES = Messages.getString("TabCorrespondences.3"); //$NON-NLS-1$
	private static final String MY_PARTY = Messages.getString("TabCorrespondences.4"); //$NON-NLS-1$
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

	private MyParty myParty;

	/**
	 * Creates tabbed pane for display of correspondence related data.
	 * @param clientFrame parent frame
	 */
	public TabCorrespondences(RutaClientFrame clientFrame)
	{
		super(clientFrame);
		final RutaClient client = clientFrame.getClient();
		myParty = client.getMyParty();
		final BusinessParty cdrParty = new BusinessParty();
		cdrParty.setCoreParty(client.getCDRParty());
		final DefaultTreeModel correspondenceTreeModel =
				new CorrespondenceTreeModel(new DefaultMutableTreeNode(CORRESPONDECES), myParty, cdrParty);
		correspondenceTree = new JTree(correspondenceTreeModel);
		correspondenceTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		final CorrespondenceTreeCellRenderer correspondenceTreeCellRenderer = new CorrespondenceTreeCellRenderer();
		correspondenceTree.setCellRenderer(correspondenceTreeCellRenderer);
		selectNode(correspondenceTree, CORRESPONDECES);
		final JPanel treePanel = new JPanel(new BorderLayout());
		treePanel.add(correspondenceTree, BorderLayout.CENTER);

		final JLabel blankLabel = new JLabel();

		leftPane = new JScrollPane(treePanel);
		leftPane.setPreferredSize(new Dimension(325, 500));

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
				final String nodeTitle = (String) selectedObject;
				if(MY_PARTY.equals(nodeTitle))
				{
					final BusinessParty my = myParty.getMyFollowingParty();
					if(my != null)
						partyList.add(my);
				}
				else if(BUSINESS_PARTNERS.equals(nodeTitle))
					partyList = myParty.getBusinessPartners();
 				else if(ARCHIVED_PARTNERS.equals(nodeTitle))
					partyList = myParty.getArchivedParties();

				if(!CORRESPONDECES.equals(nodeTitle))
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
		final JMenuItem newOrderItem = new JMenuItem(Messages.getString("TabCorrespondences.5")); //$NON-NLS-1$

		newOrderItem.addActionListener(event ->
		{
			final Object selectedUserObject = getSelectedUserObject(correspondenceTree);
			if(selectedUserObject == null) return;
			new Thread(() ->
			{
				String correspondentID = null;
				BuyingCorrespondence corr = null;;
				if(selectedUserObject instanceof BusinessParty)
				{
					correspondentID = ((BusinessParty) selectedUserObject).getPartyID();
					corr = BuyingCorrespondence.newInstance(client, (BusinessParty) selectedUserObject, true);
				}
				else if(selectedUserObject instanceof Correspondence)
				{
					final Object parentUserObject =
							((DefaultMutableTreeNode) getSelectedNode(correspondenceTree).getParent()).getUserObject();
					if(parentUserObject != null && parentUserObject instanceof BusinessParty)
					{
						correspondentID = ((BusinessParty) parentUserObject).getPartyID();
						corr = BuyingCorrespondence.newInstance(client, (BusinessParty) parentUserObject, true);
					}
				}
				try
				{
					myParty.addBuyingCorrespondence(corr);
				}
				catch (DetailException e)
				{
					logger.error(Messages.getString("TabCorrespondences.6"), e); //$NON-NLS-1$
				}
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
					final TreePath path = correspondenceTree.getPathForLocation(event.getX(), event.getY());
					final Object selectedUserObject = getSelectedUserObject(path);
					if(selectedUserObject == null) return;
					correspondenceTree.setSelectionPath(path);
					if((selectedUserObject instanceof BusinessParty &&
							((BusinessParty) selectedUserObject).isPartner()) ||
							(selectedUserObject instanceof Correspondence &&
									((BusinessParty) ((DefaultMutableTreeNode) getSelectedNode(correspondenceTree).getParent()).getUserObject()).isPartner()))
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
				final int viewRowIndex = table.rowAtPoint(event.getPoint());
				if(viewRowIndex != -1)
				{
					final int modelRowIndex = table.convertRowIndexToModel(viewRowIndex);
					if(SwingUtilities.isLeftMouseButton(event) && event.getClickCount() == 2)
					{
						//					final Correspondence corr = ((CorrespondenceListTableModel) tableModel).getCorrespondenceAtIndex(modelRowIndex);
						final Correspondence corr = ((CorrespondenceListTableModel) table.getModel()).getCorrespondenceAtIndex(modelRowIndex);
						partnerCorrespondenceTableModel.setCorrespondence(corr);
						((DefaultTableModel) partnerCorrespondenceTableModel).fireTableDataChanged();
						rightScrollPane.setViewportView(partnerCorrespondenceTable);
						selectNode(correspondenceTree, corr);
						repaint();
					}
					else if(SwingUtilities.isRightMouseButton(event))
					{
						table.setRowSelectionInterval(viewRowIndex, viewRowIndex);
					}
				}
			}
		});
		return table;
	}

	/**
	 * @param tableModel
	 * @return
	 */
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

		JPopupMenu correspondencePopupMenu = new JPopupMenu();
		JMenuItem processDocumentItem = new JMenuItem(Messages.getString("TabCorrespondences.7")); //$NON-NLS-1$
		JMenuItem resendDocumentItem = new JMenuItem(Messages.getString("TabCorrespondences.8")); //$NON-NLS-1$
		JMenuItem resendApplicationResponseItem = new JMenuItem(Messages.getString("TabCorrespondences.9")); //$NON-NLS-1$
		JMenuItem resendInvoiceItem = new JMenuItem(Messages.getString("TabCorrespondences.10")); //$NON-NLS-1$
		JMenuItem resendOrderItem = new JMenuItem(Messages.getString("TabCorrespondences.11")); //$NON-NLS-1$
		JMenuItem resendOrderResponseItem = new JMenuItem(Messages.getString("TabCorrespondences.12")); //$NON-NLS-1$
		JMenuItem resendOrderResponseSimpleItem = new JMenuItem(Messages.getString("TabCorrespondences.13")); //$NON-NLS-1$
		JMenuItem resendOrderChangeItem = new JMenuItem(Messages.getString("TabCorrespondences.14")); //$NON-NLS-1$
		JMenuItem resendOrderCancellationItem = new JMenuItem(Messages.getString("TabCorrespondences.15")); //$NON-NLS-1$

		JMenuItem viewApplicationResponseItem = new JMenuItem(Messages.getString("TabCorrespondences.16")); //$NON-NLS-1$
		JMenuItem viewResendApplicationResponseItem = new JMenuItem(Messages.getString("TabCorrespondences.17")); //$NON-NLS-1$
		JMenuItem viewInvoiceItem = new JMenuItem(Messages.getString("TabCorrespondences.18")); //$NON-NLS-1$
		JMenuItem viewResendInvoiceItem = new JMenuItem(Messages.getString("TabCorrespondences.19")); //$NON-NLS-1$
		JMenuItem viewOrderItem = new JMenuItem(Messages.getString("TabCorrespondences.20")); //$NON-NLS-1$
		JMenuItem viewResendOrderItem = new JMenuItem(Messages.getString("TabCorrespondences.21")); //$NON-NLS-1$
		JMenuItem viewOrderChangeItem = new JMenuItem(Messages.getString("TabCorrespondences.22")); //$NON-NLS-1$
		JMenuItem viewResendOrderChangeItem = new JMenuItem(Messages.getString("TabCorrespondences.23")); //$NON-NLS-1$
		JMenuItem viewOrderCancellationItem = new JMenuItem(Messages.getString("TabCorrespondences.24")); //$NON-NLS-1$
		JMenuItem viewResendOrderCancellationItem = new JMenuItem(Messages.getString("TabCorrespondences.25")); //$NON-NLS-1$
		JMenuItem viewOrderResponseItem = new JMenuItem(Messages.getString("TabCorrespondences.26")); //$NON-NLS-1$
		JMenuItem viewResendOrderResponseItem = new JMenuItem(Messages.getString("TabCorrespondences.27")); //$NON-NLS-1$
		JMenuItem viewOrderResponseSimpleItem = new JMenuItem(Messages.getString("TabCorrespondences.28")); //$NON-NLS-1$
		JMenuItem viewResendOrderResponseSimpleItem = new JMenuItem(Messages.getString("TabCorrespondences.29")); //$NON-NLS-1$

		processDocumentItem.addActionListener(event ->
		{
			new Thread(() ->
			{
				try
				{
					final Correspondence corr = ((CorrespondenceTableModel) tableModel).getCorrespondence();
					if(!corr.isAlive())
						corr.start();
					corr.waitThreadBlocked();
					corr.proceed();
				}
				catch(Exception e)
				{
					clientFrame.appendToConsole(new StringBuilder(Messages.getString("TabCorrespondences.30")), Color.RED); //$NON-NLS-1$
				}
			}).start();
		});

		resendDocumentItem.addActionListener(event ->
		{
			new Thread(() ->
			{
				try
				{
					final Correspondence corr = ((CorrespondenceTableModel) tableModel).getCorrespondence();
					if(!corr.isAlive())
						corr.start();
					corr.waitThreadBlocked();
					corr.proceed();
				}
				catch(Exception e)
				{
					clientFrame.appendToConsole(new StringBuilder(Messages.getString("TabCorrespondences.31")), Color.RED); //$NON-NLS-1$
				}
			}).start();
		});

		resendApplicationResponseItem.addActionListener(event ->
		{
			final Object clientProperty = ((JMenuItem) event.getSource()).getClientProperty(Messages.getString("TabCorrespondences.32")); //$NON-NLS-1$
			boolean cdr;
			int option = JOptionPane.YES_OPTION;
			if(clientProperty != null)
			{
				cdr = (boolean) clientProperty;
				option = showConfirmDialog(Messages.getString("TabCorrespondences.33"), cdr); //$NON-NLS-1$
			}
			if(option == JOptionPane.YES_OPTION)
				new Thread(() ->
				{
					final Correspondence corr = ((CorrespondenceTableModel) tableModel).getCorrespondence();
					final RutaProcess process = (RutaProcess) corr.getState();
					final ApplicationResponseType appResponse = corr.getLastDocument(ApplicationResponseType.class);
					if(appResponse != null)
					{
						final DocumentReference documentReference = corr.getDocumentReference(appResponse.getUUIDValue());
						try
						{
							process.getClient().cdrSendDocument(appResponse, documentReference, corr);
						}
						catch(Exception e)
						{
							process.getClient().getClientFrame().
							processExceptionAndAppendToConsole(e, new StringBuilder(Messages.getString("TabCorrespondences.34"))); //$NON-NLS-1$
						}
					}
					else
					{
						process.getClient().getClientFrame().appendToConsole(
								new StringBuilder(Messages.getString("TabCorrespondences.35")), Color.BLACK); //$NON-NLS-1$
					}
				}).start();

		});

		resendInvoiceItem.addActionListener(event ->
		{
			final Object clientProperty = ((JMenuItem) event.getSource()).getClientProperty(Messages.getString("TabCorrespondences.36")); //$NON-NLS-1$
			boolean cdr;
			int option = JOptionPane.YES_OPTION;
			if(clientProperty != null)
			{
				cdr = (boolean) clientProperty;
				option = showConfirmDialog(Messages.getString("TabCorrespondences.37"), cdr); //$NON-NLS-1$
			}
			if(option == JOptionPane.YES_OPTION)
				new Thread(() ->
				{
					final Correspondence corr = ((CorrespondenceTableModel) tableModel).getCorrespondence();
					final RutaProcess process = (RutaProcess) corr.getState();
					final InvoiceType invoice = corr.getLastDocument(InvoiceType.class);
					if(invoice != null)
					{
						final DocumentReference documentReference = corr.getDocumentReference(invoice.getUUIDValue());
						try
						{
							process.getClient().cdrSendDocument(invoice, documentReference, corr);
						}
						catch(Exception e)
						{
							process.getClient().getClientFrame().
							processExceptionAndAppendToConsole(e, new StringBuilder(Messages.getString("TabCorrespondences.38"))); //$NON-NLS-1$
						}
					}
					else
					{
						process.getClient().getClientFrame().appendToConsole(
								new StringBuilder(Messages.getString("TabCorrespondences.39")), Color.BLACK); //$NON-NLS-1$
					}
				}).start();

		});

		resendOrderItem.addActionListener(event ->
		{
			final Object clientProperty = ((JMenuItem) event.getSource()).getClientProperty(Messages.getString("TabCorrespondences.40")); //$NON-NLS-1$
			boolean cdr;
			int option = JOptionPane.YES_OPTION;
			if(clientProperty != null)
			{
				cdr = (boolean) clientProperty;
				option = showConfirmDialog(Messages.getString("TabCorrespondences.41"), cdr); //$NON-NLS-1$
			}
			if(option == JOptionPane.YES_OPTION)
				new Thread(() ->
				{
					final Correspondence corr = ((CorrespondenceTableModel) tableModel).getCorrespondence();
					final RutaProcess process = (RutaProcess) corr.getState();
					final OrderType order = corr.getLastDocument(OrderType.class);
					if(order != null)
					{
						final DocumentReference documentReference = corr.getDocumentReference(order.getUUIDValue());
						try
						{
							process.getClient().cdrSendDocument(order, documentReference, corr);
						}
						catch(Exception e)
						{
							process.getClient().getClientFrame().
							processExceptionAndAppendToConsole(e, new StringBuilder(Messages.getString("TabCorrespondences.42"))); //$NON-NLS-1$
						}
					}
					else
					{
						process.getClient().getClientFrame().appendToConsole(
								new StringBuilder(Messages.getString("TabCorrespondences.43")), Color.BLACK); //$NON-NLS-1$
					}
				}).start();

		});

		resendOrderResponseItem.addActionListener(event ->
		{
			final Object clientProperty = ((JMenuItem) event.getSource()).getClientProperty(Messages.getString("TabCorrespondences.44")); //$NON-NLS-1$
			boolean cdr;
			int option = JOptionPane.YES_OPTION;
			if(clientProperty != null)
			{
				cdr = (boolean) clientProperty;
				option = showConfirmDialog(Messages.getString("TabCorrespondences.45"), cdr); //$NON-NLS-1$
			}
			if(option == JOptionPane.YES_OPTION)
				new Thread(() ->
				{
					final Correspondence corr = ((CorrespondenceTableModel) tableModel).getCorrespondence();
					final RutaProcess process = (RutaProcess) corr.getState();
					final OrderResponseType orderResponse = corr.getLastDocument(OrderResponseType.class);
					if(orderResponse != null)
					{
						final DocumentReference documentReference = corr.getDocumentReference(orderResponse.getUUIDValue());
						try
						{
							process.getClient().cdrSendDocument(orderResponse, documentReference, corr);
						}
						catch(Exception e)
						{
							process.getClient().getClientFrame().
							processExceptionAndAppendToConsole(e, new StringBuilder(Messages.getString("TabCorrespondences.46"))); //$NON-NLS-1$
						}
					}
					else
					{
						process.getClient().getClientFrame().appendToConsole(
								new StringBuilder(Messages.getString("TabCorrespondences.47")), //$NON-NLS-1$
								Color.BLACK);
					}
				}).start();
		});

		resendOrderResponseSimpleItem.addActionListener(event ->
		{
			final Object clientProperty = ((JMenuItem) event.getSource()).getClientProperty(Messages.getString("TabCorrespondences.48")); //$NON-NLS-1$
			boolean cdr;
			int option = JOptionPane.YES_OPTION;
			if(clientProperty != null)
			{
				cdr = (boolean) clientProperty;
				option = showConfirmDialog(Messages.getString("TabCorrespondences.49"), cdr); //$NON-NLS-1$
			}
			if(option == JOptionPane.YES_OPTION)
				new Thread(() ->
				{
					final Correspondence corr = ((CorrespondenceTableModel) tableModel).getCorrespondence();
					final RutaProcess process = (RutaProcess) corr.getState();
					final OrderResponseSimpleType orderResponseSimple = corr.getLastDocument(OrderResponseSimpleType.class);
					if(orderResponseSimple != null)
					{
						final DocumentReference documentReference = corr.getDocumentReference(orderResponseSimple.getUUIDValue());
						try
						{
							process.getClient().cdrSendDocument(orderResponseSimple, documentReference, corr);
						}
						catch(Exception e)
						{
							process.getClient().getClientFrame().
							processExceptionAndAppendToConsole(e, new StringBuilder(Messages.getString("TabCorrespondences.50"))); //$NON-NLS-1$
						}
					}
					else
						process.getClient().getClientFrame().appendToConsole(
								new StringBuilder(Messages.getString("TabCorrespondences.51")), //$NON-NLS-1$
								Color.BLACK);
				}).start();
		});

		resendOrderChangeItem.addActionListener(event ->
		{
			final Object clientProperty = ((JMenuItem) event.getSource()).getClientProperty(Messages.getString("TabCorrespondences.52")); //$NON-NLS-1$
			boolean cdr;
			int option = JOptionPane.YES_OPTION;
			if(clientProperty != null)
			{
				cdr = (boolean) clientProperty;
				option = showConfirmDialog(Messages.getString("TabCorrespondences.53"), cdr); //$NON-NLS-1$
			}
			if(option == JOptionPane.YES_OPTION)
				new Thread(() ->
				{
					final Correspondence corr = ((CorrespondenceTableModel) tableModel).getCorrespondence();
					final RutaProcess process = (RutaProcess) corr.getState();
					final OrderChangeType orderChange = corr.getLastDocument(OrderChangeType.class);
					if(orderChange != null)
					{
						final DocumentReference documentReference = corr.getDocumentReference(orderChange.getUUIDValue());
						try
						{
							process.getClient().cdrSendDocument(orderChange, documentReference, corr);
						}
						catch(Exception e)
						{
							process.getClient().getClientFrame().
							processExceptionAndAppendToConsole(e, new StringBuilder(Messages.getString("TabCorrespondences.54"))); //$NON-NLS-1$
						}
					}
					else
						process.getClient().getClientFrame().appendToConsole(
								new StringBuilder(Messages.getString("TabCorrespondences.55")), //$NON-NLS-1$
								Color.BLACK);
				}).start();
		});

		resendOrderCancellationItem.addActionListener(event ->
		{
			final Object clientProperty = ((JMenuItem) event.getSource()).getClientProperty(Messages.getString("TabCorrespondences.56")); //$NON-NLS-1$
			boolean cdr;
			int option = JOptionPane.YES_OPTION;
			if(clientProperty != null)
			{
				cdr = (boolean) clientProperty;
				option = showConfirmDialog(Messages.getString("TabCorrespondences.57"), cdr); //$NON-NLS-1$
			}
			if(option == JOptionPane.YES_OPTION)
				new Thread(() ->
				{
					final Correspondence corr = ((CorrespondenceTableModel) tableModel).getCorrespondence();
					final RutaProcess process = (RutaProcess) corr.getState();
					final OrderCancellationType orderCancellation = corr.getLastDocument(OrderCancellationType.class);
					if(orderCancellation != null)
					{
						final DocumentReference documentReference = corr.getDocumentReference(orderCancellation.getUUIDValue());
						try
						{
							process.getClient().cdrSendDocument(orderCancellation, documentReference, corr);
						}
						catch(Exception e)
						{
							process.getClient().getClientFrame().
							processExceptionAndAppendToConsole(e, new StringBuilder(Messages.getString("TabCorrespondences.58"))); //$NON-NLS-1$
						}
					}
					else
						process.getClient().getClientFrame().appendToConsole(
								new StringBuilder(Messages.getString("TabCorrespondences.59")), //$NON-NLS-1$
								Color.BLACK);
				}).start();
		});

		viewApplicationResponseItem.addActionListener(event ->
		{
			new Thread(() ->
			{
				final Correspondence corr = ((CorrespondenceTableModel) tableModel).getCorrespondence();
				final int vieRowIndex = table.getSelectedRow();
				final int modelRowIndex = table.convertRowIndexToModel(vieRowIndex);
				final Object document = corr.getDocumentAtIndex(modelRowIndex);

				if(document != null)
					clientFrame.showApplicationResponseDialog(Messages.getString("TabCorrespondences.60"), //$NON-NLS-1$
							(ApplicationResponseType) document, false, null);
				else
					clientFrame.appendToConsole(new StringBuilder(Messages.getString("TabCorrespondences.61")), Color.BLACK); //$NON-NLS-1$
			}).start();
		});

		viewResendApplicationResponseItem.addActionListener(event ->
		{
			new Thread(() ->
			{
				final Correspondence corr = ((CorrespondenceTableModel) tableModel).getCorrespondence();
				final int vieRowIndex = table.getSelectedRow();
				final int modelRowIndex = table.convertRowIndexToModel(vieRowIndex);
				final Object document = corr.getDocumentAtIndex(modelRowIndex);

				if(document != null)
					clientFrame.showApplicationResponseDialog(Messages.getString("TabCorrespondences.62"), //$NON-NLS-1$
							(ApplicationResponseType) document, false, corr);
				else
					clientFrame.appendToConsole(new StringBuilder(Messages.getString("TabCorrespondences.63")), Color.BLACK); //$NON-NLS-1$
			}).start();
		});

		viewInvoiceItem.addActionListener(event ->
		{
			new Thread(() ->
			{
				final Correspondence corr = ((CorrespondenceTableModel) tableModel).getCorrespondence();
				final int vieRowIndex = table.getSelectedRow();
				final int modelRowIndex = table.convertRowIndexToModel(vieRowIndex);
				final Object document = corr.getDocumentAtIndex(modelRowIndex);

				if(document != null)
					clientFrame.showInvoiceDialog(Messages.getString("TabCorrespondences.64"), (InvoiceType) document, false, null); //$NON-NLS-1$
				else
					clientFrame.appendToConsole(new StringBuilder(Messages.getString("TabCorrespondences.65")), Color.BLACK); //$NON-NLS-1$
			}).start();
		});

		viewResendInvoiceItem.addActionListener(event ->
		{
			new Thread(() ->
			{
				final Correspondence corr = ((CorrespondenceTableModel) tableModel).getCorrespondence();
				final int vieRowIndex = table.getSelectedRow();
				final int modelRowIndex = table.convertRowIndexToModel(vieRowIndex);
				final Object document = corr.getDocumentAtIndex(modelRowIndex);

				if(document != null)
					clientFrame.showInvoiceDialog(Messages.getString("TabCorrespondences.66"), (InvoiceType) document, false, corr); //$NON-NLS-1$
				else
					clientFrame.appendToConsole(new StringBuilder(Messages.getString("TabCorrespondences.67")), Color.BLACK); //$NON-NLS-1$
			}).start();
		});

		viewOrderCancellationItem.addActionListener(event ->
		{
			new Thread(() ->
			{
				final Correspondence corr = ((CorrespondenceTableModel) tableModel).getCorrespondence();
				final int vieRowIndex = table.getSelectedRow();
				final int modelRowIndex = table.convertRowIndexToModel(vieRowIndex);
				final Object document = corr.getDocumentAtIndex(modelRowIndex);

				if(document != null)
					clientFrame.showOrderCancellationDialog(Messages.getString("TabCorrespondences.68"), (OrderCancellationType) document, false, null); //$NON-NLS-1$
				else
					clientFrame.appendToConsole(new StringBuilder(Messages.getString("TabCorrespondences.69")), Color.BLACK); //$NON-NLS-1$
			}).start();
		});

		viewResendOrderCancellationItem.addActionListener(event ->
		{
			new Thread(() ->
			{
				final Correspondence corr = ((CorrespondenceTableModel) tableModel).getCorrespondence();
				final int vieRowIndex = table.getSelectedRow();
				final int modelRowIndex = table.convertRowIndexToModel(vieRowIndex);
				final Object document = corr.getDocumentAtIndex(modelRowIndex);

				if(document != null)
					clientFrame.showOrderCancellationDialog(Messages.getString("TabCorrespondences.70"), (OrderCancellationType) document, false, corr); //$NON-NLS-1$
				else
					clientFrame.appendToConsole(new StringBuilder(Messages.getString("TabCorrespondences.71")), Color.BLACK); //$NON-NLS-1$
			}).start();
		});

		viewOrderChangeItem.addActionListener(event ->
		{
			new Thread(() ->
			{
				final Correspondence corr = ((CorrespondenceTableModel) tableModel).getCorrespondence();
				final int vieRowIndex = table.getSelectedRow();
				final int modelRowIndex = table.convertRowIndexToModel(vieRowIndex);
				final Object document = corr.getDocumentAtIndex(modelRowIndex);

				if(document != null)
					clientFrame.showOrderChangeDialog(Messages.getString("TabCorrespondences.72"), (OrderChangeType) document, false, null); //$NON-NLS-1$
				else
					clientFrame.appendToConsole(new StringBuilder(Messages.getString("TabCorrespondences.73")), Color.BLACK); //$NON-NLS-1$
			}).start();
		});

		viewResendOrderChangeItem.addActionListener(event ->
		{
			new Thread(() ->
			{
				final Correspondence corr = ((CorrespondenceTableModel) tableModel).getCorrespondence();
				final int vieRowIndex = table.getSelectedRow();
				final int modelRowIndex = table.convertRowIndexToModel(vieRowIndex);
				final Object document = corr.getDocumentAtIndex(modelRowIndex);

				if(document != null)
					clientFrame.showOrderChangeDialog(Messages.getString("TabCorrespondences.74"), (OrderChangeType) document, false, corr); //$NON-NLS-1$
				else
					clientFrame.appendToConsole(new StringBuilder(Messages.getString("TabCorrespondences.75")), Color.BLACK); //$NON-NLS-1$
			}).start();
		});

		viewOrderItem.addActionListener(event ->
		{
			new Thread(() ->
			{
				final Correspondence corr = ((CorrespondenceTableModel) tableModel).getCorrespondence();
				final int vieRowIndex = table.getSelectedRow();
				final int modelRowIndex = table.convertRowIndexToModel(vieRowIndex);
				final Object document = corr.getDocumentAtIndex(modelRowIndex);

				if(document != null)
					//					clientFrame.showPreviewOrderDialog("View Order", (OrderType) document);
					clientFrame.showOrderDialog(Messages.getString("TabCorrespondences.76"), (OrderType) document, false, null); //$NON-NLS-1$
				else
					clientFrame.appendToConsole(new StringBuilder(Messages.getString("TabCorrespondences.77")), Color.BLACK); //$NON-NLS-1$

			}).start();
		});

		viewResendOrderItem.addActionListener(event ->
		{
			new Thread(() ->
			{
				final Correspondence corr = ((CorrespondenceTableModel) tableModel).getCorrespondence();
				final int vieRowIndex = table.getSelectedRow();
				final int modelRowIndex = table.convertRowIndexToModel(vieRowIndex);
				final Object document = corr.getDocumentAtIndex(modelRowIndex);

				if(document != null)
					clientFrame.showOrderDialog(Messages.getString("TabCorrespondences.78"), (OrderType) document, false, corr); //$NON-NLS-1$
				else
					clientFrame.appendToConsole(new StringBuilder(Messages.getString("TabCorrespondences.79")), Color.BLACK); //$NON-NLS-1$

			}).start();
		});

		viewOrderResponseItem.addActionListener(event ->
		{
			new Thread(() ->
			{
				final Correspondence corr = ((CorrespondenceTableModel) tableModel).getCorrespondence();
				final int vieRowIndex = table.getSelectedRow();
				final int modelRowIndex = table.convertRowIndexToModel(vieRowIndex);
				final Object document = corr.getDocumentAtIndex(modelRowIndex);

				if(document != null)
					clientFrame.showOrderResponseDialog(Messages.getString("TabCorrespondences.80"), (OrderResponseType) document, false, null); //$NON-NLS-1$
				else
					clientFrame.appendToConsole(new StringBuilder(Messages.getString("TabCorrespondences.81")), Color.BLACK); //$NON-NLS-1$
			}).start();
		});

		viewResendOrderResponseItem.addActionListener(event ->
		{
			new Thread(() ->
			{
				final Correspondence corr = ((CorrespondenceTableModel) tableModel).getCorrespondence();
				final int vieRowIndex = table.getSelectedRow();
				final int modelRowIndex = table.convertRowIndexToModel(vieRowIndex);
				final Object document = corr.getDocumentAtIndex(modelRowIndex);

				if(document != null)
					clientFrame.showOrderResponseDialog(Messages.getString("TabCorrespondences.82"), (OrderResponseType) document, false, corr); //$NON-NLS-1$
				else
					clientFrame.appendToConsole(new StringBuilder(Messages.getString("TabCorrespondences.83")), Color.BLACK); //$NON-NLS-1$
			}).start();
		});

		viewOrderResponseSimpleItem.addActionListener(event ->
		{
			new Thread(() ->
			{
				final Correspondence corr = ((CorrespondenceTableModel) tableModel).getCorrespondence();
				final int vieRowIndex = table.getSelectedRow();
				final int modelRowIndex = table.convertRowIndexToModel(vieRowIndex);
				final Object document = corr.getDocumentAtIndex(modelRowIndex);

				if(document != null)
					clientFrame.showOrderResponseSimpleDialog(Messages.getString("TabCorrespondences.84"), //$NON-NLS-1$
							(OrderResponseSimpleType) document, false, true, null);
				else
					clientFrame.appendToConsole(new StringBuilder(Messages.getString("TabCorrespondences.85")), Color.BLACK); //$NON-NLS-1$
			}).start();
		});

		viewResendOrderResponseSimpleItem.addActionListener(event ->
		{
			new Thread(() ->
			{
				final Correspondence corr = ((CorrespondenceTableModel) tableModel).getCorrespondence();
				final int vieRowIndex = table.getSelectedRow();
				final int modelRowIndex = table.convertRowIndexToModel(vieRowIndex);
				final Object document = corr.getDocumentAtIndex(modelRowIndex);

				if(document != null)
					clientFrame.showOrderResponseSimpleDialog(Messages.getString("TabCorrespondences.86"), //$NON-NLS-1$
							(OrderResponseSimpleType) document, false, true, corr);
				else
					clientFrame.appendToConsole(new StringBuilder(Messages.getString("TabCorrespondences.87")), Color.BLACK); //$NON-NLS-1$
			}).start();
		});

		table.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent event)
			{
				final int viewRowIndex = table.rowAtPoint(event.getPoint());
				if(viewRowIndex != -1)
				{
					final int modelRowIndex = table.convertRowIndexToModel(viewRowIndex);
					if(SwingUtilities.isRightMouseButton(event))
					{
						table.setRowSelectionInterval(viewRowIndex, viewRowIndex);
						final Correspondence corr = ((CorrespondenceTableModel) tableModel).getCorrespondence();
						//MMM maybe it should be tested for the right state of the process also before displaying menu item
						final DocumentReference documentReference = corr.getDocumentReferenceAtIndex(modelRowIndex);
						correspondencePopupMenu.removeAll();
						final RutaProcess process = (RutaProcess) corr.getState();
						final Status documentStatus = documentReference.getStatus();
						if(ApplicationResponseType.class.getName().equals(documentReference.getDocumentTypeValue()))
						{
							if(documentReference == corr.getLastDocumentReference() &&
									myParty.getArchivedParty(corr.getCorrespondentID()) == null)
							{
								if(process.getClass() == BuyerOrderingProcess.class  ||
										process.getClass() == CustomerBillingProcess.class ||
										process.getClass() == ClosingProcess.class) // in a case prevoius state was SupplierValidateResponseState
								{
									if(documentStatus == DocumentReference.Status.CDR_RECEIVED)
									{
										correspondencePopupMenu.add(viewApplicationResponseItem);
										resendApplicationResponseItem.putClientProperty("CDR", true); //$NON-NLS-1$
										correspondencePopupMenu.add(resendApplicationResponseItem); //out of state machine
									}
									else if(documentStatus == DocumentReference.Status.CORR_RECEIVED)
									{
										correspondencePopupMenu.add(viewApplicationResponseItem);
										resendApplicationResponseItem.putClientProperty("CDR", false); //$NON-NLS-1$
										correspondencePopupMenu.add(resendApplicationResponseItem); //out of state machine
									}
									else if(documentStatus == DocumentReference.Status.CORR_FAILED)
									{
										correspondencePopupMenu.add(viewApplicationResponseItem);
										correspondencePopupMenu.add(resendApplicationResponseItem); //out of state machine
									}
									else if(documentStatus == DocumentReference.Status.UBL_INVALID ||
											documentStatus == DocumentReference.Status.UBL_VALID ||
											documentStatus == DocumentReference.Status.CLIENT_SENT)
									{
										correspondencePopupMenu.add(viewApplicationResponseItem);
									}
									else
									{
										correspondencePopupMenu.add(viewResendApplicationResponseItem); //in state machine
										correspondencePopupMenu.add(resendDocumentItem); //in state machine
									}
								}
								else if(process.getClass() == SupplierBillingProcess.class &&
										(process.getState().getClass() == SupplierRaiseInvoiceState.class ||
												process.getState().getClass() == SupplierValidateResponseState.class))
								{
									correspondencePopupMenu.add(viewApplicationResponseItem);
									correspondencePopupMenu.add(processDocumentItem);
								}
								else
								{
									correspondencePopupMenu.add(viewApplicationResponseItem);
								}
							}
							else
							{
								correspondencePopupMenu.add(viewApplicationResponseItem);
							}
						}
						else if(InvoiceType.class.getName().equals(documentReference.getDocumentTypeValue()))
						{
							if(documentReference == corr.getLastDocumentReference() &&
									myParty.getArchivedParty(corr.getCorrespondentID()) == null)
							{
								if(process.getClass() == SupplierBillingProcess.class)
								{
									if(documentStatus == DocumentReference.Status.CDR_RECEIVED)
									{
										correspondencePopupMenu.add(viewInvoiceItem);
										resendInvoiceItem.putClientProperty("CDR", true); //$NON-NLS-1$
										correspondencePopupMenu.add(resendInvoiceItem); //out of state machine
									}
									else if(documentStatus == DocumentReference.Status.CORR_RECEIVED)
									{
										correspondencePopupMenu.add(viewInvoiceItem);
										resendInvoiceItem.putClientProperty("CDR", false); //$NON-NLS-1$
										correspondencePopupMenu.add(resendInvoiceItem); //out of state machine
									}
									else if(documentStatus == DocumentReference.Status.CORR_FAILED)
									{
										correspondencePopupMenu.add(viewInvoiceItem);
										correspondencePopupMenu.add(resendInvoiceItem); //out of state machine
									}
									else if(documentStatus == DocumentReference.Status.UBL_INVALID ||
											documentStatus == DocumentReference.Status.UBL_VALID ||
											documentStatus == DocumentReference.Status.CLIENT_SENT)
									{
										correspondencePopupMenu.add(viewInvoiceItem);
									}
									else
									{
										correspondencePopupMenu.add(viewResendInvoiceItem); //in state machine
										correspondencePopupMenu.add(resendDocumentItem); //in state machine
									}
								}
								else if(process.getClass() == CustomerBillingProcess.class &&
										process.getState().getClass() == CustomerReconcileChargesState.class)
								{
									if(corr.getLastDocument() != null) // after the document has been inserted in the database
									{
										correspondencePopupMenu.add(processDocumentItem);
									}
									correspondencePopupMenu.add(viewInvoiceItem);
								}
								else
								{
									correspondencePopupMenu.add(viewInvoiceItem);
								}
							}
							else
							{
								correspondencePopupMenu.add(viewInvoiceItem);
							}
						}
						else if(OrderType.class.getName().equals(documentReference.getDocumentTypeValue()))
						{
							if(documentReference == corr.getLastDocumentReference() &&
									myParty.getArchivedParty(corr.getCorrespondentID()) == null)
							{
								if(process.getClass() == BuyerOrderingProcess.class)
								{
									if(documentStatus == DocumentReference.Status.CDR_RECEIVED)
									{
										correspondencePopupMenu.add(viewOrderItem);
										resendOrderItem.putClientProperty("CDR", true); //$NON-NLS-1$
										correspondencePopupMenu.add(resendOrderItem); //out of state machine
									}
									else if(documentStatus == DocumentReference.Status.CORR_RECEIVED)
									{
										correspondencePopupMenu.add(viewOrderItem);
										resendOrderItem.putClientProperty("CDR", false); //$NON-NLS-1$
										correspondencePopupMenu.add(resendOrderItem); //out of state machine
									}
									else if(documentStatus == DocumentReference.Status.CORR_FAILED)
									{
										correspondencePopupMenu.add(viewOrderItem);
										correspondencePopupMenu.add(resendOrderItem); //out of state machine
									}
									else if(documentStatus == DocumentReference.Status.UBL_INVALID ||
											documentStatus == DocumentReference.Status.UBL_VALID ||
											documentStatus == DocumentReference.Status.CLIENT_SENT)
									{
										correspondencePopupMenu.add(viewOrderItem);
									}
									else
									{
										correspondencePopupMenu.add(viewResendOrderItem); //in state machine
										correspondencePopupMenu.add(resendDocumentItem); //in state machine
									}
								}
								else if(process instanceof SellerOrderingProcess &&
										process.getState() instanceof SellerProcessOrderState)
								{
									if(corr.getLastDocument() != null) // after the document has been inserted in the database
										correspondencePopupMenu.add(processDocumentItem);
									correspondencePopupMenu.add(viewOrderItem);
								}
								else
								{
									correspondencePopupMenu.add(viewOrderItem);
								}
							}
							else
							{
								correspondencePopupMenu.add(viewOrderItem);
							}
						}
						else if(OrderResponseType.class.getName().equals(documentReference.getDocumentTypeValue()))
						{
							if(documentReference == corr.getLastDocumentReference() &&
									myParty.getArchivedParty(corr.getCorrespondentID()) == null)
							{
								if(process.getClass() == SellerOrderingProcess.class)
								{
									if(documentStatus == DocumentReference.Status.CDR_RECEIVED)
									{
										correspondencePopupMenu.add(viewOrderResponseItem);
										resendOrderResponseItem.putClientProperty("CDR", true); //$NON-NLS-1$
										correspondencePopupMenu.add(resendOrderResponseItem); //out of state machine
									}
									else if(documentStatus == DocumentReference.Status.CORR_RECEIVED)
									{
										correspondencePopupMenu.add(viewOrderResponseItem);
										resendOrderResponseItem.putClientProperty("CDR", false); //$NON-NLS-1$
										correspondencePopupMenu.add(resendOrderResponseItem); //out of state machine
									}
									else if(documentStatus == DocumentReference.Status.CORR_FAILED)
									{
										correspondencePopupMenu.add(viewOrderResponseItem);
										correspondencePopupMenu.add(resendOrderResponseItem); //out of state machine
									}
									else if(documentStatus == DocumentReference.Status.UBL_INVALID ||
											documentStatus == DocumentReference.Status.UBL_VALID ||
											documentStatus == DocumentReference.Status.CLIENT_SENT)
									{
										correspondencePopupMenu.add(viewOrderResponseItem);
									}
									else
									{
										correspondencePopupMenu.add(viewResendOrderResponseItem); //in state machine
										correspondencePopupMenu.add(resendDocumentItem); //in state machine
									}
								}
								else if(process.getClass() == BuyerOrderingProcess.class &&
										process.getState().getClass() == BuyerProcessOrderResponseState.class)
								{
									if(corr.getLastDocument() != null) // after the document has been inserted in the database
										correspondencePopupMenu.add(processDocumentItem);
									correspondencePopupMenu.add(viewOrderResponseItem);
								}
								else
								{
									correspondencePopupMenu.add(viewOrderResponseItem);
								}
							}
							else
							{
								correspondencePopupMenu.add(viewOrderResponseItem);
							}
						}
						else if(OrderResponseSimpleType.class.getName().equals(documentReference.getDocumentTypeValue()))
						{
							if(documentReference == corr.getLastDocumentReference() &&
									myParty.getArchivedParty(corr.getCorrespondentID()) == null)
							{
								if(process.getClass() == SellerOrderingProcess.class)
								{
									if(documentStatus == DocumentReference.Status.CDR_RECEIVED)
									{
										correspondencePopupMenu.add(viewOrderResponseSimpleItem);
										resendOrderResponseSimpleItem.putClientProperty("CDR", true); //$NON-NLS-1$
										correspondencePopupMenu.add(resendOrderResponseSimpleItem); //out of state machine
									}
									else if(documentStatus == DocumentReference.Status.CDR_RECEIVED)
									{
										correspondencePopupMenu.add(viewOrderResponseSimpleItem);
										resendOrderResponseSimpleItem.putClientProperty("CDR", false); //$NON-NLS-1$
										correspondencePopupMenu.add(resendOrderResponseSimpleItem); //out of state machine
									}
									else if(documentStatus == DocumentReference.Status.CORR_FAILED)
									{
										correspondencePopupMenu.add(viewOrderResponseSimpleItem);
										correspondencePopupMenu.add(resendOrderResponseSimpleItem); //out of state machine
									}
									else if(documentStatus == DocumentReference.Status.UBL_INVALID ||
											documentStatus == DocumentReference.Status.UBL_VALID ||
											documentStatus == DocumentReference.Status.CLIENT_SENT)
									{
										correspondencePopupMenu.add(viewOrderResponseSimpleItem);
									}
									else
									{
										correspondencePopupMenu.add(viewResendOrderResponseSimpleItem); //in state machine
										correspondencePopupMenu.add(resendDocumentItem); //in state machine
									}
								}
								else if(process.getClass() == BuyerOrderingProcess.class &&
										process.getState().getClass() == BuyerProcessOrderResponseSimpleState.class)
								{
									if(corr.getLastDocument() != null)// && // after the document has been inserted in the database
									{
											//((BuyerOrderingProcess) process).getOrderResponseSimple(corr).isAcceptedIndicatorValue(false)) //MMM does this work???
										correspondencePopupMenu.add(processDocumentItem);
									}
									correspondencePopupMenu.add(viewOrderResponseSimpleItem);
								}
								else if(process.getClass() == SupplierBillingProcess.class)
								{
									if(documentStatus == DocumentReference.Status.CDR_RECEIVED)
									{
										correspondencePopupMenu.add(viewOrderResponseSimpleItem);
										resendOrderResponseSimpleItem.putClientProperty("CDR", true); //$NON-NLS-1$
										correspondencePopupMenu.add(resendOrderResponseSimpleItem); //out of state machine
									}
									else if(documentStatus == DocumentReference.Status.CORR_RECEIVED)
									{
										correspondencePopupMenu.add(viewOrderResponseSimpleItem);
										resendOrderResponseSimpleItem.putClientProperty("CDR", false); //$NON-NLS-1$
										correspondencePopupMenu.add(resendOrderResponseSimpleItem); //out of state machine
									}
									else if(documentStatus == DocumentReference.Status.CORR_FAILED)
									{
										correspondencePopupMenu.add(viewOrderResponseSimpleItem);
										correspondencePopupMenu.add(resendOrderResponseSimpleItem); //out of state machine
									}
									else if(documentStatus == DocumentReference.Status.UBL_INVALID ||
											documentStatus == DocumentReference.Status.UBL_VALID ||
											documentStatus == DocumentReference.Status.CLIENT_SENT)
									{
										correspondencePopupMenu.add(viewOrderResponseSimpleItem);
									}
									else
									{
										correspondencePopupMenu.add(viewResendOrderResponseSimpleItem); //in state machine
										correspondencePopupMenu.add(resendDocumentItem); //in state machine
									}
								}
								else
								{
									correspondencePopupMenu.add(viewOrderResponseSimpleItem);
								}
							}
							else
							{
								correspondencePopupMenu.add(viewOrderResponseSimpleItem);
							}
						}
						else if(OrderChangeType.class.getName().equals(documentReference.getDocumentTypeValue()))
						{
							if(documentReference == corr.getLastDocumentReference() &&
									myParty.getArchivedParty(corr.getCorrespondentID()) == null)
							{
								if(process.getClass() == BuyerOrderingProcess.class)
								{
									if(documentStatus == DocumentReference.Status.CDR_RECEIVED)
									{
										correspondencePopupMenu.add(viewOrderChangeItem);
										resendOrderChangeItem.putClientProperty("CDR", true); //$NON-NLS-1$
										correspondencePopupMenu.add(resendOrderChangeItem); //out of state machine
									}
									else if(documentStatus == DocumentReference.Status.CORR_RECEIVED)
									{
										correspondencePopupMenu.add(viewOrderChangeItem);
										resendOrderChangeItem.putClientProperty("CDR", false); //$NON-NLS-1$
										correspondencePopupMenu.add(resendOrderChangeItem); //out of state machine
									}
									else if(documentStatus == DocumentReference.Status.CORR_FAILED)
									{
										correspondencePopupMenu.add(viewOrderChangeItem);
										correspondencePopupMenu.add(resendOrderChangeItem); //out of state machine
									}
									else if(documentStatus == DocumentReference.Status.UBL_INVALID ||
											documentStatus == DocumentReference.Status.UBL_VALID ||
											documentStatus == DocumentReference.Status.CLIENT_SENT)
									{
										correspondencePopupMenu.add(viewOrderChangeItem);
									}
									else
									{
										correspondencePopupMenu.add(viewResendOrderChangeItem); //in state machine
										correspondencePopupMenu.add(resendDocumentItem); //in state machine
									}
								}
								else if(process.getClass() == SellerOrderingProcess.class &&
										process.getState() instanceof SellerProcessOrderState)
								{
									if(corr.getLastDocument() != null) // after the document has been inserted in the database
										correspondencePopupMenu.add(processDocumentItem);
									correspondencePopupMenu.add(viewOrderChangeItem);
								}
								else
								{
									correspondencePopupMenu.add(viewOrderChangeItem);
								}
							}
							else
							{
								correspondencePopupMenu.add(viewOrderItem);
							}
						}
						else if(OrderCancellationType.class.getName().equals(documentReference.getDocumentTypeValue()))
						{
							if(documentReference == corr.getLastDocumentReference() &&
									myParty.getArchivedParty(corr.getCorrespondentID()) == null)
							{
								if(process.getClass() == BuyerOrderingProcess.class)
								{
									if(documentStatus == DocumentReference.Status.CDR_RECEIVED)
									{
										correspondencePopupMenu.add(viewOrderCancellationItem);
										resendOrderCancellationItem.putClientProperty("CDR", true); //$NON-NLS-1$
										correspondencePopupMenu.add(resendOrderCancellationItem); //out of state machine
									}
									else if(documentStatus == DocumentReference.Status.CORR_RECEIVED)
									{
										correspondencePopupMenu.add(viewOrderCancellationItem);
										resendOrderCancellationItem.putClientProperty("CDR", false); //$NON-NLS-1$
										correspondencePopupMenu.add(resendOrderCancellationItem); //out of state machine
									}
									else if(documentStatus == DocumentReference.Status.CORR_FAILED)
									{
										correspondencePopupMenu.add(viewOrderCancellationItem);
										correspondencePopupMenu.add(resendOrderCancellationItem); //out of state machine
									}
									else if(documentStatus == DocumentReference.Status.UBL_INVALID ||
											documentStatus == DocumentReference.Status.UBL_VALID ||
											documentStatus == DocumentReference.Status.CLIENT_SENT)
									{
										correspondencePopupMenu.add(viewOrderCancellationItem);
									}
									else
									{
										correspondencePopupMenu.add(viewResendOrderCancellationItem); //in state machine
										correspondencePopupMenu.add(resendDocumentItem); //in state machine
									}
								}
								else
								{
									correspondencePopupMenu.add(viewOrderCancellationItem);
								}
							}
							else
							{
								correspondencePopupMenu.add(viewOrderItem);
							}
						}
						correspondencePopupMenu.show(table, event.getX(), event.getY());
					}
				}
			}
		});

		return table;
	}

	/**
	 * Shows the dialog requesting confirmation for resending the document to the CDR.
	 * @param documentName name of the document e.g. "Order"
	 * @param cdr true when CDR has been previously successfully received the document, false
	 * when correspondent party has been previously successfully received the document
	 * @return integer representing chosen option
	 */
	private int showConfirmDialog(String documentName, boolean cdr)
	{
		return JOptionPane.showConfirmDialog(myParty.getClient().getClientFrame(),
				documentName + Messages.getString("TabCorrespondences.104") + (cdr ? CDR : Messages.getString("TabCorrespondences.105"))  + //$NON-NLS-1$ //$NON-NLS-2$
						Messages.getString("TabCorrespondences.106"), //$NON-NLS-1$
						Messages.getString("TabCorrespondences.107"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE); //$NON-NLS-1$
	}

	/**
	 * Creates table showing list of Business Partners.
	 * @param tableModel model containing party data to display
	 * @return constructed table object
	 */
	private JTable createPartyListTable(DefaultTableModel tableModel)
	{
		final JTable table = newEmptyPartyListTable(tableModel);
		table.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent event)
			{
				final int viewRowIndex = table.rowAtPoint(event.getPoint());
				if(viewRowIndex != -1)
				{
					final int modelRowIndex = table.convertRowIndexToModel(viewRowIndex);
					if(SwingUtilities.isLeftMouseButton(event) && event.getClickCount() == 2)
					{
						//					final BusinessParty party = ((PartyListTableModel) tableModel).getPartyAtIndex(modelRowIndex);
						final BusinessParty party = ((PartyListTableModel) table.getModel()).getPartyAtIndex(modelRowIndex);
						final String partyID = party.getPartyID();
						partnerCorrespondenceListTableModel.setCorrespondences(myParty.findAllCorrespondences(partyID));
						((DefaultTableModel) partnerCorrespondenceListTableModel).fireTableDataChanged();
						rightScrollPane.setViewportView(partnerCorrespondenceListTable);
						selectNode(correspondenceTree, party);
						repaint();
					}
					else if(SwingUtilities.isRightMouseButton(event))
					{
						table.setRowSelectionInterval(viewRowIndex, viewRowIndex);
					}
				}
			}
		});
		return table;
	}

	@Override
	protected void doDispatchEvent(ActionEvent event)
	{
		Object source = event.getSource();
		String command = event.getActionCommand();
		if(source instanceof Correspondence)
		{
			Correspondence corr = (Correspondence) source;
			if(CorrespondenceEvent.CORRESPONDENCE_ADDED.equals(command))
			{
				makeVisibleNode(correspondenceTree, corr);
				final Object selectedUserObject = getSelectedUserObject(correspondenceTree);
				if(selectedUserObject instanceof BusinessParty)
				{
					final String correspondentID = corr.getCorrespondentID();
					if(((BusinessParty) selectedUserObject).getPartyID().equals(correspondentID))
					{
						List<Correspondence> correspondences = new ArrayList<>();
						if(corr.getCorrespondentPartyName().equals(CDR))
							correspondences.add(corr);
						else
							correspondences = myParty.findAllCorrespondences(correspondentID);
						partnerCorrespondenceListTableModel.setCorrespondences(correspondences);
						partnerCorrespondenceListTableModel.fireTableDataChanged();
					}
				}
				else if(selectedUserObject instanceof Correspondence)
				{
					if(corr == selectedUserObject)
						partnerCorrespondenceTableModel.fireTableDataChanged();
				}
			}
			else if(CorrespondenceEvent.CORRESPONDENCE_UPDATED.equals(command))
			{
				makeVisibleNode(correspondenceTree, corr);
				final Object selectedUserObject = getSelectedUserObject(correspondenceTree);
				if(selectedUserObject instanceof Correspondence)
					if(corr == selectedUserObject)
					{
						final int selectedRow = partnerCorrespondenceTable.getSelectedRow();
						partnerCorrespondenceTableModel.fireTableDataChanged();
						if(selectedRow != -1)
							partnerCorrespondenceTable.setRowSelectionInterval(selectedRow, selectedRow);
					}
			}
			else if(CorrespondenceEvent.CORRESPONDENCE_REMOVED.equals(command))
			{
				final Object selectedUserObject = getSelectedUserObject(correspondenceTree);
				if(selectedUserObject instanceof BusinessParty)
				{
					final String correspondentID = corr.getCorrespondentID();
					if(((BusinessParty) selectedUserObject).getPartyID().equals(correspondentID))
					{
						if(!corr.getCorrespondentPartyName().equals(CDR))
						{
							List<Correspondence> correspondences = myParty.findAllCorrespondences(correspondentID);
							partnerCorrespondenceListTableModel.setCorrespondences(correspondences);
							partnerCorrespondenceListTableModel.fireTableDataChanged();
						}
					}
				}
			}
		}
		else if(source.getClass() == BusinessParty.class)
		{
			BusinessParty party = (BusinessParty) source;
			if(BusinessPartyEvent.BUSINESS_PARTNER_ADDED.equals(command))
			{
				makeVisibleNode(correspondenceTree, party);
				final Object selectedUserObject = getSelectedUserObject(correspondenceTree);
				if(selectedUserObject instanceof String)
					partiesTableModel.fireTableDataChanged();
			}
			else if(BusinessPartyEvent.BUSINESS_PARTNER_TRANSFERED.equals(command))
			{
				makeVisibleNode(correspondenceTree, party);
				final Object selectedUserObject = getSelectedUserObject(correspondenceTree);
				if(selectedUserObject instanceof String)
					partiesTableModel.fireTableDataChanged();
			}
			else if(BusinessPartyEvent.BUSINESS_PARTNER_REMOVED.equals(command))
			{
				makeVisibleNode(correspondenceTree, party);
				final Object selectedUserObject = getSelectedUserObject(correspondenceTree);
				if(selectedUserObject instanceof String)
					partiesTableModel.fireTableDataChanged();
			}
			else if(BusinessPartyEvent.ARCHIVED_PARTY_ADDED.equals(command))
			{
				makeVisibleNode(correspondenceTree, party);
				final Object selectedUserObject = getSelectedUserObject(correspondenceTree);
				if(selectedUserObject instanceof String)
					partiesTableModel.fireTableDataChanged();
			}
			else if(BusinessPartyEvent.ARCHIVED_PARTY_REMOVED.equals(command) ||
					BusinessPartyEvent.ARCHIVED_PARTY_TRANSFERED.equals(command))
			{
				makeVisibleNode(correspondenceTree, party);
				final Object selectedUserObject = getSelectedUserObject(correspondenceTree);
				if(selectedUserObject instanceof String)
					partiesTableModel.fireTableDataChanged();
			}
		}
		else if(source.getClass() == ArrayList.class)
		{
			if(CorrespondenceEvent.ALL_CORRESPONDENCES_REMOVED.equals(command))
			{
				selectNode(correspondenceTree, CORRESPONDECES);
			}
		}
	}
}