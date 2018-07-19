package rs.ruta.client;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.UUID;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlType;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.ItemIdentificationType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.ItemType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PriceType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.IDType;

/**
 * Wrapper class for {@link ItemType} with some additional convinient methods.
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
	@XmlElement(name = "InStock")
	private boolean inStock;

	public Item()
	{
		super();
	}

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
		item.inStock = inStock;
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

	public boolean isInStock()
	{
		return inStock;
	}

	public void setInStock(boolean inStock)
	{
		this.inStock = inStock;
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

	/**
	 * Verifies whether {@code Item} has all of its mandatory fields.
	 * @return {@code null} if it has all mandatory fields, or {@code String} designating the first missing field
	 */
	public String verifyItem()
	{
		try
		{
			if(getNameValue() == null)
				return "Name"; 
		}
		catch(Exception e)
		{
			return "Name"; 
		}
		try
		{
			if(getSellersItemIdentification().getIDValue() == null)
				return "ID"; 
		}
		catch(Exception e)
		{
			return "ID"; 
		}
		try
		{
			if(getPrice().getPriceAmountValue() == null)
				return "Price"; 
		}
		catch(Exception e)
		{
			return "Price"; 
		}
		try
		{
			if(getClassifiedTaxCategoryAtIndex(0).getPercentValue() == null)
				return "Tax"; 
		}
		catch(Exception e)
		{
			return "Tax"; 
		}
		return null;
	}

}