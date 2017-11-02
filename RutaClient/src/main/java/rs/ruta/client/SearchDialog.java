package rs.ruta.client;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.*;
import javax.swing.border.TitledBorder;

import rs.ruta.common.SearchCriterion;

public class SearchDialog extends JDialog
{
	private static final long serialVersionUID = -5806194025786533572L;
	private String partyName;
	private String partyCompanyID;
	private String partyClassCode;
	private String partyCity;
	private String partyCountry;

	private JTextField pNameField;
	private JTextField pIndustryClassCodeField;
	private JTextField pCompanyIDField;
	private JTextField pCityField;
	private JTextField pCountryField;
	private JRadioButton pAll;
	private JRadioButton pAny;

	private String itemName;
	private String itemBarcode;
	private String itemCommCode;

	private JTextField iNameField;
	private JTextField iBarcodeField;
	private JTextField iCommCodeField;
	private JRadioButton iAll;
	private JRadioButton iAny;

	private SearchCriterion criterion;

	private boolean searchPressed; // true if the sign up button were pressed

	public SearchDialog(ClientFrame owner)
	{
		super(owner, true);
		searchPressed = false;
		//partyName = partyCompanyID = partyClassCode = partyCity = partyCountry = null;
		criterion = new SearchCriterion();
		setSize(500, 350);
		setLocationRelativeTo(owner);

		int width = 20;
		pNameField = new JTextField(width);
		pCompanyIDField = new JTextField(width);
		pIndustryClassCodeField = new JTextField(width);
		pCityField = new JTextField(width);
		pCountryField = new JTextField(width);

		iNameField = new JTextField(width);
		iBarcodeField = new JTextField(width);
		iCommCodeField = new JTextField(width);

		add(getPartyPanel(), BorderLayout.NORTH);
		add(getItemPanel(), BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel();

		JButton searchButton = new JButton("Search");
		buttonPanel.add(searchButton);
		searchButton.addActionListener(event ->
		{
			searchPressed = true;
			criterion.setPartyName(pNameField.getText());
			criterion.setPartyCompanyID(pCompanyIDField.getText());
			criterion.setPartyClassCode(pIndustryClassCodeField.getText());
			criterion.setPartyCity(pCityField.getText());
			criterion.setPartyCountry(pCountryField.getText());
			criterion.setPartyAll(pAll.isSelected());

			criterion.setItemName(iNameField.getText());
			criterion.setItemBarcode(iBarcodeField.getText());
			criterion.setItemCommCode(iCommCodeField.getText());
			criterion.setItemAll(iAll.isSelected());

			setVisible(false);
		});

		JButton cancelButton = new JButton("Cancel");
		buttonPanel.add(cancelButton);
		cancelButton.addActionListener(event ->
		{
			setVisible(false);
		});

		add(buttonPanel, BorderLayout.SOUTH);
	}

	private JPanel getPartyPanel()
	{
		JPanel partyPanel = new JPanel();
		GridBagLayout grid = new GridBagLayout();
		partyPanel.setLayout(grid);
		JPanel radioPanel = new JPanel();
		ButtonGroup group = new ButtonGroup();
		pAll = new JRadioButton("Match all of the following", true);
		group.add(pAll);
		radioPanel.add(pAll);
		pAny = new JRadioButton("Match any of the following", false);
		group.add(pAny);
		radioPanel.add(pAny);

		putGridCell(partyPanel, 0, 0, 2, 1, radioPanel);
		putGridCell(partyPanel, 1, 0, 1, 1, new JLabel("Name: ", SwingConstants.LEFT));
		putGridCell(partyPanel, 1, 1, 1, 1, pNameField);
		putGridCell(partyPanel, 2, 0, 1, 1, new JLabel("Company ID: ", SwingConstants.LEFT));
		putGridCell(partyPanel, 2, 1, 1, 1, pCompanyIDField);
		putGridCell(partyPanel, 3, 0, 1, 1, new JLabel("Industry Classification Code: ", SwingConstants.LEFT));
		putGridCell(partyPanel, 3, 1, 1, 1, pIndustryClassCodeField);
		putGridCell(partyPanel, 4, 0, 1, 1, new JLabel("City: ", SwingConstants.LEFT));
		putGridCell(partyPanel, 4, 1, 1, 1, pCityField);
		putGridCell(partyPanel, 5, 0, 1, 1, new JLabel("Country: ", SwingConstants.LEFT));
		putGridCell(partyPanel, 5, 1, 1, 1, pCountryField);

		partyPanel.setBorder(new TitledBorder("Party"));

		return partyPanel;
	}
	private JPanel getItemPanel()
	{
		JPanel itemPanel = new JPanel();
		GridBagLayout grid = new GridBagLayout();
		itemPanel.setLayout(grid);
		JPanel radioPanel = new JPanel();
		ButtonGroup group = new ButtonGroup();
		iAll = new JRadioButton("Match all of the following", true);
		group.add(iAll);
		radioPanel.add(iAll);
		iAny = new JRadioButton("Match any of the following", false);
		group.add(iAny);
		radioPanel.add(iAny);

		putGridCell(itemPanel, 0, 0, 2, 1, radioPanel);
		putGridCell(itemPanel, 1, 0, 1, 1, new JLabel("Name: ", SwingConstants.LEFT));
		putGridCell(itemPanel, 1, 1, 1, 1, iNameField);
		putGridCell(itemPanel, 2, 0, 1, 1, new JLabel("Barcode: ", SwingConstants.LEFT));
		putGridCell(itemPanel, 2, 1, 1, 1, iBarcodeField);
		putGridCell(itemPanel, 3, 0, 1, 1, new JLabel("Commodity Code: ", SwingConstants.LEFT));
		putGridCell(itemPanel, 3, 1, 1, 1, iCommCodeField);

		itemPanel.setBorder(new TitledBorder("Item"));

		return itemPanel;
	}

	public void putGridCell(JPanel panel, int row, int column, int width, int height, Component comp)
	{
		GridBagConstraints con = new GridBagConstraints();
		con.weightx = 0;
		con.weighty = 0;
		con.gridx = column;
		con.gridy = row;
		con.gridwidth = width;
		con.gridheight = height;
		con.anchor = GridBagConstraints.EAST;
		con.fill = GridBagConstraints.BOTH;
		panel.add(comp, con);
	}

	public String getPartyName()
	{
		return partyName;
	}

	public void setPartyName(String username)
	{
		this.partyName = username;
	}

	public String getPartyCompanyID()
	{
		return partyCompanyID;
	}

	public void setPartyCompanyID(String password)
	{
		this.partyCompanyID = password;
	}

	/**Checks if the Search button is pressed
	 * @return true if the Search button is pressed
	 */
	public boolean isSearchPressed()
	{
		return searchPressed;
	}

	public void setSearchPressed(boolean registerPressed)
	{
		this.searchPressed = registerPressed;
	}

	public SearchCriterion getCriterion()
	{
		return criterion;
	}
}
