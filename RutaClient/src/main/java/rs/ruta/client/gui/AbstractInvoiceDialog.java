package rs.ruta.client.gui;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.InvoiceLineType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.OrderLineType;

abstract public class AbstractInvoiceDialog extends JDialog
{
	private static final long serialVersionUID = 4226073055517318153L;
	protected List<InvoiceLineType> invoiceLines;
	protected JPanel headerPanel;
	protected JPanel buttonPanel;
	protected JTable invoiceLinesTable;
	protected MouseAdapter stopEditingListener;

	/**
	 * Constructor that is setting the common part of all {@code AbstractInvoiceTable} instances
	 * i.e. a invoiceLinesTable displaying Invoice Lines items.
	 * @param owner parent frame of this dialogue
	 * @param invoiceLines {@link InvoiceLineType Invoice Lines} to display
	 * @param editable whether the Invoice lines are editable
	 */
	public AbstractInvoiceDialog(RutaClientFrame owner, List<InvoiceLineType> invoiceLines, boolean editable)
	{
		super(owner, true);
		setSize(700, 500);
		setLocationRelativeTo(owner);
		this.invoiceLines = invoiceLines;

		headerPanel = new JPanel(new BorderLayout());
		add(headerPanel, BorderLayout.NORTH);

		final JPanel invoicePanel = new JPanel(new BorderLayout());
		final InvoiceLinesTableModel invoiceTableModel = new InvoiceLinesTableModel(invoiceLines, editable);
		invoiceLinesTable = createInvoiceTable(invoiceTableModel);
		invoicePanel.add(new JScrollPane(invoiceLinesTable));
		add(invoicePanel, BorderLayout.CENTER);

		stopEditingListener = new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				stopEditing();
			}
		};
		addMouseListener(stopEditingListener);
		getRootPane().addMouseListener(stopEditingListener);
		invoiceLinesTable.getTableHeader().addMouseListener(stopEditingListener);
		invoiceLinesTable.addMouseListener(stopEditingListener);

		buttonPanel = new JPanel();
		buttonPanel.addMouseListener(stopEditingListener);
		add(buttonPanel, BorderLayout.SOUTH);

	}

	protected void stopEditing()
	{
		if(invoiceLinesTable.isEditing())
			invoiceLinesTable.getCellEditor().stopCellEditing();
	}

	public List<InvoiceLineType> getInvoiceLines()
	{
		return invoiceLines;
	}

	private JTable createInvoiceTable(DefaultTableModel tableModel)
	{
		JTable table = new JTable(tableModel);
		table.setFillsViewportHeight(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		TableColumnModel columnModel = table.getColumnModel();
		columnModel.getColumn(0).setPreferredWidth(20);
		columnModel.getColumn(1).setPreferredWidth(100);
		columnModel.getColumn(2).setPreferredWidth(100);
		columnModel.getColumn(3).setPreferredWidth(100);
		columnModel.getColumn(4).setPreferredWidth(100);
		table.setPreferredScrollableViewportSize(table.getPreferredSize());
		return table;
	}

	/**
	 * Removes all {@link OrderLineType Invoice lines} which {@link LineItemType line items} have a quantity
	 * that is a {@code null} value or 0.
	 * @param invoiceLines invoiceLines to process
	 * @return true if trimming is done without throwing any exception; false otherwise
	 */
	//MMM not used
	@Deprecated
	protected boolean trimInvoiceLines(List<OrderLineType> invoiceLines)
	{
		boolean success = true;
		try
		{
			invoiceLines.removeIf(orderLine ->
			orderLine.getLineItem().getQuantityValue() == null ||
			new BigDecimal(0).compareTo(orderLine.getLineItem().getQuantityValue()) >= 0);
		}
		catch(Exception e)
		{
			success = false;
		}
		return success;
	}

	protected void numberInvoiceLines(List<InvoiceLineType> invoiceLines)
	{
		int lineNumber = 0;
		for(InvoiceLineType invoiceLine: invoiceLines)
		{
			invoiceLine.setID(String.valueOf(lineNumber++));
		}
	}

}