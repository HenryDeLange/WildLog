package wildlog.ui.helpers;

import javax.swing.JTabbedPane;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Visit;
import wildlog.ui.panels.PanelElement;
import wildlog.ui.panels.PanelLocation;
import wildlog.ui.panels.PanelVisit;
import wildlog.ui.panels.bulkupload.BulkUploadPanel;
import wildlog.ui.panels.interfaces.PanelCanSetupHeader;


public final class UtilsPanelGenerator {

    private static PanelElement getNewElementPanel(WildLogApp inApp) {
        return new PanelElement(inApp, new Element());
    }

    private static PanelElement getElementPanel(WildLogApp inApp, long inID) {
        Element tempElement = inApp.getDBI().findElement(inID, null, false, Element.class);
        return new PanelElement(inApp, tempElement);
    }

    private static PanelLocation getNewLocationPanel(WildLogApp inApp) {
        return new PanelLocation(inApp, new Location());
    }

    private static PanelLocation getLocationPanel(WildLogApp inApp, long inID) {
        Location tempLocation = inApp.getDBI().findLocation(inID, null, false, Location.class);
        return new PanelLocation(inApp, tempLocation);
    }

    private static PanelVisit getNewVisitPanel(WildLogApp inApp, Location inLocation) {
        return new PanelVisit(inApp, inLocation, new Visit());
    }

    private static PanelVisit getVisitPanel(WildLogApp inApp, Location inLocation, long inID) {
        Visit tempVisit = inApp.getDBI().findVisit(inID, null, true, Visit.class);
        return new PanelVisit(inApp, inLocation, tempVisit);
    }

    /**
     * This will open a tab for the provided panel, showing an existing tab if already opened
     * or showing a new tab if not already opened.
     */
    public static void openPanelAsTab(final WildLogApp inApp, final long inID, final PanelCanSetupHeader.TabTypes inTabType, final JTabbedPane inTabPane, final Location inLocationForVisit) {
        if (inID > 0 && inTabType != null && inTabPane != null) {
            int foundAt = -1;
            for (int t = 0; t < inTabPane.getTabCount(); t++) {
                if (inTabPane.getTabComponentAt(t) instanceof PanelCanSetupHeader.HeaderPanel) {
                    PanelCanSetupHeader.HeaderPanel headerPanel = (PanelCanSetupHeader.HeaderPanel)inTabPane.getTabComponentAt(t);
                    if (headerPanel.getID() == inID && headerPanel.getTabType() != null && inTabType.equals(headerPanel.getTabType())) {
                        foundAt = t;
                        inTabPane.setSelectedIndex(foundAt);
                        break;
                    }
                }
            }
            if (foundAt < 0) {
                // The tab wasn't found so we'll have to open a new one.
                PanelCanSetupHeader panelCanSetupHeader = null;
                if (PanelCanSetupHeader.TabTypes.LOCATION.equals(inTabType)) {
                    panelCanSetupHeader = getLocationPanel(inApp, inID);
                }
                else
                if (PanelCanSetupHeader.TabTypes.ELEMENT.equals(inTabType)) {
                    panelCanSetupHeader = getElementPanel(inApp, inID);
                }
                else
                if (PanelCanSetupHeader.TabTypes.VISIT.equals(inTabType)) {
                    panelCanSetupHeader = getVisitPanel(inApp, inLocationForVisit, inID);
                }
                else
                if (PanelCanSetupHeader.TabTypes.BULK_UPLOAD.equals(inTabType)) {
                    panelCanSetupHeader = getVisitPanel(inApp, inLocationForVisit, inID);
                }
                if (panelCanSetupHeader != null) {
                    inTabPane.add(panelCanSetupHeader);
                    panelCanSetupHeader.setupTabHeader(inTabType);
                    inTabPane.setSelectedComponent(panelCanSetupHeader);
                }
            }
        }
    }

    /**
     * This will open a new tab to use to add new locations, elements and visits from.
     */
    public static void openNewPanelAsTab(final WildLogApp inApp, final PanelCanSetupHeader.TabTypes inTabType, final JTabbedPane inTabPane, final Location inLocationForVisit) {
        if (inTabType != null && inTabPane != null) {
            PanelCanSetupHeader panelCanSetupHeader = null;
            if (PanelCanSetupHeader.TabTypes.LOCATION.equals(inTabType)) {
                panelCanSetupHeader = getNewLocationPanel(inApp);
            }
            else
            if (PanelCanSetupHeader.TabTypes.ELEMENT.equals(inTabType)) {
                panelCanSetupHeader = getNewElementPanel(inApp);
            }
            else
            if (PanelCanSetupHeader.TabTypes.VISIT.equals(inTabType)) {
                panelCanSetupHeader = getNewVisitPanel(inApp, inLocationForVisit);
            }
            if (panelCanSetupHeader != null) {
                inTabPane.add(panelCanSetupHeader);
                panelCanSetupHeader.setupTabHeader(inTabType);
                inTabPane.setSelectedComponent(panelCanSetupHeader);
            }
        }
    }

    /**
     * Removes the tab with the given name and type.
     */
    public static void removeOpenedTab(long inID, PanelCanSetupHeader.TabTypes inTabType, JTabbedPane inTabPane) {
        if (inID > 0 && inTabType != null && inTabPane != null) {
            for (int t = 0; t < inTabPane.getTabCount(); t++) {
                if (inTabPane.getTabComponentAt(t) instanceof PanelCanSetupHeader.HeaderPanel) {
                    PanelCanSetupHeader.HeaderPanel headerPanel = (PanelCanSetupHeader.HeaderPanel)inTabPane.getTabComponentAt(t);
                    if (inID == headerPanel.getID() && headerPanel.getTabType() != null && inTabType.equals(headerPanel.getTabType())) {
                        inTabPane.remove(t);
                        break;
                    }
                }
            }
        }
    }

    /**
     * Opens a new tab for the bulk upload.
     */
    public static void openBulkUploadTab(BulkUploadPanel inBulkUploadPanel, JTabbedPane inTabPane) {
        if (inBulkUploadPanel != null) {
            if (inBulkUploadPanel.isShowAsTab()) {
                inTabPane.add(inBulkUploadPanel);
                inBulkUploadPanel.setupTabHeader(PanelCanSetupHeader.TabTypes.BULK_UPLOAD);
                inTabPane.setSelectedComponent(inBulkUploadPanel);
            }
            else {
                inBulkUploadPanel.setVisible(false);
            }
        }
    }

}
