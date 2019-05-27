package wildlog.data.dataobjects;

import wildlog.data.dataobjects.interfaces.DataObjectWithAudit;
import wildlog.data.enums.WildLogDataType;


public class WildLogDeleteLog extends DataObjectWithAudit {
    private WildLogDataType type;

    
    public WildLogDeleteLog() {
    }
    
    public WildLogDeleteLog(WildLogDataType inType, long inID) {
        type = inType;
        id = inID;
    }

    
    public WildLogDataType getType() {
        return type;
    }

    public void setType(WildLogDataType inType) {
        type = inType;
    }
    
}
