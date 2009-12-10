/*
 * DateCellRenderer.java is part of WildLog
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
package wildlog.utils.ui;

import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Delangeh
 */
// Set this DateCellRenderer as a CellRenderer to the Date column of your JTable.
public class DateCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (value instanceof Date) {
            // Use SimpleDateFormat class to get a formatted String from Date object.
            String strDate = new SimpleDateFormat("dd MMM yyyy").format((Date) value);
            // Sorting algorithm will work with model value. So you dont need to worry
            // about the renderer's display value.
            this.setText(strDate);
        }

        return this;
    }

}
