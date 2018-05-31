package rs.ruta.client.gui;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.LineItemType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.OrderLineType;
import oasis.names.specification.ubl.schema.xsd.order_21.OrderType;

public class PreviewOrderDialog extends AbstractOrderDialog
{
	private static final long serialVersionUID = -4088005817534470754L;

	/**
	 * Creates new dialogue for previewing an {@link OrderType orderLines}.
	 * @param owner parent frame of this dialogue
	 * @param orderLines {@link OrderType orderLines} to display
	 */
	public PreviewOrderDialog(RutaClientFrame owner, OrderType order)
	{
		super(owner, order.getOrderLine(), false);
		final JButton closeButton = new JButton("Close");
		getRootPane().setDefaultButton(closeButton);
		closeButton.addActionListener(event ->
		{
			setVisible(false);
		});
		buttonPanel.add(closeButton);
		add(buttonPanel, BorderLayout.SOUTH);
	}
}