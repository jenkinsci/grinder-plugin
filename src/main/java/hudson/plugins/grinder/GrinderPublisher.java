package hudson.plugins.grinder;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

/**
 * The publisher creates the results we want from the Grinder execution.
 *
 * @author Eivind B Waaler
 */
public class GrinderPublisher extends Recorder {

   private String name;

   @DataBoundConstructor
   public GrinderPublisher(String name) {
      this.name = name;
   }

   public String getName() {
      return name;
   }

   @Override
   public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
      throws InterruptedException, IOException {

      PrintStream logger = listener.getLogger();
      
      if (build.getWorkspace().child(name).exists()) {
         InputStream is = build.getWorkspace().child(name).read();
         try {
            build.addAction(new GrinderBuildAction(build, is, logger));
         } catch (GrinderParseException gpe) {
            logger.println("Grinder report failed!");
            build.setResult(Result.FAILURE);
         }
      } else {
         logger.println("Grinder out* log file not found!");
         build.setResult(Result.FAILURE);
      }

      return true;
   }

   @Override
   public Action getProjectAction(AbstractProject<?, ?> project) {
      return project instanceof Project ? new GrinderProjectAction((Project)project) : null;
   }

   public BuildStepMonitor getRequiredMonitorService() {
      return BuildStepMonitor.NONE;
   }

   @Extension
   public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

      public DescriptorImpl() {
         super(GrinderPublisher.class);
      }

      public String getDisplayName() {
         return GrinderPlugin.DISPLAY_NAME;
      }

      @Override
      public boolean isApplicable(Class<? extends AbstractProject> jobType) {
         return Project.class.isAssignableFrom(jobType);
      }
   }
}
