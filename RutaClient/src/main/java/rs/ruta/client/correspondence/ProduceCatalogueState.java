package rs.ruta.client.correspondence;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.helger.commons.error.list.IErrorList;
import com.helger.ubl21.UBL21Validator;

import oasis.names.specification.ubl.schema.xsd.catalogue_21.CatalogueType;
import rs.ruta.client.Catalogue;
import rs.ruta.client.MyParty;

@XmlRootElement(name = "ProduceCatalogueState", namespace = "urn:rs:ruta:client")
//@XmlType(name = "ProduceCatalogueState", namespace = "urn:rs:ruta:client")
public class ProduceCatalogueState extends CreateCatalogueProcessState
{
	private static final CreateCatalogueProcessState INSTANCE = new ProduceCatalogueState();

	public static CreateCatalogueProcessState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public CatalogueType produceCatalogue(final RutaProcess process)
	{
		final MyParty myParty = process.getClient().getMyParty();
		final CatalogueType catalogue = myParty.produceCatalogue(process.getClient().getCDRParty());
		if(catalogue == null)
			throw new StateTransitionException("My Catalogue is malformed. UBL validation has failed.");
		changeState(process, DistributeCatalogueState.getInstance());
		return catalogue;
	}

}
