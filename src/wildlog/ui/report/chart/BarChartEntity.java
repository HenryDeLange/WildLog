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
public class BarChartEntity implements Comparable<BarChartEntity> {
    // Variables
    private Color color;
    private int value;
    private String description;
    private Object barName;

    // Constructor
    public BarChartEntity(Object inBarName, String inDescription, int inValue, Color inColor) {
        color = inColor;
        value = inValue;
        description = inDescription;
        barName = inBarName;
    }

    @Override
    public int compareTo(BarChartEntity inBarChartEntity) {
        if (barName == null || inBarChartEntity.getBarName() == null)
            return 0;
        if (barName instanceof Comparable && inBarChartEntity.getBarName() instanceof Comparable) {
            return ((Comparable)barName).compareTo(((Comparable)inBarChartEntity.getBarName()));
        }
        else return 0;
    }

    // Getters and Setters
    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Object getBarName() {
        return barName;
    }

    public void setBarName(Object barName) {
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
