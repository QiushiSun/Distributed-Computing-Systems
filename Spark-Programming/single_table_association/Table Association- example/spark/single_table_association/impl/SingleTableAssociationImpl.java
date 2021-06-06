package DSPPCode.spark.single_table_association.impl;

import DSPPCode.spark.single_table_association.question.SingleTableAssociation;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SingleTableAssociationImpl extends SingleTableAssociation {

  @Override
  public JavaRDD<Tuple2<String, String>> singleTableAssociation(JavaRDD<String> lines) {

    //读入子女表 父母-孩子
    JavaPairRDD<String, String> childparentRDD1 =
        lines.mapToPair(
            new PairFunction<String, String, String>() {
              @Override
              public Tuple2<String, String> call(String s) throws Exception {
                String[] tokens = s.split(" ");
                return new Tuple2<String, String>(tokens[1], tokens[0]);
              }
            });

    //读入子女表 孩子-父母
    JavaPairRDD<String, String> childparentRDD2 =
        lines.mapToPair(
            new PairFunction<String, String, String>() {
              @Override
              public Tuple2<String, String> call(String s) throws Exception {
                String[] tokens = s.split(" ");
                return new Tuple2<String, String>(tokens[0], tokens[1]);
              }
            });

    JavaRDD<Tuple2<String,String>> tmp =
    childparentRDD1
        .cogroup(childparentRDD2)
        .flatMap(
            new FlatMapFunction<
                Tuple2<String, Tuple2<Iterable<String>, Iterable<String>>>, Tuple2<String, String>>() {
              @Override
              public Iterator<Tuple2<String, String>> call(
                  Tuple2<String, Tuple2<Iterable<String>, Iterable<String>>> stringTuple2Tuple2)
                  throws Exception {
                    List<Tuple2<String, String>> list = new ArrayList<>();
                    for (String t1 : stringTuple2Tuple2._2._1) {
                      for (String t2 : stringTuple2Tuple2._2._2) {
                        if (!t1.equals(t2)) {
                          Tuple2<String, String> tmp = new Tuple2<String, String>(t1, t2);
                          list.add(tmp);
                        }
                      }
                    }
                return list.iterator();
              }
            });
    return tmp;
  }
}
