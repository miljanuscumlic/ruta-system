package rs.ruta.client.gui;

import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

class SearchNameDialog extends JDialog implements ActionListener, PropertyChangeListener
{
	private static final long serialVersionUID = -7052948397738881313L;
	private String typedText = null;
    private JTextField textField;
    private JOptionPane optionPane;
    private String btnString1 = "Enter";
    private String btnString2 = "Cancel";
    private String initialText;

    public SearchNameDialog(JFrame parent, String initialText)
    {
        super(parent, true);
        setTitle("Search rename");
        this.initialText = initialText;
        textField = new JTextField(initialText);

        String msgString1 = "Enter new name: ";
        Object[] components = { msgString1, textField };
        Object[] options = { btnString1, btnString2 };

        optionPane = new JOptionPane(components, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION,
                null, options, options[0]);

        setContentPane(optionPane);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        //Ensure the text field always gets the first focus.
        addComponentListener(new ComponentAdapter()
        {
            @Override
            public void componentShown(ComponentEvent ce)
            {
                textField.requestFocusInWindow();
                textField.selectAll();
            }
        });

        //Register an event handler that puts the text into the option pane.
        textField.addActionListener(this);

        //Register an event handler that reacts to option pane state changes.
        optionPane.addPropertyChangeListener(this);
        pack();
        setLocationRelativeTo(parent);
    }

    /*
     * Handles events for the text field.
     */
    @Override
    public void actionPerformed(ActionEvent e)
    {
        optionPane.setValue(btnString1);
    }

    /*
     * Reacts to state changes in the option pane.
     */
    @Override
    public void propertyChange(PropertyChangeEvent e)
    {
        String prop = e.getPropertyName();

        if (isVisible() && (e.getSource() == optionPane) &&
                (JOptionPane.VALUE_PROPERTY.equals(prop) || JOptionPane.INPUT_VALUE_PROPERTY.equals(prop)))
        {
            Object value = optionPane.getValue();
            if (value == JOptionPane.UNINITIALIZED_VALUE)
                return;

            //Reset the JOptionPane's value. If you don't do this, then if the user
            //presses the same button next time, no property change event will be fired.
            optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);

            if (btnString1.equals(value))
            {
                typedText = textField.getText().trim();
                if (!"".equals(typedText))
                {
                	if(initialText.equals(typedText))
                		typedText = null;
                	dispose();
                }
                else //text was invalid
                {
                    textField.selectAll();
                    JOptionPane.showMessageDialog(this, "New name must not be an empty string!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    typedText = null;
                    textField.requestFocusInWindow();
                }
            }
            else //user closed dialog or clicked cancel
            {
                typedText = null;
                dispose();
            }
        }
    }

    /**
     * Returns new {@link Search} name typed by the user, or {@code null} if the typed string was invalid
     * or the same as the previous one.
     * @return new {@code Search} name or {@code null}
     */
    public String getSearchName()
    {
        return typedText;
    }

    public static void main(String... args)
    {
        EventQueue.invokeLater(() -> new SearchNameDialog(null, "").setVisible(true));
    }
}