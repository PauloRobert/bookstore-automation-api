package com.demoqa;

import com.demoqa.tests.SmokeFlowTest;
import com.demoqa.tests.UserApiTest;
import com.demoqa.tests.BookApiTest;

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

    static {
        log.info("==========================================");
        log.info("INICIANDO A EXECUÇÃO DA BOOKSTORE API SUITE");
        log.info("==========================================");
    }
}