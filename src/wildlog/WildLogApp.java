/*
 * WildLogApp.java is part of WildLog
 *
 * Copyright (C) 2009 Henry James de Lange
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package wildlog;

import java.io.File;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import wildlog.data.dbi.DBI;
import wildlog.data.dbi.DBI_db4o;
import wildlog.mapping.MapFrame;

/**
 * The main class of the application.
 */
public class WildLogApp extends SingleFrameApplication {

    @Override
    protected void initialize(String[] arg0) {
        System.out.println("STARTING UP WildLog...");
        super.initialize(arg0);
        File dataFolder = new File(File.separatorChar + "WildLog" + File.separatorChar + "Data" + File.separatorChar);
        dataFolder.mkdirs();
        File imagesFolder = new File(File.separatorChar + "WildLog" + File.separatorChar + "Images" + File.separatorChar);
        imagesFolder.mkdirs();
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
        dbi.close();
        System.out.println("SHUTTING DOWN WildLog");
    }
    
    
    // Make sure the application uses the same DBI instance...
    // Might want to think of other ways to do this, but seems ok for now
    // The DBI is initialized in startup() and closed in shutdown()
    private DBI dbi;
    
    public DBI getDBI() {
        return dbi;
    }

    // Only open one MapFrame for the application (to reduce memory use)
    private MapFrame mapFrame;

    public MapFrame getMapFrame() {
        // Setup MapFrame - Note: If this is in the constructor the frame keeps poping up when the application starts
        if (mapFrame == null) mapFrame = new MapFrame("WildLog Map");
        return mapFrame;
    }
    
}
