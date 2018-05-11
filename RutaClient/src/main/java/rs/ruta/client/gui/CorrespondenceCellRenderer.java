package rs.ruta.client.gui;

import java.awt.Component;
import java.awt.Font;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import rs.ruta.client.correspondence.Correspondence;

public class CorrespondenceCellRenderer extends DefaultTreeCellRenderer
{
	private static final long serialVersionUID = -2029178175613224399L;

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus)
	{
		Font plainFont = null, boldFont = null;
		String nodeText = null;
		final Component component = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		final DefaultMutableTreeNode node = ((DefaultMutableTreeNode) value);
		final Object userObject = node.getUserObject();
		if(plainFont == null)
		{
			plainFont = component.getFont().deriveFont(Font.PLAIN);
			boldFont = plainFont.deriveFont(Font.BOLD);
		}
		if(userObject instanceof Correspondence)
		{
			final Correspondence correspondence = (Correspondence) node.getUserObject();
			nodeText = correspondence.getName();
			if(correspondence.isRecentlyUpdated())
			{
				if(!sel)
					component.setFont(boldFont);
				else
				{
					component.setFont(plainFont);
					correspondence.setRecentlyUpdated(false);
				}
			}
			else
				component.setFont(plainFont);
		}
		else
		{
			component.setFont(plainFont);
			nodeText = userObject.toString();
		}
		return component;
	}
}