/*
 * AccommodationType.java is part of WildLog
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


public enum AccommodationType {
    CAMPING("Camping"),
    SMALL_UNIT("Small Unit"),
    BIG_UNIT("Big Unit"),
    NONE("None");
    
    private String text;
    
    AccommodationType(String inText) {
        text = inText;
    }
    
    @Override
    public String toString() {
        return text;
    }
    
    public String test() {
        return text;
    }

}
