/*
 * TimeFormat.java is part of WildLog
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
 * @author DeLange
 */
public enum TimeFormat {
    H24("24"),
    AM("AM"),
    PM("PM"),
    NONE("None");

    private String text;

    TimeFormat(String inText) {
        text = inText;
    }

    @Override
    public String toString() {
        return text;
    }

    public static TimeFormat getEnumFromText(String inText) {
        if (inText.equalsIgnoreCase(H24.text)) return H24;
        if (inText.equalsIgnoreCase(AM.text)) return AM;
        if (inText.equalsIgnoreCase(PM.text)) return PM;
        if (inText.equalsIgnoreCase(NONE.text)) return NONE;
        return NONE;
    }
    
}
