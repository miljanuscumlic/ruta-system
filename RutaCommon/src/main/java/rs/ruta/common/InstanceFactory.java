package rs.ruta.common;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nullable;
import javax.swing.Icon;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.error.list.IErrorList;
import com.helger.ubl21.UBL21Validator;

import oasis.names.specification.ubl.schema.xsd.applicationresponse_21.ApplicationResponseType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.DocumentReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.DocumentResponseType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.OrderReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.ResponseType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.TaxCategoryType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.TaxSchemeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.IDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.NoteType;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;
import oasis.names.specification.ubl.schema.xsd.order_21.OrderType;
import oasis.names.specification.ubl.schema.xsd.ordercancellation_21.OrderCancellationType;
import oasis.names.specification.ubl.schema.xsd.orderchange_21.OrderChangeType;
import oasis.names.specification.ubl.schema.xsd.orderresponse_21.OrderResponseType;
import oasis.names.specification.ubl.schema.xsd.orderresponsesimple_21.OrderResponseSimpleType;

/**
 * Class factory that instantiate different objects and have some convinient check methods.
 */
public final class InstanceFactory
{
	public static final String CURRENCY_CODE = "RSD";
	public static final String TAX_CATEGORY_0 = "0";
	public static final String TAX_CATEGORY_10 = "10";
	public static final String TAX_CATEGORY_20 = "20";
	public static final TaxSchemeType TAX_SCHEME = new TaxSchemeType();
	public static final Map<String, TaxCategoryType> taxCategories;

	public static final String APP_RESPONSE_POSITIVE = "POSITIVE";
	public static final String APP_RESPONSE_NEGATIVE = "NEGATIVE";

	public static final String ACCEPT_ORDER = "Accept Order";
	public static final String ADD_DETAIL = "Add Detail";
	public static final String DECIDE_LATER = "Decide Later";
	public static final String REJECT_ORDER = "Reject Order";
	public static final String CANCEL_ORDER = "Cancel Order";
	public static final String CHANGE_ORDER = "Change Order";
	public static final String ACCEPT_INVOICE = "Accept Invoice";
	public static final String MODIFY_INVOICE = "Modify Invoice";
	public static final String REJECT_INVOICE = "Reject Invoice";
	public static final String ACCEPT = "Accept";
	private static final Logger logger = LoggerFactory.getLogger("rs.ruta.common");

	static
	{
		TAX_SCHEME.setName("Serbian tax scheme");
		TAX_SCHEME.setCurrencyCode(CURRENCY_CODE);

		Map<String, TaxCategoryType> tempMap = new HashMap<String, TaxCategoryType>();
		TaxCategoryType tc0 = new TaxCategoryType();
		IDType id0 = new IDType(TAX_CATEGORY_0);
		tc0.setID(id0);
		tc0.setPercent(new BigDecimal(TAX_CATEGORY_0));
		tc0.setTaxScheme(TAX_SCHEME);
		tempMap.put(TAX_CATEGORY_0, tc0);

		TaxCategoryType tc1 = new TaxCategoryType();
		IDType id1 = new IDType(TAX_CATEGORY_10);
		tc1.setID(id1);
		tc1.setPercent(new BigDecimal(TAX_CATEGORY_10));
		tc1.setTaxScheme(TAX_SCHEME);
		tempMap.put(TAX_CATEGORY_10, tc1);

		TaxCategoryType tc2 = new TaxCategoryType();
		IDType id2 = new IDType(TAX_CATEGORY_20);
		tc2.setID(id2);
		tc2.setPercent(new BigDecimal(TAX_CATEGORY_20));
		tc2.setTaxScheme(TAX_SCHEME);
		tempMap.put(TAX_CATEGORY_20, tc2);

		taxCategories = Collections.unmodifiableMap(tempMap);
	}

	public static String[] getTaxCategories()
	{
		return new String[] {TAX_CATEGORY_0, TAX_CATEGORY_10, TAX_CATEGORY_20};
	}

