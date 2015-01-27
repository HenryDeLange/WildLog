package wildlog.data.dataobjects.interfaces;

import wildlog.WildLogApp;
import wildlog.ui.helpers.ProgressbarTask;

public interface DataObjectWithXML extends DataObjectBasicInfo {
    public String toXML(WildLogApp inApp, ProgressbarTask inProgressbarTask, boolean inIncludeSightings);
}
