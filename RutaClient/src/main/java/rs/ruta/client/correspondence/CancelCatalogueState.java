package rs.ruta.client.correspondence;

import javax.xml.bind.annotation.XmlRootElement;

import rs.ruta.client.MyParty;

@XmlRootElement(name = "CancelCatalogueState")
public class CancelCatalogueState extends DeleteCatalogueProcessState
{
	private static CancelCatalogueState INSTANCE = new CancelCatalogueState();

	public static CancelCatalogueState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void doActivity(Correspondence correspondence)
	{
		final DeleteCatalogueProcess process = (DeleteCatalogueProcess) correspondence.getState();
		final MyParty myParty = process.getClient().getMyParty();
		myParty.cancelCatalogue();
		changeState(process, EndOfProcessState.getInstance());
	}
}