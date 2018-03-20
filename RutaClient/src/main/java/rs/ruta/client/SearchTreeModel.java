package rs.ruta.client;

import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import oasis.names.specification.ubl.schema.xsd.catalogue_21.CatalogueType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;

public class SearchTreeModel extends DefaultTreeModel
{
	private static final long serialVersionUID = -5486578776182215565L;
	private MyParty party;

	public SearchTreeModel(TreeNode root)
	{
		super(root);
	}

	public SearchTreeModel(TreeNode root, MyParty party)
	{
		super(root);
		this.party = party;
		populateTree();
		setAsksAllowsChildren(true);
	}

	public MyParty getParty()
	{
		return party;
	}

	private TreeNode populateTree()
	{
/*		Comparator<BusinessParty> partyNameComparator = (first, second)  ->
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
		};*/

		List<Search<PartyType>> partySearches = party.getPartySearches();
		List<Search<CatalogueType>> catalogueSearches = party.getCatalogueSearches();

/*		// all business partners that are followed
		Set<BusinessParty> followingPartners = new TreeSet<BusinessParty>(partyNameComparator);
		// all other parties that are followed
		Set<BusinessParty> followingOthers = new TreeSet<BusinessParty>(partyNameComparator);
		//MMM: add a list of Searches and results

		followingPartners.addAll(party.getBusinessPartners());
		followingOthers.addAll(party.getFollowingParties());
//		followingPartners.retainAll(party.getFollowingParties()); // MMM: Why is this not working?
		followingPartners.retainAll(followingOthers);

		followingOthers.removeAll(party.getBusinessPartners());*/

		DefaultMutableTreeNode partySearchNode = new DefaultMutableTreeNode("Parties");
		((DefaultMutableTreeNode) root).add(partySearchNode);

		for(Search<PartyType> pSearch: partySearches)
		{
			DefaultMutableTreeNode pSearchNode = new DefaultMutableTreeNode(pSearch);
			pSearchNode.setAllowsChildren(false);
			partySearchNode.add(pSearchNode);
		}

		DefaultMutableTreeNode catalogueSearchNode = new DefaultMutableTreeNode("Catalogues");
		((DefaultMutableTreeNode) root).add(catalogueSearchNode);

		for(Search<CatalogueType> cSearch : catalogueSearches)
		{
			DefaultMutableTreeNode cSearchNode = new DefaultMutableTreeNode(cSearch);
			cSearchNode.setAllowsChildren(false);
			catalogueSearchNode.add(cSearchNode);
		}

		return root;
	}

}
