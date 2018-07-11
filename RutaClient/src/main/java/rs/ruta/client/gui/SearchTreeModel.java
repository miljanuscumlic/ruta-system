package rs.ruta.client.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import oasis.names.specification.ubl.schema.xsd.catalogue_21.CatalogueType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import rs.ruta.client.CatalogueSearch;
import rs.ruta.client.MyParty;
import rs.ruta.client.PartySearch;
import rs.ruta.client.Search;

public class SearchTreeModel extends RutaTreeModel
{
	private static final long serialVersionUID = -5486578776182215565L;
	private static final String CATALOGUES = Messages.getString("SearchTreeModel.0"); //$NON-NLS-1$
	private static final String PARTIES = Messages.getString("SearchTreeModel.1"); //$NON-NLS-1$
	private MyParty myParty;
	private List<Search<PartyType>> partySearches;
	private List<Search<CatalogueType>> catalogueSearches;

	public SearchTreeModel(TreeNode root)
	{
		super(root);
	}

	public SearchTreeModel(TreeNode root, MyParty myParty)
	{
		super(root);
		this.myParty = myParty;
		populateModel(myParty);
		populateTree();
		setAsksAllowsChildren(true);
		myParty.addActionListener(this, SearchEvent.class);
	}

	@Override
	protected void populateModel(MyParty myParty)
	{
		partySearches = myParty.getPartySearches();
		catalogueSearches = myParty.getCatalogueSearches();
	}

	@Override
	protected TreeNode populateTree()
	{
/*		partySearches = myParty.getPartySearches();
		catalogueSearches = myParty.getCatalogueSearches();*/


/*		// all business partners that are followed
		Set<BusinessParty> followingPartners = new TreeSet<BusinessParty>(partyNameComparator);
		// all other parties that are followed
		Set<BusinessParty> followingOthers = new TreeSet<BusinessParty>(partyNameComparator);
		//MMM: add a list of Searches and results

		followingPartners.addAll(myParty.getBusinessPartners());
		followingOthers.addAll(myParty.getFollowingParties());
//		followingPartners.retainAll(myParty.getFollowingParties()); // MMM: Why is this not working?
		followingPartners.retainAll(followingOthers);

		followingOthers.removeAll(myParty.getBusinessPartners());*/

		DefaultMutableTreeNode partySearchNode = new DefaultMutableTreeNode(PARTIES);
		((DefaultMutableTreeNode) root).add(partySearchNode);

		for(Search<PartyType> pSearch: partySearches)
		{
			DefaultMutableTreeNode pSearchNode = new DefaultMutableTreeNode(pSearch);
			pSearchNode.setAllowsChildren(false);
			partySearchNode.add(pSearchNode);
		}

		DefaultMutableTreeNode catalogueSearchNode = new DefaultMutableTreeNode(CATALOGUES);
		((DefaultMutableTreeNode) root).add(catalogueSearchNode);

		for(Search<CatalogueType> cSearch : catalogueSearches)
		{
			DefaultMutableTreeNode cSearchNode = new DefaultMutableTreeNode(cSearch);
			cSearchNode.setAllowsChildren(false);
			catalogueSearchNode.add(cSearchNode);
		}

		return root;
	}

	/**
	 * Adds node to the model right after the parent node i.e. at the index 0.
	 * @param userObject object contained in new node
	 */
	@Override
	public void addNode(Object userObject)
	{
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(userObject);
		node.setAllowsChildren(false);
		if(((Search<?>) userObject).getResultType() == CatalogueType.class)
			insertNodeInto(node, searchNode(CATALOGUES), 0);
		else if(((Search<?>) userObject).getResultType() == PartyType.class)
			insertNodeInto(node, searchNode(PARTIES), 0);
		nodeChanged(node); //necessary if display name is longer than the one's which place this node is taking up after insert
	}

