package wildlog.maps.geotools;

import java.awt.Color;
import java.awt.Polygon;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.geotools.coverage.GridSampleDimension;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.data.FeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.styling.ChannelSelection;
import org.geotools.styling.ContrastEnhancement;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Fill;
import org.geotools.styling.Graphic;
import org.geotools.styling.Mark;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.RasterSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.SLD;
import org.geotools.styling.SelectedChannelType;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.Symbolizer;
import org.geotools.xml.styling.SLDParser;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPolygon;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory2;
import org.opengis.style.ContrastMethod;


public class GeoToolsLayerUtils {
    
    private GeoToolsLayerUtils() {
    }
 
    public static Style createGeoTIFFStyleRGB(AbstractGridCoverage2DReader inReader) {
        GridCoverage2D gridCoverage = null;
        try {
            gridCoverage = inReader.read(null);
        } 
        catch (IOException ex) {
            ex.printStackTrace(System.err);
            return null;
        }
        int numBands = gridCoverage.getNumSampleDimensions();
        if (numBands < 3) {
            return null;
        }
        String[] sampleDimensionNames = new String[numBands];
        for (int i = 0; i < numBands; i++) {
            GridSampleDimension dimension = gridCoverage.getSampleDimension(i);
            sampleDimensionNames[i] = dimension.getDescription().toString();
        }
        final int RED = 0, GREEN = 1, BLUE = 2;
        int[] channelNum = { -1, -1, -1 };
        for (int i = 0; i < numBands; i++) {
            String name = sampleDimensionNames[i].toLowerCase();
            if (name != null) {
                if (name.matches("red.*")) {
                    channelNum[RED] = i + 1;
                } 
                else 
                if (name.matches("green.*")) {
                    channelNum[GREEN] = i + 1;
                } 
                else 
                if (name.matches("blue.*")) {
                    channelNum[BLUE] = i + 1;
                }
            }
        }
        if (channelNum[RED] < 0 || channelNum[GREEN] < 0 || channelNum[BLUE] < 0) {
            channelNum[RED] = 1;
            channelNum[GREEN] = 2;
            channelNum[BLUE] = 3;
        }
        SelectedChannelType[] selectedChannelTypes = new SelectedChannelType[gridCoverage.getNumSampleDimensions()];
        StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory();
        FilterFactory2 filterFactory = CommonFactoryFinder.getFilterFactory2();
        // NOTE: The NORMALIZE and HISTOGRAM options don't really work very well... I'm doing it in JavaFX instead.
        ContrastEnhancement contrastEnhancement = styleFactory.contrastEnhancement(filterFactory.literal(1.0), ContrastMethod.NONE);
        for (int i = 0; i < 3; i++) {
            selectedChannelTypes[i] = styleFactory.createSelectedChannelType(String.valueOf(channelNum[i]), contrastEnhancement);
        }
        RasterSymbolizer sym = styleFactory.getDefaultRasterSymbolizer();
        ChannelSelection sel = styleFactory.channelSelection(selectedChannelTypes[RED], selectedChannelTypes[GREEN], selectedChannelTypes[BLUE]);
        sym.setChannelSelection(sel);
        return SLD.wrapSymbolizers(sym);
    }
    
    public static Style createGeoTIFFStyleGreyscale(int inBand) {
        StyleFactory sf = CommonFactoryFinder.getStyleFactory();
        FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();
        ContrastEnhancement ce = sf.contrastEnhancement(ff.literal(1.0), ContrastMethod.NONE);
        SelectedChannelType sct = sf.createSelectedChannelType(String.valueOf(inBand), ce);
        RasterSymbolizer sym = sf.getDefaultRasterSymbolizer();
        ChannelSelection sel = sf.channelSelection(sct);
        sym.setChannelSelection(sel);
        return SLD.wrapSymbolizers(sym);
    }

