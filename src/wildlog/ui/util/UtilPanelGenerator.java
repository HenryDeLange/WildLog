package wildlog.ui.util;

import wildlog.ui.panel.*;
import org.jdesktop.application.Application;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Visit;
import wildlog.data.dbi.DBI;
import wildlog.WildLogApp;


public class UtilPanelGenerator {
    private DBI dbi;
    
    // CONSTRUCTOR:
    public UtilPanelGenerator() {
        WildLogApp app = (WildLogApp) Application.getInstance();
        dbi = app.getDBI();
    }
    
    // METHODS:
    public PanelElement getNewElementPanel() {
        return new PanelElement(new Element());
    }
    
    public PanelElement getElementPanel(String inEnglishName) {
        Element tempElement = dbi.find(new Element(inEnglishName));
        return new PanelElement(tempElement);
    }
    
    public PanelLocation getNewLocationPanel() {
        return new PanelLocation(new Location());
    }
    
    public PanelLocation getLocationPanel(String inName) {
        Location tempLocation = dbi.find(new Location(inName));
        return new PanelLocation(tempLocation);
    }
    
    public PanelVisit getNewVisitPanel(Location inLocation) {
        return new PanelVisit(inLocation, new Visit());
    }
    
    public PanelVisit getVisitPanel(Location inLocation, String inName) {
        Visit tempVisit = dbi.find(new Visit(inName));
        return new PanelVisit(inLocation, tempVisit);
    }


}
