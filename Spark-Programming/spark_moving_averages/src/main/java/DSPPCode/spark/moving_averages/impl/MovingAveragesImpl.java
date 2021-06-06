package DSPPCode.spark.moving_averages.impl;

import DSPPCode.spark.moving_averages.question.MovingAverages;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.codehaus.janino.Java;
import scala.Tuple2;
import shapeless.Tuple;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MovingAveragesImpl extends MovingAverages{
  @Override
  public JavaRDD<String> movingAverages(JavaRDD<String> lines) {
    long n = lines.count();
    JavaPairRDD<Integer,Integer> posAndValue =
        lines.flatMapToPair(
            (PairFlatMapFunction<String,Integer,Integer>) line -> {
              ArrayList<Tuple2<Integer,Integer>> posValue = new ArrayList<>();
              String[] posAndValue1 =line.substring(1,line.length()-1).split(",");
              int pos = Integer.parseInt(posAndValue1[0]);
              int value = Integer.parseInt(posAndValue1[1]);
              posValue.add(new Tuple2<>(pos,value));
              for (int k=1;k<=2;k++){
                int left = pos-k;
                int right = pos+k;
                if (left>=1){
                  posValue.add(new Tuple2<>(left,value));
                }
                if (right<=n){ // 切勿使用right<=lines.count()
                  posValue.add(new Tuple2<>(right,value));
                }
              }
              return posValue.iterator();
            });

    JavaPairRDD<Integer,Iterable<Integer>> posIntermediateResult = posAndValue.groupByKey();

    JavaRDD<String> timeSeries =
        posIntermediateResult.map((Function<Tuple2<Integer, Iterable<Integer>>, String>) posTuple->{
          int sum = 0;
          int num = 0;
          for (Integer value:posTuple._2){
            sum+=value;
            num++;
          }
          int avg = sum/num;
          return "["+posTuple._1+","+avg+"]";
        });

    return timeSeries;
  }
}