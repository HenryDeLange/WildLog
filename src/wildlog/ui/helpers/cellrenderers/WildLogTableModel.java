package wildlog.ui.helpers.cellrenderers;

import java.util.Vector;
import javax.swing.table.DefaultTableModel;


public class WildLogTableModel extends DefaultTableModel {

    public WildLogTableModel() {
    }

    public WildLogTableModel(Object[] columnNames, int rowCount) {
        super(columnNames, rowCount);
    }

    public WildLogTableModel(Object[][] data, Object[] columnNames) {
        super(data, columnNames);
    }

    public WildLogTableModel(Vector data, Vector columnNames) {
        super(data, columnNames);
    }

    public WildLogTableModel(Vector columnNames, int rowCount) {
        super(columnNames, rowCount);
    }

    public WildLogTableModel(int rowCount, int columnCount) {
        super(rowCount, columnCount);
    }

    @Override
    public Class<?> getColumnClass(int inColumn) {
        // Need to do this to get the auto-sorting to work and the cell renderers to know what data the column uses
        Object temp = null;
        int rowCounter = 0;
        while (temp == null && rowCounter < getRowCount()) {
            temp = getValueAt(rowCounter++, inColumn);
        }
        if (temp != null) {
            return temp.getClass();
        }
        return Object.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

}
