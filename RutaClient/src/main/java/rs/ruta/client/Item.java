package rs.ruta.client;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlType;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.ItemType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PriceType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.IDType;

/**
 *Wrapper class for {@link PartyType} with additional convinient methods.
 */
@XmlRootElement(name = "Item", namespace = "urn:rs:ruta:client")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Item")
public class Item extends ItemType
{
	@XmlElement(name = "ID")
	private IDType id;
	@XmlElement(name = "Price")
	private PriceType price;

	public Item() { super(); }

	public Item(ItemType item)
	{
		item.cloneTo(this);
	}

	@Override
	public Item clone()
	{
		Item ret = new Item();
		super.cloneTo(ret);
		return ret;
	}

	public PriceType getPrice()
	{
		return price;
	}

	public void setPrice(PriceType price)
	{
		this.price = price;
	}

	public IDType getID()
	{
		return id;
	}

	public void setID(IDType id)
	{
		this.id = id;
	}

	/**
	 * Sets a new value for an {@link IDType ID} field. If ID is {@code null} it creates a new object.
	 * @param value value to set
	 * @return ID object
	 */
	public IDType setID(@Nullable final String value)
	{
		if(id == null)
			id = new IDType(value);
		else
			id.setValue(value);
		return id;
	}




}
