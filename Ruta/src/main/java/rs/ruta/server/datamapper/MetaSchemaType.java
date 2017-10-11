package rs.ruta.server.datamapper;

import org.exist.security.SchemaType;

public enum MetaSchemaType implements SchemaType
{
    SECRET_KEY("http://ruta.rs/party/key", "SecretKey"),
    UNIQUE_ID("http://ruta.rs/party/id", "UniqueID");

    private final String namespace;
    private final String alias;

    MetaSchemaType(final String namespace, final String alias)
    {
        this.namespace = namespace;
        this.alias = alias;
    }

    @Override
    public String getNamespace()
    {
        return namespace;
    }

    @Override
    public String getAlias()
    {
        return alias;
    }

    public static MetaSchemaType valueOfNamespace(final String namespace)
    {
        for(final MetaSchemaType metaSchemaType : MetaSchemaType.values())
            if(metaSchemaType.getNamespace().equals(namespace))
                return metaSchemaType;
        return null;
    }

    public static MetaSchemaType valueOfAlias(final String alias)
    {
        for(final MetaSchemaType metaSchemaType : MetaSchemaType.values())
            if(metaSchemaType.getAlias().equals(alias))
                return metaSchemaType;
        return null;
    }

}
