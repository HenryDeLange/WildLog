package wildlog.ui.helpers;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import javax.swing.text.NumberFormatter;


public class SpinnerFixer {

    private SpinnerFixer() {
    }

    public static void configureSpinners(final JSpinner inSpinner) {
        // Fix die issue met spinners se selection all
        ((JSpinner.NumberEditor)inSpinner.getEditor()).getTextField().addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (e.getSource() instanceof JTextComponent) {
                        final JTextComponent textComponent=((JTextComponent)e.getSource());
                        SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    textComponent.selectAll();
                                }
                            }
                        );
                    }
                }
                @Override
                public void focusLost(FocusEvent e) {
                }
        });
        // Maak dat die spinner datelik sy changes stuur na die model, want anders maak dit staat op die focus change
        // en baie components is nie focusable nie, so dan werk dinge nie reg nie...
        JFormattedTextField formattedTextField = ((JSpinner.NumberEditor) inSpinner.getEditor()).getTextField();
        ((NumberFormatter) formattedTextField.getFormatter()).setCommitsOnValidEdit(true);
        // Maak dat as mens die data uitvee dat dit default na die min value
        formattedTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent inEvent) {
                JFormattedTextField editedText = (JFormattedTextField) inEvent.getSource();
                if (inEvent.getKeyChar() == KeyEvent.VK_BACK_SPACE || inEvent.getKeyChar() == KeyEvent.VK_DELETE
                        || (inEvent.getKeyChar() >= '0' && inEvent.getKeyChar() <= '9')
                        || (inEvent.getKeyChar() >= '.' && ((SpinnerNumberModel) inSpinner.getModel()).getMinimum() instanceof Double)) {
                    int number = -1;
                    try {
                        number = Integer.parseInt(editedText.getText());
                    }
                    catch (NumberFormatException ex) {
                        // Do nothing since these can be empty string or even characters at this point in time we don't know nor care :P
                    }
// FIXME: Daar is 'n issue as mens twee keer na mekaar die hele text select en dan bv 23 op die spnHours tik, dan raak hy confused op die tweede een omdat die stelsel self daai extra 0 byvoeg vooraan... So vir eers haal ek die 00 af van die spinner dat dinge darm half werk
                    String textAfterEdit = editedText.getText().substring(0, editedText.getSelectionStart())
                            + inEvent.getKeyChar() + editedText.getText().substring(editedText.getSelectionEnd());
                    double minValue;
                    double maxValue;
                    double valueAfterEdit;
                    if (((SpinnerNumberModel) inSpinner.getModel()).getMinimum() instanceof Integer) {
                        minValue = (int)((SpinnerNumberModel)inSpinner.getModel()).getMinimum();
                        maxValue = (int)((SpinnerNumberModel)inSpinner.getModel()).getMaximum();
                        try {
                            valueAfterEdit = Integer.parseInt(textAfterEdit);
                        }
                        catch (NumberFormatException ex) {
                            valueAfterEdit = 0;
                        }
                    }
                    else {
                        minValue = (double)((SpinnerNumberModel)inSpinner.getModel()).getMinimum();
                        maxValue = (double)((SpinnerNumberModel)inSpinner.getModel()).getMaximum();
                        try {
                            valueAfterEdit = Double.parseDouble(textAfterEdit);
                        }
                        catch (NumberFormatException ex) {
                            valueAfterEdit = 0.0;
                        }
                    }
                    if (editedText.getText().isEmpty() && number != minValue) {
                        if (((SpinnerNumberModel)inSpinner.getModel()).getMinimum() instanceof Integer) {
                            editedText.setText(""+((SpinnerNumberModel)inSpinner.getModel()).getMinimum());
                        }
                        else {
                            Double tempValue = (Double) ((SpinnerNumberModel)inSpinner.getModel()).getMinimum();
                            editedText.setText(""+tempValue.intValue());
                        }
                    }
                    if (valueAfterEdit > maxValue) {
                        inEvent.consume();
                    }
                }
                else {
                    inEvent.consume();
                }
            }
        });
    }

}
