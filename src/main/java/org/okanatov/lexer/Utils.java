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

    int ppos = 0;

    while (m.find()) {
      if (m.start() != ppos) {
        result.add(text.substring(ppos, m.start()));
      }

      result.add(text.substring(m.start(), m.end()));
      ppos = m.end();
    }

    if (ppos != text.length()) {
      result.add(text.substring(ppos, text.length()));
    }

    return result;
  }
}
