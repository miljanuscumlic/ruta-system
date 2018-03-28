package rs.ruta.client.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import rs.ruta.client.BusinessParty;

public class PartyTreeCellRenderer extends DefaultTreeCellRenderer
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
		if(userObject instanceof BusinessParty)
		{
			final BusinessParty party = (BusinessParty) node.getUserObject();
			nodeText = party.getPartySimpleName();
			if(party.isRecentlyUpdated())
			{
				if(!sel)
					component.setFont(boldFont);
				else
				{
					component.setFont(plainFont);
					party.setRecentlyUpdated(false);
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
/*		//does not do correct resizing of the nodes
		Graphics g = component.getGraphics();
		if(g != null)
		{
			final FontMetrics fontMetrics = g.getFontMetrics(component.getFont());
			final int nodeWidth = fontMetrics.stringWidth(nodeText);
			final Dimension d = component.getSize();
			component.setSize(new Dimension(Math.max(nodeWidth, (int) d.width), d.height));

			Dimension setted = component.getSize();

			setted.getWidth();

		}*/



		return component;
	}

/*	@Override
	public Dimension getPreferredSize()
	{
		try
		{
			Dimension d = getSize();
//			Toolkit.getDefaultToolkit().getFontMetrics(getFont());
//			getFont().getLineMetrics(str, frc)

			int width = g.getFontMetrics().stringWidth(text);

			// get Rectangle for position after last text-character
			final Rectangle rectangle = this.modelToView(getDocument().getLength());
			if(rectangle != null)
				return new Dimension(this.getWidth(), this.getInsets().top + rectangle.y +
						rectangle.height + this.getInsets().bottom);
		}
		catch (BadLocationException e)
		{
			e.printStackTrace();  // TODO: implement catch
		}

		return super.getPreferredSize();
	}*/
}