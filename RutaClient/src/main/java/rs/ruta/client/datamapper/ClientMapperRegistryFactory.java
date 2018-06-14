package rs.ruta.client.datamapper;

import rs.ruta.common.datamapper.AbstractMapperRegistryFactory;
import rs.ruta.common.datamapper.ExistConnector;
import rs.ruta.common.datamapper.MapperRegistry;

/**
 * Factory class for instantiating appropriate client side objects during initialization
 * of the client side {@link MapperRegistry}.
 */
public class ClientMapperRegistryFactory extends AbstractMapperRegistryFactory
{
	@Override
	protected MapperRegistry createMapperRegistry()
	{
		return new ClientMapperRegistry();
	}

	@Override
	protected ExistConnector createExistConnector()
	{
		return new LocalExistConnector();
	}

}
