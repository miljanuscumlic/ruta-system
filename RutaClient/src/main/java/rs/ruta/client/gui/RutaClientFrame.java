package rs.ruta.client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Properties;
import java.util.concurrent.Future;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oasis.names.specification.ubl.schema.xsd.applicationresponse_21.ApplicationResponseType;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;
import oasis.names.specification.ubl.schema.xsd.order_21.OrderType;
import oasis.names.specification.ubl.schema.xsd.ordercancellation_21.OrderCancellationType;
import oasis.names.specification.ubl.schema.xsd.orderchange_21.OrderChangeType;
import oasis.names.specification.ubl.schema.xsd.orderresponse_21.OrderResponseType;
import oasis.names.specification.ubl.schema.xsd.orderresponsesimple_21.OrderResponseSimpleType;
import rs.ruta.client.Item;
import rs.ruta.client.MyParty;
import rs.ruta.client.Party;
import rs.ruta.client.RutaClient;
import rs.ruta.client.Search;
import rs.ruta.client.correspondence.Correspondence;
import rs.ruta.common.BugReport;
import rs.ruta.common.BugReportSearchCriterion;
import rs.ruta.common.SearchCriterion;
import rs.ruta.common.datamapper.DatabaseException;
import rs.ruta.common.datamapper.DetailException;
import rs.ruta.services.RutaException;

public class RutaClientFrame extends JFrame implements ActionListener
{
	private static final long serialVersionUID = -6582749886269431483L;
	private static final String DEFAULT_WIDTH = "1000"; //$NON-NLS-1$
	private static final String DEFAULT_HEIGHT = "800"; //$NON-NLS-1$
	private static final Logger logger = LoggerFactory.getLogger("rs.ruta.client"); //$NON-NLS-1$
	public static final int TAB_PRODUCTS = 0;
	public static final int TAB_CORRESPONDENSCES = 1;
	public static final int TAB_CDR_DATA = 2;

	private RutaClient client;
	private JTabbedPane tabbedPane;
	private JSplitPane splitPane;
	private JTextPane consolePane;
	private AboutDialog aboutDialog;
	private UpdateDialog updateDialog;
	private PartyDialog partyDialog;
	private ProductDialog productDialog;
	private RegisterDialog registerDialog;
	private SearchDialog searchDialog;
	private CDRSettingsDialog settingsDialog;
	private BugReportDialog bugReportDialog;
	private BugExploreDialog bugExploreDialog;
	private JFileChooser chooser;

	private JMenuItem myPartyItem = new JMenuItem(Messages.getString("RutaClientFrame.3")); //$NON-NLS-1$
	private JMenuItem myCatalogueItem = new JMenuItem(Messages.getString("RutaClientFrame.4")); //$NON-NLS-1$
	private JMenuItem newProductItem = new JMenuItem(Messages.getString("RutaClientFrame.5")); //$NON-NLS-1$
	private JMenuItem saveDataItem = new JMenuItem(Messages.getString("RutaClientFrame.6")); //$NON-NLS-1$
	private JMenuItem localRegisterPartyItem = new JMenuItem(Messages.getString("RutaClientFrame.7")); //$NON-NLS-1$
	private JMenuItem localDeregisterPartyItem = new JMenuItem(Messages.getString("RutaClientFrame.8")); //$NON-NLS-1$
	private JMenuItem exitItem = new JMenuItem(Messages.getString("RutaClientFrame.9")); //$NON-NLS-1$

	private JMenuItem cdrGetDocumentsItem = new JMenuItem(Messages.getString("RutaClientFrame.10")); //$NON-NLS-1$
	private JMenuItem cdrSearchItem = new JMenuItem(Messages.getString("RutaClientFrame.11")); //$NON-NLS-1$
	private JMenuItem cdrUpdateCatalogueItem = new JMenuItem(Messages.getString("RutaClientFrame.12")); //$NON-NLS-1$
	private JMenuItem cdrDeleteCatalogueItem = new JMenuItem(Messages.getString("RutaClientFrame.13")); //$NON-NLS-1$
	private JMenuItem cdrUpdatePartyItem = new JMenuItem(Messages.getString("RutaClientFrame.14")); //$NON-NLS-1$
	private JMenuItem cdrRegisterPartyItem = new JMenuItem(Messages.getString("RutaClientFrame.15")); //$NON-NLS-1$
	private JMenuItem cdrDeregisterPartyItem = new JMenuItem(Messages.getString("RutaClientFrame.16")); //$NON-NLS-1$
	private JMenuItem cdrSettingsItem = new JMenuItem(Messages.getString("RutaClientFrame.17")); //$NON-NLS-1$

	private TabComponent tabCDR;
	private TabComponent tabProducts;
	private TabComponent tabCorrespondences;

	public RutaClientFrame() {}

	public void setClient(RutaClient client)
	{
		this.client = client;
	}

	public void initialize()
	{
		final MyParty myParty = client.getMyParty();

		//get frame related properties
		final Properties properties = client.getProperties();
		final int left = Integer.parseInt(properties.getProperty("mainFrame.left", "0")); //$NON-NLS-1$ //$NON-NLS-2$
		final int top = Integer.parseInt(properties.getProperty("mainFrame.top", "0")); //$NON-NLS-1$ //$NON-NLS-2$
		int width = Integer.parseInt(properties.getProperty("mainFrame.width", DEFAULT_WIDTH)); //$NON-NLS-1$
		int height = Integer.parseInt(properties.getProperty("mainFrame.height", DEFAULT_HEIGHT)); //$NON-NLS-1$
		setBounds(left, top, width, height);
		setTitle(Messages.getString("RutaClientFrame.24") + client.getMyParty().getPartySimpleName()); //$NON-NLS-1$

		chooser = new JFileChooser();
		final FileFilter filter = new FileNameExtensionFilter("XML files", "xml"); //$NON-NLS-1$ //$NON-NLS-2$
		chooser.setFileFilter(filter);

		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent event)
			{
				dispatchFalseMouseEvent();
				System.exit(0);
			}

			@Override
			public void windowIconified(WindowEvent e)
			{
				dispatchFalseMouseEvent();
			}

			@Override
			public void windowLostFocus(WindowEvent e)
			{
				dispatchFalseMouseEvent();
			}

