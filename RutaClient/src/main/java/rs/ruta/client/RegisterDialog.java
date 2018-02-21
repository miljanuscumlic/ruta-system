package rs.ruta.client;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.*;

public class RegisterDialog extends JDialog
{
	private static final long serialVersionUID = 7700950953345766469L;
	private String username;
	private String password; //MMM: not secure, should be changed
	private boolean registerPressed; // true if the sign up button were pressed

	public RegisterDialog(ClientFrame owner)
	{
		super(owner, true);
		registerPressed = false;
		username = password = null;
		setSize(500, 130);
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
		usernameField.setInputVerifier(new UsernameVerifier());
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
		passwordField.setInputVerifier(new PasswordVerifier());
		passPanel.add(passwordField);
		credentialsPanel.add(passPanel, cons2);
/*		JLabel partyLabel = new JLabel("Party data: ");
		GridBagConstraints cons3 = new GridBagConstraints();
		cons3.weightx = 100;
		cons3.weighty = 100;
		cons3.gridx = 0;
		cons3.gridy = 2;
		cons3.gridwidth = 1;
		cons3.gridheight = 1;
		credentialsPanel.add(partyLabel, cons3);*/
		add(credentialsPanel, BorderLayout.NORTH);

		JPanel buttonPanel = new JPanel();

		JButton registerButton = new JButton("Register");
		buttonPanel.add(registerButton);
		registerButton.addActionListener(event ->
		{
			username = usernameField.getText();
			password = passwordField.getText();
			registerPressed = true;
			setVisible(false);
		});

		getRootPane().setDefaultButton(registerButton);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.setVerifyInputWhenFocusTarget(false);
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

	private class UsernameVerifier extends InputVerifier
	{
		@Override
		public boolean verify(JComponent input)
		{
			String text = ((JTextField)input).getText();
			if(text.length() < 3)
				return false;
			else
				return true;
		}

		@Override
		public boolean shouldYieldFocus(JComponent input)
		{
			boolean valid = verify(input);
			if (!valid)
				EventQueue.invokeLater(() ->
				JOptionPane.showMessageDialog(RegisterDialog.this, "Username has to be at least 3 characters long.",
						"Incorrect input data", JOptionPane.ERROR_MESSAGE));
			return valid;
		}
	}

	private class PasswordVerifier extends InputVerifier
	{
		@Override
		public boolean verify(JComponent input)
		{
			String text = ((JTextField)input).getText();
			if(text.length() < 3)
				return false;
			else
				return true;
		}

		@Override
		public boolean shouldYieldFocus(JComponent input)
		{
			boolean valid = verify(input);
			if (!valid)
				EventQueue.invokeLater(() ->
				JOptionPane.showMessageDialog(RegisterDialog.this, "Password has to be at least 3 characters long.",
						"Incorrect input data", JOptionPane.ERROR_MESSAGE));
			return valid;
		}
	}
}
