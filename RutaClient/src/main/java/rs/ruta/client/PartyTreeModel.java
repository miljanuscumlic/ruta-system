package rs.ruta.client;

import java.util.*;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.NameType;
import rs.ruta.common.InstanceFactory;

public class PartyTreeModel extends DefaultTreeModel
{
	private static final long serialVersionUID = 4960608964751110674L;
	private MyParty party;
	//using sets because they are faster than lists when it comes to the sorting
	private Set<BusinessParty> followingPartners; // all business partners that are followed
	private Set<BusinessParty> followingOthers;	// all other parties that are followed
	private Set<BusinessParty> archivedParties;

	public PartyTreeModel(TreeNode root)
	{
		super(root);
	}

	public PartyTreeModel(TreeNode root, MyParty party)
	{
		super(root);
		this.party = party;
		populateTree();
		setAsksAllowsChildren(true);
	}

	public TreeNode populateTree()
	{
		/*		Comparator<PartyType> partyNameComparator = new Comparator<PartyType>()
		{
			@Override
			public int compare(PartyType first, PartyType second)
			{
				String firstName = InstanceFactory.getPropertyOrNull(first.getPartyName().get(0).getName(), NameType::getValue);
				String secondName = InstanceFactory.getPropertyOrNull(second.getPartyName().get(0).getName(), NameType::getValue);
				if(firstName == null)
					if(secondName == null)
						return 0;
					else
						return 1;
				else
					return firstName.compareToIgnoreCase(secondName);
			}
		};*/

		Comparator<BusinessParty> partyNameComparator = (first, second)  ->
		{
/*			String firstName = InstanceFactory.getPropertyOrNull(first.getCoreParty().getPartyName().get(0).getName(), NameType::getValue);
			String secondName = InstanceFactory.getPropertyOrNull(second.getCoreParty().getPartyName().get(0).getName(), NameType::getValue);*/
			final String firstName = first.getPartyName();
			final String secondName = second.getPartyName();
			if(firstName == null)
				if(secondName == null)
					return 0;
				else
					return 1;
			else
				return firstName.compareToIgnoreCase(secondName);
		};

		followingPartners = new TreeSet<BusinessParty>(partyNameComparator);
		followingOthers = new TreeSet<BusinessParty>(partyNameComparator);
		archivedParties =  new TreeSet<BusinessParty>(partyNameComparator);
		followingPartners.addAll(party.getBusinessPartners());
		followingOthers.addAll(party.getOtherParties());
		archivedParties.addAll(party.getArchivedParties());

		DefaultMutableTreeNode myPartyNode = new DefaultMutableTreeNode("My Party");
		((DefaultMutableTreeNode) root).add(myPartyNode);

		if(party.getMyFollowingParty() != null)
		{
			DefaultMutableTreeNode myParty = new DefaultMutableTreeNode(party.getMyFollowingParty());
			myParty.setAllowsChildren(false);
			myPartyNode.add(myParty);
		}

		DefaultMutableTreeNode businessPartnersNode = new DefaultMutableTreeNode("Business Partners");
		((DefaultMutableTreeNode) root).add(businessPartnersNode);

		for(BusinessParty fParty: followingPartners)
		{
			DefaultMutableTreeNode partnerNode = new DefaultMutableTreeNode(fParty);
			partnerNode.setAllowsChildren(false);
			businessPartnersNode.add(partnerNode);
		}

		DefaultMutableTreeNode otherPartiesNode = new DefaultMutableTreeNode("Other Parties");
		((DefaultMutableTreeNode) root).add(otherPartiesNode);

		for(BusinessParty fOther : followingOthers)
		{
			DefaultMutableTreeNode otherNode = new DefaultMutableTreeNode(fOther);
			otherNode.setAllowsChildren(false);
			otherPartiesNode.add(otherNode);
		}

		DefaultMutableTreeNode archivedPartiesNode = new DefaultMutableTreeNode("Archived Parties");
		((DefaultMutableTreeNode) root).add(archivedPartiesNode);

		for(BusinessParty archived : archivedParties)
		{
			DefaultMutableTreeNode archivedNode = new DefaultMutableTreeNode(archived);
			archivedNode.setAllowsChildren(false);
			archivedPartiesNode.add(archivedNode);
		}



		return root;
	}

	@Override
	public Object getRoot()
	{
		return root;
	}

	public void setRoot(DefaultMutableTreeNode root)
	{
		this.root = root;
	}

// 	This method overriding is the alternative to setAllowsChildren code inserted in the class methods
//	@Override
//	public boolean isLeaf(Object node)
//	{
//		return ((DefaultMutableTreeNode)node).getUserObject() instanceof String ? false : true;
//	}


	/*	public PartyTreeModel(PartyType root)
	{
		this.root = root;
	}

  @Override
	public Object getChild(Object parent, int index)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getChildCount(Object parent)
	{

		return 0;
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public int getIndexOfChild(Object parent, Object child)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void addTreeModelListener(TreeModelListener l)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void removeTreeModelListener(TreeModelListener l)
	{
		// TODO Auto-generated method stub

	}*/

}
