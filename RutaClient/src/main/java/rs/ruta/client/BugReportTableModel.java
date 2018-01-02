package rs.ruta.client;

import javax.swing.table.*;

import rs.ruta.common.BugReport;

public class BugReportTableModel extends AbstractTableModel
{
	private static final long serialVersionUID = 3845356013956033331L;

	private String[] rowNames =
		{
			"ID", "Summary", "Description", "Reported by", "Product", "Component", "Version", "Status", "Resolution",
			"Platform", "Operating system", "Java version", "Reported", "Modified", "Priority", "Severity",
			"Attachment", "Comment"
		};

	private BugReport bugReport;
	/**Number of {@link ReportAttachment}s in {@code BugReport}.*/
	private int numAtts;
	/**Number of {@link ReportComment}s in {@code BugReport}.*/
	private int numComms;

	public BugReportTableModel()
	{
		bugReport = null;
		numAtts = numComms = 0;
	}

	public void setBugReport(BugReport bugReport)
	{
		this.bugReport = bugReport;
		numAtts = bugReport.getAttachments().size();
		numComms = bugReport.getComments().size();
	}

	@Override
	public int getRowCount()
	{
		int rowCnt = rowNames.length - 2 + numAtts + numComms;
		return rowCnt;
	}

	@Override
	public int getColumnCount()
	{
		return 2;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		int fixedRowCnt = rowNames.length - 2; //number of rows every model has i.e. everything except atts and comments
		if(columnIndex == 0)
			if(rowIndex < fixedRowCnt)
				return rowNames[rowIndex];
			else // Attachment or Comment
			{
				int relative = rowIndex - fixedRowCnt + 1; // = 1,2,3...

				if(relative <= numAtts)
					return "Attachment " + relative;
				else
					return "Comment " + (relative - numAtts);
			}
		else
		{
			if(bugReport == null)
				return null;
			switch(rowIndex)
			{
			case 0:
				return  bugReport.getId();
			case 1:
				return  bugReport.getSummary();
			case 2:
				return  bugReport.getDescription();
			case 3:
				return bugReport.getReportedBy();
			case 4:
				return bugReport.getProduct();
			case 5:
				return bugReport.getComponent();
			case 6:
				return bugReport.getVersion();
			case 7:
				return bugReport.getStatus();
			case 8:
				return bugReport.getResolution();
			case 9:
				return bugReport.getPlatform();
			case 10:
				return bugReport.getOs();
			case 11:
				return bugReport.getJavaVersion();
			case 12:
				return bugReport.getReported(); //MMM: should be called method like Party.getRegistrationDate() - place it in InstanceFactory
			case 13:
				return bugReport.getModified(); //MMM: should be called method like Party.getRegistrationDate() - place it in InstanceFactory
			case 14:
				return bugReport.getPriority();
			case 15:
				return bugReport.getSeverity();
			default:
			{
				int relative = rowIndex - fixedRowCnt + 1; // = 1,2,3...

				if(relative <= numAtts)
					return bugReport.getAttachments().get(relative - 1).getName(); //MMM: put here popumenu with Open and Save options: FileChooser
				else
					return bugReport.getComments().get(relative - numAtts - 1).getText();
			}
			}
		}
	}

	//MMM: method not used because the table is not editable
/*	@Override
	public void setValueAt(Object obj, int rowIndex, int columnIndex)
	{
		/*int fixedRowCnt = rowNames.length - 2; //number of rows every model has i.e. everything except atts and comments
		switch(rowIndex)
		{
		case 0:
			bugReport.setId(obj.toString());
		case 1:
			bugReport.setSummary(obj.toString());
		case 2:
			bugReport.setDescription(obj.toString());
		case 3:
			bugReport.setReportedBy(obj.toString());
		case 4:
			bugReport.setProduct(obj.toString());
		case 5:
			bugReport.setComponent(obj.toString());
		case 6:
			bugReport.setVersion(obj.toString());
		case 7:
			bugReport.setStatus(obj.toString());
		case 8:
			bugReport.setResolution(obj.toString());
		case 9:
			bugReport.setPlatform(obj.toString());
		case 10:
			bugReport.setOs(obj.toString());
		case 11:
			bugReport.setJavaVersion(obj.toString());
		case 12:
			bugReport.setReported(obj.toString());
		case 13:
			bugReport.setModified(obj.toString());
		case 14:
			bugReport.setPriority(obj.toString());
		case 15:
			bugReport.setSeverity(obj.toString());
		default:
		{
			int relative = rowIndex - fixedRowCnt + 1; // = 1,2,3...

			if(relative <= numAtts)
				bugReport.getAttachments().get(relative - 1).getName(); //MMM: put here FileChooser for opening the file
			else
				bugReport.getComments().get(relative - numAtts - 1).getText();
		}
		}
	}*/

	@Override
	public String getColumnName(int columnIndex)
	{
		switch(columnIndex)
		{
		case 0:
			return "Property";
		case 1:
			return "Value";
		default:
			return "";
		}
	}

	//MMM: method not used because the table is not editable - th default return false
/*	@Override
	public boolean isCellEditable(int row, int column)
	{
		return (!tableEditable) || (column == 0 || row == 4 || row == 16 || row == 27) ? false : true;
	}*/


	@Override
	public Class<?> getColumnClass(int columnIndex)
	{
		switch(columnIndex)
		{
		case 0:
			return String.class;
		default:
			return Object.class;
		}
	}

}

