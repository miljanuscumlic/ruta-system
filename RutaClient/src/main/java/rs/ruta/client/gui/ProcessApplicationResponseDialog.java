package rs.ruta.client.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

import oasis.names.specification.ubl.schema.xsd.applicationresponse_21.ApplicationResponseType;
import rs.ruta.common.InstanceFactory;

public class ProcessApplicationResponseDialog extends AbstractApplicationResponseDialog
{
	private static final long serialVersionUID = 3652472045211452799L;
	private String decision;

	/**
	 * Creates {@link ProcessApplicationResponseDialog} for making new {@link ApplicationResponseType} document.
	 * @param owner parent frame
	 * @param applicationResponse Application Response to show
	 */
	public ProcessApplicationResponseDialog(RutaClientFrame owner, ApplicationResponseType applicationResponse)
	{
		super(owner, applicationResponse, false);

		JButton acceptButton = new JButton(InstanceFactory.ACCEPT);
		JButton invoiceButton = new JButton(InstanceFactory.MODIFY_INVOICE);
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

		invoiceButton.addActionListener(listener);
		acceptButton.addActionListener(listener);
		postponeButton.addActionListener(listener);

		final String responseCode = applicationResponse.getDocumentResponseAtIndex(0).getResponse().getResponseCodeValue();
		if(InstanceFactory.APP_RESPONSE_POSITIVE.equals(responseCode))
		{
			buttonPanel.add(acceptButton);
			getRootPane().setDefaultButton(acceptButton);
		}
		else //if(InstanceFactory.APP_RESPONSE_NEGATIVE.equals(responseCode))
		{
			buttonPanel.add(invoiceButton);
			buttonPanel.add(postponeButton);
			getRootPane().setDefaultButton(postponeButton);
		}
	}

	public String getDecision()
	{
		return decision;
	}
}