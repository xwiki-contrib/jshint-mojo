package com.cj.jshintmojo.jshint;

import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;

import com.cj.jshintmojo.util.Rhino;

public class JSHint {

    private final Rhino rhino;

    public JSHint (String jshintCode) {

        rhino = new Rhino ();
        try {
            rhino.eval (
                    "print=function(){};" +
                            "quit=function(){};" +
                            "arguments=[];");

            rhino.eval(adaptForRhino(commentOutTheShebang(resourceAsString(jshintCode))));
        } catch (EcmaError e) {
            throw new RuntimeException ("Javascript eval error:" + e.getScriptStackTrace (), e);
        }
    }

    /**
     * Fix the JSHint code to work with Rhino. The following issues are addressed:
     * <ul>
     * <li><a href="https://github.com/jshint/jshint/issues/2308">Rhino fails on jshint@2.7.0 #2308</a></li>
     * </ul>
     * 
     * @param jsHintCode the JSHint code
     * @return the modified JSHint code that works with Rhino
     */
    private String adaptForRhino(String jsHintCode)
    {
        // Apply the fix suggested in https://github.com/segrey/jshint/commit/1824d740c2d6656e663de43cf6d95fc8a642c796
        return jsHintCode.replace(" window = {};", " global = this;");
    }

    private String commentOutTheShebang (String code) {
        String minusShebang = code.startsWith ("#!") ? "//" + code : code;
        return minusShebang;
    }

    public List<Error> run (InputStream source, String options, String globals) {
        final List<Error> results = new ArrayList<JSHint.Error> ();

        String sourceAsText = toString (source);

        NativeObject nativeOptions = toJsObject (options);
        NativeObject nativeGlobals = toJsObject (globals);

        Boolean codePassesMuster = rhino.call ("JSHINT", sourceAsText, nativeOptions, nativeGlobals);

        if (!codePassesMuster) {
            NativeArray errors = rhino.eval ("JSHINT.errors");

            for (Object next : errors) {
                if (next != null) { // sometimes it seems that the last error in the list is null
                    Error error = new Error (new JSObject (next));
                    results.add (error);
                }
            }
        }

        return results;
    }

    private NativeObject toJsObject (String options) {
        NativeObject nativeOptions = new NativeObject ();
        for (final String nextOption : options.split (",")) {
            final String option = nextOption.trim ();
            if (!option.isEmpty ()) {
                final String name;
                final Object value;

                final int valueDelimiter = option.indexOf (':');
                if (valueDelimiter == -1) {
                    name = option;
                    value = Boolean.TRUE;
                } else {
                    name = option.substring (0, valueDelimiter);
                    String rest = option.substring (valueDelimiter + 1).trim ();
                    if (rest.matches ("[0-9]+")) {
                        value = Integer.parseInt (rest);
                    } else if (rest.equals ("true")) {
                        value = Boolean.TRUE;
                    } else if (rest.equals ("false")) {
                        value = Boolean.FALSE;
                    } else {
                        value = rest;
                    }
                }
                nativeOptions.defineProperty (name, value, NativeObject.READONLY);
            }
        }
        return nativeOptions;
    }

    private static String toString (InputStream in) {
        try {
            return IOUtils.toString (in, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException (e);
        }
    }

    private String resourceAsString (String name) {
        return toString (getClass ().getResourceAsStream (name));
    }

    @SuppressWarnings ("unchecked")
    static class JSObject {
        private NativeObject a;

        public JSObject (Object o) {
            if (o == null) throw new NullPointerException ();
            this.a = (NativeObject) o;
        }

        public <T> T dot (String name) {
            return (T) a.get (name);
        }
    }

    @SuppressWarnings ("serial")
    public static class Error implements Serializable {
        public String id, code, raw, evidence, reason;
        public Number line, character;

        public Error (JSObject o) {
            id = nullSafeToString (o, "id");
            code = nullSafeToString (o, "code");
            raw = nullSafeToString (o, "raw");
            evidence = nullSafeToString (o, "evidence");
            line = o.dot ("line");
            character = o.dot ("character");
            reason = nullSafeToString (o, "reason");
        }

        private String nullSafeToString (JSObject o, String name) {
            return o.dot (name) != null ? o.dot (name).toString () : "";
        }

        // NOTE: for Unit Testing purpose.
        public Error () {
        }
    }
}
