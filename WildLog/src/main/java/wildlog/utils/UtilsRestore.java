package wildlog.utils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileFilter;
import org.apache.logging.log4j.Level;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;
import wildlog.WildLogApp;
import static wildlog.WildLogView.STATIC_TAB_COUNT;
import wildlog.data.dbi.WildLogDBI;
import wildlog.ui.helpers.ProgressbarTask;
import wildlog.ui.helpers.WLFileChooser;
import wildlog.ui.helpers.WLOptionPane;

public class UtilsRestore {
    
    public static void doDatabaseRestore() {
        WildLogApp app = WildLogApp.getApplication();
        int option = WLOptionPane.showOptionDialog(app.getMainFrame(), 
                "<html>Select the type of backup you want to restore form."
                + "<br/><b>Warning: </b>Restoring a database backup will overwrite recent changes.</html>", 
                "Restore Database", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, 
                null, new String[] {
                    "H2 Database File (Recommended)", 
                    "SQL Script File"
                }, null);
        if (option != JOptionPane.CLOSED_OPTION) {
            WLFileChooser fileChooser = new WLFileChooser(WildLogPaths.WILDLOG_BACKUPS.getAbsoluteFullPath().toFile());
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            if (option == 0) {
                // Use H2
                fileChooser.setDialogTitle("Select the H2 Database file to restore");
                fileChooser.setFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File inFile) {
                        if (inFile.isDirectory()) {
                            return true;
                        }
                        return inFile.getName().equalsIgnoreCase(WildLogDBI.BACKUP_H2);
                    }

                    @Override
                    public String getDescription() {
                        return "WildLog Backup H2 Database File";
                    }
                });
            }
            else {
                // Use SQL
                fileChooser.setDialogTitle("Select the SQL Script File to restore");
                fileChooser.setFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File inFile) {
                        if (inFile.isDirectory()) {
                            return true;
                        }
                        return inFile.getName().equalsIgnoreCase(WildLogDBI.BACKUP_SQL);
                    }

                    @Override
                    public String getDescription() {
                        return "WildLog Backup SQL Script File";
                    }
                });
            }
            int result = fileChooser.showOpenDialog(app.getMainFrame());
            if (result == JFileChooser.APPROVE_OPTION) {
                // Close all tabs and go to the home tab
                if (app.getMainFrame() != null) {
                    app.getMainFrame().getTabbedPane().setSelectedIndex(0);
                    while (app.getMainFrame().getTabbedPane().getTabCount() > STATIC_TAB_COUNT) {
                        app.getMainFrame().getTabbedPane().remove(STATIC_TAB_COUNT);
                    }
                }
                // If this is from the menu then use the progress bar
                if(app.getMainFrame() != null) {
                    // Lock the input/display and show busy message
                    // Note: we never remove the Busy dialog and greyed out background since the app will be restarted anyway when done (Don't use JDialog since it stops the code until the dialog is closed...)
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            JPanel panel = new JPanel(new AbsoluteLayout());
                            panel.setPreferredSize(new Dimension(400, 50));
                            panel.setBorder(new LineBorder(new Color(245, 80, 40), 3));
                            JLabel label = new JLabel("<html>Busy restoring the database....</html>");
                            label.setFont(new Font("Tahoma", Font.BOLD, 12));
                            label.setBorder(new LineBorder(new Color(195, 65, 20), 4));
                            panel.setBackground(new Color(0.22f, 0.26f, 0.20f, 0.95f));
                            panel.add(label, new AbsoluteConstraints(410, 20, -1, -1));
                            panel.setBackground(new Color(0.22f, 0.26f, 0.20f, 0.25f));
                            JPanel glassPane = (JPanel) app.getMainFrame().getGlassPane();
                            glassPane.removeAll();
                            glassPane.setLayout(new BorderLayout(100, 100));
                            glassPane.add(panel, BorderLayout.CENTER);
                            glassPane.addMouseListener(new MouseAdapter() {});
                            glassPane.addKeyListener(new KeyAdapter() {});
                            app.getMainFrame().setGlassPane(glassPane);
                            app.getMainFrame().getGlassPane().setVisible(true);
                            app.getMainFrame().getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                        }
                    });
                    // Start the process in another thread to allow the UI to update correctly and use the progressbar for feedback
                    UtilsConcurency.kickoffProgressbarTask(app, new ProgressbarTask(app) {
                        private boolean restoreFailed = false;

                        @Override
                        protected Object doInBackground() throws Exception {
                            try {
                                app.getDBI().doRestore(fileChooser.getSelectedFile().toPath());
                            }
                            catch (Exception ex) {
                                WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                                restoreFailed = true;
                            }
                            return null;
                        }

                        @Override
                        protected void finished() {
                            super.finished();
                            // Using invokeLater because I hope the progressbar will have finished by then, otherwise the popup is shown
                            // that asks whether you want to close the application or not, and it's best to rather restart afterwards.
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    // Close the application to be safe (make sure no wierd references/paths are still used, etc.)
                                    if (!restoreFailed) {
                                        WLOptionPane.showMessageDialog(app.getMainFrame(),
                                                "The active database was restored to a previous state. Please restart the application.",
                                                "Backup Restored", JOptionPane.INFORMATION_MESSAGE);
                                    }
                                    else {
                                        WLOptionPane.showMessageDialog(app.getMainFrame(),
                                                "The active database could not be restored to a previous state.",
                                                "Backup Restored Failed!", JOptionPane.ERROR_MESSAGE);
                                    }
                                    app.quit(null);
                                }
                            });
                        }
                    });
                }
                // If this was from the WildLogApp launch then don't use the progress bar
                else {
                    try {
                        app.getDBI().doRestore(fileChooser.getSelectedFile().toPath());
                        WLOptionPane.showMessageDialog(app.getMainFrame(),
                                "The active database was restored to a previous state. Please restart the application.",
                                "Backup Restored", JOptionPane.INFORMATION_MESSAGE);
                    }
                    catch (Exception ex) {
                        WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                        WLOptionPane.showMessageDialog(app.getMainFrame(),
                                "The active database could not be restored to a previous state.",
                                "Backup Restored Failed!", JOptionPane.ERROR_MESSAGE);
                    }
                    app.quit(null);
                }
            }
        }
    }
    
}