			@Override
			public void windowDeactivated(WindowEvent e)
			{
				dispatchFalseMouseEvent();
			}
		});

		//setting tabs
		tabbedPane = new JTabbedPane();
		tabProducts = new TabProducts(this);
		tabbedPane.addTab(Messages.getString("RutaClientFrame.27"), tabProducts); //$NON-NLS-1$
		tabCorrespondences = new TabCorrespondences(this);
		tabbedPane.addTab(Messages.getString("RutaClientFrame.28"), tabCorrespondences); //$NON-NLS-1$
		tabCDR = new TabCDRData(this);
		tabbedPane.addTab(Messages.getString("RutaClientFrame.29"), tabCDR); //$NON-NLS-1$

		tabbedPane.addChangeListener(event ->
		{
			loadTab(tabbedPane.getSelectedIndex());
		});

		consolePane = new JTextPane();
		consolePane.setSize(3,50);
		consolePane.setCaretColor(Color.white);
		consolePane.setEditable(false);

		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tabbedPane, new JScrollPane(consolePane));
		add(splitPane, BorderLayout.CENTER);

		//setting view listeners after model listeners of tabbedPane
		myParty.addActionListener(this, RutaClientFrameEvent.class);
		myParty.addActionListener(this, SearchEvent.class);
		myParty.addActionListener(this, CorrespondenceEvent.class);
		myParty.addActionListener(this, BusinessPartyEvent.class);
		myParty.addActionListener(this, PartnershipEvent.class);
		myParty.addActionListener(this, ItemEvent.class);

		//setting the menu
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		JMenu localDataMenu = new JMenu(Messages.getString("RutaClientFrame.30")); //$NON-NLS-1$
		JMenu cdrMenu = new JMenu(Messages.getString("RutaClientFrame.31")); //$NON-NLS-1$
		JMenu helpMenu = new JMenu(Messages.getString("RutaClientFrame.32")); //$NON-NLS-1$
		menuBar.add(localDataMenu);
		menuBar.add(cdrMenu);
		menuBar.add(helpMenu);

		localDataMenu.add(myPartyItem);
		localDataMenu.add(myCatalogueItem);
		localDataMenu.add(newProductItem);
		localDataMenu.addSeparator();
		localDataMenu.add(saveDataItem);
		localDataMenu.addSeparator();
		localDataMenu.add(localRegisterPartyItem);
		localDataMenu.add(localDeregisterPartyItem);
		localDataMenu.addSeparator();
		localDataMenu.add(exitItem);

		myPartyItem.addActionListener(event ->
		{
			showPartyDialog(client.getMyParty().getCoreParty(), Messages.getString("RutaClientFrame.33"), true, false); //$NON-NLS-1$
		});

		myCatalogueItem.addActionListener(event ->
		{
			tabbedPane.setSelectedIndex(TAB_PRODUCTS);
		});

		newProductItem.addActionListener(event ->
		{
			Item product = showProductDialog(client.getMyParty().createEmptyProduct(), Messages.getString("RutaClientFrame.34"), true); //$NON-NLS-1$
			if (product != null)
			{
				try
				{
					if(product.isInStock())
						client.getMyParty().addProduct(product);
					else
						client.getMyParty().archiveProduct(product);
					tabbedPane.setSelectedIndex(TAB_PRODUCTS);
				}
				catch (DetailException e)
				{
					logger.error(Messages.getString("RutaClientFrame.35"), e); //$NON-NLS-1$
					EventQueue.invokeLater(() ->
					JOptionPane.showMessageDialog(null, Messages.getString("RutaClientFrame.36"), //$NON-NLS-1$
							Messages.getString("RutaClientFrame.37"), JOptionPane.ERROR_MESSAGE)); //$NON-NLS-1$
				}
			}
			else
			{
				client.getMyParty().decreaseProductID();
			}
		});

		saveDataItem.addActionListener(event ->
		{
			appendToConsole(new StringBuilder(Messages.getString("RutaClientFrame.38")), Color.BLACK); //$NON-NLS-1$
			new Thread(() ->
			{
				try
				{
					if(client.getMyParty().isRegisteredWithLocalDatastore())
					{
						client.getMyParty().storeAllData();
						appendToConsole(new StringBuilder(Messages.getString("RutaClientFrame.39")), Color.GREEN); //$NON-NLS-1$
					}
					else
						appendToConsole(new StringBuilder(Messages.getString("RutaClientFrame.40")), //$NON-NLS-1$
								Color.RED);
				}
				catch(Exception e)
				{
					appendToConsole(new StringBuilder(Messages.getString("RutaClientFrame.41")), //$NON-NLS-1$
							Color.RED);
					getLogger().error(Messages.getString("RutaClientFrame.42"), e); //$NON-NLS-1$
				}
			}).start();
		});

		localRegisterPartyItem.addActionListener(event ->
		{
			if(myParty.isRegisteredWithLocalDatastore())
				appendToConsole(new StringBuilder(Messages.getString("RutaClientFrame.43")), Color.BLUE); //$NON-NLS-1$
			else
			{
				disablePartyMenuItems();
				new Thread(() ->
				{
						try
						{
							if(!myParty.isRegisteredWithLocalDatastore())
								client.setInitialUsername(showLocalSignUpDialog(Messages.getString("RutaClientFrame.44"), true)); //$NON-NLS-1$
						}
						catch (DetailException e1)
						{
							processExceptionAndAppendToConsole(e1,
									new StringBuilder(Messages.getString("RutaClientFrame.45"))); //$NON-NLS-1$
						}
				}).start();
			}
		});

		localDeregisterPartyItem.addActionListener(event ->
		{
			if(myParty.isRegisteredWithCDR())
				appendToConsole(new StringBuilder(Messages.getString("RutaClientFrame.46")) //$NON-NLS-1$
						, Color.RED);
			else if(myParty.isRegisteredWithLocalDatastore())
			{
				int option = JOptionPane.showConfirmDialog(RutaClientFrame.this,
						Messages.getString("RutaClientFrame.47"), Messages.getString("RutaClientFrame.48"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
				if(option == JOptionPane.YES_OPTION)
				{
					disablePartyMenuItems();
					new Thread(() ->
					{
						appendToConsole(new StringBuilder(Messages.getString("RutaClientFrame.49")), Color.BLACK); //$NON-NLS-1$
						client.localDeregisterMyParty();
						updateTitle(""); //$NON-NLS-1$
						appendToConsole(new StringBuilder(Messages.getString("RutaClientFrame.51")), //$NON-NLS-1$
								Color.GREEN);
						repaint();
						EventQueue.invokeLater(() ->
						{
							JOptionPane.showMessageDialog(RutaClientFrame.this, Messages.getString("RutaClientFrame.52")); //$NON-NLS-1$
							System.exit(0);
						});
					}).start();
				}
			}
			else
				appendToConsole(new StringBuilder(Messages.getString("RutaClientFrame.53")), Color.RED); //$NON-NLS-1$
		});

		exitItem.addActionListener(event ->
		{
			dispatchFalseMouseEvent();
			System.exit(0);
		});

		cdrMenu.add(cdrGetDocumentsItem);
		cdrMenu.add(cdrSearchItem);
		cdrMenu.addSeparator();
		cdrMenu.add(cdrUpdateCatalogueItem);
		cdrMenu.add(cdrDeleteCatalogueItem);
		cdrMenu.addSeparator();
		cdrMenu.add(cdrUpdatePartyItem);
		cdrMenu.add(cdrRegisterPartyItem);
		cdrMenu.add(cdrDeregisterPartyItem);
		cdrMenu.addSeparator();
		cdrMenu.add(cdrSettingsItem);

		cdrGetDocumentsItem.addActionListener(event ->
		{
			if(client.getMyParty().isRegisteredWithCDR())
			{
				disableGetDocumentsMenuItem();
				new Thread(()->
				{
					client.cdrGetNewDocuments();
				}).start();
			}
			else
				appendToConsole(new StringBuilder(Messages.getString("RutaClientFrame.54")), Color.RED); //$NON-NLS-1$
		});

		cdrSearchItem.addActionListener(event ->
		{
			if(client.getMyParty().isRegisteredWithCDR())
			{
				new Thread(() ->
				{
					showSearchDialog(Messages.getString("RutaClientFrame.55"), null, true); //$NON-NLS-1$
				}).start();
			}
			else
				appendToConsole(new StringBuilder(Messages.getString("RutaClientFrame.56")), Color.RED); //$NON-NLS-1$
		});

		cdrRegisterPartyItem.addActionListener(event ->
		{
			if(myParty.isRegisteredWithCDR())
				appendToConsole(new StringBuilder(Messages.getString("RutaClientFrame.57")), Color.BLUE); //$NON-NLS-1$
			else
			{
				disablePartyMenuItems();
				new Thread(() ->
				{
					boolean cdrRegistration = true;
					if(client.getInitialUsername() == null || !myParty.isRegisteredWithLocalDatastore())
					{
						JOptionPane.showMessageDialog(RutaClientFrame.this, Messages.getString("RutaClientFrame.58"), Messages.getString("RutaClientFrame.59"), JOptionPane.INFORMATION_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
						try
						{
							final String username = showLocalSignUpDialog(Messages.getString("RutaClientFrame.60"), true); //$NON-NLS-1$
							if(username != null)
								client.setInitialUsername(username);
							else
								cdrRegistration = false;
						}
						catch (DetailException e)
						{
							processExceptionAndAppendToConsole(e,
									new StringBuilder(Messages.getString("RutaClientFrame.61"))); //$NON-NLS-1$
						}
					}
					if(cdrRegistration)
						showCDRSignUpDialog(Messages.getString("RutaClientFrame.62")); //$NON-NLS-1$
				}).start();
			}
		});

		cdrDeregisterPartyItem.addActionListener(event ->
		{
			if(myParty.isRegisteredWithCDR())
			{
				if(!myParty.getBusinessPartners().isEmpty())
				{
					JOptionPane.showMessageDialog(RutaClientFrame.this, Messages.getString("RutaClientFrame.63"), //$NON-NLS-1$
							Messages.getString("RutaClientFrame.64"), JOptionPane.INFORMATION_MESSAGE); //$NON-NLS-1$
				}
				else
				{
					int option = JOptionPane.showConfirmDialog(RutaClientFrame.this,
							Messages.getString("RutaClientFrame.65"), //$NON-NLS-1$
									Messages.getString("RutaClientFrame.66"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE); //$NON-NLS-1$
					if(option == JOptionPane.YES_OPTION)
					{
						disablePartyMenuItems();
						new Thread(() ->
						{
							client.cdrDeregisterMyParty();
						}).start();
					}
				}
			}
			else
				appendToConsole(new StringBuilder(Messages.getString("RutaClientFrame.67")), Color.RED); //$NON-NLS-1$
		});

		cdrUpdatePartyItem.addActionListener(event ->
		{
			if(myParty.isRegisteredWithCDR())
			{
				if(myParty.isDirtyMyParty())
				{
					appendToConsole(new StringBuilder(Messages.getString("RutaClientFrame.68")), Color.BLACK); //$NON-NLS-1$
					disablePartyMenuItems();
					new Thread(() ->
					{
						client.cdrUpdateMyParty();

					}).start();
				}
				else
					appendToConsole(new StringBuilder(Messages.getString("RutaClientFrame.69")), Color.BLUE); //$NON-NLS-1$
			}
			else
				appendToConsole(new StringBuilder(Messages.getString("RutaClientFrame.70")), Color.RED); //$NON-NLS-1$
		});

		cdrUpdateCatalogueItem.addActionListener(event ->
		{
			if(myParty.isRegisteredWithCDR())
			{
				if(myParty.isDirtyCatalogue())
				{
					disableCatalogueMenuItems();
					new Thread(()->
					{
						try
						{
							client.cdrSynchroniseMyCatalogue();
						}
						catch(Exception e)
						{
							appendToConsole(new StringBuilder(Messages.getString("RutaClientFrame.71")). //$NON-NLS-1$
									append(e.getMessage()), Color.RED);
							logger.error(Messages.getString("RutaClientFrame.72"), e); //$NON-NLS-1$
							enableCatalogueMenuItems();
						}
					}).start();
				}
				else
					appendToConsole(new StringBuilder(Messages.getString("RutaClientFrame.73")), Color.BLUE); //$NON-NLS-1$
			}
			else
				appendToConsole(new StringBuilder(Messages.getString("RutaClientFrame.74")), Color.RED); //$NON-NLS-1$
		});

		cdrDeleteCatalogueItem.addActionListener(event ->
		{
			if(myParty.isRegisteredWithCDR())
			{
				if(myParty.isCatalogueInCDR())
				{
					int option = JOptionPane.showConfirmDialog(RutaClientFrame.this,
							Messages.getString("RutaClientFrame.75"), //$NON-NLS-1$
							Messages.getString("RutaClientFrame.76"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE); //$NON-NLS-1$
					if(option == JOptionPane.YES_OPTION)
					{
						disableCatalogueMenuItems();
						new Thread( () ->
						{
							try
							{
								client.cdrDeleteMyCatalogue();
							}
							catch(Exception e)
							{
								appendToConsole(new StringBuilder(Messages.getString("RutaClientFrame.77")). //$NON-NLS-1$
										append(e.getMessage()), Color.RED);
								enableCatalogueMenuItems();
							}
						}).start();
					}
				}
				else
					appendToConsole(new StringBuilder(Messages.getString("RutaClientFrame.78")), Color.RED); //$NON-NLS-1$
			}
			else
				appendToConsole(new StringBuilder(Messages.getString("RutaClientFrame.79")), Color.RED); //$NON-NLS-1$
		});

		cdrSettingsItem.addActionListener(event ->
		{
			settingsDialog = new CDRSettingsDialog(RutaClientFrame.this);
			settingsDialog.setTitle(Messages.getString("RutaClientFrame.80")); //$NON-NLS-1$
			settingsDialog.setVisible(true);
			if(settingsDialog.isApplyPressed())
			{
				RutaClient.setCDREndPoint(settingsDialog.getServiceLocation());
				RutaClient.setConnectTimeout(Integer.valueOf(settingsDialog.getConnectTimeout()) * 1000);
				RutaClient.setRequestTimeout(Integer.valueOf(settingsDialog.getRequestTimeout()) * 1000);
				settingsDialog.setApplyPressed(false);
			}
		});

		JMenuItem aboutItem = new JMenuItem(Messages.getString("RutaClientFrame.81")); //$NON-NLS-1$
		helpMenu.add(aboutItem);
		JMenuItem updateItem = new JMenuItem(Messages.getString("RutaClientFrame.82")); //$NON-NLS-1$
		helpMenu.add(updateItem);
		JMenuItem reportBugItem = new JMenuItem(Messages.getString("RutaClientFrame.83")); //$NON-NLS-1$
		helpMenu.add(reportBugItem);
		JMenuItem exploreBugItem = new JMenuItem(Messages.getString("RutaClientFrame.84")); //$NON-NLS-1$
		helpMenu.add(exploreBugItem);

		aboutItem.addActionListener(event ->
		{
			if(aboutDialog == null)
				aboutDialog = new AboutDialog(RutaClientFrame.this);
			aboutDialog.setVisible(true);
		});

		updateItem.addActionListener(event ->
		{
			if(updateDialog == null)
				updateDialog = new UpdateDialog(RutaClientFrame.this, client);
			updateDialog.setVisible(true);
		});

		reportBugItem.addActionListener(event ->
		{
			new Thread(()->
			{
				sendBugReport();
			}).start();
		});

		exploreBugItem.addActionListener(event ->
		{
			if(client.getMyParty().isRegisteredWithCDR())
			{
				bugExploreDialog = new BugExploreDialog(RutaClientFrame.this);
				bugExploreDialog.setVisible(true);
			}
			else
			{
				appendToConsole(new StringBuilder(Messages.getString("RutaClientFrame.85")), Color.RED); //$NON-NLS-1$
			}
		});

		tabbedPane.setSelectedIndex(TAB_PRODUCTS);
		setVisible(true);
	}

	/**
	 * Gets the {@link Logger} of the {@code RutaClientFrame}.
	 * @return logger
	 */
	public static Logger getLogger()
	{
		return logger;
	}


	/**
	 * Initialize tabbed pane
	 */
	public void initializeTabbedPane()
	{
		//setting tabs
		tabbedPane = new JTabbedPane();
		tabProducts = new TabProducts(this);
		tabbedPane.addTab(Messages.getString("RutaClientFrame.86"), tabProducts); //$NON-NLS-1$
		tabCorrespondences = new TabCorrespondences(this);
		tabbedPane.addTab(Messages.getString("RutaClientFrame.87"), tabCorrespondences); //$NON-NLS-1$
		tabCDR = new TabCDRData(this);
		tabbedPane.addTab(Messages.getString("RutaClientFrame.88"), tabCDR); //$NON-NLS-1$

		tabbedPane.addChangeListener(event ->
		{
			loadTab(tabbedPane.getSelectedIndex());
		});
	}

	/**
	 * Dispatches false {@link MouseEvent mouse event} to trigger {@code focusTracker}
	 * event listener which will save the data of a last edited cell of the table
	 * in current view if it is still in editing state.
	 */
	private void dispatchFalseMouseEvent()
	{
		tabbedPane.getComponent(tabbedPane.getSelectedIndex()).dispatchEvent(
				new MouseEvent(RutaClientFrame.this, MouseEvent.MOUSE_CLICKED, 1, 0, 0, 0, 1, false));
	}

	/**
	 * Checks whether My Party is registered with the CDR, then opens {@link BugReportDialog}
	 * and sends {@link BugReport} to the CDR by calling appropriate method in the {@link Client}
	 * class. {@code sendBugReport} method is extracted from the {@code ActionListener} because it is used in
	 * more than one place.
	 */
	public void sendBugReport()
	{
		if(client.getMyParty().isRegisteredWithCDR())
		{
			bugReportDialog = new BugReportDialog(RutaClientFrame.this);
			bugReportDialog.setVisible(true);
			if(bugReportDialog.isReportPressed())
			{
				bugReportDialog.clearData();
				client.cdrReportBug(bugReportDialog.getBugReport());
			}
		}
		else
			appendToConsole(new StringBuilder(Messages.getString("RutaClientFrame.89")), Color.RED); //$NON-NLS-1$
	}

	/**
	 * Checks whether My Party is registered with the CDR, then sends a request to the CDR for the list of
	 * {@link BugReport}s based on some search criterion.
	 * @param criterion search criterion
	 * @return {@link Future} object representing the response.
	 */
	public Future<?> searchBugReport(BugReportSearchCriterion criterion)
	{
		Future<?> future = null;
		if(client.getMyParty().isRegisteredWithCDR())
			future = client.cdrSearchBugReport(criterion);
		else
			appendToConsole(new StringBuilder(Messages.getString("RutaClientFrame.90")), Color.RED); //$NON-NLS-1$
		return future;
	}

	/**
	 * Checks whether My Party is registered with the CDR, then sends a request for the list of all
	 * {@link BugReport bugs reported} to the CDR.
	 * @return {@link Future} object representing the response.
	 */
	public Future<?> findAllBugs()
	{
		Future<?> future = null;
		if(client.getMyParty().isRegisteredWithCDR())
			future = client.cdrFindAllBugs();
		else
			appendToConsole(new StringBuilder(Messages.getString("RutaClientFrame.91")), Color.RED); //$NON-NLS-1$
		return future;
	}

	/**
	 * Enables menu items regarding Search. Method is called after client gets the response from the CDR service.
	 * Method's body is executed in the {@link EventQueue}.
	 */
	public void enableSearchMenuItems()
	{
		EventQueue.invokeLater(() ->
		{
			cdrSearchItem.setEnabled(true);
			cdrDeregisterPartyItem.setEnabled(true);
		});
	}

	/**
	 * Disables menu items regarding Search. Method is called after client sends the request to the CDR service.
	 * Method's body is executed in the {@link EventQueue}.
	 */
	public void disableSearchMenuItems()
	{
		EventQueue.invokeLater(() ->
		{
			cdrSearchItem.setEnabled(false);
			cdrDeregisterPartyItem.setEnabled(false);
		});
	}

	/**
	 * Enables menu items regarding My Party. Method is called after client gets the response from the CDR service.
	 * Method's body is executed in the {@link EventQueue}.
	 */
	public void enablePartyMenuItems()
	{
		EventQueue.invokeLater(() ->
		{
			cdrRegisterPartyItem.setEnabled(true);
			cdrUpdatePartyItem.setEnabled(true);
			cdrDeregisterPartyItem.setEnabled(true);
		});
	}

	/**
	 * Disables menu items regarding My Party. Method is called after client sends the request to the CDR service.
	 * Method's body is executed in the {@link EventQueue}.
	 */
	public void disablePartyMenuItems()
	{
		EventQueue.invokeLater(() ->
		{
			cdrRegisterPartyItem.setEnabled(false);
			cdrUpdatePartyItem.setEnabled(false);
			cdrDeregisterPartyItem.setEnabled(false);
		});
	}

	/**
	 * Enables menu items regarding My Catalogue.. Method is called after client gets the response from the CDR service.
	 * Method's body is executed in the {@link EventQueue}.
	 */
	public void enableCatalogueMenuItems()
	{
		EventQueue.invokeLater(() ->
		{
			cdrUpdateCatalogueItem.setEnabled(true);
			cdrDeleteCatalogueItem.setEnabled(true);
			cdrDeregisterPartyItem.setEnabled(true);
		});
	}

	/**
	 * Disables menu items regarding My Catalogue. Method is called after client sends the request to the CDR service.
	 * Method's body is executed in the {@link EventQueue}.
	 */
	public void disableCatalogueMenuItems()
	{
		EventQueue.invokeLater(() ->
		{
			cdrUpdateCatalogueItem.setEnabled(false);
			cdrDeleteCatalogueItem.setEnabled(false);
			cdrDeregisterPartyItem.setEnabled(false);
		});
	}

	/**
	 * Enables Get New Documents menu item.
	 * Method's body is executed in the {@link EventQueue}.
	 */
	public void enableGetDocumentsMenuItem()
	{
		EventQueue.invokeLater(() ->
		{
			cdrGetDocumentsItem.setEnabled(true);
		});
	}

	/**
	 * Disables Get New Documents menu item.
	 * Method's body is executed in the {@link EventQueue}.
	 */
	public void disableGetDocumentsMenuItem()
	{
		EventQueue.invokeLater(() ->
		{
			cdrGetDocumentsItem.setEnabled(false);
		});
	}

	/**
	 * Saves properties from {@code RutaClientFrame} class fields to the {@link Properties} object.
	 */
	public void saveProperties()
	{
		Properties properties = client.getProperties();
		properties.put("mainFrame.left", String.valueOf(getX())); //$NON-NLS-1$
		properties.put("mainFrame.top", String.valueOf(getY())); //$NON-NLS-1$
		properties.put("mainFrame.width", String.valueOf(getWidth())); //$NON-NLS-1$
		properties.put("mainFrame.height", String.valueOf(getHeight())); //$NON-NLS-1$
	}

	/**
	 * Repaints tab.
	 * @param tabIndex index of the tab to repaint
	 */
	private void loadTab(int tabIndex)
	{
		Component component = null;
		switch(tabIndex)
		{
		case TAB_CDR_DATA:
			component = tabCDR;
			break;
		case TAB_PRODUCTS:
			component = tabProducts;
			break;
		case TAB_CORRESPONDENSCES:
			component = tabCorrespondences;
			break;
		}
		tabbedPane.setComponentAt(tabIndex, component);
	}

	/**
	 * Shows dialog with {@link Party} data which could be changed. If changes have been made to the {@code Party}
	 * object they are saved within this method.
	 * @param party {@code Party} object which data are to be shown
	 * @param title title of the dialog
	 * @param editable whether Party dialog's dadat are editable
	 * @param registration whether the dialog is shown during local database registration
	 * @return {@code Party} with potentially changed data
	 */
	public Party showPartyDialog(Party party, String title, boolean editable, boolean registration)
	{
		partyDialog = new PartyDialog(RutaClientFrame.this, editable, registration);
		partyDialog.setTitle(title);

		//setting clone and not original object as a dialog's party field because the changes to the party will be rejected
		//if they are not accepted by pressing the dialog's OK button. If original object is set instead, changes remain
		//no matter what button was pressed
		partyDialog.setParty(party.clone());
		partyDialog.setVisible(true);
		if(partyDialog.isChanged())
		{
			party = partyDialog.getParty();
			updateTitle(party.getPartySimpleName());
			final MyParty myParty = client.getMyParty();
			myParty.setCoreParty(party);
			myParty.setDirtyMyParty(true);
			partyDialog.setChanged(false);
		}
		return party;
	}

	/**
	 * Shows dialog with {@link Item}'s.
	 * @param product {@code Item product} which data are to be shown/amended
	 * @param title title of the dialog
	 * @param editable whether the data are editable
	 * @return {@code Item product} with potentially changed data or {@code null} if data are not changed
	 */
	public Item showProductDialog(@NotNull Item product, String title, boolean editable)
	{
		productDialog = new ProductDialog(RutaClientFrame.this, client.getMyParty(),
				product.clone(), editable);
		productDialog.setTitle(title);
		//setting clone and not original object as a dialog's party field because the changes to the party will be rejected
		//if they are not accepted by pressing the dialog's OK button. If original object is set instead, changes remain
		//no matter what button was pressed
		productDialog.setVisible(true);
		if(productDialog.isChanged())
		{
			product = productDialog.getProduct();
			productDialog.setChanged(false);
			return product;
		}
		else
			return null;
	}

	/**
	 * Shows dialog for the sign up request with the CDR service.
	 * @param title title of the dialog
	 */
	public void showCDRSignUpDialog(String title)
	{
		boolean registerPressed = false;
		registerDialog = new RegisterDialog(RutaClientFrame.this, false, false, true);
		registerDialog.setTitle(Messages.getString("RutaClientFrame.96") + title); //$NON-NLS-1$
		registerDialog.setVisible(true);
		registerPressed = registerDialog.isOKPressed();
		registerDialog.setOKPressed(false);
		if(registerPressed)
		{
			final String username = registerDialog.getUsername();
			final String password = registerDialog.getPassword();
			client.cdrRegisterMyParty(username, password);
		}
		else
			enablePartyMenuItems();
	}

	/**
	 * Shows dialog for the sign up request with the local database.
	 * @param title title of the dialog
	 * @param mayExit true when dialog can be discarded
	 * @return username
	 * @throws DetailException if Party could not be registered
	 */
	public String showLocalSignUpDialog(String title, boolean mayExit) throws DetailException
	{
		String username = null;
		registerDialog = new RegisterDialog(RutaClientFrame.this, false, true, mayExit);
		registerDialog.setTitle(Messages.getString("RutaClientFrame.97") + title); //$NON-NLS-1$
		registerDialog.setVisible(true);
		if(registerDialog.isOKPressed())
		{
			registerDialog.setOKPressed(false);
			username = registerDialog.getUsername();
			final String password = registerDialog.getPassword();
			client.localRegisterMyParty(username, password);
			final Properties properties = client.getProperties();
			if(registerDialog.isRememberMe())
			{
				properties.setProperty("username", username); //$NON-NLS-1$
				properties.setProperty("password", password); //$NON-NLS-1$
			}
			else
			{
				properties.remove("username"); //$NON-NLS-1$
				properties.remove("password"); //$NON-NLS-1$
			}
		}
		EventQueue.invokeLater(() -> enablePartyMenuItems());
		return username;
	}

	/**
	 * Shows dialog for the log in request with the local database.
	 * @param title title of the dialog
	 * @return true if log in was successful
	 * @throws DatabaseException due to database connectivity issues
	 */
	public boolean showLocalLogInDialog(String title) throws DatabaseException
	{
		boolean success = false;
		client.setEnableStoringProperties(false);// do not store properties if log-in is unsuccessful
		registerDialog = new RegisterDialog(RutaClientFrame.this, true, true, false);
		registerDialog.setTitle(Messages.getString("RutaClientFrame.102") + title); //$NON-NLS-1$
		registerDialog.setVisible(true);
		if(registerDialog.isOKPressed())
		{
			client.setEnableStoringProperties(true);
			registerDialog.setOKPressed(false);
			registerDialog.setVisible(false);
			final String username = registerDialog.getUsername();
			final String password = registerDialog.getPassword();
			final Properties properties = client.getProperties();
			properties.setProperty("username", username); //$NON-NLS-1$
			properties.setProperty("password", password); //$NON-NLS-1$
			success = client.isLocalUserRegistеred();

			if(!registerDialog.isRememberMe())
			{
				properties.remove("username"); //$NON-NLS-1$
				properties.remove("password"); //$NON-NLS-1$
			}
		}
		return success;
	}

	/**
	 * Shows {@link SearchDialog} for viewing passed {@link SearchCriterion} or creating a new one
	 * and calls a method that makes the acctual request to the CDR.
	 * @param title {@code SearchDialog}'s title
	 * @param search {@link Search} to process, view and/or amend
	 * @param editable whether search criterion is editable or not
	 */
	public void showSearchDialog(String title, Search<?> search, boolean editable)
	{
		searchDialog = new SearchDialog(RutaClientFrame.this, search, editable);
		searchDialog.setTitle(title);
		searchDialog.setVisible(true);
		if(searchDialog.isSearchPressed())
		{
			searchDialog.setSearchPressed(false);
			client.cdrSearch(searchDialog.getSearch(), !editable);
		}
		else if(searchDialog.isDerivedPressed())
		{
			searchDialog.setDerivedPressed(false);
			showSearchDialog(Messages.getString("RutaClientFrame.107"), search, true); //$NON-NLS-1$
		}
	}

	/**
	 * Shows {@link OrderDialog} for making new {@link OrderType order}. {@code corr} argument should be set to {@code null}
	 * when new {@code Order} is to be created or old one viewed and to appropriate non-{@code null} value only when
	 * some old {@code Order} failed to be delievered and new sending attempt of it could be tried.
	 * @param title {@code OrderDialog}'s title
	 * @param order {@code Order} to display
	 * @param editable whether the Order is editable i.e. its quantity column
	 * @param corr {@link Correspondence} of the {@link OrderType}
	 * @return {@code OrderType} or {@code null} if user aborts Order creation
	 */
	public OrderType showOrderDialog(String title, OrderType order, boolean editable, Correspondence corr)
	{
		OrderDialog orderDialog = new OrderDialog(RutaClientFrame.this, order, editable, corr);
		orderDialog.setTitle(title);
		orderDialog.setVisible(true);
		if(orderDialog.isSendPressed())
		{
			order = orderDialog.getOrder();
			orderDialog.setSendPressed(false);
		}
		else
			order = null;
		return order;
	}

	/**
	 * Shows {@link ProcessOrderDialog} for making a decision about a response to an {@link OrderType Order}.
	 * @param title dialog's title
	 * @param order order to make decision upon about the response
	 * @result {@code String} representing the decision
	 */
	public String showProcessOrderDialog(String title, OrderType order)
	{
		final ProcessOrderDialog processOrderDialog = new ProcessOrderDialog(RutaClientFrame.this, order);
		processOrderDialog.setTitle(title);
		processOrderDialog.setVisible(true);
		return processOrderDialog.getDecision();
	}

	/**
	 * Shows {@link OrderResponseDialog} for making new {@link OrderResponseType} document.  {@code corr}
	 * argument should be set to {@code null}
	 * when new {@code Order Response} is to be created or old one viewed and to appropriate non-{@code null} value only when
	 * some old {@code Order Response} failed to be delievered and new sending attempt of it could be tried.
	 * @param title dialog's title
	 * @param orderResponse Order Response to show and/or amend
	 * @param editable true if Order Response is to be created/changed, false if it is just a preview of one
	 * @param corr {@link Correspondence} if {@link OrderResponseType} is already a part of it; {@code null}
	 * otherwise i.e. should be created and appended to it
	 * @return Order Response or {@code null} if user has decided to abort the creation of it
	 */
	public OrderResponseType showOrderResponseDialog(String title, OrderResponseType orderResponse,
			boolean editable, Correspondence corr)
	{
		final OrderResponseDialog orderResponseDialog =
				new OrderResponseDialog(RutaClientFrame.this, orderResponse, editable, corr);
		orderResponseDialog.setTitle(title);
		orderResponseDialog.setVisible(true);
		if(orderResponseDialog.isSendPressed())
		{
			orderResponse = orderResponseDialog.getOrderResponse();
			orderResponseDialog.setSendPressed(false);
		}
		else
			orderResponse = null;
		return orderResponse;
	}

	/**
	 * Shows {@link ProcessOrderResponseDialog} for making a deciosion about a response to an
	 * {@link OrderResponseType Order Response}.
	 * @param orderResponse Order Response to make decision upon about the response
	 * @result {@code String} representing the decision
	 */
	public String showProcessOrderResponseDialog(OrderResponseType orderResponse)
	{
		final ProcessOrderResponseDialog processDialog = new ProcessOrderResponseDialog(RutaClientFrame.this, orderResponse);
		processDialog.setTitle(Messages.getString("RutaClientFrame.108")); //$NON-NLS-1$
		processDialog.setVisible(true);
		return processDialog.getDecision();
	}

	/**
	 * Shows {@link ProcessOrderResponseSimpleDialog} for making a deciosion about a response to an
	 * {@link OrderResponseSimpleType Order Response Simple}.
	 * @param applicationResponse Order Response Simple to make decision upon about the response
	 * @result {@code String} representing the decision
	 */
	public String showProcessOrderResponseSimpleDialog(OrderResponseSimpleType orderResponseSimple)
	{
		final ProcessOrderResponseSimpleDialog processDialog =
				new ProcessOrderResponseSimpleDialog(RutaClientFrame.this, orderResponseSimple,
						orderResponseSimple.isAcceptedIndicatorValue(false));
		processDialog.setTitle(Messages.getString("RutaClientFrame.109")); //$NON-NLS-1$
		processDialog.setVisible(true);
		return processDialog.getDecision();
	}

	/**
	 * Shows {@link OrderResponseSimpleDialog} for making new {@link OrderResponseSimpleType} document.
	 * {@code corr} argument should be set to {@code null}
	 * when new {@code Order Response} is to be created or old one viewed and to appropriate non-{@code null} value only when
	 * some old {@code Order Response} failed to be delievered and new sending attempt of it could be tried.
	 * @param title dialog's title
	 * @param orderResponseSimple Order Response Simple to show and/or amend
	 * @param editable true if dialog data could be amended
	 * @param obsoleteCatalogue true if Order has been sent with the reference to some previous version
	 * of the Catalogue
	 * @param corr {@link Correspondence} of the {@link OrderResponseSimpleType}
	 * @return Order Response Simple or {@code null} if user has decided to discard the creation of it
	 */
	public OrderResponseSimpleType showOrderResponseSimpleDialog(String title,
			OrderResponseSimpleType orderResponseSimple, boolean editable, boolean obsoleteCatalogue, Correspondence corr)
	{
		final OrderResponseSimpleDialog orderResponseDialog =
				new OrderResponseSimpleDialog(RutaClientFrame.this, orderResponseSimple, editable, obsoleteCatalogue, corr);
		if(title != null)
			orderResponseDialog.setTitle(title);
		else if(orderResponseSimple.isAcceptedIndicatorValue(false))
			orderResponseDialog.setTitle(Messages.getString("RutaClientFrame.110")); //$NON-NLS-1$
		else
			orderResponseDialog.setTitle(Messages.getString("RutaClientFrame.111")); //$NON-NLS-1$
		orderResponseDialog.setVisible(true);
		if(orderResponseDialog.isSendPressed())
		{
			orderResponseDialog.setSendPressed(false);
			orderResponseSimple = orderResponseDialog.getOrderResponseSimple();
		}
		else
			orderResponseSimple = null;
		return orderResponseSimple;
	}

	/**
	 * Shows {@link OrderChangeDialog} for making new {@link OrderChangeType} document.
	 * @param title dialog's title
	 * @param orderChange Order Change to show and/or amend
	 * @param editable true if Order Change is to be created/changed, false if it is just a preview of one
	 * @param corr {@link Correspondence} if {@link OrderChangeType} is already a part of it; {@code null}
	 * otherwise i.e. should be created and appended to it
	 * @return Order Change or {@code null} if user has decided to abort the creation of it
	 */
	public OrderChangeType showOrderChangeDialog(String title, OrderChangeType orderChange,
			boolean editable, Correspondence corr)
	{
		final OrderChangeDialog orderChangeDialog =
				new OrderChangeDialog(RutaClientFrame.this, orderChange, editable, corr);
		orderChangeDialog.setTitle(title);
		orderChangeDialog.setVisible(true);
		if(orderChangeDialog.isSendPressed())
		{
			orderChange = orderChangeDialog.getOrderChange();
			orderChangeDialog.setSendPressed(false);
		}
		else
			orderChange = null;
		return orderChange;
	}

	/**
	 * Shows {@link OrderCancellationDialog} for making new {@link OrderCancellationType} document.
	 * @param title dialog's title
	 * @param orderCancellation Order Cancellation to show and/or amend
	 * @param editable true if Order Cancellation is to be created/changed, false if it is just a preview of one
	 * @param corr {@link Correspondence} if {@link OrderCancellationType} is already a part of it; {@code null}
	 * otherwise i.e. should be created and appended to it
	 * @return Order Cancellation or {@code null} if user has decided to abort the creation of it
	 */
	public OrderCancellationType showOrderCancellationDialog(String title, OrderCancellationType orderCancellation,
			boolean editable, Correspondence corr)
	{
		final OrderCancellationDialog orderCancellationDialog =
				new OrderCancellationDialog(RutaClientFrame.this, orderCancellation, editable, corr);
		orderCancellationDialog.setTitle(title);
		orderCancellationDialog.setVisible(true);
		if(orderCancellationDialog.isSendPressed())
		{
			orderCancellation = orderCancellationDialog.getOrderCancellation();
			orderCancellationDialog.setSendPressed(false);
		}
		else
			orderCancellation = null;
		return orderCancellation;
	}

	/**
	 * Shows {@link ApplicationResponseDialog} for making new {@link ApplicationResponseType} document.
	 * @param title dialog's title
	 * @param applicationResponse Application Response to show and/or amend
	 * @param editable true if Application Response is to be created/changed, false if it is just a preview of one
	 * @param corr {@link Correspondence} if {@link ApplicationResponseType} is already a part of it; {@code null}
	 * otherwise i.e. should be created and appended to it
	 * @return Application Response or {@code null} if user has decided to abort the creation of it
	 */
	public ApplicationResponseType showApplicationResponseDialog(String title, ApplicationResponseType applicationResponse,
			boolean editable, Correspondence corr)
	{
		final ApplicationResponseDialog appResponseDialog =
				new ApplicationResponseDialog(RutaClientFrame.this, applicationResponse, editable, corr);
		appResponseDialog.setTitle(title);
		appResponseDialog.setVisible(true);
		if(appResponseDialog.isSendPressed())
		{
			applicationResponse = appResponseDialog.getApplicationResponse();
			appResponseDialog.setSendPressed(false);
		}
		else
			applicationResponse = null;
		return applicationResponse;
	}

	/**
	 * Shows {@link ProcessApplicationResponseDialog} for making a deciosion about a response to an
	 * {@link ApplicationResponseType Application Response}.
	 * @param applicationResponse Application Response to make decision upon about the response
	 * @result {@code String} representing the decision
	 */
	public String showProcessApplicationResponseDialog(ApplicationResponseType applicationResponse)
	{
		final ProcessApplicationResponseDialog processDialog =
				new ProcessApplicationResponseDialog(RutaClientFrame.this, applicationResponse);
		processDialog.setTitle(Messages.getString("RutaClientFrame.112")); //$NON-NLS-1$
		processDialog.setVisible(true);
		return processDialog.getDecision();
	}

	/**
	 * Shows {@link InvoiceDialog} for making new {@link InvoiceType Invoice}. {@code corr} argument should be set to {@code null}
	 * when new {@code Invoice} is to be created or old one viewed and to appropriate non-{@code null} value only when
	 * some old {@code Invoice} failed to be delievered and new sending attempt of it could be tried.
	 * @param title {@code InvoiceDialog}'s title
	 * @param invoice {@code Invoice} to display
	 * @param editable whether the Invoice is editable
	 * @param corr {@link Correspondence} of the {@link InvoiceType}
	 * @return {@code InvoiceType} or {@code null} if user aborts Invoice creation
	 */
	public InvoiceType showInvoiceDialog(String title, InvoiceType invoice, boolean editable, Correspondence corr)
	{
		InvoiceDialog invoiceDialog = new InvoiceDialog(RutaClientFrame.this, invoice, editable, corr);
		invoiceDialog.setTitle(title);
		invoiceDialog.setVisible(true);
		if(invoiceDialog.isSendPressed())
		{
			invoice = invoiceDialog.getInvoice();
			invoiceDialog.setSendPressed(false);
		}
		else
			invoice = null;
		return invoice;
	}

	/**
	 * Shows {@link ProcessInvoiceDialog} for making a decision about a response to an {@link InvoiceType Invoice}.
	 * @param invoice Invoice to make decision upon about the response
	 * @result {@code String} representing the decision
	 */
	public String showProcessInvoiceDialog(InvoiceType invoice)
	{
		final ProcessInvoiceDialog processInvoiceDialog = new ProcessInvoiceDialog(RutaClientFrame.this, invoice);
		processInvoiceDialog.setTitle(Messages.getString("RutaClientFrame.113")); //$NON-NLS-1$
		processInvoiceDialog.setVisible(true);
		return processInvoiceDialog.getDecision();
	}

	public RutaClient getClient()
	{
		return client;
	}

	/**
	 * Repaints currently selected tab.
	 */
	@Override
	public void repaint()
	{
		repaint(tabbedPane.getSelectedIndex());
	}

	/**
	 * Repaints tab with passed index.
	 * @param tabIndex index of the tab to repaint
	 */
	public void repaint(int tabIndex)
	{
		super.repaint();
		tabbedPane.setSelectedIndex(tabIndex);
		loadTab(tabIndex);
	}

	/**
	 * Updates main frame's title because of changed name of the Party.
	 * @param partyName party name that should be shown as a part of the title
	 */
	public void updateTitle(String partyName)
	{
		setTitle(Messages.getString("RutaClientFrame.114") + partyName); //$NON-NLS-1$
	}

	/**
	 * Updates main frame's title because of the local data change.
	 * @param dirty true when local data (namly My Catalogue) are changed and are not in sync with the CDR
	 */
	public void updateTitle(boolean dirty)
	{
		if(dirty)
			setTitle(getTitle().replaceAll("[*]+$", "") + "*"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		else
			setTitle(getTitle().replaceAll("[*]+$", "")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		final Object source = event.getSource();
		final Class<? extends ActionEvent> eventClazz = event.getClass();
		if(eventClazz == BusinessPartyEvent.class)
		{
			tabCDR.dispatchEvent(event);
			tabCorrespondences.dispatchEvent(event);
			if(tabbedPane.getSelectedIndex() == TAB_CDR_DATA ||
					tabbedPane.getSelectedIndex() == TAB_CORRESPONDENSCES)
				repaint();
		}
		else if(eventClazz == PartnershipEvent.class)
		{
			tabCDR.dispatchEvent(event);
			if(tabbedPane.getSelectedIndex() == TAB_CDR_DATA)
				repaint();
		}
		else if(eventClazz == CorrespondenceEvent.class)
		{
			tabCorrespondences.dispatchEvent(event);
			if(tabbedPane.getSelectedIndex() == TAB_CORRESPONDENSCES)
				repaint();
		}
		else if(eventClazz == SearchEvent.class)
		{
			tabCDR.dispatchEvent(event);
			if(source instanceof Search) // do not show TAB_CDR_DATA only when whole list is deleted
				repaint(TAB_CDR_DATA);
		}
		else if(eventClazz == ItemEvent.class)
		{
			tabProducts.dispatchEvent(event);
			if(tabbedPane.getSelectedIndex() == TAB_PRODUCTS)
				repaint();
		}
	}

	/**
	 * Appends current date and time and passed coloured string to the console. All this is done inside the
	 * {@link EventQueue}.
	 * @param textBuilder {@link StringBuilder string} to be shown on the console
	 * @param color colour of the string
	 */
	public void appendToConsole(StringBuilder textBuilder, Color color)
	{
		if(consolePane != null) // do not append to console anything before it is instantiated
			EventQueue.invokeLater(()->
			{
				StyleContext sc = StyleContext.getDefaultStyleContext();
				AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.BLACK);

				DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.MEDIUM);
				StyledDocument doc = consolePane.getStyledDocument();
				try
				{
					doc.insertString(doc.getLength(), formatter.format(LocalDateTime.now()) + ": ", aset); //$NON-NLS-1$
					aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, color);
					doc.insertString(doc.getLength(), textBuilder.append("\n").toString(), aset); //$NON-NLS-1$
				}
				catch (BadLocationException e)
				{
					getLogger().error(Messages.getString("RutaClientFrame.122"), e); //$NON-NLS-1$
				}
				consolePane.setCaretPosition(consolePane.getDocument().getLength());
			});
	}

	/**
	 * Processes exception thrown by called webmethod or some local one.
	 * @param e exception to be processed
	 * @param msgBuilder {@link StringBuilder message} to be processed for display on the console
	 * @param recursion true when the recursive method call is permissable
	 * @return message to be displayed on the console
	 */
	private StringBuilder processException(Exception e, StringBuilder msgBuilder, boolean recursion)
	{
		msgBuilder = msgBuilder.append(" "); //$NON-NLS-1$
		final Throwable cause = e.getCause();
		final String errorMessage = e.getMessage();
		if(errorMessage != null && (cause == null || (cause != null && cause.getClass() != RutaException.class)))
			if(e instanceof RutaException)
				msgBuilder.append(Messages.getString("RutaClientFrame.124")).append(errorMessage).append(" "). //$NON-NLS-1$ //$NON-NLS-2$
				append(((RutaException) e).getFaultInfo().getDetail());
			else
				msgBuilder.append(Messages.getString("RutaClientFrame.126")).append(trimSOAPFaultMessage(errorMessage)); //$NON-NLS-1$
		if(cause != null)
		{
			if(cause instanceof RutaException)
				msgBuilder.append(Messages.getString("RutaClientFrame.127")).append(cause.getMessage()).append(" "). //$NON-NLS-1$ //$NON-NLS-2$
				append(((RutaException) cause).getFaultInfo().getDetail());
			else if(cause.getMessage() != null)
				msgBuilder.append(Messages.getString("RutaClientFrame.129")).append(trimSOAPFaultMessage(cause.getMessage())); //$NON-NLS-1$
		if(cause.getCause() != null)
			processException((Exception) cause.getCause(), msgBuilder, false);
		}
		return msgBuilder;
	}

	/**
	 * Processes exception in a way that depends on whether it is thrown by webmethod or some local method and displays
	 * exception message on the console.
	 * @param e exception to be processed
	 * @param msg {@link StringBuilder message} to be displayed on the console
	 */
	public void processExceptionAndAppendToConsole(Exception e, StringBuilder msgBuilder)
	{
		processException(e, msgBuilder, true);
		appendToConsole(msgBuilder, Color.RED);
		getLogger().error(msgBuilder.toString() + Messages.getString("RutaClientFrame.130"), e); //$NON-NLS-1$
	}

	/**
	 * Removes automatically prepended and appended portion of the SOAPFault detail string.
	 * @param message string to be processed
	 * @return trimmed string
	 */
	private String trimSOAPFaultMessage(String message)
	{
		return message.replaceFirst("(.*?)Client received SOAP Fault from server: (.+) " //$NON-NLS-1$
				+ "Please see the server log to find more detail regarding exact cause of the failure.", "$2"); //$NON-NLS-1$ //$NON-NLS-2$
	}

}