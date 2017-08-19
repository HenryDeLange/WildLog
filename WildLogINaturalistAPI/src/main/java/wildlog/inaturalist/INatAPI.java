package wildlog.inaturalist;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.IOException;
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
    
    
    private INatAPI() {
    }
    
    
    public static INaturalistObservation getObservation(long inINaturalistID) {
        try {
            // POST die data na iNaturalist
            URL url = new URL("https://www.inaturalist.org/observations/" + inINaturalistID + ".json");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            // Lees die terugvoer
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"))) {
// TODO: Map die comments
                List<INaturalistObservation> lstINatObservation = GSON.fromJson(
                        reader, new TypeToken<List<INaturalistObservation>>(){}.getType());
                if (lstINatObservation != null && !lstINatObservation.isEmpty()) {
                    urlConnection.disconnect();
                    return lstINatObservation.get(0);
                }
            }
        }
        catch (JsonSyntaxException | IOException ex) {
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
        catch (JsonIOException | JsonSyntaxException | IOException e) {
            e.printStackTrace(System.err);
        }
        return null;
    }
    
    public static INaturalistObservation createObservation(INaturalistAddObservation inINaturalistAddObservation, String inToken) {
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
                List<INaturalistObservation> lstINatObservation = GSON.fromJson(
                        reader, new TypeToken<List<INaturalistObservation>>(){}.getType());
                if (lstINatObservation != null && !lstINatObservation.isEmpty()) {
                    urlConnection.disconnect();
                    return lstINatObservation.get(0);
                }
            }
            // POST die foto(s) na iNaturalist
// TODO
        }
        catch (JsonSyntaxException | IOException ex) {
            ex.printStackTrace(System.err);
        }
        return null;
    }
    
    public static INaturalistObservation updateObservation(INaturalistUpdateObservation inINaturalistUpdateObservation, String inToken) {
        try {
            // PUT die data na iNaturalist
            URL url = new URL("https://www.inaturalist.org/observations.json");
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
                List<INaturalistObservation> lstINatObservation = GSON.fromJson(
                        reader, new TypeToken<List<INaturalistObservation>>(){}.getType());
                if (lstINatObservation != null && !lstINatObservation.isEmpty()) {
                    urlConnection.disconnect();
                    return lstINatObservation.get(0);
                }
            }
            // POST die foto(s) na iNaturalist
// TODO
        }
        catch (JsonSyntaxException | IOException ex) {
            ex.printStackTrace(System.err);
        }
        return null;
    }
    
    public static INaturalistObservation deleteObservation(long inINaturalistID, String inToken) {
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
                List<INaturalistObservation> lstINatObservation = GSON.fromJson(
                        reader, new TypeToken<List<INaturalistObservation>>(){}.getType());
                if (lstINatObservation != null && !lstINatObservation.isEmpty()) {
                    urlConnection.disconnect();
                    return lstINatObservation.get(0);
                }
            }
        }
        catch (JsonSyntaxException | IOException ex) {
            ex.printStackTrace(System.err);
        }
        return null;
    }
    
}
