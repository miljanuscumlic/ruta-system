package rs.ruta.client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rs.ruta.client.Item;
import rs.ruta.client.MyParty;
import rs.ruta.common.InstanceFactory;
import rs.ruta.common.datamapper.DetailException;

public class TabProducts extends TabComponent
{
	private static final long serialVersionUID = 7435742718848842547L;
	protected static Logger logger = LoggerFactory.getLogger("rs.ruta.client"); //$NON-NLS-1$
	private static final String IN_STOCK = Messages.getString("TabProducts.1"); //$NON-NLS-1$
	private static final String OUT_OF_STOCK = Messages.getString("TabProducts.2"); //$NON-NLS-1$
	private static final Object PRODUCTS_AND_SERVICES = Messages.getString("TabProducts.3"); //$NON-NLS-1$
	private JTable productListTable;
	private DefaultTableModel productListTableModel;
	private List<Item> products;
	private List<Item> archivedProducts;
	private JTree productTree;
	private MyParty myParty;

	public TabProducts(final RutaClientFrame clientFrame)
	{
		super(clientFrame);
		rightScrollPane = new JScrollPane();
		myParty = clientFrame.getClient().getMyParty();
		products = myParty.getProducts();
		archivedProducts = myParty.getArchivedProducts();
		productListTableModel = new ActiveProductListTableModel(myParty, true);
		productListTableModel = new ProductListTableModel(products, myParty, true);
		productListTable = createProductListTable(productListTableModel);

		final DefaultTreeModel productTreeModel = new ProductTreeModel(new DefaultMutableTreeNode(PRODUCTS_AND_SERVICES), myParty);
		productTree = createProductTree(productTreeModel);
		selectNode(productTree, PRODUCTS_AND_SERVICES);

		final JPanel productPanel = new JPanel(new BorderLayout());
		productPanel.add(productTree, BorderLayout.CENTER);

		leftPane = new JScrollPane(productPanel);
		leftPane.setPreferredSize(new Dimension(325, 500));

		rightPane = new JPanel(new BorderLayout());
		rightPane.add(rightScrollPane);

		arrangeTab();
	}

	@SuppressWarnings("unchecked")
	private void setUpComboBoxColumn(final JTable table, final TableColumn tableColumn)
	{
		final JComboBox<String> comboBox = new JComboBox<String>(InstanceFactory.getTaxCategories());
		comboBox.setRenderer(new ComBoxCellRenderer());
		comboBox.setFont(new JLabel("Test").getFont().deriveFont(Font.PLAIN)); //$NON-NLS-1$
		tableColumn.setCellEditor(new DefaultCellEditor(comboBox));
		final DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setToolTipText(Messages.getString("TabProducts.5")); //$NON-NLS-1$
		tableColumn.setCellRenderer(renderer);
	}

	private class ComBoxCellRenderer extends BasicComboBoxRenderer
	{
		private static final long serialVersionUID = -1039593028193581377L;

		@Override
		public Component getListCellRendererComponent(@SuppressWarnings("rawtypes") JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus)
		{
			Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (value != null)
				setText((String) value);
			setOpaque(true);

			setForeground(Color.BLACK);
			if(isSelected)
				setBackground(Color.LIGHT_GRAY);
			else
				setBackground(Color.WHITE);
			return this;
		}
	}

