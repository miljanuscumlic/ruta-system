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
import rs.ruta.client.correspondence.Correspondence;
import rs.ruta.client.BusinessPartyEvent;
import rs.ruta.client.CorrespondenceEvent;

/**
 * Tree model that is an adapter model for the parties of {@link MyParty} object.
 */
public class CorrespondenceTreeModel extends RutaTreeModel
{
	private static final long serialVersionUID = -841281945123956954L;
	private static final String BUSINESS_PARTNERS = "Business Partners";
	private static final String MY_PARTY = "My Party";
	private static final String CDR = "CDR";
	private MyParty myParty;
	//using sets for keeping sorted collection of parties and because they are faster than lists
	//when it comes to the sorting; sets should be maintained and representing current view of the data model
	private Set<BusinessParty> businessPartners;
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

	}

	/**
	 * Constructs {@link DefaultMutableTreeNode nodes} from objects of the model and populates the tree with them.
	 * @return {@link TreeNode root node}
	 */
	@Override
	protected TreeNode populateTree()
	{
		DefaultMutableTreeNode myPartyNode = new DefaultMutableTreeNode(MY_PARTY);
		((DefaultMutableTreeNode) root).add(myPartyNode);
		if(myParty.getMyFollowingParty() != null)
		{
			DefaultMutableTreeNode myFollowingPartyNode = new DefaultMutableTreeNode(cdrParty);
			myFollowingPartyNode.setAllowsChildren(false);
			myPartyNode.add(myFollowingPartyNode);
		}

		DefaultMutableTreeNode businessPartnersNode = new DefaultMutableTreeNode(BUSINESS_PARTNERS);
		((DefaultMutableTreeNode) root).add(businessPartnersNode);
		for(BusinessParty bParty: businessPartners)
		{
			/*			DefaultMutableTreeNode partnerNode = new DefaultMutableTreeNode(fParty);
			partnerNode.setAllowsChildren(true);
			businessPartnersNode.add(partnerNode);*/
			addNode(bParty);
		}
		return root;
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
		else if(source.getClass() == BuyingCorrespondence.class)
		{
			final Correspondence corr = (Correspondence) source;

			if(CorrespondenceEvent.CORRESPONDENCE_ADDED.equals(command))
			{
				final BusinessParty bParty = myParty.getBusinessPartner(corr.getCorrespondentID());
				addNode(corr, bParty);
			}
		}
	}

	@Override
	protected void addNode(Object userObject)
	{
		final BusinessParty party = (BusinessParty) userObject;
		DefaultMutableTreeNode partyNode = new DefaultMutableTreeNode(party);
		partyNode.setAllowsChildren(true);
		insertNodeInto(partyNode, searchNode(BUSINESS_PARTNERS), getIndex(party, businessPartners));

		List<Correspondence> partyCorrespondences = myParty.findAllCorrespondences(party.getPartyID());
		if(partyCorrespondences != null)
		{
			correspondences.put(party, partyCorrespondences);
			for(Correspondence corr: partyCorrespondences)
			{
				DefaultMutableTreeNode corrNode = new DefaultMutableTreeNode(corr);
				corrNode.setAllowsChildren(false);
				insertNodeInto(corrNode, partyNode, getIndex(corr, partyCorrespondences));
			}
		}
	}

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