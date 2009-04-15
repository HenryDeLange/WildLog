/*
 * AddFrequency.java is part of WildLog
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


public enum AddFrequency {
    HIGH("90 - 100%", "Added almost always. (Each sighting is recorded)"),
    MEDIUM("75 - 90%", "Added frequently. (Most good sightings are added)"),
    LOW("40 - 75%", "Added infrequently. (Only the first sighting per visit)"),
    VERY_LOW("0 - 40%", "Very seldomly added. (Might be so common that it is not realy recorded)"),
    NONE("None", "None");
    
    private String text;
    private String description;

    public String getText() {
        return text;
    }

    public String getDescription() {
        return description;
    }

    AddFrequency(String inText, String inDescription) {
        text = inText;
        description = inDescription;
    }
    
    @Override
    public String toString() {
        return text;
    }

}
