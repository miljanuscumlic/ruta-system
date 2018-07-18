package rs.ruta.server.datamapper;

import rs.ruta.common.datamapper.ExistConnector;
import rs.ruta.common.datamapper.DatabaseAdmin;

public class RemoteExistConnector extends ExistConnector
{
	public RemoteExistConnector()
	{
		DatabaseAdmin.newInstance("admin", null);
	}

}
