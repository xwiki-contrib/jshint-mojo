package com.cj.jshintmojo.reporter;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Test cases of {@link JSLintReporter}.
 */
class JSLintReporterTest {

    private final JSLintReporter reporter = new JSLintReporter();

    @Test
    void formatType() {
        assertThat(JSLintReporter.FORMAT, is("jslint"));
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
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<jslint>\n" +
                "</jslint>\n"));
    }

    @Test
    void reportSingleFileFailedResults() {
        String report = this.reporter.report(
                JSHintReporterTestUtil.createSingleFileFailedResults());
        assertThat(report, is(
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<jslint>\n" +
                "\t<file name=\"/path/to/A.js\">\n" +
                "\t\t<issue line=\"5\" char=\"2\" reason=\"Missing semicolon.\" evidence=\"}\" severity=\"W\" />\n" +
                "\t\t<issue line=\"12\" char=\"5\" reason=\"Bad line breaking before &apos;&amp;&amp;&apos;.\" evidence=\"    &amp;&amp; window.setImmediate;\" severity=\"W\" />\n" +
                "\t\t<issue line=\"1137\" char=\"26\" reason=\"Too many errors.\" evidence=\"\" severity=\"E\" />\n" +
                "\t</file>\n" +
                "</jslint>\n"));
    }

    @Test
    void reportMultipleFilesFailedResults() {
        String report = this.reporter.report(
                JSHintReporterTestUtil.createMultipleFilesFailedResults());
        assertThat(report, is(
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<jslint>\n" +
                "\t<file name=\"/path/to/A.js\">\n" +
                "\t\t<issue line=\"5\" char=\"2\" reason=\"Missing semicolon.\" evidence=\"}\" severity=\"W\" />\n" +
                "\t\t<issue line=\"12\" char=\"5\" reason=\"Bad line breaking before &apos;&amp;&amp;&apos;.\" evidence=\"    &amp;&amp; window.setImmediate;\" severity=\"W\" />\n" +
                "\t\t<issue line=\"1137\" char=\"26\" reason=\"Too many errors.\" evidence=\"\" severity=\"E\" />\n" +
                "\t</file>\n" +
                "\t<file name=\"/path/to/B.js\">\n" +
                "\t\t<issue line=\"10\" char=\"5\" reason=\"Comma warnings can be turned off with &apos;laxcomma&apos;.\" evidence=\"    , [info, &quot;info&quot;]\" severity=\"I\" />\n" +
                "\t</file>\n" +
                "\t<file name=\"/path/to/C.js\">\n" +
                "\t\t<issue line=\"3\" char=\"14\" reason=\"&apos;args&apos; is already defined.\" evidence=\"    var args = a;\" severity=\"W\" />\n" +
                "\t\t<issue line=\"12\" char=\"5\" reason=\"Use &apos;===&apos; to compare with &apos;0&apos;.\" evidence=\"    if (list.length == 0)\" severity=\"W\" />\n" +
                "\t</file>\n" +
                "</jslint>\n"));
    }

}
