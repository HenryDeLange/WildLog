package wildlog.ui.helpers;

import org.jdesktop.application.Application;
import org.jdesktop.application.Task;


public abstract class ProgressbarTask<T extends Object, V extends Object> extends Task<T, V> {

    public ProgressbarTask(Application application) {
        super(application);
    }

    @Override
    public void setMessage(String inMessage) {
        super.setMessage(inMessage);
    }

    public void setTaskProgress(int inProgress) {
        super.setProgress(inProgress);
    }

    public void setTaskProgress(int inProgress, int inMin, int inMax) {
        super.setProgress(inProgress, inMin, inMax);
    }

    public void setTaskProgress(float inProgress) {
        super.setProgress(inProgress);
    }

    public void setTaskProgress(float inProgress, float inMin, float inMax) {
        super.setProgress(inProgress, inMin, inMax);
    }

}
