package wildlog.ui.panels.bulkupload.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import wildlog.data.dataobjects.Element;


public class ComboBoxSuggestedElementModel extends DefaultComboBoxModel<Element> {
    private final Map<Element, Integer> mapElementSuggestions;

    
    public ComboBoxSuggestedElementModel(Map<Element, Integer> inMapElementSuggestions) {
        // Note: This map is not a copy but all the JComboBoxes on the Bulk Import tab point to the same list (kept on the BulkUploadPanel instance)
        mapElementSuggestions = inMapElementSuggestions;
    }

    
    @Override
    public int getSize() {
        return mapElementSuggestions.size();
    }

    @Override
    public Element getElementAt(int inIndex) {
        List<Map.Entry<Element, Integer>> lstSortedData = getSortedData();
        if (inIndex >= 0 && inIndex < lstSortedData.size()) {
            return lstSortedData.get(inIndex).getKey();
        }
        return null;
    }

    @Override
    public void addElement(Element anObject) {
        // Do nothing
    }

    @Override
    public void insertElementAt(Element anObject, int index) {
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

    public void registerElementSelection(Element inElement) {
        if (inElement != null) {
            mapElementSuggestions.put(inElement, mapElementSuggestions.getOrDefault(inElement, -1) + 1);
            if (mapElementSuggestions.size() >= 10) {
                // Remove the least often used value
                List<Map.Entry<Element, Integer>> lstSortedData = getSortedData();
                Element elementToRemove = lstSortedData.get(mapElementSuggestions.size() - 1).getKey();
                if (elementToRemove.equals(inElement)) {
                    elementToRemove = lstSortedData.get(mapElementSuggestions.size() - 2).getKey();
                }
                mapElementSuggestions.remove(elementToRemove);
            }
        }
    }
    
    private List<Map.Entry<Element, Integer>> getSortedData() {
        List<Map.Entry<Element, Integer>> lstSortedData = new ArrayList<>(mapElementSuggestions.entrySet());
        Collections.sort(lstSortedData, new Comparator<Map.Entry<Element, Integer>>() {
            @Override
            public int compare(Map.Entry<Element, Integer> inEntry1, Map.Entry<Element, Integer> inEntry2) {
                int result = -1 * Integer.compare(inEntry1.getValue(), inEntry2.getValue());
                if (result == 0) {
                    result = inEntry1.getKey().getPrimaryName().compareTo(inEntry2.getKey().getPrimaryName());
                }
                return result;
            }
        });
        return lstSortedData;
    }

}
