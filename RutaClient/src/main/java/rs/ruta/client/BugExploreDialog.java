package rs.ruta.client;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.concurrent.Future;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import rs.ruta.common.BugReport;
import rs.ruta.common.datamapper.RutaException;
import rs.ruta.services.FindAllBugReportsResponse;

public class BugExploreDialog extends JDialog
{
	private static final long serialVersionUID = -1075911023849983930L;
	private List<BugReport> bugReports;
	private AbstractTableModel bugListModel;
	private AbstractTableModel bugReportModel;
	private ClientFrame owner;
	private Future<?> future = null;
	/**Index of the selected {@link BugReport} in the table displaying the list of all bug reports.
	 */
	private int selectedBugRow = -1; // default is that none is selected

	public BugExploreDialog(ClientFrame owner)
	{
		super(owner, true);
		this.owner = owner;
		setTitle("Explore the bugs");
		setResizable(true);
		setSize(700, 850);
		setLocationRelativeTo(owner);
		setLayout(new BorderLayout());
		bugReports = owner.getClient().getBugReports();

		bugListModel = new BugListTableModel(bugReports);
		add(createBugListPanel(), BorderLayout.NORTH);

		bugReportModel = new BugReportTableModel();
		add(createBugReportPanel(), BorderLayout.CENTER);

		add(createBottomPanel(), BorderLayout.SOUTH);

/*		JPanel dialogPanel = new JPanel();
		GridBagLayout grid = new GridBagLayout();
		dialogPanel.setLayout(grid);
		dialogPanel.setSize(this.getMaximumSize());

		bugListModel = new BugListTableModel(bugReports);
		bugReportModel = new BugReportTableModel();
		putGridCell(dialogPanel, 0, 0, 1, 1, null, createBugListPanel());
		putGridCell(dialogPanel, 1, 0, 1, 1, null, createBugReportPanel());
		putGridCell(dialogPanel, 2, 0, 1, 1, null, createBottomPanel());

		add(dialogPanel);*/

	}

	private JPanel createBugListPanel()
	{
		JPanel bugListPanel = new JPanel();
		GridBagLayout grid = new GridBagLayout();
		bugListPanel.setLayout(grid);

		JTable bugListTable = new JTable(bugListModel);
		bugListTable.setFillsViewportHeight(true);
		bugListTable.getTableHeader().setReorderingAllowed(false); //disables column reordering
		bugListTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		ListSelectionModel selectionModel = bugListTable.getSelectionModel();
		selectionModel.addListSelectionListener(new ListSelectionListener()
		{
			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				int first = e.getFirstIndex();
				int last = e.getLastIndex();
				if (((ListSelectionModel)e.getSource()).isSelectedIndex(first))
					selectedBugRow = first;
				else if (((ListSelectionModel)e.getSource()).isSelectedIndex(last))
					selectedBugRow = last;
				//MMM: here should be called method that checks weather the selected BugReport should be retrieved from the CDR and then retrieve it
				((BugReportTableModel) bugReportModel).setBugReport(bugReports.get(selectedBugRow));
				bugReportModel.fireTableDataChanged();
			}
		});

		//specifing preferred column and row sizes
		TableColumnModel tableColumnModel = bugListTable.getColumnModel();
		TableColumn tableColumn = tableColumnModel.getColumn(0);
		tableColumn.setResizable(false);
		tableColumn = tableColumnModel.getColumn(1);
		tableColumn.setPreferredWidth(200);

