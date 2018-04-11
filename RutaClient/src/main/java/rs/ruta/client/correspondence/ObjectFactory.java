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
	private static final QName _CatalogueCorrespondence_QNAME = new QName("urn:rs:ruta:client", "CatalogueCorrespondence");
	private static final QName _CreateCatalogueProcess_QNAME = new QName("urn:rs:ruta:client", "CreateCatalogueProcess");
	private static final QName _DeleteCatalogueProcess_QNAME = new QName("urn:rs:ruta:client", "DeleteCatalogueProcess");
	private static final QName _ResolveNextProcess_QNAME = new QName("urn:rs:ruta:client", "ResolveNextCatalogueProcess");
	private static final QName _OrderingProcess_QNAME = new QName("urn:rs:ruta:client", "OrderingProcess");
	private static final QName _BillingProcess_QNAME = new QName("urn:rs:ruta:client", "BillingProcess");
	private static final QName _PaymentNotificationProcess_QNAME = new QName("urn:rs:ruta:client", "PaymentNotificationProcess");
	private static final QName _PrepareCatalogueState_QNAME = new QName("urn:rs:ruta:client", "PrepareCatalogueState");
	private static final QName _ProduceCatalogueState_QNAME = new QName("urn:rs:ruta:client", "ProduceCatalogueState");
	private static final QName _DistributeCatalogueState_QNAME = new QName("urn:rs:ruta:client", "DistributeCatalogueState");
	private static final QName _ReceiveCatalogueAppResponseState_QNAME = new QName("urn:rs:ruta:client", "ReceiveCatalogueAppResponseState");
	private static final QName _DecideOnActionState_QNAME = new QName("urn:rs:ruta:client", "DecideOnActionState");
	private static final QName _EndOfProcessState_QNAME = new QName("urn:rs:ruta:client", "EndOfProcessState");
	private static final QName _NextCatalogueState_QNAME = new QName("urn:rs:ruta:client", "NextCatalogueState");
	private static final QName _CancelCatalogueState_QNAME = new QName("urn:rs:ruta:client", "CancelCatalogueState");
	private static final QName _NotifyOfCatalogueDeletionState_QNAME = new QName("urn:rs:ruta:client", "NotifyOfCatalogueDeletionState");
	private static final QName _ReceiveCatalogueDeletionAppRespState_QNAME = new QName("urn:rs:ruta:client", "ReceiveCatalogueDeletionAppRespState");
	private static final QName _ReviewDeletionOfCatalogueState_QNAME = new QName("urn:rs:ruta:client", "ReviewDeletionOfCatalogueState");


	private static final QName _CreateCatalogueCorrespondenceState_QNAME = new QName("urn:rs:ruta:client", "CreateCatalogueCorrespondenceState");
	private static final QName _DeleteCatalogueCorrespondenceState_QNAME = new QName("urn:rs:ruta:client", "DeleteCatalogueCorrespondenceState");


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
	@XmlElementDecl(namespace = "urn:rs:ruta:client", name = "CatalogueCorrespondence")
	@Nonnull
	public JAXBElement<CatalogueCorrespondence> createCatalogueCorrespondence(
			@Nullable final CatalogueCorrespondence value)
	{
		return new JAXBElement<CatalogueCorrespondence>(_CatalogueCorrespondence_QNAME, CatalogueCorrespondence.class, null, value);
	}

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
	@XmlElementDecl(namespace = "urn:rs:ruta:client", name = "CreateCatalogueProcess")
	@Nonnull
	public JAXBElement<CreateCatalogueProcess> createCreateCatalogueProcess(@Nullable final CreateCatalogueProcess value)
	{
		return new JAXBElement<CreateCatalogueProcess>(_CreateCatalogueProcess_QNAME, CreateCatalogueProcess.class, null, value);
	}

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
	@XmlElementDecl(namespace = "urn:rs:ruta:client", name = "DeleteCatalogueProcess")
	@Nonnull
	public JAXBElement<DeleteCatalogueProcess> createDeleteCatalogueProcess(@Nullable final DeleteCatalogueProcess value)
	{
		return new JAXBElement<DeleteCatalogueProcess>(_DeleteCatalogueProcess_QNAME, DeleteCatalogueProcess.class, null, value);
	}

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
	@XmlElementDecl(namespace = "urn:rs:ruta:client", name = "ResolveNextCatalogueProcess")
	@Nonnull
	public JAXBElement<ResolveNextCatalogueProcess> createResolveNextCatalogueProcess(@Nullable final ResolveNextCatalogueProcess value)
	{
		return new JAXBElement<ResolveNextCatalogueProcess>(_ResolveNextProcess_QNAME, ResolveNextCatalogueProcess.class, null, value);
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
	@XmlElementDecl(namespace = "urn:rs:ruta:client", name = "OrderingProcess")
	@Nonnull
	public JAXBElement<OrderingProcess> createOrderingProcess(@Nullable final OrderingProcess value)
	{
		return new JAXBElement<OrderingProcess>(_OrderingProcess_QNAME, OrderingProcess.class, null, value);
	}

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
	@XmlElementDecl(namespace = "urn:rs:ruta:client", name = "BillingProcess")
	@Nonnull
	public JAXBElement<BillingProcess> createBillingProcess(@Nullable final BillingProcess value)
	{
		return new JAXBElement<BillingProcess>(_BillingProcess_QNAME, BillingProcess.class, null, value);
	}

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
	@XmlElementDecl(namespace = "urn:rs:ruta:client", name = "PaymentNotificationProcess")
	@Nonnull
	public JAXBElement<PaymentNotificationProcess> createPaymentNotificationProcess(@Nullable final PaymentNotificationProcess value)
	{
		return new JAXBElement<PaymentNotificationProcess>(_PaymentNotificationProcess_QNAME, PaymentNotificationProcess.class, null, value);
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
	@XmlElementDecl(namespace = "urn:rs:ruta:client", name = "PrepareCatalogueState")
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
	@XmlElementDecl(namespace = "urn:rs:ruta:client", name = "ProduceCatalogueState")
	@Nonnull
	public JAXBElement<ProduceCatalogueState> createProduceCatalogueState(@Nullable final ProduceCatalogueState value)
	{
		return new JAXBElement<ProduceCatalogueState>(_ProduceCatalogueState_QNAME, ProduceCatalogueState.class, null, value);
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
	@XmlElementDecl(namespace = "urn:rs:ruta:client", name = "DistributeCatalogueState")
	@Nonnull
	public JAXBElement<DistributeCatalogueState> createDistributeCatalogueState(@Nullable final DistributeCatalogueState value)
	{
		return new JAXBElement<DistributeCatalogueState>(_DistributeCatalogueState_QNAME, DistributeCatalogueState.class, null, value);
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
	@XmlElementDecl(namespace = "urn:rs:ruta:client", name = "ReceiveCatalogueAppResponseState")
	@Nonnull
	public JAXBElement<ReceiveCatalogueAppResponseState> createReceiveCatalogueAppResponseState(
			@Nullable final ReceiveCatalogueAppResponseState value)
	{
		return new JAXBElement<ReceiveCatalogueAppResponseState>(_ReceiveCatalogueAppResponseState_QNAME,
				ReceiveCatalogueAppResponseState.class, null, value);
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
	@XmlElementDecl(namespace = "urn:rs:ruta:client", name = "DecideOnActionState")
	@Nonnull
	public JAXBElement<DecideOnActionState> createDecideOnActionState(@Nullable final DecideOnActionState value)
	{
		return new JAXBElement<DecideOnActionState>(_DecideOnActionState_QNAME, DecideOnActionState.class, null, value);
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
	@XmlElementDecl(namespace = "urn:rs:ruta:client", name = "NextCatalogueState")
	@Nonnull
	public JAXBElement<NextCatalogueState> createNextCatalogueState(@Nullable final NextCatalogueState value)
	{
		return new JAXBElement<NextCatalogueState>(_NextCatalogueState_QNAME, NextCatalogueState.class, null, value);
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
	@XmlElementDecl(namespace = "urn:rs:ruta:client", name = "CancelCatalogueState")
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
	@XmlElementDecl(namespace = "urn:rs:ruta:client", name = "NotifyOfCatalogueDeletionState")
	@Nonnull
	public JAXBElement<NotifyOfCatalogueDeletionState> createNotifyOfCatalogueDeletionState(@Nullable final NotifyOfCatalogueDeletionState value)
	{
		return new JAXBElement<NotifyOfCatalogueDeletionState>(_NotifyOfCatalogueDeletionState_QNAME, NotifyOfCatalogueDeletionState.class, null, value);
	}

	/**
	 * Creates an instance of {@link ReceiveCatalogueDeletionAppRespState}.
	 * @return created {@code ReceiveCatalogueDeletionAppRespState} object and never {@code null}
	 */
	@Nonnull
	public ReceiveCatalogueDeletionAppRespState createReceiveCatalogueDeletionAppRespState()
	{
		return new ReceiveCatalogueDeletionAppRespState();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link ReceiveCatalogueDeletionAppRespState }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "urn:rs:ruta:client", name = "ReceiveCatalogueDeletionAppRespState")
	@Nonnull
	public JAXBElement<ReceiveCatalogueDeletionAppRespState> createReceiveCatalogueDeletionAppRespState(@Nullable final ReceiveCatalogueDeletionAppRespState value)
	{
		return new JAXBElement<ReceiveCatalogueDeletionAppRespState>(_ReceiveCatalogueDeletionAppRespState_QNAME, ReceiveCatalogueDeletionAppRespState.class, null, value);
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
	@XmlElementDecl(namespace = "urn:rs:ruta:client", name = "ReviewDeletionOfCatalogueState")
	@Nonnull
	public JAXBElement<ReviewDeletionOfCatalogueState> createReviewDeletionOfCatalogueState(@Nullable final ReviewDeletionOfCatalogueState value)
	{
		return new JAXBElement<ReviewDeletionOfCatalogueState>(_ReviewDeletionOfCatalogueState_QNAME, ReviewDeletionOfCatalogueState.class, null, value);
	}

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
	@XmlElementDecl(namespace = "urn:rs:ruta:client", name = "EndOfProcessState")
	@Nonnull
	public JAXBElement<EndOfProcessState> createEndOfProcessState(@Nullable final EndOfProcessState value)
	{
		return new JAXBElement<EndOfProcessState>(_EndOfProcessState_QNAME, EndOfProcessState.class, null, value);
	}






/*	*//**
	 * Creates an instance of {@link CreateCatalogueCorrespondenceState}.
	 * @return created {@code CreateCatalogueCorrespondenceState} object and never {@code null}
	 *//*
	@Nonnull
	public CreateCatalogueCorrespondenceState createCreateCatalogueCorrespondenceState()
	{
		return new CreateCatalogueCorrespondenceState();
	}

	*//**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link CreateCatalogueCorrespondenceState }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 *//*
	@XmlElementDecl(namespace = "urn:rs:ruta:client", name = "CreateCatalogueCorrespondenceState")
	@Nonnull
	public JAXBElement<CreateCatalogueCorrespondenceState> createCreateCatalogueCorrespondenceState(
			@Nullable final CreateCatalogueCorrespondenceState value)
	{
		return new JAXBElement<CreateCatalogueCorrespondenceState>(_CreateCatalogueCorrespondenceState_QNAME,
				CreateCatalogueCorrespondenceState.class, null, value);
	}

	*//**
	 * Creates an instance of {@link DeleteCatalogueCorrespondenceState}.
	 * @return created {@code DeleteCatalogueCorrespondenceState} object and never {@code null}
	 *//*
	@Nonnull
	public DeleteCatalogueCorrespondenceState createDeleteCatalogueCorrespondenceState()
	{
		return new DeleteCatalogueCorrespondenceState();
	}

	*//**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link DeleteCatalogueCorrespondenceState }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 *//*
	@XmlElementDecl(namespace = "urn:rs:ruta:client", name = "DeleteCatalogueCorrespondenceState")
	@Nonnull
	public JAXBElement<DeleteCatalogueCorrespondenceState> createDeleteCatalogueCorrespondenceState(
			@Nullable final DeleteCatalogueCorrespondenceState value)
	{
		return new JAXBElement<DeleteCatalogueCorrespondenceState>(_DeleteCatalogueCorrespondenceState_QNAME,
				DeleteCatalogueCorrespondenceState.class, null, value);
	}*/

/*	public RutaProcessState createRutaProcessState()
	{
		return createInstance(RutaProcessState.class);
	}*/

/*	@XmlElementDecl(name = "RutaProcessState", namespace = "urn:rs:ruta:client")
	@Nonnull
	public JAXBElement<RutaProcessState> createRutaProcessState(
			@Nullable final RutaProcessState value)
	{
		return new JAXBElement<RutaProcessState>(_RutaProcessState_QNAME, RutaProcessState.class, null, value);
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