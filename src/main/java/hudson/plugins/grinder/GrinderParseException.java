package hudson.plugins.grinder;

/**
 * Exception used to show Grinder parsing has failed.
 *
 * @author Eivind B Waaler
 */
public class GrinderParseException extends RuntimeException {
   public GrinderParseException(String msg, Exception e) {
      super(msg, e);
   }

   public GrinderParseException(String msg) {
      super(msg);
   }
}
