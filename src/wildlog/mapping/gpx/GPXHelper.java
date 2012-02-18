package wildlog.mapping.gpx;

import com.topografix.gpx._1._1.GpxType;
import com.topografix.gpx._1._1.WptType;
import java.io.File;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;



public class GPXHelper {
    
    public static void testGPX() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select GPX file");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File inFile) {
                if (inFile != null)
                    return (inFile.isDirectory() || inFile.getName().toLowerCase().endsWith("gpx"));
                return false;
            }
            
            @Override
            public String getDescription() {
                return "GPX";
            }
        });
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            GpxType gpx = null; 
            try {
                JAXBContext jc = JAXBContext.newInstance("com.topografix.gpx._1._1"); 
                Unmarshaller unmarshaller = jc.createUnmarshaller(); 
                JAXBElement<GpxType> root = (JAXBElement<GpxType>)unmarshaller.unmarshal(fileChooser.getSelectedFile()); 
                gpx = root.getValue(); 
            } catch(JAXBException ex) { 
               ex.printStackTrace();
            } 
            if (gpx != null) {
                List<WptType> waypoints = gpx.getWpt(); 
                for(WptType waypoint : waypoints) { 
                    System.out.println(waypoint.getName() + " -> " + waypoint.getLat() + " | " + waypoint.getLon());
                }
            }
        }
    }
    
}
