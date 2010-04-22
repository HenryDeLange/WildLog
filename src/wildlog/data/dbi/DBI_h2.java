/*
 * DBI_H2.java is part of WildLog
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

package wildlog.data.dbi;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Henry
 */
public class DBI_h2 extends DBI_JDBC {
    // Constructor
    public DBI_h2() {
        super();
        Statement state = null;
        ResultSet results = null;
        try {
            Class.forName("org.h2.Driver").newInstance();
            conn = DriverManager.getConnection("jdbc:h2:/wildlog/data/wildlog");
            super.init();
            
            // Create tables
            results = conn.getMetaData().getTables(null, null, "ELEMENTS", null);
            state = conn.createStatement();
            if (!results.next()) {
                state.execute(createElementsTable);
            }
            results = conn.getMetaData().getTables(null, null, "LOCATIONS", null);
            state = conn.createStatement();
            if (!results.next()) {
                state.execute(createLocationsTable);
            }
            results = conn.getMetaData().getTables(null, null, "VISITS", null);
            state = conn.createStatement();
            if (!results.next()) {
                state.execute(createVisitsTable);
            }
            results = conn.getMetaData().getTables(null, null, "SIGHTINGS", null);
            state = conn.createStatement();
            if (!results.next()) {
                state.execute(createSightingsTable);
            }
            results = conn.getMetaData().getTables(null, null, "FILES", null);
            state = conn.createStatement();
            if (!results.next()) {
                state.execute(createFotosTable);
            }
        }
        catch (ClassNotFoundException cnfe) {
            System.err.println("\nUnable to load the JDBC driver " + "org.apache.derby.jdbc.EmbeddedDriver");
            System.err.println("Please check your CLASSPATH.");
            cnfe.printStackTrace(System.err);
        }
        catch (InstantiationException ie) {
            System.err.println("\nUnable to instantiate the JDBC driver " + "org.apache.derby.jdbc.EmbeddedDriver");
            ie.printStackTrace(System.err);
        }
        catch (IllegalAccessException iae) {
            System.err.println("\nNot allowed to access the JDBC driver " + "org.apache.derby.jdbc.EmbeddedDriver");
            iae.printStackTrace(System.err);
        }
        catch (SQLException sqle) {
            printSQLException(sqle);
        }
    }

}
