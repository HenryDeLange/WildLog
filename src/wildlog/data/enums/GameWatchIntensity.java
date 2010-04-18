/*
 * GameWatchIntensity.java is part of WildLog
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


public enum GameWatchIntensity {
    VERY_HIGH("Heavily Focused"),
    HIGH("Focused"),
    MEDIUM("Lightly Focused"),
    LOW("Not Realy Focus"),
    VERY_LOW("No Focus"),
    NONE("None");
            
    private String text;
    
    GameWatchIntensity(String inText) {
        text = inText;
    }
    
    @Override
    public String toString() {
        return text;
    }

    public static GameWatchIntensity getEnumFromText(String inText) {
        if (inText.equalsIgnoreCase(VERY_HIGH.text)) return VERY_HIGH;
        if (inText.equalsIgnoreCase(HIGH.text)) return HIGH;
        if (inText.equalsIgnoreCase(MEDIUM.text)) return MEDIUM;
        if (inText.equalsIgnoreCase(LOW.text)) return LOW;
        if (inText.equalsIgnoreCase(VERY_LOW.text)) return VERY_LOW;
        if (inText.equalsIgnoreCase(NONE.text)) return NONE;
        return NONE;
    }

}
