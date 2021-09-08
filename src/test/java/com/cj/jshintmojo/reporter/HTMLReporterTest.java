package com.cj.jshintmojo.reporter;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Test cases of {@link HTMLReporter}.
 */
class HTMLReporterTest {

    private final HTMLReporter reporter = new HTMLReporter();

    @Test
    void formatType() {
        assertThat(HTMLReporter.FORMAT, is("html"));
    }

    @Test
    void reportNullResults() {
        String report = this.reporter.report(null);
        assertThat(report, is(""));
    }

    @Test
    void reportAllPassedResults() {
        String report = this.reporter.report(
                JSHintReporterTestUtil.createAllPassedResults());
        assertThat(report, is(
        		"<!DOCTYPE HTML>\n<html>\n</html>\n"));
    }

    @Test
    void reportSingleFileFailedResults() {
        String report = this.reporter.report(
                JSHintReporterTestUtil.createSingleFileFailedResults());
        assertThat(report, is(
        		"<!DOCTYPE HTML>\n<html>\n<h2>/path/to/A.js</h2>\n" + 
        		"\t\t<div style=\"background-color:#2956B2;color:white;padding:4px\">" +
        		"<span style=\"padding-right:40px;padding-left:4px;\">line:5 char:2</span>" +
        		"<span style=\"font-weight:bold;padding-right:50px;\">Missing semicolon.</span></div>" +
        		"<div style=\"margin-left:20px;margin-bottom:1em;font-size:11pt;font-family:consolas;\"><p>}</p></div>" +
        		"\t\t<div style=\"background-color:#2956B2;color:white;padding:4px\">" +
        		"<span style=\"padding-right:40px;padding-left:4px;\">line:12 char:5</span>" +
        		"<span style=\"font-weight:bold;padding-right:50px;\">Bad line breaking before &apos;&amp;&amp;&apos;.</span></div>" +
        		"<div style=\"margin-left:20px;margin-bottom:1em;font-size:11pt;font-family:consolas;\"><p>    &amp;&amp; window.setImmediate;</p></div>" +
        		"\t\t<div style=\"background-color:#2956B2;color:white;padding:4px\">" +
        		"<span style=\"padding-right:40px;padding-left:4px;\">line:1137 char:26</span>" +
        		"<span style=\"font-weight:bold;padding-right:50px;\">Too many errors.</span></div>" +
        		"<div style=\"margin-left:20px;margin-bottom:1em;font-size:11pt;font-family:consolas;\"><p></p></div></html>\n"));
    }

    @Test
    void reportMultipleFilesFailedResults() {
        String report = this.reporter.report(
                JSHintReporterTestUtil.createMultipleFilesFailedResults());
        assertThat(report, is(
        		"<!DOCTYPE HTML>\n<html>\n"
        		+ "<h2>/path/to/A.js</h2>\n"
        		+ "\t\t<div style=\"background-color:#2956B2;color:white;padding:4px\">"
        		+ "<span style=\"padding-right:40px;padding-left:4px;\">line:5 char:2</span>"
        		+ "<span style=\"font-weight:bold;padding-right:50px;\">Missing semicolon.</span></div>"
        		+ "<div style=\"margin-left:20px;margin-bottom:1em;font-size:11pt;font-family:consolas;\"><p>}</p></div>"
        		+ "\t\t<div style=\"background-color:#2956B2;color:white;padding:4px\">"
        		+ "<span style=\"padding-right:40px;padding-left:4px;\">line:12 char:5</span>"
        		+ "<span style=\"font-weight:bold;padding-right:50px;\">Bad line breaking before &apos;&amp;&amp;&apos;.</span></div>"
        		+ "<div style=\"margin-left:20px;margin-bottom:1em;font-size:11pt;font-family:consolas;\"><p>    &amp;&amp; window.setImmediate;</p></div>"
        		+ "\t\t<div style=\"background-color:#2956B2;color:white;padding:4px\">"
        		+ "<span style=\"padding-right:40px;padding-left:4px;\">line:1137 char:26</span>"
        		+ "<span style=\"font-weight:bold;padding-right:50px;\">Too many errors.</span></div>"
        		+ "<div style=\"margin-left:20px;margin-bottom:1em;font-size:11pt;font-family:consolas;\"><p></p></div>"
        		+ "<h2>/path/to/B.js</h2>\n"
        		+ "\t\t<div style=\"background-color:#2956B2;color:white;padding:4px\">"
        		+ "<span style=\"padding-right:40px;padding-left:4px;\">line:10 char:5</span>"
        		+ "<span style=\"font-weight:bold;padding-right:50px;\">Comma warnings can be turned off with &apos;laxcomma&apos;.</span></div>"
        		+ "<div style=\"margin-left:20px;margin-bottom:1em;font-size:11pt;font-family:consolas;\"><p>    , [info, &quot;info&quot;]</p></div>"
        		+ "<h2>/path/to/C.js</h2>"
        		+ "\n\t\t<div style=\"background-color:#2956B2;color:white;padding:4px\">"
        		+ "<span style=\"padding-right:40px;padding-left:4px;\">line:3 char:14</span>"
        		+ "<span style=\"font-weight:bold;padding-right:50px;\">&apos;args&apos; is already defined.</span></div>"
        		+ "<div style=\"margin-left:20px;margin-bottom:1em;font-size:11pt;font-family:consolas;\"><p>    var args = a;</p></div>"
        		+ "\t\t<div style=\"background-color:#2956B2;color:white;padding:4px\">"
        		+ "<span style=\"padding-right:40px;padding-left:4px;\">line:12 char:5</span>"
        		+ "<span style=\"font-weight:bold;padding-right:50px;\">Use &apos;===&apos; to compare with &apos;0&apos;.</span></div>"
        		+ "<div style=\"margin-left:20px;margin-bottom:1em;font-size:11pt;font-family:consolas;\"><p>    if (list.length == 0)</p></div></html>\n"));
    }

}
