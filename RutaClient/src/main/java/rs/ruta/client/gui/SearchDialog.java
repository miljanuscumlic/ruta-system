package rs.ruta.client.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.List;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import oasis.names.specification.ubl.schema.xsd.catalogue_21.CatalogueType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import rs.ruta.client.CatalogueSearch;
import rs.ruta.client.PartySearch;
import rs.ruta.client.Search;
import rs.ruta.common.CatalogueSearchCriterion;
import rs.ruta.common.PartySearchCriterion;
import rs.ruta.common.SearchCriterion;

public class SearchDialog extends JDialog
{
	private static final long serialVersionUID = -5806194025786533572L;

	private JTextField sNameField;

	private JTextField pNameField;
	private JTextField pIndustryClassCodeField;
	private JTextField pCompanyIDField;
	private JTextField pCityField;
	private JTextField pCountryField;
	private JRadioButton pAll;
	private JRadioButton pAny;

	private JTextField iNameField;
	private JTextField iDescriptionField;
	private JTextField iBarcodeField;
	private JTextField iCommCodeField;
	private JTextField iKeywordField;
	private JRadioButton iAll;
	private JRadioButton iAny;

	private Search<?> search;
	private String searchName;
	/**
	 * true if search button has been pressed
	 */
	private boolean searchPressed;

