package rs.ruta.client.correspondence;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * {@code ObjectFactory} is a helper class in the process of mapping objects to {@code XML} elements.
 * {@code ObjectFactory} has two methods {@code createXxx} for every class which objects it is mapping.
 * One method is for instantiating the object, and the other is for instantiating the {@link JAXBElement}
 * that is an representation of the XML element to which it is mapped.
 */
@XmlRegistry
public class ObjectFactory
{
	private static final QName _CatalogueCorrespondence_QNAME =
			new QName("http://www.ruta.rs/ns/client", "CatalogueCorrespondence");

	private static final QName _CreateCatalogueProcess_QNAME =
			new QName("http://www.ruta.rs/ns/client", "CreateCatalogueProcess");
	private static final QName _DecideOnActionState_QNAME =
			new QName("http://www.ruta.rs/ns/client", "DecideOnActionState");
	private static final QName _DistributeCatalogueState_QNAME =
			new QName("http://www.ruta.rs/ns/client", "DistributeCatalogueState");
	private static final QName _PrepareCatalogueState_QNAME =
			new QName("http://www.ruta.rs/ns/client", "PrepareCatalogueState");
	private static final QName _ProduceCatalogueState_QNAME =
			new QName("http://www.ruta.rs/ns/client", "ProduceCatalogueState");
	private static final QName _ReceiveCatalogueAppResponseState_QNAME =
			new QName("http://www.ruta.rs/ns/client", "ReceiveCatalogueAppResponseState");

	private static final QName _DeleteCatalogueProcess_QNAME =
			new QName("http://www.ruta.rs/ns/client", "DeleteCatalogueProcess");
	private static final QName _CancelCatalogueState_QNAME =
			new QName("http://www.ruta.rs/ns/client", "CancelCatalogueState");
	private static final QName _NotifyOfCatalogueDeletionState_QNAME =
			new QName("http://www.ruta.rs/ns/client", "NotifyOfCatalogueDeletionState");
	private static final QName _ReceiveCatalogueDeletionAppResponseState_QNAME =
			new QName("http://www.ruta.rs/ns/client", "ReceiveCatalogueDeletionAppResponseState");
	private static final QName _ReviewDeletionOfCatalogueState_QNAME =
			new QName("http://www.ruta.rs/ns/client", "ReviewDeletionOfCatalogueState");

	private static final QName _ResolveNextProcess_QNAME =
			new QName("http://www.ruta.rs/ns/client", "ResolveNextCatalogueProcess");
	private static final QName _NextCatalogueState_QNAME =
			new QName("http://www.ruta.rs/ns/client", "NextCatalogueState");

	private static final QName _BuyingCorrespondence_QNAME =
			new QName("http://www.ruta.rs/ns/client", "BuyingCorrespondence");

	private static final QName _BuyerOrderingProcess_QNAME =
			new QName("http://www.ruta.rs/ns/client", "BuyerOrderingProcess");
	private static final QName _BuyerAcceptOrderState_QNAME =
			new QName("http://www.ruta.rs/ns/client", "BuyerAcceptOrderState");
	private static final QName _BuyerCancelOrderState_QNAME =
			new QName("http://www.ruta.rs/ns/client", "BuyerCancelOrderState");
	private static final QName _BuyerChangeOrderState_QNAME =
			new QName("http://www.ruta.rs/ns/client", "BuyerChangeOrderState");
	private static final QName _BuyerOrderRejectedState_QNAME =
			new QName("http://www.ruta.rs/ns/client", "BuyerOrderRejectedState");
	private static final QName _BuyerPrepareOrderState_QNAME =
			new QName("http://www.ruta.rs/ns/client", "BuyerPrepareOrderState");
	private static final QName _BuyerProcessOrderResponseState_QNAME =
			new QName("http://www.ruta.rs/ns/client", "BuyerProcessOrderResponseState");
	private static final QName _BuyerProcessOrderResponseSimpleState_QNAME =
			new QName("http://www.ruta.rs/ns/client", "BuyerProcessOrderResponseSimpleState");
	private static final QName _BuyerReceiveOrderResponseState_QNAME =
			new QName("http://www.ruta.rs/ns/client", "BuyerReceiveOrderResponseState");
	private static final QName _BuyerSendApplicationResponseState_QNAME =
			new QName("http://www.ruta.rs/ns/client", "BuyerSendApplicationResponseState");
	private static final QName _BuyerSendOrderState_QNAME =
			new QName("http://www.ruta.rs/ns/client", "BuyerSendOrderState");
	private static final QName _BuyerSendOrderChangeState_QNAME =
			new QName("http://www.ruta.rs/ns/client", "BuyerSendOrderChangeState");
	private static final QName _BuyerSendOrderCancellationState_QNAME =
			new QName("http://www.ruta.rs/ns/client", "BuyerSendOrderCancellationState");

	private static final QName _SellerOrderingProcess_QNAME =
			new QName("http://www.ruta.rs/ns/client", "SellerOrderingProcess");
	private static final QName _SellerAcceptOrderState_QNAME =
			new QName("http://www.ruta.rs/ns/client", "SellerAcceptOrderState");
	private static final QName _SellerAddDetailState_QNAME =
			new QName("http://www.ruta.rs/ns/client", "SellerAddDetailState");
	private static final QName _SellerCancelOrderState_QNAME =
			new QName("http://www.ruta.rs/ns/client", "SellerCancelOrderState");
	private static final QName _SellerChangeOrderState_QNAME =
			new QName("http://www.ruta.rs/ns/client", "SellerChangeOrderState");
	private static final QName _SellerOrderAcceptedState_QNAME =
			new QName("http://www.ruta.rs/ns/client", "SellerOrderAcceptedState");
	private static final QName _SellerProcessOrderState_QNAME =
			new QName("http://www.ruta.rs/ns/client", "SellerProcessOrderState");
	private static final QName _SellerReceiveOrderState_QNAME =
			new QName("http://www.ruta.rs/ns/client", "SellerReceiveOrderState");
	private static final QName _SellerReceiveOrderChangeCancellationState_QNAME =
			new QName("http://www.ruta.rs/ns/client", "SellerReceiveOrderChangeCancellationState");
	private static final QName _SellerRejectOrderState_QNAME =
			new QName("http://www.ruta.rs/ns/client", "SellerRejectOrderState");
	private static final QName _SellerSendOrderResponseState_QNAME =
			new QName("http://www.ruta.rs/ns/client", "SellerSendOrderResponseState");
	private static final QName _SellerSendOrderResponseSimpleState_QNAME =
			new QName("http://www.ruta.rs/ns/client", "SellerSendOrderResponseSimpleState");

