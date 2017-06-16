package rs.ruta.client.datamapper;

import java.util.ArrayList;

public interface DataMapper
{
	public ArrayList<?> findAll();

	public void insertAll();

	public void closeConnection();



}
