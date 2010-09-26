package wildlog.data.dataobjects.interfaces;

import KmlGenerator.objects.KmlEntry;
import wildlog.WildLogApp;

/**
 *
 * @author Henry
 */
public interface DataObjectWithKML {
    public KmlEntry toKML(int inID, WildLogApp inApp);
}