	private static final QName _CustomerBillingProcess_QNAME =
			new QName("http://www.ruta.rs/ns/client", "CustomerBillingProcess");
	private static final QName _CustomerCreateApplicationResponseState_QNAME =
	new QName("http://www.ruta.rs/ns/client", "CustomerCreateApplicationResponseState");
	private static final QName _CustomerReceiveInvoiceState_QNAME =
			new QName("http://www.ruta.rs/ns/client", "CustomerReceiveInvoiceState");
	private static final QName _CustomerReconcileChargesState_QNAME =
			new QName("http://www.ruta.rs/ns/client", "CustomerReconcileChargesState");
	private static final QName _CustomerSendApplicationResponseState_QNAME =
			new QName("http://www.ruta.rs/ns/client", "CustomerSendApplicationResponseState");

	private static final QName _SupplierBillingProcess_QNAME =
			new QName("http://www.ruta.rs/ns/client", "SupplierBillingProcess");
	private static final QName _SupplierRaiseInvoiceState_QNAME =
			new QName("http://www.ruta.rs/ns/client", "SupplierRaiseInvoiceState");
	private static final QName _SupplierSendInvoiceState_QNAME =
			new QName("http://www.ruta.rs/ns/client", "SupplierSendInvoiceState");
	private static final QName _SupplierReceiveApplicationResponseState_QNAME =
			new QName("http://www.ruta.rs/ns/client", "SupplierReceiveApplicationResponseState");

	private static final QName _PaymentNotificationProcess_QNAME =
			new QName("http://www.ruta.rs/ns/client", "PaymentNotificationProcess");

	private static final QName _ClosingProcess_QNAME =
			new QName("http://www.ruta.rs/ns/client", "ClosingProcess");

	private static final QName _ClosingState_QNAME =
			new QName("http://www.ruta.rs/ns/client", "ClosingState");


	/**
	 * Creates an instance of {@link CatalogueCorrespondence}.
	 * @return created {@code CatalogueCorrespondence} object and never {@code null}
	 */
	@Nonnull
	public CatalogueCorrespondence createCatalogueCorrespondence()
	{
		return new CatalogueCorrespondence();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link CatalogueCorrespondence }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/client", name = "CatalogueCorrespondence")
	@Nonnull
	public JAXBElement<CatalogueCorrespondence> createCatalogueCorrespondence(
			@Nullable final CatalogueCorrespondence value)
	{
		return new JAXBElement<CatalogueCorrespondence>(_CatalogueCorrespondence_QNAME, CatalogueCorrespondence.class, null, value);
	}

	/**
	 * Creates an instance of {@link BuyingCorrespondence}.
	 * @return created {@code BuyingCorrespondence} object and never {@code null}
	 */
	@Nonnull
	public BuyingCorrespondence createBuyingCorrespondence()
	{
		return new BuyingCorrespondence();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link BuyingCorrespondence }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/client", name = "BuyingCorrespondence")
	@Nonnull
	public JAXBElement<BuyingCorrespondence> createBuyingCorrespondence(
			@Nullable final BuyingCorrespondence value)
	{
		return new JAXBElement<BuyingCorrespondence>(_BuyingCorrespondence_QNAME, BuyingCorrespondence.class, null, value);
	}

	//Create Catalogue Process

	/**
	 * Creates an instance of {@link CreateCatalogueProcess}.
	 * @return created {@code CreateCatalogueProcess} object and never {@code null}
	 */
	@Nonnull
	public CreateCatalogueProcess createCreateCatalogueProcess()
	{
		return new CreateCatalogueProcess();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link CreateCatalogueProcess }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/client", name = "CreateCatalogueProcess")
	@Nonnull
	public JAXBElement<CreateCatalogueProcess> createCreateCatalogueProcess(@Nullable final CreateCatalogueProcess value)
	{
		return new JAXBElement<CreateCatalogueProcess>(_CreateCatalogueProcess_QNAME, CreateCatalogueProcess.class, null, value);
	}

	/**
	 * Creates an instance of {@link DecideOnActionState}.
	 * @return created {@code DecideOnActionState} object and never {@code null}
	 */
	@Nonnull
	public DecideOnActionState createDecideOnActionState()
	{
		return new DecideOnActionState();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link DecideOnActionState }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/client", name = "DecideOnActionState")
	@Nonnull
	public JAXBElement<DecideOnActionState> createDecideOnActionState(@Nullable final DecideOnActionState value)
	{
		return new JAXBElement<DecideOnActionState>(_DecideOnActionState_QNAME, DecideOnActionState.class, null, value);
	}

	/**
	 * Creates an instance of {@link DistributeCatalogueState}.
	 * @return created {@code DistributeCatalogueState} object and never {@code null}
	 */
	@Nonnull
	public DistributeCatalogueState createDistributeCatalogueState()
	{
		return new DistributeCatalogueState();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link DistributeCatalogueState }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/client", name = "DistributeCatalogueState")
	@Nonnull
	public JAXBElement<DistributeCatalogueState> createDistributeCatalogueState(@Nullable final DistributeCatalogueState value)
	{
		return new JAXBElement<DistributeCatalogueState>(_DistributeCatalogueState_QNAME, DistributeCatalogueState.class, null, value);
	}

