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
	 * Does activity that is done in the ending state of the process.
	 * @param process process which this state belongs to
	 */
	default public void endOfProcess(final RutaProcess process)
	{
		throw new StateTransitionException();
	}

	/**
	 * Does activity pertaining the {@link RutaProcess state machine} being in this state.
	 * @param correspondence {@link Correspondence} which process of this state belongs to
	 * @throws StateTransitionException if this method is invoked for the wrong {@link RutaProcessState state}
	 * that is not the one that the state machine should transition to or if due some kind of error
	 */
	default public void doActivity(final Correspondence correspondence)
	{
		throw new StateTransitionException();
	}

}