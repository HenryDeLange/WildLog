package wildlog.inaturalist.responseobjects;

public class INaturalistUser {
    private String login;
    private String user_icon_url;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getUser_icon_url() {
        return user_icon_url;
    }

    public void setUser_icon_url(String user_icon_url) {
        this.user_icon_url = user_icon_url;
    }

}
