package rs.ruta.client;

import javax.swing.*;

public class PartyTableDateCellEditor extends PartyTableCellEditor
{
	private static final long serialVersionUID = -2781237382224053830L;
	private InputVerifier verifier;

	public PartyTableDateCellEditor(InputVerifier verifier)
	{
		super();
		this.verifier = verifier;
		getComponent().setInputVerifier(verifier);
	}

	@Override
	public boolean stopCellEditing()
	{
		if (!verifier.shouldYieldFocus(getComponent()))
			return false;
		return super.stopCellEditing();
	}
}
