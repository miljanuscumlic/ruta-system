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
public class CorrespondenceTreeModel extends RutaTreeModel
{
	private static final long serialVersionUID = -841281945123956954L;
	private static final String BUSINESS_PARTNERS = "Business Partners";
	private static final String MY_PARTY = "My Party";
	private MyParty myParty;
	//using sets for keeping sorted collection of parties and because they are faster than lists
	//when it comes to the sorting; sets should be maintained and represent current view of data model
	private Set<BusinessParty> businessPartners;

	public CorrespondenceTreeModel(TreeNode root, MyParty myParty)
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
		businessPartners.addAll(myParty.getBusinessPartners());

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

		return root;
	}

	/**
	 * Gets the index of the node containing the object in the tree, relative to its parent.
	 * @param object contained object of the node
	 * @param collection set to be searched
	 * @return index of the node in the tree or -1 if node has not been found
	 */
	private <T> int getIndex(T object, Set<T> collection)
	{
		int index = -1;
		for(T element: collection)
		{
			index++;
			if(object.equals(element))
				break;
		}
		return index < collection.size() ? index : -1;
	}


	@Override
	public void addNode(Object userObject, String command)
	{

	}

	//MMM should be changed; lines in the method left only for reference purpose
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
				businessPartners.clear();
				deleteChildrenNodes(BUSINESS_PARTNERS);
			}
		}
	}

}