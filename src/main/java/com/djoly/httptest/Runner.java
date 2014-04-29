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
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author David Joly
 */
public class Runner {

    private File reportDir;
    private List<ReportWriter> writers;
    private PrintStream out;
    private CSVReader csvReader;
    private AssertionTester assertionTester;

    public Runner(CSVReader csvReader, AssertionTester assertionTester, PrintStream out, File reportDir, List<ReportWriter> writers) {
        this.reportDir = reportDir;
        this.writers = writers;
        this.out = out;
        this.csvReader = csvReader;
        this.assertionTester = assertionTester;
    }

    public int run() throws Exception {

        List<Assertion> assertions = new ArrayList<Assertion>();

        int rowNum = 0;
        boolean hasFailure = false;
        List<String[]> rows = csvReader.readAll();

        for(String[] row : rows) {
            rowNum++;

            if(row.length < 2) {
                out.println(String.format("Assertion on row %s is not formatted properly. Two or three cells expected.",rowNum + 1));
            } else {
                String location = StringUtils.EMPTY;
                if(row.length > 2) {
                    location = row[2];
                }
                Assertion assertion = new Assertion(row[0],Integer.parseInt(row[1]),location);

                try {
                    assertionTester.testAssertion(assertion);
                } catch(Exception e) {
                    out.println(String.format("Error testing assertion on line %s: %s",rowNum,e.getMessage()));
                    assertion.setMessage(e.getMessage());
                    assertion.setResult(Result.ERROR);
                }
                if(!Result.PASS.equals(assertion.getResult())) {
                    hasFailure = true;
                }
                assertions.add(assertion);
            }
        }

        Collections.sort(assertions, new AssertionComparator());

        for(ReportWriter writer : writers) {
            try {
                File report = writer.write(assertions,reportDir);
                out.println(String.format("Report written to %s", report.getAbsolutePath()));
            } catch(Exception e) {
                out.println(String.format("Could not generate report. Error %s",e.getMessage()));
                return 1;
            }
        }

        return hasFailure ? 2 : 0;
    }

    /**
     * Process command-line arguments and configure Runner instance.
     *
     * @param args
     */
    public static void main(String args[]) {
        ArgumentParser parser = ArgumentParsers.newArgumentParser("httptest")
                .defaultHelp(true)
                .description("Run a suite of tests against a web server.");

        parser.addArgument("-t", "--tests")
                .required(true)
                .help("Specify the csv file containing the test suite.");

        parser.addArgument("-u", "--url")
                .required(true)
                .help("The URL of the web server being tested against.");

        parser.addArgument("-d","--dir")
                .setDefault(System.getProperty("user.dir"))
                .help("The directory the test reports should be written.");

        List<String> defaultReports = new ArrayList<String>();
        defaultReports.add("html");

        parser.addArgument("-r","--report")
                .choices("html", "csv")
                .action(Arguments.append())
                .type(String.class)
                .setDefault(defaultReports)
                .help("Specify a list of reports that should be written (separated by space).");

        Namespace ns = null;

        try {
            ns = parser.parseArgs(args);
            String serverUrl = (String) ns.get("url");
            List<String> reports = (List<String>) ns.get("report");
            File reportDir = new File((String) ns.get("dir"));
            File testsFile = new File((String) ns.get("tests"));

            if(!reportDir.isDirectory()) {
                System.out.println("Specified report directory is not a directory.");
                System.exit(1);
            }

            if(!reportDir.canWrite()) {
                System.out.println(String.format("Report directory %s is not writable.", reportDir.getPath()));
                System.exit(1);
            }

            if(!testsFile.isFile()) {
                System.out.println(String.format("Tests file %s not found.",testsFile.getPath()));
                System.exit(1);
            }

            if(!testsFile.canRead()) {
                System.out.println("Cannot read tests file.");
                System.exit(1);
            }

            List<ReportWriter> writersList = new ArrayList<ReportWriter>();
            if(reports.contains("csv")) {
                writersList.add(new CsvReportWriter());
            }

            if(reports.contains("html")) {
                writersList.add(new HtmlReportWriter());
            }

            AssertionTester assertionTester = new AssertionTester(new HttpClient(),serverUrl);
            CSVReader csvReader = new CSVReader(new FileReader(testsFile),'\t');
            Runner runner = new Runner(csvReader,assertionTester,System.out,reportDir,writersList);
            int res = runner.run();
            System.exit(res);

        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
}
