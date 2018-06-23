package rs.ruta.client.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Nullable;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import rs.ruta.client.BusinessParty;
import rs.ruta.client.MyParty;
import rs.ruta.common.PartnershipRequest;

/**
 * Tree model that is an adapter model for the parties of {@link MyParty} object.
 */
public class PartyTreeModel extends RutaTreeModel
{
	private static final long serialVersionUID = 4960608964751110674L;
//	private static final String DEREGISTERED_PARTIES = "Deregistered Parties";
	private static final String ARCHIVED_PARTIES = "Archived Parties";
	private static final String OTHER_PARTIES = "Other Parties";
	private static final String BUSINESS_PARTNERS = "Business Partners";
	private static final String MY_PARTY = "My Party";
	private static final String PARTNERSHIP_REQUESTS = "Partnership Requests";
	private static final String OUTBOUND_PARTNERSHIPS = "Sent";
	private static final String INBOUND_PARTNERSHIPS = "Received";

	private MyParty myParty;
	//using sets for keeping sorted collection of parties and because they are faster than lists
	//when it comes to the sorting; sets should be maintained and represent current view of data model
	private Set<BusinessParty> businessPartners;
	private Set<BusinessParty> otherParties;
	private Set<BusinessParty> archivedParties;
//	private Set<BusinessParty> deregisteredParties;

	public PartyTreeModel(TreeNode root, MyParty myParty)
	{
		super(root);
		this.myParty = myParty;
		populateModel(myParty);
		populateTree();
		setAsksAllowsChildren(true);
		myParty.addActionListener(this, BusinessPartyEvent.class);
//		myParty.addActionListener(this, PartnershipEvent.class);
	}

	@Override
	protected void populateModel(MyParty myParty)
	{
		Comparator<BusinessParty> partyNameComparator = (first, second)  ->
		{
			final String firstName = first.getPartySimpleName();
			final String secondName = second.getPartySimpleName();
			if(firstName == null)
			{
				if(secondName == null)
					return 0;
				else
					return 1;
			}
			else if(firstName.equals(secondName)) //enable set to have elements with the same name
			{
				if(first.getPartyID().equals(second.getPartyID())) //disable set to have same elements
					return 0;
				else
					return 1;
			}
			else
				return firstName.compareToIgnoreCase(secondName);
		};

		businessPartners = new TreeSet<BusinessParty>(partyNameComparator);
		otherParties = new TreeSet<BusinessParty>(partyNameComparator);
		archivedParties =  new TreeSet<BusinessParty>(partyNameComparator);
//		deregisteredParties = new TreeSet<BusinessParty>(partyNameComparator);
		businessPartners.addAll(myParty.getBusinessPartners());
		otherParties.addAll(myParty.getOtherParties());
		archivedParties.addAll(myParty.getArchivedParties());
//		deregisteredParties.addAll(myParty.getDeregisteredParties());
	}

