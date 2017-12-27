package rs.ruta.client;

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
	public final static QName _MyParty_QNAME = new QName("urn:rs:ruta:client", "MyParty");

	/**Creates an instance of {@link ExistTransaction}.
     * @return created {@code ExistTransaction} object and never {@code null}
     */
    @Nonnull
    public MyParty createMyParty()
    {
        return new MyParty();
    }

    /** Creates an instance of {@link JAXBElement }{@code <}{@link MyParty }{@code >}.
    * @return created JAXBElement and never {@code null}
    */
   @XmlElementDecl(namespace = "urn:rs:ruta:client", name = "MyParty")
   @Nonnull
   public JAXBElement<MyParty> createExistTransaction(@Nullable final MyParty value)
   {
       return new JAXBElement<MyParty>(_MyParty_QNAME, MyParty.class, null, value);
   }

}
