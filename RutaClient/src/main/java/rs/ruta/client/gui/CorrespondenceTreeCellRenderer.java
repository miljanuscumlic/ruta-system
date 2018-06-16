package rs.ruta.client.gui;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import rs.ruta.client.BusinessParty;
import rs.ruta.client.correspondence.Correspondence;

public class CorrespondenceTreeCellRenderer extends DefaultTreeCellRenderer
{
	private static final long serialVersionUID = -2029178175613224399L;

	private Icon outOfSyncIcon;
	private Icon defaultLeafIcon;

	public CorrespondenceTreeCellRenderer()
	{
		defaultLeafIcon = null;
		final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		final InputStream iconStream = classLoader.getResourceAsStream("outofsyncicon.gif");
		try
		{
			outOfSyncIcon =  new ImageIcon(ImageIO.read(iconStream));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

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
//		if(userObject != null)
		{
			if(plainFont == null)
			{
				plainFont = component.getFont().deriveFont(Font.PLAIN);
				boldFont = plainFont.deriveFont(Font.BOLD);
			}
			if(userObject instanceof Correspondence)
			{
				if(defaultLeafIcon == null)
				{
					defaultLeafIcon = ((JLabel) component).getIcon();
				}

				final Correspondence correspondence = (Correspondence) node.getUserObject();
				nodeText = correspondence.getName();
				if(correspondence.isRecentlyUpdated())
				{
					if(!sel)
					{
						component.setFont(boldFont);
					}
					else
					{
						component.setFont(plainFont);
						correspondence.setRecentlyUpdated(false);
					}
				}
				else
				{
					component.setFont(plainFont);
				}
				if(correspondence.isOutOfSync())
				{
					((JLabel) component).setIcon(outOfSyncIcon);
				}
				else
				{
					((JLabel) component).setIcon(defaultLeafIcon);
				}
			}
/*			else if(userObject instanceof BusinessParty)
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
			}*/
			else
			{
				component.setFont(plainFont);
				nodeText = userObject.toString();
			}
		}
		return component;
	}
}