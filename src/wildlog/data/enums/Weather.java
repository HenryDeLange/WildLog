/*
 * Weather.java is part of WildLog
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

package wildlog.data.enums;


public enum Weather {
    SUNNY("Sunny"),
    LIGHT_OVERCAST("Few clouds - Not raining"),
    HEAVY_OVERCAST("Many clouds - Not raining"),
    MIST("Mist"),
    LIGHT_RAIN("Light rain"),
    HEAVY_RAIN("Heavy rain"),
    OTHER("Other (Can specify in details)"),
    NONE("None");
    
    private final String text;
    
    Weather(String inText) {
        text = inText;
    }
    
    @Override
    public String toString() {
        return text;
    }

}