	/**
	 * Creates an instance of {@link PrepareCatalogueState}.
	 * @return created {@code PrepareCatalogueState} object and never {@code null}
	 */
	@Nonnull
	public PrepareCatalogueState createPrepareCatalogueState()
	{
		return new PrepareCatalogueState();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link PrepareCatalogueState }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/client", name = "PrepareCatalogueState")
	@Nonnull
	public JAXBElement<PrepareCatalogueState> createPrepareCatalogueState(@Nullable final PrepareCatalogueState value)
	{
		return new JAXBElement<PrepareCatalogueState>(_PrepareCatalogueState_QNAME, PrepareCatalogueState.class, null, value);
	}

	/**
	 * Creates an instance of {@link ProduceCatalogueState}.
	 * @return created {@code ProduceCatalogueState} object and never {@code null}
	 */
	@Nonnull
	public ProduceCatalogueState createProduceCatalogueState()
	{
		return new ProduceCatalogueState();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link ProduceCatalogueState }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/client", name = "ProduceCatalogueState")
	@Nonnull
	public JAXBElement<ProduceCatalogueState> createProduceCatalogueState(@Nullable final ProduceCatalogueState value)
	{
		return new JAXBElement<ProduceCatalogueState>(_ProduceCatalogueState_QNAME, ProduceCatalogueState.class, null, value);
	}

	/**
	 * Creates an instance of {@link ReceiveCatalogueAppResponseState}.
	 * @return created {@code ReceiveCatalogueAppResponseState} object and never {@code null}
	 */
	@Nonnull
	public ReceiveCatalogueAppResponseState createReceiveCatalogueAppResponseState()
	{
		return new ReceiveCatalogueAppResponseState();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link ReceiveCatalogueAppResponseState }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/client", name = "ReceiveCatalogueAppResponseState")
	@Nonnull
	public JAXBElement<ReceiveCatalogueAppResponseState> createReceiveCatalogueAppResponseState(
			@Nullable final ReceiveCatalogueAppResponseState value)
	{
		return new JAXBElement<ReceiveCatalogueAppResponseState>(_ReceiveCatalogueAppResponseState_QNAME,
				ReceiveCatalogueAppResponseState.class, null, value);
	}

	//Delete Catalogue Process

	/**
	 * Creates an instance of {@link DeleteCatalogueProcess}.
	 * @return created {@code DeleteCatalogueProcess} object and never {@code null}
	 */
	@Nonnull
	public DeleteCatalogueProcess createDeleteCatalogueProcess()
	{
		return new DeleteCatalogueProcess();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link DeleteCatalogueProcess }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/client", name = "DeleteCatalogueProcess")
	@Nonnull
	public JAXBElement<DeleteCatalogueProcess> createDeleteCatalogueProcess(@Nullable final DeleteCatalogueProcess value)
	{
		return new JAXBElement<DeleteCatalogueProcess>(_DeleteCatalogueProcess_QNAME, DeleteCatalogueProcess.class, null, value);
	}

	/**
	 * Creates an instance of {@link CancelCatalogueState}.
	 * @return created {@code CancelCatalogueState} object and never {@code null}
	 */
	@Nonnull
	public CancelCatalogueState createCancelCatalogueState()
	{
		return new CancelCatalogueState();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link CancelCatalogueState }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/client", name = "CancelCatalogueState")
	@Nonnull
	public JAXBElement<CancelCatalogueState> createCancelCatalogueState(@Nullable final CancelCatalogueState value)
	{
		return new JAXBElement<CancelCatalogueState>(_CancelCatalogueState_QNAME, CancelCatalogueState.class, null, value);
	}

	/**
	 * Creates an instance of {@link NotifyOfCatalogueDeletionState}.
	 * @return created {@code NotifyOfCatalogueDeletionState} object and never {@code null}
	 */
	@Nonnull
	public NotifyOfCatalogueDeletionState createNotifyOfCatalogueDeletionState()
	{
		return new NotifyOfCatalogueDeletionState();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link NotifyOfCatalogueDeletionState }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/client", name = "NotifyOfCatalogueDeletionState")
	@Nonnull
	public JAXBElement<NotifyOfCatalogueDeletionState> createNotifyOfCatalogueDeletionState(
			@Nullable final NotifyOfCatalogueDeletionState value)
	{
		return new JAXBElement<NotifyOfCatalogueDeletionState>(_NotifyOfCatalogueDeletionState_QNAME,
				NotifyOfCatalogueDeletionState.class, null, value);
	}

	/**
	 * Creates an instance of {@link ReceiveCatalogueDeletionAppResponseState}.
	 * @return created {@code ReceiveCatalogueDeletionAppResponseState} object and never {@code null}
	 */
	@Nonnull
	public ReceiveCatalogueDeletionAppResponseState createReceiveCatalogueDeletionAppRespState()
	{
		return new ReceiveCatalogueDeletionAppResponseState();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link ReceiveCatalogueDeletionAppResponseState }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/client", name = "ReceiveCatalogueDeletionAppResponseState")
	@Nonnull
	public JAXBElement<ReceiveCatalogueDeletionAppResponseState> createReceiveCatalogueDeletionAppRespState(
			@Nullable final ReceiveCatalogueDeletionAppResponseState value)
	{
		return new JAXBElement<ReceiveCatalogueDeletionAppResponseState>(_ReceiveCatalogueDeletionAppResponseState_QNAME,
				ReceiveCatalogueDeletionAppResponseState.class, null, value);
	}

	/**
	 * Creates an instance of {@link ReviewDeletionOfCatalogueState}.
	 * @return created {@code ReviewDeletionOfCatalogueState} object and never {@code null}
	 */
	@Nonnull
	public ReviewDeletionOfCatalogueState createReviewDeletionOfCatalogueState()
	{
		return new ReviewDeletionOfCatalogueState();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link ReviewDeletionOfCatalogueState }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/client", name = "ReviewDeletionOfCatalogueState")
	@Nonnull
	public JAXBElement<ReviewDeletionOfCatalogueState> createReviewDeletionOfCatalogueState(
			@Nullable final ReviewDeletionOfCatalogueState value)
	{
		return new JAXBElement<ReviewDeletionOfCatalogueState>(_ReviewDeletionOfCatalogueState_QNAME,
				ReviewDeletionOfCatalogueState.class, null, value);
	}

