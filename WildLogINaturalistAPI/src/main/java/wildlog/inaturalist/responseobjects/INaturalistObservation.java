package wildlog.inaturalist.responseobjects;

import java.util.List;

public class INaturalistObservation {
    private int id;
    private String observed_on;
    private Object description;
    private String latitude;
    private String longitude;
    private Object map_scale;
    private Object timeframe;
    private String species_guess;
    private int user_id;
    private int taxon_id;
    private String created_at;
    private String updated_at;
    private String place_guess;
    private boolean id_please;
    private String observed_on_string;
    private int iconic_taxon_id;
    private int num_identification_agreements;
    private int num_identification_disagreements;
    private String time_observed_at;
    private String time_zone;
    private boolean location_is_exact;
    private boolean delta;
    private Object positional_accuracy;
    private Object geoprivacy;
    private String quality_grade;
    private String positioning_method;
    private String positioning_device;
    private boolean out_of_range;
    private Object license;
    private String uri;
    private int observation_photos_count;
    private int comments_count;
    private Object zic_time_zone;
    private Object oauth_application_id;
    private int observation_sounds_count;
    private int identifications_count;
    private boolean captive;
    private Object community_taxon_id;
    private int site_id;
    private Object old_uuid;
    private Object public_positional_accuracy;
    private boolean mappable;
    private int cached_votes_total;
    private String last_indexed_at;
    private String uuid;
    private Object short_description;
    private String user_login;
    private String iconic_taxon_name;
    private List<Object> tag_list;
    private int faves_count;
    private String created_at_utc;
    private String updated_at_utc;
    private String time_observed_at_utc;
    private boolean owners_identification_from_vision;
    private boolean coordinates_obscured;
    private INaturalistTaxon taxon;
    private INaturalistIconicTaxon iconic_taxon;
    private INaturalistUser user;
    private List<INaturalistPhoto> photos;
    private List<INaturalistObservationFieldValue> observation_field_values;
    private List<Object> project_observations;
    private List<INaturalistObservationPhoto> observation_photos;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getObserved_on() {
        return observed_on;
    }

    public void setObserved_on(String observed_on) {
        this.observed_on = observed_on;
    }

    public Object getDescription() {
        return description;
    }

