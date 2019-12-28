package wildlog.ui.panels.bulkupload.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;


public class ComboBoxSuggestedElementModel extends DefaultComboBoxModel<ComboBoxSuggestedElementWrapper> {
    private final Map<ComboBoxSuggestedElementWrapper, Integer> mapElementSuggestions;

    
    public ComboBoxSuggestedElementModel(Map<ComboBoxSuggestedElementWrapper, Integer> inMapElementSuggestions) {
        // Note: This map is not a copy but all the JComboBoxes on the Bulk Import tab point to the same list (kept on the BulkUploadPanel instance)
        mapElementSuggestions = inMapElementSuggestions;
    }

    
    @Override
    public int getSize() {
        return mapElementSuggestions.size();
    }

    @Override
    public ComboBoxSuggestedElementWrapper getElementAt(int inIndex) {
        List<Map.Entry<ComboBoxSuggestedElementWrapper, Integer>> lstSortedData = getSortedData();
        if (inIndex >= 0 && inIndex < lstSortedData.size()) {
            return lstSortedData.get(inIndex).getKey();
        }
        return null;
    }

    @Override
    public void addElement(ComboBoxSuggestedElementWrapper anObject) {
        // Do nothing
    }

    @Override
    public void insertElementAt(ComboBoxSuggestedElementWrapper anObject, int index) {
        // Do nothing
    }

    @Override
    public void removeElementAt(int index) {
        // Do nothing
    }

    @Override
    public void removeElement(Object anObject) {
        // Do nothing
    }

    public void registerElementSelection(ComboBoxSuggestedElementWrapper inElementWrapper) {
        if (inElementWrapper != null) {
            mapElementSuggestions.put(inElementWrapper, mapElementSuggestions.getOrDefault(inElementWrapper, -1) + 1);
            if (mapElementSuggestions.size() >= 10) {
                // Remove the least often used value
                List<Map.Entry<ComboBoxSuggestedElementWrapper, Integer>> lstSortedData = getSortedData();
                ComboBoxSuggestedElementWrapper elementWrapperToRemove = lstSortedData.get(mapElementSuggestions.size() - 1).getKey();
                if (elementWrapperToRemove.equals(inElementWrapper)) {
                    elementWrapperToRemove = lstSortedData.get(mapElementSuggestions.size() - 2).getKey();
                }
                mapElementSuggestions.remove(elementWrapperToRemove);
            }
        }
    }
    
    private List<Map.Entry<ComboBoxSuggestedElementWrapper, Integer>> getSortedData() {
        List<Map.Entry<ComboBoxSuggestedElementWrapper, Integer>> lstSortedData = new ArrayList<>(mapElementSuggestions.entrySet());
        Collections.sort(lstSortedData, new Comparator<Map.Entry<ComboBoxSuggestedElementWrapper, Integer>>() {
            @Override
            public int compare(Map.Entry<ComboBoxSuggestedElementWrapper, Integer> inEntry1, Map.Entry<ComboBoxSuggestedElementWrapper, Integer> inEntry2) {
                int result = -1 * Integer.compare(inEntry1.getValue(), inEntry2.getValue());
                if (result == 0) {
                    result = inEntry1.getKey().getElement().getPrimaryName().compareTo(inEntry2.getKey().getElement().getPrimaryName());
                }
                return result;
            }
        });
        return lstSortedData;
    }

}
