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
				new StringBuilder("Order " + process.getOrder(correspondence).getIDValue() + 
						" has been rejected by " + correspondence.getCorrespondentPartyName() + " party."), Color.BLACK);  
		process.setOrderRejected(true);
		changeState(process, ClosingState.getInstance());
	}
}