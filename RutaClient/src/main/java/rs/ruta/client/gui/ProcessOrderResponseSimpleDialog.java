package rs.ruta.client.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import oasis.names.specification.ubl.schema.xsd.orderresponsesimple_21.OrderResponseSimpleType;
import rs.ruta.common.InstanceFactory;

public class ProcessOrderResponseSimpleDialog extends AbstractOrderResponseSimpleDialog
{
	private static final long serialVersionUID = 3652472045211452799L;
	private String decision;

	/**
	 * Creates {@link OrderResponseSimpleDialog} for making new {@link OrderResponseSimpleType} document.
	 * @param owner parent frame
	 * @param applicationResponse Order Response Simple to show or amend
	 * @param accepted true if Order is to be accepted; false otherwise
	 */
	public ProcessOrderResponseSimpleDialog(RutaClientFrame owner, OrderResponseSimpleType orderResponseSimple,
			boolean accepted)
	{
		super(owner, orderResponseSimple, accepted, false);

		JButton acceptButton = new JButton(InstanceFactory.ACCEPT_ORDER);
		JButton cancelButton = new JButton(InstanceFactory.CANCEL_ORDER);
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

		cancelButton.addActionListener(listener);
		acceptButton.addActionListener(listener);
		postponeButton.addActionListener(listener);

		getRootPane().setDefaultButton(postponeButton);
		buttonPanel.add(acceptButton);
		buttonPanel.add(cancelButton);
		buttonPanel.add(postponeButton);
	}

	public String getDecision()
	{
		return decision;
	}

}