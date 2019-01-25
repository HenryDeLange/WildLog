package wildlog.data.wrappers.json.gbif;

import java.util.List;


public class GBIFData {
    private int count;
    private boolean endOfRecords;
    private List<GBIFOccurence> results;

    public int getCount() {
        return count;
    }

    public void setCount(int inCount) {
        count = inCount;
    }

    public boolean isEndOfRecords() {
        return endOfRecords;
    }

    public void setEndOfRecords(boolean inEndOfRecords) {
        endOfRecords = inEndOfRecords;
    }

    public List<GBIFOccurence> getResults() {
        return results;
    }

    public void setResults(List<GBIFOccurence> inResults) {
        results = inResults;
    }

    @Override
    public String toString() {
        return super.toString() + "; count=" + count + "; endOfRecords=" + endOfRecords + "; results=" + results;
    }
    
}
