package org.okanatov.lexer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
  public static List<String> split(String pattern, String text) {
    List<String> result = new ArrayList<String>();

    Pattern p = Pattern.compile(pattern);
    Matcher m = p.matcher(text);

    int last_match = 0;

    while (m.find()) {
      result.add(text.substring(last_match, m.start()));
      result.add(text.substring(m.start(), m.end()));
      last_match = m.end();
    }

    result.add(text.substring(last_match, text.length()));

    return result;
  }
}
