package com.cj.jshintmojo.jshint;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.cj.jshintmojo.Mojo;
import com.cj.jshintmojo.jshint.JSHint.Error;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class JSHintTest {
    public static class OutputMessagesVariant {
        protected String expectedEvalIsEvilMessage(){
            return "eval can be harmful.";
        }

        protected String expectedErrorMessageForTwoTooManyParameters(){
            return "This function has too many parameters. (2)";
        }

        protected String expectedLineTooLongMessage(){
            return "Line is too long.";
        }
    }

    private final String jsHintScript;
    private final OutputMessagesVariant variants = new OutputMessagesVariant();

    public JSHintTest() throws Exception
    {
        this.jsHintScript = Mojo.getJSHintScript(Mojo.getJSHintVersion());
    }

    @Test
    void booleanOptionsCanBeFalse(){
        // given
        final String globals = "";
        final String options = "evil:false";
        final InputStream code = toStream("eval('var x = 1 + 1;');");
        final JSHint jsHint = new JSHint(jsHintScript);

        // when
        List<JSHint.Error> errors = jsHint.run(code, options, globals);

        // then
        assertNotNull(errors);
        assertEquals(1, errors.size());
        assertEquals(variants.expectedEvalIsEvilMessage(), errors.get(0).reason);
    }

    @Test
    void booleanOptionsCanBeTrue(){
        // given
        final String globals = "";
        final String options = "evil:true";
        final InputStream code = toStream("eval('var x = 1 + 1;');");
        final JSHint jsHint = new JSHint(jsHintScript);

        // when
        List<JSHint.Error> errors = jsHint.run(code, options, globals);

        // then
        assertNotNull(errors);
        assertEquals(0, errors.size());
    }

    @Test
    void supportsOptionsThatTakeANumericValue(){
        // given
        final String globals = "alert";
        final String options = "maxlen:10";
        final InputStream code = toStream(" alert('Over Max Length');");
        final JSHint jsHint = new JSHint(jsHintScript);

        // when
        List<JSHint.Error> errors = jsHint.run(code, options, globals);

        // then
        assertNotNull(errors);
        assertEquals(1, errors.size());
        assertEquals(variants.expectedLineTooLongMessage(), errors.get(0).reason);
    }

    @Test
    void supportsParametersWithValues(){
        // given
        final String globals = "";
        final String options = "maxparams:1";
        final InputStream code = toStream("function cowboyFunction(param1, param2){return 'yee-haw!';}");
        final JSHint jsHint = new JSHint(jsHintScript);

        // when
        List<JSHint.Error> errors = jsHint.run(code, options, globals);

        // then
        assertNotNull(errors);
        assertEquals(1, errors.size());
        assertEquals(variants.expectedErrorMessageForTwoTooManyParameters(), errors.get(0).reason);
    }

    @Test
    void supportsParametersWithoutValues(){
        // given
        final String globals = "Foo";
        final String options = "nonew";
        final InputStream code = toStream("new Foo();");
        final JSHint jsHint = new JSHint(jsHintScript);

        // when
        List<JSHint.Error> errors = jsHint.run(code, options, globals);

        // then
        assertNotNull(errors);
        assertEquals(1, errors.size());
        assertEquals("Do not use 'new' for side effects.", errors.get(0).raw);
    }

    @Test
    void supportsTheGlobalsParameter(){
        // given
        final String globals = "someGlobal";
        final String options = "undef";
        final InputStream code = toStream("(function(){var value = someGlobal();}());");
        final JSHint jsHint = new JSHint(jsHintScript);

        // when
        List<JSHint.Error> errors = jsHint.run(code, options, globals);

        // then
        assertNotNull(errors);
        assertEquals(0, errors.size(), "Expected no errors, but received:\n " + toString(errors));
    }

    private static InputStream toStream(String text){
        return new ByteArrayInputStream(text.getBytes());
    }

    private static String toString(List<Error> errors) {
        StringBuilder text = new StringBuilder ();
        for (Error error: errors){
            text.append (error.reason).append ("\n");
        }
        return text.toString();
    }
}
