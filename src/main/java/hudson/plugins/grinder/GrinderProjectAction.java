package hudson.plugins.grinder;

import hudson.model.AbstractBuild;
import hudson.model.Project;
import hudson.model.Result;
import hudson.util.ChartUtil;
import hudson.util.ChartUtil.NumberOnlyBuildLabel;
import hudson.util.ColorPalette;
import hudson.util.DataSetBuilder;
import hudson.util.ShiftedCategoryAxis;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.ui.RectangleInsets;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.awt.Color;
import java.io.IOException;

/**
 * Action used for Grinder report on project level.
 *
 * @author Eivind B Waaler
 */
public class GrinderProjectAction extends AbstractGrinderAction {

   private final Project project;

   public GrinderProjectAction(Project project) {
      this.project = project;
   }

   public Project getProject() {
      return project;
   }

   public GrinderBuildAction getActionByBuildNumber(int number) {
      return project.getBuildByNumber(number).getAction(GrinderBuildAction.class);
   }

   public void doMeanTimeGraph(StaplerRequest request, StaplerResponse response)
      throws IOException {

      if (shouldReloadGraph(request, response)) {
         ChartUtil.generateGraph(request, response, createMeanTimeGraph(), 800, 150);
      }
   }

   public void doStdDevGraph(StaplerRequest request, StaplerResponse response)
      throws IOException {

      if (shouldReloadGraph(request, response)) {
         ChartUtil.generateGraph(request, response, createStdDevGraph(), 800, 150);
      }
   }

   public void doMeanRespLengthGraph(StaplerRequest request, StaplerResponse response)
      throws IOException {

      if (shouldReloadGraph(request, response)) {
         ChartUtil.generateGraph(request, response, createMeanRespLengthGraph(), 800, 150);
      }
   }

   private JFreeChart createMeanTimeGraph() {
      return createNumberBuildGraph(Test.MEAN_TEST_TIME, "Time (ms)");
   }

   private JFreeChart createStdDevGraph() {
      return createNumberBuildGraph(Test.TEST_TIME_STANDARD_DEVIATION, "Time (ms)");
   }

   private JFreeChart createMeanRespLengthGraph() {
      return createNumberBuildGraph(Test.MEAN_RESPONSE_LENGTH, "Length (bytes)");
   }

   private JFreeChart createNumberBuildGraph(String valueName, String unitName) {
      DataSetBuilder<String, NumberOnlyBuildLabel> builder = new DataSetBuilder<String, NumberOnlyBuildLabel>();

      for (Object build : project.getBuilds()) {
         AbstractBuild abstractBuild = (AbstractBuild) build;
         if (!abstractBuild.isBuilding() && abstractBuild.getResult().isBetterOrEqualTo(Result.SUCCESS)) {
            GrinderBuildAction action = abstractBuild.getAction(GrinderBuildAction.class);
            builder.add(action.getTotals().getValue(valueName), valueName, new NumberOnlyBuildLabel(abstractBuild));
         }
      }

      JFreeChart chart = ChartFactory.createStackedAreaChart(
         valueName + " Trend",
         "Build",
         unitName,
         builder.build(),
         PlotOrientation.VERTICAL,
         false,
         false,
         false);

      chart.setBackgroundPaint(Color.WHITE);

      CategoryPlot plot = chart.getCategoryPlot();
      plot.setBackgroundPaint(Color.WHITE);
      plot.setOutlinePaint(null);
      plot.setForegroundAlpha(0.8f);
      plot.setRangeGridlinesVisible(true);
      plot.setRangeGridlinePaint(Color.black);

      CategoryAxis domainAxis = new ShiftedCategoryAxis(null);
      plot.setDomainAxis(domainAxis);
      domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
      domainAxis.setLowerMargin(0.0);
      domainAxis.setUpperMargin(0.0);
      domainAxis.setCategoryMargin(0.0);

      CategoryItemRenderer renderer = plot.getRenderer();
      renderer.setSeriesPaint(2, ColorPalette.RED);
      renderer.setSeriesPaint(1, ColorPalette.YELLOW);
      renderer.setSeriesPaint(0, ColorPalette.BLUE);

      NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
      rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

      // crop extra space around the graph
      plot.setInsets(new RectangleInsets(0, 0, 0, 5.0));

      return chart;
   }

   private boolean shouldReloadGraph(StaplerRequest request, StaplerResponse response) throws IOException {
      return shouldReloadGraph(request, response, project.getLastSuccessfulBuild());
   }
}
