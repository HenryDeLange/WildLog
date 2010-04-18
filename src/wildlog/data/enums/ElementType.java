/*
 * ElementType.java is part of WildLog
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


public enum ElementType {
    ANIMAL("Mammal"),
    BIRD("Bird"),
    REPTILE("Reptile"),
    AMPHIBIAN("Amphibian"),
    FISH("Fish"),
    INSECT("Insect"),
    PLANT("Plant"),
    OTHER("Other"),
    NONE("None");
    
    private String text;
    
    ElementType(String inText) {
        text = inText;
    }
    
    @Override
    public String toString() {
        return text;
    }

    public void fix(String inText) {
        text = inText;
    }

    public static ElementType getEnumFromText(String inText) {
        if (inText.equalsIgnoreCase(ANIMAL.text)) return ANIMAL;
        if (inText.equalsIgnoreCase(BIRD.text)) return BIRD;
        if (inText.equalsIgnoreCase(REPTILE.text)) return REPTILE;
        if (inText.equalsIgnoreCase(AMPHIBIAN.text)) return AMPHIBIAN;
        if (inText.equalsIgnoreCase(FISH.text)) return FISH;
        if (inText.equalsIgnoreCase(INSECT.text)) return INSECT;
        if (inText.equalsIgnoreCase(PLANT.text)) return PLANT;
        if (inText.equalsIgnoreCase(OTHER.text)) return OTHER;
        if (inText.equalsIgnoreCase(NONE.text)) return NONE;
        return NONE;
    }
    
}
