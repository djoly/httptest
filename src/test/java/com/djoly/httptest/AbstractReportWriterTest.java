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

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author David Joly
 */
public abstract class AbstractReportWriterTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    protected ReportWriter writer;
    protected File dir;

    @Before
    public void setUp() throws Exception {
        dir = temp.newFolder();
        writer = getReportWriter();
    }

    protected List<Assertion> getAssertions() {
        List<Assertion> assertions = new ArrayList<Assertion>();
        assertions.add(new Assertion("/foo",301,"/bar"));
        assertions.add(new Assertion("/baz",400));
        assertions.add(new Assertion("/bat",301,"/bar"));
        assertions.add(new Assertion("/foo/bar",302,"/bar"));

        assertions.get(0).setResult(Result.PASS);
        assertions.get(1).setResult(Result.ERROR);
        assertions.get(1).setMessage("Foo");
        assertions.get(2).setResult(Result.FAIL);
        assertions.get(3).setResult(Result.PASS);
        return assertions;
    }

    abstract protected ReportWriter getReportWriter();
}
