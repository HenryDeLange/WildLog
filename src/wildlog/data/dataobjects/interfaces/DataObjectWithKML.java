package wildlog.data.dataobjects.interfaces;

import wildlog.maps.kml.generator.KmlEntry;
import wildlog.WildLogApp;

public interface DataObjectWithKML {
    public KmlEntry toKML(int inID, WildLogApp inApp);
}
