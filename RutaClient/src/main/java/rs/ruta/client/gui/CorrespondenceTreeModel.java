package rs.ruta.client.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Nullable;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import rs.ruta.client.BusinessParty;
import rs.ruta.client.MyParty;
import rs.ruta.client.Party;
import rs.ruta.client.correspondence.BuyingCorrespondence;
import rs.ruta.client.correspondence.CatalogueCorrespondence;
import rs.ruta.client.correspondence.Correspondence;

/**
 * Tree model that is an adapter model for the parties of {@link MyParty} object.
 */
public class CorrespondenceTreeModel extends RutaTreeModel
{
	private static final long serialVersionUID = -841281945123956954L;
	private static final String ARCHIVED_PARTNERS = Messages.getString("CorrespondenceTreeModel.0"); 
	private static final String BUSINESS_PARTNERS = Messages.getString("CorrespondenceTreeModel.1"); 
	private static final String CDR = Messages.getString("CorrespondenceTreeModel.2"); 
	private static final String MY_PARTY = Messages.getString("CorrespondenceTreeModel.3"); 
	private MyParty myParty;
	//using sets for keeping sorted collection of parties and because they are faster than lists
	//when it comes to the sorting; sets should be maintained and representing current view of the data model
	private Set<BusinessParty> businessPartners;
	private Set<BusinessParty> archivedPartners;
	private Map<BusinessParty, List<Correspondence>> correspondences;
	private BusinessParty cdrParty;

	public CorrespondenceTreeModel(TreeNode root, MyParty myParty, BusinessParty cdrParty)
	{
		super(root);
		this.myParty = myParty;
		this.cdrParty = cdrParty;
		populateModel(myParty);
		populateTree();
		setAsksAllowsChildren(true);
		myParty.addActionListener(this, BusinessPartyEvent.class);
		myParty.addActionListener(this, CorrespondenceEvent.class);
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
		businessPartners.addAll(myParty.getBusinessPartners());
		correspondences = new HashMap<>();
		for(BusinessParty bParty: businessPartners)
			correspondences.put(bParty, myParty.findAllCorrespondences(bParty.getPartyID()));

		archivedPartners = new TreeSet<BusinessParty>(partyNameComparator);
//		List<BusinessParty> allArchivedParties = myParty.getArchivedParties();
//		for(BusinessParty archived: allArchivedParties)
//		{
//			final List<Correspondence> allCorrs = myParty.findAllCorrespondences(archived.getPartyID());
//			if (allCorrs != null)
//				archivedPartners.add(archived);
//		}
		archivedPartners.addAll(myParty.getArchivedParties());
		for(BusinessParty aParty : archivedPartners)
			correspondences.put(aParty, myParty.findAllCorrespondences(aParty.getPartyID()));

	}

