package hudson.plugins.grinder;

import hudson.model.Action;
import hudson.model.Run;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.io.IOException;

/**
 * Abstract class with functionality common to all Grinder actions.
 *
 * @author Eivind B Waaler
 */
public class AbstractGrinderAction implements Action {
   public String getIconFileName() {
      return GrinderPlugin.ICON_FILE_NAME;
   }

   public String getDisplayName() {
      return GrinderPlugin.DISPLAY_NAME;
   }

   public String getUrlName() {
      return GrinderPlugin.URL;
   }

   protected boolean shouldReloadGraph(StaplerRequest request, StaplerResponse response, Run build) throws IOException {
      return !request.checkIfModified(build.getTimestamp(), response);
   }
}
