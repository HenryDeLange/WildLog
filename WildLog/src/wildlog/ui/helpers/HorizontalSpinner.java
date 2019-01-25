package wildlog.ui.helpers;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicSpinnerUI;

/**
 * Based on code from http://stackoverflow.com/questions/28903436/how-to-have-a-java-spinner-with-left-and-right-arrows
 */
public class HorizontalSpinner extends BasicSpinnerUI {

    public static ComponentUI createUI(JComponent inComponent) {
        return new HorizontalSpinner();
    }

    @Override
    protected Component createNextButton() {
        Component component = createArrowButton(SwingConstants.EAST);
        component.setName("Spinner.nextButton");
        installNextButtonListeners(component);
        return component;
    }

    @Override
    protected Component createPreviousButton() {
        Component component = createArrowButton(SwingConstants.WEST);
        component.setName("Spinner.previousButton");
        installPreviousButtonListeners(component);
        return component;
    }

    private Component createArrowButton(int inDirection) {
        JButton button = new BasicArrowButton(inDirection);
        button.setInheritsPopupMenu(true);
        return button;
    }

    @Override
    public void installUI(JComponent inComponent) {
        super.installUI(inComponent);
        inComponent.removeAll();
        inComponent.setLayout(new BorderLayout());
        inComponent.add(createNextButton(), BorderLayout.EAST);
        inComponent.add(createPreviousButton(), BorderLayout.WEST);
        inComponent.add(createEditor(), BorderLayout.CENTER);
    }

}
