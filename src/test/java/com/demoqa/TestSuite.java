package com.demoqa;

import com.demoqa.tests.BookApiTest;
import com.demoqa.tests.SmokeFlowTest;
import com.demoqa.tests.UserApiTest;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Suite
@SuiteDisplayName("Bookstore API Test Suite")
@SelectClasses({
        SmokeFlowTest.class,
        UserApiTest.class,
        BookApiTest.class
})
public class TestSuite {

    private static final Logger log = LoggerFactory.getLogger(TestSuite.class);
    public static ExtentReports extent;

    static {
        log.info("==========================================");
        log.info("INICIANDO A EXECUÇÃO DA BOOKSTORE API SUITE");
        log.info("==========================================");

        ExtentSparkReporter spark = new ExtentSparkReporter("target/extent-report.html");
        extent = new ExtentReports();
        extent.attachReporter(spark);
    }

    public static void flushReport() {
        if (extent != null) {
            extent.flush();
        }
    }
}