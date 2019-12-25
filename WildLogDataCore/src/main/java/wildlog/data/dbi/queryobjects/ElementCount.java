package wildlog.data.dbi.queryobjects;

import wildlog.data.enums.ElementType;


public class ElementCount {
    private long elementID;
    private String elementName;
    private ElementType elementType;
    private int count;

    public ElementCount() {
    }

    public ElementCount(long inElementID, String inElementName, ElementType inElementType, int inCount) {
        elementID = inElementID;
        elementName = inElementName;
        elementType = inElementType;
        count = inCount;
    }

    public long getElementID() {
        return elementID;
    }

    public void setElementID(long inElementID) {
        elementID = inElementID;
    }

    public String getElementName() {
        return elementName;
    }

    public void setElementName(String inElementName) {
        elementName = inElementName;
    }

    public ElementType getElementType() {
        return elementType;
    }

    public void setElementType(ElementType inelementType) {
        elementType = inelementType;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int inCount) {
        count = inCount;
    }

}
