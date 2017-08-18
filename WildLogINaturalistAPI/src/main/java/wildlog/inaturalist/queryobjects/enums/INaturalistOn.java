package wildlog.inaturalist.queryobjects.enums;

/**
 *  Used to for [GET/observations] to filter on a date.
 */
public class INaturalistOn {
    private int year;
    private int month;
    private int day;

    public INaturalistOn() {
    }

    /**
     * The month must be a valid month from 1 to 12. 
     * Can be 0 to indicate any month in the year. <br/>
     * The day must be a valid day for the month from 1 to 31. 
     * Can be 0 to indicate any day in the month.
     */
    public INaturalistOn(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    /**
     * Must be a valid month from 1 to 12. 
     * <b>Can be 0 to indicate any month in the year.</b>
     */
    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    /**
     * Must be a valid day for the month from 1 to 31. 
     * <b>Can be 0 to indicate any day in the month.</b>
     */
    public void setDay(int day) {
        this.day = day;
    }
    
    public String getAsINatString() {
        String result = Integer.toString(year);
        if (month > 0 && month <= 12) {
            result = result + "-" + month;
        }
        if (day > 0 && day <= 31) {
            result = result + "-" + day;
        }
        return result;
    }

    @Override
    public String toString() {
        return getAsINatString();
    }
    
}
