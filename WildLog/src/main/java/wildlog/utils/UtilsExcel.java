package wildlog.utils;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.maps.utils.UtilsGPS;


public final class UtilsExcel {
    
    private UtilsExcel() {
    }
    
    public static void exportSightingToExcelHeader(Sheet sheet, int rowCount) {
        Row row = sheet.createRow(rowCount);
        int col = 0;
        row.createCell(col++).setCellValue("CREATURE");
        row.createCell(col++).setCellValue("SCIENTIFIC_NAME");
        row.createCell(col++).setCellValue("CREATURE_TYPE");
        row.createCell(col++).setCellValue("PLACE");
        row.createCell(col++).setCellValue("PLACE_GPS_ACCURACY");
        row.createCell(col++).setCellValue("PLACE_GPS_ACCURACY_VALUE");
        row.createCell(col++).setCellValue("PLACE_LATITUDE");
        row.createCell(col++).setCellValue("PLACE_LONGITUDE");
        row.createCell(col++).setCellValue("PERIOD");
        row.createCell(col++).setCellValue("PERIOD_TYPE");
        row.createCell(col++).setCellValue("PERIOD_START_DATE");
        row.createCell(col++).setCellValue("PERIOD_END_DATE");
        row.createCell(col++).setCellValue("PERIOD_DESCRIPTION");
        row.createCell(col++).setCellValue("OBSERVATION");
        row.createCell(col++).setCellValue("CERTAINTY");
        row.createCell(col++).setCellValue("EVIDENCE");
        row.createCell(col++).setCellValue("TIME_ACCURACY");
        row.createCell(col++).setCellValue("TIME_OF_DAY");
        row.createCell(col++).setCellValue("OBSERVATION_DATE");
        row.createCell(col++).setCellValue("OBSERVATION_TIME");
        row.createCell(col++).setCellValue("OBSERVATION_GPS_ACCURACY");
        row.createCell(col++).setCellValue("OBSERVATION_GPS_ACCURACY_VALUE");
        row.createCell(col++).setCellValue("OBSERVATION_LATITUDE");
        row.createCell(col++).setCellValue("OBSERVATION_LONGITUDE");
        row.createCell(col++).setCellValue("NUMBER_OF_CREATURES");
        row.createCell(col++).setCellValue("LIFE_STATUS");
        row.createCell(col++).setCellValue("TAG");
        row.createCell(col++).setCellValue("DETAILS");
    }

    public static void exportSightingToExcel(Sheet sheet, int rowCount, Sighting tempSighting, WildLogApp inApp) {
        Row row = sheet.createRow(rowCount);
        int col = 0;
        row.createCell(col++).setCellValue(tempSighting.getCachedElementName());
        Element tempElement = inApp.getDBI().findElement(tempSighting.getElementID(), null, Element.class);
        if (tempElement != null) {
            row.createCell(col++).setCellValue(tempElement.getScientificName());
            row.createCell(col++).setCellValue(getStringValue(tempElement.getType()));
        }
        else {
            row.createCell(col++).setCellValue("");
            row.createCell(col++).setCellValue("");
        }
        row.createCell(col++).setCellValue(tempSighting.getCachedLocationName());
        Location tempLocation = inApp.getDBI().findLocation(tempSighting.getLocationID(), null, Location.class);
        if (tempLocation != null) {
            row.createCell(col++).setCellValue(getStringValue(tempLocation.getGPSAccuracy()));
            row.createCell(col++).setCellValue(getStringValue(tempLocation.getGPSAccuracyValue()));
            row.createCell(col++).setCellValue(UtilsGPS.getLatDecimalDegree(tempLocation));
            row.createCell(col++).setCellValue(UtilsGPS.getLonDecimalDegree(tempLocation));
        }
        else {
            row.createCell(col++).setCellValue("");
            row.createCell(col++).setCellValue("");
            row.createCell(col++).setCellValue("");
            row.createCell(col++).setCellValue("");
        }
        row.createCell(col++).setCellValue(tempSighting.getCachedVisitName());
        Visit tempVisit = inApp.getDBI().findVisit(tempSighting.getVisitID(), null, false, Visit.class);
        if (tempVisit != null) {
            row.createCell(col++).setCellValue(getStringValue(tempVisit.getType()));
            if (tempVisit.getStartDate() != null) {
                row.createCell(col++).setCellValue(UtilsTime.WL_DATE_FORMATTER_FOR_FILES.format(
                        UtilsTime.getLocalDateFromDate(tempVisit.getStartDate())));
            }
            else {
                row.createCell(col++).setCellValue("");
            }
            if (tempVisit.getEndDate() != null) {
                row.createCell(col++).setCellValue(UtilsTime.WL_DATE_FORMATTER_FOR_FILES.format(
                        UtilsTime.getLocalDateFromDate(tempVisit.getEndDate())));
            }
            else {
                row.createCell(col++).setCellValue("");
            }
            row.createCell(col++).setCellValue(getStringValue(tempVisit.getDescription()));
        }
        else {
            row.createCell(col++).setCellValue("");
            row.createCell(col++).setCellValue("");
            row.createCell(col++).setCellValue("");
            row.createCell(col++).setCellValue("");
        }
        row.createCell(col++).setCellValue(Long.toString(tempSighting.getID()));
        row.createCell(col++).setCellValue(getStringValue(tempSighting.getCertainty()));
        row.createCell(col++).setCellValue(getStringValue(tempSighting.getSightingEvidence()));
        row.createCell(col++).setCellValue(getStringValue(tempSighting.getTimeAccuracy()));
        row.createCell(col++).setCellValue(getStringValue(tempSighting.getTimeOfDay()));
        row.createCell(col++).setCellValue(UtilsTime.WL_DATE_FORMATTER_FOR_FILES.format(
                UtilsTime.getLocalDateFromDate(tempSighting.getDate())));
        row.createCell(col++).setCellValue(UtilsTime.getLocalTimeFromDate(tempSighting.getDate()).toString());
        row.createCell(col++).setCellValue(getStringValue(tempSighting.getGPSAccuracy()));
        row.createCell(col++).setCellValue(getStringValue(tempSighting.getGPSAccuracyValue()));
        row.createCell(col++).setCellValue(UtilsGPS.getLatDecimalDegree(tempSighting));
        row.createCell(col++).setCellValue(UtilsGPS.getLonDecimalDegree(tempSighting));
        row.createCell(col++).setCellValue(tempSighting.getNumberOfElements());
        row.createCell(col++).setCellValue(getStringValue(tempSighting.getLifeStatus()));
        row.createCell(col++).setCellValue(getStringValue(tempSighting.getTag()));
        row.createCell(col++).setCellValue(getStringValue(tempSighting.getDetails()));
    }
    
    private static String getStringValue(Object inValue) {
        if (inValue != null) {
            return inValue.toString();
        }
        return "";
    }
    
}
