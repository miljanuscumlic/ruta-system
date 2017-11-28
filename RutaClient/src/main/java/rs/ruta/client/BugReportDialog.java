package rs.ruta.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import rs.ruta.common.BugReport;
import rs.ruta.common.ReportAttachment;
import rs.ruta.common.RutaVersion;

public class BugReportDialog extends JDialog
{
	private static final long serialVersionUID = -4919954196081520043L;
	private BugReport bugReport;
	private JTextField versionField;
	private JTextField platformField;
	private JTextField osField;
	private JTextField javaField;
	private JTextField summaryField;
	private JTextArea descriptionArea;
	private JComboBox<String> componentBox;
	private JLabel productLabel = new JLabel("Ruta", SwingConstants.LEFT);
	private JTextField att1Field;
	private JTextField att2Field;

	private boolean reportPressed;

	public BugReportDialog(ClientFrame owner)
	{
		super(owner, "Report a Bug", true);
		setResizable(false);
		setSize(520, 500);
		setLocationRelativeTo(owner);

		bugReport = new BugReport();

		int width = 30;
		componentBox = new JComboBox<>();
		for(String kind: RutaVersion.getKinds())
			componentBox.addItem(kind);

		versionField = new JTextField(width);
		versionField.setText(Client.getVersion().getVersion());
		platformField = new JTextField(width);
		platformField.setText(bugReport.getPlatform());
		osField = new JTextField(width);
		osField.setText(bugReport.getOs());
		javaField = new JTextField(width);
		javaField.setText(bugReport.getJavaVersion());
		summaryField = new JTextField(width);
		descriptionArea = new JTextArea(8, width);
		att1Field = new JTextField(width);
		att1Field.setEditable(false);
		att1Field.setOpaque(true);
		att1Field.setBackground(Color.WHITE);
		att1Field.setBorder(javax.swing.BorderFactory.createLineBorder(Color.GRAY));
		att2Field = new JTextField(width);
		att2Field.setEditable(false);
		att2Field.setOpaque(true);
		att2Field.setBackground(Color.WHITE);
		att2Field.setBorder(javax.swing.BorderFactory.createLineBorder(Color.GRAY));

		add(createBugPanel(), BorderLayout.NORTH);

		JPanel buttonPanel = new JPanel();
		JButton reportButton = new JButton("Report");
		buttonPanel.add(reportButton);
		JButton cancelButton = new JButton("Cancel");
		buttonPanel.add(cancelButton);
		add(buttonPanel, BorderLayout.SOUTH);

		reportButton.addActionListener(event ->
		{
			reportPressed = true;
			String s = (String) componentBox.getSelectedItem();
/*			bugReport = new BugReport(summaryField.getText(), null, productLabel.getText(),
					(String) componentBox.getSelectedItem(), versionField.getText(), platformField.getText(),
					osField.getText(), descriptionArea.getText(), javaField.getText(), null, null);*/

			bugReport.setSummary(summaryField.getText());
			bugReport.setComponent((String) componentBox.getSelectedItem());
			bugReport.setVersion(versionField.getText());
			bugReport.setPlatform(platformField.getText());
			bugReport.setOs(osField.getText());
			bugReport.setDescription(descriptionArea.getText());
			bugReport.setJavaVersion(javaField.getText());
			setVisible(false);
			dispose();
		});

		cancelButton.addActionListener(event ->
		{
			setVisible(false);
			dispose();
		});
	}

	public BugReport getBugReport()
	{
		return bugReport;
	}

	public boolean isReportPressed()
	{
		return reportPressed;
	}

