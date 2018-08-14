package org.okanatov.lexer;

import static org.junit.Assert.assertEquals;

import java.util.List;
import org.junit.Test;

/**
 * This class "unit"tests the Utils class.
 */
public class UtilsTest {
  @Test
  public void testSplitIfPatternNotInEnd() {
    final String text = "aabbaabbaa";
    final String pattern = "bb";

    List<String> result = Utils.split(pattern, text);
    assertEquals(5,    result.size());
    assertEquals("aa", result.get(0));
    assertEquals("bb", result.get(1));
    assertEquals("aa", result.get(2));
    assertEquals("bb", result.get(3));
    assertEquals("aa", result.get(4));
  }

  @Test
  public void testSplitIfPatternInEnd() {
    final String text = "aabbaabb";
    final String pattern = "bb";

    List<String> result = Utils.split(pattern, text);
    assertEquals(5,    result.size());
    assertEquals("aa", result.get(0));
    assertEquals("bb", result.get(1));
    assertEquals("aa", result.get(2));
    assertEquals("bb", result.get(3));
    assertEquals("",   result.get(4));
  }

  @Test
  public void testSplitIfPatternNotInEnd2() {
    final String text = "aabbaab";
    final String pattern = "bb";

    List<String> result = Utils.split(pattern, text);
    assertEquals(3,    result.size());
    assertEquals("aa", result.get(0));
    assertEquals("bb", result.get(1));
    assertEquals("aab", result.get(2));
  }

  @Test
  public void testSplitIfPatternInStart() {
    final String text = "bbaabb";
    final String pattern = "bb";

    List<String> result = Utils.split(pattern, text);
    assertEquals(5,    result.size());
    assertEquals("",   result.get(0));
    assertEquals("bb", result.get(1));
    assertEquals("aa", result.get(2));
    assertEquals("bb", result.get(3));
    assertEquals("",   result.get(4));
  }

  @Test
  public void testSplitIfPatternNotFound() {
    final String text = "ccaacc";
    final String pattern = "bb";

    List<String> result = Utils.split(pattern, text);
    assertEquals(1,        result.size());
    assertEquals("ccaacc", result.get(0));
  }
}
