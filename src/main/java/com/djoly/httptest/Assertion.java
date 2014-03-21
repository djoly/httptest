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

import org.apache.commons.lang.StringUtils;

/**
 * @author David Joly
 */
public class Assertion {

    private String url;
    private int responseCode;
    private String location;
    private String message;
    private Result result;

    public Assertion(String url, int responseCode) {
        this.url = url;
        this.responseCode = responseCode;
        this.location = StringUtils.EMPTY;
        this.message = StringUtils.EMPTY;
    }

    public Assertion(String url, int responseCode, String location) {
        this.url = url;
        this.responseCode = responseCode;
        this.location = location;
        this.message = StringUtils.EMPTY;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public String getUrl() {
        return url;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getLocation() {
        return location;
    }

    public String getMessage() {
        return message;
    }

    public Result getResult() {
        return result;
    }
}
