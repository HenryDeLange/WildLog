/*
 * WaterDependancy.java is part of WildLog
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


public enum WaterDependancy {
    VERY_HIGH("Always close to water"),
    HIGH("Needs access to water"),
    OPPORTUNISTIC("Not needed, but will use it"),
    LOW("Not effected/interisted"),
    NONE("None");
    
    private String text;
    
    WaterDependancy(String inText) {
        text = inText;
    }
    
    @Override
    public String toString() {
        return text;
    }

    public static WaterDependancy getEnumFromText(String inText) {
        if (inText.equalsIgnoreCase(VERY_HIGH.text)) return VERY_HIGH;
        if (inText.equalsIgnoreCase(HIGH.text)) return HIGH;
        if (inText.equalsIgnoreCase(OPPORTUNISTIC.text)) return OPPORTUNISTIC;
        if (inText.equalsIgnoreCase(LOW.text)) return LOW;
        if (inText.equalsIgnoreCase(NONE.text)) return NONE;
        return NONE;
    }

}
