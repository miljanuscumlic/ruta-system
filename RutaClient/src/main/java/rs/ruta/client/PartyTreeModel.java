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
	private BusinessParty party;

	public PartyTreeModel(TreeNode root)
	{
		super(root);
	}

	public PartyTreeModel(TreeNode root, BusinessParty party)
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

		Comparator<PartyType> partyNameComparator = (first, second)  ->
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
		};

		// all business partners that are followed
		Set<PartyType> followingPartners = new TreeSet<PartyType>(partyNameComparator);
		// all other parties that are followed
		Set<PartyType> followingOthers = new TreeSet<PartyType>(partyNameComparator);

		followingPartners.addAll(party.getBusinessPartners());
		followingPartners.retainAll(party.getFollowingParties());

		followingOthers.addAll(party.getFollowingParties());
		followingOthers.removeAll(party.getBusinessPartners());

		DefaultMutableTreeNode myPartyNode = new DefaultMutableTreeNode("My Party");
		((DefaultMutableTreeNode) root).add(myPartyNode);

		DefaultMutableTreeNode fPartnersNode = new DefaultMutableTreeNode("Business Partners");
		((DefaultMutableTreeNode) root).add(fPartnersNode);

		for(PartyType fParty: followingPartners)
			fPartnersNode.add(new DefaultMutableTreeNode(fParty.getPartyName().get(0).getName().getValue()));

		DefaultMutableTreeNode fOthersNode = new DefaultMutableTreeNode("Other Parties");
		((DefaultMutableTreeNode) root).add(fOthersNode);

		for(PartyType fOther : followingOthers)
			fOthersNode.add(new DefaultMutableTreeNode(fOther.getPartyName().get(0).getName().getValue()));

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
