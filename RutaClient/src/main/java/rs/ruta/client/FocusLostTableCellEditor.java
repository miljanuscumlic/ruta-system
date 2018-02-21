package rs.ruta.client;

import javax.swing.*;

/**
 *{@link DefaultCellEditor Table cell editor} that enables the cell data to be saved on
 *losing the focus on some of its constituent cells by clicking on some other GUI component.
 */
public class FocusLostTableCellEditor extends DefaultCellEditor
{
	private static final long serialVersionUID = -2401407150721477314L;

	public FocusLostTableCellEditor(JTextField textField)
	{
		super(textField);
		((FocusLostTableCell) textField).setTableEditor(this);
	}

}
