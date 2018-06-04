package rs.ruta.client.correspondence;

import java.awt.Color;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.helger.commons.error.list.IErrorList;
import com.helger.ubl21.UBL21Validator;

import oasis.names.specification.ubl.schema.xsd.catalogue_21.CatalogueType;
import rs.ruta.client.BusinessParty;
import rs.ruta.client.Catalogue;
import rs.ruta.client.MyParty;
import rs.ruta.client.RutaClient;
import rs.ruta.client.gui.RutaClientFrame;
import rs.ruta.common.DocumentReference;

@XmlRootElement(name = "ProduceCatalogueState")
public class ProduceCatalogueState extends CreateCatalogueProcessState
{
	private static final CreateCatalogueProcessState INSTANCE = new ProduceCatalogueState();

	public static CreateCatalogueProcessState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void doActivity(Correspondence correspondence) throws StateActivityException
	{
		final RutaProcess process = (RutaProcess) correspondence.getState();
		final RutaClient client = process.getClient();
		final RutaClientFrame clientFrame = client.getClientFrame();
		clientFrame.appendToConsole(new StringBuilder("Collecting data and producing My Catalogue..."), Color.BLACK);
		final CatalogueType catalogue = client.getMyParty().produceCatalogue(client.getCDRParty());
		if(catalogue == null)
			throw new StateActivityException("My Catalogue is malformed. UBL validation has failed.");
		else
			saveCatalogue(correspondence, catalogue);
		changeState(process, DistributeCatalogueState.getInstance());
	}

	/**
	 * Sets Catalogue of the process, adds it's {@link DocumentReference} to the correspondence.
	 * @param correspondence which catalogue is part of
	 * @param catalogue catalogue to save
	 */
	private void saveCatalogue(Correspondence correspondence, CatalogueType catalogue)
	{
		final CatalogueProcess process = (CatalogueProcess) correspondence.getState();
		process.setCatalogue(catalogue);
		correspondence.addDocumentReference(catalogue.getProviderParty(),
				catalogue.getUUIDValue(), catalogue.getIDValue(),
				catalogue.getIssueDateValue(), catalogue.getIssueTimeValue(),
				catalogue.getClass().getName(), DocumentReference.Status.UBL_VALID);
		correspondence.setRecentlyUpdated(true);
	}
}