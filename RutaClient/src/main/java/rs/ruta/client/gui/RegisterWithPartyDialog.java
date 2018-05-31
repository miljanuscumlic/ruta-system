package rs.ruta.client.gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.*;
import javax.swing.table.*;

import rs.ruta.client.Party;

@Deprecated
public class RegisterWithPartyDialog extends JDialog
{
	private static final long serialVersionUID = 7700950953345766469L;
	private PartyTableModel partyModel;
	private String username;
	private String password; //MMM: not secure, should be changed
	private boolean registerPressed; // true if the sign up button were pressed

	public RegisterWithPartyDialog(RutaClientFrame owner, Party party)
	{
		super(owner, true);
		registerPressed = false;
		username = password = null;
		partyModel = new PartyTableModel();
		partyModel.setTableEditable(false);
		setParty(party);
		setSize(500, 590);
		setLocationRelativeTo(owner);

		JPanel credentialsPanel = new JPanel();
		GridBagLayout grid = new GridBagLayout();
		credentialsPanel.setLayout(grid);
		GridBagConstraints cons1 = new GridBagConstraints();
		cons1.weightx = 100;
		cons1.weighty = 100;
		cons1.gridx = 0;
		cons1.gridy = 0;
		cons1.gridwidth = 1;
		cons1.gridheight = 1;
		JPanel userPanel = new JPanel();
		JLabel usernameLabel = new JLabel("Username: ", SwingConstants.LEFT);
		userPanel.add(usernameLabel);
		JTextField usernameField = new JTextField(30);
		userPanel.add(usernameField);
		credentialsPanel.add(userPanel, cons1);
		GridBagConstraints cons2 = new GridBagConstraints();
		cons2.weightx = 100;
		cons2.weighty = 100;
		cons2.gridx = 0;
		cons2.gridy = 1;
		cons2.gridwidth = 1;
		cons2.gridheight = 1;
		JPanel passPanel = new JPanel();
		JLabel passwordLabel = new JLabel("Password: ",  SwingConstants.LEFT);
		passPanel.add(passwordLabel);
		JTextField passwordField = new JPasswordField(30);
		passPanel.add(passwordField);
		credentialsPanel.add(passPanel, cons2);
		JLabel partyLabel = new JLabel("Party data: ");
		GridBagConstraints cons3 = new GridBagConstraints();
		cons3.weightx = 100;
		cons3.weighty = 100;
		cons3.gridx = 0;
		cons3.gridy = 2;
		cons3.gridwidth = 1;
		cons3.gridheight = 1;
		credentialsPanel.add(partyLabel, cons3);
		add(credentialsPanel, BorderLayout.NORTH);

		JPanel partyPanel = new JPanel();
		JTable table = new PartyTable(partyModel);
		table.setFillsViewportHeight(true);
		table.getTableHeader().setReorderingAllowed(false);
		table.setRowSelectionAllowed(false);
		partyPanel.add(new JScrollPane(table));
		add(partyPanel, BorderLayout.CENTER);

		TableColumnModel tableColumnModel = table.getColumnModel();
		TableColumn tableColumn = tableColumnModel.getColumn(0);
		tableColumn.setResizable(false);
		tableColumn = tableColumnModel.getColumn(1);
		tableColumn.setPreferredWidth(280);

		JPanel buttonPanel = new JPanel();

		JButton registerButton = new JButton("Register");
		buttonPanel.add(registerButton);
		registerButton.addActionListener(event ->
		{
			registerPressed = true;
			username = usernameField.getText();
			password = passwordField.getText();
			setVisible(false);
		});

		JButton changeButton = new JButton("Change party data");
		buttonPanel.add(changeButton);
		changeButton.addActionListener(event ->
		{
			Party changedParty = owner.showPartyDialog(party, "My Party", false);
			setParty(changedParty);
			repaint();
		});

		JButton cancelButton = new JButton("Cancel");
		buttonPanel.add(cancelButton);
		cancelButton.addActionListener(event ->
		{
			passwordField.setText("");
			usernameField.setText("");
			setVisible(false);
		});

		add(buttonPanel, BorderLayout.SOUTH);
	}


	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	/**Checks if the Register button is pressed
	 * @return true if the Register button is pressed
	 */
	public boolean isRegisterPressed()
	{
		return registerPressed;
	}

	public void setRegisterPressed(boolean registerPressed)
	{
		this.registerPressed = registerPressed;
	}

	/**Sets the party field of the SignUpDialog and companion orderLinesTable model that contains Party data dispayed in this dialog.
	 * @param party Party instance that should be set
	 */
	public void setParty(Party party)
	{
		partyModel.setParty(party);
	}
}
