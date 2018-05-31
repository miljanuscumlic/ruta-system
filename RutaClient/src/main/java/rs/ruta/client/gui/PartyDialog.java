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
	 * True when orderLinesTable content has changed.
	 */
	private boolean changed;
	private DefaultTableModel partyTableModel;

	/**
	 * Constructs the dialog for displaying and/or changing the data of a {@link Party}.
	 * @param owner parent frame
	 * @param registration true when dialog is shown during local database registration process
	 */
	public PartyDialog(RutaClientFrame owner, boolean registration)
	{
		super(owner, true);
		partyTableModel = new PartyTableModel();
		changed = false;
		party = null;

		setSize(500, 500);
		setLocationRelativeTo(owner);

		JPanel partyPanel = new JPanel();
		JTable table = new PartyTable(partyTableModel);
//		orderLinesTable.setDefaultEditor(Object.class, new FocusLostTableCellEditor(new FocusLostTableCell()));// doesn't work MMM: WHY???

		table.setFillsViewportHeight(true);
		table.getTableHeader().setReorderingAllowed(false); //disables column reordering
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		partyPanel.add(new JScrollPane(table));
		add(partyPanel, BorderLayout.CENTER);

		//specifing preferred column sizes
		TableColumnModel tableColumnModel = table.getColumnModel();
		TableColumn tableColumn = tableColumnModel.getColumn(0);
		tableColumn.setResizable(false);
		tableColumn.setMinWidth(160);
		tableColumn.setPreferredWidth(160);
		tableColumn.setMaxWidth(180);
		tableColumn = tableColumnModel.getColumn(1);

		MouseAdapter tableLostFocus = new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if(table.isEditing())
					table.getCellEditor().stopCellEditing();
			}
		};
		table.getTableHeader().addMouseListener(tableLostFocus);
		addMouseListener(tableLostFocus);

//		TableCellRenderer renderer = new PartyTableCellRenderer();
//		orderLinesTable.setDefaultRenderer(Object.class, renderer);

		JPanel buttonPanel = new JPanel();

		JButton okButton = new JButton("OK");
		buttonPanel.add(okButton);
		okButton.addActionListener(event ->
		{
			if(table.isEditing())
				table.getCellEditor().stopCellEditing();
			String missingField = party.verifyParty();
			if(missingField == null)
			{
				changed = ((PartyTable) table).hasChanged();
				setVisible(false);
			}
			else
				JOptionPane.showMessageDialog(PartyDialog.this, missingField + " field is mandatory.",
						"Error: Missing mandatory field", JOptionPane.ERROR_MESSAGE);
		});
		//getRootPane().setDefaultButton(okButton); does not work

		JButton cancelButton = new JButton("Cancel");
		cancelButton.setVerifyInputWhenFocusTarget(false);//do not verify previously focused element when Cancel is clicked
		buttonPanel.add(cancelButton);
		cancelButton.addActionListener(event ->
		{
			if(registration)
				EventQueue.invokeLater( () ->
				JOptionPane.showMessageDialog(this,
						"Entering My Party data is mandatory step during application setup."));
			else
				setVisible(false);
		});

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
	 * Gets the {@code change} field of the Party dialog that tells whether there has been a change in the data model
	 * of the {@link Party}.
	 * @return true if there was an alternation of the data in the dialog, false otherwise
	 */
	public boolean isChanged()
	{
		return changed;
	}

	/**
	 * Sets the {@code change} field of the Party dialog. {@code change} field is true when the alternation
	 * of the data in the dialog has been made.
	 */
	public void setChanged(boolean changed)
	{
		this.changed = changed;
	}
}
