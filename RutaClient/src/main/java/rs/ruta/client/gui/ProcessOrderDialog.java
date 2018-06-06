package rs.ruta.client.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import oasis.names.specification.ubl.schema.xsd.order_21.OrderType;
import rs.ruta.common.InstanceFactory;

public class ProcessOrderDialog extends AbstractOrderDialog
{
	private static final long serialVersionUID = -805322842356794669L;
	private String decision;

	public ProcessOrderDialog(RutaClientFrame owner, OrderType order)
	{
		super(owner, order.getOrderLine(), false);
		JButton acceptButton = new JButton(InstanceFactory.ACCEPT_ORDER);
		JButton rejectButton = new JButton(InstanceFactory.REJECT_ORDER);
		JButton addDetailButton = new JButton(InstanceFactory.ADD_DETAIL);
		JButton postponeButton = new JButton(InstanceFactory.DECIDE_LATER);
		decision = null;

		ActionListener listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event)
			{
				decision = ((JButton) event.getSource()).getText();
				setVisible(false);
			}
		};

		acceptButton.addActionListener(listener);
		rejectButton.addActionListener(listener);
		addDetailButton.addActionListener(listener);
		postponeButton.addActionListener(listener);

		getRootPane().setDefaultButton(postponeButton);
		postponeButton.requestFocusInWindow();
		buttonPanel.add(acceptButton);
		buttonPanel.add(rejectButton);
		buttonPanel.add(addDetailButton);
		buttonPanel.add(postponeButton);

	}

	public String getDecision()
	{
		return decision;
	}
}