	/**
	 * Returns {@code null} or the value of the property of the object. Value of the property
	 * can be of the type {@code String}, {@code BigDecimal}, or some other type.
	 * @param <T> type of object which extracting metod is being called
	 * @param <U> type of the return value of extracting method
	 * @param property property of the object whose value is get
	 * @param extractor function which has two type parameters: of the type T or some supertype of the T,
	 * and U or some subtype of U
	 * @return value of the type U which is the result of the called passed method for getting the value
	 */
	public static <T, U> U getPropertyOrNull(T property, Function<? super T, ? extends U> extractor)
	{
		return property != null ? extractor.apply(property) : null;
	}

	public static LocalDate getLocalDate(XMLGregorianCalendar xgc)
	{
		LocalDate date = null;
		if(xgc != null)
		{
			date = LocalDate.of(xgc.getYear(), xgc.getMonth(), xgc.getDay());
		}
		//System.out.println(date);
		return date;
	}

	public static LocalDateTime getLocalDateTime(XMLGregorianCalendar xgc)
	{
		LocalDateTime date = null;
		if(xgc != null)
		{
			date = LocalDateTime.of(xgc.getYear(), xgc.getMonth(), xgc.getDay(), xgc.getHour(), xgc.getMinute(), xgc.getSecond());
		}
		//System.out.println(date);
		return date;
	}

