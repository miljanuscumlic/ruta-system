package rs.ruta.client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import oasis.names.specification.ubl.schema.xsd.applicationresponse_21.ApplicationResponseType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.DocumentResponseType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.ResponseType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.NoteType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.ResponseCodeType;
import oasis.names.specification.ubl.schema.xsd.orderresponsesimple_21.OrderResponseSimpleType;
import rs.ruta.common.InstanceFactory;

public abstract class AbstractApplicationResponseDialog extends JDialog
{
	private static final long serialVersionUID = 3259789645341776081L;
	protected static ApplicationResponseType applicationResponse;
	protected JTable responseTable;
	protected JPanel buttonPanel;

	/**
	 * Creates {@link OrderResponseSimpleDialog} for making new {@link ApplicationResponseType} document.
	 * {@code corr} argument should be set to {@code null}
	 * when new {@code Order Response} is to be created or old one viewed and to appropriate non-{@code null} value only when
	 * some old {@code Order Response} failed to be delievered and new sending attempt of it could be tried.
	 * @param owner parent frame
	 * @param applicationResponse Application Response to show or amend
	 * @param editable true if Application Response document is to be created; false if is to be viewed only
	 *
	 */
	public AbstractApplicationResponseDialog(RutaClientFrame owner, ApplicationResponseType applicationResponse,
			boolean editable)
	{
		super(owner, true);
		ApplicationResponseDialog.applicationResponse = applicationResponse;
		setSize(700, 180);
		setLocationRelativeTo(owner);
		final JPanel responsePanel = new JPanel(new BorderLayout());
		final ApplicationResponseModel responseModel = new ApplicationResponseModel(editable);
		responseTable = createResponseTable(responseModel);

		responsePanel.add(new JScrollPane(responseTable));
		add(responsePanel, BorderLayout.CENTER);

		MouseAdapter tableFocus = new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent event)
			{
				stopEditing();
			}
		};
		addMouseListener(tableFocus);
		buttonPanel = new JPanel();
		add(buttonPanel, BorderLayout.SOUTH);
	}

	protected void stopEditing()
	{
		if(responseTable.isEditing())
			responseTable.getCellEditor().stopCellEditing();
	}

	public ApplicationResponseType getApplicationResponse()
	{
		return applicationResponse;
	}

	private JTable createResponseTable(DefaultTableModel tableModel)
	{
		JTable table = new JTable(tableModel);
		table.setTableHeader(null);
		table.setFillsViewportHeight(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		TableColumnModel columnModel = table.getColumnModel();
		columnModel.getColumn(0).setPreferredWidth(180);
		columnModel.getColumn(1).setPreferredWidth(520);
		return table;
	}

	private static class ApplicationResponseModel extends DefaultTableModel
	{
		private static final long serialVersionUID = 3487280406309398968L;

		//must be static because it's used in getRowCount method which is called from the
		//constructor of the super class
		private static String[] rowNames = { "Response Document Type", "ID", "Issue Date", "Response code", "Note"};     
		private static int RESPONSE_CODE_INDEX = 3;
		private static int NOTE_INDEX = 4;
		private boolean editable;

		protected ApplicationResponseModel(boolean editable)
		{
			super();
			this.editable = editable;
		}

		@Override
		public boolean isCellEditable(int row, int column)
		{
			return editable ? (column == 1 && (row == NOTE_INDEX /*|| row == RESPONSE_CODE_INDEX*/)) : false;
		}

		@Override
		public int getRowCount()
		{
			return rowNames.length;
		}

		@Override
		public int getColumnCount()
		{
			return 2;
		}

		@Override
		public String getColumnName(int column)
		{
			return null;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex)
		{
			if(columnIndex == 0)
			{
				return rowNames[rowIndex];
			}
			else if(columnIndex == 1)
			{
				switch(rowIndex)
				{
				case 0:
					return "Application Response"; 
				case 1:
					return applicationResponse.getIDValue();
				case 2:
					return InstanceFactory.getLocalDateAsString(applicationResponse.getIssueDateValue());
				case 3:
					try
					{
						return applicationResponse.getDocumentResponseAtIndex(0).getResponse().getResponseCodeValue();
					}
					catch(Exception e)
					{
						return null;
					}
				case 4:
					if(applicationResponse.getNoteCount() != 0)
						return applicationResponse.getNoteAtIndex(0).getValue();
					else
						return null;
				default:
					return null;
				}
			}
			else
				return null;
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex)
		{
			if(columnIndex == 1)
			{
				if(rowIndex == NOTE_INDEX)
				{
					applicationResponse.getNote().clear();
					applicationResponse.addNote(new NoteType((String) aValue));
				}
//				else if(rowIndex == RESPONSE_CODE_INDEX)
//				{
//					applicationResponse.getDocumentResponse().clear();
//					final DocumentResponseType docResponse = new DocumentResponseType();
//					final ResponseType response = new ResponseType();
//					response.setResponseCode(new ResponseCodeType((String) aValue));
//					docResponse.setResponse(response);
//					applicationResponse.addDocumentResponse(docResponse);
//				}
			}
		}
	}
}