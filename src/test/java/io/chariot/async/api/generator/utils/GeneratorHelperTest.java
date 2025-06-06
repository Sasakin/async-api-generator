package io.chariot.async.api.generator.utils;

import org.junit.jupiter.api.Test;

import static org.gradle.internal.impldep.org.testng.AssertJUnit.assertEquals;

public class GeneratorHelperTest {

    @Test
    public void dehyphenizeClassNameTest() {
        var input = "className";
        var expected = "className";
        var actual = GeneratorHelper.dehyphenizeClassName(input);
        assertEquals(actual, expected);

        input = "class-Name";
        expected = "className";
        actual = GeneratorHelper.dehyphenizeClassName(input);
        assertEquals(actual, expected);

        input = "class-ext";
        expected = "classExt";
        actual = GeneratorHelper.dehyphenizeClassName(input);
        assertEquals(actual, expected);
    }
}
