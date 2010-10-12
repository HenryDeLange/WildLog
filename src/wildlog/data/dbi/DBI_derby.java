package wildlog.data.dbi;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 *
 * @author Henry
 */
// Note: This is the initial implementation, I did not try to
// write it with speed in mind, and optimisation should be
// done later (better use of preparedstatements, etc.)
// and indexes
public class DBI_derby extends DBI_JDBC {
    // Constructor
    public DBI_derby() {
        super();
        System.setProperty("derby.system.home", "/WildLog/Data/");

        Statement state = null;
        ResultSet results = null;
        
        /*
         *  The JDBC driver is loaded by loading its class.
         *  If you are using JDBC 4.0 (Java SE 6) or newer, JDBC drivers may
         *  be automatically loaded, making this code optional.
         *
         *  In an embedded environment, this will also start up the Derby
         *  engine (though not any databases), since it is not already
         *  running. In a client environment, the Derby engine is being run
         *  by the network server framework.
         *
         *  In an embedded environment, any static Derby system properties
         *  must be set before loading the driver to take effect.
         */
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();

            Properties props = new Properties(); // connection properties
            // providing a user name and password is optional in the embedded
            // and derbyclient frameworks
            props.put("user", "WILDLOG");
            props.put("password", "WILDLOG");

            /* By default, the schema APP will be used when no username is
             * provided.
             * Otherwise, the schema name is the same as the user name (in this
             * case "user1" or USER1.)
             *
             * Note that user authentication is off by default, meaning that any
             * user can connect to your database using any password. To enable
             * authentication, see the Derby Developer's Guide.
             */

            //String dbName = "wildlog"; // the name of the database

            /*
             * This connection specifies create=true in the connection URL to
             * cause the database to be created when connecting for the first
             * time. To remove the database, remove the directory derbyDB (the
             * same as the database name) and its contents.
             *
             * The directory derbyDB will be created under the directory that
             * the system property derby.system.home points to, or the current
             * directory (user.dir) if derby.system.home is not set.
             */
            // Note: The default databse location is set by: System.setProperty("derby.system.home", "/WildLog/Data/");
            // and needs to be done before JDBC is opened (start of constructor)
            conn = DriverManager.getConnection("jdbc:derby:wildlog;create=true", props);
            super.init();

            // Create tables
            results = conn.getMetaData().getTables(null, null, "ELEMENTS", null);
            state = conn.createStatement();
            if (!results.next()) {
                state.execute(createElementsTable.replaceAll("longvarchar", "long varchar"));
            }
            results = conn.getMetaData().getTables(null, null, "LOCATIONS", null);
            state = conn.createStatement();
            if (!results.next()) {
                state.execute(createLocationsTable.replaceAll("longvarchar", "long varchar"));
            }
            results = conn.getMetaData().getTables(null, null, "VISITS", null);
            state = conn.createStatement();
            if (!results.next()) {
                state.execute(createVisitsTable.replaceAll("longvarchar", "long varchar"));
            }
            results = conn.getMetaData().getTables(null, null, "SIGHTINGS", null);
            state = conn.createStatement();
            if (!results.next()) {
                state.execute(createSightingsTable.replaceAll("longvarchar", "long varchar"));
            }
            results = conn.getMetaData().getTables(null, null, "FILES", null);
            state = conn.createStatement();
            if (!results.next()) {
                state.execute(createFilesTable.replaceAll("longvarchar", "long varchar"));
            }
            results = conn.getMetaData().getTables(null, null, "WILDLOG", null);
            state = conn.createStatement();
            if (!results.next()) {
                state.execute(createWildLogTable.replaceAll("longvarchar", "long varchar"));
            }

            super.doUpdates();
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

    @Override
    public void doBackup() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void doExportCSV(String inPath) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void doImportCSV(String inPath, String inPrefix) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    
}
