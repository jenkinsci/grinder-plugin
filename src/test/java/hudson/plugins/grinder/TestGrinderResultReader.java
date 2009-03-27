package hudson.plugins.grinder;

import junit.framework.TestCase;

import java.io.InputStream;
import java.util.List;

/**
 * Test class for the Grinder results loader.
 *
 * @author Eivind B Waaler
 */
public class TestGrinderResultReader extends TestCase {

   public void testLoadOneTestResultWithError() throws Exception {
      InputStream is = getClass().getResourceAsStream("/out_2.log");

      ResultReader rr = new ResultReader(is, System.out);
      List<Test> tests = rr.getTests();

      assertNotNull(tests);
      assertEquals(1, tests.size());

      assertTest(tests.get(0), "Test 101", 1, 0, 47.0, 0.0, 1518.0, 0.0, 1, 16.0, 16.0, 47.0, "Hent forside (HTML) med pålogging");

      Test totals = rr.getTotals();

      assertTest(totals, "Totals", 1, 0, 47.0, 0.0, 1518.0, 0.0, 1, 16.0, 16.0, 47.0, "");
   }

   public void testLoadMultipleTestResultsFromWindows() throws Exception {
      InputStream is = getClass().getResourceAsStream("/out_1.log");

      ResultReader rr = new ResultReader(is, System.out);
      List<Test> tests = rr.getTests();

      assertNotNull(tests);
      assertEquals(8, tests.size());

      assertTest(tests.get(0), "Test 101", 50, 0, 17407.6, 15017.92, 67586.60, 0.0, 0, 1.60, 4.08, 4317.0, "Hent forside (HTML) med pålogging");
      assertTest(tests.get(1), "Test 201", 30, 0, 1311.03, 1509.49, 26856.77, 0.0, 0, 0.0, 0.0, 1203.63, "Hent fagområder");
      assertTest(tests.get(2), "Test 301", 20, 0, 1807.9, 2233.07, 22882.4, 0.0, 0, 0.0, 0.0, 1730.6, "Hent nyhet A");
      assertTest(tests.get(3), "Test 401", 10, 0, 903.6, 652.5, 24198.0, 0.0, 0, 0.0, 0.0, 772.3, "Hent nyhet B");
      assertTest(tests.get(4), "Test 501", 10, 0, 867.6, 1436.25, 23326.0, 0.0, 0, 0.0, 0.0, 795.6, "Hent nyhet C");
      assertTest(tests.get(5), "Test 601", 10, 0, 953.2, 1535.51, 22879.0, 0.0, 0, 0.0, 0.0, 823.6, "Hent nyhet D");
      assertTest(tests.get(6), "Test 701", 10, 0, 692.4, 930.25, 21385.5, 0.0, 0, 0.0, 0.0, 659.5, "Åpne ansattsøk");
      assertTest(tests.get(7), "Test 801", 10, 0, 2222.6, 1522.19, 37542.5, 0.0, 0, 0.0, 0.0, 2016.1, "Utfør ansattsøk - fornavn Eivind");

      assertTest(rr.getTotals(), "Totals", 150, 0, 6681.75, 11597.56, 39573.27, 0.0, 0, 0.53, 1.36, 2248.28, "");
   }

   public void testLoadMultipleTestResultsFromMac() throws Exception {
      InputStream is = getClass().getResourceAsStream("/out_3.log");

      ResultReader rr = new ResultReader(is, System.out);
      List<Test> tests = rr.getTests();

      assertNotNull(tests);
      assertEquals(5, tests.size());

      assertTest(tests.get(0), "Test 1", 100, 0, 698.49, 453.3, 30152.0, 0.0, 0, 1.39, 69.67, 344.75, "Frontpage");
      assertTest(tests.get(1), "Test 2", 100, 0, 1717.36, 580.06, 29175.0, 0.0, 0, 0.0, 0.0, 1583.85, "Search");
      assertTest(tests.get(2), "Test 3", 100, 0, 284.55, 309.91, 126.0, 0.0, 0, 0.0, 0.0, 284.2, "English");
      assertTest(tests.get(3), "Test 4", 100, 0, 468.92, 218.33, 42491.0, 0.0, 0, 0.0, 0.0, 344.14, "Trondheim");
      assertTest(tests.get(4), "Test 5", 100, 0, 383.9, 213.65, 31369.0, 0.0, 0, 0.0, 0.0, 285.86, "UI design");

      assertTest(rr.getTotals(), "Totals", 500, 0, 710.64, 646.79, 26662.6, 0.0, 0, 0.28, 13.93, 568.56, "");
   }

   public void testSeleniumLogFile() throws Exception {
      InputStream is = getClass().getResourceAsStream("/out_Selenium1-0.log");

      ResultReader rr = new ResultReader(is, System.out);
      List<Test> tests = rr.getTests();
   }

   public void testNullInput() throws Exception {
      try {
         new ResultReader(null, System.out);
      } catch (GrinderParseException e) {
         assertEquals("Empty input stream", e.getMessage());
      }
   }

   private void assertTest(
      Test test,
      String id,
      Number testCount,
      Number errorCount,
      Number meanTime,
      Number stdDev,
      Number meanRespLength,
      Number respBytesPrSec,
      Number respErrorCount,
      Number resolveHostMeanTime,
      Number establishConnMeanTime,
      Number firstByteMeanTime,
      String name) {

      assertEquals(id, test.getId());
      assertEquals(testCount, test.getTestCount());
      assertEquals(errorCount, test.getErrorCount());
      assertEquals(meanTime, test.getMeanTime());
      assertEquals(stdDev, test.getStdDev());
      assertEquals(meanRespLength, test.getMeanRespLength());
      assertEquals(respBytesPrSec, test.getRespBytesPrSecond());
      assertEquals(respErrorCount, test.getRespErrorCount());
      assertEquals(resolveHostMeanTime, test.getResolveHostMeanTime());
      assertEquals(establishConnMeanTime, test.getEstablishConnMeanTime());
      assertEquals(firstByteMeanTime, test.getFirstByteMeanTime());
      assertEquals(name, test.getName());
   }
}
