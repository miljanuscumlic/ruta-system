package rs.ruta.client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.text.BadLocationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rs.ruta.common.BugReport;
import rs.ruta.common.BugReportSearchCriterion;
import rs.ruta.common.ReportAttachment;
import rs.ruta.common.ReportComment;
import rs.ruta.common.datamapper.RutaException;
import rs.ruta.services.AddBugReportCommentResponse;
import rs.ruta.services.FindAllBugReportsResponse;
import rs.ruta.services.FindBugReportResponse;
import rs.ruta.services.SearchBugReportResponse;

public class BugExploreDialog extends JDialog
{
	private static final long serialVersionUID = -1075911023849983930L;
	private static Logger logger = LoggerFactory.getLogger("rs.ruta.client");
	private List<BugReport> bugReports;
	private AbstractTableModel bugListModel;
	private AbstractTableModel bugReportModel;
	private RutaClientFrame parent;
	private Future<?> future = null;
	private BugReportSearchCriterion criterion;
	private JTextArea commentArea;
	private JTable bugListTable;
	private JTable bugReportTable;
	/**
	 * Index of selected {@link BugReport} in the table displaying the list of all bug reports.
	 */
	private int selectedIndex = -1; //none is selected by default

	public BugExploreDialog(RutaClientFrame parent)
	{
		super(parent, true);
		this.parent = parent;
		setTitle("Explore the bugs");
		setResizable(false);
		setSize(750, 810);
		setLocationRelativeTo(parent);
		setLayout(new BorderLayout());
		commentArea = new JTextArea(5,50);
		bugReports = parent.getClient().getBugReports();
		criterion = new BugReportSearchCriterion();

		bugListModel = new BugListTableModel(bugReports);
		add(createBugListPanel(), BorderLayout.NORTH);

		bugReportModel = new BugReportTableModel();
		add(createBugReportPanel(), BorderLayout.CENTER);

		add(createBottomPanel(), BorderLayout.SOUTH);

		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				parent.getClient().setBugReports(bugReports);
				super.windowClosing(e);
			}
		});
	}

	private JPanel createBugListPanel()
	{
		JPanel bugListPanel = new JPanel();
		GridBagLayout grid = new GridBagLayout();
		bugListPanel.setLayout(grid);

		bugListTable = new JTable(bugListModel);
		bugListTable.setFillsViewportHeight(true);
		bugListTable.getTableHeader().setReorderingAllowed(false); //disables column reordering
		bugListTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		//specifing preferred column and row sizes
		TableColumnModel tableColumnModel = bugListTable.getColumnModel();
		TableColumn tableColumn = tableColumnModel.getColumn(0);
		tableColumn.setResizable(false);
		tableColumn.setPreferredWidth(40);
		tableColumnModel.getColumn(1).setPreferredWidth(200);
		tableColumnModel.getColumn(2).setPreferredWidth(30);
		tableColumnModel.getColumn(3).setPreferredWidth(25);
		tableColumnModel.getColumn(5).setPreferredWidth(85);

		//defining selection listener for table row selection
		ListSelectionModel selectionModel = bugListTable.getSelectionModel();
		selectionModel.addListSelectionListener(new ListSelectionListener()
		{
			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				int first = e.getFirstIndex();
				int last = e.getLastIndex();
				if (((ListSelectionModel) e.getSource()).isSelectedIndex(first))
					selectedIndex = first;
				else if (((ListSelectionModel) e.getSource()).isSelectedIndex(last))
					selectedIndex = last;
				new Thread( () ->
				{
					checkAndGetBugReport(selectedIndex);
					((BugReportTableModel) bugReportModel).setBugReport(bugReports.get(selectedIndex));
					bugReportModel.fireTableDataChanged();
				}).start();
			}
		});

		Insets insets1 = new Insets(10, 5, 0, 0);
		Insets insets2 = new Insets(5, 5, 0, 0);
		JButton reloadListButton = new JButton("Reload bug report list");

		reloadListButton.addActionListener(event ->
		{
			new Thread( () ->
			{
				WaitingDialog waitingDialog = new WaitingDialog(this, future);
				EventQueue.invokeLater(() -> waitingDialog.setVisible(true));
				future = parent.searchBugReport(criterion);

				try
				{
					SearchBugReportResponse res = (SearchBugReportResponse) future.get();
					EventQueue.invokeLater(() -> waitingDialog.setVisible(false));
					bugReports = res.getReturn();
					if(bugReports != null && !bugReports.isEmpty())
					{
						((BugListTableModel) bugListModel).setBugReports(bugReports);
						bugListModel.fireTableDataChanged();
						//bugListTable.repaint();
						//"Search request for the bug reports has been sent to the CDR service. Waiting for a response..."
						parent.appendToConsole("Bug report list has been successfully retrieved from the CDR service.", Color.GREEN);
					}
					else //zarro boogs found
						EventQueue.invokeLater( () ->
						JOptionPane.showMessageDialog(this, "Zarro boogs found!", "Information", JOptionPane.INFORMATION_MESSAGE));
				}
				catch(Exception e)
				{
					EventQueue.invokeLater( () ->
					{
						waitingDialog.setVisible(false);
						JOptionPane.showMessageDialog(BugExploreDialog.this,
								"There has been an error during the retrieval of the bug report list.\n Please try again later.",
								"Error", JOptionPane.ERROR_MESSAGE);
					});
				}
			}).start();
		});

		JScrollPane bugListPane = new JScrollPane(bugListTable);
		bugListPane.setPreferredSize(new Dimension(700, 150));
		putGridCell(bugListPanel, 0, 0, 1, 1, insets1, bugListPane);
		putGridCell(bugListPanel, 1, 0, 1, 1, insets2, reloadListButton);

		bugListPanel.setBorder(new TitledBorder("Bug report list"));
		return bugListPanel;
	}

	/**
	 * Checks whether the BugReport should be retrieved from the CDR, and sends a request if it should be.
	 * If the BugReort is complete i.e. previously retrieved from the CDR, it wiil not send new request to the CDR.
	 * @param index {@code BugReport}'s index in the table
	 */
	private void checkAndGetBugReport(int index)
	{
		BugReport bugReport = bugReports.get(index);
		if(bugReport.getReportedBy() == null) //BugReport has not been retrieved from the CDR yet
		{
			Future<?> future = null;
			WaitingDialog waitingDialog = new WaitingDialog(BugExploreDialog.this, future);
			EventQueue.invokeLater(() -> waitingDialog.setVisible(true));

			future = parent.getClient().cdrFindBug(bugReport.getID());
			try
			{
				FindBugReportResponse res = (FindBugReportResponse) future.get();
				EventQueue.invokeLater(() -> waitingDialog.setVisible(false));
				BugReport newBugReport = res.getReturn();
				if(newBugReport != null)
				{
					bugReports.set(index, newBugReport);
					bugListModel.fireTableDataChanged();
					//due to repaint selection will be lost, so select previously selected row
					bugListTable.setRowSelectionInterval(selectedIndex, selectedIndex);
					parent.appendToConsole("Bug report with ID: " + bugReport.getID() +
							" has been successfully retrieved from the CDR service.", Color.GREEN);
				}
				else
				{
					parent.appendToConsole("Bug report with ID: " + bugReport.getID() +
							" does not exist on the CDR service anymore.", Color.BLACK);
					EventQueue.invokeLater( () ->
					{
						JOptionPane.showMessageDialog(BugExploreDialog.this,
								"Bug report with ID: " + bugReport.getID() +
								" does not exist anymore. It wiil be deleted from the bug report list.",
								"Error", JOptionPane.ERROR_MESSAGE);
						bugReports.remove(index);
						bugListModel.fireTableDataChanged();
					});
				}
			}
			catch(Exception e)
			{
				EventQueue.invokeLater( () ->
				{
					waitingDialog.setVisible(false);
					JOptionPane.showMessageDialog(BugExploreDialog.this,
							"There has been an error during the retrieval of the bug report.\nPlease try again later.",
							"Error", JOptionPane.ERROR_MESSAGE);
				});
				parent.appendToConsole("There has been an error. Bug report has not been retrieved from the CDR service. " +
						e.getMessage(), Color.RED);
			}
		}
	}

	private Component createBugReportPanel()
	{
		JPanel bugReportPanel = new JPanel();
		GridBagLayout grid = new GridBagLayout();
		bugReportPanel.setLayout(grid);

		bugReportTable = new JTable(bugReportModel);
		bugReportTable.setFillsViewportHeight(true);
		bugReportTable.getTableHeader().setReorderingAllowed(false);
		bugReportTable.setRowSelectionAllowed(false);
		bugReportTable.setColumnSelectionAllowed(false);

		//specifing preferred column and row sizes
		TableColumnModel tableColumnModel = bugReportTable.getColumnModel();
		TableColumn tableColumn = tableColumnModel.getColumn(0);
		tableColumn.setResizable(false);
		tableColumn.setMinWidth(125); //tableColumn.setPreferredWidth doesn't work
		tableColumn.setMaxWidth(125);

		//setting cell renderer
		TableCellRenderer cellRenderer = new BugReportCellRenderer();
		tableColumnModel.getColumn(0).setCellRenderer(cellRenderer);
		tableColumnModel.getColumn(1).setCellRenderer(cellRenderer);

		//right mouse click for saving attachment
		bugReportTable.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent event)
			{
				int row = bugReportTable.rowAtPoint(event.getPoint());

				if(SwingUtilities.isRightMouseButton(event))
				{
					BugReport bugReport = ((BugReportTableModel) bugReportModel).getBugReport();
					String rowName = bugReportModel.getValueAt(row, 0).toString();
					if(rowName.contains("Attach")) //attachment row
					{
						int attchNum = -1; // ordered number of an attachment
						attchNum += Integer.parseInt(rowName.replaceFirst("Attachment ", ""));
						JFileChooser chooser = new JFileChooser();
						chooser.setCurrentDirectory(new File("."));
						chooser.setDialogTitle("Save attachment");

						final ReportAttachment attch = bugReport.getAttachments().get(attchNum);
						chooser.setSelectedFile(new File(attch.getName()));
						int result = chooser.showSaveDialog(BugExploreDialog.this);

						if(result == JFileChooser.APPROVE_OPTION)
						{
							String selectedFilePath = chooser.getSelectedFile().getPath();
							try
							{
								attch.createFile(selectedFilePath);
							}
							catch (IOException e)
							{
								logger.error("Exception is ", e);
								EventQueue.invokeLater( () ->
								JOptionPane.showMessageDialog(BugExploreDialog.this,
										"There has been an error during the saving of the attachment to a file system.",
										"Error", JOptionPane.ERROR_MESSAGE));
							}
						}
					}
				}
			}
		});

		bugReportModel.addTableModelListener(new TableModelListener()
		{
			@Override
			public void tableChanged(TableModelEvent e)
			{
				int first = 0;
				int last = bugReportModel.getRowCount();
				if(EventQueue.isDispatchThread())
					updateRowHeights(first, last);
				else
					EventQueue.invokeLater(() -> updateRowHeights(first, last));
			}
		});

		Insets insets1 = new Insets(10, 5, 0, 0);
		Insets insets2 = new Insets(5, 5, 0, 0);
		JButton sendButton = new JButton("Send comment");
		JButton reloadButton = new JButton("Reload bug report");
		JButton sendReloadButton = new JButton("Send and reload");

		//sending comment
		sendButton.addActionListener(event ->
		{
			if(((BugReportTableModel) bugReportModel).getBugReport() != null)
			{
				String commentText = commentArea.getText().trim();
				if(commentText.isEmpty()) //commentText.matches("^\\s*$")
				{
					EventQueue.invokeLater( () ->
					JOptionPane.showMessageDialog(BugExploreDialog.this,
							"Comment cannot be empty or constitute of only blank characters.",
							"Warning", JOptionPane.WARNING_MESSAGE));
				}
				else
				{
					new Thread( () ->
					{
						BugReport bugReport = ((BugReportTableModel) bugReportModel).getBugReport();
						ReportComment comment = new ReportComment();
						comment.setText(commentText);
						Future<?> future = null;
						WaitingDialog waitingDialog = new WaitingDialog(BugExploreDialog.this, future);
						EventQueue.invokeLater(() -> waitingDialog.setVisible(true));

						future = parent.getClient().cdrAddBugReportComment(bugReport.getID(), comment);
						try
						{
							future.get();
							EventQueue.invokeLater(() -> waitingDialog.setVisible(false));
							parent.appendToConsole("Bug report comment has been successfully deposited to the CDR service.", Color.GREEN);
							commentArea.setText(null);

							EventQueue.invokeLater( () ->
							JOptionPane.showMessageDialog(BugExploreDialog.this,
									"Bug report comment has been successfully deposited to the CDR service.\n" +
											"You can reload the bug report to see your comment among the others.",
											"Information", JOptionPane.INFORMATION_MESSAGE));
						}
						catch(Exception e)
						{
							EventQueue.invokeLater( () ->
							{
								waitingDialog.setVisible(false);
								JOptionPane.showMessageDialog(BugExploreDialog.this,
										"There has been an error during the sending of the bug comment.\n Please try again later.",
										"Error", JOptionPane.ERROR_MESSAGE);
							});
							parent.appendToConsole("There has been an error. Bug report comment has not been deposited to the CDR service.",
									Color.RED);
						}
					}).start();
				}
			}
			else
			{
				EventQueue.invokeLater( () ->
				JOptionPane.showMessageDialog(this, "Bug report should be selected from the bug report list first!", "Warning",
						JOptionPane.WARNING_MESSAGE));
			}
		});

		//reloading bug report
		reloadButton.addActionListener(event ->
		{
			if(((BugReportTableModel) bugReportModel).getBugReport() != null)
			{
				//starting new thread so that waitingDialig could be invoked through Event Dispatch Thread
				new Thread( () ->
				{
					Future<?> future = null;
					WaitingDialog waitingDialog = new WaitingDialog(BugExploreDialog.this, future);
					EventQueue.invokeLater(() -> waitingDialog.setVisible(true));

					BugReport bugReport = ((BugReportTableModel) bugReportModel).getBugReport();
					String bugId = bugReport.getID();
					int index = selectedIndex;
					future = parent.getClient().cdrFindBug(bugId);
					try
					{
						FindBugReportResponse res = (FindBugReportResponse) future.get();
						EventQueue.invokeLater(() -> waitingDialog.setVisible(false));
						BugReport newBugReport = res.getReturn();
						if(newBugReport != null)
						{
							bugReports.set(index, newBugReport);
							((BugReportTableModel) bugReportModel).setBugReport(newBugReport);
							//bugReportTable.repaint(); //repaint doesn't work automaticaly when the list is scrolled down to the bottom
							bugReportModel.fireTableDataChanged();
							bugListModel.fireTableDataChanged();
							bugListTable.setRowSelectionInterval(selectedIndex, selectedIndex);
							parent.appendToConsole("Bug report with ID: " + bugId +
									" has been successfully retrieved from the CDR service.", Color.GREEN);
						}
						else
						{
							parent.appendToConsole("Bug report with ID: " + bugId + " does not exist on the CDR service anymore.",
									Color.BLACK);
							EventQueue.invokeLater( () ->
							{
								JOptionPane.showMessageDialog(BugExploreDialog.this,
										"Bug report with ID: " + bugId + " does not exist anymore. It wiil be deleted from the bug report list.",
										"Error", JOptionPane.ERROR_MESSAGE);
								bugReports.remove(index);
								((BugReportTableModel) bugReportModel).setBugReport(null);
								bugReportModel.fireTableDataChanged();
								bugListModel.fireTableDataChanged();
							});
						}
					}
					catch(Exception e)
					{
						EventQueue.invokeLater( () ->
						{
							waitingDialog.setVisible(false);
							JOptionPane.showMessageDialog(BugExploreDialog.this,
									"There has been an error during the retrieval of the bug report.\n Please try again later.",
									"Error", JOptionPane.ERROR_MESSAGE);
						});
						parent.appendToConsole("There has been an error. Bug report has not been retrieved from the CDR service. " +
								e.getMessage(), Color.RED);
					}
				}).start();
			}
			else
			{
				EventQueue.invokeLater( () ->
				JOptionPane.showMessageDialog(this, "Bug report should be selected from the bug report list first!", "Warning",
						JOptionPane.WARNING_MESSAGE));
			}
		});

		//sendReloadButton.addActionListener(addAction);

		JScrollPane bugReportPane = new JScrollPane(bugReportTable);
		bugReportPane.setPreferredSize(new Dimension(700, 360));
		putGridCell(bugReportPanel, 0, 0, 3, 1, insets1, bugReportPane);
		putGridCell(bugReportPanel, 1, 0, 1, 1, insets2, new JLabel("New comment:"));
		putGridCell(bugReportPanel, 1, 1, 2, 1, insets2, new JScrollPane(commentArea));
		putGridCell(bugReportPanel, 2, 0, 1, 1, insets2, sendButton);
		putGridCell(bugReportPanel, 2, 1, 1, 1, insets2, reloadButton);
		//putGridCell(bugReportPanel, 2, 2, 1, 1, insets2, sendReloadButton);

		bugReportPanel.setBorder(new TitledBorder("Selected bug report"));
		if(EventQueue.isDispatchThread())
			updateRowHeights(0, bugReportTable.getRowCount());
		else
			EventQueue.invokeLater(() -> updateRowHeights(0, bugReportTable.getRowCount()));
		return bugReportPanel;
	}

	private class BugReportCellRenderer extends DefaultTableCellRenderer
	{
		private static final long serialVersionUID = 4287741975694660153L;

		public BugReportCellRenderer()
		{
			setVerticalAlignment(SwingConstants.TOP);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int column)
		{
			BugReportTableModel tableModel = (BugReportTableModel) table.getModel();
			BugReport bugReport = tableModel.getBugReport();
			Object cellContent = tableModel.getValueAt(row, column);
			ReportComment comment = null;
			boolean nondefault = false; //whether the renderer is default one
			int lineWidth = 80;
			String text = null;
			if(cellContent != null && column == 1)
			{
				if((comment = tableModel.getComment(row)) != null) //comment rows
				{
					nondefault = true;
					text = formatText(comment.getText(), lineWidth);
				}
				else if(row == 2) //description row
				{
					nondefault = true;
					text = formatText(bugReport.getDescription(), lineWidth);
				}
			}
			if(nondefault)
				return new CellTextArea(text);
			else // all cells but ones with comment or description contents gets default renderer
			{
				DefaultTableCellRenderer renderer = (DefaultTableCellRenderer)
						super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				renderer.setVerticalAlignment(SwingConstants.TOP);
				return renderer;
			}
		}

		/**Formats input text in a way that it breaks the lines if they are longer than passed {@code width} parameter.
		 * @param text input text
		 * @param width text line's length
		 * @return formatted text
		 */
		private String formatText(String text, int width)
		{
			StringBuilder output = new StringBuilder();
			String lines[] = text.split("\n");
			for(String line: lines)
			{
				while(line.length() > width)
				{
					output.append(line.substring(0, width));
					line = line.substring(width + 1);
					output.append("\n");
				}
				output.append(line).append("\n");
			}
			output.replace(output.length()-1, output.length(), ""); //delete last new line character
			return output.toString();
		}

		private int rowCount(String text)
		{
			int cnt = 1;
			String lines[] = text.split("\n");
			cnt += lines.length;
			return cnt;
		}
	}

	/**
	 *Extends {@link JTextArea} defining component that is a renderer of cells in {@link JTable} used in {@code BugExploreDialog}.
	 *This class enables that height of the table row that contains object of it, is facilitated with the option of
	 *row height change.
	 */
	private class CellTextArea extends JTextArea
	{
		private static final long serialVersionUID = -3904298507917717991L;

		public CellTextArea(String text)
		{
			setText(text);
			setLineWrap(true);
			setWrapStyleWord(true);
		}

		@Override
		public Dimension getPreferredSize()
		{
			try
			{
				// get Rectangle for position after last text-character
				final Rectangle rectangle = this.modelToView(getDocument().getLength());
				if(rectangle != null)
					return new Dimension(this.getWidth(), this.getInsets().top + rectangle.y +
							rectangle.height + this.getInsets().bottom);
			}
			catch (BadLocationException e)
			{
				e.printStackTrace();  // TODO: implement catch
			}

			return super.getPreferredSize();
		}
	}

	/**Auto adjusts the height of rows in a JTable.
	 * @param first first row number which height is updated
	 * @param last last row number which row height is updated
	 */
	private void updateRowHeights(final int first, final int last)
	{
		/*
		 * The only way to know the row height for sure is to render each cell
		 * to determine the rendered height. After your table is populated with
		 * data you can do:
		 */
		for (int row = first; row < last; row++)
		{
			int rowHeight = 18;
			for (int column = 0; column < bugReportTable.getColumnCount(); column++)
			{
				Component comp = bugReportTable.prepareRenderer(bugReportTable.getCellRenderer(row, column), row, column);
				rowHeight = Math.max(rowHeight, comp.getPreferredSize().height);
			}
			if(rowHeight != bugReportTable.getRowHeight(row))
				bugReportTable.setRowHeight(row, rowHeight);
		}
	}

	private Component createBottomPanel()
	{
		JPanel bottomPanel = new JPanel();
		JButton newBugButton = new JButton("New bug report");
		JButton closeButton = new JButton("Close");

		newBugButton.addActionListener(event ->
		{
			//MMM: commented code below is relevant only if this dialog is closed after newBug button has been clicked
			/*			if(! commentArea.getText().trim().isEmpty())
			{
				EventQueue.invokeLater( () ->
				{
					int option = JOptionPane.showConfirmDialog(BugExploreDialog.this,
							"Comment area is not empty. If you proceed the contents will be discarded.",
							"Warning", JOptionPane.OK_CANCEL_OPTION);
					if(option == JOptionPane.OK_OPTION)
						clientFrame.sendBugReport();
				});
			}
			else*/
			new Thread(()->
			{
				parent.sendBugReport();
			}).start();
		});

		closeButton.addActionListener(event ->
		{
			if(! commentArea.getText().trim().isEmpty())
			{
				EventQueue.invokeLater( () ->
				{
					int option = JOptionPane.showConfirmDialog(BugExploreDialog.this,
							"Comment area is not empty. If you proceed the contents will be discarded.",
							"Warning", JOptionPane.OK_CANCEL_OPTION);
					if(option == JOptionPane.OK_OPTION)
					{
						parent.getClient().setBugReports(bugReports);
						setVisible(false);
					}
				});
			}
			else
			{
				parent.getClient().setBugReports(bugReports);
				setVisible(false);
			}
		});
		bottomPanel.add(newBugButton);
		bottomPanel.add(closeButton);
		return bottomPanel;
	}

	//MMM: this method should be part of some common package and be static, because it is used in many different dialogs
	private void putGridCell(JPanel panel, int row, int column, int width, int height, Insets insets, Component comp)
	{
		GridBagConstraints con = new GridBagConstraints();
		con.weightx = 0;
		con.weighty = 0;
		con.gridx = column;
		con.gridy = row;
		con.gridwidth = width;
		con.gridheight = height;
		if(insets != null)
			con.insets = insets;
		con.anchor = GridBagConstraints.NORTHWEST;
		con.fill = GridBagConstraints.NONE;
		panel.add(comp, con);
	}

	private class WaitingDialog extends JDialog
	{
		private static final long serialVersionUID = 8796227873338377731L;

		public WaitingDialog(JDialog parent, Future<?> future)
		{
			super(parent, true);
			setLocationRelativeTo(parent);
			setSize(300, 100);
			setTitle("Waiting");

			JLabel label = new JLabel("<html>Request has been sent to the CDR service.<br>Waiting for a response...</html>");
			JPanel panel = new JPanel();
			panel.add(label);
			add(panel, BorderLayout.CENTER);

			addWindowListener(new WindowAdapter()
			{
				@Override
				public void windowClosing(WindowEvent event)
				{
					if(future != null)
						future.cancel(true);
					super.windowClosing(event);
				}
			});
		}
	};
}