package wildlog.ui.utils;

import java.util.Calendar;
import java.util.Date;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import wildlog.astro.AstroCalculator;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.enums.Latitudes;
import wildlog.data.enums.Longitudes;
import wildlog.data.enums.TimeFormat;
import wildlog.mapping.utils.UtilsGps;


public class UtilsTime {

    private UtilsTime() {
    }

    public static void modeChanged(JSpinner inSpnHours, JSpinner inSpnMinutes, JComboBox<TimeFormat> inCmbTimeFormat, TimeFormat inPrevTimeFormat) {
        // NOTE: The 12 o'clock times are inclusive going forwards, because at by at 12:00 we are already a few (mili)seconds into PM.
        //       For example time is calculated like this:
        //         day time morning -> 11:59am -> 12:00pm -> 12:01pm -> day time afternoon
        //         night time evening -> 11:59pm -> 12:00am -> 12:01am -> night time morning
        if (!TimeFormat.AM.equals(inCmbTimeFormat.getSelectedItem())
                && !TimeFormat.PM.equals(inCmbTimeFormat.getSelectedItem())
                && !TimeFormat.H24.equals(inCmbTimeFormat.getSelectedItem())) {
            inSpnHours.getModel().setValue(0);
            inSpnMinutes.getModel().setValue(0);
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

    public static Date getDateFromUI(JSpinner inSpnHours, JSpinner inSpnMinutes, JComboBox<TimeFormat> inCmbTimeFormat, Date inDate) {
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
        }
        catch (NumberFormatException ex) {
            ex.printStackTrace(System.err);
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
                double latitude = UtilsGps.getDecimalDegree(sighting.getLatitude(), sighting.getLatDegrees(), sighting.getLatMinutes(), sighting.getLatSeconds());
                double longitude = UtilsGps.getDecimalDegree(sighting.getLongitude(), sighting.getLonDegrees(), sighting.getLonMinutes(), sighting.getLonSeconds());
                // Sun Light
                sighting.setTimeOfDay(AstroCalculator.getSunCategory(sighting.getDate(), latitude, longitude));
                // Moon Light
                sighting.setMoonlight(AstroCalculator.getMoonlight(sighting.getDate(), latitude, longitude));
            }
        }
    }

}
