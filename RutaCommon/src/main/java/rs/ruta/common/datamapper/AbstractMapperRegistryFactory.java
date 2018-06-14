package rs.ruta.common.datamapper;

/**
 * Abstract class defining interface for factory classes that instantiate
 * appropriate {@link MapperRegistry} subclass.
 */
public abstract class AbstractMapperRegistryFactory
{
	protected abstract MapperRegistry createMapperRegistry();

	protected abstract ExistConnector createExistConnector();

}