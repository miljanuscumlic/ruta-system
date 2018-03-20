package rs.ruta.client.gui;

import javax.swing.table.*;

import rs.ruta.common.BugReport;
import rs.ruta.common.InstanceFactory;
import rs.ruta.common.ReportComment;

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

	public BugReport getBugReport()
	{
		return bugReport;
	}

	@Override
	public int getRowCount()
	{
		return rowNames.length - 2 + numAtts + numComms;
	}

	@Override
	public int getColumnCount()
	{
		return 2;
	}

	/**Gets the number of rows that are always present i.e. not counting attachment and comment rows.
	 * @return number of allways displayed rows
	 */
	public int getFixedRowCount()
	{
		return rowNames.length - 2;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		int fixedRowCnt = getFixedRowCount();
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
				return bugReport.getID();
			case 1:
				return bugReport.getSummary();
			case 2:
				return bugReport.getDescription();
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
				return InstanceFactory.getLocalDateTimeAsString(bugReport.getReported());
			case 13:
				return InstanceFactory.getLocalDateTimeAsString(bugReport.getModified());
			case 14:
				return bugReport.getPriority();
			case 15:
				return bugReport.getSeverity();
			default: // >= 16
				int relative = rowIndex - fixedRowCnt; // = 0,1,2,3...

				if(relative < numAtts)
					return bugReport.getAttachments().get(relative).getName();
				else if(relative < numAtts + numComms)
					return bugReport.getComments().get(relative - numAtts).getText();
				else
					return null;
			}
		}
	}

	//MMM: method not used because the table is not editable - might be used later
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

	//MMM: method not used because the table is not editable: default implementation returns false - might be used later
/*	@Override
	public boolean isCellEditable(int row, int column)
	{
		return (!tableEditable) || (column == 0 || row == 4 || row == 16 || row == 27) ? false : true;
	}*/

	@Override
	public Class<?> getColumnClass(int columnIndex)
	{
		return String.class;
/*		switch(columnIndex)
		{
		case 0:
			return String.class;
		default:
			return Object.class;
		}*/
	}

	/**Checks whether passed row index contains the {@link ReportComment} and gets it if it does.
	 * @param rowIndex index of the table row
	 * @return comment or null if row does not contain it
	 */
	public ReportComment getComment(int rowIndex)
	{
		ReportComment comment = null;
		int index = rowIndex - getFixedRowCount() - numAtts; // comment index
		if(index >= 0)
			comment = bugReport.getComments().get(index);

		return comment;
	}

}