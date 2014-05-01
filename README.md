## HTTPTESTER

HTTP Tester is a simple utility intended for the regression testing of URLs. Currently, only GET requests are supported.
future versions may add support for additional requests.

### Basic usage

The httptest tool runs on the shell (either linux or Windows). It's built on java, obviously, so you need java installed to run it. Invoking the help command will display the options available.

```
$ ./httptest --help
usage: httptest [-h] -t TESTS -u URL [-d DIR] [-r {html,csv}]

Run a suite of tests against a web server.

optional arguments:
  -h, --help             show this help message and exit
  -t TESTS, --tests TESTS
                         Specify the csv file containing the test suite.
  -u URL, --url URL      The URL of the web server being tested against.
  -d DIR, --dir DIR      The directory the test reports should be written. 
                         (default: /path/to/present/working/directory)
  -r {html,csv}, --report {html,csv}
                         Specify a list of reports that should be written 
                         (separated by space). (default: html)

```

#### -t | --tests

The tests argument is the path to a CSV file containing URL tests/assertions

```
./httptest -t /path/to/tests.csv
```

#### -u | --url

Currently, this tool works only on a single base URL. Specify the URL here (sans the forward slash at the end).

#### -r | --report

By default, a simple HTML result file will be generated. Optionally, you can specify the report be in CSV format, or generate both.

```
$ ./httptest -r csv html
```

#### -d | --dir

If you want to dump the test results into a directory other than the present working directory, specify the directory here.

```
$ ./httptest -u http://www.example.com
```

All together now:

```
$ ./httptest -u http://www.example.com -d /home/me/testreports -r csv html -t /home/me/tests.csv
```

### The CSV Tests File

The CSV file is actually tab-delimited. There's not an option at this time to override the delimiter. Each row in the CSV file is considered a single test, or assertion. Each row must have, at minimum, two columns. The first is the URL path/query and the second is the expected response code. Optionally, a third column can be used to specify the Location header value. This is useful for testing 301 and 302 redirects. Below is a simple example (notice, no header!):

```
/foo	403
/bar	301	/baz
/bat	200
```

And that's it. 

### Get it!

To run httptest, you need to have java installed on the machine you wish to run it, whether it's a build server or a QA tester's laptop. To build the application you will need both [gradle](http://www.gradle.org/) and java installed. With gradle and java installed:

```
git clone https://github.com/djoly/httptest.git
cd httptest
gradle distTar
```

The distTar task will package everything up into a distribution tarball in the ./build/distributions directory. If you prefer zip files, use the distZip task instead.

### Want more features?

Currently, httptest will meet your needs if you only need to test GET requests and only care about the response code and location header. If you need more robust testing, drop me note and let me know. Alternatively, feel free to fork this project and make modifications to it.
