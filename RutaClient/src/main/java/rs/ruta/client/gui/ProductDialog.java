package rs.ruta.client.gui;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.UUID;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumnModel;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.ItemIdentificationType;
import rs.ruta.client.Item;
import rs.ruta.client.MyParty;

public class ProductDialog extends JDialog
{
	private static final long serialVersionUID = 4495188858602414235L;
	private ProductTableModel productTableModel;
	private JTable productTable;
	private boolean changed;
	private Item item;

	public ProductDialog(RutaClientFrame owner, MyParty myParty, Item item, boolean editable)
	{
		super(owner, true);
		this.item = item;
		setSize(500,250);
		setLocationRelativeTo(owner);

		final JPanel productPanel = new JPanel();

		productTableModel = new ProductTableModel(myParty, item, editable);
		productTable = new ProductTable(productTableModel);
		productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		productTable.setFillsViewportHeight(true);
		productTable.setColumnSelectionAllowed(false);
		productTable.getTableHeader().setReorderingAllowed(false);

		productPanel.add(new JScrollPane(productTable));
		add(productPanel, BorderLayout.CENTER);

		TableColumnModel tableColumnModel = productTable.getColumnModel();
		tableColumnModel.getColumn(0).setPreferredWidth(150);
		tableColumnModel.getColumn(1).setPreferredWidth(350);
		tableColumnModel.getColumn(0).setResizable(false);

		MouseAdapter tableLostFocus = new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				stopEditing();
			}
		};
		productTable.getTableHeader().addMouseListener(tableLostFocus);
		addMouseListener(tableLostFocus);

		JPanel buttonPanel = new JPanel();

		JButton okButton = new JButton(Messages.getString("ProductDialog.0")); 
		okButton.addActionListener(event ->
		{
			stopEditing();
			String missingField = item.verifyItem();
			if(missingField == null)
			{
				changed = productTableModel.isChanged();
				setVisible(false);
			}
			else
				JOptionPane.showMessageDialog(ProductDialog.this, missingField + Messages.getString("ProductDialog.1"), 
						Messages.getString("ProductDialog.2"), JOptionPane.ERROR_MESSAGE); 
		});
		if(editable)
		{
			buttonPanel.add(okButton);
			getRootPane().setDefaultButton(okButton);
			okButton.requestFocusInWindow();
		}

		JButton cancelButton = new JButton(Messages.getString("ProductDialog.3")); 
		cancelButton.addActionListener(event ->
		{
			setVisible(false);
		});

		if(!editable)
		{
			getRootPane().setDefaultButton(cancelButton);
			cancelButton.requestFocusInWindow();
			cancelButton.setText(Messages.getString("ProductDialog.4")); 

		}
		cancelButton.setVerifyInputWhenFocusTarget(false); //do not verify previously focused element when Cancel is clicked
		buttonPanel.add(cancelButton);
		add(buttonPanel, BorderLayout.SOUTH);


	}

	private void stopEditing()
	{
		if(productTable.isEditing())
			productTable.getCellEditor().stopCellEditing();
	}

	public boolean isChanged()
	{
		return changed;
	}

	public void setChanged(boolean changed)
	{
		this.changed = changed;
	}

	public Item getProduct()
	{
		return item;
	}

}