	package rs.ruta.client;

	import java.awt.event.ActionEvent;
	import java.awt.event.ActionListener;
	import java.math.BigDecimal;

	import java.util.*;
	import java.util.stream.Collectors;
	import java.util.stream.Stream;

	import javax.annotation.Nullable;
	import javax.xml.bind.annotation.*;
	import javax.xml.datatype.XMLGregorianCalendar;

	import org.slf4j.Logger;
	import org.slf4j.LoggerFactory;

	import com.helger.commons.error.list.IErrorList;
	import com.helger.ubl21.UBL21Validator;

	import oasis.names.specification.ubl.schema.xsd.catalogue_21.CatalogueType;
	import oasis.names.specification.ubl.schema.xsd.cataloguedeletion_21.CatalogueDeletionType;
	import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.CatalogueLineType;
	import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.CatalogueReferenceType;
	import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.CommodityClassificationType;
	import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.CustomerPartyType;
	import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.ItemIdentificationType;
	import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.ItemLocationQuantityType;
	import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.ItemType;
	import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.LineItemType;
	import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.OrderLineType;
	import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyNameType;
	import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
	import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PriceType;
	import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.SupplierPartyType;
	import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.TaxCategoryType;
	import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.BarcodeSymbologyIDType;
	import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.CommodityCodeType;
	import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.DescriptionType;
	import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.IDType;
	import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.IssueDateType;
	import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.KeywordType;
	import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.NameType;
	import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.PackSizeNumericType;
	import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.PriceAmountType;
	import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.UUIDType;
	import oasis.names.specification.ubl.schema.xsd.order_21.OrderType;
	import oasis.names.specification.ubl.schema.xsd.orderresponsesimple_21.OrderResponseSimpleType;
	import rs.ruta.client.correspondence.BuyerOrderingProcess;
	import rs.ruta.client.correspondence.BuyingCorrespondence;
	import rs.ruta.client.correspondence.CatalogueCorrespondence;
	import rs.ruta.client.correspondence.Correspondence;
	import rs.ruta.client.correspondence.CreateCatalogueProcess;
	import rs.ruta.client.correspondence.CreateCatalogueProcessState;
	import rs.ruta.client.correspondence.DeleteCatalogueProcess;
	import rs.ruta.client.correspondence.NotifyOfCatalogueDeletionState;
	import rs.ruta.client.correspondence.PrepareCatalogueState;
	import rs.ruta.client.correspondence.RutaProcess;
	import rs.ruta.client.correspondence.SellerOrderingProcess;
	import rs.ruta.common.BusinessPartySearchCriterion;
	import rs.ruta.common.DeregistrationNotice;
	import rs.ruta.common.InstanceFactory;
	import rs.ruta.common.RutaUser;
	import rs.ruta.common.datamapper.DataMapper;
	import rs.ruta.common.datamapper.DatabaseException;
	import rs.ruta.common.datamapper.DetailException;
	import rs.ruta.common.datamapper.MapperRegistry;

	/**
	 * Class representing party of {@code Ruta application} containing all the data pertaining that
	 * party and all of its correspondeces with other parties in {@code Ruta System}.
	 */
	@XmlRootElement(name = "MyParty")
	@XmlAccessorType(XmlAccessType.NONE)
	@XmlSeeAlso({CatalogueType.class}) //this solves the issue JAXB context not seeing the CatatalogueType
	public class MyParty extends BusinessParty
	{
		private static Logger logger = LoggerFactory.getLogger("rs.ruta.client");
		private List<Item> products;
		/**
		 * MyParty data retrieved from the CDR service.
		 */
		//	@XmlElement(name = "MyFollowingParty")
		@Nullable
		private BusinessParty myFollowingParty;
		/**
		 * Helper list containing all following parties of MyParty except {@code myFollowingParty}.
		 * List of all following parties of MyParty. Should always be accessed with {@link #getFollowingParty(String)}
		 * method call unless prior to access is checked for {@code null}.
		 */
		//	@XmlElement(name = "FollowingParty")
		@Nullable
		private List<BusinessParty> followingParties;
		/**
		 * List containing only business partners of MyParty.
		 * Should always be accessed with {@link #getBusinessPartners()} method call unless prior to access is checked for {@code null}.
		 */
		@Nullable
		private List<BusinessParty> businessPartners;
		/**
		 * List containing only following parties that are not business partners of MyParty.
		 * Should always be accessed with {@link #getOtherParties()} method call unless prior to access is checked for {@code null}.
		 */
		@Nullable
		private List<BusinessParty> otherParties;
		/**
		 * List of unfollowed parties from the CDR service.
		 * Should always be accessed with {@link #getArchivedParties()} method call unless prior to access is checked for {@code null}.
		 */
		//	@XmlElement(name = "ArchivedParty")
		@Nullable
		private List<BusinessParty> archivedParties;
		/**
		 * List of deregistered parties from the CDR service.
		 * Should always be accessed with {@link #getArchivedParties()} method call unless prior to access is checked for {@code null}.
		 */
		//	@XmlElement(name = "DeregisteredParty")
		@Nullable
		private List<BusinessParty> deregisteredParties;
		//	@XmlElement(name = "PartySearch")
		private List<Search<PartyType>> partySearches;
		//	@XmlElement(name = "CatalogueSearch")
		private List<Search<CatalogueType>> catalogueSearches;
		//	private List<ActionListener> actionListeners;
		private Map<Class<? extends ActionEvent>, List<ActionListener>> actionListeners;
		private CatalogueCorrespondence catalogueCorrespondence;
		private List<BuyingCorrespondence> buyingCorrespondences;

		@XmlElement(name = "LocalUser")
		private RutaUser localUser;
		@XmlElement(name = "CDRUser")
		private RutaUser cdrUser;
		@XmlElement(name = "DirtyCatalogue")
		private boolean dirtyCatalogue;
		@XmlElement(name = "DirtyMyParty")
		private boolean dirtyMyParty;
		@XmlElement(name = "InsertMyCatalogue")
		private boolean insertMyCatalogue; // true when catalogue should be inserted in the CDR i.e. deposited for the first time
		@XmlElement(name = "SearchNumber")
		private long searchNumber;
		@XmlElement(name = "CatalogueID")
		private long catalogueID;
		@XmlElement(name = "CatalogueDeletionID")
		private long catalogueDeletionID;
		@XmlElement(name = "ItemID")
		private long itemID;
		@XmlElement(name = "CatalogueIssueDate")
		protected XMLGregorianCalendar catalogueIssueDate;
		@XmlAttribute(name = "JAXBVersion")
		private String jaxb; // version of the MyParty class
		@XmlTransient
		private RutaClient client;

		public MyParty()
		{
			super();
			setFollowing(true);
			products = getProducts();
			dirtyCatalogue = dirtyMyParty = insertMyCatalogue = true;
			//		username = password = secretKey = null;
			localUser = new RutaUser();
			cdrUser = new RutaUser();
			followingParties = businessPartners = otherParties = archivedParties = deregisteredParties = null;
			//		catalogueProcesses = null;
			searchNumber = catalogueID = catalogueDeletionID = itemID = 0;
			catalogueIssueDate = null;
			createListenerMap();
			jaxb = RutaClient.getVersion().getJaxbVersion();
		}

		/**
		 * Populates all lists of {@code MyParty} from a local data store.
		 * @throws DetailException if data could not be retrieved from a data store
		 */
		public void loadData() throws DetailException
		{
			final MapperRegistry mapperRegistry = MapperRegistry.getInstance();
			setProducts(mapperRegistry.getMapper(Item.class).findAll());
			BusinessPartySearchCriterion criterion = new BusinessPartySearchCriterion();
			criterion.setPartner(true);
			final DataMapper<BusinessParty, String> businessPartyMapper = mapperRegistry.getMapper(BusinessParty.class);
			businessPartners = businessPartyMapper.findMany(criterion);
			criterion = new BusinessPartySearchCriterion();
			criterion.setOther(true);
			otherParties = businessPartyMapper.findMany(criterion);
			final String myPartyID = getPartyID();
			if(myPartyID != null)
				if(otherParties != null)
				{
					try
					{
						myFollowingParty = getOtherParties().stream().filter(party -> party.getPartyID().equals(myPartyID)).findFirst().get();
						otherParties.remove(myFollowingParty);
					}
					catch(NoSuchElementException e)
					{ //OK; myFollowingParty is not among the following parties
						myFollowingParty = businessPartyMapper.find(myPartyID);
					}
				}
				else
					myFollowingParty = businessPartyMapper.find(myPartyID);

			criterion = new BusinessPartySearchCriterion();
			criterion.setArchived(true);
			setArchivedParties(businessPartyMapper.findMany(criterion));
			criterion = new BusinessPartySearchCriterion();
			criterion.setDeregistered(true);
			setDeregisteredParties(businessPartyMapper.findMany(criterion));
			setPartySearches(Search.toListOfGenerics(mapperRegistry.getMapper(PartySearch.class).findAll()));
			setCatalogueSearches(Search.toListOfGenerics(mapperRegistry.getMapper(CatalogueSearch.class).findAll()));

//			logger.info("*****Start loading catalogue correspondence");

			final List<CatalogueCorrespondence> corrs = mapperRegistry.getMapper(CatalogueCorrespondence.class).findAll();
//			logger.info("*****End loading catalogue correspondence");

//				logger.info("*****Start starting catalogue correspondence thread");

				CatalogueCorrespondence corr = null;
				if(corrs == null || corrs.isEmpty())
				{
					corr = CatalogueCorrespondence.newInstance(client);
				}
				else
				{
					corr = corrs.get(0);
					corr.setClient(client);
					((RutaProcess) corr.getState()).setClient(client);
				}
				corr.start();
				setCatalogueCorrespondence(corr);
//				logger.info("*****End starting catalogue correspondence thread");

//			logger.info("*****Start loading buying correspondence");
			buyingCorrespondences = mapperRegistry.getMapper(BuyingCorrespondence.class).findAll();
//			logger.info("*****End loading buying correspondence");
//				logger.info("*****Start loop for buying correspondence thread");
				if(buyingCorrespondences != null)
					for(BuyingCorrespondence bCorr : buyingCorrespondences)
					{
						/*				if(bCorr.isStopped())
					{
						bCorr.setActive(true);
						((RutaProcess) bCorr.getState()).setActive(true);
					}*/
					if(bCorr.isActive())
					{
//						logger.info("*****Start starting buying correspondence thread");
						bCorr.setClient(client);
						((RutaProcess) bCorr.getState()).setClient(client);
						bCorr.start();
//						logger.info("*****End starting buying correspondence thread");
					}
				}
//			logger.info("*****End loop for buying correspondence thread");
		}

		/**
		 * Stores My Party's data to a local data store.
		 * @throws Exception if data could not be stored to a data store
		 */
		public void storeAllData() throws Exception
		{
			stopCorrespondencies();
			setSearchNumber(Search.getSearchNum());
			final MapperRegistry mapperRegistry = MapperRegistry.getInstance();
			mapperRegistry.getMapper(MyParty.class).insert(getLocalUsername(), this);
			mapperRegistry.getMapper(Item.class).insertAll(null, getProducts());
			/*		if(followingParties != null && !followingParties.isEmpty())
				mapperRegistry.getMapper(BusinessParty.class).insertAll(null, getFollowingParties());*/
			if(businessPartners != null && !businessPartners.isEmpty())
				mapperRegistry.getMapper(BusinessParty.class).insertAll(null, businessPartners);
			if(otherParties != null && !otherParties.isEmpty())
				mapperRegistry.getMapper(BusinessParty.class).insertAll(null, otherParties);
			if(archivedParties != null && !archivedParties.isEmpty())
				mapperRegistry.getMapper(BusinessParty.class).insertAll(null, archivedParties);
			if(deregisteredParties != null && !deregisteredParties.isEmpty())
				mapperRegistry.getMapper(BusinessParty.class).insertAll(null, deregisteredParties);
			if(myFollowingParty != null)
				mapperRegistry.getMapper(BusinessParty.class).insert(null, myFollowingParty);
			if(partySearches != null && !partySearches.isEmpty())
			{
				List<PartySearch> newList = Search.fromLisfOfGenerics(getPartySearches());
				mapperRegistry.getMapper(PartySearch.class).insertAll(null, newList);
			}
			if(catalogueSearches != null && !catalogueSearches.isEmpty())
			{
				List<CatalogueSearch> newList = Search.fromLisfOfGenerics(getCatalogueSearches());
				mapperRegistry.getMapper(CatalogueSearch.class).insertAll(null, newList);
			}
			waitCorrespondencesToStop();
			if(catalogueCorrespondence != null)
			{
				/*			JAXBContext jaxbContext = JAXBContext.newInstance(CatalogueCorrespondence.class);
				Marshaller marshaller = jaxbContext.createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
				marshaller.marshal(catalogueCorrespondence, System.out);*/

				mapperRegistry.getMapper(CatalogueCorrespondence.class).insert(null, catalogueCorrespondence);
			}
			if(buyingCorrespondences != null && !buyingCorrespondences.isEmpty())
			{
				for(BuyingCorrespondence bCorr : buyingCorrespondences)
				{
					/*				JAXBContext jaxbContext = JAXBContext.newInstance(BuyingCorrespondence.class);
					Marshaller marshaller = jaxbContext.createMarshaller();
					marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
					marshaller.marshal(bCorr, System.out);*/
					if(bCorr.isActive() || bCorr.isStopped())
						mapperRegistry.getMapper(BuyingCorrespondence.class).insert(null, bCorr);
				}
				//			mapperRegistry.getMapper(BuyingCorrespondence.class).insertAll(null, buyingCorrespondences);
			}

		}

		/**
		 * Stores only My Party data to a local data store that have not be stored after start of the application.
		 * Some data that are stored immediately after they had be changed, should not be stored in this method again.
		 * <p>This method should be refined.</p>
		 * @throws Exception if data could not be stored to a data store
		 */
		public void storeDirtyData() throws Exception
		{
			setSearchNumber(Search.getSearchNum());
			final MapperRegistry mapperRegistry = MapperRegistry.getInstance();
			mapperRegistry.getMapper(MyParty.class).insert(getLocalUsername(), this);
			mapperRegistry.getMapper(Item.class).insertAll(null, getProducts());

			/*		if(followingParties != null && !followingParties.isEmpty())
				mapperRegistry.getMapper(BusinessParty.class).insertAll(null, getFollowingParties());*/

			/*		if(businessPartners != null && !businessPartners.isEmpty())
				mapperRegistry.getMapper(BusinessParty.class).insertAll(null, businessPartners);
			if(otherParties != null && !otherParties.isEmpty())
				mapperRegistry.getMapper(BusinessParty.class).insertAll(null, otherParties);
			if(archivedParties != null && !archivedParties.isEmpty())
				mapperRegistry.getMapper(BusinessParty.class).insertAll(null, archivedParties);
			if(deregisteredParties != null && !deregisteredParties.isEmpty())
				mapperRegistry.getMapper(BusinessParty.class).insertAll(null, deregisteredParties);
			if(myFollowingParty != null)
				mapperRegistry.getMapper(BusinessParty.class).insert(null, myFollowingParty);*/

			if(partySearches != null && !partySearches.isEmpty())
			{
				List<PartySearch> newList = Search.fromLisfOfGenerics(getPartySearches());
				mapperRegistry.getMapper(PartySearch.class).insertAll(null, newList);
			}
			if(catalogueSearches != null && !catalogueSearches.isEmpty())
			{
				List<CatalogueSearch> newList = Search.fromLisfOfGenerics(getCatalogueSearches());
				mapperRegistry.getMapper(CatalogueSearch.class).insertAll(null, newList);
			}
			if(catalogueCorrespondence != null)
			{
				mapperRegistry.getMapper(CatalogueCorrespondence.class).insert(null, catalogueCorrespondence);
			}

			if(buyingCorrespondences != null && !buyingCorrespondences.isEmpty())
			{
				mapperRegistry.getMapper(BuyingCorrespondence.class).insertAll(null, buyingCorrespondences);
			}

		}

		/**
		 * Deletes all the objects from the data model and all the data from the data store.
		 * @throws DetailException if data could not be deleted
		 */
		public void deleteData() throws DetailException
		{
			dirtyCatalogue = dirtyMyParty = insertMyCatalogue = true;
			searchNumber = catalogueID = catalogueDeletionID = itemID = 0;
			catalogueIssueDate = null;
			Search.resetSearchNumber();
			clearParties();
			clearSearches();
			clearProducts();
			clearCorrespondences();
		}

		public RutaClient getClient()
		{
			return client;
		}

		public void setClient(RutaClient client)
		{
			this.client = client;
		}

		/**
		 * Creates a {@link HashMap map} with (key, value) pairs where the key is a {@link Class} object of the
		 * event and the value is a list of all listeners that are listening for that event.
		 */
		private void createListenerMap()
		{
			actionListeners = new HashMap<>();
			actionListeners.put(BusinessPartyEvent.class, new ArrayList<>());
			actionListeners.put(SearchEvent.class, new ArrayList<>());
			actionListeners.put(RutaClientFrameEvent.class, new ArrayList<>());
			actionListeners.put(CorrespondenceEvent.class, new ArrayList<>());
		}

		public Map<Class<? extends ActionEvent>, List<ActionListener>> getActionListeners()
		{
			return actionListeners;
		}

		/**
		 * Registeres new {@link ActionListener} with {@code MyParty} object.
		 * @param listener listener to register
		 * @param eventClazz {@link Class} object of the event that the listener is listening for
		 */
		public void addActionListener(ActionListener listener, Class<? extends ActionEvent> eventClazz)
		{
			actionListeners.get(eventClazz).add(listener);
		}

		/**
		 * Notifies all registered {@link ActionListener}s sending {@link ActionEvent} that describes the change
		 * in the data model.
		 * @param event
		 */
		public void notifyListeners(ActionEvent event)
		{
			/*		List<ActionListener> listeners = actionListeners.stream().filter(listener -> ((RutaTreeModel) listener).
					listenFor(event.getClass())).collect(Collectors.toList());
			for(ActionListener listener : listeners)
				listener.actionPerformed(event);*/

			actionListeners.get(event.getClass()).stream().forEach(listener -> listener.actionPerformed(event));

		}

		public List<Item> getProducts()
		{
			if (products == null)
				products = new ArrayList<Item>();
			return products;
		}

		public void setProducts(@Nullable List<Item> products)
		{
			this.products = products;
		}

		public void setProducts(CatalogueType catalogue)
		{
			/*		try
			{
				String strID = InstanceFactory.getPropertyOrNull(catalogue.getID(), IDType::getValue);
				catalogueID = Integer.parseInt(strID);
			}
			catch(Exception e)
			{
				catalogueID = 0;
			}*/
			List<CatalogueLineType> catalogueLines = catalogue.getCatalogueLine();
			if(!catalogueLines.isEmpty())
			{
				products.clear();
				products = catalogueLines.stream().map(line -> new Item(line.getItem())).collect(Collectors.toList());
				/*			for(CatalogueLineType line : catalogueLines)
					products.add(line.getItem());*/
			}
		}

		/**
		 * Removes all products from the data model and data store.
		 * <p>Notifies listeners registered for this type of the {@link BusinessPartyEvent event}.</p>
		 * @throws DetailException if data could not be deleted from the database
		 */
		public void clearProducts() throws DetailException
		{
			MapperRegistry.getInstance().getMapper(Item.class).deleteAll();
			//MMM:	TODO	notifyListeners(new BusinessPartyEvent(products, ItemEvent.ALL_PRODUCTS_REMOVED));
			this.products = null;
		}

		public String getProductNameAsString(final int index)
		{
			final Item item = products.get(index);
			try
			{
				return InstanceFactory.getPropertyOrNull(item.getName(), NameType::getValue);
			}
			catch(Exception e) { return null; }
		}

		public void setProductName(int index, String value)
		{
			value = value.trim();
			final Item item = products.get(index);
			if(hasCellValueChanged(InstanceFactory.getPropertyOrNull(item.getName(), NameType::getValue), value))
				item.setName(value);
		}

		public String getProductDescriptionAsString(final int index)
		{
			final Item item = products.get(index);
			try
			{
				return InstanceFactory.getPropertyOrNull(item.getDescription().get(0), DescriptionType::getValue);
			}
			catch(Exception e) { return null; }
		}

		public void setProductDescription(int index, String value)
		{
			value = value.trim();
			final Item item = products.get(index);
			List<DescriptionType> descriptions = item.getDescription();
			if(descriptions.isEmpty())
				descriptions.add(new DescriptionType());
			if(hasCellValueChanged(descriptions.get(0).getValue(), value))
				descriptions.get(0).setValue(value);
		}

		public String getProductIDAsString(final int index)
		{
			final Item item = products.get(index);
			try
			{
				return InstanceFactory.getPropertyOrNull(item.getSellersItemIdentification().getID(), IDType::getValue);
			}
			catch(Exception e) { return null; }
		}

		/**
		 * Sets product's ID which is unique in the scope of MyParty.
		 * @param index product's index in the list of all products
		 * @param value ID's value to set
		 * @throws ProductException if product's ID is not unique or is equal to an empty string
		 */
		public void setProductID(int index, String value) throws ProductException
		{
			value = value.trim();
			if("".equals(value))
				throw new ProductException("Product ID must be a non-empty string value.\nChange you made have not been accepted.");
			if(!isUniqueProductID(value))
				throw new ProductException("Product ID is not unique.");
			final Item item = products.get(index);
			if(item.getSellersItemIdentification() == null)
				item.setSellersItemIdentification(new ItemIdentificationType());
			if(item.getSellersItemIdentification().getID() == null)
				item.getSellersItemIdentification().setID(new IDType());
			if(hasCellValueChanged(item.getSellersItemIdentification().getIDValue(), value))
				item.getSellersItemIdentification().setID(value);
		}


		public String getProductBarcodeAsString(final int index)
		{
			final Item item = products.get(index);
			try
			{
				return InstanceFactory.getPropertyOrNull(item.getSellersItemIdentification().getBarcodeSymbologyID(), BarcodeSymbologyIDType::getValue);
			}
			catch(Exception e) { return null; }
		}

		public void setProductBarcode(int index, String value) throws ProductException
		{
			value = value.trim();
			final Item item = products.get(index);
			if(item.getSellersItemIdentification() == null)
				//item.setSellersItemIdentification(new ItemIdentificationType());
				throw new ProductException("Product ID is mandatory, and it must be entered first!");
			if(item.getSellersItemIdentification().getBarcodeSymbologyID() == null)
				item.getSellersItemIdentification().setBarcodeSymbologyID(new BarcodeSymbologyIDType());
			if(hasCellValueChanged(item.getSellersItemIdentification().getBarcodeSymbologyIDValue(), value))
				item.getSellersItemIdentification().setBarcodeSymbologyID(value);
		}

		public BigDecimal getProductPackSize(final int index) //MMM: check whether return value should be String instead
		{
			final Item item = products.get(index);
			try
			{
				return InstanceFactory.getPropertyOrNull(item.getPackSizeNumeric(), PackSizeNumericType::getValue);
			}
			catch(Exception e) { return null; }
		}

		public void setProductPackSizeNumeric(int index, BigDecimal value)
		{
			final Item item = products.get(index);
			if(item.getPackSizeNumeric() == null)
				item.setPackSizeNumeric(new PackSizeNumericType());
			if(hasCellValueChanged(item.getPackSizeNumericValue(), value))
				if(value.doubleValue() == 0) // delete PackSizeNumericType field because of UBL conformance
					item.setPackSizeNumeric((PackSizeNumericType) null);
				else
					item.setPackSizeNumeric(value);
		}

		public String getProductCommodityCodeAsString(final int index)
		{
			final Item item = products.get(index);
			try
			{
				return InstanceFactory.getPropertyOrNull(item.getCommodityClassification().get(0).getCommodityCode(),
						CommodityCodeType::getValue);
			}
			catch(Exception e) { return null; }
		}

		public void setProductCommodityCode(int index, String value)
		{
			value = value.trim();
			final Item item = products.get(index);
			List<CommodityClassificationType> commodities = item.getCommodityClassification();
			if(commodities.isEmpty())
				commodities.add(new CommodityClassificationType());
			if(commodities.get(0).getCommodityCode() == null)
				commodities.get(0).setCommodityCode(new CommodityCodeType());
			if(hasCellValueChanged(commodities.get(0).getCommodityCodeValue(), value))
				commodities.get(0).setCommodityCode(value);;
		}

		public BigDecimal getProductPrice(final int index)
		{
			try
			{
				final Item item = products.get(index);
				return InstanceFactory.getPropertyOrNull(item.getPrice().getPriceAmount(), PriceAmountType::getValue);
			}
			catch(Exception e) { return null; }
		}

		public void setProductPrice(int index, final BigDecimal value)
		{
			final Item item = products.get(index);
			if(item.getPrice() == null)
				item.setPrice(new PriceType());
			if(item.getPrice().getPriceAmount() == null)
			{
				// to conform to the UBL, currencyID is mandatory
				PriceAmountType priceAmount = new PriceAmountType(value);
				priceAmount.setCurrencyID("RSD"); // MMM: currencyID should be pooled from somewhere in the UBL definitions - check specifications
				item.getPrice().setPriceAmount(priceAmount);
			}
			if(hasCellValueChanged(item.getPrice().getPriceAmountValue(), value))
				item.getPrice().setPriceAmount(value);
		}

		public String getProductTaxPrecentAsString(final int index)
		{
			try
			{
				final Item item = products.get(index);
				return InstanceFactory.getPropertyOrNull(item.getClassifiedTaxCategoryAtIndex(0), TaxCategoryType::getPercentValue).toString();
			}
			catch(Exception e) { return null; }
		}

		/**
		 * Sets the tax precentage. It is equivalent and it is done by changing of the tax category.
		 * @param index product's index in the list
		 * @param value value to set
		 */
		public void setProductTaxPrecent(final int index, String value)
		{
			value = value.trim();
			final List<TaxCategoryType> taxCategoryList = products.get(index).getClassifiedTaxCategory();
			final TaxCategoryType newCategory = InstanceFactory.getTaxCategory(value);
			TaxCategoryType oldCategory = null;
			if(!taxCategoryList.isEmpty())
				oldCategory = taxCategoryList.get(0);
			if(hasCellValueChanged(oldCategory, newCategory))
			{
				taxCategoryList.clear();
				taxCategoryList.add(newCategory);
			}
		}

		/**
		 * Retrieves the list of keywords set for the {@link Item}.
		 * @param index index of the {@code Item} in the list of products
		 * @return list of keywords
		 */
		public String getProductKeywordsAsString(final int index)
		{
			final Item item = products.get(index);
			return item.getKeywordCount() == 0 ? null :
				item.getKeyword().stream().map(keyword -> keyword.getValue()).collect(Collectors.joining(", "));
		}

		public List<KeywordType> getProductKeywords(final int index)
		{
			final Item item = products.get(index);
			return item.getKeyword();
		}

		public void setProductKeywords(int index, List<KeywordType> value)
		{
			final Item item = products.get(index);
			item.setKeyword(value);
		}

		public void setProductKeywords(int index, String value)
		{
			value = value.trim();
			final Item item = products.get(index);
			List<KeywordType> keywords =
					Stream.of(value.split("( )*[,;]+")).map(keyword -> new KeywordType(keyword)).collect(Collectors.toList());
			item.setKeyword(keywords);
		}

		/**
		 * Gets the next eligable ID for the {@link Item product}.
		 * <p> Method is not optimized.
		 * @return product's ID
		 */
		private String getNextProductID()
		{
			String nextID = String.valueOf(++itemID);
			while(!isUniqueProductID(nextID))
				nextID = String.valueOf(++itemID);
			return nextID;
		}

		/**
		 * Tests whether the ID is unique in the list of all {@code Item product}s.
		 * @param uuid ID to test
		 * @return true if it is unique, false otherwise
		 */
		private boolean isUniqueProductID(String id)
		{
			try
			{
				return !products.stream().anyMatch(item -> id.equals(item.getSellersItemIdentification().getIDValue()));
			}
			catch(Exception e) // if for some reason ID could not be accessed e.g. when it is not set - should not happen but still
			{
				return true;
			}
		}

		/**
		 * Returns the number of My Party's products.
		 * @return number of products
		 */
		public int getProductCount()
		{
			return products.size();
		}

		/**
		 * Returns ID of the latest created Catalogue Document.
		 * @return catalogue ID
		 */
		public long getCatalogueID()
		{
			return catalogueID;
		}

		public void setCatalogueID(long catalogueID)
		{
			this.catalogueID = catalogueID;
		}

		/**
		 * Returns next ID for the newly created Catalogue Document.
		 * @return next catalogue ID
		 */
		public long nextCatalogueID()
		{
			return ++catalogueID;
		}

		/**
		 * Returns ID of the latest created Catalogue Deletion Document.
		 * @return catalogue deletion ID
		 */
		public long getCatalogueDeletionID()
		{
			return catalogueDeletionID;
		}

		public void setCatalogueDeletionID(long catalogueDeletionID)
		{
			this.catalogueDeletionID = catalogueDeletionID;
		}

		/**
		 * Returns next ID for the newly created CatalogueDeletion Document.
		 * @return next catalogue deletion ID
		 */
		public long nextCatalogueDeletionID()
		{
			return ++catalogueDeletionID;
		}

		/**
		 * Returns ID of the latest created {@link Item product item}.
		 * @return item ID
		 */
		public long getItemID()
		{
			return itemID;
		}

		public void setItemID(long itemID)
		{
			this.itemID = itemID;
		}

		/**
		 * Gets issue date of the latest catalogue.
		 * @return catalogue issue date
		 */
		public XMLGregorianCalendar getCatalogueIssueDate()
		{
			return catalogueIssueDate;
		}

		/**
		 * Sets catalogue issue date to current date.
		 * @return set catalogue issue date
		 */
		public XMLGregorianCalendar setCatalogueIssueDate()
		{
			this.catalogueIssueDate = InstanceFactory.getDate();
			return catalogueIssueDate;
		}

		/**
		 * Sets Catalogue issue date to {@code null}. Usually this is done after the Catalogue has been
		 * deleted from the CDR.
		 */
		public void removeCatalogueIssueDate()
		{
			this.catalogueIssueDate = null;
		}

		/**
		 * Tells whether Catalogue has ever been sent to the CDR.
		 * @return true is CDR has My Party's Catalogue, false otherwise
		 */
		public boolean isCatalogueInCDR()
		{
			return catalogueIssueDate != null ? true : false;
		}

		/**
		 * Removes all parties from the data model and data store.
		 * <p>Notifies listeners registered for this type of the {@link BusinessPartyEvent event}.</p>
		 * @throws DetailException if data could not be deleted from the database
		 */
		public void clearParties() throws DetailException
		{
			MapperRegistry.getInstance().getMapper(BusinessParty.class).deleteAll();
			notifyListeners(new BusinessPartyEvent(new ArrayList<>(), BusinessPartyEvent.ALL_PARTIES_REMOVED));
			setArchivedParties(null);
			setBusinessPartners(null);
			setDeregisteredParties(null);
			setOtherParties(null);
		}

		/**
		 * Removes all searches from the data model and data store.
		 * <p>Notifies listeners registered for this type of the {@link BusinessPartyEvent event}.</p>
		 * @throws DetailException if data could not be deleted from the database
		 */
		public void clearSearches() throws DetailException
		{
			//MMM:		TODO notifyListeners(new BusinessPartyEvent(partySearches, SearchEvent.ALL_PARTY_SEARCHES_REMOVED));
			MapperRegistry.getInstance().getMapper(PartySearch.class).deleteAll();
			setPartySearches(null);
			MapperRegistry.getInstance().getMapper(CatalogueSearch.class).deleteAll();
			setCatalogueSearches(null);
		}

		/**
		 * Removes all correspondences from the data model and data store.
		 * <p>Notifies listeners registered for this type of the {@link BusinessPartyEvent event}.</p>
		 * @throws DetailException if data could not be deleted from the database
		 */
		public void clearCorrespondences() throws DetailException
		{
			MapperRegistry.getInstance().getMapper(CatalogueCorrespondence.class).deleteAll();
			setCatalogueCorrespondence(null);
			MapperRegistry.getInstance().getMapper(BuyingCorrespondence.class).deleteAll();
			setBuyingCorrespondences(null);
			//MMM:	TODO	notifyListeners(new BusinessPartyEvent(products, CorrespondenceEvent.ALL_CORRESPONDENCES_REMOVED));
		}

		/**
		 * Gets the copy of MyParty previously retrieved from the CDR service.
		 * @return MyParty object or {@code null}
		 */
		public BusinessParty getMyFollowingParty()
		{
			return myFollowingParty;
		}

		/**
		 * Sets MyParty data previously retrieved from the CDR service.
		 * @param myFollowingParty
		 */
		public void setMyFollowingParty(BusinessParty myFollowingParty)
		{
			this.myFollowingParty = myFollowingParty;
		}

		/**
		 * Gets the {@code List} of all parties that are bussines partners of MyParty.
		 * @return list of business partners
		 */
		public List<BusinessParty> getBusinessPartners()
		{
			if(businessPartners == null)
				businessPartners = new ArrayList<BusinessParty>();
			return businessPartners;
		}

		/**
		 * Sets the list of business partners.
		 * @param businessPartners list of business partners to set
		 */
		public void setBusinessPartners(@Nullable List<BusinessParty> businessPartners)
		{
			this.businessPartners = businessPartners;
		}

		/**
		 * Removes all parties from the business partners list.
		 * <p>Notifies listeners registered for this type of the {@link BusinessPartyEvent event}.</p>
		 * @throws DetailException if data could not be deleted from the data store
		 */
		public void clearBusinessPartners() throws DetailException
		{
			for(BusinessParty party : getBusinessPartners())
				MapperRegistry.getInstance().getMapper(BusinessParty.class).delete(null, party.getPartyID());
			notifyListeners(new BusinessPartyEvent(businessPartners, BusinessPartyEvent.BUSINESS_LIST_REMOVED));
			setBusinessPartners(null);
		}

		/**
		 * Adds party to business partner list.
		 * @param party party to add
		 * @throws DetailException if party could not be inserted in the data store
		 */
		private void addBusinessPartner(BusinessParty party) throws DetailException
		{
			if(party != null)
			{
				MapperRegistry.getInstance().getMapper(BusinessParty.class).insert(null, party);
				party.setPartner(true);
				getBusinessPartners().add(party);
			}
		}

		/**
		 * Removes party from the list of business partners.
		 * @param party party to remove
		 * @return true if party was contained in list of business parties and removed from it
		 * @throws DetailException if party could not be deleted from the data store
		 */
		private boolean removeBusinessPartner(BusinessParty party) throws DetailException
		{
			boolean success = false;
			if(party != null)
			{
				try
				{
					MapperRegistry.getInstance().getMapper(BusinessParty.class).delete(null, party.getPartyID());
					success = getBusinessPartners().remove(party);
					party.setPartner(false);
				}
				catch(DatabaseException e)
				{
					if(! "Document does not exist!".equals(e.getMessage())) //OK if document does not exist, otherwise throw e
						throw e;
				}
			}
			return success;
		}

		/**
		 * Gets the business partner with passed ID.
		 * @param uuid following party's ID
		 * @return following party or {@code null} if there is no party with specified ID in the
		 * list of following parties
		 */
		public BusinessParty getBusinessPartner(String id)
		{
			try
			{
				return getBusinessPartners().stream().filter(party -> id.equals(party.getCoreParty().getPartyID())).findFirst().get();
			}
			catch(NoSuchElementException e) //if there is no party with passed uuid
			{
				return null;
			}
		}

		/**
		 * Gets the {@code List} of all parties that are not bussines partners of MyParty. Those parties are just
		 * followed by MyParty.
		 * @return list of followed parties that are not business partners
		 */
		public List<BusinessParty> getOtherParties()
		{
			if(otherParties == null)
				otherParties = new ArrayList<BusinessParty>();
			return otherParties;
		}

		/**
		 * Sets the list of parties that are followed but not are business partners.
		 * @param otherParties list of other parties to set
		 */
		public void setOtherParties(@Nullable List<BusinessParty> otherParties)
		{
			this.otherParties = otherParties;
		}

		/**
		 * Removes all parties from the other parties list.
		 * <p>Notifies listeners registered for this type of the {@link BusinessPartyEvent event}.</p>
		 * @throws DetailException if data could not be deleted from the data store
		 */
		public void clearOtherParties() throws DetailException
		{
			for(BusinessParty party : getOtherParties())
				MapperRegistry.getInstance().getMapper(BusinessParty.class).delete(null, party.getPartyID());
			notifyListeners(new BusinessPartyEvent(otherParties, BusinessPartyEvent.OTHER_LIST_REMOVED));
			setOtherParties(null);
		}

		/**
		 * Adds party to other parties list and inserts it in the data store.
		 * @param party party to add
		 * @throws DetailException if party could not be inserted in the data store
		 */
		private void addOtherParty(BusinessParty party) throws DetailException
		{
			if(party != null)
			{
				MapperRegistry.getInstance().getMapper(BusinessParty.class).insert(null, party);
				party.setPartner(false);
				getOtherParties().add(party);
			}
		}

		/**
		 * Removes party from the list of business partners.
		 * @param party party to remove
		 * @return true if party was contained in list of other parties and removed from it
		 * @throws DetailException if party could not be deleted from the data store
		 */
		private boolean removeOtherParty(BusinessParty party) throws DetailException
		{
			boolean success = false;
			if(party != null)
			{
				try
				{//if(getOtherParties().contains(party))
					MapperRegistry.getInstance().getMapper(BusinessParty.class).delete(null, party.getPartyID());
					party.setPartner(false);
					success = getOtherParties().remove(party);
				}
				catch(DatabaseException e)
				{
					if(! "Document does not exist!".equals(e.getMessage())) //OK if document does not exist, otherwise throw e
						throw e;
				}
			}
			return success;
		}

		/**
		 * Gets the other party with passed ID.
		 * @param uuid following party's ID
		 * @return following party or {@code null} if there is no party with specified ID in the
		 * list of following parties
		 */
		public BusinessParty getOtherParty(String id)
		{
			try
			{
				return getOtherParties().stream().filter(party -> id.equals(party.getCoreParty().getPartyID())).findFirst().get();
			}
			catch(NoSuchElementException e) //if there is no party with passed uuid
			{
				return null;
			}
		}

		public List<BusinessParty> getArchivedParties()
		{
			if(archivedParties == null)
				archivedParties = new ArrayList<>();
			return archivedParties;
		}

		public void setArchivedParties(List<BusinessParty> archivedParties)
		{
			this.archivedParties = archivedParties;
		}

		/**
		 * Removes all parties from the archived parties list.
		 * <p>Notifies listeners registered for this type of the {@link BusinessPartyEvent event}.</p>
		 * @throws DetailException if data could not be deleted from the data store
		 */
		public void clearArchivedParties() throws DetailException
		{
			for(BusinessParty party : getArchivedParties())
				MapperRegistry.getInstance().getMapper(BusinessParty.class).delete(null, party.getPartyID());
			notifyListeners(new BusinessPartyEvent(archivedParties, BusinessPartyEvent.ARCHIVED_LIST_REMOVED));
			setArchivedParties(null);
		}

		/**
		 * Gets archived party with passed Party ID.
		 * @param uuid archived party's ID
		 * @return archived party or {@code null} if there is no party with specified ID in the list of
		 * archived parties
		 */
		public BusinessParty getArchivedParty(String id)
		{
			try
			{
				return getArchivedParties().stream().filter(party -> id.equals(party.getCoreParty().getPartyID())).findFirst().get();
			}
			catch(NoSuchElementException e) //if there is no party with passed uuid
			{
				return null;
			}
		}

		/**
		 * Checks whether the Party is archived. Check is based on the Party ID extracted from the passed {@code party} argument.
		 * So, different Party objects with the same Party ID will have the same result.
		 * @param party party in check
		 * @return true if Party is archived, false otherwise
		 */
		private boolean checkArchivedParty(BusinessParty party)
		{
			String partyID = party.getPartyID();
			return getArchivedParty(partyID) != null ? true : false;
		}

		/**
		 * Adds party to archived party list.
		 * @param party party to add
		 * @throws DetailException if party could not be inserted in the data store
		 */
		private void addArchivedParty(BusinessParty party) throws DetailException
		{
			if(party != null)
			{
				MapperRegistry.getInstance().getMapper(BusinessParty.class).insert(null, party);
				party.setArchived(true);
				getArchivedParties().add(party);
			}
		}

		/**
		 * Removes party from the list of archived parties.
		 * @param party party to remove
		 * @return true if party was contained in list of archived parties and removed from it
		 * @throws DetailException if party could not be deleted from the data store
		 */
		private boolean removeArchivedParty(BusinessParty party) throws DetailException
		{
			boolean success = false;
			if(party != null)
			{
				try
				{
					//MMM: TODO can be removed only if there are no correspondence/documents with the party
					MapperRegistry.getInstance().getMapper(BusinessParty.class).delete(null, party.getPartyID());
					party.setArchived(false);
					success = getArchivedParties().remove(party);
				}
				catch(DatabaseException e)
				{
					if(! "Document does not exist!".equals(e.getMessage())) //OK if document does not exist, otherwise throw e
						throw e;
				}
			}
			return success;
		}

		/**
		 * Adds party to the archived list.
		 * <p>Notifies listeners registered for this type of the {@link BusinessPartyEvent event}.</p>
		 * @param party party be archive
		 * @throws DetailException if party could not be added to the data store
		 */
		public void archiveParty(BusinessParty party) throws DetailException
		{
			if(party != null)
			{
				addArchivedParty(party);
				notifyListeners(new BusinessPartyEvent(party, BusinessPartyEvent.ARCHIVED_PARTY_ADDED));
			}
		}

		/**
		 * Deletes party from archived or deregistered list depending on which one it belongs and from
		 * the data store.
		 * <p>Notifies listeners registered for this type of the {@link BusinessPartyEvent event}.</p>
		 * @param party party be deleted
		 * @throws DetailException if party could not be deleted from the data store
		 */
		public void purgeParty(BusinessParty party) throws DetailException
		{
			if(party != null)
			{
				if(party.isArchived())
				{
					removeArchivedParty(party);
					notifyListeners(new BusinessPartyEvent(party, BusinessPartyEvent.ARCHIVED_PARTY_REMOVED));
				}
				else if(party.isDeregistered())
				{
					removeDeregisteredParty(party);
					notifyListeners(new BusinessPartyEvent(party, BusinessPartyEvent.DEREGISTERED_PARTY_REMOVED));
				}
			}
		}

		public List<BusinessParty> getDeregisteredParties()
		{
			if(deregisteredParties == null)
				deregisteredParties = new ArrayList<>();
			return deregisteredParties;
		}

		/**
		 * Removes all parties from the deregistered parties list.
		 * <p>Notifies listeners registered for this type of the {@link BusinessPartyEvent event}.</p>
		 * @throws DetailException if data could not be deleted from the data store
		 */
		public void clearDeregisteredParties() throws DetailException
		{
			for(BusinessParty party : getDeregisteredParties())
				MapperRegistry.getInstance().getMapper(BusinessParty.class).delete(null, party.getPartyID());
			notifyListeners(new BusinessPartyEvent(deregisteredParties, BusinessPartyEvent.DEREGISTERED_LIST_REMOVED));
			setDeregisteredParties(null);
		}

		public void setDeregisteredParties(List<BusinessParty> deregisteredParties)
		{
			this.deregisteredParties = deregisteredParties;
		}

		/**
		 * Gets the deregistered party with passed Party ID.
		 * @param uuid deregistered party's ID
		 * @return Deregistered party or {@code null} if there is no party with specified ID in the list of
		 * Deregistered parties
		 */
		public BusinessParty getDeregisteredParty(String id)
		{
			try
			{
				return getDeregisteredParties().stream().filter(party -> id.equals(party.getCoreParty().getPartyID())).findFirst().get();
			}
			catch(NoSuchElementException e) //if there is no party with passed uuid
			{
				return null;
			}
		}

		/**
		 * Checks if the Party is Deregistered. Check is based on the Party ID only extracted from passed Party argument.
		 * So different Party objects with the same Party ID will have the same result.
		 * @param party party in check
		 * @return true if Party is Deregistered, false otherwise
		 */
		private boolean checkDeregisteredParty(BusinessParty party)
		{
			String partyID = party.getPartyID();
			return getDeregisteredParty(partyID) != null ? true : false;
		}

		/**
		 * Adds to deregisterd party list and removes it from archived list if it is in it. Also, it
		 * updates the party in the data store.
		 * <p>Notifies listeners registered for this type of the {@link BusinessPartyEvent event}.</p>
		 * @param party party to add
		 * @throws DetailException if party could not be updated in the data store
		 */
		private void addDeregisteredParty(BusinessParty party) throws DetailException
		{
			if(party != null)
			{
				party.setDeregistered(true);
				MapperRegistry.getInstance().getMapper(BusinessParty.class).insert(null, party);
				getDeregisteredParties().add(party);
				notifyListeners(new BusinessPartyEvent(party, BusinessPartyEvent.DEREGISTERED_PARTY_ADDED));

				if(checkArchivedParty(party)) //MMM: superfluos??? remove down below will succeed or not
				{	//This way it is removed the object with the same Party ID, not just
					//the same object like in this call: getArchivedParties().remove(party)
					if(removeArchivedParty(party))
						//				if(getArchivedParties().remove(getArchivedParty(party.getPartyID())))
						notifyListeners(new BusinessPartyEvent(party, BusinessPartyEvent.ARCHIVED_PARTY_REMOVED));
				}
			}
		}

		/**
		 * Removes party from the list of Deregistered parties.
		 * @param party party to be removed
		 * @return true if party was contained in list of deregistered parties and removed from it
		 * @throws DetailException if party could not be deleted from the data store
		 */
		private boolean removeDeregisteredParty(BusinessParty party) throws DetailException
		{
			boolean success = false;
			if(party != null)
			{
				try
				{
					//MMM: TODO should be removed only if there are no correspondence/documents with the party
					MapperRegistry.getInstance().getMapper(BusinessParty.class).delete(null, party.getPartyID());
					success = getDeregisteredParties().remove(party);
					party.setDeregistered(false);
				}
				catch(DatabaseException e)
				{
					if(! "Document does not exist!".equals(e.getMessage())) //OK if document does not exist, otherwise throw e
						throw e;
				}
			}
			return success;
		}

		/**
		 * Adds party to the deregistered list and removes it from the lists it is contained in.
		 * <p>Notifies listeners registered for this type of the {@link BusinessPartyEvent event}.</p>
		 * @param party party be deregister
		 * @throws DetailException if party could not be updated in the data store
		 */
		public void deregisterParty(BusinessParty party) throws DetailException
		{
			if(party != null)
			{
				removeFollowingParty(party);
				addDeregisteredParty(party);
				notifyListeners(new BusinessPartyEvent(party, BusinessPartyEvent.DEREGISTERED_PARTY_ADDED));
			}
		}

		/*	*//**
		 * Deletes party from the deregistered list.
		 * <p>Notifies listeners registered for this type of the {@link BusinessPartyEvent event}.</p>
		 * @param party party be deleted
		 *//*
		 *@Deprecated
		public void deleteDeregisteredParty(BusinessParty party)
		{
			if(party != null)
			{
				removeDeregisteredParty(party);
				notifyListeners(new BusinessPartyEvent(party, BusinessPartyEvent.DEREGISTERED_PARTY_REMOVED));
			}
		}*/

		/**
		 * Gets the {@code List} of all parties that are followed by MyParty.
		 * @return list of all followed parties
		 */
		public List<BusinessParty> getFollowingParties()
		{
			setFollowingParties(); //not optimized because this merges two lists every time method is invoked
			return followingParties;
		}

		public void setFollowingParties(List<BusinessParty> followingParties)
		{
			this.followingParties = followingParties;
			separateFollowingParties(followingParties);
		}

		/**
		 * Populates the list of following parties by mearging the lists of business partner and
		 * other parties.
		 */
		public void setFollowingParties()
		{
			followingParties = new ArrayList<>();
			followingParties.addAll(getBusinessPartners());
			followingParties.addAll(getOtherParties());
		}

		/**
		 * Populates helper lists of {@link #businessPartners} and {@link #otherParties} from the
		 * list of all {@link #followingParties}.
		 * @param followingParties list of all {@link #followingParties}
		 */
		private void separateFollowingParties(List<BusinessParty> followingParties)
		{
			if(followingParties == null)
				businessPartners = otherParties = null;
			else
			{
				businessPartners = getBusinessPartners();
				otherParties = getOtherParties();
			}

			/*		for(BusinessParty bp : followingParties)
			{
				if(bp.isPartner())
					businessPartners.add(bp);
				else
					otherParties.add(bp);
			}*/
		}

		/**
		 * Gets the following party with passed ID.
		 * @param uuid following party's ID
		 * @return following party or {@code null} if there is no party with specified ID in the
		 * list of following parties
		 */
		public BusinessParty getFollowingParty(String id)
		{
			try
			{
				return getFollowingParties().stream().filter(party -> id.equals(party.getCoreParty().getPartyID())).findFirst().get();
			}
			catch(NoSuchElementException e) //if there is no party with passed uuid
			{
				return null;
			}
		}

		/**
		 * Adds party to the list of following parties and to one appropriate helper list while removing it
		 * from the other. If party is present in the list, this method overwrites it.
		 * <p>Notifies listeners registered for this type of the {@link BusinessPartyEvent event}.</p>
		 * @param party following party
		 */
		@Deprecated
		private void addFollowingParty(BusinessParty party)
		{
			if(party != null)
			{
				//removal is necessary when party is already in the list, but its partner status has been changed
				//so this method is called because Party should be moved to the other helper list. Cumbersome.
				getFollowingParties().remove(party); //if exists MMM: better to use Sets then?
				getFollowingParties().add(party);

				if(checkArchivedParty(party))// MMM:superflous???
				{	//if following party is archived sometime before, object representing that party andreferenced
					//in the archived list has the same Party ID
					//but is not the same object as the one passed to this method. There should be removed the
					//object with the same Party ID, not the same object like it would be done by this method call:
					//getArchivedParties().remove(party). That's way this is invoked:

					/*if(getArchivedParties().remove(getArchivedParty(party.getPartyID())))
						notifyListeners(new BusinessPartyEvent(party, BusinessPartyEvent.ARCHIVED_PARTY_REMOVED));*/

					//Not neccessary to separetly notify about deletion from the archived list; archived tree will be
					//updated by notification below
					getArchivedParties().remove(getArchivedParty(party.getPartyID()));

				}
				if(party.isPartner())
				{
					getBusinessPartners().add(party);
					getOtherParties().remove(party);
					notifyListeners(new BusinessPartyEvent(party, BusinessPartyEvent.BUSINESS_PARTNER_ADDED));
				}
				else
				{
					getBusinessPartners().remove(party);
					getOtherParties().add(party);
					notifyListeners(new BusinessPartyEvent(party, BusinessPartyEvent.OTHER_PARTY_ADDED));
				}
			}
		}

		/**
		 * Adds party to the list of following parties, and sets it as a business partner in respect of
		 * {@code partner} argument.
		 * @param party following party to add
		 * @param partner whether the party is a business partner
		 * @return {@code BusinessParty} with its {@code coreParty} field asigned with passed {@code party} argument
		 * or {@code null} if {@code party} argument has a {@code null} value
		 */
		@Deprecated
		private BusinessParty addFollowingParty(PartyType party, boolean partner)
		{
			BusinessParty newParty = null;
			if(party != null)
			{
				newParty = new BusinessParty();
				newParty.setCoreParty(party);
				newParty.setPartner(partner);
				addFollowingParty(newParty);
			}
			return newParty;
		}

		/**
		 * Removes party from proper list of following parties.
		 * @param party party to remove
		 * @return true if party was contained in some of the following lists and removed from it
		 * @throws DetailException if party could not be deleted from the data store
		 */
		private boolean removeFollowingParty(BusinessParty party) throws DetailException
		{
			boolean success = false;
			if(party != null)
			{
				if(party.isPartner())
					success = removeBusinessPartner(party);
				else
					success = removeOtherParty(party);
				party.setFollowing(false);
			}
			return success;
		}

		/**
		 * Checks whether the Party is followed. Check is based on the Party ID only extracted from passed
		 * Party argument. So, different Party objects with the same Party ID will have the same result.
		 * @param party party in check
		 * @return true if Party is followed, false otherwise
		 */
		private boolean checkFollowingParty(BusinessParty party)
		{
			String partyID = party.getPartyID();
			return getFollowingParty(partyID) != null ? true : false;
		}

		/**
		 * Adds party to the proper list of {@link BusinessParty BusinessParties} and removes it from the
		 * archived list if the party was archived before.
		 * <p>Notifies listeners registered for this type of the {@link BusinessPartyEvent event}.</p>
		 * @param party party to follow
		 * @throws DetailException if party could not be inserted or updated with the data store
		 */
		public void followParty(BusinessParty party) throws DetailException
		{
			party.setFollowing(true);

			//if following party is archived sometime before, object representing that party and referenced
			//in the archived list has the same Party ID
			//but is not the same object as the one passed to this method. There should be removed the
			//object with the same Party ID, not the same object like it would be done by method call:
			//getArchivedParties().remove(party). That's why this is invoked:
			final BusinessParty archivedParty = getArchivedParty(party.getPartyID());
			boolean archived = removeArchivedParty(archivedParty);
			if(archived)
				notifyListeners(new BusinessPartyEvent(archivedParty, BusinessPartyEvent.ARCHIVED_PARTY_REMOVED));
			//		getArchivedParties().remove(getArchivedParty(party.getPartyID()));

			boolean other = false, partner = false;
			if(party.isPartner())
			{
				//			if(getOtherParties().contains(party))
				other = removeOtherParty(party);
				addBusinessPartner(party);
				if(other)
					notifyListeners(new BusinessPartyEvent(party, BusinessPartyEvent.BUSINESS_PARTNER_TRANSFERED));
				else
					notifyListeners(new BusinessPartyEvent(party, BusinessPartyEvent.BUSINESS_PARTNER_ADDED));
			}
			else
			{
				partner = removeBusinessPartner(party);
				addOtherParty(party);
				if(partner)
					notifyListeners(new BusinessPartyEvent(party, BusinessPartyEvent.OTHER_PARTY_TRANSFERED));
				else
					notifyListeners(new BusinessPartyEvent(party, BusinessPartyEvent.OTHER_PARTY_ADDED));
			}
		}

		/**
		 * Unfollows the party by removing it from appropriate list and by sending the notification to tree model
		 * listener.
		 * @param party party to unfollow
		 * @throws DetailException if party could not be deleted from the data store
		 */
		public void unfollowParty(BusinessParty party) throws DetailException
		{
			boolean partner = party.isPartner();
			removeFollowingParty(party);
			addArchivedParty(party);
			if(partner)
				notifyListeners(new BusinessPartyEvent(party, BusinessPartyEvent.BUSINESS_PARTNER_REMOVED));
			else
				notifyListeners(new BusinessPartyEvent(party, BusinessPartyEvent.OTHER_PARTY_REMOVED));
		}

		public List<Search<PartyType>> getPartySearches()
		{
			if(partySearches == null)
				partySearches = new ArrayList<Search<PartyType>>();
			return partySearches;
		}

		public void setPartySearches(@Nullable List<Search<PartyType>> partySearches)
		{
			this.partySearches = partySearches;
		}

		public List<Search<CatalogueType>> getCatalogueSearches()
		{
			if(catalogueSearches == null)
				catalogueSearches = new ArrayList<Search<CatalogueType>>();
			return catalogueSearches;
		}

		public void setCatalogueSearches(@Nullable List<Search<CatalogueType>> catalogueSearches)
		{
			this.catalogueSearches = catalogueSearches;
		}

		/*	public List<CreateCatalogueProcess> getCatalogueProcesses()
		{
			if(catalogueProcesses == null)
				catalogueProcesses = new ArrayList<CreateCatalogueProcess>();
			return catalogueProcesses;
		}

		public void setCatalogueProcesses(List<CreateCatalogueProcess> catalogueProcesses)
		{
			this.catalogueProcesses = catalogueProcesses;
		}*/

		/**
		 * Gets {@link CatalogueCorrespondence}. If it has a {@code null} value new one is instantiated.
		 * @return {@code CatalogueCorrespondence}
		 */
		public CatalogueCorrespondence getCatalogueCorrespondence()
		{
			return catalogueCorrespondence;
		}

		public void setCatalogueCorrespondence(CatalogueCorrespondence catalogueCorrespondence)
		{
			this.catalogueCorrespondence = catalogueCorrespondence;
		}

		public List<BuyingCorrespondence> getBuyingCorrespondences()
		{
			if(buyingCorrespondences == null)
				buyingCorrespondences = new ArrayList<BuyingCorrespondence>();
			return buyingCorrespondences;
		}

		public void setBuyingCorrespondences(List<BuyingCorrespondence> correspondences)
		{
			this.buyingCorrespondences = correspondences;
		}

		/**
		 * Adds {@link BuyingCorrespondence} to the list of all buying correspondences.
		 * <p>Notifies listeners registered for this type of the {@link CorrespondenceEvent event}
		 * and {@link RutaClientFrameEvent event}.</p>
		 * @param correspondence correspondence to add
		 */
		public void addBuyingCorrespondence(BuyingCorrespondence correspondence)
		{
			getBuyingCorrespondences().add(correspondence);
			notifyListeners(new CorrespondenceEvent(correspondence, CorrespondenceEvent.CORRESPONDENCE_ADDED));
		}

		/**
		 * Removes {@link BuyingCorrespondence} from the list of all buying correspondences.
		 * <p>Notifies listeners registered for this type of the {@link CorrespondenceEvent event}
		 * and {@link RutaClientFrameEvent event}.</p>
		 * @param correspondence correspondence to add
		 */
		public void removeBuyingCorrespondence(BuyingCorrespondence correspondence)
		{
			if(correspondence != null)
			{
				getBuyingCorrespondences().add(correspondence);
				notifyListeners(new CorrespondenceEvent(correspondence, CorrespondenceEvent.CORRESPONDENCE_REMOVED));
			}
		}

		/**
		 * Stops working threads of all active {@link Correspondence}s.
		 * @throws InterruptedException
		 */
		public void stopCorrespondencies() throws InterruptedException
		{
			//		getCatalogueCorrespondence().stop();
			for(BuyingCorrespondence bCorr: getBuyingCorrespondences())
				if(bCorr.isActive())
					bCorr.stop();
		}

		/**
		 * Wait for threads of all active {@link Correspondence}s to stop.
		 * @throws InterruptedException
		 */
		public void waitCorrespondencesToStop() throws InterruptedException
		{
			//		getCatalogueCorrespondence().getStoppedSemaphore().acquire();
			for(BuyingCorrespondence bCorr: getBuyingCorrespondences())
				if(bCorr.isActive())
					bCorr.waitThreadStopped();
			//				bCorr.getStoppedSemaphore().acquire();
		}

		/**
		 * Checks whether My Party is registered with the CDR service.
		 * @return true if party is registered with the CDR service
		 */
		public boolean isRegisteredWithCDR()
		{
			//return getCoreParty().getPartyID() == null ? false : true;
			return hasCDRSecretKey();
		}

		/**
		 * Checks whether My Party is registered with the local datastore.
		 * @return true if party is registered with the local datastore
		 */
		public boolean isRegisteredWithLocalDatastore()
		{
			return localUser != null && localUser.getSecretKey() != null;
		}

		/**
		 * Checks whether My Party's cdr User has a secret key.
		 * @return true if party has a secret key
		 */
		public boolean hasCDRSecretKey()
		{
			return cdrUser != null && cdrUser.getSecretKey() != null;
		}

		public boolean isDirtyCatalogue()
		{
			return dirtyCatalogue;
		}

		public void setDirtyCatalogue(Boolean dirty)
		{
			dirtyCatalogue = dirty;
		}

		//MMM: maybe this method is not necessary, because of the above one which might be mandatory because of the JAXB serialization ???
		public void setDirtyCatalogue(boolean dirtyCatalogue)
		{
			this.dirtyCatalogue = dirtyCatalogue;
		}

		public RutaUser getLocalUser()
		{
			return localUser;
		}

		public void setLocalUser(RutaUser localUser)
		{
			this.localUser = localUser;
		}

		public RutaUser getCDRUser()
		{
			return cdrUser;
		}

		public void setCDRUser(RutaUser cdrUser)
		{
			this.cdrUser = cdrUser;
		}

		/**
		 * Generates {@link CatalogueType} document from {@link Item items} in the Product table. Method returns
		 * {@code null} if Catalogue does not have any item or some item has no name or is empty string.
		 * @param receiverParty receiver Party of the Catalogue
		 * @return catalogue or {@code null}
		 */
		public CatalogueType createCatalogue(Party receiverParty)
		{
			CatalogueType catalogue = null;
			int lineCnt = 0;
			final List<Item> myProducts = getProducts();
			if(checkProductNames(myProducts) && !myProducts.isEmpty())
			{
				//populating Catalogue document
				catalogue = new CatalogueType();
				/*			final IDType catID = new IDType();
				final String strID = String.valueOf(nextCatalogueID());
				catID.setValue(strID);
				catalogue.setID(catID);*/
				final String catID = String.valueOf(nextCatalogueID());
				catalogue.setID(catID);
				catalogue.setUUID(getCatalogueUUID());
				final XMLGregorianCalendar catDate = setCatalogueIssueDate();
				catalogue.setIssueDate(catDate);
				catalogue.setIssueTime(catDate);
				catalogue.setProviderParty((PartyType) getCoreParty());
				catalogue.setReceiverParty((PartyType) receiverParty);

				for(Item prod : myProducts)
				{
					final ItemType item = (ItemType) prod.clone();//MMM using only ItemType part of the Item ???
					final CatalogueLineType catLine = new CatalogueLineType();
					/*				final IDType catLineID = new IDType();
					catLineID.setValue(catID.getValue() + "-" + lineCnt++);
					catLine.setID(catLineID);*/
					catLine.setID(catID/*.getValue()*/ + "-" + lineCnt++);
					catLine.setItem(item);
					final List<ItemLocationQuantityType> itemLocationList = catLine.getRequiredItemLocationQuantity();
					final ItemLocationQuantityType itemLocation = new ItemLocationQuantityType();
					itemLocation.setPrice(prod.getPrice());
					itemLocationList.add(itemLocation);
					catalogue.addCatalogueLine(catLine);
				}
			}
			return catalogue;
		}

		/**
		 * Generates {@link CatalogueType} document that conforms to the {@code UBL} standard.
		 * @param receiverParty receiver Party of the {@code Catalogue}
		 * @return catalogue or {@code null} if catalogue does not conform to the {@code UBL}
		 */
		public CatalogueType produceCatalogue(Party receiverParty)
		{
			CatalogueType catalogue = createCatalogue(receiverParty);
			boolean valid = validateCatalogue(catalogue);
			return valid ? catalogue : null;
		}

		/**
		 * Validates whether {@link CatalogueType} comforms to the {@code UBL} standard.
		 * @param catalogue catalogue to check
		 * @return true if catalogue has a {@code non-null} value and is valid
		 */
		private boolean validateCatalogue(CatalogueType catalogue)
		{
			boolean valid = false;
			if(catalogue != null)
			{
				final IErrorList errors = UBL21Validator.catalogue().validate(catalogue);
				if(errors.containsAtLeastOneFailure())
					logger.error(errors.toString());
				else
					valid = true;
			}
			return valid;
		}

		/**
		 * Generates {@link CatalogueDeletionType Catalogue Deletion Document}.
		 * @return {@code CatalogueDeletionType Catalogue Deletion Document}
		 */
		public CatalogueDeletionType createCatalogueDeletion(Party CDRParty)
		{
			CatalogueDeletionType catalogueDeletion = new CatalogueDeletionType();
			String strID = String.valueOf(nextCatalogueDeletionID());
			IDType catDelID = new IDType(strID);
			catalogueDeletion.setID(catDelID);
			UUIDType catDelUUID = new UUIDType(getCatalogueDeletionUUID());
			catalogueDeletion.setUUID(catDelUUID);
			catalogueDeletion.setIssueDate(InstanceFactory.getDate());
			catalogueDeletion.setDeletedCatalogueReference(getCatalogueReference());
			catalogueDeletion.setProviderParty((PartyType) getCoreParty());
			catalogueDeletion.setReceiverParty((PartyType) CDRParty);
			return catalogueDeletion;
		}

		/**
		 * Creates object of {@code CatalogueReferenceType} representing reference to the latest
		 * created {@code CatalogueType} document.
		 * @return object representing reference to the lateset catalogue document
		 */
		private CatalogueReferenceType getCatalogueReference()
		{
			CatalogueReferenceType catRef = new CatalogueReferenceType();
			catRef.setID(String.valueOf(getCatalogueID()));
			catRef.setUUID(getCatalogueUUID());
			catRef.setIssueDate(getCatalogueIssueDate());
			return catRef;
		}

		/**
		 * Generates {@link OrderType} document that conforms to the {@code UBL} standard.
		 * @param sellerID correspondent's ID
		 * @return order or {@code null} if order does not conform to the {@code UBL} or correspondent
		 * is not a business partner
		 */
		public OrderType produceOrder(String sellerID)
		{
			boolean valid = false;
			final BusinessParty seller = getBusinessPartner(sellerID);
			OrderType order = null;
			if(seller != null)
			{
				order = createOrder(seller.getCoreParty());
				valid = validateOrder(order);
			}
			return valid ? order : null;
		}

		/**
		 * Generates {@link OrderType} document.
		 * @param seller {@link PartyType seller party} which is a receiver of the {@link OrderType order}
		 * @return order created {@link OrderType order}
		 */
		public OrderType createOrder(Party seller)
		{
			final OrderType order = new OrderType();
			final CustomerPartyType buyer = new CustomerPartyType();
			buyer.setParty(getCoreParty());
			order.setBuyerCustomerParty(buyer);
			//MMM put proper values for these recommended properties below
			final SupplierPartyType sellerParty = new SupplierPartyType();
			sellerParty.setParty(seller);
			order.setSellerSupplierParty(sellerParty);
			order.setID(UUID.randomUUID().toString());//MMM from MyParty's counter
			order.setUUID(UUID.randomUUID().toString());
			final XMLGregorianCalendar now = InstanceFactory.getDate();
			order.setIssueDate(now);
			order.setIssueTime(now);
			//		System.out.println(order.getIssueDate().toString());
			//		System.out.println(order.getIssueTime().toString());
			final OrderLineType orderLine = new OrderLineType();
			final LineItemType lineItem = new LineItemType();
			lineItem.setID(UUID.randomUUID().toString()); //MMM put ordered number here
			lineItem.setItem(getCatalogue().getCatalogueLineAtIndex(0).getItem());
			orderLine.setLineItem(lineItem);
			order.getOrderLine().add(orderLine);
			//MMM put some reference id? maybe of the correspondence into AdditionalDocumentReference
			return order;
		}

		/**
		 * Validates whether {@link OrderType} comforms to the {@code UBL} standard.
		 * @param order order to check
		 * @return true if order has a {@code non-null} value and is valid
		 */
		private boolean validateOrder(OrderType order)
		{
			boolean valid = false;
			if(order != null)
			{
				final IErrorList errors = UBL21Validator.order().validate(order);
				if(errors.containsAtLeastOneFailure())
					logger.error(errors.toString());
				else
					valid = true;
			}
			return valid;
		}

		/**
		 * Gets String representing UUID of the lastly created Catalogue Document.
		 * @return UUID of lastly created Catalogue as String
		 */
		private String getCatalogueUUID()
		{
			return getCoreParty().getPartyID()+ "CAT" + getCatalogueID();
		}

		/**
		 * Gets String representing UUID of the lastly created {@link CatalogueDeletionType} document.
		 * @return UUID of lastly created {@code CatalogueDeletion} as String
		 */
		private String getCatalogueDeletionUUID()
		{
			return getCoreParty().getPartyID()+ "CDL" + getCatalogueDeletionID();
		}

		/**
		 * Checks whether all {@link Item items} in the list have a name that differs from an empty string.
		 * @param myProducts list of items
		 * @return true if all item have a name, false otherwise
		 */
		private boolean checkProductNames(List<Item> myProducts)
		{
			boolean ok = true;
			for(Item prod : myProducts)
			{
				String productName = prod.getNameValue();
				if (productName == null || productName.equals(""))
				{
					ok = false;
					break;
				}
			}
			return ok;
		}

		/**
		 * Checks whether the cell value has changed. The value is considered not being changed
		 * if a new value is equal to an empty string and old value is a {@code null}. If it has
		 * changed {@code dirtyCatalogue} field is set to true.
		 * <p>This method <b>must</b> be called during every change of any cell of the {@code Product Table},
		 * because it is updating {@code dirtyCatalogue} flag.
		 * @param oldOne old value of the cell
		 * @param newOne new value of the cell
		 * @return true if the values differs, false otherwise
		 */
		private <T> boolean hasCellValueChanged(T oldOne, T newOne)
		{
			boolean changed = false;
			if(newOne != null)
			{
				if(newOne instanceof String && newOne.toString().equals("") && oldOne == null)
					changed = false;
				changed = !newOne.equals(oldOne);
			}
			dirtyCatalogue = dirtyCatalogue || changed;
			return changed;
		}

		public String getCDRPassword()
		{
			return cdrUser.getPassword();
		}

		public void setCDRPassword(String password)
		{
			cdrUser.setPassword(password);
		}

		public String getCDRUsername()
		{
			return cdrUser.getUsername();
		}

		public void setCDRUsername(String username)
		{
			cdrUser.setUsername(username);
		}

		public String getLocalPassword()
		{
			return localUser.getPassword();
		}

		public void setLocalPassword(String password)
		{
			localUser.setPassword(password);
		}

		public String getLocalUsername()
		{
			return localUser.getUsername();
		}

		public void setLocalUsername(String username)
		{
			localUser.setUsername(username);
		}

		/**
		 * Gets the boolean value telling if the {@code Party} data are in sync with the CDR.
		 * @return true if the {@code Party} is in sync with the CDR
		 */
		public boolean isDirtyMyParty()
		{
			return dirtyMyParty;
		}

		/**
		 * Sets the value of the boolean flag designating whether the {@code Party} object has been changed
		 * since it is retrieved from the database or updated with the CDR service. If the value is true
		 * that means the the local copy of the {@code Party} is out of sync with the CDR.
		 * @param dirtyMyParty boolean value to be set
		 */
		public void setDirtyMyParty(boolean dirtyMyParty)
		{
			this.dirtyMyParty = dirtyMyParty;
		}

		/**
		 * Returns secret key of the remote {@link User} used for the SOAP message digest.
		 * @return secret key or {@code null} if it is not defined
		 */
		public String getCDRSecretKey()
		{
			return "".equals(cdrUser.getSecretKey()) ? null : cdrUser.getSecretKey();
		}

		/**
		 * Sets the value of the Party's remote {@link User} secret key used for the SOAP message digest encryption and decryption.
		 * @param secretKey secret key to set
		 */
		public void setCDRSecretKey(String secretKey)
		{
			cdrUser.setSecretKey(secretKey);
		}

		/**
		 * Returns secret key of the local {@link User} used for the local data store connection.
		 * @return secret key or {@code null} if it is not defined
		 */
		public String getLocalSecretKey()
		{
			return "".equals(localUser.getSecretKey()) ? null : localUser.getSecretKey();
		}

		/**
		 * Sets the value of the local {@link User}'s secret key used for the local data store connection.
		 * @param secretKey secret key to set
		 */
		public void setLocalSecretKey(String secretKey)
		{
			localUser.setSecretKey(secretKey);
		}

		/**
		 * Gets the boolean value that tells whether the Catalogue should be inserted in or updated with the CDR service.
		 * @return true if Catalogue should be deposited in the CDR service for the first time i.e inserted,
		 * false otherwise i.e. updated.
		 */
		public boolean isInsertMyCatalogue()
		{
			return insertMyCatalogue;
		}

		/**
		 * Sets the value of the field that tells whether the Catalogue should be inserted in or updated with the CDR service.
		 * @param insertMyCatalogue true if Catalogue should be deposited in the CDR service for the first time i.e. inserted,
		 * false otherwise i.e. updated.
		 */
		public void setInsertMyCatalogue(boolean insertMyCatalogue)
		{
			this.insertMyCatalogue = insertMyCatalogue;
		}

		public long getSearchNumber()
		{
			return searchNumber;
		}

		public void setSearchNumber(long searchNumber)
		{
			this.searchNumber = searchNumber;
		}

		/**
		 * Register My Party as a following party of a self.
		 * <p>Notifies listeners registered for this type of the {@link BusinessPartyEvent event}.</p>
		 */
		public void followMyself()
		{
			myFollowingParty = new BusinessParty();
			myFollowingParty.setRecentlyUpdated(true);
			myFollowingParty.setTimestamp(InstanceFactory.getDate());
			//copying name value because it is necessary for the tree node display, because
			//the node has to be displayed before My Party data is retrieved from the CDR
			//and the name string is used for the node display
			myFollowingParty.setPartySimpleName(getPartySimpleName());
			//copying Party ID value because it is necessary for the database insert, which
			//could be done before My Party is retrieved from the CDR
			myFollowingParty.setPartyID(getPartyID());
			notifyListeners(new BusinessPartyEvent(myFollowingParty, BusinessPartyEvent.MY_FOLLOWING_PARTY_ADDED));
		}

		/**
		 * Deregister My Party as a following party of a self.
		 * <p>Notifies listeners registered for this type of the {@link BusinessPartyEvent event}.</p>
		 */
		public void unfollowMyself()
		{
			notifyListeners(new BusinessPartyEvent(myFollowingParty, BusinessPartyEvent.MY_FOLLOWING_PARTY_REMOVED));
			myFollowingParty = null;
		}

		/**
		 * Removes {@link Item} from the product list and from the database.
		 * @param row product's index
		 * @throws Exception if {@code Item} could not be deleted from the database
		 */
		public void removeProduct(final int row) throws Exception
		{
			final String id = products.get(row).getID().getValue();
			MapperRegistry.getInstance().getMapper(Item.class).delete(null, id);
			products.remove(row);
			dirtyCatalogue = true;
		}

		/**
		 * Adds passed {@link Item} to the product list and inserts it in the database.
		 * @param item {@link Item} to be added
		 * @throws DetailException if {@code Item item} could not be inserted in the database
		 */
		public void addProduct(final Item item) throws DetailException
		{
			MapperRegistry.getInstance().getMapper(Item.class).insert(null, item);
			products.add(item);
			dirtyCatalogue = true;
		}

		/**
		 * Adds new empty {@link Item} to the product list. Sets unique product ID and
		 * unique {@code Item}'s ID used for its database identification.
		 * @param index {@code Item item}'s index in the list
		 * @throws DetailException if product could not be inserted in the database
		 */
		public void addNewEmptyProduct(final int index) throws DetailException
		{
			final Item item = new Item();
			item.setID(UUID.randomUUID().toString());
			addProduct(item);
			try
			{
				setProductID(index, getNextProductID());
			}
			catch (ProductException e) //should not happen
			{
				logger.error("Product ID si not eligable", e);
			}
		}

		/**
		 * Clears all data of My Party that are related to the CDR. This method is usually called
		 * during deregistration from the CDR service.
		 * @throws DetailException if data could not be deleted from the data store
		 */
		public void clearCDRRelatedData() throws DetailException
		{
			setDirtyMyParty(true);
			setDirtyCatalogue(true);
			setInsertMyCatalogue(true);
			setCDRSecretKey(null);
			setCDRUsername(null);
			setCDRPassword(null);
			getCoreParty().setPartyID(null);
			setCatalogueID(0);
			setCatalogueDeletionID(0);
			removeCatalogueIssueDate();
			unfollowMyself();
			clearParties();
		}

		/**
		 * @param search
		 */
		/*	private void addPartySearch(final PartySearch search)
		{
			getPartySearches().add(0, search);
		}*/

		/**
		 * Adds {@link PartySearch} to a party search list and inserts it to the data store.
		 * <p>Notifies listeners registered for this type of the {@link SearchEvent event}.</p>
		 * @param search search to add
		 * @throws DetailException if search could not be inserted in the data store
		 */
		public void addPartySearch(Search<PartyType> search) throws DetailException
		{
			if(search != null)
			{
				MapperRegistry.getInstance().getMapper(PartySearch.class).insert(null, (PartySearch) search);
				getPartySearches().add(0, search);
				notifyListeners(new SearchEvent(search, SearchEvent.PARTY_SEARCH_ADDED));
			}
		}

		/**
		 * Updates {@link PartySearch} in the collection of all party searches and in the data store.
		 * <p>Notifies listeners registered for this type of the {@link SearchEvent event}.</p>
		 * @param search search to update
		 * @return true if search was contained in list of party searches and updated
		 * @throws DetailException if search could not be updated in the data store
		 */
		public boolean updatePartySearch(final Search<PartyType> search) throws DetailException
		{
			boolean success = false;
			if(search != null)
			{
				try
				{
					MapperRegistry.getInstance().getMapper(PartySearch.class).update(null, (PartySearch) search);
					success = true; //getPartySearches().remove(newSearch) || getPartySearches().add(newSearch); //MMM ??? is it necessary at all
					notifyListeners(new SearchEvent(search, SearchEvent.PARTY_SEARCH_UPDATED));
				}
				catch(DatabaseException e)
				{
					if(! "Document does not exist!".equals(e.getMessage())) //OK if document does not exist, otherwise throw e
						throw e;
				}
			}
			return success;
		}

		/**
		 * Removes {@link PartySearch} from the collection of all party searches and from the data store.
		 * <p>Notifies listeners registered for this type of the {@link SearchEvent event}.</p>
		 * @param search search to delete
		 * @return true if search was contained in list of party searches and removed from it
		 * @throws DetailException if search could not be deleted from the data store
		 */
		public boolean removePartySearch(final Search<PartyType> search) throws DetailException
		{
			boolean success = false;
			if(search != null)
			{
				try
				{
					MapperRegistry.getInstance().getMapper(PartySearch.class).delete(null, search.getId());
					success = getPartySearches().remove(search);
					notifyListeners(new SearchEvent(search, SearchEvent.PARTY_SEARCH_REMOVED));
				}
				catch(DatabaseException e)
				{
					if(! "Document does not exist!".equals(e.getMessage())) //OK if document does not exist, otherwise throw e
						throw e;
				}
			}
			return success;
		}

		/**
		 * Removes {@link Search} from the proper collection of all searches and from the data store.
		 * <p>Notifies listeners registered for this type of the {@link SearchEvent event}.</p>
		 * @param search search to delete
		 * @return true if search was contained in an list of searches and removed from it
		 * @throws DetailException if search could not be deleted from the data store
		 */
		@SuppressWarnings("unchecked")
		public boolean removeSearch(final Search<?> search) throws DetailException
		{
			boolean success = false;
			if(search instanceof PartySearch)
				success = removePartySearch((Search<PartyType>) search);
			else if(search instanceof CatalogueSearch)
				success = removeCatalogueSearch((Search<CatalogueType>) search);
			return success;
		}

		/**
		 * Adds {@link PartySearch} to the collection of all party searches.
		 * <p>Notifies listeners registered for this type of the {@link SearchEvent event}.</p>
		 * @param search
		 * @throws DetailException if party could not be inserted to the data store
		 */
		@Deprecated
		public void listPartySearch(final PartySearch search) throws DetailException
		{
			addPartySearch(search);
			notifyListeners(new SearchEvent(search, SearchEvent.PARTY_SEARCH_ADDED));
		}

		/**
		 * Removes {@link PartySearch} from the collection of all party searches.
		 * <p>Notifies listeners registered for this type of the {@link SearchEvent event}.</p>
		 * @param search
		 * @throws DetailException if search could not be deleted from the data store
		 */
		@Deprecated
		public void delistPartySearch(final PartySearch search) throws DetailException
		{
			removePartySearch(search);
			notifyListeners(new SearchEvent(search, SearchEvent.PARTY_SEARCH_REMOVED));
		}

		/**
		 * Updates {@link PartySearch} in the collection of all party searches.
		 * <p>Notifies listeners registered for this type of the {@link SearchEvent event}.</p>
		 * @param search
		 * @throws DetailException if search could not be updated to the data store
		 */
		@Deprecated
		public void relistPartySearch(final PartySearch search) throws DetailException
		{
			removePartySearch(search);
			addPartySearch(search);
			notifyListeners(new SearchEvent(search, SearchEvent.PARTY_SEARCH_UPDATED));
		}

		/**
		 * Adds {@link PartySearch} to the collection of all party searches.
		 * @param search
		 */
		/*	private void addCatalogueSearch(final Search<CatalogueType> search)
		{
			getCatalogueSearches().add(0, search);
		}*/

		/**
		 * Adds {@link CatalogueSearch} to a catalogue search list and inserts it to the data store.
		 * <p>Notifies listeners registered for this type of the {@link SearchEvent event}.</p>
		 * @param newSearch search to add
		 * @throws DetailException if search could not be inserted in the data store
		 */
		public void addCatalogueSearch(Search<CatalogueType> newSearch) throws DetailException
		{
			if(newSearch != null)
			{
				MapperRegistry.getInstance().getMapper(CatalogueSearch.class).insert(null, (CatalogueSearch) newSearch);
				getCatalogueSearches().add(0, newSearch);
				notifyListeners(new SearchEvent(newSearch, SearchEvent.CATALOGUE_SEARCH_ADDED));
			}
		}

		/**
		 * Removes {@link CatalogueSearch} from the collection of all catalogue searches and from the data store.
		 * <p>Notifies listeners registered for this type of the {@link SearchEvent event}.</p>
		 * @param search search to delete
		 * @return true if search was contained in list of catalogue searches and removed from it
		 * @throws DetailException if search could not be deleted from the data store
		 */
		public boolean removeCatalogueSearch(final Search<CatalogueType> search) throws DetailException
		{
			boolean success = false;
			if(search != null)
			{
				try
				{
					MapperRegistry.getInstance().getMapper(CatalogueSearch.class).delete(null, search.getId());
					success = getCatalogueSearches().remove(search);
					notifyListeners(new SearchEvent(search, SearchEvent.CATALOGUE_SEARCH_REMOVED));
				}
				catch(DatabaseException e)
				{
					if(! "Document does not exist!".equals(e.getMessage())) //OK if document does not exist, otherwise throw e
						throw e;
				}
			}
			return success;
		}

		/**
		 * Updates {@link CatalogueSearch} in the collection of all catalogue searches and in the data store.
		 * <p>Notifies listeners registered for this type of the {@link SearchEvent event}.</p>
		 * @param search search to update
		 * @return true if search was contained in list of catalogue searches and updated
		 * @throws DetailException if search could not be updated in the data store
		 */
		public boolean updateCatalogueSearch(final Search<CatalogueType> search) throws DetailException
		{
			boolean success = false;
			if(search != null)
			{
				try
				{
					MapperRegistry.getInstance().getMapper(CatalogueSearch.class).update(null, (CatalogueSearch) search);
					success = true; //getCatalogueSearches().remove(newSearch) || getCatalogueSearches().add(newSearch); //MMM ??? is it necessary at all
					notifyListeners(new SearchEvent(search, SearchEvent.CATALOGUE_SEARCH_UPDATED));
				}
				catch(DatabaseException e)
				{
					if(! "Document does not exist!".equals(e.getMessage())) //OK if document does not exist, otherwise throw e
						throw e;
				}
			}
			return success;
		}

		/**
		 * Updates {@link Search} in the appropriate collection of searches and in the data store.
		 * <p>Notifies listeners registered for this type of the {@link SearchEvent event}.</p>
		 * @param search search to update
		 * @return true if search was contained in list of catalogue searches and updated
		 * @throws DetailException if search could not be updated in the data store
		 */
		@SuppressWarnings("unchecked")
		public boolean updateSearch(final Search<?> search) throws DetailException
		{
			boolean success = false;
			if(search instanceof PartySearch)
				success = updatePartySearch((Search<PartyType>) search);
			else if(search instanceof CatalogueSearch)
				success = updateCatalogueSearch((Search<CatalogueType>) search);
			return success;
		}

		/**
		 * Adds {@link CatalogueSearch} to the collection of all party searches.
		 * <p>Notifies listeners registered for this type of the {@link SearchEvent event}.</p>
		 * @param newSearch
		 * @throws DetailException
		 */
		public void listCatalogueSearch(final Search<CatalogueType> newSearch) throws DetailException
		{
			addCatalogueSearch(newSearch);
			notifyListeners(new SearchEvent(newSearch, SearchEvent.CATALOGUE_SEARCH_ADDED));
		}

		/**
		 * Removes {@link CatalogueSearch} from the collection of all party searches.
		 * <p>Notifies listeners registered for this type of the {@link SearchEvent event}.</p>
		 * @param newSearch
		 * @throws DetailException
		 */
		public void delistCatalogueSearch(final Search<CatalogueType> newSearch) throws DetailException
		{
			removeCatalogueSearch(newSearch);
			notifyListeners(new SearchEvent(newSearch, SearchEvent.CATALOGUE_SEARCH_REMOVED));
		}

		/**
		 * Updates {@link CatalogueSearch} in the collection of all party searches.
		 * <p>Notifies listeners registered for this type of the {@link SearchEvent event}.</p>
		 * @param newSearch
		 * @throws DetailException
		 */
		public void relistCatalogueSearch(final Search<CatalogueType> newSearch) throws DetailException
		{
			removeCatalogueSearch(newSearch);
			addCatalogueSearch(newSearch);
			notifyListeners(new SearchEvent(newSearch, SearchEvent.CATALOGUE_SEARCH_UPDATED));
		}

		/**
		 * Places {@link CatalogueType catalogue} document on the proper place in the data model setting it
		 * to its {@link BusinessParty owner}.
		 * <p>Notifies listeners registered for this type of the {@link RutaClientFrameEvent event}.</p>
		 * @param catalogue catalogue to place
		 */
		public void processDocBoxCatalogue(CatalogueType catalogue)
		{
			PartyType provider = catalogue.getProviderParty();

			if(BusinessParty.sameParties(this, provider))
			{
				myFollowingParty.setCatalogue(catalogue);
				myFollowingParty.setRecentlyUpdated(true);
				notifyListeners(new BusinessPartyEvent(myFollowingParty, BusinessPartyEvent.CATALOGUE_UPDATED));
			}
			else
			{
				final List<BusinessParty> followingParties = getFollowingParties();
				for(BusinessParty bParty: followingParties)
					if(BusinessParty.sameParties(bParty, provider))
					{
						bParty.setCatalogue(catalogue);
						bParty.setRecentlyUpdated(true);
						notifyListeners(new BusinessPartyEvent(bParty, BusinessPartyEvent.CATALOGUE_UPDATED));
						break;
					}
			}
		}

		/**
		 * Places {@link PartyType party} object on the proper place in the data model by setting it
		 * to its {@link BusinessParty owner}.
		 * <p>Notifies listeners registered for these events: {@link RutaClientFrameEvent} and
		 * {@link BusinessPartyEvent}.</p>
		 * @param party party to place
		 */
		public void processDocBoxParty(PartyType party)
		{
			if(BusinessParty.sameParties(this, party))
			{
				final String oldName = myFollowingParty.getPartySimpleName();
				myFollowingParty.setCoreParty(party);
				myFollowingParty.setRecentlyUpdated(true);
				notifyListeners(new BusinessPartyEvent(myFollowingParty, BusinessPartyEvent.PARTY_UPDATED));
			}
			else
			{
				final List<BusinessParty> followingParties = getFollowingParties();
				for(BusinessParty bParty: followingParties)
				{
					if(BusinessParty.sameParties(bParty, party))
					{
						final String oldName = bParty.getPartySimpleName();
						bParty.setCoreParty(party);
						bParty.setRecentlyUpdated(true);
						notifyListeners(new BusinessPartyEvent(bParty, BusinessPartyEvent.PARTY_UPDATED));
						break;
					}
				}
			}
		}

		/**
		 * Places {@link CatalogueDeletionType catalogue deletion} document on the proper place in the data model
		 * by deleting respective {@link CatalogueType catalogue} from its {@link BusinessParty owner}.
		 * <p>Notifies listeners registered for this type of the {@link RutaClientFrameEvent event}.</p>
		 * @param catDeletion {@link CatalogueDeletionType catalogue deletion} to process
		 */
		public void processDocBoxCatalogueDeletion(CatalogueDeletionType catDeletion)
		{
			PartyType provider = catDeletion.getProviderParty();
			if(BusinessParty.sameParties(this, provider))
			{
				myFollowingParty.setCatalogue(null);
				myFollowingParty.setRecentlyUpdated(true);
				notifyListeners(new BusinessPartyEvent(myFollowingParty, BusinessPartyEvent.CATALOGUE_UPDATED));
			}
			else
			{
				final List<BusinessParty> followingParties = getFollowingParties();
				for(BusinessParty bParty: followingParties)
				{
					if(BusinessParty.sameParties(bParty, provider))
					{
						bParty.setCatalogue(null);
						bParty.setRecentlyUpdated(true);
						notifyListeners(new BusinessPartyEvent(bParty, BusinessPartyEvent.CATALOGUE_UPDATED));
						break;
					}
				}
			}
		}

		/**
		 * Processes {@link DeregistrationNotice deregistration notice} document by moving
		 * {@link BusinessParty party} specified in it to the list of deregistered parties.
		 * <p>Notifies listeners registered for this type of the {@link RutaClientFrameEvent event}.</p>
		 * @param notice deregistration notice to process
		 * @throws DetailException if party could not be updated in the data store
		 */
		public void processDocBoxDeregistrationNotice(DeregistrationNotice notice) throws DetailException
		{
			PartyType party = notice.getParty();
			final List<BusinessParty> followingParties = getFollowingParties();
			for(BusinessParty bParty: followingParties)
				if(BusinessParty.sameParties(bParty, party))
				{
					//				notifyListeners(new RutaClientFrameEvent(bParty, RutaClientFrameEvent.SELECT_NEXT));
					deregisterParty(bParty);
					bParty.setRecentlyUpdated(true);
					notifyListeners(new BusinessPartyEvent(bParty, BusinessPartyEvent.PARTY_MOVED));
					break;
				}
		}

		/**
		 * Processes {@link OrderType order} document by appending it to a proper {@link BuyingCorrespondence correspondence}.
		 * <p>Notifies listeners registered for this type of the {@link RutaClientFrameEvent event}.</p>
		 * @param order order to process
		 * @return TODO
		 */
		public String processDocBoxOrder(OrderType order) throws DetailException
		{
			final String correspondentID = order.getBuyerCustomerParty().getParty().getPartyIdentificationAtIndex(0).getIDValue();
			final PartyType correspondentParty = order.getBuyerCustomerParty().getParty();
			final BuyingCorrespondence newCorr = BuyingCorrespondence.newInstance(client, correspondentParty, correspondentID, false);
			((SellerOrderingProcess) newCorr.getState()).setOrder(order);
			addBuyingCorrespondence(newCorr);
			newCorr.addDocumentReference(order.getBuyerCustomerParty().getParty(), order.getUUIDValue(),
					order.getIDValue(), order.getIssueDateValue(), order.getIssueTimeValue(),
					order.getClass().getName(), this);
			newCorr.start();
			newCorr.setRecentlyUpdated(true);
			return newCorr.getName();
		}

		/**
		 * Processes {@link OrderType order} document by appending it to proper {@link BuyingCorrespondence correspondence}.
		 * <p>Notifies listeners registered for this type of the {@link RutaClientFrameEvent event}.</p>
		 * @param orderResponseSimple order to process
		 * @throws DetailException if proper {@link Correspondence} could not be found
		 */
		public void processDocBoxOrderResponseSimple(OrderResponseSimpleType orderResponseSimple) throws DetailException
		{
			final String correspondentID = orderResponseSimple.getSellerSupplierParty().getParty().
					getPartyIdentificationAtIndex(0).getIDValue();
			final IDType orderID = orderResponseSimple.getOrderReference().getDocumentReference().getID();
			final Correspondence corr = findCorrespondence(correspondentID, orderID);
			if(corr == null)
				throw new DetailException("Matching correspondence could not be found.");
			else
			{
				corr.addDocumentReference(orderResponseSimple.getSellerSupplierParty().getParty(),
						orderResponseSimple.getUUIDValue(), orderResponseSimple.getIDValue(),
						orderResponseSimple.getIssueDateValue(), orderResponseSimple.getIssueTimeValue(),
						orderResponseSimple.getClass().getName(), this);
				((BuyerOrderingProcess) corr.getState()).setOrderResponseSimple(orderResponseSimple);
				corr.setRecentlyUpdated(true);
				if(corr.isAlive())
					corr.proceed();
				else
					corr.start();
			}
		}

		/**
		 * Executes the process of creation and update of {@link CatalogueType} in the CDR.
		 */
		public void executeCreateCatalogueProcess()
		{
			if(catalogueCorrespondence == null)
			{
				catalogueCorrespondence = CatalogueCorrespondence.newInstance(client);
				catalogueCorrespondence.setCreateCatalogueProcess(true);
				catalogueCorrespondence.setState(CreateCatalogueProcess.newInstance(client));
				catalogueCorrespondence.start();
				notifyListeners(new CorrespondenceEvent(catalogueCorrespondence, CorrespondenceEvent.CORRESPONDENCE_ADDED));
			}
			else
			{
				catalogueCorrespondence.setCreateCatalogueProcess(true);
				catalogueCorrespondence.proceed();
			}
		}

		/**
		 * Executes the process of deletion of {@link CatalogueType} in the CDR.
		 */
		public void executeDeleteCatalogueProcess()
		{
			if(catalogueCorrespondence == null)
			{
				catalogueCorrespondence = CatalogueCorrespondence.newInstance(client);
				catalogueCorrespondence.setCreateCatalogueProcess(false);
				catalogueCorrespondence.setState(DeleteCatalogueProcess.newInstance(client));
				catalogueCorrespondence.start();
				notifyListeners(new CorrespondenceEvent(catalogueCorrespondence, CorrespondenceEvent.CORRESPONDENCE_ADDED));
			}
			else
			{
				catalogueCorrespondence.setCreateCatalogueProcess(false);
				catalogueCorrespondence.proceed();
			}
		}

		/**
		 * Finds all {@link Correspondence}s with a particular party.
		 * @param correspondentID ID of the correspondent party
		 * @return list of correspondences or {@code null} if there are none
		 */
		public List<Correspondence> findAllCorrespondences(final String correspondentID)
		{
			final List<Correspondence> corrs = getBuyingCorrespondences().stream().
					filter(bCorr -> correspondentID.equals(bCorr.getCorrespondentID())).
					collect(Collectors.toList());
			return corrs.isEmpty() ? null : corrs;
		}

		/**
		 * Finds {@link Correspondence} with particular party that contains particular document.
		 * @param correspondentID ID of the correspondent party
		 * @param docID {@link IDType id} of the document
		 * @return {@code Correspondence} or {@code null} if no correspondence is found
		 */
		public Correspondence findCorrespondence(final String correspondentID, final IDType docID)
		{
			final List<Correspondence> corrs = findAllCorrespondences(correspondentID);
			final List<Correspondence> foundCorrs;
			Correspondence corr = null;
			if(corrs != null)
			{
				foundCorrs = corrs.stream().filter(elem -> elem.isActive()).
						filter(elem -> ! elem.getDocumentReferences().stream().
								filter(ref -> docID.getValue().equals(ref.getIDValue())).collect(Collectors.toList()).isEmpty()).
						collect(Collectors.toList());
				if(foundCorrs.size() == 1)
					corr = foundCorrs.get(0);
			}
			return corr;
		}

	}