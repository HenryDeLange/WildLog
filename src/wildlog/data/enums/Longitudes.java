/*
 * Longitudes.java is part of WildLog
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

public enum Longitudes {
    EAST("E", "East (+)"),
    WEST("W", "West (-)"),
    NONE("", "None");

    private String key;
    private String text;

    Longitudes(String inKey, String inText) {
        key = inKey;
        text = inText;
    }

    public String getKey() {
        return key;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return text;
    }

    public static Longitudes getEnumFromText(String inText) {
        if (inText.equalsIgnoreCase(EAST.text)) return EAST;
        if (inText.equalsIgnoreCase(EAST.key)) return EAST;
        if (inText.equalsIgnoreCase(WEST.text)) return WEST;
        if (inText.equalsIgnoreCase(WEST.key)) return WEST;
        if (inText.equalsIgnoreCase(NONE.text)) return NONE;
        if (inText.equalsIgnoreCase(NONE.key)) return NONE;
        return NONE;
    }
}
