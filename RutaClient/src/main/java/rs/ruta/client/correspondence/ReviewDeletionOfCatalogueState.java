package rs.ruta.client.correspondence;

import java.awt.EventQueue;
import java.util.concurrent.Semaphore;

import javax.swing.JOptionPane;
import javax.xml.bind.annotation.XmlRootElement;

import rs.ruta.client.gui.RutaClientFrame;

@XmlRootElement(name = "ReviewDeletionOfCatalogueState")
public class ReviewDeletionOfCatalogueState extends DeleteCatalogueProcessState
{
	private static ReviewDeletionOfCatalogueState INSTANCE = new ReviewDeletionOfCatalogueState();

	public static ReviewDeletionOfCatalogueState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void doActivity(Correspondence correspondence) throws StateActivityException
	{
		final DeleteCatalogueProcess process = (DeleteCatalogueProcess) correspondence.getState();
		final RutaClientFrame clientFrame = process.getClient().getClientFrame();
		final Semaphore decision = new Semaphore(0);
		EventQueue.invokeLater(() ->
		{
			int option = JOptionPane.showConfirmDialog(clientFrame,
					Messages.getString("ReviewDeletionOfCatalogueState.0"), 
					Messages.getString("ReviewDeletionOfCatalogueState.1"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE); 
			if(option == JOptionPane.YES_OPTION)
				changeState(process, NotifyOfCatalogueDeletionState.getInstance());
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
			throw new StateActivityException(Messages.getString("ReviewDeletionOfCatalogueState.2"), e); 
		}
	}
}