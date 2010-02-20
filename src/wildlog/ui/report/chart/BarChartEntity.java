/*
 * BarChartEntity.java is part of WildLog
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

package wildlog.ui.report.chart;

import java.awt.Color;

/**
 *
 * @author DeLangeH
 */
public class BarChartEntity {
    // Variables
    private Color color;
    private int value;
    private String description;
    private String barName;

    // Constructor
    public BarChartEntity(String inBarName, String inDescription, int inValue, Color inColor) {
        color = inColor;
        value = inValue;
        description = inDescription;
        barName = inBarName;
    }

    // Getters and Setters
    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getBarName() {
        return barName;
    }

    public void setBarName(String barName) {
        this.barName = barName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

}
