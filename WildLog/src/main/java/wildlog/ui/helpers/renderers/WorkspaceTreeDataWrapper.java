package wildlog.ui.helpers.renderers;

import wildlog.data.dataobjects.interfaces.DataObjectWithWildLogFile;


public class WorkspaceTreeDataWrapper {
    private DataObjectWithWildLogFile dataObject;
    private boolean isSelected;


    public WorkspaceTreeDataWrapper() {
    }

    public WorkspaceTreeDataWrapper(DataObjectWithWildLogFile dataObject, boolean isSelected) {
        this.dataObject = dataObject;
        this.isSelected = isSelected;
    }

    public DataObjectWithWildLogFile getDataObject() {
        return dataObject;
    }

    public void setDataObject(DataObjectWithWildLogFile inDataObject) {
        dataObject = inDataObject;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean inIsSelected) {
        isSelected = inIsSelected;
    }

    @Override
    public String toString() {
        return dataObject.toString();
    }

}
