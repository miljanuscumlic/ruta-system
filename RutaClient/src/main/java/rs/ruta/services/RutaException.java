
package rs.ruta.services;

import javax.xml.ws.WebFault;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.10
 * Generated source version: 2.2
 * 
 */
@WebFault(name = "RutaException", targetNamespace = "http://ruta.rs/ns/services")
public class RutaException
    extends Exception
{

    /**
     * Java type that goes as soapenv:Fault detail element.
     * 
     */
    private FaultInfo faultInfo;

    /**
     * 
     * @param faultInfo
     * @param message
     */
    public RutaException(String message, FaultInfo faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @param faultInfo
     * @param cause
     * @param message
     */
    public RutaException(String message, FaultInfo faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @return
     *     returns fault bean: rs.ruta.services.FaultInfo
     */
    public FaultInfo getFaultInfo() {
        return faultInfo;
    }

}