	@Override
	protected void doDispatchEvent(ActionEvent event)
	{
		Object source = event.getSource();
		String command = event.getActionCommand();
		Object selectedObject = getSelectedUserObject(productTree);
		if(selectedObject == null) return;
		boolean archived = false;
		if(selectedObject.getClass() == String.class)
		{
			String nodeTitle = (String) selectedObject;
			if(OUT_OF_STOCK.equals(nodeTitle))
				archived = true;
			else if(IN_STOCK.equals(nodeTitle))
				archived = false;
		}
		if(source instanceof Item)
		{
			final Item item = (Item) source;
			if(!archived)
			{
				if(ItemEvent.ITEM_ADDED.equals(command))
				{
					productListTableModel.fireTableDataChanged();
					int viewRowIndex = productListTable.convertRowIndexToView(products.indexOf(item));
					productListTable.setRowSelectionInterval(viewRowIndex, viewRowIndex);
				}
				else if(ItemEvent.ITEM_UPDATED.equals(command))
				{
					int viewRowIndex = productListTable.getSelectedRow();
					productListTableModel.fireTableDataChanged();
					if(viewRowIndex >= 0 && viewRowIndex <= products.size())
						productListTable.setRowSelectionInterval(viewRowIndex, viewRowIndex);
				}
				else if(ItemEvent.ITEM_REMOVED.equals(command))
				{
					int viewRowIndex = productListTable.getSelectedRow();
					if(viewRowIndex >= products.size())
						viewRowIndex--;
					productListTableModel.fireTableDataChanged();
					if(viewRowIndex >= 0 && viewRowIndex <= products.size())
						productListTable.setRowSelectionInterval(viewRowIndex, viewRowIndex);
				}
			}
			else
			{
				if(ItemEvent.ARCHIVED_ITEM_ADDED.equals(command))
				{
					productListTableModel.fireTableDataChanged();
					int viewRowIndex = productListTable.convertRowIndexToView(archivedProducts.indexOf(item));
					productListTable.setRowSelectionInterval(viewRowIndex, viewRowIndex);
				}
				else if(ItemEvent.ARCHIVED_ITEM_UPDATED.equals(command))
				{
					int viewRowIndex = productListTable.getSelectedRow();
					productListTableModel.fireTableDataChanged();
					if(viewRowIndex >= 0 && viewRowIndex <= archivedProducts.size())
						productListTable.setRowSelectionInterval(viewRowIndex, viewRowIndex);
				}
				else if(ItemEvent.ARCHIVED_ITEM_REMOVED.equals(command))
				{
					int viewRowIndex = productListTable.getSelectedRow();
					if(viewRowIndex >= archivedProducts.size())
						viewRowIndex--;
					productListTableModel.fireTableDataChanged();
					if(viewRowIndex >= 0 && viewRowIndex <= archivedProducts.size())
						productListTable.setRowSelectionInterval(viewRowIndex, viewRowIndex);
				}
			}
			if(ItemEvent.ITEMS_REMOVED.equals(command) ||
					ItemEvent.ARCHIVED_ITEMS_REMOVED.equals(command))
			{
				productListTableModel.fireTableDataChanged();
			}
		}
	}

