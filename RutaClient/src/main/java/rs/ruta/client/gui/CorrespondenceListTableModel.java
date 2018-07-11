package rs.ruta.client.gui;

import java.util.List;

import javax.swing.table.DefaultTableModel;

import rs.ruta.client.correspondence.Correspondence;
import rs.ruta.common.InstanceFactory;

public class CorrespondenceListTableModel extends DefaultTableModel
{
	private static final long serialVersionUID = -1366607627023019652L;
	private static String[] columnNames =
		{
				Messages.getString("CorrespondenceListTableModel.0"), Messages.getString("CorrespondenceListTableModel.1"), Messages.getString("CorrespondenceListTableModel.2"), Messages.getString("CorrespondenceListTableModel.3"), Messages.getString("CorrespondenceListTableModel.4"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
				Messages.getString("CorrespondenceListTableModel.5"), Messages.getString("CorrespondenceListTableModel.6") //$NON-NLS-1$ //$NON-NLS-2$
		};
	private List<Correspondence> correspondences = null;

	public CorrespondenceListTableModel()
	{
		super();
	}

	public void setCorrespondences(List<Correspondence> correspondeces)
	{
		this.correspondences = correspondeces;
	}

	/**
	 * Gets the {@link Correspondence correspondence} from the list of correspondences.
	 * @param index party's index
	 * @return party or {@code null} if correspondences field is {@code null}
	 */
	public Correspondence getCorrespondenceAtIndex(int index)
	{
		return correspondences != null ? correspondences.get(index) : null;
	}

	@Override
	public int getRowCount()
	{
		return correspondences != null ? correspondences.size() : 0;
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
		final Correspondence corr = correspondences.get(rowIndex);
		try
		{
			switch(columnIndex)
			{
			case 0:
				return rowIndex + 1;
			case 1:
				return corr.getName();
			case 2:
				return corr.getDocumentReferenceCount();
			case 3:
				return InstanceFactory.getLocalDateTimeAsString(corr.getCreationTime());
			case 4:
				return InstanceFactory.getLocalDateTimeAsString(corr.getLastActivityTime());
			case 5:
				return InstanceFactory.getLocalDateTimeAsString(corr.getLastDocumentIssueTime());
			case 6:
				return corr.isActive() ? Messages.getString("CorrespondenceListTableModel.7") : Messages.getString("CorrespondenceListTableModel.8"); //$NON-NLS-1$ //$NON-NLS-2$
			default:
				return null;
			}
		}
		catch(Exception e)
		{
			return null;
		}
	}

	@Override
	public boolean isCellEditable(int row, int column)
	{
		return false;
	}
}