	//Resolve Next Catalogue Process

	/**
	 * Creates an instance of {@link ResolveNextCatalogueProcess}.
	 * @return created {@code ResolveNextCatalogueProcess} object and never {@code null}
	 */
	@Nonnull
	public ResolveNextCatalogueProcess createResolveNextCatalogueProcess()
	{
		return new ResolveNextCatalogueProcess();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link ResolveNextCatalogueProcess }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/client", name = "ResolveNextCatalogueProcess")
	@Nonnull
	public JAXBElement<ResolveNextCatalogueProcess> createResolveNextCatalogueProcess(@Nullable final ResolveNextCatalogueProcess value)
	{
		return new JAXBElement<ResolveNextCatalogueProcess>(_ResolveNextProcess_QNAME, ResolveNextCatalogueProcess.class, null, value);
	}

	/**
	 * Creates an instance of {@link NextCatalogueState}.
	 * @return created {@code NextCatalogueState} object and never {@code null}
	 */
	@Nonnull
	public NextCatalogueState createNextCatalogueState()
	{
		return new NextCatalogueState();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link NextCatalogueState }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/client", name = "NextCatalogueState")
	@Nonnull
	public JAXBElement<NextCatalogueState> createNextCatalogueState(@Nullable final NextCatalogueState value)
	{
		return new JAXBElement<NextCatalogueState>(_NextCatalogueState_QNAME, NextCatalogueState.class, null, value);
	}

	//BuyerOrdering Process

	/**
	 * Creates an instance of {@link BuyerOrderingProcess}.
	 * @return created {@code BuyerOrderingProcess} object and never {@code null}
	 */
	@Nonnull
	public BuyerOrderingProcess createBuyerOrderingProcess()
	{
		return new BuyerOrderingProcess();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link BuyerOrderingProcess }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/client", name = "BuyerOrderingProcess")
	@Nonnull
	public JAXBElement<BuyerOrderingProcess> createBuyerOrderingProcess(@Nullable final BuyerOrderingProcess value)
	{
		return new JAXBElement<BuyerOrderingProcess>(_BuyerOrderingProcess_QNAME, BuyerOrderingProcess.class, null, value);
	}

	/**
	 * Creates an instance of {@link BuyerCancelOrderState}.
	 * @return created {@code BuyerCancelOrderState} object and never {@code null}
	 */
	@Nonnull
	public BuyerCancelOrderState createBuyerCancelOrderState()
	{
		return new BuyerCancelOrderState();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link BuyerCancelOrderState }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/client", name = "BuyerCancelOrderState")
	@Nonnull
	public JAXBElement<BuyerCancelOrderState> createBuyerCancelOrderState(@Nullable final BuyerCancelOrderState value)
	{
		return new JAXBElement<BuyerCancelOrderState>(_BuyerCancelOrderState_QNAME, BuyerCancelOrderState.class, null, value);
	}

	/**
	 * Creates an instance of {@link BuyerChangeOrderState}.
	 * @return created {@code BuyerChangeOrderState} object and never {@code null}
	 */
	@Nonnull
	public BuyerChangeOrderState createBuyerChangeOrderState()
	{
		return new BuyerChangeOrderState();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link BuyerChangeOrderState }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/client", name = "BuyerChangeOrderState")
	@Nonnull
	public JAXBElement<BuyerChangeOrderState> createBuyerChangeOrderState(@Nullable final BuyerChangeOrderState value)
	{
		return new JAXBElement<BuyerChangeOrderState>(_BuyerChangeOrderState_QNAME, BuyerChangeOrderState.class, null, value);
	}

	/**
	 * Creates an instance of {@link BuyerAcceptOrderState}.
	 * @return created {@code BuyerAcceptOrderState} object and never {@code null}
	 */
	@Nonnull
	public BuyerAcceptOrderState createBuyerOrderAcceptedState()
	{
		return new BuyerAcceptOrderState();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link BuyerAcceptOrderState }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/client", name = "BuyerAcceptOrderState")
	@Nonnull
	public JAXBElement<BuyerAcceptOrderState> createBuyerOrderAcceptedState(@Nullable final BuyerAcceptOrderState value)
	{
		return new JAXBElement<BuyerAcceptOrderState>(_BuyerAcceptOrderState_QNAME, BuyerAcceptOrderState.class, null, value);
	}

	/**
	 * Creates an instance of {@link BuyerOrderRejectedState}.
	 * @return created {@code BuyerOrderRejectedState} object and never {@code null}
	 */
	@Nonnull
	public BuyerOrderRejectedState createBuyerOrderRejectedState()
	{
		return new BuyerOrderRejectedState();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link BuyerOrderRejectedState }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/client", name = "BuyerOrderRejectedState")
	@Nonnull
	public JAXBElement<BuyerOrderRejectedState> createBuyerOrderRejectedState(@Nullable final BuyerOrderRejectedState value)
	{
		return new JAXBElement<BuyerOrderRejectedState>(_BuyerOrderRejectedState_QNAME, BuyerOrderRejectedState.class, null, value);
	}

	/**
	 * Creates an instance of {@link BuyerPrepareOrderState}.
	 * @return created {@code BuyerPrepareOrderState} object and never {@code null}
	 */
	@Nonnull
	public BuyerPrepareOrderState createBuyerPrepareOrderState()
	{
		return new BuyerPrepareOrderState();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link BuyerPrepareOrderState }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/client", name = "BuyerPrepareOrderState")
	@Nonnull
	public JAXBElement<BuyerPrepareOrderState> createBuyerPrepareOrderState(@Nullable final BuyerPrepareOrderState value)
	{
		return new JAXBElement<BuyerPrepareOrderState>(_BuyerPrepareOrderState_QNAME, BuyerPrepareOrderState.class, null, value);
	}

