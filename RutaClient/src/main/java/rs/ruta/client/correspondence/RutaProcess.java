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
@XmlType(name = "RutaProcess", namespace = "urn:rs:ruta:client")
@XmlAccessorType(XmlAccessType.NONE)
public abstract class RutaProcess
{
	@XmlAnyElement(lax = true)
	protected RutaProcessState state;
	@XmlElement
	IDType id;
	RutaClient client;

	/**
	 * Gets current state of the process.
	 * @return current state
	 */
	public RutaProcessState getState()
	{
		return state;
	}

	/**
	 * Sets the state of a process
	 * @param state state to be set
	 */
	public void setState(RutaProcessState state)
	{
		this.state = state;
	}

	/**
	 * Changes state of a process
	 * @param state state to be transitioned to
	 */
	protected void changeState(RutaProcessState state)
	{
		setState(state);
	}

	public IDType getId()
	{
		return id;
	}

	public String getIdValue()
	{
		return id.getValue();
	}

	public void setId(IDType id)
	{
		this.id = id;
	}

	public void setId(String value)
	{
		if(id == null)
			id = new IDType();
		id.setValue(value);
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
