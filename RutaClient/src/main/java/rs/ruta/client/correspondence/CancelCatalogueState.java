package rs.ruta.client.correspondence;

import javax.xml.bind.annotation.XmlRootElement;

import rs.ruta.client.MyParty;

@XmlRootElement(name = "CancelCatalogueState", namespace = "urn:rs:ruta:client")
public class CancelCatalogueState extends DeleteCatalogueProcessState
{

	private static CancelCatalogueState INSTANCE = new CancelCatalogueState();

	public static CancelCatalogueState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void cancelCatalogue(final RutaProcess process) throws StateTransitionException
	{
		final MyParty myParty = process.getClient().getMyParty();
		myParty.setDirtyCatalogue(true);
		myParty.setInsertMyCatalogue(true);
		myParty.removeCatalogueIssueDate();
		changeState(process, EndOfProcessState.getInstance());
	}

}