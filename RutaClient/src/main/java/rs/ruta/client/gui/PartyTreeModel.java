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
import rs.ruta.client.BusinessPartyEvent;

/**
 * Tree model that is an adapter model for the parties of {@link MyParty} object.
 */
public class PartyTreeModel extends RutaTreeModel
{
	private static final String DEREGISTERED_PARTIES = "Deregistered Parties";
	private static final String ARCHIVED_PARTIES = "Archived Parties";
	private static final String OTHER_PARTIES = "Other Parties";
	private static final String BUSINESS_PARTNERS = "Business Partners";
	private static final String MY_PARTY = "My Party";
	private static final long serialVersionUID = 4960608964751110674L;
	private MyParty myParty;
	//using sets for keeping sorted collection of parties and because they are faster than lists
	//when it comes to the sorting; sets should be maintained and represent current view of data model
	private Set<BusinessParty> businessPartners;
	private Set<BusinessParty> otherParties;
	private Set<BusinessParty> archivedParties;
	private Set<BusinessParty> deregisteredParties;

	public PartyTreeModel(TreeNode root, MyParty myParty)
	{
		super(root);
		this.myParty = myParty;
		populateTree();
		setAsksAllowsChildren(true);
		myParty.addActionListener(this, BusinessPartyEvent.class);
	}

	/**
	 * Constructs {@link DefaultMutableTreeNode nodes} from objects of the model and populates the tree with them.
	 * @return {@link TreeNode root node}
	 */
	@Override
	protected TreeNode populateTree()
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
		deregisteredParties = new TreeSet<BusinessParty>(partyNameComparator);
		businessPartners.addAll(myParty.getBusinessPartners());
		otherParties.addAll(myParty.getOtherParties());
		archivedParties.addAll(myParty.getArchivedParties());
		deregisteredParties.addAll(myParty.getDeregisteredParties());

		DefaultMutableTreeNode myPartyNode = new DefaultMutableTreeNode(MY_PARTY);
		((DefaultMutableTreeNode) root).add(myPartyNode);
		if(myParty.getMyFollowingParty() != null)
		{
			DefaultMutableTreeNode myFollowingPartyNode = new DefaultMutableTreeNode(myParty.getMyFollowingParty());
			myFollowingPartyNode.setAllowsChildren(false);
			myPartyNode.add(myFollowingPartyNode);
		}

		DefaultMutableTreeNode businessPartnersNode = new DefaultMutableTreeNode(BUSINESS_PARTNERS);
		((DefaultMutableTreeNode) root).add(businessPartnersNode);
		for(BusinessParty fParty: businessPartners)
		{
			DefaultMutableTreeNode partnerNode = new DefaultMutableTreeNode(fParty);
			partnerNode.setAllowsChildren(false);
			businessPartnersNode.add(partnerNode);
		}

		DefaultMutableTreeNode otherPartiesNode = new DefaultMutableTreeNode(OTHER_PARTIES);
		((DefaultMutableTreeNode) root).add(otherPartiesNode);
		for(BusinessParty fOther : otherParties)
		{
			DefaultMutableTreeNode otherNode = new DefaultMutableTreeNode(fOther);
			otherNode.setAllowsChildren(false);
			otherPartiesNode.add(otherNode);
		}

		DefaultMutableTreeNode archivedPartiesNode = new DefaultMutableTreeNode(ARCHIVED_PARTIES);
		((DefaultMutableTreeNode) root).add(archivedPartiesNode);
		for(BusinessParty archived : archivedParties)
		{
			DefaultMutableTreeNode archivedNode = new DefaultMutableTreeNode(archived);
			archivedNode.setAllowsChildren(false);
			archivedPartiesNode.add(archivedNode);
		}

		DefaultMutableTreeNode deregisteredPartiesNode = new DefaultMutableTreeNode(DEREGISTERED_PARTIES);
		((DefaultMutableTreeNode) root).add(deregisteredPartiesNode);
		for(BusinessParty deregistered : deregisteredParties)
		{
			DefaultMutableTreeNode deregisteredNode = new DefaultMutableTreeNode(deregistered);
			deregisteredNode.setAllowsChildren(false);
			deregisteredPartiesNode.add(deregisteredNode);
		}

