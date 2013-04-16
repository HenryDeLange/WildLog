package wildlog.ui.helpers;

import javax.swing.JTabbedPane;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Visit;
import wildlog.ui.panels.*;
import wildlog.ui.panels.interfaces.PanelCanSetupHeader;


public final class UtilPanelGenerator {

    // METHODS:
    public static PanelElement getNewElementPanel(WildLogApp inApp) {
        return new PanelElement(inApp, new Element());
    }

    public static PanelElement getElementPanel(WildLogApp inApp, String inPrimaryName) {
        Element tempElement = inApp.getDBI().find(new Element(inPrimaryName));
        return new PanelElement(inApp, tempElement);
    }

    public static PanelLocation getNewLocationPanel(WildLogApp inApp) {
        return new PanelLocation(inApp, new Location());
    }

    public static PanelLocation getLocationPanel(WildLogApp inApp, String inName) {
        Location tempLocation = inApp.getDBI().find(new Location(inName));
        return new PanelLocation(inApp, tempLocation);
    }

    public static PanelVisit getNewVisitPanel(WildLogApp inApp, Location inLocation) {
        return new PanelVisit(inApp, inLocation, new Visit());
    }

    public static PanelVisit getVisitPanel(WildLogApp inApp, Location inLocation, String inName) {
        Visit tempVisit = inApp.getDBI().find(new Visit(inName));
        return new PanelVisit(inApp, inLocation, tempVisit);
    }

    public static void addPanelAsTab(PanelCanSetupHeader inPanel, JTabbedPane inTabPane) {
        if (inPanel != null && inTabPane != null) {
            inTabPane.add(inPanel);
            inPanel.setupTabHeader();
            inTabPane.setSelectedComponent(inPanel);
        }
    }

}
