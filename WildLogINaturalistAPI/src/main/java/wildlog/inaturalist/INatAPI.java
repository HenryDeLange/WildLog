package wildlog.inaturalist;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import wildlog.inaturalist.queryobjects.INaturalistAddObservation;
import wildlog.inaturalist.queryobjects.INaturalistSearchObservations;
import wildlog.inaturalist.queryobjects.INaturalistUpdateObservation;
import wildlog.inaturalist.responseobjects.INaturalistObservation;


public class INatAPI {
    private static final int PAGE_LIMIT_INATURALIST = 20;
    private static final Gson GSON = new Gson();
    private static final JsonParser PARSER = new JsonParser();
    
    
    private INatAPI() {
    }
    
    
    public static JsonElement getObservation(long inINaturalistID) {
        try {
            // POST die data na iNaturalist
            URL url = new URL("https://www.inaturalist.org/observations/" + inINaturalistID + ".json");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            // Lees die terugvoer
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
    
    public static List<INaturalistObservation> searchObservations(INaturalistSearchObservations inINaturalistSearchObservations) {
        try {    
            int requestPage = 0;
            int totalEntries = 0;
            final List<INaturalistObservation> lstAllINaturalistResults = new ArrayList<>(PAGE_LIMIT_INATURALIST);
            do {
                requestPage = requestPage + 1; // Note: Increase first because page 0 and 1 seems to return the same info
                URL url = new URL("https://www.inaturalist.org/observations.json" 
                        + inINaturalistSearchObservations.getQueryString());
                URLConnection urlConnection = url.openConnection();
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
                    List<INaturalistObservation> lstINatObservation = GSON.fromJson(
                            reader, new TypeToken<List<INaturalistObservation>>(){}.getType());
                    if (lstINatObservation != null && !lstINatObservation.isEmpty()) {
                        lstAllINaturalistResults.addAll(lstINatObservation);
                    }
                }
            }
            while ((requestPage * PAGE_LIMIT_INATURALIST) < totalEntries);
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
            // Lees die terugvoer
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
            // Lees die terugvoer
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
            // DELETE die data na iNaturalist
            URL url = new URL("https://www.inaturalist.org/observations/" + inINaturalistID + ".json");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("DELETE");
            urlConnection.setDoInput(true);
            urlConnection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
            urlConnection.setRequestProperty("Authorization", "Bearer " + inToken);
            // Lees die terugvoer
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