	public SearchDialog(RutaClientFrame owner)
	{
		super(owner, true);
		setResizable(false);
		searchPressed = false;

		setSize(500, 420);
		setLocationRelativeTo(owner);

		int width = 20;
		sNameField = new JTextField(width);
		sNameField.setText(Search.getNextSearchName());

		addComponentListener(new ComponentAdapter()
		{
			@Override
			public void componentShown(ComponentEvent e)
			{
				sNameField.requestFocusInWindow();
				sNameField.selectAll();
			}
		});

		pNameField = new JTextField(width);
		pCompanyIDField = new JTextField(width);
		pIndustryClassCodeField = new JTextField(width);
		pCityField = new JTextField(width);
		pCountryField = new JTextField(width);

		iNameField = new JTextField(width);
		iDescriptionField = new JTextField(width);
		iBarcodeField = new JTextField(width);
		iCommCodeField = new JTextField(width);
		iKeywordField = new JTextField(width);

		JPanel searchPartyPanel = new JPanel();

		searchPartyPanel.setLayout(new BorderLayout());
		searchPartyPanel.add(createSearchNamePanel(), BorderLayout.NORTH);
		searchPartyPanel.add(createPartyPanel(), BorderLayout.CENTER);
		add(searchPartyPanel, BorderLayout.NORTH);
		add(createItemPanel(), BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel();

		JButton searchButton = new JButton("Search");
		buttonPanel.add(searchButton);
		JButton cancelButton = new JButton("Cancel");
		buttonPanel.add(cancelButton);
		getRootPane().setDefaultButton(searchButton);

		searchButton.addActionListener(event ->
		{
			searchPressed = true;
			searchName = sNameField.getText();
			SearchCriterion searchCriterion;
			if(isCatalogueSearchedFor())
			{
				search = new CatalogueSearch();//Search<CatalogueType>();
				((CatalogueSearch) search).setResultType(CatalogueType.class);
				CatalogueSearchCriterion criterion = new CatalogueSearchCriterion();
				criterion.setPartyName(pNameField.getText());
				criterion.setPartyCompanyID(pCompanyIDField.getText());
				criterion.setPartyClassCode(pIndustryClassCodeField.getText());
				criterion.setPartyCity(pCityField.getText());
				criterion.setPartyCountry(pCountryField.getText());
				criterion.setPartyAll(pAll.isSelected());

				criterion.setItemName(iNameField.getText());
				criterion.setItemDescription(iDescriptionField.getText());
				criterion.setItemBarcode(iBarcodeField.getText());
				criterion.setItemCommCode(iCommCodeField.getText());
				criterion.setItemKeyword(iKeywordField.getText());
				criterion.setItemAll(iAll.isSelected());
				searchCriterion = criterion;
			}
			else
			{
				search = new PartySearch();//Search<PartyType>();
				((PartySearch) search).setResultType(PartyType.class);
				PartySearchCriterion criterion = new PartySearchCriterion();
				criterion.setPartyName(pNameField.getText());
				criterion.setPartyCompanyID(pCompanyIDField.getText());
				criterion.setPartyClassCode(pIndustryClassCodeField.getText());
				criterion.setPartyCity(pCityField.getText());
				criterion.setPartyCountry(pCountryField.getText());
				criterion.setPartyAll(pAll.isSelected());
				searchCriterion = criterion;
			}
			search.setCriterion(searchCriterion.nullEmptyFields());
			search.setId();
			search.setSearchName(searchName);
//			search.setTimestamp();
			setVisible(false);
		});

		cancelButton.addActionListener(event ->
		{
			Search.decreaseSearchNumber();
			setVisible(false);
		});
		add(buttonPanel, BorderLayout.SOUTH);

		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent event)
			{
				Search.decreaseSearchNumber();
				setVisible(false);
			}
		});
	}

	/**
	 * Checks whether the search is supposed to query Catalogues. If not the Parties are only queried.
	 * @return true if Catalogues should be queried
	 */
	private boolean isCatalogueSearchedFor()
	{
		return !iNameField.getText().trim().equals("") ||
				!iDescriptionField.getText().trim().equals("") ||
				!iBarcodeField.getText().trim().equals("") ||
				!iCommCodeField.getText().trim().equals("") ||
				!iKeywordField.getText().trim().equals("");
	}

	private JPanel createSearchNamePanel()
	{
		JPanel searchNamePanel = new JPanel();
		GridBagLayout grid = new GridBagLayout();
		searchNamePanel.setLayout(grid);

		Insets insets = new Insets(10, 0, 10, 0);
		putGridCell(searchNamePanel, 0, 0, 1, 1, insets, new JLabel("Search name: ", SwingConstants.LEFT));
		putGridCell(searchNamePanel, 0, 1, 1, 1, insets, sNameField);

		return searchNamePanel;
	}

	private JPanel createPartyPanel()
	{
		JPanel partyPanel = new JPanel();
		GridBagLayout grid = new GridBagLayout();
		partyPanel.setLayout(grid);
		JPanel radioPanel = new JPanel();
		ButtonGroup radioGroup = new ButtonGroup();
		pAll = new JRadioButton("match all of the following", true);
		radioGroup.add(pAll);
		radioPanel.add(pAll);
		pAny = new JRadioButton("match any of the following", false);
		radioGroup.add(pAny);
		radioPanel.add(pAny);

		putGridCell(partyPanel, 0, 0, 2, 1, null, radioPanel);
		putGridCell(partyPanel, 1, 0, 1, 1, null, new JLabel("Name: ", SwingConstants.LEFT));
		putGridCell(partyPanel, 1, 1, 1, 1, null, pNameField);
		putGridCell(partyPanel, 2, 0, 1, 1, null, new JLabel("Company ID: ", SwingConstants.LEFT));
		putGridCell(partyPanel, 2, 1, 1, 1, null, pCompanyIDField);
		putGridCell(partyPanel, 3, 0, 1, 1, null, new JLabel("Industry Classification Code: ", SwingConstants.LEFT));
		putGridCell(partyPanel, 3, 1, 1, 1, null, pIndustryClassCodeField);
		putGridCell(partyPanel, 4, 0, 1, 1, null, new JLabel("City: ", SwingConstants.LEFT));
		putGridCell(partyPanel, 4, 1, 1, 1, null, pCityField);
		putGridCell(partyPanel, 5, 0, 1, 1, null, new JLabel("Country: ", SwingConstants.LEFT));
		putGridCell(partyPanel, 5, 1, 1, 1, null, pCountryField);

		partyPanel.setBorder(new TitledBorder("Party"));

		return partyPanel;
	}

	private JPanel createItemPanel()
	{
		JPanel itemPanel = new JPanel();
		GridBagLayout grid = new GridBagLayout();
		itemPanel.setLayout(grid);
		JPanel radioPanel = new JPanel();
		ButtonGroup radioGroup = new ButtonGroup();
		iAll = new JRadioButton("match all of the following", true);
		radioGroup.add(iAll);
		radioPanel.add(iAll);
		iAny = new JRadioButton("match any of the following", false);
		radioGroup.add(iAny);
		radioPanel.add(iAny);

		putGridCell(itemPanel, 0, 0, 2, 1, null, radioPanel);
		putGridCell(itemPanel, 1, 0, 1, 1, null, new JLabel("Name: ", SwingConstants.LEFT));
		putGridCell(itemPanel, 1, 1, 1, 1, null, iNameField);
		putGridCell(itemPanel, 2, 0, 1, 1, null, new JLabel("Description: ", SwingConstants.LEFT));
		putGridCell(itemPanel, 2, 1, 1, 1, null, iDescriptionField);
		putGridCell(itemPanel, 3, 0, 1, 1, null, new JLabel("Barcode: ", SwingConstants.LEFT));
		putGridCell(itemPanel, 3, 1, 1, 1, null, iBarcodeField);
		putGridCell(itemPanel, 4, 0, 1, 1, null, new JLabel("Commodity Code: ", SwingConstants.LEFT));
		putGridCell(itemPanel, 4, 1, 1, 1, null, iCommCodeField);
		putGridCell(itemPanel, 5, 0, 1, 1, null, new JLabel("Keyword: ", SwingConstants.LEFT));
		putGridCell(itemPanel, 5, 1, 1, 1, null, iKeywordField);

		itemPanel.setBorder(new TitledBorder("Item"));

		return itemPanel;
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
		con.anchor = GridBagConstraints.EAST;
		con.fill = GridBagConstraints.BOTH;
		panel.add(comp, con);
	}

	public Search<?> getSearch()
	{
		return search;
	}

	/**
	 * Checks whether the {@code Search} button has been pressed.
	 * @return true if the {@code Search} button has been pressed
	 */
	public boolean isSearchPressed()
	{
		return searchPressed;
	}

	public void setSearchPressed(boolean registerPressed)
	{
		this.searchPressed = registerPressed;
	}

	public String getSearchName()
	{
		return searchName;
	}

	public void setSearchName(String searchName)
	{
		this.searchName = searchName;
	}
}
