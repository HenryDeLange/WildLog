/*
 * CateringType.java is part of WildLog
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


public enum CateringType {
    ONLY_SELF_CATERING("Only Self Catering"),
    ONLY_NON_CATERING("Only Not Self Catering"),
    ALL_CATERING("Both Self and Not Self Catering"),
    NO_CATERING("No Catering is Possible/Allowed"),
    NONE("None");
    
    private String text;
    
    CateringType(String inText) {
        text = inText;
    }
    
    @Override
    public String toString() {
        return text;
    }

}
