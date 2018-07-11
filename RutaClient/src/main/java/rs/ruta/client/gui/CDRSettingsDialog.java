package rs.ruta.client.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import rs.ruta.client.RutaClient;

public class CDRSettingsDialog extends JDialog
{
	private static final long serialVersionUID = -1973674445580159781L;
	private String serviceLocation;
	private JTextField serviceField;
	private String connectTimeout;
	private JTextField connectTimeoutField;
	private String requestTimeout;
	private JTextField requestTimeoutField;
	private boolean applyPressed; //wheather Apply button is pressed

	public CDRSettingsDialog(RutaClientFrame owner)
	{
		super(owner, true);
		setResizable(false);
		setSize(550, 200);
		setLocationRelativeTo(owner);
		serviceLocation = RutaClient.getCDREndPoint();
		serviceField = new JTextField(30);
		serviceField.setText(serviceLocation);
		connectTimeout = String.valueOf(RutaClient.getConnectTimeout() / 1000);
		connectTimeoutField = new JTextField(30);
		connectTimeoutField.setText(connectTimeout);
		connectTimeoutField.setInputVerifier(new NonnegativeNumberVerifier());
		requestTimeout = String.valueOf(RutaClient.getRequestTimeout() / 1000);
		requestTimeoutField = new JTextField(30);
		requestTimeoutField.setText(requestTimeout);
		requestTimeoutField.setInputVerifier(new NonnegativeNumberVerifier());
		applyPressed = false;
		JPanel cdrPanel = new JPanel();
		cdrPanel.setLayout(new BorderLayout());
		cdrPanel.add(createSettingsPanel(), BorderLayout.NORTH);
		add(cdrPanel, BorderLayout.NORTH);
		add(createButtonPanel(), BorderLayout.CENTER);
	}

	public boolean isApplyPressed()
	{
		return applyPressed;
	}

	public void setApplyPressed(boolean apply)
	{
		this.applyPressed = apply;
		}

	public String getServiceLocation()
	{
		return serviceLocation;
		}

	public void setServiceLocation(String server)
	{
		this.serviceLocation = server;
		}

	public String getConnectTimeout()
	{
		return connectTimeout;
	}

	public void setConnectTimeout(String connectTimeout)
	{
		this.connectTimeout = connectTimeout;
	}

	public String getRequestTimeout()
	{
		return requestTimeout;
	}

	public void setRequestTimeout(String requestTimeout)
	{
		this.requestTimeout = requestTimeout;
	}

	private JPanel createSettingsPanel()
	{
		final JPanel settingsPanel = new JPanel();
		final GridBagLayout grid = new GridBagLayout();
		settingsPanel.setLayout(grid);
		final JButton resetButton = new JButton(Messages.getString("CDRSettingsDialog.0")); 
		resetButton.setToolTipText(Messages.getString("CDRSettingsDialog.1")); 

		resetButton.addActionListener(event ->
		{
			serviceLocation = RutaClient.getDefaultEndPoint();
			serviceField.setText(serviceLocation);
			connectTimeout = requestTimeout = "0"; 
			connectTimeoutField.setText("0"); 
			requestTimeoutField.setText("0"); 
		});

		final Insets insets = new Insets(5, 0, 0, 0);
		putGridCell(settingsPanel, 0, 0, 1, 1, insets, new JLabel(Messages.getString("CDRSettingsDialog.5"), SwingConstants.LEFT)); 
		putGridCell(settingsPanel, 0, 1, 1, 1, insets, serviceField);
		putGridCell(settingsPanel, 1, 0, 1, 1, insets, new JLabel(Messages.getString("CDRSettingsDialog.6"), SwingConstants.LEFT)); 
		putGridCell(settingsPanel, 1, 1, 1, 1, insets, connectTimeoutField);
		putGridCell(settingsPanel, 2, 0, 1, 1, insets, new JLabel(Messages.getString("CDRSettingsDialog.7"), SwingConstants.LEFT)); 
		putGridCell(settingsPanel, 2, 1, 1, 1, insets, requestTimeoutField);
		putGridCell(settingsPanel, 3, 1, 1, 1, new Insets(5, 0, 5, 0), resetButton);
		settingsPanel.setBorder(new TitledBorder(Messages.getString("CDRSettingsDialog.8"))); 

		return settingsPanel;
	}

	private JPanel createButtonPanel()
	{
		final JPanel buttonPanel = new JPanel();
		final JButton applyButton = new JButton(Messages.getString("CDRSettingsDialog.9")); 
		final JButton cancelButton = new JButton(Messages.getString("CDRSettingsDialog.10")); 
		getRootPane().setDefaultButton(applyButton);
		applyButton.requestFocusInWindow();
		applyButton.addActionListener(event ->
		{
			String serviceString = serviceField.getText();
			connectTimeout = connectTimeoutField.getText();
			requestTimeout = requestTimeoutField.getText();
			//MMM here should be some better validation of the input string
			if("".equals(serviceString)) 
				JOptionPane.showMessageDialog(this, Messages.getString("CDRSettingsDialog.12"), Messages.getString("CDRSettingsDialog.13"), JOptionPane.ERROR_MESSAGE);  
			else
			{
				serviceLocation = serviceString;
				applyPressed = true;
				setVisible(false);
			}
		});

		cancelButton.setVerifyInputWhenFocusTarget(false);
		cancelButton.addActionListener(event ->
		{
			setVisible(false);
		});
		Insets insets = new Insets(15, 0, 0, 0);
		putGridCell(buttonPanel, 0, 0, 1, 1, insets, applyButton);
		putGridCell(buttonPanel, 0, 1, 1, 1, insets, cancelButton);

		return buttonPanel;
	}

	//MMM this method should be part of some common package and be static, because it is used in many different dialogs
	private void putGridCell(JPanel panel, int row, int column, int width, int height, Insets insets, Component comp)
	{
		GridBagConstraints con = new GridBagConstraints();
		con.weightx = 0;
		con.weighty = 0;
		con.gridx = column;
		con.gridy = row;
		con.gridwidth = width;
		con.gridheight = height;
		if(insets != null)
			con.insets = insets;
		con.anchor = GridBagConstraints.EAST;
//		con.fill = GridBagConstraints.BOTH;
		panel.add(comp, con);
	}

	private class NonnegativeNumberVerifier extends InputVerifier
	{
		@Override
		public boolean verify(JComponent input)
		{
			final String text = ((JTextField) input).getText();
			try
			{
				final Integer value = Integer.valueOf(text);
				if(value >= 0)
					return true;
				else
					return false;
			}
			catch(NumberFormatException e)
			{
				return false;
			}
		}

		@Override
		public boolean shouldYieldFocus(JComponent input)
		{
			final boolean valid = verify(input);
			if(!valid)
			{
				EventQueue.invokeLater(() -> JOptionPane.showMessageDialog(CDRSettingsDialog.this,
						Messages.getString("CDRSettingsDialog.14"), Messages.getString("CDRSettingsDialog.15"), JOptionPane.ERROR_MESSAGE));  
			}
			return valid;
		}
	}

}