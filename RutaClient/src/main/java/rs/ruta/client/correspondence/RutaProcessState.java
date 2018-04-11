package rs.ruta.client.correspondence;

/**
 * Interface encapsulating a state in the {@link RutaProcess} state machine.
 */
//@XmlType(name = "RutaProcessState", namespace = "urn:rs:ruta:client")
//@XmlSeeAlso(CreateCatalogueProcessState.class)
//@XmlType( factoryClass=ObjectFactory.class, factoryMethod="createRutaProcessState")
public interface RutaProcessState
{
	default public void changeState(final RutaProcess process, final RutaProcessState state)
	{
		process.changeState(state);
	}

	default public void endOfProcess(final RutaProcess process)
	{
		throw new StateTransitionException();
	}

}