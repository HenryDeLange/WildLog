/*
 * WildLogBetaApp.java
 */

package wildlog;

import java.io.File;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import wildlog.data.dbi.DBI;
import wildlog.data.dbi.DBI_db4o;

/**
 * The main class of the application.
 */
public class WildLogApp extends SingleFrameApplication {

    @Override
    protected void initialize(String[] arg0) {
        super.initialize(arg0);
        System.out.println("<<<<<<< INITIALIZING...");
        File dataFolder = new File(File.separatorChar + "WildLog" + File.separatorChar + "Data" + File.separatorChar);
        dataFolder.mkdirs();
        File imagesFolder = new File(File.separatorChar + "WildLog" + File.separatorChar + "Images" + File.separatorChar);
        imagesFolder.mkdir();
        // Might need to add Util folder here with JAR files in... Or installer should do that...
        dbi = new DBI_db4o();
    }

    /**
     * At startup create and show the main frame of the application.
     */
    @Override
    protected void startup() {
        show(new WildLogView(this));
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override 
    protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of WildLogBetaApp
     */
    public static WildLogApp getApplication() {
        return Application.getInstance(WildLogApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(WildLogApp.class, args);
    }

    @Override
    protected void shutdown() {
        super.shutdown();
        System.out.println("...SHUTTING DOWN >>>>>>>>>");
        dbi.close();
    }
    
    
    // Make sure the application uses the same DBI instance...
    // Might want to think of other ways to do this, but seems ok for now
    // The DBI is initialized in startup() and closed in shutdown()
    private DBI dbi;
    
    public DBI getDBI() {
        return dbi;
    }
    
}
