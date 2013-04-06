package wildlog.data.dataobjects.interfaces;

import wildlog.WildLogApp;
import wildlog.html.utils.UtilsHTML;

public interface DataObjectWithHTML {
    public String toHTML(boolean inIsRecursive, boolean inIncludeImages, WildLogApp inApp, UtilsHTML.ImageExportTypes inExportType);
}
