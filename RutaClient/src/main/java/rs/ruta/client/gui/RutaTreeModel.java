package rs.ruta.client.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Set;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import rs.ruta.client.MyParty;

public abstract class RutaTreeModel extends DefaultTreeModel  implements ActionListener
{
	private static final long serialVersionUID = -8711344527654849179L;

	public RutaTreeModel(TreeNode root)
	{
		super(root);
	}

	/**
	 * Constructs {@link DefaultMutableTreeNode nodes} from objects of the model and populates the tree with them.
	 * @return {@link TreeNode root node}
	 */
	protected abstract TreeNode populateTree();

	/**
	 * Populates data model.
	 * @param myParty
	 */
	protected abstract void populateModel(MyParty myParty);

	/**
	 * Searches for a node in the tree.
	 * @param tree tree to be searched
	 * @param userObject object which node is to be searched for
	 * @return {@link DefaultMutableTreeNode node} containing searched object or {@code null}
	 * if the object is not present in the tree
	 */
	protected DefaultMutableTreeNode searchNode(Object userObject)
	{
		DefaultMutableTreeNode node = null;
		boolean success = false;
		@SuppressWarnings("unchecked")
		final Enumeration<DefaultMutableTreeNode> enumeration = ((DefaultMutableTreeNode) getRoot()).breadthFirstEnumeration();
		while(!success && enumeration.hasMoreElements())
		{
			node = enumeration.nextElement();
			if(userObject.getClass() == String.class)
			{
				if(userObject.equals(node.getUserObject()))
					success = true;
			}
			else if(userObject == node.getUserObject())
				success = true;
		}
		if(success)
			return node;
		else
			return null;
	}

	/**
	 * Adds node to the model.
	 * @param userObject object contained in new node
	 */
	protected abstract void addNode(Object userObject);

	/**
	 * Deletes {@link DefaultMutableTreeNode node} from the tree model.
	 * @param userObject object which wrapper node is to be deleted
	 */
	protected void deleteNode(Object userObject)
	{
		DefaultMutableTreeNode node = searchNode(userObject);
		if(node != null)
			removeNodeFromParent(node);
	}

	/**
	 * Updates view of the {@link DefaultMutableTreeNode node}.
	 * @param userObject object which wrapper node is to be updated
	 */
	protected void updateNode(Object userObject)
	{
		DefaultMutableTreeNode node = searchNode(userObject);
		if(node != null)
			nodeChanged(node);
	}

	/**
	 * Deletes all children nodes of the parent.
	 * @param parentNodeName name of the parent node
	 */
//	protected void deleteChildrenNodes(String parentNodeName)
	protected void deleteChildrenNodes(Object parentNodeName)
	{
		DefaultMutableTreeNode node = searchNode(parentNodeName);
		node.removeAllChildren();
		nodeStructureChanged(node);
	}

	/**
	 * Gets the index of the node in the tree, relative to its parent.
	 * @param <T> type of the object in the set
	 * @param userObject contained object of the node
	 * @param collection collection of objects to be searched
	 * @return index of the node in the tree or -1 if node has not been found
	 */
	protected <T> int getIndex(T userObject, Collection<T> collection) //Set<T> collection
	{
		int index = -1;
		for(T element: collection)
		{
			index++;
			if(userObject.equals(element))
				break;
		}
		return index < collection.size() ? index : -1;
	}


}
