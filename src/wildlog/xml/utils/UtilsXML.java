package wildlog.xml.utils;

import java.nio.file.Path;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.WildLogFile;
import wildlog.data.dataobjects.interfaces.DataObjectWithGPS;
import wildlog.data.dataobjects.interfaces.DataObjectWithXML;
import wildlog.mapping.utils.UtilsGps;
import wildlog.ui.helpers.ProgressbarTask;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.WildLogPaths;


public final class UtilsXML {
    
    private UtilsXML() {
    }

    public static Path exportXML(DataObjectWithXML inDataObject, WildLogApp inApp, ProgressbarTask inProgressbarTask, boolean inIncludeSightings) {
        if (inProgressbarTask != null) {
            inProgressbarTask.setMessage("Starting the XML Export for '" + inDataObject.getDisplayName() + "' ");
            inProgressbarTask.setTaskProgress(0);
        }
        Path toFile = WildLogPaths.WILDLOG_EXPORT_XML.getAbsoluteFullPath().resolve(inDataObject.getExportPrefix()).resolve(inDataObject.getDisplayName() + ".xml");
        if (inProgressbarTask != null) {
            inProgressbarTask.setTaskProgress(1);
            inProgressbarTask.setMessage("Busy with the XML Export for '" + inDataObject.getDisplayName() + "' ");
        }
        UtilsFileProcessing.createFileFromBytes(inDataObject.toXML(inApp, inProgressbarTask, inIncludeSightings).getBytes(), toFile);
        if (inProgressbarTask != null) {
            inProgressbarTask.setTaskProgress(100);
            inProgressbarTask.setMessage("Done with the XML Export for '" + inDataObject.getDisplayName() + "' ");
        }
        return toFile;
    }
    
    public static String getGPSInfoAsXML(DataObjectWithGPS inDataObjectWithGPS) {
        StringBuilder builder = new StringBuilder(100);
        builder.append("<GPS>");
        builder.append("<gpsAccuracy>").append(inDataObjectWithGPS.getGPSAccuracy()).append("</gpsAccuracy>");
        builder.append("<Latitude>");
        builder.append("<latValue>").append(inDataObjectWithGPS.getLatitude()).append("</latValue>");
        builder.append("<latDegrees>").append(inDataObjectWithGPS.getLatDegrees()).append("</latDegrees>");
        builder.append("<latMinutes>").append(inDataObjectWithGPS.getLatMinutes()).append("</latMinutes>");
        builder.append("<latSeconds>").append(inDataObjectWithGPS.getLatSeconds()).append("</latSeconds>");
        builder.append("<latitudeAsText>").append(UtilsGps.getLatitudeString(inDataObjectWithGPS)).append("</latitudeAsText>");
        builder.append("<latDesimalDegrees>").append(UtilsGps.getDecimalDegree(inDataObjectWithGPS.getLatitude(), 
                inDataObjectWithGPS.getLatDegrees(), inDataObjectWithGPS.getLatMinutes(), inDataObjectWithGPS.getLatSeconds()))
                .append("</latDesimalDegrees>");
        builder.append("</Latitude>");
        builder.append("<Longitude>");
        builder.append("<lonValue>").append(inDataObjectWithGPS.getLongitude()).append("</lonValue>");
        builder.append("<lonDegrees>").append(inDataObjectWithGPS.getLonDegrees()).append("</lonDegrees>");
        builder.append("<lonMinutes>").append(inDataObjectWithGPS.getLonMinutes()).append("</lonMinutes>");
        builder.append("<lonSeconds>").append(inDataObjectWithGPS.getLonSeconds()).append("</lonSeconds>");
        builder.append("<longitudeAsText>").append(UtilsGps.getLongitudeString(inDataObjectWithGPS)).append("</longitudeAsText>");
        builder.append("<lonDesimalDegrees>").append(UtilsGps.getDecimalDegree(inDataObjectWithGPS.getLongitude(), 
                inDataObjectWithGPS.getLonDegrees(), inDataObjectWithGPS.getLonMinutes(), inDataObjectWithGPS.getLonSeconds()))
                .append("</lonDesimalDegrees>");
        builder.append("</Longitude>");
        builder.append("</GPS>");
        return builder.toString();
    }
    
    public static String getWildLogFileInfoAsXML(WildLogFile inWildLogFile) {
        StringBuilder builder = new StringBuilder(100);
        builder.append("<File>");
        builder.append("<filename>").append(inWildLogFile.getFilename()).append("</filename>");
        builder.append("<filetype>").append(inWildLogFile.getFileType()).append("</filetype>");
        builder.append("<filepath>").append(inWildLogFile.getRelativePath()).append("</filepath>");
        builder.append("</File>");
        return builder.toString();
    }
    
}