	/**
	 * Constructs {@link DefaultMutableTreeNode nodes} from objects of the model and populates the tree with them.
	 * @return {@link TreeNode root node}
	 */
	@Override
	protected TreeNode populateTree()
	{
		final DefaultMutableTreeNode myPartyNode = new DefaultMutableTreeNode(MY_PARTY);
		((DefaultMutableTreeNode) root).add(myPartyNode);
		if(myParty.getMyFollowingParty() != null)
		{
			final DefaultMutableTreeNode myFollowingPartyNode = new DefaultMutableTreeNode(myParty.getMyFollowingParty());
			myFollowingPartyNode.setAllowsChildren(false);
			myPartyNode.add(myFollowingPartyNode);
		}

		final DefaultMutableTreeNode businessPartnersNode = new DefaultMutableTreeNode(BUSINESS_PARTNERS);
		((DefaultMutableTreeNode) root).add(businessPartnersNode);
		for(BusinessParty fParty: businessPartners)
		{
			final DefaultMutableTreeNode partnerNode = new DefaultMutableTreeNode(fParty);
			partnerNode.setAllowsChildren(false);
			businessPartnersNode.add(partnerNode);
		}

		final DefaultMutableTreeNode otherPartiesNode = new DefaultMutableTreeNode(OTHER_PARTIES);
		((DefaultMutableTreeNode) root).add(otherPartiesNode);
		for(BusinessParty fOther : otherParties)
		{
			final DefaultMutableTreeNode otherNode = new DefaultMutableTreeNode(fOther);
			otherNode.setAllowsChildren(false);
			otherPartiesNode.add(otherNode);
		}

		final DefaultMutableTreeNode partnershipsNode = new DefaultMutableTreeNode(PARTNERSHIP_REQUESTS);
		((DefaultMutableTreeNode) root).add(partnershipsNode);
		final DefaultMutableTreeNode outboundPartnershipsNode = new DefaultMutableTreeNode(OUTBOUND_PARTNERSHIPS);
		outboundPartnershipsNode.setAllowsChildren(false);
		partnershipsNode.add(outboundPartnershipsNode);
//		for(PartnershipRequest pRequest: outboundPartnershipRequests)
//		{
//			if(!pRequest.isResolved())
//			{
//				final DefaultMutableTreeNode outboundNode = new DefaultMutableTreeNode(pRequest);
//				outboundNode.setAllowsChildren(false);
//				outboundPartnershipsNode.add(outboundNode);
//			}
//		}
		final DefaultMutableTreeNode inboundPartnershipsNode = new DefaultMutableTreeNode(INBOUND_PARTNERSHIPS);
		inboundPartnershipsNode.setAllowsChildren(false);
		partnershipsNode.add(inboundPartnershipsNode);
//		for(PartnershipRequest pRequest: inboundPartnershipRequests)
//		{
//			if(!pRequest.isResolved())
//			{
//				final DefaultMutableTreeNode inboundNode = new DefaultMutableTreeNode(pRequest);
//				inboundNode.setAllowsChildren(false);
//				inboundPartnershipsNode.add(inboundNode);
//			}
//		}

		final DefaultMutableTreeNode archivedPartiesNode = new DefaultMutableTreeNode(ARCHIVED_PARTIES);
		((DefaultMutableTreeNode) root).add(archivedPartiesNode);
		for(BusinessParty archived : archivedParties)
		{
			final DefaultMutableTreeNode archivedNode = new DefaultMutableTreeNode(archived);
			archivedNode.setAllowsChildren(false);
			archivedPartiesNode.add(archivedNode);
		}

/*		final DefaultMutableTreeNode deregisteredPartiesNode = new DefaultMutableTreeNode(DEREGISTERED_PARTIES);
		((DefaultMutableTreeNode) root).add(deregisteredPartiesNode);
		for(BusinessParty deregistered : deregisteredParties)
		{
			final DefaultMutableTreeNode deregisteredNode = new DefaultMutableTreeNode(deregistered);
			deregisteredNode.setAllowsChildren(false);
			deregisteredPartiesNode.add(deregisteredNode);
		}*/

		return root;
	}

