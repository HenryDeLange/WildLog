/*
 * EndangeredStatus.java is part of WildLog
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


public enum EndangeredStatus {
    EX("Ex", "Extinct"),
    EW("Ew", "Extinct in Wild"),
    CR("Cr", "Critically Endangered"),
    EN("En", "Endangered"),
    VU("Vu", "Vunerable"),
    NT("Nt", "Near threatened"),
    LC("Lc", "Least Concern"),
    NONE("", "None");
    
    private String text;
    private String key;
    
    EndangeredStatus(String inKey, String inText) {
        text = inText;
        key = inKey;
    }
    
    @Override
    public String toString() {
        return text;
    }
    
    public String key() {
        return key;
    }

}
