package wildlog.inaturalist.responseobjects;


public class INaturalistObservationFieldValue {
    private String created_at;
    private int id;
    private int observation_field_id;
    private int observation_id;
    private String updated_at;
    private String value;

    
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

    public int getObservation_field_id() {
        return observation_field_id;
    }

    public void setObservation_field_id(int inObservation_field_id) {
        observation_field_id = inObservation_field_id;
    }

    public int getObservation_id() {
        return observation_id;
    }

    public void setObservation_id(int inObservation_id) {
        observation_id = inObservation_id;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String inUpdated_at) {
        updated_at = inUpdated_at;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String inValue) {
        value = inValue;
    }

}
