package rs.ruta.client.correspondence;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

/**
 * Abstract class encapsulating a state in the {@link RutaProcess} state machine.
 */
/*@XmlType(name = "RutaProcessState", namespace = "urn:rs:ruta:client")
//@XmlSeeAlso(CreateCatalogueProcessState.class)
public abstract class RutaProcessState
{
	public void changeState(RutaProcess process, RutaProcessState state)
	{
		process.changeState(state);
	}

}*/

//@XmlType(name = "RutaProcessState", namespace = "urn:rs:ruta:client")
//@XmlSeeAlso(CreateCatalogueProcessState.class)
//@XmlType( factoryClass=ObjectFactory.class, factoryMethod="createRutaProcessState")
public interface RutaProcessState
{
	default public void changeState(RutaProcess process, RutaProcessState state)
	{
		process.changeState(state);
	}

}