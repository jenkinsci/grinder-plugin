package hudson.plugins.grinder;

import hudson.Plugin;
import hudson.tasks.BuildStep;

/**
 * Entry point for the grinder plugin.
 *
 * @author Eivind B Waaler
 */
public class GrinderPlugin extends Plugin {

   static final String ICON_FILE_NAME = "graph.gif";
   static final String DISPLAY_NAME = "Grinder report";
   static final String URL = "grinder";

   @Override
   public void start() throws Exception {
      BuildStep.PUBLISHERS.addRecorder(GrinderPublisher.DESCRIPTOR);
   }
}
