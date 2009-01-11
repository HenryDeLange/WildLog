package wildlog.data.dbi;

import java.util.List;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Foto;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.MapPoint;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;


public interface DBI {
    public void close();
    public void doBackup();
    public void exportHTML(Element inElement);
    public void exportHTML(Location inLocation);
    
    public Element find(Element inElement);
    public Location find(Location inLocation);
    public Visit find(Visit inVisit);
    public Sighting find(Sighting inSighting);
    public Foto find(Foto inFoto);
    public MapPoint find(MapPoint inMapPoint);
    
    public List<Element> list(Element inElement);
    public List<Location> list(Location inLocation);
    public List<Visit> list(Visit inVisit);
    public List<Sighting> list(Sighting inSighting);
    public List<Foto> list(Foto inFoto);
    public List<MapPoint> list(MapPoint inMapPoint);
    
    public void createOrUpdate(Element inElement);
    public void createOrUpdate(Location inLocation);
    public void createOrUpdate(Visit inVisit);
    public void createOrUpdate(Sighting inSighting);
    public void createOrUpdate(Foto inFoto);
    public void createOrUpdate(MapPoint inMapPoint);
    
    public void delete(Element inElement);
    public void delete(Location inLocation);
    public void delete(Visit inVisit);
    public void delete(Sighting inSighting);
    public void delete(Foto inFoto);
    public void delete(MapPoint inMapPoint);

}
