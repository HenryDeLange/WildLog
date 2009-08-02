/*
 * SightingWrapper.java is part of WildLog
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

package wildlog.data.dataobjects.wrappers;

import wildlog.data.dataobjects.Sighting;

/**
 * This class wraps a Sighting object in order to return just the Creature name as the toString() value.
 * @author Henry
 */
public class SightingWrapper {
    // Variables
    private Sighting sighting;
    private boolean isForLocation;

    // Contructor
    public SightingWrapper(Sighting inSighting, boolean inIsForLocation) {
        sighting = inSighting;
        isForLocation = inIsForLocation;
    }

    // Methods
    @Override
    public String toString() {
        if (isForLocation)
            return sighting.getElement().getPrimaryName() + " (" + sighting.getDate().getDate() + "-" + (sighting.getDate().getMonth()+1) + "-" + (sighting.getDate().getYear()+1900) + ")";
        else
            return sighting.getLocation().getName() + " (" + sighting.getDate().getDate() + "-" + (sighting.getDate().getMonth()+1) + "-" + (sighting.getDate().getYear()+1900) + ")";
    }

    public Sighting getSighting() {
        return sighting;
    }

}
