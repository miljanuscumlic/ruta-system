package rs.ruta.client.correspondence;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Abstract class that serves as a marker class for all correspondences in {@code Ruta System}.
 * Correspondence is a process of exchanging {@code UBL business documents} among parties
 * in other to do the bussiness. One type of a correspondence can be a process of ordering, invocing
 * and paying ordered goods and services.
 */
@XmlRootElement
public abstract class Correspondence extends RutaProcess
{

}
