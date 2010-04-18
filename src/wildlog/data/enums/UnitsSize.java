/*
 * UnitsSize.java is part of WildLog
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

public enum UnitsSize {
    METER("m"),
    CENTI_METER("cm"),
    NONE("None");

    private String text;

    UnitsSize(String inText) {
        text = inText;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return text;
    }

    public static UnitsSize getEnumFromText(String inText) {
        if (inText.equalsIgnoreCase(METER.text)) return METER;
        if (inText.equalsIgnoreCase(CENTI_METER.text)) return CENTI_METER;
        if (inText.equalsIgnoreCase(NONE.text)) return NONE;
        return NONE;
    }

}
