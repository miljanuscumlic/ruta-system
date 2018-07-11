package rs.ruta.client.correspondence;

import java.awt.Color;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "BuyerOrderRejectedState")
public class BuyerOrderRejectedState extends BuyerOrderingProcessState
{
	private static BuyerOrderingProcessState INSTANCE = new BuyerOrderRejectedState();

	public static BuyerOrderingProcessState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void doActivity(Correspondence correspondence)
	{
		final BuyerOrderingProcess process = (BuyerOrderingProcess) correspondence.getState();
		correspondence.getClient().getClientFrame().appendToConsole(
				new StringBuilder(Messages.getString("BuyerOrderRejectedState.0") + process.getOrder(correspondence).getIDValue() + //$NON-NLS-1$
						Messages.getString("BuyerOrderRejectedState.1") + correspondence.getCorrespondentPartyName() + Messages.getString("BuyerOrderRejectedState.2")), Color.BLACK); //$NON-NLS-1$ //$NON-NLS-2$
		process.setOrderRejected(true);
		changeState(process, ClosingState.getInstance());
	}
}