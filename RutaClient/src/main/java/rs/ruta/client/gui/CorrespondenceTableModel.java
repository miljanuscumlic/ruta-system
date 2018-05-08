package rs.ruta.client.gui;

import javax.swing.table.DefaultTableModel;
import javax.xml.datatype.XMLGregorianCalendar;

import rs.ruta.client.correspondence.Correspondence;
import rs.ruta.common.InstanceFactory;

/**
 * Data model for a table displaying {@link Correspondence} of a party.
 */
public class CorrespondenceTableModel extends DefaultTableModel
{
	private static final long serialVersionUID = -6952612036544033712L;

	private static String[] columnNames =
		{
				"No.", "From", "Document", "ID", "Issue Time", "Received Time"
		};

	private Correspondence corr;

	public CorrespondenceTableModel()
	{
		super();
	}

	@Override
	public boolean isCellEditable(int row, int column)
	{
		return false;
	}

	public void setCorrespondence(Correspondence corr)
	{
		this.corr = corr;
	}

	@Override
	public int getRowCount()
	{
		return corr != null ? corr.getDocumentReferenceCount() : 0;
	}

	@Override
	public int getColumnCount()
	{
		return columnNames.length;
	}

	@Override
	public String getColumnName(int columnIndex)
	{
		return columnNames[columnIndex];
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		switch(columnIndex)
		{
		case 0:
			return rowIndex + 1;
		case 1:
			return corr.getDocumentReferenceAtIndex(rowIndex).getIssuerParty().getPartyNameAtIndex(0);
		case 2:
			return corr.getDocumentReferenceAtIndex(rowIndex).getDocumentTypeValue();
		case 3:
			return corr.getDocumentReferenceAtIndex(rowIndex).getIDValue();
		case 4:
			final XMLGregorianCalendar issueDate = corr.getDocumentReferenceAtIndex(rowIndex).getIssueDateValue();
			final XMLGregorianCalendar issueTime = corr.getDocumentReferenceAtIndex(rowIndex).getIssueTimeValue();
			return InstanceFactory.getLocalDateTimeAsString(InstanceFactory.mergeDateTime(issueDate, issueTime));
		case 5:
			final XMLGregorianCalendar receivedTime = corr.getDocumentReferenceAtIndex(rowIndex).getReceivedTime();
			return InstanceFactory.getLocalDateTimeAsString(receivedTime);
		default:
			return null;
		}
	}
}