package rs.ruta.client.correspondence;

import java.util.concurrent.Future;

import oasis.names.specification.ubl.schema.xsd.catalogue_21.CatalogueType;

/**
 * Class encapsulating all types of document processes in regard with the {@link CatalogueType} document.
 * All methods declared by this class have default behaviour of throwing {@link StateActivityException}
 * and its subclasses redefine those methods that should have non-default behaviour.
 */
public class CatalogueProcess extends DocumentProcess
{
	protected CatalogueType catalogue;
	protected Future<?> future;

	public CatalogueType getCatalogue()
	{
		return catalogue;
	}

	public void setCatalogue(CatalogueType catalogue)
	{
		this.catalogue = catalogue;
	}

	public Future<?> getFuture()
	{
		return future;
	}

	public void setFuture(Future<?> future)
	{
		this.future = future;
	}

}