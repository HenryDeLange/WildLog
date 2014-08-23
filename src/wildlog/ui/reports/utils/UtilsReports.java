package wildlog.ui.reports.utils;

import java.util.Arrays;


public class UtilsReports {

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
