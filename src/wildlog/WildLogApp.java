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
import javax.swing.ImageIcon;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import wildlog.data.dbi.DBI;
import wildlog.data.dbi.DBI_h2;
import wildlog.data.dbi.DBI_derby;
import wildlog.data.enums.Latitudes;
import wildlog.data.enums.Longitudes;
import wildlog.mapping.MapFrame;

/**
 * The main class of the application.
 */
public class WildLogApp extends SingleFrameApplication {
    // Variables - This is actualy a very bad hack, but its the easiest and quickest to do for now...
    private Latitudes prevLat;
    private String prevLatDeg;
    private String prevLatMin;
    private String prevLatSec;
    private Longitudes prevLon;
    private String prevLonDeg;
    private String prevLonMin;
    private String prevLonSec;

    // Getters and Setters
    public Latitudes getPrevLat() {
        return prevLat;
    }

    public void setPrevLat(Latitudes prevLat) {
        this.prevLat = prevLat;
    }

    public String getPrevLatDeg() {
        return prevLatDeg;
    }

    public void setPrevLatDeg(String prevLatDeg) {
        this.prevLatDeg = prevLatDeg;
    }

    public String getPrevLatMin() {
        return prevLatMin;
    }

    public void setPrevLatMin(String prevLatMin) {
        this.prevLatMin = prevLatMin;
    }

    public String getPrevLatSec() {
        return prevLatSec;
    }

    public void setPrevLatSec(String prevLatSec) {
        this.prevLatSec = prevLatSec;
    }

    public Longitudes getPrevLon() {
        return prevLon;
    }

    public void setPrevLon(Longitudes prevLon) {
        this.prevLon = prevLon;
    }

    public String getPrevLonDeg() {
        return prevLonDeg;
    }

    public void setPrevLonDeg(String prevLonDeg) {
        this.prevLonDeg = prevLonDeg;
    }

    public String getPrevLonMin() {
        return prevLonMin;
    }

    public void setPrevLonMin(String prevLonMin) {
        this.prevLonMin = prevLonMin;
    }

    public String getPrevLonSec() {
        return prevLonSec;
    }

    public void setPrevLonSec(String prevLonSec) {
        this.prevLonSec = prevLonSec;
    }


    @Override
    protected void initialize(String[] arg0) {
        System.out.println("STARTING UP WildLog...");
        super.initialize(arg0);
        File dataFolder = new File(File.separatorChar + "WildLog" + File.separatorChar + "Data" + File.separatorChar);
        dataFolder.mkdirs();
        File imagesFolder = new File(File.separatorChar + "WildLog" + File.separatorChar + "Images" + File.separatorChar);
        imagesFolder.mkdirs();

        //dbi = new DBI_derby();
        dbi = new DBI_h2();
    }

    /**
     * At startup create and show the main frame of the application.
     */
    @Override
    protected void startup() {
        ImageIcon icon = new ImageIcon(getClass().getResource("resources/icons/WildLog Icon.gif"));
        WildLogView view = new WildLogView(this);
        view.getFrame().setIconImage(icon.getImage());
        show(view);
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
        if (mapFrame == null) {
            mapFrame = new MapFrame("WildLog Map");
        }
        return mapFrame;
    }
    
}