	@Override
	protected void addNode(Object userObject)
	{
		if(userObject.getClass() == BusinessParty.class)
		{
			final BusinessParty userParty = (BusinessParty) userObject;
			final DefaultMutableTreeNode node = new DefaultMutableTreeNode(userParty);
			node.setAllowsChildren(false);
			if(userParty.isPartner())
				insertNodeInto(node, searchNode(BUSINESS_PARTNERS), getIndex(userParty, businessPartners));
			else if(userParty.isFollowing())
				insertNodeInto(node, searchNode(OTHER_PARTIES), getIndex(userParty, otherParties));
	/*		else if(userParty.isDeregistered())
				insertNodeInto(node, searchNode(DEREGISTERED_PARTIES), getIndex(userParty, deregisteredParties));*/
			else if(userParty.isArchived())
				insertNodeInto(node, searchNode(ARCHIVED_PARTIES), getIndex(userParty, archivedParties));
			else
				insertNodeInto(node, searchNode(MY_PARTY), 0);
			nodeChanged(node); //necessary when new display name is longer than the old one which is replaced
		}

	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		Object source = event.getSource();
		String command = event.getActionCommand();

		if(source.getClass() == BusinessParty.class)
		{
			final BusinessParty sourceParty = (BusinessParty) source;

			if(BusinessPartyEvent.BUSINESS_PARTNER_ADDED.equals(command))
			{
				//add to business partners
				businessPartners.add(sourceParty);
				addNode(sourceParty);
			}
			else if(BusinessPartyEvent.BUSINESS_PARTNER_TRANSFERED.equals(command))
			{
				//delete from other parties
				otherParties.remove(sourceParty);
				deleteNode(sourceParty);
				//add to business partners
				businessPartners.add(sourceParty);
				addNode(sourceParty);
			}
			else if(BusinessPartyEvent.BUSINESS_PARTNER_REMOVED.equals(command))
			{
				//delete from business partners
				businessPartners.remove(sourceParty);
				deleteNode(sourceParty);
				//add to archived parties
//				archivedParties.add(sourceParty);
//				addNode(sourceParty);
			}
			else if(BusinessPartyEvent.OTHER_PARTY_ADDED.equals(command))
			{
				//add to other parties
				otherParties.add(sourceParty);
				addNode(sourceParty);
			}
			else if(BusinessPartyEvent.OTHER_PARTY_TRANSFERED.equals(command))
			{
				//delete from business partners
				businessPartners.remove(sourceParty);
				deleteNode(sourceParty);
				//add to other parties
				otherParties.add(sourceParty);
				addNode(sourceParty);
			}
			else if(BusinessPartyEvent.OTHER_PARTY_REMOVED.equals(command))
			{
				//remove from other parties
				otherParties.remove(sourceParty);
				deleteNode(sourceParty);
				//add to archived parties
//				archivedParties.add(sourceParty);
//				addNode(sourceParty);
			}
			else if(BusinessPartyEvent.ARCHIVED_PARTY_ADDED.equals(command))
			{
				//add to archived parties
				archivedParties.add(sourceParty);
				addNode(sourceParty);
			}
			else if(BusinessPartyEvent.ARCHIVED_PARTY_REMOVED.equals(command))
			{
				//delete from archived parties
				archivedParties.remove(sourceParty);
				deleteNode(sourceParty);
			}
			/*else if(BusinessPartyEvent.DEREGISTERED_PARTY_ADDED.equals(command))
			{	//MMM check is it necessary to remove form the lists
				//delete from a list if it is contained in any
				businessPartners.remove(sourceParty);
				otherParties.remove(sourceParty);
				archivedParties.remove(sourceParty);
				deleteNode(sourceParty);
				//add to deregistered parties
				deregisteredParties.add(sourceParty);
				addNode(sourceParty);
			}
			else if(BusinessPartyEvent.DEREGISTERED_PARTY_REMOVED.equals(command))
			{
				//delete from deregistered parties
				deregisteredParties.remove(sourceParty);
				deleteNode(sourceParty);
			}*/
			else if(BusinessPartyEvent.MY_FOLLOWING_PARTY_ADDED.equals(command))
			{
				//add following party
				addNode(sourceParty);
			}
			else if(BusinessPartyEvent.MY_FOLLOWING_PARTY_REMOVED.equals(command))
			{
				//delete my following party
				deleteNode(sourceParty);
			}
			else if(BusinessPartyEvent.PARTY_UPDATED.equals(command))
			{
				//update node text
				updateNode(sourceParty);
			}
		}
		else if(source.getClass() == ArrayList.class)
		{
			if(BusinessPartyEvent.ALL_PARTIES_REMOVED.equals(command))
			{
				archivedParties.clear();
				deleteChildrenNodes(ARCHIVED_PARTIES);
/*				deregisteredParties.clear();
				deleteChildrenNodes(DEREGISTERED_PARTIES);*/
				businessPartners.clear();
				deleteChildrenNodes(BUSINESS_PARTNERS);
				otherParties.clear();
				deleteChildrenNodes(OTHER_PARTIES);
			}
			else if(BusinessPartyEvent.ARCHIVED_LIST_REMOVED.equals(command))
			{
				archivedParties.clear();
				deleteChildrenNodes(ARCHIVED_PARTIES);
			}
/*			else if(BusinessPartyEvent.DEREGISTERED_LIST_REMOVED.equals(command))
			{
				deregisteredParties.clear();
				deleteChildrenNodes(DEREGISTERED_PARTIES);
			}*/
			else if(BusinessPartyEvent.BUSINESS_LIST_REMOVED.equals(command))
			{
				businessPartners.clear();
				deleteChildrenNodes(BUSINESS_PARTNERS);
			}
			else if(BusinessPartyEvent.OTHER_LIST_REMOVED.equals(command))
			{
				otherParties.clear();
				deleteChildrenNodes(OTHER_PARTIES);
			}
		}
	}
}