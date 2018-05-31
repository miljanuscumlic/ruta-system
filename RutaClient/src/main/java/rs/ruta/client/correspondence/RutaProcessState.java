package rs.ruta.client.correspondence;

/**
 * Interface encapsulating a state in the {@link RutaProcess} state machine.
 */
public interface RutaProcessState
{
	/**
	 * Changes state of the process by switching to a new one.
	 * @param process process which this state belongs to
	 * @param state next state
	 */
	default public void changeState(final RutaProcess process, final RutaProcessState state)
	{
		process.changeState(state);
	}

	/**
	 * Does activity pertaining the {@link RutaProcess state machine} being in this state.
	 * @param correspondence {@link Correspondence} which process this state belongs to
	 * @throws StateActivityException if this method is invoked for the {@link RutaProcessState state}
	 * that is not the one that the state machine should transition to, or if due some kind of error
	 */
	default public void doActivity(final Correspondence correspondence) throws StateActivityException
	{
		throw new StateActivityException();
	}

}