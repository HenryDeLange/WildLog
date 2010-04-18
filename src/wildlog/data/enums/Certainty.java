/*
 * Certainty.java is part of WildLog
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


public enum Certainty {
    SURE    ("100%     (Sure)"),
    GOOD    ("90 - 99% (Good)"),
    BAD     ("70 - 90% (Bad)"),
    VERY_BAD(" 0 - 70% (Unsure)"),
    NONE("None");
    
    private String text;
    
    Certainty(String inText) {
        text = inText;
    }
    
    @Override
    public String toString() {
        return text;
    }

    public static Certainty getEnumFromText(String inText) {
        if (inText.equalsIgnoreCase(SURE.text)) return SURE;
        if (inText.equalsIgnoreCase(GOOD.text)) return GOOD;
        if (inText.equalsIgnoreCase(BAD.text)) return BAD;
        if (inText.equalsIgnoreCase(VERY_BAD.text)) return VERY_BAD;
        if (inText.equalsIgnoreCase(NONE.text)) return NONE;
        return NONE;
    }

}
