package rs.ruta.common.datamapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

import oasis.names.specification.ubl.schema.xsd.order_21.OrderType;
import rs.ruta.common.PartyID;
import rs.ruta.common.RutaUser;

/**{@code ObjectFactory} is a helper class in the process of mapping objects to {@code XML} elements.
 * {@code ObjectFactory} has two methods {@code createXxx} for every class which objects it is mapping.
 * One method is for instantiating the object, and the other is for instantiating the {@link JAXBElement}
 * that is an representation of the XML element to which it is mapped. These methods are used internally
 * by JAXB.
 */
@XmlRegistry
public class ObjectFactory
{
	public final static QName _DatabaseOperation_QNAME = new QName("http://www.ruta.rs/ns/common", "DatabaseOperation");
	public final static QName _DistributionOperation_QNAME = new QName("http://www.ruta.rs/ns/common", "DistributionOperation");
	public final static QName _DatabaseTransaction_QNAME = new QName("http://www.ruta.rs/ns/common", "DatabaseTransaction");
	public final static QName _DistributionTransaction_QNAME = new QName("http://www.ruta.rs/ns/common", "DistributionTransaction");

	/**Creates an instance of {@link DatabaseOperation}.
	 * @return created {@code DatabaseOperation} object and never {@code null}
	 */
	@Nonnull
	public DatabaseOperation createDatabaseOperation()
	{
		return new DatabaseOperation();
	}

	/** Creates an instance of {@link JAXBElement }{@code <}{@link DatabaseOperation }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/common", name = "DatabaseOperation")
	@Nonnull
	public JAXBElement<DatabaseOperation> createDatabaseOperation(@Nullable final DatabaseOperation value)
	{
		return new JAXBElement<DatabaseOperation>(_DatabaseOperation_QNAME, DatabaseOperation.class, null, value);
	}

	/**Creates an instance of {@link DistributionOperation}.
	 * @return created {@code DistributionOperation} object and never {@code null}
	 */
	@Nonnull
	public DistributionOperation createDistributionOperation()
	{
		return new DistributionOperation();
	}

	/** Creates an instance of {@link JAXBElement }{@code <}{@link DistributionOperation }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/common", name = "DistributionOperation")
	@Nonnull
	public JAXBElement<DistributionOperation> createDistributionOperation(@Nullable final DistributionOperation value)
	{
		return new JAXBElement<DistributionOperation>(_DistributionOperation_QNAME, DistributionOperation.class, null, value);
	}

	/**Creates an instance of {@link DatabaseTransaction}.
	 * @return created {@code DatabaseTransaction} object and never {@code null}
	 */
	@Nonnull
	public DatabaseTransaction createDatabaseTransaction()
	{
		return new DatabaseTransaction();
	}

	/** Creates an instance of {@link JAXBElement }{@code <}{@link DatabaseTransaction }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/common", name = "DatabaseTransaction")
	@Nonnull
	public JAXBElement<DatabaseTransaction> createDatabaseTransaction(@Nullable final DatabaseTransaction value)
	{
		return new JAXBElement<DatabaseTransaction>(_DatabaseTransaction_QNAME, DatabaseTransaction.class, null, value);
	}

	/**Creates an instance of {@link DistributionTransaction}.
	 * @return created {@code DistributionTransaction} object and never {@code null}
	 */
	@Nonnull
	public DistributionTransaction createDistributionTransaction()
	{
		return new DistributionTransaction();
	}

	/** Creates an instance of {@link JAXBElement }{@code <}{@link DistributionTransaction }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/common", name = "DistributionTransaction")
	@Nonnull
	public JAXBElement<DistributionTransaction> createDistributionTransaction(@Nullable final DistributionTransaction value)
	{
		return new JAXBElement<DistributionTransaction>(_DistributionTransaction_QNAME, DistributionTransaction.class, null, value);
	}

}