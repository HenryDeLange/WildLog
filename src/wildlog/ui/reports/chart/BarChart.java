package wildlog.ui.reports.chart;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;

// FIXME: Die code werk nou so half (dink ek), maar dit kan doen met baie refactoring en performance/rebustness improvements as ek eendag tyd/moed kry.
public class BarChart extends JPanel {
        // Variables
        private static final int MIN_BAR_HEIGHT = 20;
        private static final int MIN_BAR_WIDTH = 3;
        private static final int LABEL_BUFFER = 102;
        private static final int TOTAL_BUFFER = 20;
        private static final int SCALE_BUFFER = 10;
        private static final int BAR_HEIGHT_BUFFER = 5;
        private static final int BAR_WIDTH_BORDER_BUFFER = 2;
	private List<BarChartEntity> bars = new ArrayList<BarChartEntity>();
        private int chartWidth;
        private int chartHeight;

        // Constructor
        public BarChart(int inWidth, int inHeight) {
            super();
            chartWidth = inWidth;
            chartHeight = inHeight;
        }

        // Methods
	public void addBar(BarChartEntity inBarChartEntity) {
		bars.add(inBarChartEntity);
		repaint();
	}

	@Override
	public void paintComponent(Graphics g) {
            if (bars.size() > 0) {
                if (bars.get(0).getBarName() instanceof Date)
                    Collections.sort(bars, Collections.reverseOrder());
                else
                    Collections.sort(bars);
            }
            if (bars.size() > 0) {
                // Determine longest bar and most entries per row
                Map<String, Integer> chartMaxValues = new LinkedHashMap<String, Integer>();
                Map<String, Integer> chartBarsPerRow = new LinkedHashMap<String, Integer>();
                int maxValue = Integer.MIN_VALUE;
                int maxEntriesPerRow = Integer.MIN_VALUE;
                for (BarChartEntity entity : bars) {
                    String entityName = entity.getBarName().toString();
                    if (entity.getBarName() instanceof Date)
                        entityName = new SimpleDateFormat("dd MMM yyyy").format(entity.getBarName());
                    // Max Value
                    Integer temp = chartMaxValues.get(entityName);
                    if (temp == null) temp = 0;
                    chartMaxValues.put(entityName, temp + entity.getValue());
                    maxValue = Math.max(maxValue, chartMaxValues.get(entityName));
                    // Entries per row
                    temp = chartBarsPerRow.get(entityName);
                    if (temp == null) temp = 0;
                    chartBarsPerRow.put(entityName, temp.intValue() + 1);
                    maxEntriesPerRow = Math.max(maxEntriesPerRow, chartBarsPerRow.get(entityName));
                }

                // Determine bar sort order
                List<String> unsortedList = new ArrayList<String>(chartMaxValues.keySet());
                List<String> sortOrder = new ArrayList<String>(unsortedList.size());
                for (int t = 0; t < unsortedList.size(); t++) {
                    if (sortOrder.isEmpty()) {
                        sortOrder.add(unsortedList.get(t));
                    }
                    else {
                        boolean added = false;
                        for (int i = 0; i < sortOrder.size(); i++) {
                            if (chartMaxValues.get(unsortedList.get(t)) > chartMaxValues.get(sortOrder.get(i))) {
                                sortOrder.add(i, unsortedList.get(t));
                                added = true;
                                break;
                            }
                        }
                        if (added == false) {
                            sortOrder.add(unsortedList.get(t));
                        }
                    }
                }
                Map<String, Integer> chartSortOrder = new LinkedHashMap<String, Integer>(sortOrder.size());
                for (int t = 0; t < sortOrder.size(); t++) {
                    chartSortOrder.put(sortOrder.get(t), t);
                }

                // Paint bars and labels
                int barWidth = 0;
                int barHeight = ((chartHeight - SCALE_BUFFER) / chartMaxValues.size()) - BAR_HEIGHT_BUFFER;
                if (barHeight < MIN_BAR_HEIGHT)
                    barHeight = MIN_BAR_HEIGHT;
                Map<String, BarChartCoordinate> chartCoords = new LinkedHashMap<String, BarChartCoordinate>();
                for (BarChartEntity entity : bars) {
                    String entityName = entity.getBarName().toString();
                    if (entity.getBarName() instanceof Date)
                        entityName = new SimpleDateFormat("dd MMM yyyy").format(entity.getBarName());
                    // Setup
                    int value = entity.getValue();
                    // Note: floor, ceil, round produce differnet results with many bars
                    barWidth = (int)(Math.floor((chartWidth - (LABEL_BUFFER + TOTAL_BUFFER)) * ((double)value / maxValue)) - BAR_WIDTH_BORDER_BUFFER);
                    if (barWidth < MIN_BAR_WIDTH)
                        barWidth = MIN_BAR_WIDTH;
                    BarChartCoordinate coord = chartCoords.get(entityName);
                    if (coord == null) {
                        coord = new BarChartCoordinate(LABEL_BUFFER, (barHeight + BAR_HEIGHT_BUFFER) * chartSortOrder.get(entityName) + BAR_HEIGHT_BUFFER);
                        // Labels
                        g.setColor(Color.BLACK);
                        String label = entityName;
                        if (label.length() > 17)
                            label = label.substring(0, 15) + "..";
                        g.drawString(label, BAR_HEIGHT_BUFFER, coord.getY() + barHeight/2 + BAR_HEIGHT_BUFFER);
                    }
                    // Draw Bars
                    g.setColor(entity.getColor());
                    g.fillRect(coord.getX(), coord.getY(), barWidth, barHeight);
                    g.setColor(Color.BLACK);
                    g.drawRect(coord.getX(), coord.getY(), barWidth, barHeight);
                    // Update Coordinates
                    coord.setX(coord.getX() + barWidth);
                    chartCoords.put(entityName, coord);
                }

                // Get the new height
                if ((barHeight + BAR_HEIGHT_BUFFER) * chartCoords.size() + BAR_HEIGHT_BUFFER > chartHeight) {
                    chartHeight = (barHeight + BAR_HEIGHT_BUFFER) * chartCoords.size() + BAR_HEIGHT_BUFFER;
                }

                // The chart is to wide (horizontal scrolling isn't great), so try to redraw chart to fit in

                if (LABEL_BUFFER + TOTAL_BUFFER + barWidth * maxEntriesPerRow > chartWidth) {
                    g.clearRect(LABEL_BUFFER, 0, chartWidth, chartHeight);
                    int maxRowValue = chartMaxValues.get(sortOrder.get(0));
                    // Draw the resized rows (if applicable)
                    if (maxRowValue > 0) {
                        for (BarChartEntity entity : bars) {
                            String entityName = entity.getBarName().toString();
                            if (entity.getBarName() instanceof Date)
                                entityName = new SimpleDateFormat("dd MMM yyyy").format(entity.getBarName());
                            BarChartCoordinate coord = chartCoords.get(entityName);
                            if (coord != null) {
                                BarChartCoordinate newCoord = new BarChartCoordinate(LABEL_BUFFER, coord.getY());
                                barWidth = (int)Math.floor(((double)((double)chartMaxValues.get(entityName) / (double)maxRowValue)) * (chartWidth - LABEL_BUFFER - TOTAL_BUFFER));
                                // Draw the row
                                g.setColor(new Color(125, 198, 48));
                                g.fillRect(newCoord.getX(), newCoord.getY(), barWidth, barHeight);
                                g.setColor(Color.BLACK);
                                g.drawRect(newCoord.getX(), newCoord.getY(), barWidth, barHeight);
                                // Update Coordinates
                                newCoord.setX(newCoord.getX() + barWidth);
                                chartCoords.put(entityName, newCoord);
                            }
                        }
                    }
                }

                // Paint totals
                for (BarChartEntity entity : bars) {
                    String entityName = entity.getBarName().toString();
                    if (entity.getBarName() instanceof Date)
                        entityName = new SimpleDateFormat("dd MMM yyyy").format(entity.getBarName());
                    BarChartCoordinate coord = chartCoords.get(entityName);
                    if (coord != null) {
                        g.setColor(Color.BLACK);
                        g.drawString(chartMaxValues.get(entityName).toString(), coord.getX() + 3, coord.getY() + barHeight/2 + BAR_HEIGHT_BUFFER);
                        chartCoords.remove(entityName);
                    }
                }
            }
	}

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(chartWidth, chartHeight);
    }

    public int getChartHeight() {
        return chartHeight;
    }

    public int getChartWidth() {
        return chartWidth;
    }

}