		Insets insets1 = new Insets(10, 5, 0, 0);
		Insets insets2 = new Insets(5, 5, 0, 0);
		Insets insets3 = new Insets(5, 5, 0, 0);
		JButton firstBugsButton = new JButton("First");
		JButton prevBugsButton = new JButton("Previous");
		JButton nextBugsButton = new JButton("Next");
		JButton lastBugsButton = new JButton("Last");
		JButton reloadButton = new JButton("Reload list");

/*		ActionListener addAction = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new File("."));
				chooser.setDialogTitle("Add Attachment");
				int result = chooser.showOpenDialog(BugReportDialog.this);
				if(result == JFileChooser.APPROVE_OPTION)
				{
					try
					{
						File selectedFile = chooser.getSelectedFile();

						if((JButton)(event.getSource()) == prevBugsButton)
						{
							att1 = new ReportAttachment(selectedFile);
							att1Field.setText(chooser.getSelectedFile().getName());
							bugListPanel.remove(prevBugsButton);
							putGridCell(bugListPanel, 9, 0, 1, 1, insets2, remAtt1Button);
							bugListPanel.revalidate();
							bugListPanel.repaint();
						}
						else
						{
							att2 = new ReportAttachment(selectedFile);
							att2Field.setText(chooser.getSelectedFile().getName());
							bugListPanel.remove(nextBugsButton);
							putGridCell(bugListPanel, 10, 0, 1, 1, insets2, remAtt2Button);
							bugListPanel.revalidate();
							bugListPanel.repaint();
						}
					}
					catch (IOException e)
					{
						JOptionPane.showMessageDialog(BugReportDialog.this, "Attachment could not be appended to a Bug Report!",
								"Error Message", JOptionPane.ERROR_MESSAGE);
						//e.printStackTrace();
					}
				}
			}
		};
		prevBugsButton.addActionListener(addAction);
		nextBugsButton.addActionListener(addAction);

		ActionListener removeAction = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				if(event.getSource() == remAtt1Button)
				{
					att1 = null;
					att1Field.setText(null);
					bugListPanel.remove(remAtt1Button);
					putGridCell(bugListPanel, 9, 0, 1, 1, insets2, prevBugsButton);
					bugListPanel.revalidate();
					bugListPanel.repaint();
				}
				else //remAtt2Button
				{
					att2 = null;
					att2Field.setText(null);
					bugListPanel.remove(remAtt2Button);
					putGridCell(bugListPanel, 10, 0, 1, 1, insets2, nextBugsButton);
					bugListPanel.revalidate();
					bugListPanel.repaint();
				}
			}
		};*/

		reloadButton.addActionListener(event ->
		{
			try
			{
				new Thread( () ->
				{
					//MMM: here should be displayed dialog with the information about waiting for a response

/*					EventQueue.invokeLater( () ->
							JOptionPane.showMessageDialog(BugExploreDialog.this,
							"Request for the bug report list has been sent to the CDR service.\nWaiting for a response...",
							"Information", JOptionPane.INFORMATION_MESSAGE));*/

					future = owner.findAllBugs();
					try
					{
						FindAllBugReportsResponse res = (FindAllBugReportsResponse) future.get();
						bugReports = res.getReturn();
						if(bugReports != null)
						{
							((BugListTableModel) bugListModel).setBugReports(bugReports);
							bugListTable.repaint();
//							bugListModel.fireTableDataChanged();
						}
					}
					catch(Exception e)
					{
						EventQueue.invokeLater( () ->
						JOptionPane.showMessageDialog(BugExploreDialog.this,
								"There has been an error during the retrieval of the bug report list.\n Please try again later.",
								"Error", JOptionPane.ERROR_MESSAGE));
					}
				}).start();
			}
			catch(Exception e)
			{
				EventQueue.invokeLater( () ->
				JOptionPane.showMessageDialog(BugExploreDialog.this,
						"There has been an error during update check.\n Please try again later.",
						"Error", JOptionPane.ERROR_MESSAGE));
			}

		});

		JScrollPane bugListPane = new JScrollPane(bugListTable);
		bugListPane.setPreferredSize(new Dimension(650, 150));
		putGridCell(bugListPanel, 0, 0, 1, 1, insets1, bugListPane);
/*		putGridCell(bugListPanel, 1, 0, 1, 1, insets2, firstBugsButton);
		putGridCell(bugListPanel, 1, 1, 1, 1, insets2, prevBugsButton);
		putGridCell(bugListPanel, 1, 2, 1, 1, insets2, nextBugsButton);
		putGridCell(bugListPanel, 1, 3, 1, 1, insets2, lastBugsButton);*/
		putGridCell(bugListPanel, 1, 0, 1, 1, insets2, reloadButton);