	/**
	 * Creates an instance of {@link BuyerProcessOrderResponseState}.
	 * @return created {@code BuyerProcessOrderResponseState} object and never {@code null}
	 */
	@Nonnull
	public BuyerProcessOrderResponseState createBuyerProcessOrderResponseState()
	{
		return new BuyerProcessOrderResponseState();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link BuyerProcessOrderResponseState }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/client", name = "BuyerProcessOrderResponseState")
	@Nonnull
	public JAXBElement<BuyerProcessOrderResponseState> createBuyerProcessResponseState(@Nullable final BuyerProcessOrderResponseState value)
	{
		return new JAXBElement<BuyerProcessOrderResponseState>(_BuyerProcessOrderResponseState_QNAME, BuyerProcessOrderResponseState.class, null, value);
	}

	/**
	 * Creates an instance of {@link BuyerProcessOrderResponseSimpleState}.
	 * @return created {@code BuyerProcessOrderResponseSimpleState} object and never {@code null}
	 */
	@Nonnull
	public BuyerProcessOrderResponseSimpleState createBuyerProcessResponseSimpleState()
	{
		return new BuyerProcessOrderResponseSimpleState();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link BuyerProcessOrderResponseSimpleState }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/client", name = "BuyerProcessOrderResponseSimpleState")
	@Nonnull
	public JAXBElement<BuyerProcessOrderResponseSimpleState> createBuyerProcessResponseState(
			@Nullable final BuyerProcessOrderResponseSimpleState value)
	{
		return new JAXBElement<BuyerProcessOrderResponseSimpleState>(_BuyerProcessOrderResponseSimpleState_QNAME,
				BuyerProcessOrderResponseSimpleState.class, null, value);
	}

	/**
	 * Creates an instance of {@link BuyerSendOrderState}.
	 * @return created {@code BuyerReceiveOrderResponseState} object and never {@code null}
	 */
	@Nonnull
	public BuyerReceiveOrderResponseState createBuyerReceiveOrderResponseState()
	{
		return new BuyerReceiveOrderResponseState();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link BuyerReceiveOrderResponseState }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/client", name = "BuyerReceiveOrderResponseState")
	@Nonnull
	public JAXBElement<BuyerReceiveOrderResponseState> createBuyerReceiveOrderResponseState(
			@Nullable final BuyerReceiveOrderResponseState value)
	{
		return new JAXBElement<BuyerReceiveOrderResponseState>(_BuyerReceiveOrderResponseState_QNAME,
				BuyerReceiveOrderResponseState.class, null, value);
	}

	/**
	 * Creates an instance of {@link BuyerSendApplicationResponseState}.
	 * @return created {@code BuyerSendApplicationResponseState} object and never {@code null}
	 */
	@Nonnull
	public BuyerSendApplicationResponseState createBuyerSendApplicationResponseState()
	{
		return new BuyerSendApplicationResponseState();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link BuyerSendApplicationResponseState }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/client", name = "BuyerSendApplicationResponseState")
	@Nonnull
	public JAXBElement<BuyerSendApplicationResponseState> createBuyerSendApplicationResponseState(
			@Nullable final BuyerSendApplicationResponseState value)
	{
		return new JAXBElement<BuyerSendApplicationResponseState>(_BuyerSendApplicationResponseState_QNAME,
				BuyerSendApplicationResponseState.class, null, value);
	}

	/**
	 * Creates an instance of {@link BuyerSendOrderState}.
	 * @return created {@code BuyerSendOrderState} object and never {@code null}
	 */
	@Nonnull
	public BuyerSendOrderState createBuyerSendOrderState()
	{
		return new BuyerSendOrderState();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link BuyerSendOrderState }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/client", name = "BuyerSendOrderState")
	@Nonnull
	public JAXBElement<BuyerSendOrderState> createBuyerSendOrderState(@Nullable final BuyerSendOrderState value)
	{
		return new JAXBElement<BuyerSendOrderState>(_BuyerSendOrderState_QNAME, BuyerSendOrderState.class, null, value);
	}

	/**
	 * Creates an instance of {@link BuyerSendOrderChangeState}.
	 * @return created {@code BuyerSendOrderChangeState} object and never {@code null}
	 */
	@Nonnull
	public BuyerSendOrderChangeState createBuyerSendOrderChangeState()
	{
		return new BuyerSendOrderChangeState();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link BuyerSendOrderChangeState }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/client", name = "BuyerSendOrderChangeState")
	@Nonnull
	public JAXBElement<BuyerSendOrderChangeState> createBuyerSendOrderChangeState(@Nullable final BuyerSendOrderChangeState value)
	{
		return new JAXBElement<BuyerSendOrderChangeState>(_BuyerSendOrderChangeState_QNAME, BuyerSendOrderChangeState.class, null, value);
	}

	/**
	 * Creates an instance of {@link BuyerSendOrderCancellationState}.
	 * @return created {@code BuyerSendOrderCancellationState} object and never {@code null}
	 */
	@Nonnull
	public BuyerSendOrderCancellationState createBuyerSendOrderCancellationState()
	{
		return new BuyerSendOrderCancellationState();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link BuyerSendOrderCancellationState }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/client", name = "BuyerSendOrderCancellationState")
	@Nonnull
	public JAXBElement<BuyerSendOrderCancellationState> createBuyerSendOrderCancellationState(@Nullable final BuyerSendOrderCancellationState value)
	{
		return new JAXBElement<BuyerSendOrderCancellationState>(_BuyerSendOrderCancellationState_QNAME, BuyerSendOrderCancellationState.class, null, value);
	}

	//SelllerOrderingProcess

	/**
	 * Creates an instance of {@link SellerOrderingProcess}.
	 * @return created {@code SellerOrderingProcess} object and never {@code null}
	 */
	@Nonnull
	public SellerOrderingProcess createSellerOrderingProcess()
	{
		return new SellerOrderingProcess();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link SellerOrderingProcess }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/client", name = "SellerOrderingProcess")
	@Nonnull
	public JAXBElement<SellerOrderingProcess> createSellerOrderingProcess(@Nullable final SellerOrderingProcess value)
	{
		return new JAXBElement<SellerOrderingProcess>(_SellerOrderingProcess_QNAME, SellerOrderingProcess.class, null, value);
	}

