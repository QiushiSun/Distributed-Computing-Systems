package DSPPCode.flink.grade_point.impl;
import DSPPCode.flink.grade_point.question.*;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.util.Collector;
import javax.xml.crypto.Data;
import java.math.BigDecimal;

public class GradePointImpl extends GradePoint{

  @Override
  public DataStream<Tuple3<String, Integer, Float>> calculate(DataStream<String> text) {
    // 将成绩信息映射为 [stu id,学分,成绩]
    DataStream<Tuple3<String,Integer,Float>> points = text.map(
        new MapFunction<String, Tuple3<String, Integer, Float>>() {
          @Override
          public Tuple3<String, Integer, Float> map(String value) throws Exception {
            String[] infos = value.split(" ");
            float point = Float.parseFloat(infos[2]);
            if (point >= 0 && point<=5){
              return new Tuple3<>(infos[0],Integer.parseInt(infos[1]),point);
            }
            // 若成绩为-1，表示成绩异常，计为0分
            return new Tuple3<>(infos[0],Integer.parseInt(infos[1]),point+1);
          }
        });

    DataStream<Tuple3<String,Integer,Float>> pointGroupByKey = points
        .keyBy(0)
        .reduce(new ReduceFunction<Tuple3<String, Integer, Float>>() {
          @Override
          public Tuple3<String, Integer, Float> reduce(Tuple3<String, Integer, Float> preRecord,
              Tuple3<String, Integer, Float> currRecord) throws Exception {
            float temp_pre_course = preRecord.f1*preRecord.f2;
            float temp_cur_course = currRecord.f1*currRecord.f2;
            float raw_grade_point = (temp_pre_course+temp_cur_course)/(preRecord.f1+ currRecord.f1);
            return Tuple3.of(preRecord.f0,preRecord.f1+currRecord.f1, (float)(Math.round(raw_grade_point*10))/10
                // 先Math.round四舍五入掉小数点后第二位，然后再/10变成绩点
            );
          }
        });
    // pointGroupByKey.print();

    DataStream<Tuple3<String,Integer,Float>> result = pointGroupByKey
        .filter(value -> value.f1 == 10);
    // result.print();
    return result;
  }
}
