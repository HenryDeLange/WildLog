package wildlog.inaturalist.responseobjects;


public class INaturalistUser {
    private String login;
    private String user_icon_url;

    
    public String getLogin() {
        return login;
    }

    public void setLogin(String inLogin) {
        login = inLogin;
    }

    public String getUser_icon_url() {
        return user_icon_url;
    }

    public void setUser_icon_url(String inUser_icon_url) {
        user_icon_url = inUser_icon_url;
    }

}
