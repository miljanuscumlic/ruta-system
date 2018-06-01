package rs.ruta.client.gui;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import oasis.names.specification.ubl.schema.xsd.orderresponsesimple_21.OrderResponseSimpleType;

public abstract class AbstractOrderResponseSimpleDialog extends JDialog
{
	private static final long serialVersionUID = 3259789645341776081L;
	protected JTable responseTable;
	protected OrderResponseSimpleType orderResponseSimple;
	protected JPanel buttonPanel;

	/**
	 * Creates {@link OrderResponseSimpleDialog} for making new {@link OrderResponseSimpleType} document.
	 * @param owner parent frame
	 * @param editable true if Order Response Simple document is to be created; false if is to be viewed only
	 * @param orderCancellation Order Response Simple to show or amend
	 */
	public AbstractOrderResponseSimpleDialog(RutaClientFrame owner, OrderResponseSimpleType orderResponseSimple,
			boolean editable)
	{
		super(owner, true);
		this.orderResponseSimple = orderResponseSimple;
		setSize(700, 180);
		setLocationRelativeTo(owner);
		final JPanel responsePanel = new JPanel(new BorderLayout());
		final OrderResponseSimpleModel responseModel = new OrderResponseSimpleModel(orderResponseSimple, editable,
				orderResponseSimple.isAcceptedIndicatorValue(false));
		responseTable = createResponseTable(responseModel);

		responsePanel.add(new JScrollPane(responseTable));
		add(responsePanel, BorderLayout.CENTER);

		MouseAdapter tableFocus = new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent event)
			{
				stopEditing();
			}
		};
		addMouseListener(tableFocus);
		buttonPanel = new JPanel();
		add(buttonPanel, BorderLayout.SOUTH);

	}

	public OrderResponseSimpleType getOrderResponseSimple()
	{
		return orderResponseSimple;
	}

	private JTable createResponseTable(DefaultTableModel tableModel)
	{
		JTable table = new JTable(tableModel);
		table.setTableHeader(null);
		table.setFillsViewportHeight(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		TableColumnModel columnModel = table.getColumnModel();
		columnModel.getColumn(0).setPreferredWidth(180);
		columnModel.getColumn(1).setPreferredWidth(520);
		return table;
	}

	protected void stopEditing()
	{
		if(responseTable.isEditing())
			responseTable.getCellEditor().stopCellEditing();
	}

}