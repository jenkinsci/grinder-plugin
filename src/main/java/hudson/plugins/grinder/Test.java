package hudson.plugins.grinder;

import java.util.Map;
import java.util.HashMap;

/**
 * Model class representing one Grinder test.
 *
 * @author Eivind B Waaler
 */
public class Test {
   public static final String MEAN_RESPONSE_LENGTH = "Mean Response Length";
   public static final String TEST_COUNT = "Test Count";
   public static final String ERROR_COUNT = "Error Count";
   public static final String MEAN_TEST_TIME = "Mean Test Time";
   public static final String TEST_TIME_STANDARD_DEVIATION = "Test Time Standard Deviation";
   public static final String RESPONSE_BYTES_PER_SECOND = "Response Bytes Per Second";
   public static final String RESPONSE_ERROR_COUNT = "Response Error Count";
   public static final String RESOLVE_HOST_MEAN_TIME = "Resolve Host Mean Time";
   public static final String ESTABLISH_CONNECTION_MEAN_TIME = "Establish Connection Mean Time";
   public static final String FIRST_BYTE_MEAN_TIME = "First Byte Mean Time";

   private String id;
   private String name;
   private Map<String, Number> values = new HashMap<String, Number>();

   public Test(
      String id,
      int testCount,
      int errorCount,
      double meanTime,
      double stdDev,
      double meanRespLength,
      double respBytesPrSecond,
      int respErrorCount,
      double resolveHostMeanTime,
      double establishConnMeanTime,
      double firstByteMeanTime,
      String name) {
      
      this.id = id;
      values.put(TEST_COUNT, testCount);
      values.put(ERROR_COUNT, errorCount);
      values.put(MEAN_TEST_TIME, meanTime);
      values.put(TEST_TIME_STANDARD_DEVIATION, stdDev);
      values.put(MEAN_RESPONSE_LENGTH, meanRespLength);
      values.put(RESPONSE_BYTES_PER_SECOND, respBytesPrSecond);
      values.put(RESPONSE_ERROR_COUNT, respErrorCount);
      values.put(RESOLVE_HOST_MEAN_TIME, resolveHostMeanTime);
      values.put(ESTABLISH_CONNECTION_MEAN_TIME, establishConnMeanTime);
      values.put(FIRST_BYTE_MEAN_TIME, firstByteMeanTime);
      this.name = name;
   }

   public String getId() {
      return id;
   }

   public Number getTestCount() {
      return values.get(TEST_COUNT);
   }

   public Number getErrorCount() {
      return values.get(ERROR_COUNT);
   }

   public Number getMeanTime() {
      return values.get(MEAN_TEST_TIME);
   }

   public Number getStdDev() {
      return values.get(TEST_TIME_STANDARD_DEVIATION);
   }

   public Number getMeanRespLength() {
      return values.get(MEAN_RESPONSE_LENGTH);
   }

   public Number getRespBytesPrSecond() {
      return values.get(RESPONSE_BYTES_PER_SECOND);
   }

   public Number getRespErrorCount() {
      return values.get(RESPONSE_ERROR_COUNT);
   }

   public Number getResolveHostMeanTime() {
      return values.get(RESOLVE_HOST_MEAN_TIME);
   }

   public Number getEstablishConnMeanTime() {
      return values.get(ESTABLISH_CONNECTION_MEAN_TIME);
   }

   public Number getFirstByteMeanTime() {
      return values.get(FIRST_BYTE_MEAN_TIME);
   }

   public Number getValue(String name) {
      return values.get(name);
   }

   public String getName() {
      return name;
   }
}
