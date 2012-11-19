package wildlog.ui.helpers;

import javax.swing.JTabbedPane;
import wildlog.ui.panels.*;
import org.jdesktop.application.Application;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Visit;
import wildlog.data.dbi.DBI;
import wildlog.WildLogApp;
import wildlog.ui.panels.interfaces.PanelCanSetupHeader;


public final class UtilPanelGenerator {
    private static DBI dbi = ((WildLogApp)Application.getInstance()).getDBI();

    // METHODS:
    public static PanelElement getNewElementPanel() {
        return new PanelElement(new Element());
    }

    public static PanelElement getElementPanel(String inPrimaryName) {
        Element tempElement = dbi.find(new Element(inPrimaryName));
        return new PanelElement(tempElement);
    }

    public static PanelLocation getNewLocationPanel() {
        return new PanelLocation(new Location());
    }

    public static PanelLocation getLocationPanel(String inName) {
        Location tempLocation = dbi.find(new Location(inName));
        return new PanelLocation(tempLocation);
    }

    public static PanelVisit getNewVisitPanel(Location inLocation) {
        return new PanelVisit(inLocation, new Visit());
    }

    public static PanelVisit getVisitPanel(Location inLocation, String inName) {
        Visit tempVisit = dbi.find(new Visit(inName));
        return new PanelVisit(inLocation, tempVisit);
    }

    public static void addPanelAsTab(PanelCanSetupHeader inPanel, JTabbedPane inTabPane) {
        if (inPanel != null && inTabPane != null) {
            inTabPane.add(inPanel);
            inPanel.setupTabHeader();
            inTabPane.setSelectedComponent(inPanel);
        }
    }

}
