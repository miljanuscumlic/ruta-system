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
	public void decideOnAction(final RutaProcess process) throws StateTransitionException
	{
		final RutaClientFrame clientFrame = process.getClient().getClientFrame();
		Semaphore decision = new Semaphore(0);
		EventQueue.invokeLater(() ->
		{
			int option = JOptionPane.showConfirmDialog(clientFrame,
					"Catalogue update has been rejected. Would you like to start catalogue update process over again?",
					"Catalogue update rejected by CDR service", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if(option == JOptionPane.YES_OPTION)
				changeState(process, PrepareCatalogueState.getInstance());
			else
				changeState(process, EndOfProcessState.getInstance());
			decision.release();
		});

		try
		{
			decision.acquire();
		}
		catch (InterruptedException e)
		{
			throw new StateTransitionException("Interrupted execution of Create Catalogue Process!", e);
		}
	}

	@Override
	public void doActivity(Correspondence correspondence, RutaProcess process)
	{
		final RutaClientFrame clientFrame = process.getClient().getClientFrame();
		Semaphore decision = new Semaphore(0);
		EventQueue.invokeLater(() ->
		{
			int option = JOptionPane.showConfirmDialog(clientFrame,
					"Catalogue update has been rejected. Would you like to start catalogue update process over again?",
					"Catalogue update rejected by CDR service", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if(option == JOptionPane.YES_OPTION)
				changeState(process, PrepareCatalogueState.getInstance());
			else
				changeState(process, EndOfProcessState.getInstance());
			decision.release();
		});

		try
		{
			decision.acquire();
		}
		catch (InterruptedException e)
		{
			throw new StateTransitionException("Interrupted execution of Create Catalogue Process!", e);
		}
	}
}