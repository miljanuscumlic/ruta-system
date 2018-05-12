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
 * Wrapper class for {@link PartyType} with additional convinient methods.
 */
@XmlRootElement(name = "Item")
@XmlType(name = "Item")
@XmlAccessorType(XmlAccessType.NONE)
public class Item extends ItemType

{
	private static final long serialVersionUID = -6958759984921980423L;
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
		cloneTo(ret);
		return ret;
	}

	public void cloneTo(Item item)
	{
		super.cloneTo(item);
		if(id != null)
			item.id = id.clone();
		if(price != null)
			item.price = price.clone();
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
	 * Sets a new value for an {@link IDType ID} field. If ID is {@code null} it creates a new
	 * {@link IDType ID} object.
	 * @param value value to set
	 * @return {@link IDType ID} object
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