	/**
	 * Deletes {@link DefaultMutableTreeNode node} and optionally user object contained in the node from the model.
	 * RutaUser object is to be deleted from within this method when the method call originates from the GUI.
	 * @param userObject object which wrapper node is to be deleted
	 * @param deleteFromDataModel true if user object should be deleted from the data model also
	 */
	@SuppressWarnings("unchecked")
	public void deleteNode(Object userObject, boolean deleteFromDataModel)
	{
		DefaultMutableTreeNode node = searchNode(userObject);

		if(deleteFromDataModel)
		{
			Class<?> searchClass = ((Search<?>) userObject).getResultType();
			if(searchClass == PartyType.class)
				partySearches.remove((Search<PartyType>) userObject);
			else if(searchClass == CatalogueType.class)
				catalogueSearches.remove((Search<CatalogueType>) userObject);
		}
		if(node != null)
			removeNodeFromParent(node);
/*		DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();

		if(parent == null)
			throw new IllegalArgumentException("Node does not have a parent.");

		int[] childIndex = new int[1];
//		Object[] removedArray = new Object[1];

		childIndex[0] = parent.getIndex(node);
		parent.remove(childIndex[0]);
		removedArray[0] = node;
		nodesWereRemoved(parent, childIndex, removedArray);

		fireTreeNodesRemoved(this, getPathToRoot(node), childIndex, new Object[] {node});*/
	}

	/**
	 * Rewiring the nodes in the tree model in a way that the node in question is positioned right after
	 * the parent node.
	 * @param userObject object contained in changed node
	 */
	@Override
	protected void updateNode(Object userObject)
	{
		DefaultMutableTreeNode node = searchNode(userObject);
		DefaultMutableTreeNode parentNode = null;
		if(node != null)
		{
			parentNode = (DefaultMutableTreeNode) node.getParent();
			if(parentNode != null)
			{
				removeNodeFromParent(node);
				insertNodeInto(node, parentNode, 0);
				nodeChanged(node); //necessary if display name is longer than the previous one
			}
		}
	}

	/**
	 * Checks whether the object is the last in one of the lists of the model.
	 * @param search object to check
	 * @return true if object is last in one of the lists, false otherwise
	 */
	public boolean isLastObject(Search<?> search)
	{
		boolean last = false;
		int partyIndex = partySearches.indexOf(search);
		int catalogueIndex = catalogueSearches.indexOf(search);
		if(partyIndex == -1 && catalogueIndex == -1)
			throw new IllegalArgumentException(Messages.getString("SearchTreeModel.2")); //$NON-NLS-1$
		if(partyIndex == partySearches.size() - 1)
			last = true;
		else if (catalogueIndex == catalogueSearches.size() - 1)
			last = true;
		return last;
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		final Object source = event.getSource();
		final String command = event.getActionCommand();

		if(source.getClass() == PartySearch.class)
		{
			final PartySearch sourceSearch = (PartySearch) source;

			if(SearchEvent.PARTY_SEARCH_ADDED.equals(command))
			{
				addNode(sourceSearch);
			}
			else if(SearchEvent.PARTY_SEARCH_REMOVED.equals(command))
			{
				deleteNode(sourceSearch, false);
			}
			else if(SearchEvent.PARTY_SEARCH_UPDATED.equals(command))
			{
				updateNode(sourceSearch);
			}
		}
		else if(source.getClass() == CatalogueSearch.class)
		{
			final CatalogueSearch sourceSearch = (CatalogueSearch) source;

			if(SearchEvent.CATALOGUE_SEARCH_ADDED.equals(command))
			{
				addNode(sourceSearch);
			}
			else if(SearchEvent.CATALOGUE_SEARCH_REMOVED.equals(command))
			{
				deleteNode(sourceSearch, false);
			}
			else if(SearchEvent.CATALOGUE_SEARCH_UPDATED.equals(command))
			{
				updateNode(sourceSearch);
			}
		}
		else if(source.getClass() == ArrayList.class)
		{
			if(SearchEvent.ALL_PARTY_SEARCHES_REMOVED.equals(command))
			{
				deleteChildrenNodes(PARTIES);
			}
			else if(SearchEvent.ALL_CATALOGUE_SEARCHES_REMOVED.equals(command))
			{
				deleteChildrenNodes(CATALOGUES);
			}
		}
	}
}