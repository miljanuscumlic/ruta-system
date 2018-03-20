package rs.ruta.client.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.*;

import rs.ruta.common.BugReport;
import rs.ruta.common.InstanceFactory;

/**{@link TableModel} class that holds the model of the {@link BugReport} list retrieved from the CDR service.
 * List displays partial contents of these {@code BugReport}s, i.e. not all fields are displayed.
 */
public class BugListTableModel extends AbstractTableModel
{
	private static final long serialVersionUID = -3079128130727300652L;

	private String[] colNames =
		{
			"ID", "Summary", "Component", "Version", "Status", "Modified"
		};

	private List<BugReport> bugReports;

	public BugListTableModel()
	{
		bugReports = new ArrayList<BugReport>();
	}

	public BugListTableModel(List<BugReport> bugReports)
	{
		if(bugReports == null)
			bugReports = new ArrayList<BugReport>();
		this.bugReports = bugReports;
	}

	public void setBugReports( List<BugReport> bugReports)
	{
		this.bugReports = bugReports;
	}

	public List<BugReport> getBugReports()
	{
		return bugReports;
	}

	@Override
	public int getRowCount()
	{
		return bugReports != null ? bugReports.size() : 0;
	}

	@Override
	public int getColumnCount()
	{
		return colNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		if(bugReports.size() != 0)
		{
			BugReport bugReport = bugReports.get(rowIndex);
			switch(columnIndex)
			{
			case 0:
				return bugReport.getID();
			case 1:
				return bugReport.getSummary();
			case 2:
				return bugReport.getComponent();
			case 3:
				return bugReport.getVersion();
			case 4:
				return bugReport.getStatus();
			case 5:
				return InstanceFactory.getLocalDateTimeAsString(bugReport.getModified());
			default:
				return null;
			}
		}
		else
			return null;
	}

	@Override
	public String getColumnName(int columnIndex)
	{
		return colNames[columnIndex];
	}

	@Override
	public Class<?> getColumnClass(int columnIndex)
	{
		return String.class;
	}
}