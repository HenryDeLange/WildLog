/*
 * LatLonConverter.java is part of WildLog
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


package wildlog.utils;

import wildlog.data.enums.Latitudes;
import wildlog.data.enums.Longitudes;

/**
 *
 * @author DeLange
 */
public class LatLonConverter {

    public static float getDecimalDegree(Latitudes inLatitudes, int inDegrees, int inMinutes, int inSeconds) {
        if (inLatitudes != null) {
            if (inLatitudes.equals(Latitudes.SOUTH))
                return -1*(inDegrees + (inMinutes + inSeconds/60.0f)/60.0f);
            if (inLatitudes.equals(Latitudes.NORTH))
                return inDegrees + (inMinutes + inSeconds/60.0f)/60.0f;
        }
        return 0;
    }

    public static float getDecimalDegree(Longitudes inLongitudes, int inDegrees, int inMinutes, int inSeconds) {
        if (inLongitudes != null) {
            if (inLongitudes.equals(Longitudes.EAST))
                return inDegrees + (inMinutes + inSeconds/60.0f)/60.0f;
            if (inLongitudes.equals(Longitudes.WEST))
                return -1*(inDegrees + (inMinutes + inSeconds/60.0f)/60.0f);
        }
        return 0;
    }

    // DD -> Deg, Min, Sec
//    double degrees = (int)dd;
//    double ddMinutes = (Math.Abs(dd) - Math.Abs(degrees));
//    double minutes = (int)(ddMinutes * 60);
//    double seconds = ((ddMinutes * 60 - minutes) * 60.0);

}
