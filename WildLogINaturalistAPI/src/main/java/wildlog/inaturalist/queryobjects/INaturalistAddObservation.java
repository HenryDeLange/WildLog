package wildlog.inaturalist.queryobjects;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import wildlog.inaturalist.queryobjects.enums.INaturalistGeoprivacy;
import wildlog.inaturalist.queryobjects.enums.INaturalistIdPlease;
import wildlog.inaturalist.utils.UtilsINaturalist;


/**
 * Used to for [POST/observations]
 */
public class INaturalistAddObservation {
    private static final DateTimeFormatter ZONED_DATE_TIME_FORMAT = DateTimeFormatter.ISO_DATE_TIME;
    private String species_guess;
    private int taxon_id;
    private INaturalistIdPlease id_please;
    private ZonedDateTime observed_on_string;
    private String time_zone;
    private String description;
    private String tag_list;
    private String place_guess;
    private double latitude;
    private double longitide;
    private int map_scale;
    private int positional_accuracy;
    private INaturalistGeoprivacy geoprivacy;
// TODO: Doen "observation[observation_field_values_attributes][order]" vir, ek neem aan, stuff soos die "captive" status, ens.
    
    public String getDataString() {
        StringBuilder stringBuilder = new StringBuilder();
        // Note: POST data doesn't start with a '?' since it isn't part of the URL
        if (species_guess != null && !species_guess.isEmpty()) {
            stringBuilder.append("observation[species_guess]=").append(UtilsINaturalist.forURL(species_guess)).append('&');
        }
        if (taxon_id > 0) {
            stringBuilder.append("observation[taxon_id]=").append(taxon_id).append('&');
        }
        if (id_please != null) {
            stringBuilder.append("observation[id_please]=").append(id_please).append('&');
        }
        if (observed_on_string != null) {
            stringBuilder.append("observation[observed_on_string]=").append(UtilsINaturalist.forURL(
                    ZONED_DATE_TIME_FORMAT.format(observed_on_string))).append('&');
        }
        if (time_zone != null && !time_zone.isEmpty()) {
            stringBuilder.append("observation[time_zone]=").append(UtilsINaturalist.forURL(time_zone)).append('&');
        }
        if (description != null && !description.isEmpty()) {
            stringBuilder.append("observation[description]=").append(UtilsINaturalist.forURL(description)).append('&');
        }
        if (tag_list != null && !tag_list.isEmpty()) {
            stringBuilder.append("observation[tag_list]=").append(UtilsINaturalist.forURL(tag_list)).append('&');
        }
        if (place_guess != null && !place_guess.isEmpty()) {
            stringBuilder.append("observation[place_guess]=").append(UtilsINaturalist.forURL(place_guess)).append('&');
        }
        if (latitude >= -90 && latitude <= 90 && latitude != 0) {
            stringBuilder.append("observation[latitude]=").append(latitude).append('&');
        }
        if (longitide >= -180 && longitide <= 180 && longitide != 0) {
            stringBuilder.append("observation[longitide]=").append(longitide).append('&');
        }
        if (map_scale >= 0 && map_scale <= 19) {
            stringBuilder.append("observation[map_scale]=").append(map_scale).append('&');
        }
        if (positional_accuracy >= 0) {
            stringBuilder.append("observation[positional_accuracy]=").append(positional_accuracy).append('&');
        }
        if (geoprivacy != null) {
            stringBuilder.append("observation[geoprivacy]=").append(UtilsINaturalist.forURL(geoprivacy)).append('&');
        }
        if (stringBuilder.length() > 0) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
//        try {
//            return URLEncoder.encode(stringBuilder.toString(), "UTF-8").replace("+", "%20");
//        }
//        catch (UnsupportedEncodingException ex) {
//            ex.printStackTrace(System.err);
            return stringBuilder.toString();
//        }
    }

    public String getSpecies_guess() {
        return species_guess;
    }

    /**
     * Equivalent of the "What did you see?" field on the observation form, this is the name of the organism observed. 
     * If the taxon ID is absent, iNat will try to choose a single taxon based on this string, but it may fail if there's some taxonomic ambiguity. 
     */
    public void setSpecies_guess(String species_guess) {
        this.species_guess = species_guess;
    }

    public int getTaxon_id() {
        return taxon_id;
    }

    /**
     * A valid iNat taxon ID.
     */
    public void setTaxon_id(int taxon_id) {
        this.taxon_id = taxon_id;
    }

    public INaturalistIdPlease getId_please() {
        return id_please;
    }

    public void setId_please(INaturalistIdPlease id_please) {
        this.id_please = id_please;
    }

    public ZonedDateTime getObserved_on_string() {
        return observed_on_string;
    }

    public void setObserved_on_string(ZonedDateTime observed_on_string) {
        this.observed_on_string = observed_on_string;
    }

    public String getTime_zone() {
        return time_zone;
    }

    public void setTime_zone(String time_zone) {
        this.time_zone = time_zone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTag_list() {
        return tag_list;
    }

    /**
     * Comma-separated list of tags.
     */
    public void setTag_list(String tag_list) {
        this.tag_list = tag_list;
    }

    public String getPlace_guess() {
        return place_guess;
    }

    /**
     * Name of the place where the observation was recorded. 
     * Note that iNat will <b>not</b> try to automatically look up coordinates based on this string. 
     * That task is uncertain enough that the UI should perform it so the user can confirm it. 
     */
    public void setPlace_guess(String place_guess) {
        this.place_guess = place_guess;
    }

    public double getLatitude() {
        return latitude;
    }

    /**
     * Presumed datum is WGS84. 
     * Between -90 to 90.
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitide() {
        return longitide;
    }

    /**
     * Presumed datum is WGS84. 
     * Between -180 to 180.
     */
    public void setLongitide(double longitide) {
        this.longitide = longitide;
    }

    public int getMap_scale() {
        return map_scale;
    }

    /**
     * Google Maps zoom level at which to show this observation's map marker. 
     * Between 0 to 19;
     */
    public void setMap_scale(int map_scale) {
        this.map_scale = map_scale;
    }

    public int getPositional_accuracy() {
        return positional_accuracy;
    }

    /**
     * Positional accuracy of the observation coordinates in meters. 
     * Any positive integer.
     */
    public void setPositional_accuracy(int positional_accuracy) {
        this.positional_accuracy = positional_accuracy;
    }

    public INaturalistGeoprivacy getGeoprivacy() {
        return geoprivacy;
    }

    public void setGeoprivacy(INaturalistGeoprivacy geoprivacy) {
        this.geoprivacy = geoprivacy;
    }
    
}
