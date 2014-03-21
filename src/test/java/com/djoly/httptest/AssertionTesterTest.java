package com.djoly.httptest;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;

/**
 * @author David Joly
 */
public class AssertionTesterTest {

    final static String SERVER_URL = "http://www.google.com";
    @Mock
    private HttpClient httpClient;
    private AssertionTester tester;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        tester = new AssertionTester(httpClient,SERVER_URL);

        final Field statusLineField = HttpMethodBase.class.getDeclaredField("statusLine");
        statusLineField.setAccessible(true);
        final Field responseHeadersField = HttpMethodBase.class.getDeclaredField("responseHeaders");
        responseHeadersField.setAccessible(true);


        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                GetMethod r = (GetMethod) args[0];

                StatusLine status = null;
                HeaderGroup headerGroup = new HeaderGroup();

                if ("/match-response-code".equals(r.getPath())) {
                    status = new StatusLine("HTTP/1.1 200 OK");
                } else if ("/match-location-and-response-code".equals(r.getPath())) {
                    status = new StatusLine("HTTP/1.1 301 Redirect");
                    headerGroup.addHeader(new Header("Location",SERVER_URL+"/bar"));
                } else if ("/match-location-not-response-code".equals(r.getPath())) {
                    status = new StatusLine("HTTP/1.1 302 Redirect");
                    headerGroup.addHeader(new Header("Location",SERVER_URL+"/bar"));
                } else if ("/match-response-code-not-location".equals(r.getPath())) {
                    status = new StatusLine("HTTP/1.1 301 Redirect");
                    headerGroup.addHeader(new Header("Location",SERVER_URL+"/foo"));
                } else if("/not-match-response-code".equals(r.getPath())) {
                    status = new StatusLine("HTTP/1.1 200 OK");
                } else if("/error".equals(r.getPath())) {
                    throw new HttpException("Uh-oh");
                }

                statusLineField.set(r, status);
                responseHeadersField.set(r,headerGroup);
                return null;
            }
        }).when(httpClient).executeMethod(any(HttpMethod.class));
    }

    @Test
    public void matchResponseCode() {
        Assertion assertion = new Assertion("/match-response-code",200);
        tester.testAssertion(assertion);
        assertEquals(Result.PASS, assertion.getResult());
    }

    @Test
    public void notMatchResponseCode() {
        Assertion assertion = new Assertion("/not-match-response-code",400);
        tester.testAssertion(assertion);
        assertEquals(Result.FAIL, assertion.getResult());
        assertEquals("Expected status code 400 but got 200",assertion.getMessage());
    }

    @Test
    public void matchLocationAndResponseCode() {
        Assertion assertion = new Assertion("/match-location-and-response-code",301,"/bar");
        tester.testAssertion(assertion);
        assertEquals(Result.PASS, assertion.getResult());
    }

    @Test
    public void matchResponseCodeNotLocation() {
        Assertion assertion = new Assertion("/match-response-code-not-location",301,"/bar");
        tester.testAssertion(assertion);
        assertEquals(Result.FAIL, assertion.getResult()); //Returning a /foo location header, should fail...
        assertEquals(String.format("Location header did not match. Expected %s but got %s",SERVER_URL+"/bar",SERVER_URL+"/foo"),assertion.getMessage());
    }

    @Test
    public void matchLocationNotResponseCode() {
        Assertion assertion = new Assertion("/match-location-not-response-code",301,"/bar");
        tester.testAssertion(assertion);
        assertEquals(Result.FAIL, assertion.getResult()); //Returning a 302, should fail...
        assertEquals("Expected status code 301 but got 302",assertion.getMessage());
    }

    @Test
    public void errorThrown() {
        Assertion assertion = new Assertion("/error",301);
        tester.testAssertion(assertion);
        assertEquals(Result.ERROR, assertion.getResult());
        assertEquals("Uh-oh",assertion.getMessage());
    }
}
