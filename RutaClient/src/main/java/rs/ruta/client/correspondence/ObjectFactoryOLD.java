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
 * <p>All methods pertaining state machine's {@link RutaProcessState states} have to be defined in this
 * class of this package, regardless if those states are defined in some other package. This is because
 * of the fact that state classes implements {@code RutaProcessState} which is an interface and not a class.
 * It seems that JAXB is imposing this rule because of it.</p>
 */
@XmlRegistry
public class ObjectFactoryOLD
{
	private static final QName _CatalogueCorrespondence_QNAME =
			new QName("urn:rs:ruta:client:correspondence", "CatalogueCorrespondence");

	private static final QName _CreateCatalogueProcess_QNAME =
			new QName("urn:rs:ruta:client:correspondence", "CreateCatalogueProcess");
	private static final QName _DecideOnActionState_QNAME =
			new QName("urn:rs:ruta:client:correspondence:catalogue:create", "DecideOnActionState");
	private static final QName _DistributeCatalogueState_QNAME =
			new QName("urn:rs:ruta:client:correspondence:catalogue:create", "DistributeCatalogueState");
	private static final QName _PrepareCatalogueState_QNAME =
			new QName("urn:rs:ruta:client:correspondence:catalogue:create", "PrepareCatalogueState");
	private static final QName _ProduceCatalogueState_QNAME =
			new QName("urn:rs:ruta:client:correspondence:catalogue:create", "ProduceCatalogueState");
	private static final QName _ReceiveCatalogueAppResponseState_QNAME =
			new QName("urn:rs:ruta:client:correspondence:catalogue:create", "ReceiveCatalogueAppResponseState");

	private static final QName _DeleteCatalogueProcess_QNAME =
			new QName("urn:rs:ruta:client:correspondence", "DeleteCatalogueProcess");
	private static final QName _CancelCatalogueState_QNAME =
			new QName("urn:rs:ruta:client:correspondence:catalogue:delete", "CancelCatalogueState");
	private static final QName _NotifyOfCatalogueDeletionState_QNAME =
			new QName("urn:rs:ruta:client:correspondence:catalogue:delete", "NotifyOfCatalogueDeletionState");
	private static final QName _ReceiveCatalogueDeletionAppResponseState_QNAME =
			new QName("urn:rs:ruta:client:correspondence:catalogue:delete", "ReceiveCatalogueDeletionAppResponseState");
	private static final QName _ReviewDeletionOfCatalogueState_QNAME =
			new QName("urn:rs:ruta:client:correspondence:catalogue:delete", "ReviewDeletionOfCatalogueState");

	private static final QName _ResolveNextProcess_QNAME =
			new QName("urn:rs:ruta:client:correspondence", "ResolveNextCatalogueProcess");
	private static final QName _NextCatalogueState_QNAME =
			new QName("urn:rs:ruta:client:correspondence:catalogue:resolve", "NextCatalogueState");

	private static final QName _BuyingCorrespondence_QNAME =
			new QName("urn:rs:ruta:client:correspondence", "BuyingCorrespondence");
	private static final QName _OrderingProcess_QNAME =
			new QName("urn:rs:ruta:client:correspondence", "OrderingProcess");

