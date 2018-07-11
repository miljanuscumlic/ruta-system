package rs.ruta.client.correspondence;

import java.awt.Color;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.UUID;

import javax.swing.JOptionPane;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.datatype.XMLGregorianCalendar;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import rs.ruta.client.BusinessParty;
import rs.ruta.client.MyParty;
import rs.ruta.client.RutaClient;
import rs.ruta.common.InstanceFactory;
import rs.ruta.common.datamapper.DetailException;
import rs.ruta.common.datamapper.MapperRegistry;

/**
 * Class encapsulating {@link Correspondence} between parties during which {@link OrderType},
 * {@link InvoiceType} and alike {@code UBL} business documents are exchanged among parties
 * of the {@code Ruta system}.
 */
@XmlRootElement(name = "BuyingCorrespondence")
public class BuyingCorrespondence extends Correspondence
{
	/**
	 * Constructs new instance of a {@link BuyingCorrespondence} and sets its state to
	 * default value and uuid to a random value.
	 * @param client {@link RutaClient} object
	 * @param correspondentParty correspondent {@link PartyType}
	 * @param buyer true if correspondence is on the Buyer's Party side, false if on the Seller's Party side
	 * @return {@code BuyingCorrespondence}
	 */
	public static BuyingCorrespondence newInstance(RutaClient client, BusinessParty correspondentParty, boolean buyer)
	{
		BuyingCorrespondence corr = new BuyingCorrespondence();
		corr.setId(UUID.randomUUID().toString());
		if(buyer)
			corr.setState(BuyerOrderingProcess.newInstance(client));
		else
			corr.setState(SellerOrderingProcess.newInstance(client));
		corr.setClient(client);
		corr.setName(corr.uuid.getValue());
		corr.setCorrespondentParty(correspondentParty.getCoreParty());
		final XMLGregorianCalendar currentDateTime = InstanceFactory.getDate();
		corr.setCreationTime(currentDateTime);
		corr.setLastActivityTime(currentDateTime);
		corr.setActive(true);
		corr.setStopped(false);
		corr.setRecentlyUpdated(true);
		client.getClientFrame().appendToConsole(new StringBuilder(Messages.getString("BuyingCorrespondence.0")). 
				append(corr.getName()).append(Messages.getString("BuyingCorrespondence.1")), Color.BLACK); 
		return corr;
	}

	@Override
	public void run()
	{
		try
		{
			final Thread myThread = Thread.currentThread();
			while (thread == myThread && active && !stopped)
			{
				state.doActivity(this);
			}
			if(discarded)
			{
				delete();
				client.getClientFrame().appendToConsole(new StringBuilder(Messages.getString("BuyingCorrespondence.2")). 
						append(getName()).append(Messages.getString("BuyingCorrespondence.3")), Color.BLACK); 
			}
		}
		catch(Exception e)
		{
/*			EventQueue.invokeLater(() ->
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error messsage", JOptionPane.ERROR_MESSAGE));*/
			getClient().getClientFrame().
			processExceptionAndAppendToConsole(e, new StringBuilder(Messages.getString("BuyingCorrespondence.4")). 
					append(getIdValue()).append(Messages.getString("BuyingCorrespondence.5"))); 
		}
		finally
		{
/*			if(stopped)
				signalThreadStopped();*/
		}
	}

	@Override
	protected synchronized void doStore() throws DetailException
	{
		MapperRegistry.getInstance().getMapper(BuyingCorrespondence.class).insert(null, this);
	}

	@Override
	protected synchronized void doDelete() throws DetailException
	{
		getClient().getMyParty().removeBuyingCorrespondence(this);
	}
}