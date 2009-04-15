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
    SUCCULENT_KAROO("Succulent Karoo", "Succulents (thick fleshy leaves), annuals (spring flowers), bulbs, tubers, etc."),
    NAMA_KAROO("Nama Karoo","Covers most of the central plateau and forms a transition between the Cape flora and the tropical savanna."),
    FYNBOS("Fynbos","Evergreen heathlands and shrublands whith fine-leafed low shrubs and leafless tufted grasslike plants. Trees and grasses are rare."),
    GRASSLAND("Grassland","Grasses dominate the vegetation and woody plants are absent or rare."),
    SAVANNA("Savanna","Wooded grasslands of the tropics and subtropics."),
    THICKET("Thicket","Closed shrubland to low forest dominated by trees, shrubs and vines."),
    FORREST("Forest","Indigenous evergreen and semi-deciduous closed forests of the coastal lowlands and escarpment slopes."),
    WETLAND("Wetland","Inland and coastal habitats (mountain sponges, midland marshes, swamp forests, estuaries). Linked by rivers and streams. High water table, water-carrying soil."),
    COASTAL("Coastal","Sandy beaches, sand dunes and rocky shores."),
    MARINE("Marine","Coral reefs, kelp beds and the open sea."),
    OTHER("Other", "Other"),
    NONE("None", "None");
    
    private String text;
    private String description;
    
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

    public void fix(String inText) {
        text = inText;
    }

}
