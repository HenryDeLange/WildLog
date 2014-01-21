package wildlog.ui.utils;

import java.util.Calendar;
import java.util.Date;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import wildlog.data.enums.TimeFormat;


public class UtilsTime {

    private UtilsTime() {
    }

    public static void modeChanged(JSpinner inSpnHours, JSpinner inSpnMinutes, JComboBox<TimeFormat> inCmbTimeFormat, TimeFormat inPrevTimeFormat) {
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

}
