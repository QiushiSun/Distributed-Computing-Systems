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
import shapeless.Tuple;

public class StationStatisticsImpl extends StationStatistics {

  // spark程序需要做的第一件事情，就是创建一个SparkContext对象，它将告诉spark如何访问一个集群，
  // 而要创建一个SparkContext对象，你首先要创建一个SparkConf对象，该对象访问了你的应用程序的信息

  public  JavaRDD<Tuple3<String, String, Integer>> stationStatistics(JavaRDD<String> lines){
    JavaPairRDD<String,Integer> ps = lines.mapToPair(
        new PairFunction<String, String, Integer>() {
          @Override
          public Tuple2<String, Integer> call(String s) throws Exception {
            String[] str = null;
            str = s.split(" ");
            String[] str_time = str[2].split(":");
            Integer time = Integer.parseInt(str_time[0])*3600+Integer.parseInt(str_time[1])*60+Integer.parseInt(str_time[2]);
            return new Tuple2<String,Integer>(str[0]+" "+str[1],time);
          }
        }
    );

    JavaPairRDD<String,Integer> result = ps.reduceByKey(new Function2<Integer, Integer, Integer>() {
      @Override
      public Integer call(Integer v1, Integer v2) throws Exception {
        return Math.abs(v1-v2);
      }
    });

    JavaRDD<Tuple3<String,String,Integer>> j = result.map(
        new Function<Tuple2<String, Integer>, Tuple3<String, String, Integer>>() {
          @Override
          public Tuple3<String, String, Integer> call(Tuple2<String, Integer> v1) throws Exception {
            String[] str = null;
            str = v1._1.split(" ");
            return new Tuple3<>(str[0],str[1], v1._2);
          }
        }
    );

    return j;
  }

}
