package rs.ruta.client.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import oasis.names.specification.ubl.schema.xsd.orderresponse_21.OrderResponseType;
import rs.ruta.common.InstanceFactory;

public class ProcessOrderResponseDialog extends AbstractOrderResponseDialog
{
	private static final long serialVersionUID = 4323876859820554276L;
	private String decision;

	/**
	 * Creates dialog for making a decision on what kind of the response document on {@link OrderType}
	 * would be created.
	 * @param owner parent frame
	 * @param orderResponse {@link OrderResponseType} to display
	 */
	public ProcessOrderResponseDialog(RutaClientFrame owner, OrderResponseType orderResponse)
	{
		super(owner, orderResponse, false);

		JButton acceptButton = new JButton(InstanceFactory.ACCEPT_ORDER);
		JButton cancelButton = new JButton(InstanceFactory.CANCEL_ORDER);
		JButton changeButton = new JButton(InstanceFactory.CHANGE_ORDER);
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
		cancelButton.addActionListener(listener);
		changeButton.addActionListener(listener);
		postponeButton.addActionListener(listener);

		getRootPane().setDefaultButton(postponeButton);
		buttonPanel.add(acceptButton);
		buttonPanel.add(cancelButton);
		buttonPanel.add(changeButton);
		buttonPanel.add(postponeButton);
	}

	public String getDecision()
	{
		return decision;
	}

}