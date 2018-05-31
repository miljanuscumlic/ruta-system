package rs.ruta.client.correspondence;

/**
 * Class encapsulating all types of document processes that serve as states of the {@link BuyingCorrespondence}
 * state machine.
 * All methods declared by this class have default behaviour of throwing {@link StateActivityException}
 * and its subclasses redefine those methods that should have non-default behaviour.
 */
abstract public class BuyingProcess extends DocumentProcess
{


}