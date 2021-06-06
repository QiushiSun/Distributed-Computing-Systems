package DSPPCode.spark.broadcast_k_means.impl;

import DSPPCode.spark.broadcast_k_means.question.BroadcastKMeans;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import java.util.List;

public class BroadcastKMeansImpl extends BroadcastKMeans {
  @Override
  public Integer closestPoint(List<Integer> p, Broadcast<List<List<Double>>> kPoints) {
    int bestIndex = 0;
    double closest = Double.MAX_VALUE;
    for (int i=0; i < kPoints.value().size();i++){
      // System.out.println(kPoints.value().get(i));
      // System.out.println("here");
      double dist = distanceSquared(p,kPoints.value().get(i));
      if (dist<closest){
        closest = dist;
        bestIndex = i;
      }
    }
    return bestIndex;
  }

  @Override
  public Broadcast<List<List<Double>>> createBroadcastVariable(JavaSparkContext sc,
      List<List<Double>> localVariable) {
    return sc.broadcast(localVariable);
  }
}
