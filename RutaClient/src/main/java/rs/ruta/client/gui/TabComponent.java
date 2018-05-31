
package rs.ruta.client.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.concurrent.Semaphore;

import javax.swing.DefaultRowSorter;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.slf4j.Logger;

/**
 * Abstract class {@code TabComponent} represents abstraction of one tab in the view of the {@link RutaClientFrame main frame}
 * of the {@code Ruta Client application}. Every tab is an instance of a {@code TabComponent}'s subclass. {@code TabComponent}
 * contains common methods used in its subclasses.
 */
public abstract class TabComponent extends Container
{
	private static final long serialVersionUID = 4341543994574335442L;
	protected RutaClientFrame clientFrame;
	protected Logger logger;
	protected JComponent leftPane;
	protected JComponent rightPane;
	protected JScrollPane rightScrollPane;

	public TabComponent(RutaClientFrame clientFrame)
	{
		this.clientFrame = clientFrame;
		this.logger = RutaClientFrame.getLogger();
	}

	/**
	 * Repaints containing component.
	 */
	@Override
	public void repaint()
	{
		revalidate();
		super.repaint();
	}

	/**
	 * Creates orderLinesTable containing catalogue data of a party.
	 * @param tableModel model containing catalogue data
	 * @return constructed orderLinesTable object
	 */
	@SuppressWarnings("unchecked")
	protected JTable createCatalogueTable(DefaultTableModel tableModel)
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
		((DefaultRowSorter<DefaultTableModel, Integer>) table.getRowSorter()).setSortable(0, false);

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
	 * Creates and formats the view of empty orderLinesTable that will contain data from the list of parties.
	 * @param tableModel model representing the list of parties to be displayed
	 * @return created orderLinesTable
	 */
	protected JTable newEmptyPartyListTable(DefaultTableModel tableModel)
	{
		JTable table = new JTable(tableModel);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setFillsViewportHeight(true);
		TableColumnModel colModel = table.getColumnModel();
		colModel.getColumn(0).setPreferredWidth(20);
		colModel.getColumn(1).setPreferredWidth(150);
		colModel.getColumn(2).setPreferredWidth(200);
		colModel.getColumn(3).setPreferredWidth(100);
		colModel.getColumn(4).setPreferredWidth(100);
		colModel.getColumn(5).setPreferredWidth(100);
		colModel.getColumn(6).setPreferredWidth(100);
		colModel.getColumn(7).setPreferredWidth(100);
		colModel.getColumn(8).setPreferredWidth(100);
		colModel.getColumn(9).setPreferredWidth(100);
		colModel.getColumn(10).setPreferredWidth(100);
		colModel.getColumn(11).setPreferredWidth(100);
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
			JLabel comp = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			comp.setText(Integer.toString(row + 1));
			comp.setFont(getFont().deriveFont(Font.PLAIN));
			return comp;
		}
	}

	/**
	 * Gets the object contained in the selected node of the tree.
	 * @param tree tree to be searched
	 * @return object of the selected node or {@code null} if no node is selected
	 */
	protected Object getSelectedUserObject(JTree tree)
	{
		final TreePath path = tree.getSelectionPath();
		if(path == null) return null;
		final DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
		final Object selectedParty = selectedNode.getUserObject();
		return selectedParty;
	}

	/**
	 * Gets the object contained in the selected node of the tree.
	 * @param path {@link TreePath tree path} of the seleceted node
	 * @return object of the selected node or {@code null} if no node is selected
	 */
	protected Object getSelectedUserObject(TreePath path)
	{
		if(path == null) return null;
		final DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
		final Object selectedParty = selectedNode.getUserObject();
		return selectedParty;
	}

	/**
	 * Gets the selected {@link DefaultMutableTreeNode node} of the tree.
	 * @param tree tree to be searched
	 * @return selected node or {@code null} if no node is selected
	 */
	protected DefaultMutableTreeNode getSelectedNode(JTree tree)
	{
		final TreePath path = tree.getSelectionPath();
		if(path == null) return null;
		final DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
		return selectedNode;
	}

	/**
	 * Makes visible tree node containing the object.
	 * @param tree tree which node should be made visible
	 * @param object object contained in the tree node
	 */
	protected void makeVisibleNode(final JTree tree, final Object object)
	{
		final DefaultMutableTreeNode node = searchNode(tree, object);
		if(node != null)
		{
			final TreeNode[] nodePath = node.getPath();
			final TreePath treePath = new TreePath(nodePath);
			tree.scrollPathToVisible(treePath);
			tree.makeVisible(treePath);
		}
	}

	/**
	 * Selects tree node containing the object.
	 * @param tree tree which node should be selected
	 * @param object object contained in the tree node
	 */
	protected void selectNode(final JTree tree, final Object object)
	{
		final DefaultMutableTreeNode nodeToSelect = searchNode(tree, object);
		if(nodeToSelect != null)
		{
//			final TreeNode[] nodes = ((DefaultTreeModel) tree.getModel()).getPathToRoot(nodeToSelect);
			final TreeNode[] nodePath = nodeToSelect.getPath();
			final TreePath treePath = new TreePath(nodePath);
			tree.scrollPathToVisible(treePath);
//			tree.makeVisible(treePath);
			tree.setSelectionPath(treePath);
		}
	}

	/**
	 * Finds next node of the tree eligable for the selection. If node containing the object could not be found
	 * in the tree, root node is get. If node exists in the tree method tries to get the next sibling node.
	 * If next sibling node does not exist, previous one is selected. If previous node does not exist, parent node
	 * is get if it exists.
	 * @param tree tree which node should be selected
	 * @param object object contained in the tree node
	 * @return next node to be selected
	 */
	protected DefaultMutableTreeNode getNextNodeForSelection(final JTree tree, final Object object)
	{
		final DefaultMutableTreeNode node = searchNode(tree, object);
		DefaultMutableTreeNode nodeToSelect = null;
		if(node == null)
			nodeToSelect = (DefaultMutableTreeNode) tree.getModel().getRoot();
		else
		{
			nodeToSelect = node.getNextSibling();
			if(nodeToSelect == null)
				nodeToSelect = node.getPreviousSibling();
			if(nodeToSelect == null)
				nodeToSelect = (DefaultMutableTreeNode) node.getParent();
		}
		return nodeToSelect;
	}

	/**
	 * Selects node in a tree.
	 * @param tree tree which not should be selected
	 * @param nodeToSelect node to select
	 */
	protected void selectNode(final JTree tree, final DefaultMutableTreeNode nodeToSelect)
	{
		if(nodeToSelect != null)
		{
			final TreeNode[] nodePath = ((DefaultTreeModel) tree.getModel()).getPathToRoot(nodeToSelect);
			final TreePath treePath = new TreePath(nodePath);
			tree.scrollPathToVisible(treePath);
			tree.setSelectionPath(treePath);
		}
	}

	/**
	 * Selects next eligable node in a tree. This method is usually called after node deletion when there is a need
	 * to select next eligable node in a tree.
	 * @param tree tree which node should be selected
	 * @param object object contained in the tree node
	 */
	protected void selectNextNode(final JTree tree, final Object object)
	{
		selectNode(tree, getNextNodeForSelection(tree, object));
	}

	/**
	 * Selects next sibling tree node of the node containing the object. If next node does not exist,
	 * previous node is selected; if previous node does not exist parent node is selected if exists.
	 * @param tree tree which node should be selected
	 * @param object object contained in the tree node
	 */
	@Deprecated
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
	 * @param object object contained in the tree node which parent is to be selected
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
	 * @return {@link DefaultMutableTreeNode node} containing searched object or {@code null} if
	 * the object is not present in the tree
	 */
	protected DefaultMutableTreeNode searchNode(JTree tree, Object object)
	{
		DefaultMutableTreeNode node = null;
		boolean success = false;
		@SuppressWarnings("unchecked")
		final Enumeration<DefaultMutableTreeNode> enumeration =
		((DefaultMutableTreeNode) tree.getModel().getRoot()).breadthFirstEnumeration();
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
	 * Delegates {@link ActionEvent event} to a particular subclass of {@link TabComponent}. Events that are
	 * dispatched are ones that update the view, like selecting tree node, updating orderLinesTable view etc.
	 * <p>This method blocks until the event is completely processed.</p>
	 * @param event event to dispatch
	 */
	public void dispatchEvent(ActionEvent event)
	{
		Semaphore waitEDT = new Semaphore(0);
		EventQueue.invokeLater(() ->
		{
			doDispatchEvent(event);
			waitEDT.release();
		});
		try
		{
			waitEDT.acquire();
		}
		catch(InterruptedException e)
		{
			logger.info("View update has failed. Could not synchronise with the EventQueue. Exception is: ", e);
		}
	}

	/**
	 * Dispathes {@link ActionEvent event} to a proper {@link ActionEvent event} and do the view update based
	 * on the passed event.
	 * @param event event to dispatch and process
	 */
	protected abstract void doDispatchEvent(ActionEvent event);

	/**
	 * Creates {@link TableRowSorter}, sets zeroth column with row numbers not to be sortable and sorts
	 * the orderLinesTable by the first column.
	 * @param tableModel
	 * @return sorter created {@code TableSorter}
	 */
	protected TableRowSorter<DefaultTableModel> createTableRowSorter(DefaultTableModel tableModel)
	{
		return createTableRowSorter(tableModel, 1, false);
	}

	/**
	 * Creates {@link TableRowSorter}, sets zeroth column with row numbers not to be sortable and sorts
	 * the orderLinesTable by the column with paseed {@code sortIndex}.
	 * @param tableModel data model of the orderLinesTable
	 * @param sortIndex index of the column by which the orderLinesTable should be sorted by default
	 * @param descending true when sorting orderLines should be descending
	 * @return sorter created {@code TableSorter}
	 */
	protected TableRowSorter<DefaultTableModel> createTableRowSorter(DefaultTableModel tableModel, int sortIndex, boolean descending)
	{
		TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<DefaultTableModel>(tableModel);
		sorter.setSortable(0, false);
		sorter.toggleSortOrder(sortIndex);
		if(descending)
			sorter.toggleSortOrder(sortIndex);
/*		sorter.addRowSorterListener(new RowSorterListener()
		{
			@Override
			public void sorterChanged(RowSorterEvent evt)
			{
				//doesn't work in searchTree???!!!
				int columnIndex = 0;
				for (int i = 0; i < orderLinesTable.getRowCount(); i++)
					orderLinesTable.setValueAt(i + 1, i, columnIndex);
			}
		});*/
		return sorter;
	}

	/**
	 * Notifies {@link TableRowSorter} about model change.
	 * @param sorter
	 */
	protected void notifyRowSorter(TableRowSorter<DefaultTableModel> sorter, JTable table)
	{
//		sorter.setSortable(0, false);
		sorter.allRowsChanged();
	}

	/**
	 * Sets the left and right components of the {@link TabComponent}.
	 * @param left
	 * @param right
	 */
	protected void arrangeTab()
	{
		if(getComponentCount() == 0)
			setLayout(new BorderLayout());
		else
			removeAll();
		add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPane, rightPane));

		repaint();
	}

}