	/**
	 * Constructs {@link DefaultMutableTreeNode nodes} from objects of the model and populates the tree with them.
	 * @return {@link TreeNode root node}
	 */
	@Override
	protected TreeNode populateTree()
	{
		final DefaultMutableTreeNode cdrPartyNode = new DefaultMutableTreeNode(cdrParty);
		((DefaultMutableTreeNode) root).add(cdrPartyNode);
		final CatalogueCorrespondence catalogueCorrespondence = myParty.getCatalogueCorrespondence();
		if(catalogueCorrespondence != null)
		{
			final DefaultMutableTreeNode corrNode = new DefaultMutableTreeNode(catalogueCorrespondence);
			corrNode.setAllowsChildren(false);
			cdrPartyNode.add(corrNode);
		}

/*		final DefaultMutableTreeNode myPartyNode = new DefaultMutableTreeNode(MY_PARTY);
		((DefaultMutableTreeNode) root).add(myPartyNode);
		if(myParty.getMyFollowingParty() != null)
		{
			final DefaultMutableTreeNode cdrPartyNode = new DefaultMutableTreeNode(cdrParty);
			myPartyNode.add(cdrPartyNode);
//			addNode(myParty.getCatalogueCorrespondence(), cdrParty);
			final CatalogueCorrespondence catalogueCorrespondence = myParty.getCatalogueCorrespondence();
			if(catalogueCorrespondence != null)
			{
				final DefaultMutableTreeNode corrNode = new DefaultMutableTreeNode(catalogueCorrespondence);
				corrNode.setAllowsChildren(false);
				cdrPartyNode.add(corrNode);
			}
		}*/

		final DefaultMutableTreeNode businessPartnersNode = new DefaultMutableTreeNode(BUSINESS_PARTNERS);
		((DefaultMutableTreeNode) root).add(businessPartnersNode);
		for(BusinessParty bParty: businessPartners)
		{
			/*			DefaultMutableTreeNode partnerNode = new DefaultMutableTreeNode(fParty);
			partnerNode.setAllowsChildren(true);
			businessPartnersNode.add(partnerNode);*/
			addNode(bParty);
		}

  		final DefaultMutableTreeNode archivedNode = new DefaultMutableTreeNode(ARCHIVED_PARTNERS);
		((DefaultMutableTreeNode) root).add(archivedNode);
		for(BusinessParty aParty : archivedPartners)
			addNode(aParty);

		return root;
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		final Object source = event.getSource();
		final String command = event.getActionCommand();

		if(source.getClass() == BusinessParty.class)
		{
			final BusinessParty sourceParty = (BusinessParty) source;
			if(BusinessPartyEvent.BUSINESS_PARTNER_ADDED.equals(command))
			{
				businessPartners.add(sourceParty);
				addNode(sourceParty);
			}
			else if(BusinessPartyEvent.BUSINESS_PARTNER_TRANSFERED.equals(command)) //MMM check is this TRANSEFER obsolete
			{
				businessPartners.add(sourceParty);
				addNode(sourceParty);
			}
			else if(BusinessPartyEvent.BUSINESS_PARTNER_REMOVED.equals(command))
			{
				businessPartners.remove(sourceParty);
				//adding to archived parties here instead of ARCHIVED_PARTY_ADDED event is received
				//because every business partner goes to archived, and to archived go unfollowed parties
				//and business partners, and we don't need unfollowed parties in this archived list
//				archivedPartners.add(sourceParty);
				deleteNode(sourceParty);
			}
			else if(BusinessPartyEvent.ARCHIVED_PARTY_ADDED.equals(command))
			{
				if(!archivedPartners.contains(sourceParty))
				{
					archivedPartners.add(sourceParty);
					addNode(sourceParty);
				}
			}
			else if(BusinessPartyEvent.ARCHIVED_PARTY_TRANSFERED.equals(command))
			{
				archivedPartners.remove(sourceParty);
				deleteNode(sourceParty);
			}
			else if(BusinessPartyEvent.ARCHIVED_PARTY_REMOVED.equals(command))
			{
				if(myParty.findAllCorrespondences(sourceParty.getPartyID()) == null)
				{
					archivedPartners.remove(sourceParty);
					deleteNode(sourceParty);
				}
			}
			else if(BusinessPartyEvent.PARTY_UPDATED.equals(command))
			{
				//update node text
				updateNode(sourceParty);
			}
		}
		else if(source.getClass() == BuyingCorrespondence.class)
		{
			final Correspondence corr = (Correspondence) source;
			final BusinessParty bParty = myParty.getBusinessPartner(corr.getCorrespondentID());
			if(CorrespondenceEvent.CORRESPONDENCE_ADDED.equals(command))
			{
				addNode(corr, bParty);
			}
			else if(CorrespondenceEvent.CORRESPONDENCE_REMOVED.equals(command))
			{
				deleteNode(corr);
				correspondences.get(bParty).remove(corr);
			}
		}
		else if(source.getClass() == CatalogueCorrespondence.class)
		{
			final Correspondence corr = (Correspondence) source;
			if(CorrespondenceEvent.CORRESPONDENCE_ADDED.equals(command))
			{
				addNode(corr, cdrParty);
			}
		}
		else if(source.getClass() == ArrayList.class)
		{
			if(BusinessPartyEvent.ALL_PARTIES_REMOVED.equals(command))
			{
				businessPartners.clear();
				deleteChildrenNodes(BUSINESS_PARTNERS);
				archivedPartners.clear();
				deleteChildrenNodes(ARCHIVED_PARTNERS);
			}
			else if(CorrespondenceEvent.ALL_CORRESPONDENCES_REMOVED.equals(command))
			{
				deleteChildrenNodes(BUSINESS_PARTNERS);
				deleteChildrenNodes(ARCHIVED_PARTNERS);
				deleteChildrenNodes(cdrParty);
				correspondences.clear();
			}
		}
	}

	@Override
	protected void addNode(Object userObject)
	{
		final BusinessParty party = (BusinessParty) userObject;
		final DefaultMutableTreeNode partyNode = new DefaultMutableTreeNode(party);
		partyNode.setAllowsChildren(true);
		if(party.isPartner())
			insertNodeInto(partyNode, searchNode(BUSINESS_PARTNERS), getIndex(party, businessPartners));
		else if(party.isArchived())
			insertNodeInto(partyNode, searchNode(ARCHIVED_PARTNERS), getIndex(party, archivedPartners));

		final List<Correspondence> partyCorrespondences = myParty.findAllCorrespondences(party.getPartyID());
		if(partyCorrespondences != null)
		{
			correspondences.put(party, partyCorrespondences);
			for(Correspondence corr: partyCorrespondences)
			{
				final DefaultMutableTreeNode corrNode = new DefaultMutableTreeNode(corr);
				corrNode.setAllowsChildren(false);
				insertNodeInto(corrNode, partyNode, getIndex(corr, partyCorrespondences));
			}
		}
	}

	/**
	 * Adds node as a child to a parent node.
	 * @param userObject object contained in node to add
	 * @param parentObject object contained in parent node
	 */
	private void addNode(Object userObject, Object parentObject)
	{
		final BusinessParty party = (BusinessParty) parentObject;
		final Correspondence corr = (Correspondence) userObject;
		List<Correspondence> corrs = correspondences.get(party);
		if(corrs == null)
		{
			corrs = new ArrayList<Correspondence>();
			correspondences.put(party, corrs);
		}
		corrs.add(corr);
		final DefaultMutableTreeNode corrNode = new DefaultMutableTreeNode(corr);
		corrNode.setAllowsChildren(false);
		insertNodeInto(corrNode, searchNode(parentObject), getIndex(corr, corrs));
	}

}