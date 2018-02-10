package rs.ruta.common.datamapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

import rs.ruta.common.PartyID;
import rs.ruta.common.User;

/**{@code ObjectFactory} is a helper class in the process of mapping objects to {@code XML} elements.
 * {@code ObjectFactory} has two methods {@code createXxx} for every class which objects it is mapping.
 * One method is for instantiating the object, and the other is for instantiating the {@link JAXBElement}
 * that is an representation of the XML element to which it is mapped. These methods are used internally
 * by JAXB.
 */
@XmlRegistry
public class ObjectFactory
{
	public final static QName _DatabaseTransaction_QNAME = new QName("urn:rs:ruta:services", "DatabaseTransaction");
	public final static QName _DistributionTransaction_QNAME = new QName("urn:rs:ruta:services", "DistributionTransaction");

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
   @XmlElementDecl(namespace = "urn:rs:ruta:services", name = "DatabaseTransaction")
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
  @XmlElementDecl(namespace = "urn:rs:ruta:services", name = "DistributionTransaction")
  @Nonnull
  public JAXBElement<DistributionTransaction> createDistributionTransaction(@Nullable final DistributionTransaction value)
  {
      return new JAXBElement<DistributionTransaction>(_DistributionTransaction_QNAME, DistributionTransaction.class, null, value);
  }

}
