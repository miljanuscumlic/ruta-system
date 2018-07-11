package rs.ruta.client.correspondence;

import java.awt.EventQueue;
import java.util.concurrent.Semaphore;

import javax.swing.JOptionPane;
import javax.xml.bind.annotation.XmlRootElement;

import rs.ruta.client.gui.RutaClientFrame;

@XmlRootElement(name = "DecideOnActionState")
public class DecideOnActionState extends CreateCatalogueProcessState
{
	private static final DecideOnActionState INSTANCE = new DecideOnActionState();

	public static DecideOnActionState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void doActivity(Correspondence correspondence) throws StateActivityException
	{
		final RutaProcess process = (RutaProcess) correspondence.getState();
		final RutaClientFrame clientFrame = process.getClient().getClientFrame();
		final Semaphore decision = new Semaphore(0);
		EventQueue.invokeLater(() ->
		{
			int option = JOptionPane.showConfirmDialog(clientFrame,
					Messages.getString("DecideOnActionState.0"), //$NON-NLS-1$
					Messages.getString("DecideOnActionState.1"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE); //$NON-NLS-1$
			if(option == JOptionPane.YES_OPTION)
				changeState(process, PrepareCatalogueState.getInstance());
			else
				changeState(process, ClosingState.getInstance());
			decision.release();
		});

		try
		{
			decision.acquire();
		}
		catch (InterruptedException e)
		{
			throw new StateActivityException(Messages.getString("DecideOnActionState.2"), e); //$NON-NLS-1$
		}
	}
}