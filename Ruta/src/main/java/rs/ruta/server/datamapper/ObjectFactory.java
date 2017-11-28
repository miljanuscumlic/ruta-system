package rs.ruta.server.datamapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**{@code ObjectFactory} is a helper class in the process of mapping objects to {@code XML} elements.
 * {@code ObjectFactory} has two methods {@code createXxx} for every class which objects it is mapping.
 * One method is for instantiating the object, and the other is for instantiating the {@link JAXBElement}
 * that is an representation of the XML element to which it is mapped.
 */
@XmlRegistry
public class ObjectFactory
{
	public final static QName _ExistTransaction_QNAME = new QName("urn:rs:ruta:services", "ExistTransaction");
	public final static QName _PartyID_QNAME = new QName("urn:rs:ruta:services", "PartyID");
	public final static QName _User_QNAME = new QName("urn:rs:ruta:services", "User");

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

   /**Creates an instance of {@link PartyID}.
    * @return created {@code PartyID} object and never {@code null}
    */
   @Nonnull
   public PartyID createPartyID()
   {
       return new PartyID();
   }

   /** Creates an instance of {@link JAXBElement }{@code <}{@link PartyID }{@code >}.
   * @return created JAXBElement and never {@code null}
   */
  @XmlElementDecl(namespace = "urn:rs:ruta:services", name = "PartyID")
  @Nonnull
  public JAXBElement<PartyID> createPartyID(@Nullable final PartyID value)
  {
      return new JAXBElement<PartyID>(_PartyID_QNAME, PartyID.class, null, value);
  }

  /**Creates an instance of {@link User}.
   * @return created {@code User} object and never {@code null}
   */
  @Nonnull
  public User createUser()
  {
	  return new User();
  }

  /** Creates an instance of {@link JAXBElement }{@code <}{@link User }{@code >}.
   * @return created JAXBElement and never {@code null}
   */
  @XmlElementDecl(namespace = "urn:rs:ruta:services", name = "User")
  @Nonnull
  public JAXBElement<User> createUser(@Nullable final User value)
  {
	  return new JAXBElement<User>(_User_QNAME, User.class, null, value);
  }

}
