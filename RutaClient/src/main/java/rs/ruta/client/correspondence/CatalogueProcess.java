package rs.ruta.client.correspondence;

/**
 * Class encapsulating all types of document processes in regard with the {@link CatalogueType} document.
 * All methods declared by this class have default behaviour of throwing {@link StateTransitionException}
 * and its subclasses redefine those methods that should have non-default behaviour.
 */
public class CatalogueProcess extends DocumentProcess
{
	/**
	 * Creates, validates and distributes the {@link CatalogueType} {@code UBL document} in the {@code Ruta System}.
	 * @param correspondence correspondence to which process belongs
	 * @throws StateTransitionException
	 */
	public void createCatalogue(final Correspondence correspondence) throws StateTransitionException
	{
		throw new StateTransitionException();
	}

	/**
	 * Creates, validates and distributes the {@link CatalogueType} {@code UBL document} in the {@code Ruta System}.
	 * @param correspondence correspondence to which process belongs
	 * @throws StateTransitionException
	 */
	public void createCatalogueExecute(final Correspondence correspondence) throws StateTransitionException
	{
		throw new StateTransitionException();
	}

	/**
	 * Deletes {@link CatalogueType} document from the {@code Ruta System}
	 * @param correspondence correspondence to which process belongs
	 * @throws StateTransitionException
	 */
	public void deleteCatalogue(final Correspondence correspondence) throws StateTransitionException
	{
		throw new StateTransitionException();
	}

	/**
	 * Deletes {@link CatalogueType} document from the {@code Ruta System}
	 * @param correspondence correspondence to which process belongs
	 * @throws StateTransitionException
	 */
	public void deleteCatalogueExecute(final Correspondence correspondence) throws StateTransitionException
	{
		throw new StateTransitionException();
	}

	/**
	 * Resolves which processes should be the next one: for deletion of the catalogue, or new creation of the catalogue.
	 * @param correspondence correspondence to which process belongs
	 * @throws StateTransitionException
	 */
	public void resolveNextCatalogueProcess(final Correspondence correspondence) throws StateTransitionException
	{
		throw new StateTransitionException();
	}

}