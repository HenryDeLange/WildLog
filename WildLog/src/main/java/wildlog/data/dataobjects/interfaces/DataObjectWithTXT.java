package wildlog.data.dataobjects.interfaces;

import wildlog.WildLogApp;
import wildlog.ui.helpers.ProgressbarTask;

public interface DataObjectWithTXT extends DataObjectBasicInfo {
    public String toTXT(WildLogApp inApp, ProgressbarTask inProgressbarTask);
}