	private JPanel createBugPanel()
	{
		JPanel bugPanel = new JPanel();
		GridBagLayout grid = new GridBagLayout();
		bugPanel.setLayout(grid);
		Insets insets1 = new Insets(10, 5, 0, 0);
		Insets insets2 = new Insets(5, 5, 0, 0);
		Insets insets3 = new Insets(5, 5, 0, 0);
/*		JLabel att1Filename = new JLabel("", SwingConstants.SOUTH_EAST);
		JLabel att2Filename = new JLabel("", SwingConstants.SOUTH_EAST);*/
		JButton att1Button = new JButton("Add attachment");
		JButton att2Button = new JButton("Add attachment");

		ActionListener action = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new File("."));
				chooser.setDialogTitle("Add Attachment");
				int result = chooser.showOpenDialog(BugReportDialog.this);
				if(result == JFileChooser.APPROVE_OPTION)
				{
					ReportAttachment att;
					try
					{
						att = new ReportAttachment(chooser.getSelectedFile());
						bugReport.getAttachments().add(att);
						if((JButton)(event.getSource()) == att1Button)
						{
							att1Field.setText(chooser.getSelectedFile().getName());
						}
						else
							att2Field.setText(chooser.getSelectedFile().getName());
					}
					catch (IOException e)
					{
						JOptionPane.showMessageDialog(BugReportDialog.this, "Attachment could not be appended to a Bug Report!",
								"Error Message", JOptionPane.ERROR_MESSAGE);
						//e.printStackTrace();
					}
				}
			}
		};

		att1Button.addActionListener( action);
		att2Button.addActionListener( action);

		putGridCell(bugPanel, 0, 0, 1, 1, insets1, new JLabel("Product: ", SwingConstants.LEFT));
		putGridCell(bugPanel, 0, 1, 1, 1, insets1, productLabel);
		putGridCell(bugPanel, 1, 0, 1, 1, insets2, new JLabel("Component: ", SwingConstants.LEFT));
		putGridCell(bugPanel, 1, 1, 1, 1, insets2, componentBox);
		putGridCell(bugPanel, 2, 0, 1, 1, insets2, new JLabel("Version: ", SwingConstants.LEFT));
		putGridCell(bugPanel, 2, 1, 1, 1, insets2, versionField);
		putGridCell(bugPanel, 3, 0, 1, 1, insets2, new JLabel("Hardware: ", SwingConstants.LEFT));
		putGridCell(bugPanel, 3, 1, 1, 1, insets2, platformField);
		putGridCell(bugPanel, 4, 0, 1, 1, insets2, new JLabel("Operating system: ", SwingConstants.LEFT));
		putGridCell(bugPanel, 4, 1, 1, 1, insets2, osField);
		putGridCell(bugPanel, 5, 0, 1, 1, insets2, new JLabel("Java version: ", SwingConstants.LEFT));
		putGridCell(bugPanel, 5, 1, 1, 1, insets2, javaField);
		putGridCell(bugPanel, 6, 0, 1, 1, insets2, new JLabel("Bug summary: ", SwingConstants.LEFT));
		putGridCell(bugPanel, 6, 1, 1, 1, insets2, summaryField);
		putGridCell(bugPanel, 7, 0, 1, 1, insets2, new JLabel("Bug description: ", SwingConstants.LEFT));
		putGridCell(bugPanel, 7, 1, 1, 1, insets2, new JScrollPane(descriptionArea));
		putGridCell(bugPanel, 8, 0, 1, 1, insets2, att1Button);
		putGridCell(bugPanel, 8, 1, 1, 1, insets3, att1Field);
		putGridCell(bugPanel, 9, 0, 1, 1, insets2, att2Button);
		putGridCell(bugPanel, 9, 1, 1, 1, insets3, att2Field);
		putGridCell(bugPanel, 10, 0, 2, 1, insets1, new JLabel("All filled fields are merely hints. Please correct them if neccessary.",
				SwingConstants.LEFT));
		return bugPanel;
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
		con.anchor = GridBagConstraints.WEST;
		con.fill = GridBagConstraints.NONE;
		panel.add(comp, con);
	}

	/**Clears all fields that are frequently changed in the dialog. Come may stay like OS, Java etc.
	 */
	public void clearData()
	{
		reportPressed = false;
		versionField.setText(null);;
		platformField.setText(null);;
		osField.setText(null);;
		javaField.setText(null);;
		summaryField.setText(null);;
		descriptionArea.setText(null);;
	}
}
