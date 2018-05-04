package rs.ruta.client.correspondence.buying.ordering.buyer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

import rs.ruta.client.correspondence.BuyerAcceptOrderState;
import rs.ruta.client.correspondence.BuyerCancelOrderState;
import rs.ruta.client.correspondence.BuyerChangeOrderState;
import rs.ruta.client.correspondence.BuyerOrderAcceptedState;
import rs.ruta.client.correspondence.BuyerOrderRejectedState;
import rs.ruta.client.correspondence.BuyerPlaceOrderState;
import rs.ruta.client.correspondence.BuyerProcessResponseState;
import rs.ruta.client.correspondence.BuyerReceiveOrderResponseState;

/**
 * {@code ObjectFactory} is a helper class in the process of mapping objects to {@code XML} elements.
 * {@code ObjectFactory} has two methods {@code createXxx} for every class which objects it is mapping.
 * One method is for instantiating the object, and the other is for instantiating the {@link JAXBElement}
 * that is an representation of the XML element to which it is mapped.
 */
@XmlRegistry
public class ObjectFactory
{

}