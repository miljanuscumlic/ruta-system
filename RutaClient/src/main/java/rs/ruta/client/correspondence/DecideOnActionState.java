package rs.ruta.client.correspondence;

import java.awt.EventQueue;

import javax.swing.JOptionPane;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import rs.ruta.client.gui.RutaClientFrame;

@XmlRootElement(name = "DecideOnActionState", namespace = "urn:rs:ruta:client")
//@XmlType(name = "DecideOnActionState", namespace = "urn:rs:ruta:client")
public class DecideOnActionState extends CreateCatalogueProcessState
{
	private static final DecideOnActionState INSTANCE = new DecideOnActionState();

	public static DecideOnActionState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void decideOnAction(final RutaProcess process) throws StateTransitionException
	{
		final RutaClientFrame clientFrame = ((RutaClientFrame) process.getState()).getClient().getClientFrame();
		EventQueue.invokeLater(() ->
		{
			int option = JOptionPane.showConfirmDialog(clientFrame,
					"Catalogue update has been rejected. Would you like to start catalogue update process over again?",
					"Catalogue update rejected by CDR service", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if(option == JOptionPane.YES_OPTION)
				changeState(process, PrepareCatalogueState.getInstance());
			else
				changeState(process, EndOfProcessState.getInstance());
		});
	}

}