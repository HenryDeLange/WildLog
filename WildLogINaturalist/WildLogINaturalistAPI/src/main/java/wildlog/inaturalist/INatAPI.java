package wildlog.inaturalist;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import wildlog.inaturalist.queryobjects.INaturalistAddObservation;
import wildlog.inaturalist.queryobjects.INaturalistSearchObservations;
import wildlog.inaturalist.queryobjects.INaturalistUpdateObservation;
import wildlog.inaturalist.queryobjects.INaturalistUploadPhoto;
import wildlog.inaturalist.utils.UtilsINaturalist;


public class INatAPI {
    private static final int PAGE_LIMIT_INATURALIST = 20;
    private static final JsonParser PARSER = new JsonParser();
    
    
    private INatAPI() {
//    API Documentation
//    http://www.inaturalist.org/pages/api+reference

//    WildLog App
//    http://www.inaturalist.org/oauth/applications
//    http://www.inaturalist.org/oauth/applications/179

//    WildLog_ID Field
//    https://www.inaturalist.org/observation_fields
//    https://www.inaturalist.org/observation_fields/7112
    }
    
// TODO: Kyk of ek meeste van hierdie services kan skuif na die NodeJS API want dit lyk vinniger
    
    public static JsonElement getObservation(long inINaturalistID) {
        try {
            // POST die data na iNaturalist
            URL url = new URL("https://www.inaturalist.org/observations/" + inINaturalistID + ".json");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            // Lees die terugvoer (dit doen ook dan eers die stuur)
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"))) {
                JsonElement jsonElement = PARSER.parse(reader);
                return jsonElement;
            }
            finally {
                urlConnection.disconnect();
            }
        }
        catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
        return null;
    }
    
    public static List<JsonObject> getUserObservations(String inINaturalistLoginName, String inTaxonID, String inPlaceName) {
        try {    
            int requestPage = 0;
            int totalEntries = 0;
            final List<JsonObject> lstAllINaturalistResults = new ArrayList<>(PAGE_LIMIT_INATURALIST);
            do {
                requestPage = requestPage + 1; // Note: Increase first because page 0 and 1 seems to return the same info
                String urlString = "https://www.inaturalist.org/observations/" + inINaturalistLoginName + ".json"
                        + "?page=" + requestPage + "&per_page=" + PAGE_LIMIT_INATURALIST;
                if (inTaxonID != null && !inTaxonID.isEmpty()) {
                    urlString = urlString + "&taxon_id=" + inTaxonID;
                }
                if (inPlaceName != null && !inPlaceName.isEmpty()) {
                    urlString = urlString + "&place_id=" + inPlaceName;
                }
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoInput(true);
                Map<String, List<String>> mapHTTPHeaders = urlConnection.getHeaderFields();
                Set<Map.Entry<String, List<String>>> setHTTPHeaderEntries = mapHTTPHeaders.entrySet();
                for (Map.Entry<String, List<String>> headerEntry : setHTTPHeaderEntries) {
                    if ("X-Total-Entries".equalsIgnoreCase(headerEntry.getKey())) {
                        List<String> headerValues = headerEntry.getValue();
                        for (String value : headerValues) {
                            try {
                                totalEntries = Integer.parseInt(value);
                                break;
                            }
                            catch (NumberFormatException ex) {
                                ex.printStackTrace(System.err);
                            }
                        }
                        break;
                    }
                }
                // Lees die terugvoer (dit doen ook dan eers die stuur)
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"))) {
                    JsonElement jsonElement = PARSER.parse(reader);
                    if (jsonElement != null) {
                        JsonArray jsonArray = jsonElement.getAsJsonArray();
                        for (int t = 0; t < jsonArray.size(); t++) {
                            lstAllINaturalistResults.add(jsonArray.get(t).getAsJsonObject());
                        }
                    }
                }
            }
            while (lstAllINaturalistResults.size() < totalEntries);
            return lstAllINaturalistResults;
        }
        catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return null;
    }
    
    public static List<JsonObject> searchObservations(INaturalistSearchObservations inINaturalistSearchObservations) {
        try {    
            int requestPage = 0;
            int totalEntries = 0;
            final List<JsonObject> lstAllINaturalistResults = new ArrayList<>(PAGE_LIMIT_INATURALIST);
            inINaturalistSearchObservations.setPer_page(PAGE_LIMIT_INATURALIST);
            do {
                requestPage = requestPage + 1; // Note: Increase first because page 0 and 1 seems to return the same info
                inINaturalistSearchObservations.setPage(requestPage);
                URL url = new URL("https://www.inaturalist.org/observations.json" 
                        + inINaturalistSearchObservations.getQueryString());
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoInput(true);
                Map<String, List<String>> mapHTTPHeaders = urlConnection.getHeaderFields();
                Set<Map.Entry<String, List<String>>> setHTTPHeaderEntries = mapHTTPHeaders.entrySet();
                for (Map.Entry<String, List<String>> headerEntry : setHTTPHeaderEntries) {
                    if ("X-Total-Entries".equalsIgnoreCase(headerEntry.getKey())) {
                        List<String> headerValues = headerEntry.getValue();
                        for (String value : headerValues) {
                            try {
                                totalEntries = Integer.parseInt(value);
                                break;
                            }
                            catch (NumberFormatException ex) {
                                ex.printStackTrace(System.err);
                            }
                        }
                        break;
                    }
                }
                // Lees die terugvoer
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"))) {
                    JsonElement jsonElement = PARSER.parse(reader);
                    if (jsonElement != null) {
                        JsonArray jsonArray = jsonElement.getAsJsonArray();
                        for (int t = 0; t < jsonArray.size(); t++) {
                            lstAllINaturalistResults.add(jsonArray.get(t).getAsJsonObject());
                        }
                    }
                }
            }
            while (lstAllINaturalistResults.size() < totalEntries);
            return lstAllINaturalistResults;
        }
        catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return null;
    }
    
    public static JsonElement createObservation(INaturalistAddObservation inINaturalistAddObservation, String inToken) {
        try {
            // POST die data na iNaturalist
            URL url = new URL("https://www.inaturalist.org/observations.json");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
            urlConnection.setRequestProperty("Authorization", "Bearer " + inToken);
            try (OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream())) {
                writer.write(inINaturalistAddObservation.getDataString());
                writer.flush();
            }
            // Lees die terugvoer (dit doen ook dan eers die stuur)
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"))) {
                JsonElement jsonElement = PARSER.parse(reader);
                return jsonElement;
            }
            finally {
                urlConnection.disconnect();
            }
        }
        catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
        return null;
    }
    
    public static JsonElement updateObservation(INaturalistUpdateObservation inINaturalistUpdateObservation, String inToken) {
        try {
            // PUT die data na iNaturalist
            URL url = new URL("https://www.inaturalist.org/observations/" + inINaturalistUpdateObservation.getId() + ".json");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("PUT");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
            urlConnection.setRequestProperty("Authorization", "Bearer " + inToken);
            try (OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream())) {
                writer.write(inINaturalistUpdateObservation.getDataString());
                writer.flush();
            }
            // Lees die terugvoer (dit doen ook dan eers die stuur)
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"))) {
                JsonElement jsonElement = PARSER.parse(reader);
                return jsonElement;
            }
            finally {
                urlConnection.disconnect();
            }
        }
        catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
        return null;
    }
    
    public static JsonElement deleteObservation(long inINaturalistID, String inToken) {
        try {
            // DELETE die data op iNaturalist
            URL url = new URL("https://www.inaturalist.org/observations/" + inINaturalistID + ".json");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("DELETE");
            urlConnection.setDoInput(true);
            urlConnection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
            urlConnection.setRequestProperty("Authorization", "Bearer " + inToken);
            // Lees die terugvoer (dit doen ook dan eers die stuur)
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"))) {
                JsonElement jsonElement = PARSER.parse(reader);
                return jsonElement;
            }
            finally {
                urlConnection.disconnect();
            }
        }
        catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
        return null;
    }
    
    public static JsonElement uploadPhoto(INaturalistUploadPhoto inINaturalistUploadPhoto, String inToken) {
        try {
            final String BOUNDARY = "afskorting" + Long.toHexString(System.currentTimeMillis());
            final String CRLF = "\r\n";
            // POST die data na iNaturalist
            URL url = new URL("https://www.inaturalist.org/observation_photos.json");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestProperty("Content-type", "multipart/form-data; boundary=" + BOUNDARY);
            urlConnection.setRequestProperty("Authorization", "Bearer " + inToken);
            try (OutputStream output = urlConnection.getOutputStream();
                 PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, "UTF-8"), true);) {
                // Send normal parameters
                writer.append("--" + BOUNDARY).append(CRLF);
                writer.append("Content-Disposition: form-data; name=\"observation_photo[observation_id]\"").append(CRLF);
                writer.append(CRLF);
                writer.append(Long.toString(inINaturalistUploadPhoto.getObservation_id())).append(CRLF);
                writer.flush();
                // Send binary file
                writer.append("--" + BOUNDARY).append(CRLF);
                writer.append("Content-Disposition: form-data; name=\"file\"; "
                        + "filename=\"WildLogFile.jpg\"").append(CRLF);
                writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(
                        inINaturalistUploadPhoto.getFile().getFileName().toString())).append(CRLF);
                
                writer.append(CRLF);
                writer.flush();
                Files.copy(inINaturalistUploadPhoto.getFile(), output);
                // Important before continuing with writer!
                output.flush();
                // CRLF is important! It indicates end of boundary.
                writer.append(CRLF);
                writer.flush();
                // End of multipart/form-data.
                writer.append("--" + BOUNDARY + "--").append(CRLF);
                writer.flush();
            }
            // Lees die terugvoer (dit doen ook dan eers die stuur)
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"))) {
                JsonElement jsonElement = PARSER.parse(reader);
                return jsonElement;
            }
            finally {
                urlConnection.disconnect();
            }
        }
        catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
        return null;
    }
    
    public static JsonElement getAuthenticatedUser(String inToken) {
        try {
            // POST die data na iNaturalist
            URL url = new URL("https://www.inaturalist.org/users/edit.json");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
            urlConnection.setRequestProperty("Authorization", "Bearer " + inToken);
            // Lees die terugvoer (dit doen ook dan eers die stuur)
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"))) {
                JsonElement jsonElement = PARSER.parse(reader);
                return jsonElement;
            }
            finally {
                urlConnection.disconnect();
            }
        }
        catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
        return null;
    }
    
    public static JsonElement addObservationFieldValue(long inObservation_id, int inObservation_field_id, String inValue, String inToken) {
        try {
            // PUT die data na iNaturalist
            URL url = new URL("https://www.inaturalist.org/observation_field_values.json");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
            urlConnection.setRequestProperty("Authorization", "Bearer " + inToken);
            StringBuilder stringBuilder = new StringBuilder(64);
            stringBuilder.append("observation_field_value[observation_id]=").append(inObservation_id).append('&');
            stringBuilder.append("observation_field_value[observation_field_id]=").append(inObservation_field_id).append('&');
            stringBuilder.append("observation_field_value[value]=").append(UtilsINaturalist.forURL(inValue));
            try (OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream())) {
                writer.write(stringBuilder.toString());
                writer.flush();
            }
            // Lees die terugvoer (dit doen ook dan eers die stuur)
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"))) {
                JsonElement jsonElement = PARSER.parse(reader);
                return jsonElement;
            }
            finally {
                urlConnection.disconnect();
            }
        }
        catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
        return null;
    }
    
    public static JsonElement removeObservationFieldValue(int inId, String inToken) {
        try {
            // DELETE die data op iNaturalist
            URL url = new URL("https://www.inaturalist.org/observation_field_values/" + inId + ".json");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("DELETE");
            urlConnection.setDoInput(true);
            urlConnection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
            urlConnection.setRequestProperty("Authorization", "Bearer " + inToken);
            // Lees die terugvoer (dit doen ook dan eers die stuur)
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"))) {
                JsonElement jsonElement = PARSER.parse(reader);
                return jsonElement;
            }
            finally {
                urlConnection.disconnect();
            }
        }
        catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
        return null;
    }
    
}
