package wildlog.reports;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.Level;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Font;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.enums.VisitType;
import wildlog.data.utils.UtilsData;
import wildlog.ui.helpers.ProgressbarTask;
import wildlog.ui.helpers.WLOptionPane;
import wildlog.utils.UtilsConcurency;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.UtilsTime;
import wildlog.utils.WildLogPaths;


public class ReportVisitDates {

    public static void doReport(String inTitle) {
        UtilsConcurency.kickoffProgressbarTask(WildLogApp.getApplication(), new ProgressbarTask(WildLogApp.getApplication()) {
            @Override
            protected Object doInBackground() throws Exception {
                setMessage("Starting the Report: " + inTitle);
                setTaskProgress(1);
                setMessage("Busy with the Report: " + inTitle + "... " + getProgress() + "%");
                Path path = WildLogPaths.WILDLOG_EXPORT_REPORTS.getAbsoluteFullPath().resolve(inTitle + ".xls");
                setTaskProgress(2);
                setMessage("Busy with the Report: " + inTitle + "... " + getProgress() + "%");
                List<Visit> lstVisits = WildLogApp.getApplication().getDBI().listVisits(null, 0, null, true, Visit.class);
                Files.createDirectories(path.getParent());
                setTaskProgress(5);
                setMessage("Busy with the Report: " + inTitle + "... " + getProgress() + "%");
                // Sort the Visits to be ordered by Location and then date
                Collections.sort(lstVisits, new Comparator<Visit>() {
                    @Override
                    public int compare(Visit inVisit1, Visit inVisit2) {
                        int result = inVisit1.getCachedLocationName().compareTo(inVisit2.getCachedLocationName());
                        if (result == 0) {
                            if (inVisit1.getStartDate() == null) {
                                result = -1;
                            }
                            else
                            if (inVisit2.getStartDate() == null) {
                                result = 1;
                            }
                            else {
                                result = UtilsTime.getLocalDateFromDate(inVisit1.getStartDate()).compareTo(UtilsTime.getLocalDateFromDate(inVisit2.getStartDate()));
                            }
                        }
                        if (result == 0) {
                            if (inVisit1.getEndDate() == null) {
                                result = -1;
                            }
                            else
                            if (inVisit2.getEndDate() == null) {
                                result = 1;
                            }
                            else {
                                result = UtilsTime.getLocalDateFromDate(inVisit1.getEndDate()).compareTo(UtilsTime.getLocalDateFromDate(inVisit2.getEndDate()));
                            }
                        }
                        return result;
                    }
                });
                setTaskProgress(10);
                setMessage("Busy with the Report: " + inTitle + "... " + getProgress() + "%");
                // Calculate the values
                Map<String, List<ReportData>> mapReportData = new HashMap<>();
                ReportData prevReportData = new ReportData();
                for (Visit visit : lstVisits) {
                    List<ReportData> lstReportData = mapReportData.get(visit.getCachedLocationName());
                    if (lstReportData == null) {
                        lstReportData = new ArrayList<>();
                        mapReportData.put(visit.getCachedLocationName(), lstReportData);
                    }
                    ReportData reportData = new ReportData();
                    reportData.locationName = visit.getCachedLocationName();
                    reportData.visitName = visit.getName();
                    reportData.startDate = UtilsTime.getLocalDateFromDate(visit.getStartDate());
                    reportData.endDate = UtilsTime.getLocalDateFromDate(visit.getEndDate());
                    reportData.visitType = visit.getType();
                    if (reportData.startDate != null && reportData.endDate != null) {
                        reportData.days = (int) ChronoUnit.DAYS.between(reportData.startDate, reportData.endDate) + 1; // +1 om die eerste dag ook te tel
                    }
                    else {
                        reportData.days = 0;
                    }
                    reportData.sigtingCount = WildLogApp.getApplication().getDBI().countSightings(0, 0, visit.getLocationID(), visit.getID());
                    if (!reportData.locationName.equals(prevReportData.locationName)) {
                        prevReportData = new ReportData();
                    }
                    LocalDate prevDate = prevReportData.endDate;
                    if (prevDate == null) {
                        prevDate = prevReportData.startDate;
                    }
                    if (prevDate != null && reportData.startDate != null) {
                        if (reportData.startDate.isBefore(prevDate)) {
                            reportData.isOverlapping = true;
                        }
                        if (ChronoUnit.DAYS.between(prevDate, reportData.startDate) > 1) {
                            reportData.isMissing = true;
                        }
                    }
                    else {
                        reportData.isMissing = false;
                        reportData.isOverlapping = false;
                    }
                    List<Sighting> lstSightings = WildLogApp.getApplication().getDBI().listSightings(0, visit.getLocationID(), visit.getID(), false, Sighting.class);
                    boolean outsideRange = false;
                    int startGap = Integer.MAX_VALUE;
                    int endGap = Integer.MAX_VALUE;
                    for (Sighting sighting : lstSightings) {
                        LocalDate sightingDate = UtilsTime.getLocalDateFromDate(sighting.getDate());
                        if (sightingDate.isBefore(reportData.startDate) || sightingDate.isAfter(reportData.endDate)) {
                            outsideRange = true;
                        }
                        startGap = Math.min(startGap, Math.abs((int) ChronoUnit.DAYS.between(sightingDate, reportData.startDate)));
                        endGap = Math.min(endGap, Math.abs((int) ChronoUnit.DAYS.between(sightingDate, reportData.endDate)));
                    }
                    reportData.observationsDateRange = "";
                    if (outsideRange) {
                        reportData.observationsDateRange = "OBSERVATION DATE OUTSIDE PERIOD DATE. ";
                    }
                    if (startGap > 2) {
                        reportData.observationsDateRange = "LATE OBSERVATION START DATE. ";
                    }
                    if (endGap > 2) {
                        reportData.observationsDateRange = "EARLY OBSERVATION END DATE.";
                    }
                    // If there was a missing period, then add a row for it
                    if (reportData.isMissing) {
                        reportData.isMissing = false;
                        ReportData missingReportData = new ReportData();
                        missingReportData.locationName = reportData.locationName;
                        missingReportData.visitName = "MISSING";
                        missingReportData.startDate = prevDate;
                        missingReportData.endDate = reportData.startDate;
                        if (missingReportData.startDate != null && missingReportData.endDate != null) {
                            missingReportData.days = (int) ChronoUnit.DAYS.between(missingReportData.startDate, missingReportData.endDate) + 1; // +1 om die eerste dag ook te tel
                        }
                        else {
                            missingReportData.days = 0;
                        }
                        lstReportData.add(missingReportData);
                    }
                    lstReportData.add(reportData);
                    prevReportData = reportData;
                }
                setTaskProgress(50);
                setMessage("Busy with the Report: " + inTitle + "... " + getProgress() + "%");
                // Write the report
                HSSFWorkbook workbook = new HSSFWorkbook();
                HSSFCellStyle styleHeader = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setBold(true);
                styleHeader.setFont(font);
                HSSFCellStyle styleWarning = workbook.createCellStyle();
                font = workbook.createFont();
                font.setColor(HSSFColor.HSSFColorPredefined.RED.getIndex());
                font.setBold(true);
                styleWarning.setFont(font);
                List<String> lstKeys = new ArrayList<>(mapReportData.keySet());
                Collections.sort(lstKeys);
                for (String key : lstKeys) {
                    HSSFSheet sheet = workbook.createSheet(key);
                    HSSFRow row = sheet.createRow(0);
                    row.createCell(0).setCellValue("Place Name");
                    row.createCell(1).setCellValue("Period Name");
                    row.createCell(2).setCellValue("Start Date");
                    row.createCell(3).setCellValue("End Date");
                    row.createCell(4).setCellValue("Period Type");
                    row.createCell(5).setCellValue("Days");
                    row.createCell(6).setCellValue("Observations");
                    row.createCell(7).setCellValue("Has Overlap");
                    row.createCell(8).setCellValue("Incorrect Observation Dates");
                    row.getCell(0).setCellStyle(styleHeader);
                    row.getCell(1).setCellStyle(styleHeader);
                    row.getCell(2).setCellStyle(styleHeader);
                    row.getCell(3).setCellStyle(styleHeader);
                    row.getCell(4).setCellStyle(styleHeader);
                    row.getCell(5).setCellStyle(styleHeader);
                    row.getCell(6).setCellStyle(styleHeader);
                    row.getCell(7).setCellStyle(styleHeader);
                    row.getCell(8).setCellStyle(styleHeader);
                    List<ReportData> lstReportData = mapReportData.get(key);
                    for (int r = 0; r < lstReportData.size(); r++) {
                        ReportData reportData = lstReportData.get(r);
                        row = sheet.createRow(r + 1);
                        row.createCell(0).setCellValue(reportData.locationName);
                        row.createCell(1).setCellValue(reportData.visitName);
                        if (reportData.visitName.equals("MISSING")) {
                            row.getCell(1).setCellStyle(styleWarning);
                        }
                        if (reportData.startDate != null) {
                            row.createCell(2).setCellValue(UtilsTime.WL_DATE_FORMATTER_FOR_VISITS_WEI.format(reportData.startDate));
                        }
                        if (reportData.endDate != null) {
                            row.createCell(3).setCellValue(UtilsTime.WL_DATE_FORMATTER_FOR_VISITS_WEI.format(reportData.endDate));
                        }
                        row.createCell(4).setCellValue(UtilsData.stringFromObject(reportData.visitType));
                        row.createCell(5).setCellValue(reportData.days);
                        row.createCell(6).setCellValue(reportData.sigtingCount);
                        if (reportData.isOverlapping) {
                            row.createCell(7).setCellValue("OVERLAPPING");
                            row.getCell(7).setCellStyle(styleWarning);
                        }
                        if (reportData.observationsDateRange != null && !reportData.observationsDateRange.isEmpty()) {
                            row.createCell(8).setCellValue(reportData.observationsDateRange.trim());
                            row.getCell(8).setCellStyle(styleWarning);
                        }
                    }
                    sheet.autoSizeColumn(0);
                    sheet.autoSizeColumn(1);
                    sheet.autoSizeColumn(2);
                    sheet.autoSizeColumn(3);
                    sheet.autoSizeColumn(4);
                    sheet.autoSizeColumn(5);
                    sheet.autoSizeColumn(6);
                    sheet.autoSizeColumn(7);
                    sheet.autoSizeColumn(8);
                }
                try (FileOutputStream out = new FileOutputStream(path.toFile())) {
                    workbook.write(out);
                }
                catch (IOException ex) {
                    WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                    WLOptionPane.showMessageDialog(WildLogApp.getApplication().getMainFrame(),
                            "<html>The new report could not be created. "
                                    + "<br/>If the file is already open in another application, please close it and try again.</html>",
                            "Report Error", WLOptionPane.ERROR_MESSAGE);
                }
                // Open the folder
                UtilsFileProcessing.openFile(path);
                setTaskProgress(100);
                setMessage("Done with the Report: " + inTitle);
                return null;
            }
        });
    }
    
    private static class ReportData {
        private String locationName;
        private String visitName;
        private LocalDate startDate;
        private LocalDate endDate;
        private VisitType visitType;
        private int days;
        private int sigtingCount;
        private boolean isMissing;
        private boolean isOverlapping;
        private String observationsDateRange;
    }
    
}