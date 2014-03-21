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

import java.util.Comparator;

public class AssertionComparator implements Comparator<Assertion> {

    public int compare(Assertion a1,Assertion a2) {
        int w1 = getWeight(a1.getResult());
        int w2 = getWeight(a2.getResult());
        return (w1 > w2 ? -1 :(w1 == w2 ? 0 : 1));
    }

    private int getWeight(Result res) {
        int weight = 0;
        switch(res) {
            case ERROR: weight = 3; break;
            case FAIL: weight = 2; break;
            case PASS: weight = 1; break;
        }
        return weight;
    }
}
