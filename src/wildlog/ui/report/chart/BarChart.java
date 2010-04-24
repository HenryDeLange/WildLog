/*
 * BarChart.java is part of WildLog
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

package wildlog.ui.report.chart;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

public class BarChart extends JPanel {
        // Variables
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
	protected void paintComponent(Graphics g) {
            if (bars.size() > 0) {
                if (bars.get(0).getBarName() instanceof Date)
                    Collections.sort(bars, Collections.reverseOrder());
                else
                    Collections.sort(bars);
            }
            if (bars.size() > 0) {
                // Determine longest bar
                Map<String, Integer> chartMaxValues = new LinkedHashMap<String, Integer>();
                int max = Integer.MIN_VALUE;
                for (BarChartEntity entity : bars) {
                    String entityName = entity.getBarName().toString();
                    if (entity.getBarName() instanceof Date)
                        entityName = new SimpleDateFormat("dd MMM yyyy").format(entity.getBarName());
                    Integer temp = chartMaxValues.get(entityName);
                    if (temp == null) temp = 0;
                    chartMaxValues.put(entityName, temp + entity.getValue());
                    max = Math.max(max, chartMaxValues.get(entityName));
                }

                // Determine bar sort order
                List<String> unsortedList = new ArrayList<String>(chartMaxValues.keySet());
                List<String> sortOrder = new ArrayList<String>(unsortedList.size());
                for (int t = 0; t < unsortedList.size(); t++) {
                    if (sortOrder.size() == 0) {
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
                final int labelsBuffer = 102;
                final int totalsBuffer = 15;
                //final int scaleBuffer = 20;
                final int scaleBuffer = 10;
                int barHeight = ((chartHeight - scaleBuffer) / chartMaxValues.size()) - 5;
                Map<String, BarChartCoordinate> chartCoords = new LinkedHashMap<String, BarChartCoordinate>();
                for (BarChartEntity entity : bars) {
                    String entityName = entity.getBarName().toString();
                    if (entity.getBarName() instanceof Date)
                        entityName = new SimpleDateFormat("dd MMM yyyy").format(entity.getBarName());
                    // Setup
                    int value = entity.getValue();
                    int barWidth = (int)((chartWidth - (labelsBuffer + totalsBuffer)) * ((double)value / max));
                    BarChartCoordinate coord = chartCoords.get(entityName);
                    if (coord == null) {
                        coord = new BarChartCoordinate(labelsBuffer, (barHeight + 5) * chartSortOrder.get(entityName) + 5);
                        // Labels
                        g.setColor(Color.BLACK);
                        String label = entityName;
                        if (label.length() > 17)
                            label = label.substring(0, 15) + "..";
                        g.drawString(label, 5, coord.getY() + barHeight/2 + 5);
                    }

                    // Draw Bars
                    g.setColor(entity.getColor());
                    g.fillRect(coord.getX(), coord.getY(), barWidth, barHeight);
                    g.setColor(Color.BLACK);
                    g.drawRect(coord.getX(), coord.getY(), barWidth, barHeight);

                    // TODO: Fix - Die interval is verkeer hoe meer sightings dar is
    //                // Draw scale
    //                g.setColor(Color.BLACK);
    //                int interval = max/10;
    //                if (interval == 0) interval = 1;
    //                for (int t = 0; t <= max/interval; t++) {
    //                    g.drawString(Integer.toString(t*interval), labelsBuffer + (int)((double)(chartWidth - labelsBuffer - totalsBuffer)/(double)((double)max/interval))*t - Integer.toString(t*interval).length()*2, chartHeight - 3);
    //                    g.fillRect(labelsBuffer + (int)((double)(chartWidth - labelsBuffer - totalsBuffer)/(double)((double)max/interval))*t, chartHeight - 15, 2, 2);
    //                }

                    // Update Coordinates
                    coord.setX(coord.getX() + barWidth);
                    chartCoords.put(entityName, coord);
                }
                // Paint totals
                for (BarChartEntity entity : bars) {
                    String entityName = entity.getBarName().toString();
                    if (entity.getBarName() instanceof Date)
                        entityName = new SimpleDateFormat("dd MMM yyyy").format(entity.getBarName());
                    BarChartCoordinate coord = chartCoords.get(entityName);
                    if (coord != null) {
                        g.setColor(Color.BLACK);
                        g.drawString(chartMaxValues.get(entityName).toString(), coord.getX() + 3, coord.getY() + barHeight/2 + 5);
                        chartCoords.remove(entityName);
                    }
                }
            }

	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(chartWidth, chartHeight);
	}
}