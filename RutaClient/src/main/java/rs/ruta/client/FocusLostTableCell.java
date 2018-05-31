package rs.ruta.client;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;

/**
 *{@link JTextField Text field} contained in the cell of the {@link JTable orderLinesTable} that enables the
 *data entered in the cell to be saved in the model after the focus has been lost i.e. has been transfered
 *to some other GUI component.
 */
public class FocusLostTableCell extends JTextField implements FocusListener
{
	private static final long serialVersionUID = 8240204326892117735L;
	private DefaultCellEditor tableEditor;

	public FocusLostTableCell()
	{
		super();
		addFocusListener(this);
	}

	public DefaultCellEditor getTableEditor()
	{
		return tableEditor;
	}

	public void setTableEditor(DefaultCellEditor tableEditor)
	{
		this.tableEditor = tableEditor;
	}

	@Override
	public void focusGained(FocusEvent e) { }

	@Override
	public void focusLost(FocusEvent event)
	{
		tableEditor.stopCellEditing();
	}
}
