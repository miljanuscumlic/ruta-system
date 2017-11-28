package rs.ruta.client;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

public class CDRSettingsDialog extends JDialog
{
	private static final long serialVersionUID = -1973674445580159781L;
	private JTextField serviceField;
	private String serviceLocation;
	private boolean applyPressed; //wheather Apply button is pressed

	public CDRSettingsDialog(ClientFrame owner)
	{
		super(owner, true);
		setResizable(false);
		setSize(450, 150);
		setLocationRelativeTo(owner);
		serviceLocation = Client.getCDREndPoint();
		serviceField = new JTextField(30);
		serviceField.setText(serviceLocation);
		applyPressed = false;
		JPanel cdrPanel = new JPanel();
		cdrPanel.setLayout(new BorderLayout());
		cdrPanel.add(createSettingsPanel(), BorderLayout.NORTH);
		add(cdrPanel, BorderLayout.NORTH);
		add(createButtonPanel(), BorderLayout.CENTER);
	}

	public boolean isApplyPressed() { return applyPressed; }

	public void setApplyPressed(boolean apply) { this.applyPressed = apply; }

	public String getServiceLocation() { return serviceLocation; }

	public void setServiceLocation(String server) { this.serviceLocation = server; }

	private JPanel createSettingsPanel()
	{
		JPanel settingsPanel = new JPanel();
		GridBagLayout grid = new GridBagLayout();
		settingsPanel.setLayout(grid);
		JButton reset = new JButton("Restore defaults");
		reset.setToolTipText("Revert to default service location setting.");

		reset.addActionListener(event ->
		{
			serviceLocation = Client.getDefaultEndPoint();
			serviceField.setText(serviceLocation);
		});

		Insets insets = new Insets(5, 0, 0, 0);
		putGridCell(settingsPanel, 0, 0, 1, 1, insets, new JLabel("Service location: ", SwingConstants.LEFT));
		putGridCell(settingsPanel, 0, 1, 1, 1, insets, serviceField);
		putGridCell(settingsPanel, 1, 1, 1, 1, new Insets(5, 0, 5, 0), reset);
		settingsPanel.setBorder(new TitledBorder("Service settings"));

		return settingsPanel;
	}

	private JPanel createButtonPanel()
	{
		JPanel buttonPanel = new JPanel();
		JButton apply = new JButton("Apply and close");
		JButton cancel = new JButton("Cancel");

		apply.addActionListener(event ->
		{
			String serviceString = serviceField.getText();
			//MMM: here should be some better validation of the input string
			if("".equals(serviceString))
				JOptionPane.showMessageDialog(this, "Service location field can not be empty!", "Invalid input", JOptionPane.ERROR_MESSAGE);
			else
			{
				serviceLocation = serviceString;
				applyPressed = true;
				setVisible(false);
			}
		});

		cancel.addActionListener(event ->
		{
			setVisible(false);
		});
		Insets insets = new Insets(15, 0, 0, 0);
		putGridCell(buttonPanel, 0, 0, 1, 1, insets, apply);
		putGridCell(buttonPanel, 0, 1, 1, 1, insets, cancel);

		return buttonPanel;
	}

	//MMM: this method should be part of some common package and be static, because it is used in many different dialogs
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

}
