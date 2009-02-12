/*
 * AreaType.java is part of WildLog
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


public enum AreaType {
    OPEN("Out in the open"),
    THICKET("In thick cover"),
    WETLAND("In a wetland area"),
    RIVER("Near a river"),
    DAM("Near a dam"),
    CLIFF("Near a cliff"),
    MOUNTAIN("On a mountain"),
    SAND("On desert sand"),
    HUMANS("Near human structures"),
    NONE("None");
    
    
    private final String text;
    
    AreaType(String inText) {
        text = inText;
    }
    
    @Override
    public String toString() {
        return text;
    }

}
