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
import rs.ruta.common.datamapper.DetailException;

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
		clientFrame.appendToConsole(new StringBuilder(Messages.getString("ProduceCatalogueState.0")), Color.BLACK); //$NON-NLS-1$
		final CatalogueType catalogue = client.getMyParty().produceCatalogue(client.getCDRParty());
		if(catalogue == null)
			throw new StateActivityException(Messages.getString("ProduceCatalogueState.1")); //$NON-NLS-1$
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
		try
		{
			correspondence.addDocumentReference(catalogue, DocumentReference.Status.UBL_VALID);
			/*correspondence.addDocumentReference(catalogue.getProviderParty(),
					catalogue.getUUIDValue(), catalogue.getIDValue(),
					catalogue.getIssueDateValue(), catalogue.getIssueTimeValue(),
					catalogue.getClass().getName(), DocumentReference.Status.UBL_VALID);*/
			correspondence.setRecentlyUpdated(true);
		}
		catch (DetailException e)
		{
			throw new StateActivityException(e.getMessage());
		}
	}
}