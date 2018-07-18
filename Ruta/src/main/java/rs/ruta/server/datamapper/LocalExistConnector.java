package rs.ruta.server.datamapper;

import rs.ruta.common.datamapper.ExistConnector;
import rs.ruta.common.datamapper.DatabaseAdmin;

public class LocalExistConnector extends ExistConnector
{
	public LocalExistConnector()
	{
		DatabaseAdmin.newInstance("admin", null);
		setLocalAPI();
	}
}