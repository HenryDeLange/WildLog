/*
 * DBI_db4o.java is part of WildLog
 *
 * Copyright (C) 2009 Henry James de Lange
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package wildlog.data.dbi;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Predicate;
import com.db4o.query.Query;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Foto;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.MapPoint;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.util.SightingCounter;
import wildlog.data.dataobjects.Visit;
import wildlog.data.dataobjects.util.IndicatorOfVersionAndUpdate;
import wildlog.data.enums.ElementType;
import wildlog.data.enums.Habitat;


// Look at maybe callign commit only at certain times, maybe even only when database is closed? My be faster or slower...
public class DBI_db4o implements DBI {
    private final String filePath = File.separatorChar + "WildLog" + File.separatorChar + "Data" + File.separatorChar + "wildlog.wld";
    private ObjectContainer db;
    private SightingCounter counter;
    private final int currentDatabaseVersion = 2;
    
    // Contructor:
    public DBI_db4o() {
        // Database File
        File tempFile = new File(filePath);
        // Can remove delete at later point...
        //tempFile.delete();

        // Cascade Update
        Db4o.configure().objectClass(Element.class).cascadeOnUpdate(true);
        Db4o.configure().objectClass(Location.class).cascadeOnUpdate(true);
        Db4o.configure().objectClass(Visit.class).cascadeOnUpdate(true);
        Db4o.configure().objectClass(Sighting.class).objectField("fotos").cascadeOnUpdate(true); // Or cacade all, but not Elements

        // Cascade Delete
        Db4o.configure().objectClass(Element.class).cascadeOnDelete(true);
        Db4o.configure().objectClass(Location.class).cascadeOnDelete(true);
        Db4o.configure().objectClass(Visit.class).cascadeOnDelete(true);
        Db4o.configure().objectClass(Sighting.class).objectField("fotos").cascadeOnDelete(true); // Or cacade all, but not Elements

        // Indexes
        Db4o.configure().objectClass(Element.class).objectField("primaryName").indexed(true);
        Db4o.configure().objectClass(Element.class).objectField("type").indexed(true);
        Db4o.configure().objectClass(Location.class).objectField("name").indexed(true);
        Db4o.configure().objectClass(Location.class).objectField("visits").indexed(true);
        Db4o.configure().objectClass(Visit.class).objectField("name").indexed(true);
        Db4o.configure().objectClass(Visit.class).objectField("startDate").indexed(true);
        Db4o.configure().objectClass(Visit.class).objectField("endDate").indexed(true);
        Db4o.configure().objectClass(Visit.class).objectField("sightings").indexed(true);
        Db4o.configure().objectClass(Sighting.class).objectField("date").indexed(true);
        Db4o.configure().objectClass(Sighting.class).objectField("element").indexed(true);
        Db4o.configure().objectClass(Sighting.class).objectField("location").indexed(true);
        Db4o.configure().objectClass(Foto.class).objectField("name").indexed(true);
        Db4o.configure().objectClass(Foto.class).objectField("fileLocation").indexed(true);

        // Open database
        db = Db4o.openFile(filePath);

        // Check the SightingCounter object
        ObjectSet<SightingCounter> tempList = db.get(new SightingCounter());
        if (tempList.hasNext()) {
            counter = tempList.next();
        }
        else {
            counter = new SightingCounter();
            // Start at 1, thus no sighting should ever have counter = 0
            counter.setCount(1);
        }

        // Check to see if it is needed to do an update of the database
        ObjectSet<IndicatorOfVersionAndUpdate> results = db.get(new IndicatorOfVersionAndUpdate());
        if (results.hasNext()) {
            IndicatorOfVersionAndUpdate temp = results.next();
            if (temp.getDatabaseVersion() == 1) doUpdate_v1();
        }
        else {
            IndicatorOfVersionAndUpdate temp = new IndicatorOfVersionAndUpdate();
            temp.setDatabaseVersion(currentDatabaseVersion);
            db.set(temp);
        }
        
    }
    
    // Methods:
    @Override
    public void close() {
        db.close();
    }

    @Override
    public void doBackup() {
        close();
        File fromFile = new File(filePath);
        File toFile = new File(File.separatorChar + "WildLog" + File.separatorChar + "Backup" + File.separatorChar + "backup.wld");
        toFile.mkdirs();
        FileInputStream fileInput = null;
        FileOutputStream fileOutput = null;
        try {
            if (toFile.exists()) toFile.delete();
            fileInput = new FileInputStream(fromFile);
            fileOutput = new FileOutputStream(toFile);
            byte[] tempBytes = new byte[(int)fromFile.length()];
            fileInput.read(tempBytes);
            fileOutput.write(tempBytes);
            fileOutput.flush();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        finally {
            try {
                fileInput.close();
                fileOutput.close();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public boolean isSightingUnique(Sighting inSighting) {
        Sighting tempSighting = new Sighting(inSighting.getDate(), inSighting.getElement(), inSighting.getLocation(), 0);
        // Important this method also gives the Sighting a SightingCounter, 
        // thus makeing it unique if it doesn't have one...
        if (inSighting.getSightingCounter() > 0)
            tempSighting.setSightingCounter(inSighting.getSightingCounter());
        else {
            inSighting.setSightingCounter(counter.getCount());
            counter.increase();
            db.set(counter);
        }
        Sighting searchSighting = find(tempSighting);
        if (searchSighting == null) {
            return true;
        }
        if (searchSighting.equals(inSighting) && list(tempSighting).size() == 1) {
            return true;
        }
        return false;
    }

    //@Override
    //public void refresh(Object inObject) {
    //    db.ext().refresh(inObject, 7);
    //}

    @Override
    public Element find(Element inElement) {
        ObjectSet<Element> tempList = db.get(inElement);
        if (tempList.hasNext()) return tempList.next();
        else return null;
    }

    @Override
    public Location find(Location inLocation) {
        if (inLocation == null) return null;
        ObjectSet<Location> tempList = db.get(inLocation);
        if (tempList.hasNext()) return tempList.next();
        else return null;
    }

    @Override
    public Visit find(Visit inVisit) {
        ObjectSet<Visit> tempList = db.get(inVisit);
        if (tempList.hasNext()) return tempList.next();
        else return null;
    }

    @Override
    public Sighting find(Sighting inSighting) {
        ObjectSet<Sighting> tempList = db.get(inSighting);
        if (tempList.hasNext()) return tempList.next();
        else return null;
    }

    @Override
    public Foto find(Foto inFoto) {
        ObjectSet<Foto> tempList = db.get(inFoto);
        if (tempList.hasNext()) return tempList.next();
        else return null;
    }

    @Override
    public MapPoint find(MapPoint inMapPoint) {
        ObjectSet<MapPoint> tempList = db.get(inMapPoint);
        if (tempList.hasNext()) return tempList.next();
        else return null;
    }
    
    
    @Override
    public List<Element> list(Element inElement) {
        ObjectSet<Element> tempList = db.get(inElement);
        return tempList;
    }
    
    @Override
    public List<Location> list(Location inLocation) {
        ObjectSet<Location> tempList = db.get(inLocation);
        return tempList;
    }

    @Override
    public List<Visit> list(Visit inVisit) {
        ObjectSet<Visit> tempList = db.get(inVisit);
        return tempList;
    }

    @Override
    public List<Sighting> list(Sighting inSighting) {
        ObjectSet<Sighting> tempList = db.get(inSighting);
        return tempList;
    }

    @Override
    public List<Foto> list(Foto inFoto) {
       ObjectSet<Foto> tempList = db.get(inFoto);
       return tempList;
    }

    @Override
    public List<MapPoint> list(MapPoint inMapPoint) {
       ObjectSet<MapPoint> tempList = db.get(inMapPoint);
       return tempList;
    }


    @Override
    public List<Element> searchElementOnType(final ElementType inType) {
        List<Element> tempList = db.query(new Predicate<Element>() {
            public boolean match(Element temp) {
                if (inType != null) {
                    if (!(inType.equals(temp.getType()))) {
                        return false;
                    }
                }
                return true;
            }
        });
        if (tempList.size() > 0) return tempList;
        else return new ArrayList<Element>(0);
    }

    @Override
    public List<Element> searchElementOnPrimaryName(final String inPrimaryName) {
        List<Element> tempList = db.query(new Predicate<Element>() {
            public boolean match(Element temp) {
                if (inPrimaryName != null) {
                    if (!(temp.getPrimaryName().toLowerCase().indexOf(inPrimaryName.toLowerCase()) > -1)) {
                        return false;
                    }
                }
                return true;
            }
        });
        if (tempList.size() > 0) return tempList;
        else return new ArrayList<Element>(0);
    }

    @Override
    public List<Element> searchElementOnTypeAndPrimaryName(final ElementType inType, final String inPrimaryName) {
        List<Element> tempList = db.query(new Predicate<Element>() {
            public boolean match(Element temp) {
                if (inType != null) {
                    if (!(inType.equals(temp.getType()))) {
                        return false;
                    }
                }
                if (inPrimaryName != null) {
                    if (!(temp.getPrimaryName().toLowerCase().indexOf(inPrimaryName.toLowerCase()) > -1)) {
                        return false;
                    }
                }
                return true;
            }
        });
        if (tempList.size() > 0) return tempList;
        else return new ArrayList<Element>(0);
    }

    @Override
    public List<Location> searchLocationOnName(final String inName) {
        List<Location> tempList = db.query(new Predicate<Location>() {
            public boolean match(Location temp) {
                if (inName != null) {
                    if (!(temp.getName().toLowerCase().indexOf(inName.toLowerCase()) > -1)) {
                        return false;
                    }
                }
                return true;
            }
        });
        if (tempList.size() > 0) return tempList;
        else return new ArrayList<Location>(0);
    }
    
    
    @Override
    public boolean createOrUpdate(Element inElement) {
        Element tempElement = find(new Element(inElement.getPrimaryName()));
        if (tempElement == null) {
            db.set(inElement);
            db.commit();
            return true;
        }
        if (tempElement.equals(inElement) && list(new Element(inElement.getPrimaryName())).size() == 1) {
            db.set(inElement);
            db.commit();
            return true;
        }
        return false;
    }
    
    @Override
    public boolean createOrUpdate(Location inLocation) {
        Location tempLocation = find(new Location(inLocation.getName()));
        if (tempLocation == null) {
            db.set(inLocation);
            db.commit();
            return true;
        }
        if (tempLocation.equals(inLocation) && list(new Location(inLocation.getName())).size() == 1) {
            db.set(inLocation);
            db.commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean createOrUpdate(Visit inVisit) {
        Visit tempVisit = find(new Visit(inVisit.getName()));
        if (tempVisit == null) {
            db.set(inVisit);
            db.commit();
            return true;
        }
        if (tempVisit.equals(inVisit) && list(new Visit(inVisit.getName())).size() == 1) {
            db.set(inVisit);
            db.commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean createOrUpdate(Sighting inSighting) {
        if (isSightingUnique(inSighting)) {
            db.set(inSighting);
            db.commit();
            return true;
        }
        else
            return false;
    }

    @Override
    public boolean createOrUpdate(Foto inFoto) {
        db.set(inFoto);
        db.commit();
        return false;
    }

    @Override
    public boolean createOrUpdate(MapPoint inMapPoint) {
        db.set(inMapPoint);
        db.commit();
        return false;
    }
    
    
    @Override
    public boolean delete(Element inElement) {
        inElement = find(inElement);
        // Find any sightings that have this element and delete them
        Sighting tempSighting = new Sighting();
        tempSighting.setElement(inElement);
        final List<Sighting>  sightings = list(tempSighting);
        if (sightings != null) {
            for (int t = 0; t < sightings.size(); t++) {
                // Also delete sighting from Visit
                final int index = t;
                List<Visit> tempList = db.query(new Predicate<Visit>() {
                    public boolean match(Visit temp) {
                        if (temp.getSightings().contains(sightings.get(index)))
                            return true;
                        else
                            return false;
                    }
                });
                for (int i = 0; i < tempList.size(); i++) {
                    tempList.get(i).getSightings().remove(sightings.get(index));
                    createOrUpdate(tempList.get(i));
                }
                delete(sightings.get(t));
                // The code below used to work but broke.. Above is a work around.
//                // Also delete sighting from Visit
//                Visit tempVisit = new Visit();
//                tempVisit.getSightings().add(sightings.get(t));
//                tempVisit = find(tempVisit);
//                tempVisit.getSightings().remove(sightings.get(t));
//                createOrUpdate(tempVisit);
//                delete(sightings.get(t));
            }
        }
        // This leaves the Sightings still intact, but with null Elements,
        // thats why they are removed above.
        db.delete(inElement);
        db.commit();
        return false;
    }

    @Override
    public boolean delete(Location inLocation) {
        inLocation = find(inLocation);
        db.delete(inLocation);
        db.commit();
        return false;
    }

    @Override
    public boolean delete(Visit inVisit) {
        inVisit = find(inVisit);
        db.delete(inVisit);
        db.commit();
        return false;
    }

    @Override
    public boolean delete(Sighting inSighting) {
        inSighting = find(inSighting);
        db.delete(inSighting);
        db.commit();
        return false;
    }

    @Override
    public boolean delete(Foto inFoto) {
        // NOTE: that the actual files are not deleted currently... Moet huidiglik verkieslik nie delete nie
        inFoto = find(inFoto);
        db.delete(inFoto);
        db.commit();
        return false;
    }

    @Override
    public boolean delete(MapPoint inMapPoint) {
        inMapPoint = find(inMapPoint);
        db.delete(inMapPoint);
        db.commit();
        return false;
    }

    // Private Update Method
    private void doUpdate_v1() {
        System.out.println("Performing database update...");
        // Update ENUMs
        // Note: New fields can just be added, but changes in fields needs to be re-updated
        Query query = db.query();
        query.constrain(Habitat.class);
        query.descend("text").constrain("Name Karoo");
        ObjectSet result = query.execute();
        for(int x = 0; x < result.size(); x++) {
            Habitat hab = (Habitat)result.get(x);
            hab.fix("Nama Karoo");
            db.set(hab);
        }
        query = db.query();
        query.constrain(Element.class);
        result = query.execute();
        for(int x = 0; x < result.size(); x++) {
            Element element = (Element)result.get(x);
            element.setLifespan(element.getBreedingAge());
            element.setBreedingAge("");
            db.set(element);
        }

        // Other Updates
        // Note: most can be done implicitely in the code and don't need explicit updates...

        // Set IndicatorOfVersionAndUpdate to represent changes
        ObjectSet<IndicatorOfVersionAndUpdate> results = db.get(new IndicatorOfVersionAndUpdate());
        if (results.hasNext()) {
            IndicatorOfVersionAndUpdate temp = results.next();
            temp.setDatabaseVersion(2);
            db.set(temp);
        }

        // Commit Changes
        db.commit();
    }
    
    // NATIVE QUERY EXAMPLE:
//        List<Sighting> tempList = db.query(new Predicate<Sighting>() {
//            public boolean match(Sighting temp) {
//                if (inSighting.getDate() != null) {
//                    if (!inSighting.getDate().equals(temp.getDate())) {
//                        return false;
//                    }
//                }
//                if (inSighting.getElement() != null) {
//                    if (!inSighting.getElement().equals(temp.getElement())) {
//                        return false;
//                    }
//                }
//                if (inSighting.getLocation() != null) {
//                    if (!inSighting.getLocation().equals(temp.getLocation())) {
//                        return false;
//                    }
//                }
//                return true;
//            }
//        });
//        if (tempList.size() > 0) return tempList.get(0);
//        else return new Sighting();
    
    
}
