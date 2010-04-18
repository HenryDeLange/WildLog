/*
 * FotoType.java is part of WildLog
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

/**
 *
 * @author Henry
 */
public enum FotoType {
    IMAGE("Image"),
    MOVIE("Movie"),
    OTHER("Other"),
    NONE("None");

    private String text;

    FotoType(String inText) {
        text = inText;
    }

    @Override
    public String toString() {
        return text;
    }

    public static FotoType getEnumFromText(String inText) {
        if (inText.equalsIgnoreCase(IMAGE.text)) return IMAGE;
        if (inText.equalsIgnoreCase(MOVIE.text)) return MOVIE;
        if (inText.equalsIgnoreCase(OTHER.text)) return OTHER;
        if (inText.equalsIgnoreCase(NONE.text)) return NONE;
        return NONE;
    }

}
