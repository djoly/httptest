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

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @author David Joly
 */
public class HtmlReportWriter implements ReportWriter {

    public File write(List<Assertion> assertions,File dir) throws IOException {

        int errorCount = 0;
        int passCount = 0;
        int failCount = 0;

        String lineHtml = Util.readResourceToString("/report_line.html");
        StringBuilder lines = new StringBuilder();

        for(Assertion a : assertions) {
            switch(a.getResult()) {
                case PASS: passCount++; break;
                case FAIL: failCount++; break;
                case ERROR: errorCount++; break;
            }

            lines.append(
                String.format(
                    lineHtml,
                    a.getResult(),
                    a.getUrl(),
                    a.getResponseCode(),
                    a.getLocation(),
                    a.getResult(),
                    a.getMessage()
                ));
        }

        String reportHtml = Util.readResourceToString("/report.html");
        String output  = String.format(
                reportHtml, assertions.size(),
                passCount, failCount, errorCount, lines.toString());

        File reportFile = new File(dir.getAbsolutePath() + File.separator + "test_results.html");
        FileUtils.writeStringToFile(reportFile, output, Charset.defaultCharset());
        return reportFile;
    }
}
