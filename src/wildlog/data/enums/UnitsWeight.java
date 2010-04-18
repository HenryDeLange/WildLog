/*
 * UnitsWeight.java is part of WildLog
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

public enum UnitsWeight {
    KILOGRAM("kg"),
    GRAM("g"),
    NONE("None");

    private String text;

    UnitsWeight(String inText) {
        text = inText;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return text;
    }

    public static UnitsWeight getEnumFromText(String inText) {
        if (inText.equalsIgnoreCase(KILOGRAM.text)) return KILOGRAM;
        if (inText.equalsIgnoreCase(GRAM.text)) return GRAM;
        if (inText.equalsIgnoreCase(NONE.text)) return NONE;
        return NONE;
    }

}
