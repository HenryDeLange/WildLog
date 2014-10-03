package wildlog.ui.reports.utils;

import java.text.SimpleDateFormat;
import java.util.Arrays;


public final class UtilsReports {
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");

    private UtilsReports() {
    }
    
    /**
     * Remove the empty entries from the Enums, to not display then in the Report Filter.
     * @param inList
     * @return 
     */
    public static Enum[] removeEmptyEntries(Enum[] inList) {
        Enum[] array = new Enum[inList.length];
        int counter = 0;
        for (Enum temp : inList) {
            if (!temp.toString().trim().isEmpty()) {
                array[counter++] = temp;
            }
        }
        return Arrays.copyOf(array, counter);
    }
    
}
