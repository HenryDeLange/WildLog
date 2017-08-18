package wildlog.inaturalist.responseobjects;

public class INaturalistObservationPhoto {
    private String created_at;
    private int id;
    private int observation_id;
    private int photo_id;
    private Object position;
    private String updated_at;
    private INaturalistPhoto photo;

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getObservation_id() {
        return observation_id;
    }

    public void setObservation_id(int observation_id) {
        this.observation_id = observation_id;
    }

    public int getPhoto_id() {
        return photo_id;
    }

    public void setPhoto_id(int photo_id) {
        this.photo_id = photo_id;
    }

    public Object getPosition() {
        return position;
    }

    public void setPosition(Object position) {
        this.position = position;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public INaturalistPhoto getPhoto() {
        return photo;
    }

    public void setPhoto(INaturalistPhoto photo) {
        this.photo = photo;
    }

}
