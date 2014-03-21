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

import au.com.bytecode.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * @author David Joly
 */
public class CsvReportWriter implements ReportWriter {

    public File write(List<Assertion> assertions,File dir) throws IOException {

        File reportFile = new File(dir.getAbsolutePath() + File.separator + "test_results.csv");

        String[] header = {"URL","Response Code","Location","Result","Message"};

        CSVWriter writer = new CSVWriter(new FileWriter(reportFile), '\t');
        writer.writeNext(header);

        for(Assertion a : assertions) {
            String[] row = {
                a.getUrl(),
                String.valueOf(a.getResponseCode()),
                a.getLocation(),
                a.getResult().toString(),
                a.getMessage()
            };
            writer.writeNext(row);
        }

        writer.close();
        return reportFile;
    }
}