		bugListPanel.setBorder(new TitledBorder("Bug report list"));
		return bugListPanel;
	}

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
		tableColumn.setMinWidth(110);
		tableColumn.setMaxWidth(130);
		bugReportTable.setRowHeight(2, 60);
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setVerticalAlignment(SwingConstants.TOP);
		tableColumn.setCellRenderer(renderer);
		tableColumnModel.getColumn(1).setCellRenderer(renderer);

		Insets insets1 = new Insets(10, 5, 0, 0);
		Insets insets2 = new Insets(5, 5, 0, 0);
		Insets insets3 = new Insets(5, 5, 0, 0);
		JButton sendButton = new JButton("Send comment");
		JButton reloadButton = new JButton("Reload bug");
		JButton sendReloadButton = new JButton("Send and reload");
		JTextArea commentArea = new JTextArea(5,58);

		/*ActionListener addAction = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new File("."));
				chooser.setDialogTitle("Add Attachment");
				int result = chooser.showOpenDialog(BugReportDialog.this);
				if(result == JFileChooser.APPROVE_OPTION)
				{
					try
					{
						File selectedFile = chooser.getSelectedFile();

						if((JButton)(event.getSource()) == reloadButton)
						{
							att1 = new ReportAttachment(selectedFile);
							att1Field.setText(chooser.getSelectedFile().getName());
							bugReportPanel.remove(reloadButton);
							putGridCell(bugReportPanel, 9, 0, 1, 1, insets2, remAtt1Button);
							bugReportPanel.revalidate();
							bugReportPanel.repaint();
						}
						else
						{
							att2 = new ReportAttachment(selectedFile);
							att2Field.setText(chooser.getSelectedFile().getName());
							bugReportPanel.remove(sendReloadButton);
							putGridCell(bugReportPanel, 10, 0, 1, 1, insets2, remAtt2Button);
							bugReportPanel.revalidate();
							bugReportPanel.repaint();
						}
					}
					catch (IOException e)
					{
						JOptionPane.showMessageDialog(BugReportDialog.this, "Attachment could not be appended to a Bug Report!",
								"Error Message", JOptionPane.ERROR_MESSAGE);
						//e.printStackTrace();
					}
				}
			}
		};

		sendButton.addActionListener(addAction);
		reloadButton.addActionListener(addAction);
		sendReloadButton.addActionListener(addAction);*/

/*		ActionListener removeAction = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				if(event.getSource() == remAtt1Button)
				{
					att1 = null;
					att1Field.setText(null);
					bugReportPanel.remove(remAtt1Button);
					putGridCell(bugReportPanel, 9, 0, 1, 1, insets2, reloadButton);
					bugReportPanel.revalidate();
					bugReportPanel.repaint();
				}
				else //remAtt2Button
				{
					att2 = null;
					att2Field.setText(null);
					bugReportPanel.remove(remAtt2Button);
					putGridCell(bugReportPanel, 10, 0, 1, 1, insets2, sendReloadButton);
					bugReportPanel.revalidate();
					bugReportPanel.repaint();
				}
			}
		};

		remAtt1Button.addActionListener(removeAction);
		remAtt2Button.addActionListener(removeAction);*/

		JScrollPane bugReportPane = new JScrollPane(bugReportTable);
		bugReportPane.setPreferredSize(new Dimension(650, 360));
		putGridCell(bugReportPanel, 0, 0, 3, 1, insets1, bugReportPane);
		putGridCell(bugReportPanel, 1, 0, 3, 1, insets2, new JLabel("New comment:"));
		putGridCell(bugReportPanel, 2, 0, 3, 1, insets2, new JScrollPane(commentArea));
		putGridCell(bugReportPanel, 3, 0, 1, 1, insets2, sendButton);
		putGridCell(bugReportPanel, 3, 1, 1, 1, insets2, reloadButton);
		putGridCell(bugReportPanel, 3, 2, 1, 1, insets2, sendReloadButton);

		bugReportPanel.setBorder(new TitledBorder("Selected bug report"));
		return bugReportPanel;
	}

	private Component createBottomPanel()
	{
		JPanel bottomPanel = new JPanel();
		JButton newBugButton = new JButton("New bug report");
		JButton closeButton = new JButton("Close");

		newBugButton.addActionListener(event ->
		{
			setVisible(false);
			owner.reportBug();
		});

		closeButton.addActionListener(event ->
		{
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
			con.anchor = GridBagConstraints.WEST;
			con.fill = GridBagConstraints.NONE;
			panel.add(comp, con);
		}
}
