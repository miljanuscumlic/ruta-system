package rs.ruta.client.correspondence;

/**
 * Class encapsulating all types of document processes that serve as states of the {@link BuyingCorrespondence}
 * state machine.
 * All methods declared by this class have default behaviour of throwing {@link StateTransitionException}
 * and its subclasses redefine those methods that should have non-default behaviour.
 */
public class BuyingProcess extends DocumentProcess
{
	/**
	 * Processes the {@link OrderType} {@code UBL document} in the {@code Ruta System}.
	 * @param correspondence correspondence to which process belongs
	 * @throws StateTransitionException
	 */
	public void ordering(final Correspondence correspondence) throws StateTransitionException
	{
		throw new StateTransitionException();
	}

	/**
	 * Processes the {@link OrderType} {@code UBL document} on the Buyer Party's side.
	 * @param correspondence correspondence to which process belongs
	 * @throws StateTransitionException
	 */
	public void orderingActivity(final Correspondence correspondence) throws StateTransitionException
	{
		throw new StateTransitionException();
	}

	/**
	 * Processes the {@link InvoiceType} document in the {@code Ruta System}.
	 * @param correspondence correspondence to which process belongs
	 * @throws StateTransitionException
	 */
	public void billing(final Correspondence correspondence) throws StateTransitionException
	{
		throw new StateTransitionException();
	}

	/**
	 * Processes {@link PaymentNotificationType} document in the {@code Ruta System}.
	 * @param correspondence correspondence to which process belongs
	 * @throws StateTransitionException
	 */
	public void paymentNotification(final Correspondence correspondence) throws StateTransitionException
	{
		throw new StateTransitionException();
	}

}