package rs.ruta.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import rs.ruta.common.BugReport;
import rs.ruta.common.BugReportSearchCriterion;
import rs.ruta.common.ReportComment;
import rs.ruta.common.datamapper.RutaException;
import rs.ruta.services.AddBugReportCommentResponse;
import rs.ruta.services.FindAllBugReportsResponse;
import rs.ruta.services.FindBugReportResponse;
import rs.ruta.services.SearchBugReportResponse;

public class BugExploreDialog extends JDialog
{
	private static final long serialVersionUID = -1075911023849983930L;
	private List<BugReport> bugReports;
	private AbstractTableModel bugListModel;
	private AbstractTableModel bugReportModel;
	private ClientFrame owner;
	private Future<?> future = null;
	private BugReportSearchCriterion criterion;
	private JTextArea commentArea;
	private JTable bugListTable;
	/**Index of the selected {@link BugReport} in the table displaying the list of all bug reports.
	 */
	private int selectedIndex = -1; //none is selected by default

	public BugExploreDialog(ClientFrame owner)
	{
		super(owner, true);
		this.owner = owner;
		setTitle("Explore the bugs");
		setResizable(false);
		setSize(750, 810);
		setLocationRelativeTo(owner);
		setLayout(new BorderLayout());
		commentArea = new JTextArea(5,50);
		bugReports = owner.getClient().getBugReports();
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
				owner.getClient().setBugReports(bugReports);
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
		tableColumn = tableColumnModel.getColumn(1);
		tableColumn.setPreferredWidth(200);
		tableColumnModel.getColumn(2).setPreferredWidth(30);
		tableColumnModel.getColumn(3).setPreferredWidth(25);
		tableColumnModel.getColumn(5).setPreferredWidth(85);

		//defining listener for table row selection
		ListSelectionModel selectionModel = bugListTable.getSelectionModel();
		selectionModel.addListSelectionListener(new ListSelectionListener()
		{
			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				int first = e.getFirstIndex();
				int last = e.getLastIndex();
				if (((ListSelectionModel)e.getSource()).isSelectedIndex(first))
					selectedIndex = first;
				else if (((ListSelectionModel)e.getSource()).isSelectedIndex(last))
					selectedIndex = last;
				new Thread( () ->
				{
					checkBugRecency(selectedIndex);
					((BugReportTableModel) bugReportModel).setBugReport(bugReports.get(selectedIndex));
					bugReportModel.fireTableDataChanged();
				}).start();
			}

			/**Checks whether the BugReport should be retrieved from the CDR, and sends a request if it should be.
			 * If the BugReort is complete i.e. previously retrieved from the CDR, it wiil not send new request to the CDR.
			 * @param index {@code BugReport}'s index in the table
			 */
			private void checkBugRecency(int index)
			{
				BugReport bugReport = bugReports.get(index);
				if(bugReport.getReportedBy() == null)
				{
					Future<?> future = null;
					WaitingDialog waitingDialog = new WaitingDialog(BugExploreDialog.this, future);
					EventQueue.invokeLater(() -> waitingDialog.setVisible(true));

					future = owner.getClient().cdrFindBug(bugReport.getId());
					try
					{
						FindBugReportResponse res = (FindBugReportResponse) future.get();
						EventQueue.invokeLater(() -> waitingDialog.setVisible(false));
						BugReport newBugReport = res.getReturn();
						if(newBugReport != null)
						{
							bugReports.set(index, newBugReport);
							//bugListTable.repaint();
							bugListModel.fireTableDataChanged();
							bugListTable.setRowSelectionInterval(selectedIndex, selectedIndex); //select previously selected row
							owner.appendToConsole("Bug report has been successfully retrieved from the CDR service.", Color.GREEN);
						}
						else
						{
							owner.appendToConsole("Bug report with ID: " + bugReport.getId() + " does not exist on the CDR service anymore.",
									Color.BLACK);
							EventQueue.invokeLater( () ->
							{
								JOptionPane.showMessageDialog(BugExploreDialog.this,
										"Bug report with ID: " + bugReport.getId() + " does not exist anymore. It wiil be deleted from the bug report list.",
										"Error", JOptionPane.ERROR_MESSAGE);
								bugReports.remove(index);
								bugListModel.fireTableDataChanged();
								//bugListTable.repaint();
							});
						}
					}
					catch(Exception e)
					{
						EventQueue.invokeLater( () ->
						JOptionPane.showMessageDialog(BugExploreDialog.this,
								"There has been an error during the retrieval of the bug report.\nPlease try again later.",
								"Error", JOptionPane.ERROR_MESSAGE));
						owner.appendToConsole("There has been an error. Bug report has not been retrieved from the CDR service.", Color.RED);
					}
				}
			}
		});

		Insets insets1 = new Insets(10, 5, 0, 0);
		Insets insets2 = new Insets(5, 5, 0, 0);
		Insets insets3 = new Insets(5, 5, 0, 0);
		JButton reloadListButton = new JButton("Reload bug report list");

