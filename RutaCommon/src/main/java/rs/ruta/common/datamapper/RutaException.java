package rs.ruta.common.datamapper;

import javax.xml.ws.WebFault;

/**
 * RutaException is the main class of exceptions trasmited among the parties of the Ruta System.
 * Instances of it are thrown when there are issues with the user input data, database connectivity,
 * data transformation on the service's side, etc.
 * <p>WebFault annotation is mandatory so that detail element could be included inside the SOAPFault.</p>
 */

@WebFault(name = "RutaException")
public class RutaException extends Exception
{
	private static final long serialVersionUID = 5805059348235585370L;

	//this is solution with separate class FaultInfo as detail
	private FaultInfo faultInfo;

	public RutaException() { }

	public RutaException(String reason, String detail)
	{
		super(reason);
		faultInfo = new FaultInfo();
		faultInfo.setDetail(detail);
	}

	public RutaException(String reason, FaultInfo faultInfo)
	{
		super(reason);
		this.faultInfo = faultInfo;
	}

	public RutaException(String reason, String detail, Throwable cause)
	{
		super(reason, cause);
		faultInfo = new FaultInfo();
		faultInfo.setDetail(detail);
	}

	public RutaException(String reason, FaultInfo faultInfo, Throwable cause)
	{
		super(reason, cause);
		this.faultInfo = faultInfo;
	}

	public FaultInfo getFaultInfo()
	{
		return faultInfo;
	}

	public void setFaultInfo(FaultInfo faultInfo)
	{
		this.faultInfo = faultInfo;
	}

/*	//this is the solution with only String field faultInfo as a detail
	public RutaException(String reason, String faultInfo)
	{
		super(reason);
		this.faultInfo = faultInfo;
	}

	public RutaException(String reason, String faultInfo, Throwable cause)
	{
		super(reason, cause);
		this.faultInfo = faultInfo;
	}

	public String getFaultInfo()
	{
		return faultInfo;
	}*/

}
