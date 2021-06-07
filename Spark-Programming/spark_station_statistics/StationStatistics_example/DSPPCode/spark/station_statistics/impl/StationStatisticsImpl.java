package DSPPCode.spark.station_statistics.impl;

import DSPPCode.spark.station_statistics.question.StationStatistics;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;
import scala.Tuple3;
import java.util.Iterator;
import scala.collection.JavaConversions.*;
public class StationStatisticsImpl extends StationStatistics {


  public  JavaRDD<Tuple3<String, String, Integer>> stationStatistics(JavaRDD<String> lines){
    JavaPairRDD<String, Integer> ps = lines.mapToPair(
        new PairFunction<String, String, Integer>()  {
          @Override
          public Tuple2<String, Integer> call(String s) throws Exception {
            String[] strArray = null;
            strArray = s.split(" ");
            String[] times = null;
            times = strArray[2].split(":");
            String key = strArray[0] + " "+ strArray[1];
            Integer time = Integer.parseInt(times[0]) * 3600 + Integer.parseInt(times[1]) *60 + Integer.parseInt(times[2]);
            return new Tuple2<String,Integer>(strArray[0]+ " " + strArray[1], time);
          }
        });
    JavaPairRDD<String,Integer> result = ps.reduceByKey(new Function2<Integer, Integer, Integer>() {
      @Override
      public Integer call(Integer integer, Integer integer2) throws Exception {
        return Math.abs(integer - integer2);
      }
    });
    JavaRDD<Tuple3<String, String, Integer>> j = result.map(
        new Function<Tuple2<String, Integer>, Tuple3<String, String, Integer>>() {
          @Override
          public Tuple3<String, String, Integer> call(Tuple2<String, Integer> stringIntegerTuple2)
              throws Exception {
              String[] str = null;
              str = stringIntegerTuple2._1.split(" ");
              Tuple3 t = new Tuple3(str[0], str[1], stringIntegerTuple2._2);
              return t;
            }
        });
    return j;
  }

}