		reloadListButton.addActionListener(event ->
		{
			new Thread( () ->
			{
				WaitingDialog waitingDialog = new WaitingDialog(this, future);
				EventQueue.invokeLater(() -> waitingDialog.setVisible(true));
				future = owner.searchBugReport(criterion);

				try
				{
					SearchBugReportResponse res = (SearchBugReportResponse) future.get();
					EventQueue.invokeLater(() -> waitingDialog.setVisible(false));
					bugReports = res.getReturn();
					if(bugReports != null && bugReports.size() != 0)
					{
						((BugListTableModel) bugListModel).setBugReports(bugReports);
						bugListModel.fireTableDataChanged();
						//bugListTable.repaint();
						//"Search request for the bug reports has been sent to the CDR service. Waiting for a response..."
						owner.appendToConsole("Bug report list has been successfully retrieved from the CDR service.", Color.GREEN);
						//							bugListModel.fireTableDataChanged();
					}
					else
						//zarro boogs found
						EventQueue.invokeLater( () ->
							JOptionPane.showMessageDialog(this, "Zarro boogs found!", "Information", JOptionPane.INFORMATION_MESSAGE));
				}
				catch(Exception e)
				{
					EventQueue.invokeLater( () ->
					JOptionPane.showMessageDialog(BugExploreDialog.this,
							"There has been an error during the retrieval of the bug report list.\n Please try again later.",
							"Error", JOptionPane.ERROR_MESSAGE));
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

	private class WaitingDialog extends JDialog
	{
		private static final long serialVersionUID = 8796227873338377731L;

		public WaitingDialog(JDialog parent, Future<?> future)
		{
			super(parent, true);
			setLocationRelativeTo(parent);
			setSize(300, 100);
			setTitle("Waiting");

			JLabel label = new JLabel("\nWaiting CDR service for a response...");
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

	private Component createBugReportPanel()
	{
		JPanel bugReportPanel = new JPanel();
		GridBagLayout grid = new GridBagLayout();
		bugReportPanel.setLayout(grid);

		JTable bugReportTable = new JTable(bugReportModel);
		bugReportTable.setFillsViewportHeight(true);
		bugReportTable.getTableHeader().setReorderingAllowed(false); //disables column reordering
		bugReportTable.setRowSelectionAllowed(false);
		bugReportTable.setColumnSelectionAllowed(false);

		//specifing preferred column and row sizes
		TableColumnModel tableColumnModel = bugReportTable.getColumnModel();
		TableColumn tableColumn = tableColumnModel.getColumn(0);
		tableColumn.setResizable(false);
		tableColumn.setMinWidth(125);
		tableColumn.setMaxWidth(125);

		//setting cell renderer
/*		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setVerticalAlignment(SwingConstants.TOP);
		tableColumn.setCellRenderer(renderer);
		tableColumnModel.getColumn(0).setCellRenderer(renderer);*/
		TableCellRenderer cellRenderer = new BugReportCellRenderer();
		tableColumnModel.getColumn(1).setCellRenderer(cellRenderer);
		tableColumnModel.getColumn(0).setCellRenderer(cellRenderer);

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
						int attchNum = -1; // ordered umber of an attachment
						attchNum += Integer.parseInt(rowName.replaceFirst("Attachment ", ""));
						JFileChooser chooser = new JFileChooser();
						chooser.setCurrentDirectory(new File("."));
						chooser.setDialogTitle("Save attachment");
						try
						{
							File attch = bugReport.getAttachments().get(attchNum).getFile();
							chooser.setSelectedFile(attch);
							int result = chooser.showSaveDialog(BugExploreDialog.this);

							if(result == JFileChooser.APPROVE_OPTION)
							{
								String selectedFilePath = chooser.getSelectedFile().getPath();
								try
								{
									Files.write(Paths.get(selectedFilePath), Files.readAllBytes(Paths.get(attch.getAbsolutePath())));
								}
								catch (Exception e)
								{
									e.printStackTrace();
									EventQueue.invokeLater( () ->
									JOptionPane.showMessageDialog(BugExploreDialog.this,
											"There has been an error during the saving of the attachment to a file system.",
											"Error", JOptionPane.ERROR_MESSAGE));
								}
							}
						}
						catch (IOException e1)
						{
							e1.printStackTrace();
							EventQueue.invokeLater( () ->
							JOptionPane.showMessageDialog(BugExploreDialog.this,
									"There has been an error during the extraction of the attachment from the bug report.",
									"Error", JOptionPane.ERROR_MESSAGE));
						}
					}
				}
			}
		});

		Insets insets1 = new Insets(10, 5, 0, 0);
		Insets insets2 = new Insets(5, 5, 0, 0);
		Insets insets3 = new Insets(5, 5, 0, 0);
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

						future = owner.getClient().cdrAddBugReportComment(bugReport.getId(), comment);
						try
						{
							AddBugReportCommentResponse res = (AddBugReportCommentResponse) future.get();
							EventQueue.invokeLater(() -> waitingDialog.setVisible(false));
							owner.appendToConsole("Bug report comment has been successfully deposited to the CDR service.", Color.GREEN);
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
							JOptionPane.showMessageDialog(BugExploreDialog.this,
									"There has been an error during sending of the bug comment.\n Please try again later.",
									"Error", JOptionPane.ERROR_MESSAGE));
							owner.appendToConsole("There has been an error. Bug report comment has not been deposited to the CDR service.",
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
				//starting new thread so that waitingDialig could be invoked through EventQueue thread
				new Thread( () ->
				{
					Future<?> future = null;
					WaitingDialog waitingDialog = new WaitingDialog(BugExploreDialog.this, future);
					EventQueue.invokeLater(() -> waitingDialog.setVisible(true));

					BugReport bugReport = ((BugReportTableModel) bugReportModel).getBugReport();
					String bugId = bugReport.getId();
					int index = selectedIndex;
					future = owner.getClient().cdrFindBug(bugReport.getId());
					try
					{
						FindBugReportResponse res = (FindBugReportResponse) future.get();
						EventQueue.invokeLater(() -> waitingDialog.setVisible(false));
						//				waitingDialog.setVisible(false);
						BugReport newBugReport = res.getReturn();
						if(newBugReport != null)
						{
							bugReports.set(index, newBugReport);
							((BugReportTableModel) bugReportModel).setBugReport(newBugReport);
							bugReportModel.fireTableDataChanged();
							//bugReportTable.repaint(); //MMM: repaint doesn't work automaticaly when the list is scrolled down to the bottom
							bugListModel.fireTableDataChanged();
							bugListTable.setRowSelectionInterval(selectedIndex, selectedIndex);
							owner.appendToConsole("Bug report with ID: " + bugId + " has been successfully retrieved from the CDR service.", Color.GREEN);
						}
						else
						{
							owner.appendToConsole("Bug report with ID: " + bugId + " does not exist on the CDR service anymore.",
									Color.BLACK);
							EventQueue.invokeLater( () ->
							{
								JOptionPane.showMessageDialog(BugExploreDialog.this,
										"Bug report with ID: " + bugId + " does not exist anymore. It wiil be deleted from the bug report list.",
										"Error", JOptionPane.ERROR_MESSAGE);
								bugReports.remove(index);
								((BugReportTableModel) bugReportModel).setBugReport(null);
								bugReportModel.fireTableDataChanged();
								//bugReportTable.repaint();
								bugListModel.fireTableDataChanged();
							});
						}
					}
					catch(Exception e)
					{
						EventQueue.invokeLater( () ->
						JOptionPane.showMessageDialog(BugExploreDialog.this,
								"There has been an error during the retrieval of the bug report.\n Please try again later.",
								"Error", JOptionPane.ERROR_MESSAGE));
						owner.appendToConsole("There has been an error. Bug report has not been retrieved from the CDR service.", Color.RED);
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

		//		sendReloadButton.addActionListener(addAction);

		JScrollPane bugReportPane = new JScrollPane(bugReportTable);
		bugReportPane.setPreferredSize(new Dimension(700, 360));
		putGridCell(bugReportPanel, 0, 0, 3, 1, insets1, bugReportPane);
		putGridCell(bugReportPanel, 1, 0, 1, 1, insets2, new JLabel("New comment:"));
		putGridCell(bugReportPanel, 1, 1, 2, 1, insets2, new JScrollPane(commentArea));
		putGridCell(bugReportPanel, 2, 0, 1, 1, insets2, sendButton);
		putGridCell(bugReportPanel, 2, 1, 1, 1, insets2, reloadButton);
		//		putGridCell(bugReportPanel, 3, 2, 1, 1, insets2, sendReloadButton);

		bugReportPanel.setBorder(new TitledBorder("Selected bug report"));
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
			if(cellContent != null && column == 1 /*&& row >= tableModel.getFixedRowCount()*/ //optimizations: rows 0-15 are certainly not comment rows
					&& (comment = tableModel.getComment(row)) != null) //only comment rows
			{
				JTextArea area = new JTextArea();
				String text = formatTextArea(comment.getText(), 80);
				area.setText(text);

				//setting row height
				EventQueue.invokeLater( () ->
				{
					int fontHeight = this.getFontMetrics(this.getFont()).getHeight();
					int lines = rowCount(text);
					int height = fontHeight * lines;
					table.setRowHeight(row, height);
				});
				return area;
			}
			else // all cells but with comment text
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
		private String formatTextArea(String text, int width)
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

	private Component createBottomPanel()
	{
		JPanel bottomPanel = new JPanel();
		JButton newBugButton = new JButton("New bug report");
		JButton closeButton = new JButton("Close");

		newBugButton.addActionListener(event ->
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
						//						setVisible(false);
						owner.sendBugReport();
					}
				});
			}
			else
			{
				//				setVisible(false);
				owner.sendBugReport();
			}
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
						owner.getClient().setBugReports(bugReports);
					}
				});
			}
			owner.getClient().setBugReports(bugReports);
			setVisible(false);
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
}