	private static final QName _BuyerOrderingProcess_QNAME =
			new QName("urn:rs:ruta:client:correspondence", "BuyerOrderingProcess");
	private static final QName _BuyerAcceptOrderState_QNAME =
			new QName("urn:rs:ruta:client:correspondence:buying:ordering:buyer", "BuyerAcceptOrderState");
	private static final QName _BuyerCancelOrderState_QNAME =
			new QName("urn:rs:ruta:client:correspondence:buying:ordering:buyer", "BuyerCancelOrderState");
	private static final QName _BuyerChangeOrderState_QNAME =
			new QName("urn:rs:ruta:client:correspondence:buying:ordering:buyer", "BuyerChangeOrderState");
	private static final QName _BuyerOrderAcceptedState_QNAME =
			new QName("urn:rs:ruta:client:correspondence:buying:ordering:buyer", "BuyerOrderAcceptedState");
	private static final QName _BuyerOrderRejectedState_QNAME =
			new QName("urn:rs:ruta:client:correspondence:buying:ordering:buyer", "BuyerOrderRejectedState");
	private static final QName _BuyerPlaceOrderState_QNAME =
			new QName("urn:rs:ruta:client:correspondence:buying:ordering:buyer", "BuyerPlaceOrderState");
	private static final QName _BuyerProcessResponseState_QNAME =
			new QName("urn:rs:ruta:client:correspondence:buying:ordering:buyer", "BuyerProcessResponseState");
	private static final QName _BuyerReceiveOrderResponseState_QNAME =
			new QName("urn:rs:ruta:client:correspondence:buying:ordering:buyer", "BuyerReceiveOrderResponseState");

	private static final QName _SellerOrderingProcess_QNAME =
			new QName("urn:rs:ruta:client:correspondence", "SellerOrderingProcess");
	private static final QName _SellerAcceptOrderState_QNAME =
			new QName("urn:rs:ruta:client:correspondence:buying:ordering:seller", "SellerAcceptOrderState");
	private static final QName _SellerAddDetailState_QNAME =
			new QName("urn:rs:ruta:client:correspondence:buying:ordering:seller", "SellerAddDetailState");
	private static final QName _SellerCancelOrderState_QNAME =
			new QName("urn:rs:ruta:client:correspondence:buying:ordering:seller", "SellerCancelOrderState");
	private static final QName _SellerChangeOrderState_QNAME =
			new QName("urn:rs:ruta:client:correspondence:buying:ordering:seller", "SellerChangeOrderState");
	private static final QName _SellerProcessOrderState_QNAME =
			new QName("urn:rs:ruta:client:correspondence:buying:ordering:seller", "SellerProcessOrderState");
	private static final QName _SellerReceiveOrderChangeCancellationState_QNAME =
			new QName("urn:rs:ruta:client:correspondence:buying:ordering:seller", "SellerReceiveOrderChangeCancellationState");
	private static final QName _SellerReceiveOrderState_QNAME =
			new QName("urn:rs:ruta:client:correspondence:buying:ordering:seller", "SellerReceiveOrderState");
	private static final QName _SellerRejectOrderState_QNAME =
			new QName("urn:rs:ruta:client:correspondence:buying:ordering:seller", "SellerRejectOrderState");

	private static final QName _BillingProcess_QNAME =
			new QName("urn:rs:ruta:client:correspondence", "BillingProcess");
	private static final QName _PaymentNotificationProcess_QNAME =
			new QName("urn:rs:ruta:client:correspondence", "PaymentNotificationProcess");
	private static final QName _BuyingClosingProcess_QNAME =
			new QName("urn:rs:ruta:client:correspondence", "BuyingClosingProcess");

