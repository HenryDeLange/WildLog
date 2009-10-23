/*
 * DBI.java is part of WildLog
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

import java.util.Date;
import java.util.List;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Foto;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.MapPoint;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.enums.ElementType;


public interface DBI {
    public void close();
    public void doBackup();
    public void exportWLD(boolean inIncludeThumbnails);
    public void importWLD();

    public boolean isSightingUnique(Sighting inSighting);
    //public void refresh(Object inObject);
    
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

    public List<Element> searchElementOnType(ElementType inType);
    public List<Element> searchElementOnPrimaryName(String inPrimaryName);
    public List<Element> searchElementOnTypeAndPrimaryName(ElementType inType, String inPrimaryString);
    public List<Location> searchLocationOnName(String inName);
    public List<Sighting> searchSightingOnDate(Date inStartDate, Date inEndDate);
    
    public boolean createOrUpdate(Element inElement);
    public boolean createOrUpdate(Location inLocation);
    public boolean createOrUpdate(Visit inVisit);
    public boolean createOrUpdate(Sighting inSighting);
    public boolean createOrUpdate(Foto inFoto);
    public boolean createOrUpdate(MapPoint inMapPoint);
    
    public boolean delete(Element inElement);
    public boolean delete(Location inLocation);
    public boolean delete(Visit inVisit);
    public boolean delete(Sighting inSighting);
    public boolean delete(Foto inFoto);
    public boolean delete(MapPoint inMapPoint);

}
