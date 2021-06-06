package DSPPCode.flink.digital_conversion.impl;

import DSPPCode.flink.digital_conversion.question.DigitalConversion;
import DSPPCode.flink.digital_conversion.question.DigitalWord;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple1;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.util.Collector;
import javax.xml.crypto.Data;

public class DigitalConversionImpl extends DigitalConversion{

  @Override
  public DataStream<String> digitalConversion(DataStream<Tuple1<String>> digitals) {
    // 过滤掉小数
    DataStream<Tuple1<String>> integer = digitals.filter((FilterFunction<Tuple1<String>>)
        value -> value.getField(0)
            .toString().matches("[0-9]+"))
        .returns(Types.TUPLE(Types.STRING));

    // 将整数转换为对应的单词
    DataStream<String> word = integer.map((MapFunction<Tuple1<String>,String>)
        value -> {
      // 解析出整数
      int index = Integer.parseInt(value.getField(0));
      // 通过枚举类获得整数对应的英文单词
      //     System.out.print("here\n");
      //     System.out.print(DigitalWord.values()[index]);
          // DigitalWord.values() 以数组的形式返回枚举类型所有成员
      return DigitalWord.values()[index].getWord();
        }).returns(Types.STRING);

    // integer.print();

    return word;
  }
}