    public void setDescription(Object description) {
        this.description = description;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Object getMap_scale() {
        return map_scale;
    }

    public void setMap_scale(Object map_scale) {
        this.map_scale = map_scale;
    }

    public Object getTimeframe() {
        return timeframe;
    }

    public void setTimeframe(Object timeframe) {
        this.timeframe = timeframe;
    }

    public String getSpecies_guess() {
        return species_guess;
    }

    public void setSpecies_guess(String species_guess) {
        this.species_guess = species_guess;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getTaxon_id() {
        return taxon_id;
    }

    public void setTaxon_id(int taxon_id) {
        this.taxon_id = taxon_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getPlace_guess() {
        return place_guess;
    }

    public void setPlace_guess(String place_guess) {
        this.place_guess = place_guess;
    }

    public boolean isId_please() {
        return id_please;
    }

    public void setId_please(boolean id_please) {
        this.id_please = id_please;
    }

    public String getObserved_on_string() {
        return observed_on_string;
    }

    public void setObserved_on_string(String observed_on_string) {
        this.observed_on_string = observed_on_string;
    }

    public int getIconic_taxon_id() {
        return iconic_taxon_id;
    }

    public void setIconic_taxon_id(int iconic_taxon_id) {
        this.iconic_taxon_id = iconic_taxon_id;
    }

    public int getNum_identification_agreements() {
        return num_identification_agreements;
    }

    public void setNum_identification_agreements(int num_identification_agreements) {
        this.num_identification_agreements = num_identification_agreements;
    }

    public int getNum_identification_disagreements() {
        return num_identification_disagreements;
    }

    public void setNum_identification_disagreements(int num_identification_disagreements) {
        this.num_identification_disagreements = num_identification_disagreements;
    }

    public String getTime_observed_at() {
        return time_observed_at;
    }

    public void setTime_observed_at(String time_observed_at) {
        this.time_observed_at = time_observed_at;
    }

    public String getTime_zone() {
        return time_zone;
    }

    public void setTime_zone(String time_zone) {
        this.time_zone = time_zone;
    }

    public boolean isLocation_is_exact() {
        return location_is_exact;
    }

    public void setLocation_is_exact(boolean location_is_exact) {
        this.location_is_exact = location_is_exact;
    }

    public boolean isDelta() {
        return delta;
    }

    public void setDelta(boolean delta) {
        this.delta = delta;
    }

    public Object getPositional_accuracy() {
        return positional_accuracy;
    }

    public void setPositional_accuracy(Object positional_accuracy) {
        this.positional_accuracy = positional_accuracy;
    }

    public Object getGeoprivacy() {
        return geoprivacy;
    }

    public void setGeoprivacy(Object geoprivacy) {
        this.geoprivacy = geoprivacy;
    }

    public String getQuality_grade() {
        return quality_grade;
    }

    public void setQuality_grade(String quality_grade) {
        this.quality_grade = quality_grade;
    }

    public String getPositioning_method() {
        return positioning_method;
    }

    public void setPositioning_method(String positioning_method) {
        this.positioning_method = positioning_method;
    }

    public String getPositioning_device() {
        return positioning_device;
    }

    public void setPositioning_device(String positioning_device) {
        this.positioning_device = positioning_device;
    }

    public boolean getOut_of_range() {
        return out_of_range;
    }

    public void setOut_of_range(boolean out_of_range) {
        this.out_of_range = out_of_range;
    }

    public Object getLicense() {
        return license;
    }

    public void setLicense(Object license) {
        this.license = license;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public int getObservation_photos_count() {
        return observation_photos_count;
    }

    public void setObservation_photos_count(int observation_photos_count) {
        this.observation_photos_count = observation_photos_count;
    }

    public int getComments_count() {
        return comments_count;
    }

    public void setComments_count(int comments_count) {
        this.comments_count = comments_count;
    }

    public Object getZic_time_zone() {
        return zic_time_zone;
    }

    public void setZic_time_zone(Object zic_time_zone) {
        this.zic_time_zone = zic_time_zone;
    }

    public Object getOauth_application_id() {
        return oauth_application_id;
    }

    public void setOauth_application_id(Object oauth_application_id) {
        this.oauth_application_id = oauth_application_id;
    }

    public int getObservation_sounds_count() {
        return observation_sounds_count;
    }

    public void setObservation_sounds_count(int observation_sounds_count) {
        this.observation_sounds_count = observation_sounds_count;
    }

    public int getIdentifications_count() {
        return identifications_count;
    }

    public void setIdentifications_count(int identifications_count) {
        this.identifications_count = identifications_count;
    }

    public boolean isCaptive() {
        return captive;
    }

    public void setCaptive(boolean captive) {
        this.captive = captive;
    }

    public Object getCommunity_taxon_id() {
        return community_taxon_id;
    }

    public void setCommunity_taxon_id(Object community_taxon_id) {
        this.community_taxon_id = community_taxon_id;
    }

    public int getSite_id() {
        return site_id;
    }

    public void setSite_id(int site_id) {
        this.site_id = site_id;
    }

    public Object getOld_uuid() {
        return old_uuid;
    }

    public void setOld_uuid(Object old_uuid) {
        this.old_uuid = old_uuid;
    }

    public Object getPublic_positional_accuracy() {
        return public_positional_accuracy;
    }

    public void setPublic_positional_accuracy(Object public_positional_accuracy) {
        this.public_positional_accuracy = public_positional_accuracy;
    }

    public boolean isMappable() {
        return mappable;
    }

    public void setMappable(boolean mappable) {
        this.mappable = mappable;
    }

    public int getCached_votes_total() {
        return cached_votes_total;
    }

    public void setCached_votes_total(int cached_votes_total) {
        this.cached_votes_total = cached_votes_total;
    }

    public String getLast_indexed_at() {
        return last_indexed_at;
    }

    public void setLast_indexed_at(String last_indexed_at) {
        this.last_indexed_at = last_indexed_at;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Object getShort_description() {
        return short_description;
    }

    public void setShort_description(Object short_description) {
        this.short_description = short_description;
    }

    public String getUser_login() {
        return user_login;
    }

    public void setUser_login(String user_login) {
        this.user_login = user_login;
    }

    public String getIconic_taxon_name() {
        return iconic_taxon_name;
    }

    public void setIconic_taxon_name(String iconic_taxon_name) {
        this.iconic_taxon_name = iconic_taxon_name;
    }

    public List<Object> getTag_list() {
        return tag_list;
    }

    public void setTag_list(List<Object> tag_list) {
        this.tag_list = tag_list;
    }

    public int getFaves_count() {
        return faves_count;
    }

    public void setFaves_count(int faves_count) {
        this.faves_count = faves_count;
    }

    public String getCreated_at_utc() {
        return created_at_utc;
    }

    public void setCreated_at_utc(String created_at_utc) {
        this.created_at_utc = created_at_utc;
    }

    public String getUpdated_at_utc() {
        return updated_at_utc;
    }

    public void setUpdated_at_utc(String updated_at_utc) {
        this.updated_at_utc = updated_at_utc;
    }

    public String getTime_observed_at_utc() {
        return time_observed_at_utc;
    }

    public void setTime_observed_at_utc(String time_observed_at_utc) {
        this.time_observed_at_utc = time_observed_at_utc;
    }

    public boolean isOwners_identification_from_vision() {
        return owners_identification_from_vision;
    }

    public void setOwners_identification_from_vision(boolean owners_identification_from_vision) {
        this.owners_identification_from_vision = owners_identification_from_vision;
    }

    public boolean isCoordinates_obscured() {
        return coordinates_obscured;
    }

    public void setCoordinates_obscured(boolean coordinates_obscured) {
        this.coordinates_obscured = coordinates_obscured;
    }

    public INaturalistTaxon getTaxon() {
        return taxon;
    }

    public void setTaxon(INaturalistTaxon taxon) {
        this.taxon = taxon;
    }

    public INaturalistIconicTaxon getIconic_taxon() {
        return iconic_taxon;
    }

    public void setIconic_taxon(INaturalistIconicTaxon iconic_taxon) {
        this.iconic_taxon = iconic_taxon;
    }

    public INaturalistUser getUser() {
        return user;
    }

    public void setUser(INaturalistUser user) {
        this.user = user;
    }

    public List<INaturalistPhoto> getPhotos() {
        return photos;
    }

    public void setPhotos(List<INaturalistPhoto> photos) {
        this.photos = photos;
    }

    public List<INaturalistObservationFieldValue> getObservation_field_values() {
        return observation_field_values;
    }

    public void setObservation_field_values(List<INaturalistObservationFieldValue> observation_field_values) {
        this.observation_field_values = observation_field_values;
    }

    public List<Object> getProject_observations() {
        return project_observations;
    }

    public void setProject_observations(List<Object> project_observations) {
        this.project_observations = project_observations;
    }

    public List<INaturalistObservationPhoto> getObservation_photos() {
        return observation_photos;
    }

    public void setObservation_photos(List<INaturalistObservationPhoto> observation_photos) {
        this.observation_photos = observation_photos;
    }

}
