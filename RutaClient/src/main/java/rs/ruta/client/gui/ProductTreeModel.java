package rs.ruta.client.gui;

import java.awt.event.ActionEvent;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import rs.ruta.client.Item;
import rs.ruta.client.MyParty;

public class ProductTreeModel extends RutaTreeModel
{
	private static final long serialVersionUID = -2524672299973742238L;
	private static final String ACTIVE = Messages.getString("ProductTreeModel.0"); //$NON-NLS-1$
	private static final String ARCHIVED = Messages.getString("ProductTreeModel.1"); //$NON-NLS-1$

	public ProductTreeModel(TreeNode root, MyParty myParty)
	{
		super(root);
		populateModel(myParty);
		populateTree();
		setAsksAllowsChildren(false);
		myParty.addActionListener(this, ItemEvent.class);
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		final Object source = event.getSource();
		final String command = event.getActionCommand();

		if(source.getClass() == Item.class)
		{

		}

	}

	@Override
	protected TreeNode populateTree()
	{
		final DefaultMutableTreeNode active = new DefaultMutableTreeNode(ACTIVE);
		((DefaultMutableTreeNode) root).add(active);
		final DefaultMutableTreeNode archived = new DefaultMutableTreeNode(ARCHIVED);
		((DefaultMutableTreeNode) root).add(archived);

		return root;
	}

	@Override
	protected void populateModel(MyParty myParty)
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected void addNode(Object userObject)
	{
		// TODO Auto-generated method stub

	}

}
