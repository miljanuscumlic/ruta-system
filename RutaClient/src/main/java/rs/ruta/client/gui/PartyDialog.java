package rs.ruta.client.gui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.table.*;

import rs.ruta.client.Party;

public class PartyDialog extends JDialog
{
	private static final long serialVersionUID = 8652433075065940074L;
	private Party party;
	/**
	 * True when table content has changed.
	 */
	private boolean changed;
	private DefaultTableModel partyTableModel;
	private JTable partyTable;

	/**
	 * Constructs the dialog for displaying and/or changing the data of a {@link Party}.
	 * @param owner parent frame
	 * @param editable true when {@code Party} data are editable
	 * @param registration true when dialog is shown during local database registration process
	 */
	public PartyDialog(RutaClientFrame owner, boolean editable, boolean registration)
	{
		super(owner, true);
		partyTableModel = new PartyTableModel(editable);
		changed = false;
		party = null;

		setSize(500, 500);
		setLocationRelativeTo(owner);

		JPanel partyPanel = new JPanel();
		partyTable = new PartyTable(partyTableModel);
//		partyTable.setDefaultEditor(Object.class, new FocusLostTableCellEditor(new FocusLostTableCell()));// doesn't work MMM: WHY???

		partyTable.setFillsViewportHeight(true);
		partyTable.getTableHeader().setReorderingAllowed(false); //disables column reordering
		partyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		partyPanel.add(new JScrollPane(partyTable));
		add(partyPanel, BorderLayout.CENTER);

		//specifing preferred column sizes
		TableColumnModel tableColumnModel = partyTable.getColumnModel();
		TableColumn tableColumn = tableColumnModel.getColumn(0);
		tableColumn.setResizable(false);
		tableColumn.setMinWidth(160);
		tableColumn.setPreferredWidth(160);
		tableColumn.setMaxWidth(180);

		MouseAdapter tableLostFocus = new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				stopEditing();
			}
		};
		partyTable.getTableHeader().addMouseListener(tableLostFocus);
		addMouseListener(tableLostFocus);

//		TableCellRenderer renderer = new PartyTableCellRenderer();
//		table.setDefaultRenderer(Object.class, renderer);

		JPanel buttonPanel = new JPanel();

		JButton okButton = new JButton("OK");
		okButton.addActionListener(event ->
		{
			stopEditing();
			String missingField = party.verifyParty();
			if(missingField == null)
			{
				changed = ((PartyTable) partyTable).hasChanged();
				setVisible(false);
			}
			else
				JOptionPane.showMessageDialog(PartyDialog.this, missingField + " field is mandatory.",
						"Error: Missing mandatory field", JOptionPane.ERROR_MESSAGE);
		});
		if(editable)
		{
			buttonPanel.add(okButton);
			getRootPane().setDefaultButton(okButton);
			okButton.requestFocusInWindow();
		}

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(event ->
		{
			if(registration)
				EventQueue.invokeLater( () ->
				JOptionPane.showMessageDialog(this,
						"Entering My Party data is mandatory step during application setup."));
			else
				setVisible(false);
		});

		if(!editable)
		{
			getRootPane().setDefaultButton(cancelButton);
			cancelButton.requestFocusInWindow();
			cancelButton.setText("Close");

		}
		cancelButton.setVerifyInputWhenFocusTarget(false); //do not verify previously focused element when Cancel is clicked
		buttonPanel.add(cancelButton);
		add(buttonPanel, BorderLayout.SOUTH);
	}

	public void setParty(Party party)
	{
		this.party = party;
		((PartyTableModel) partyTableModel).setParty(party);
	}

	public Party getParty()
	{
		return party;
	}

	/**
	 * Tests whether there has been a change in the data model of the {@link Party}.
	 * @return true if there was an alternation of the data in the dialog, false otherwise
	 */
	public boolean isChanged()
	{
		return changed;
	}

	/**
	 * Sets the {@code change} field of the Party dialog denoting whether there has been a change
	 * in the data model of the {@link Party}.
	 * @param changed true when the alternation of the data in the dialog has been made
	 */
	public void setChanged(boolean changed)
	{
		this.changed = changed;
	}

	private void stopEditing()
	{
		if(partyTable.isEditing())
			partyTable.getCellEditor().stopCellEditing();
	}
}
