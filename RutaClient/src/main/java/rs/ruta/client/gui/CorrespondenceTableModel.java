package rs.ruta.client.gui;

import javax.swing.table.DefaultTableModel;
import javax.xml.datatype.XMLGregorianCalendar;

import rs.ruta.client.correspondence.Correspondence;
import rs.ruta.common.DocumentReference;
import rs.ruta.common.InstanceFactory;

/**
 * Data model for a table displaying one {@link Correspondence} of a party.
 */
public class CorrespondenceTableModel extends DefaultTableModel
{
	private static final long serialVersionUID = -6952612036544033712L;

	private static String[] columnNames =
		{
				"No.", "From", "Document Type", "ID", "Issue Time", "Added Time", "Status"       
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


	public Correspondence getCorrespondence()
	{
		return corr;
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
		final DocumentReference documentReference = corr.getDocumentReferenceAtIndex(rowIndex);
		if(documentReference != null)
		{
			switch(columnIndex)
			{
			case 0:
				return rowIndex + 1;
			case 1:
				try
				{
					return documentReference.getIssuerParty().getPartyNameAtIndex(0).getNameValue();
				}
				catch(Exception e)
				{
					return null;
				}
			case 2:
				return InstanceFactory.getDocumentName(documentReference.getDocumentTypeValue());
			case 3:
				return documentReference.getIDValue();
			case 4:
				final XMLGregorianCalendar issueDate = documentReference.getIssueDateValue();
				final XMLGregorianCalendar issueTime = documentReference.getIssueTimeValue();
				return InstanceFactory.getLocalDateTimeAsString(InstanceFactory.mergeDateTime(issueDate, issueTime));
			case 5:
				final XMLGregorianCalendar receivedTime = documentReference.getReceivedTime();
				return InstanceFactory.getLocalDateTimeAsString(receivedTime);
			case 6:
				return documentReference.getStatus();
			default:
				return null;
			}
		}
		else
			return null;
	}
}