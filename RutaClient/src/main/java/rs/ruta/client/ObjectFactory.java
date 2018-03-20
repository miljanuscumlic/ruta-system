package rs.ruta.client;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;

/**
 * {@code ObjectFactory} is a helper class in the process of mapping objects to {@code XML} elements.
 * {@code ObjectFactory} has two methods {@code createXxx} for every class which objects it is mapping.
 * One method is for instantiating the object, and the other is for instantiating the {@link JAXBElement}
 * that is an representation of the XML element to which it is mapped.
 */
@XmlRegistry
public class ObjectFactory
{
	public final static QName _MyParty_QNAME = new QName("urn:rs:ruta:client", "MyParty");
	public final static QName _BusinessParty_QNAME = new QName("urn:rs:ruta:client", "BusinessParty");
	public final static QName _PartySearch_QNAME = new QName("urn:rs:ruta:client", "PartySearch");
	public final static QName _CatalogueSearch_QNAME = new QName("urn:rs:ruta:client", "CatalogueSearch");
	public final static QName _Item_QNAME = new QName("urn:rs:ruta:client", "Item");

	/**
	 * Creates an instance of {@link MyParty}.
	 * @return created {@code MyParty} object and never {@code null}
	 */
	@Nonnull
	public MyParty createMyParty()
	{
		return new MyParty();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link MyParty }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "urn:rs:ruta:client", name = "MyParty")
	@Nonnull
	public JAXBElement<MyParty> createMyParty(@Nullable final MyParty value)
	{
		return new JAXBElement<MyParty>(_MyParty_QNAME, MyParty.class, null, value);
	}

	/**
	 * Creates an instance of {@link BusinessParty}.
	 * @return created {@code BusinessParty} object and never {@code null}
	 */
	@Nonnull
	public BusinessParty createBusinessParty()
	{
		return new BusinessParty();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link BusinessParty }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "urn:rs:ruta:client", name = "BusinessParty")
	@Nonnull
	public JAXBElement<BusinessParty> createBusinessParty(@Nullable final BusinessParty value)
	{
		return new JAXBElement<BusinessParty>(_BusinessParty_QNAME, BusinessParty.class, null, value);
	}

	/**
	 * Creates an instance of {@link PartySearch}.
	 * @return created {@code PartySearch} object and never {@code null}
	 */
	@Nonnull
	public PartySearch createPartySearch()
	{
		return new PartySearch();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link PartySearch }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "urn:rs:ruta:client", name = "PartySearch")
	@Nonnull
	public JAXBElement<PartySearch> createPartySearch(@Nullable final PartySearch value)
	{
		return new JAXBElement<PartySearch>(_PartySearch_QNAME, PartySearch.class, null, value);
	}

	/**
	 * Creates an instance of {@link CatalogueSearch}.
	 * @return created {@code CatalogueSearch} object and never {@code null}
	 */
	@Nonnull
	public CatalogueSearch createCatalogueSearch()
	{
		return new CatalogueSearch();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link CatalogueSearch }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "urn:rs:ruta:client", name = "CatalogueSearch")
	@Nonnull
	public JAXBElement<CatalogueSearch> createCatalogueSearch(@Nullable final CatalogueSearch value)
	{
		return new JAXBElement<CatalogueSearch>(_CatalogueSearch_QNAME, CatalogueSearch.class, null, value);
	}

	/**
	 * Creates an instance of {@link Item}.
	 * @return created {@code Item} object and never {@code null}
	 */
	@Nonnull
	public Item createItem()
	{
		return new Item();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link Item }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "urn:rs:ruta:client", name = "Item")
	@Nonnull
	public JAXBElement<Item> createItem(@Nullable final Item value)
	{
		return new JAXBElement<Item>(_Item_QNAME, Item.class, null, value);
	}

}