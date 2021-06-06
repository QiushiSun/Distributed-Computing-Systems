
package DSPPCode.spark.single_table_association.impl;
import DSPPCode.spark.single_table_association.question.SingleTableAssociation;

// load java utils

import java.util.ArrayList;
import java.util.Iterator; // 引入Iterator类
import java.util.List;

// load spark related modules
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;

import scala.Tuple2; // load tuple2
import shapeless.Tuple;

public class SingleTableAssociationImpl extends SingleTableAssociation {
  private static final String FLAG = "child";

  @Override
  public JavaRDD<Tuple2<String, String>> singleTableAssociation(JavaRDD<String> lines) {

    JavaRDD<String> association = lines.filter(new Function<String, Boolean>() {
      @Override
      public Boolean call(String v1) throws Exception {
        if (v1.contains(FLAG)){
          return false;
        } // 这段不加也是可以的，其实就是用来过滤掉表头
        else{
          return true ;
        }
      }
    });

    JavaPairRDD<String,String> childParentRdd = association.mapToPair(
        new PairFunction<String, String, String>() {
          @Override
          public Tuple2<String, String> call(String s) throws Exception {
            String[] tokens = s.split(" ");
            String chd = tokens[0];
            String prt = tokens[1];
            return new Tuple2<>(chd,prt);
          }
        });

    JavaPairRDD<String,String> parentChildRdd = association.mapToPair(
        new PairFunction<String, String, String>() {
          @Override
          public Tuple2<String, String> call(String s) throws Exception {
            String[] tokens = s.split(" ");
            String chd = tokens[0];
            String prt = tokens[1];
            return new Tuple2<>(prt,chd);
          }
        });
    // remark : 注意join操作 (a1,a2)join(a1,b2)=(a1,(a2,b2))，比较的是value1是否相等


    JavaPairRDD<String, Tuple2<String,String>> result = parentChildRdd.join(childParentRdd);

    return result.values();
  }
}
