package rs.ruta.client;

import java.awt.*;

import javax.swing.*;
import javax.swing.table.*;

public class PartyDialog extends JDialog
{
	private static final long serialVersionUID = 8652433075065940074L;
	private Party party;
	private boolean changed; // table content has changed
	private AbstractTableModel partyModel;

	public PartyDialog(ClientFrame owner)
	{
		super(owner, true);
		partyModel = new PartyTableModel();
		changed = false;
		party = null;

		setSize(500, 500);
		setLocationRelativeTo(owner);

		JPanel partyPanel = new JPanel();
		JTable table = new PartyTable(partyModel);

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

//		TableCellRenderer renderer = new PartyTableCellRenderer();
//		table.setDefaultRenderer(Object.class, renderer);

		JPanel buttonPanel = new JPanel();

		JButton okButton = new JButton("OK");
		buttonPanel.add(okButton);
		okButton.addActionListener(event ->
		{
			changed = ((PartyTable)table).hasChanged();
			setVisible(false);
		});

		JButton cancelButton = new JButton("Cancel");
		buttonPanel.add(cancelButton);
		cancelButton.addActionListener(event ->
		{
			//PartyDialog.this.setVisible(false);
			setVisible(false);
		});

		add(buttonPanel, BorderLayout.SOUTH);
	}

	public void setParty(Party party)
	{
		this.party = party;
		((PartyTableModel)partyModel).setParty(party);
	}

	public Party getParty()
	{
/*		if(party.getPartyID() == null) //ensuring that party object always has a nonempty identification field
			party.setPartyID("");*/
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
	public void setChanged(boolean c)
	{
		changed = c;
	}
}