	/**
	 * Creates an instance of {@link SellerAcceptOrderState}.
	 * @return created {@code SellerAcceptOrderState} object and never {@code null}
	 */
	@Nonnull
	public SellerAcceptOrderState createSellerAcceptOrderState()
	{
		return new SellerAcceptOrderState();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link SellerAcceptOrderState }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/client", name = "SellerAcceptOrderState")
	@Nonnull
	public JAXBElement<SellerAcceptOrderState> createSellerAcceptOrderState(@Nullable final SellerAcceptOrderState value)
	{
		return new JAXBElement<SellerAcceptOrderState>(_SellerAcceptOrderState_QNAME, SellerAcceptOrderState.class, null, value);
	}

	/**
	 * Creates an instance of {@link SellerAddDetailState}.
	 * @return created {@code SellerAddDetailState} object and never {@code null}
	 */
	@Nonnull
	public SellerAddDetailState createSellerAddDetailState()
	{
		return new SellerAddDetailState();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link SellerAddDetailState }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/client", name = "SellerAddDetailState")
	@Nonnull
	public JAXBElement<SellerAddDetailState> createSellerAddDetailState(@Nullable final SellerAddDetailState value)
	{
		return new JAXBElement<SellerAddDetailState>(_SellerAddDetailState_QNAME, SellerAddDetailState.class, null, value);
	}

	/**
	 * Creates an instance of {@link SellerCancelOrderState}.
	 * @return created {@code SellerCancelOrderState} object and never {@code null}
	 */
	@Nonnull
	public SellerCancelOrderState createSellerCancelOrderState()
	{
		return new SellerCancelOrderState();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link SellerCancelOrderState }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/client", name = "SellerCancelOrderState")
	@Nonnull
	public JAXBElement<SellerCancelOrderState> createSellerCancelOrderState(@Nullable final SellerCancelOrderState value)
	{
		return new JAXBElement<SellerCancelOrderState>(_SellerCancelOrderState_QNAME, SellerCancelOrderState.class, null, value);
	}

	/**
	 * Creates an instance of {@link SellerChangeOrderState}.
	 * @return created {@code SellerChangeOrderState} object and never {@code null}
	 */
	@Nonnull
	public SellerChangeOrderState createSellerChangeOrderState()
	{
		return new SellerChangeOrderState();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link SellerChangeOrderState }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/client", name = "SellerChangeOrderState")
	@Nonnull
	public JAXBElement<SellerChangeOrderState> createSellerChangeOrderState(@Nullable final SellerChangeOrderState value)
	{
		return new JAXBElement<SellerChangeOrderState>(_SellerChangeOrderState_QNAME, SellerChangeOrderState.class, null, value);
	}

	/**
	 * Creates an instance of {@link SellerOrderAcceptedState}.
	 * @return created {@code SellerOrderAcceptedState} object and never {@code null}
	 */
	@Nonnull
	public SellerOrderAcceptedState createSellerOrderAcceptedState()
	{
		return new SellerOrderAcceptedState();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link SellerOrderAcceptedState }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/client", name = "SellerOrderAcceptedState")
	@Nonnull
	public JAXBElement<SellerOrderAcceptedState> createSellerOrderAcceptedState(@Nullable final SellerOrderAcceptedState value)
	{
		return new JAXBElement<SellerOrderAcceptedState>(_SellerOrderAcceptedState_QNAME, SellerOrderAcceptedState.class, null, value);
	}

	/**
	 * Creates an instance of {@link SellerProcessOrderState}.
	 * @return created {@code SellerProcessOrderState} object and never {@code null}
	 */
	@Nonnull
	public SellerProcessOrderState createSellerProcessOrderState()
	{
		return new SellerProcessOrderState();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link SellerProcessOrderState }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/client", name = "SellerProcessOrderState")
	@Nonnull
	public JAXBElement<SellerProcessOrderState> createSellerProcessOrderState(@Nullable final SellerProcessOrderState value)
	{
		return new JAXBElement<SellerProcessOrderState>(_SellerProcessOrderState_QNAME, SellerProcessOrderState.class, null, value);
	}

	/**
	 * Creates an instance of {@link SellerReceiveOrderChangeCancellationState}.
	 * @return created {@code SellerReceiveOrderChangeCancellationState} object and never {@code null}
	 */
	@Nonnull
	public SellerReceiveOrderChangeCancellationState createSellerReceiveOrderChangeCancellationState()
	{
		return new SellerReceiveOrderChangeCancellationState();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link SellerReceiveOrderChangeCancellationState }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/client", name = "SellerReceiveOrderChangeCancellationState")
	@Nonnull
	public JAXBElement<SellerReceiveOrderChangeCancellationState> createSellerReceiveOrderChangeCancellationState(
			@Nullable final SellerReceiveOrderChangeCancellationState value)
	{
		return new JAXBElement<SellerReceiveOrderChangeCancellationState>(_SellerReceiveOrderChangeCancellationState_QNAME,
				SellerReceiveOrderChangeCancellationState.class, null, value);
	}

	/**
	 * Creates an instance of {@link SellerReceiveOrderState}.
	 * @return created {@code SellerReceiveOrderState} object and never {@code null}
	 */
	@Nonnull
	public SellerReceiveOrderState createSellerReceiveOrderState()
	{
		return new SellerReceiveOrderState();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link SellerReceiveOrderState }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/client", name = "SellerReceiveOrderState")
	@Nonnull
	public JAXBElement<SellerReceiveOrderState> createSellerReceiveOrderState(@Nullable final SellerReceiveOrderState value)
	{
		return new JAXBElement<SellerReceiveOrderState>(_SellerReceiveOrderState_QNAME, SellerReceiveOrderState.class, null, value);
	}

