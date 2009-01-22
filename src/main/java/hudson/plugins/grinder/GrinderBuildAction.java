package hudson.plugins.grinder;

import hudson.model.AbstractBuild;
import hudson.util.ChartUtil;
import hudson.util.ColorPalette;
import hudson.util.DataSetBuilder;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.StatisticalBarRenderer;
import org.jfree.data.statistics.DefaultStatisticalCategoryDataset;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

/**
 * Action used for Grinder report on build level.
 *
 * @author Eivind B Waaler
 */
public class GrinderBuildAction extends AbstractGrinderAction {
   private final AbstractBuild<?, ?> build;
   private Test totals;
   private List<Test> tests;

   public GrinderBuildAction(AbstractBuild<?, ?> build, InputStream is, PrintStream logger) {
      this.build = build;
      ResultReader rs = new ResultReader(is, logger);
      totals = rs.getTotals();
      tests = rs.getTests();
      logger.println("Created Grinder results");
   }

   public AbstractBuild<?, ?> getBuild() {
      return build;
   }

   public Test getTotals() {
      return totals;
   }

   public List<Test> getTests() {
      return tests;
   }

   public void doTestGraph(StaplerRequest request, StaplerResponse response)
      throws IOException {

      if (shouldReloadGraph(request, response, build)) {
         ChartUtil.generateGraph(request, response, createTestGraph(), 800, 400);
      }
   }

   private JFreeChart createTestGraph() {
      DefaultStatisticalCategoryDataset timeDS = new DefaultStatisticalCategoryDataset();
      DataSetBuilder<String, Comparable> lengthDS = new DataSetBuilder<String, Comparable>();

      for (Test test : tests) {
         timeDS.add(test.getMeanTime(), test.getStdDev(), Test.MEAN_TEST_TIME, test.getId());
         lengthDS.add(test.getMeanRespLength(), Test.MEAN_RESPONSE_LENGTH, test.getId());
      }

      final CategoryAxis xAxis = new CategoryAxis("Test name");
      xAxis.setLowerMargin(0.01);
      xAxis.setUpperMargin(0.01);
      xAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
      xAxis.setMaximumCategoryLabelLines(3);

      final ValueAxis timeAxis = new NumberAxis("Time (ms)");
      final ValueAxis lengthAxis = new NumberAxis("Length (bytes)");

      final BarRenderer timeRenderer = new StatisticalBarRenderer();
      timeRenderer.setSeriesPaint(2, ColorPalette.RED);
      timeRenderer.setSeriesPaint(1, ColorPalette.YELLOW);
      timeRenderer.setSeriesPaint(0, ColorPalette.BLUE);
      timeRenderer.setItemMargin(0.0);

      final CategoryPlot plot = new CategoryPlot(timeDS, xAxis, timeAxis, timeRenderer);
      plot.setBackgroundPaint(Color.WHITE);
      plot.setOutlinePaint(null);
      plot.setForegroundAlpha(0.8f);
      plot.setRangeGridlinesVisible(true);
      plot.setRangeGridlinePaint(Color.black);

      final CategoryItemRenderer lengthRenderer = new LineAndShapeRenderer();
      plot.setRangeAxis(1, lengthAxis);
      plot.setDataset(1, lengthDS.build());
      plot.mapDatasetToRangeAxis(1, 1);
      plot.setRenderer(1, lengthRenderer);

      JFreeChart chart = new JFreeChart("Test time", plot);
      chart.setBackgroundPaint(Color.WHITE);

      return chart;
   }

}
