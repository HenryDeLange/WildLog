package wildlog.maps.gpx;

import com.topografix.gpx._1._1.GpxType;
import com.topografix.gpx._1._1.WptType;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


public final class GpxReader {
    private static JAXBContext context;
    private static Unmarshaller unmarshaller;
    private static XPathFactory xPathFactory;
    private static XPath xPath;

    private GpxReader() {
    }
    
    public static List<WptType> getWaypoints(Path inGpxFile) {
        GpxType gpx = null;
        try {
            if (context == null) {
                 context = JAXBContext.newInstance("com.topografix.gpx._1._1");
            }
            if (unmarshaller == null) {
                unmarshaller = context.createUnmarshaller();
            }
            @SuppressWarnings("unchecked")
            JAXBElement<GpxType> root = (JAXBElement<GpxType>) unmarshaller.unmarshal(inGpxFile.toFile());
            gpx = root.getValue();
        }
        catch(JAXBException ex) {
           ex.printStackTrace(System.err);
        }
        if (gpx != null) {
            return gpx.getWpt();
        }
        return new ArrayList<WptType>(0);
    }
    
    public static WptType getSpecificWaypoint(Path inGpxFile, String inTagName) {
        try {
            InputSource xml = new InputSource(Files.newBufferedReader(inGpxFile));
            if (xPathFactory == null) {
                xPathFactory = XPathFactory.newInstance();
            }
            if (xPath == null) {
                xPath = xPathFactory.newXPath();
                xPath.setNamespaceContext(new NamespaceContext() {
                    @Override
                    public String getNamespaceURI(String prefix) {
                        if ("gpxnsp".equals(prefix)) { 
                            return "http://www.topografix.com/GPX/1/1";
                        }
                        return null;
                    }

                    @Override
                    public String getPrefix(String namespaceURI) {
                        if ("http://www.topografix.com/GPX/1/1".equals(namespaceURI)) {
                            return "gpxnsp";
                        }
                        return null;
                    }

                    @Override
                    public Iterator getPrefixes(String namespaceURI) {
                        return null;
                    }
                });
            }
            NodeList result = (NodeList) xPath.evaluate("(/gpxnsp:gpx/gpxnsp:wpt[gpxnsp:name='" + inTagName + "'])[1]/@*[name()='lat' or name()='lon']", xml, XPathConstants.NODESET);
            if (result.getLength() == 2) {
                WptType wptType = new WptType();
                wptType.setLat(new BigDecimal(result.item(0).getTextContent()));
                wptType.setLon(new BigDecimal(result.item(1).getTextContent()));
                wptType.setName(inTagName);
                return wptType;
            }
        }
        catch (XPathExpressionException | IOException ex) {
            ex.printStackTrace(System.err);
        }
        return null;
    }

}