	private static final QName _EndOfProcessState_QNAME =
			new QName("urn:rs:ruta:client:correspondence", "EndOfProcessState");

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
	@XmlElementDecl(namespace = "urn:rs:ruta:client:correspondence", name = "CatalogueCorrespondence")
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
	@XmlElementDecl(namespace = "urn:rs:ruta:client:correspondence", name = "BuyingCorrespondence")
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
	@XmlElementDecl(namespace = "urn:rs:ruta:client:correspondence", name = "CreateCatalogueProcess")
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
	@XmlElementDecl(namespace = "urn:rs:ruta:client:correspondence:catalogue:create", name = "DecideOnActionState")
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
	@XmlElementDecl(namespace = "urn:rs:ruta:client:correspondence:catalogue:create", name = "DistributeCatalogueState")
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
	@XmlElementDecl(namespace = "urn:rs:ruta:client:correspondence:catalogue:create", name = "PrepareCatalogueState")
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
	@XmlElementDecl(namespace = "urn:rs:ruta:client:correspondence:catalogue:create", name = "ProduceCatalogueState")
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
	@XmlElementDecl(namespace = "urn:rs:ruta:client:correspondence:catalogue:create", name = "ReceiveCatalogueAppResponseState")
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
	@XmlElementDecl(namespace = "urn:rs:ruta:client:correspondence", name = "DeleteCatalogueProcess")
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
	@XmlElementDecl(namespace = "urn:rs:ruta:client:correspondence:catalogue:delete", name = "CancelCatalogueState")
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
	@XmlElementDecl(namespace = "urn:rs:ruta:client:correspondence:catalogue:delete", name = "NotifyOfCatalogueDeletionState")
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
	@XmlElementDecl(namespace = "urn:rs:ruta:client:correspondence:catalogue:delete",
			name = "ReceiveCatalogueDeletionAppResponseState")
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
	@XmlElementDecl(namespace = "urn:rs:ruta:client:correspondence:catalogue:delete", name = "ReviewDeletionOfCatalogueState")
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
	@XmlElementDecl(namespace = "urn:rs:ruta:client:correspondence", name = "ResolveNextCatalogueProcess")
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
	@XmlElementDecl(namespace = "urn:rs:ruta:client:correspondence:catalogue:resolve", name = "NextCatalogueState")
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
	@XmlElementDecl(namespace = "urn:rs:ruta:client:correspondence", name = "BuyerOrderingProcess")
	@Nonnull
	public JAXBElement<BuyerOrderingProcess> createBuyerOrderingProcess(@Nullable final BuyerOrderingProcess value)
	{
		return new JAXBElement<BuyerOrderingProcess>(_BuyerOrderingProcess_QNAME, BuyerOrderingProcess.class, null, value);
	}

