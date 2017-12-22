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
 * that is an representation of the XML element to which it is mapped.
 */
@XmlRegistry
public class ObjectFactory
{
	public final static QName _ExistTransaction_QNAME = new QName("urn:rs:ruta:services", "ExistTransaction");

	/**Creates an instance of {@link ExistTransaction}.
     * @return created {@code ExistTransaction} object and never {@code null}
     */
    @Nonnull
    public ExistTransaction createExistTransaction()
    {
        return new ExistTransaction();
    }

    /** Creates an instance of {@link JAXBElement }{@code <}{@link ExistTransaction }{@code >}.
    * @return created JAXBElement and never {@code null}
    */
   @XmlElementDecl(namespace = "urn:rs:ruta:services", name = "ExistTransaction")
   @Nonnull
   public JAXBElement<ExistTransaction> createExistTransaction(@Nullable final ExistTransaction value)
   {
       return new JAXBElement<ExistTransaction>(_ExistTransaction_QNAME, ExistTransaction.class, null, value);
   }

}
