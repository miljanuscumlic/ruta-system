package rs.ruta.server;

import javax.jws.*;

import oasis.names.specification.ubl.schema.xsd.catalogue_2.CatalogueType;

@WebService
public interface Server
{
	@WebMethod
	public void putDocument(CatalogueType d);

	@WebMethod
	public CatalogueType getDocument();

}
