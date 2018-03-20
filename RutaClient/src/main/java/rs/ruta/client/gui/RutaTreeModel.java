package rs.ruta.client.gui;

import java.awt.event.ActionEvent;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

public class RutaTreeModel extends DefaultTreeModel
{
	private static final long serialVersionUID = -8711344527654849179L;

	public RutaTreeModel(TreeNode root)
	{
		super(root);
	}

	/**
	 * Checks whether the model listens to some particular {@link ActionEvent}.
	 * @param eventClazz class object of the {@code ActionEvent}
	 * @return true if model listens to event
	 */
	public boolean listenFor(Class<? extends ActionEvent> eventClazz)
	{
		return false;
	}


}
