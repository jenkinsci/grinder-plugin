package hudson.plugins.grinder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Class used to read results from a Grinder output file.
 *
 * @author Eivind B Waaler
 */
public class ResultReader {
   private List<Test> tests;
   private Test totals;

   private transient final PrintStream hudsonConsoleWriter;

   private static final String PATTERN_STATS_HEADER = " Tests        Errors .*";
   private static final String PATTERN_TEST = "Test \\d.*";
   private static final String PATTERN_TOTALS = "Totals .*";
   private static final String PATTERN_TPS = " TPS ";

   /**
    * Construct a result reader for grinder out log files.
    *
    * @param is     The input stream giving the out log file.
    * @param logger Logger to print messages to.
    * @throws GrinderParseException Thrown if the parsing fails.
    */
   public ResultReader(InputStream is, PrintStream logger) {
      hudsonConsoleWriter = logger;
      parse(is);
   }

   /**
    * Construct a result reader for grinder out log files.
    *
    * @param logger Logger to print messages to.
    * @throws GrinderParseException Thrown if the parsing fails.
    */
   public ResultReader(PrintStream logger) {
      hudsonConsoleWriter = logger;
   }

   private void parse(InputStream is) {
      if (is == null) {
         throw new GrinderParseException("Empty input stream");
      }

      if (tests == null) {
         tests = new ArrayList<Test>();
      }

      try {
         BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));

         String str = findStats(bufferedReader);
         boolean hasTPS = str.contains(PATTERN_TPS);

         Scanner scanner = new Scanner(str);
         String match = scanner.findWithinHorizon(PATTERN_TEST, 0);

         boolean hasnext = scanner.hasNext();
         while (match != null) {
            tests.add(readTest(match, false, hasTPS));
            match = scanner.findWithinHorizon(PATTERN_TEST, 0);
         }
         match = scanner.findWithinHorizon(PATTERN_TOTALS, 0);
         totals = readTest(match, true, hasTPS);
      } catch (Exception e) {
         String errMsg = "Problem parsing Grinder out log file";
         hudsonConsoleWriter.println(errMsg + ": " + e.getMessage());
         e.printStackTrace(hudsonConsoleWriter);
         throw new GrinderParseException(errMsg, e);
      }
   }

   public List<Test> getTests() {
      return tests;
   }

   public Test getTotals() {
      return totals;
   }

   private Test readTest(String testLine, boolean isTotals, boolean hasTPS) {
      Scanner scanner = new Scanner(testLine).useDelimiter("\\s{2,}").useLocale(Locale.ENGLISH);
      String id = scanner.next();
      int testCount = scanner.nextInt();
      int errorCount = scanner.nextInt();
      double meanTestTime = scanner.nextDouble();
      double testStdDevTime = scanner.nextDouble();
      double tps = 0.0;
      if (hasTPS) {
         tps = scanner.nextDouble();
      }
      if (scanner.hasNextDouble()) {
         double meanRespLength = scanner.nextDouble();
         double respBytesPrSecond = 0.0;
         if (scanner.hasNextDouble()) {
            respBytesPrSecond = scanner.nextDouble();
         } else {
            scanner.next(); // reported as '?' in log
         }
         int respErrorCount = scanner.nextInt();
         double resolveHostMeanTime = 0.0;
         if (scanner.hasNextDouble()) {
            resolveHostMeanTime = scanner.nextDouble();
         } else {
            scanner.next(); // reported as '-' in log
         }
         double establishConnMeanTime = 0.0;
         if (scanner.hasNextDouble()) {
            establishConnMeanTime = scanner.nextDouble();
         } else {
            scanner.next(); // reported as '-' in log
         }
         double firstByteMeanTime = Double.valueOf(scanner.next().replaceAll("[)]",""));
         String name = isTotals ? "" : scanner.next().replaceAll("\"", "");

         return new Test(
            id,
            testCount,
            errorCount,
            meanTestTime,
            testStdDevTime,
            tps,
            meanRespLength,
            respBytesPrSecond,
            respErrorCount,
            resolveHostMeanTime,
            establishConnMeanTime,
            firstByteMeanTime,
            name
         );
      } else {
         String name = isTotals ? "" : scanner.next().replaceAll("\"", "");

         return new Test(
            id,
            testCount,
            errorCount,
            meanTestTime,
            testStdDevTime,
            name
         );
      }
   }

   private String findStats(BufferedReader bufferedReader) {
      Scanner scanner = new Scanner(bufferedReader);
      Pattern pattern = Pattern.compile(PATTERN_STATS_HEADER);
      StringBuilder toBeReturned = new StringBuilder();
      String line = scanner.findInLine(pattern);
      while (line == null && scanner.hasNextLine()) {
         scanner.nextLine();
         line = scanner.findInLine(pattern);
      }
      hudsonConsoleWriter.println(line);
      toBeReturned.append(line).append("\n");

      while (scanner.hasNextLine()) {
         String nextline = scanner.nextLine();
         hudsonConsoleWriter.println(nextline);
         toBeReturned.append(nextline).append("\n");
      }

      return toBeReturned.toString();
   }

}
