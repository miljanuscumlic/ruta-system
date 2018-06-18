package rs.ruta.client.gui;

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
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rs.ruta.client.Item;
import rs.ruta.client.MyParty;
import rs.ruta.common.InstanceFactory;
import rs.ruta.common.datamapper.DetailException;

public class TabProducts extends TabComponent
{
	private static final long serialVersionUID = 7435742718848842547L;
	private JTable productListTable;
	private DefaultTableModel productListTableModel;
	private List<Item> products;
	protected static Logger logger = LoggerFactory.getLogger("rs.ruta.client");

	public TabProducts(final RutaClientFrame clientFrame)
	{
		super(clientFrame);
		final MyParty myParty = clientFrame.getClient().getMyParty();
		products = myParty.getProducts();
		productListTableModel = new ProductListTableModel(myParty, true);
		productListTable = createCatalogueTable(productListTableModel);
		//			table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		productListTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		final TableColumn tableColumn = productListTable.getColumnModel().getColumn(8);
		setUpComboBoxColumn(productListTable, tableColumn);

		final JPopupMenu cataloguePopupMenu = new JPopupMenu();

		productListTable.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent event)
			{
				if(SwingUtilities.isRightMouseButton(event))
				{
					final int viewRowIndex = productListTable.rowAtPoint(event.getPoint());
					if(viewRowIndex > -1 && viewRowIndex < productListTable.getRowCount())
					{
						productListTable.setRowSelectionInterval(viewRowIndex, viewRowIndex);
						cataloguePopupMenu.show(productListTable, event.getX(), event.getY());
					}
				}
			}
		});

		final JMenuItem deleteItem = new JMenuItem("Delete");
		final JMenuItem newItem = new JMenuItem("Add New");
		final JMenuItem editItem = new JMenuItem("Edit");
		cataloguePopupMenu.add(newItem);
		cataloguePopupMenu.addSeparator();
		cataloguePopupMenu.add(editItem);
		cataloguePopupMenu.add(deleteItem);

		newItem.addActionListener(event ->
		{
			Item newProduct =
					clientFrame.showProductDialog(myParty.createEmptyProduct(), "Add New Product or Service", true);
			if (newProduct != null)
			{
				try
				{
					myParty.addProduct(newProduct);
				}
				catch (DetailException e)
				{
					logger.error("Could not insert new product in the database! Exception is: ", e);
					EventQueue.invokeLater(() ->
					JOptionPane.showMessageDialog(null, "Could not insert new product in the database!",
							"Database Error", JOptionPane.ERROR_MESSAGE)
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
			final int modelRowIndex = productListTable.convertRowIndexToModel(productListTable.getSelectedRow());
			Item editedProduct =
					clientFrame.showProductDialog(myParty.getProducts().get(modelRowIndex), "Edit Product or Service", true);
			if(editedProduct != null)
			{
				try
				{
					myParty.updateProduct(editedProduct, modelRowIndex);
				}
				catch (DetailException e)
				{
					EventQueue.invokeLater(() ->
					JOptionPane.showMessageDialog(clientFrame, "Could not update the product in the database.",
							"Database Error", JOptionPane.ERROR_MESSAGE)
							);
					logger.error("Could not update the product in the database.", e);
				}
			}
		});

		deleteItem.addActionListener( event ->
		{
			try
			{
				final int modelRowIndex = productListTable.convertRowIndexToModel(productListTable.getSelectedRow());
				myParty.removeProduct(modelRowIndex);
				//				repaint();
			}
			catch (Exception e)
			{
				EventQueue.invokeLater(() ->
				JOptionPane.showMessageDialog(clientFrame, "Could not remove the product from products list.",
						"Database Error", JOptionPane.ERROR_MESSAGE)
						);
				logger.error("Could not remove the product from the list.", e);
			}
		});

		leftPane = new JScrollPane(productListTable);
		rightPane = null;
		arrangeTab();
	}

	@SuppressWarnings("unchecked")
	private void setUpComboBoxColumn(final JTable table, final TableColumn tableColumn)
	{
		final JComboBox<String> comboBox = new JComboBox<String>(InstanceFactory.getTaxCategories());
		comboBox.setRenderer(new ComBoxCellRenderer());
		comboBox.setFont(new JLabel("Test").getFont().deriveFont(Font.PLAIN));
		tableColumn.setCellEditor(new DefaultCellEditor(comboBox));
		final DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setToolTipText("Click for combo box");
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
			//			component.setFont(component.getFont().deriveFont(Font.PLAIN));

			return this;
		}
	}

	@Override
	protected void doDispatchEvent(ActionEvent event)
	{
		Object source = event.getSource();
		String command = event.getActionCommand();
		if(source instanceof Item)
		{
			final Item item = (Item) source;
			if(ItemEvent.ITEM_ADDED.equals(command))
			{
//				int viewRowIndex = productListTable.getSelectedRow();
				productListTableModel.fireTableDataChanged();
//				if(viewRowIndex >= 0 && viewRowIndex <= products.size())
//					productListTable.setRowSelectionInterval(viewRowIndex, viewRowIndex);
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
			else if(ItemEvent.ALL_ITEMS_REMOVED.equals(command))
			{
				productListTableModel.fireTableDataChanged();
			}
		}
	}

}