/*
 * ActiveTime.java is part of WildLog
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


public enum ActiveTime {
    DAY("Day"),
    NIGHT("Night"),
    ALWAYS("Always"),
    DAWN_OR_DUST("Dawn or Dust"),
    NONE("None");
    
    private String text;
    
    ActiveTime(String inText) {
        text = inText;
    }
    
    @Override
    public String toString() {
        return text;
    }

    public static ActiveTime getEnumFromText(String inText) {
        if (inText.equalsIgnoreCase(DAY.text)) return DAY;
        if (inText.equalsIgnoreCase(NIGHT.text)) return NIGHT;
        if (inText.equalsIgnoreCase(ALWAYS.text)) return ALWAYS;
        if (inText.equalsIgnoreCase(DAWN_OR_DUST.text)) return DAWN_OR_DUST;
        if (inText.equalsIgnoreCase(NONE.text)) return NONE;
        return NONE;
    }

}
