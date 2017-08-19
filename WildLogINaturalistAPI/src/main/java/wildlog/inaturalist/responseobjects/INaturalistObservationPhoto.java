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

    public void setCreated_at(String inCreated_at) {
        created_at = inCreated_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int inId) {
        id = inId;
    }

    public int getObservation_id() {
        return observation_id;
    }

    public void setObservation_id(int inObservation_id) {
        observation_id = inObservation_id;
    }

    public int getPhoto_id() {
        return photo_id;
    }

    public void setPhoto_id(int inPhoto_id) {
        photo_id = inPhoto_id;
    }

    public Object getPosition() {
        return position;
    }

    public void setPosition(Object inPosition) {
        position = inPosition;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String inUpdated_at) {
        updated_at = inUpdated_at;
    }

    public INaturalistPhoto getPhoto() {
        return photo;
    }

    public void setPhoto(INaturalistPhoto inPhoto) {
        photo = inPhoto;
    }

}