	/**
	 * Transforms {@link XMLGregorianCalendar} to a {@code String} that is in the form of {@link LocalDate} object.
	 * @param xgc date and time
	 * @return string representation of a date
	 */
	public static String getLocalDateAsString(XMLGregorianCalendar xgc)
	{
		LocalDate date = InstanceFactory.getLocalDate(xgc);
		return date != null ? DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).format(date) : null;
	}

	/**
	 * Transforms {@link XMLGregorianCalendar} to a {@code String} that is in the form of {@link LocalDateTime} object.
	 * @param xgc date and time
	 * @return string representation of a date
	 */
	public static String getLocalDateTimeAsString(XMLGregorianCalendar xgc)
	{
		LocalDateTime date = InstanceFactory.getLocalDateTime(xgc);
		DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.MEDIUM);
		return date != null ? formatter.format(date) : null;
	}

	/**
	 * Constructs a {@link XMLGregorianCalendar} using the current time.
	 * @return current time as {@code XMLGregorianCalendar}
	 */
	public static XMLGregorianCalendar getDate()
	{
		try
		{
			return DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar());
		}
		catch (DatatypeConfigurationException e) {
			throw new Error(e);
		}
	}

	/**
	 * Constructs a {@link XMLGregorianCalendar} from passed {@link GregorianCalendar}.
	 * @return time as {@code XMLGregorianCalendar}
	 */
	public static XMLGregorianCalendar getDate(GregorianCalendar gc)
	{
		try
		{
			return DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
		}
		catch (DatatypeConfigurationException e) {
			throw new Error(e);
		}
	}

	/**
	 * Dissect passed string in a day, month and year and constructs a new {@link XMLGregorianCalendar} object.
	 * @param str string representation of the datum
	 * @return XMLGregorianCalendar object representing datum
	 * @throws Exception if something goes wrong within the call of static factory method that constructs the XMLGregorianCalendar object
	 */
	public static XMLGregorianCalendar getXMLGregorianCalendar(String str) throws Exception
	{
//		System.out.println(str);
		String[] datum = str.split("\\.");
		return DatatypeFactory.newInstance().newXMLGregorianCalendarDate(
				Integer.parseInt(datum[2]), Integer.parseInt(datum[1]), Integer.parseInt(datum[0]), DatatypeConstants.FIELD_UNDEFINED);
	}

	/**
	 * Merges date and time {@code XMLGregorianCalendar} objects into one. Merge is done in a way
	 * that the date part is taken from the {@code date} argument, and the time part is taken from
	 * the {@code time} argument.  If either of these two are {@code null} returned result is also
	 * {@code null}.
	 * @param date
	 * @param time
	 * @return merged {@link XMLGregorianCalendar datetime} object or {@code null}
	 */
	public static XMLGregorianCalendar mergeDateTime(XMLGregorianCalendar date, XMLGregorianCalendar time)
	{
		XMLGregorianCalendar dateTime = null;
		if(date != null && time != null)
		{
			dateTime = date;
			dateTime.setTime(time.getHour(), time.getMinute(), time.getSecond());
		}
		return dateTime;
	}

	/**
	 * Gets the object representing specific {@link TaxCategoryType tax category}.
	 * @param taxType parameter designating tax category. May be:
	 * <br>TAX_CATEGORY_0 for 0% tax rate
	 * <br>TAX_CATEGORY_10 for 10% tax rate
	 * <br>TAX_CATEGORY_20 for 20% tax rate
	 * @return {@code TaxCategoryType} object or {@code null} if {@code taxType} code is invalid
	 */
	public static TaxCategoryType getTaxCategory(String taxType)
	{
		return taxCategories.get(taxType);
	}

	/**
	 * Formats input string representing document type in a way that it strips all from it except
	 * its simple name (i.e. after the last "." character), removes from it "Type" substring if exists and
	 * splits Camel Titled leftover name into words that are concatenated with a space characher.
	 * <p>For example:</br>
	 * For an input: "oasis.names.specification.ubl.schema.xsd.orderresponsesimple_21.OrderResponseSimpleType"</br>
	 * the output is:</br>
	 * "Order Response Simple"
	 * </p>
	 * @param type document type
	 * @return {@code String} containing words concatenated with one space caracter
	 */
	public static String getDocumentName(String type)
	{
		return Stream.of(
				type.
				replaceAll("(.*\\.)*(.+?)(Type)", "$2").
				split("(?<![A-Z])(?=[A-Z])")).
				collect(Collectors.joining(" "));
	}

	/**
	 * Validates whether XML document comforms to {@code UBL} standard.
	 * @param document document to validate
	 * @return true if document has a {@code non-null} value and is valid
	 */
	public static <T,U> boolean validateUBLDocument(@Nullable T document,
			Function<T, IErrorList> validator)
	{
		boolean valid = false;
		if(document != null)
		{
			final IErrorList errors = validator.apply(document);
			if(errors.containsAtLeastOneFailure())
			{
				logger.error(errors.toString());
				//MMM Printing for a test
				if(document instanceof InvoiceType)
				try
				{
					JAXBContext jc = JAXBContext.newInstance(InvoiceType.class);
					Marshaller m = jc.createMarshaller();
					m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
					m.marshal(new JAXBElement<InvoiceType>(new QName("", "InvoiceType"),
							InvoiceType.class, null, (InvoiceType) document), System.out);
				}
				catch (JAXBException e)
				{
					e.printStackTrace();
				}
			}
			else
				valid = true;
		}
		return valid;
	}

	/**
	 * Gets the ID of the Sender Party as definied in the document. If no sender is set or sender ID is missing
	 * return value is {@code null}.
	 * @param document document to inspect
	 * @return ID of the Sender Party
	 */
	public static String getDocumentSenderID(Object document)
	{
		String senderID = null;
		try
		{
			senderID = getDocumentSenderParty(document).getPartyIdentificationAtIndex(0).getIDValue();
		}
		catch(Exception e) { }

		return senderID;
	}

	public static PartyType getDocumentSenderParty(Object document)
	{
		PartyType senderParty = null;
		try
		{
			final Class<? extends Object> documentClazz = document.getClass();
			if(documentClazz == OrderType.class)
				senderParty = ((OrderType) document).getBuyerCustomerParty().getParty();
			else if(documentClazz == OrderResponseType.class)
				senderParty = ((OrderResponseType) document).getSellerSupplierParty().getParty();
			else if(documentClazz == OrderResponseSimpleType.class)
				senderParty = ((OrderResponseSimpleType) document).getSellerSupplierParty().getParty();
			else if(documentClazz == OrderChangeType.class)
				senderParty = ((OrderChangeType) document).getBuyerCustomerParty().getParty();
			else if(documentClazz == OrderCancellationType.class)
				senderParty = ((OrderCancellationType) document).getBuyerCustomerParty().getParty();
			else if(documentClazz == ApplicationResponseType.class)
				senderParty = ((ApplicationResponseType) document).getSenderParty();
			else if(documentClazz == InvoiceType.class)
				senderParty = ((InvoiceType) document).getAccountingSupplierParty().getParty();
			else if(documentClazz == DocumentReceipt.class)
				senderParty = ((DocumentReceipt) document).getSenderParty();
			else if(documentClazz == PartnershipRequest.class)
				senderParty = ((PartnershipRequest) document).getRequesterParty();
			else if(documentClazz == PartnershipResponse.class)
				senderParty = ((PartnershipResponse) document).getRequestedParty();
			else if(documentClazz == PartnershipResolution.class)
				senderParty = ((PartnershipResolution) document).getRequestedParty();
			else if(documentClazz == PartnershipBreakup.class)
				senderParty = ((PartnershipBreakup) document).getRequesterParty();
			//MMM other document types


		}
		catch(Exception e) {
			int i = 1;}

		return senderParty;
	}

	/**
	 * Gets the ID of the Sender Party as definied in the document. If no sender is set or sender ID is missing
	 * return value is {@code null}.
	 * @param document document to inspect
	 * @return ID of the Sender Party
	 */
	public static String getDocumentReceiverID(Object document)
	{
		String receiverID = null;
		try
		{

			receiverID = getDocumentReceiverParty(document).getPartyIdentificationAtIndex(0).getIDValue();

		}
		catch(Exception e) { }

		return receiverID;
	}

	public static PartyType getDocumentReceiverParty(Object document)
	{
		PartyType receiverParty = null;
		try
		{
			final Class<? extends Object> documentClazz = document.getClass();
			if(documentClazz == OrderType.class)
				receiverParty = ((OrderType) document).getSellerSupplierParty().getParty();
			else if(documentClazz == OrderResponseType.class)
				receiverParty = ((OrderResponseType) document).getBuyerCustomerParty().getParty();
			else if(documentClazz == OrderResponseSimpleType.class)
				receiverParty = ((OrderResponseSimpleType) document).getBuyerCustomerParty().getParty();
			else if(documentClazz == OrderChangeType.class)
				receiverParty = ((OrderChangeType) document).getSellerSupplierParty().getParty();
			else if(documentClazz == OrderCancellationType.class)
				receiverParty = ((OrderCancellationType) document).getSellerSupplierParty().getParty();
			else if(documentClazz == ApplicationResponseType.class)
				receiverParty = ((ApplicationResponseType) document).getReceiverParty();
			else if(documentClazz == InvoiceType.class)
				receiverParty = ((InvoiceType) document).getAccountingCustomerParty().getParty();
			else if(documentClazz == DocumentReceipt.class)
				receiverParty = ((DocumentReceipt) document).getReceiverParty();
			else if(documentClazz == PartnershipRequest.class)
				receiverParty = ((PartnershipRequest) document).getRequestedParty();
			else if(documentClazz == PartnershipResponse.class)
				receiverParty = ((PartnershipResponse) document).getRequesterParty();
			else if(documentClazz == PartnershipResolution.class)
				receiverParty = ((PartnershipResolution) document).getRequesterParty();
			else if(documentClazz == PartnershipBreakup.class)
				receiverParty = ((PartnershipBreakup) document).getRequestedParty();
			//MMM other document types

		}
		catch(Exception e) { }

		return receiverParty;
	}

	/**
	 * Gets the document's ID or {@code null} if ID is missing.
	 * @param document document to inspect
	 * @return document's ID
	 */
	public static <T> String getDocumentID(T document)
	{
		String documentID = null;
		final Class<? extends Object> documentClazz = document.getClass();
		if(documentClazz == OrderType.class)
			documentID = ((OrderType) document).getIDValue();
		else if(documentClazz == OrderResponseType.class)
			documentID = ((OrderResponseType) document).getIDValue();
		else if(documentClazz == OrderResponseSimpleType.class)
			documentID = ((OrderResponseSimpleType) document).getIDValue();
		else if(documentClazz == OrderChangeType.class)
			documentID = ((OrderChangeType) document).getIDValue();
		else if(documentClazz == OrderCancellationType.class)
			documentID = ((OrderCancellationType) document).getIDValue();
		else if(documentClazz == ApplicationResponseType.class)
			documentID = ((ApplicationResponseType) document).getIDValue();
		else if(documentClazz == InvoiceType.class)
			documentID = ((InvoiceType) document).getIDValue();
		else if(documentClazz == DocumentReceipt.class)
			documentID = ((DocumentReceipt) document).getIDValue();
		else if(document instanceof PartnershipDocument)
			documentID = ((PartnershipDocument) document).getIDValue();
		//MMM other document types

		return documentID;
	}

	/**
	 * Gets the document's UUID or {@code null} if UUID is missing.
	 * @param document document to inspect
	 * @return document's UUID
	 */
	public static <T> String getDocumentUUID(T document)
	{
		String documentUUID = null;
		final Class<? extends Object> documentClazz = document.getClass();
		if(documentClazz == OrderType.class)
			documentUUID = ((OrderType) document).getUUIDValue();
		else if(documentClazz == OrderResponseType.class)
			documentUUID = ((OrderResponseType) document).getUUIDValue();
		else if(documentClazz == OrderResponseSimpleType.class)
			documentUUID = ((OrderResponseSimpleType) document).getUUIDValue();
		else if(documentClazz == OrderChangeType.class)
			documentUUID = ((OrderChangeType) document).getUUIDValue();
		else if(documentClazz == OrderCancellationType.class)
			documentUUID = ((OrderCancellationType) document).getUUIDValue();
		else if(documentClazz == ApplicationResponseType.class)
			documentUUID = ((ApplicationResponseType) document).getUUIDValue();
		else if(documentClazz == InvoiceType.class)
			documentUUID = ((InvoiceType) document).getUUIDValue();
		else if(documentClazz == DocumentReceipt.class)
			documentUUID = ((DocumentReceipt) document).getIDValue();
		else if(document instanceof PartnershipDocument)
			documentUUID = ((PartnershipDocument) document).getIDValue();
		//MMM other document types

		return documentUUID;
	}

	/**
	 * Creates {@link ApplicationResponseType} document as a response to some other UBL document.
	 * @param senderParty sender Party of the {@code Application Response} document
	 * @param receiverParty receiver Party of the {@code Application Response} document
	 * @param refUUID UUID of referenced document
	 * @param refID ID of the referenced document
	 * @param responseCode response code of the {@code Application Response} document
	 * @param note note
	 * @return {@code ApplicationResponseType}
	 */
	public static ApplicationResponseType createApplicationResponse(
			PartyType senderParty, PartyType receiverParty, String refUUID, String refID,
			String responseCode, String note)
	{
		final ApplicationResponseType appResponse = new ApplicationResponseType();
		final String id = UUID.randomUUID().toString();
		appResponse.setID(id);
		appResponse.setUUID(id);
		final XMLGregorianCalendar now = InstanceFactory.getDate();
		appResponse.setIssueDate(now);
		appResponse.setIssueTime(now);
		appResponse.setSenderParty(senderParty);
		appResponse.setReceiverParty(receiverParty);
		final DocumentResponseType docResponse = new DocumentResponseType();
		final ResponseType response = new ResponseType();
		response.setResponseCode(responseCode);
		docResponse.setResponse(response);
		final DocumentReferenceType docReference = new DocumentReferenceType();
		docReference.setUUID(refUUID);
		docReference.setID(refID);
		docResponse.getDocumentReference().add(docReference);
		appResponse.getDocumentResponse().add(docResponse);
		final NoteType appNote = new NoteType();
		appNote.setValue(note);
		appResponse.getNote().add(appNote);
		return appResponse;
	}

	/**
	 * Generates {@link ApplicationResponseType} document that conforms to the {@code UBL} standard.
	 * @param document document to which Application Response is to be created
	 * @param responseCode response code of the Application Response as a {@code String}
	 * @param note note
	 * @return Application Response or {@code null} if Application Response does not conform to the {@code UBL}
	 */
	public static ApplicationResponseType produceApplicationResponse(Object document, String responseCode, String note)
	{
		boolean valid = false;
		ApplicationResponseType appResponse = null;
		if(document.getClass() == OrderResponseSimpleType.class)
		{
			OrderResponseSimpleType orderResponseSimple = (OrderResponseSimpleType) document;
			appResponse = createApplicationResponse(orderResponseSimple.getBuyerCustomerParty().getParty(),
					orderResponseSimple.getSellerSupplierParty().getParty(), orderResponseSimple.getUUIDValue(),
					orderResponseSimple.getIDValue(), responseCode, note);
		}
		if(document.getClass() == OrderResponseType.class)
		{
			OrderResponseType orderResponse = (OrderResponseType) document;
			appResponse = createApplicationResponse(orderResponse.getBuyerCustomerParty().getParty(),
					orderResponse.getSellerSupplierParty().getParty(), orderResponse.getUUIDValue(),
					orderResponse.getIDValue(), responseCode, note);
		}
		else if(document.getClass() == InvoiceType.class)
		{
			//MMM check this
			InvoiceType invoice = (InvoiceType) document;
			appResponse = createApplicationResponse(invoice.getAccountingCustomerParty().getParty(),
					invoice.getAccountingSupplierParty().getParty(), invoice.getUUIDValue(),
					invoice.getIDValue(), responseCode, note);
		}

		valid = validateUBLDocument(appResponse,
				doc -> UBL21Validator.applicationResponse().validate(doc));
		return valid ? appResponse : null;
	}

	/**
	 * Creates {@link OrderReferenceType Order Reference} to a passed {@code Order} argument.
	 * @param order
	 * @return created {@code OrderReferenceType}
	 */
	public static OrderReferenceType createOrderReference(OrderType order)
	{
		final OrderReferenceType orderReference = new OrderReferenceType();
		orderReference.setID(UUID.randomUUID().toString());
		final DocumentReferenceType docReference = new DocumentReferenceType();
		docReference.setID(order.getID());
		docReference.setUUID(order.getUUIDValue());
		docReference.setIssueDate(order.getIssueDate());
		docReference.setIssueTime(order.getIssueTime());
		docReference.setDocumentType(order.getClass().getName());
		orderReference.setDocumentReference(docReference);
		return orderReference;
	}

	/**
	 * Creates {@link DocumentReferenceType Document Reference}.
	 * @param issuerParty Party that issued referenced document
	 * @param uuid referenced document's UUID
	 * @param id referenced document's ID
	 * @param issueDate issue date of referenced document
	 * @param issueTime issue time of referenced document
	 * @param docType document's type as fully qualified name
	 * @result created Document Reference
	 */
	public static DocumentReferenceType createDocumentReference(PartyType issuerParty, String uuid, String id,
			XMLGregorianCalendar issueDate, XMLGregorianCalendar issueTime, String docType)
	{
		final DocumentReferenceType docReference = new DocumentReferenceType();
		docReference.setID(id);
		docReference.setUUID(uuid);
		docReference.setIssueDate(issueDate);
		docReference.setIssueTime(issueTime);
		docReference.setIssuerParty(issuerParty);
		docReference.setDocumentType(docType);
		return docReference;
	}

	public static PartnershipResponse createPartnershipResponse(PartnershipRequest request, boolean accepted)
	{
		PartnershipResponse response = new PartnershipResponse();
		response.setID(UUID.randomUUID().toString());
		response.setRequesterParty(request.getRequesterParty());
		response.setRequestedParty(request.getRequestedParty());
		response.setIssueTime(InstanceFactory.getDate());
		response.setAccepted(accepted);
		return response;
	}
}