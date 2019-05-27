package wildlog.data.dataobjects;

import wildlog.data.dataobjects.interfaces.DataObjectWithAudit;
import wildlog.data.enums.WildLogUserTypes;


public class WildLogUser extends DataObjectWithAudit {
    private String username;
    private String password;
    private WildLogUserTypes type;

    
    public WildLogUser() {
    }

    public WildLogUser(String inUsername, String inPassword, WildLogUserTypes inType) {
        username = inUsername;
        password = inPassword;
        type = inType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String inUsername) {
        username = inUsername;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String inPassword) {
        password = inPassword;
    }

    public WildLogUserTypes getType() {
        return type;
    }

    public void setType(WildLogUserTypes inType) {
        type = inType;
    }
    
}