    public static Style createShapefileStyleBasic(FeatureSource featureSource, Color inLineColor, Color inFillColor, Double inLineOpacity, Double inFillOpacity) {
        SimpleFeatureType schema = (SimpleFeatureType)featureSource.getSchema();
        Class geomType = schema.getGeometryDescriptor().getType().getBinding();
        if (Polygon.class.isAssignableFrom(geomType) || MultiPolygon.class.isAssignableFrom(geomType)) {
            return createPolygonStyle(inLineColor, inFillColor, inLineOpacity, inFillOpacity);
        } 
        else 
        if (LineString.class.isAssignableFrom(geomType) || MultiLineString.class.isAssignableFrom(geomType)) {
            return createLineStyle(inLineColor, inLineOpacity);
        } 
        else {
            return createPointStyle(inLineColor, inFillColor, inLineOpacity, inFillOpacity, 10);
        }
    }

    private static Style createPolygonStyle(Color inLineColor, Color inFillColor, Double inLineOpacity, Double inFillOpacity) {
        StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory();
        FilterFactory2 filterFactory = CommonFactoryFinder.getFilterFactory2();
        Stroke stroke = styleFactory.createStroke(filterFactory.literal(inLineColor),
                filterFactory.literal(1), filterFactory.literal(inLineOpacity));
        Fill fill = styleFactory.createFill(filterFactory.literal(inFillColor), filterFactory.literal(inFillOpacity));
        // Setting the geometryPropertyName arg to null signals that we want to draw the default geomettry of features
        Symbolizer sym = styleFactory.createPolygonSymbolizer(stroke, fill, null);
        Rule rule = styleFactory.createRule();
        rule.symbolizers().add(sym);
        FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(new Rule[]{rule});
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(fts);
        return style;
    }
    
    private static Style createLineStyle(Color inLineColor, Double inLineOpacity) {
        StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory();
        FilterFactory2 filterFactory = CommonFactoryFinder.getFilterFactory2();
        Stroke stroke = styleFactory.createStroke(filterFactory.literal(inLineColor), filterFactory.literal(inLineOpacity));
        //Setting the geometryPropertyName arg to null signals that we want to draw the default geomettry of features
        Symbolizer sym = styleFactory.createLineSymbolizer(stroke, null);
        Rule rule = styleFactory.createRule();
        rule.symbolizers().add(sym);
        FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(new Rule[]{rule});
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(fts);
        return style;
    }

    public static Style createPointStyle(Color inLineColor, Color inFillColor, Double inLineOpacity, Double inFillOpacity, int inSize) {
        StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory();
        FilterFactory2 filterFactory = CommonFactoryFinder.getFilterFactory2();
        Graphic gr = styleFactory.createDefaultGraphic();
        Mark mark = styleFactory.getCircleMark();
        mark.setStroke(styleFactory.createStroke(filterFactory.literal(inLineColor), filterFactory.literal(inLineOpacity)));
        mark.setFill(styleFactory.createFill(filterFactory.literal(inFillColor), filterFactory.literal(inFillOpacity)));
        gr.graphicalSymbols().clear();
        gr.graphicalSymbols().add(mark);
        gr.setSize(filterFactory.literal(inSize));
        // Setting the geometryPropertyName arg to null signals that we want to draw the default geomettry of features
        PointSymbolizer sym = styleFactory.createPointSymbolizer(gr, null);
        Rule rule = styleFactory.createRule();
        rule.symbolizers().add(sym);
        FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(new Rule[]{rule});
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(fts);
        return style;
    }
    
    public static Style createShapefileStyleFile(FeatureSource inFeatureSource, Path inStylePath) {
        // NOTE: Mens kan die SLD files maklik maak in die uDIG GIS program...
        if (Files.exists(inStylePath)) {
            return createFromSLD(inStylePath);
        }
        System.err.println("SLD style file not found... Using default style instead.  -  " + inStylePath);
        return createShapefileStyleBasic(inFeatureSource, new Color(18, 133, 28), new Color(23, 168, 35), 0.7, 0.6);
    }

    private static Style createFromSLD(Path inSLDPath) {
        try {
            StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory();
            SLDParser stylereader = new SLDParser(styleFactory, inSLDPath.toFile());
            Style[] style = stylereader.readXML();
            return style[0];
        }
        catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
        return null;
    }
    
}
