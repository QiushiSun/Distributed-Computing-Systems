package DSPPCode.spark.station_statistics.question;

import java.io.Serializable;
// 简单说就是为了保存在内存中的各种对象的状态，并且可以把保存的对象状态再读出来。虽然你可以用你自己的各种各样的方法来保存Object States，
// 但是Java给你提供一种应该比你自己好的保存对象状态的机制,那就是序列化。
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import scala.Tuple3;

public abstract class StationStatistics implements Serializable {

  private static final String MODE = "local";

  public void run(String[] args) {
    JavaSparkContext sc = new JavaSparkContext(MODE, getClass().getName());
    // 读入文本数据，创建名为lines的RDD
    JavaRDD<String> lines = sc.textFile(args[0]);
    JavaRDD<Tuple3<String, String, Integer>> statistics = stationStatistics(lines);
    statistics.saveAsTextFile(args[1]);
    sc.close();
  }

  /**
   * TODO 请完成该方法
   * <p>
   * 请在此方法中计算出每个人在每个基站停留的总时间（以秒为单位）
   *
   * @param lines 包含了输入文本文件数据的RDD
   * @return 包含了每个人在每个基站停留的总时间的RDD
   */
  public abstract JavaRDD<Tuple3<String, String, Integer>> stationStatistics(JavaRDD<String> lines);
}
