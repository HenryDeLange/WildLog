package wildlog.ui.panels.bulkupload.helpers;

import javax.swing.JComponent;
import wildlog.data.dataobjects.Element;


public class ComboBoxSuggestedElementWrapper {
    private Element element;
    private JComponent renderedCell;

    
    public ComboBoxSuggestedElementWrapper(Element element, JComponent renderedCell) {
        this.element = element;
        this.renderedCell = renderedCell;
    }

    
    @Override
    public boolean equals(Object inObject) {
        if (inObject instanceof ComboBoxSuggestedElementWrapper) {
            return element.getID() == ((ComboBoxSuggestedElementWrapper) inObject).getElement().getID();
        }
        return (this == inObject);
    }
    
    @Override
    public int hashCode() {
        return Long.hashCode(element.getID());
    }

    @Override
    public String toString() {
        return element.toString();
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public JComponent getRenderedCell() {
        return renderedCell;
    }

    public void setRenderedCell(JComponent renderedCell) {
        this.renderedCell = renderedCell;
    }
    
}