	/**
	 * Creates an instance of {@link BuyerAcceptOrderState}.
	 * @return created {@code BuyerAcceptOrderState} object and never {@code null}
	 */
	@Nonnull
	public BuyerAcceptOrderState createBuyerAcceptOrderState()
	{
		return new BuyerAcceptOrderState();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link BuyerAcceptOrderState }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "urn:rs:ruta:client:correspondence:buying:ordering:buyer", name = "BuyerAcceptOrderState")
	@Nonnull
	public JAXBElement<BuyerAcceptOrderState> createBuyerAcceptOrderState(@Nullable final BuyerAcceptOrderState value)
	{
		return new JAXBElement<BuyerAcceptOrderState>(_BuyerAcceptOrderState_QNAME, BuyerAcceptOrderState.class, null, value);
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
	@XmlElementDecl(namespace = "urn:rs:ruta:client:correspondence:buying:ordering:buyer", name = "BuyerCancelOrderState")
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
	@XmlElementDecl(namespace = "urn:rs:ruta:client:correspondence:buying:ordering:buyer", name = "BuyerChangeOrderState")
	@Nonnull
	public JAXBElement<BuyerChangeOrderState> createBuyerChangeOrderState(@Nullable final BuyerChangeOrderState value)
	{
		return new JAXBElement<BuyerChangeOrderState>(_BuyerChangeOrderState_QNAME, BuyerChangeOrderState.class, null, value);
	}

	/**
	 * Creates an instance of {@link BuyerOrderAcceptedState}.
	 * @return created {@code BuyerOrderAcceptedState} object and never {@code null}
	 */
	@Nonnull
	public BuyerOrderAcceptedState createBuyerOrderAcceptedState()
	{
		return new BuyerOrderAcceptedState();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link BuyerOrderAcceptedState }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "urn:rs:ruta:client:correspondence:buying:ordering:buyer", name = "BuyerOrderAcceptedState")
	@Nonnull
	public JAXBElement<BuyerOrderAcceptedState> createBuyerOrderAcceptedState(@Nullable final BuyerOrderAcceptedState value)
	{
		return new JAXBElement<BuyerOrderAcceptedState>(_BuyerOrderAcceptedState_QNAME, BuyerOrderAcceptedState.class, null, value);
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
	@XmlElementDecl(namespace = "urn:rs:ruta:client:correspondence:buying:ordering:buyer", name = "BuyerOrderRejectedState")
	@Nonnull
	public JAXBElement<BuyerOrderRejectedState> createBuyerOrderRejectedState(@Nullable final BuyerOrderRejectedState value)
	{
		return new JAXBElement<BuyerOrderRejectedState>(_BuyerOrderRejectedState_QNAME, BuyerOrderRejectedState.class, null, value);
	}

	/**
	 * Creates an instance of {@link BuyerPlaceOrderState}.
	 * @return created {@code BuyerPlaceOrderState} object and never {@code null}
	 */
	@Nonnull
	public BuyerPlaceOrderState createBuyerPlaceOrderState()
	{
		return new BuyerPlaceOrderState();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link BuyerPlaceOrderState }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "urn:rs:ruta:client:correspondence:buying:ordering:buyer", name = "BuyerPlaceOrderState")
	@Nonnull
	public JAXBElement<BuyerPlaceOrderState> createBuyerPlaceOrderState(@Nullable final BuyerPlaceOrderState value)
	{
		return new JAXBElement<BuyerPlaceOrderState>(_BuyerPlaceOrderState_QNAME, BuyerPlaceOrderState.class, null, value);
	}

	/**
	 * Creates an instance of {@link BuyerProcessResponseState}.
	 * @return created {@code BuyerProcessResponseState} object and never {@code null}
	 */
	@Nonnull
	public BuyerProcessResponseState createBuyerProcessResponseState()
	{
		return new BuyerProcessResponseState();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link BuyerProcessResponseState }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "urn:rs:ruta:client:correspondence:buying:ordering:buyer", name = "BuyerProcessResponseState")
	@Nonnull
	public JAXBElement<BuyerProcessResponseState> createBuyerProcessResponseState(@Nullable final BuyerProcessResponseState value)
	{
		return new JAXBElement<BuyerProcessResponseState>(_BuyerProcessResponseState_QNAME, BuyerProcessResponseState.class, null, value);
	}

	/**
	 * Creates an instance of {@link BuyerReceiveOrderResponseState}.
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
	@XmlElementDecl(namespace = "urn:rs:ruta:client:correspondence:buying:ordering:buyer", name = "BuyerReceiveOrderResponseState")
	@Nonnull
	public JAXBElement<BuyerReceiveOrderResponseState> createBuyerReceiveOrderResponseState(@Nullable final BuyerReceiveOrderResponseState value)
	{
		return new JAXBElement<BuyerReceiveOrderResponseState>(_BuyerReceiveOrderResponseState_QNAME, BuyerReceiveOrderResponseState.class, null, value);
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
	@XmlElementDecl(namespace = "urn:rs:ruta:client:correspondence", name = "SellerOrderingProcess")
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
	@XmlElementDecl(namespace = "urn:rs:ruta:client:correspondence:buying:ordering:seller", name = "SellerAcceptOrderState")
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
	@XmlElementDecl(namespace = "urn:rs:ruta:client:correspondence:buying:ordering:seller", name = "SellerAddDetailState")
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
	@XmlElementDecl(namespace = "urn:rs:ruta:client:correspondence:buying:ordering:seller", name = "SellerCancelOrderState")
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
	@XmlElementDecl(namespace = "urn:rs:ruta:client:correspondence:buying:ordering:seller", name = "SellerChangeOrderState")
	@Nonnull
	public JAXBElement<SellerChangeOrderState> createSellerChangeOrderState(@Nullable final SellerChangeOrderState value)
	{
		return new JAXBElement<SellerChangeOrderState>(_SellerChangeOrderState_QNAME, SellerChangeOrderState.class, null, value);
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
	@XmlElementDecl(namespace = "urn:rs:ruta:client:correspondence:buying:ordering:seller", name = "SellerProcessOrderState")
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
	@XmlElementDecl(namespace = "urn:rs:ruta:client:correspondence:buying:ordering:seller", name = "SellerReceiveOrderChangeCancellationState")
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
	@XmlElementDecl(namespace = "urn:rs:ruta:client:correspondence:buying:ordering:seller", name = "SellerReceiveOrderState")
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
	@XmlElementDecl(namespace = "urn:rs:ruta:client:correspondence:buying:ordering:seller", name = "SellerRejectOrderState")
	@Nonnull
	public JAXBElement<SellerRejectOrderState> createSellerRejectOrderState(@Nullable final SellerRejectOrderState value)
	{
		return new JAXBElement<SellerRejectOrderState>(_SellerRejectOrderState_QNAME, SellerRejectOrderState.class, null, value);
	}

	//Billing Process

	/**
	 * Creates an instance of {@link BillingProcess}.
	 * @return created {@code BillingProcess} object and never {@code null}
	 */
	@Nonnull
	public BillingProcess createBillingProcess()
	{
		return new BillingProcess();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link BillingProcess }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "urn:rs:ruta:client:correspondence", name = "BillingProcess")
	@Nonnull
	public JAXBElement<BillingProcess> createBillingProcess(@Nullable final BillingProcess value)
	{
		return new JAXBElement<BillingProcess>(_BillingProcess_QNAME, BillingProcess.class, null, value);
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
	@XmlElementDecl(namespace = "urn:rs:ruta:client:correspondence", name = "PaymentNotificationProcess")
	@Nonnull
	public JAXBElement<PaymentNotificationProcess> createPaymentNotificationProcess(@Nullable final PaymentNotificationProcess value)
	{
		return new JAXBElement<PaymentNotificationProcess>(_PaymentNotificationProcess_QNAME, PaymentNotificationProcess.class, null, value);
	}

	//Buying Closing Process

	/**
	 * Creates an instance of {@link BuyingClosingProcess}.
	 * @return created {@code BuyingClosingProcess} object and never {@code null}
	 */
	@Nonnull
	public BuyingClosingProcess createBuyingClosingProcess()
	{
		return new BuyingClosingProcess();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link BuyingClosingProcess }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "urn:rs:ruta:client:correspondence", name = "BuyingClosingProcess")
	@Nonnull
	public JAXBElement<BuyingClosingProcess> createBuyingClosingProcess(@Nullable final BuyingClosingProcess value)
	{
		return new JAXBElement<BuyingClosingProcess>(_BuyingClosingProcess_QNAME, BuyingClosingProcess.class, null, value);
	}

	//EndOfProcess State

	/**
	 * Creates an instance of {@link EndOfProcessState}.
	 * @return created {@code EndOfProcessState} object and never {@code null}
	 */
	@Nonnull
	public EndOfProcessState createEndOfProcessState()
	{
		return new EndOfProcessState();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link EndOfProcessState }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "urn:rs:ruta:client:correspondence", name = "EndOfProcessState")
	@Nonnull
	public JAXBElement<EndOfProcessState> createEndOfProcessState(@Nullable final EndOfProcessState value)
	{
		return new JAXBElement<EndOfProcessState>(_EndOfProcessState_QNAME, EndOfProcessState.class, null, value);
	}




	/**
	 * Creates an instance of {@link OrderingProcess}.
	 * @return created {@code OrderingProcess} object and never {@code null}
	 */
	@Nonnull
	public OrderingProcess createOrderingProcess()
	{
		return new OrderingProcess();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link OrderingProcess }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "urn:rs:ruta:client:correspondence", name = "OrderingProcess")
	@Nonnull
	public JAXBElement<OrderingProcess> createOrderingProcess(@Nullable final OrderingProcess value)
	{
		return new JAXBElement<OrderingProcess>(_OrderingProcess_QNAME, OrderingProcess.class, null, value);
	}

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