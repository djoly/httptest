/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 David Joly
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.djoly.httptest;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;

/**
 * @author David Joly
 */
public class AssertionTester {

    private HttpClient client;
    private String serverUrl;

    /**
     * @param client
     * @param serverUrl
     */
    public AssertionTester(HttpClient client, String serverUrl) {
        this.client = client;
        this.serverUrl = serverUrl;
    }

    /**
     * Tests the provided {@link Assertion} and attached the {@link Result}.
     *
     * @param assertion
     */
    public void testAssertion(Assertion assertion) {
        HttpMethod request = new GetMethod(serverUrl + assertion.getUrl());
        request.setFollowRedirects(false);

        try {
            client.executeMethod(request);

            if(request.getStatusCode() != assertion.getResponseCode()) {
                assertion.setMessage(String.format("Expected status code %s but got %s",assertion.getResponseCode(),request.getStatusCode()));
                assertion.setResult(Result.FAIL);

            } else if(StringUtils.isNotBlank(assertion.getLocation())) {
                Header location = request.getResponseHeader("Location");

                if(location == null || StringUtils.isBlank(location.getValue())) {
                    assertion.setResult(Result.FAIL);
                    assertion.setMessage("Expected a location header, but none returned");

                } else if(StringUtils.equals(serverUrl + assertion.getLocation(),location.getValue())) {
                    assertion.setResult(Result.PASS);
                } else {
                    assertion.setMessage(String.format("Location header did not match. Expected %s but got %s",serverUrl+assertion.getLocation(),location.getValue()));
                    assertion.setResult(Result.FAIL);
                }
            } else {
                assertion.setResult(Result.PASS);
            }

        } catch(HttpException e) {
            assertion.setMessage(e.getMessage());
            assertion.setResult(Result.ERROR);
        } catch(IOException e) {
            assertion.setMessage(e.getMessage());
            assertion.setResult(Result.ERROR);
        } finally {
            request.releaseConnection();
        }
    }
}
