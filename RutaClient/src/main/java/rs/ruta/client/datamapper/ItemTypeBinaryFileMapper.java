package rs.ruta.client.datamapper;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.*;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.*;
import rs.ruta.client.*;

public class ItemTypeBinaryFileMapper<T> extends BinaryFileDataMapper<T>
{
//	public static final String COLUMNS = " id, lastname, firstname, number_of_dependents ";

//	private static String FILENAME = "client-products.dat";

//	private Client client;
	private BusinessParty businessParty;

	public ItemTypeBinaryFileMapper(BusinessParty businessParty, String filename)
	{
		super(filename);
		this.businessParty = businessParty;
	}

	@Override
	@SuppressWarnings("unchecked")
	public ArrayList<ItemType> findAll()
	{
		//reading Catalogue ID

/*		try(ObjectInputStream input = new ObjectInputStream(new FileInputStream(filename)))
		{
			long res = findLong();
			businessParty.setCatalogueID(res);
			//client.setCatalogueID((Long) input.readObject());
		}
		catch(IOException e)
		{
			return null;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}*/
		//reading myProducts list
		return (ArrayList<ItemType>) super.findAll();
	}


	@Override
	public void insertAll()
	{
		//insertAll(client.getMyProducts());

		try(ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(filename)))
		{
/*			long catID = businessParty.getCatalogueID(); // Catalogue ID number
			output.writeObject(catID);*/
			ArrayList<ItemType> list = businessParty.getProducts();
			for(int i = 0; i < list.size(); i++ )
				output.writeObject(new Product(businessParty, i)); // writing data to the file
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	protected T doLoad(long mapID, Object element)
	{
		Product p = (Product) element;
		ItemType item = new ItemType();

		NameType name = new NameType();
		name.setValue(p.getName());
		item.setName(name);

		ItemIdentificationType identification = new ItemIdentificationType();
		IDType id = new IDType();
		id.setValue(p.getId());
		identification.setID(id);
		item.setSellersItemIdentification(identification);

		DescriptionType desc = new DescriptionType();
		desc.setValue(p.getDescription());
		item.getDescription().add(desc);

		PackSizeNumericType packSize = new PackSizeNumericType();
		packSize.setValue(p.getPackSize());
		item.setPackSizeNumeric(packSize);

		BarcodeSymbologyIDType barcode = new BarcodeSymbologyIDType();
		barcode.setValue(p.getBarcode());
		identification.setBarcodeSymbologyID(barcode);

		CommodityClassificationType commodity = new CommodityClassificationType();
		CommodityCodeType commodityCode = new CommodityCodeType();
		commodityCode.setValue(p.getCommodityCode());
		commodity.setCommodityCode(commodityCode);
		item.getCommodityClassification().add(commodity);

		List<KeywordType> keywords;
		keywords = p.getKeywords();
		item.setKeyword(keywords);

		return (T) item;
	}


	/*	@Override
	protected String findStatement()
	{
		return "SELECT " + COLUMNS +
				" FROM people" +
				" WHERE id = ?";
	}*/

	/*	public ItemType find(long id)
	{
		return (ItemType) abstractFind(id);
	}*/

	/*	protected Object doLoad(Long id, ResultSet rs) throws SQLException {
	String lastNameArg = rs.getString(2);
	String firstNameArg = rs.getString(3);
	int numDependentsArg = rs.getInt(4);
	return new ItemType(id, lastNameArg, firstNameArg, numDependentsArg);
}
 */

	/*
	 protected DomainObject doLoad(Long id, ResultSet rs) throws SQLException {
String lastNameArg = rs.getString(2);
String firstNameArg = rs.getString(3);
int numDependentsArg = rs.getInt(4);
return new Person(id, lastNameArg, firstNameArg, numDependentsArg);
}

	 */


/*		public List findByLastName(String name) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = DB.prepare(findLastNameStatement);
			stmt.setString(1, name);
			rs = stmt.executeQuery();
			return loadAll(rs);
		} catch (SQLException e) {
			throw new ApplicationException(e);
		} finally {
			DB.cleanUp(stmt, rs);
		}
	}	*/


	@Override
	protected long getMapID(Object element)
	{
		return ((Product) element).getMapID();
	}

	public static class Product implements Serializable
	{
		private static final long serialVersionUID = 4178508604860862703L;
		private String name;
		private String description;
		private String id;
		private long mapID;
		static private long nextID = 0;
		private BigDecimal packSize;
		private String barcode;
		private String commodityCode;
		private List<KeywordType> keywords;

		public Product(BusinessParty businessParty, int index)
		{
			mapID = nextID++;
			name = businessParty.getProductNameAsString(index);
			//id = Long.valueOf(client.getProductID(index));
			id = businessParty.getProductIDAsString(index);
			description = businessParty.getProductDescriptionAsString(index);
			packSize = businessParty.getProductPackSizeAsBigDecimal(index);
			barcode = businessParty.getProductBarcodeAsString(index);
			commodityCode = businessParty.getProductCommodityCodeAsString(index);
			keywords = businessParty.getProductKeywords(index);
		}

		public String getId() { return id; }

		public String getName () { return name; }

		public String getDescription() { return description; }

		public long getMapID() { return mapID; }

		public BigDecimal getPackSize() { return packSize; }

		public String getBarcode() { return barcode; }

		public String getCommodityCode() { return commodityCode; }

		public List<KeywordType> getKeywords() { return keywords; }

		@Override
		public String toString()
		{
			return " Id: "+ id + " Name: " + name + " Description: " + description;
		}

	}
}
