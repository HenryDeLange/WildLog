package wildlog.ui.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import org.apache.logging.log4j.Level;
import wildlog.WildLogApp;
import wildlog.astro.AstroCalculator;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.enums.Latitudes;
import wildlog.data.enums.Longitudes;
import wildlog.data.enums.TimeFormat;
import wildlog.maps.utils.UtilsGPS;


public final class UtilsTime {
    // The default pattern used in WildLog to display dates. 
    // This pattern is used by the SimpleDateFormat instances used by the JXDatePicker's
// TODO: Vervang eendag al die SwingX date pickers met iets beter, dalk JavaFX, en raak dan ontslae van SimpleDateFormat.
    public static final String DEFAULT_WL_DATE_FORMAT_PATTERN = "dd MMM yyyy";
    // Preffer to use DateTimeFormatter because it is threadsafe and SimpleDateFormat is not.
    public static final DateTimeFormatter WL_DATE_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_WL_DATE_FORMAT_PATTERN);
    public static final DateTimeFormatter WL_DATE_FORMATTER_WITH_HHMM = DateTimeFormatter.ofPattern("dd MMM yyyy (HH:mm)");
    public static final DateTimeFormatter WL_DATE_FORMATTER_WITH_HHMMSS = DateTimeFormatter.ofPattern("dd MMM yyyy (HH:mm:ss)");
    public static final DateTimeFormatter WL_DATE_FORMATTER_WITH_HHMMSS_AMPM = DateTimeFormatter.ofPattern("dd MMM yyyy (hh:mm:ss a)");
    public static final DateTimeFormatter WL_DATE_FORMATTER_FOR_VISIT_NAME = DateTimeFormatter.ofPattern("dd MMM yyyy (HH'h'mm)");
    public static final DateTimeFormatter WL_DATE_FORMATTER_FOR_FILES = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter WL_DATE_FORMATTER_FOR_FILES_CAMERATRAP_TIMESTAMP = DateTimeFormatter.ofPattern("yyyy MM dd HH mm ss");
    public static final DateTimeFormatter WL_DATE_FORMATTER_FOR_FILES_WITH_TIMESTAMP = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH'h'mm'm'ss's'");
    public static final DateTimeFormatter WL_DATE_FORMATTER_FOR_BACKUP_MONTHLY = DateTimeFormatter.ofPattern("yyyy-MM, MMM 'week' W");

    
    private UtilsTime() {
    }

    
    public static void modeChanged(JSpinner inSpnHours, JSpinner inSpnMinutes, JSpinner inSpnSeconds, JComboBox<TimeFormat> inCmbTimeFormat, TimeFormat inPrevTimeFormat) {
        // NOTE: The 12 o'clock times are inclusive going forwards, because by 12:00 we are already a few (mili)seconds into PM.
        //       For example time is calculated like this:
        //         day time morning -> 11:59am -> 12:00pm -> 12:01pm -> day time afternoon
        //         night time evening -> 11:59pm -> 12:00am -> 12:01am -> night time morning
        if (!TimeFormat.AM.equals(inCmbTimeFormat.getSelectedItem())
                && !TimeFormat.PM.equals(inCmbTimeFormat.getSelectedItem())
                && !TimeFormat.H24.equals(inCmbTimeFormat.getSelectedItem())) {
            inSpnHours.getModel().setValue(0);
            inSpnMinutes.getModel().setValue(0);
            if (inSpnSeconds != null) {
                inSpnSeconds.getModel().setValue(0);
            }
        }
        if (TimeFormat.AM.equals(inCmbTimeFormat.getSelectedItem()) || TimeFormat.PM.equals(inCmbTimeFormat.getSelectedItem())) {
            if ((int)inSpnHours.getValue() > 12) {
                inSpnHours.getModel().setValue((int)inSpnHours.getValue() - 12);
            }
            else
            if ((int)inSpnHours.getValue() < 1) {
                inSpnHours.getModel().setValue(12);
            }
            ((SpinnerNumberModel)inSpnHours.getModel()).setMinimum(1);
            ((SpinnerNumberModel)inSpnHours.getModel()).setMaximum(12);
        }
        else {
            if (TimeFormat.H24.equals(inCmbTimeFormat.getSelectedItem()) && TimeFormat.PM.equals(inPrevTimeFormat)) {
                if ((int)inSpnHours.getValue() < 12) {
                    inSpnHours.getModel().setValue((int)inSpnHours.getValue() + 12);
                }
            }
            else
            if (TimeFormat.H24.equals(inCmbTimeFormat.getSelectedItem()) && TimeFormat.AM.equals(inPrevTimeFormat)) {
                if ((int)inSpnHours.getValue() == 12) {
                    inSpnHours.getModel().setValue(0);
                }
            }
            ((SpinnerNumberModel)inSpnHours.getModel()).setMinimum(0);
            ((SpinnerNumberModel)inSpnHours.getModel()).setMaximum(23);
        }
    }

    public static Date getDateFromUI(JSpinner inSpnHours, JSpinner inSpnMinutes, JSpinner inSpnSeconds, JComboBox<TimeFormat> inCmbTimeFormat, Date inDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(inDate);
        try {
            // Hours
            if (TimeFormat.PM.equals(inCmbTimeFormat.getSelectedItem())) {
                int tempHours = 12 + (Integer)inSpnHours.getValue();
                if (tempHours >= 24) {
                    calendar.set(Calendar.HOUR_OF_DAY, tempHours - 12);
                }
                else {
                    calendar.set(Calendar.HOUR_OF_DAY, tempHours);
                }
            }
            else
            if (TimeFormat.AM.equals(inCmbTimeFormat.getSelectedItem())) {
                int tempHours = (Integer)inSpnHours.getValue();
                if (tempHours == 12) {
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                }
                else {
                    calendar.set(Calendar.HOUR_OF_DAY, tempHours);
                }
            }
            else {
                calendar.set(Calendar.HOUR_OF_DAY, (Integer)inSpnHours.getValue());
            }
            // Minutes
            calendar.set(Calendar.MINUTE, (Integer)inSpnMinutes.getValue());
            // Seconds
            if (inSpnSeconds != null) {
                calendar.set(Calendar.SECOND, (Integer)inSpnSeconds.getValue());
            }
        }
        catch (NumberFormatException ex) {
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
            calendar.set(Calendar.HOUR_OF_DAY, -1);
            calendar.set(Calendar.MINUTE, -1);
            inCmbTimeFormat.setSelectedItem(TimeFormat.NONE);
        }
        return calendar.getTime();
    }
    
    public static void calculateSunAndMoon(Sighting sighting) {
        // Check if time is usable
        if (sighting.getDate() != null && sighting.getTimeAccuracy() != null && sighting.getTimeAccuracy().isUsableTime()) {
            // Moon phase
            sighting.setMoonPhase(AstroCalculator.getMoonPhase(sighting.getDate()));
            // Check if GPS is usable
            if (sighting.getLatitude() != null && !sighting.getLatitude().equals(Latitudes.NONE)
                    && sighting.getLongitude() != null && !sighting.getLongitude().equals(Longitudes.NONE)) {
                double latitude = UtilsGPS.getDecimalDegree(sighting.getLatitude(), sighting.getLatDegrees(), sighting.getLatMinutes(), sighting.getLatSeconds());
                double longitude = UtilsGPS.getDecimalDegree(sighting.getLongitude(), sighting.getLonDegrees(), sighting.getLonMinutes(), sighting.getLonSeconds());
                // Sun Light
                sighting.setTimeOfDay(AstroCalculator.getSunCategory(sighting.getDate(), latitude, longitude));
                // Moon Light
                sighting.setMoonlight(AstroCalculator.getMoonlight(sighting.getDate(), latitude, longitude));
            }
        }
    }
    
    public static Date getDateFromLocalDateTime(LocalDateTime inLocalDateTime) {
        if (inLocalDateTime != null) {
            return Date.from(inLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());
        }
        return null;
    }
    
    public static Date getDateFromLocalDate(LocalDate inLocalDate) {
        if (inLocalDate != null) {
            return Date.from(inLocalDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        }
        return null;
    }
    
    public static Date getDateFromLocalTime(LocalTime inLocalTime) {
        if (inLocalTime != null) {
            return Date.from(inLocalTime.atDate(LocalDate.now()).atZone(ZoneId.systemDefault()).toInstant());
        }
        return null;
    }
    
    public static LocalDateTime getLocalDateTimeFromDate(Date inDate) {
        if (inDate != null) {
            return LocalDateTime.ofInstant(inDate.toInstant(), ZoneId.systemDefault());
        }
        return null;
    }
    
    public static LocalDate getLocalDateFromDate(Date inDate) {
        if (inDate != null) {
            return getLocalDateTimeFromDate(inDate).toLocalDate();
        }
        return null;
    }
    
    public static LocalTime getLocalTimeFromDate(Date inDate) {
        if (inDate != null) {
            return getLocalDateTimeFromDate(inDate).toLocalTime();
        }
        return null;
    }

}
