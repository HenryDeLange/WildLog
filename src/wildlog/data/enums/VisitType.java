/*
 * VisitType.java is part of WildLog
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


public enum VisitType {
    VACATION("Vacation"),
    REMOTE_CAMERA("Remote Camera"),
    BIRD_ATLASSING("Bird Atlassing"),
    DAY_VISIT("Day Visit"),
    OTHER("Other"),
    NONE("None");
    
    private String text;
    
    VisitType(String inText) {
        text = inText;
    }
    
    @Override
    public String toString() {
        return text;
    }

    public static VisitType getEnumFromText(String inText) {
        if (inText.equalsIgnoreCase(VACATION.text)) return VACATION;
        if (inText.equalsIgnoreCase(REMOTE_CAMERA.text)) return REMOTE_CAMERA;
        if (inText.equalsIgnoreCase(BIRD_ATLASSING.text)) return BIRD_ATLASSING;
        if (inText.equalsIgnoreCase(DAY_VISIT.text)) return DAY_VISIT;
        if (inText.equalsIgnoreCase(OTHER.text)) return OTHER;
        if (inText.equalsIgnoreCase(NONE.text)) return NONE;
        return NONE;
    }

}
