/*
 * FeedingClass.java is part of WildLog
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


public enum FeedingClass {
    CARNIVORE("Carnivore"),
    HERBIVORE("Herbivore"),
    OMNIVORE("Omnivore"),
    PHOTOSYNTHESYS("Photo-Synthesis"),
    PARASITE("Parasite"),
    NONE("None");
    
    private String text;
    
    FeedingClass(String inText) {
        text = inText;
    }
    
    @Override
    public String toString() {
        return text;
    }

    public static FeedingClass getEnumFromText(String inText) {
        if (inText.equalsIgnoreCase(CARNIVORE.text)) return CARNIVORE;
        if (inText.equalsIgnoreCase(HERBIVORE.text)) return HERBIVORE;
        if (inText.equalsIgnoreCase(OMNIVORE.text)) return OMNIVORE;
        if (inText.equalsIgnoreCase(PHOTOSYNTHESYS.text)) return PHOTOSYNTHESYS;
        if (inText.equalsIgnoreCase(PARASITE.text)) return PARASITE;
        if (inText.equalsIgnoreCase(NONE.text)) return NONE;
        return NONE;
    }

}