	/**
	 * Creates an instance of {@link SellerRejectOrderState}.
	 * @return created {@code SellerRejectOrderState} object and never {@code null}
	 */
	@Nonnull
	public SellerRejectOrderState createSellerRejectOrderState()
	{
		return new SellerRejectOrderState();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link SellerRejectOrderState }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/client", name = "SellerRejectOrderState")
	@Nonnull
	public JAXBElement<SellerRejectOrderState> createSellerRejectOrderState(@Nullable final SellerRejectOrderState value)
	{
		return new JAXBElement<SellerRejectOrderState>(_SellerRejectOrderState_QNAME, SellerRejectOrderState.class, null, value);
	}

	/**
	 * Creates an instance of {@link SellerSendOrderResponseState}.
	 * @return created {@code SellerSendOrderResponseState} object and never {@code null}
	 */
	@Nonnull
	public SellerSendOrderResponseState createSellerSendOrderResponseState()
	{
		return new SellerSendOrderResponseState();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link SellerSendOrderResponseState }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/client", name = "SellerSendOrderResponseState")
	@Nonnull
	public JAXBElement<SellerSendOrderResponseState> createSellerSendOrderResponseState(@Nullable final SellerSendOrderResponseState value)
	{
		return new JAXBElement<SellerSendOrderResponseState>(_SellerSendOrderResponseState_QNAME, SellerSendOrderResponseState.class, null, value);
	}

	/**
	 * Creates an instance of {@link SellerSendOrderResponseSimpleState}.
	 * @return created {@code SellerSendOrderResponseSimpleState} object and never {@code null}
	 */
	@Nonnull
	public SellerSendOrderResponseSimpleState createSellerSendOrderResponseSimpleState()
	{
		return new SellerSendOrderResponseSimpleState();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link SellerSendOrderResponseSimpleState }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/client", name = "SellerSendOrderResponseSimpleState")
	@Nonnull
	public JAXBElement<SellerSendOrderResponseSimpleState> createSellerSendOrderResponseSimpleState(
			@Nullable final SellerSendOrderResponseSimpleState value)
	{
		return new JAXBElement<SellerSendOrderResponseSimpleState>(
				_SellerSendOrderResponseSimpleState_QNAME, SellerSendOrderResponseSimpleState.class, null, value);
	}

	//Customer Billing Process

	/**
	 * Creates an instance of {@link CustomerBillingProcess}.
	 * @return created {@code CustomerBillingProcess} object and never {@code null}
	 */
	@Nonnull
	public CustomerBillingProcess createCustomerBillingProcess()
	{
		return new CustomerBillingProcess();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link CustomerBillingProcess }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/client", name = "CustomerBillingProcess")
	@Nonnull
	public JAXBElement<CustomerBillingProcess> createCustomerBillingProcess(@Nullable final CustomerBillingProcess value)
	{
		return new JAXBElement<CustomerBillingProcess>(_CustomerBillingProcess_QNAME, CustomerBillingProcess.class, null, value);
	}

	/**
	 * Creates an instance of {@link CustomerCreateApplicationResponseState}.
	 * @return created {@code CustomerCreateApplicationResponseState} object and never {@code null}
	 */
	@Nonnull
	public CustomerCreateApplicationResponseState createCustomerCreateApplicationResponseState()
	{
		return new CustomerCreateApplicationResponseState();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link CustomerCreateApplicationResponseState }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/client", name = "CustomerCreateApplicationResponseState")
	@Nonnull
	public JAXBElement<CustomerCreateApplicationResponseState> createCustomerCreateApplicationResponseState(@Nullable final CustomerCreateApplicationResponseState value)
	{
		return new JAXBElement<CustomerCreateApplicationResponseState>(_CustomerCreateApplicationResponseState_QNAME, CustomerCreateApplicationResponseState.class, null, value);
	}

	/**
	 * Creates an instance of {@link CustomerReceiveInvoiceState}.
	 * @return created {@code CustomerReceiveInvoiceState} object and never {@code null}
	 */
	@Nonnull
	public CustomerReceiveInvoiceState createCustomerReceiveInvoiceState()
	{
		return new CustomerReceiveInvoiceState();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link CustomerReceiveInvoiceState }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/client", name = "CustomerReceiveInvoiceState")
	@Nonnull
	public JAXBElement<CustomerReceiveInvoiceState> createCustomerReceiveInvoiceState(@Nullable final CustomerReceiveInvoiceState value)
	{
		return new JAXBElement<CustomerReceiveInvoiceState>(_CustomerReceiveInvoiceState_QNAME, CustomerReceiveInvoiceState.class, null, value);
	}

	/**
	 * Creates an instance of {@link CustomerReconcileChargesState}.
	 * @return created {@code CustomerReconcileChargesState} object and never {@code null}
	 */
	@Nonnull
	public CustomerReconcileChargesState createCustomerReconcileChargesState()
	{
		return new CustomerReconcileChargesState();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link CustomerReconcileChargesState }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/client", name = "CustomerReconcileChargesState")
	@Nonnull
	public JAXBElement<CustomerReconcileChargesState> createCustomerReconcileChargesState(@Nullable final CustomerReconcileChargesState value)
	{
		return new JAXBElement<CustomerReconcileChargesState>(_CustomerReconcileChargesState_QNAME, CustomerReconcileChargesState.class, null, value);
	}

	/**
	 * Creates an instance of {@link CustomerSendApplicationResponseState}.
	 * @return created {@code CustomerSendApplicationResponseState} object and never {@code null}
	 */
	@Nonnull
	public CustomerSendApplicationResponseState createCustomerSendApplicationResponseState()
	{
		return new CustomerSendApplicationResponseState();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link CustomerSendApplicationResponseState }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/client", name = "CustomerSendApplicationResponseState")
	@Nonnull
	public JAXBElement<CustomerSendApplicationResponseState> createCustomerSendApplicationResponseState(@Nullable final CustomerSendApplicationResponseState value)
	{
		return new JAXBElement<CustomerSendApplicationResponseState>(_CustomerSendApplicationResponseState_QNAME, CustomerSendApplicationResponseState.class, null, value);
	}

	//Supplier Billing Process

