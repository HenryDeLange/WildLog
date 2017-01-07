package wildlog.ui.helpers;

import java.awt.Toolkit;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import org.apache.logging.log4j.Level;
import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.NumberFormatter;
import wildlog.WildLogApp;


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
        // Maak dat as mens die data uitvee dat dit default na 0, en dat net die regte characters gewys word
        formattedTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent inEvent) {
                JFormattedTextField editedText = (JFormattedTextField) inEvent.getSource();
                double minValue;
                double maxValue;
                if (((SpinnerNumberModel) inSpinner.getModel()).getMinimum() instanceof Integer) {
                    minValue = (int)((SpinnerNumberModel) inSpinner.getModel()).getMinimum();
                    maxValue = (int)((SpinnerNumberModel) inSpinner.getModel()).getMaximum();
                }
                else {
                    minValue = (double)((SpinnerNumberModel)inSpinner.getModel()).getMinimum();
                    maxValue = (double)((SpinnerNumberModel)inSpinner.getModel()).getMaximum();
                }
                if (inEvent.getKeyChar() == KeyEvent.VK_BACK_SPACE || inEvent.getKeyChar() == KeyEvent.VK_DELETE
                        || (inEvent.getKeyChar() >= '0' && inEvent.getKeyChar() <= '9')
                        || (inEvent.getKeyChar() == '.' && ((SpinnerNumberModel) inSpinner.getModel()).getMinimum() instanceof Double)
                        || (inEvent.getKeyChar() == '-' && minValue < 0)) {
                    // Daar is 'n issue as mens twee keer na mekaar die hele text select en dan bv 23 op die spnHours tik, 
                    // dan raak hy confused op die tweede een omdat die stelsel self daai extra 0 byvoeg vooraan...
                    // So vir eers haal ek die 00 af van die spinner dat dinge darm half werk
                    String textAfterEdit = editedText.getText().substring(0, editedText.getSelectionStart())
                            + inEvent.getKeyChar() + editedText.getText().substring(editedText.getSelectionEnd());
                    double valueAfterEdit;
                    if (((SpinnerNumberModel) inSpinner.getModel()).getMinimum() instanceof Integer) {
                        try {
                            valueAfterEdit = Integer.parseInt(textAfterEdit);
                        }
                        catch (NumberFormatException ex) {
                            valueAfterEdit = 0;
                        }
                    }
                    else {
                        try {
                            valueAfterEdit = Double.parseDouble(textAfterEdit);
                        }
                        catch (NumberFormatException ex) {
                            valueAfterEdit = 0.0;
                        }
                    }
                    // Show 0 when all text is deleted
                    if (editedText.getText().isEmpty()) {
                        editedText.setText("0");
                    }
                    if (valueAfterEdit > maxValue || valueAfterEdit < minValue) {
                        inEvent.consume();
                    }
                }
                else {
                    inEvent.consume();
                }
            }
        });
        // Sit die code by om situasies te hanteer waar die text spasies, ens. bevat
        ((JTextComponent) inSpinner.getEditor().getComponent(0)).getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent inDocumentEvent) {
                processInput(inDocumentEvent);
            }

            @Override
            public void removeUpdate(DocumentEvent inDocumentEvent) {
                processInput(inDocumentEvent);
            }

            @Override
            public void insertUpdate(DocumentEvent inDocumentEvent) {
                processInput(inDocumentEvent);
            }

            private void processInput(final DocumentEvent inDocumentEvent) {
                // Get rid of non-numeric text (that got pasted in via the clipboard)
                try {
                    final String currentText = inDocumentEvent.getDocument().getText(0, inDocumentEvent.getDocument().getLength());
                    if (!currentText.trim().isEmpty() && !isOnlyNumberChars(currentText)) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                String newText = currentText.replaceAll("[^\\d.-]", "");
                                ((JTextComponent) inSpinner.getEditor().getComponent(0)).setText(newText);
                                if (WildLogApp.getApplication().getWildLogOptions().isEnableSounds()) {
                                    Toolkit.getDefaultToolkit().beep();
                                }
                            }
                        });
                    }
                }
                catch (BadLocationException ex) {
                    WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                }
            }
        });
    }
    
    private static boolean isOnlyNumberChars(String inText) {
        for (char c : inText.toCharArray()) {
            if (!(c >= '0' && c <= '9' || c == '.' || c == '-' || c == '+')) {
                return false;
            }
        }
        return true;
    }

}
