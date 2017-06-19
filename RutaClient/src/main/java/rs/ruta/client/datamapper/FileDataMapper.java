package rs.ruta.client.datamapper;

import java.io.*;
import java.util.*;

public abstract class FileDataMapper<T> implements DataMapper
{
	protected String filename;
	protected Map<Long, T> loadedMap = new HashMap<Long, T>();

	public FileDataMapper(String filename)
	{
		this.filename = filename;
	}

	protected String getFileName() { return filename; }

//	protected ObjectInputStream input;
//	protected ObjectOutputStream output;

/*	public FileDataMapper(String fileName)
	{
		Path path = Paths.get(fileName);
		try
		{
			if (! Files.exists(path))
				Files.createFile(path);
			output = new ObjectOutputStream(new FileOutputStream(fileName));
			input = new ObjectInputStream(new FileInputStream(fileName));
		} catch (FileNotFoundException e)
		{	// should never get here
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}*/

	@Override
	public void closeConnection()
	{
/*		try
		{
			input.close();
			output.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}*/
	}

	/**Reads long number from the store.
	 * @return read long number
	 */
	public Long findLong()
	{
		Long result = null;
		String fileName = getFileName();

		try(ObjectInputStream input = new ObjectInputStream(new FileInputStream(fileName)))
		{
			result = (Long) input.readObject();
		}
		catch(FileNotFoundException e)
		{
			return result;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return result;

/*		Long result = null;
		try
		{
			result = (Long) input.readObject();
		} catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		return result;*/
	}

	@Override
	public ArrayList<?> findAll()
	{
		ArrayList<T> result = null;

		try(ObjectInputStream input = new ObjectInputStream(new FileInputStream(filename)))
		{
//			input.readObject();//skipping catalogue Id number because it is already read - not so good technique
			loadedMap.clear();
			Object elem = null;
			ArrayList<Object> list = new ArrayList<Object>();
			try
			{
				while((elem = input.readObject()) != null)
					list.add(elem);
			}
			catch(EOFException e)
			{
				if(list.size() != 0)
					result = (ArrayList<T>) loadAll(list);
			}
		}
		catch (FileNotFoundException | EOFException e)
		{
			return result;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return result;

/*		ArrayList<T> result = null;

		try
		{
			loadedMap.clear();
			Object elem = null;
			ArrayList<Object> list = new ArrayList<Object>();
			try
			{
				while((elem = input.readObject()) != null)
					list.add(elem);
			}
			catch(EOFException e)
			{
				if(list.size() != 0)
					result = (ArrayList<T>) loadAll(list);
				else
					result = null;
			}
		}
		catch (IOException e)
		{
			return result;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return result;*/
	}

	/**Searches for the object in the cache map. If not found, the proper domain model object is constructed from it and put in the map.
	 * @param element input object that should be placed in the cache map
	 * @return object of the proper domain model type
	 */
	protected T load(Object element)
	{
		long mapID = getMapID(element);
		if (loadedMap.containsKey(mapID))
			return loadedMap.get(mapID);
		T result = doLoad(mapID, element);
		loadedMap.put(mapID, result);
		return result;
		}

	abstract protected long getMapID(Object element);

	/**Loads data from the input object to the domain model object.
	 * @param id id of the object from the cache map of loaded objects
	 * @param element object to be processed
	 * @return domain model object
	 */
	abstract protected T doLoad(long id, Object element);

	/**Loads objects from the list to the cache map of objects of the proper domain model type.
	 * @param elements list of objects to be processed
	 * @return list of domain model objects of the proper domain model type
	 */
	protected ArrayList<T> loadAll(ArrayList<Object> elements)
	{
		ArrayList<T> result = new ArrayList<T>();
		for(Object el: elements)
			result.add(load(el));
		return result;
	}

	public void insertAll(ArrayList<?> elements) // MMM: not called from anywhere
	{
		String fileName = getFileName();

		try(ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(fileName)))
		{
			output.writeObject(elements); // writing data to the file
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

//	abstract protected String findStatement();

/*	protected Object abstractFind(Long id)
	{
		Object result = (Object) loadedMap.get(id);
		if (result != null) return result;
		PreparedStatement findStatement = null;
		try {
			findStatement = DB.prepare(findStatement());
			findStatement.setLong(1, id.longValue());
			ResultSet rs = findStatement.executeQuery();
			rs.next();
			result = load(rs);
			return result;
		} catch (SQLException e) {
			throw new ApplicationException(e);
		} finally {
			DB.cleanUp(findStatement);
		}
	}*/

	/*	protected Object load(ResultSet rs) throws SQLException
	{
		Long id = new Long(rs.getLong(1));
		if (loadedMap.containsKey(id)) return (Object) loadedMap.get(id);
		Object result = doLoad(id, rs);
		loadedMap.put(id, result);
		return result;
	}

	abstract protected Object doLoad(Long id, ResultSet rs) throws SQLException;

	protected List loadAll(ResultSet rs) throws SQLException
	{
		List result = new ArrayList();
		while (rs.next())
		result.add(load(rs));
		return result;
	}

*/


}
