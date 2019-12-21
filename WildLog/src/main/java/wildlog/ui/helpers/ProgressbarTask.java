package wildlog.ui.helpers;

import org.apache.logging.log4j.Level;
import org.jdesktop.application.Application;
import org.jdesktop.application.Task;
import wildlog.WildLogApp;


public abstract class ProgressbarTask<T extends Object, V extends Object> extends Task<T, V> {

    public ProgressbarTask(Application inApplication) {
        super(inApplication);
    }

    @Override
    public void setMessage(String inMessage) {
        super.setMessage(inMessage);
    }

    public void setTaskProgress(int inProgress) {
        try {
            super.setProgress(inProgress);
        }
        catch (Exception ex) {
            WildLogApp.LOGGER.log(Level.ERROR, "Incorrect progress value: " + inProgress);
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
        }
    }

    public void setTaskProgress(int inProgress, int inMin, int inMax) {
        try {
            super.setProgress(inProgress, inMin, inMax);
        }
        catch (Exception ex) {
            WildLogApp.LOGGER.log(Level.ERROR, "Incorrect progress value: " + inProgress + ", " + inMin + ", " + inMax);
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
        }
    }

    public void setTaskProgress(float inProgress) {
        try {
            super.setProgress(inProgress);
        }
        catch (Exception ex) {
            WildLogApp.LOGGER.log(Level.ERROR, "Incorrect progress value: " + inProgress);
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
        }
    }

    public void setTaskProgress(float inProgress, float inMin, float inMax) {
        try {
            super.setProgress(inProgress, inMin, inMax);
        }
        catch (Exception ex) {
            WildLogApp.LOGGER.log(Level.ERROR, "Incorrect progress value: " + inProgress + ", " + inMin + ", " + inMax);
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
        }
    }

}
