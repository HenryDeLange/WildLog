/*
 * LocationRating.java is part of WildLog
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


public enum LocationRating {
    HIGH("Very Nice"),
    NORMAL("Nice"),
    DECENT("Decent"),
    LOW("Bad"),
    NONE("None");

    private String text;

    LocationRating(String inText) {
        text = inText;
    }

    @Override
    public String toString() {
        return text;
    }

    public static LocationRating getEnumFromText(String inText) {
        if (inText.equalsIgnoreCase(HIGH.text)) return HIGH;
        if (inText.equalsIgnoreCase(NORMAL.text)) return NORMAL;
        if (inText.equalsIgnoreCase(DECENT.text)) return DECENT;
        if (inText.equalsIgnoreCase(LOW.text)) return LOW;
        if (inText.equalsIgnoreCase(NONE.text)) return NONE;
        return NONE;
    }

}
