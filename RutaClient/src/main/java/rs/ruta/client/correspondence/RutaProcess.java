package rs.ruta.client.correspondence;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.IDType;
import rs.ruta.client.RutaClient;

/**
 * Abstract class describing state machine as a process. Process can be something like an
 * {@code UBL} {@link DocumentProcess} or {@link Correspondence} between parties in the {@code Ruta System}.
 */
@XmlType(name = "RutaProcess")
@XmlAccessorType(XmlAccessType.NONE)
public abstract class RutaProcess
{
	@XmlAnyElement(lax = true)
	protected RutaProcessState state;
	@XmlElement(name = "UUID")
	protected IDType uuid;
	/**
	 * True when the process is in the middle of its execution.
	 */
	@XmlElement(name = "Active")
	protected boolean active;
	protected RutaClient client;

	/**
	 * Gets current state of the process.
	 * @return current state
	 */
	public RutaProcessState getState()
	{
		return state;
	}

	/**
	 * Sets the state of a process.
	 * @param state state to be set
	 */
	public void setState(RutaProcessState state)
	{
		this.state = state;
	}

	/**
	 * Changes state of a process.
	 * @param state state to be transitioned to
	 */
	protected void changeState(RutaProcessState state)
	{
		setState(state);
	}

	/**
	 * Checks whether the process is in the middle of its execution.
	 * @return true if active
	 */
	public boolean isActive()
	{
		return active;
	}

	public void setActive(boolean active)
	{
		this.active = active;
	}

	/**
	 * Gets UUID as a {@link IDType} or {@code null} if UUID is not set.
	 * @return
	 */
	public IDType getId()
	{
		return uuid;
	}

	/**
	 * Gets UUID as a {@code String} or {@code null} if UUID is not set.
	 * @return
	 */
	public String getIdValue()
	{
		return uuid != null ? uuid.getValue() : null;
	}

	public void setId(IDType id)
	{
		this.uuid = id;
	}

	public void setId(String value)
	{
		if(uuid == null)
			uuid = new IDType();
		uuid.setValue(value);
	}

	/**
	 * Gets the {@link RutaClient} of application.
	 * @return {@link RutaClient} object
	 */
	public RutaClient getClient()
	{
		return client;
	}

	/**
	 * Sets the {@link RutaClient} of application.
	 * @param client {@link RutaClient} object to set
	 */
	public void setClient(RutaClient client)
	{
		this.client = client;
	}

}
