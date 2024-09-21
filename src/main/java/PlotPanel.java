import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.JPanel;
import java.awt.Color;
import java.util.List;

public class PlotPanel extends JPanel {

    private JFreeChart chart;
    private XYPlot plot;
    private XYSeriesCollection scatterDataset;
    private XYSeriesCollection functionDataset;

    public PlotPanel() {
        scatterDataset = new XYSeriesCollection();
        functionDataset = new XYSeriesCollection();

        chart = ChartFactory.createScatterPlot(
                "Scatter Plot with Approximation Function",
                "X-Axis",
                "Y-Axis",
                scatterDataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        plot = chart.getXYPlot();

        XYDotRenderer dotRenderer = new XYDotRenderer();
        dotRenderer.setDotWidth(5);
        dotRenderer.setDotHeight(5);
        plot.setRenderer(0, dotRenderer);

        XYLineAndShapeRenderer lineRenderer = new XYLineAndShapeRenderer();
        lineRenderer.setSeriesLinesVisible(0, true);
        lineRenderer.setSeriesShapesVisible(0, false);
        plot.setRenderer(1, lineRenderer);

        plot.setBackgroundPaint(Color.white);
        plot.setDomainGridlinePaint(Color.black);
        plot.setRangeGridlinePaint(Color.black);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));
        this.add(chartPanel);
    }

    public void updateScatterDataset(List<MyPoint> points) {
        XYSeries scatterSeries = new XYSeries("User Input Dots");
        for (MyPoint point : points) {
            scatterSeries.add(point.getX(), point.getY());
        }
        scatterDataset.removeAllSeries();
        scatterDataset.addSeries(scatterSeries);
        autoAdjustRange(points);
    }

    public void updateFunctionDataset(FunctionType functionType, double[] coeffs, List<MyPoint> points) {
        XYSeries functionSeries = new XYSeries("Approximation Function");

        double minX = points.stream().mapToDouble(MyPoint::getX).min().orElse(0);
        double maxX = points.stream().mapToDouble(MyPoint::getX).max().orElse(5);

        for (double x = minX; x <= maxX; x += 0.1) {
            double y = calculateApproximation(x, functionType, coeffs);
            functionSeries.add(x, y);
        }

        functionDataset.removeAllSeries();
        functionDataset.addSeries(functionSeries);
        plot.setDataset(1, functionDataset);
    }

    private double calculateApproximation(double x, FunctionType functionType, double[] coeffs) {
        switch (functionType) {
            case LINEAR:
                return coeffs[0] * x + coeffs[1];  // y = a + bx
            case POLY2:
                return coeffs[0] + coeffs[1] * x + coeffs[2] * x * x; // y = a + bx + cx^2
            case POLY3:
                return coeffs[0] + coeffs[1] * x + coeffs[2] * x * x + coeffs[3] * x * x * x; // y = a + bx + cx^2 + dx^3
            case EXP:
                return coeffs[0] * Math.exp(coeffs[1] * x);  // y = a * exp(bx)
            case LOG:
                return coeffs[0] + coeffs[1] * Math.log(x);  // y = a + b * log(x)
            case POWER:
                return coeffs[0] * Math.pow(x, coeffs[1]);   // y = a * x^b
            default:
                throw new IllegalArgumentException("Unknown function type");
        }
    }

    private void autoAdjustRange(List<MyPoint> points) {
        if (points.isEmpty()) return;

        double minX = points.stream().mapToDouble(MyPoint::getX).min().orElse(0);
        double maxX = points.stream().mapToDouble(MyPoint::getX).max().orElse(1);
        double minY = points.stream().mapToDouble(MyPoint::getY).min().orElse(0);
        double maxY = points.stream().mapToDouble(MyPoint::getY).max().orElse(1);

        double paddingX = (maxX - minX) * 0.1;
        double paddingY = (maxY - minY) * 0.1;

        minX -= paddingX;
        maxX += paddingX;
        minY -= paddingY;
        maxY += paddingY;

        if (minX == maxX) {
            maxX += 1;
        }
        if (minY == maxY) {
            maxY += 1;
        }

        plot.getDomainAxis().setRange(minX, maxX);
        plot.getRangeAxis().setRange(minY, maxY);
    }


}
