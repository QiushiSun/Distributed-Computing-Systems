package DSPPCode.spark.topk_power.impl;
import DSPPCode.spark.topk_power.question.*;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.PairFunction;
import org.codehaus.janino.Java;
import scala.Tuple2;
import shapeless.Tuple;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
public class TopKPowerImpl extends TopKPower{

  @Override
  public int topKPower(JavaRDD<String> lines) {
    JavaPairRDD<String,Integer> words = lines.flatMap(line -> Arrays.asList(line.split(" ")).iterator())
        .mapToPair(word -> new Tuple2<>(word,1))
        .reduceByKey(Integer::sum);

    JavaPairRDD<Integer,String> swap = words.mapToPair(
        tuple2 -> new Tuple2<>(tuple2._2*tuple2._2,tuple2._1))
        .sortByKey(false);

    List<Tuple2<Integer,String>> ans = swap.take(5);
    Integer ans_val = 0;
    for (Tuple2<Integer,String>tuple2:ans){
      ans_val += tuple2._1;
    }
    return ans_val;
  }
}



// public class TopKPowerImpl extends TopKPower{
//   @Override
//   public int topKPower(JavaRDD<String> lines) {
//     // 计算每个数字出现次数，得到的数字转化为 <数字，出现次数>
//     JavaPairRDD<String, Integer> words = lines
//         .flatMap(line -> Arrays.asList(line.split(" ")).iterator())
//         // asStream/asList
//         .mapToPair(word -> new Tuple2<>(word,1))
//         .reduceByKey(Integer::sum); // words:[id,次数]
//
//     JavaPairRDD<Integer,String> countPower = words.mapToPair(
//         tuple2 -> new Tuple2<>(tuple2._2()* tuple2._2(), tuple2._1()))
//         .sortByKey(false);
//
//     List<Tuple2<Integer ,String>> top5 = countPower.take(5);
//
//     Integer ans = 0;
//     for (Tuple2<Integer,String>tuple2:top5){
//       ans+=tuple2._1();
//     }
//     return ans;
//   }
// }
