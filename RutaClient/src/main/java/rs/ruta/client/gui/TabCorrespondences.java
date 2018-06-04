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
import javax.swing.tree.TreeSelectionModel;

import oasis.names.specification.ubl.schema.xsd.applicationresponse_21.ApplicationResponseType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;
import oasis.names.specification.ubl.schema.xsd.order_21.OrderType;
import oasis.names.specification.ubl.schema.xsd.ordercancellation_21.OrderCancellationType;
import oasis.names.specification.ubl.schema.xsd.orderchange_21.OrderChangeType;
import oasis.names.specification.ubl.schema.xsd.orderresponse_21.OrderResponseType;
import oasis.names.specification.ubl.schema.xsd.orderresponsesimple_21.OrderResponseSimpleType;
import rs.ruta.client.BusinessParty;
import rs.ruta.client.CorrespondenceEvent;
import rs.ruta.client.MyParty;
import rs.ruta.client.RutaClient;
import rs.ruta.client.RutaClientFrameEvent;
import rs.ruta.client.correspondence.BuyerOrderingProcess;
import rs.ruta.client.correspondence.BuyingCorrespondence;
import rs.ruta.client.correspondence.CatalogueCorrespondence;
import rs.ruta.client.correspondence.Correspondence;
import rs.ruta.client.correspondence.CustomerBillingProcess;
import rs.ruta.client.correspondence.DocumentReference;
import rs.ruta.client.correspondence.RutaProcess;
import rs.ruta.client.correspondence.RutaProcessState;
import rs.ruta.client.correspondence.SellerOrderingProcess;
import rs.ruta.client.correspondence.SellerProcessOrderState;
import rs.ruta.client.correspondence.SupplierBillingProcess;
import rs.ruta.common.datamapper.DetailException;

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

	private MyParty myParty;

	/**
	 * Creates tabbed pane for display of correspondence related data.
	 * @param clientFrame parent frame
	 */
	public TabCorrespondences(RutaClientFrame clientFrame)
	{
		super(clientFrame);
		final RutaClient client = clientFrame.getClient();
		/*final MyParty */myParty = client.getMyParty();
		final BusinessParty cdrParty = new BusinessParty();
		cdrParty.setCoreParty(client.getCDRParty());
		final DefaultTreeModel correspondenceTreeModel =
		new CorrespondenceTreeModel(new DefaultMutableTreeNode("Correspondences"), myParty, cdrParty);
		correspondenceTree = new JTree(correspondenceTreeModel);
		correspondenceTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		final CorrespondenceTreeCellRenderer correspondenceTreeCellRenderer = new CorrespondenceTreeCellRenderer();
		correspondenceTree.setCellRenderer(correspondenceTreeCellRenderer);
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
				final String secondLevelObject = (String) selectedObject;
				if(MY_PARTY.equals(secondLevelObject))
				{
					final BusinessParty my = myParty.getMyFollowingParty();
					if(my != null)
						partyList.add(my);
				}
				else if(BUSINESS_PARTNERS.equals(secondLevelObject))
					partyList = myParty.getBusinessPartners();

/*				//MMM TODO
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
//				final PartyType correspondentParty = selectedParty.getCoreParty();
				final BuyingCorrespondence corr = BuyingCorrespondence.newInstance(client, selectedParty, true);
				try
				{
					myParty.addBuyingCorrespondence(corr);
				}
				catch (DetailException e)
				{
					logger.error("Correspondence could not be inserted in the database", e);
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

		JPopupMenu correspondencePopupMenu = new JPopupMenu();
		JMenuItem processDocumentItem = new JMenuItem("Process");
		JMenuItem resendDocumentItem = new JMenuItem("Resend");
		JMenuItem viewApplicationResponseItem = new JMenuItem("View");
		JMenuItem viewResendApplicationResponseItem = new JMenuItem("View and Resend");
		JMenuItem viewInvoiceItem = new JMenuItem("View");
		JMenuItem viewResendInvoiceItem = new JMenuItem("View and Resend");
		JMenuItem viewOrderItem = new JMenuItem("View");
		JMenuItem viewResendOrderItem = new JMenuItem("View and Resend");
		JMenuItem viewOrderChangeItem = new JMenuItem("View");
		JMenuItem viewResendOrderChangeItem = new JMenuItem("View and Resend");
		JMenuItem viewOrderCancellationItem = new JMenuItem("View");
		JMenuItem viewResendOrderCancellationItem = new JMenuItem("View and Resend");
		JMenuItem viewOrderResponseItem = new JMenuItem("View");
		JMenuItem viewResendOrderResponseItem = new JMenuItem("View and Resend");
		JMenuItem viewOrderResponseSimpleItem = new JMenuItem("View");
		JMenuItem viewResendOrderResponseSimpleItem = new JMenuItem("View and Resend");

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
					clientFrame.appendToConsole(new StringBuilder("Correspondence has been interrupted!"), Color.RED);
				}
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
					clientFrame.showApplicationResponseDialog("View Application Response",
							(ApplicationResponseType) document, false, null);
				else
					clientFrame.appendToConsole(new StringBuilder("Application Response does not exist!"), Color.BLACK);
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
					clientFrame.showApplicationResponseDialog("View Application Response",
							(ApplicationResponseType) document, false, corr);
				else
					clientFrame.appendToConsole(new StringBuilder("Application Response does not exist!"), Color.BLACK);
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
					clientFrame.showInvoiceDialog("View Invoice", (InvoiceType) document, false, null);
				else
					clientFrame.appendToConsole(new StringBuilder("Invoice does not exist!"), Color.BLACK);
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
					clientFrame.showInvoiceDialog("View Invoice", (InvoiceType) document, false, corr);
				else
					clientFrame.appendToConsole(new StringBuilder("Invoice does not exist!"), Color.BLACK);
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
					clientFrame.showOrderCancellationDialog("View Order Cancellation", (OrderCancellationType) document, false, null);
				else
					clientFrame.appendToConsole(new StringBuilder("Order Cancellation does not exist!"), Color.BLACK);
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
					clientFrame.showOrderCancellationDialog("View Order Cancellation", (OrderCancellationType) document, false, corr);
				else
					clientFrame.appendToConsole(new StringBuilder("Order Cancellation does not exist!"), Color.BLACK);
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
					clientFrame.showOrderChangeDialog("View Order Change", (OrderChangeType) document, false, null);
				else
					clientFrame.appendToConsole(new StringBuilder("Order Change does not exist!"), Color.BLACK);
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
					clientFrame.showOrderChangeDialog("View Order Change", (OrderChangeType) document, false, corr);
				else
					clientFrame.appendToConsole(new StringBuilder("Order Change does not exist!"), Color.BLACK);
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
					clientFrame.showOrderDialog("View Order", (OrderType) document, false, null);
				else
					clientFrame.appendToConsole(new StringBuilder("Order does not exist!"), Color.BLACK);

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
					clientFrame.showOrderDialog("View and Resend Order", (OrderType) document, false, corr);
				else
					clientFrame.appendToConsole(new StringBuilder("Order does not exist!"), Color.BLACK);

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
					clientFrame.showOrderResponseDialog("View Order Response", (OrderResponseType) document, false, null);
				else
					clientFrame.appendToConsole(new StringBuilder("Order Response does not exist!"), Color.BLACK);
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
					clientFrame.showOrderResponseDialog("View and Resend Order Response", (OrderResponseType) document, false, corr);
				else
					clientFrame.appendToConsole(new StringBuilder("Order Response does not exist!"), Color.BLACK);
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
					clientFrame.showOrderResponseSimpleDialog("View Order Response Simple",
							(OrderResponseSimpleType) document, false, true, null);
				else
					clientFrame.appendToConsole(new StringBuilder("Order Response Simple does not exist!"), Color.BLACK);
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
					clientFrame.showOrderResponseSimpleDialog("View and Resend Order Response Simple",
							(OrderResponseSimpleType) document, false, true, corr);
				else
					clientFrame.appendToConsole(new StringBuilder("Order Response Simple does not exist!"), Color.BLACK);
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
					clientFrame.appendToConsole(new StringBuilder("Correspondence has been interrupted!"), Color.RED);
				}
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
						//MMM maybe it should be tested for the rigth state of the process also before displaying menu item
						final DocumentReference documentReference = corr.getDocumentReferenceAtIndex(modelRowIndex);
						correspondencePopupMenu.removeAll();
						final RutaProcess process = (RutaProcess) corr.getState();
						if(ApplicationResponseType.class.getName().equals(documentReference.getDocumentTypeValue()))
						{
							if(documentReference == corr.getLastDocumentReference() &&
									process.getClass() == BuyerOrderingProcess.class &&
									!documentReference.getStatus().equals(DocumentReference.Status.CDR_RECEIVED)) //sending failed earlier
							{
								correspondencePopupMenu.add(viewResendApplicationResponseItem);
								correspondencePopupMenu.add(resendDocumentItem);
							}
							else
								correspondencePopupMenu.add(viewApplicationResponseItem);

							if(documentReference == corr.getLastDocumentReference() &&
									process.getClass() == SupplierBillingProcess.class)
							{
								correspondencePopupMenu.add(processDocumentItem);
							}
						}
						else if(InvoiceType.class.getName().equals(documentReference.getDocumentTypeValue()))
						{
							if(documentReference == corr.getLastDocumentReference() &&
									process.getClass() == SupplierBillingProcess.class &&
									!documentReference.getStatus().equals(DocumentReference.Status.CDR_RECEIVED)) //sending failed earlier
							{
								correspondencePopupMenu.add(viewResendInvoiceItem);
								correspondencePopupMenu.add(resendDocumentItem);
							}
							else if(documentReference == corr.getLastDocumentReference() &&
									process.getClass() == CustomerBillingProcess.class)
							{
								correspondencePopupMenu.add(processDocumentItem);
							}
							else
								correspondencePopupMenu.add(viewInvoiceItem);
						}
						else if(OrderType.class.getName().equals(documentReference.getDocumentTypeValue()))
						{
							if(documentReference == corr.getLastDocumentReference() &&
									process.getClass() == BuyerOrderingProcess.class &&
									!documentReference.getStatus().equals(DocumentReference.Status.CDR_RECEIVED)) //sending failed earlier
							{
								correspondencePopupMenu.add(viewResendOrderItem);
								correspondencePopupMenu.add(resendDocumentItem);
							}
							else if(documentReference == corr.getLastDocumentReference() &&
									process instanceof SellerOrderingProcess &&
									process.getState() instanceof SellerProcessOrderState &&
									corr.getLastDocument() != null) // after the Order has been inserted in the database
							{
								correspondencePopupMenu.add(viewOrderItem);
								correspondencePopupMenu.add(processDocumentItem);
							}
							else
								correspondencePopupMenu.add(viewOrderItem);
						}
						else if(OrderResponseType.class.getName().equals(documentReference.getDocumentTypeValue()))
						{
							if(documentReference == corr.getLastDocumentReference() &&
									process.getClass() == SellerOrderingProcess.class &&
									!documentReference.getStatus().equals(DocumentReference.Status.CDR_RECEIVED)) //sending failed earlier
							{
								correspondencePopupMenu.add(viewResendOrderResponseItem);
								correspondencePopupMenu.add(resendDocumentItem);
							}
							else if(documentReference == corr.getLastDocumentReference() &&
									process.getClass() == BuyerOrderingProcess.class &&
									corr.getLastDocument() != null)
							{
								correspondencePopupMenu.add(viewOrderResponseItem);
								correspondencePopupMenu.add(processDocumentItem);
							}
							else
								correspondencePopupMenu.add(viewOrderResponseItem);
						}
						else if(OrderResponseSimpleType.class.getName().equals(documentReference.getDocumentTypeValue()))
						{
							if(documentReference == corr.getLastDocumentReference() &&
									process.getClass() == SellerOrderingProcess.class &&
									!documentReference.getStatus().equals(DocumentReference.Status.CDR_RECEIVED)) //sending failed earlier
							{
								correspondencePopupMenu.add(viewResendOrderResponseSimpleItem);
								correspondencePopupMenu.add(resendDocumentItem);
							}
							else if(documentReference == corr.getLastDocumentReference() &&
									process.getClass() == BuyerOrderingProcess.class &&
									corr.getLastDocument() != null &&
									((BuyerOrderingProcess) process).getOrderResponseSimple(corr).isAcceptedIndicatorValue(false)) //MMM does this work???
							{
								correspondencePopupMenu.add(viewOrderResponseSimpleItem);
								correspondencePopupMenu.add(processDocumentItem);
							}
							else
								correspondencePopupMenu.add(viewOrderResponseSimpleItem);
						}
						else if(OrderChangeType.class.getName().equals(documentReference.getDocumentTypeValue()))
						{
							if(documentReference == corr.getLastDocumentReference() &&
									process.getClass() == BuyerOrderingProcess.class &&
									!documentReference.getStatus().equals(DocumentReference.Status.CDR_RECEIVED)) //sending failed earlier
							{
								correspondencePopupMenu.add(viewResendOrderChangeItem);
								correspondencePopupMenu.add(resendDocumentItem);
							}
							else if(documentReference == corr.getLastDocumentReference() &&
									process.getClass() == SellerOrderingProcess.class &&
									corr.getLastDocument() != null)
							{
								correspondencePopupMenu.add(viewOrderChangeItem);
								correspondencePopupMenu.add(processDocumentItem);
							}
							else
								correspondencePopupMenu.add(viewOrderChangeItem);
						}
						else if(OrderCancellationType.class.getName().equals(documentReference.getDocumentTypeValue()))
						{
							if(documentReference == corr.getLastDocumentReference() &&
									process.getClass() == BuyerOrderingProcess.class &&
									!documentReference.getStatus().equals(DocumentReference.Status.CDR_RECEIVED)) //sending failed earlier
							{
								correspondencePopupMenu.add(viewResendOrderCancellationItem);
								correspondencePopupMenu.add(resendDocumentItem);
							}
							else
								correspondencePopupMenu.add(viewOrderCancellationItem);
						}
						correspondencePopupMenu.show(table, event.getX(), event.getY());
					}
				}
			}
		});

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
	}
}