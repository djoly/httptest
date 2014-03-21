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

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author David Joly
 */
public class AssertionComparatorTest {

    @Test
    public void testCompare() {
        List<Assertion> assertions = new ArrayList<Assertion>();
        assertions.add(new Assertion("/foo",401));
        assertions.add(new Assertion("/foo",401));
        assertions.add(new Assertion("/foo",401));
        assertions.add(new Assertion("/foo",401));
        assertions.add(new Assertion("/foo",401));
        assertions.add(new Assertion("/foo",401));

        assertions.get(0).setResult(Result.PASS);
        assertions.get(1).setResult(Result.PASS);
        assertions.get(2).setResult(Result.PASS);
        assertions.get(3).setResult(Result.FAIL);
        assertions.get(4).setResult(Result.ERROR);
        assertions.get(5).setResult(Result.PASS);

        Collections.sort(assertions,new AssertionComparator());

        Assert.assertEquals(Result.ERROR, assertions.get(0).getResult());
        Assert.assertEquals(Result.FAIL, assertions.get(1).getResult());
        Assert.assertEquals(Result.PASS, assertions.get(2).getResult());
    }
}
