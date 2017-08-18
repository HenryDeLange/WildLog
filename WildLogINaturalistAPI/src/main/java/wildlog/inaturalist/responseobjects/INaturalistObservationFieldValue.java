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

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getObservation_field_id() {
        return observation_field_id;
    }

    public void setObservation_field_id(int observation_field_id) {
        this.observation_field_id = observation_field_id;
    }

    public int getObservation_id() {
        return observation_id;
    }

    public void setObservation_id(int observation_id) {
        this.observation_id = observation_id;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
