/*
 * ActiveTimeSpesific.java is part of WildLog
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


public enum ActiveTimeSpesific {
    EARLY_MORNING("Early Morning - Before the sun"),
    MORNING("Morning - Just after the sun"),
    MID_MORNING("Mid Morning - Before mid day"),
    MIDDAY("Mid Day - Heat of the day"),
    MID_AFTERNOON("Mid Afternoon - After mid day"),
    AFTERNOON("Afternoon - Before sunset"),
    LATE_AFTERNOON("Late Afternoon - Just after sunset"),
    DEEP_NIGHT("Night - Darkness of night"),
    NONE("None");

    private String text;

    ActiveTimeSpesific(String inText) {
        text = inText;
    }

    @Override
    public String toString() {
        return text;
    }

}
