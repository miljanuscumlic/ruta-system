package rs.ruta.server;

import javax.annotation.*;
import javax.jws.*;
import javax.servlet.ServletContext;
import javax.xml.ws.*;
import javax.xml.ws.handler.MessageContext;

import oasis.names.specification.ubl.schema.xsd.catalogue_2.CatalogueType;

@WebService(endpointInterface = "rs.ruta.server.Server")
@BindingType(javax.xml.ws.soap.SOAPBinding.SOAP12HTTP_BINDING)
public class CDR implements Server
{
	@Resource
	private WebServiceContext wsCtx;
	private ServletContext sCtx;
	private static final Documents documents = new Documents();
	private static final int maxLength = 16;

	@Override
	@WebMethod
	public void putDocument(CatalogueType d)
	{
		init();
		documents.addDocument(d);
	}

	@Override
	@WebMethod
	public CatalogueType getDocument()
	{
		init();
		return documents.removeDocument();
	}

	private void init()
	{
		if (wsCtx == null)
			throw new RuntimeException("DI failed on wsCtx!"); // MMM: ? DI Dependancy Injection
		if (sCtx == null) // ServletContext not yet set?
		{
			MessageContext mCtx = wsCtx.getMessageContext();
			sCtx = (ServletContext) mCtx.get(MessageContext.SERVLET_CONTEXT);
			documents.setServletContext(sCtx);
		}
	}
}
