/*
 * Habitat.java is part of WildLog
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


public enum Habitat {
    NEED_MORE_WORK("Need to improve this...", "Maybe have two habitat types..."),
    OTHER("Other", "Other"),
    NONE("None", "None");
    
    private final String text;
    private final String description;
    
    Habitat(String inText, String inDescription) {
        text = inText;
        description = inDescription;
    }
    
    @Override
    public String toString() {
        return text;
    }
    
    public String description() {
        return description;
    }

}