		return root;
	}

	/**
	 * Gets the index of the node in the tree, relative to its parent.
	 * @param party contained object of the node
	 * @param collection set to be searched
	 * @return index of the node in the tree or -1 if node has not been found
	 */
	private int getIndex(BusinessParty party, Set<BusinessParty> collection)
	{
		int index = -1;
		for(BusinessParty element: collection)
		{
			index++;
			if(party.equals(element))
				break;
		}
		return index < collection.size() ? index : -1;
	}

	/**
	 * Adds node to the model right after the parent node i.e. at the index 0.
	 * @param userObject object contained in new node
	 * @param command event command that is resulting in node addition to the model
	 */
	@Override
	public void addNode(Object userObject, String command)
	{
		final BusinessParty userParty = (BusinessParty) userObject;
		final DefaultMutableTreeNode node = new DefaultMutableTreeNode(userParty);
		node.setAllowsChildren(false);
		if(userParty.isPartner())
			insertNodeInto(node, searchNode(BUSINESS_PARTNERS), getIndex(userParty, businessPartners));
		else if(userParty.isFollowing())
			insertNodeInto(node, searchNode(OTHER_PARTIES), getIndex(userParty, otherParties));
		else if(userParty.isDeregistered())
			insertNodeInto(node, searchNode(DEREGISTERED_PARTIES), getIndex(userParty, deregisteredParties));
		else if(userParty.isArchived())
			insertNodeInto(node, searchNode(ARCHIVED_PARTIES), getIndex(userParty, archivedParties));
		else
			insertNodeInto(node, searchNode(MY_PARTY), 0);
		nodeChanged(node); //necessary when new display name is longer than the old one which is replaced
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
				addNode(sourceParty, command);
			}
			else if(BusinessPartyEvent.BUSINESS_PARTNER_TRANSFERED.equals(command))
			{
				//delete from other parties
				otherParties.remove(sourceParty);
				deleteNode(sourceParty);
				//add to business partners
				businessPartners.add(sourceParty);
				addNode(sourceParty, command);
			}
			else if(BusinessPartyEvent.BUSINESS_PARTNER_REMOVED.equals(command))
			{
				//delete from business partners
				businessPartners.remove(sourceParty);
				deleteNode(sourceParty);
				//add to archived parties
				archivedParties.add(sourceParty);
				addNode(sourceParty, command);
			}
			else if(BusinessPartyEvent.OTHER_PARTY_ADDED.equals(command))
			{
				//add to other parties
				otherParties.add(sourceParty);
				addNode(sourceParty, command);
			}
			else if(BusinessPartyEvent.OTHER_PARTY_TRANSFERED.equals(command))
			{
				//delete from business partners
				businessPartners.remove(sourceParty);
				deleteNode(sourceParty);
				//add to other parties
				otherParties.add(sourceParty);
				addNode(sourceParty, command);
			}
			else if(BusinessPartyEvent.OTHER_PARTY_REMOVED.equals(command))
			{
				//remove from other parties
				otherParties.remove(sourceParty);
				deleteNode(sourceParty);
				//add to archived parties
				archivedParties.add(sourceParty);
				addNode(sourceParty, command);
			}
			else if(BusinessPartyEvent.ARCHIVED_PARTY_ADDED.equals(command))
			{
				//add to archived parties
				archivedParties.add(sourceParty);
				addNode(sourceParty, command);
			}
			else if(BusinessPartyEvent.ARCHIVED_PARTY_REMOVED.equals(command))
			{
				//delete from archived parties
				archivedParties.remove(sourceParty);
				deleteNode(sourceParty);
			}
			else if(BusinessPartyEvent.DEREGISTERED_PARTY_ADDED.equals(command))
			{
				//delete from a list if it is contained in any
				businessPartners.remove(sourceParty);
				otherParties.remove(sourceParty);
				archivedParties.remove(sourceParty);
				deleteNode(sourceParty);
				//add to deregistered parties
				deregisteredParties.add(sourceParty);
				addNode(sourceParty, command);
			}
			else if(BusinessPartyEvent.DEREGISTERED_PARTY_REMOVED.equals(command))
			{
				//delete from deregistered parties
				deregisteredParties.remove(sourceParty);
				deleteNode(sourceParty);
			}
			else if(BusinessPartyEvent.MY_FOLLOWING_PARTY_ADDED.equals(command))
			{
				//add following party
				addNode(sourceParty, command);
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
				deregisteredParties.clear();
				deleteChildrenNodes(DEREGISTERED_PARTIES);
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
			else if(BusinessPartyEvent.DEREGISTERED_LIST_REMOVED.equals(command))
			{
				deregisteredParties.clear();
				deleteChildrenNodes(DEREGISTERED_PARTIES);
			}
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

	@Deprecated
	@Override
	public boolean listensFor(Class<? extends ActionEvent> eventClazz)
	{
		return eventClazz == null ? false : eventClazz == BusinessPartyEvent.class;
	}
}