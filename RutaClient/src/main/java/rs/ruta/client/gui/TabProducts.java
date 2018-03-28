package rs.ruta.client.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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

import rs.ruta.client.MyParty;
import rs.ruta.common.InstanceFactory;

public class TabProducts extends TabComponent
{
	protected static Logger logger = LoggerFactory.getLogger("rs.ruta.client");

	public TabProducts(final RutaClientFrame clientFrame)
	{
		super(clientFrame);
		final MyParty myParty = clientFrame.getClient().getMyParty();
		final DefaultTableModel tableModel = new ProductTableModel(myParty, true);
		final JTable table = createCatalogueTable(tableModel);
		//			table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		TableColumn tableColumn = table.getColumnModel().getColumn(8);
		setUpComboBoxColumn(table, tableColumn);

		final JPopupMenu cataloguePopupMenu = new JPopupMenu();

		table.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent event)
			{
				if(SwingUtilities.isRightMouseButton(event))
				{
					final int row = table.rowAtPoint(event.getPoint());
					if(row < table.getRowCount() - 1) //except the last row
					{
						table.setRowSelectionInterval(row, row);
						cataloguePopupMenu.show(table, event.getX(), event.getY());
					}
				}
			}
		});

		final JMenuItem deleteItem = new JMenuItem("Delete item");
		cataloguePopupMenu.add(deleteItem);

		deleteItem.addActionListener( event ->
		{
			final int row = table.getSelectedRow();
			int col = table.getSelectedColumn();

			try
			{
				myParty.removeProduct(row);
				repaint();
			}
			catch (Exception e)
			{
				EventQueue.invokeLater(() ->
				JOptionPane.showMessageDialog(clientFrame, "Could not remove the product from the list.",
						"Database Error", JOptionPane.ERROR_MESSAGE)
						);
				logger.error("Could not remove the product from the list.", e);
			}
		});

		component = new JScrollPane(table);
	}

	@SuppressWarnings("unchecked")
	private void setUpComboBoxColumn(final JTable table, final TableColumn tableColumn)
	{
		final JComboBox<String> comboBox = new JComboBox<String>(InstanceFactory.getTaxCategories());
		comboBox.setRenderer(new ComBoxCellRenderer());
		comboBox.setFont(new JLabel("Test").getFont().deriveFont(Font.PLAIN));
		tableColumn.setCellEditor(new DefaultCellEditor(comboBox));
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
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

}