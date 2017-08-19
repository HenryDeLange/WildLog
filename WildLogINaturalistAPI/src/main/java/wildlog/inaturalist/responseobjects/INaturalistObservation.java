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

    public void setId(int inId) {
        id = inId;
    }

    public String getObserved_on() {
        return observed_on;
    }

    public void setObserved_on(String inObserved_on) {
        observed_on = inObserved_on;
    }

    public Object getDescription() {
        return description;
    }

    public void setDescription(Object inDescription) {
        description = inDescription;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String inLatitude) {
        latitude = inLatitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String inLongitude) {
        longitude = inLongitude;
    }

    public Object getMap_scale() {
        return map_scale;
    }

    public void setMap_scale(Object inMap_scale) {
        map_scale = inMap_scale;
    }

    public Object getTimeframe() {
        return timeframe;
    }

    public void setTimeframe(Object inTimeframe) {
        timeframe = inTimeframe;
    }

    public String getSpecies_guess() {
        return species_guess;
    }

    public void setSpecies_guess(String inSpecies_guess) {
        species_guess = inSpecies_guess;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int inUser_id) {
        user_id = inUser_id;
    }

    public int getTaxon_id() {
        return taxon_id;
    }

    public void setTaxon_id(int inTaxon_id) {
        taxon_id = inTaxon_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String inCreated_at) {
        created_at = inCreated_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String inUpdated_at) {
        updated_at = inUpdated_at;
    }

    public String getPlace_guess() {
        return place_guess;
    }

    public void setPlace_guess(String inPlace_guess) {
        place_guess = inPlace_guess;
    }

    public boolean isId_please() {
        return id_please;
    }

    public void setId_please(boolean inId_please) {
        id_please = inId_please;
    }

    public String getObserved_on_string() {
        return observed_on_string;
    }

    public void setObserved_on_string(String inObserved_on_string) {
        observed_on_string = inObserved_on_string;
    }

    public int getIconic_taxon_id() {
        return iconic_taxon_id;
    }

    public void setIconic_taxon_id(int inIconic_taxon_id) {
        iconic_taxon_id = inIconic_taxon_id;
    }

    public int getNum_identification_agreements() {
        return num_identification_agreements;
    }

    public void setNum_identification_agreements(int inNum_identification_agreements) {
        num_identification_agreements = inNum_identification_agreements;
    }

    public int getNum_identification_disagreements() {
        return num_identification_disagreements;
    }

    public void setNum_identification_disagreements(int inNum_identification_disagreements) {
        num_identification_disagreements = inNum_identification_disagreements;
    }

    public String getTime_observed_at() {
        return time_observed_at;
    }

    public void setTime_observed_at(String inTime_observed_at) {
        time_observed_at = inTime_observed_at;
    }

    public String getTime_zone() {
        return time_zone;
    }

    public void setTime_zone(String inTime_zone) {
        time_zone = inTime_zone;
    }

    public boolean isLocation_is_exact() {
        return location_is_exact;
    }

    public void setLocation_is_exact(boolean inLocation_is_exact) {
        location_is_exact = inLocation_is_exact;
    }

    public boolean isDelta() {
        return delta;
    }

    public void setDelta(boolean inDelta) {
        delta = inDelta;
    }

    public Object getPositional_accuracy() {
        return positional_accuracy;
    }

    public void setPositional_accuracy(Object inPositional_accuracy) {
        positional_accuracy = inPositional_accuracy;
    }

    public Object getGeoprivacy() {
        return geoprivacy;
    }

    public void setGeoprivacy(Object inGeoprivacy) {
        geoprivacy = inGeoprivacy;
    }

    public String getQuality_grade() {
        return quality_grade;
    }

    public void setQuality_grade(String inQuality_grade) {
        quality_grade = inQuality_grade;
    }

    public String getPositioning_method() {
        return positioning_method;
    }

    public void setPositioning_method(String inPositioning_method) {
        positioning_method = inPositioning_method;
    }

    public String getPositioning_device() {
        return positioning_device;
    }

    public void setPositioning_device(String inPositioning_device) {
        positioning_device = inPositioning_device;
    }

    public boolean getOut_of_range() {
        return out_of_range;
    }

    public void setOut_of_range(boolean inOut_of_range) {
        out_of_range = inOut_of_range;
    }

    public Object getLicense() {
        return license;
    }

    public void setLicense(Object inLicense) {
        license = inLicense;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String inUri) {
        uri = inUri;
    }

    public int getObservation_photos_count() {
        return observation_photos_count;
    }

    public void setObservation_photos_count(int inObservation_photos_count) {
        observation_photos_count = inObservation_photos_count;
    }

    public int getComments_count() {
        return comments_count;
    }

    public void setComments_count(int inComments_count) {
        comments_count = inComments_count;
    }

    public Object getZic_time_zone() {
        return zic_time_zone;
    }

    public void setZic_time_zone(Object inZic_time_zone) {
        zic_time_zone = inZic_time_zone;
    }

    public Object getOauth_application_id() {
        return oauth_application_id;
    }

    public void setOauth_application_id(Object inOauth_application_id) {
        oauth_application_id = inOauth_application_id;
    }

    public int getObservation_sounds_count() {
        return observation_sounds_count;
    }

    public void setObservation_sounds_count(int inObservation_sounds_count) {
        observation_sounds_count = inObservation_sounds_count;
    }

    public int getIdentifications_count() {
        return identifications_count;
    }

    public void setIdentifications_count(int inIdentifications_count) {
        identifications_count = inIdentifications_count;
    }

    public boolean isCaptive() {
        return captive;
    }

    public void setCaptive(boolean inCaptive) {
        captive = inCaptive;
    }

    public Object getCommunity_taxon_id() {
        return community_taxon_id;
    }

    public void setCommunity_taxon_id(Object inCommunity_taxon_id) {
        community_taxon_id = inCommunity_taxon_id;
    }

    public int getSite_id() {
        return site_id;
    }

    public void setSite_id(int inSite_id) {
        site_id = inSite_id;
    }

    public Object getOld_uuid() {
        return old_uuid;
    }

    public void setOld_uuid(Object inOld_uuid) {
        old_uuid = inOld_uuid;
    }

    public Object getPublic_positional_accuracy() {
        return public_positional_accuracy;
    }

    public void setPublic_positional_accuracy(Object inPublic_positional_accuracy) {
        public_positional_accuracy = inPublic_positional_accuracy;
    }

    public boolean isMappable() {
        return mappable;
    }

    public void setMappable(boolean inMappable) {
        mappable = inMappable;
    }

    public int getCached_votes_total() {
        return cached_votes_total;
    }

    public void setCached_votes_total(int inCached_votes_total) {
        cached_votes_total = inCached_votes_total;
    }

    public String getLast_indexed_at() {
        return last_indexed_at;
    }

    public void setLast_indexed_at(String inLast_indexed_at) {
        last_indexed_at = inLast_indexed_at;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String inUuid) {
        uuid = inUuid;
    }

    public Object getShort_description() {
        return short_description;
    }

    public void setShort_description(Object inShort_description) {
        short_description = inShort_description;
    }

    public String getUser_login() {
        return user_login;
    }

    public void setUser_login(String inUser_login) {
        user_login = inUser_login;
    }

    public String getIconic_taxon_name() {
        return iconic_taxon_name;
    }

    public void setIconic_taxon_name(String inIconic_taxon_name) {
        iconic_taxon_name = inIconic_taxon_name;
    }

    public List<Object> getTag_list() {
        return tag_list;
    }

    public void setTag_list(List<Object> inTag_list) {
        tag_list = inTag_list;
    }

    public int getFaves_count() {
        return faves_count;
    }

    public void setFaves_count(int inFaves_count) {
        faves_count = inFaves_count;
    }

    public String getCreated_at_utc() {
        return created_at_utc;
    }

    public void setCreated_at_utc(String inCreated_at_utc) {
        created_at_utc = inCreated_at_utc;
    }

    public String getUpdated_at_utc() {
        return updated_at_utc;
    }

    public void setUpdated_at_utc(String inUpdated_at_utc) {
        updated_at_utc = inUpdated_at_utc;
    }

    public String getTime_observed_at_utc() {
        return time_observed_at_utc;
    }

    public void setTime_observed_at_utc(String inTime_observed_at_utc) {
        time_observed_at_utc = inTime_observed_at_utc;
    }

    public boolean isOwners_identification_from_vision() {
        return owners_identification_from_vision;
    }

    public void setOwners_identification_from_vision(boolean inOwners_identification_from_vision) {
        owners_identification_from_vision = inOwners_identification_from_vision;
    }

    public boolean isCoordinates_obscured() {
        return coordinates_obscured;
    }

    public void setCoordinates_obscured(boolean inCoordinates_obscured) {
        coordinates_obscured = inCoordinates_obscured;
    }

    public INaturalistTaxon getTaxon() {
        return taxon;
    }

    public void setTaxon(INaturalistTaxon inTaxon) {
        taxon = inTaxon;
    }

    public INaturalistIconicTaxon getIconic_taxon() {
        return iconic_taxon;
    }

    public void setIconic_taxon(INaturalistIconicTaxon inIconic_taxon) {
        iconic_taxon = inIconic_taxon;
    }

    public INaturalistUser getUser() {
        return user;
    }

    public void setUser(INaturalistUser inUser) {
        user = inUser;
    }

    public List<INaturalistPhoto> getPhotos() {
        return photos;
    }

    public void setPhotos(List<INaturalistPhoto> inPhotos) {
        photos = inPhotos;
    }

    public List<INaturalistObservationFieldValue> getObservation_field_values() {
        return observation_field_values;
    }

    public void setObservation_field_values(List<INaturalistObservationFieldValue> inObservation_field_values) {
        observation_field_values = inObservation_field_values;
    }

    public List<Object> getProject_observations() {
        return project_observations;
    }

    public void setProject_observations(List<Object> inProject_observations) {
        project_observations = inProject_observations;
    }

    public List<INaturalistObservationPhoto> getObservation_photos() {
        return observation_photos;
    }

    public void setObservation_photos(List<INaturalistObservationPhoto> inObservation_photos) {
        observation_photos = inObservation_photos;
    }

}
