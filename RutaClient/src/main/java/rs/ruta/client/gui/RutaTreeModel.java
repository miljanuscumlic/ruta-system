package rs.ruta.client.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

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
	 * @param command event command that is resulting in node addition to the model
	 */
	protected abstract void addNode(Object userObject, String command);

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
	 * Deletes all childer nodes from the parent.
	 * @param parentNodeName name of the parent node
	 */
	protected void deleteChildrenNodes(String parentNodeName)
	{
		DefaultMutableTreeNode node = searchNode(parentNodeName);
		node.removeAllChildren();
		nodeStructureChanged(node);
	}

	/**
	 * Checks whether the model listens for some particular {@link ActionEvent}.
	 * @param eventClazz class object of the {@code ActionEvent}
	 * @return true if model listens for event
	 */
	@Deprecated
	public boolean listensFor(Class<? extends ActionEvent> eventClazz)
	{
		return false;
	}


}