	/**
	 * Creates table containing lists of products.
	 * @param tableModel model containing product data
	 * @return constructed table object
	 */
	private JTable createProductListTable(DefaultTableModel tableModel)
	{
		final JTable table = createCatalogueTable(tableModel);
		final TableColumn tableColumn = table.getColumnModel().getColumn(8);
		setUpComboBoxColumn(table, tableColumn);

		final JPopupMenu cataloguePopupMenu = new JPopupMenu();
		final JMenuItem archiveItem = new JMenuItem(Messages.getString("TabProducts.6")); //$NON-NLS-1$
		final JMenuItem unarchiveItem = new JMenuItem(Messages.getString("TabProducts.7")); //$NON-NLS-1$
		final JMenuItem newItem = new JMenuItem(Messages.getString("TabProducts.8")); //$NON-NLS-1$
		final JMenuItem editItem = new JMenuItem(Messages.getString("TabProducts.9")); //$NON-NLS-1$

		newItem.addActionListener(event ->
		{
			Item newProduct =
					clientFrame.showProductDialog(myParty.createEmptyProduct(), Messages.getString("TabProducts.10"), true); //$NON-NLS-1$
			if (newProduct != null)
			{
				try
				{
					if(newProduct.isInStock())
						myParty.addProduct(newProduct);
					else
						myParty.archiveProduct(newProduct);
				}
				catch (DetailException e)
				{
					logger.error(Messages.getString("TabProducts.11"), e); //$NON-NLS-1$
					EventQueue.invokeLater(() ->
					JOptionPane.showMessageDialog(null, Messages.getString("TabProducts.12"), //$NON-NLS-1$
							Messages.getString("TabProducts.13"), JOptionPane.ERROR_MESSAGE) //$NON-NLS-1$
							);
				}
			}
			else
			{
				myParty.decreaseProductID();
			}
		});

		editItem.addActionListener(event ->
		{
			final int modelRowIndex = table.convertRowIndexToModel(table.getSelectedRow());
			boolean inStock = (boolean) editItem.getClientProperty("InStock");
			Item originalProduct = null;
			if(inStock)
				originalProduct = myParty.getProducts().get(modelRowIndex);
			else
				originalProduct = myParty.getArchivedProducts().get(modelRowIndex);
			Item editedProduct =
					clientFrame.showProductDialog(originalProduct, Messages.getString("TabProducts.15"), true); //$NON-NLS-1$
			if(editedProduct != null)
			{
				try
				{
					if(editedProduct.isInStock())
					{
						if(originalProduct.isInStock())
						{
							myParty.updateProduct(editedProduct, modelRowIndex);
						}
						else
						{
							myParty.unarchiveProduct(modelRowIndex);
							myParty.addProduct(editedProduct);
						}
					}
					else
					{
						if(originalProduct.isInStock())
						{
							myParty.removeProduct(modelRowIndex);
							myParty.archiveProduct(editedProduct);
						}
						else
						{
							myParty.updateArchivedProduct(editedProduct, modelRowIndex);
						}
					}
				}
				catch (DetailException e)
				{
					EventQueue.invokeLater(() ->
					JOptionPane.showMessageDialog(clientFrame, Messages.getString("TabProducts.16"), //$NON-NLS-1$
							Messages.getString("TabProducts.17"), JOptionPane.ERROR_MESSAGE) //$NON-NLS-1$
							);
					logger.error(Messages.getString("TabProducts.18"), e); //$NON-NLS-1$
				}
			}
		});

		archiveItem.addActionListener( event ->
		{
			try
			{
				final int modelRowIndex = table.convertRowIndexToModel(table.getSelectedRow());
				final Item product = myParty.removeProduct(modelRowIndex);
				myParty.archiveProduct(product);
			}
			catch (Exception e)
			{
				EventQueue.invokeLater(() ->
				JOptionPane.showMessageDialog(clientFrame, Messages.getString("TabProducts.19"), //$NON-NLS-1$
						Messages.getString("TabProducts.20"), JOptionPane.ERROR_MESSAGE) //$NON-NLS-1$
						);
				logger.error(Messages.getString("TabProducts.21"), e); //$NON-NLS-1$
			}
		});

		unarchiveItem.addActionListener( event ->
		{
			try
			{
				final int modelRowIndex = table.convertRowIndexToModel(table.getSelectedRow());
				final Item product = myParty.unarchiveProduct(modelRowIndex);
				myParty.addProduct(product);
			}
			catch (Exception e)
			{
				EventQueue.invokeLater(() ->
				JOptionPane.showMessageDialog(clientFrame, Messages.getString("TabProducts.22"), //$NON-NLS-1$
						Messages.getString("TabProducts.23"), JOptionPane.ERROR_MESSAGE) //$NON-NLS-1$
						);
				logger.error(Messages.getString("TabProducts.24"), e); //$NON-NLS-1$
			}
		});

		table.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent event)
			{
				if(SwingUtilities.isRightMouseButton(event))
				{
					Object selectedObject = getSelectedUserObject(productTree);
					if(selectedObject == null) return;
					boolean inStock = false;
					if(selectedObject.getClass() == String.class)
					{
						String nodeTitle = (String) selectedObject;
						if(OUT_OF_STOCK.equals(nodeTitle))
							inStock = false;
						else if(IN_STOCK.equals(nodeTitle))
							inStock = true;
					}

					final int viewRowIndex = table.rowAtPoint(event.getPoint());
					if(viewRowIndex > -1)
					{
						cataloguePopupMenu.removeAll();
						if(inStock)
						{
							cataloguePopupMenu.add(newItem);
							cataloguePopupMenu.addSeparator();
							editItem.putClientProperty("InStock", true); //$NON-NLS-1$
							cataloguePopupMenu.add(editItem);
							cataloguePopupMenu.add(archiveItem);
						}
						else
						{
							cataloguePopupMenu.add(newItem);
							cataloguePopupMenu.addSeparator();
							editItem.putClientProperty("InStock", false); //$NON-NLS-1$
							cataloguePopupMenu.add(editItem);
							cataloguePopupMenu.add(unarchiveItem);
						}
						cataloguePopupMenu.show(table, event.getX(), event.getY());
					}
					else
					{
						cataloguePopupMenu.removeAll();
						cataloguePopupMenu.add(newItem);
						cataloguePopupMenu.show(table, event.getX(), event.getY());
					}
				}
			}
		});

		return table;
	}

	private JTree createProductTree(DefaultTreeModel productTreeModel)
	{
		final JTree tree = new JTree(productTreeModel);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		final JLabel blankLabel = new JLabel();

		final JPopupMenu treePopupMenu = new JPopupMenu();
		final JMenuItem newItem = new JMenuItem(Messages.getString("TabProducts.27")); //$NON-NLS-1$
		treePopupMenu.add(newItem);

		newItem.addActionListener(event ->
		{
			final Item newProduct =
					clientFrame.showProductDialog(myParty.createEmptyProduct(), Messages.getString("TabProducts.28"), true); //$NON-NLS-1$
			if (newProduct != null)
			{
				try
				{
					if(newProduct.isInStock())
						myParty.addProduct(newProduct);
					else
						myParty.archiveProduct(newProduct);
				}
				catch (DetailException e)
				{
					logger.error(Messages.getString("TabProducts.29"), e); //$NON-NLS-1$
					EventQueue.invokeLater(() ->
					JOptionPane.showMessageDialog(null, Messages.getString("TabProducts.30"), //$NON-NLS-1$
							Messages.getString("TabProducts.31"), JOptionPane.ERROR_MESSAGE) //$NON-NLS-1$
							);
				}
			}
			else
			{
				myParty.decreaseProductID();
			}
		});

		tree.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent event)
			{
				if(SwingUtilities.isRightMouseButton(event))
				{
					final TreePath path = tree.getPathForLocation(event.getX(), event.getY());
					final Object selectedObject = getSelectedUserObject(path);
					if(selectedObject == null) return;
					tree.setSelectionPath(path);
					if(selectedObject.getClass() == String.class)
					{
						String nodeTitle = (String) selectedObject;
						if(IN_STOCK.equals(nodeTitle) || OUT_OF_STOCK.equals(nodeTitle))
						{
							treePopupMenu.show(tree, event.getX(), event.getY());
						}
					}
				}
			}

		});

		tree.addTreeSelectionListener(event ->
		{
			final Object selectedObject = getSelectedUserObject(tree);
			if(selectedObject == null) return;
			if(selectedObject.getClass() == String.class)
			{
				String nodeTitle = (String) selectedObject;
				if(nodeTitle.equals(IN_STOCK))
				{
					((ProductListTableModel) productListTableModel).setProducts(products);
					productListTableModel.fireTableDataChanged();
					rightScrollPane.setViewportView(productListTable);
				}
				else if(nodeTitle.equals(OUT_OF_STOCK))
				{
					((ProductListTableModel) productListTableModel).setProducts(archivedProducts);
					productListTableModel.fireTableDataChanged();
					rightScrollPane.setViewportView(productListTable);
				}
				else if(nodeTitle.equals(PRODUCTS_AND_SERVICES))
				{
					rightScrollPane.setViewportView(blankLabel);
				}
			}
			else
				rightScrollPane.setViewportView(blankLabel);
			repaint();
		});


		return tree;
	}
}