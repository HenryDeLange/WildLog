package wildlog.data.dataobjects.interfaces;


public abstract class DataObjectWithAudit {
    protected long id;
    protected long auditTime;
    protected String auditUser;
    protected long syncIndicator;

    
    public long getID() {
        return id;
    }

    public void setID(long inID) {
        id = inID;
    }
    
    public long getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(long inAuditTime) {
        auditTime = inAuditTime;
    }

    public String getAuditUser() {
        return auditUser;
    }

    public void setAuditUser(String inAuditUser) {
        auditUser = inAuditUser;
    }

    public long getSyncIndicator() {
        return syncIndicator;
    }

    public void setSyncIndicator(long inSyncIndicator) {
        syncIndicator = inSyncIndicator;
    }

}
