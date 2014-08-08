package wildlog.ui.reports.helpers;

import javax.swing.ImageIcon;
import wildlog.utils.CountWrapper;


public class CountInfoWrapper <T> extends CountWrapper {
    private T object;
    private ImageIcon icon;
    private String name;

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }

    public ImageIcon getIcon() {
        return icon;
    }

    public void setIcon(ImageIcon icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
}
