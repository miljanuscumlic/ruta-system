package rs.ruta.client.correspondence;

import java.util.UUID;

import javax.xml.bind.annotation.XmlRootElement;

import oasis.names.specification.ubl.schema.xsd.cataloguedeletion_21.CatalogueDeletionType;
import rs.ruta.client.RutaClient;

/**
 * Class encapsulating {@link Correspondence} between parties when exchanging {@link CatalogueType}
 * and {@link CatalogueDeletionType} {@code UBL} business documents. In {@code Ruta system} this
 * correspondence is made between a {@link PartyType party} which is a user of the system and a
 * {@code Central Data Repository} as another party in communication.
 */
@XmlRootElement(name = "CatalogueCorrespondence", namespace = "urn:rs:ruta:client")
public class CatalogueCorrespondence extends Correspondence
{
/*	public CatalogueCorrespondence()
	{
		state = CreateCatalogueProcess.newInstance();
	}*/

	/**
	 * Constructs new instance of a {@link CatalogueCorrespondence} and sets its state to
	 * default value and id to a random value.
	 * @return {@code CatalogueCorrespondence}
	 */
	public static CatalogueCorrespondence newInstance(RutaClient client)
	{
		CatalogueCorrespondence corr = new CatalogueCorrespondence();
		corr.setId(UUID.randomUUID().toString());
		corr.setState(CreateCatalogueProcess.newInstance(client));
		corr.setClient(client);
		return corr;
	}

	/**
	 * Invokes execution of the process of creation, validation and distribution of the {@link CatalogueType}
	 * in the {@code Ruta System}.
	 */
	public void executeCreateCatalogueProcess()
	{
		((CatalogueProcess) state).createCatalogue(this);
	}

	/**
	 * Invokes execution of the process of deletion of the {@link CatalogueType} {@code UBL document} from the
	 * {@code Ruta System}.
	 */
	public void executeDeleteCatalogueProcess()
	{
		((CatalogueProcess) state).deleteCatalogue(this);
	}

}