package wildlog.data.dataobjects;

import wildlog.data.dataobjects.interfaces.DataObjectWithAudit;
import wildlog.data.enums.system.WildLogDataType;


public class WildLogDeleteLog extends DataObjectWithAudit {
    private WildLogDataType type;

    
    public WildLogDeleteLog() {
    }
    
    public WildLogDeleteLog(WildLogDataType inType, long inID) {
        type = inType;
        id = inID;
    }

    @Override
    public String toString() {
        return type + ":" + id;
    }
    
    public WildLogDataType getType() {
        return type;
    }

    public void setType(WildLogDataType inType) {
        type = inType;
    }
    
}
