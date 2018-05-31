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

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.OrderLineType;
import oasis.names.specification.ubl.schema.xsd.order_21.OrderType;

public abstract class AbstractOrderDialog extends JDialog
{
	private static final long serialVersionUID = 4226073055517318153L;
	protected List<OrderLineType> orderLines;
	protected JPanel headerPanel;
	protected JPanel buttonPanel;
	protected JTable orderLinesTable;
	protected MouseAdapter stopTableEditingListener;

	/**
	 * Constructor that is setting the common part of all {@code AbstractOrderTable} instances
	 * i.e. a orderLinesTable displaying orderLines line items.
	 * @param owner parent frame of this dialogue
	 * @param orderLines {@link OrderLineType orderLines lines} to display
	 * @param editable whether the Order lines are editable i.e. their quantity value
	 */
	public AbstractOrderDialog(RutaClientFrame owner, List<OrderLineType> orderLines, boolean editable)
	{
		super(owner, true);
		setSize(700, 500);
		setLocationRelativeTo(owner);
		this.orderLines = orderLines;

		headerPanel = new JPanel(new BorderLayout());
		add(headerPanel, BorderLayout.NORTH);

		final JPanel orderPanel = new JPanel(new BorderLayout());
		final OrderLinesTableModel orderTableModel = new OrderLinesTableModel(orderLines, editable);
		orderLinesTable = createOrderTable(orderTableModel);
		orderPanel.add(new JScrollPane(orderLinesTable));
		add(orderPanel, BorderLayout.CENTER);

		stopTableEditingListener = new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				stopEditing();
			}
		};
		addMouseListener(stopTableEditingListener);
		getRootPane().addMouseListener(stopTableEditingListener);
		orderLinesTable.getTableHeader().addMouseListener(stopTableEditingListener);
		orderLinesTable.addMouseListener(stopTableEditingListener);

		buttonPanel = new JPanel();
		buttonPanel.addMouseListener(stopTableEditingListener);
		add(buttonPanel, BorderLayout.SOUTH);

	}

	protected void stopEditing()
	{
		if(orderLinesTable.isEditing())
			orderLinesTable.getCellEditor().stopCellEditing();
	}

	public List<OrderLineType> getOrderLines()
	{
		return orderLines;
	}

	private JTable createOrderTable(DefaultTableModel tableModel)
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
	 * Removes all {@link OrderLineType orderLines lines} which {@link LineItemType line items} have a quantity
	 * that is a {@code null} value or 0.
	 * @param orderLines orderLines to process
	 * @return true if trimming is done without throwing any exception; false otherwise
	 */
	protected boolean trimOrderLines(List<OrderLineType> orderLines)
	{
		boolean success = true;
		try
		{
			orderLines.removeIf(orderLine ->
			orderLine.getLineItem().getQuantityValue() == null ||
			new BigDecimal(0).compareTo(orderLine.getLineItem().getQuantityValue()) >= 0);
		}
		catch(Exception e)
		{
			success = false;
		}
		return success;
	}

	protected void numberOrderLines(List<OrderLineType> orderLines)
	{
		int lineNumber = 0;
		for(OrderLineType orderLine: orderLines)
		{
			orderLine.getLineItem().setID(String.valueOf(lineNumber++));
		}
	}

}