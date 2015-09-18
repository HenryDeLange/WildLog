package wildlog.data.dataobjects.interfaces;

import wildlog.WildLogApp;
import wildlog.html.utils.UtilsHTMLExportTypes;
import wildlog.ui.helpers.ProgressbarTask;

public interface DataObjectWithHTML extends DataObjectWithWildLogFile {
    public String toHTML(boolean inIsRecursive, boolean inIncludeImages, boolean inIsSummary, 
            WildLogApp inApp, UtilsHTMLExportTypes inExportType, ProgressbarTask inProgressbarTask);
}
