package rs.ruta.client;

import java.util.*;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PartyType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.NameType;

public class PartyTreeModel extends DefaultTreeModel
{

	private static final long serialVersionUID = 4960608964751110674L;
	private MyParty party;

	public PartyTreeModel(TreeNode root)
	{
		super(root);
	}

	public PartyTreeModel(TreeNode root, MyParty party)
	{
		super(root);
		this.party = party;
		populateTree();
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
			String firstName = InstanceFactory.getPropertyOrNull(first.getCoreParty().getPartyName().get(0).getName(), NameType::getValue);
			String secondName = InstanceFactory.getPropertyOrNull(second.getCoreParty().getPartyName().get(0).getName(), NameType::getValue);
			if(firstName == null)
				if(secondName == null)
					return 0;
				else
					return 1;
			else
				return firstName.compareToIgnoreCase(secondName);
		};

		// all business partners that are followed
		Set<BusinessParty> followingPartners = new TreeSet<BusinessParty>(partyNameComparator);
		// all other parties that are followed
		Set<BusinessParty> followingOthers = new TreeSet<BusinessParty>(partyNameComparator);

		followingPartners.addAll(party.getBusinessPartners());
		followingPartners.retainAll(party.getFollowingParties());

		followingOthers.addAll(party.getFollowingParties());
		if(party.getFollowingParties().size() > 0) // removes my Party
			followingOthers.remove((party.getFollowingParties().get(0)));
		followingOthers.removeAll(party.getBusinessPartners());

		if(party.getFollowingParties().size() > 0)
		{
			DefaultMutableTreeNode myPartyNode = new DefaultMutableTreeNode(party.getFollowingParties().get(0));
			((DefaultMutableTreeNode) root).add(myPartyNode);
		}

		DefaultMutableTreeNode fPartnersNode = new DefaultMutableTreeNode("Business Partners");
		((DefaultMutableTreeNode) root).add(fPartnersNode);

		for(BusinessParty fParty: followingPartners)
			fPartnersNode.add(new DefaultMutableTreeNode(fParty));

		DefaultMutableTreeNode fOthersNode = new DefaultMutableTreeNode("Other Parties");
		((DefaultMutableTreeNode) root).add(fOthersNode);

		for(BusinessParty fOther : followingOthers)
			fOthersNode.add(new DefaultMutableTreeNode(fOther));

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
	public boolean isLeaf(Object node)
	{
		// TODO Auto-generated method stub
		return false;
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
