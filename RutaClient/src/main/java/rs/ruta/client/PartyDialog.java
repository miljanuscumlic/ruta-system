package rs.ruta.client;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;
import javax.swing.table.*;

public class PartyDialog extends JDialog
{
	private static final long serialVersionUID = 8652433075065940074L;
	private Party party;
	/**
	 * True when table content has changed.
	 */
	private boolean changed;
	private AbstractTableModel partyTableModel;

	public PartyDialog(ClientFrame owner)
	{
		super(owner, true);
		partyTableModel = new PartyTableModel();
		changed = false;
		party = null;

		setSize(500, 500);
		setLocationRelativeTo(owner);

		JPanel partyPanel = new JPanel();
		JTable table = new PartyTable(partyTableModel);
//		table.setDefaultEditor(Object.class, new FocusLostTableCellEditor(new FocusLostTableCell()));// doesn't work MMM: WHY???

		table.setFillsViewportHeight(true);
		table.getTableHeader().setReorderingAllowed(false); //disables column reordering
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		partyPanel.add(new JScrollPane(table));
		add(partyPanel, BorderLayout.CENTER);

		//specifing preferred column sizes
		TableColumnModel tableColumnModel = table.getColumnModel();
		TableColumn tableColumn = tableColumnModel.getColumn(0);
		tableColumn.setResizable(false);
		tableColumn = tableColumnModel.getColumn(1);
		tableColumn.setPreferredWidth(280);

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
//		table.setDefaultRenderer(Object.class, renderer);

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
				JOptionPane.showMessageDialog(PartyDialog.this, missingField + " is mandatory.",
						"Error: Missing mandatory field", JOptionPane.ERROR_MESSAGE);
		});
		//getRootPane().setDefaultButton(okButton); does not work

		JButton cancelButton = new JButton("Cancel");
		cancelButton.setVerifyInputWhenFocusTarget(false);//do not verify previously focused element when Cancel is clicked
		buttonPanel.add(cancelButton);
		cancelButton.addActionListener(event ->
		{
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
	 * Gets the change filed of the Party dialog. Change field is true if there was a alternation of the data in the dialog.
	 * @return change field
	 */
	public boolean isChanged()
	{
		return changed;
	}

	/**
	 * Sets the change filed of the Party dialog. Change field is true if there was a alternation of the data in the dialog.
	 */
	public void setChanged(boolean changed)
	{
		this.changed = changed;
	}
}
