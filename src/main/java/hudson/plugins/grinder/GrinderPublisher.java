package hudson.plugins.grinder;

import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.Publisher;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

/**
 * The publisher creates the results we want from the Grinder execution.
 *
 * @author Eivind B Waaler
 */
public class GrinderPublisher extends Publisher {

   private String name;

   @DataBoundConstructor
   public GrinderPublisher(String name) {
      this.name = name;
   }

   public String getName() {
      return name;
   }

   public Descriptor<Publisher> getDescriptor() {
      return DESCRIPTOR;
   }

   @Override
   public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
      throws InterruptedException, IOException {

      PrintStream logger = listener.getLogger();
      
      if (build.getProject().getWorkspace().child(name).exists()) {
         InputStream is = build.getProject().getWorkspace().child(name).read();
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

   public Action getProjectAction(Project project) {
      return new GrinderProjectAction(project);
   }

   public static final Descriptor<Publisher> DESCRIPTOR = new DescriptorImpl();

   public static final class DescriptorImpl extends Descriptor<Publisher> {

      protected DescriptorImpl() {
         super(GrinderPublisher.class);
      }

      public String getDisplayName() {
         return GrinderPlugin.DISPLAY_NAME;
      }
   }
}