	/**
	 * Creates an instance of {@link SupplierBillingProcess}.
	 * @return created {@code SupplierBillingProcess} object and never {@code null}
	 */
	@Nonnull
	public SupplierBillingProcess createSupplierBillingProcess()
	{
		return new SupplierBillingProcess();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link SupplierBillingProcess }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/client", name = "SupplierBillingProcess")
	@Nonnull
	public JAXBElement<SupplierBillingProcess> createSupplierBillingProcess(@Nullable final SupplierBillingProcess value)
	{
		return new JAXBElement<SupplierBillingProcess>(_SupplierBillingProcess_QNAME, SupplierBillingProcess.class, null, value);
	}

	/**
	 * Creates an instance of {@link SupplierRaiseInvoiceState}.
	 * @return created {@code SupplierRaiseInvoiceState} object and never {@code null}
	 */
	@Nonnull
	public SupplierRaiseInvoiceState createSupplierRaiseInvoiceState()
	{
		return new SupplierRaiseInvoiceState();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link SupplierRaiseInvoiceState }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/client", name = "SupplierRaiseInvoiceState")
	@Nonnull
	public JAXBElement<SupplierRaiseInvoiceState> createSupplierRaiseInvoiceState(@Nullable final SupplierRaiseInvoiceState value)
	{
		return new JAXBElement<SupplierRaiseInvoiceState>(_SupplierRaiseInvoiceState_QNAME, SupplierRaiseInvoiceState.class, null, value);
	}

	/**
	 * Creates an instance of {@link SupplierSendInvoiceState}.
	 * @return created {@code SupplierSendInvoiceState} object and never {@code null}
	 */
	@Nonnull
	public SupplierSendInvoiceState createSupplierSendInvoiceState()
	{
		return new SupplierSendInvoiceState();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link SupplierSendInvoiceState }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/client", name = "SupplierSendInvoiceState")
	@Nonnull
	public JAXBElement<SupplierSendInvoiceState> createSupplierSendInvoiceState(@Nullable final SupplierSendInvoiceState value)
	{
		return new JAXBElement<SupplierSendInvoiceState>(_SupplierSendInvoiceState_QNAME, SupplierSendInvoiceState.class, null, value);
	}

	/**
	 * Creates an instance of {@link SupplierReceiveApplicationResponseState}.
	 * @return created {@code SupplierReceiveApplicationResponseState} object and never {@code null}
	 */
	@Nonnull
	public SupplierReceiveApplicationResponseState createSupplierReceiveApplicationResponseState()
	{
		return new SupplierReceiveApplicationResponseState();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link SupplierReceiveApplicationResponseState }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/client", name = "SupplierReceiveApplicationResponseState")
	@Nonnull
	public JAXBElement<SupplierReceiveApplicationResponseState> createSupplierReceiveApplicationResponseState(@Nullable final SupplierReceiveApplicationResponseState value)
	{
		return new JAXBElement<SupplierReceiveApplicationResponseState>(_SupplierReceiveApplicationResponseState_QNAME, SupplierReceiveApplicationResponseState.class, null, value);
	}









	//Payment Notification Process

	/**
	 * Creates an instance of {@link PaymentNotificationProcess}.
	 * @return created {@code PaymentNotificationProcess} object and never {@code null}
	 */
	@Nonnull
	public PaymentNotificationProcess createPaymentNotificationProcess()
	{
		return new PaymentNotificationProcess();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link PaymentNotificationProcess }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/client", name = "PaymentNotificationProcess")
	@Nonnull
	public JAXBElement<PaymentNotificationProcess> createPaymentNotificationProcess(@Nullable final PaymentNotificationProcess value)
	{
		return new JAXBElement<PaymentNotificationProcess>(_PaymentNotificationProcess_QNAME, PaymentNotificationProcess.class, null, value);
	}

	//Buying Closing Process

	/**
	 * Creates an instance of {@link ClosingProcess}.
	 * @return created {@code ClosingProcess} object and never {@code null}
	 */
	@Nonnull
	public ClosingProcess createBuyingClosingProcess()
	{
		return new ClosingProcess();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link ClosingProcess }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/client", name = "ClosingProcess")
	@Nonnull
	public JAXBElement<ClosingProcess> createBuyingClosingProcess(@Nullable final ClosingProcess value)
	{
		return new JAXBElement<ClosingProcess>(_ClosingProcess_QNAME, ClosingProcess.class, null, value);
	}

	//Closing State

	/**
	 * Creates an instance of {@link ClosingState}.
	 * @return created {@code ClosingState} object and never {@code null}
	 */
	@Nonnull
	public ClosingState createClosingStateState()
	{
		return new ClosingState();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link ClosingState }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/client", name = "ClosingState")
	@Nonnull
	public JAXBElement<ClosingState> createClosingState(@Nullable final ClosingState value)
	{
		return new JAXBElement<ClosingState>(_ClosingState_QNAME, ClosingState.class, null, value);
	}


/*	*//**
	 * Creates an instance of {@link OrderingProcess}.
	 * @return created {@code OrderingProcess} object and never {@code null}
	 *//*
	@Nonnull
	public OrderingProcess createOrderingProcess()
	{
		return new OrderingProcess();
	}

	*//**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link OrderingProcess }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 *//*
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/client", name = "OrderingProcess")
	@Nonnull
	public JAXBElement<OrderingProcess> createOrderingProcess(@Nullable final OrderingProcess value)
	{
		return new JAXBElement<OrderingProcess>(_OrderingProcess_QNAME, OrderingProcess.class, null, value);
	}*/

	@SuppressWarnings("unchecked")
	private <T> T createInstance(Class<T> anInterface)
	{
		return (T) Proxy.newProxyInstance(anInterface.getClassLoader(), new Class[] {anInterface}, new InterfaceInvocationHandler());
	}

	private static class InterfaceInvocationHandler implements InvocationHandler
	{

		private Map<String, Object> values = new HashMap<String, Object>();

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
		{
			String methodName = method.getName();
			if(methodName.startsWith("get"))
				return values.get(methodName.substring(3));
			else
			{
				values.put(methodName.substring(3), args[0]);
				return null;
			}
		}

	}

}