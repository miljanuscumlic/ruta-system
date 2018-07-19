package rs.ruta.client.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

/**
 * Dialog for database registration and login.
 */
public class RegisterDialog extends JDialog
{
	private static final long serialVersionUID = 7700950953345766469L;
	private String username;
	private String password; //MMM: not secure, should be changed
	private boolean okPressed; // true if the sign up button were pressed
	private boolean rememberMe;

	/**
	 * Creates dialog for database registration or login.
	 * @param owner parent frame
	 * @param login true when log-in procedure is in place, not a registration
	 * @param rememberCredentials true when {@code Remeber me} checkbox should be displayed
	 * @param mayExit true if dialog can be exited without entering credentials
	 */
	public RegisterDialog(RutaClientFrame owner, boolean login, boolean rememberCredentials, boolean mayExit)
	{
		super(owner, true);
		okPressed = rememberMe = false;
		username = password = null;
		setSize(500, 180);
		setLocationRelativeTo(owner);

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				exit(owner, login, mayExit);
			}
		});

		final JPanel credentialsPanel = new JPanel();
		credentialsPanel.setLayout(new GridBagLayout());
		final JLabel usernameLabel = new JLabel("Username: ", SwingConstants.LEFT); 
		final JTextField usernameField = new JTextField(30);
		final UsernameVerifier usernameVerifier = new UsernameVerifier();
		usernameField.setInputVerifier(usernameVerifier);
		final JLabel passwordLabel = new JLabel("Password: ",  SwingConstants.LEFT); 
		final JTextField passwordField = new JPasswordField(30);
		final PasswordVerifier passwordVerifier = new PasswordVerifier();
		passwordField.setInputVerifier(passwordVerifier);

		final JCheckBox rememberMeBox = new JCheckBox("Remember me"); 
		final Insets insets = new Insets(15, 0, 0, 0);
		putGridCell(credentialsPanel, 0, 0, 1, 1, insets, usernameLabel);
		putGridCell(credentialsPanel, 0, 1, 1, 1, insets, usernameField);
		putGridCell(credentialsPanel, 1, 0, 1, 1, insets, passwordLabel);
		putGridCell(credentialsPanel, 1, 1, 1, 1, insets, passwordField);
		if(rememberCredentials)
			putGridCell(credentialsPanel, 2, 0, 1, 1, insets, rememberMeBox);

		add(credentialsPanel, BorderLayout.NORTH);

		final JPanel buttonPanel = new JPanel();
		final JButton okButton = new JButton();
		if(login)
			okButton.setText("Log in"); 
		else
			okButton.setText("Register"); 
		buttonPanel.add(okButton);
		okButton.addActionListener(event ->
		{
			if(usernameVerifier.shouldYieldFocus(usernameField) &&
					passwordVerifier.shouldYieldFocus(passwordField))
			{
				rememberMe = rememberMeBox.isSelected();
				username = usernameField.getText();
				password = passwordField.getText();
				okPressed = true;
				setVisible(false);
			}
		});

		getRootPane().setDefaultButton(okButton);
		okButton.requestFocusInWindow();
		final JButton cancelButton = new JButton("Cancel"); 
		cancelButton.setVerifyInputWhenFocusTarget(false);
		buttonPanel.add(cancelButton);
		cancelButton.addActionListener(event ->
		{
			passwordField.setText(""); //$NON-NLS-1$
			usernameField.setText(""); //$NON-NLS-1$
			exit(owner, login, mayExit);
		});

		add(buttonPanel, BorderLayout.SOUTH);
	}

	public String getUsername()
	{
		return username;
	}

	public String getPassword()
	{
		return password;
	}

	/**
	 * Checks wheter the OK button was pressed.
	 * @return true if the OK button was pressed
	 */
	public boolean isOKPressed()
	{
		return okPressed;
	}

	public void setOKPressed(boolean okPressed)
	{
		this.okPressed = okPressed;
	}

	/**
	 * Tests whether the user checked {@code Remember me} chechbox.
	 * @return true if the checkbox is checked
	 */
	public boolean isRememberMe()
	{
		return rememberMe;
	}

	public void setRememberMe(boolean rememberMe)
	{
		this.rememberMe = rememberMe;
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
		con.fill = GridBagConstraints.BOTH;
		panel.add(comp, con);
	}

	private void exit(RutaClientFrame owner, boolean login, boolean mayExit)
	{
		if(!mayExit)
		{
			String procedure = null;
			if(login)
				procedure = "log-in"; 
			else
				procedure = "registration"; 
			int option = JOptionPane.showConfirmDialog(owner, "Entering your credentials is mandatory during " + procedure + 
					".\nNot entering them will close the application. Do you want to proceed?", 
					"Warning", JOptionPane.YES_NO_OPTION); 
			if(option == JOptionPane.NO_OPTION)
			{
				setVisible(false);
				System.exit(0);
			}
		}
		else
		{
			setVisible(false);
		}
	}

	private class UsernameVerifier extends InputVerifier
	{
		@Override
		public boolean verify(JComponent input)
		{
			String text = ((JTextField) input).getText();
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
