package rs.ruta.client.gui;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Enumeration;

import javax.swing.DefaultRowSorter;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public abstract class TabComponent
{
	protected RutaClientFrame clientFrame;
	protected Component component;

	public TabComponent(RutaClientFrame clientFrame)
	{
		this.clientFrame = clientFrame;
	}

	public Component getComponent()
	{
		return component;
	}

	public void setComponent(Component component)
	{
		this.component = component;
	}

	/**
	 * Repaints containing component.
	 */
	public void repaint()
	{
		if(component != null)
		{
			component.revalidate();
			component.repaint();
		}
	}

	@SuppressWarnings("unchecked")
	protected JTable createCatalogueTable(AbstractTableModel tableModel)
	{
		JTable table = new JTable(tableModel)
		{
			private static final long serialVersionUID = -2879401192820075582L;
			//implementing column header's tooltips
			@Override
			protected JTableHeader createDefaultTableHeader()
			{
				return new JTableHeader(columnModel)
				{
					private static final long serialVersionUID = -2681152311259025964L;
					@Override
					public String getToolTipText(MouseEvent e)
					{
						java.awt.Point p = e.getPoint();
						int index = columnModel.getColumnIndexAtX(p.x);
						int realIndex = columnModel.getColumn(index).getModelIndex();
						switch(realIndex)
						{
						case 3:
							return "Integer numbers; 0 for field deletion";
						case 4:
							return "ID field is mandatory and must be unique";
						case 9:
							return "Comma separated values";
						default:
							return null;
						}
					}
				};
			}
		};
		table.setFillsViewportHeight(true);
		table.setAutoCreateRowSorter(true);

		table.getRowSorter();
		table.getRowSorter().getModelRowCount();
		((DefaultRowSorter<AbstractTableModel, Integer>) table.getRowSorter()).setSortable(0, false);

		table.getColumnModel().getColumn(0).setCellRenderer(new RowNumberRenderer());

		TableColumnModel colModel = table.getColumnModel();
		colModel.getColumn(0).setPreferredWidth(20);
		colModel.getColumn(1).setPreferredWidth(200);
		colModel.getColumn(2).setPreferredWidth(200);
		colModel.getColumn(3).setPreferredWidth(20);
		colModel.getColumn(9).setPreferredWidth(200);

		final AWTEventListener focusTracker = new AWTEventListener()
		{
			@Override
			public void eventDispatched(AWTEvent event)
			{
				if(event.getID() != MouseEvent.MOUSE_CLICKED && event.getID() != KeyEvent.KEY_PRESSED)
					return;
				if(!isPartOfTable((Component) event.getSource()))
					if(table.isEditing())
					{
						final TableCellEditor cellEditor = table.getCellEditor();
						if(cellEditor != null && !cellEditor.stopCellEditing())
							cellEditor.cancelCellEditing();
						table.dispatchEvent(new FocusEvent(table, FocusEvent.FOCUS_LOST));
					}
			}

			protected boolean isPartOfTable(Component component)
			{
				while(component != null && component != table)
					component = component.getParent();
				return component == table;
			}

		};
		Toolkit.getDefaultToolkit().addAWTEventListener(focusTracker, AWTEvent.KEY_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK);

		return table;
	}

	/**
	 * Renderer class that enables a column with the row numbers to appear as not to be sortable.
	 */
	public class RowNumberRenderer extends DefaultTableCellRenderer
	{
		private static final long serialVersionUID = -5116120417827277443L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int column)
		{
			JLabel component = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			component.setText(Integer.toString(row + 1));
			component.setFont(getFont().deriveFont(Font.PLAIN));
			return component;
		}
	}

	/**
	 * Selects the tree node containing the object.
	 * @param tree tree which node should be selected
	 * @param object object contained in the tree node
	 */
	protected void selectNode(final JTree tree, final Object object)
	{
		final DefaultMutableTreeNode nodeToSelect = searchNode(tree, object);
		if(nodeToSelect != null)
		{
			final TreeNode[] nodes = ((DefaultTreeModel) tree.getModel()).getPathToRoot(nodeToSelect);
			final TreePath treePath = new TreePath(nodes);
			tree.scrollPathToVisible(treePath);
			tree.setSelectionPath(treePath);
		}
	}

	/**
	 * Selects previous sibling tree node of the node containing the object. If previous node does
	 * not exist, root node is selected if exist.
	 * @param tree tree which node should be selected
	 * @param object object contained in the tree node
	 */
	protected void selectPreviousSiblingNode(final JTree tree, final Object object)
	{
		final DefaultMutableTreeNode node = searchNode(tree, object);
		DefaultMutableTreeNode nodeToSelect = node.getPreviousSibling();
		if(nodeToSelect == null)
			nodeToSelect = (DefaultMutableTreeNode) node.getParent();
		if(nodeToSelect != null)
		{
			final TreeNode[] nodes = ((DefaultTreeModel) tree.getModel()).getPathToRoot(nodeToSelect);
			final TreePath treePath = new TreePath(nodes);
			tree.scrollPathToVisible(treePath);
			tree.setSelectionPath(treePath);
		}
	}

	/**
	 * Selects next sibling tree node of the node containing the object. If next node does not exist,
	 * previous node is selected; if previous node does not exist root node is selected if exist.
	 * @param tree tree which node should be selected
	 * @param object object contained in the tree node
	 */
	protected void selectNextSiblingNode(final JTree tree, final Object object)
	{
		final DefaultMutableTreeNode node = searchNode(tree, object);
		DefaultMutableTreeNode nodeToSelect = node.getNextSibling();
		if(nodeToSelect == null)
			nodeToSelect = (DefaultMutableTreeNode) node.getPreviousSibling();
		if(nodeToSelect == null)
			nodeToSelect = (DefaultMutableTreeNode) node.getParent();
		if(nodeToSelect != null)
		{
			final TreeNode[] nodes = ((DefaultTreeModel) tree.getModel()).getPathToRoot(nodeToSelect);
			final TreePath treePath = new TreePath(nodes);
			tree.scrollPathToVisible(treePath);
			tree.setSelectionPath(treePath);
		}
	}

	/**
	 * Selects parent node of the tree node containing the object.
	 * @param tree tree which node should be selected
	 * @param object object contained in the tree node
	 */
	protected void selectParentNode(final JTree tree, final Object object)
	{
		DefaultMutableTreeNode nodeToSelect = (DefaultMutableTreeNode) searchNode(tree, object).getParent();
		if(nodeToSelect == null)
			nodeToSelect = (DefaultMutableTreeNode) tree.getModel().getRoot();
		else
		{
			final TreeNode[] nodes = ((DefaultTreeModel) tree.getModel()).getPathToRoot(nodeToSelect);
			final TreePath treePath = new TreePath(nodes);
			tree.scrollPathToVisible(treePath);
			tree.setSelectionPath(treePath);
		}
	}

	/**
	 * Searches for an object in the tree.
	 * @param tree tree to be searched
	 * @param object object to be searched for
	 * @return {@link DefaultMutableTreeNode node} containing searched object or {@code null} if there the object is not present in the tree
	 */
	protected DefaultMutableTreeNode searchNode(JTree tree, Object object)
	{
		DefaultMutableTreeNode node = null;
		boolean success = false;
		@SuppressWarnings("unchecked")
		final Enumeration<DefaultMutableTreeNode> enumeration = ((DefaultMutableTreeNode) tree.getModel().getRoot()).breadthFirstEnumeration();
		while(!success && enumeration.hasMoreElements())
		{
			node = enumeration.nextElement();
			if(object.getClass() == String.class)
			{
				if(object.equals(node.getUserObject()))
					success = true;
			}
			else if(object == node.getUserObject())
				success = true;
		}
		if(success)
			return node;
		else
			return null;
	}

	/**
	 *Repaints the selected tab.
	 */
	//MMM: should be implemented to update view of trees with no path collapsing as it is now
/*	public void repaintTabbedPane()
	{
		int selectedTab = tabbedPane.getSelectedIndex();
		loadTab(selectedTab);

				if(selectedTab == 0)
		{
			leftPane.revalidate();
			leftPane.repaint();
			rightPane.revalidate();
			rightPane.repaint();

			arrangeTab0(leftPane, rightPane);
		}
		else
			loadTab(selectedTab);


				Component treeContainer = ((JComponent) ((JComponent)((JComponent) tabPane.getComponent(0)).getComponent(0)).getComponent(0)).getComponent(0);
		JTree partyTree = (JTree) ((JComponent) treeContainer).getComponent(0);
		JTree searchTree = (JTree) ((JComponent) treeContainer).getComponent(1);
		((DefaultTreeModel) partyTree.getModel()).reload();
		((DefaultTreeModel) searchTree.getModel()).reload();
	}*/
}
