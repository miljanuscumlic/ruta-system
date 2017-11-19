package rs.ruta.client;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

import rs.ruta.common.RutaVersion;

public class NotifyDialog extends JDialog
{
	private static final long serialVersionUID = 7550213837453337452L;
	private RutaVersion version;
	private JList<String> sideList;
	private JTextField sideField;
	private JTextField versionField;
	private JTextField jaxbField;
	private JTextField linkField;

	private boolean notifyPressed; // true if the notify button were pressed

	public NotifyDialog(ClientFrame owner	)
	{
		super(owner, "Notify about new Ruta Client application", true);
		setResizable(false);
		setSize(500, 150);
		setLocationRelativeTo(owner);
		notifyPressed = false;
		version = new RutaVersion();

		String[] types = RutaVersion.getKinds();
		sideList = new JList<String>(types);
		sideList.setVisibleRowCount(1);
		sideList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		sideList.setSelectedValue(types[0], true);;
		int width = 20;
		sideField = new JTextField(width);
		versionField = new JTextField(width);
		jaxbField = new JTextField(width);
		linkField = new JTextField(width);

		add(createVersionPanel(), BorderLayout.NORTH);

		JPanel buttonPanel = new JPanel();
		JButton notifyButton = new JButton("Notify");
		buttonPanel.add(notifyButton);
		JButton cancelButton = new JButton("Cancel");
		buttonPanel.add(cancelButton);
		add(buttonPanel, BorderLayout.SOUTH);

		notifyButton.addActionListener(event ->
		{
			notifyPressed = true;
			version.setSide(sideList.getSelectedValue());
			version.setVersion(versionField.getText());
			version.setJaxbVersion(jaxbField.getText());
			version.setWeblink(linkField.getText());
			setVisible(false);
		});

		cancelButton.addActionListener(event ->
		{
			setVisible(false);
		});
	}

	public boolean isNotifyPressed()
	{
		return notifyPressed;
	}

	public void setNotifyPressed(boolean notifyPressed)
	{
		this.notifyPressed = notifyPressed;
	}

	public RutaVersion getVersion()
	{
		return version;
	}

	public void setVersion(RutaVersion version)
	{
		this.version = version;
	}

	private JPanel createVersionPanel()
	{
		JPanel versionPanel = new JPanel();
		GridBagLayout grid = new GridBagLayout();
		versionPanel.setLayout(grid);
		Insets insets = new Insets(10, 0, 0, 0);
		putGridCell(versionPanel, 0, 0, 1, 1, insets, new JLabel("Ruta side: ", SwingConstants.LEFT));
		putGridCell(versionPanel, 0, 1, 1, 1, insets, new JScrollPane(sideList));
		putGridCell(versionPanel, 1, 0, 1, 1, null, new JLabel("Ruta Client version: ", SwingConstants.LEFT));
		putGridCell(versionPanel, 1, 1, 1, 1, null, versionField);
		putGridCell(versionPanel, 2, 0, 1, 1, null, new JLabel("JAXB version: ", SwingConstants.LEFT));
		putGridCell(versionPanel, 2, 1, 1, 1, null, jaxbField);
		putGridCell(versionPanel, 3, 0, 1, 1, null, new JLabel("Weblink: ", SwingConstants.LEFT));
		putGridCell(versionPanel, 3, 1, 1, 1, null, linkField);

		return versionPanel;
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
		panel.add(comp, con);
	}

}
