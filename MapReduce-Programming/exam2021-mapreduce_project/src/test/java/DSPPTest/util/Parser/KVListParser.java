package DSPPTest.util.Parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Parser of (Key, [Value List]) pairs
 */
public class KVListParser {

  // delimiter in a record
  protected String delimiter = ",";

  // delimiter between records
  protected String recordDelimiter = "\n|\r\n";

  public KVListParser() {
  }

  public KVListParser(String delimiter) {
    this.delimiter = delimiter;
  }

  public KVListParser(String delimiter, String recordDelimiter) {
    this.delimiter = delimiter;
    this.recordDelimiter = recordDelimiter;
  }

  /**
   * Parse a string to a map of (Key, Sorted Value) pairs
   *
   * @param str the string need to be parsed
   * @return a map of (Key, Sorted Value) pairs
   * @throws Exception a duplicated key occurs
   */
  public Map<String, String> parse(String str) throws Exception {
    Map<String, String> ret = new HashMap<>();
    String[] records = str.split(recordDelimiter);
    for (String record : records) {
      // 删除字符串中的'('、')'、'['、']'和空格
      record = record.replaceAll("[\\\\(.*|\\\\).*|\\[.*|\\].*| ]","");
      String[] kv = record.split(delimiter);
      if (ret.containsKey(kv[0])) {
        throw new Exception("Duplicated key: " + kv[0]);
      }
      // 将所有的Value值存入valueList，并排序
      List<String> valueList = new ArrayList<>();
      for (int i = 1; i < kv.length; i++) {
        valueList.add(kv[i]);
      }
      Collections.sort(valueList);

      // 将排序好的valueList中所有的值拼接为一个字符串
      StringBuilder valueListString = new StringBuilder();
      for (String s : valueList) {
        valueListString.append(s);
      }

      ret.put(kv[0], valueListString.toString());
    }
    return ret;
  }

}
