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
    
    private String text;
    
    Weather(String inText) {
        text = inText;
    }
    
    @Override
    public String toString() {
        return text;
    }

    public static Weather getEnumFromText(String inText) {
        if (inText.equalsIgnoreCase(SUNNY.text)) return SUNNY;
        if (inText.equalsIgnoreCase(LIGHT_OVERCAST.text)) return LIGHT_OVERCAST;
        if (inText.equalsIgnoreCase(HEAVY_OVERCAST.text)) return HEAVY_OVERCAST;
        if (inText.equalsIgnoreCase(MIST.text)) return MIST;
        if (inText.equalsIgnoreCase(LIGHT_RAIN.text)) return LIGHT_RAIN;
        if (inText.equalsIgnoreCase(HEAVY_RAIN.text)) return HEAVY_RAIN;
        if (inText.equalsIgnoreCase(OTHER.text)) return OTHER;
        if (inText.equalsIgnoreCase(NONE.text)) return NONE;
        return NONE;
    }

}
