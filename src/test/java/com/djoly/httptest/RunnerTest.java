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

import au.com.bytecode.opencsv.CSVReader;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author David Joly
 */
public class RunnerTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Mock
    private ReportWriter writer;
    private List<ReportWriter> writers;
    private File dir;

    @Mock
    private File reportFile;

    @Mock
    private PrintStream out;

    @Mock
    private AssertionTester assertionTester;

    @Before
    public void setUp() throws Exception{
        MockitoAnnotations.initMocks(this);
        dir = temp.newFolder();

        writers = new ArrayList<ReportWriter>();
        writers.add(writer);

        when(writer.write(any(ArrayList.class),any(File.class))).thenReturn(reportFile);

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Assertion a = (Assertion)args[0];

                if("/bad".equals(a.getUrl())) {
                    a.setResult(Result.FAIL);
                } else {
                    a.setResult(Result.PASS);
                }
                return null;
            }
        }).when(assertionTester).testAssertion(any(Assertion.class));
    }

    @Test
    public void assertionsFromCsvPassedToAssertionTest() {
        try {
            Runner runner = new Runner(getCsvReader("/url_tests.csv"),assertionTester,out,dir,writers);
            runner.run();
            verify(assertionTester, times(4)).testAssertion(any(Assertion.class));
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void assertionArePassedToWriterSorted() throws Exception {

        Runner runner = new Runner(getCsvReader("/url_tests.csv"),assertionTester,out,dir,writers);
        runner.run();

        ArgumentCaptor<ArrayList> arg = ArgumentCaptor.forClass(ArrayList.class);

        verify(writer).write(arg.capture(),any(File.class));
        ArrayList<Assertion> assertions = arg.getValue();
        assertEquals(4,assertions.size());
        assertEquals(Result.FAIL,assertions.get(0).getResult());
    }

    @Test
    public void runnerReturnResultCode2IfFailedAssertionExists() throws Exception {
        Runner runner = new Runner(getCsvReader("/url_tests.csv"),assertionTester,out,dir,writers);
        assertEquals(2,runner.run());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void runnerReturnsResultCode1OnWriterException() throws Exception {
        when(writer.write(any(ArrayList.class),any(File.class))).thenThrow(new RuntimeException("Foo"));
        Runner runner = new Runner(getCsvReader("/url_tests.csv"),assertionTester,out,dir,writers);
        assertEquals(1,runner.run());
        verify(out).println("Could not generate report. Error Foo");
    }

    private CSVReader getCsvReader(String resource) {
        return new CSVReader(new InputStreamReader(
            this.getClass().getResourceAsStream(resource)),'\t');
    }


}
