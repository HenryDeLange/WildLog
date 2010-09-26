package wildlog.data.dataobjects.interfaces;

import wildlog.WildLogApp;

/**
 *
 * @author Henry
 */
public interface DataObjectWithHTML {
    public String toHTML(boolean inIsRecursive, boolean inIncludeImages, WildLogApp inApp);
}
