package wildlog.ui.utils;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;


public abstract class WildLogMainView extends JFrame {
    
    public abstract boolean closeAllTabs();

    public abstract void refreshHomeTab();

    public abstract JTabbedPane getTabbedPane();
    
    public abstract void browseSelectedElement(Element inElement);

    public abstract void browseSelectedLocation(Location inLocation);

    public abstract void browseSelectedVisit(Visit inVisit);
    
    public abstract void browseSelectedSighting(Sighting inSighting);
    
}
